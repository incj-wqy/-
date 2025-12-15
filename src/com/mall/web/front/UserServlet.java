package com.mall.web.front;

import com.mall.model.User;
import com.mall.util.DBUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;

@WebServlet("/UserServlet")
public class UserServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if ("toRegister".equals(action)) {
            request.getRequestDispatcher("/WEB-INF/jsp/front/register.jsp").forward(request, response);
        } else if ("register".equals(action)) {
            doRegister(request, response);
        } else if ("toLogin".equals(action)) {
            request.getRequestDispatcher("/WEB-INF/jsp/front/login.jsp").forward(request, response);
        } else if ("login".equals(action)) {
            doLogin(request, response);
        } else if ("logout".equals(action)) {
            request.getSession().invalidate();
            response.sendRedirect("IndexServlet");
        } else if ("center".equals(action)) {
            toCenter(request, response);
        } else if ("update".equals(action)) {
            doUpdate(request, response);
        } else {
            response.sendRedirect("IndexServlet");
        }
    }

    private void doRegister(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String email    = request.getParameter("email");
        String phone    = request.getParameter("phone");
        String address  = request.getParameter("address");

        // 简单校验：用户名与密码必填
        if (username == null || username.trim().isEmpty()
                || password == null || password.trim().isEmpty()) {
            request.setAttribute("msg", "用户名和密码不能为空");
            request.getRequestDispatcher("/WEB-INF/jsp/front/register.jsp").forward(request, response);
            return;
        }

        try (Connection conn = DBUtil.getConnection()) {
            // 检查用户名是否存在
            String checkSql = "SELECT COUNT(*) FROM user WHERE username=?";
            PreparedStatement checkPs = conn.prepareStatement(checkSql);
            checkPs.setString(1, username);
            ResultSet rs = checkPs.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                request.setAttribute("msg", "用户名已存在");
                request.getRequestDispatcher("/WEB-INF/jsp/front/register.jsp").forward(request, response);
                return;
            }

            String sql = "INSERT INTO user(username,password,email,phone,address) VALUES(?,?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);  // 实际项目需要加密
            ps.setString(3, email);
            ps.setString(4, phone);
            ps.setString(5, address);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("msg", "注册失败，请稍后再试");
            request.getRequestDispatcher("/WEB-INF/jsp/front/register.jsp").forward(request, response);
            return;
        }

        // 注册成功跳转到登录
        response.sendRedirect("UserServlet?action=toLogin");
    }

    private void doLogin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String remember = request.getParameter("remember");

        User user = null;
        try (Connection conn = DBUtil.getConnection()) {
            String sql = "SELECT * FROM user WHERE username=? AND password=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setEmail(rs.getString("email"));
                user.setPhone(rs.getString("phone"));
                user.setAddress(rs.getString("address"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (user != null) {
            HttpSession session = request.getSession();
            session.setAttribute("currentUser", user);

            // 记住密码（简单版）：直接存明文 cookie
            if ("on".equals(remember)) {
                Cookie cu = new Cookie("username", username);
                Cookie cp = new Cookie("password", password);
                cu.setMaxAge(7 * 24 * 3600);
                cp.setMaxAge(7 * 24 * 3600);
                response.addCookie(cu);
                response.addCookie(cp);
            }
            response.sendRedirect("IndexServlet");
        } else {
            request.setAttribute("msg", "用户名或密码错误");
            request.getRequestDispatcher("/WEB-INF/jsp/front/login.jsp").forward(request, response);
        }
    }

    private void toCenter(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User u = (User) session.getAttribute("currentUser");
        if (u == null) {
            response.sendRedirect("UserServlet?action=toLogin");
            return;
        }
        request.getRequestDispatcher("/WEB-INF/jsp/front/user_center.jsp").forward(request, response);
    }

    private void doUpdate(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        HttpSession session = request.getSession();
        User u = (User) session.getAttribute("currentUser");
        if (u == null) {
            response.sendRedirect("UserServlet?action=toLogin");
            return;
        }

        String email   = request.getParameter("email");
        String phone   = request.getParameter("phone");
        String address = request.getParameter("address");

        try (Connection conn = DBUtil.getConnection()) {
            String sql = "UPDATE user SET email=?, phone=?, address=? WHERE id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, email);
            ps.setString(2, phone);
            ps.setString(3, address);
            ps.setInt(4, u.getId());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }

        u.setEmail(email);
        u.setPhone(phone);
        u.setAddress(address);
        session.setAttribute("currentUser", u);

        response.sendRedirect("UserServlet?action=center");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
