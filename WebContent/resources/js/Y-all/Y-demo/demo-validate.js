

define(function(require, exports, module){

  require("../Y-script/Y-base.js");
  require("../Y-script/Y-validate.js");
  require("../../lib/jquery.validate.js")($);

  var v = Y.create('Validate',{
      target:'#form1',
	  submitHandler:function(){
		  v.reSet();
	  },
	  rules:{
	      name1:{
		      required:true,
			  email:true
		  },
		  name2:{
		      required:true,
			  number:true
		  }
	  },
	  idRules:{
	     id1:{
		     required:true,
			 email:true
		 }
	  },
	  idMessages:{
	  	id1:{
		     required:'请输入',
			 email:'邮箱'
		 }
	  },
	  messages:{
	      name1:{
		      required:'请输入',
			  email:'邮箱'
		  },
		  name2:{
		      required:'不为空',
			  number:'数字'
		  }
	  }
  });
  
  $('#btn-add').click(function(){
      var el = $('#modal').clone().addClass('add-el');
	  var num = $('#form1').find('.add-el').length;
	  $('#form1').append(el);
	  var input = el.find('input');
	  input.attr('id','addinput-'+num);
	  v.addRule(input,{number:true},{number:'应该是数字'},input.attr('id'));
  });
   
});