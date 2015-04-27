define(function(require) {
	var Site = require('../comp/init.js');
	require('../content/chineseAmountExchange.js');
	require('../Y-all/Y-script/Y-imgplayer.js');
	require('../content/fileUpload.js');
	
	$('.delimg').click(function(){
		var _this = $(this);
		$.ajax({
			url: '/backstage/delImg',
			data: {
				attachmentId: $(this).parent().find('img').attr('attachmentId'),
				filePhysicalPath: $(this).parent().find('img').attr('filePhysicalPath')
			},
			type: 'get',
			datatype: 'json',
			success: function(res){
				if(res.code==1){
					
					_this.parent().remove();
					window.location.reload(window.location.href);
				}
				if(res.code==0){
					alert(res.message);
				}
			},
			error: function(msg){
				alert(msg);
			}
		});
		
	});

	
//图片上传
	var stateType;
 	liveUplodify('upfile');
 	
var _attach;

function liveUplodify(id){
    var input = $('#'+id);
    input.uploadify({
		height        : 31,
		width         : 160,
		buttonText : '<span class="u-btn u-btn-gray">选择上传图片</span>',
		fileTypeExts  : '*.jpg; *.jpeg; *.bmp;  *.png',  
		multi           : true,
		queueSizeLimit: '5',
		auto: true,
		queueID: 'queueDiv',
		swf           : '/resources/swf/uploadify.swf?tag='+new Date().getTime(),
		uploader      : '/upload/imagesUpload;jsessionid='+ $_GLOBAL.sessionID,		 	 
		fileSizeLimit : '4MB',
		scriptData: { JSESSIONID: $_GLOBAL.sessionID},
		//formData		: 'oldFilePath',
		onInit: function () {   
         },
		onUploadSuccess : function(file, data, response) {
			$('#imageUrl').empty();
            var info = $.parseJSON(data);
		    if(!info) return;
		    $('.uploadimg').attr('src',info.resData);  
		    $("#image").attr("src", info.resData); 
		    var html='';
		    html+='<li>图片地址：</li>';
		    html+='<li><em style="color:blue">'+info.resData+'</em></li>';
		    $("#imageUrl").append(html);
		},

		onUploadError : function(file, errorCode, errorMsg, errorString) {
            
		}, 
		onQueueComplete:function(queueData){
			var successs = queueData.uploadsSuccessful;
			var errors = queueData.uploadsErrored;
			var allnum = input.data('fileNum');
			if(successs >= allnum || errors > 0) {
				//submitUpload();
			}
		},
		onDialogClose : function(swfuploadifyQueue){
			input.data('fileNum',swfuploadifyQueue.queueLength);
		},
	    onCancel : function(file) {		    	
	    }
	});			
}

$('.attach').click(function(){
	Y.create('Window',{
		content: '.upload-scan',
		title: '上传扫描件',
		key: 'uplodWin'
	}).show();
	_attach = $(this);
});

$('.upcancel,.loanChecckSubmit').click(function(){
	Y.getCmp('uplodWin').close();
});	
	
	
function setImageValue()
{
	for(var i=0;i<$('.attach').length;i++)
	{
		var parentA=$('.attach').eq(i);
		var code=parentA.attr('code');
		var _imgs1 = $('.attach').eq(i).parent().parent().find('img');
		var _attachPaths="";
		for(var j = 0;j<_imgs1.length;j++){
			_attachPaths += ';'+_imgs1.eq(j).attr('src')+','+_imgs1.eq(j).attr('serverPath');
		}
		$('#pathHiddenId_'+(i+1)).val(_attachPaths);
	}
}


   
});