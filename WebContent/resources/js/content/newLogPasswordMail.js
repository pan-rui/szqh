define(function(require) {
	var Site = require('../comp/init.js');
	require('../plugins/jquery.box.js');
	require('../plugins/jquery.combobox.js');
	require('../Y-all/Y-script/Y-countdown.js');

	var verify = $('.new_captcha');
	verify.click(function() {
		$('#newcaptcha').attr('src',
				'/anon/getImgCode?dateTag=' + new Date().getTime());
	});
	$("#userName").val("");
	$("#userName").change(function(){
		var _this=$(this).val();
		$.ajax({
			url : '/userManage/getBindEmail',
			type : 'post',
			dataType : 'json',
			data : {
				userName : _this
			},
			success : function(res) {
				if(res.code==1){
					if(res.message!=null){
						$('input[name = mail]').val(res.mail);
						$('#mail_read').html(res.message);
						$("#bandMail").show(1000); 
						$("#sub_button").show(1000);
					}else{
						$('input[name = mail]').val("");
						$('#mail_read').html("**********");
					}
					
				}else{
					$('input[name = mail]').val("");
					$('#mail_read').html("**********");
				//	$('#user_Name').after("<font color='red'>"+res.message+"</font>");
				}
				
			},error:function(e){
				console.log(e);
			}
		})
	});
	
	var password_form = $('#password_form');
	if (password_form.length) {
		password_form.validate({
			errorClass : 'error-tip',
			errorElement : 'b',
			errorPlacement : function(error, element) {
				if (element.attr('name') == 'imgCode') {
					element.next().next().after(error);
				} else {
					element.after(error);
				}
			},
			rules : {
				userName : {
					required : true,
					customRemote : {
						url : '/userManage/validationIsUserName',
						customError : function(element, res) {
							return res.message;
						}
					}
				},
				mail : {
					required : true
				},
				imgCode : {
					required : true,
					customRemote : {
						url : '/anon/checkImgCode',
						customError : function(element, res) {
							return res.message;
						}
					}
				},
				mobile : {
					required : true,
					customRemote : {
						url : '/anon/checkUserMobile',
						data : {
							mobile : function() {
								return $('[name=mobile]').val();
							},
							userName : function() {
								return $("#userName").val();
							}
						},
						customError : function(element, res) {
							return res.message;
						}
					}
				}
			},
			messages : {
				userName : {
					required : '请输入用户名'
				},
				mail : {
					required : '请输入邮箱'
				},
				imgCode : {
					required : '请输入验证码'
				},
				code : {
					required : '请输入手机验证码'
				},
				mobile:{
					required : '请输入手机号'
				}
			},
			onkeyup : false

		});

	}

});