/**
 * app新版本上传
 */

define(function(require, exports, module) {
	var Site = require('../comp/init.js');
	require('../plugins/jquery.uploadify.js');
	require('../Y-all/Y-script/Y-htmluploadify.js');
	
	var uploadHost = $("#uploadHost").val();
	/*app Adroid版本上传*/
	$('#appUpLoadUrl_Android').uploadify({
		buttonClass : 'u-btn u-btn-gray',
		buttonText : '上传app最新版本',
		fileTypeExts : '*.apk',
		multi : false,
		swf : '/resources/swf/uploadify.swf',
		uploader : '/upload/imagesUpload;jsessionid='+ $_GLOBAL.sessionID,
		fileSizeLimit : '50MB',
		successTimeout : 180000,
		onUploadStart:function(){
			$('body').Y('Window',{
				content:"<span><img src='/resources/images/common/loadingScroll.gif' /><br/>上传中，请稍后...</span>",
				key:'lodding',
				simple:true
			});
		},
		onUploadSuccess : function(file, data, response) {
			handdleResult(data, "appUpLoadUrl_container",  "param_value");
			 Y.getCmp('lodding').close();
		},
		onUploadError : function(file, errorCode, errorMsg,
				errorString) {
			 Y.getCmp('lodding').close();
			alert("资料上传失败！异常信息：" + errorString);
		},
		onCancel : function(file) {
			 Y.getCmp('lodding').close();
			alert("已取消！");
		}
	});
	
	/*app IOS版本上传*/
	$('#appUpLoadUrl_IOS').uploadify({
		buttonClass : 'u-btn u-btn-gray',
		buttonText : '上传app最新版本',
		fileTypeExts : '*.ipa',
		multi : false,
		swf : '/resources/swf/uploadify.swf',
		uploader : '/upload/imagesUpload;jsessionid='+ $_GLOBAL.sessionID,
		fileSizeLimit : '50MB',
		successTimeout : 180000,
		onUploadStart:function(){
			$('body').Y('Window',{
				content:"<span><img src='/resources/images/common/loadingScroll.gif' /><br/>上传中，请稍后...</span>",
				key:'lodding',
				simple:true
			});
		},
		onUploadSuccess : function(file, data, response) {
			handdleResult(data, "appUpLoadUrl_container",  "param_value");
			 Y.getCmp('lodding').close();
		},
		onUploadError : function(file, errorCode, errorMsg,
				errorString) {
			 Y.getCmp('lodding').close();
			alert("资料上传失败！异常信息：" + errorString);
		},
		onCancel : function(file) {
			 Y.getCmp('lodding').close();
			alert("已取消！");
		}
	});
	
	function handdleResult(data, containerId, storePathId){
		var result = "";
		if (data.indexOf("pre") > 0) {
			var startIndex = data.indexOf(">") + 1;
			var endIndex = data.length - 6;
			data = data.substring(startIndex, endIndex);
			data = eval("(" + data + ")");
			if (data.code == 0) {
				var imgUrl = data.resData;
				$("#"+storePathId).val(imgUrl);
			} else {
				result = "<span style='color:red;'>"+data.resData+"</span>"
			}
		} else {
			data = eval("(" + data + ")");
			if (data.code == 0) {
				var imgUrl =data.resData;
				
				$("#"+storePathId).val(imgUrl);
	
			} else {
				result = "<span style='color:red;'>"+data.resData+"</span>"
			}
		}
		$("#"+containerId).append(result);
		
	}
	

	var systemParam_form = $('#systemParam_form');
	$('.submit_form').click(function() {
        systemParam_form.submit();
	})
	if (systemParam_form.length) {
        systemParam_form.validate({
			errorClass : 'error-tip',
			errorElement : 'b',
			submitHandler : function() {
                systemParam_form.ajaxSubmit({
					success : function(res) {
						alert(res.message);
                        window.location.href = "/backstage/sysParamManage";
					}
				});
			},

			rules : {

                param_name: {
                    required: true
                }
            },
			messages : {
                param_name : {
					required : '请输入参数名称'
				}
			},
			onkeyup : false

		});

	}

	
});