<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.mall.model.Admin" %>
<%
  Admin adminUser = (Admin) session.getAttribute("adminUser");
  String adminName = adminUser == null ? "未登录" : adminUser.getUsername();
%>

<!-- 引入 Bootstrap 5 -->
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

<nav class="navbar navbar-dark bg-dark mb-4">
  <div class="container-fluid">
    <span class="navbar-brand mb-0 h5">后台管理 - 当前管理员：<b><%=adminName%></b></span>
    <div>
      <a class="btn btn-outline-light btn-sm me-2" href="<%=request.getContextPath()%>/admin/home">首页</a>
      <a class="btn btn-outline-light btn-sm me-2" href="<%=request.getContextPath()%>/admin/category?action=list">商品分类</a>
      <a class="btn btn-outline-light btn-sm me-2" href="<%=request.getContextPath()%>/admin/product?action=list">商品管理</a>
      <a class="btn btn-outline-light btn-sm me-2" href="<%=request.getContextPath()%>/admin/order?action=list">订单管理</a>
      <a class="btn btn-outline-light btn-sm me-2" href="<%=request.getContextPath()%>/admin/stats">数据统计</a>
    </div>
  </div>
</nav>