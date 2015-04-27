
(function($){

define(function(){

	Y.inherit('Countdown','box',{
		doInit: function(cfg){
			if(cfg.beControl) {
			    var beControl = this.toJqObj(cfg.beControl);
				this.beControl = beControl;
			}
			cfg.el = "span";
			if(!cfg.renderTo) cfg.renderTo = cfg.target;
			this.time = this.cfg.time;
			this.callBase('doInit','box',cfg);
			var main = $('<span>');
			this.el.append(main);
			this.main = main;
		},
		setTime: function(number){
		    if(typeof number == 'number' || typeof number == 'string'){
			    this.time = number;
				var timeStr= ''+this.time;
			    if(timeStr.length == 1) {
			    	timeStr = "0" + timeStr;
			    }
				this.main.find('b').html(timeStr);
			}
		    return this.time;
		},
		startTimer: function(){
		    var _this = this;
			var content = this.main;
			var cfg = this.cfg;
			var str = cfg.message;
			this.setMessage(str);
			this.setTime(cfg.time);
			if(this.timer) {
			    clearInterval(this.timer);
			}
		    var timer = setInterval(function(){
			    var time = _this.time;
		        time -= cfg.space;
			    if(time < 0) {
				    time = 0;
		        }
			    _this.setTime(time);
				if(time <= 0) {
					_this.timeover();
				}
		    },cfg.space*1000);
			this.timer = timer;
			this.on('close',function(){
				if(this.beControl) {
					_this.beControl.show();
			    }
			});
		},
		timeover:function(){
		    this.fire('timeover');
			this.doTimeover();
		},
		doTimeover: function(){
		    var timer = this.timer;
		    var _this =this;
		    if(this.cfg.autoDisappear) {
		    	this.close(0);
			}
			if(timer) clearInterval(timer);
		},
		setMessage:function(msg){
		    var cfg = this.cfg;
			var str = msg.replace(/\{0\}/g,"<b class="+cfg.numberCss+" style='width:30px;'>" + this.time + "</b>");
			this.main.html(str);
		},
		doShow :function(){
			this.callBase('doShow','box');
			this.startTimer();
			if(this.beControl){
			    this.beControl.hide();
			}
			if(this.cfg.numberCss) {
			    this.main.find('b').addClass(this.cfg.numberCss);
			}
			this.el.css('display','inline-block');
			this.el.css('display','inline-block');
		},
		doClose:function(){
		    this.callBase('doClose','box');
			if(this.beControl) {
			    this.beControl.show();
			}
		},
		doDistroy: function(){
		    this.callBase('doDistroy','box');
			if(this.timer) {
			    clearInterval(this.timer);
			}
		}
		
	});
	
	
    Y.Countdown.defaults = {
		message: '{0}秒之后可重新发送',
		numberCss:'countdown-num',
		autoSize: true,
		time: 60,
		space: 1,
		el: 'span',
		css: 'countdown-countdown',
		autoDisappear:true
	}
	
	Y.Countdown.init = function(){
	    var btns = $('.Y-countdown');
		if(!btns.length) return;
		btns.each(function(i,item){
		    var renderTo = $("<span>");
			var countdown = Y.create('Countdown',{
			    beControl: $(item),
				renderTo: renderTo,
				closeAction: 'hide',
				key: $(this).attr('key') ||  $(this).attr('id') || null,
				time: $(this).attr('time')
			});
			$(item).click(function(){
			    $(this).after(renderTo);
				countdown.show();
			});
		});
	}
	Y.Countdown.init();
	
});

})($);