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

	var editor= KindEditor.create('textarea[name=content]',{
		uploadJson : '/upload/imagesUpload.htm;jsessionid='+ $_GLOBAL.sessionID,
        fileManagerJson : '/upload/imagesUpload.htm;jsessionid='+ $_GLOBAL.sessionID,
        allowFileManager : true,
        afterBlur: function(){this.sync();}
	});
	//$('input[name=realName]').attr("disabled", true);
	var addForm=$('#add_pop_form');
	$('.fn-submit1').click(function(){
		addForm.submit();
		alert('发布公告成功！');
	});
});