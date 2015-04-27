define(function(require) {
	var Site = require('../comp/init.js');
	require('../content/chineseAmountExchange.js');
	require('../Y-all/Y-script/Y-imgplayer.js');
	require('../Y-all/Y-script/Y-msg.js');
	require('../content/fileUpload.js');

    var tested = false;

	var addForm=$('#sms_form');
	$('.fn-submit1').click( function(){
		
		if(checkFiled()){
			Y.confirm('请选择','确定执行该操作？',function(opn){
				if(opn=="yes"){
					addForm.ajaxSubmit({
						success : function(res) {
			                if(res.code == 1){
			                    alert(res.message);
			                    tested = true;
			                }else{
			                    alert(res.message);
			                }
	
						}
					});
				}
			})
		}
		
		
		
	});
	
	
	
	
	$('.fn-submit2').click( function(){
		
		if(checkFiled()){
			if(!tested){
				Y.alert("请先将内容发送至测试手机，验证短信后再正式发送");
				return ;
			}
			
			addForm.attr("action", "/backstage/smsCenter/doSend");  
			Y.confirm('请选择','确定执行该操作？',function(opn){
				if(opn=="yes"){
					addForm.ajaxSubmit({
						success : function(res) {
			                if(res.code == 1){
			                	 $('#smsContent').val("");
			                	 Y.alert(res.message);
			                    tested = false;
			                    addForm.attr("action", "/backstage/smsCenter/doTest");  
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
		var tMobile = $.trim($('#testMobile').val());
		var content = $.trim($('#smsContent').val());
		
		if(tMobile=="" || content==""){
			Y.alert("手机号和短信内容都不能为空！");
			return false;
		}else{
			return true;
		}
	}
	
	
	

	
	
	
});