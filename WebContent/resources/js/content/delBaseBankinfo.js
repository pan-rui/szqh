define(function(require) {
	require('../comp/init.js');
	require('../content/chineseAmountExchange.js');
	require('../Y-all/Y-script/Y-msg.js');
	
	$('[name=deduct]').click(function(){
		var checkValue = $(this).val();
		if(checkValue == "NO"){
			$('[name=singleDeductLimit]').attr("disabled",true);
			$('[name=singleDeductLimit]').val(0);
		}else{
			$('[name=singleDeductLimit]').attr("disabled",false);
		}
	});
	
	$('[name=withdrawal]').click(function(){
		var checkValue = $(this).val();
		if(checkValue == "NO"){
			$('[name=singleWithdrawalLimit]').attr("disabled",true);
			$('[name=singleWithdrawalLimit]').val(0);
		}else{
			$('[name=singleWithdrawalLimit]').attr("disabled",false);
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
	
	$(".delBankInfo").click(function() {
		var _this = this;
		Y.confirm('请选择','确定执行该操作？',function(opn){
			if(opn=="yes"){
				$.ajax({
					url : '/backstage/sysBankInfoManage/delSysBankInfo',
					type : 'post',
					dataType : 'json',
					data : {
						
						bankCode : $(_this).attr("data")
					},
					success : function(json) {
						alert(json.message);
						location.reload();
					}
				})
			}
		})
	})
	var form = $('#addSysBankBaseInfo_form');
	$('.fn-submit1').click(function(){
		form.submit();
	});
	var form2 =  $('#edit_form');
	$('#SysBankInfo_form').click(function(){
		form2.submit();
	});
	if (form2.length) {
		form2.validate({
			errorClass : 'error-tip',
			errorElement : 'b',
			errorPlacement : function(error, element) {
				if (element.attr('name') == 'singleDeductLimit'||
						element.attr('name') == 'singleWithdrawalLimit') {
					element.next().after(error);
				}
				
			},
			submitHandler : function() {
				form2.ajaxSubmit({
					success : function(res) {
						alert(res.message);
						window.location.href = "/backstage/sysBankInfoManage";
					}
				});
			},
			rules : {
				singleDeductLimit:{
					required: true,
					number : true
				},
				singleWithdrawalLimit:{
					required: true,
					number : true
				}
			},
			messages : {
				singleDeductLimit:{
					required: '请输入代扣限额',
					number : '请输入数字'
				},
				singleWithdrawalLimit:{
					required: '请输入提现限额',
					number : '请输入数字'
				}
			},
			onkeyup : false

		});
	}
	if (form.length) {
		form.validate({
			errorClass : 'error-tip',
			errorElement : 'b',
			errorPlacement : function(error, element) {
				if (element.attr('name') == 'singleDeductLimit'||
						element.attr('name') == 'singleWithdrawalLimit'||
						element.attr('name') == 'bankCode'||
						element.attr('name') == 'bankName') {
					element.next().after(error);
				}
				
			},
			submitHandler : function() {
				form.ajaxSubmit({
					success : function(res) {
						alert(res.message);
						window.location.href = "/backstage/sysBankInfoManage";
					}
				});
			},
			rules : {
				bankCode:{
					required:true
				},
				bankName:{
					required:true
				},
				singleDeductLimit:{
					required: true,
					number : true
				},
				singleWithdrawalLimit:{
					required: true,
					number : true
				}
			},
			messages : {
				bankCode:{
					required:'请输入银行简称'
				},
				bankName:{
					required:'请输入银行名称'
				},
				singleDeductLimit:{
					required: '请输入代扣限额',
					number : '请输入数字'
				},
				singleWithdrawalLimit:{
					required: '请输入提现限额',
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
			$('#logurl').attr("value",$("[name=logoUrl]").val());
		});
		if($('input[name=bankCode]').val()) {
			var startBankCode = $('input[name=bankCode]').val();
			bankObjList.filter('[t='+startBankCode+']').triggerHandler('click');
		} else bankObjList.eq(0).click();
	}
	
	
	
	}
);