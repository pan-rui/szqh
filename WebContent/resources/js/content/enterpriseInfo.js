define(function(require) {
	var Site = require('../comp/init.js');
	require('../plugins/jquery.box.js');
	require('../plugins/jquery.combobox.js');
	require('../content/fileUpload.js');
	require('../Y-all/Y-script/Y-selectarea.js');
	var state = $('[name=authState]').val();
	if(state=='IS' || state=="IN"){
		$('#submit_button').attr('disabled',true);
		$('.fn-g-btn').hide();
		$('.upload-btn').hide();
		$('.sz').hide();
		$('[name=realName]').attr('disabled',true);
		$('[name=certNo]').attr('disabled',true);
		$('[name=businessPeriod]').attr('disabled',true);
		$('[name=cardPeriod]').attr('disabled',true);
		$('[name=contactName]').attr('disabled',true)
	}
	var updateEnterpriseInfo_form = $('#updateEnterpriseInfo_form');
	if (updateEnterpriseInfo_form.length) {
		
		updateEnterpriseInfo_form.validate({
			errorClass : 'error-tip',
			errorElement : 'b',
			submitHandler : function() {
				var str = "处理中" ,count = 0;
				var submit_button = $('#submit_button');
				submit_button.html(str).attr('disabled',true);
				var this_timer = setInterval(function(){
					submit_button.html(str + Array(count++%3 + 2).join('.'));
				},500);
				$("#updateEnterpriseInfo_form").ajaxSubmit({
					dataType : 'json',
					success : function(res) {
						if(res.code===1){
							alert("申请实名认证已提交");
							$('.fn-g-btn').hide();
							$('.sz').hide();
							$('.upload-btn').hide();
							$.ajax({
								url : '/userManage/sendRealNameAuthentication',
								type : 'post',
								dataType : 'json',
								data : {
									activaStep : $('[name=activaStep]').val()
								},
								success : function(res){
									if(res.code==0){
										alert(res.message);
									}
									if(res.activaStep == 1){
										window.location.href="/userManage/userRealNameInfo";
									}else{
										window.location.href="/anon/signBankCard";
									}
									
								}
							});
						}else if(res.code===0){
							clearInterval(this_timer);
							submit_button.html("实名认证").attr('disabled',false);
							alert(res.message);
						}else{
							clearInterval(this_timer);
							submit_button.html("实名认证").attr('disabled',false);
							alert("请勿重复提交!请刷新后，重填内容再提交！");
						}
						
					}
				});
			},
			errorPlacement : function(error, element) {
				if (element.attr('name') == 'businessPeriod'||element.attr('name') == 'cardPeriod') {
					element.parent().next().after(error);
				} else {
					element.after(error);
				}
			},
			rules : {
				enterpriseName : {
					required : true
				},
				organizationCode : {
					required : true
				},
				taxRegistrationNo : {
					required : true
				},
				businessLicenseNo : {
					required : true
				},
				businessLicenseProvince : {
					required : true
				},
				businessLicenseCity : {
					required : true
				},
				commonlyUsedAddress : {
					required : true
				},
				businessPeriod: {
					required : true
				},
				legalRepresentativeName : {
					required : true
				},
				legalRepresentativeCardNo : {
					required : true,
					checkID: true
				},
				cardPeriod : {
					required : true
				},
				mobile : {
					required : true,
					isMobile : true
				},
				contactName : {
					required : true
				},
				comPhone : {
					required : true,
					isPhoneOrMobile : true
				},
				contactCertNo : {
					required : true,
					checkID: true
				}

			},
			messages : {
				enterpriseName : {
					required : '请输入企业名称'
				},
				organizationCode : {
					required : '请输入组织机构代码'
				},
				taxRegistrationNo : {
					required : '请输入税务登记号'
				},
				businessLicenseNo : {
					required : '请输入营业执照注册号'
				},
				businessLicenseProvince : {
					required : '请选择注册地省份'
				},
				businessLicenseCity : {
					required : '请选择注册地城市'
				},
				commonlyUsedAddress : {
					required : '请输入常用地址'
				},
				businessPeriod: {
					required : '请输入经营期限'
				},
				legalRepresentativeName : {
					required : '请输入法人真实姓名'
				},
				legalRepresentativeCardNo : {
					required : '请输入法人身份证号',
					checkID: '请输入正确的身份证号'
				},
				cardPeriod : {
					required : '请输入法人身份证到期时间'
				},
				mobile : {
					required : '请输入手机号',
					isMobile : '请输入正确的手机号'
				},
				contactName : {
					required : '请输入联系人姓名'
				},
				comPhone : {
					required : '请输入公司联系电话(座机)',
					isPhoneOrMobile : '请输入正确的电话号码'
				},
				contactCertNo : {
					required : '请输入身份证号码',
					checkID: '请输入正确的身份证号'
				}
			},
			onkeyup : false

		});

	}
	
	
	$('#isForever').click(function(){
		var input = $('.fn-isdate');
		if($(this).attr('checked')) {
			input.val('').attr('disabled',true).rules('remove','required');
		} else {
			input.removeAttr('disabled').rules('add','required');
		}
	});
	if($('#isForever').attr('checked')) {
		var input = $('.fn-isdate');
		input.val('').attr('disabled',true).rules('remove','required');
	}
	$('#isForeverCard').click(function(){
		var input = $('.fn-isdate1');
		if($(this).attr('checked')) {
			input.val('').attr('disabled',true).rules('remove','required');
		} else {
			input.removeAttr('disabled').rules('add','required');
		}
	});
	if($('#isForeverCard').attr('checked')) {
		var input = $('.fn-isdate1');
		input.val('').attr('disabled',true).rules('remove','required');
	}
	
});