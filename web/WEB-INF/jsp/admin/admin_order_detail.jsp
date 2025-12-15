<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.mall.model.Order, java.util.List, com.mall.model.OrderItem" %>
<html>
<head>
    <meta charset="UTF-8">
    <title>订单详情</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<jsp:include page="admin_nav.jsp" />

<%
    Order order = (Order) request.getAttribute("order");
    List<OrderItem> items = (List<OrderItem>) request.getAttribute("orderItems");
%>

<div class="container mt-4">
    <h2>订单详情</h2>
    <a href="<%=request.getContextPath()%>/admin/order?action=list" class="btn btn-secondary mb-4">← 返回订单列表</a>

    <% if (order != null) { %>
    <div class="row mb-4">
        <div class="col-md-6">
            <h5>订单信息</h5>
            <ul class="list-group">
                <li class="list-group-item"><strong>订单号：</strong><%=order.getOrderNo()%></li>
                <li class="list-group-item"><strong>用户ID：</strong><%=order.getUserId()%></li>
                <li class="list-group-item"><strong>总金额：</strong>￥<%=order.getTotalAmount()%></li>
                <li class="list-group-item"><strong>状态：</strong><%=order.getStatus()%></li>
                <li class="list-group-item"><strong>支付方式：</strong><%=order.getPaymentMethod()%></li>
                <li class="list-group-item"><strong>下单时间：</strong><%=order.getCreateTime()%></li>
                <li class="list-group-item"><strong>收货地址：</strong><%=order.getAddress() == null ? "未填写" : order.getAddress()%></li>
            </ul>
        </div>
    </div>

    <h5>商品明细</h5>
    <div class="table-responsive">
        <table class="table table-bordered">
            <thead class="table-light">
            <tr>
                <th>商品名</th>
                <th>单价</th>
                <th>数量</th>
                <th>小计</th>
            </tr>
            </thead>
            <tbody>
            <% if (items != null && !items.isEmpty()) {
                for (OrderItem item : items) { %>
            <tr>
                <td><%=item.getProductName()%></td>
                <td>￥<%=item.getPrice()%></td>
                <td><%=item.getQuantity()%></td>
                <td>￥<%=item.getPrice() * item.getQuantity()%></td>
            </tr>
            <% }
            } else { %>
            <tr><td colspan="4" class="text-center">无商品信息</td></tr>
            <% } %>
            </tbody>
        </table>
    </div>
    <% } else { %>
    <div class="alert alert-warning">订单不存在或已被删除。</div>
    <% } %>
</div>

</body>
</html>