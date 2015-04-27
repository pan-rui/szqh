define(function(require) {
	var Site = require('../comp/init.js');
	require('../plugins/jquery.box.js');
	require('../Y-all/Y-script/Y-window.js');
	require('../plugins/jquery.combobox.js');
	require('../content/chineseAmountExchange.js');
	$("input[name='amount']").blur(function(){
		var standard = 0.0;
		var cash=$("input[name='amount']").val()
		$("#fuwu").text(standard);
		$("#f").text(standard);
		var scash=cash-standard;
		$("#ti").text(cash);
		if(scash>0){
			$("#s").text(scash)
		}else{
			$("#s").text("0.0")
		}
		$("#fee").val(standard);
		$("#rea").val(scash);
		var chineseAmount = convertCurrency(cash);
		if(chineseAmount.indexOf("error") >= 0 || cash == ""){
			$("#amountChinese").val("");
			$(".fn-y-tip").css("display","none");
			return;
		}
		$(".fn-y-tip").css("display","block");
		$("#amountChinese").val(chineseAmount);
	})
	$("#biao").mouseover(function(){
		$("#table").show();
	})
	$("#biao").mouseout(function(){
		$("#table").hide();
	})
	var launchDeduction_form = $('#launchDeduction_form');
	if (launchDeduction_form.length) {
		launchDeduction_form.validate({
			errorClass : 'error-tip',
			errorElement : 'b',
			errorPlacement : function(error, element) {
				if (element.attr('name') == 'amount') {
					element.next().after(error);
					
				} else {
					element.after(error);
				}
			},
			rules : {
				amount : {
					required : true,
					customRemote : {
						url : '/deduct/validationDeductAmount',
						customError : function(element, res) {
							return res.message;
						}
					}
				}
			},
			messages : {
				amount : {
					required : '请填输入金额'
				}
			},
			onkeyup : false

		});
	}
	$("#agree").click(function(){
		if($('#license').attr("checked")=="checked"){
			$(window.parent.document.body).find('form').submit();
			return true;
		}else{
			alert("请勾选协议！");
			return false;
		}
	});
	$("#disagree").click(function(){
		window.parent.Y.getCmp('wnd1').close();
	});
	
	$("#downLoanReceipt").click(function(){
		var path = eval($(this).attr('path'));
		location.href = path;
		return false;
	});
	
	$("#cancelagree").click(function(){
		window.close();
		return false;
	});
	
	$("#ok").click(function(){
		if(launchDeduction_form.valid()){
			var bankCardNo_4 = $("[name=bankCardNo_4]").val();
			var fees = $("[name=fees]").val();
			var amount = $("[name=amount]").val();
			var real = $("[name=real]").val();
			var url = "/deduct/signDeduction?bankCardNo_4="+ bankCardNo_4 +"&fees=" +fees+"&amount=" + amount+"&real=" + real;
			setIframeSrc(url);
			$('body').Y('Window', {
				content : '#signContainer',
				key:'wnd1'
			});
		}

	});
	
	//异步加载iFrame
	function setIframeSrc(obj) {
		 var s = obj;
	    var $iframe1 = $("#iframe1");
	    $iframe1.attr('src', s);
	    iframe = document.getElementById("iframe1");
	    if (iframe.attachEvent){  
	        iframe.attachEvent("onload", function(){  
	        	$iframe1.css("visibility","visible");
	        	reinitIframe();
	        });  
	    } else {  
	        iframe.onload = function(){  
	        	$iframe1.css("visibility","visible");
	        	reinitIframe();
	        };  
	    }
	}
	function reinitIframe(){
		var iframe = document.getElementById("iframe1");
		try{
			var bHeight = iframe.contentWindow.document.body.scrollHeight;
			var dHeight = iframe.contentWindow.document.documentElement.scrollHeight;
			var height = Math.max(bHeight, dHeight);
			iframe.height =  height;
		}catch (ex){}
	}
	$('.fm-license').find('*').css('position','static');
});