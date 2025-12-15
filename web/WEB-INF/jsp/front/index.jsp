<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*,com.mall.model.Category,com.mall.model.Product" %>
<jsp:include page="header.jsp">
    <jsp:param name="title" value="首页" />
</jsp:include>

<div class="container" style="display:flex; gap:20px; margin-top:20px;">
    
    <aside style="width: 250px; flex-shrink: 0;">
        <div class="card" style="padding: 0; overflow: hidden;">
            <h3 style="background:var(--primary-color); color:white; margin:0; padding:15px; font-size:16px;">
                <i class="fas fa-list"></i> 商品分类
            </h3>
            <ul style="padding: 10px 0;">
                <%
                    List<Category> cats = (List<Category>) request.getAttribute("categories");
                    if (cats != null) {
                        for (Category c : cats) {
                            if (c.getParentId() == 0) {
                %>
                <li style="border-bottom:1px solid #f4f4f4;">
                    <a href="${pageContext.request.contextPath}/ProductServlet?action=list&categoryId=<%=c.getId()%>"
                       style="display:block; padding:10px 20px; font-weight:600;">
                        <%=c.getName()%> <i class="fas fa-angle-right" style="float:right; color:#ccc;"></i>
                    </a>
                </li>
                <%
                            }
                        }
                    }
                %>
            </ul>
        </div>

        <div class="card">
            <h3><i class="fas fa-bullhorn"></i> 最新公告</h3>
            <p style="font-size:13px; color:#666;">
                新人注册立减 ¥10！<br>
                结算输入优惠码：<b style="color:var(--danger-color);">OFF10</b>
            </p>
        </div>
    </aside>

    <main style="flex:1;">
        <div style="background:#e3f2fd; height:300px; border-radius:8px; display:flex; align-items:center; justify-content:center; margin-bottom:20px; color:#1565c0;">
            <div style="text-align:center;">
                <h1 style="font-size:40px; margin-bottom:10px;">年中大促开启</h1>
                <p>全场商品低至 5 折起</p>
                <a href="${pageContext.request.contextPath}/ProductServlet?action=list" class="btn" style="margin-top:20px;">立即抢购</a>
            </div>
        </div>

        <h2>热门商品</h2>
        <div class="product-grid">
            <%
                List<Product> hot = (List<Product>) request.getAttribute("hotProducts");
                if (hot != null) {
                    for (Product p : hot) {
            %>
            <div class="product-card">
                <a href="${pageContext.request.contextPath}/ProductServlet?action=detail&id=<%=p.getId()%>">
                    <img src="<%= (p.getImage()==null || p.getImage().trim().isEmpty() ? "https://via.placeholder.com/200" : p.getImage()) %>">
                    <div class="name"><%=p.getName()%></div>
                    <div class="price">￥<%=p.getPrice()%></div>
                </a>
            </div>
            <%
                    }
                }
            %>
        </div>
    </main>
</div>

<%@ include file="footer.jsp" %>
