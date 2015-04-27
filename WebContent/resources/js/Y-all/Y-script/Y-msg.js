
(function($){

define(function(require, exports, module){
    require("../Y-script/Y-window.js");
    Y.inherit('Msg','Window',{
	    doInit: function(cfg){
		    var _this = this;
		    this.callBase('doInit','Window',cfg);
			var wndItem = this.item.addClass(cfg.msgClass);
			this.item = $("<div>");
			var bottomBar = $('<div>').addClass(cfg.bottomClass);
			bottomBar.append($('<a>').attr({
			    href: 'javascript:;'
			}).addClass(cfg.yesClass)
			  .append($('<span>').html('确定'))
			  .click(function(){
			    _this.close();
			    if(cfg.callback) {
				    cfg.callback('yes');
				}
			}));
			if(cfg.type == 'confirm') {
			    bottomBar.append($('<a>').attr({
			        href: 'javascript:;'
			    }).css({
				    'margin-left': 10
				}).addClass(cfg.yesClass)
				  .append($('<span>').html('取消'))
				  .click(function(){
				    _this.close();
			        if(cfg.callback) {
				        cfg.callback('no');
				    }
			    }));
			}
			this.bind(this.closeBtn,'click',function(){
			    if(cfg.callback) {
				    cfg.callback('close');
				}
			});
			if(cfg.icon) {
			    var iconClass = Y.Msg.icons[cfg.icon] || '';
				if(iconClass) {
				    wndItem.prepend($('<a>').attr({
				        href: 'javascript:;'
				    }).addClass(iconClass));
				}
			}
			this.item.append(wndItem)
			         .append(bottomBar);
		}
    });
	
	$.extend(Y,{
	    alert: function(title,msg,callback){
		    $('body').Y('Msg',{
			    title: title,
				content: msg,
				callback: callback
			});
		},
		confirm: function(title,msg,callback){
		    $('body').Y('Msg',{
			    title: title,
				content: msg,
				type: 'confirm',
				callback: callback
			});
		}
	});
	
	Y.Msg.defaults = $.extend({},Y.Window.defaults,{
	    type: 'alert',
		title: '',
		simple: false,
		yesClass: 'base-btn base-btn-green',
		noClass: 'base-btn base-btn-gray',
		msgClass: 'wnd-msg-msg',
		width: 360,
		autoSize: true,
		bottomClass: 'wnd-msg-bottom',
	    bodyStyle: {
		    'text-align': 'center'
		}
	});
	Y.Msg.icons = {
	    success:'wnd-msg-success',
		fail: 'wnd-msg-fail',
		warn: 'wnd-msg-warn',
		error: 'wnd-msg-error',
		ask: 'wnd-msg-ask'
	}
});
  
})($);
