// src/main/java/com/mall/util/ChatMemoryUtil.java
package com.mall.util;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatMemoryUtil {
    public static final String ROLE_USER = "user";
    public static final String ROLE_ASSISTANT = "assistant";
    private static final String CHAT_HISTORY_KEY = "mall_chat_history";

    @SuppressWarnings("unchecked")
    public static List<Map<String, String>> getHistory(HttpSession session) {
        Object obj = session.getAttribute(CHAT_HISTORY_KEY);
        if (obj instanceof List<?>) {
            return (List<Map<String, String>>) obj;
        }
        List<Map<String, String>> history = new ArrayList<>();
        session.setAttribute(CHAT_HISTORY_KEY, history);
        return history;
    }

    public static void addHistory(HttpSession session, String role, String content) {
        if (session == null || role == null || content == null) return;
        List<Map<String, String>> history = getHistory(session);
        Map<String, String> msg = new HashMap<>();
        msg.put("role", role);
        msg.put("content", content);
        history.add(msg);
        if (history.size() > 10) history.remove(0);
    }
}