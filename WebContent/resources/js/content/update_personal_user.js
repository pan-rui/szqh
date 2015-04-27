define(function(require) {
	var Site = require('../comp/init.js');
	require('../plugins/jquery.box.js');
	require('../plugins/jquery.combobox.js');
	require('../Y-all/Y-script/Y-selectarea.js');
	require('../Y-all/Y-script/Y-selectbranch.js');
	require('../content/fileUpload.js');
	require('../Y-all/Y-script/Y-msg.js');
	var Site = require('../Y-all/Y-script/Y-tip.js');
	
	$('input[name=realName]').Y('RareWordTip',{
		showEle: $('p.u-tip a')
	});
	
	
	var saved = false;
	var personal_form = $('#personal_form');
	var yiJiuPrefixion = "yj_";
	$('.submit_form').click(function() {
		personal_form.submit();
	})
	if (personal_form.length) {
		personal_form.validate({
			errorClass : 'error-tip',
			errorElement : 'b',
			submitHandler : function() {
				personal_form.ajaxSubmit({
					success : function(res) {
						alert(res.message);
						if(res.code==1){
							saved = true;
						}
					}
				});
			},
			errorPlacement : function(error, element) {
				if (element.attr('name') == 'businessPeriod'
						|| element.attr('name') == 'bankType'
						|| element.attr('name') == 'roleIds') {
					element.parent().append(error);
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
				realName : {
					required : true
				},
				certNo : {
					required : true,
					mustNotInclude: ' ',
					noZh:true,
					checkID:true
				},
				businessPeriod : {
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
				userName : {
					required : true,
					customRemote : {
						url : '/backstage/userManage/common/validationUserName',
						customError : function(element, res) {
							return res.message;
						},
						customSuccess: function(element, data,res) {
							$('input[name=accountName]').val( yiJiuPrefixion + $('input[name=userName]').val());
						}
					}
				},
				identityName : {
					required : true,
					customRemote : {
						url : '/anon/checkIdentityName',
						data : {
							identityName : function() {
								return $('input[name=identityName]').val();
							},
							type : function() {
								return 'JG';
							}
						},
						customError : function(element, res) {
							return res.message;
						}
					}
				},
				
				roleIds : {
					required : true
				}
			},
			messages : {
				accountName : {
					required : '请输入账户名'
				},
				realName : {
					required : '请输入真实姓名'
				},
				certNo : {
					required : '输入证件号码',
					mustNotInclude: '不允许包含空格',
					noZh:'不允许中文',
					checkID:'身份证号错误'
				},
				businessPeriod : {
					required : '请输入身份证到期时间'
				},
				mobile : {
					required : '请输入手机电话',
					isMobile : '请输入正确的手机号'
				},
				mail : {
					required : '请输入常用电子邮箱'
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
					required : '请输入选用户名'
				},
				roleIds : {
					required : '请选择角色'
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
	$("input[name = 'roleIds']").click(function(){
		if($("#investor").attr('checked')){
			$("#refereesDiv").show();
		}else{
			$("#refereesDiv").hide();
		}
		
	});
	$("input[name = 'referees']").change(function(){
		var e = $('#referees-messge');
		e.empty();
		var er = $('#referees-messge-right');
		er.empty();
	    var referees = $(this).val();
		var url = '/backstage/userManage/common/validationReferees';
		var data = {'referees' : referees};
		var result = $_GLOBAL.ajax(url, data);
		if(result.code == '1'){
			er.attr("class","right-tip");
			er.append(" ");
			e.attr("class","info-tip");
		}else{
			e.attr("class","error-tip");
			er.attr("class","");
		}
		e.append(result.message);
	});

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
		
		/*
		if(!saved){
			alert("请先保存后，再申请实名认证");
			return;
		}
		*/
		
		Y.confirm('请选择','确定执行该操作？',function(opn){
			if(opn=="yes"){
				$.ajax({
					url : '/backstage/userManage/backRealNameAut',
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