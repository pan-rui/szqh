define(function(require) {
	var Site = require('../comp/init.js');
	require('../Y-all/Y-script/Y-msg.js');
	
	$('.fn-time').click(function() {
		WdatePicker({
			startDate : '%y-%M-01 HH:mm:ss',
			dateFmt : 'yyyy-MM-dd HH:mm:ss'
		});
	});
	
	$(".udateState").click(function() {
		var _this = this;
		Y.confirm('请选择','确定执行该操作？',function(opn){
			if(opn=="yes"){
				$.ajax({
					url : '/backstage/userManage/updateState',
					type : 'post',
					dataType : 'json',
					data : {
						userBaseId : $(_this).next().val(),
						state : $(_this).attr("state")
					},
					success : function(res) {
						alert(res.message);
                        location.replace(location.href);
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
						userBaseId :  $(_this).next().val()
					},
					success : function(res) {
						alert(res.message),
						location.reload()
					}
				});
			}
		});
	});
	
	$('.queryBalance').click(function() {
		var url = '/backstage/userManage/userBaseInfoManage/queryBalance';
		var data = {'userBaseId':$(this).attr('data')};
		var res = $_GLOBAL.ajax(url, data);
		if(res.code==1){
			alert("余额：￥"+res.balance+"  可用余额：￥"+res.availableBalance+"  冻结金额：￥"+res.freezeAmount);
		}else{
			alert("查询失败！");
		}
	});
});