package com.mall.service;

import com.mall.util.DBUtil;
import java.sql.*;

public class OrderService {

    /**
     * 查询订单
     */
    public static String queryOrder(String orderNo) {
        if (orderNo == null || orderNo.trim().isEmpty()) {
            return "❌ 订单号不能为空";
        }

        orderNo = orderNo.trim();

        try (Connection conn = DBUtil.getConnection()) {
            // 查询订单基本信息
            String sql = "SELECT o.id, o.order_no, o.total_amount, o.status, o.create_time, " +
                    "u.username as customer_name " +
                    "FROM orders o " +
                    "LEFT JOIN user u ON o.user_id = u.id " +
                    "WHERE o.order_no = ?";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, orderNo);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                StringBuilder result = new StringBuilder();
                result.append("📦 订单查询结果：\n");
                result.append("========================\n");
                result.append("订单号：").append(rs.getString("order_no")).append("\n");
                result.append("客户：").append(rs.getString("customer_name")).append("\n");
                result.append("总金额：¥").append(String.format("%.2f", rs.getDouble("total_amount"))).append("\n");
                result.append("状态：").append(getStatusText(rs.getString("status"))).append("\n");
                result.append("下单时间：").append(rs.getTimestamp("create_time")).append("\n");

                // 查询订单商品
                String itemSql = "SELECT p.name, oi.price, oi.quantity, oi.subtotal " +
                        "FROM order_item oi " +
                        "JOIN product p ON oi.product_id = p.id " +
                        "WHERE oi.order_id = ?";

                PreparedStatement itemPs = conn.prepareStatement(itemSql);
                itemPs.setInt(1, rs.getInt("id"));
                ResultSet itemRs = itemPs.executeQuery();

                result.append("\n📋 订单商品：\n");
                int itemCount = 0;
                while (itemRs.next()) {
                    itemCount++;
                    result.append(itemCount).append(". ").append(itemRs.getString("name")).append("\n");
                    result.append("   单价：¥").append(String.format("%.2f", itemRs.getDouble("price"))).append("\n");
                    result.append("   数量：").append(itemRs.getInt("quantity")).append("\n");
                    result.append("   小计：¥").append(String.format("%.2f", itemRs.getDouble("subtotal"))).append("\n");
                }

                if (itemCount == 0) {
                    result.append("（暂无商品信息）\n");
                }

                return result.toString();

            } else {
                return "❌ 未找到订单【" + orderNo + "】，请核对订单号";
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return "❌ 查询失败：" + e.getMessage();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取订单状态文本
     */
    private static String getStatusText(String statusCode) {
        switch (statusCode) {
            case "pending": return "待付款";
            case "paid": return "已付款";
            case "shipped": return "已发货";
            case "delivered": return "已送达";
            case "cancelled": return "已取消";
            case "refunded": return "已退款";
            default: return statusCode;
        }
    }
}