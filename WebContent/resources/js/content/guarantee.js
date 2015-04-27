//担保机构js
define(function(require) {
	var Site = require('../comp/init.js');
	require('../Y-all/Y-script/Y-window.js');
	require('../Y-all/Y-script/Y-msg.js');
	require('../Y-all/Y-script/Y-imgplayer.js');
	require('../comp/security.js');
	require("../Y-all/Y-script/Y-countdown");
 	$('#a001').removeClass("index-bg"); 
	 $('#a002').removeClass("nemu-bg");  
	 $('#a003').addClass("nemu-bg");  
	 $('#a004').removeClass("nemu-bg");  
	 $('#a005').removeClass("nemu-bg"); 
 
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
			content : '#payPasswordCheckContainer'
		});
	});
	
	$("#payPassword").focus(function(){
		var e = $('.error-tip');
		e.hide();
	});
	var e = $('.error-tip');
	e.hide();
	$('#confirm_pay').click(function (){
		
		if(checkCode()){   //checkPayPassword()
			
			$('body').Y('Window',{
				content:"<span><img src='/resources/images/common/loadingScroll.gif' /><br/>系统处理中，请您耐心等候...</span>",
				key:'lodding',
				simple:true
			});
			
			var url = '/tradeQuery/manualReimbursement';
			var data = {tradeId : $('#tradeId').val(), demandId : $('#demandId').val(), smsCode: $('#code1').val(),business:$("#business1").val(),mobile: $("#mobile").val(), token : $('#token').val()};
			var result = $_GLOBAL.ajax(url, data);
			
			
			if(result.code==1){
				alert(result.message);
				Y.getCmp('lodding').close();
				window.location.reload();
			}else{
				alert(result.message);
				Y.getCmp('lodding').close();
			}
			
			/*
			$.ajax({
				type: "POST",
			    url : url,
			    dataType : 'json',
			    data : data,
			    success: function(res) {
			    	Y.getCmp('lodding').close();
					alert(res.message);
			    }
			});
			var backUrl =  document.location.href;
			backUrl = backUrl.substring(0, backUrl.lastIndexOf("=")+1);
			backUrl = backUrl + "xq";
			document.location.href = backUrl;
			*/
			
		}
	});
	
	//检查验证码
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
	

	//加密审核密码
	var modulus = "";
	var exponent = "";
	function setHiddenPwd(){
		var checkPwd = $('[name=checkPassword]').val();
		var key = RSAUtils.getKeyPair(exponent, '', modulus);
		return RSAUtils.encryptedString(key, checkPwd);
	}
	$('#loanAuditFinish').click(function(){
		$.ajax({
			url : '/login/keyPair',
			type : 'post',
			dataType : 'json',
			success : function(res){
				if(res.code==1){
					modulus = res.modulus;
					exponent = res.exponent;
				}
			}
		});
	});
	
	//20141209  begin
	$('#loanAuditFinish').click(function (){
		if($(this).attr('data')=='level1'){
			Y.confirm('请选择','确认生成合同？',function(opn){
				if(opn == "yes"){
					var url = '/guaranteeCenter/guaranteeMakeContract';
					var data = {demandId : $('#demandId').val(), tradeId : $('#tradeId').val()};
					var result = $_GLOBAL.ajax(url, data);
					if(result.code==1){
						//alert("合同生成成功！");
						//$("#loanAuditFinish").html("合同已生成");
						
						$("#loanAuditFinish").html("已生成");
						$('#loanAuditFinished1').val("yes");
						window.open("/tradeDownload/downLoadWord?type=contract_DB&tradeId="+$('#tradeId').val()+"&detailID=0&downType=creatAndPrivew");
						
					}else{
						alert(result.message);
					}
				}
			})
		}
		/*
		else{
			$('body').Y('Window', {
				content : '#recCheckPasswordContainer',
				key:'wnd2',
				close:function(){
					$('#checkPasswordTwo').val("");
				}
			});
		}
		*/
	});
	
	
	$('#creatCertificate').click(function (){
		
		if("yes"!=$('#loanAuditFinished1').val()){
			Y.alert("先生成合同，再做生成数据保全证书！")
			return false;
		}
		 
		Y.confirm('请选择','确认执行该操作？',function(opn){
			if(opn == "yes"){
				var url = '/guaranteeCenter/creatCertificate';
				var data = {type : "contract_DB", tradeId : $('#tradeId').val()};
				
				var result = $_GLOBAL.ajax(url, data);
				if(result.code==1){
					//alert("数字证书已生成,开始审核");
					//document.location.href = result.url;
					$('#certifCreated').val("yes");
					$("#creatCertificate").html("已生成");
					window.open(result.url);
					
				}else{
					alert(result.message);
				}
			}
		})
		
	});
	
	//20141209  end
	
	function checkCheckPassword(obj){
	 	var status = false;
		var e = $('.error-tip');
		e.hide();
		var url = '/guaranteeCenter/authPasswordCheck';
		var data;
		if("lvOne" == obj){
			var pwd1 = $('#checkPasswordOne').val();
		    if( pwd1==""){
		    	e.html('请输入审核密码').show();
		    	return false;
		    }
			data = {'pwd' : pwd1,'type' : "lvOne"};
		}else{
			var pwd2 = $('#checkPasswordTwo').val();
		    if( pwd2==""){
		    	e.html('请输入审核密码').show();
		    	return false;
		    }
			data = {'pwd' : pwd2,'type' : "lvTwo"};
		}
		var result = $_GLOBAL.ajax(url, data);
		if('1' == result.code){
			status = true;
		}else{
			e.html(result.message).show();
		}
		return status;
	}

	function checkLoanAuditFinished(){
		if("yes"==$('#loanAuditFinished1').val()){
			return true;
		}else{
			alert("审核前请先生成合同！")
			return false;
		}
	}
	
	$('#confirm_check_one').click(function(){
		if(checkLoanAuditFinished()&&checkCheckPassword("lvOne")){
			//Y.getCmp('wnd1').close();
			$('[name=checkPassword]').val("");
			
			if("MAKELOAN"==$('#checkType').val()){
				var url = '/guaranteeCenter/guaranteeMakeLoanAuditing';
				var data = {demandId : $('#demandId').val(), tradeId : $('#tradeId').val()};
				var result = $_GLOBAL.ajax(url, data);
				if(result.code==1){
					alert(result.message);
					//document.location.href = "/guaranteeCenter/guaranteeDetails?demandId="+$('#demandId').val()+"&operate=hk";
					$('#loanAuditFinish').hide();
					window.location.reload();
				}else{
					alert(result.message);
				}
			}
			/*
			else{
				$('body').Y('Window', {
					content : '#recCheckPasswordContainer',
					key:'wnd2',
					close:function(){
						$('#checkPasswordTwo').val("");
					}
				});
			}
			*/
		}
		
	});
	
	$('#confirm_check').click(function(){
		if(checkCheckPassword("lvTwo")){
			
			$('#confirm_check').hide();  //防止多次提交
			//$('#confirm_check').hide();
			
			if("DEPLOY"==$('#checkType').val()){
				var url = '/guaranteeCenter/guaranteeDeployAuditing';
				var data = {demandId : $('#demandId').val(),  token : $('#token').val()};
				var result = $_GLOBAL.ajax(url, data);
				alert(result.message);
				document.location.href = document.location.href;
			}else{
				$('body').Y('Window',{
					content:"<span><img src='/resources/images/common/loadingScroll.gif' /><br/>处理中，请您耐心等候...</span>",
					key:'lodding',
					simple:true
				});
				var url = '/guaranteeCenter/guaranteeAuditingFinish';
				var data = {tradeId : $('#tradeId').val(),  token : $('#token').val()};
				var result = $_GLOBAL.ajax(url, data);

				if(result.code==1){
					alert(result.message);
				}else{
					alert(result.message);
				}
				$('#loanAuditFinish').hide();
				//document.location.href = document.location.href;
				window.location.reload();
			}
			
		}
		return false;
	});
	
	function checkPayPassword(){
	 	var status = false;
		var e = $('.error-tip');
		e.hide();
		var securityData = getSecurityData();
	    if( $.trim(securityData) === 'invalid'){
	    	e.html('支付密码不能为空或少于6位').show();
	    	return false;
	    }
	    $('#payPassword').val(securityData)
		var url = '/userManage/validationPayPassword';
		var data = {'payPassword' : securityData};
		var result = $_GLOBAL.ajax(url, data);
		if('1' == result.code){
			status = true;
		}else{
			e.html(result.message).show();
		}
		return status;
	}
	
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
	$("[name=confirm_check]").focus(function(){
		$('.error-tip');
	});
	$("[name=confirm_check_one]").focus(function(){
		$('.error-tip');
	});
	$("#recheckGoback").click(
		function(){
			Y.getCmp('wnd2').close();
			document.location.href = document.location.href;
	});
	
	

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
	
	
	
});
