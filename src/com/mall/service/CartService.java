package com.mall.service;

import com.mall.model.CartItem;
import com.mall.model.Product;
import com.mall.util.DBUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class CartService {

    // 模糊匹配商品 ID（支持部分匹配）
    public static int findProductIdByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return -1;
        }
        String cleanName = name.trim();
        try (Connection conn = DBUtil.getConnection()) {
            String sql = "SELECT id FROM product WHERE name LIKE ? AND status = 1 ORDER BY id LIMIT 1";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, "%" + cleanName + "%");
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return -1;
    }

    public static Product getProductById(int productId) {
        try (Connection conn = DBUtil.getConnection()) {
            String sql = "SELECT id, name, price, stock, status FROM product WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, productId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Product p = new Product();
                p.setId(rs.getInt("id"));
                p.setName(rs.getString("name"));
                p.setPrice(rs.getDouble("price"));
                p.setStock(rs.getInt("stock"));
                p.setStatus(rs.getInt("status"));
                return p;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static Map<Integer, CartItem> getCart(HttpSession session, String anonymousId) {
        if (session == null || anonymousId == null) {
            throw new IllegalArgumentException("Session 或 anonymousId 不能为空");
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

    public static void persistCartToCookie(HttpServletResponse response, HttpSession session, String anonymousId) {
        if (response == null || session == null || anonymousId == null) return;

        Integer userId = (Integer) session.getAttribute("userId");
        if (userId != null) return; // 登录用户建议存 DB，此处简化

        String cartKey = "cart_anonymous_" + anonymousId;
        @SuppressWarnings("unchecked")
        Map<Integer, CartItem> cart = (Map<Integer, CartItem>) session.getAttribute(cartKey);
        if (cart == null || cart.isEmpty()) {
            removeCookie(response, "cart_" + cartKey);
            return;
        }

        String cartJson = com.alibaba.fastjson.JSON.toJSONString(new ArrayList<>(cart.values()));
        javax.servlet.http.Cookie cookie = new javax.servlet.http.Cookie("cart_" + cartKey, cartJson);
        cookie.setMaxAge(7 * 24 * 60 * 60);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }

    private static void removeCookie(HttpServletResponse response, String name) {
        if (response == null || name == null) return;
        javax.servlet.http.Cookie cookie = new javax.servlet.http.Cookie(name, "");
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    // ========== 对外公开的操作方法（供 ChatServlet 调用） ==========

    public static String addToCart(HttpServletRequest request, HttpServletResponse response,
                                   String productName, int quantity) {
        if (request == null || response == null || productName == null || quantity <= 0) {
            return "参数无效，请提供商品名称和有效数量";
        }

        try {
            HttpSession session = request.getSession();
            String anonymousId = getAnonymousId(request, response);

            int pid = findProductIdByName(productName);
            if (pid <= 0) {
                return "未找到商品【" + productName + "】，请确认名称是否正确";
            }

            Product p = getProductById(pid);
            if (p == null || p.getStatus() != 1) {
                return "商品【" + productName + "】已下架或不存在";
            }
            if (p.getStock() < quantity) {
                return "库存不足！当前库存为 " + p.getStock() + "，无法添加 " + quantity + " 件";
            }

            Map<Integer, CartItem> cart = getCart(session, anonymousId);
            CartItem item = cart.get(pid);
            if (item == null) {
                item = new CartItem(p, quantity);
                cart.put(pid, item);
            } else {
                int newQty = Math.min(item.getQuantity() + quantity, p.getStock());
                item.setQuantity(newQty);
            }

            persistCartToCookie(response, session, anonymousId);
            return "✅ 已将【" + productName + "】x" + quantity + " 加入购物车！";
        } catch (Exception e) {
            e.printStackTrace();
            return "操作失败：" + e.getMessage();
        }
    }

    public static String deleteFromCart(HttpServletRequest request, HttpServletResponse response, String productName) {
        if (request == null || response == null || productName == null) {
            return "参数无效";
        }

        try {
            HttpSession session = request.getSession();
            String anonymousId = getAnonymousId(request, response);
            int pid = findProductIdByName(productName);
            if (pid <= 0) {
                return "未找到商品【" + productName + "】";
            }

            Map<Integer, CartItem> cart = getCart(session, anonymousId);
            if (cart.remove(pid) != null) {
                persistCartToCookie(response, session, anonymousId);
                return "✅ 已将【" + productName + "】从购物车移除";
            } else {
                return "购物车中没有【" + productName + "】";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "删除失败：" + e.getMessage();
        }
    }

    public static String clearCart(HttpServletRequest request, HttpServletResponse response) {
        if (request == null || response == null) return "参数无效";

        try {
            HttpSession session = request.getSession();
            String anonymousId = getAnonymousId(request, response);
            Integer userId = (Integer) session.getAttribute("userId");
            String cartKey = userId != null ? "cart_" + userId : "cart_anonymous_" + anonymousId;

            session.removeAttribute(cartKey);
            if (userId == null) {
                removeCookie(response, "cart_" + cartKey);
            }
            return "✅ 购物车已清空！";
        } catch (Exception e) {
            e.printStackTrace();
            return "清空失败：" + e.getMessage();
        }
    }

    // ========== 工具方法 ==========

    private static String getAnonymousId(HttpServletRequest request, HttpServletResponse response) {
        if (request == null || response == null) {
            return UUID.randomUUID().toString().replace("-", "");
        }
        javax.servlet.http.Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (javax.servlet.http.Cookie cookie : cookies) {
                if ("anonymous_id".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        String id = UUID.randomUUID().toString().replace("-", "");
        javax.servlet.http.Cookie cookie = new javax.servlet.http.Cookie("anonymous_id", id);
        cookie.setMaxAge(365 * 24 * 60 * 60);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
        return id;
    }
}