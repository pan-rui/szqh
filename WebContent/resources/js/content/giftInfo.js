define(function(require){
	var Site = require('../comp/init.js');
	
	$('textarea[name=description]').xheditor({});
	$('.time').click(function() {
		WdatePicker({
			startDate : '%y-%M-01 HH:mm:ss',
			dateFmt : 'yyyy-MM-dd HH:mm:ss'
		});
	});
	
	var form = $('#add_gift_form');
	$('.fn-submit1').click(function(){
		form.submit();
	});
	if (form.length){
		form.validate({
			errorClass : 'error-tip',
			errorElement : 'b',
			errorPlacement : function(error, element){
				if (element.attr('name') == 'giftName'||element.attr('name') == 'giftCode'||
						element.attr('name') == 'startTime'||element.attr('name') == 'endTime'||
						element.attr('name') == 'giftType'||element.attr('name') == 'giftStatus') {
					element.after(error);
				} 
			},
			rules :{
				giftName : {
					required: true
				},
				giftCode : {
					required: true
				},
				giftType : {
					required: true
				},
				startTime : {
					required: true
				},
				endTime : {
					required: true
				},
				giftStatus : {
					required: true
				}
			},
			messages : {
				giftName : {
					required: '请输入礼品名称'
				},
				giftCode : {
					required: '请输入礼品代码'
				},
				giftType : {
					required: '请输入礼品类型'
				},
				startTime : {
					required: '请输入礼品开始时间'
				},
				endTime : {
					required: '请输入礼品结束时间'
				},
				giftStatus : {
					required: '请输入礼品状态'
				}
			},
			onkeyup : false
		});
	}
});