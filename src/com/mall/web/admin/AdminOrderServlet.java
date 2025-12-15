package com.mall.web.admin;

import com.mall.model.Admin;
import com.mall.model.Order;
import com.mall.model.OrderItem;
import com.mall.util.DBUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.util.*;

@WebServlet("/admin/order")
public class AdminOrderServlet extends HttpServlet {

    private boolean checkLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Admin admin = (Admin) request.getSession().getAttribute("adminUser");
        if (admin == null) {
            response.sendRedirect(request.getContextPath() + "/admin/login");
            return false;
        }
        return true;
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (!checkLogin(req, resp)) return;

        String action = req.getParameter("action");
        if (action == null || "list".equals(action)) {
            list(req, resp);
        } else if ("detail".equals(action)) {
            detail(req, resp);
        } else if ("updateStatus".equals(action)) {
            updateStatus(req, resp);
        } else if ("saveLogistics".equals(action)) {
            saveLogistics(req, resp);
        }
    }

    // 列表 + 多条件筛选（时间 / 状态 / 订单号）
    private void list(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String status = req.getParameter("status");
        String orderNo = req.getParameter("orderNo");
        String startDate = req.getParameter("startDate"); // yyyy-MM-dd
        String endDate = req.getParameter("endDate");

        List<Order> orders = new ArrayList<>();

        try (Connection conn = DBUtil.getConnection()) {
            StringBuilder sql = new StringBuilder("SELECT * FROM orders WHERE 1=1 ");
            List<Object> params = new ArrayList<>();

            if (status != null && !status.trim().isEmpty()) {
                sql.append(" AND status=? ");
                params.add(status.trim());
            }
            if (orderNo != null && !orderNo.trim().isEmpty()) {
                sql.append(" AND order_no LIKE ? ");
                params.add("%" + orderNo.trim() + "%");
            }
            if (startDate != null && !startDate.trim().isEmpty()) {
                sql.append(" AND create_time>=? ");
                params.add(startDate.trim() + " 00:00:00");
            }
            if (endDate != null && !endDate.trim().isEmpty()) {
                sql.append(" AND create_time<=? ");
                params.add(endDate.trim() + " 23:59:59");
            }
            sql.append(" ORDER BY create_time DESC");

            PreparedStatement ps = conn.prepareStatement(sql.toString());
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Order o = new Order();
                o.setId(rs.getInt("id"));
                o.setOrderNo(rs.getString("order_no"));
                o.setUserId(rs.getInt("user_id"));
                o.setTotalAmount(rs.getDouble("total_amount"));
                o.setStatus(rs.getString("status"));
                o.setPaymentMethod(rs.getString("payment_method"));
                o.setAddress(rs.getString("address"));
                o.setLogisticsCompany(rs.getString("logistics_company"));
                o.setTrackingNo(rs.getString("tracking_no"));
                o.setCreateTime(rs.getTimestamp("create_time"));
                orders.add(o);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        req.setAttribute("orders", orders);
        req.setAttribute("status", status);
        req.setAttribute("orderNo", orderNo);
        req.setAttribute("startDate", startDate);
        req.setAttribute("endDate", endDate);
        req.getRequestDispatcher("/WEB-INF/jsp/admin/admin_order_list.jsp").forward(req, resp);
    }

    // 订单详情
    private void detail(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String orderNo = req.getParameter("orderNo");
        Order order = null;
        List<OrderItem> items = new ArrayList<>();

        try (Connection conn = DBUtil.getConnection()) {
            String sql = "SELECT * FROM orders WHERE order_no=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, orderNo);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                order = new Order();
                order.setId(rs.getInt("id"));
                order.setOrderNo(rs.getString("order_no"));
                order.setUserId(rs.getInt("user_id"));
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
            resp.sendRedirect("order?action=list");
            return;
        }

        req.setAttribute("order", order);
        req.getRequestDispatcher("/WEB-INF/jsp/admin/admin_order_detail.jsp").forward(req, resp);
    }

    // 批量/单个更新订单状态
    private void updateStatus(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String orderNo = req.getParameter("orderNo");
        String status = req.getParameter("status");

        try (Connection conn = DBUtil.getConnection()) {
            String sql = "UPDATE orders SET status=? WHERE order_no=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, status);
            ps.setString(2, orderNo);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }

        resp.sendRedirect("order?action=detail&orderNo=" + orderNo);
    }

    // 保存物流信息
    private void saveLogistics(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String orderNo = req.getParameter("orderNo");
        String company = req.getParameter("logisticsCompany");
        String tracking = req.getParameter("trackingNo");

        try (Connection conn = DBUtil.getConnection()) {
            String sql = "UPDATE orders SET logistics_company=?, tracking_no=? WHERE order_no=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, company);
            ps.setString(2, tracking);
            ps.setString(3, orderNo);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }

        resp.sendRedirect("order?action=detail&orderNo=" + orderNo);
    }
}
