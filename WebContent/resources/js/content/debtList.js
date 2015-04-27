define(function(require) {
    var Site = require('../comp/init.js');
    require('../content/chineseAmountExchange.js');
//	require('../Y-all/Y-script/Y-htmlupload.js');
//	require('../content/fileUpload.js');
    require('../Y-all/Y-script/Y-selectarea.js');
    require('../Y-all/Y-script/Y-selectbranch.js');
    require('../Y-all/Y-script/Y-msg.js');
//	require('../plugins/jquery.box.js');
    require('../plugins/jquery.window.js');
//	require('../plugins/jquery.combobox.js');
    require('../Y-all/Y-script/Y-countdown.js');
    require('../content/securityCenter.js');



    $('.cancelTransfer').click(function (){
        var tradeId = $(this).attr("tradeId");
        var tradeTransferId = $(this).attr("tradeTransferId");
        Y.confirm('请选择','确定执行该操作？',function(opn){
            if(opn == 'yes') {
                $.ajax({
                    url : '/tradeQuery/entries/cancelTransfer',
                    type : 'post',
                    dataType : 'json',
                    data : {
                        bizNo : tradeId,
                        tradeTransferId : tradeTransferId
                    },
                    success : function(res) {
                        alert(res.message),
                            location.reload()
                    }
                });
            }
        });
    });

    $('#transfer').click(function (){
        $('body').Y('Window',{
            content:"<span>处理中，请您耐心等候...</span>",
            key:'lodding',
            simple:true
        });
        window.location.href="/tradeQuery/entries/12/1/2";

    });

    $('#transfering').click(function (){
        $('body').Y('Window',{
            content:"<span>处理中，请您耐心等候...</span>",
            key:'lodding',
            simple:true
        });
        window.location.href="/tradeQuery/entries/transferingSubmit";


    });

    $('#transfered').click(function (){
        $('body').Y('Window',{
            content:"<span>处理中，请您耐心等候...</span>",
            key:'lodding',
            simple:true
        });
        window.location.href="/tradeQuery/entries/transferedSubmit";
    });

    $('[name=transMoney]').bind("change",function() {
        $.ajax({
            url: '/tradeQuery/entries/transferChargeMoney',
            dataType: 'json',
            type:'post',
            data: {
                transferMoney: $("#transMoney").val(),
                tradeDetailId:$("#tradeDetailId").val()
            },
            success: function (res) {
                if (res.code == 1) {
                    $("#transCharge").html(res.message.standardString);
                    $("#chargeMoney").val(res.message.amount);
                    $("#sb").removeAttr("disabled");
                } else {
                    $("#sb").attr("disabled", true);
                    alert(res.message);
                }
            }, error: function (e) {
            }
        })
    });



});