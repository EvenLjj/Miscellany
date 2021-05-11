<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head lang="en">
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <link href="/seckill/theme/img/favicon.ico" rel="icon" type="image/x-icon" />
  <link href="/seckill/theme/img/favicon.ico" rel="shortcut icon" type="image/x-icon" />
  <title>学生时代</title>
  <link href="/seckill/theme/css/scojs.css" rel="stylesheet">
  <link href="/seckill/theme/css/colpick.css" rel="stylesheet">
  <link href="/seckill/theme/css/bootstrap.css" rel="stylesheet">
  <link href="/seckill/theme/css/bootstrap.min.css" rel="stylesheet">
  <link href="/seckill/theme/css/main.css" rel="stylesheet">
  <link href="/seckill/theme/css/body.css" rel="stylesheet">
  <!--<link rel="stylesheet" href="../dist/css/danmuplayer.css">-->
</head>
<body>
<!-- 开头 -->
<div class="navbar navbar-inverse navbar-fixed-top" role="navigation" id="menu-nav">
	    <div class="container">
	        <div class="navbar-header">
	            <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse" id="demo-navbar">
	                <span class="sr-only">切换导航</span>
	                <span class="icon-bar"></span>
	                <span class="icon-bar"></span>
	                <span class="icon-bar"></span>
	            </button>
	            <a class="navbar-brand" href="#">学生时代</a>
	        </div>
		    <div class="navbar-collapse collapse" id="demo-navbar">
		            <ul class="nav navbar-nav">
		                <li class="active"><a href="/seckill/classmates/list">同学</a></li>
		                <li><a href="#teachers">老师</a></li>
		                <li class="dropdown">
		                    <a href="#" class="dropdown-toggle" data-toggle="dropdown">学校<span class="caret"></span></a>
		                    <ul class="dropdown-menu" role="menu">
		                        <li><a href="#tab-primary" data-tab="tab-primary">丁小</a></li>
		                        <li><a href="#tab-middle" data-tab="tab-middle">李初</a></li>
		                        <li><a href="#tab-high" data-tab="tab-high">李中</a></li>
		                        <li><a href="#tab-university" data-tab="tab-university">通大</a></li>
		                        <li><a href="#tab-graduate" data-tab="tab-graduate">上大</a></li>
		                    </ul>
		                </li>
		                <li><a href="#" data-toggle="modal" data-target="#about-modal">关于</a></li>
		            </ul>
		    </div>
		</div>
 </div>

<!-- 广告轮播 -->
<div id="ad-carousel" class="carousel slide" data-ride="carousel">
    <ol class="carousel-indicators">
        <li data-target="#ad-carousel" data-slide-to="0" class="active"></li>
        <li data-target="#ad-carousel" data-slide-to="1"></li>
        <li data-target="#ad-carousel" data-slide-to="2"></li>
        <li data-target="#ad-carousel" data-slide-to="3"></li>
        <li data-target="#ad-carousel" data-slide-to="4"></li>
    </ol>
    <div class="carousel-inner">
        <div class="item active">
            <img src="/seckill/theme/img/dingxiao.jpg" alt="1 slide">

            <div class="container">
                <div class="carousel-caption">
                    <h1>丁小</h1>

                    <p>人之初，性本善</p>

                    <p><a class="btn btn-lg btn-primary" href="http://www.ntdsxx.net/"
                          role="button" target="_blank">点击发现</a></p>
                </div>
            </div>
        </div>
        <div class="item">
            <img src="/seckill/theme/img/yangzhong.jpg" alt="2 slide">

            <div class="container">
                <div class="carousel-caption">
                    <h1>李堡初级中学</h1>

                    <p>求真务实，追求卓越</p>

                    <p><a class="btn btn-lg btn-primary" href="http://www.halbcz.net/" target="_blank"
                          role="button">点击发现</a></p>
                </div>
            </div>
        </div>
        <div class="item">
            <img src="/seckill/theme/img/lizhong.jpg" alt="3 slide">

            <div class="container">
                <div class="carousel-caption">
                    <h1>李堡中学</h1>

                    <p>校风:“团结和谐  严谨务实”<br/>
                                                                                 学风:“学而不厌”<br/>
                                                                                 教风:“诲人不倦”<br/>
                                                                                 校训:恒                                                          
                    </p>

                    <p><a class="btn btn-lg btn-primary" href="http://www.lbzx.net/index.asp" target="_blank"
                          role="button">点击发现</a></p>
                </div>
            </div>
        </div>
        <div class="item">
            <img src="/seckill/theme/img/tongda.jpg" alt="4 slide">

            <div class="container">
                <div class="carousel-caption">
                    <h1>南通大学</h1>

                    <p>祈通中西，力求精进</p>

                    <p><a class="btn btn-lg btn-primary" href="http://www.ntu.edu.cn/" target="_blank"
                          role="button">点击发现</a></p>
                </div>
            </div>
        </div>
        <div class="item">
            <img src="/seckill/theme/img/shangda.jpg" alt="5 slide">

            <div class="container">
                <div class="carousel-caption">
                    <h1>上海大学</h1>

                    <p>自强不息，求实创新</p>

                    <p><a class="btn btn-lg btn-primary" href="http://yjsb.shu.edu.cn/" target="_blank"
                          role="button">点击发现</a></p>
                </div>
            </div>
        </div>
    </div>
    <a class="left carousel-control" href="#ad-carousel" data-slide="prev"><span
            class="glyphicon glyphicon-chevron-left"></span></a>
    <a class="right carousel-control" href="#ad-carousel" data-slide="next"><span
            class="glyphicon glyphicon-chevron-right"></span></a>
   </div>
   
