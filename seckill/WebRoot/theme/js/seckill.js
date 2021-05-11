//存放主要交互逻辑js代码
//javascript 模块化
var seckill={
		//封装秒杀相关ajax的url
		URL:{
			now:function(){
				return '/seckill/seckill/time/now';
			},
			exposer:function(seckillId){
				return '/seckill/seckill/'+seckillId+'/exposer';
			},
			execution:function(seckillId,md5){
				return '/seckill/seckill/'+seckillId+"/"+md5+'/execution';
			}

		},
		validatePhone:function(phone){
			if(phone&&phone.length==11&&!isNaN(phone)){
				return true;
			}else{
				return false;
			}
		},
		handleSeckill:function(seckillId,node){
			//获取秒杀地址，控制逻辑，执行秒杀
			node.hide()
				.html('<button class="btn btn-primary btn-lg" id="killBtn">立即秒杀</button>');
			$.post(seckill.URL.exposer(seckillId),function(result){
				//alert("sekillUrl"+seckill.URL.exposer(seckillId));
				//在回调函数中执行秒杀
				if(result&&result['success']){
					var exposer=result['data'];
					if(exposer['exposed']){
						//开始秒杀
						//获取秒杀地址
						var md5=exposer['md5'];
						var killUrl=seckill.URL.execution(seckillId, md5);
						console.log("killUrl"+killUrl);
						//防止重复提交
						$('#killBtn').one('click',function(){
							//执行秒杀请求
							//1：按钮变灰
							$(this).addClass('disabled');
							//2:发送秒杀请求
							$.post(killUrl,function(result){
								var killResult=result['data'];
								var state=killResult['state'];
								var stateInfo=killResult['stateInfo'];
								if(result&&result['success']){
									//3:显示秒杀结果
									node.html('<span class="label label-success">'+stateInfo+'</span>');
								}else{
									node.html('<span class="label label-warning">'+stateInfo+'</span>');
								}
							});
						});
						node.show();
					}else{
						//	前后台时间不一致，时间未到
						var now=exposer['now'];
						var start=exposer['start'];
						var end=exposer['end'];
						//重新走计时逻辑
						seckill.countdown(seckillId, now, start, end);
					}
				}else{
					console.log('result'+result);
				}
			});
		}
		,
		countdown:function(seckillId,nowTime,startTime,endTime){
			//alert("seckillId:"+seckillId+" nowTime:"+nowTime+" startTime:"+startTime+" endTime:"+endTime);
			var seckillBox=$('#seckill-box');
			if(nowTime>endTime){
				seckillBox.html('秒杀结束');
			}else if(nowTime<startTime){
				//秒杀没开始
				var killTime=new Date(startTime-0+1000);
				//每一次时间变化都会调用该服务(jquery的countdown函数)
				seckillBox.countdown(killTime,function(event){
					//时间格式
					var format=event.strftime('秒杀倒计时: %D天 %H时 %M分 %S秒');
					seckillBox.html(format);
				}).on('finish.countdown',function(){
					//获取秒杀地址，控制逻辑，执行秒杀
					seckill.handleSeckill(seckillId,seckillBox);
				});
			}else{
				// 秒杀开始
				seckill.handleSeckill(seckillId,seckillBox);
			}
		},
		//详情页秒杀逻辑
		detail:{
			//详情页初始化
			init:function(params){
				//手机验证和登陆，计时交互
				//规划我们的交互流程
				//在cookie中查找手机号
				var killPhone=$.cookie('killPhone');
				
				if(!seckill.validatePhone(killPhone)){
					//绑定手机号
					var killPhoneModal=$('#killPhoneModal');
					killPhoneModal.modal({
						show:true,//显示弹出层
						backdrop:'static',//禁止位置关闭
						keyboard:false//关闭键盘事件
					});
					$('#killPhoneBtn').click(function(){
						var inputPhone=$('#killPhoneKey').val();
						if(seckill.validatePhone(inputPhone)){
							//将电话写入cookie
							$.cookie('killPhone',inputPhone,
									{expires:7,path:'/seckill'});
							//刷新页面
							window.location.reload();
						}else{
							$('#killPhoneMessage').hide().html('<label class="label label-danger">手机号出错</label>').show(300);
						}
					});
				}else{
					var startTime=params['startTime'];
					var endTime=params['endTime'];
					var seckillId=params['seckillId'];
					//已经登陆
					$.get(seckill.URL.now(),function(result){
						//alert(result.data+"boolean"+result.success);
						if(result&&result.success){
							var nowTime=result.data;
							//alert("nowTime"+nowTime);
							//alert("another"+result['data']);
							//时间判断
							seckill.countdown(seckillId,nowTime,startTime,endTime);
						}else{
							console.log("result"+result);
						}
					});
				}
			}
		}
};