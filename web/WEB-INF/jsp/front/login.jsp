<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>登录 - SimpleMall</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
            font-family: 'Segoe UI', 'Microsoft YaHei', sans-serif;
        }

        :root {
            --primary-color: #4361ee;
            --primary-dark: #3a56d4;
            --secondary-color: #7209b7;
            --success-color: #4cc9f0;
            --danger-color: #f72585;
            --light-color: #f8f9fa;
            --dark-color: #212529;
            --gray-color: #6c757d;
            --border-color: #e0e0e0;
            --shadow: 0 10px 30px rgba(0, 0, 0, 0.08);
            --transition: all 0.3s ease;
        }

        body {
            background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 20px;
        }

        .container {
            width: 100%;
            max-width: 480px;
            animation: fadeIn 0.5s ease-out;
        }

        @keyframes fadeIn {
            from { opacity: 0; transform: translateY(20px); }
            to { opacity: 1; transform: translateY(0); }
        }

        .auth-container {
            background: white;
            border-radius: 20px;
            box-shadow: var(--shadow);
            overflow: hidden;
            transition: var(--transition);
        }

        .auth-container:hover {
            box-shadow: 0 15px 40px rgba(0, 0, 0, 0.12);
        }

        .auth-header {
            background: linear-gradient(135deg, var(--primary-color), var(--secondary-color));
            color: white;
            padding: 40px 30px;
            text-align: center;
            position: relative;
            overflow: hidden;
        }

        .auth-header::before {
            content: '';
            position: absolute;
            top: -50%;
            left: -50%;
            width: 200%;
            height: 200%;
            background: radial-gradient(circle, rgba(255,255,255,0.1) 1px, transparent 1px);
            background-size: 20px 20px;
            transform: rotate(30deg);
            animation: float 20s linear infinite;
        }

        @keyframes float {
            0% { transform: rotate(30deg) translateX(0); }
            100% { transform: rotate(30deg) translateX(-20px); }
        }

        .auth-header h2 {
            font-size: 32px;
            font-weight: 700;
            margin-bottom: 10px;
            position: relative;
            z-index: 1;
        }

        .auth-header p {
            font-size: 16px;
            opacity: 0.9;
            position: relative;
            z-index: 1;
        }

        .auth-body {
            padding: 40px 30px;
        }

        .form-group {
            margin-bottom: 25px;
            position: relative;
        }

        .form-group label {
            display: block;
            margin-bottom: 8px;
            color: var(--dark-color);
            font-weight: 600;
            font-size: 14px;
            transition: var(--transition);
        }

        .form-group.focused label {
            color: var(--primary-color);
        }

        .input-wrapper {
            position: relative;
        }

        .input-wrapper i {
            position: absolute;
            left: 15px;
            top: 50%;
            transform: translateY(-50%);
            color: var(--gray-color);
            transition: var(--transition);
        }

        .form-group.focused .input-wrapper i {
            color: var(--primary-color);
        }

        .form-group input {
            width: 100%;
            padding: 14px 14px 14px 45px;
            border: 2px solid var(--border-color);
            border-radius: 12px;
            font-size: 15px;
            transition: var(--transition);
            background: var(--light-color);
        }

        .form-group input:focus {
            outline: none;
            border-color: var(--primary-color);
            box-shadow: 0 0 0 3px rgba(67, 97, 238, 0.1);
            background: white;
        }

        .form-group input::placeholder {
            color: #adb5bd;
        }

        .remember-forgot {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 30px;
        }

        .checkbox-container {
            display: flex;
            align-items: center;
            cursor: pointer;
            user-select: none;
        }

        .checkbox-container input {
            display: none;
        }

        .checkmark {
            width: 20px;
            height: 20px;
            border: 2px solid var(--border-color);
            border-radius: 6px;
            margin-right: 10px;
            position: relative;
            transition: var(--transition);
        }

        .checkbox-container input:checked ~ .checkmark {
            background: var(--primary-color);
            border-color: var(--primary-color);
        }

        .checkmark::after {
            content: '';
            position: absolute;
            display: none;
            left: 6px;
            top: 2px;
            width: 5px;
            height: 10px;
            border: solid white;
            border-width: 0 2px 2px 0;
            transform: rotate(45deg);
        }

        .checkbox-container input:checked ~ .checkmark::after {
            display: block;
        }

        .checkbox-container span {
            color: var(--dark-color);
            font-size: 14px;
        }

        .forgot-link {
            color: var(--primary-color);
            text-decoration: none;
            font-size: 14px;
            font-weight: 500;
            transition: var(--transition);
        }

        .forgot-link:hover {
            color: var(--primary-dark);
            text-decoration: underline;
        }

        .submit-btn {
            width: 100%;
            padding: 16px;
            background: linear-gradient(135deg, var(--primary-color), var(--secondary-color));
            color: white;
            border: none;
            border-radius: 12px;
            font-size: 16px;
            font-weight: 600;
            cursor: pointer;
            transition: var(--transition);
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 10px;
        }

        .submit-btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 8px 20px rgba(67, 97, 238, 0.3);
        }

        .submit-btn:active {
            transform: translateY(0);
        }

        .submit-btn i {
            font-size: 18px;
        }

        .auth-footer {
            text-align: center;
            padding: 25px 30px;
            border-top: 1px solid var(--border-color);
            background: var(--light-color);
        }

        .message-box {
            padding: 12px 20px;
            border-radius: 10px;
            margin-bottom: 20px;
            text-align: center;
            font-size: 14px;
            font-weight: 500;
            animation: slideIn 0.3s ease-out;
        }

        @keyframes slideIn {
            from { opacity: 0; transform: translateX(-10px); }
            to { opacity: 1; transform: translateX(0); }
        }

        .message-box.error {
            background: rgba(247, 37, 133, 0.1);
            color: var(--danger-color);
            border: 1px solid rgba(247, 37, 133, 0.2);
        }

        .message-box.success {
            background: rgba(76, 201, 240, 0.1);
            color: #0096c7;
            border: 1px solid rgba(76, 201, 240, 0.2);
        }

        .register-link {
            color: var(--primary-color);
            text-decoration: none;
            font-weight: 600;
            transition: var(--transition);
            position: relative;
            display: inline-block;
        }

        .register-link::after {
            content: '';
            position: absolute;
            bottom: -2px;
            left: 0;
            width: 0;
            height: 2px;
            background: var(--primary-color);
            transition: var(--transition);
        }

        .register-link:hover::after {
            width: 100%;
        }

        .register-link:hover {
            color: var(--primary-dark);
        }

        .footer-text {
            color: var(--gray-color);
            font-size: 14px;
            margin-bottom: 10px;
        }

        /* 响应式设计 */
        @media (max-width: 576px) {
            .auth-header {
                padding: 30px 20px;
            }

            .auth-body {
                padding: 30px 20px;
            }

            .auth-footer {
                padding: 20px;
            }

            .auth-header h2 {
                font-size: 28px;
            }

            .remember-forgot {
                flex-direction: column;
                align-items: flex-start;
                gap: 15px;
            }
        }

        /* 加载动画 */
        .submit-btn.loading {
            pointer-events: none;
            opacity: 0.8;
        }

        .submit-btn.loading i {
            animation: spin 1s linear infinite;
        }

        @keyframes spin {
            0% { transform: rotate(0deg); }
            100% { transform: rotate(360deg); }
        }
    </style>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>
