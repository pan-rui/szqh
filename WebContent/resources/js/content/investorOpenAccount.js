define(function(require) {
	var Site = require('../comp/init.js');
	var Site = require('../Y-all/Y-script/Y-tip.js');
	require('../content/fileUpload.js');
	
	$('input[name=realName]').Y('RareWordTip',{
		showEle: $('b.fn-tip a'),
		align:'right'
	});
	var investorOpenAccount_form = $('#investorOpenAccount_form');
	$("#nextBtn").click(function(){
		investorOpenAccount_form.submit();
	});
	
	if (investorOpenAccount_form.length) {
		investorOpenAccount_form.validate({
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
					isMobile : true,
					customRemote : {
						url : '/anon/checkUserName?dateTag=' + new Date().getTime(),
						customError : function(element, res) {
							return res.message;
						}
					}
				},
				realName : {
					required : true,
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
							checkType : 'investor'
						},
						customError : function(element, res) {
							return res.message;
						}
					}
				}
//				businessPeriod : {
//					required : true
//				}

			},
			messages : {
				userName : {
					required : '请输入用户名',
					isMobile:'请输入正确的手机号码'
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
	if($("#availabelBroker").val() == 'false'){
		alert("该经纪人没有创建投资人权限!");
		$("#operateButton").remove();
	}
	
	$("[name='type']").click(function() {
		if("JG" == $(this).val()){
			alert("暂未开放，敬请期待！");
			window.location.href=window.location.href;
			return;
			$("#realNameText").text("单位名称:");
			$("[name='enterpriseName']").val($("[name='realName']").val());
		}else{
			window.location.href=window.location.href;
		}
	});
	$("[name='realName']").change(function(){
		$("[name='enterpriseName']").val($("[name='realName']").val());
	});
});