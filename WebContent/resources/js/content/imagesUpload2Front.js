define(function(require, exports, module){
	require('../plugins/jquery.uploadify.js');
	require('../Y-all/Y-script/Y-htmluploadify.js');
	$('#bankUrl_Upload').uploadify({
		height : 26,
		width : 100,
		buttonClass : 'u-btn u-btn-gray',
		buttonText : '选择上传图片',
		fileTypeExts : '*.png',
		multi : false,
		swf : '/resources/swf/uploadify.swf',
		uploader : '/upload/imagesUpload2Front?fileName='+$('#bankUrl_Img').attr('src')+';jsessionid='+ $_GLOBAL.sessionID,
		fileSizeLimit : '3MB',
		successTimeout : 180000,
		onUploadStart:function(){
			$('body').Y('Window',{
				content:"<span><img src='/resources/images/common/loadingScroll.gif' /><br/>上传中，请稍后...</span>",
				key:'lodding',
				simple:true
			});
		},
		onUploadSuccess : function(file, data, response) {
			handdleResult(data, "bankUrlUrl_imgcontainer", "bankUrl_Img", "bankUrl_ImgLink");
//			window.location.href="/backstage/uploadImages2Front";
			location.reload();
		    Y.getCmp('lodding').close();
		},
		onUploadError : function(file, errorCode, errorMsg,
				errorString) {
			Y.getCmp('lodding').close();
			alert("图片上传失败！异常信息：" + errorString);
		},
		onCancel : function(file) {
			Y.getCmp('lodding').close();
			alert("已取消！");
		}
	});
	$('#index5Url_Upload').uploadify({
		height : 26,
		width : 100,
		buttonClass : 'u-btn u-btn-gray',
		buttonText : '选择上传图片',
		fileTypeExts : '*.jpg',
		multi : false,
		swf : '/resources/swf/uploadify.swf',
		uploader : '/upload/imagesUpload2Front?fileName='+$('#index5Url_Upload_Img').attr('alt')+';jsessionid='+ $_GLOBAL.sessionID,
		fileSizeLimit : '3MB',
		successTimeout : 180000,
		onUploadStart:function(){
			$('body').Y('Window',{
				content:"<span><img src='/resources/images/common/loadingScroll.gif' /><br/>上传中，请稍后...</span>",
				key:'lodding',
				simple:true
			});
		},
		onUploadSuccess : function(file, data, response) {
			handdleResult(data, "index5Url_Upload_Img", "index5Url_Upload_Img", "index5Url_Upload_ImgLink");
//			window.location.href="/backstage/uploadImages2Front";
			location.reload();
		    Y.getCmp('lodding').close();
		},
		onUploadError : function(file, errorCode, errorMsg,
				errorString) {
			Y.getCmp('lodding').close();
			alert("图片上传失败！异常信息：" + errorString);
		},	
		onCancel : function(file) {
			Y.getCmp('lodding').close();
			alert("已取消！");
		}
	});
	$('#index4Url_Upload').uploadify({
		height : 26,
		width : 100,
		buttonClass : 'u-btn u-btn-gray',
		buttonText : '选择上传图片',
		fileTypeExts : '*.jpg',
		multi : false,
		swf : '/resources/swf/uploadify.swf',
		uploader : '/upload/imagesUpload2Front?fileName='+$('#index4Url_Upload_Img').attr('alt')+';jsessionid='+ $_GLOBAL.sessionID,
		fileSizeLimit : '3MB',
		successTimeout : 180000,
		onUploadStart:function(){
			$('body').Y('Window',{
				content:"<span><img src='/resources/images/common/loadingScroll.gif' /><br/>上传中，请稍后...</span>",
				key:'lodding',
				simple:true
			});
		},
		onUploadSuccess : function(file, data, response) {
			handdleResult(data, "index4Url_Upload_Img", "index4Url_Upload_Img", "index4Url_Upload_ImgLink");
//			window.location.href="/backstage/uploadImages2Front";
			location.reload();
		    Y.getCmp('lodding').close();
		},
		onUploadError : function(file, errorCode, errorMsg,
				errorString) {
			Y.getCmp('lodding').close();
			alert("图片上传失败！异常信息：" + errorString);
		},	
		onCancel : function(file) {
			Y.getCmp('lodding').close();
			alert("已取消！");
		}
	});
	$('#index1Url_Upload').uploadify({
		height : 26,
		width : 100,
		buttonClass : 'u-btn u-btn-gray',
		buttonText : '选择上传图片',
		fileTypeExts : '*.jpg',
		multi : false,
		swf : '/resources/swf/uploadify.swf',
		uploader : '/upload/imagesUpload2Front?fileName='+$('#index1Url_Upload_Img').attr('alt')+';jsessionid='+ $_GLOBAL.sessionID,
		fileSizeLimit : '3MB',
		successTimeout : 180000,
		onUploadStart:function(){
			$('body').Y('Window',{
				content:"<span><img src='/resources/images/common/loadingScroll.gif' /><br/>上传中，请稍后...</span>",
				key:'lodding',
				simple:true
			});
		},
		onUploadSuccess : function(file, data, response) {
			handdleResult(data, "index1Url_Upload_imgcontainer", "index1Url_Upload_Img", "index1Url_Upload_ImgLink");
//			window.location.href="/backstage/uploadImages2Front";
			location.reload();
		    Y.getCmp('lodding').close();
		},
		onUploadError : function(file, errorCode, errorMsg,
				errorString) {
			Y.getCmp('lodding').close();
			alert("图片上传失败！异常信息：" + errorString);
		},
		onCancel : function(file) {
			Y.getCmp('lodding').close();
			alert("已取消！");
		}
	});
	$('#index2Url_Upload').uploadify({
		height : 26,
		width : 100,
		buttonClass : 'u-btn u-btn-gray',
		buttonText : '选择上传图片',
		fileTypeExts : '*.jpg',
		multi : false,
		swf : '/resources/swf/uploadify.swf',
		uploader : '/upload/imagesUpload2Front?fileName='+$('#index2Url_Upload_Img').attr('alt')+';jsessionid='+ $_GLOBAL.sessionID,
		fileSizeLimit : '3MB',
		successTimeout : 180000,
		onUploadStart:function(){
			$('body').Y('Window',{
				content:"<span><img src='/resources/images/common/loadingScroll.gif' /><br/>上传中，请稍后...</span>",
				key:'lodding',
				simple:true
			});
		},
		onUploadSuccess : function(file, data, response) {
			handdleResult(data, "index2Url_Upload_imgcontainer", "index2Url_Upload_Img", "index2Url_Upload_ImgLink");
//			window.location.href="/backstage/uploadImages2Front";
			location.reload();
		    Y.getCmp('lodding').close();
		},
		onUploadError : function(file, errorCode, errorMsg,
				errorString) {
			Y.getCmp('lodding').close();
			alert("图片上传失败！异常信息：" + errorString);
		},
		onCancel : function(file) {
			Y.getCmp('lodding').close();
			alert("已取消！");
		}
	});
	$('#index3Url_Upload').uploadify({
		height : 26,
		width : 100,
		buttonClass : 'u-btn u-btn-gray',
		buttonText : '选择上传图片',
		fileTypeExts : '*.jpg',
		multi : false,
		swf : '/resources/swf/uploadify.swf',
		uploader : '/upload/imagesUpload2Front?fileName='+$('#index3Url_Upload_Img').attr('alt')+';jsessionid='+ $_GLOBAL.sessionID,
		fileSizeLimit : '3MB',
		successTimeout : 180000,
		onUploadStart:function(){
			$('body').Y('Window',{
				content:"<span><img src='/resources/images/common/loadingScroll.gif' /><br/>上传中，请稍后...</span>",
				key:'lodding',
				simple:true
			});
		},
		onUploadSuccess : function(file, data, response) {
			handdleResult(data, "index3Url_Upload_imgcontainer", "index3Url_Upload_Img", "index3Url_Upload_ImgLink");
//			window.location.href="/backstage/uploadImages2Front";
			location.reload();
		    Y.getCmp('lodding').close();
		},
		onUploadError : function(file, errorCode, errorMsg,
				errorString) {
			Y.getCmp('lodding').close();
			alert("图片上传失败！异常信息：" + errorString);
		},
		onCancel : function(file) {
			Y.getCmp('lodding').close();
			alert("已取消！");
		}
	});
	function handdleResult(data, containerId, imgId, linkId, storePathId){
		var result = "";
		if (data.indexOf("pre") > 0) {
			var startIndex = data.indexOf(">") + 1;
			var endIndex = data.length - 6;
			data = data.substring(startIndex, endIndex);
			data = eval("(" + data + ")");
			if (data.code == 0) {
				var imgUrl = data.resData;
				$("#"+imgId).attr("src", imgUrl);
				//$("#"+linkId).attr("href", imgUrl);
				$("#"+storePathId).val(imgUrl);
				
			} else {
				result = "<span style='color:red;'>"+data.resData+"</span>"
			}
		} else {
			data = eval("(" + data + ")");
			if (data.code == 0) {
				var imgUrl = data.resData;
				$("#"+imgId).attr("src", imgUrl);
				//$("#"+linkId).attr("href", imgUrl);
				$("#"+storePathId).val(imgUrl);
				
				if($('#'+linkId).data('jqzoom')){
					$('#'+linkId).data('jqzoom').largeimage.node.src=imgUrl;
				};
				
				$("#"+imgId).parents('.item').css('height',140);
			} else {
				result = "<span style='color:red;'>"+data.resData+"</span>"
			}
		}
		$("#"+containerId).append(result);
		
		$('#' + containerId).show(1500);
		setTimeout(function(){ $('#'+containerId).height(75);},1000);
	}
});