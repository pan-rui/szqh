define(function(require) {
	var Site = require('../comp/init.js');
	var Site = require('../Y-all/Y-script/Y-tip.js');
	require('../content/fileUpload.js');
	
	$('input[name=realName]').Y('RareWordTip',{
		showEle: $('b.fn-tip a')
	});
	
	var brokerOpenAccount_form = $('#brokerOpenAccount_form');
	if (brokerOpenAccount_form.length) {
		brokerOpenAccount_form.validate({
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
				userName : {
					required : true,
					NumandLetter_ : true,
					rangelength:[6,20],
					customRemote : {
						url : '/anon/checkUserName?dateTag=' + new Date().getTime(),
						customError : function(element, res) {
							return res.message;
						}
					}
				},
				realName : {
					required : true
				},
//				certNo : {
//					required : true,
//					customRemote : {
//						url : '/anon/checkCertNo?dateTag='+ new Date().getTime(),
//						customError : function(element, res) {
//							return res.message;
//						}
//					}
//				},
				
				mail : {
					required : true,
					customRemote : {
						url : '/anon/checkEmailOrMobile?dateTag='+ new Date().getTime(),
						data : {
							email : function() {
								return $('input[name=mail]').val();
							},
							checkType : 'broker'
						},
						customError : function(element, res) {
							return res.message;
						}
					}
				},
//				businessPeriod : {
//					required : true
//				}

			},
			messages : {
				userName : {
					required : '请输入用户名',
					NumandLetter_ : '只能为数字、字母、下划线',
					rangelength :'请输入6-20位字符',
				},
				realName : {
					required : '请输入真实姓名'
				},
				mail : {
					required : '请输入常用电子邮箱'
				},
//				certNo : {
//					required : '请输入身份证号码'
//				},
				businessPeriod : {
					required : '请输入身份证到期时间'
				}

			},
			onkeyup : false

		});
	}

	$('#isForever').click(function() {
		var input = $('.fn-isdate');
		if ($(this).attr('checked')) {
			input.val('').attr('disabled', true).rules('remove', 'required');
		} else {
			input.removeAttr('disabled').rules('add', 'required');
		}
	});

	if ($('#isForever').attr('checked')) {
		var input = $('.fn-isdate');
		input.val('').attr('disabled', true).rules('remove', 'required');
	}
});