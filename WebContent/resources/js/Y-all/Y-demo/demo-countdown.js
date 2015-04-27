

define(function(require, exports, module){

  require("../Y-script/Y-base.js");
  require("../Y-script/Y-countdown.js");

	$('#countdown').click(function(){
	    $('#countdownEle').Y('Countdown',{
	        beControl: '#countdown',
			autoDisappear:true,
			time:15,
			timeover:function(){
			    alert('timeover');
			}
	    });
	});
    
});