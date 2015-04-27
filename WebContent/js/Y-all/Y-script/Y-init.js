
(function($){

define(function(){
    if(Y.isCmpInit) return;
    var cmpInit = {
	    Countdown: function(){
		    var btns = $('.Y-countdown,.Y-Countdown');
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
		},
		ImgPlayer: function(){
		    var btns = $('.Y-ImgPlayer,.Y-imgPlayer');
		    if(!btns.length) return;
		    btns.each(function(i,item){
			    var ImgPlayer = Y.create('ImgPlayer',{
			        eleArr: '.Y-ImgPlayer img',
				    key: $(this).attr('key') ||  $(this).attr('id') || 'ImgPlayer' + i,
					pathInfo : $(this).attr('pathInfo'),
					titleInfo : $(this).attr('titleInfo')
			    });
		    });
		}
	}
	for(var cmp in cmpInit) {
	    if(Y[cmp]) {
		    cmpInit[cmp]();
			delete cmpInit[cmp];
		}
	}
	Y.cmpReady(function(name){
	    if(cmpInit[name]) cmpInit[name]();
	});
	Y.isCmpInit = true;
});

})($);