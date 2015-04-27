define(function(require) {
	require("../comp/init");
	require("../Y-all/Y-script/Y-countdown");
	require('../Y-all/Y-script/Y-imgplayer.js');
	require('../lib/calculator2.js');

	$('#a001').removeClass("index-bg");
	$('#a002').addClass("nemu-bg");
	$('#a003').removeClass("nemu-bg");
	$('#a004').removeClass("nemu-bg");
	$('#a005').removeClass("nemu-bg");
	$('#getCode1').click(function() {
		var business = $("#business1").val();
		var mobile = $("#mobile1").val();
		var countdown = Y.getCmp('getCode1');
		sendMobile(business, mobile, countdown);
	});

	$('.fm-license').find('*').css('position', 'static');
	$('#btn_license').click(function() {
		$('body').Y('Window', {
			simple: true,
			content: '.fm-license',
			closeEle: '.close,[type=reset]'
		});
	});
	
	
	 /*获取queryString传入的激活页面*/
    var search=location.search,
    	rqueryItm=/([^=&\?]*)=([^&]*)?/g,
    	ret={},
    	match;
    
    //转换为json
    while(match=rqueryItm.exec(search)){
    	ret[match[1]]=match[2];
    }

	/*选项卡切换*/
	var tab = $('.m-tab2'),
		strips = tab.find('.m-tab2Strips li'),
		tabContents = tab.find('.m-tab2Content');

	$('.m-tab2Strips').on('click', 'li', function() {
		var index = strips.index(this);

		if (index == -1) {
			return;
		}

		//去掉所有的active样式
		//然后给索引位置的添加上
		strips.removeClass('active')
			.eq(index).addClass('active');

		tabContents.removeClass('active')
			.eq(index)
			.addClass('active');
	});
	
	//激活相应的选项卡
    if(!ret.tab){
    	strips.eq(0).trigger('click');
    }else{
    	$('#'+ret.tab).trigger('click');
    }

	/*图片预览*/
	Y.create('ImgPlayer', {
		eleArr: '.m-picList img',
		titleInfo: 'alt',
		content: '',
		pathInfo: function() {
			return $(this).attr('src');
		}
	});


	function sysDate() {
		var url = '/boot/currentTime.htm';
		var result = $_GLOBAL.ajax(url);
		//alert(result.message);
		var gloabTime = new Date().getTime();
		if (result.code == 1) {
			gloabTime = result.TimeLong;
			//alert(":"+result.TimeLong);
			//alert(":"+result.TimeStr);
		}
		return gloabTime;
	}
	var currentDate = sysDate(); //当前时间
	function showLeftTimeFun() {
		var deadTime = $('.getDeadTime').val(); //投资结束时间
		currentDate += 1000; //当前时间
		var startTime = $('.getinvestAvlDate').val(); //起投时间
		var effectiveTime = $('.geteffectiveDate').val(); //成立时间
		//var finishDate=$(this).parents('.leftTime').find('.tradeFinishDate').val();还款时间
		var tradeValue = $('.tradeValue').val(); //交易状态
		var leftTime = (deadTime - currentDate) / 1000;
		var
			timefield = $('.showLeftTime', this.parentNode);

		if (startTime < currentDate) {

			if (tradeValue == 1 && leftTime > 0) {
				var days = Math.floor(leftTime / 86400);
				var hours = Math.floor((leftTime % 86400) / 3600);
				var minutes = Math.floor(((leftTime % 86400) % 3600) / 60);
				var seconds = Math.floor(((leftTime % 86400) % 3600) % 60);
				var result = "剩余时间:<br/>" + days + "<span>天</span>" + hours + "<span>时</span>" + minutes + "<span>分</span> " + seconds + "<span>秒</span>";
				//console.log($('.showLeftTime',this.parentNode).length)
				timefield.html(result);

			} else if (tradeValue == 3 || tradeValue == 7) {
					timefield.html("合同已终止");

			} else if (tradeValue == 2 || tradeValue == 5 || tradeValue == 8){
					timefield.html(effectiveTime + "合同成立");
			
			} else if (tradeValue == 6) {
				timefield.html("合同待成立");
			} else {
				timefield.html("已过投资时间");
			}


		} else {
			var bgTime = (startTime - currentDate) / 1000;
			var days_a = Math.floor(bgTime / 86400);
			var hours_a = Math.floor((bgTime % 86400) / 3600);
			var minutes_a = Math.floor(((bgTime % 86400) % 3600) / 60);
			var seconds_a = Math.floor(((bgTime % 86400) % 3600) % 60);
			var result_a = "距可投资：</br>" + days_a + "<span>天</span>" + hours_a + "<span>时</span>" + minutes_a + "<span>分</span> " + seconds_a + "<span>秒</span>";
			//timefield.text(2222);
			timefield.html(result_a);
		}



	}


	setInterval(showLeftTimeFun, 1000);
});