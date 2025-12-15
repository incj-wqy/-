<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*,com.mall.model.Category" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>分类编辑</title>
</head>
<body>
<jsp:include page="admin_nav.jsp" />

<%
    Category c = (Category) request.getAttribute("category");
    if (c == null) { c = new Category(); }
    List<Category> all = (List<Category>) request.getAttribute("allCategories");
%>

<div class="container">
    <h2 class="mb-4"><%= (c.getId()==0 ? "新增分类" : "编辑分类") %></h2>

    <form action="<%=request.getContextPath()%>/admin/category?action=save" method="post" class="row g-3">
        <input type="hidden" name="id" value="<%=c.getId()%>">

        <div class="col-md-6">
            <label class="form-label">名称</label>
            <input type="text" name="name" class="form-control" value="<%=c.getName()==null?"":c.getName()%>" required>
        </div>

        <div class="col-md-6">
            <label class="form-label">上级分类</label>
            <select name="parentId" class="form-select">
                <option value="0">无（顶级）</option>
                <%
                    if (all != null) {
                        for (Category cc : all) {
                            String selected = (cc.getId() == c.getParentId()) ? "selected" : "";
                %>
                <option value="<%=cc.getId()%>" <%=selected%>><%=cc.getName()%></option>
                <%
                        }
                    }
                %>
            </select>
        </div>

        <div class="col-md-6">
            <label class="form-label">排序</label>
            <input type="number" name="sort" class="form-control" value="<%=c.getSort()%>">
        </div>

        <div class="col-12">
            <button type="submit" class="btn btn-primary">保存</button>
            <a href="<%=request.getContextPath()%>/admin/category?action=list" class="btn btn-secondary">返回列表</a>
        </div>
    </form>
</div>

</body>
</html>