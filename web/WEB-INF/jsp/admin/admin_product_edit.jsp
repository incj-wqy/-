<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*,com.mall.model.Product,com.mall.model.Category" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>商品编辑</title>
</head>
<body>
<jsp:include page="admin_nav.jsp" />

<%
  Product p = (Product) request.getAttribute("product");
  if (p == null) { p = new Product(); }
  List<Category> cats = (List<Category>) request.getAttribute("categories");
%>

<div class="container">
  <h2 class="mb-4"><%= (p.getId() == 0 ? "新增商品" : "编辑商品") %></h2>

  <form action="<%=request.getContextPath()%>/admin/product?action=save" method="post">
    <input type="hidden" name="id" value="<%=p.getId()%>">

    <div class="mb-3">
      <label class="form-label">商品名称</label>
      <input type="text" name="name" class="form-control" value="<%= (p.getName() == null ? "" : p.getName()) %>" required>
    </div>

    <div class="mb-3">
      <label class="form-label">分类</label>
      <select name="categoryId" class="form-select" required>
        <%
          if (cats != null && !cats.isEmpty()) {
            for (Category c : cats) {
              String selected = (c.getId() == p.getCategoryId()) ? "selected" : "";
        %>
        <option value="<%=c.getId()%>" <%=selected%>><%=c.getName()%></option>
        <%
          }
        } else {
        %>
        <option>暂无分类，请先添加</option>
        <%
          }
        %>
      </select>
    </div>

    <div class="row mb-3">
      <div class="col-md-6">
        <label class="form-label">价格（元）</label>
        <input type="number" step="0.01" name="price" class="form-control"
               value="<%= (p.getPrice() == 0 ? "" : String.format("%.2f", p.getPrice())) %>" required>
      </div>
      <div class="col-md-6">
        <label class="form-label">库存</label>
        <input type="number" name="stock" class="form-control" value="<%= p.getStock() %>" required>
      </div>
    </div>

    <div class="mb-3">
      <label class="form-label">状态</label>
      <select name="status" class="form-select">
        <option value="1" <%= (p.getStatus() == 1 ? "selected" : "") %>>上架</option>
        <option value="0" <%= (p.getStatus() == 0 ? "selected" : "") %>>下架</option>
      </select>
    </div>

    <div class="mb-3">
      <label class="form-label">主图地址（URL）</label>
      <input type="text" name="image" class="form-control" value="<%= (p.getImage() == null ? "" : p.getImage()) %>">
    </div>

    <div class="mb-3">
      <label class="form-label">多图地址（逗号分隔 URL）</label>
      <textarea name="images" class="form-control" rows="2"><%= (p.getImages() == null ? "" : p.getImages()) %></textarea>
    </div>

    <div class="mb-3">
      <label class="form-label">商品详情（支持简单 HTML）</label>
      <textarea name="description" class="form-control" rows="6"><%= (p.getDescription() == null ? "" : p.getDescription()) %></textarea>
    </div>

    <div class="d-flex gap-2">
      <button type="submit" class="btn btn-primary">保存商品</button>
      <a href="<%=request.getContextPath()%>/admin/product?action=list" class="btn btn-secondary">返回列表</a>
    </div>
  </form>
</div>

</body>
</html>