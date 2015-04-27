define(function(require) {
	var Site = require('../comp/init.js');
	require('../plugins/jquery.box.js');
	require('../plugins/jquery.combobox.js');
	require('../Y-all/Y-script/Y-selectarea.js');
	require('../Y-all/Y-script/Y-selectbranch.js');
	
	var user_form = $('#user_form');

	$('.submit_form').click(function() {
		user_form.submit();
	})
	if (user_form.length) {
		user_form.validate({
			errorClass : 'error-tip',
			errorElement : 'b',
			submitHandler : function() {
				user_form.ajaxSubmit({
					success : function(res) {
						alert(res.message);
					}
				});
			},
			errorPlacement : function(error, element) {
				if (element.attr('name') == 'businessPeriod'
						|| element.attr('name') == 'bankType'
						|| element.attr('name') == 'roleIds') {
					element.parent().append(error);
				} else {
					element.after(error);
				}

			},
			rules : {
				
				state : {
					required : true
				},
				roleIds : {
					required : true
				}
			},
			messages : {
				state : {
					required : '请选择状态'
				},
				roleIds : {
					required : '请选择角色'
				}
			},
			onkeyup : false
		});

	}
	
	$("input[name = 'roleIds']").click(function(){
		if($("#investor").attr('checked')){
			$("#refereesDiv").show();
		}else{
			$("#refereesDiv").hide();
		}
		
	});
	$("input[name = 'referees']").change(function(){
		var e = $('#referees-messge');
		e.empty();
		var er = $('#referees-messge-right');
		er.empty();
	    var referees = $(this).val();
		var url = '/backstage/userManage/common/validationReferees';
		var data = {'referees' : referees};
		var result = $_GLOBAL.ajax(url, data);
		if(result.code == '1'){
			er.attr("class","right-tip");
			er.append(" ");
			e.attr("class","info-tip");
		}else{
			e.attr("class","error-tip");
			er.attr("class","");
		}
		e.append(result.message);
	});
	
});