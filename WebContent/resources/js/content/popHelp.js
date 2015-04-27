define(function(require) {
	var Site = require('../comp/init.js');
	require('../content/chineseAmountExchange.js');
	require('../Y-all/Y-script/Y-imgplayer.js');
	require('../content/fileUpload.js');
	require('../content/pictureUpload.js');
	 Y.create('ImgPlayer',{
			eleArr:'#guaranteeLicenseUrl_Img',
			titleInfo: 'alt',
			content:'',
			pathInfo: function(){
			  return $(this).attr('src');
			}
		});
	$('.time').click(function() {
		WdatePicker({
			startDate : '%y-%M-01 HH:mm:ss',
			dateFmt : 'yyyy-MM-dd HH:mm:ss'
		});
	});
	
	function formatFloat(src, pos)
	{
	    return Math.round(src*Math.pow(10, pos))/Math.pow(10, pos);
	}

	//$('textarea[name=content]').xheditor({});
	var editor= KindEditor.create('textarea[name=content]',{
		uploadJson : '/upload/imagesUpload.htm;jsessionid='+ $_GLOBAL.sessionID,
        fileManagerJson : '/upload/imagesUpload.htm;jsessionid='+ $_GLOBAL.sessionID,
        allowFileManager : true,
        afterBlur: function(){this.sync();}
	});
	//$('input[name=realName]').attr("disabled", true);
	
	$('[name=type]').click(function() {
		if(5 == $(this).val()){
			$('#selectModules').hide();
		}else{
			$('#selectModules').show();
		}
	})
	
	var add_pop_form = $('#add_pop_form');
	
	$('.fn-submit1').click(function() {
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
		    			window.location.href = window.location.href;
		    		}
		    	});
			},
			onkeyup : false

		});
	}
	
	var update_pop_form = $('#update_pop_form');
	
	$('.fn-submit2').click(function() {
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
		    			window.location.href = window.location.href;
		    		}
		    	});
			},
			onkeyup : false

		});
	}
});