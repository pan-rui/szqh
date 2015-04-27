define(function(require) {
	var Site = require('../comp/init.js');
	require('../plugins/jquery.box.js');
	require('../plugins/jquery.combobox.js');
	require('../plugins/jquery.window.js');
	require('../Y-all/Y-script/Y-countdown.js');
	require('../content/securityPassword.js')(180,24);
	
	/*if (completeWithdrawals_form.length) {
		completeWithdrawals_form.validate({
			errorClass : 'error-tip',
			errorElement : 'b',
			errorPlacement : function(error, element) {
				error.css('width','auto');
				if (element.attr('name') == 'payPassword') {
					element.next().after(error);
				} else {
					element.after(error);
				}
			},
			rules : {
				payPassword : {
					required: true,
					customRemote : {
						url : '/userManage/validationPayPassword',
						data:{
							payPassword:function(){
								return isEnd && _payPassword;
							}
						},
						customError : function(element, res) {
							return res.message;
						}
					}
				}
			},
			messages : {
				payPassword : {
					required: '请填输入支付密码'
				}
			},
			onkeyup : false,
			onblur:false,
			onfocusout:false,
			onclick:false
		});
	}*/
	var completeDeduction_form = $('#completeDeduction_form');


	completeDeduction_form.submit(function(){
	
		return validateForm();
			
		
		
	});
	/* validate */
	function validateForm(){
		var str = "处理中" ,count = 0;
		var submit_button = $('#submit-a');
		submit_button.html(str).attr('disabled',true);
		var this_timer = setInterval(function(){
			submit_button.html(str + Array(count++%3 + 2).join('.'));
		},500);
		var e = $('#pay-password-messge').hide();
		var e1 = $('#code-messge').hide();
		var securityData = getSecurityData();
		if($.trim(securityData) === 'invalid') {
			e.text('支付密码不能为空或少于6位').show();
			clearInterval(this_timer);
			submit_button.html("确认快捷划入").attr('disabled',false);
			return false;
		}else{
			var url = '/userManage/validationPayPassword';
			var data = {'payPassword' : securityData};
			var result = $_GLOBAL.ajax(url, data);
			if(result.code==0){
				//alert(result.message);
				e.text(result.message).show();
				clearInterval(this_timer);
				submit_button.html("确认快捷划入").attr('disabled',false);
				return false;
			}else if(result.code==1){
				$('#payPassword').val(securityData);
			}
		}
		/*var code = $('input[name=code]').val();
		if($.trim(code) =='' ||isNaN($.trim(code))) {
			e1.text('验证码不能为空且只能是数字');
			return false;
		}else{
			var url1 = '/anon/checkSmsCode';
			var data1 = {
					mobile : $('#bundPhone').val(),
					business :$('#smsBizType').val(),
					code : $('#code').val()
					};
			var result1 = $_GLOBAL.ajax(url1, data1);
			if(result1.code==0){
				//alert(result.message);
				e1.text(result1.message);
				return false;
			}else if(result1.code==1){
				return true;
			}
		}*/
		return true;
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
	
	 $("#code").change(
		    	function(){
					var e1 = $('#code-messge');
					e1.text("").hide();
				}
		    );
});