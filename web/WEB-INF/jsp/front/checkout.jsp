<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.mall.model.User" %>
<%
    // 从session中获取用户信息
    User user = (User) session.getAttribute("user");
    String address = "";
    if (user != null && user.getAddress() != null) {
        address = user.getAddress();
    }
%>
<jsp:include page="header.jsp">
    <jsp:param name="title" value="订单结算" />
</jsp:include>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>订单结算</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css">
    <style>
        body {
            font-family: 'Arial', sans-serif;
            background-color: #f5f5f5;
            margin: 0;
            padding: 0;
        }
        .container {
            max-width: 800px;
            margin: 40px auto;
            padding: 30px;
            background-color: #fff;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
            border-radius: 8px;
        }
        h2 {
            text-align: center;
            color: #333;
            font-size: 2rem;
            margin-bottom: 30px;
            padding-bottom: 15px;
            border-bottom: 1px solid #eee;
        }
        .form-group {
            margin-bottom: 20px;
        }
        .form-group label {
            display: block;
            font-size: 1.1rem;
            color: #333;
            margin-bottom: 8px;
            font-weight: 500;
        }
        .form-group input, .form-group select {
            width: 100%;
            padding: 12px;
            border: 1px solid #ddd;
            border-radius: 5px;
            font-size: 1rem;
            box-sizing: border-box;
        }
        .form-group input:focus, .form-group select:focus {
            border-color: #3498db;
            outline: none;
            box-shadow: 0 0 5px rgba(52, 152, 219, 0.3);
        }
        .form-group input[type="submit"] {
            background-color: #3498db;
            color: white;
            border: none;
            cursor: pointer;
            padding: 14px;
            font-size: 1.1rem;
            font-weight: 600;
            transition: background-color 0.3s;
        }
        .form-group input[type="submit"]:hover {
            background-color: #2980b9;
        }
        .total-amount {
            font-size: 1.3rem;
            color: #e74c3c;
            padding: 10px 0;
        }
        .back-link {
            text-align: center;
            margin-top: 20px;
        }
        .back-link a {
            color: #3498db;
            text-decoration: none;
        }
        .back-link a:hover {
            text-decoration: underline;
        }
    </style>
</head>
<body>

<div class="container">
    <h2>订单结算</h2>
    <form action="${pageContext.request.contextPath}/OrderServlet?action=create" method="post">
        <div class="form-group">
            <label for="address">收货地址：</label>
            <input type="text" id="address" name="address" size="60" value="<%= address %>" required>
        </div>

        <div class="form-group">
            <label for="paymentMethod">支付方式：</label>
            <select name="paymentMethod" id="paymentMethod" required>
                <option value="支付宝">支付宝</option>
                <option value="微信">微信</option>
                <option value="货到付款">货到付款</option>
            </select>
        </div>

        <div class="form-group">
            <label for="coupon">优惠券：</label>
            <input type="text" id="coupon" name="coupon" placeholder="例如：OFF10（减10元）">
        </div>

        <div class="form-group">
            <label>订单金额：</label>
            <div class="total-amount">￥${cartTotal}</div>
        </div>

        <div class="form-group">
            <input type="submit" value="确认下单">
        </div>
    </form>

    <div class="back-link">
        <a href="${pageContext.request.contextPath}/CartServlet">返回购物车</a>
    </div>
</div>

</body>
</html>