define(function(require){
	var Site = require('../comp/init.js');
	
	$('.fn-time').click(function() {
		WdatePicker({
			startDate : '%y-%M-01',
			dateFmt : 'yyyy-MM-dd'
		});
	});
	
	/*var projectStatistics_form = $('#projectStatistics_form');
	$('.s-btn').click(function(){
		var dimension = $('[name = dimension]').val();
		if(dimension == 'month'){
			var startYear = $('[name=startTime]').val().substring(0,4);
			var endYear = $('[name=endTime]').val().substring(0,4);
			if(startYear != endYear){
				alert("只能查询同一年度的月份数据!");
				return false;
			}else{
				projectStatistics_form.submit();
			}
		}
	});*/
	
});