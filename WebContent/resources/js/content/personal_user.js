define(function(require) {
	var Site = require('../comp/init.js');
	require('../plugins/jquery.box.js');
	require('../plugins/jquery.combobox.js');
	require('../plugins/jquery.selectBranch.js');
	require('../content/bank.js');
	require('../content/fileUpload.js');
	var Site = require('../Y-all/Y-script/Y-tip.js');
	
	$('input[name=realName]').Y('RareWordTip',{
		showEle: $('p.u-tip a')
	});
	$('input[name=userName]').blur(function() {
		var replaceName = $('input[name=userName]').val().replace(/[ ]/g,"");
		$('input[name=userName]').attr("value",replaceName);
	})
	var yrdPrefixion=$_GLOBAL.yrdPrefixion;
	var personal_form = $('#personal_form');
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
							$('input[name=accountName]').val( yrdPrefixion + $('input[name=userName]').val());
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
		if($("#broker").attr('checked')){
			$("#customerSourceDiv").show();
		}else{
			$("#customerSourceDiv").hide();
		}
		
	});
	$("input[name = 'referees']").change(function(){
		var e = $('#referees-messge');
		e.empty();
		var er = $('#referees-messge-right');
		er.empty();
	    var referees = $(this).val();
		if(!referees) {
            er.attr("class","");
            return;
        }
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
	
});