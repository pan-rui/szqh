define(function(require) {
	var Site = require('../comp/init.js');
	require('../plugins/jquery.box.js');
	require('../plugins/jquery.combobox.js');
	require('../comp/security.js');
	
	$('input[name=userName]').blur(function(){
	   if($(this).val()=='')return false;
	   var email=/^[A-Za-z\d]+([-_.][A-Za-z\d]+)*@([A-Za-z\d]+[-.])+[A-Za-z\d]{2,5}$/;
	   var mob=/^(((13[0-9]{1})|(15[0-9]{1})|(18[0-9]{1})|(14[0-9]{1}))+\d{8})$/; 
	   if(email.test($(this).val())){
	   		$('input[name=mail]').val($(this).val());
	   }else if(mob.test($(this).val())){ 
	   		$('input[name=mobile]').val($(this).val());
	   }else {
		   $('input[name=mobile]').removeClass('igipt').parent().show();
	   }
   });

	$('#newLogPasswordMail_form').validate({
		errorClass : 'e-tips',
		errorElement : 'b',
		focusInvalid : false,
        errorPlacement: function(er,el){
        	if(el.attr('name')=='captcha')el.css('margin-left',0).parent().append(er);
        	else el.css('margin-left',0).after(er);
        },
        rules : {
			
		},
		messages : {
			
		},
		onkeyup : false
	});
	
	
	$('#newLogPassword_mobileForm').validate({
		errorClass : 'e-tips',
		errorElement : 'b',
		focusInvalid : false,
        errorPlacement: function(er,el){
        	if(el.attr('name')=='captcha')el.css('margin-left',0).parent().append(er);
        	else el.css('margin-left',0).after(er);
        },
        rules : {

		},
		messages : {

		},
		onkeyup : false
	});
	
	
	
	$('#newLogPasswordPhone').click(function(){
		$('#newLogPasswordMail').css('background','#30a9e2');
		$(this).css('background','#f9ae78');
		$('#mailLog').css('display','none');
		$('#phoneLog').css('display','block');
	});
	
	$('#newLogPasswordMail').click(function(){
		$('#newLogPasswordPhone').css('background','#fc8026');
		$(this).css('background','#67c8f4');
		$('#mailLog').css('display','block');
		$('#phoneLog').css('display','none');
	});
	
	
});