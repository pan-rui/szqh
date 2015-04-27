define(function(require) {
	var Site = require('../comp/init.js');
	require('../plugins/jquery.box.js');
	require('../plugins/jquery.combobox.js');

	var addlogPasswordAndPayPassword_form = $('#addlogPasswordAndPayPassword_form');
	if (addlogPasswordAndPayPassword_form.length) {
		addlogPasswordAndPayPassword_form.validate({
			errorClass : 'error-tip',
			errorElement : 'b',
			errorPlacement : function(error, element) {
				if (element.attr('name') == 'businessPeriod') {
					element.parent().next().after(error);
				} else {
					element.after(error);
				}
			},
			rules : {
				logPassword : {
					required: true,
					minlength: 6,
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
					minlength: 6,
					mustNotInclude: ' ',
					notAllNum: true,
					notAllSame: true,
					noZh:true
				},
				payPasswordTo: {
					required : true,
					equalTo:'#payPassword'
				}
				
			},
			messages : {
				logPassword : {
					required: '请填输入登录密码',
					range: '登录密码为6-20位',
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
					required: '请填输入支付密码',
					range: '登录密码为6-20位',
					mustNotInclude: '登录密码不允许包含空格',
					notAllNum: '登录密码不能全为数字',
					notAllSame: '不能使用完全相同的数字、字母或符号',
					noZh:'不允许中文'
				},
				payPasswordTo: {
					required: '请再次确认支付密码',
					equalTo: '两次输入的登录密码不一致，请重新输入'
				}
			},
			onkeyup : false

		});

	}
	
});