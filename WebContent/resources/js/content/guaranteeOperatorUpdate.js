define(function(require) {
	var Site = require('../comp/init.js');
	
	var newImgCod = $('.newImgCod');
	newImgCod.click(function() {
		$('#newImgCod').attr('src','/anon/getImgCode?dateTag=' + new Date().getTime());
	});

	var openOperator_form = $('#guaranteeUpdateOperator_form');
	
	$('#formSubmit').click(function() {
		openOperator_form.submit();
	})
	
	if (openOperator_form.length) {
		openOperator_form.validate({
			
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
				/*userName : {
					required : true,
					customRemote : {
						url : '/anon/checkUserName?dateTag=' + new Date().getTime(),
						customError : function(element, res) {
							return res.message;
						}
					}
				},*/
				payPassword : {
					required : false,
					minlength: 6,
					mustNotInclude: ' ',
					notAllNum: true,
					notAllSame: true,
					noZh:true
				},
				payPassword2 : {
					required : false,
					equalTo:'#payPassword'
				},
				operatorType : {
					required : true
				},
				remark : {
					required : true
				},
				imgCode : {
					required : true,
					customRemote : {
						url : '/anon/checkImgCode?dateTag=' + new Date().getTime(),
						customError : function(element, res) {
							return res.message;
						}
					}
				}

			},
			messages : {
				/*userName : {
					required : '请输入用户名'
				},*/
				payPassword : {
					required: '请填输入审核密码',
					range: '审核密码为6-20位',
					mustNotInclude: '审核密码不允许包含空格',
					notAllNum: '审核密码不能全为数字',
					notAllSame: '不能使用完全相同的数字、字母或符号',
					noZh:'不允许中文'
				},
				payPassword2 : {
					required: '请再次确认审核密码',
					equalTo: '两次输入的审核密码不一致，请重新输入'
				},
				operatorType : {
					required: '请选择操作员类型'
				},
				remark : {
					required: '请填写备注'
				},
				imgCode : {
					required : '请输入验证码'
				}

			},
			submitHandler:function(){
				openOperator_form.ajaxSubmit({
		    		success:function(res){
		    			alert(res.message);
		    			window.location.href = window.location.href;
		    		}
		    	});
			},
			onkeyup : false

		});
	}
	
	
	$('[name=operatorType]').click(function() {
		if(1 == $(this).val()){
			$('#payPasswordDIV').hide();
			$('#payPasswordDIV2').hide();
		}else{
			$('#payPasswordDIV').show();
			$('#payPasswordDIV2').show();
		}
	})
});