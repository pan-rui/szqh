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

	
	/** 验证发布借款需求FORM表单 */
	var addForm=$('#add_loanapply_form1');
	
	/**jquery validate验证规则添加*/
    var chrnum = /1[3|5|7|8|][0-9]{9}/;
    jQuery.validator.addMethod("chrnumComb", function(value, element) { 
    	return this.optional(element) || (chrnum.test(value)); 
    }, "请输入正确的手机号码"); 
    
    var chrnum1 = /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/;
    jQuery.validator.addMethod("chrnumComb1", function(value, element) { 
    	return this.optional(element) || (chrnum1.test(value)); 
    }, "请输入正确的身份证号码"); 
    
	
//	/** 验证借款申请FORM表单 */
//	if (addForm.length) {
//		addForm.validate({
//			
//			errorClass : 'error-tip',
//			errorElement : 'b',
//			errorPlacement : function(error, element) {
//				if (element.attr('name') == 'loanAmount') {
//					element.next().after(error);
//				}else {
//					element.after(error);
//				}
//			},
//			rules : {
//				loanerName : {
//					required : true,
//					minlength: 2,
//					maxlength: 4
//				},
//				loanerPhone : {
//					required : true,
//					chrnumComb:true
//				},
//				loanerAddress : {
//					required : true,
//					minlength: 4
//				},
//				loanerIdentity : {
//					required : true,
//					chrnumComb1:true
//				},
//				loanAmount : {
//					required:true,
//					number : true,
//					firstNum : true
//				},
//				loanPurpose : {
//					required : true
//				},
//				timeLimit : {
//					required : true
//				}
//			},
//			messages : {
//				loanerName : {
//					required : '请输入借款人姓名'
//				},
//				loanerPhone : {
//					required : '请输入手机号码'
//				},
//				loanerAddress : {
//					required : '请输入家庭住址'
//				},
//				loanerIdentity : {
//					required : '请输入身份证号'
//				},
//				loanAmount : {
//					required:'请输入金额',
//					number : '输入数字',
//					firstNum : '第一个数字必须大于0'
//				},
//				loanPurpose : {
//					required : '请输入借款用途'
//				},
//				timeLimit : {
//					required : '请选择还款期限'
//				}
//			},
//			submitHandler:function(){
//				addForm.ajaxSubmit({
//					success:function(res){
//		    			alert(res.message);
//		    			window.location.href = "/backstage/loanReview";
//		    		}
//		    	});
//			},
//			onkeyup : false
//
//		});
//	}
	
	var accessUrl='/backstage/loanReview/updateLoanApplySubmit';
	var	rejectUrl='/backstage/loanReview/updateLoanApplyRebut';
	
	$('.fn-submit1').click(function(){
		var guaranteeId = $('select[name="guaranteeId"]').val();
		if(guaranteeId==""){
			alert("请选择相关信贷公司!");
			return false;
		}
		addForm.attr('action',accessUrl);
		addForm.ajaxSubmit({
			success:function(res){
    			alert(res.message);
    			window.location.href = "/backstage/loanReview";
    		}
    	});
	});
	
    $('.fn-submit2').click(function(){
    	
    	if(confirm('是否驳回')){
    		addForm.attr('action',rejectUrl);
    		addForm.ajaxSubmit({
				success:function(res){
	    			alert(res.message);
	    			window.location.href = "/backstage/loanReview";
	    		}
	    	});
    	}
	});
    
});