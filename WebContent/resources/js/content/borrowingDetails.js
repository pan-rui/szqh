define(function(require) {
	var Site = require('../comp/init.js');
	require('../Y-all/Y-script/Y-window.js');
	require('../content/securityPassword.js')(180,24);
	require('../Y-all/Y-script/Y-imgplayer.js');
	require("../Y-all/Y-script/Y-countdown");
	
	Y.create('ImgPlayer',{
		eleArr:'#guaranteeLicenseUrl_Img',
		titleInfo: 'alt',
		content:'',
		pathInfo: function(){
		  return $(this).attr('src');
		}
	});
	var changeWnd;
	$('#payLoan').click(function(){
		$('body').Y('Window', {
            key:"payPasswordCheckContainer",
			content : '#payPasswordCheckContainer'
		});
	});
	$('#advancePayLoan').click(function(){
		$('body').Y('Window', {
			content : '#payPasswordCheckContainer'
		});
	});
	$("#payPassword").focus(function(){
		var e = $('#pay-password-messge');
		e.hide();
	});
	var e = $('#pay-password-messge');
	e.hide();
	$('#confirm_pay').click(function (){
		
		if(checkCode()){  //checkPayPassword()
			
			
			$('body').Y('Window',{
				content:"<span><img src='/resources/images/common/loadingScroll.gif' /><br/>还款处理中，请您耐心等候...</span>",
				key:'lodding',
				simple:true
			});
			
			var url = '/tradeQuery/manualReimbursement';
			var data = {tradeId : $('#tradeId').val(), demandId : $('#demandId').val(),smsCode: $('#code1').val(),business:$("#business1").val(),mobile: $("#mobile").val(),token : $('#token').val()};
			var result = $_GLOBAL.ajax(url, data);
			Y.getCmp('lodding').close();
			if('1' == result.code){
				alert(result.message);
				var backUrl =  document.location.href;
				backUrl = backUrl.substring(0, backUrl.lastIndexOf("=")+1);
				backUrl = backUrl + "xq";
				document.location.href = backUrl;
			}else{
				alert(result.message);
				e.append(result.message);
			}
			/*$.ajax({
				type: "POST",
			    url : url,
			    dataType : 'json',
			    data : data,
			    success: function(res) {
			    	Y.getCmp('lodding').close();
					alert(res.message);
			    }
			});*/
			
		}
	});

    window.confirmClick=function(paytk){
        $('body').Y('Window',{
            content:"<span><img src='/resources/images/common/loadingScroll.gif' /><br/>还款处理中，请您耐心等候...</span>",
            key:'lodding',
            simple:true
        });
        var url = '/tradeQuery/manualReimbursement';
        var data = {tradeId : $('#tradeId').val(), demandId : $('#demandId').val(),smsCode: $('#code1').val(),business:$("#business1").val(),mobile: $("#mobile").val(),token : $('#token').val(),paytk:paytk};
        var result = $_GLOBAL.ajax(url, data);
        Y.getCmp('lodding').close();
        if('1' == result.code){
            alert(result.message);
            var backUrl =  document.location.href;
            backUrl = backUrl.substring(0, backUrl.lastIndexOf("=")+1);
            backUrl = backUrl + "xq";
            document.location.href = backUrl;
        }else{
            alert(result.message);
            e.append(result.message);
        }

    }




    function checkCode(){
		var e = $('#pay-code-messge');
		e.hide();
		if( $.trim($("#code1").val())==""){
			e.html('请输入短信验证码!');
			e.show();
			return false;
		}
		return true;
	}
	
	function checkPayPassword(){
	 	var status = false;
		var e = $('#pay-password-messge');
		e.hide();
	    var securityData = getSecurityData();
	    if( $.trim(securityData) === 'invalid'){
	    	e.html('支付密码不能为空或少于6位');
	    	e.show();
	    	return false;
	    }
	    $('#payPassword').val(securityData)
		var url = '/userManage/validationPayPassword';
		var data = {'payPassword' : securityData};
		var result = $_GLOBAL.ajax(url, data);
		if('1' == result.code){
			status = true;
		}else{
			e.html(result.message);
			e.show();
		}
		return status;
	}
	
	$('#confirmFinishCollectBtn').click(function (){
		if(window.confirm("是否立即进行融资确认？")){
			var url = '/tradeQuery/confirmFinishCollect';
			var data = {tradeId : $('#tradeId').val(), token : $('#token').val()};
			var result = $_GLOBAL.ajax(url, data);
			alert(result.message);
			document.location.href = document.location.href;
		}
	});
	$('#recharge').click(function() {
		var href = $(this).attr('href');
		$(this).attr('href','/deduct/launchDeduction');
		var _this = this;
		$.ajax({
			url:href,
			type:'post',
			dataType:'json',
			success:function(res){
				alert(res.message);
				location.reload();
			}
		})
	});
	
	
	

	
	   // -------------------------------------发送手机验证码-----------------------------------------------
	
	
		$('#getCode1').click(function() {
			var business = $("#business1").val();
			var mobile = $("#mobile").val();
			var countdown = Y.getCmp('getCode1');
			sendMobile(business, mobile, countdown);
		});	

		
	  	function sendMobile(business, mobile, conutdown) {
	  		var url = '/anon/sendSmsCode';
	  		var data =  {business:$("#business1").val(),mobile: $("#mobile").val()};
			var result = $_GLOBAL.ajax(url, data);
			
			if ('1' == result.code){
				alert("短信已发送");
			}else{
				alert(result.message);
			}
			

		}		
	  	
	    //----tab 切换------------------------------	
	    $("#tradeinfo").click(function(){
	        $("#touzhidiv").hide();
	        $("#tradediv").show();
	        $('#touziinfo').removeClass('curr');
	        $(this).addClass('curr');
		});
		
		$("#touziinfo").click(function(){
		    $("#tradediv").hide();
	        $("#touzhidiv").show();
	        $('#tradeinfo').removeClass('curr');
	        $(this).addClass('curr');
		});
		
		
	
});
