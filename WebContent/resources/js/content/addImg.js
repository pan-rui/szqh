define(function(require) {
	var Site = require('../comp/init.js');
	require('../content/chineseAmountExchange.js');
	require('../Y-all/Y-script/Y-imgplayer.js');
	require('../content/fileUpload.js');
    // 
	var img_form = $('#img_form');
	$('.submit_form').click(function() {
		img_form.submit();
	})
	if (img_form.length) {
		img_form.validate({
			errorClass : 'error-tip',
			errorElement : 'b',
			submitHandler : function() {
				img_form.ajaxSubmit({
					success : function(res) {
						alert(res.message);
                        window.location.href = "/backstage/pageQueryLoanDemand?module=OVER";
					}
				});
			},

			rules : {

                param_name: {
                    required: true
                },
                param_id: {
                    required: true
                }
            },
			messages : {
                param_name : {
					required : '请输入项目名称'
				},
				param_id : {
					required : '请输入项目id'
				}

			},
			onkeyup : false


		});

	}
});