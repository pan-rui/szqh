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
			startDate : '%y-%M-01 HH:mm:ss',
			dateFmt : 'yyyy-MM-dd HH:mm:ss'
		});
	});
	
	function formatFloat(src, pos)
	{
	    return Math.round(src*Math.pow(10, pos))/Math.pow(10, pos);
	}
	
	$('[name=online_link]').click(function(){
		var url = '/backstage/noticeCenter/changeStatus';
		var data = {'popId':$(this).attr('data'),'status':2};
		var result = $_GLOBAL.ajax(url, data);
		alert(result.message);
		document.location.href = document.location.href;
	});
	$('[name=offline_link]').click(function(){
		var url = '/backstage/noticeCenter/changeStatus';
		var data = {'popId':$(this).attr('data'),'status':3};
		var result = $_GLOBAL.ajax(url, data);
		alert(result.message);
		document.location.href = document.location.href;
	});
	$('[name=offline_privew]').click(function(){
		var url = '/help/announcement';
		var data = {'popId':$(this).attr('data'),'status':2};
		var result = $_GLOBAL.ajax(url, data);
		alert(result.message);
		document.location.href = document.location.href;
	});
});