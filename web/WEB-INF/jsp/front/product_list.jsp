<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*,com.mall.model.Product" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="header.jsp">
  <jsp:param name="title" value="商品列表" />
</jsp:include>

<div class="container">

  <div class="card" style="margin-bottom: 20px;">
    <form method="get" action="${pageContext.request.contextPath}/ProductServlet" style="display:flex; gap: 20px; align-items: flex-end; flex-wrap: wrap;">
      <input type="hidden" name="action" value="list">

      <div style="flex:1; min-width: 200px;">
        <label style="font-weight:bold; display:block; margin-bottom:5px;">分类</label>
        <select name="categoryId" style="margin:0;">
          <option value="">所有分类</option>
          <option value="1" ${"1".equals(categoryId) ? "selected" : ""}>电子产品</option>
          <option value="2" ${"2".equals(categoryId) ? "selected" : ""}>服装</option>
        </select>
      </div>

      <div style="flex:1; min-width: 200px;">
        <label style="font-weight:bold; display:block; margin-bottom:5px;">排序</label>
        <select name="sort" style="margin:0;">
          <option value="">默认排序</option>
          <option value="priceAsc" ${"priceAsc".equals(sort) ? "selected" : ""}>价格低 -> 高</option>
          <option value="priceDesc" ${"priceDesc".equals(sort) ? "selected" : ""}>价格高 -> 低</option>
        </select>
      </div>

      <div>
        <button type="submit" class="btn"><i class="fas fa-filter"></i> 筛选</button>
        <a href="${pageContext.request.contextPath}/ProductServlet?action=list" class="btn" style="background:var(--secondary-color); color:#333;">重置</a>
      </div>
    </form>
  </div>

  <div class="product-grid">
    <%
      List<Product> list = (List<Product>) request.getAttribute("products");
      if (list != null && !list.isEmpty()) {
        for (Product p : list) {
    %>
    <div class="product-card">
      <a href="${pageContext.request.contextPath}/ProductServlet?action=detail&id=<%=p.getId()%>">
        <img src="<%= (p.getImage() == null || p.getImage().trim().isEmpty() ? "https://via.placeholder.com/250" : p.getImage()) %>" alt="<%=p.getName()%>">
        <div class="name"><%=p.getName()%></div>
        <div class="price">￥<%=String.format("%.2f", p.getPrice())%></div>
      </a>
      <div style="padding: 0 10px;">
        <a href="${pageContext.request.contextPath}/ProductServlet?action=detail&id=<%=p.getId()%>" class="btn btn-sm" style="width:100%;">查看详情</a>
      </div>
    </div>
    <%
      }
    } else {
    %>
    <div style="grid-column: 1/-1; text-align:center; padding: 50px;">
      <h3>没有找到相关商品</h3>
    </div>
    <%
      }
    %>
  </div>

  <div style="text-align:center; margin-top:30px;">
  </div>
</div>

<%@ include file="footer.jsp" %>
