define(function(require) {
	var Site = require('../comp/init.js');
	require('../content/fileUpload.js');
	require('../content/chineseAmountExchange.js');
	$('.time').click(function() {
		WdatePicker({
			startDate : '%y-%M-01 HH:mm:ss',
			dateFmt : 'yyyy-MM-dd HH:mm:ss'
		});
	});
	
	$('#radio002_1').blur(function() {
		
		if($("#radio002").prop("checked")){
			if($("#radio002_1").val().trim().match(/^\d+$/) && $("#radio002_1").val().substr(0,1)!=0){
				
				$(".error-tip").remove();
			}else{
				
				$(".error-tip").remove();
				$("#radio002_1").parent().append("<b class='error-tip'>请认真输入天数</b>");
				//alert("请认真输入天数");
				return false;
			}
		}
	});
	function formatFloat(src, pos)
	{
	    return Math.round(src*Math.pow(10, pos))/Math.pow(10, pos);
	}
	
	//$('textarea[name=loanNote]').xheditor({});
	var editor= KindEditor.create('textarea[name=loanNote]',{
		uploadJson : '/upload/imagesUpload.htm;jsessionid='+ $_GLOBAL.sessionID,
        fileManagerJson : '/upload/imagesUpload.htm;jsessionid='+ $_GLOBAL.sessionID,
        allowFileManager : true,
        filterMode:false,
        afterBlur: function(){this.sync();}
	});
	//$('input[name=realName]').attr("disabled", true);
	$('input[name=userName]').blur( function(){
		var _this=$(this).val();
		$.ajax({
			url : '/backstage/getRealName',
			type : 'post',
			dataType : 'json',
			data : {
				userName : _this,
				roleId : 13
			},
			success : function(res) {
				if(res.code==1){
					if(res.message!=null){
						$('p[name=realName]').text(res.message);
						$('input[name=loanerUserName]').val(res.message);
						if(res.userType=="GR"){
							$('.qiYe').hide();
							$('.geRen').show();
						}else if(res.userType=="JG"){
							$('.qiYe').show();
							$('.geRen').hide();
						}
					}else{
						$('p[name=realName]').text("");
						$('p[name=loanerUserName]').text("");
					}
					
				}else{
					$('p[name=realName]').text("");
					$('p[name=loanerUserName]').text("");
				//	$('#user_Name').after("<font color='red'>"+res.message+"</font>");
				}
				
			},error:function(e){
				console.log(e);
			}
		})
	});
	
	$('input[name=timeLimitUnit]').click(function(){
		var sel = $(this).parent().parent().find('select');
		var allsel =  $(this).parent().parent().parent().find('select');
		sel.parent().parent().parent().parent().find('.jqTransformSelectOpen').hide();
		sel.parent().find('.jqTransformSelectOpen').show();
		allsel.attr('disabled',true);
		sel.removeAttr('disabled');
	});
	
	$('input[name=saturationConditionMethod]').click(function(){
		var inp = $(this).parent().find('input[type=text]');
		var allinp = $(this).parent().parent().find('input[type=text]');
		allinp.attr('disabled',true);
		inp.removeAttr('disabled');
	});
	
	function templateChange(obj){
		var curId = obj;
		$.ajax({
			url : '/backstage/queryRuleInfo',
			type : 'post',
			dataType : 'json',
			data : {
				name : $("#"+curId).val()
			},
			success : function(res) {		
				//p.text(res.message);
				//sel.after("<p class="u-tip mt5">"+res.message+"</p>")
				var loanInterest1 = $("#templateRate1").val();
				var loanInterest2 = $("#templateRate2").val();
				if($.trim(loanInterest1) == ""){
					loanInterest1 = 0;
				}
				if($.trim(loanInterest2) == ""){
					loanInterest2 = 0;
				}
				
				var investRate1 = $("#investRate1").val();
				var investRate2 = $("#investRate2").val();
				if($.trim(investRate1) == ""){
					investRate1 = 0;
				}
				if($.trim(investRate2) == ""){
					investRate2 = 0;
				}
				var totalInterestRate = 0;
				var totalInvestRate = 0;
				if(curId == "divisionTemplateId1"){
					$("#s1").text(res.message);
					$("#templateRate1").val(parseFloat(res.loanInterest));
					$("#investRate1").val(parseFloat(res.investorInterest));
					totalInterestRate = parseFloat(loanInterest2) + parseFloat(res.loanInterest);
					totalInvestRate = parseFloat(investRate2) + parseFloat(res.investorInterest);
				}else{
					$("#templateRate2").val(parseFloat(res.loanInterest));
					$("#investRate2").val(parseFloat(res.investorInterest));
					totalInterestRate = parseFloat(loanInterest1) + parseFloat(res.loanInterest);
					totalInvestRate = parseFloat(investRate1) + parseFloat(res.investorInterest);
					$("#s2").text(res.message);
				}
				$("#loanInterest").val(formatFloat(totalInterestRate, 2));
				$("#investInterestRate").val(formatFloat(totalInvestRate, 2));
			},error:function(e){
				console.log(e);
			}
		})
	}
	document.getElementById("divisionTemplateId1").onchange = function(){
		templateChange("divisionTemplateId1");
	}
	document.getElementById("divisionTemplateId2").onchange = function(){
		templateChange("divisionTemplateId2");
	}
	
    $('input[name=divisionTimeLimitUnit]').click(function(){
		var sel = $(this).parent().parent().find('select');
		//var p=$(this).parent().parent().find('p').last();
		var allsel =  $(this).parent().parent().parent().find('select');
		sel.parent().parent().parent().parent().find('.jqTransformSelectOpen').hide();
		sel.parent().find('.jqTransformSelectOpen').show();
		allsel.attr('disabled',true);
		sel.removeAttr('disabled');
		var s=$(this).next().text();
		sel.get(0).onchange = function(){
			//	alert(sel.val())
			$.ajax({
				url : '/backstage/queryRuleInfo',
				type : 'post',
				dataType : 'json',
				data : {
					name : sel.val()
				},
				success : function(res) {		
					//p.text(res.message);
					//sel.after("<p class="u-tip mt5">"+res.message+"</p>")
					if(s=="筹资阶段："){
						$("#s1").text(res.message);
						$("#s2").text("");
					}else{
						$("#s2").text(res.message);
						$("#s1").text("");
					}
				},error:function(e){
					console.log(e);
				}
			})
		}
	});
   
	/** 验证发布借款需求FORM表单 */
	var addForm=$('#add_loandemand_form');
	if(addForm.length){
		addForm.validate({
			errorClass: 'error-tip',
			errorElement: 'b',
			errorPlacement: function(error, element) {
				if(element.attr('name') == 'userName'||element.attr('name') == 'loanAmount'||element.attr('name') == 'interestRate'){
					element.next().after(error);
				}else if(element.attr('name') == 'dimensions'||element.attr('name') == 'timeLimitUnit'){
					element.parent().parent().after(error);
				}else{
					element.after(error);
				}
			},
			rules : {
				userName : {
					required : true,
					customRemote : {
						url : '/backstage/checkBorrower',
						customError : function(element, res) {
							return res.message;
						}
					}
				},
				loanName : {
					required : true
				},
				dimensions : {
					required: true,
					noZh:true
				},
				loanAmount : {
					required:true,
					number : true
				},
				leastInvestAmount : {
					required:true,
					number : true
				},
				interestRate : {
					required:true,
					number : true
				},
				loanPurpose : {
					required:true
				},
				timeLimitUnit : {
					required:true
				},
				

				contractTemplateId:{
					required:true,
				},
				receiptTemplateId:{
					required:true,
				},
				
				
				deadline : {
					required:true
				},
				investAvlDate : {
					required:true
				},
				guaranteeLicenceNo: {
					required:true
					
				},
				guaranteeLicenseName : {
					required:true
				}
			},
			messages: {
				userName:{
					required: "请输入借款人用户名"
				},
				loanName : {
					required: "请输入借款标题"
				},
				dimensions : {
					required: "请选择贷款规模",
					noZh:'请选择贷款规模'
				},
				loanAmount : {
					required:'请输入金额',
					number : '输入数字'
				},
				leastInvestAmount : {
					required:'请输入金额',
					number : '输入数字'
				},
				interestRate : {
					required:'请输入年利率',
					number : '输入数字'
				},
				loanPurpose : {
					required:'请输入金额',
				},
				timeLimitUnit : {
					required:'请选择期限',
				},
				
				contractTemplateId:{
					required:'请选择合同模板'
				},
				receiptTemplateId:{
					required:'请投资凭证模板'
				},
				
				
				
				deadline : {
					required:"请选择截止时间"
				},
				investAvlDate : {
					required:"请选择可投资时间"
				},
				guaranteeLicenceNo: {
					required:"请输入担保函编号"
				},
				guaranteeLicenseName : {
					required:"请输入担保函名称"
				}
			},
			submitHandler:function(){
				addForm.ajaxSubmit({
		    		success:function(res){
		    			alert(res.message);
		    			if(res.code==0){
		    				return;
		    			}
		    			
		    			if('wite'==$('input[name=status]').val()){
		    				window.location.href="/backstage/pageQueryLoanDemand?module=WITE";
		    			}else if('pass'==$('input[name=status]').val()){
		    				window.location.href="/backstage/pageQueryOfflineLoanDemand";
		    			}else{
		    				window.location.href="/backstage/pageQueryLoanDemand?module=DRAFT";
		    			}
		    		}
		    	});
			}
		});
	}
	
	$('.fn-submit1').click(function(){
		if('pass' != $('input[name=status]').val()){
			$('input[name=status]').val('wite');
		}
		if($("#radio002").prop("checked")){
			if($("#radio002_1").val().trim().match(/^\d+$/) && $("#radio002_1").val().substr(0,1)!=0){
				submitForm();
			}else{
				$(".error-tip").remove();
				$("#radio002_1").parent().append("<b class='error-tip'>请认真输入天数</b>");
				//alert("请认真输入天数");
				return false;
			}
		}else{
			submitForm();
		}
		
	});
    $('.fn-submit2').click(function(){
    	if('pass' != $('input[name=status]').val()){
    		$('input[name=status]').val('draft');
		}
		
		if($("#radio002").prop("checked")){
			if($("#radio002_1").val().trim().match(/^\d+$/) && $("#radio002_1").val().substr(0,1)!=0){
				submitForm();
			}else{
				$(".error-tip").remove();
				$("#radio002_1").parent().append("<b class='error-tip'>请认真输入天数</b>");
				//alert("请认真输入天数");
				return false;
			}
		}else{
			submitForm();
		}
    	
	});
    
    //$('[name=interestRate]').val(($('[name=interestRate]').val()|0||''));
    
    function submitForm(){
    	$('[name=interestRate]').val(($('[name=interestRate]').val()));
    	addForm.submit();
    	//window.location.href="/backstage/pageQueryLoanDemand?module=DRAFT";
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
    $('[name=leastInvestAmount]').change(
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
});