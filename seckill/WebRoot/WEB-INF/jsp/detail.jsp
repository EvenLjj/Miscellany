<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<!DOCTYPE html>
<html>
<head>
<title>秒杀系统</title>
<%@include file="common/head.jsp"%>
</head>
<body>
	<div class="container">
		<div class="penel panel-default text-center">
			<div class="panel-heading">
				<h1>${seckill.name}</h1>
			</div>
			<div class="panel-body">
				<h2 class="text-danger">
					<!-- 显示time图标 -->
					<span class="glyphicon glyphicon-time"></span>
					<!-- 展示倒计时 -->
					<span class="glyphicon" id="seckill-box"></span>
				</h2>
			</div>
		</div>
	</div>
	
		<!-- 弹出层 实现登陆系统-->
	<div id="killPhoneModal" class="modal fade">
	    <!-- modal-content -->
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<h3 class="modal-title text-center">
						<span class="glyphicon glyphicon-phone"></span>电话：
					</h3>
				</div>
				
				<div class="modal-body">
					<div class="row">
						<div class="col-xs-8 col-xs-offset-2">
							<input type="text" name="killPhone" id="killPhoneKey" 
							placeholder="请输入手机号" class="form-control"/>
						</div>
					</div>
				</div>
				
				<div class="modal-footer">
					<!-- 验证信息 -->
					<span id="killPhoneMessage" class="glyphicon"></span>
					<button type="button" id="killPhoneBtn" class="btn btn-success">
						<span class="glyphicon glyphicon-phone"></span>
						提交
					</button>
				</div>
			</div>	
		</div>
	</div>
	
	
</body>
<!-- jQuery文件。务必在bootstrap.min.js 之前引入 -->
<script src="http://apps.bdimg.com/libs/jquery/2.0.0/jquery.min.js"></script>

<!-- 最新的 Bootstrap 核心 JavaScript 文件 -->
<script src="http://apps.bdimg.com/libs/bootstrap/3.3.0/js/bootstrap.min.js"></script>

<!-- 使用cdn的jquery操作cookie的js -->
<script src="http://cdn.bootcss.com/jquery-cookie/1.4.1/jquery.cookie.min.js"></script>
<script src="http://cdn.bootcss.com/jquery.countdown/2.1.0/jquery.countdown.min.js"></script>

<!--有一个注意点-->
<script type="text/javascript" src="${pageContext.request.contextPath}/theme/js/seckill.js"></script>
<script type="text/javascript">
	$(function(){
		seckill.detail.init(
			{
				seckillId : "${seckill.seckillId}",
				startTime : "${seckill.startTime.time}",
				endTime   : "${seckill.endTime.time}"
			}
		);
	});
</script>
</html>