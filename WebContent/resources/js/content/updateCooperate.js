define(function(require) {
	var Site = require('../comp/init.js');
	require('../Y-all/Y-script/Y-tip.js');
	require('../Y-all/Y-script/Y-htmluploadify.js');
	require('../plugins/jquery.box.js');
	require('../plugins/jquery.combobox.js');
	require('../plugins/jquery.window.js');
	require('../content/chineseAmountExchange.js');
	require('../Y-all/Y-script/Y-imgplayer.js');
	
	var uploadHost = $("#uploadHost").val();
		//合作机构图片上传
		$('#cooperate_Upload').uploadify({
			height : 30,
			width : 120,
			buttonClass : 'u-btn u-btn-gray',
			buttonText : '选择上传图片',
			fileTypeExts : '*.gif; *.jpg; *.jpeg; *.bmp;  *.png',
			multi : false,
			swf : '/resources/swf/uploadify.swf',
			uploader : uploadHost+'/upload/imagesUpload;jsessionid='+ $_GLOBAL.sessionID,
			fileSizeLimit : '4MB',
			onUploadStart:function(){
				$('body').Y('Window',{
					content:"<span><img src='/resources/images/common/loadingScroll.gif' /><br/>上传中，请稍后...</span>",
					key:'lodding',
					simple:true
				});
			},
			scriptData: { JSESSIONID: $_GLOBAL.sessionID},
			onInit: function () {   
	         },
			onUploadSuccess : function(file, data, response) {
				var info = $.parseJSON(data);
			    if(!info) return;
			    $("#fileName").val(info.fileName);
			    $("#filePath").val(info.resData);
			    $("#requestPath").val(info.serverPath);
			    $('#uploadimg').attr('src',info.resData);  
				
				 Y.getCmp('lodding').close();
				 alert("文件上传成功！");
			},
			onUploadError : function(file, errorCode, errorMsg,
					errorString) {
				 Y.getCmp('lodding').close();
				alert("文件上传失败！异常信息：" + errorString);
			},
			onCancel : function(file) {
				 Y.getCmp('lodding').close();
				alert("已取消！");
			}
		});

	
	
	var update_form = $('#update_form');
	
	$('.fn-submit1').click(function() {
		update_form.submit();
	})
	
	/** 验证借款申请FORM表单 */
	if (update_form.length) {
		update_form.validate({
			
			errorClass : 'error-tip',
			errorElement : 'b',
			errorPlacement : function(error, element) {
				
				if(element.is('.suffixInput')){
					element.next().after(error);
				}else {
					element.after(error);
				}
			},
			rules : {
				cooName : {
					required : true
				},
				cooHerf : {
					required : true
				},
				picName : {
					required : true
				},
				sortNo : {
					required : true,
					number : true,
					digits:true
				}
			},
			messages : {
				cooName : {
					required : '请输入机构名称'
				},
				cooHerf : {
					required : '请输入机构链接'
				},
				picName : {
					required : '请上传机构图片'
				},
				sortNo : {
					required : '请输入排序编号',
					number : '输入数字',
					digits:'请输入整数'    
				}
			},
			submitHandler:function(){
				update_form.ajaxSubmit({
					success:function(res){
		    			alert(res.message);
		    			if(res.code == 1){
		    				
		    				document.location.href = "/backstage/cooperateManage";
		    				
		    			}else {
		    				
		    				document.location.href = document.location.href;
		    					 
						}
		    		}
		    	});
			},
			onkeyup : false

		});
	}
	
});