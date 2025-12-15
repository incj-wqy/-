<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>后台登录</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">
<div class="container d-flex justify-content-center align-items-center min-vh-100">
  <div class="card p-4 shadow" style="width: 100%; max-width: 400px;">
    <h3 class="text-center mb-4">后台登录</h3>
    <form action="<%=request.getContextPath()%>/admin/login" method="post">
      <div class="mb-3">
        <label class="form-label">用户名</label>
        <input type="text" name="username" class="form-control" required>
      </div>
      <div class="mb-3">
        <label class="form-label">密码</label>
        <input type="password" name="password" class="form-control" required>
      </div>
      <button type="submit" class="btn btn-primary w-100">登录</button>
    </form>

    <%
      String msg = (String) request.getAttribute("msg");
      if (msg != null && !msg.trim().isEmpty()) {
    %>
    <div class="alert alert-danger mt-3 mb-0"><%=msg%></div>
    <%
      }
    %>
  </div>
</div>
</body>
</html>