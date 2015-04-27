define(function(require) {
	var Site = require('../comp/init.js');
	var form = $('#backstageLogin_form');
	require('../content/securityPassword.js')(180,25);
	
	$('.backstageLogin').click(function() {
		if(validateForm()){
			form.submit();
		}
		return false;
	})
	
	/* validate */
	function validateForm(){
		var securityData = getSecurityData();
		var user = $('input[name=userName]');
		if ($.trim(user.val()) === '' || securityData === 'invalid'){
			$('.err').eq(0).html('请填写账户或密码，密码不小于6位!');
			$('.err').show();
			return false;
		}
		$('#password').val(securityData);
		return true;
	}
	
	if (form.length) {
		form.validate({
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
					required : true
				},
				logPassword : {
					required : true
				}

			},
			messages : {
				userName : {
					required : '请输入用户名'
				},
				logPassword : {
					required : '请输入登录密码'
				}
			},
			onkeyup : false
		});
	}
});