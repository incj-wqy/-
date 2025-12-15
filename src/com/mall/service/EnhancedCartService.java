package com.mall.service;

import com.mall.model.CartItem;
import com.mall.model.Product;
import com.mall.util.DBUtil;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class EnhancedCartService {

    /**
     * 添加到购物车（增强版）
     */
    public static String addToCart(HttpServletRequest request, HttpServletResponse response,
                                   String productName, int quantity) {
        if (productName == null || productName.trim().isEmpty()) {
            return "❌ 商品名称不能为空";
        }
        if (quantity <= 0) {
            return "❌ 数量必须大于0";
        }

        productName = productName.trim();

        try {
            // 1. 获取商品信息
            Product product = findProductByName(productName);
            if (product == null) {
                return "❌ 未找到商品【" + productName + "】";
            }
            if (product.getStatus() != 1) {
                return "❌ 商品【" + productName + "】已下架";
            }
            if (product.getStock() < quantity) {
                return "❌ 库存不足！当前库存：" + product.getStock();
            }

            // 2. 获取购物车
            HttpSession session = request.getSession();
            String anonymousId = getAnonymousId(request, response);
            Map<Integer, CartItem> cart = getCart(session, anonymousId);

            // 3. 添加或更新购物车项
            CartItem item = cart.get(product.getId());
            if (item == null) {
                item = new CartItem(product, quantity);
                cart.put(product.getId(), item);
            } else {
                int newQuantity = item.getQuantity() + quantity;
                if (newQuantity > product.getStock()) {
                    newQuantity = product.getStock();
                }
                item.setQuantity(newQuantity);
            }

            // 4. 持久化
            persistCart(session, response, anonymousId, cart);

            return "✅ 已将【" + productName + "】x" + quantity + " 加入购物车！";

        } catch (Exception e) {
            e.printStackTrace();
            return "❌ 操作失败：" + e.getMessage();
        }
    }

    /**
     * 从购物车删除
     */
    public static String deleteFromCart(HttpServletRequest request, HttpServletResponse response,
                                        String productName) {
        if (productName == null || productName.trim().isEmpty()) {
            return "❌ 商品名称不能为空";
        }

        productName = productName.trim();

        try {
            // 1. 获取商品信息
            Product product = findProductByName(productName);
            if (product == null) {
                return "❌ 未找到商品【" + productName + "】";
            }

            // 2. 获取购物车
            HttpSession session = request.getSession();
            String anonymousId = getAnonymousId(request, response);
            Map<Integer, CartItem> cart = getCart(session, anonymousId);

            // 3. 删除
            CartItem removed = cart.remove(product.getId());
            if (removed != null) {
                // 4. 持久化
                persistCart(session, response, anonymousId, cart);
                return "✅ 已将【" + productName + "】从购物车移除";
            } else {
                return "❌ 购物车中没有【" + productName + "】";
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "❌ 操作失败：" + e.getMessage();
        }
    }

    /**
     * 清空购物车
     */
    public static String clearCart(HttpServletRequest request, HttpServletResponse response) {
        try {
            HttpSession session = request.getSession();
            String anonymousId = getAnonymousId(request, response);
            Integer userId = (Integer) session.getAttribute("userId");

            String cartKey = userId != null ? "cart_" + userId : "cart_anonymous_" + anonymousId;

            // 从session移除
            session.removeAttribute(cartKey);

            // 如果是匿名用户，删除cookie
            if (userId == null) {
                removeCookie(response, "cart_" + cartKey);
            }

            return "✅ 购物车已清空！";

        } catch (Exception e) {
            e.printStackTrace();
            return "❌ 清空失败：" + e.getMessage();
        }
    }

    /**
     * 查看购物车
     */
    public static String viewCart(HttpServletRequest request, HttpServletResponse response) {
        try {
            HttpSession session = request.getSession();
            String anonymousId = getAnonymousId(request, response);
            Map<Integer, CartItem> cart = getCart(session, anonymousId);

            if (cart.isEmpty()) {
                return "🛒 购物车是空的";
            }

            StringBuilder result = new StringBuilder();
            result.append("🛒 您的购物车：\n");
            result.append("========================\n");

            double total = 0;
            int totalItems = 0;

            for (CartItem item : cart.values()) {
                Product product = item.getProduct();
                double subtotal = item.getSubtotal();

                result.append("商品：").append(product.getName()).append("\n");
                result.append("单价：¥").append(String.format("%.2f", product.getPrice())).append("\n");
                result.append("数量：").append(item.getQuantity()).append("\n");
                result.append("小计：¥").append(String.format("%.2f", subtotal)).append("\n");
                result.append("------------------------\n");

                total += subtotal;
                totalItems += item.getQuantity();
            }

            result.append("总计：").append(totalItems).append(" 件商品\n");
            result.append("金额：¥").append(String.format("%.2f", total)).append("\n");

            return result.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "❌ 查看购物车失败：" + e.getMessage();
        }
    }

    /**
     * 修改购物车商品数量
     */
    public static String updateCartQuantity(HttpServletRequest request, HttpServletResponse response,
                                            String productName, int newQuantity) {
        if (productName == null || productName.trim().isEmpty()) {
            return "❌ 商品名称不能为空";
        }
        if (newQuantity < 0) {
            return "❌ 数量不能为负数";
        }

        productName = productName.trim();

        try {
            // 1. 获取商品信息
            Product product = findProductByName(productName);
            if (product == null) {
                return "❌ 未找到商品【" + productName + "】";
            }

            // 2. 获取购物车
            HttpSession session = request.getSession();
            String anonymousId = getAnonymousId(request, response);
            Map<Integer, CartItem> cart = getCart(session, anonymousId);

            // 3. 更新数量
            CartItem item = cart.get(product.getId());
            if (item == null) {
                return "❌ 购物车中没有【" + productName + "】";
            }

            if (newQuantity == 0) {
                // 数量为0时删除
                cart.remove(product.getId());
                persistCart(session, response, anonymousId, cart);
                return "✅ 已从购物车移除【" + productName + "】";
            } else if (newQuantity > product.getStock()) {
                return "❌ 库存不足！当前库存：" + product.getStock();
            } else {
                item.setQuantity(newQuantity);
                persistCart(session, response, anonymousId, cart);
                return "✅ 已将【" + productName + "】数量修改为 " + newQuantity;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "❌ 操作失败：" + e.getMessage();
        }
    }

    // ========== 私有工具方法 ==========

    /**
     * 根据名称查找商品
     */
    /**
     * 根据名称查找商品（增强版）
     */
    private static Product findProductByName(String productName) {
        if (productName == null || productName.trim().isEmpty()) {
            return null;
        }

        String name = productName.trim();

        // 尝试多种匹配方式
        Product product = ProductService.findProductByName(name);

        if (product == null) {
            // 尝试移除修饰词
            String simplifiedName = name
                    .replace("手机", "")
                    .replace("的", "")
                    .replace("把", "")
                    .replace("将", "")
                    .trim();

            if (!simplifiedName.equals(name)) {
                product = ProductService.findProductByName(simplifiedName);
            }
        }

        return product;
    }

    /**
     * 获取购物车
     */
    @SuppressWarnings("unchecked")
    private static Map<Integer, CartItem> getCart(HttpSession session, String anonymousId) {
        if (session == null || anonymousId == null) {
            return new LinkedHashMap<>();
        }

        Integer userId = (Integer) session.getAttribute("userId");
        String cartKey = userId != null ? "cart_" + userId : "cart_anonymous_" + anonymousId;

        Map<Integer, CartItem> cart = (Map<Integer, CartItem>) session.getAttribute(cartKey);
        if (cart == null) {
            cart = new LinkedHashMap<>();
            session.setAttribute(cartKey, cart);
        }
        return cart;
    }

    /**
     * 获取匿名用户ID
     */
    private static String getAnonymousId(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("anonymous_id".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        // 生成新的匿名ID
        String newId = UUID.randomUUID().toString().replace("-", "");
        Cookie cookie = new Cookie("anonymous_id", newId);
        cookie.setMaxAge(365 * 24 * 60 * 60); // 1年
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        response.addCookie(cookie);

        return newId;
    }

    /**
     * 持久化购物车
     */
    private static void persistCart(HttpSession session, HttpServletResponse response,
                                    String anonymousId, Map<Integer, CartItem> cart) {
        Integer userId = (Integer) session.getAttribute("userId");

        if (userId != null) {
            // 登录用户：保存到数据库（简化版，实际应该保存到数据库）
            // 这里只保存到session
            String cartKey = "cart_" + userId;
            session.setAttribute(cartKey, cart);
        } else {
            // 匿名用户：保存到session和cookie
            String cartKey = "cart_anonymous_" + anonymousId;
            session.setAttribute(cartKey, cart);

            // 序列化到cookie
            if (cart.isEmpty()) {
                removeCookie(response, "cart_" + cartKey);
            } else {
                List<Map<String, Object>> cartData = new ArrayList<>();
                for (CartItem item : cart.values()) {
                    Map<String, Object> itemData = new HashMap<>();
                    itemData.put("productId", item.getProduct().getId());
                    itemData.put("quantity", item.getQuantity());
                    cartData.add(itemData);
                }

                String cartJson = com.alibaba.fastjson.JSON.toJSONString(cartData);
                Cookie cookie = new Cookie("cart_" + cartKey, cartJson);
                cookie.setMaxAge(7 * 24 * 60 * 60); // 7天
                cookie.setPath("/");
                cookie.setHttpOnly(true);
                response.addCookie(cookie);
            }
        }
    }

    /**
     * 删除cookie
     */
    private static void removeCookie(HttpServletResponse response, String name) {
        Cookie cookie = new Cookie(name, "");
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}