package com.mall.web.admin;

import com.mall.model.Admin;
import com.mall.model.Category;
import com.mall.util.DBUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/admin/category")
public class AdminCategoryServlet extends HttpServlet {

    // 校验是否已登录后台
    private boolean checkLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Admin admin = (Admin) request.getSession().getAttribute("adminUser");
        if (admin == null) {
            response.sendRedirect(request.getContextPath() + "/admin/login");
            return false;
        }
        return true;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!checkLogin(request, response)) return;

        String action = request.getParameter("action");
        if (action == null || "list".equals(action)) {
            list(request, response);
        } else if ("toEdit".equals(action)) {
            toEdit(request, response);
        } else if ("save".equals(action)) {
            save(request, response);
        } else if ("delete".equals(action)) {
            delete(request, response);
        }
    }

    // 分类列表
    private void list(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<Category> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection()) {
            String sql = "SELECT * FROM category ORDER BY parent_id, sort";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Category c = new Category();
                c.setId(rs.getInt("id"));
                c.setName(rs.getString("name"));
                c.setParentId(rs.getInt("parent_id"));
                c.setSort(rs.getInt("sort"));
                list.add(c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        request.setAttribute("categories", list);
        request.getRequestDispatcher("/WEB-INF/jsp/admin/admin_category_list.jsp").forward(request, response);
    }

    // 新增/编辑页面
    private void toEdit(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idStr = request.getParameter("id");
        Category c = new Category();

        // 如果是编辑，根据 id 查询一条
        if (idStr != null && !"".equals(idStr)) {
            try (Connection conn = DBUtil.getConnection()) {
                String sql = "SELECT * FROM category WHERE id=?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, Integer.parseInt(idStr));
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    c.setId(rs.getInt("id"));
                    c.setName(rs.getString("name"));
                    c.setParentId(rs.getInt("parent_id"));
                    c.setSort(rs.getInt("sort"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 所有分类（用于选择上级）
        List<Category> all = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection()) {
            String sql = "SELECT * FROM category ORDER BY parent_id, sort";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Category cc = new Category();
                cc.setId(rs.getInt("id"));
                cc.setName(rs.getString("name"));
                cc.setParentId(rs.getInt("parent_id"));
                cc.setSort(rs.getInt("sort"));
                all.add(cc);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        request.setAttribute("category", c);
        request.setAttribute("allCategories", all);
        request.getRequestDispatcher("/WEB-INF/jsp/admin/admin_category_edit.jsp").forward(request, response);
    }

    // 保存分类（新增 + 编辑）
    private void save(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("utf-8");

        String idStr     = request.getParameter("id");
        String name      = request.getParameter("name");
        String parentStr = request.getParameter("parentId");
        String sortStr   = request.getParameter("sort");

        int parentId = 0;
        int sort = 0;
        try { parentId = Integer.parseInt(parentStr); } catch (Exception ignored) {}
        try { sort = Integer.parseInt(sortStr); } catch (Exception ignored) {}

        // 关键：id 为空 或 0 都认为是“新增”
        boolean isNew = (idStr == null || idStr.trim().isEmpty() || "0".equals(idStr.trim()));

        try (Connection conn = DBUtil.getConnection()) {
            if (isNew) {
                String sql = "INSERT INTO category(name,parent_id,sort) VALUES(?,?,?)";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, name);
                ps.setInt(2, parentId);
                ps.setInt(3, sort);
                ps.executeUpdate();
            } else {
                String sql = "UPDATE category SET name=?, parent_id=?, sort=? WHERE id=?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, name);
                ps.setInt(2, parentId);
                ps.setInt(3, sort);
                ps.setInt(4, Integer.parseInt(idStr.trim()));
                ps.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        response.sendRedirect(request.getContextPath() + "/admin/category?action=list");
    }

    // 删除分类
    private void delete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idStr = request.getParameter("id");
        try (Connection conn = DBUtil.getConnection()) {
            String sql = "DELETE FROM category WHERE id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, Integer.parseInt(idStr));
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }

        response.sendRedirect(request.getContextPath() + "/admin/category?action=list");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 让 POST 和 GET 共用同一套逻辑
        doGet(request, response);
    }
}
