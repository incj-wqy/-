package com.mall.web.front;

import com.mall.service.CartService;
import com.mall.util.ChatMemoryUtil;
import com.mall.util.DBUtil;
import com.mall.util.LLMClientUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebServlet("/front/chat")
public class ChatServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");
        PrintWriter out = response.getWriter();

        String userMsg = request.getParameter("message");
        String orderNo = request.getParameter("orderNo");
        String replyMsg = "";

        if (userMsg == null || userMsg.trim().isEmpty()) {
            replyMsg = "请输入有效指令或问题！";
        } else {
            userMsg = userMsg.trim();

            // ========== 1. 商品操作指令（结构化） ==========
            if (userMsg.startsWith("添加商品")) {
                replyMsg = addProduct(userMsg);
            } else if (userMsg.startsWith("删除商品")) {
                replyMsg = deleteProduct(userMsg);
            } else if (userMsg.startsWith("修改商品")) {
                replyMsg = updateProduct(userMsg);
            } else if (userMsg.startsWith("查询商品")) {
                replyMsg = queryProduct(userMsg);
            }
            // ========== 2. 订单查询 ==========
            else if (userMsg.contains("订单") && orderNo != null && !orderNo.isEmpty()) {
                replyMsg = queryOrder(orderNo);
            }
            // ========== 3. 商品自然语言 ==========
            else if (isProductIntent(userMsg)) {
                replyMsg = handleProductIntent(userMsg, request, response);
            }
            // ========== 4. 购物车自然语言 ==========
            else if (isCartIntent(userMsg)) {
                replyMsg = handleCartIntent(userMsg, request, response);
            }
            // ========== 5. 通用聊天 ==========
            else {
                try {
                    HttpSession session = request.getSession();
                    List<Map<String, String>> history = ChatMemoryUtil.getHistory(session);
                    ChatMemoryUtil.addHistory(session, ChatMemoryUtil.ROLE_USER, userMsg);
                    String chatReply = LLMClientUtil.callLLM(userMsg, history);
                    ChatMemoryUtil.addHistory(session, ChatMemoryUtil.ROLE_ASSISTANT, chatReply);
                    replyMsg = chatReply;
                } catch (Exception e) {
                    e.printStackTrace();
                    replyMsg = "抱歉，服务暂时不可用。";
                }
            }

            saveChatRecord(userMsg, replyMsg, orderNo);
        }

        out.write("{\"code\":1,\"data\":\"" +
                replyMsg.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n") +
                "\",\"msg\":\"success\"}");
        out.close();
    }

    // ========== 意图识别 ==========

    private boolean isProductIntent(String msg) {
        return msg.contains("商品") || msg.contains("价格") || msg.contains("修改") ||
                msg.contains("添加") || msg.contains("删除") || msg.contains("查询") ||
                msg.contains("库存") || msg.contains("上架") || msg.contains("下架");
    }

    private boolean isCartIntent(String msg) {
        return msg.contains("购物车") || msg.contains("加入") || msg.contains("加") ||
                msg.contains("删除") || msg.contains("移除") || msg.contains("清空") ||
                msg.matches(".*\\d+.*个.*");
    }

    // ========== 商品自然语言处理（内嵌完整 prompt） ==========

    private String handleProductIntent(String userMsg, HttpServletRequest request, HttpServletResponse response) {
        // ✅ 内嵌完整提示词，支持上架/下架
        String systemPrompt = """
你是一个 SimpleMall 商城的后台操作助手，拥有商品管理权限。
请将用户自然语言严格转换为 JSON，包含 action 和参数。

## 商品状态说明
- "上架" 或 "恢复销售" → status: 1
- "下架" 或 "停止销售" → status: 0

## 支持的操作
- "add": 添加商品（需 name, categoryId, price, stock, description）
- "delete": 删除商品（需 name）
- "update": 修改商品（需 name, price, stock, status）
- "query": 查询商品（需 name 或 "all"）

## 输出格式（严格 JSON，无其他文字）
{"action":"update","name":"REDMI Turbo 4 Pro","price":800}

## 示例
输入：把 REDMI Turbo 4 Pro 的价格改成 800
输出：{"action":"update","name":"REDMI Turbo 4 Pro","price":800}

输入：把 REDMI Turbo 4 Pro 下架
输出：{"action":"update","name":"REDMI Turbo 4 Pro","status":0}

输入：将 iPhone 16 上架
输出：{"action":"update","name":"iPhone 16","status":1}

输入：删除华为 Mate 60
输出：{"action":"delete","name":"华为 Mate 60"}

输入：添加小米14 分类1 价格3999 库存50 描述：旗舰手机
输出：{"action":"add","name":"小米14","categoryId":1,"price":3999,"stock":50,"description":"旗舰手机"}

输入：查询所有商品
输出：{"action":"query","name":"all"}

## 重要规则
1. 只输出 JSON，不添加任何解释、注释或额外文字
2. 参数缺失时可省略该字段，但 name 必须存在
3. 确保 JSON 语法正确

现在解析以下输入：
""";

        try {
            List<Map<String, String>> fakeHistory = new ArrayList<>();
            fakeHistory.add(Map.of("role", "user", "content", systemPrompt + userMsg));

            String llmResponse = LLMClientUtil.callLLM("", fakeHistory);
            JSONObject cmd = JSON.parseObject(llmResponse.trim());
            String action = cmd.getString("action");

            if ("update".equals(action)) {
                String name = cmd.getString("name");
                Double price = cmd.getDouble("price");
                Integer stock = cmd.getInteger("stock");
                Integer status = cmd.getInteger("status");

                if (name == null || (price == null && stock == null && status == null)) {
                    return "请指定商品名称和要修改的参数（价格/库存/状态）。例如：“把 REDMI Turbo 4 Pro 下架”";
                }

                StringBuilder simMsg = new StringBuilder("修改商品 ").append(name);
                if (price != null) simMsg.append(" 价格").append(price.intValue());
                if (stock != null) simMsg.append(" 库存").append(stock);
                if (status != null) simMsg.append(" 状态").append(status);

                return updateProduct(simMsg.toString());

            } else if ("delete".equals(action)) {
                String name = cmd.getString("name");
                if (name == null) return "请指定商品名称";
                return deleteProduct("删除商品 " + name);

            } else if ("add".equals(action)) {
                String name = cmd.getString("name");
                Integer categoryId = cmd.getInteger("categoryId");
                Double price = cmd.getDouble("price");
                Integer stock = cmd.getInteger("stock");
                String description = cmd.getString("description");

                if (name == null || price == null || stock == null) {
                    return "添加商品需提供名称、价格、库存";
                }

                StringBuilder simMsg = new StringBuilder("添加商品 ").append(name)
                        .append(" 分类").append(categoryId != null ? categoryId : 0)
                        .append(" 价格").append(price.intValue())
                        .append(" 库存").append(stock);
                if (description != null && !description.isEmpty()) {
                    simMsg.append(" 描述：").append(description);
                }

                return addProduct(simMsg.toString());

            } else if ("query".equals(action)) {
                String name = cmd.getString("name");
                if ("all".equals(name)) {
                    return queryProduct("查询商品 所有");
                } else {
                    return queryProduct("查询商品 " + name);
                }

            } else {
                return "抱歉，不支持该商品操作";
            }
        } catch (Exception e) {
            return "抱歉，我没理解您的商品操作。例如：“把 REDMI Turbo 4 Pro 下架”";
        }
    }

    // ========== 购物车自然语言处理（内嵌 prompt） ==========

    private String handleCartIntent(String userMsg, HttpServletRequest request, HttpServletResponse response) {
        String systemPrompt = """
你是一个 SimpleMall 商城的指令解析器，请严格按以下规则输出：

## 任务
将用户自然语言转换为 JSON，只包含 action 和参数。

## 支持的 action
- "add": 添加商品（需 productName, quantity）
- "delete": 删除商品（需 productName）
- "clear": 清空购物车（无需参数）

## 输出格式（严格 JSON，无其他文字）
{"action":"add","productName":"iPhone 16","quantity":2}

## 示例
输入：把 REDMI Turbo 4 Pro 加 2 个到购物车
输出：{"action":"add","productName":"REDMI Turbo 4 Pro","quantity":2}

输入：删除购物车里的华为手机
输出：{"action":"delete","productName":"华为手机"}

输入：清空我的购物车
输出：{"action":"clear"}

## 重要规则
1. 只输出 JSON
2. quantity 默认为 1
3. productName 必须完整提取

现在解析以下输入：
""";

        try {
            List<Map<String, String>> fakeHistory = new ArrayList<>();
            fakeHistory.add(Map.of("role", "user", "content", systemPrompt + userMsg));

            String llmResponse = LLMClientUtil.callLLM("", fakeHistory);
            JSONObject cmd = JSON.parseObject(llmResponse.trim());
            String action = cmd.getString("action");

            if ("add".equals(action)) {
                String productName = cmd.getString("productName");
                int quantity = cmd.getIntValue("quantity");
                if (productName == null || quantity <= 0) {
                    return "请指定商品名称和数量，例如：加 iPhone 16 2 个到购物车";
                }
                return CartService.addToCart(request, response, productName, quantity);
            } else if ("delete".equals(action)) {
                String productName = cmd.getString("productName");
                if (productName == null) {
                    return "请指定要删除的商品名称";
                }
                return CartService.deleteFromCart(request, response, productName);
            } else if ("clear".equals(action)) {
                return CartService.clearCart(request, response);
            } else {
                return "抱歉，不支持该购物车操作";
            }
        } catch (Exception e) {
            return "抱歉，我没理解您的购物车操作。例如：“加 REDMI Turbo 4 Pro 2 个到购物车”";
        }
    }

    // ========== 商品结构化指令方法（保持不变） ==========

    private String addProduct(String userMsg) {
        String name = extractParam(userMsg, "添加商品\\s+(.*?)(?=\\s+分类|\\s+价格|\\s+库存|\\s+描述|$)");
        Integer categoryId = extractNumParam(userMsg, "分类[:：]?\\s*(\\d+)");
        Double price = extractDoubleParam(userMsg, "价格[:：]?\\s*(\\d+\\.?\\d*)");
        Integer stock = extractNumParam(userMsg, "库存[:：]?\\s*(\\d+)");
        String description = extractParam(userMsg, "描述[:：]?\\s*(.*)");

        if (name == null || name.isEmpty() || price == null || stock == null) {
            return "添加失败：商品名称、价格、库存为必填项！";
        }
        if (isProductExist(name)) {
            return "添加失败：商品【" + name + "】已存在！";
        }

        try (Connection conn = DBUtil.getConnection()) {
            String sql = "INSERT INTO product(name,category_id,price,stock,status,image,images,description) VALUES(?,?,?,?,?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, name);
            ps.setInt(2, categoryId == null ? 0 : categoryId);
            ps.setDouble(3, price);
            ps.setInt(4, stock);
            ps.setInt(5, 1);
            ps.setString(6, "");
            ps.setString(7, "");
            ps.setString(8, description == null ? "" : description);
            ps.executeUpdate();
            return "添加成功：商品【" + name + "】已入库！";
        } catch (SQLException e) {
            e.printStackTrace();
            return "添加失败：" + e.getMessage();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String deleteProduct(String userMsg) {
        String name = extractParam(userMsg, "删除商品\\s+(.+)");
        if (name == null || name.isEmpty()) {
            return "删除失败：请指定要删除的商品名称！";
        }
        if (!isProductExist(name)) {
            return "删除失败：商品【" + name + "】不存在！";
        }

        try (Connection conn = DBUtil.getConnection()) {
            String sql = "DELETE FROM product WHERE name=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, name);
            ps.executeUpdate();
            return "删除成功：商品【" + name + "】已移除！";
        } catch (SQLException e) {
            e.printStackTrace();
            return "删除失败：" + e.getMessage();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String updateProduct(String userMsg) {
        if (!userMsg.startsWith("修改商品")) {
            return "修改失败：指令格式错误！";
        }
        String rest = userMsg.substring("修改商品".length()).trim();
        if (rest.isEmpty()) {
            return "修改失败：请指定商品名称和修改参数！";
        }

        int priceIndex = findKeywordIndex(rest, "价格");
        int stockIndex = findKeywordIndex(rest, "库存");
        int statusIndex = findKeywordIndex(rest, "状态");

        int firstParamIndex = Integer.MAX_VALUE;
        if (priceIndex >= 0) firstParamIndex = Math.min(firstParamIndex, priceIndex);
        if (stockIndex >= 0) firstParamIndex = Math.min(firstParamIndex, stockIndex);
        if (statusIndex >= 0) firstParamIndex = Math.min(firstParamIndex, statusIndex);

        String name, paramPart;
        if (firstParamIndex == Integer.MAX_VALUE) {
            name = rest;
            paramPart = "";
        } else {
            name = rest.substring(0, firstParamIndex).trim();
            paramPart = rest.substring(firstParamIndex).trim();
        }

        if (name.isEmpty()) {
            return "修改失败：请指定要修改的商品名称！";
        }
        if (!isProductExist(name)) {
            return "修改失败：商品【" + name + "】不存在！";
        }

        Double price = extractDoubleParamFlexible(paramPart, "价格");
        Integer stock = extractNumParamFlexible(paramPart, "库存");
        Integer status = extractNumParamFlexible(paramPart, "状态");

        StringBuilder sql = new StringBuilder("UPDATE product SET ");
        List<Object> params = new ArrayList<>();
        boolean hasParam = false;

        if (price != null) {
            sql.append("price=?, ");
            params.add(price);
            hasParam = true;
        }
        if (stock != null) {
            sql.append("stock=?, ");
            params.add(stock);
            hasParam = true;
        }
        if (status != null) {
            sql.append("status=?, ");
            params.add(status);
            hasParam = true;
        }

        if (!hasParam) {
            return "修改失败：请指定要修改的参数（价格/库存/状态）！";
        }

        sql.setLength(sql.length() - 2);
        sql.append(" WHERE name=?");
        params.add(name);

        try (Connection conn = DBUtil.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql.toString());
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            ps.executeUpdate();
            return "修改成功：商品【" + name + "】信息已更新！";
        } catch (SQLException e) {
            e.printStackTrace();
            return "修改失败：" + e.getMessage();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String queryProduct(String userMsg) {
        if (userMsg.contains("所有")) {
            try (Connection conn = DBUtil.getConnection()) {
                String sql = "SELECT * FROM product ORDER BY create_time DESC LIMIT 10";
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery();
                return buildProductResult(rs);
            } catch (SQLException e) {
                e.printStackTrace();
                return "查询失败：" + e.getMessage();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            String name = extractParam(userMsg, "查询商品\\s+(.+)");
            if (name == null || name.isEmpty()) {
                return "查询失败：请指定商品名称！";
            }
            try (Connection conn = DBUtil.getConnection()) {
                String sql = "SELECT * FROM product WHERE name LIKE ?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, "%" + name + "%");
                ResultSet rs = ps.executeQuery();
                return buildProductResult(rs);
            } catch (SQLException e) {
                e.printStackTrace();
                return "查询失败：" + e.getMessage();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private String queryOrder(String orderNo) {
        try (Connection conn = DBUtil.getConnection()) {
            String sql = "SELECT status FROM orders WHERE order_no=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, orderNo);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return "你的订单【" + orderNo + "】当前状态：" + rs.getString("status") + "！";
            } else {
                return "未查询到订单【" + orderNo + "】，请核对订单号！";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "订单查询失败：" + e.getMessage();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // ========== 辅助方法 ==========

    private boolean isProductExist(String name) {
        try (Connection conn = DBUtil.getConnection()) {
            String sql = "SELECT id FROM product WHERE name=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String buildProductResult(ResultSet rs) throws SQLException {
        StringBuilder result = new StringBuilder("查询结果：\n");
        int count = 0;
        while (rs.next()) {
            count++;
            result.append("【商品").append(count).append("】\n");
            result.append("名称：").append(rs.getString("name")).append("\n");
            result.append("分类ID：").append(rs.getInt("category_id")).append("\n");
            result.append("价格：¥").append(rs.getDouble("price")).append("\n");
            result.append("库存：").append(rs.getInt("stock")).append("\n");
            result.append("状态：").append(rs.getInt("status") == 1 ? "上架" : "下架").append("\n");
            result.append("------------------------\n");
        }
        if (count == 0) {
            return "查询结果：无匹配商品！";
        }
        return result.toString();
    }

    private String extractParam(String content, String regex) {
        if (content == null || regex == null) return null;
        Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return null;
    }

    private Integer extractNumParam(String userMsg, String regex) {
        if (userMsg == null || regex == null) return null;
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(userMsg);
        if (matcher.find()) {
            try {
                return Integer.parseInt(matcher.group(1).trim());
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    private Double extractDoubleParam(String userMsg, String regex) {
        if (userMsg == null || regex == null) return null;
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(userMsg);
        if (matcher.find()) {
            try {
                return Double.parseDouble(matcher.group(1).trim());
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    private int findKeywordIndex(String text, String keyword) {
        String regex = Pattern.quote(keyword) + "[\\s:：为]*\\d";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(text);
        if (m.find()) {
            return m.start();
        }
        return -1;
    }

    private Integer extractNumParamFlexible(String text, String keyword) {
        if (text == null || text.isEmpty()) return null;
        String regex = Pattern.quote(keyword) + "[\\s:：为]*(\\d+)";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(text);
        if (m.find()) {
            try {
                return Integer.parseInt(m.group(1));
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    private Double extractDoubleParamFlexible(String text, String keyword) {
        if (text == null || text.isEmpty()) return null;
        String regex = Pattern.quote(keyword) + "[\\s:：为]*(\\d+\\.?\\d*)";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(text);
        if (m.find()) {
            try {
                return Double.parseDouble(m.group(1));
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    private void saveChatRecord(String userMsg, String replyMsg, String orderNo) {
        // 如需记录日志，可取消注释
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }
}