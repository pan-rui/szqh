
function getRepayPlanId(replanId){
	 var t = $('#ih');
	 var f = $('#f');
	 var fd = $('#fd');	
	 t.empty();
	 f.empty();
	 fd.empty();
	$.ajax({
		url:'/guaranteeCenter/getPlanId/'+replanId,
		type:'post',
		dataType:'json',
		async:false,
		success:function(json){
			 var html = '';
			f.append(json.message);
			fd.append(json.amoutCN);
			html+='<input type="hidden" id="tradeId"  name="tradeId"  value='+json.tradeId+'></input>';	
			html+='<input type="hidden" id="repayPlanId"  name="repayPlanId"  value='+json.repayPlanId+' ></input>';				
			html+='<input type="hidden" id="token"  name="token"  value='+json.token+' ></input>';	
			t.append(html);
			},
		error:function(){
			alert("出错了!");
		} 
	});	

}

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
	$('.u-close,.consider').on('click', function() {
		Y.getCmp('getCode1').close();
	});
	$('.payLoan').click(function(){
		var repayPlanId=$(this).attr("repayPlanId");
		getRepayPlanId(repayPlanId);
		$('body').Y('Window', {
            key:"payPasswordCheckContainer",
            title:'还款',
			content : '#payPasswordCheckContainer',
			closeEle : '.u-close,.consider'
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
			
			var url = '/guaranteeCenter/guaranteeRepayMoney';
			var data = {tradeId : $('#tradeId').val(), repayPlanId : $('#repayPlanId').val(),smsCode: $('#code1').val(),business:$("#business1").val(),mobile: $("#mobile").val(),token : $('#token').val()};
			var result = $_GLOBAL.ajax(url, data);
			Y.getCmp('lodding').close();
			if('1' == result.code){
				alert(result.message);
//				var backUrl =  document.location.href;
//				backUrl = backUrl.substring(0, backUrl.lastIndexOf("=")+1);
//				backUrl = backUrl;
				document.location.href =' /guaranteeCenter/repayPlanGuranetee/12/1';
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

        var url = '/guaranteeCenter/guaranteeRepayMoney';
        var data = {tradeId : $('#tradeId').val(), repayPlanId : $('#repayPlanId').val(),smsCode: $('#code1').val(),business:$("#business1").val(),mobile: $("#mobile").val(),token : $('#token').val(),paytk:paytk};

        var result = $_GLOBAL.ajax(url, data);
        Y.getCmp('lodding').close();
        if('1' == result.code){
            window.location.href = "/guaranteeCenter/repayPlanGuranetee/12/1";
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
