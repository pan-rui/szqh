define(function(require) {
    var Site = require('../comp/init.js');


    $(".status").click(function(){
        var giftId = $(this).attr("giftId");
        var status = $(this).attr("status")
        var message = "确定要启用红包吗？启用后冻结红包账户金额，且不能修改！";
        if(status == "GAMEOVER"){
            message = "确定要手动结束活动吗？";
        }
        if(window.confirm(message)){
            var url = '/backstage/giftMoney/changeGiftMoneyStatus';
            var data = {giftId : giftId,status:status};
            var result = $_GLOBAL.ajax(url, data);
            alert(result.message);
            if(result.code ==0){
                return;
            }
            window.location.href = "/backstage/giftMoney/pageQueryGiftMoney";
        }
    });

});