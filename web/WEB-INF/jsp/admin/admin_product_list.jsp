<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*,com.mall.model.Product" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>商品管理</title>
</head>
<body>
<jsp:include page="admin_nav.jsp" />

<%
    String keyword = (String) request.getAttribute("keyword");
    String status = (String) request.getAttribute("status");
%>

<div class="container">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2>商品管理</h2>
        <a href="<%=request.getContextPath()%>/admin/product?action=toEdit" class="btn btn-success">+ 新增商品</a>
    </div>

    <!-- 搜索表单 -->
    <form method="get" action="<%=request.getContextPath()%>/admin/product" class="row g-3 mb-4">
        <input type="hidden" name="action" value="list">
        <div class="col-md-3">
            <input type="text" name="keyword" class="form-control" placeholder="关键字" value="<%=keyword==null?"":keyword%>">
        </div>
        <div class="col-md-2">
            <select name="status" class="form-select">
                <option value="" <%= (status==null || "".equals(status)) ? "selected" : "" %>>全部状态</option>
                <option value="1" <%= "1".equals(status) ? "selected" : "" %>>上架</option>
                <option value="0" <%= "0".equals(status) ? "selected" : "" %>>下架</option>
            </select>
        </div>
        <div class="col-md-2">
            <button type="submit" class="btn btn-outline-primary">搜索</button>
        </div>
    </form>

    <!-- 商品列表 -->
    <div class="table-responsive">
        <table class="table table-bordered table-hover align-middle">
            <thead class="table-dark">
            <tr>
                <th>ID</th>
                <th>名称</th>
                <th>价格</th>
                <th>库存</th>
                <th>状态</th>
                <th>操作</th>
            </tr>
            </thead>
            <tbody>
            <%
                List<Product> list = (List<Product>) request.getAttribute("products");
                if (list != null && !list.isEmpty()) {
                    for (Product p : list) {
            %>
            <tr>
                <td><%=p.getId()%></td>
                <td><%=p.getName()%></td>
                <td>￥<%=p.getPrice()%></td>
                <td><%=p.getStock()%></td>
                <td>
            <span class="badge bg-<%=p.getStatus()==1 ? "success" : "secondary"%>">
              <%=p.getStatus()==1 ? "上架" : "下架"%>
            </span>
                </td>
                <td>
                    <a href="<%=request.getContextPath()%>/admin/product?action=toEdit&id=<%=p.getId()%>" class="btn btn-sm btn-outline-primary">编辑</a>
                    <a href="<%=request.getContextPath()%>/admin/product?action=toggle&id=<%=p.getId()%>&status=<%=p.getStatus()==1?0:1%>"
                       class="btn btn-sm btn-outline-<%=p.getStatus()==1 ? "warning" : "success"%>">
                        <%=p.getStatus()==1 ? "下架" : "上架"%>
                    </a>
                    <a href="<%=request.getContextPath()%>/admin/product?action=delete&id=<%=p.getId()%>"
                       onclick="return confirm('确认删除该商品？');" class="btn btn-sm btn-outline-danger">删除</a>
                </td>
            </tr>
            <%
                }
            } else {
            %>
            <tr>
                <td colspan="6" class="text-center text-muted">暂无商品数据</td>
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