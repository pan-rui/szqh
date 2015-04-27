define(function(require) {
	var Site = require('../comp/init.js');
	var Site = require('../Y-all/Y-script/Y-tip.js');
	require('../Y-all/Y-script/Y-countdown.js');

	var pre_regist_form = $('#pre_regist_form');

	$('.submit').on('click', function() {
		pre_regist_form.submit();
	})

	pre_regist_form.validate({
		errorClass: 'tips',
		errorElement: 'div',
		highlight: false,
		unhighlight: false,
		submitHandler: function(form) {
			pre_regist_form[0].submit();
		},
		errorPlacement: function(error, elem) {
			elem.parents('.control').after(error);
		},
		rules: {
			userName: {
				required : true,
				rangelength:[6,20],
				NumandLetter_ : true,
				customRemote : {
					url : '/anon/checkUserName?dateTag=' + new Date().getTime(),
					customError : function(element, res) {
						return res.message;
					}
				}
			},
			password: {
				required: true,
				rangelength: [6, 20],
				mustNotInclude: ' ',
				notAllNum: true,
				notAllSame: true,
				noZh: true
			},
			logPasswordTO: {
				required: true,
				equalTo: '#logPassword'
			},
			mobile: {
				required: true,
				isMobile: true,
				customRemote: {
					url: '/anon/checkMobile?dateTag=' + new Date().getTime(),
					customError: function(element, res) {
						return res.message;
					}
				}
			},
			code: {
				required: true,
				customRemote: {
					url: '/anon/checkSmsCode',
					data: {
						mobile: function() {
							return $('#bundPhone').val();
						},
						business: function() {
							return $('#smsBizType').val();
						},
						code: function() {
							return $('#code').val();
						}
					},
					customError: function(element, res) {
						return res.message;
					}
				}
			}
		},
		messages: {
			userName: {
				required: '请输入用户名',
				rangelength: '请输入6-20位字符',
				NumandLetter_: '只能为数字、字母、下划线'
			},
			password: {
				required: '请输入登录密码',
				rangelength: '登录密码为6-20位',
				mustNotInclude: '登录密码不允许包含空格',
				notAllNum: '登录密码不能全为数字',
				notAllSame: '不能使用完全相同的数字、字母或符号',
				noZh: '不允许中文'
			},
			logPasswordTO: {
				required: '请再次确认登录密码',
				equalTo: '两次输入的登录密码不一致，请重新输入'
			},
			mobile: {
				required: '请输入手机号',
				isMobile: '请输入正确的手机号'
			},
			code: {
				required: '请输入验证码'
			}
		},
		onkeyup: false

	});


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
			$('.getCodeWrap').Y('countdown', {
				message: "{0}秒后重新发送"
			});
		}
	});

	function sendMobile(business, mobile, conutdown) {
		$.ajax({
			url: '/anon/sendSmsCode',
			dataType: 'json',
			data: {
				mobile: mobile,
				business: business
			},
			cache: false,
			success: function(res) {
				if (res.code == 0) {
					if(res.message.length>0){
						alert(res.message);
					}else{
						alert('获取验证码失败');
					}
					
					if (countdown) {
						countdown.close();
					}
				}
			},
			error: function() {
				alert('获取动态验证码失败');
				if (countdown) {
					countdown.close();
				}
			}
		});
	}

});