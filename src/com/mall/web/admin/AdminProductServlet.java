package com.mall.web.admin;

import com.mall.model.Admin;
import com.mall.model.Category;
import com.mall.model.Product;
import com.mall.util.DBUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/admin/product")
public class AdminProductServlet extends HttpServlet {

    private boolean checkLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Admin admin = (Admin) request.getSession().getAttribute("adminUser");
        if (admin == null) {
            response.sendRedirect(request.getContextPath() + "/admin/login");
            return false;
        }
        return true;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (!checkLogin(req, resp)) return;

        String action = req.getParameter("action");
        if (action == null || "list".equals(action)) {
            list(req, resp);
        } else if ("toEdit".equals(action)) {
            toEdit(req, resp);
        } else if ("save".equals(action)) {
            save(req, resp);
        } else if ("toggle".equals(action)) {
            toggle(req, resp);
        } else if ("delete".equals(action)) {
            delete(req, resp);
        }
    }

    // 商品列表
    private void list(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String keyword = req.getParameter("keyword");
        String statusStr = req.getParameter("status"); // 1 / 0

        List<Product> list = new ArrayList<>();

        try (Connection conn = DBUtil.getConnection()) {
            StringBuilder sql = new StringBuilder("SELECT * FROM product WHERE 1=1 ");
            List<Object> params = new ArrayList<>();

            if (keyword != null && !keyword.trim().isEmpty()) {
                sql.append(" AND name LIKE ? ");
                params.add("%" + keyword.trim() + "%");
            }
            if (statusStr != null && !statusStr.trim().isEmpty()) {
                sql.append(" AND status = ? ");
                params.add(Integer.parseInt(statusStr));
            }
            sql.append(" ORDER BY create_time DESC");

            PreparedStatement ps = conn.prepareStatement(sql.toString());
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Product p = new Product();
                p.setId(rs.getInt("id"));
                p.setName(rs.getString("name"));
                p.setCategoryId(rs.getInt("category_id"));
                p.setPrice(rs.getDouble("price"));
                p.setStock(rs.getInt("stock"));
                p.setStatus(rs.getInt("status"));
                p.setImage(rs.getString("image"));
                list.add(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        req.setAttribute("products", list);
        req.setAttribute("keyword", keyword);
        req.setAttribute("status", statusStr);
        req.getRequestDispatcher("/WEB-INF/jsp/admin/admin_product_list.jsp").forward(req, resp);
    }

    // 商品编辑页面
    private void toEdit(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String idStr = req.getParameter("id");
        Product p = new Product();

        if (idStr != null && !"".equals(idStr)) {
            try (Connection conn = DBUtil.getConnection()) {
                String sql = "SELECT * FROM product WHERE id=?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, Integer.parseInt(idStr));
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    p.setId(rs.getInt("id"));
                    p.setName(rs.getString("name"));
                    p.setCategoryId(rs.getInt("category_id"));
                    p.setPrice(rs.getDouble("price"));
                    p.setStock(rs.getInt("stock"));
                    p.setStatus(rs.getInt("status"));
                    p.setImage(rs.getString("image"));
                    p.setImages(rs.getString("images"));
                    p.setDescription(rs.getString("description"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 分类列表
        List<Category> cats = new ArrayList<>();
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
                cats.add(c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        req.setAttribute("product", p);
        req.setAttribute("categories", cats);
        req.getRequestDispatcher("/WEB-INF/jsp/admin/admin_product_edit.jsp").forward(req, resp);
    }

    // 保存商品（新增 + 编辑）
    private void save(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("utf-8");

        String idStr    = req.getParameter("id");
        String name     = req.getParameter("name");
        String category = req.getParameter("categoryId");
        String price    = req.getParameter("price");
        String stock    = req.getParameter("stock");
        String status   = req.getParameter("status");
        String image    = req.getParameter("image");
        String images   = req.getParameter("images");
        String desc     = req.getParameter("description");

        // id 为空或 0 视为新增
        boolean isNew = (idStr == null || idStr.trim().isEmpty() || "0".equals(idStr.trim()));

        try (Connection conn = DBUtil.getConnection()) {
            if (isNew) {
                String sql = "INSERT INTO product(name,category_id,price,stock,status,image,images,description) " +
                        "VALUES(?,?,?,?,?,?,?,?)";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, name);
                ps.setInt(2, Integer.parseInt(category));
                ps.setDouble(3, Double.parseDouble(price));
                ps.setInt(4, Integer.parseInt(stock));
                ps.setInt(5, Integer.parseInt(status));
                ps.setString(6, image);
                ps.setString(7, images);
                ps.setString(8, desc);
                ps.executeUpdate();
            } else {
                String sql = "UPDATE product SET name=?,category_id=?,price=?,stock=?,status=?,image=?,images=?,description=? " +
                        "WHERE id=?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, name);
                ps.setInt(2, Integer.parseInt(category));
                ps.setDouble(3, Double.parseDouble(price));
                ps.setInt(4, Integer.parseInt(stock));
                ps.setInt(5, Integer.parseInt(status));
                ps.setString(6, image);
                ps.setString(7, images);
                ps.setString(8, desc);
                ps.setInt(9, Integer.parseInt(idStr.trim()));
                ps.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        resp.sendRedirect(req.getContextPath() + "/admin/product?action=list");
    }

    // 上下架切换
    private void toggle(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String idStr = req.getParameter("id");
        String statusStr = req.getParameter("status");

        try (Connection conn = DBUtil.getConnection()) {
            String sql = "UPDATE product SET status=? WHERE id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, Integer.parseInt(statusStr));
            ps.setInt(2, Integer.parseInt(idStr));
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }

        resp.sendRedirect(req.getContextPath() + "/admin/product?action=list");
    }

    // 删除商品
    private void delete(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String idStr = req.getParameter("id");

        try (Connection conn = DBUtil.getConnection()) {
            String sql = "DELETE FROM product WHERE id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, Integer.parseInt(idStr));
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }

        resp.sendRedirect(req.getContextPath() + "/admin/product?action=list");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // 统一用 doGet 处理
        doGet(req, resp);
    }
}
