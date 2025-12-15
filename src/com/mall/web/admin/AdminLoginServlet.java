package com.mall.web.admin;

import com.mall.model.Admin;
import com.mall.util.DBUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;

@WebServlet("/admin/login")
public class AdminLoginServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/jsp/admin/admin_login.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        Admin admin = null;
        try (Connection conn = DBUtil.getConnection()) {
            String sql = "SELECT * FROM admin WHERE username=? AND password=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                admin = new Admin();
                admin.setId(rs.getInt("id"));
                admin.setUsername(rs.getString("username"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (admin != null) {
            request.getSession().setAttribute("adminUser", admin);
            response.sendRedirect(request.getContextPath() + "/admin/home");
        } else {
            request.setAttribute("msg", "账号或密码错误");
            request.getRequestDispatcher("/WEB-INF/jsp/admin/admin_login.jsp").forward(request, response);
        }
    }
}
