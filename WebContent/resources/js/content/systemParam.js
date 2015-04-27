define(function(require) {
	var Site = require('../comp/init.js');

	var systemParam_form = $('#systemParam_form');
	$('.submit_form').click(function() {
        systemParam_form.submit();
	})
	if (systemParam_form.length) {
        systemParam_form.validate({
			errorClass : 'error-tip',
			errorElement : 'b',
			submitHandler : function() {
                systemParam_form.ajaxSubmit({
					success : function(res) {
						alert(res.message);
                        window.location.href = "/backstage/sysParamManage";
					}
				});
			},

			rules : {

                param_name: {
                    required: true
                },
                param_value: {
                    required: true
                }
            },
			messages : {
                param_name : {
					required : '请输入参数名称'
				},
                param_value : {
					required : '请输入参数值'
				}

			},
			onkeyup : false

		});

	}

	
});