<div id="video">
   <div>
	   <audio id="music" src="/seckill/BeTheBest.mp3" autoplay="autoplay" loop="loop"></audio>    
	  
	   <marquee direction="right" behabior="alternate"> <a id="audio_btn"><img src="/seckill/theme/img/play.png" width="40" height="45" id="music_btn" border="0"></a></marquee>
   </div>
   <div id="danmup" style="left: 50%;margin-left:-400px;top:20px">
   </div>
   <div style="display: none">
	  <span class="glyphicon" aria-hidden="true">&#xe072</span>
	  <span class="glyphicon" aria-hidden="true">&#xe073</span>
	  <span class="glyphicon" aria-hidden="true">&#xe242</span>
	  <span class="glyphicon" aria-hidden="true">&#xe115</span>
	  <span class="glyphicon" aria-hidden="true">&#xe111</span>
	  <span class="glyphicon" aria-hidden="true">&#xe096</span>
	  <span class="glyphicon" aria-hidden="true">&#xe097</span>
    </div>
</div>   

<!-- 关于 -->
<div class="modal fade" id="about-modal" tabindex="-1" role="dialog" aria-labelledby="modal-label"
     aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"><span
                        aria-hidden="true">&times;</span><span class="sr-only">关闭</span></button>
                <h4 class="modal-title" id="modal-label">关于</h4>
            </div>
            <div class="modal-body">
                <p>还记得和你一起玩丢手绢的ta们吗？<br>还记得和你一起敲锣打鼓去食堂吃饭的ta们吗？<br>
                                                                  还记得在宿舍嬉戏打闹躲被窝看小说的ta们吗？<br>还记得和你一起起早贪黑备战高考的ta们吗？<br>
                                                                  还记得网吧五连坐从来没赢过的ta们吗？<br>忘不了，就对了。留住美好回忆，就在“学生时代”。<br>
                                                                  美好回忆需要你来分享，邮箱联系evenljj@163.com上传属于自己的回忆与创意，或者关注微信公众号：SHU_family，更多精彩等你来哦。(个人创意，请勿抄袭)</p>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">了解了</button>
            </div>
        </div>
    </div>
</div>
<footer id="footer" class="top-space">
		<div class="footer1">
			<div class="container">
				<div class="row">
					
					<div class="col-md-6 widget">
						<h3 class="widget-title">学生时代</h3>
						<div class="widget-body">
							<p><br>
								<a href="http://www.51cco.com.cn/seckill/memory.mp4">视频下载</a><br>
								<br>

							</p>	
						</div>
					</div>

				<!-- 	<div class="col-md-3 widget">
						<h3 class="widget-title">Follow me</h3>
						<div class="widget-body">
							<p class="follow-me-icons">
								<a href=""><i class="fa fa-twitter fa-2"></i></a>
								<a href=""><i class="fa fa-dribbble fa-2"></i></a>
								<a href=""><i class="fa fa-github fa-2"></i></a>
								<a href=""><i class="fa fa-facebook fa-2"></i></a>
							</p>	
						</div>
					</div>
 -->
					<div class="col-md-6 widget">
						<h3 class="widget-title">Evenliu</h3>
						<div class="widget-body">
							<p>如有建议或改善的意见，请联系：evenljj@163.com</p>
						<a  href="#top">回到顶部</a>
						</div>
					</div>

				</div> <!-- /row of widgets -->
			</div>
		</div>

		<div class="footer2">
			<div class="container">
				<div class="row">
					
					<div class="col-md-6 widget">
						<div class="widget-body">
							<p class="simplenav">
							</p>
						</div>
					</div>

					<div class="col-md-6 widget">
						<div class="widget-body">
							<p class="text-right">
								Copyright &copy; 2016  Evenliu
							</p>
						</div>
					</div>

				</div> <!-- /row of widgets -->
			</div>
		</div>

	</footer>	
</body>
<script src="/seckill/theme/js/jquery-2.1.4.min.js"></script>
<script src="/seckill/theme/js/bootstrap.min.js"></script>
<script src="/seckill/theme/js/jquery.shCircleLoader.js"></script>
<script src="/seckill/theme/js/sco.tooltip.js"></script>
<script src="/seckill/theme/js/colpick.js"></script>
<script src="/seckill/theme/js/jquery.danmu.js"></script>
<script src="/seckill/theme/js/main.js"></script>
<!--<script src="../dist/js/danmuplayer.min.js"></script>-->
<script>
  $("#danmup").DanmuPlayer({
    src:"/seckill/memory.mp4",
    height: "480px", //区域的高度
    width: "800px" //区域的宽度
    ,urlToGetDanmu:"getAllDanmus"
    ,urlToPostDanmu:"saveDanmu"
  });

  $("#danmup .danmu-div").danmu("addDanmu",[
    { "text":"这是滚动弹幕" ,color:"white",size:1,position:0,time:2}
    ,{ "text":"我是帽子绿" ,color:"green",size:1,position:0,time:3}
    ,{ "text":"哈哈哈啊哈" ,color:"black",size:1,position:0,time:10}
    ,{ "text":"这是顶部弹幕" ,color:"yellow" ,size:1,position:1,time:3}
    ,{ "text":"这是底部弹幕" , color:"red" ,size:1,position:2,time:9}
    ,{ "text":"大家好，我是EvenLiu" ,color:"orange",size:1,position:1,time:3}
  ]);
  
  $(function () {
		$("#audio_btn").click(function () {
			var music = document.getElementById("music");
			if (music.paused) {
				music.play();
				$("#music_btn").attr("src", "/seckill/theme/img/play.png");
			} else {
				music.pause();
				$("#music_btn").attr("src", "/seckill/theme/img/pause.png");
			}
		});
		a = $("span [class='swiper-pagination-bullet swiper-pagination-bullet-active']").index();
		//alert(a);
	});
</script>
</html>