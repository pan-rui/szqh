define(function(require) {
	var Site = require('../comp/init.js');
	require('../Y-all/Y-script/Y-window.js');










    //----tab 切换------------------------------
	    $("#tradeinfo").click(function(){
	        $("#touzhidiv").hide();
	        $("#tradediv").show();
	        $('#touziinfo').removeClass('curr');
	        $(this).addClass('curr');
		});
		
		$("#touziinfo").click(function(){
		    $("#tradediv").hide();
	        $("#touzhidiv").show();
	        $('#tradeinfo').removeClass('curr');
	        $(this).addClass('curr');
		});
		
		
	
});
