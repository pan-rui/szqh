//担保机构js
define(function(require) {
	var Site = require('../comp/init.js');
	require('../Y-all/Y-script/Y-window.js');
	require('../Y-all/Y-script/Y-msg.js');
	require('../content/securityPassword.js')(180,24);
	require('../Y-all/Y-script/Y-imgplayer.js');
	require('../comp/security.js');
	
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
		e.empty();
	});
	
	$('#confirm_pay').click(function (){
		if(checkPayPassword()){
			var url = '/tradeQuery/manualReimbursement';
			var data = {tradeId : $('#tradeId').val(), demandId : $('#demandId').val(), payPassword : $('#payPassword').val(), token : $('#token').val()};
			var result = $_GLOBAL.ajax(url, data);
			alert(result.message);
			var backUrl =  document.location.href;
			backUrl = backUrl.substring(0, backUrl.lastIndexOf("=")+1);
			backUrl = backUrl + "xq";
			document.location.href = backUrl;
		}
	});
	

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
	$('#loanAuditFinish').click(function (){
		$('body').Y('Window', {
			content : '#checkPasswordCheckContainer',
			key:'wnd1',
			close:function(){
				$('#checkPasswordOne').val("");
			}
		});
	});
	
	function checkCheckPassword(obj){
	 	var status = false;
		var e = $('.error-tip');
		e.empty();
		var url = '/guaranteeCenter/authPasswordCheck';
		var data;
		if("lvOne" == obj){
			var pwd1 = $('#checkPasswordOne').val();
		    if( pwd1==""){
		    	e.append('请输入审核密码');
		    	return false;
		    }
			data = {'pwd' : pwd1,'type' : "lvOne"};
		}else{
			var pwd2 = $('#checkPasswordTwo').val();
		    if( pwd2==""){
		    	e.append('请输入审核密码');
		    	return false;
		    }
			data = {'pwd' : pwd2,'type' : "lvTwo"};
		}
		var result = $_GLOBAL.ajax(url, data);
		if('1' == result.code){
			status = true;
		}else{
			e.append(result.message);
		}
		return status;
	}
	
	$('#confirm_check_one').click(function(){
		if(checkCheckPassword("lvOne")){
			Y.getCmp('wnd1').close();
			$('[name=checkPassword]').val("");
			if("MAKELOAN"==$('#checkType').val()){
				var url = '/guaranteeCenter/guaranteeMakeLoanAuditing';
				var data = {demandId : $('#demandId').val(), tradeId : $('#tradeId').val()};
				var result = $_GLOBAL.ajax(url, data);
				if(result.code==1){
					$('body').Y('Window', {
						content : '#recCheckPasswordContainer',
						key:'wnd2',
						close:function(){
							$('#checkPasswordTwo').val("");
						}
					});
				}else{
					alert(result.message);
				}
			}else{
				$('body').Y('Window', {
					content : '#recCheckPasswordContainer',
					key:'wnd2',
					close:function(){
						$('#checkPasswordTwo').val("");
					}
				});
			}
		}
		
	});
	
	$('#confirm_check').click(function(){
		if(checkCheckPassword("lvTwo")){
			if("DEPLOY"==$('#checkType').val()){
				var url = '/guaranteeCenter/guaranteeDeployAuditing';
				var data = {demandId : $('#demandId').val(),  token : $('#token').val()};
				var result = $_GLOBAL.ajax(url, data);
				alert(result.message);
				document.location.href = document.location.href;
			}else{
				var url = '/tradeQuery/guaranteeAuditingFinish';
				var data = {tradeId : $('#tradeId').val(),  token : $('#token').val()};
				var result = $_GLOBAL.ajax(url, data);
				alert(result.message);
				document.location.href = document.location.href;
			}
			
		}
		
	});
	
	function checkPayPassword(){
	 	var status = false;
		var e = $('.error-tip');
		e.empty();
		var securityData = getSecurityData();
	    if( $.trim(securityData) === 'invalid'){
	    	e.append('支付密码不能为空或少于6位');
	    	return false;
	    }
	    $('#payPassword').val(securityData)
		var url = '/userManage/validationPayPassword';
		var data = {'payPassword' : securityData};
		var result = $_GLOBAL.ajax(url, data);
		if('1' == result.code){
			status = true;
		}else{
			e.append(result.message);
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
			$('body').Y('Window', {
				content : '#checkPasswordCheckContainer',
				key:'wnd1',
				close:function(){
					$('#checkPasswordOne').val("");
				}
			});
	});
});
