define(function(require) {
	require('../plugins/jquery.countdown.js');
	var sms_url = 'http://192.168.12.103:8082';
	// -------------------------------------发送手机验证码-----------------------------------------------
	var countdown;
	$('#countdown').click(function() {
		countdown = $('#countdownEle').countdown({
			autoSize : true,
			message : '{0}秒后重新获取验证码',
			show : function() {
				$('#countdown').hide();
				$('#countdownEle').show();
			},
			close : function() {
				$('#countdownEle').hide();
				$('#countdown').show();
			}
		});
		sendMobile(true);
	});
	
	function sendMobile(flag) {
		$.ajax({
			url : sms_url + '/anon/sendPhoneCode.htm',
			dataType : 'json',
			data : {
				mobile : function() {
					return $('input[name=mobile]').val();
				},
				smsBizType : function() {
					return $('input[name=smsBizType]').val();
				}
			},
			success : function(res) {
				if (flag) {
					if (res.code == 1) {
					} else {
						alert(res.message);
						if (countdown)
							countdown.close();
					}
				}
			},
			error : function() {
				alert('获取验证码失败');
				if (countdown)
					countdown.close();
			}
		});
	}
	
	
	return {
		required : true,
		customRemote : {
			url : sms_url+'/anon/checkPhoneCode.htm',
			data : {
				mobile : function() {
					return $('input[name=mobile]').val();
				},
				smsBizType : function() {
					return $('input[name=smsBizType]').val();
				},
				code : function() {
					return $('input[name=code]').val();
				}
			},
			customError : function(element, res) {
				return res.message;
			}
		}
	};
	
});