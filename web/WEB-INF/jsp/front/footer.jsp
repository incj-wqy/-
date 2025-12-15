<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<script src="https://cdn.bootcdn.net/ajax/libs/jquery/3.7.1/jquery.min.js"></script>
<link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/font-awesome/6.4.0/css/all.min.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css">

<footer class="site-footer">
    <div class="footer-container">
        <div class="footer-section">
            <h3>关于我们</h3>
            <p>SimpleMall是一个简单易用的在线购物平台，为您提供优质的商品和便捷的购物体验。</p>
        </div>

        <div class="footer-section">
            <h3>客户服务</h3>
            <ul>
                <li><a href="#">帮助中心</a></li>
                <li><a href="#">退换货政策</a></li>
                <li><a href="#">配送说明</a></li>
                <li><a href="#">支付方式</a></li>
            </ul>
        </div>

        <div class="footer-section">
            <h3>联系方式</h3>
            <p><<i class="fas fa-phone"></i> 400-123-4567</p>
            <p><<i class="fas fa-envelope"></i> service@simplemall.com</p>
            <div class="social-links">
                <a href="#"><<i class="fab fa-weixin"></i></a>
                <a href="#"><<i class="fab fa-weibo"></i></a>
                <a href="#"><<i class="fab fa-qq"></i></a>
            </div>
        </div>
    </div>

    <div class="copyright">
        <p>&copy; 2023 SimpleMall. All rights reserved.</p>
    </div>
</footer>

<!-- 客服悬浮按钮 -->
<div id="chatBtn" style="position: fixed; bottom: 40px; right: 40px; width: 60px; height: 60px; background: #3498db; border-radius: 50%; color: white; text-align: center; line-height: 60px; cursor: pointer; font-size: 18px; box-shadow: 0 4px 12px rgba(0,0,0,0.15); z-index: 9999; transition: all 0.3s ease; border: none; outline: none;">
    <<i class="fas fa-comments"></i>
</div>

<!-- 客服聊天窗口 -->
<div id="chatWindow" style="position: fixed; bottom: 120px; right: 40px; width: 400px; height: 580px; border: 1px solid #e5e7eb; border-radius: 12px; box-shadow: 0 4px 20px rgba(0,0,0,0.1); background: white; display: none; flex-direction: column; z-index: 9998; overflow: hidden;">
    <!-- 窗口头部 -->
    <div style="height: 60px; line-height: 60px; padding: 0 20px; border-bottom: 1px solid #e5e7eb; display: flex; justify-content: space-between; align-items: center; background: #3498db; color: white;">
        <div style="display: flex; align-items: center; gap: 10px;">
            <<i class="fas fa-comments"></i>
            <span style="font-size: 16px; font-weight: 600;">SimpleMall 智能客服</span>
        </div>
        <span id="closeChat" style="cursor: pointer; font-size: 22px;">&times;</span>
    </div>

    <!-- 聊天内容区 -->
    <div id="chatContent" style="flex: 1; padding: 20px; overflow-y: auto; background: #f9fafb; font-size: 14px; line-height: 1.5;">
        <%
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
            String currentDate = sdf.format(new java.util.Date());
        %>
        <div style="text-align: center; margin: 10px 0 20px; font-size: 12px; color: #9ca3af;">
            <%= currentDate %>
        </div>
        <!-- 初始欢迎消息 -->
        <div class="msg robot-msg" style="margin: 12px 0;">
            <div style="max-width: 70%; background: #e5e7eb; color: #111827; padding: 10px 15px; border-radius: 18px 18px 18px 4px;">
                你好！我是商城智能客服，支持以下操作：<br>
                1. 添加商品 名称 分类ID 价格 库存 描述：xxx<br>
                2. 删除商品 名称<br>
                3. 修改商品 名称 价格/库存/状态<br>
                4. 查询商品 名称/所有<br>
                5. 查询订单（输入订单号）
            </div>
            <div style="font-size: 10px; color: #9ca3af; margin-top: 4px; text-align: left;">
                <%= new java.text.SimpleDateFormat("HH:mm").format(new java.util.Date()) %>
            </div>
        </div>
    </div>

    <!-- 输入区 -->
    <div style="padding: 15px; border-top: 1px solid #e5e7eb; background: white;">
        <div style="display: flex; align-items: center; gap: 10px; width: 100%;">
            <input type="text" id="orderNoInput" placeholder="订单号（可选）" style="width: 100px; padding: 0 12px; border: 1px solid #d1d5db; border-radius: 20px; outline: none; height: 40px; font-size: 14px;">
            <input type="text" id="userInput" placeholder="请输入你的问题/指令..." style="flex: 1; padding: 0 15px; border: 1px solid #d1d5db; border-radius: 20px; outline: none; height: 40px; font-size: 14px;">
            <button id="sendBtn" style="width: 70px; height: 40px; background: #3498db; color: white; border: none; border-radius: 20px; cursor: pointer; font-size: 14px;">
                发送
            </button>
        </div>
    </div>
