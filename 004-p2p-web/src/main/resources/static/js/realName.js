
//同意实名认证协议
$(function() {
	$("#agree").click(function(){
		var ischeck = document.getElementById("agree").checked;
		if (ischeck) {
			$("#btnRegist").attr("disabled", false);
			$("#btnRegist").removeClass("fail");
		} else {
			$("#btnRegist").attr("disabled","disabled");
			$("#btnRegist").addClass("fail");
		}
	});
});
//打开注册协议弹层
function alertBox(maskid,bosid){
	$("#"+maskid).show();
	$("#"+bosid).show();
}
//关闭注册协议弹层
function closeBox(maskid,bosid){
	$("#"+maskid).hide();
	$("#"+bosid).hide();
}

//错误提示
function showError(id,msg) {
	$("#"+id+"Ok").hide();
	$("#"+id+"Err").html("<i></i><p>"+msg+"</p>");
	$("#"+id+"Err").show();
	$("#"+id).addClass("input-red");
}
//错误隐藏
function hideError(id) {
	$("#"+id+"Err").hide();
	$("#"+id+"Err").html("");
	$("#"+id).removeClass("input-red");
}
//显示成功
function showSuccess(id) {
	$("#"+id+"Err").hide();
	$("#"+id+"Err").html("");
	$("#"+id+"Ok").show();
	$("#"+id).removeClass("input-red");
}


//进行手机号的正则校验，因为手机号需要接收短信验证码，所以需要保证手机号的合法性
$(function () {
	$("#phone").blur(function () {
		var val = $.trim($(this).val());
		if (val == '' || val == null) {
			showError("phone", "手机号不能为空");
		}else if (!/^1[1-9]\d{9}$/.test(val)) {
//	正则校验不通过
			showError("phone", "手机号码格式不正确！");
		} else {
			showSuccess("phone");
		}
	});

});

//进行姓名的正则校验，只需要保证姓名都是中文即可
$(function () {
	$("#realName").blur(function () {
		var realName=$.trim($("#realName").val());
		if(realName==null||realName==""){
			showError("realName","请输入姓名");
		}else if(!/^[\u4e00-\u9fa5]{0,}$/.test(realName)){
			showError("realName","请输入正确姓名");
		}else {
			showSuccess("realName");
		}
	});
});

//身份证号的正则校验
$(function () {
	$("#idCard").blur(function () {
		var idCard=$.trim($("#idCard").val());
		if(idCard==null||idCard==""){
			showError("idCard","请输入身份证号码");
		}else if(!/(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/.test(idCard)){
			showError("idCard","请输入合法的身份证号码");
		}else {
			showSuccess("idCard");
		}
	});
});

//校验验证码
$(function () {
	$("#messageCode").blur(function () {
		var messageCode=$.trim($(this).val());
		if(messageCode==""||messageCode==null){
			showError("messageCode","请输入验证码");
		}else if(messageCode.length!=6){
			showError("messageCode","请输入6位验证码");
		}else{
			showSuccess("messageCode");
		}
	});
});

//获取验证码
$(function () {
	$("#messageCodeBtn").click(function () {
		//检验手机号码格式是否正确
		$("#phone").blur();
		var html = $("#phoneErr").html();
		var text = $("#phoneErr").text();

		if (html == null || html == "") {
			var phone = $.trim($("#phone").val());

			var _this=$(this);
			if(!$(this).hasClass("on")) {
				$.ajax({
					url: "/004-p2p-web//loan/page/getVerificationCode",
					data: {"phone": phone},
					type: "get",
					success: function (data) {
						if (data.code == 1) {
							alert(data.msg);

							//此处引入了leftTime.js
							//发送成功后倒计时
							$.leftTime(60, function (d) {
								if (d.status) {
									_this.addClass("on");
									_this.html((d.s == "00" ? "60" : d.s) + "秒后重新获取");
								} else {
									_this.removeClass("on");
									_this.html("获取验证码");
								}
							});
						}
					},
					error: function () {
						alert("对不起，系统异常");
					}
				});
			}
		}
	});
});
//点击认证按钮进行个人信息的认证
$(function () {
	$("#btnRegist").click(function () {
		$("#phone").blur();
		$("#idCard").blur();
		$("#messageCode").blur();
		$("#realName").blur();

		//使用jQuery的选择器进行错误信息元素的获取
		var errs=$("div[id$='Err']");
		if (errs.text() == "") {
		//	此处表示没有错误信息，所以可以进行表单的提交
			var phone = $("#phone").val();
			var idCard = $("#idCard").val();
			var messageCode = $("#messageCode").val();
			var realName = $("#realName").val();
			$.ajax({
				url:"/004-p2p-web/loan/page/realNameVerifySubmit",
				data:{"phone":phone,
						"idCard":idCard,
						"messageCode":messageCode,
						"realName":realName},
				type:"get",
				success:function (data) {
					if (data.code == 1) {
						alert(data.msg);
						window.location = "/004-p2p-web/index";
					}
				},
				error:function () {
					alert("对不起，系统异常，请稍后重试");
				}
			})
		}

	});
});
