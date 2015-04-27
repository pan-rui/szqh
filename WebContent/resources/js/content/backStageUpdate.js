define(function(require) {
	var Site = require('../comp/init.js');
	var form = $('#updateUserPassword_form');
	
	$('#updateUserPassword').click(function() {
//			$.ajax({
//				url : '/backstagelogin/updateUserPassword',
//				type : 'post',
//				dataType : 'json',
//				data : {
//					userId : $('#userId').val(),
//					newPassword : $('#newPassword').val()
//				},
//				success : function(res) {
//					alert(res.message);
//					window.location.href="/backstage/backstageIdex";
//				}
//			})
		
			form.submit();
		
		
		
	});

	if (form.length) {
		form.validate({
			errorClass : 'error-tip',
			errorElement : 'b',
			errorPlacement : function(error, element) {
				if (element.attr('name') == 'newPassword' || element.attr('name') == 'oldPassword'||
						element.attr('name') == 'reNewPassword') {
					element.next().after(error);
				} 
			},
			submitHandler : function() {
				form.ajaxSubmit({
					success : function(res) {
						alert(res.message);
						window.location.href="/backstage/backstageIdex";
					}
				});
			},
			rules : {
                oldPassword:{
                    required : true
                },
				newPassword : {
					required : true,
					maxlength : 12,
					minlength : 6
				},
				reNewPassword : {
					required : true,
					equalTo : "#newPassword"
				}

			},
			messages : {
                oldPassword : {
                    required : '请输入旧密码'

                },
				newPassword : {
					required : '请输入新密码',
					maxlength : '请输入小于12位密码',
					minlength : '请输入大于6位密码'
				},
				reNewPassword : {
					required : '请输入确认密码',
						equalTo : '两次密码不一致'
				}
			},
			onkeyup : false
		});
	}
});