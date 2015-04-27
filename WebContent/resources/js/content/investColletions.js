define(function(require){
	require("../comp/init");
	require("../Y-all/Y-script/Y-countdown");
	
	$('#getCode1').click(function() {
		var business = $("#business1").val();
		var mobile = $("#mobile1").val();
		var countdown = Y.getCmp('getCode1');
		sendMobile(business, mobile, countdown);
	});	
	
				
	
	
	
});





