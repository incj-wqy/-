<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*,com.mall.model.Order" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>订单管理</title>
</head>
<body>
<jsp:include page="admin_nav.jsp" />

<%
  String status = (String) request.getAttribute("status");
  String orderNo = (String) request.getAttribute("orderNo");
  String startDate = (String) request.getAttribute("startDate");
  String endDate = (String) request.getAttribute("endDate");
%>

<div class="container">
  <h2 class="mb-4">订单管理</h2>

  <!-- 筛选表单 -->
  <form method="get" action="<%=request.getContextPath()%>/admin/order" class="row g-3 mb-4">
    <input type="hidden" name="action" value="list">
    <div class="col-md-2">
      <label class="form-label">状态</label>
      <select name="status" class="form-select">
        <option value="" <%= (status==null || "".equals(status)) ? "selected" : "" %>>全部</option>
        <option value="待付款" <%= "待付款".equals(status) ? "selected" : "" %>>待付款</option>
        <option value="已发货" <%= "已发货".equals(status) ? "selected" : "" %>>已发货</option>
        <option value="已完成" <%= "已完成".equals(status) ? "selected" : "" %>>已完成</option>
      </select>
    </div>
    <div class="col-md-2">
      <label class="form-label">订单号</label>
      <input type="text" name="orderNo" class="form-control" value="<%=orderNo==null?"":orderNo%>">
    </div>
    <div class="col-md-2">
      <label class="form-label">开始日期</label>
      <input type="date" name="startDate" class="form-control" value="<%=startDate==null?"":startDate%>">
    </div>
    <div class="col-md-2">
      <label class="form-label">结束日期</label>
      <input type="date" name="endDate" class="form-control" value="<%=endDate==null?"":endDate%>">
    </div>
    <div class="col-md-2 d-flex align-items-end">
      <button type="submit" class="btn btn-outline-primary w-100">筛选</button>
    </div>
  </form>

  <!-- 订单列表 -->
  <div class="table-responsive">
    <table class="table table-bordered table-hover align-middle">
      <thead class="table-dark">
      <tr>
        <th>订单号</th>
        <th>用户ID</th>
        <th>金额</th>
        <th>状态</th>
        <th>支付方式</th>
        <th>下单时间</th>
        <th>操作</th>
      </tr>
      </thead>
      <tbody>
      <%
        List<Order> orders = (List<Order>) request.getAttribute("orders");
        if (orders != null && !orders.isEmpty()) {
          for (Order o : orders) {
      %>
      <tr>
        <td><%=o.getOrderNo()%></td>
        <td><%=o.getUserId()%></td>
        <td class="text-success">￥<%=o.getTotalAmount()%></td>
        <td>
            <span class="badge bg-<%="已完成".equals(o.getStatus()) ? "success" : "warning"%>">
              <%=o.getStatus()%>
            </span>
        </td>
        <td><%=o.getPaymentMethod()%></td>
        <td><%=o.getCreateTime()%></td>
        <td>
          <a href="<%=request.getContextPath()%>/admin/order?action=detail&orderNo=<%=o.getOrderNo()%>"
             class="btn btn-sm btn-outline-info">详情</a>
        </td>
      </tr>
      <%
        }
      } else {
      %>
      <tr>
        <td colspan="7" class="text-center text-muted">暂无订单</td>
      </tr>
      <%
        }
      %>
      </tbody>
    </table>
  </div>
</div>

</body>
</html>