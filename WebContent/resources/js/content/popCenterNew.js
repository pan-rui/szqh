define(function(require) {
	var Site = require('../comp/init.js');
	require('../content/chineseAmountExchange.js');
	require('../Y-all/Y-script/Y-imgplayer.js');
	require('../content/fileUpload.js');
	require('../comp/init.js');
	require('../content/chineseAmountExchange.js');
	require('../Y-all/Y-script/Y-msg.js');
	 Y.create('ImgPlayer',{
			eleArr:'#guaranteeLicenseUrl_Img',
			titleInfo: 'alt',
			content:'',
			pathInfo: function(){
			  return $(this).attr('src');
			}
		});
	$('.time').click(function() {
		WdatePicker({
			startDate : '%y-%M-01 HH:mm:ss',
			dateFmt : 'yyyy-MM-dd HH:mm:ss'
		});
	});
	
	function formatFloat(src, pos)
	{
	    return Math.round(src*Math.pow(10, pos))/Math.pow(10, pos);
	}
	
	$('[name=online_link]').click(function(){
		var url = '/backstage/popModuleCenter/changeStatus';
		var data = {'popId':$(this).attr('data'),'status':2};
		var result = $_GLOBAL.ajax(url, data);
		alert(result.message);
		document.location.href = document.location.href;
	});
	$('[name=offline_link]').click(function(){
		var url = '/backstage/popModuleCenter/changeStatus';
		var data = {'popId':$(this).attr('data'),'status':3};
		var result = $_GLOBAL.ajax(url, data);
		alert(result.message);
		document.location.href = document.location.href;
	});
	$('[name=offline_privew]').click(function(){
		var url = '/help/announcement';
		var data = {'popId':$(this).attr('data'),'status':2};
		var result = $_GLOBAL.ajax(url, data);
		alert(result.message);
		document.location.href = document.location.href;
	});
	$('[name=delete_link]').click(function() {
		var _this = this;
		Y.confirm('请选择','确定执行该操作？',function(opn){
			if(opn=="yes"){
				$.ajax({
					url : '/backstage/popHelp/deleteHelp',
					type : 'post',
					dataType : 'json',
					data : {
						
						popId : $(_this).attr("data")
					},
					success : function(json) {
						alert(json.message);
						location.reload();
					}
				})
			}
			
		})
	})
	
	$('[name=online_module]').click(function(){
		var url = '/backstage/popModule/online';
		var data = {'moduleId':$(this).attr('data')};
		var result = $_GLOBAL.ajax(url, data);
		alert(result.message);
		document.location.href = document.location.href;
	});
	$('[name=offline_module]').click(function(){
		var url = '/backstage/popModule/offline';
		var data = {'moduleId':$(this).attr('data')};
		var result = $_GLOBAL.ajax(url, data);
		alert(result.message);
		document.location.href = document.location.href;
	});
	
	$('[name=delete_module]').click(function(){
		var _this = this;
		Y.confirm('请选择','确定执行该操作？',function(opn){
				
				if(opn=="yes"){
					$.ajax({
						url : '/backstage/popModule/delete',
						type : 'post',
						dataType : 'json',
						data : {
							moduleId : $(_this).attr("data")
						},
						success : function(json) {
							alert(json.message);
							location.reload();
						}
					})
				}
			
		})
		
	});
	
	var addForm=$('#pop_form');
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
	
	function  checkFiled() {
		var moduleName = $.trim($('#moduleName').val());
		var sortOrder = $.trim($('#sortOrder').val());
		var vmPage = $.trim($('#vmPage').val());
		
		if(moduleName=="" || sortOrder=="" || vmPage==""){
			Y.alert("存在未录入的字段！");
			return false;
		}else{
			return true;
		}
	}

	
});