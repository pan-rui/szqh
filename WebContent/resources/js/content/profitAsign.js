define(function(require) {
	var Site = require('../comp/init.js');
	require('../plugins/jquery.box.js');
	require('../plugins/jquery.combobox.js');
	require('../plugins/jquery.window.js');
	require('../Y-all/Y-script/Y-window.js');
	require('../Y-all/Y-script/Y-tip.js');
	
	$("#distributionQuota").Y('ToolTip',{
	    content:'该配置为分享佣金收益给您客户的额度',
	    align: 'top'
	});
	
	$("#modifyAsign").click(function(){
		$("#setContainer").toggle();
		$("#doneContainer").toggle();
	});
	
	$("#distroyAsign").click(function(){
		var tblBaseId = $(this).attr('data');
		var token = $('[name = token]').val();
		if(window.confirm("是否立即取消分配的额度？")){
			$.ajax({
				url : '/investorManager/distroyQuota',
				data : {'tblBaseId' : tblBaseId, 'token' : token},
				type : 'post',
				dataType : 'json',
				success : function(res){
					alert(res.message);
					document.location.href = document.location.href;
				}
			});
		}
	});
	
	var profit_form_one = $('#profit_form_one');
	
	
	var pageNo = $('[name = pageNo]').val();
	var userName = $('[name = userName]').val();
	var realName = $('[name = realName]').val();
	
	if (profit_form_one.length) {
		profit_form_one.validate({
			errorClass : 'error-tip',
			errorElement : 'b',
			submitHandler : function() {
				profit_form_one.ajaxSubmit({
					success : function(res) {
						
						alert(res.message);
						
						if(res.code == 1){ //成功后返回
							//history.back();  
							//history.go(-1);
							document.location.href = "/investorManager/investorManage?pageNo="+pageNo+"&userName="+userName+"&realName="+realName;

						}else{ //刷新
							document.location.href = document.location.href;
						}
						
					}
				});
			},
			rules : {
				distributionQuota : {
					required : true,
					mustNotInclude : ' ',
					number:true,
					//firstNum:true
					min:0,
					max:100  
				}
			},
			messages : {
				distributionQuota : {
					required : '请填写配置额度',
					number : '配额必须是数字',
					//firstNum : '第一个数字必须大于0'
					min:'额度最小值为0',
					max:'额度最大值为100'
				}
			},
			onkeyup : false
		});

	}

	
});