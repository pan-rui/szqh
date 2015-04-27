define(function(require) {
	var Site = require('../comp/init.js');
	require('../plugins/jquery.box.js');
	require('../plugins/jquery.combobox.js');
	require('../content/securityPassword.js')(180,24);
	
	var completeWithdrawals_form = $('#completeTransferBankAccount_form');
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
			submit_button.html("确认转账到卡").attr('disabled',false);
			return false;
		}else{
			var url = '/userManage/validationPayPassword';
			var data = {'payPassword' : securityData};
			var result = $_GLOBAL.ajax(url, data);
			if(result.code==0){
				//alert(result.message);
				e.text(result.message).show();
				clearInterval(this_timer);
				submit_button.html("确认转账到卡").attr('disabled',false);
				return false;
			}else if(result.cdoe==1){
				return true;
			}
		}
		$('#payPassword').val(securityData);
		return true;
	}
});