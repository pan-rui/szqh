define(function(require){
	var Site = require('../comp/init.js');
	
	$('textarea[name=description]').xheditor({});
	$('.time').click(function() {
		WdatePicker({
			startDate : '%y-%M-01 HH:mm:ss',
			dateFmt : 'yyyy-MM-dd HH:mm:ss'
		});
	});
	
	var form = $('#add_activity_form');
	$('.fn-submit1').click(function(){
		form.submit();
	});
	if (form.length){
		form.validate({
			errorClass : 'error-tip',
			errorElement : 'b',
			errorPlacement : function(error, element){
				if (element.attr('name') == 'activityName'||element.attr('name') == 'sendGiftCode'||
						element.attr('name') == 'startTime'||element.attr('name') == 'endTime') {
					element.after(error);
				} 
			},
			rules :{
				activityName : {
					required: true
				},
				sendGiftCode : {
					required: true
				},
				startTime : {
					required: true
				},
				endTime : {
					required: true
				}
			},
			messages : {
				activityName : {
					required: '请输入活动名称'
				},
				sendGiftCode : {
					required: '请输入活动礼品代码'
				},
				startTime : {
					required: '请输入活动开始时间'
				},
				endTime : {
					required: '请输入活动结束时间'
				}
			},
			onkeyup : false
		});
	}
});