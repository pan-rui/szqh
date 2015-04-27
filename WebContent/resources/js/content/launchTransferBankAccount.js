define(function(require){
	require('../comp/init.js');
	require('../content/chineseAmountExchange.js');
	require('../Y-all/Y-script/Y-selectarea.js');
	
	var form = $('#launchTransferBankAccount_form');
	if (form.length) {
		form.validate({
			errorClass : 'error-tip',
			errorElement : 'b',
			errorPlacement : function(error, element) {
				if (element.attr('name') == 'imgCode') {
					element.next().next().after(error);
				} else if(element.attr('name') == 'provName' || element.attr('name') == 'cityName') {
				    $('select[name=cityName]').parent().after(error.css('width','auto'));
				}else if(element.attr('name') == 'tradeAmount'){
					element.next().after(error);
				}else{
					element.after(error);
				}
			},
			rules : {
				bankAccountNo : {
					required : true,
					number:true,
					rangelength:[16,19]
				},
				provName:{
					required: true
				},
				cityName:{
					required: true
				},
				realName:{
					required: true
				},
				certNo:{
					required: true
				},
				mobile:{
					required: true,
					isMobile : true
				},
				tradeAmount:{
					required: true,
					number:true
				}
			},
			messages : {
				bankAccountNo : {
					required : '请输入银行卡号',
					number : '银行卡号只能是数字',
					rangelength : '银行卡号为16到19位'
				},
				provName:{
					required: '请选择省份'
				},
				cityName:{
					required: '请选择市'
				},
				realName:{
					required: '请输入收款方的真实姓名'
				},
				certNo:{
					required: '请输入收款方的证件号'
				},
				mobile:{
					required: '请输入收款方的手机号码',
					isMobile : '请输入正确的手机号'
				},
				tradeAmount:{
					required: '请输入金额',
					number : '金额只能是数字'
				}
			},
			onkeyup : false

		});
	}
	
	  $('[name=tradeAmount]').keyup(
		function(){
			var amount = $(this).val();
			var result = convertCurrency(amount);
			if(result.indexOf("error") >= 0 || amount == ""){
				$("#amountChinese").text("");
				return;
			}
			$("#amountChinese").val(result);
		}
     );
	
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
			$('[name=bankCode]').val($(this).attr('t'));
			$('[name=bankName]').val(alt);
			$('[name=banklogo]').val(src);
			//obj.reStart();
		});
		if($('input[name=bankType]').val()) {
			var startBankCode = $('input[name=bankType]').val();
			bankObjList.filter('[t='+startBankCode+']').triggerHandler('click');
		} else bankObjList.eq(0).click();
	}
})