define(function(require) {
	var Site = require('../comp/init.js');
	require('../plugins/jquery.box.js');
	require('../plugins/jquery.combobox.js');
	require('../content/fileUpload.js');
	var state = $('[name=authState]').val();
	if(state=='IS' || state=="IN"){
		$('#submit_button').attr('disabled',true);
		$('.fn-g-btn').hide();
		$('.upload-btn').hide();
		$('.sz').hide();
		$('[name=realName]').attr('disabled',true);
		$('[name=certNo]').attr('disabled',true);
		$('[name=businessPeriod]').attr('disabled',true);
		$('[name=contactName]').attr('disabled',true)
	}
	var updateUserRealNameInfo_form = $('#updateUserRealNameInfo_form');
	if (updateUserRealNameInfo_form.length) {
		updateUserRealNameInfo_form.validate({
			errorClass : 'error-tip',
			errorElement : 'b',
			submitHandler : function() {
				var str = "处理中" ,count = 0;
				var submit_button = $('#submit_button');
				submit_button.html(str).attr('disabled',true);
				var this_timer = setInterval(function(){
					submit_button.html(str + Array(count++%3 + 2).join('.'));
				},500);
				$("#updateUserRealNameInfo_form").ajaxSubmit({
					dataType : 'json',
					success : function(res) {
						if(res.code===1){
							alert("申请实名认证已提交");
							$('.fn-g-btn').hide();
							$.ajax({
								url : '/userManage/sendRealNameAuthentication',
								type : 'post',
								dataType : 'json',
								data : {
									activaStep : $('[name=activaStep]').val()
								},
								success : function(res){
									window.location.href="/anon/signBankCard"
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
				realName : {
					required : true
				},
				certNo : {
					required : true,
					customRemote : {
						url : '/anon/checkCertNoByType?dateTag=' + new Date().getTime(),
						data:{
							certNo : function() {
								return $('input[name=certNo]').val();
							}
						},
						customError : function(element, res) {
							return res.message;
						}
					}
				},
				businessPeriod: {
						required : true
				}

			},
			messages : {
				realName : {
					required : '请输入真实姓名'
				},
				certNo : {
					required : '请输入证件号码',
					checkID: true
				},
				businessPeriod: {
					required : '请输入到期时间'
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