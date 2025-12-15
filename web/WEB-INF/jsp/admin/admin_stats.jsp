<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>数据统计</title>
  <style>
    .bar-container { background:#eee; height:20px; width:100%; max-width:300px; border-radius:3px; }
    .bar { background:#0d6efd; height:20px; border-radius:3px; }
  </style>
</head>
<body>
<jsp:include page="admin_nav.jsp" />

<%
  List<Object[]> dailySales = (List<Object[]>) request.getAttribute("dailySales");
  List<Object[]> categorySales = (List<Object[]>) request.getAttribute("categorySales");
  List<Object[]> topProducts = (List<Object[]>) request.getAttribute("topProducts");
%>

<div class="container">
  <h2 class="mb-4">数据统计</h2>

  <!-- 日销售额 -->
  <div class="card mb-4">
    <div class="card-header bg-primary text-white">最近 7 天销售额（时间趋势）</div>
    <div class="card-body">
      <div class="table-responsive">
        <table class="table table-sm">
          <thead>
          <tr><th>日期</th><th>销售额</th><th>图示</th></tr>
          </thead>
          <tbody>
          <%
            if (dailySales != null && !dailySales.isEmpty()) {
              double max = 0;
              for (Object[] row : dailySales) {
                double v = (Double) row[1];
                if (v > max) max = v;
              }
              if (max == 0) max = 1;
              for (Object[] row : dailySales) {
                String day = (String) row[0];
                double amount = (Double) row[1];
                int width = (int) (amount / max * 300);
          %>
          <tr>
            <td><%=day%></td>
            <td class="text-success">￥<%=String.format("%.2f", amount)%></td>
            <td>
              <div class="bar-container">
                <div class="bar" style="width:<%=width%>px;"></div>
              </div>
            </td>
          </tr>
          <%
            }
          } else {
          %>
          <tr><td colspan="3" class="text-center text-muted">暂无数据</td></tr>
          <%
            }
          %>
          </tbody>
        </table>
      </div>
    </div>
  </div>

  <!-- 品类销量 -->
  <div class="card mb-4">
    <div class="card-header bg-success text-white">品类销量对比（按数量）</div>
    <div class="card-body">
      <div class="table-responsive">
        <table class="table table-sm">
          <thead>
          <tr><th>品类</th><th>销量</th><th>图示</th></tr>
          </thead>
          <tbody>
          <%
            if (categorySales != null && !categorySales.isEmpty()) {
              long maxQty = 0;
              for (Object[] row : categorySales) {
                long q = (Long) row[1];
                if (q > maxQty) maxQty = q;
              }
              if (maxQty == 0) maxQty = 1;
              for (Object[] row : categorySales) {
                String cname = (String) row[0];
                long qty = (Long) row[1];
                int width = (int) (qty * 1.0 / maxQty * 300);
          %>
          <tr>
            <td><%=cname%></td>
            <td><%=qty%></td>
            <td>
              <div class="bar-container">
                <div class="bar" style="width:<%=width%>px;"></div>
              </div>
            </td>
          </tr>
          <%
            }
          } else {
          %>
          <tr><td colspan="3" class="text-center text-muted">暂无数据</td></tr>
          <%
            }
          %>
          </tbody>
        </table>
      </div>
    </div>
  </div>

  <!-- Top 商品 -->
  <div class="card">
    <div class="card-header bg-info text-white">Top 商品排行榜（按销量）</div>
    <div class="card-body">
      <div class="table-responsive">
        <table class="table table-sm">
          <thead>
          <tr><th>排名</th><th>商品</th><th>销量</th></tr>
          </thead>
          <tbody>
          <%
            if (topProducts != null && !topProducts.isEmpty()) {
              int rank = 1;
              for (Object[] row : topProducts) {
                String pname = (String) row[0];
                long qty = (Long) row[1];
          %>
          <tr>
            <td><b><%=rank++%></b></td>
            <td><%=pname%></td>
            <td><%=qty%></td>
          </tr>
          <%
            }
          } else {
          %>
          <tr><td colspan="3" class="text-center text-muted">暂无数据</td></tr>
          <%
            }
          %>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</div>

</body>
</html>