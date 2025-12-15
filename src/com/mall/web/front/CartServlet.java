package com.mall.web.front;

import com.mall.model.CartItem;
import com.mall.model.Product;
import com.mall.util.DBUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.util.*;

@WebServlet("/CartServlet")
public class CartServlet extends HttpServlet {

    // 从 session 获取购物车（Map<productId, CartItem>）
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
        if ("add".equals(action)) {
            add(request, response);
        } else if ("list".equals(action)) {
            list(request, response);
        } else if ("update".equals(action)) {
            update(request, response);
        } else if ("delete".equals(action)) {
            delete(request, response);
        } else if ("clear".equals(action)) {
            clear(request, response);
        } else {
            list(request, response);
        }
    }

    // 加入购物车
    private void add(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Map<Integer, CartItem> cart = getCart(session);

        String pidStr = request.getParameter("productId");
        String qtyStr = request.getParameter("quantity");
        int pid = Integer.parseInt(pidStr);
        int qty = 1;
        try { qty = Integer.parseInt(qtyStr); } catch (Exception ignored) {}

        // 从数据库查询商品
        Product p = null;
        try (Connection conn = DBUtil.getConnection()) {
            String sql = "SELECT * FROM product WHERE id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, pid);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                p = new Product();
                p.setId(rs.getInt("id"));
                p.setName(rs.getString("name"));
                p.setPrice(rs.getDouble("price"));
                p.setImage(rs.getString("image"));
                p.setStock(rs.getInt("stock"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (p != null) {
            CartItem item = cart.get(pid);
            if (item == null) {
                item = new CartItem(p, qty);
                cart.put(pid, item);
            } else {
                item.setQuantity(item.getQuantity() + qty);
            }
        }

        response.sendRedirect("CartServlet?action=list");
    }

    // 购物车列表
    private void list(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Map<Integer, CartItem> cart = getCart(session);

        double total = 0;
        for (CartItem item : cart.values()) {
            total += item.getSubtotal();
        }
        request.setAttribute("cartTotal", total);

        request.getRequestDispatcher("/WEB-INF/jsp/front/cart.jsp").forward(request, response);
    }

    // 更新数量
    private void update(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Map<Integer, CartItem> cart = getCart(session);

        String pidStr = request.getParameter("productId");
        String qtyStr = request.getParameter("quantity");
        int pid = Integer.parseInt(pidStr);
        int qty = Integer.parseInt(qtyStr);

        CartItem item = cart.get(pid);
        if (item != null) {
            if (qty <= 0) {
                cart.remove(pid);
            } else {
                item.setQuantity(qty);
            }
        }

        response.sendRedirect("CartServlet?action=list");
    }

    // 删除某个商品
    private void delete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Map<Integer, CartItem> cart = getCart(session);

        String pidStr = request.getParameter("productId");
        int pid = Integer.parseInt(pidStr);
        cart.remove(pid);

        response.sendRedirect("CartServlet?action=list");
    }

    // 清空购物车
    private void clear(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        session.removeAttribute("cart");
        response.sendRedirect("CartServlet?action=list");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
