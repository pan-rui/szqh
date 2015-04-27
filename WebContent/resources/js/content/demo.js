/**
 * @fileoverview tab组件，支持click, hover事件
 * @author yangle | yorsal.coms
 * @created  2012-05-09
 * @updated  2012-05-09
 */


define(function(require, exports, module) {
  require('../comp/init.js');
  
  require('../Y-all/Y-script/Y-imgplayer.js');
    require('../Y-all/Y-script/Y-msg.js');
  Y.create('ImgPlayer',{
		eleArr:'#guaranteeLicenseUrl_Img',
		titleInfo: 'alt',
		content:'',
		pathInfo: function(){
		  return $(this).attr('src');
		}
	});
  
	require('../Y-all/Y-script/Y-tip.js');
	var verify = $('.new_captcha');
	require('../content/securityPassword.js')(210,31);
	require('../comp/security.js');
	var newcaptcha = $('#newcaptcha');
	verify.click(function() {
		var img = new Image();
		img.src = '/anon/getImgCode?dateTag=' + new Date().getTime();
		img.onload = function(){
			var obj = $(img).addClass('code-img vt new_captcha');
			obj.attr('title',newcaptcha.attr('title')).css({width:60,height:20});
			newcaptcha.after(obj);
			newcaptcha.remove();
			obj.click(function(){
				verify.click();
			})
			newcaptcha = obj;
		}
	});
	$('[name=userName]').val(getCookie("userName"));
	var login_form = $('#login_form');
	
	$('#submit-a').click(function() {
		var needCode=$('.needCode').val();
		if(needCode=="true"){
			var captcha = $("#verifyCode").val();
			var url = "/login/validateCaptcha";
			var data = {'captcha' : captcha};
			var result = $_GLOBAL.ajax(url, data);
			if(1!=result.code){
				$(".validIcon").removeClass("correctCircleIcon");
				$(".validIcon").addClass("failureCircleIcon");
				$(".err").text("验证码校验失败！");
				$(".err").css("display", "inline-block");
				return false;

			}
		}		
		login_form.submit();
		return false;
	})

	login_form.submit(function(){
		return validateForm();
	});
	
	//加密部分--------------------------
	var pwd="";
	var modulus="";
	var exponent="";
	function setHiddenPwd(){
		var loginPwd = $('#passwords').val();
		var key = RSAUtils.getKeyPair(exponent, '', modulus);
		pwd =  RSAUtils.encryptedString(key, loginPwd);
	}
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
	
	var whithControl = "0";
	$('input:radio').click(function(){
		whithControl = $('input:radio:checked').val();
		if(whithControl=='1'){
			$('.withControl').css('display','none');
			$('.withOutControl').css('display','block');	
			
		}else{
			$('.withControl').css('display','block');
			$('.withOutControl').css('display','none');
		}
	}); 
	var securityData="";
	function validateForm(){
		var user = $('input[name=userName]');
		var whithControl0=$('#whithControl').val();
		if(whithControl0!=null){
			whithControl=whithControl0;
		}
		if(whithControl=='0'){
			securityData = getSecurityData();
		}else{
			setHiddenPwd();
			securityData=pwd;
		}
		if ($.trim(user.val()) === '' || securityData === 'invalid'){
			$('.err').eq(0).html('请填写账户或密码，密码不小于6位!');
			$('.err').show();
			return false;
		}
		$('#password').val(securityData);
		setCookie("userName", $('[name=userName]').val());
//		$.getCookie("name");
//		$.deleteCookie("name");
		return true;
	}
	$("#verifyCode").Y('ToolTip',{
	    content:'验证码为全英文字符, 不区分大小写',
	    align: 'top'
	});
	
	$("#verifyCode").blur(
			function (){
				var captcha = $(this).val();
				var url = "/login/validateCaptcha";
				var data = {'captcha' : captcha};
				var result = $_GLOBAL.ajax(url, data);
				if(1==result.code){
					$(".validIcon").removeClass("failureCircleIcon");
					$(".validIcon").addClass("correctCircleIcon");
				}else{
					$(".validIcon").removeClass("correctCircleIcon");
					$(".validIcon").addClass("failureCircleIcon");
				}
			}		
	);
	
	/*
	jQuery cookie
	*/
	 function setCookie (sName, sValue, oExpires, sPath, sDomain, bSecure) {
		var sCookie = sName + '=' + encodeURIComponent(sValue);
		if (oExpires) {
			sCookie += '; expires=' + oExpires.toGMTString();
		};
		if (sPath) {
			sCookie += '; path=' + sPath;
		};
		if (sDomain) {
			sCookie += '; domain=' + sDomain;
		};
		if (bSecure) {
			sCookie += '; secure';
		};
		document.cookie = sCookie;
	};
	
	 function getCookie(sName) {
		var sRE = '(?:; )?' + sName + '=([^;]*)';
		var oRE = new RegExp(sRE);
		if (oRE.test(document.cookie)) {
			return decodeURIComponent(RegExp['$1']);
		} else {
			return null;
		};
	}
	
	function deleteCookie(sName, sPath, sDomain) {
		this.setCookie(sName, '', new Date(0), sPath, sDomain);
	}
	

});