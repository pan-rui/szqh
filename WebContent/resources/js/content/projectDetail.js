define(function(require){
	require('../comp/init.js');
	require('../Y-all/Y-script/Y-imgplayer.js');
	
	/*选项卡切换*/
	var tab=$('.m-tab2')
		strips=tab.find('.m-tab2Strips li'),
		tabContents=tab.find('.m-tab2Content');
	
	$('.m-tab2Strips').on('click','li',function(){
		var index=strips.index(this);
		
		if(index==-1){
			return;
		}
		
		//去掉所有的active样式
		//然后给索引位置的添加上
		strips.removeClass('active')
			.eq(index).addClass('active');
		
		tabContents.removeClass('active')
			.eq(index).addClass('active');
		
	});
	
	
	/*图片预览*/
	Y.create('ImgPlayer',{
		eleArr:'.m-picList img',
		titleInfo: 'alt',
		content:'',
		pathInfo: function(){
		  return $(this).attr('src');
		}
	});
	
});