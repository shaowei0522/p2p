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

//注册协议确认
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



//验证手机号码是否正确，是否注册
$(function () {
	$("#phone").blur(function () {
		var val = $(this).val();
		if (!/^1[1-9]\d{9}$/.test(val)) {
			//	正则校验不通过
			showError("phone", "手机号码格式不正确！");
		} else {
			$.ajax({
				url:"/004-p2p-web/loan/page/verifyPhone",
				type: "get",
				data:'phone='+val,
				success:function (data) {
					if (data.code == 1) {
						showSuccess("phone");
					} else {
						showError("phone","该手机号已经被注册，请输入新的手机号")
					}

				},
				error:function () {
					showError("phone", "系统异常！");
				}
			})
		}
	});
});

//验证密码是否符合要求
$(function () {
	$("#loginPassword").blur(function () {
		if (!/^(([a-zA-Z]+[0-9]+)|([0-9]+[a-zA-Z]+))[a-zA-Z0-9]*/.test($(this).val())) {
			showError("loginPassword", "密码不符合要求");
		} else {
			showSuccess("loginPassword");
		}
	});
});

//获取手机验证码
$(function () {
	$("#messageCodeBtn").click(function () {
	//	要想获取手机的验证码，首先要保证手机号码是合法的，还有密码是正确的
		//获取用户输入的手机号
		var phone = $("#phone").val();
		//	此处再进行判断用户是否满足提交表单的条件
		if (!$("#phoneOk").is(':visible')){
			showError("phoneOk", "请输入正确的手机号");
			return;
		}
		if(!$("#loginPasswordOk").is(':visible')) {
			showError("loginPasswordOk", "请输入正确的密码");
			return;
		}

        var _this=$(this);
        if(!$(this).hasClass("on")) {
//	此处表示手机号与密码符合提交条件，可以获取验证码了
            $.ajax({
                url: "/004-p2p-web/loan/page/getVerificationCode",
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
                    alert("对不起，系统异常，请稍后进行重试！");
                }
            });
        }


	});
});

//点击注册按钮进行表单提交
//	方式一：避重就轻的方法，通过判断input框后面的span是否显示，来决定表单是否可以提交
$(function () {
	$("#btnRegist").click(function () {
		//获取用户输入的手机号以及密码
		var phone = $("#phone").val();
		var loginPassword = $("#loginPassword").val();
		var verificationCode = $("#messageCode").val();
	//	此处再进行判断用户是否满足提交表单的条件
		if ($("#phoneOk").is(':visible') && $("#loginPasswordOk").is(':visible')) {
		//	此处表示手机号与密码符合提交表单的条件，可以注册了
			if (verificationCode == "" || verificationCode == null) {
				showError("messageCode", "请输入验证码");
				return;
			}
			alert(phone + "" + loginPassword);
			$.ajax({
				url:"/004-p2p-web/loan/page/registerSubmit",
				data:{"phone":phone,
						"loginPassword":loginPassword,
						"verificationCode":verificationCode},
				type:"get",
				success:function (data) {
					alert(data.msg);
                    if (data.code == 1) {
                        //注册成功之后应该跳转至实名认证的页面，进行实名认证
                        window.location = "/004-p2p-web/loan/page/realNameVerify";
                    }

				},
				error:function () {
					alert("sorry,system exception!");
				}
			})
		}
	});
});




