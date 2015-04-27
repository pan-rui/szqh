define(function(require) {
	var Site = require('../comp/init.js');
	require('../plugins/jquery.box.js');
	require('../plugins/jquery.combobox.js');
	require('../Y-all/Y-script/Y-msg.js');
	var Site = require('../Y-all/Y-script/Y-tip.js');
	
	$('input[name=realName]').Y('RareWordTip',{
		showEle: $('p.u-tip a')
	});
	
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
					}
				});
			},
			errorPlacement : function(error, element) {
				if (element.attr('name') == 'roleIds') {
					element.parent().append(error);
				} else {
					element.after(error);
				}

			},
			rules : {
				
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
				
				mobile : {
					required : '请输入手机电话',
					isMobile : '请输入正确的手机号'
				},
				mail : {
					required : '请输入常用电子邮箱'
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

	
	
});