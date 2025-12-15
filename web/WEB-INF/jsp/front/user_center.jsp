<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.mall.model.User" %>
<jsp:include page="header.jsp">
    <jsp:param name="title" value="个人中心" />
</jsp:include>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>个人中心</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css">
</head>
<body>
<%
    User u = (User) session.getAttribute("currentUser");
%>
<h2>个人中心</h2>
<p>欢迎你，<b><%=u.getUsername()%></b></p>

<h3>收货信息 / 联系方式</h3>
<form action="${pageContext.request.contextPath}/UserServlet?action=update" method="post">
    邮箱：<input type="email" name="email" value="<%=u.getEmail()==null?"":u.getEmail()%>"><br>
    手机：<input type="text" name="phone" value="<%=u.getPhone()==null?"":u.getPhone()%>"><br>
    收货地址：<input type="text" name="address" size="50" value="<%=u.getAddress()==null?"":u.getAddress()%>"><br>
    <input type="submit" value="保存">
</form>

<h3>订单状态概览</h3>
<ul>
    <li><a href="${pageContext.request.contextPath}/OrderServlet?action=list&status=待付款">待付款订单</a></li>
    <li><a href="${pageContext.request.contextPath}/OrderServlet?action=list&status=已发货">已发货订单</a></li>
    <li><a href="${pageContext.request.contextPath}/OrderServlet?action=list&status=已完成">已完成订单</a></li>
    <li><a href="${pageContext.request.contextPath}/OrderServlet?action=list">全部订单</a></li>
</ul>

</body>
</html>
