define(function(require) {
	require('../comp/init.js');
	require('../content/chineseAmountExchange.js');
	require('../Y-all/Y-script/Y-msg.js');
	
	$('[name=deduct]').click(function(){
		var checkValue = $(this).val();
		if(checkValue == "NO"){
			$('[name=singleDeductLimit]').attr("disabled",true);
			$('[name=oddDeductLimit]').attr("disabled",true);
			$('[name=oddDeductLimit]').val(0);
			$('[name=singleDeductLimit]').val(0);
		}else{
			$('[name=singleDeductLimit]').attr("disabled",false);
			$('[name=oddDeductLimit]').attr("disabled",false);
		}
	});
	
	$('[name=withdrawal]').click(function(){
		var checkValue = $(this).val();
		if(checkValue == "NO"){
			$('[name=singleWithdrawalLimit]').attr("disabled",true);
			$('[name=oddWithdrawalLimit]').attr("disabled",true);
			$('[name=singleWithdrawalLimit]').val(0);
			$('[name=oddWithdrawalLimit]').val(0);
		}else{
			$('[name=singleWithdrawalLimit]').attr("disabled",false);
			$('[name=oddWithdrawalLimit]').attr("disabled",false);
		}
	});
	
    $('[name=singleDeductLimit]').change(
		function(){
			var amount = $(this).val();
			var result = convertCurrency(amount);
			if(result.indexOf("error") >= 0 || amount == ""){
				$("#singleDeducChinese").text("");
				return;
			}
			$("#singleDeducChinese").text(result);
		}
     );
    $('[name=oddDeductLimit]').change(
		function(){
			var amount = $(this).val();
			var result = convertCurrency(amount);
			if(result.indexOf("error") >= 0 || amount == ""){
				$("#oddDeductChinese").text("");
				return;
			}
			$("#oddDeductChinese").val(result);
		}
     );
    $('[name=singleWithdrawalLimit]').change(
		function(){
			var amount = $(this).val();
			var result = convertCurrency(amount);
			if(result.indexOf("error") >= 0 || amount == ""){
				$("#singleWithdrawalChinese").text("");
				return;
			}
			$("#singleWithdrawalChinese").text(result);
		}
     );
    $('[name=oddWithdrawalLimit]').change(
		function(){
			var amount = $(this).val();
			var result = convertCurrency(amount);
			if(result.indexOf("error") >= 0 || amount == ""){
				$("#oddWithdrawalChinese").text("");
				return;
			}
			$("#oddWithdrawalChinese").text(result);
		}
     );
	$(".dismiss").click(function() {
		var _this = this;
		Y.confirm('请选择','确定执行该操作？',function(opn){
			if(opn=="yes"){
				$.ajax({
					url : '/backstage/bankInfoManage/deleteBankInfoById',
					type : 'post',
					dataType : 'json',
					data : {
						bankId : $(_this).attr("data")
					},
					success : function(res) {
						alert(res.message);
						location.reload();
					}
				})
			}
		})
	})
	
	if($('[name=opt]').val()=="detail"){
		$('.fn-submit1').hide();
	}
	
	var form = $('#addBankInfo_form');
	$('.fn-submit1').click(function(){
		form.submit();
	});
	if (form.length) {
		form.validate({
			errorClass : 'error-tip',
			errorElement : 'b',
			errorPlacement : function(error, element) {
				if (element.attr('name') == 'singleDeductLimit'||element.attr('name') == 'oddDeductLimit'||
						element.attr('name') == 'singleWithdrawalLimit'||element.attr('name') == 'oddWithdrawalLimit') {
					element.next().after(error);
				} 
			},
			submitHandler : function() {
				form.ajaxSubmit({
					success : function(res) {
						alert(res.message);
						location.reload();
					}
				});
			},
			rules : {
				singleDeductLimit:{
					required: true,
					number : true
				},
				oddDeductLimit:{
					required: true,
					number : true
				},
				singleWithdrawalLimit:{
					required: true,
					number : true
				},
				oddWithdrawalLimit:{
					required: true,
					number : true
				}
			},
			messages : {
				singleDeductLimit:{
					required: '请输入单笔代扣限额',
					number : '请输入数字'
				},
				oddDeductLimit:{
					required: '请输入单日代扣限额',
					number : '请输入数字'
				},
				singleWithdrawalLimit:{
					required: '请输入单笔提现限额',
					number : '请输入数字'
				},
				oddWithdrawalLimit:{
					required: '请输入单日提现限额',
					number : '请输入数字'
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
			$('[name=bankName]').val(alt);
			$('[name=bankCode]').val($(this).attr('t'));
			$('[name=logoUrl]').val(src);
			//obj.reStart();
		});
		if($('input[name=bankCode]').val()) {
			var startBankCode = $('input[name=bankCode]').val();
			bankObjList.filter('[t='+startBankCode+']').triggerHandler('click');
		} else bankObjList.eq(0).click();
	}
});