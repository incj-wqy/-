package com.mall.web.admin;

import com.mall.model.Admin;
import com.mall.util.DBUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.util.*;

@WebServlet("/admin/stats")
public class AdminStatsServlet extends HttpServlet {

    private boolean checkLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Admin admin = (Admin) request.getSession().getAttribute("adminUser");
        if (admin == null) {
            response.sendRedirect(request.getContextPath() + "/admin/login");
            return false;
        }
        return true;
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!checkLogin(request, response)) return;

        List<Object[]> dailySales = new ArrayList<>();
        List<Object[]> categorySales = new ArrayList<>();
        List<Object[]> topProducts = new ArrayList<>();

        try (Connection conn = DBUtil.getConnection()) {

            // 最近 7 天销售额（按日期）
            String sql1 = "SELECT DATE(create_time) d, SUM(total_amount) s " +
                    "FROM orders GROUP BY DATE(create_time) " +
                    "ORDER BY d DESC LIMIT 7";
            PreparedStatement ps1 = conn.prepareStatement(sql1);
            ResultSet rs1 = ps1.executeQuery();
            while (rs1.next()) {
                dailySales.add(new Object[]{rs1.getString("d"), rs1.getDouble("s")});
            }

            // 各品类销量（按数量）
            String sql2 = "SELECT c.name cname, IFNULL(SUM(oi.quantity),0) qty " +
                    "FROM category c " +
                    "LEFT JOIN product p ON c.id = p.category_id " +
                    "LEFT JOIN order_item oi ON p.id = oi.product_id " +
                    "GROUP BY c.id, c.name " +
                    "ORDER BY qty DESC";
            PreparedStatement ps2 = conn.prepareStatement(sql2);
            ResultSet rs2 = ps2.executeQuery();
            while (rs2.next()) {
                categorySales.add(new Object[]{rs2.getString("cname"), rs2.getLong("qty")});
            }

            // TOP 商品销量
            String sql3 = "SELECT p.name pname, SUM(oi.quantity) qty " +
                    "FROM order_item oi " +
                    "JOIN product p ON oi.product_id = p.id " +
                    "GROUP BY p.id, p.name " +
                    "ORDER BY qty DESC LIMIT 10";
            PreparedStatement ps3 = conn.prepareStatement(sql3);
            ResultSet rs3 = ps3.executeQuery();
            while (rs3.next()) {
                topProducts.add(new Object[]{rs3.getString("pname"), rs3.getLong("qty")});
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        request.setAttribute("dailySales", dailySales);
        request.setAttribute("categorySales", categorySales);
        request.setAttribute("topProducts", topProducts);
        request.getRequestDispatcher("/WEB-INF/jsp/admin/admin_stats.jsp").forward(request, response);
    }
}
