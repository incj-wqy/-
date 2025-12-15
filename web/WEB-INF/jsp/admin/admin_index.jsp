<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>后台首页</title>
  <link rel="stylesheet" href="<%=request.getContextPath()%>/static/css/style.css">
  <style>
    /* 页面布局样式 */
    .page-container {
      display: flex;
      flex-direction: column;
      height: 100vh;
      padding: 20px;
      box-sizing: border-box;
    }

    .stats-section {
      margin-bottom: 20px;
      padding: 20px;
      background: white;
      border-radius: 8px;
      box-shadow: 0 2px 10px rgba(0,0,0,0.1);
    }

    .chat-section {
      flex: 1;
      min-height: 500px;
      background: white;
      border-radius: 8px;
      box-shadow: 0 2px 10px rgba(0,0,0,0.1);
      overflow: hidden;
    }

    .stats-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
      gap: 20px;
      margin-top: 15px;
    }

    .stat-item {
      padding: 15px;
      background: #f8f9fa;
      border-radius: 6px;
      border-left: 4px solid #4CAF50;
    }

    .stat-item h3 {
      margin: 0 0 8px 0;
      color: #666;
      font-size: 14px;
    }

    .stat-value {
      font-size: 24px;
      font-weight: bold;
      color: #2c3e50;
      margin: 0;
    }

    .stat-value.currency {
      color: #27ae60;
    }

    .chat-header {
      padding: 15px 20px;
      background: #4CAF50;
      color: white;
      font-weight: bold;
    }

    /* 响应式设计 */
    @media (max-width: 768px) {
      .page-container {
        padding: 10px;
      }

      .stats-grid {
        grid-template-columns: 1fr;
      }

      .chat-section {
        min-height: 400px;
      }
    }
  </style>
</head>
<body>

<jsp:include page="admin_nav.jsp" />

<%
  Double totalSalesObj = (Double) request.getAttribute("totalSales");
  Integer orderCountObj = (Integer) request.getAttribute("orderCount");
  Integer userCountObj = (Integer) request.getAttribute("userCount");

  double totalSales = totalSalesObj == null ? 0 : totalSalesObj;
  int orderCount = orderCountObj == null ? 0 : orderCountObj;
  int userCount = userCountObj == null ? 0 : userCountObj;
%>

<div class="page-container">
  <div class="stats-section">
    <h2>数据概览</h2>
    <div class="stats-grid">
      <div class="stat-item">
        <h3>总销售额</h3>
        <p class="stat-value currency">￥<%=String.format("%.2f", totalSales)%></p>
      </div>
      <div class="stat-item">
        <h3>订单总数</h3>
        <p class="stat-value"><%=orderCount%></p>
      </div>
      <div class="stat-item">
        <h3>用户总数</h3>
        <p class="stat-value"><%=userCount%></p>
      </div>
    </div>
  </div>

  <div class="chat-section">
    <div class="chat-header">
      在线客服聊天窗口
    </div>
    <iframe
            src="http://localhost:8080/chat/44f106f1c5c20273"
            style="width: 100%; height: calc(100% - 50px);"
            frameborder="0"
            allow="microphone"
            title="在线客服聊天">
    </iframe>
  </div>
</div>

</body>
</html>