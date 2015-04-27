define(function(require) {
	var Site = require('../comp/init.js');
	require('../content/fileUpload.js');
	require('../plugins/jquery.box.js');
	require('../plugins/jquery.combobox.js');
	require('../plugins/jquery.window.js');
	
	$('.fn-time').click(function() {
		WdatePicker({
			startDate : '%y-%M-01 HH:mm:ss',
			dateFmt : 'yyyy-MM-dd HH:mm:ss'
		});
	});
	/*
	
	$('#downLoand').click(function (){
		
		$('body').Y('Window',{
			content:"<span><img src='/resources/images/common/loadingScroll.gif' /><br/>系统处理中，请您耐心等候...</span>",
			key:'lodding',
			simple:true
		});
		
		 $(_this).parents('form').submit();
		 window.location.reload();
	 
	});
	*/
	
	
	var downLoand_form = $('#downLoand'),
		timer;
	
	$('#downLoand').click(function() {
		
		$('body').Y('Window',{
			content:"<span><img src='/resources/images/common/loadingScroll.gif' /><br/>系统处理中，请您耐心等候...</span>",
			key:'lodding',
			simple:true
		});
		
		//window.location.reload();
        //downLoand_form.submit();
		if(timer){
        	clearTimeout(timer)
        	timer=null;
        }
        timer=setTimeout(hideMask,1000);//等待2秒
	})
	
	
	function hideMask(){
		Y.getCmp('lodding').close();
	}
	
	
	var queryform = $('#queryform');
	$('#queryButton').click(function() {
		
		$('body').Y('Window',{
			content:"<span><img src='/resources/images/common/loadingScroll.gif' /><br/>系统处理中，请您耐心等候...</span>",
			key:'lodding',
			simple:true
		});
		queryform.attr('action', '/backstage/report/queryResult'); 
		queryform.attr('target', '_self'); 
		queryform.submit();
	})
 
	 
	
	
	
});