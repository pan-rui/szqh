define(function(require) {
	var Site = require('../comp/init.js');
	require('../plugins/jquery.box.js');
	require('../plugins/jquery.combobox.js');
	require('../content/fileUpload.js');
	var state = $('[name=authState]').val();
	var certifyLevel = $('[name=certifyLevel]').val();
	if((state=='IS' || state=="IN")&&certifyLevel!=1){
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
							$('.sz').hide();
							$('.upload-btn').hide();
							$.ajax({
								url : '/userManage/sendRealNameAuthentication',
								type : 'post',
								dataType : 'json',
								data : {
									activaStep : 'logined'
								},
								success : function(res){
									
									
									if(res.message=="您的身份证号已进行实名认证，请进行关联认证") {
										alert(res.message);
										window.location.href=res.url;
									} else {
										if(res.code==0){
											alert("提交失败："+res.message);
										}
										if(res.activaStep == 1){
											window.location.href="/userManage/userRealNameInfo";
										}else{
											window.location.href="/anon/signBankCard";
										}
									}
								}
							});
						}else if(res.code===0){
							clearInterval(this_timer);
							submit_button.html("实名认证").attr('disabled',false);
							alert(res.message);
							window.location.href="/userManage/userHome";
						}else{
							clearInterval(this_timer);
							submit_button.html("实名认证").attr('disabled',false);
							alert("请勿重复提交!请刷新后，重填内容再提交！");
						}
						
					}
				});
			},
			errorPlacement : function(error, element) {
				if (element.attr('name') == 'businessPeriod') {
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
					checkID: true
				},
				businessPeriod: {
						required : true
				},
				certFrontPath : {
					required : true
				},
				certBackPath : {
					required : true
				}

			},
			messages : {
				realName : {
					required : '请输入真实姓名'
				},
				certNo : {
					required : '请输入证件号码',
					checkID:'请输入正确的身份证号码'
				},
				businessPeriod: {
					required : '请输入到期时间'
				},
				cardPeriod : {
					required : '请输入到期时间'
				},
				certFrontPath : {
					required : '请上传原件扫描件'
				},
				certBackPath : {
					required : '请上传原件扫描件'
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
	
	
	var img_form = $('#updateWeakUserRealNameInfo_form');
	$('.submit_form').click(throttle(function() {
		img_form.submit();
	},1000))
	
	
	/**
	 * 节流阀
	 */
	function throttle(func,delay){
		var timer,handler;
		
		//委托
		handler=function(){
			timer=null;
		};
		
		return function(){
			
			//如果存在定时器  表示还在不执行方法的时间范围内
			if(!timer){
				timer=setTimeout(handler,delay);
				func.apply(this,arguments);
			}
		};
	}
	
	
	if (img_form.length) {
		img_form.validate({
			errorClass : 'error-tip',
			errorElement : 'b',
			submitHandler : function() {
				img_form.ajaxSubmit({
					success : function(res) {
						alert(res.message);
						window.location.href = "/userManage/userHome";
					}
				});
			},

			rules : {

				realName: {
                    required: true
                },
                certNo: {
                    required: true,
                    checkID:true
                }
            },
			messages : {
				realName : {
					required : '请输入姓名'
				},
				certNo : {
					required : '请输入身份证号码',
					checkID:'请输入正确的身份证号码'
				}

			},
			onkeyup : false


		});

	}

	
});