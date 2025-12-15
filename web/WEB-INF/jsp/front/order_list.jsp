<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*,com.mall.model.Order" %>
<jsp:include page="header.jsp">
    <jsp:param name="title" value="我的订单" />
</jsp:include>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>我的订单</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css">
</head>
<body>
<h2>我的订单</h2>

<form method="get" action="${pageContext.request.contextPath}/OrderServlet">
    <input type="hidden" name="action" value="list">
    状态筛选：
    <select name="status">
        <option value=""  ${empty status ? "selected" : ""}>全部</option>
        <option value="待付款" ${"待付款".equals(status) ? "selected" : ""}>待付款</option>
        <option value="已发货" ${"已发货".equals(status) ? "selected" : ""}>已发货</option>
        <option value="已完成" ${"已完成".equals(status) ? "selected" : ""}>已完成</option>
    </select>
    <input type="submit" value="筛选">
</form>

<br>

<table border="1" cellpadding="5" cellspacing="0" width="100%">
    <tr>
        <th>订单号</th>
        <th>金额</th>
        <th>状态</th>
        <th>支付方式</th>
        <th>下单时间</th>
        <th>操作</th>
    </tr>
    <%
        List<Order> orders = (List<Order>) request.getAttribute("orders");
        if (orders != null && !orders.isEmpty()) {
            for (Order o : orders) {
    %>
    <tr>
        <td><%=o.getOrderNo()%></td>
        <td>￥<%=o.getTotalAmount()%></td>
        <td><%=o.getStatus()%></td>
        <td><%=o.getPaymentMethod()%></td>
        <td><%=o.getCreateTime()%></td>
        <td>
            <a href="${pageContext.request.contextPath}/OrderServlet?action=detail&orderNo=<%=o.getOrderNo()%>">
                查看详情
            </a>
        </td>
    </tr>
    <%
        }
    } else {
    %>
    <tr>
        <td colspan="6" align="center">暂无订单</td>
    </tr>
    <%
        }
    %>
</table>

</body>
</html>
