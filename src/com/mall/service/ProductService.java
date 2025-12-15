package com.mall.service;

import com.mall.model.Product;
import com.mall.util.DBUtil;
import com.mall.util.DBInitializer;
import javax.servlet.http.HttpSession;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductService {
    static {
        // 初始化数据库（如果为空）
        DBInitializer.initDefaultProducts();
    }

    // ========== 公开方法（带权限验证） ==========

    /**
     * 添加商品（带权限验证）
     */
    public static String addProduct(String name, Integer categoryId, Double price,
                                    Integer stock, String description, HttpSession session) {
        // 权限检查
//        if (!isAdmin(session)) {
//            return "❌ 权限不足：您需要管理员权限才能添加商品";
//        }

        // 调用无权限验证的内部方法
        return addProductInternal(name, categoryId, price, stock, description);
    }

    /**
     * 修改商品信息（带权限验证）
     */
    public static String updateProduct(String name, Double price, Integer stock,
                                       Integer status, HttpSession session) {
        // 权限检查
//        if (!isAdmin(session)) {
//            return "❌ 权限不足：您需要管理员权限才能修改商品";
//        }

        // 调用无权限验证的内部方法
        return updateProductInternal(name, price, stock, status);
    }

    /**
     * 删除商品（带权限验证）
     */
    public static String deleteProduct(String name, HttpSession session) {
        // 权限检查
//        if (!isAdmin(session)) {
//            return "❌ 权限不足：您需要管理员权限才能删除商品";
//        }

        // 调用无权限验证的内部方法
        return deleteProductInternal(name);
    }

    /**
     * 查询商品（无需权限验证）
     */
    public static String queryProduct(String keyword) {
        return queryProductInternal(keyword);
    }

    /**
     * 根据名称模糊查找商品
     */
    /**
     * 根据名称查找商品（增强版模糊匹配）
     */
    public static Product findProductByName(String productName) {
        if (productName == null || productName.trim().isEmpty()) {
            return null;
        }

        String name = productName.trim().toLowerCase();

        try (Connection conn = DBUtil.getConnection()) {
            // 方案1：精确匹配（优先级最高）
            String exactSql = "SELECT id, name, category_id, price, stock, status, description " +
                    "FROM product WHERE LOWER(name) = ? AND status = 1";
            PreparedStatement exactPs = conn.prepareStatement(exactSql);
            exactPs.setString(1, name);
            ResultSet exactRs = exactPs.executeQuery();

            if (exactRs.next()) {
                return buildProductFromResultSet(exactRs);
            }

            // 方案2：LIKE 模糊匹配
            String likeSql = "SELECT id, name, category_id, price, stock, status, description " +
                    "FROM product WHERE LOWER(name) LIKE ? AND status = 1 " +
                    "ORDER BY LENGTH(name) ASC, id ASC LIMIT 1";
            PreparedStatement likePs = conn.prepareStatement(likeSql);
            likePs.setString(1, "%" + name + "%");
            ResultSet likeRs = likePs.executeQuery();

            if (likeRs.next()) {
                return buildProductFromResultSet(likeRs);
            }

            // 方案3：分词模糊匹配（如果输入是部分词）
            String[] keywords = name.split("\\s+");
            if (keywords.length > 1) {
                // 构建更灵活的查询
                StringBuilder keywordSql = new StringBuilder();
                keywordSql.append("SELECT id, name, category_id, price, stock, status, description ")
                        .append("FROM product WHERE status = 1 AND (");

                for (int i = 0; i < keywords.length; i++) {
                    if (i > 0) keywordSql.append(" OR ");
                    keywordSql.append("LOWER(name) LIKE ?");
                }

                keywordSql.append(") ORDER BY (");

                // 根据匹配关键词数量排序
                for (int i = 0; i < keywords.length; i++) {
                    if (i > 0) keywordSql.append(" + ");
                    keywordSql.append("CASE WHEN LOWER(name) LIKE ? THEN 1 ELSE 0 END");
                }

                keywordSql.append(") DESC, LENGTH(name) ASC LIMIT 1");

                PreparedStatement keywordPs = conn.prepareStatement(keywordSql.toString());

                // 设置参数
                int paramIndex = 1;
                for (String keyword : keywords) {
                    keywordPs.setString(paramIndex++, "%" + keyword + "%");
                }
                for (String keyword : keywords) {
                    keywordPs.setString(paramIndex++, "%" + keyword + "%");
                }

                ResultSet keywordRs = keywordPs.executeQuery();
                if (keywordRs.next()) {
                    return buildProductFromResultSet(keywordRs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    /**
     * 智能商品查找（返回多个可能结果）
     */
    public static List<Product> searchProducts(String keyword, int limit) {
        List<Product> products = new ArrayList<>();

        if (keyword == null || keyword.trim().isEmpty()) {
            return products;
        }

        String searchTerm = keyword.trim().toLowerCase();

        try (Connection conn = DBUtil.getConnection()) {
            // 构建搜索查询
            String sql = "SELECT id, name, category_id, price, stock, status, description " +
                    "FROM product WHERE status = 1 " +
                    "AND (LOWER(name) LIKE ? OR LOWER(description) LIKE ?) " +
                    "ORDER BY " +
                    "CASE WHEN LOWER(name) = ? THEN 1 " +
                    "     WHEN LOWER(name) LIKE ? THEN 2 " +
                    "     ELSE 3 END, " +
                    "LENGTH(name) ASC " +
                    "LIMIT ?";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, "%" + searchTerm + "%");
            ps.setString(2, "%" + searchTerm + "%");
            ps.setString(3, searchTerm);
            ps.setString(4, searchTerm + "%");
            ps.setInt(5, limit);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                products.add(buildProductFromResultSet(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return products;
    }

    /**
     * 智能查询商品并返回友好提示
     */
    public static String smartQueryProduct(String userInput) {
        if (userInput == null || userInput.trim().isEmpty()) {
            return "🔍 请输入商品名称进行查询";
        }

        String keyword = userInput.trim();
        List<Product> products = searchProducts(keyword, 5);

        if (products.isEmpty()) {
            // 尝试更宽松的搜索
            String[] words = keyword.split("\\s+");
            if (words.length > 1) {
                // 使用第一个词重新搜索
                products = searchProducts(words[0], 5);

                if (products.isEmpty()) {
                    return "❌ 未找到商品【" + keyword + "】。请检查商品名称是否正确，或尝试输入完整名称。";
                }
            } else {
                return "❌ 未找到商品【" + keyword + "】。请检查商品名称是否正确。";
            }
        }

        // 构建结果
        StringBuilder result = new StringBuilder();
        if (products.size() == 1) {
            Product p = products.get(0);
            result.append("✅ 找到商品：\n\n");
            result.append("📱 名称：").append(p.getName()).append("\n");
            result.append("💰 价格：¥").append(String.format("%.2f", p.getPrice())).append("\n");
            result.append("📦 库存：").append(p.getStock()).append("\n");
            result.append("📊 状态：").append(p.getStatus() == 1 ? "上架" : "下架").append("\n");
            if (p.getDescription() != null && !p.getDescription().isEmpty()) {
                String desc = p.getDescription();
                result.append("📝 描述：").append(desc.length() > 100 ? desc.substring(0, 100) + "..." : desc).append("\n");
            }
        } else {
            result.append("🔍 找到 ").append(products.size()).append(" 个相关商品：\n\n");
            for (int i = 0; i < products.size(); i++) {
                Product p = products.get(i);
                result.append(i + 1).append(". ").append(p.getName())
                        .append(" - ¥").append(String.format("%.2f", p.getPrice()))
                        .append(" (库存：").append(p.getStock()).append(")\n");
            }
            result.append("\n💡 提示：请使用完整商品名称进行操作，如：");
            result.append("'把").append(products.get(0).getName()).append("加入购物车'");
        }

        return result.toString();
    }

    /**
     * 根据ID获取商品
     */
    public static Product getProductById(int productId) {
        try (Connection conn = DBUtil.getConnection()) {
            String sql = "SELECT id, name, category_id, price, stock, status, description " +
                    "FROM product WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, productId);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return buildProductFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    /**
     * 检查商品是否存在
     */
    public static boolean productExists(String name) {
        return isProductExist(name);
    }

    /**
     * 获取商品库存
     */
    public static int getProductStock(String productName) {
        try (Connection conn = DBUtil.getConnection()) {
            String sql = "SELECT stock FROM product WHERE name = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, productName);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("stock");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    /**
     * 更新商品库存
     */
    public static boolean updateProductStock(int productId, int quantity) {
        try (Connection conn = DBUtil.getConnection()) {
            // 先检查当前库存
            String checkSql = "SELECT stock FROM product WHERE id = ?";
            PreparedStatement checkPs = conn.prepareStatement(checkSql);
            checkPs.setInt(1, productId);
            ResultSet rs = checkPs.executeQuery();

            if (rs.next()) {
                int currentStock = rs.getInt("stock");
                if (currentStock < quantity) {
                    return false; // 库存不足
                }

                // 更新库存
                String updateSql = "UPDATE product SET stock = stock - ? WHERE id = ?";
                PreparedStatement updatePs = conn.prepareStatement(updateSql);
                updatePs.setInt(1, quantity);
                updatePs.setInt(2, productId);

                int rows = updatePs.executeUpdate();
                return rows > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    /**
     * 获取热门商品
     */
    public static List<Product> getHotProducts(int limit) {
        List<Product> products = new ArrayList<>();

        try (Connection conn = DBUtil.getConnection()) {
            String sql = "SELECT id, name, category_id, price, stock, status, description " +
                    "FROM product WHERE status = 1 " +
                    "ORDER BY create_time DESC LIMIT ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, limit);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                products.add(buildProductFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return products;
    }

    // ========== 内部方法（无权限验证） ==========

    /**
     * 添加商品（内部方法，无权限验证）
     */
    private static String addProductInternal(String name, Integer categoryId, Double price,
                                             Integer stock, String description) {
        // 参数验证
        if (name == null || name.trim().isEmpty()) {
            return "❌ 商品名称不能为空";
        }
        if (price == null || price <= 0) {
            return "❌ 商品价格必须大于0";
        }
        if (stock == null || stock < 0) {
            return "❌ 库存不能为负数";
        }

        name = name.trim();

        // 检查是否已存在
        if (isProductExist(name)) {
            return "❌ 商品【" + name + "】已存在";
        }

        try (Connection conn = DBUtil.getConnection()) {
            String sql = "INSERT INTO product(name, category_id, price, stock, status, description, create_time) " +
                    "VALUES(?, ?, ?, ?, 1, ?, NOW())";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, name);
            ps.setInt(2, categoryId != null ? categoryId : 0);
            ps.setDouble(3, price);
            ps.setInt(4, stock);
            ps.setString(5, description != null ? description : "");

            int rows = ps.executeUpdate();
            if (rows > 0) {
                return "✅ 添加成功：商品【" + name + "】已入库！\n" +
                        "📊 商品信息：\n" +
                        "名称：" + name + "\n" +
                        "分类ID：" + (categoryId != null ? categoryId : 0) + "\n" +
                        "价格：¥" + String.format("%.2f", price) + "\n" +
                        "库存：" + stock + "\n" +
                        "状态：上架";
            } else {
                return "❌ 添加失败，请重试";
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return "❌ 数据库错误：" + e.getMessage();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 修改商品信息（内部方法，无权限验证）
     */
    private static String updateProductInternal(String name, Double price, Integer stock, Integer status) {
        if (name == null || name.trim().isEmpty()) {
            return "❌ 商品名称不能为空";
        }

        name = name.trim();

        // 检查商品是否存在
        if (!isProductExist(name)) {
            return "❌ 商品【" + name + "】不存在";
        }

        // 构建更新SQL
        StringBuilder sql = new StringBuilder("UPDATE product SET ");
        List<Object> params = new ArrayList<>();
        boolean hasUpdate = false;

        if (price != null && price > 0) {
            sql.append("price = ?, ");
            params.add(price);
            hasUpdate = true;
        }

        if (stock != null && stock >= 0) {
            sql.append("stock = ?, ");
            params.add(stock);
            hasUpdate = true;
        }

        if (status != null && (status == 0 || status == 1)) {
            sql.append("status = ?, ");
            params.add(status);
            hasUpdate = true;
        }

        if (!hasUpdate) {
            return "❌ 请指定要修改的参数（价格/库存/状态）";
        }

        // 移除最后的逗号和空格
        sql.setLength(sql.length() - 2);
        sql.append(" WHERE name = ?");
        params.add(name);

        try (Connection conn = DBUtil.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql.toString());
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            int rows = ps.executeUpdate();
            if (rows > 0) {
                // 获取修改后的信息
                String info = getProductInfo(name);
                return "✅ 修改成功：商品【" + name + "】信息已更新！\n" + info;
            } else {
                return "❌ 修改失败，商品可能不存在";
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "❌ 数据库错误：" + e.getMessage();
        }
    }

    /**
     * 删除商品（内部方法，无权限验证）
     */
    private static String deleteProductInternal(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "❌ 商品名称不能为空";
        }

        name = name.trim();

        // 检查商品是否存在
        if (!isProductExist(name)) {
            return "❌ 商品【" + name + "】不存在";
        }

        try (Connection conn = DBUtil.getConnection()) {
            // 先检查是否有关联订单
            String checkSql = "SELECT COUNT(*) FROM order_item oi " +
                    "JOIN product p ON oi.product_id = p.id " +
                    "WHERE p.name = ?";
            PreparedStatement checkPs = conn.prepareStatement(checkSql);
            checkPs.setString(1, name);
            ResultSet rs = checkPs.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                return "❌ 删除失败：该商品已有订单记录，不能删除";
            }

            // 执行删除
            String deleteSql = "DELETE FROM product WHERE name = ?";
            PreparedStatement ps = conn.prepareStatement(deleteSql);
            ps.setString(1, name);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                return "✅ 删除成功：商品【" + name + "】已移除！";
            } else {
                return "❌ 删除失败，商品可能不存在";
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return "❌ 数据库错误：" + e.getMessage();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 查询商品（内部方法，无权限验证）
     */
    private static String queryProductInternal(String keyword) {
        try (Connection conn = DBUtil.getConnection()) {
            String sql;
            PreparedStatement ps;

            if ("all".equalsIgnoreCase(keyword)) {
                sql = "SELECT id, name, category_id, price, stock, status, description " +
                        "FROM product ORDER BY create_time DESC LIMIT 20";
                ps = conn.prepareStatement(sql);
            } else {
                sql = "SELECT id, name, category_id, price, stock, status, description " +
                        "FROM product WHERE name LIKE ? OR description LIKE ? " +
                        "ORDER BY create_time DESC LIMIT 20";
                ps = conn.prepareStatement(sql);
                ps.setString(1, "%" + keyword + "%");
                ps.setString(2, "%" + keyword + "%");
            }

            ResultSet rs = ps.executeQuery();
            return buildQueryResult(rs, keyword);

        } catch (SQLException e) {
            e.printStackTrace();
            return "❌ 查询失败：" + e.getMessage();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // ========== 私有工具方法 ==========

    /**
     * 检查商品是否存在
     */
    private static boolean isProductExist(String name) {
        try (Connection conn = DBUtil.getConnection()) {
            // 使用更宽松的匹配
            String sql = "SELECT id FROM product WHERE LOWER(name) LIKE ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, "%" + name.toLowerCase() + "%");
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取商品详细信息
     */
    private static String getProductInfo(String name) {
        try (Connection conn = DBUtil.getConnection()) {
            String sql = "SELECT name, price, stock, status FROM product WHERE name = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                StringBuilder info = new StringBuilder();
                info.append("📊 最新信息：\n");
                info.append("商品：").append(rs.getString("name")).append("\n");
                info.append("价格：¥").append(String.format("%.2f", rs.getDouble("price"))).append("\n");
                info.append("库存：").append(rs.getInt("stock")).append("\n");
                info.append("状态：").append(rs.getInt("status") == 1 ? "上架" : "下架").append("\n");
                return info.toString();
            }
            return "";
        } catch (SQLException e) {
            e.printStackTrace();
            return "";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 构建查询结果
     */
    private static String buildQueryResult(ResultSet rs, String keyword) throws SQLException {
        StringBuilder result = new StringBuilder();
        int count = 0;

        while (rs.next()) {
            count++;
            result.append("【商品").append(count).append("】\n");
            result.append("ID：").append(rs.getInt("id")).append("\n");
            result.append("名称：").append(rs.getString("name")).append("\n");
            result.append("分类ID：").append(rs.getInt("category_id")).append("\n");
            result.append("价格：¥").append(String.format("%.2f", rs.getDouble("price"))).append("\n");
            result.append("库存：").append(rs.getInt("stock")).append("\n");
            result.append("状态：").append(rs.getInt("status") == 1 ? "上架" : "下架").append("\n");
            String desc = rs.getString("description");
            if (desc != null && !desc.isEmpty()) {
                result.append("描述：").append(desc.length() > 50 ? desc.substring(0, 50) + "..." : desc).append("\n");
            }
            result.append("------------------------\n");
        }

        if (count == 0) {
            return "🔍 未找到匹配的商品";
        } else {
            return "📋 找到 " + count + " 个商品：\n\n" + result.toString();
        }
    }

    /**
     * 从ResultSet构建Product对象
     */
    private static Product buildProductFromResultSet(ResultSet rs) throws SQLException {
        Product product = new Product();
        product.setId(rs.getInt("id"));
        product.setName(rs.getString("name"));
        product.setCategoryId(rs.getInt("category_id"));
        product.setPrice(rs.getDouble("price"));
        product.setStock(rs.getInt("stock"));
        product.setStatus(rs.getInt("status"));
        product.setDescription(rs.getString("description"));
        return product;
    }

    /**
     * 检查是否是管理员
     */
    private static boolean isAdmin(HttpSession session) {
        if (session == null) {
            return false;
        }

        // 检查session中是否有管理员标识
        Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
        if (isAdmin != null && isAdmin) {
            return true;
        }

        // 检查用户ID（简单示例）
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId != null && userId == 1) { // 假设用户ID为1的是管理员
            return true;
        }

        return false;
    }

    // ========== 兼容旧版本的方法（不推荐使用） ==========

    /**
     * 兼容旧版本的添加商品方法（不推荐使用）
     */
    @Deprecated
    public static String addProduct(String name, Integer categoryId, Double price,
                                    Integer stock, String description) {
        return addProductInternal(name, categoryId, price, stock, description);
    }

    /**
     * 兼容旧版本的修改商品方法（不推荐使用）
     */
    @Deprecated
    public static String updateProduct(String name, Double price, Integer stock, Integer status) {
        return updateProductInternal(name, price, stock, status);
    }

    /**
     * 兼容旧版本的删除商品方法（不推荐使用）
     */
    @Deprecated
    public static String deleteProduct(String name) {
        return deleteProductInternal(name);
    }
}