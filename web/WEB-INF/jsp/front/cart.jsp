<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*,com.mall.model.CartItem" %>
<jsp:include page="header.jsp">
    <jsp:param name="title" value="购物车" />
</jsp:include>

<div class="container">
    <h2><i class="fas fa-shopping-cart"></i> 我的购物车</h2>

    <div class="card">
        <table>
            <thead>
            <tr>
                <th width="40%">商品信息</th>
                <th>单价</th>
                <th>数量</th>
                <th>小计</th>
                <th>操作</th>
            </tr>
            </thead>
            <tbody>
            <%
                Map<Integer, CartItem> cart = (Map<Integer, CartItem>) session.getAttribute("cart");
                if (cart != null && !cart.isEmpty()) {
                    for (CartItem item : cart.values()) {
            %>
            <tr>
                <td>
                    <div style="display:flex; align-items:center; gap:10px;">
                        <img src="<%=item.getProduct().getImage()%>" style="width:50px;height:50px;object-fit:cover;border-radius:4px;">
                        <b><%=item.getProduct().getName()%></b>
                    </div>
                </td>
                <td>￥<%=item.getProduct().getPrice()%></td>
                <td>
                    <form action="${pageContext.request.contextPath}/CartServlet?action=update" method="post" style="display:flex; gap:5px;">
                        <input type="hidden" name="productId" value="<%=item.getProduct().getId()%>">
                        <input type="number" name="quantity" value="<%=item.getQuantity()%>" min="1" style="width:70px; padding:5px; margin:0;">
                        <button type="submit" class="btn btn-sm" style="margin:0;"><i class="fas fa-sync"></i></button>
                    </form>
                </td>
                <td style="color:var(--danger-color); font-weight:bold;">￥<%=item.getSubtotal()%></td>
                <td>
                    <a href="${pageContext.request.contextPath}/CartServlet?action=delete&productId=<%=item.getProduct().getId()%>"
                       style="color:#e74c3c;">
                        <i class="fas fa-trash"></i> 删除
                    </a>
                </td>
            </tr>
            <%
                }
            } else {
            %>
            <tr>
                <td colspan="5" style="text-align:center; padding: 40px;">
                    <i class="fas fa-shopping-basket" style="font-size: 40px; color: #ddd; margin-bottom: 10px;"></i>
                    <p>购物车空空如也，快去选购吧！</p>
                </td>
            </tr>
            <%
                }
            %>
            </tbody>
        </table>

        <div style="display:flex; justify-content: space-between; align-items: center; margin-top: 20px; padding-top: 20px; border-top: 1px solid #eee;">
            <div>
                <a href="${pageContext.request.contextPath}/ProductServlet?action=list" class="btn" style="background:#95a5a6;">
                    <i class="fas fa-arrow-left"></i> 继续购物
                </a>
                <a href="${pageContext.request.contextPath}/CartServlet?action=clear" class="btn btn-danger" style="margin-left: 10px;">
                    清空购物车
                </a>
            </div>
            <div style="text-align: right;">
                <span style="font-size: 18px; margin-right: 20px;">总计: <b style="color:var(--danger-color); font-size: 24px;">￥${cartTotal}</b></span>
                <a href="${pageContext.request.contextPath}/OrderServlet?action=toCheckout" class="btn" style="padding: 12px 30px; font-size: 16px;">
                    去结算 <i class="fas fa-arrow-right"></i>
                </a>
            </div>
        </div>
    </div>
</div>

<%@ include file="footer.jsp" %>
