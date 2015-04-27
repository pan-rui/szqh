define(function(require) {
	var Site = require('../comp/init.js');
	require('../content/chineseAmountExchange.js');
	require('../Y-all/Y-script/Y-imgplayer.js');
	require('../Y-all/Y-script/Y-msg.js');
	require('../content/fileUpload.js');

//	$('textarea[name=content]').xheditor({});
	var editor= KindEditor.create('#content',{
		uploadJson : '/upload/imagesUpload;jsessionid='+ $_GLOBAL.sessionID,
        fileManagerJson : '/upload/imagesUpload;jsessionid='+ $_GLOBAL.sessionID,
        allowFileManager : true
	});
	
	var add_pop_form = $('#add_pop_form');
	
	$('.fn-submit1').click(function() {
		editor.sync();
		add_pop_form.submit();
	})
	
	if (add_pop_form.length) {
		add_pop_form.validate({
			
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
				title : {
					required : true,
					minlength: 4,
				},
				content : {
					required : true,
				}
			},
			messages : {
				title : {
					required : '请输入标题'
				},
				content : {
					required : '请输入内容'
				}
			},
			submitHandler:function(){
				add_pop_form.ajaxSubmit({
		    		success:function(res){
		    			alert(res.message);
		    			window.location.href = "/app/appNews";
		    		}
		    	});
			},
			onkeyup : false

		});
	}
	
	var update_pop_form = $('#update_pop_form');
	
	$('.fn-submit2').click(function() {
		editor.sync();
		update_pop_form.submit();
	})
	
	if (update_pop_form.length) {
		update_pop_form.validate({
			
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
				title : {
					required : true,
					minlength: 4,
				},
				content : {
					required : true,
				}
			},
			messages : {
				title : {
					required : '请输入标题'
				},
				content : {
					required : '请输入内容'
				}
			},
			submitHandler:function(){
				update_pop_form.ajaxSubmit({
		    		success:function(res){
		    			alert(res.message);
		    			window.location.href = "/app/appNews";
		    		}
		    	});
			},
			onkeyup : false

		});
	}
});