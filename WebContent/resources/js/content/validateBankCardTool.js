define(function(require) {
	require('../comp/init.js');
	require('../Y-all/Y-script/Y-selectarea.js');
	
	$(".fn-submit1").click(function(){
		form.submit();
	});
	var form = $('#addBankCard_form');
	if (form.length) {
		form.validate({
			errorClass : 'error-tip',
			errorElement : 'b',
			errorPlacement : function(error, element) {
				if (element.attr('name') == 'imgCode') {
					element.next().next().after(error);
				} else if(element.attr('name') == 'bankProvince' || element.attr('name') == 'bankCity') {
				    $('select[name=bankCity]').parent().after(error.css('width','auto'));
				}else{
					element.after(error);
				}
			},
			submitHandler : function() {
				form.ajaxSubmit({
					success : function(res) {
						alert(res.message);
					}
				});
			},
			rules : {
				bankCardNo : {
					required : true,
					number:true
//					customRemote : {
//						url : '/anon/checkUserName?dateTag=' + new Date().getTime(),
//						customError : function(element, res) {
//							return res.message;
//						}
//					}
				},
				bankProvince:{
					required: true
				},
				bankCity:{
					required: true
				},
				idCardNo:{
					required: true,
					mustNotInclude: ' ',
					noZh:true,
					checkID:true
				},
				bankOpenName:{
					required: true
				}
				
			},
			messages : {
				bankCardNo : {
					required : '请输入银行卡号',
					number : '银行账号只能是数字'
				},
				bankProvince:{
					required: '请选择省份'
				},
				bankCity:{
					required: '请选择市'
				},
				idCardNo:{
					required: '请输入身份证号',
					mustNotInclude: '不能保护空格',
					noZh : '不能输入中文',
					checkID : '身份证不合法'
				},
				bankOpenName:{
					required: '请输入账户名'
				}
			},
			onkeyup : false

		});
	}
	
	var bankObj = $('.choose-bank');
	if (bankObj.length > 0) {
		var bankObjList = bankObj.find('.bank-list').find('li');
		$('.select-box', bankObj).find('img').attr('src',
				bankObjList.eq(0).find('img').attr('src')).attr('alt',
				bankObjList.eq(0).find('img').attr('alt'));

		$('#bank_id').val(bankObjList.eq(0).find('span').html());
		$('#bank_url').val(bankObjList.eq(0).find('img').attr('src'));

		$('.select-right', bankObj).click(function(e) {
			e.preventDefault();
			bankObj.find('.bank-list').toggle();
		});

		bankObjList.hover(function() {
			$(this).addClass('cur')
		},function() {
			$(this).removeClass('cur')
		});

		
		$('body').click(function(e) {
			if (bankObj.length && !$(e.target).hasClass('select-right')) {
				bankObj.find('.bank-list').hide();
			}
		});

		bankObjList.click(function() {
			var src = $(this).find('img').attr('src');
			var alt = $(this).find('img').attr('alt');
			$('.select-box', bankObj).find('img').attr('src', src).attr('alt',alt);
			bankObj.find('.bank-list').hide();
			$('[name=bankType]').val($(this).attr('t'));
			//obj.reStart();
		});
		if($('input[name=bankType]').val()) {
			var startBankCode = $('input[name=bankType]').val();
			bankObjList.filter('[t='+startBankCode+']').triggerHandler('click');
		} else bankObjList.eq(0).click();
	}
});