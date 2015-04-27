

define(function(require, exports, module){

  require("../Y-script/Y-base.js");
  require("../Y-script/Y-combobox.js");
	$('#div2').Y('Combobox',{
	    key:'com1',
		width:380,
		height:300,
		autoSize: true,
		store:['test1','test2','九龙坡','渝北',{value:'ddk',text:'大渡口'},'江北']
	});
	var cou = Y.getCmp('com1');
	cou.select(function(value,text){
	    
	});
	cou.change(function(value,text){
	    $('#info').html("value:"+value+",text:"+text);
	});
	$('#btn1').click(function(){
	    cou.addItem($('#new').val());
	});
	
	
	
   
});