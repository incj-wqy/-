<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*,com.mall.model.Category" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>商品分类管理</title>
</head>
<body>
<jsp:include page="admin_nav.jsp" />

<div class="container">
  <div class="d-flex justify-content-between align-items-center mb-4">
    <h2>商品分类管理</h2>
    <a href="<%=request.getContextPath()%>/admin/category?action=toEdit" class="btn btn-success">+ 新增分类</a>
  </div>

  <div class="table-responsive">
    <table class="table table-bordered table-hover align-middle">
      <thead class="table-dark">
      <tr>
        <th>ID</th>
        <th>名称</th>
        <th>上级ID</th>
        <th>排序</th>
        <th>操作</th>
      </tr>
      </thead>
      <tbody>
      <%
        List<Category> cats = (List<Category>) request.getAttribute("categories");
        if (cats != null) {
          for (Category c : cats) {
      %>
      <tr>
        <td><%=c.getId()%></td>
        <td><%=c.getName()%></td>
        <td><%=c.getParentId() == 0 ? "顶级" : c.getParentId()%></td>
        <td><%=c.getSort()%></td>
        <td>
          <a href="<%=request.getContextPath()%>/admin/category?action=toEdit&id=<%=c.getId()%>" class="btn btn-sm btn-outline-primary">编辑</a>
          <a href="<%=request.getContextPath()%>/admin/category?action=delete&id=<%=c.getId()%>"
             onclick="return confirm('确认删除该分类？');" class="btn btn-sm btn-outline-danger">删除</a>
        </td>
      </tr>
      <%
          }
        }
      %>
      </tbody>
    </table>
  </div>
</div>

</body>
</html>