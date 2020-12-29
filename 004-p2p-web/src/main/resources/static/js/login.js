var referrer = "";//登录后返回页面
referrer = document.referrer;
if (!referrer) {
	try {
		if (window.opener) {                
			// IE下如果跨域则抛出权限异常，Safari和Chrome下window.opener.location没有任何属性              
			referrer = window.opener.location.href;
		}  
	} catch (e) {
	}
}

//按键盘Enter键即可登录
$(document).keyup(function(event){
	if(event.keyCode == 13){
		login();
	}
});


$(function () {
	//验证码
	$("#messageCodeBtn").click(function(){

		//提交数据
		var phone=$.trim($("#phone").val());
		var _this=$(this);
		if(!$(this).hasClass("on")){

			$.ajax({
				type: "get",
				url: "/004-p2p-web/loan/page/getVerificationCode",
				data: {"phone":phone},
				success: function(data){
					if(data.code=="1"){
						alert(data.msg);
						//发送成功后倒计时

						$.leftTime(60,function(d){
							if(d.status){
								_this.addClass("on");
								_this.html((d.s=="00"?"60":d.s)+"秒后重新获取");
							}else{
								_this.removeClass("on");
								_this.html("获取验证码");
							}
						});



					}
					if(data.code=="0"){

						alert(data.msg);
					}

				},
				error:function(){
					alert("系统异常");
				}
			});
		}
	});


	$("#loginId").click(function () {
		//提交数据
		var phone=$.trim($("#phone").val());
		var loginPassword=$.trim($("#loginPassword").val());
		var messageCode=$.trim($("#messageCode").val());

		$.ajax({
			type: "get",
			url: "/004-p2p-web/loan/page/loginSubmit",
			data: {"phone":phone,"loginPassword":loginPassword,"messageCode":messageCode},
			success: function(data){
				if(data.code=="1"){
					window.location.href=$("#redirectURL").val();
				}
				if(data.code=="0"){

					alert(data.msg);
				}

			},
			error:function(){
				alert("系统异常");
			}
		});
	});
});
