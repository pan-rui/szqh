// JavaScript Document
(function($){

define(function(require, exports, module){
    Y.inherit('Marquee','component',{
	    init: function(cfg){
			var ele,eleArr = cfg.eleArr;
			if(cfg.simple){
				ele = $(cfg.target).children();
			}else{
				ele = this.makeEle();
				ele.className = cfg.css;
				ele = $(ele);
				ele.append($(eleArr).children().addClass(cfg.childCss));
			}
			var mainEle = this.makeEle();
			this.mainEle = $(mainEle);
			this.mainEle.append(ele)
			ele.css({width:'auto'});
			if(cfg.gotoType == 'left' || cfg.gotoType == 'right'){
				this.mainEle.css('width','10000');
				ele.css({float:'left'});
			}
			$(cfg.target).append(mainEle);
			var _this = this;
			this.mainEle.mouseenter(function(){
				_this.stop();
			})
			this.mainEle.mouseleave(function(){
				_this.start();
			})
			this.mainEle.mousedown(function(e){
				$(this).attr('downsts',true);
				e = e || window.event;
				this._y = e.pageY;
				this._x = e.pageX;
				if (e.preventDefault) {
					e.preventDefault();
				} else {
					e.returnValue = false;
				}
			});
			this.mainEle.mouseup(function(){
				$(this).removeAttr('downsts');
			});
			this.mainEle.mouseleave(function(){
				$(this).removeAttr('downsts');
			});
			
			this.mainEle.mousemove(function(e){
				if(!$(this).attr('downsts') || !cfg.moveChangeAbled)return;
				e = e || window.event;
				var gotoType = cfg.gotoType;
				if(gotoType == 'left' || gotoType == 'right'){
					var num = parseInt(_this.ele.css('marginLeft')) || 0;
					if(cfg.moveChangeTypeAbled){
						if(e.pageX > this._x){
							cfg.gotoType = 'right';
						}else{
							cfg.gotoType = 'left';
						}
					}
					num += e.pageX - this._x;
					this._x = e.pageX;
					_this.makeEleMargin(num,'left',_this.eleWidth);
				}else{
					var num = parseInt(_this.ele.css('marginTop')) || 0;
					if(cfg.moveChangeTypeAbled){
						if(e.pageY > this._y){
							cfg.gotoType = 'bottom';
						}else{
							cfg.gotoType = 'top';
						}
					}
					num += e.pageY - this._y;
					this._y = e.pageY;
					_this.makeEleMargin(num,'top',_this.eleHeight);
				}
				
				if (e.preventDefault) {
					e.preventDefault();
				} else {
					e.returnValue = false;
				}
			});
			setTimeout(function(){
				_this.eleWidth = ele.width();
				_this.eleHeight = ele.height();
				if(cfg.gotoType == 'left' || cfg.gotoType == 'right')$(mainEle).css('width',_this.eleWidth * 2);
				_this.ele = ele;
				_this.cloneEle = ele.clone();
				$(mainEle).append(_this.cloneEle);
				_this.start();
			},20);
		},
		start:function(){
			var _this = this;
			this.timer = setInterval(function(){
				_this.timeout();
			},this.cfg.time);
		},
		stop:function(){
			clearInterval(this.timer);
		},
		timeout:function(){
			var cfg = this.cfg;
			var gotoType = cfg.gotoType;
			if(gotoType == 'left' || gotoType == 'right'){
				var num = parseInt(this.ele.css('marginLeft'));
				if(gotoType == 'left'){
					num -= cfg.speed;
				}else{
					num += cfg.speed;
				}
				this.makeEleMargin(num,'left',this.eleWidth);
			}else{
				var num = parseInt(this.ele.css('marginTop'));
				if(gotoType == 'top'){
					num -= cfg.speed;
				}else{
					num += cfg.speed;
				}
				this.makeEleMargin(num,'top',this.eleHeight);
			}
		},
		makeEleMargin:function(num,type,maxNum){
			var cfg = this.cfg;
			var _type = 'margin-' + type;
			this.ele.css(_type ,num);
			if(-num >= maxNum){
				this.cloneEle.after(this.ele.css(_type,cfg.margin));
				var div = this.ele;
				this.ele = this.cloneEle;
				this.cloneEle = div;
			}else if(num >= cfg.margin){
				this.ele.before(this.cloneEle.css(_type,num - maxNum));
				this.ele.css(_type,cfg.margin);
				var div = this.ele;
				this.ele = this.cloneEle;
				this.cloneEle = div;
			}
		},
		makeEle:function(){
			var div = document.createElement(this.cfg.tagName);
			div.className = this.cfg.css;
			return div;
		},
		makeChildEle:function(val){
			var div = document.createElement(this.cfg.childTagName);
			div.className = this.cfg.childCss;
			div.innerHTML = val;
			return div;
		}
	});
	Y.Marquee.defaults = $.extend({}, {
		target:'',//jq选择器
		renderTo:'',
		simple:true,//true 则支持整块移植
		tagName:'div',
		css:'',
		gotoType:'left',//滚动方向
		time:10,
		speed:2,
		moveChangeAbled:true,//能拖动内容
		moveChangeTypeAbled:true,//能拖动改变运动方向
		margin:0,//初始的margin边距
		childTagName:'div',
		childCss:''
	});	
});
  
})($);
