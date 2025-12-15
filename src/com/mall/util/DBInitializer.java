package com.mall.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DBInitializer {

    static {
        initDefaultProducts();
    }

    public static void initDefaultProducts() {
        try (Connection conn = DBUtil.getConnection()) {
            // 检查是否需要初始化
            String checkSql = "SELECT COUNT(*) as count FROM product";
            PreparedStatement checkPs = conn.prepareStatement(checkSql);
            ResultSet rs = checkPs.executeQuery();

            if (rs.next() && rs.getInt("count") == 0) {
                System.out.println("初始化默认商品数据...");

                // 添加默认商品
                String[] insertSqls = {
                        "INSERT INTO product(name, price, stock, status, description, create_time) " +
                                "VALUES ('REDMI Turbo 4 Pro', 800.0, 100, 1, '热门手机，性能强劲', NOW())",

                        "INSERT INTO product(name, price, stock, status, description, create_time) " +
                                "VALUES ('iPhone 16', 6999.0, 50, 1, '苹果旗舰手机', NOW())",

                        "INSERT INTO product(name, price, stock, status, description, create_time) " +
                                "VALUES ('华为 Mate 60', 5999.0, 80, 1, '华为旗舰手机', NOW())"
                };

                for (String sql : insertSqls) {
                    PreparedStatement ps = conn.prepareStatement(sql);
                    ps.executeUpdate();
                    ps.close();
                }

                System.out.println("✅ 默认商品数据初始化完成");
            }

        } catch (Exception e) {
            System.err.println("❌ 初始化默认商品数据失败：" + e.getMessage());
        }
    }
}