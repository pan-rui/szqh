

define(function(require, exports, module){

  require("../Y-script/Y-base.js");
  require("../Y-script/Y-number.js");
  
  $('#number').blur(function(){
      if(!$(this).val()) return;
      var num = Y.Number($(this).val());
	  var param = $('#opn').find('option:selected').attr('param');
	  var opn = $('#opn').val();
	  $('#result').html(num[opn](param));
  });

  $('#opn').find('option').click(function(){
      $('#number').blur();
  });
   
});