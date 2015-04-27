define(function(require) {
	var Site = require('../comp/init.js');
	require('../plugins/jquery.box.js');
	require('../plugins/jquery.combobox.js');
	require('../plugins/jquery.window.js');
	require('../Y-all/Y-script/Y-window.js');
	require('../content/securityPassword.js')(180,24);
	
	var password_form_one = $('#password_form_one');
	var password_form_two = $('#password_form_two');
	/* validate */
	if (password_form_one.length) {
		password_form_one.validate({
			errorClass : 'error-tip',
			errorElement : 'b',
			submitHandler : function() {
				if(''!=$('#checkHaveSetOne').val()){
					if(""== $.trim($('#passwordOne').val())){
						$("#passwordEone").css("display","block");
						$("#passwordEone").text("请输入旧密码");
						return false;
					}else{
						var url = '/guaranteeCenter/authPasswordCheck';
						var data = {'pwd' : $('#passwordOne').val(),'type' : "lvOne"};
						var result = $_GLOBAL.ajax(url, data);
						if(result.code==0){
							$("#passwordEone").css("display","block");
							$("#passwordEone").text(result.message);
							return false;
						}
					}
				}
				
				password_form_one.ajaxSubmit({
					success : function(res) {
						alert(res.message);
						document.location.href = "/guaranteeCenter/anthPasswordPage";
					}
				});
			},
			rules : {
				newPassword : {
					required : true,
					minlength : 6,
					mustNotInclude : ' ',
					notAllNum : true,
					notAllSame : true,
					noZh : true
				},
				newPasswordTo : {
					required : true,
					equalTo : '#newPassword'
				}
			},
			messages : {
				password : {
					required : '请填入当前密码'
				},
				newPassword : {
					required : '请填入新密码',
					range : '新密码为6-20位',
					mustNotInclude : '新密码不允许包含空格',
					notAllNum : '新密码不能全为数字',
					notAllSame : '不能使用完全相同的数字、字母或符号',
					noZh : '不允许中文'
				},
				newPasswordTo : {
					required : '请再次确认新密码',
					equalTo : '两次输入的新密码不一致，请重新输入'
				}
			},
			onkeyup : false
		});

	}
	if (password_form_two.length) {
		password_form_two.validate({
			errorClass : 'error-tip',
			errorElement : 'b',
			submitHandler : function() {
				if(''!=$('#checkHaveSetTwo').val()){
					if(""== $.trim($('#passwordTwo').val())){
						$("#passwordEtwo").css("display","block");
						$("#passwordEtwo").text("请输入旧密码");
						return false;
					}else{
						var url = '/guaranteeCenter/authPasswordCheck';
						var data = {'pwd' : $('#passwordTwo').val(),'type' : "lvTwo"};
						var result = $_GLOBAL.ajax(url, data);
						if(result.code==0){
							$("#passwordEtwo").css("display","block");
							$("#passwordEtwo").text(result.message);
							return false;
						}
					}
				}
				password_form_two.ajaxSubmit({
					success : function(res) {
						alert(res.message);
						document.location.href = "/guaranteeCenter/anthPasswordPage";
					}
				});
			},
			rules : {
				newPassword2 : {
					required : true,
					minlength : 6,
					mustNotInclude : ' ',
					notAllNum : true,
					notAllSame : true,
					noZh : true
				},
				newPasswordTo2 : {
					required : true,
					equalTo : '#newPassword2'
				}
			},
			messages : {
				password : {
					required : '请填入当前密码'
				},
				newPassword2 : {
					required : '请填入新密码',
					range : '新密码为6-20位',
					mustNotInclude : '新密码不允许包含空格',
					notAllNum : '新密码不能全为数字',
					notAllSame : '不能使用完全相同的数字、字母或符号',
					noZh : '不允许中文'
				},
				newPasswordTo2 : {
					required : '请再次确认新密码',
					equalTo : '两次输入的新密码不一致，请重新输入'
				}
			},
			onkeyup : false
		});

	}
	
	$("#modifyTwo").click(function(){
		$("#authPwdLeveTwoContainer").toggle();
	});
	$("#modifyOne").click(function(){
		$("#authPwdLeveOneContainer").toggle();
	});
	$("#pwd2btn").click(function(){
		if(""== $.trim($('#passwordTwo').val())){
			$("#passwordEone").text("请输入旧密码");
		}
	});
	$("#pwd1btn").click(function(){
		if(""== $.trim($('#passwordOne').val())){
			$("#passwordEtwo").text("请输入旧密码");
		}
	});
	
	$("#forgotLvOne").click(function(){
		if(window.confirm("请确认是否用支付密码找回你的审核密码")){
			$("#resetObject").val("lvOne");
			$('body').Y('Window', {
				content : '#payPasswordCheckContainer'
			});
		}
	});
	$("#forgotLvTwo").click(function(){
		if(window.confirm("请确认是否用支付密码找回你的审核密码")){
			$("#resetObject").val("lvTwo");
			$('body').Y('Window', {
				content : '#payPasswordCheckContainer'
			});
		}
	});
	$("#payPassword").focus(function(){
		var e = $('#pay-password-messge');
		e.empty();
	});
	$('#confirm_pay').click(function (){
		if(checkPayPassword()){
			var url;
			if("lvOne"==$("#resetObject").val()){
				url = '/guaranteeCenter/findAuthPwd/lvOne';
			}else{
				url = '/guaranteeCenter/findAuthPwd/lvTwo';
			}
			var data ={};
			var result = $_GLOBAL.ajax(url, data);
			alert(result.message);
			document.location.href = "/guaranteeCenter/anthPasswordPage";
		}
	});
	
	function checkPayPassword(){
	 	var status = false;
		var e = $('#pay-password-messge');
		e.empty();
	    var securityData = getSecurityData();
	    if( $.trim(securityData) === 'invalid'){
	    	e.append('支付密码不能为空或少于6位');
	    	return false;
	    }
	    $('#payPassword').val(securityData)
		var url = '/userManage/validationPayPassword';
		var data = {'payPassword' : securityData};
		var result = $_GLOBAL.ajax(url, data);
		if('1' == result.code){
			status = true;
		}else{
			e.append(result.message);
		}
		return status;
	}
	$("[name=password]").focus(function(){
		var e1 = $('#passwordEone');
		var e2 = $('#passwordEtwo');
		e1.empty();
		e2.empty();
	});
});