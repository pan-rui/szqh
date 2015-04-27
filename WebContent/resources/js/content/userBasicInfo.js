define(function(require) {
	var Site = require('../comp/init.js');
	require('../plugins/jquery.box.js');
	require('../plugins/jquery.combobox.js');
	require('../Y-all/Y-script/Y-selectarea.js');

	var updateUserBasicInfo_form = $('#updateUserBasicInfo_form');
	if (updateUserBasicInfo_form.length) {
		updateUserBasicInfo_form.validate({
			errorClass : 'error-tip',
			errorElement : 'b',
			submitHandler : function() {
				updateUserBasicInfo_form.ajaxSubmit({
					success : function(res) {
						alert(res.message);
						return;
					}
				});
			},
			errorPlacement : function(error, element) {
				if (element.attr('name') == 'payPassword') {
					element.next().after(error);
				} else {
					element.after(error);
				}

			},
			rules : {
				mail : {
					required : true,
					customRemote : {
						url : '/anon/checkEmail?dateTag=' + new Date().getTime(),
						data : {
							email : function() {
								return $('input[name=mail]').val();
							}
						},
						customError : function(element, res) {
							return res.message;
						}
					}
				},
				mobile : {
					required : true,
					customRemote : {
						url : '/anon/checkMobile?dateTag=' + new Date().getTime(),
						customError : function(element, res) {
							return res.message;
						}
					}
				}

			},
			messages : {
				mail : {
					required : '请输入联系邮箱'
				},
				mobile : {
					required : '请输入联系电话'
				}

			},
			onkeyup : false
		});
	}
	
	$('[name=p]')[0].onchange=function(){
		$('[name=businessLicenseProvince]').val($('[name=p]').val());
	}
	$('[name=c]')[0].onchange=function(){
		$('[name=businessLicenseCity]').val($('[name=c]').val());
	}
	
});