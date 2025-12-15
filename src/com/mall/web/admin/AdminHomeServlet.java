package com.mall.web.admin;

import com.mall.model.Admin;
import com.mall.util.DBUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;

@WebServlet("/admin/home")
public class AdminHomeServlet extends HttpServlet {

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

        double totalSales = 0;
        int orderCount = 0;
        int userCount = 0;

        try (Connection conn = DBUtil.getConnection()) {
            String sql1 = "SELECT IFNULL(SUM(total_amount),0) FROM orders";
            PreparedStatement ps1 = conn.prepareStatement(sql1);
            ResultSet rs1 = ps1.executeQuery();
            if (rs1.next()) totalSales = rs1.getDouble(1);

            String sql2 = "SELECT COUNT(*) FROM orders";
            PreparedStatement ps2 = conn.prepareStatement(sql2);
            ResultSet rs2 = ps2.executeQuery();
            if (rs2.next()) orderCount = rs2.getInt(1);

            String sql3 = "SELECT COUNT(*) FROM user";
            PreparedStatement ps3 = conn.prepareStatement(sql3);
            ResultSet rs3 = ps3.executeQuery();
            if (rs3.next()) userCount = rs3.getInt(1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        request.setAttribute("totalSales", totalSales);
        request.setAttribute("orderCount", orderCount);
        request.setAttribute("userCount", userCount);
        request.getRequestDispatcher("/WEB-INF/jsp/admin/admin_index.jsp").forward(request, response);
    }
}
