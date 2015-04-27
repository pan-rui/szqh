define(function(require) {
	var Site = require('../comp/init.js');
	require('../Y-all/Y-script/Y-msg.js');
	$("[name=online_link]").click(function() {
		var _this = this;
		Y.confirm('请选择','确定执行该操作？',function(opn){
			if(opn=="yes"){
				$.ajax({
					url : '/backstage/updateStatus',
					type : 'post',
					dataType : 'json',
					data : {
						tblBaseId :  $(_this).attr("data"),
						status : 2
					},
					success : function(res) {
						alert(res.message);
						location.reload();
					}
				})
			}
		})
	
	})
	$("[name=offline_link]").click(function() {
		var _this = this;
		Y.confirm('请选择','确定执行该操作？',function(opn){
			if(opn=="yes"){
				$.ajax({
					url : '/backstage/updateStatus',
					type : 'post',
					dataType : 'json',
					data : {
						tblBaseId :  $(_this).attr("data"),
						status : 3
					},
					success : function(res) {
						alert(res.message);
						location.reload();
					}
				})
			}
		})
	
	})
});