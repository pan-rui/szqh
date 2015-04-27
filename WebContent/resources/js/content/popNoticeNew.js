define(function(require) {
	var Site = require('../comp/init.js');
	require('../content/chineseAmountExchange.js');
	require('../Y-all/Y-script/Y-imgplayer.js');
	require('../content/fileUpload.js');
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
			addTime : '%y-%M-01 HH:mm:ss',
			dateFmt : 'yyyy-MM-dd HH:mm:ss'
		});
	});
	
	function formatFloat(src, pos)
	{
	    return Math.round(src*Math.pow(10, pos))/Math.pow(10, pos);
	}

	//$('textarea[name=content]').xheditor({});
	var editor= KindEditor.create('#content',{
		uploadJson : '/upload/imagesUpload;jsessionid='+ $_GLOBAL.sessionID,
        fileManagerJson : '/upload/imagesUpload;jsessionid='+ $_GLOBAL.sessionID,
        allowFileManager : true
	});
	/*新增公告*/
	var addForm=$('#add_pop_form');
	
	$('.fn-submit1').click(function(){
		editor.sync();
		addForm.submit();
	});
	
	if (addForm.length) {
		addForm.validate({
			ignore:'',
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
					minlength: 4
				},
				content : {
					required : true
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
				addForm.ajaxSubmit({
		    		success:function(res){
		    			alert("添加成功");
		    			window.location.href = "/backstage/popModuleCenter";
		    		}
		    	});
			},
			onkeyup : false

		});
	}
	
	/*修改公告*/
	var updateForm =$('#update_pop_form');
	
	$('.fn-submit2').click(function(){
		editor.sync();
		updateForm.submit();
	});
	
	if (updateForm.length) {
		updateForm.validate({
			ignore:'',
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
					minlength: 4
				},
				content : {
					required : true
				},
				addTime2 : {
					required : true
				}
			},
			messages : {
				title : {
					required : '请输入标题'
				},
				content : {
					required : '请输入内容'
				},
				addTime2 : {
					required : '请输入时间'
				}
			},
			submitHandler:function(){
				updateForm.ajaxSubmit({
		    		success:function(res){
		    			alert("修改成功");
		    			window.location.href = "/backstage/popModuleCenter";
		    		}
		    	});
			},
			onkeyup : false

		});
	}
});