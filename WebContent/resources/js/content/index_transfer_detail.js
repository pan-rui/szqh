define(function(require) {
	require("../comp/init");
	require("../Y-all/Y-script/Y-countdown");
	require('../Y-all/Y-script/Y-imgplayer.js');

	/*选项卡切换*/
	var tab = $('.m-tab3'),
		strips = tab.find('.m-tab3Strips li'),
		tabContents = tab.find('.m-tab3Content');

	$('.m-tab3Strips').on('click', 'li', function() {
		var index = strips.index(this);

		if (index == -1) {
			return;
		}

		//去掉所有的active样式
		//然后给索引位置的添加上
		strips.removeClass('curr')
			.eq(index).addClass('curr');

		tabContents.hide()
			.eq(index).show();
	});


});