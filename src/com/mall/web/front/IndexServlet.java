package com.mall.web.front;

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

@WebServlet("/IndexServlet")
public class IndexServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<Category> categories = new ArrayList<>();
        List<Product> hotProducts = new ArrayList<>();

        try (Connection conn = DBUtil.getConnection()) {
            // 所有分类
            String sqlCat = "SELECT * FROM category ORDER BY parent_id, sort";
            PreparedStatement psCat = conn.prepareStatement(sqlCat);
            ResultSet rsCat = psCat.executeQuery();
            while (rsCat.next()) {
                Category c = new Category();
                c.setId(rsCat.getInt("id"));
                c.setName(rsCat.getString("name"));
                c.setParentId(rsCat.getInt("parent_id"));
                c.setSort(rsCat.getInt("sort"));
                categories.add(c);
            }

            // 热门商品：简单用最新的前 8 条
            String sqlHot = "SELECT * FROM product WHERE status=1 ORDER BY create_time DESC LIMIT 8";
            PreparedStatement psHot = conn.prepareStatement(sqlHot);
            ResultSet rsHot = psHot.executeQuery();
            while (rsHot.next()) {
                Product p = new Product();
                p.setId(rsHot.getInt("id"));
                p.setName(rsHot.getString("name"));
                p.setPrice(rsHot.getDouble("price"));
                p.setImage(rsHot.getString("image"));
                hotProducts.add(p);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        request.setAttribute("categories", categories);
        request.setAttribute("hotProducts", hotProducts);
        request.getRequestDispatcher("/WEB-INF/jsp/front/index.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
