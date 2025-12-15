package com.mall.web.front;

import com.mall.model.Product;
import com.mall.util.DBUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/ProductServlet")
public class ProductServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if ("list".equals(action)) {
            list(request, response);
        } else if ("detail".equals(action)) {
            detail(request, response);
        } else {
            list(request, response);
        }
    }

    // 商品列表
    private void list(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String categoryIdStr = request.getParameter("categoryId");
        String pageStr       = request.getParameter("page");
        String sort          = request.getParameter("sort"); // priceAsc / priceDesc / null

        int page = 1;
        int pageSize = 8;
        if (pageStr != null) {
            try { page = Integer.parseInt(pageStr); } catch (Exception ignored) {}
        }
        int offset = (page - 1) * pageSize;

        List<Product> products = new ArrayList<>();
        int totalCount = 0;
        String where = " WHERE status=1 ";
        if (categoryIdStr != null && !"".equals(categoryIdStr)) {
            where += " AND category_id=" + Integer.parseInt(categoryIdStr);
        }
        String orderBy = " ORDER BY create_time DESC ";
        if ("priceAsc".equals(sort)) orderBy = " ORDER BY price ASC ";
        if ("priceDesc".equals(sort)) orderBy = " ORDER BY price DESC ";

        try (Connection conn = DBUtil.getConnection()) {
            // 总数
            String sqlCount = "SELECT COUNT(*) FROM product " + where;
            PreparedStatement psCount = conn.prepareStatement(sqlCount);
            ResultSet rsCount = psCount.executeQuery();
            if (rsCount.next()) {
                totalCount = rsCount.getInt(1);
            }

            // 当前页数据
            String sql = "SELECT * FROM product " + where + orderBy + " LIMIT ?,?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, offset);
            ps.setInt(2, pageSize);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Product p = new Product();
                p.setId(rs.getInt("id"));
                p.setName(rs.getString("name"));
                p.setPrice(rs.getDouble("price"));
                p.setImage(rs.getString("image"));
                p.setStock(rs.getInt("stock"));
                products.add(p);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        int totalPage = (int) Math.ceil(totalCount * 1.0 / pageSize);

        request.setAttribute("products", products);
        request.setAttribute("page", page);
        request.setAttribute("totalPage", totalPage);
        request.setAttribute("sort", sort);
        request.setAttribute("categoryId", categoryIdStr);

        request.getRequestDispatcher("/WEB-INF/jsp/front/product_list.jsp").forward(request, response);
    }

    // 商品详情
    private void detail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        Product p = null;

        if (idStr != null) {
            try (Connection conn = DBUtil.getConnection()) {
                String sql = "SELECT * FROM product WHERE id=?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, Integer.parseInt(idStr));
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    p = new Product();
                    p.setId(rs.getInt("id"));
                    p.setName(rs.getString("name"));
                    p.setPrice(rs.getDouble("price"));
                    p.setImage(rs.getString("image"));
                    p.setImages(rs.getString("images"));
                    p.setStock(rs.getInt("stock"));
                    p.setDescription(rs.getString("description"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (p == null) {
            response.sendRedirect("ProductServlet?action=list");
            return;
        }

        request.setAttribute("product", p);
        request.getRequestDispatcher("/WEB-INF/jsp/front/product_detail.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
