

define(function(require, exports, module){
  require("../Y-script/Y-base.js");
  require("../Y-script/Y-tip.js");
    function a(){
	    return $('#align').val();
	}
    $('#tip').click(function(){
      var el = $(this);
	  var tip = Y.create('Tip',{
	      target: el,
		  content:'Tip提示',
		  align:a()
	  });
	  tip.show();
    }); 
	$('#tooltip').Y('ToolTip',{
	    key: 'tooltip1',
		content:'ToolTip提示'
	});
	$('#align').change(function(){
	    Y.getCmp('tooltip1').setAlign($(this).val());
		Y.getCmp('translatetip1').setAlign($(this).val());
		Y.getCmp('translatetip2').setAlign($(this).val());
		Y.getCmp('translatetip3').setAlign($(this).val());
		Y.getCmp('rareword1').setAlign($(this).val());
	});
	
	$('#rareword').Y('RareWordTip',{
		showEle: '#open',
		key:'rareword1'
	});
	
	var arr=[];
	for(var i=0;i<100;i++){
		var m=1+Math.random()*15|0;
		var str='';
		while(m--){
			str+='qwertyuiopasdfghjklzxcvbnm'.charAt(Math.random()*26|0);
		}
		arr.push(str);
	}
	Y.create('SearchTip',{
		target:'#searchTip',
	  	content:'<div>div</div>',
	  	simple:true,
		data : arr.sort(),
		spacing: 5,
		showNumbers: 5,
		fontColor: "",
		clickAfter:function(ele,val){
		}
	});
	Y.create('StartTip',{
		target:'#starttip',
	  	content:'<div>测试</div>',
		showInfo:function(){
			return $(this).attr('defaultValue')
		},
	  	simple:true
	});
    Y.create('TranslateTip',{
	    key:'translatetip1',
		target:'#fmoney',
	  	content:'<div></div>',
	  	simple:true,
		translateType:['fmoney',4,2," "],//['fmoney',3,2];//第一个数字是格几位数字加逗号默认3 第二个数字是保留的小数位数 默认2 间隔符号 默认空格
		spacing:5,
		align:'top'
	});
	Y.create('TranslateTip',{
	    key:'translatetip2',
		target:'#digitUppercase',
	  	content:'<div>div</div>',
	  	simple:true,
		translateType:'digitUppercase',//转大写
		spacing:5,
		align:'bottom'
	});
	Y.create('TranslateTip',{
	    key:'translatetip3',
		target:'#customMade',
	  	content:'<div>div</div>',
	  	simple:true,
		translateType:'customMade',//xxxx xxxx xxxx xxxx
		spacing:5,
		length:16,
		align:'bottom'
	});
});