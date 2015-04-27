define(function(require) {
	var Site = require('../comp/init.js');
	require('../plugins/jquery.box.js');
	require('../plugins/jquery.combobox.js');
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
	var completeWithdrawals_form = $('#completeWithdrawals_form');


	completeWithdrawals_form.submit(function(){
	
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
		var securityData = getSecurityData();
		if($.trim(securityData) === 'invalid') {
			e.text('支付密码不能为空或少于6位').show();
			clearInterval(this_timer);
			submit_button.html("确认提现").attr('disabled',false);
			return false;
		}else{
			var url = '/userManage/validationPayPassword';
			var data = {'payPassword' : securityData};
			var result = $_GLOBAL.ajax(url, data);
			if(result.code==0){
				//alert(result.message);
				e.text(result.message).show();
				clearInterval(this_timer);
				submit_button.html("确认提现").attr('disabled',false);
				return false;
			}else if(result.cdoe==1){
				return true;
			}
		}
		$('#payPassword').val(securityData);
		return true;
	}
});