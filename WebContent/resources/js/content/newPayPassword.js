define(function(require) {
	var Site = require('../comp/init.js');
	require('../plugins/jquery.box.js');
	require('../plugins/jquery.combobox.js');
	require('../comp/security.js');
	var password_form = $('#password_form');
	function _submitHandler(){
		if(!pwd){
			setTimeout(_submitHandler,10);
		}else{
			var _pwd = $('[name=payPassword]').val();
			var _newPwd = $('[name=newPayPassword]').val();
			$('[name=payPassword]').val(pwd);
			$('[name=newPayPassword]').val(rsaPwd);
			password_form[0].submit();
			$('[name=payPassword]').val(_pwd);
			$('[name=newPayPassword]').val(_newPwd);
		}
	}
	if (password_form.length) {
		password_form.validate({
			errorClass : 'error-tip',
			errorElement : 'b',
			submitHandler : function(){
				_submitHandler();
			},
			errorPlacement : function(error, element) {
				if (element.attr('name') == 'businessPeriod') {
					element.parent().next().after(error);
				} else {
					element.after(error);
				}
			},
			rules : {
				payPassword : {
					required: true,
					minlength: 6,
					mustNotInclude: ' ',
					notAllNum: true,
					notAllSame: true,
					noZh:true
				},
				newPayPassword : {
					required : true,
					equalTo:'#payPassword'
				}
			},
			messages : {
				payPassword : {
					required: '请填入支付密码',
					range: '支付密码为6-20位',
					mustNotInclude: '支付密码不允许包含空格',
					notAllNum: '支付密码不能全为数字',
					notAllSame: '不能使用完全相同的数字、字母或符号',
					noZh:'不允许中文'
				},
				newPayPassword : {
					required: '请再次确认支付密码',
					equalTo: '两次输入的支付密码不一致，请重新输入'
				}
			},
			onkeyup : false

		});

	}
	
	var modulus = "";
	var exponent = "";
	var pwd,rsaPwd;
	function setHiddenPwd(){
		var loginPwd = $('[name=payPassword]').val();
		var newLoginPwd = $('[name=newPayPassword]').val();
		var key = RSAUtils.getKeyPair(exponent, '', modulus);
		pwd =  RSAUtils.encryptedString(key, loginPwd);
		rsaPwd = RSAUtils.encryptedString(key, newLoginPwd);
	}
	$('[name=next]').click(function(){
		pwd = null;
		rsaPayPwd = null;
		$.ajax({
			url : '/login/keyPair',
			type : 'post',
			dataType : 'json',
			success : function(res){
				if(res.code==1){
					modulus = res.modulus;
					exponent = res.exponent;
					setHiddenPwd();
				}
			}
		});
	});
	
});