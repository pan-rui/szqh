
define(function(require, exports, module) {
  
  require('../comp/init.js');
  require('../Y-all/Y-script/Y-window.js');
  
  $('#a001').removeClass("index-bg"); 
	 $('#a002').removeClass("nemu-bg");  
	 $('#a003').addClass("nemu-bg");  
	 $('#a004').removeClass("nemu-bg");  
	 $('#a005').removeClass("nemu-bg"); 

  $("#controlYellow").removeClass();
  $("#arrowIndex").click(function(){
	  if( "up-arrow" == $("#arrowIndex").attr("class")){
		  $(".yellow-bg").removeClass();
		  $(this).text("展开");
		  $("#levelShow").hide();
		  $("#arrowIndex").attr("class", "down-arrow"); 
		 
	  }else{
		  $("#controlYellow").attr("class", "fn-condition time-cond yellow-bg");  
		  $("#levelShow").show();
		  $(this).text("收起");
		  $("#arrowIndex").attr("class", "up-arrow");
		  $('.turn-this-dl').hide().show();
		  $('.turn-this-dl').find('input').each(function(){
			  this.value = this.value;
		  })
	  }
	  
  });
  
});