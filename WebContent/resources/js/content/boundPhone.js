define(function(require) {
	var Site = require('../comp/init.js');
	require('../plugins/jquery.box.js');
	require('../plugins/jquery.combobox.js');
	
	var boundPhone_form = $('#boundPhone_form');
	if (boundPhone_form.length) {
		boundPhone_form.validate({
			errorClass : 'error-tip',
			errorElement : 'b',
			errorPlacement : function(error, element) {
				if (element.attr('name') == 'mobile') {
					element.next().after(error);
				} else {
					element.after(error);
				}
			},
			rules : {
				mobile : {
					required : true,
					isAllNum : true
				},
				code : codeRules
			},
			messages : {
				mobile : {
					required : '请填输入手机号码',
					isAllNum : '手机号码只能全为数字'
				},
				code : {
					required : '请输入验证码',
					isAllNum : '验证码只能全为数字'
				}
			},
			onkeyup : false

		});

	}

});