define(function(require) {
	require('../comp/init.js');
	require('../Y-all/Y-script/Y-tip.js');
	require('../Y-all/Y-script/Y-countdown.js');
	
	
	var password_form = $('#password_form');
	if (password_form.length) {
		password_form.validate({
			
			errorClass : 'error-tip',
			errorElement : 'b',
			errorPlacement : function(error, element) {
				if (element.attr('name') == 'imgCode'||element.attr('name') == 'license') {
					element.next().next().after(error);
				} else {
					element.after(error);
				}
			},
			rules : {
				newPassword : {
					required: true,
					rangelength: [6,20],
					mustNotInclude: ' ',
					notAllNum: true,
					notAllSame: true,
					noZh:true
				},
				newPasswordTo : {
					required : true,
					equalTo:'#newPassword'
				}
			},
			messages : {
				newPassword : {
					required: '请输入登录密码',
					rangelength: '登录密码为6-20位',
					mustNotInclude: '登录密码不允许包含空格',
					notAllNum: '登录密码不能全为数字',
					notAllSame: '不能使用完全相同的数字、字母或符号',
					noZh:'不允许中文'
				},
				newPasswordTo : {
					required: '请再次确认登录密码',
					equalTo: '两次输入的登录密码不一致，请重新输入'
				}		

			},
			onkeyup : false

		});
	}

	// -------------------------------------发送手机验证码-----------------------------------------------
	$('#getCode').click(function() {
		var business = $("#smsBizType").val();
		var mobile = $("#bundPhone").val();
		var countdown = Y.getCmp('getCode');
		if (!$("#bundPhone").valid()) {
			countdown.close(0);
			return;
		} else {
			sendMobile(business, mobile, countdown);
			$('.getCodeWrap').Y('countdown',{
				message:"{0}秒后重新发送"
			});
		}
	});

	function sendMobile(business, mobile, conutdown) {
		$.ajax({
			url : '/PasswordManage/reGetSms',
			dataType : 'json',
			data : {
				mobile : mobile,
				business : business
			},
			cache : false,
			success : function(res) {
				if (res.code == 1) {
					
				} else {
					alert(res.message);
					if (countdown) {
						countdown.close();
					}
				}
			},
			error : function() {
				alert('获取动态验证码失败');
				if (countdown) {
					countdown.close();
				}
			}
		});
	}
	
	
	
	

});