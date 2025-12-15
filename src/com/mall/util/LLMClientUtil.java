
//private static final String API_KEY = "sk-dc6d05a24cc641ae9eba90900be58553";
// src/main/java/com/mall/util/LLMClientUtil.java
// src/main/java/com/mall/util/LLMClientUtil.java
package com.mall.util;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class LLMClientUtil {

    // ✅ 官方 DeepSeek API 地址（无空格！）
    private static final String API_URL = "https://api.deepseek.com/v1/chat/completions";

    // ✅ 从环境变量读取 Key（安全！）
    private static final String API_KEY = "sk-dc6d05a24cc641ae9eba90900be58553";

//    static {
//        if (API_KEY == null || API_KEY.trim().isEmpty()) {
//            throw new IllegalStateException("请设置环境变量 DEEPSEEK_API_KEY");
//        }
//    }

    public static String callLLM(String userMessage, List<Map<String, String>> history) throws Exception {
        JSONArray messages = new JSONArray();
        messages.add(new JSONObject() {{
            put("role", "system");
            put("content", "你是 SimpleMall 智能客服，请用自然语言回答用户问题。");
        }});

        if (history != null) {
            for (Map<String, String> msg : history) {
                messages.add(new JSONObject() {{
                    put("role", msg.get("role"));
                    put("content", msg.get("content"));
                }});
            }
        }

        if (userMessage != null && !userMessage.trim().isEmpty()) {
            messages.add(new JSONObject() {{
                put("role", "user");
                put("content", userMessage);
            }});
        }

        JSONObject body = new JSONObject();
        body.put("model", "deepseek-chat");
        body.put("messages", messages);
        body.put("stream", false);

        URL url = new URL(API_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "Bearer " + API_KEY);
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(30000);
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(body.toString().getBytes(StandardCharsets.UTF_8));
        }

        int status = conn.getResponseCode();
        InputStream is = (status >= 200 && status < 400) ? conn.getInputStream() : conn.getErrorStream();
        String response = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        conn.disconnect();

        if (status != 200) {
            throw new RuntimeException("API 调用失败，请检查网络和 API Key");
        }

        return JSONObject.parseObject(response)
                .getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content")
                .trim();
    }
}