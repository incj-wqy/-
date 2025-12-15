package com.mall.web.front;

import com.mall.model.CartItem;
import com.mall.model.Order;
import com.mall.model.OrderItem;
import com.mall.model.User;
import com.mall.util.DBUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.util.*;

@WebServlet("/OrderServlet")
public class OrderServlet extends HttpServlet {

    @SuppressWarnings("unchecked")
    private Map<Integer, CartItem> getCart(HttpSession session) {
        Map<Integer, CartItem> cart = (Map<Integer, CartItem>) session.getAttribute("cart");
        if (cart == null) {
            cart = new LinkedHashMap<>();
            session.setAttribute("cart", cart);
        }
        return cart;
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        if ("toCheckout".equals(action)) {
            toCheckout(request, response);
        } else if ("create".equals(action)) {
            create(request, response);
        } else if ("list".equals(action)) {
            list(request, response);
        } else if ("detail".equals(action)) {
            detail(request, response);
        } else {
            list(request, response);
        }
    }

    // 跳转到结算页面
    private void toCheckout(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        User u = (User) session.getAttribute("currentUser");
        if (u == null) {
            response.sendRedirect("UserServlet?action=toLogin");
            return;
        }

        Map<Integer, CartItem> cart = getCart(session);
        if (cart.isEmpty()) {
            response.sendRedirect("CartServlet?action=list");
            return;
        }

        double total = 0;
        for (CartItem item : cart.values()) {
            total += item.getSubtotal();
        }

        request.setAttribute("cartTotal", total);
        request.getRequestDispatcher("/WEB-INF/jsp/front/checkout.jsp").forward(request, response);
    }

    // 创建订单
    private void create(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("utf-8");
        HttpSession session = request.getSession();
        User u = (User) session.getAttribute("currentUser");
        if (u == null) {
            response.sendRedirect("UserServlet?action=toLogin");
            return;
        }

        Map<Integer, CartItem> cart = getCart(session);
        if (cart.isEmpty()) {
            response.sendRedirect("CartServlet?action=list");
            return;
        }

        String address = request.getParameter("address");
        String paymentMethod = request.getParameter("paymentMethod");
        String couponCode = request.getParameter("coupon");

        double total = 0;
        for (CartItem item : cart.values()) {
            total += item.getSubtotal();
        }

        // 简单优惠券逻辑：OFF10 减 10 元
        if (couponCode != null && "OFF10".equalsIgnoreCase(couponCode.trim())) {
            total = Math.max(0, total - 10);
        }

        String orderNo = "OD" + System.currentTimeMillis();

        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);

            // 插入订单
            String sqlOrder = "INSERT INTO orders(order_no,user_id,total_amount,status,payment_method,address) " +
                    "VALUES(?,?,?,?,?,?)";
            PreparedStatement psOrder = conn.prepareStatement(sqlOrder, Statement.RETURN_GENERATED_KEYS);
            psOrder.setString(1, orderNo);
            psOrder.setInt(2, u.getId());
            psOrder.setDouble(3, total);
            psOrder.setString(4, "待付款");
            psOrder.setString(5, paymentMethod);
            psOrder.setString(6, address);
            psOrder.executeUpdate();

            ResultSet rsKey = psOrder.getGeneratedKeys();
            int orderId = 0;
            if (rsKey.next()) {
                orderId = rsKey.getInt(1);
            }

            // 插入订单明细
            String sqlItem = "INSERT INTO order_item(order_id,product_id,product_name,product_image,price,quantity) " +
                    "VALUES(?,?,?,?,?,?)";
            PreparedStatement psItem = conn.prepareStatement(sqlItem);

            for (CartItem item : cart.values()) {
                psItem.setInt(1, orderId);
                psItem.setInt(2, item.getProduct().getId());
                psItem.setString(3, item.getProduct().getName());
                psItem.setString(4, item.getProduct().getImage());
                psItem.setDouble(5, item.getProduct().getPrice());
                psItem.setInt(6, item.getQuantity());
                psItem.addBatch();
            }
            psItem.executeBatch();