<div class="container">
    <div class="auth-container">
        <div class="auth-header">
            <h2>欢迎回来</h2>
            <p>登录您的 SimpleMall 账号</p>
        </div>

        <div class="auth-body">
            <%
                Cookie[] cs = request.getCookies();
                String un = "", pw = "";
                if (cs != null) {
                    for (Cookie c : cs) {
                        if ("username".equals(c.getName())) un = c.getValue();
                        if ("password".equals(c.getName())) pw = c.getValue();
                    }
                }
            %>

            <!-- 消息显示区域 -->
            <c:if test="${not empty msg}">
                <div class="message-box ${msg.contains('成功') ? 'success' : 'error'}">
                        ${msg}
                </div>
            </c:if>

            <form id="loginForm" action="${pageContext.request.contextPath}/UserServlet?action=login" method="post">
                <!-- 用户名输入 -->
                <div class="form-group" id="usernameGroup">
                    <label for="username">用户名</label>
                    <div class="input-wrapper">
                        <i class="fas fa-user"></i>
                        <input type="text"
                               id="username"
                               name="username"
                               value="<%= un %>"
                               placeholder="输入用户名"
                               required
                               autocomplete="username">
                    </div>
                </div>

                <!-- 密码输入 -->
                <div class="form-group" id="passwordGroup">
                    <label for="password">密码</label>
                    <div class="input-wrapper">
                        <i class="fas fa-lock"></i>
                        <input type="password"
                               id="password"
                               name="password"
                               value="<%= pw %>"
                               placeholder="输入密码"
                               required
                               autocomplete="current-password">
                    </div>
                </div>

                <!-- 记住密码和忘记密码 -->
                <div class="remember-forgot">
                    <label class="checkbox-container">
                        <input type="checkbox"
                               name="remember"
                            <%= (pw != null && !pw.isEmpty()) ? "checked" : "" %>>
                        <span class="checkmark"></span>
                        <span>记住密码</span>
                    </label>
                    <a href="${pageContext.request.contextPath}/UserServlet?action=forgotPassword" class="forgot-link">
                        忘记密码？
                    </a>
                </div>

                <!-- 提交按钮 -->
                <button type="submit" class="submit-btn">
                    <i class="fas fa-sign-in-alt"></i>
                    立即登录
                </button>
            </form>
        </div>

        <!-- 页脚 -->
        <div class="auth-footer">
            <p class="footer-text">没有账号？</p>
            <a href="${pageContext.request.contextPath}/UserServlet?action=toRegister" class="register-link">
                立即注册新账号
                <i class="fas fa-arrow-right" style="margin-left: 5px;"></i>
            </a>
        </div>
    </div>
