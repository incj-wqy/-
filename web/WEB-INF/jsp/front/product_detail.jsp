<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.mall.model.Product" %>
<jsp:include page="header.jsp">
    <jsp:param name="title" value="商品详情" />
</jsp:include>

<!-- 悬浮聊天按钮的CSS样式 -->
<style>
    .chat-button {
        position: fixed;
        bottom: 30px;
        right: 30px;
        width: 60px;
        height: 60px;
        background: var(--primary-color, #007bff);
        color: white;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 24px;
        cursor: pointer;
        box-shadow: 0 4px 12px rgba(0,0,0,0.15);
        z-index: 1000;
        border: none;
        transition: all 0.3s ease;
    }

    .chat-button:hover {
        background: var(--primary-hover-color, #0056b3);
        transform: scale(1.05);
        box-shadow: 0 6px 16px rgba(0,0,0,0.2);
    }

    .chat-window {
        position: fixed;
        bottom: 100px;
        right: 30px;
        width: 380px;
        height: 500px;
        background: white;
        border-radius: 12px;
        box-shadow: 0 8px 32px rgba(0,0,0,0.1);
        display: none;
        z-index: 999;
        overflow: hidden;
        border: 1px solid #eee;
        transition: all 0.3s ease;
    }

    .chat-header {
        background: var(--primary-color, #007bff);
        color: white;
        padding: 15px;
        display: flex;
        justify-content: space-between;
        align-items: center;
    }

    .chat-header h4 {
        margin: 0;
        font-size: 16px;
        font-weight: 600;
    }

    .close-chat {
        background: none;
        border: none;
        color: white;
        cursor: pointer;
        font-size: 20px;
        width: 30px;
        height: 30px;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        transition: background-color 0.3s;
    }

    .close-chat:hover {
        background: rgba(255,255,255,0.2);
    }

    @media (max-width: 768px) {
        .chat-window {
            width: calc(100% - 40px);
            right: 20px;
            bottom: 80px;
            height: 450px;
        }

        .chat-button {
            bottom: 20px;
            right: 20px;
            width: 50px;
            height: 50px;
            font-size: 20px;
        }
    }
</style>

<!-- 商品详情内容 -->
<div class="container">
    <% Product p = (Product) request.getAttribute("product"); %>

    <div class="card">
        <div style="display:flex; gap: 40px; flex-wrap: wrap;">
            <div style="flex: 0 0 400px;">
                <img id="mainImg"
                     src="<%= (p.getImage()==null || p.getImage().trim().isEmpty() ? "https://via.placeholder.com/400" : p.getImage()) %>"
                     style="width:100%; height:400px; object-fit:cover; border-radius:4px; border:1px solid #eee;">

                <div style="margin-top:10px; display:flex; gap:10px; overflow-x:auto;">
                    <%
                        String imgs = p.getImages();
                        if(imgs!=null && !imgs.isEmpty()){
                            for(String img : imgs.split(",")) {
                    %>
                    <img src="<%=img%>" style="width:60px; height:60px; object-fit:cover; cursor:pointer; border:1px solid #ddd;"
                         onclick="document.getElementById('mainImg').src=this.src">
                    <% }} %>
                </div>
            </div>

            <div style="flex:1;">
                <h2 style="border:none; padding:0; font-size:28px;"><%=p.getName()%></h2>
                <div style="color:var(--danger-color); font-size:32px; font-weight:bold; margin: 15px 0;">
                    ￥<%=p.getPrice()%>
                </div>

                <div style="background:#f9f9f9; padding:15px; border-radius:4px; margin-bottom:20px;">
                    <p>库存状态：<%= p.getStock() > 0 ? "<span style='color:green'><i class='fas fa-check-circle'></i> 有货</span>" : "<span style='color:red'>缺货</span>" %></p>
                    <p style="margin-top:5px;">商品编号：<%=p.getId()%></p>
                </div>

                <form action="${pageContext.request.contextPath}/CartServlet?action=add" method="post">
                    <input type="hidden" name="productId" value="<%=p.getId()%>">

                    <div style="margin-bottom:20px;">
                        <label>购买数量：</label>
                        <input type="number" name="quantity" value="1" min="1" max="<%=p.getStock()%>" style="width:100px; display:inline-block; margin:0;">
                    </div>

                    <button type="submit" class="btn" style="padding: 15px 40px; font-size:18px;">
                        <i class="fas fa-cart-plus"></i> 加入购物车
                    </button>
                </form>

                <hr style="margin: 30px 0; border:0; border-top:1px solid #eee;">

                <h3>商品详情</h3>
                <div style="color:#666; line-height:1.8;">
                    <%= (p.getDescription()==null ? "暂无描述" : p.getDescription()) %>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- 悬浮聊天按钮和窗口 -->
<button class="chat-button" id="chatBtn" title="在线客服咨询">
    <i class="fas fa-comment"></i>
</button>

<div class="chat-window" id="chatWindow">
    <div class="chat-header">
        <h4><i class="fas fa-headset"></i> 在线客服</h4>
        <button class="close-chat" onclick="toggleChat()">
            <i class="fas fa-times"></i>
        </button>
    </div>
    <iframe
            src="http://localhost:8080/chat/312bf207c9f875dd?mode=mobile"
            style="width: 100%; height: calc(100% - 50px);"
            frameborder="0"
            allow="microphone"
            id="chatIframe">
    </iframe>
</div>

<!-- JavaScript -->
<script>
    // 切换聊天窗口显示/隐藏
    function toggleChat() {
        var chatWindow = document.getElementById('chatWindow');
        var chatBtn = document.getElementById('chatBtn');
        var chatIframe = document.getElementById('chatIframe');

        if (chatWindow.style.display === 'block' || chatWindow.style.display === '') {
            // 关闭聊天窗口
            chatWindow.style.display = 'none';
            chatBtn.innerHTML = '<i class="fas fa-comment"></i>';
            chatBtn.title = '打开在线客服';

            // 可选：刷新iframe以重置聊天状态
            // chatIframe.src = chatIframe.src;
        } else {
            // 打开聊天窗口
            chatWindow.style.display = 'block';
            chatBtn.innerHTML = '<i class="fas fa-times"></i>';
            chatBtn.title = '关闭在线客服';

            // 添加打开动画
            chatWindow.style.opacity = '0';
            chatWindow.style.transform = 'translateY(20px)';

            setTimeout(function() {
                chatWindow.style.opacity = '1';
                chatWindow.style.transform = 'translateY(0)';
            }, 10);
        }
    }

    // 点击聊天窗口外部关闭窗口
    document.addEventListener('click', function(event) {
        var chatWindow = document.getElementById('chatWindow');
        var chatBtn = document.getElementById('chatBtn');

        // 如果点击的不是聊天窗口或按钮，且聊天窗口是打开的
        if (!chatWindow.contains(event.target) &&
            !chatBtn.contains(event.target) &&
            chatWindow.style.display === 'block') {
            toggleChat();
        }
    });

    // 防止聊天窗口内的点击事件冒泡
    document.getElementById('chatWindow').addEventListener('click', function(event) {
        event.stopPropagation();
    });

    // 页面加载时检查是否需要自动打开聊天窗口（可选）
    window.addEventListener('load', function() {
        // 可以通过URL参数控制是否自动打开聊天窗口
        // 例如：?chat=open
        var urlParams = new URLSearchParams(window.location.search);
        if (urlParams.get('chat') === 'open') {
            toggleChat();
        }
    });

    // 添加键盘快捷键支持（按ESC关闭聊天窗口）
    document.addEventListener('keydown', function(event) {
        if (event.key === 'Escape') {
            var chatWindow = document.getElementById('chatWindow');
            if (chatWindow.style.display === 'block') {
                toggleChat();
            }
        }
    });
</script>

<%@ include file="footer.jsp" %>