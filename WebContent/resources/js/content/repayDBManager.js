define(function(require) {
	var Site = require('../comp/init.js');
	require('../Y-all/Y-script/Y-window.js');
    require('../Y-all/Y-script/Y-tip.js');
	require("../Y-all/Y-script/Y-countdown");
    require('../content/chineseAmountExchange.js');


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



    $('#getCode1').click(function() {
        var business = $("#business1").val();
        var mobile = $("#mobile").val();
        var countdown = Y.getCmp('getCode1');
        sendMobile(business, mobile, countdown);
    });


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

    $('#confirm_pay').click(function (){

        if(checkCode()){


            $('body').Y('Window',{
                content:"<span><img src='/resources/images/common/loadingScroll.gif' /><br/>还款处理中，请您耐心等候...</span>",
                key:'lodding',
                simple:true
            });

            var url = '/repayDB/repayDB';
            var data = {tradeId : $('#tradeId').val(),repayPlanId:$("#repayPlanId").val(), smsCode: $('#code1').val(),business:$("#business1").val(),mobile: $("#mobile").val()};

            var result = $_GLOBAL.ajax(url, data);
            Y.getCmp('lodding').close();
            if('1' == result.code){
                var repayDialog =  Y.all["repayDialog"];
                if(repayDialog){
                    repayDialog.close();
                }
                alert(result.message);
                window.location.href = "/repayDB/repayDBManager";

//                toPage(1,"waitDBRepayManage","waitTable","waitPage");
//                toPage(1,"doneDBRepay","doneTable","donePage");
            }else{
                alert(result.message);

            }

        }
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
		
		
	
});
