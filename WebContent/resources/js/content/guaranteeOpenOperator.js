define(function(require) {
	var Site = require('../comp/init.js');
	
	var newImgCod = $('.newImgCod');
	newImgCod.click(function() {
		$('#newImgCod').attr('src','/anon/getImgCode?dateTag=' + new Date().getTime());
	});

	var openOperator_form = $('#guaranteeOpenOperator_form');
	
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
				userName : {
					required : true,
					customRemote : {
						url : '/anon/checkUserName?dateTag=' + new Date().getTime(),
						customError : function(element, res) {
							return res.message;
						}
					}
				},
				logPassword : {
					required : true,
					minlength: 6,
					mustNotInclude: ' ',
					notAllNum: true,
					notAllSame: true,
					noZh:true
				},
				rePassword : {
					required : true,
					equalTo:'#logPassword'
				},
				operatorType : {
					required : true
				},
				remark : {
					required : true
				},
				
				mobile : {
					required : true,
					minlength: 11
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
				userName : {
					required : '请输入用户名'
				},
				logPassword : {
					required: '请填输入登录密码',
					range: '登录密码为6-20位',
					mustNotInclude: '登录密码不允许包含空格',
					notAllNum: '登录密码不能全为数字',
					notAllSame: '不能使用完全相同的数字、字母或符号',
					noZh:'不允许中文'
				},
				rePassword : {
					required: '请再次确认登录密码',
					equalTo: '两次输入的登录密码不一致，请重新输入'
				},
				mobile:{
					required: '请填输入手机号',
					range: '手机号为11位',
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
		}else{
			$('#payPasswordDIV').show();
		}
	})
});