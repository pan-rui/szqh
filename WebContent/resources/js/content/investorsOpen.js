define(function(require) {
	var Site = require('../comp/init.js');
	var Site = require('../Y-all/Y-script/Y-tip.js');
	
	$('input[name=realName]').Y('RareWordTip',{
		showEle: $('b.fn-tip a')
	});
	
	var newImgCod = $('.newImgCod');
	newImgCod.click(function() {
		$('#newImgCod').attr('src','/anon/getImgCode?dateTag=' + new Date().getTime());
	});
	
	var investorsOpen_form = $('#investorsOpen_form');
	if (investorsOpen_form.length) {
		investorsOpen_form.validate({
			
			errorClass : 'error-tip',
			errorElement : 'b',
			errorPlacement : function(error, element) {
				if (element.attr('name') == 'imgCode'||element.attr('name') == 'license') {
					element.next().next().after(error);
				} else {
					element.after(error);
				}
			},
			rules : {
				userName : {
					required : true,
					rangelength:[6,20],
					NumandLetter_ : true,
					customRemote : {
						url : '/anon/checkUserName?dateTag=' + new Date().getTime(),
						customError : function(element, res) {
							return res.message;
						}
					}
				},
				realName : {
					required : true
				},
//				certNo : {
//					required : true,
//					checkID : true,
//					customRemote : {
//						url : '/anon/checkCertNo?dateTag=' + new Date().getTime(),
//						customError : function(element, res) {
//							return res.message;
//						}
//					}
//				},
//				businessPeriod : {
//					required : true
//				},
//				gender : {
//					required : true
//				},
				mail : {
					required : true,
					customRemote : {
						url : '/anon/checkEmailOrMobile?dateTag=' + new Date().getTime(),
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
				},
				imgCode : {
					required : true,
					customRemote : {
						url : '/anon/checkImgCode?dateTag=' + new Date().getTime(),
						customError : function(element, res) {
							return res.message;
						}
					}
				},
				license : {
					required : true
				}

			},
			messages : {
				accountName : {
					required : '请输入账户名'
				},
				userName : {
					required : '请输入用户名',
					rangelength :'请输入6-20位字符',
					NumandLetter_ : '只能为数字、字母、下划线'
				},
				realName : {
					required : '请输入真实姓名'
				},
//				certNo : {
//					required : '请输入证件号码',
//					checkID : "请输入正确的身份证号"
//				},
//				businessPeriod : {
//					required : '请输入证件到期时间'
//				},
//				gender : {
//					required : '请选择性别'
//				},
				mail : {
					required : '请输入常用电子邮箱'
				},
				imgCode : {
					required : '请输入验证码'
				},
				license : {
					required : '请先阅读协议'
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
	$('.fm-license').find('*').css('position','static');
	$('#btn_license').click(function(){
		$('body').Y('Window',{
			simple: true,
			content: '.fm-license',
			closeEle: '.close,[type=reset]'
		});
	});
	$("#accountTypeGR").click(function() {
		window.location.href = window.location.href;
	});
	$("#accountTypeJG").click(function() {
		alert("暂未开放，敬请期待！");
		return;
		$("[name='type']").val("JG");
		$("#realNameText").text("单位名称:");
		$("#accountTypeGR").css("background","#d6d6d6");
		$("#accountTypeGR").css("color","#787878");
		$(this).css("background","#a60000");
		$(this).css("color","#fff");
	});
	
	$("[name='realName']").change(function(){
		$("[name='enterpriseName']").val($("[name='realName']").val());
	});
});