</div>

<script>
    $(function() {
        // 显示/隐藏窗口
        $('#chatBtn').click(function() {
            $('#chatWindow').css('display', 'flex');
            $('#userInput').focus();
        });
        $('#closeChat').click(function() {
            $('#chatWindow').css('display', 'none');
        });

        // 发送消息
        $('#sendBtn').click(sendMessage);
        $('#userInput').keydown(function(e) {
            if (e.key === 'Enter' && !e.shiftKey) {
                e.preventDefault();
                sendMessage();
            }
        });

        // ========== 修复：用普通字符串拼接替代ES6模板字符串 ==========
        function sendMessage() {
            const userMsg = $.trim($('#userInput').val());
            const orderNo = $.trim($('#orderNoInput').val()) || '';
            if (!userMsg) return;

            // 1. 生成用户消息（用单引号+字符串拼接，避免JSP解析冲突）
            const userTime = new Date().toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'});
            const formattedUserMsg = userMsg.replace(/\n/g, '<br>');
            const userHtml =
                '<div class="msg user-msg" style="margin: 12px 0; text-align: right;">' +
                '<div style="max-width: 70%; display: inline-block; background: #3498db; color: white; padding: 10px 15px; border-radius: 18px 18px 4px 18px;">' +
                formattedUserMsg +
                '</div>' +
                '<div style="font-size: 10px; color: #9ca3af; margin-top: 4px; text-align: right;">' +
                userTime +
                '</div>' +
                '</div>';
            $('#chatContent').append(userHtml);

            // 2. 清空+滚动
            $('#userInput').val('');
            scrollToBottom();

            // 3. 调用接口
            callApi(userMsg, orderNo);
        }

        // ========== 修复：接口请求中的字符串拼接 ==========
        function callApi(msg, orderNo) {
            // 加载中消息
            const loadingTime = new Date().toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'});
            const loadingHtml =
                '<div class="msg robot-msg" style="margin: 12px 0;">' +
                '<div style="max-width: 70%; background: #e5e7eb; color: #111827; padding: 10px 15px; border-radius: 18px 18px 18px 4px;">' +
                '正在处理你的请求，请稍候...' +
                '</div>' +
                '<div style="font-size: 10px; color: #9ca3af; margin-top: 4px; text-align: left;">' +
                loadingTime +
                '</div>' +
                '</div>';
            $('#chatContent').append(loadingHtml);
            scrollToBottom();

            // AJAX请求
            $.ajax({
                url: '${pageContext.request.contextPath}/front/chat',
                type: 'POST',
                data: {message: msg, orderNo: orderNo},
                dataType: 'json',
                timeout: 10000,
                success: function(res) {
                    // 移除加载中
                    $('.msg.robot-msg:last').remove();

                    // 回复消息
                    const replyTime = new Date().toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'});
                    const replyContent = res.code === 1 ? res.data : (res.msg || '操作失败');
                    const formattedReply = replyContent.replace(/\n/g, '<br>');
                    const replyHtml =
                        '<div class="msg robot-msg" style="margin: 12px 0;">' +
                        '<div style="max-width: 70%; background: #e5e7eb; color: #111827; padding: 10px 15px; border-radius: 18px 18px 18px 4px;">' +
                        formattedReply +
                        '</div>' +
                        '<div style="font-size: 10px; color: #9ca3af; margin-top: 4px; text-align: left;">' +
                        replyTime +
                        '</div>' +
                        '</div>';
                    $('#chatContent').append(replyHtml);
                    scrollToBottom();
                },
                error: function() {
                    // 移除加载中
                    $('.msg.robot-msg:last').remove();

                    // 错误消息
                    const errorTime = new Date().toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'});
                    const errorHtml =
                        '<div class="msg robot-msg" style="margin: 12px 0;">' +
                        '<div style="max-width: 70%; background: #e5e7eb; color: #111827; padding: 10px 15px; border-radius: 18px 18px 18px 4px;">' +
                        '网络错误，请重试' +
                        '</div>' +
                        '<div style="font-size: 10px; color: #9ca3af; margin-top: 4px; text-align: left;">' +
                        errorTime +
                        '</div>' +
                        '</div>';
                    $('#chatContent').append(errorHtml);
                    scrollToBottom();
                }
            });
        }

        // 滚动到底部
        function scrollToBottom() {
            const chatBox = $('#chatContent')[0];
            chatBox.scrollTop = chatBox.scrollHeight;
        }

        // 下拉菜单（保留）
        $('.dropdown-toggle').off('click').on('click', function(e) {
            e.preventDefault();
            $(this).next('.dropdown-menu').toggleClass('show');
        });
        $(document).off('click').on('click', function(event) {
            if (!$(event.target).closest('.dropdown').length) {
                $('.dropdown-menu').removeClass('show');
            }
        });
    });
</script>