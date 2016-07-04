<%--
  Created by IntelliJ IDEA.
  User: hadoop
  Date: 2016/7/1
  Time: 21:03
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title><%=request.getParameter("message")%></title>
    <script type="text/javascript">
        function close() {
            window.close();
        }
    </script>
</head>
<body>
    <div>
        <button type="button" onclick="close()"><%=request.getParameter("message")%></button>
    </div>
</body>
</html>