</div>

<script>
    // 表单交互效果
    document.addEventListener('DOMContentLoaded', function() {
        const formGroups = document.querySelectorAll('.form-group');
        const loginForm = document.getElementById('loginForm');
        const submitBtn = document.querySelector('.submit-btn');

        // 为每个输入框添加焦点效果
        formGroups.forEach(group => {
            const input = group.querySelector('input');

            input.addEventListener('focus', function() {
                group.classList.add('focused');
            });

            input.addEventListener('blur', function() {
                if (!this.value) {
                    group.classList.remove('focused');
                }
            });

            // 初始化检查
            if (input.value) {
                group.classList.add('focused');
            }
        });

        // 表单提交处理
        loginForm.addEventListener('submit', function(e) {
            const username = document.getElementById('username').value.trim();
            const password = document.getElementById('password').value.trim();

            if (!username || !password) {
                e.preventDefault();
                if (!username) {
                    document.getElementById('usernameGroup').classList.add('focused');
                    document.getElementById('username').focus();
                } else {
                    document.getElementById('passwordGroup').classList.add('focused');
                    document.getElementById('password').focus();
                }

                // 显示错误提示
                const messageBox = document.querySelector('.message-box');
                if (messageBox) {
                    messageBox.textContent = '请填写完整的登录信息';
                    messageBox.className = 'message-box error';
                } else {
                    const authBody = document.querySelector('.auth-body');
                    const newMessageBox = document.createElement('div');
                    newMessageBox.className = 'message-box error';
                    newMessageBox.textContent = '请填写完整的登录信息';
                    authBody.insertBefore(newMessageBox, loginForm);
                }

                return;
            }

            // 显示加载状态
            submitBtn.classList.add('loading');
            submitBtn.innerHTML = '<i class="fas fa-spinner"></i> 登录中...';
        });

        密码显示/隐藏功能（可选，如果需要可以取消注释）
        const passwordInput = document.getElementById('password');
        const passwordIcon = document.querySelector('#passwordGroup .fa-lock');

        passwordIcon.addEventListener('click', function() {
            if (passwordInput.type === 'password') {
                passwordInput.type = 'text';
                this.classList.remove('fa-lock');
                this.classList.add('fa-unlock');
            } else {
                passwordInput.type = 'password';
                this.classList.remove('fa-unlock');
                this.classList.add('fa-lock');
            }
        });
    });
</script>
</body>
</html>