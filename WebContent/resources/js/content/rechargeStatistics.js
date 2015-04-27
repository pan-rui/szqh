define(function(require){
	var Site = require('../comp/init.js');
	
	$('.fn-time').click(function() {
		WdatePicker({
			startDate : '%y-%M-01 HH:mm:ss',
			dateFmt : 'yyyy-MM-dd HH:mm:ss'
		});
	});
	
});