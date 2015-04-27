define(function(require) {
	var Site = require('../comp/init.js');
	require('../plugins/jquery.box.js');
	require('../plugins/jquery.combobox.js');
	require('../Y-all/Y-script/Y-countdown.js');
	require('../comp/security.js');

	var activationUser_form = $('#activationUser_form');
	function _submitHandler(){
		if(!pwd){
			setTimeout(_submitHandler,10);
		}else{
			var _pwd = $('[name=logPassword]').val();
			var _rsaPayPwd = $('[name=payPassword]').val();
			$('[name=logPassword]').val(pwd);
			$('[name=payPassword]').val(rsaPayPwd);
			$('[name=logPasswordTO]').val(pwd);
			$('[name=payPasswordTo]').val(rsaPayPwd);
			activationUser_form[0].submit();
			$('[name=logPassword]').val(_pwd);
			$('[name=payPassword]').val(_rsaPayPwd);
			$('[name=logPasswordTO]').val(_pwd);
			$('[name=payPasswordTo]').val(_rsaPayPwd);
		}
	}
	if (activationUser_form.length) {
		activationUser_form.validate({
			errorClass : 'error-tip',
			errorElement : 'b',
			errorPlacement : function(error, element) {
				if (element.attr('name') == 'businessPeriod') {
					element.parent().next().after(error);
				}else if (element.attr('name') == 'code') {
					element.next().after(error);
				} else {
					element.after(error);
				}
			},
			submitHandler:function(){
				_submitHandler();
			},
			rules : {
				logPassword : {
					required: true,
					rangelength: [6,20],
					mustNotInclude: ' ',
					notAllNum: true,
					notAllSame: true,
					noZh:true
				},
				logPasswordTO : {
					required : true,
					equalTo:'#logPassword'
				},
				payPassword: {
					required: true,
					rangelength: [6,20],
					mustNotInclude: ' ',
					notAllNum: true,
					notAllSame: true,
					noZh:true
				},
				payPasswordTo: {
					required : true,
					equalTo:'#payPassword'
				},mobile : {
					required : true,
					isMobile : true,
					customRemote : {
						url : '/anon/checkMobile?dateTag=' + new Date().getTime(),
						customError : function(element, res) {
							return res.message;
						}
					}
				},
				code : {
					//required : true,
					customRemote : {
						url : '/anon/checkSmsCode',
						data : {
							mobile : function() {
								return $('#bundPhone').val();
							},
							business : function() {
								return $('#smsBizType').val();
							},
							code : function() {
								return $('#code').val();
							}
						},
						customError : function(element, res) {
							return res.message;
						}
					}
				}
			},
			messages : {
				logPassword : {
					required: '请输入登录密码',
					rangelength: '登录密码为6-20位',
					mustNotInclude: '登录密码不允许包含空格',
					notAllNum: '登录密码不能全为数字',
					notAllSame: '不能使用完全相同的数字、字母或符号',
					noZh:'不允许中文'
				},
				logPasswordTO : {
					required: '请再次确认登录密码',
					equalTo: '两次输入的登录密码不一致，请重新输入'
				},
				payPassword: {
					required: '请输入支付密码',
					rangelength: '支付密码为6-20位',
					mustNotInclude: '支付密码不允许包含空格',
					notAllNum: '支付密码不能全为数字',
					notAllSame: '不能使用完全相同的数字、字母或符号',
					noZh:'不允许中文'
				},
				payPasswordTo: {
					required: '请再次确认支付密码',
					equalTo: '两次输入的支付密码不一致，请重新输入'
				},
				mobile : {
					required : '请输入手机号',
					isMobile : '请输入正确的手机号'
				},
				code : {
					required : '请输入验证码'
				}
			},
			onkeyup : false

		});

	}
	var modulus = "";
	var exponent = "";
	var pwd,rsaPayPwd;
	function setHiddenPwd(){
		var loginPwd = $('[name=logPassword]').val();
		var payPwd = $('[name=payPassword]').val();
		var key = RSAUtils.getKeyPair(exponent, '', modulus);
		pwd =  RSAUtils.encryptedString(key, loginPwd);
		rsaPayPwd = RSAUtils.encryptedString(key, payPwd);
	}
	$('[name=next]').click(function(){
		pwd = null;
		rsaPayPwd = null;
		$.ajax({
			url : '/login/keyPair',
			type : 'post',
			dataType : 'json',
			success : function(res){
				if(res.code==1){
					modulus = res.modulus;
					exponent = res.exponent;
					setHiddenPwd();
				}
			}
		});
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
		}
	});

	function sendMobile(business, mobile, conutdown) {
		$.ajax({
			url : '/anon/sendSmsCode',
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