<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.mall.model.Order,com.mall.model.OrderItem,java.util.*" %>
<jsp:include page="header.jsp">
    <jsp:param name="title" value="订单详情" />
</jsp:include>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>订单详情</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css">
</head>
<body>
<%
    Order o = (Order) request.getAttribute("order");
%>
<h2>订单详情 - <%=o.getOrderNo()%></h2>

<h3>基本信息</h3>
<p>订单状态：<b><%=o.getStatus()%></b></p>
<p>订单金额：<b>￥<%=o.getTotalAmount()%></b></p>
<p>支付方式：<%=o.getPaymentMethod()%></p>
<p>收货地址：<%=o.getAddress()%></p>
<p>下单时间：<%=o.getCreateTime()%></p>

<h3>物流信息</h3>
<p>物流公司：<%= (o.getLogisticsCompany()==null || o.getLogisticsCompany().trim().isEmpty())
        ? "暂未发货" : o.getLogisticsCompany() %></p>
<p>运单号：<%= (o.getTrackingNo()==null || o.getTrackingNo().trim().isEmpty())
        ? "暂未发货" : o.getTrackingNo() %></p>

<h3>商品明细</h3>
<table border="1" cellpadding="5" cellspacing="0" width="100%">
    <tr>
        <th>商品名称</th>
        <th>单价</th>
        <th>数量</th>
        <th>小计</th>
    </tr>
    <%
        List<OrderItem> items = o.getItems();
        if (items != null && !items.isEmpty()) {
            for (OrderItem item : items) {
    %>
    <tr>
        <td><%=item.getProductName()%></td>
        <td>￥<%=item.getPrice()%></td>
        <td><%=item.getQuantity()%></td>
        <td>￥<%=item.getSubtotal()%></td>
    </tr>
    <%
        }
    } else {
    %>
    <tr>
        <td colspan="4" align="center">无明细</td>
    </tr>
    <%
        }
    %>
</table>

<h3>订单状态流转记录</h3>
<p>（简化：这里只展示当前状态；如果想做更完整的流转记录，可以新建一张 order_log 表）</p>

<h3>物流轨迹</h3>
<p>（占位：可以在后台录入轨迹或接第三方接口展示，这里先写“待发货/运输中”等说明即可）</p>

</body>
</html>
