define(function(require) {
	var Site = require('../comp/init.js');
	require('../content/chineseAmountExchange.js');
	require('../Y-all/Y-script/Y-imgplayer.js');
	
	//$('textarea[name=content-info]').xheditor({});
	var add_form = $('#add_pdf_template');
	
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
				name : {
					required : true,
					minlength: 6,
				},
				xslContent : {
					required : true,
				},
				pdfName : {
					required : true,
				}
			},
			messages : {
				pdfName : {
					required : '请设置生成PDF后的文件名'
				},
				xslContent : {
					required : '模板内容'
				},
				name : {
					required : '模板名'
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
				name : {
					required : true,
					minlength: 6,
				},
				xslContent : {
					required : true,
				},
				pdfName : {
					required : true,
				}
			},
			messages : {
				pdfName : {
					required : '请设置生成PDF后的文件名'
				},
				xslContent : {
					required : '模板内容'
				},
				name : {
					required : '模板名'
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