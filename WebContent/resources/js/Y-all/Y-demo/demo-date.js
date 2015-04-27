

define(function(require, exports, module){

  require("../Y-script/Y-base.js");
  require("../Y-script/Y-Date.js");
  
  $('#date').blur(function(){
      if(!$(this).val()) return;
      var date = Y.Date($(this).val());
	  var param = $('#opn').find('option:selected').attr('param');
	  param=param?eval(param):[];
	  var opn = $('#opn').val();
	  var result;
	  switch(opn) {
	      case 'isLeapYear':
		      result = date.isLeapYear(param[0],param[1],param[2]).toString();
		  break;
		  case 'add':
		      result = Y.Date(date.add(param[0],param[1],param[2])).toString();
		  break;
	  }
	   
	  $('#result').html(result);
  });

  $('#opn').find('option').click(function(){
      $('#date').blur();
  });
   
});