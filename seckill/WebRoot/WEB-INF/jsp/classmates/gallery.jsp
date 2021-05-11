<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    <meta charset="utf-8">
    <title>老同学</title>
  	<link href="/seckill/theme/css/bootstrap.css" rel="stylesheet">
  	<link href="/seckill/theme/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="/seckill/theme/css/gallery.css">
  </head>
  
 <body onselectstart="return false">
 
 	<!--2.改写视图，模板字符串-->
	<div class="wrap" id="wrap">
		<!--负责平移、旋转-->
		<div class="photo rotate-front" onClick="turn(this)"; id="photo{{index}}">
				<!--负责翻转-->
				<div class="photo-wrap">
					<div class="side side-front">
						<p class="image"><img src="/seckill/theme/img/{{img}}"/></p>
						<p class="caption">{{caption}}</p>
					</div>
					<div class="side side-back">
						<div class="desc">{{desc}}</div>
					</div>
				</div>
		</div>
	</div>
	
	<script src="/seckill/theme/img/data.js"></script>
	<script src="/seckill/theme/js/gallery.js"></script>
</body>
</html>
