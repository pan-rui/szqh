define(function(require) {
	var Site = require('../comp/init.js');
	require('../content/chineseAmountExchange.js');
	require('../Y-all/Y-script/Y-imgplayer.js');
	require('../content/fileUpload.js');

	$('.time').click(function() {
		WdatePicker({
			startDate : '%y-%M-01 HH:mm:ss',
			dateFmt : 'yyyy-MM-dd HH:mm:ss'
		});
	});
   
	/** 验证发布借款需求FORM表单 */
	var addForm=$('#add_gift_form');
	if(addForm.length){
		addForm.validate({
			errorClass: 'error-tip',
			errorElement: 'b',
			errorPlacement: function(error, element) {
				if(element.attr('name') == 'userName'||element.attr('name') == 'interestRate'){
					element.parent().after(error);
				}else{
					element.after(error);
				}
			},
			rules : {
				name : {
					required : true
				},
				type : {
					required: true
				},
				startDate : {
					required: true
				},
				endDate : {
					required: true
				}
			},
			messages: {
				name:{
					required: "请输入礼品名称"
				},
				type : {
					required: "请选择礼品类型"
				},
				startDate : {
					required: "请选择生效时间"
				},
				endDate : {
					required:'请选择失效时间'
				}
			},
			submitHandler:function(){
				addForm.ajaxSubmit({
		    		success:function(res){
		    			alert(res.message);
		    		    //window.location.href="/backstage/";
		    		}
		    	});
			}
		});
	}
	
	$('.fn-submit1').click(function(){
		$('input[name=status]').val('wite');
		submitForm();
	});
    $('.fn-submit2').click(function(){
    	$('input[name=status]').val('draft');
    	submitForm();
    	
	});
    //$('[name=interestRate]').val(($('[name=interestRate]').val()|0||''));
    function submitForm(){
    	$('[name=interestRate]').val(($('[name=interestRate]').val()));
    	if($("#leastInvestAmountCkbox").attr('checked')){
    		var leastInvestAmount = $("#leastInvestAmountTxt").val();
    		if($.trim(leastInvestAmount)=="" || isNaN($.trim(leastInvestAmount))){
    			alert("最低投资金额输入错误！");
    			return false;
    		}
    	}
    	addForm.submit();
    }
    
    $('input[name=saturationConditionMethod],input[name=timeLimitUnit],input[name=divisionTimeLimitUnit]').each(function(i,item){
    	var bechecked = $(this).attr('bechecked');
    	if(bechecked) {
    		$(this).click();
    	}
    });
    $('[name=loanAmount]').change(
    		function(){
    			var amount = $(this).val();
    			var result = convertCurrency(amount);
    			if(result.indexOf("error") >= 0 || amount == ""){
    				$("#loanAmountChinese").text("");
    				return;
    			}
    			$("#loanAmountChinese").text(result);
    		}
    );
    $('#saturationConditionAmount').change(
    		function(){
    			var amount = $(this).val();
    			var result = convertCurrency(amount);
    			if(result.indexOf("error") >= 0 || amount == ""){
    				$("#saturationConditionAmountChinese").text("");
    				return;
    			}
    			$("#saturationConditionAmountChinese").text(result);
    		}
    );
    $("#leastInvestAmountCkbox").click(function(){
    	$("#leastInvestAmountTxt").val("");
    	$("#leastInvestAmounttChinese").text("");
    	if($(this).attr('checked')){
    		$("#leastInvestAmountTxt").removeAttr("disabled");
    		$("#leastInvestAmountSel").attr("disabled",true);
    	} else {
    		$("#leastInvestAmountTxt").attr("disabled",true);
    		$("#leastInvestAmountSel").removeAttr("disabled");
    	}
    });
    $("#leastInvestAmountTxt").change(
    	function(){
			var amount = $(this).val();
			var result = convertCurrency(amount);
			if(result.indexOf("error") >= 0 || amount == ""){
				$("#leastInvestAmountChinese").text("");
				return;
			}
			$("#leastInvestAmountChinese").text(result);
		}
    );
});