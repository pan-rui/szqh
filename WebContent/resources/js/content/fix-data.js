define(function(require) {
	var Site = require('../comp/init.js');
	
	$("#formSubmit").click(function(){
		$.ajax({
			url : '/backstage/userManage/changeBrokerSubmit',
			type : 'post',
			dataType : 'json',
			data : {
			},
			success : function(res){
				alert(res.message);
				document.location.href = document.location.href;
			}
		});
	})
});