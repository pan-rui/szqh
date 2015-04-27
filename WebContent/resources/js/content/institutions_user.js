define(function(require) {
	var Site = require('../comp/init.js');
	require('../Y-all/Y-script/Y-htmlupload.js');
	require('../content/fileUpload.js');
	require('../Y-all/Y-script/Y-selectarea.js');
	require('../Y-all/Y-script/Y-selectbranch.js');
	require('../Y-all/Y-script/Y-msg.js');

	var institutions_form = $('#institutions_form');
	$('.submit_form').click(function() {
		institutions_form.submit();
	})
	var yrdPrefixion = $_GLOBAL.yrdPrefixion;
	var institutions_form = $('#institutions_form');
	if (institutions_form.length) {
		institutions_form.validate({
			errorClass : 'error-tip',
			errorElement : 'b',
			submitHandler : function() {
				institutions_form.ajaxSubmit({
					success : function(res) {
						alert(res.message);
						window.location.href="/backstage/userManage/institutionManage";
					}
				});
			},
			errorPlacement : function(error, element) {
				if (element.attr('name') == 'businessPeriod' || element.attr('name') == 'roleIds' || element.attr('name') == 'identityStartNo') {
					element.parent().append(error);
				}else if(element.attr('name') == 'businessLicenseProvince' || element.attr('name') == 'bankType'){
					element.parent().parent().next().after(error);
				}else if(element.attr('name') == 'userName'){
					element.next().after(error);
				} else {
					element.after(error);
				}

			},
			rules : {
				accountName : {
					required : true,
					customRemote : {
						url : '/backstage/userManage/common/validationAccountName',
						customError : function(element, res) {
							return res.message;
						}
					}
				},
				enterpriseName : {
					required : true
				},
				businessLicenseNo : {
					required : true,
					mustNotInclude: ' ',
					noZh:true
				},
				businessLicenseProvince : {
					required : true
				},
				commonlyUsedAddress : {
					required : true
				},
				comPhone : {
					required : true,
					isPhoneOrMobile : true
				},
				businessPeriod : {
					required : true
				},
				legalRepresentativeName : {
					required : true
				},
				legalRepresentativeCardNo : {
					required : true,
					mustNotInclude: ' ',
					noZh:true,
					checkID:true
				},
				contactCertNo : {
					required : true,
					mustNotInclude: ' ',
					noZh:true,
					checkID:true
				},
				contactName : {
					required : true
				},
				mobile : {
					required : true,
					isMobile : true
				},
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
				/*bankOpenName : {
					required : true
				},
				bankCardNo : {
					required : true
				},
				bankType : {
					required : true
				},*/
				p : {
					required : true
				},
				c : {
					required : true
				},
				userName : {
					required : true,
					NumandLetter_:true,
					minlength:6,
					maxlength:20,
					customRemote : {
						url : '/backstage/userManage/common/validationUserName',
						customError : function(element, res) {
							return res.message;
						},
						customSuccess: function(element, data,res) {
							$('input[name=accountName]').val( yrdPrefixion + $('input[name=userName]').val());
						}
					}
				},
//				institutionsInCode : {
//					required : true,
//					minlength:6,
//					maxlength:10
//				},
				identityName : {
					required : true,
					customRemote : {
						url : '/anon/checkIdentityName?dateTag='+ new Date().getTime(),
						customError : function(element, res) {
							return res.message;
						}
					}
				},
				identityStartNo : {
					required : true
				},
				identityEndNo : {
					required : true
				},
			
				roleIds : {
					required : true
				}

			},
			messages : {
				accountName : {
					required : '请输入账户名'
				},
				enterpriseName : {
					required : '请输入企业名称'
				},
				businessLicenseNo : {
					required : '请输入营业执照注册号',
					mustNotInclude: '不允许包含空格',
					noZh:'不允许中文'
				},
				p : {
					required : '请选择省'
				},
				c : {
					required : '请选择市'
				},
				businessLicenseProvince : {
					required : '请选择营业执照所在地'
				},
				commonlyUsedAddress : {
					required : '请输入常用地址'
				},
				comPhone : {
					required : '请输入公司联系电话',
					isPhoneOrMobile : '请输入正确的电话号码格式'
				},
				businessPeriod : {
					required : '请输入经营期限'
				},
				legalRepresentativeName : {
					required : '请输入法人代表姓名'
				},
				legalRepresentativeCardNo : {
					required : '请输入身份证号码',
					mustNotInclude: '不允许包含空格',
					noZh:'不允许中文',
					checkID:'身份证号错误'
				},
				contactName : {
					required : '请输入常用联系人'
				},
				mobile : {
					required : '请输入手机电话',
					isMobile : '请输入正确的手机号'
				},
				bankOpenName : {
					required : '请输入银行开户名'
				},
				bankCardNo : {
					required : '请输入银行卡号'
				},
				bankType : {
					required : '请选择银行'
				},
				userName : {
					required : '请输入选用户名',
					minlength: "不能少于6位",
					maxlength:"不能大于20位",
					NumandLetter_ : "只能为数字、字母、下划线"
				},
				contactCertNo : {
					required : '请输入身份证号码',
					mustNotInclude: '不允许包含空格',
					noZh:'不允许中文',
					checkID:'身份证号错误'
				},
//				institutionsInCode : {
//					required : "请输入机构简码",
//					minlength: "不能少于6位",
//					maxlength:"不能大于10位"
//				},
                mail:{
                    required:"请输入邮件"
                },
				identityName : {
					required : "请输入机构简码"
				},
				identityStartNo : {
					required : "请输入号段起始值"
				},
				identityEndNo : {
					required : "请输入号段结束值"
				},
			
				roleIds : {
					required : "请选择用户角色"
				}
			},
			onkeyup : false

		});

	}
	//营业期限
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
	//身份证期限
	$('#isForeverCard').click(function() {
		var input = $('.fn-isdate1');
		if ($(this).attr('checked')) {
			input.val('').attr('disabled', true).rules('remove', 'required');
		} else {
			input.removeAttr('disabled').rules('add', 'required');
		}
	});
	if ($('#isForeverCard').attr('checked')) {
		var input = $('.fn-isdate1');
		input.val('').attr('disabled', true).rules('remove', 'required');
	}
	
	$("input[name = 'roleIds']").click(function(){
		if($("#investor").attr('checked')){
			$("#refereesDiv").show();
		}
	});
	
	$('[name=bankType]')[0].onchange=function(){
		var obj  = Y.getCmp('bankType');
		if(!$(this).val())return obj.reset(1);
		obj.reStart();
		obj.renderDiv.show();
	}
	$('[name=p]')[0].onchange=function(){
		$('[name=businessLicenseProvince]').val($('[name=p]').val());
	}
	$('[name=c]')[0].onchange=function(){
		$('[name=businessLicenseCity]').val($('[name=c]').val());
	}
	$.ajax({
		url : '/bank/getAllBank',
		type : 'post',
		dataType : 'json',
		success : function(res) {
			var obj = $('[name=bankType]');
			if (res.code == 1) {
				obj.html('');
				obj.append(
						'<option value="">请选择银行</option>');
				for ( var i = 0; i < res.data.length; i++) {
					var data = res.data[i];
					obj.append(
							'<option value="' + data.bankCode + '">'
									+ data.bankName + '</option>')
				}
				if ($('#bankType').val()){
					var Y_div = $('#bankType').prev().find('div');
					obj.val($('#bankType').val());
					
					setBank(obj);
				}
				fix_select(obj);
			}
		}
	});
	function setBank(ele){
		var obj  = Y.getCmp('bankType');
		if(!obj || !obj.getAreaInfoSts){
			setTimeout(function(){
				setBank(ele);
			},10);
		}else{
			if(!ele.val())return obj.reset(1);
			obj.renderDiv.show();
		}
	}
	function fix_select(selector) {
		var i = $(selector).parent().find('div,ul').remove().css('zIndex');
		$(selector).unwrap().removeClass('jqTransformHidden').jqTransSelect();
		$(selector).parent().css('zIndex', i);
	}
	
	$("[name=sendRealName]").click(function() {
		Y.confirm('请选择','确定执行该操作？',function(opn){
			if(opn=="yes"){
				$.ajax({
					url : '/backstage/userManage/institutionManage/updateInstitutionsUser/backEnterpriseRealNameAuth',
					type : 'post',
					dataType : 'json',
					data : {
						userBaseId : $("[name=userBaseId]").val()
					},
					success : function(res) {
						alert(res.message);
						location.reload();
					}
				})
			}
		})
	});
});