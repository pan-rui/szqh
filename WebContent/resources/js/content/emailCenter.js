define(function(require) {
	var Site = require('../comp/init.js');
	require('../content/chineseAmountExchange.js');
	require('../Y-all/Y-script/Y-imgplayer.js');
	
	$('textarea[name=content-info]').xheditor({});
	var add_form = $('#add_email_template');
	
	$('.fn-submit1').click(function() {
		add_form.submit();
	})
	
	if (add_form.length) {
		add_form.validate({
			
			errorClass : 'error-tip',
			errorElement : 'b',
			errorPlacement : function(error, element) {
				if (element.attr('name') == 'imgCode') {
					element.next().next().after(error);
				} else {
					element.after(error);
				}
			},
			rules : {
				subject : {
					required : true,
					minlength: 6,
				},
				content : {
					required : true,
				},
				id : {
					required : true,
					customRemote : {
						url : '/backstage/emailCenter/checkEmailTemlateId?dateTag=' + new Date().getTime(),
						customError : function(element, res) {
							return res.message;
						}
					}
				}
			},
			messages : {
				id : {
					required : '请输入id'
				},
				subject : {
					required : '请输入标题'
				},
				content : {
					required : '请输入内容'
				}
			},
			submitHandler:function(){
				add_form.ajaxSubmit({
		    		success:function(res){
		    			alert(res.message);
		    			window.location.href = window.location.href;
		    		}
		    	});
			},
			onkeyup : false

		});
	}
	
	var update_form = $('#update_form');
	
	$('.fn-submit2').click(function() {
		update_form.submit();
	})
	
	if (update_form.length) {
		update_form.validate({
			
			errorClass : 'error-tip',
			errorElement : 'b',
			errorPlacement : function(error, element) {
				if (element.attr('name') == 'imgCode') {
					element.next().next().after(error);
				} else {
					element.after(error);
				}
			},
			rules : {
				subject : {
					required : true,
					minlength: 6,
				},
				content : {
					required : true,
				}
			},
			messages : {
				subject : {
					required : '请输入标题'
				},
				title : {
					required : '请输入内容'
				}
			},
			submitHandler:function(){
				update_form.ajaxSubmit({
		    		success:function(res){
		    			alert(res.message);
		    			window.location.href = window.location.href;
		    		}
		    	});
			},
			onkeyup : false

		});
	}
});