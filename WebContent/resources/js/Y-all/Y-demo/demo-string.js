

define(function(require, exports, module){

  require("../Y-script/Y-base.js");
  require("../Y-script/Y-string.js");
  
  $('#string').blur(function(){
      if(!$(this).val()) return;
      var str = Y.String($(this).val());
	  var param = $('#opn').find('option:selected').attr('param');
	  param=param?eval(param):[];
	  var opn = $('#opn').val();
	  $('#result').html(str[opn](param[0],param[1]));
  });

  $('#opn').find('option').click(function(){
      $('#string').blur();
  });
   
});