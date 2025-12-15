<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>用户注册 - SimpleMall</title>
  <style>
    :root {
      --primary-color: #4361ee;
      --primary-dark: #3a56d4;
      --secondary-color: #7209b7;
      --success-color: #4cc9f0;
      --danger-color: #f72585;
      --warning-color: #f8961e;
      --light-color: #f8f9fa;
      --dark-color: #212529;
      --gray-color: #6c757d;
      --border-color: #e0e0e0;
      --shadow: 0 10px 30px rgba(0, 0, 0, 0.08);
      --shadow-hover: 0 15px 40px rgba(0, 0, 0, 0.12);
      --transition: all 0.3s ease;
    }

    * {
      margin: 0;
      padding: 0;
      box-sizing: border-box;
      font-family: 'Segoe UI', 'Microsoft YaHei', sans-serif;
    }

    body {
      background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
      min-height: 100vh;
      display: flex;
      align-items: center;
      justify-content: center;
      padding: 20px;
    }

    .register-container {
      width: 100%;
      max-width: 480px;
      animation: slideUp 0.6s ease-out;
    }

    @keyframes slideUp {
      from {
        opacity: 0;
        transform: translateY(30px);
      }
      to {
        opacity: 1;
        transform: translateY(0);
      }
    }

    .register-card {
      background: white;
      border-radius: 20px;
      box-shadow: var(--shadow);
      overflow: hidden;
      transition: var(--transition);
    }

    .register-card:hover {
      box-shadow: var(--shadow-hover);
    }

    .register-header {
      background: linear-gradient(135deg, var(--primary-color), var(--secondary-color));
      color: white;
      padding: 40px 30px;
      text-align: center;
      position: relative;
      overflow: hidden;
    }

    .register-header::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: url("data:image/svg+xml,%3Csvg width='100' height='100' viewBox='0 0 100 100' xmlns='http://www.w3.org/2000/svg'%3E%3Cpath d='M11 18c3.866 0 7-3.134 7-7s-3.134-7-7-7-7 3.134-7 7 3.134 7 7 7zm48 25c3.866 0 7-3.134 7-7s-3.134-7-7-7-7 3.134-7 7 3.134 7 7 7zm-43-7c1.657 0 3-1.343 3-3s-1.343-3-3-3-3 1.343-3 3 1.343 3 3 3zm63 31c1.657 0 3-1.343 3-3s-1.343-3-3-3-3 1.343-3 3 1.343 3 3 3zM34 90c1.657 0 3-1.343 3-3s-1.343-3-3-3-3 1.343-3 3 1.343 3 3 3zm56-76c1.657 0 3-1.343 3-3s-1.343-3-3-3-3 1.343-3 3 1.343 3 3 3zM12 86c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm28-65c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm23-11c2.76 0 5-2.24 5-5s-2.24-5-5-5-5 2.24-5 5 2.24 5 5 5zm-6 60c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm29 22c2.76 0 5-2.24 5-5s-2.24-5-5-5-5 2.24-5 5 2.24 5 5 5zM32 63c2.76 0 5-2.24 5-5s-2.24-5-5-5-5 2.24-5 5 2.24 5 5 5zm57-13c2.76 0 5-2.24 5-5s-2.24-5-5-5-5 2.24-5 5 2.24 5 5 5zm-9-21c1.105 0 2-.895 2-2s-.895-2-2-2-2 .895-2 2 .895 2 2 2zM60 91c1.105 0 2-.895 2-2s-.895-2-2-2-2 .895-2 2 .895 2 2 2zM35 41c1.105 0 2-.895 2-2s-.895-2-2-2-2 .895-2 2 .895 2 2 2zM12 60c1.105 0 2-.895 2-2s-.895-2-2-2-2 .895-2 2 .895 2 2 2z' fill='%23ffffff' fill-opacity='0.1' fill-rule='evenodd'/%3E%3C/svg%3E");
      opacity: 0.3;
    }

    .register-header h2 {
      font-size: 32px;
      font-weight: 700;
      margin-bottom: 10px;
      position: relative;
      z-index: 1;
    }

    .register-header p {
      font-size: 16px;
      opacity: 0.9;
      position: relative;
      z-index: 1;
    }

    .form-section {
      padding: 40px 30px;
    }

    .form-grid {
      display: grid;
      grid-template-columns: 1fr;
      gap: 25px;
    }

    @media (min-width: 576px) {
      .form-grid {
        grid-template-columns: 1fr 1fr;
      }

      .form-grid .form-group.full-width {
        grid-column: span 2;
      }
    }

    .form-group {
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

    .form-group input:invalid:not(:focus):not(:placeholder-shown) {
      border-color: var(--warning-color);
    }

    .form-group input:valid:not(:focus) {
      border-color: var(--success-color);
    }

    .password-strength {
      margin-top: 5px;
      height: 4px;
      background: #e9ecef;
      border-radius: 2px;
      overflow: hidden;
      position: relative;
    }

    .strength-bar {
      height: 100%;
      width: 0%;
      transition: var(--transition);
      border-radius: 2px;
    }

    .strength-text {
      font-size: 12px;
      color: var(--gray-color);
      margin-top: 3px;
      text-align: right;
    }

    .message-box {
      padding: 15px 20px;
      border-radius: 10px;
      margin-bottom: 25px;
      text-align: center;
      font-size: 14px;
      font-weight: 500;
      animation: slideIn 0.3s ease-out;
    }

    @keyframes slideIn {
      from {
        opacity: 0;
        transform: translateY(-10px);
      }
      to {
        opacity: 1;
        transform: translateY(0);
      }
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

    .form-footer {
      margin-top: 30px;
      display: flex;
      justify-content: space-between;
      align-items: center;
    }

    .submit-btn {
      padding: 16px 40px;
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
      min-width: 140px;
    }

    .submit-btn:hover {
      transform: translateY(-2px);
      box-shadow: 0 8px 20px rgba(67, 97, 238, 0.3);
    }

    .submit-btn:active {
      transform: translateY(0);
    }

    .submit-btn.loading {
      pointer-events: none;
      opacity: 0.8;
    }

    .submit-btn.loading i {
      animation: spin 1s linear infinite;
    }

    @keyframes spin {
      0% {
        transform: rotate(0deg);
      }
      100% {
        transform: rotate(360deg);
      }
    }

    .login-link {
      color: var(--primary-color);
      text-decoration: none;
      font-weight: 600;
      font-size: 14px;
      transition: var(--transition);
      display: flex;
      align-items: center;
      gap: 8px;
    }

    .login-link:hover {
      color: var(--primary-dark);
      text-decoration: underline;
    }

    .form-group .input-hint {
      font-size: 12px;
      color: var(--gray-color);
      margin-top: 5px;
      display: none;
    }

    .form-group.focused .input-hint {
      display: block;
    }

    /* 响应式设计 */
    @media (max-width: 576px) {
      .register-header {
        padding: 30px 20px;
      }

      .form-section {
        padding: 30px 20px;
      }

      .register-header h2 {
        font-size: 28px;
      }

      .form-footer {
        flex-direction: column-reverse;
        gap: 20px;
      }

      .submit-btn {
        width: 100%;
      }
    }

    /* 密码显示/隐藏按钮 */
    .toggle-password {
      position: absolute;
      right: 15px;
      top: 50%;
      transform: translateY(-50%);
      background: none;
      border: none;
      color: var(--gray-color);
      cursor: pointer;
      padding: 5px;
      transition: var(--transition);
    }

    .toggle-password:hover {
      color: var(--primary-color);
    }
  </style>
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>
<div class="register-container">
  <div class="register-card">
    <div class="register-header">
      <h2>创建账号</h2>
      <p>加入 SimpleMall，开启购物之旅</p>
    </div>

    <div class="form-section">
      <!-- 消息提示区域 -->
      <c:if test="${not empty msg}">
        <div class="message-box ${msg.contains('成功') ? 'success' : 'error'}">
            ${msg}
        </div>
      </c:if>

      <form id="registerForm"
            action="${pageContext.request.contextPath}/UserServlet?action=register"
            method="post">

        <div class="form-grid">
          <!-- 用户名 -->
          <div class="form-group">
            <label for="username">
              <i class="fas fa-user" style="margin-right: 5px;"></i>
              用户名
            </label>
            <div class="input-wrapper">
              <i class="fas fa-user-circle"></i>
              <input type="text"
                     id="username"
                     name="username"
                     placeholder="输入用户名（4-20位字符）"
                     required
                     minlength="4"
                     maxlength="20"
                     pattern="[A-Za-z0-9_]+"
                     autocomplete="username">
            </div>
            <div class="input-hint">仅限字母、数字和下划线</div>
          </div>

          <!-- 密码 -->
          <div class="form-group">
            <label for="password">
              <i class="fas fa-lock" style="margin-right: 5px;"></i>
              密码
            </label>
            <div class="input-wrapper">
              <i class="fas fa-key"></i>
              <input type="password"
                     id="password"
                     name="password"
                     placeholder="输入密码（6位以上）"
                     required
                     minlength="6"
                     autocomplete="new-password">
              <button type="button" class="toggle-password" aria-label="显示/隐藏密码">
                <i class="fas fa-eye"></i>
              </button>
            </div>
            <div class="password-strength">
              <div class="strength-bar" id="strengthBar"></div>
            </div>
            <div class="strength-text" id="strengthText">密码强度：弱</div>
            <div class="input-hint">至少6位字符，建议包含字母和数字</div>
          </div>

          <!-- 邮箱 -->
          <div class="form-group full-width">
            <label for="email">
              <i class="fas fa-envelope" style="margin-right: 5px;"></i>
              邮箱地址
            </label>
            <div class="input-wrapper">
              <i class="fas fa-at"></i>
              <input type="email"
                     id="email"
                     name="email"
                     placeholder="输入常用邮箱"
                     autocomplete="email">
            </div>
            <div class="input-hint">用于账户验证和接收通知</div>
          </div>

          <!-- 手机号 -->
          <div class="form-group">
            <label for="phone">
              <i class="fas fa-phone" style="margin-right: 5px;"></i>
              手机号码
            </label>
            <div class="input-wrapper">
              <i class="fas fa-mobile-alt"></i>
              <input type="tel"
                     id="phone"
                     name="phone"
                     placeholder="11位手机号"
                     pattern="1[3-9]\d{9}"
                     autocomplete="tel">
            </div>
            <div class="input-hint">接收订单和物流信息</div>
          </div>

          <!-- 收货地址 -->
          <div class="form-group full-width">
            <label for="address">
              <i class="fas fa-map-marker-alt" style="margin-right: 5px;"></i>
              收货地址
            </label>
            <div class="input-wrapper">
              <i class="fas fa-home"></i>
              <input type="text"
                     id="address"
                     name="address"
                     placeholder="省市区街道详细地址"
                     autocomplete="street-address">
            </div>
            <div class="input-hint">可注册后完善</div>
          </div>
        </div>

        <!-- 表单底部 -->
        <div class="form-footer">
          <a href="${pageContext.request.contextPath}/UserServlet?action=toLogin" class="login-link">
            <i class="fas fa-sign-in-alt"></i>
            已有账号？去登录
          </a>
          <button type="submit" class="submit-btn">
            <i class="fas fa-user-plus"></i>
            立即注册
          </button>
        </div>
      </form>
    </div>
  </div>
</div>

<script>
  document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('registerForm');
    const submitBtn = form.querySelector('.submit-btn');
    const passwordInput = document.getElementById('password');
    const togglePasswordBtn = document.querySelector('.toggle-password');
    const strengthBar = document.getElementById('strengthBar');
    const strengthText = document.getElementById('strengthText');

    // 所有表单组
    const formGroups = document.querySelectorAll('.form-group');

    // 添加焦点效果
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

    // 密码强度检测
    passwordInput.addEventListener('input', function() {
      const password = this.value;
      let strength = 0;

      if (password.length >= 6) strength += 1;
      if (password.length >= 8) strength += 1;
      if (/[A-Z]/.test(password)) strength += 1;
      if (/[0-9]/.test(password)) strength += 1;
      if (/[^A-Za-z0-9]/.test(password)) strength += 1;

      // 更新强度条
      const percent = strength * 20;
      strengthBar.style.width = percent + '%';

      // 更新颜色和文字
      let color, text;
      if (strength <= 1) {
        color = '#f72585';
        text = '弱';
      } else if (strength <= 3) {
        color = '#f8961e';
        text = '中等';
      } else {
        color = '#4cc9f0';
        text = '强';
      }

      strengthBar.style.backgroundColor = color;
      strengthText.textContent = '密码强度：' + text;
      strengthText.style.color = color;
    });

    // 切换密码显示/隐藏
    togglePasswordBtn.addEventListener('click', function() {
      const type = passwordInput.getAttribute('type') === 'password' ? 'text' : 'password';
      passwordInput.setAttribute('type', type);

      const icon = this.querySelector('i');
      icon.classList.toggle('fa-eye');
      icon.classList.toggle('fa-eye-slash');
    });

    // 表单验证
    form.addEventListener('submit', function(e) {
      let isValid = true;
      const errors = [];

      // 检查必填项
      const requiredFields = form.querySelectorAll('input[required]');
      requiredFields.forEach(field => {
        if (!field.value.trim()) {
          isValid = false;
          field.parentElement.parentElement.classList.add('focused');
          errors.push(`${field.previousElementSibling.textContent.trim()}不能为空`);
        }
      });

      // 检查用户名格式
      const username = document.getElementById('username');
      if (username.value) {
        const usernamePattern = /^[A-Za-z0-9_]+$/;
        if (!usernamePattern.test(username.value)) {
          isValid = false;
          errors.push('用户名只能包含字母、数字和下划线');
        }
      }

      // 检查手机号格式
      const phone = document.getElementById('phone');
      if (phone.value && !/^1[3-9]\d{9}$/.test(phone.value)) {
        isValid = false;
        errors.push('请输入有效的手机号码');
      }

      if (!isValid) {
        e.preventDefault();

        // 显示错误信息
        let messageBox = document.querySelector('.message-box');
        if (!messageBox) {
          messageBox = document.createElement('div');
          messageBox.className = 'message-box error';
          formSection = document.querySelector('.form-section');
          formSection.insertBefore(messageBox, formSection.firstChild);
        }

        messageBox.textContent = errors[0] || '请检查表单输入';
        messageBox.className = 'message-box error';

        // 滚动到错误位置
        messageBox.scrollIntoView({ behavior: 'smooth', block: 'center' });

        return false;
      }

      // 显示加载状态
      submitBtn.classList.add('loading');
      submitBtn.innerHTML = '<i class="fas fa-spinner"></i> 注册中...';

      return true;
    });
  });
</script>
</body>
</html>