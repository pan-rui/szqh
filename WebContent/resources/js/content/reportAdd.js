define(function(require) {
	var Site = require('../comp/init.js');
	require('../content/chineseAmountExchange.js');
	require('../Y-all/Y-script/Y-imgplayer.js');
	require('../Y-all/Y-script/Y-msg.js');

	var addForm=$('#report_form');
	$('.fn-submit1').click( function(){
		
		if(checkFiled()){
			Y.confirm('请选择','确定执行该操作？',function(opn){
				if(opn=="yes"){
					addForm.ajaxSubmit({
						success : function(res) {
			                if(res.code == 1){
			                    Y.alert(res.message);
			                }else{
			                	Y.alert(res.message);
			                }
	
						}
					});
				}
			})
		}
		
	});
	
	
	$("#filter1Type").change(function(){
		if($(this).children('option:selected').val()=='OPTION'){
			$("#filter1Options").show(500);
		}else{
			$("#filter1Options").hide(500);
		}
	});

	$("#filter2Type").change(function(){
		if($(this).children('option:selected').val()=='OPTION'){
			$("#filter2Options").show(500);
		}else{
			$("#filter2Options").hide(500);
		}
	});
	
	$("#filter3Type").change(function(){
		if($(this).children('option:selected').val()=='OPTION'){
			$("#filter3Options").show(500);
		}else{
			$("#filter3Options").hide(500);
		}
	});
	
	$("#filter4Type").change(function(){
		if($(this).children('option:selected').val()=='OPTION'){
			$("#filter4Options").show(500);
		}else{
			$("#filter4Options").hide(500);
		}
	});
	
	$("#filter5Type").change(function(){
		if($(this).children('option:selected').val()=='OPTION'){
			$("#filter5Options").show(500);
		}else{
			$("#filter5Options").hide(500);
		}
	});
	
	$("#filter6Type").change(function(){
		if($(this).children('option:selected').val()=='OPTION'){
			$("#filter6Options").show(500);
		}else{
			$("#filter6Options").hide(500);
		}
	});
	
	

	function  checkFiled() {
		var reportName = $.trim($('#reportName').val());
		var sqlStr = $.trim($('#sqlStr').val());
		
		if(reportName=="" || sqlStr==""){
			Y.alert("报表名称和查询SQL不能为空！");
			return false;
		}else{
			return true;
		}
	}
	
	
	

	
	
	
});