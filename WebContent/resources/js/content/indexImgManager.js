/**
 * app轮播图
 */

define(function(require, exports, module) {
	var Site = require('../comp/init.js');
	require('../plugins/jquery.uploadify.js');
	require('../Y-all/Y-script/Y-htmluploadify.js');
	
	var uploadHost = $("#uploadHost").val();
	/*app 上传轮播图*/
	$('#appUpLoadUrl_index').uploadify({
		buttonClass : 'u-btn u-btn-gray',
		buttonText : '上传新的轮播图',
		fileTypeExts  : '*.jpg; *.jpeg; *.bmp;  *.png', 
		multi : false,
		swf : '/resources/swf/uploadify.swf',
		uploader : '/upload/imagesUpload;jsessionid='+ $_GLOBAL.sessionID,
		fileSizeLimit : '10MB',
		successTimeout : 180000,
		onUploadStart:function(){
			$('body').Y('Window',{
				content:"<span><img src='/resources/images/common/loadingScroll.gif' /><br/>上传中，请稍后...</span>",
				key:'lodding',
				simple:true
			});
		},
		onUploadSuccess : function(file, data, response) {
			handdleResult(data, "upLoadUrl_container",  "content");
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
	

	$("#add_form").click(function(){
		var title = $('[name=title]').val();
		var content = $('[name=content]').val();
		var remark   = $('[name=remark]').val();
		var rem1=$('[name=rem1]').val();
		var status=$("input[type='radio']:checked").val();
		if(!content.length>0){
			alert("请上传图片");
			return;
		}
		$.ajax({
			url : '/backstage/addImgSubmit',
			type : 'post',
			dataType : 'json',
			data : {
				title : title,
				content : content,
				remark   : remark,
				rem1	:rem1,
				status :status
			},
			success : function(res){
				if(res.code=="1"){
					alert(res.message);
					document.location.href = '/backstage/appIndexImgManager';
				}else{
					alert(res.message);
				}
				
			}
		});
	})
	
	
	$("#update_form").click(function(){
		var popId =$('[name=popId]').val();
		var title = $('[name=title]').val();
		var content = $('[name=content]').val();
		var remark   = $('[name=remark]').val();
		var rem1=$('[name=rem1]').val();
		var type=$('[name=type]').val();
		var status=$("input[type='radio']:checked").val();
		
		if(!content.length>0){
			alert("请上传图片");
			return;
		}
		$.ajax({
			url : '/backstage/upDateImgSubmit',
			type : 'post',
			dataType : 'json',
			data : {
				popId:popId,
				title : title,
				content : content,
				remark   : remark,
				rem1	:rem1,
				type:type,
				status:status
			},
			success : function(res){
				if(res.code=="1"){
					alert(res.message);
					document.location.href = '/backstage/appIndexImgManager';
				}else{
					alert(res.message);
				}
				
			}
		});
	})
	
	$(".del_img").click(function(){
		var id = this.id;
		$.ajax({
			url : '/backstage/deleteImg',
			type : 'post',
			dataType : 'json',
			data : {
				popId : id
			},
			success : function(res){
				if(res.code=="1"){
					alert(res.message);
					document.location.href = '/backstage/appIndexImgManager';
				}else{
					alert(res.message);
				}
				
			}
		});
	})

	
});