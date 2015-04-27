

define(function(require) {
	
	var Site = require('../comp/init.js'); 
	
	
	function sysDate(){
		var url = '/boot/currentTime.htm';
		var result = $_GLOBAL.ajax(url);
		//alert(result.message);
		var gloabTime= new Date().getTime();
		if(result.code==1){
			gloabTime = result.TimeLong;
			alert(":"+result.TimeLong);
			//alert(":"+result.TimeStr);
		}
		return gloabTime;
	}

	function showLeftTimeFun(){
		$('.getDeadTime').each(function(){
			var me=$(this);
			var deadTime=me.val();//投资结束时间
			var currentDate=me.parents('.leftTime').find('.currentDate').val();//当前时间
			var startTime=me.parents('.leftTime').find('.getinvestAvlDate').val();//起投时间
			var effectiveTime=me.parents('.leftTime').find('.geteffectiveDate').val();//成立时间
			//var finishDate=$(this).parents('.leftTime').find('.tradeFinishDate').val();还款时间
			var tradeValue=me.parents('.leftTime').find('.tradeValue').val();//交易状态

			var leftTime=(deadTime-currentDate)/1000;
			var percent=me.parents('.leftTime').find('.per').val(),
				timefield=$('.showLeftTime',this.parentNode);
			
			$(this).parents('.leftTime').find('.currentDate').val(parseFloat(currentDate)+1000);//当前时间，每调用一次方法加1000毫秒

			if(startTime<currentDate){
				
				if(parseFloat(leftTime)>0 && percent!="100%"){
					var days = Math.floor(leftTime/86400); 
		 			var hours = Math.floor((leftTime%86400)/3600); 
		 			var minutes = Math.floor(((leftTime%86400)%3600)/60);  
		 			var seconds = Math.floor(((leftTime%86400)%3600)%60);  
		 			var result= days+"<span>天</span>"+hours+"<span>时</span>"+ minutes+ "<span>分</span> "+seconds+"<span>秒</span>";  
		 			//console.log($('.showLeftTime',this.parentNode).length)
		 			timefield.html(result);
		 			//console.log(timefield[0].nodeName)
		 			me.parents('.leftTime').find('.button a').attr('class','');
		 			//console.log(2)
				}else if(effectiveTime!=""){
					if(tradeValue==3 || tradeValue==7){
						timefield.html("合同已终止");
						
					}else{
						timefield.html(effectiveTime+"合同成立");
					}
				}else if(percent=="100%"){
					timefield.html("合同待成立");
				}
				else{
					timefield.html("已过投资时间");
				}

				
			}else{
				var bgTime=(startTime-currentDate)/1000;
				var days_a = Math.floor(bgTime/86400); 
	 			var hours_a = Math.floor((bgTime%86400)/3600); 
	 			var minutes_a = Math.floor(((bgTime%86400)%3600)/60);  
	 			var seconds_a = Math.floor(((bgTime%86400)%3600)%60);  
	 			var result_a=days_a+"<span>天</span>"+hours_a+"<span>时</span>"+ minutes_a+ "<span>分</span> "+seconds_a+"<span>秒</span>";  
	 			//timefield.text(2222);
	 			timefield.html(result_a);
			}
	

			
		});
		
	}
	
	//alert(sysDate());
	setInterval(showLeftTimeFun,1000);
	
	

	
	
	
	
 
});