

define(function(require, exports, module){

  require("../Y-script/Y-base.js");
  require("../Y-script/Y-window.js");
  function m(){
     return $('#isModal').attr('checked')?true:false;
  }
  $('#btn1').click(function(){
	  var wnd = new Y.Window({
	      renderTo:'body',
		  content:$('#str').val(),
		  simple:false,
		  modal: m(),
		  width:300,
		  title:'简单内容'
	  });
	  wnd.show();
  });
  $('#btn2').click(function(){
	  $('body').Y('Window',{
		  content:'#popdiv',
		  simple:false,
		  contentClone:true,
		  modal: m(),
		  key:'modalwnd',
		  title:'复杂内容'
	  });
	  Y.getCmp('modalwnd').bind(Y.getCmp('modalwnd').wnd.find('.fn-close'),'click',function(){
	      Y.getCmp('modalwnd').close();
	  });
  });
 $('#btn3').click(function(){
	  $('body').Y('Window',{
		  content:'#popdiv',
		  //contentClone:true,
		  simple:true,
		  modal: m(),
		  closeEle:'.fn-close'
	  });
  }); 
  $('#popagain').click(function(){
      $('body').Y('Window',{
	      content:'测试'
	  });
  });

 
   
});