define(function(require) {
	var Site = require('../comp/init.js');
	require('../Y-all/Y-script/Y-imgplayer.js');
	require('../content/imagesUpload2Front.js');
	 Y.create('ImgPlayer',{
			eleArr:'#bankUrl_Img',
			titleInfo: 'alt',
			content:'',
			pathInfo: function(){
			  return $(this).attr('src');
			}
		});
	
});