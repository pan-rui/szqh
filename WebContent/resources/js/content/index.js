
define(function(require, exports, module) {
  
	  require('../comp/init.js');
	  require("../Y-all/Y-script/Y-base.js");
	  require("../Y-all/Y-script/Y-marquee.js");
	
	  var v = Y.create('Marquee',{
		target: '#marquee',//jq选择字符串 或者字符串数组
		time:20,
		gotoType:'left',
		speed:1
	  });
	  $("#bannerBg1").Slide({//banner
          effect:"fade",
          speed:600,
          timer:4000
      });
  
});