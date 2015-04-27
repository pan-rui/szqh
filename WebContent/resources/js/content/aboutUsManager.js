/**
 * app轮播图
 */

define(function(require, exports, module) {
	var Site = require('../comp/init.js');
	require('../content/chineseAmountExchange.js');
	require('../Y-all/Y-script/Y-imgplayer.js');
	require('../content/fileUpload.js');
	
	$("#add_form").click(function(){
		var title = $('[name=title]').val();
		var content = $('[name=content]').val();
		var status=$("input[type='radio']:checked").val();
		$.ajax({
			url : '/app/addAboutUsSubmit',
			type : 'post',
			dataType : 'json',
			data : {
				title : title,
				content : content,
				status :status
			},
			success : function(res){
				if(res.code=="1"){
					alert(res.message);
					document.location.href = '/app/aboutUsManager';
				}else{
					alert(res.message);
				}
				
			}
		});
	})
	
	
	$("#update_form").click(function(){
		var popId =$('[name=popId]').val(); 
		var title = $('[name=title]').val();
		var content = $('[name=content]').val();
		var type=$('[name=type]').val();
		var status=$("input[type='radio']:checked").val();
		$.ajax({
			url : '/app/upDateAboutUsSubmit',
			type : 'post',
			dataType : 'json',
			data : {
				popId:popId,
				title : title,
				content : content,
				type:type,
				status:status
			},
			success : function(res){
				if(res.code=="1"){
					alert(res.message);
					document.location.href = '/app/aboutUsManager';
				}else{
					alert(res.message);
				}
				
			}
		});
	})
	
	$(".del_img").click(function(){
		var id = this.id;
		$.ajax({
			url : '/app/deleteAboutUs',
			type : 'post',
			dataType : 'json',
			data : {
				popId : id
			},
			success : function(res){
				if(res.code=="1"){
					alert(res.message);
					document.location.href = '/app/aboutUsManager';
				}else{
					alert(res.message);
				}
				
			}
		});
	})
	//加载编辑框
	$('textarea[name=content]').xheditor({});
	
});