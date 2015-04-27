define(function(require) {
	var Site = require('../comp/init.js');
	require('../content/fileUpload.js');
	require('../plugins/jquery.box.js');
	require('../plugins/jquery.combobox.js');
	require('../plugins/jquery.window.js');
	$('.fn-time').click(function() {
		WdatePicker({
			startDate : '%y-%M-01 HH:mm:ss',
			dateFmt : 'yyyy-MM-dd HH:mm:ss'
		});
	});
	
	$('#fixTime').find('.fn-ok').click(function() {
		var url = '/backstage/trade/fixRepayTime';
		var tradeId = $("#fixObject").val();
		var expireDate = ""+$("#expireDate").val();
		var data = {'tradeId' : tradeId, 'expireDate' : expireDate};
		if(''== $.trim($("#expireDate").val())){
			alert("还款日期不能为空");
			return false;
		}
		var result = $_GLOBAL.ajax(url, data);
		alert(result.message);
		window.location.href = window.location.href;
	});
	
	$('[name=offLineTrade]').click(function() {
		var url = '/backstage/trade/offLineTrade';
		var tradeId = $(this).attr("data");
		var data = {'tradeId' : tradeId};
		var result = $_GLOBAL.ajax(url, data);
		alert(result.message);
		window.location.href = window.location.href;
	});
	$("[name=fixRepayTime]").click(function(){
		var data = $(this).attr("data");
		$("#expireDate").val("");
		$("#fixObject").val(data);
		$('body').window({
			content : '#fixTime',
			simple : true,
			closeEle : '.u-btn-gray'
		});
	});
});