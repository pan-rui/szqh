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

	/** 通过弹出层 */
	$(".pass").click(function() {
		if(checkGuaranteeAuth($(this).attr("data"))){
			$("[name=demandId]").val($(this).attr("data"));
			$('body').window({
				content : '#pass',
				simple : true,
				closeEle : '.u-btn-gray'
			});
		}
	});
	function checkGuaranteeAuth(obj){
		var url = '/backstage/guaranteeAuthCheck';
		var data = {'demandId' : obj};
		var result = $_GLOBAL.ajax(url, data);
		if(result.code==1){
			return true;
		}
		alert(result.message);
		return false;
	}
	
	$('#pass').find('input[name=status]').click(function() {
		var val = $('#pass').find('input[name=status]:checked').val();
		var input = $('#pass').find('input[name=publishDate]');
		if (val == 'pass') {
			input.attr('disabled', true);
		} else {
			input.removeAttr('disabled');
		}
	});

	/** 驳回弹出层 */
	$(".dismiss").click(function() {
		$("[name=demandId]").val($(this).attr("data"));
		$('body').window({
			content : '#dismiss',
			simple : true,
			closeEle : '.u-btn-gray'
		});
	});

	$('#pass').find('.fn-ok').click(function() {
		var oldAction = passForm.attr("action");
		//passForm.attr("action",oldAction+"?tag="+new Date().getTime());   //禁止缓存
		passForm.submit();
	});

	/** 验证审核借款需求FORM表单 */
	var passForm = $('#passForm');
	if (passForm.length) {
		passForm.validate({
			errorClass : 'error-tip',
			errorElement : 'b',
			submitHandler : function() {
				passForm.ajaxSubmit({
					dataType : 'json',
					cache: false,                      //禁止缓存
					success : function(res) {
						if (res.code == 1) {
							alert('审核通过');
							window.location.href = window.location.href;
						} else {
							alert('审核失败');
							window.location.href = window.location.href;
						}
					}
				});
			},
			rules : {
				publishDate : {
					required : true
				}
			},
			messages : {
				publishDate : {
					required : "请输入"
				}
			},
			onkeyup : false
		});
	}

	$('#dismiss').find('.fn-ok').click(function() {
		dismissForm.submit();
	});

	var dismissForm = $('#dismissForm');
	if (dismissForm.length) {
		dismissForm.validate({
			errorClass : 'error-tip',
			errorElement : 'b',
			submitHandler : function() {
				dismissForm.ajaxSubmit({
					dataType : 'json',
					success : function(res) {
						if (res.code == 1) {
							alert('驳回通过');
							window.location.href = window.location.href;
						} else {
							alert('驳回失败');
							window.location.href = window.location.href;
						}
					}
				});
			},
			rules : {
				dismissReason : {
					required : true
				}
			},
			messages : {
				dismissReason : {
					required : "请输入驳回原因"
				}
			},
			onkeyup : false
		});
	}
	$("#dismissLink").click(function(){
		var data = $(this).attr("data");
		$("#dismissReason").text(data);
		$('body').window({
			content : '#dismiss',
			simple : true,
			closeEle : '.u-btn-gray'
		});
	});
	
	$("[name=tradeOnline]").click(function(){
		var url = '/backstage/onlineTrade';
		var loanId = $(this).attr("data");
		var data = {'loanId' : loanId};
		var result = $_GLOBAL.ajax(url, data);
		alert(result.message);
		window.location.href = window.location.href;
	});
	
	$("[name=determineInvest]").click(function(){
		var url = '/backstage/determineInvest';
		var loanId = $(this).attr("data");
		var data = {'demandId' : loanId};
		var result = $_GLOBAL.ajax(url, data);
		alert(result.message);
		window.location.href = window.location.href;
	});
	
	$("[name=notifyInvest]").click(function(){
		if(window.confirm("确认发出通知？")){
			var url = '/backstage/notifyInvest';
			var loanId = $(this).attr("data");
			var data = {'demandId' : loanId};
			var result = $_GLOBAL.ajax(url, data);
			alert(result.message);
			window.location.href = window.location.href;
		}
	});
	$("[name=notifyRepay]").click(function(){
		if(window.confirm("确认发出通知？")){
			var url = '/backstage/notifyRepay';
			var loanId = $(this).attr("data");
			var data = {'demandId' : loanId};
			var result = $_GLOBAL.ajax(url, data);
			alert(result.message);
			window.location.href = window.location.href;
		}
	});
});