<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head> 
    <title>微信授权登录</title>
	<style>
	body{margin:0;padding:0;}
	#box{width:100px;height:100px;position: absolute;top:50%;left:50%;margin-top:-250px;margin-left:-250px;}
	</style>
  </head>
  
  <body>
    <div id="box">
    	<form action="/">
    		账号：<input type="text" id="name" name="name"></input><br>
    		密码：<input type="password" id="password" name="password"></input>
    		<input type="text" id="openId" name="openId" value="${openId }" style="display:none" />
    		<button type="submit">登录</button>
    	</form>
    </div>
  </body>
</html>
