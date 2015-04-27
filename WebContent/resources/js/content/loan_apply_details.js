define(function(require) {
	var Site = require('../comp/init.js');
	require('../Y-all/Y-script/Y-tip.js');
	require('../content/fileUpload.js');
	require('../plugins/jquery.box.js');
	require('../plugins/jquery.combobox.js');
	require('../plugins/jquery.window.js');
	var verify = $('.new_captcha');
	require('../content/securityPassword.js')(210,31);
	var newcaptcha = $('#newcaptcha');
	verify.click(function() {
		var img = new Image();
		img.src = '/anon/getImgCode?dateTag=' + new Date().getTime();
		img.onload = function(){
			var obj = $(img).addClass('code-img vt new_captcha');
			obj.attr('title',newcaptcha.attr('title')).css({width:60,height:20});
			newcaptcha.after(obj);
			newcaptcha.remove();
			obj.click(function(){
				verify.click();
			})
			newcaptcha = obj;
		}
	});
	
	var add_loanapply_form = $('#add_loanapply_form');
	
	$('.fn-submit1').click(function() {
		var timeLimit = $('select[name="timeLimit"]').val();
		if(timeLimit==" "){
			alert("请选择还款期限!");
			return false;
		}
		add_loanapply_form.submit();
	})
	
	/*jquery validate验证规则添加*/
    var chrnum = /1[3|5|7|8|][0-9]{9}/;
    jQuery.validator.addMethod("chrnumComb", function(value, element) { 
    	return this.optional(element) || (chrnum.test(value)); 
    }, "请输入正确的手机号码"); 
    
    var chrnum1 = /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/;
    jQuery.validator.addMethod("chrnumComb1", function(value, element) { 
    	return this.optional(element) || (chrnum1.test(value)); 
    }, "请输入正确的身份证号码"); 
    
	
	/** 验证借款申请FORM表单 */
	if (add_loanapply_form.length) {
		add_loanapply_form.validate({
			
			errorClass : 'error-tip',
			errorElement : 'b',
			errorPlacement : function(error, element) {
				if (element.attr('name') == 'loanAmount') {
					element.next().after(error);
				}else if(element.attr('name') == 'captcha'){
					element.parent().next().next().after(error);
				}else {
					element.after(error);
				}
			},
			rules : {
				loanerName : {
					required : true,
					minlength: 2,
					maxlength: 4
				},
				loanerPhone : {
					required : true,
					chrnumComb:true
				},
				loanerAddress : {
					required : true,
					minlength: 4
				},
				loanerIdentity : {
					required : true,
					chrnumComb1:true
				},
				loanAmount : {
					required:true,
					number : true,
					firstNum : true,
					max:200,
					digits:true      
				},
				loanPurpose : {
					required : true
				},
				timeLimit : {
					required : true
				},
				captcha : {
					required : true,
					customRemote : {
						url : '/loan/validateCaptcha',
						customError : function(element, res) {
							return res.message;
						}
					}
				}
			},
			messages : {
				loanerName : {
					required : '请输入借款人姓名'
				},
				loanerPhone : {
					required : '请输入手机号码'
				},
				loanerAddress : {
					required : '请输入家庭住址'
				},
				loanerIdentity : {
					required : '请输入身份证号'
				},
				loanAmount : {
					required :'请输入金额',
					number : '输入数字',
					firstNum : '第一个数字必须大于0',
					max : '最大借款为200万',
					digits:'请输入整数'    
				},
				loanPurpose : {
					required : '请输入借款用途'
				},
				timeLimit : {
					required : '请选择还款期限'
				},
				captcha : {
					required : '请输入验证码'
				}
			},
			submitHandler:function(){
				add_loanapply_form.ajaxSubmit({
					success:function(res){
		    			alert(res.message);
		    			window.location.href = "/loan/applySuccess";
		    		}
		    	});
			},
			onkeyup : false

		});
	}
	
});