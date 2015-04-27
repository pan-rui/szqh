/**
 * @fileoverview tab组件，支持click, hover事件
 * @author yangle | yorsal.coms
 * @created  2012-05-09
 * @updated  2012-05-09
 */


define(function(require, exports, module) {
  
  require('../comp/init.js');

  	$(".hothouse").find(".m-tab-hd").find("li").click(function(){
  		$(this).find("a").addClass("cur");
  		var index = $(this).index();
  		$(".house-info").find(".fn-clear").get(index).style.display = "block";
  		$.each($(this).siblings(),function(i,item){
  			var index = $(item).index();
  			$(".house-info").find(".fn-clear").get(index).style.display = "none";
  			$(item).find("a").removeClass("cur");
  		})

  	});
  
});