//获取像素比
var pixelRatio = 1 / window.devicePixelRatio;
//通过js动态设置视口(viewport)
document.write('<meta name="viewport" content="width=device-width,initial-scale=' + pixelRatio + ',minimum-scale=' + pixelRatio + ',maximum-scale=' + pixelRatio + '">');
//条件：尺寸越来，则字体大小越大，反之一样

//获取html的节点
var html = document.getElementsByTagName('html')[0];
//获取屏幕的宽度
var pageWidth = html.getBoundingClientRect().width;
//  屏幕宽度 / 数值 =结果
html.style.fontSize = pageWidth / 16 + "px";


/*! ajax请求错误拦截*/
(function(a,b){b(function(){b.ajaxSetup({complete:function(d,c){if(d.status===500){alert("系统错误！")}else{if(d.status===302||c==="parsererror"){alert("离线过久，请重新登录！");if(window.top!=window.self){window.top.location.href="login"}else{location.href="login"}}else{if(d.status===401){alert("暂无访问权限！")}}}}})})})(this,jQuery);
