<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.mall.model.User" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${title} - SimpleMall</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css">
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
</head>
<body>
<header class="site-header">
    <div class="header-container">
        <div class="site-logo">
            <a href="${pageContext.request.contextPath}/IndexServlet">
                <i class="fas fa-store"></i> <span>SimpleMall</span>
            </a>
        </div>

        <div class="search-bar">
            <form action="${pageContext.request.contextPath}/ProductServlet" method="get">
                <input type="hidden" name="action" value="list">
                <div class="search-input-group">
                    <input type="text" name="keyword" placeholder="搜索好物..." autocomplete="off">
                    <button type="submit"><i class="fas fa-search"></i></button>
                </div>
            </form>
        </div>

        <nav class="main-nav">
            <a href="${pageContext.request.contextPath}/IndexServlet" class="nav-link">首页</a>
            <a href="${pageContext.request.contextPath}/ProductServlet?action=list" class="nav-link">全部商品</a>
            <a href="${pageContext.request.contextPath}/CartServlet?action=list" class="nav-link">
                <i class="fas fa-shopping-cart"></i> 购物车
            </a>

            <div class="user-nav">
                <%
                    User user = (User) session.getAttribute("currentUser");
                    if (user == null) {
                %>
                <a href="${pageContext.request.contextPath}/UserServlet?action=toLogin" class="nav-link">登录 / 注册</a>
                <%
                } else {
                %>
                <div class="dropdown">
                    <a href="#" class="nav-link dropdown-toggle">
                        <i class="fas fa-user-circle"></i> <%=user.getUsername()%>
                    </a>
                    <div class="dropdown-menu">
                        <a href="${pageContext.request.contextPath}/UserServlet?action=center">个人中心</a>
                        <a href="${pageContext.request.contextPath}/OrderServlet?action=list">我的订单</a>
                        <a href="${pageContext.request.contextPath}/UserServlet?action=logout">退出登录</a>
                    </div>
                </div>
                <%
                    }
                %>
            </div>
        </nav>
    </div>
</header>
</body>
</html>
