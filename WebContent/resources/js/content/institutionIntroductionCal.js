define(function(require){
	$('#count').click(function() {
		var capital = $("#capital").val();
		checkNum(capital);
		var rate = $("#rate").val();
		checkNum(rate);
		var deadline = $("#deadline").val();
		checkNum(deadline);
		var earnings=capital*(rate/100)*deadline/12;
		
		document.getElementById('earnings').innerHTML=Math.round(earnings*100)/100+" 元";
		
		
	    
		
	});	
	
	 function checkNum(control)
     {
		 if(control!="") {  
			 if(isNaN(control)){
				 alert(" 请认真输入数字！ "); 
				 update(); 
			 }else{
				 return true; 
			 } 
			 
		 }else{
			 alert(" 请输入数字！ "); 
			 update();
		 }

		
     }
	
});