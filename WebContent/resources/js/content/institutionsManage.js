define(function(require) {
	var Site = require('../comp/init.js');
	require('../Y-all/Y-script/Y-msg.js');
	$(".udateState").click(function() {
		var _this = this;
		Y.confirm('请选择','确定执行该操作？',function(opn){
			if(opn == 'yes') {
				$.ajax({
					url : '/backstage/userManage/updateState',
					type : 'post',
					dataType : 'json',
					data : {
						userBaseId : $(_this).next().val(),
						state : $(_this).attr("state")
					},
					success : function(res) {
						alert(res.message),
						location.reload()
					}
				});
			}
		});
	});
	
	$(".resetPayPassword").click(function() {
		var _this = this;
		Y.confirm('请选择','确定执行该操作？',function(opn){
			if(opn=="yes"){
				$.ajax({
					url : '/backstage/userManage/resetPayPassword',
					type : 'post',
					dataType : 'json',
					data : {
						userBaseId : $(_this).next().val()
					},
					success : function(res) {
						alert(res.message),
						location.reload()
					}
				});
			}
		});
	
	});
	
});