            conn.commit();

            // 清空购物车
            session.removeAttribute("cart");

            // 跳到订单详情
            response.sendRedirect("OrderServlet?action=detail&orderNo=" + orderNo);

        } catch (Exception e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            response.sendRedirect("CartServlet?action=list");
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (Exception ignored) {}
            }
        }
    }

    // 订单列表（按状态筛选）
    private void list(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        User u = (User) session.getAttribute("currentUser");
        if (u == null) {
            response.sendRedirect("UserServlet?action=toLogin");
            return;
        }

        String status = request.getParameter("status"); // 待付款 / 已发货 等
        List<Order> orders = new ArrayList<>();

        try (Connection conn = DBUtil.getConnection()) {
            String sql = "SELECT * FROM orders WHERE user_id=? ";
            if (status != null && !status.trim().isEmpty()) {
                sql += " AND status=? ";
            }
            sql += " ORDER BY create_time DESC";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, u.getId());
            if (status != null && !status.trim().isEmpty()) {
                ps.setString(2, status);
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Order o = new Order();
                o.setId(rs.getInt("id"));
                o.setOrderNo(rs.getString("order_no"));
                o.setTotalAmount(rs.getDouble("total_amount"));
                o.setStatus(rs.getString("status"));
                o.setPaymentMethod(rs.getString("payment_method"));
                o.setAddress(rs.getString("address"));
                o.setCreateTime(rs.getTimestamp("create_time"));
                orders.add(o);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        request.setAttribute("orders", orders);
        request.setAttribute("status", status);
        request.getRequestDispatcher("/WEB-INF/jsp/front/order_list.jsp").forward(request, response);
    }

    // 订单详情
    private void detail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        User u = (User) session.getAttribute("currentUser");
        if (u == null) {
            response.sendRedirect("UserServlet?action=toLogin");
            return;
        }

        String orderNo = request.getParameter("orderNo");
        if (orderNo == null || orderNo.trim().isEmpty()) {
            response.sendRedirect("OrderServlet?action=list");
            return;
        }

        Order order = null;
        List<OrderItem> items = new ArrayList<>();

        try (Connection conn = DBUtil.getConnection()) {
            String sql = "SELECT * FROM orders WHERE order_no=? AND user_id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, orderNo);
            ps.setInt(2, u.getId());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                order = new Order();
                order.setId(rs.getInt("id"));
                order.setOrderNo(rs.getString("order_no"));
                order.setTotalAmount(rs.getDouble("total_amount"));
                order.setStatus(rs.getString("status"));
                order.setPaymentMethod(rs.getString("payment_method"));
                order.setAddress(rs.getString("address"));
                order.setLogisticsCompany(rs.getString("logistics_company"));
                order.setTrackingNo(rs.getString("tracking_no"));
                order.setCreateTime(rs.getTimestamp("create_time"));
            }

            if (order != null) {
                String sqlItem = "SELECT * FROM order_item WHERE order_id=?";
                PreparedStatement psItem = conn.prepareStatement(sqlItem);
                psItem.setInt(1, order.getId());
                ResultSet rsItem = psItem.executeQuery();
                while (rsItem.next()) {
                    OrderItem oi = new OrderItem();
                    oi.setId(rsItem.getInt("id"));
                    oi.setOrderId(rsItem.getInt("order_id"));
                    oi.setProductId(rsItem.getInt("product_id"));
                    oi.setProductName(rsItem.getString("product_name"));
                    oi.setProductImage(rsItem.getString("product_image"));
                    oi.setPrice(rsItem.getDouble("price"));
                    oi.setQuantity(rsItem.getInt("quantity"));
                    items.add(oi);
                }
                order.setItems(items);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (order == null) {
            response.sendRedirect("OrderServlet?action=list");
            return;
        }

        request.setAttribute("order", order);
        request.getRequestDispatcher("/WEB-INF/jsp/front/order_detail.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
