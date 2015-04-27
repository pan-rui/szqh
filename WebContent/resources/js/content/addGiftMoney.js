define(function (require) {
    var Site = require('../comp/init.js');
    $('.fn-time').click(function () {
        WdatePicker({
            startDate: '%y-%M-01 HH:mm:ss',
            dateFmt: 'yyyy-MM-dd HH:mm:ss'
        });
    });


    $("input[name='r_time']").bind("click", function () {
        if ($(this).val() == "tzd") {
            $("#timeLimit").val("");
            $("#timeLimit").attr("disabled", true);
            $("#useStartDate").attr("disabled", false);
            $("#useEndDate").attr("disabled", false);
        } else {
            $("#useStartDate").attr("disabled", true);
            $("#useStartDate").val("");
            $("#useEndDate").attr("disabled", true);
            $("#useEndDate").val("");
            $("#timeLimit").attr("disabled", false);

        }
    });


    $('select[name=giveRuleType]').each(function(i,item){
        item.onchange=function(){
            var giveType = $(this).val();
            if(giveType =="REGISTER" ||  giveType == "AUTHENTICATION" || giveType =="REWARD" || giveType =="LOTTERY" ||giveType =="DIRECT"){
                register();

            }else{
                $(".span_new").show();
                $(".rule_xx").remove();

            }

            $("input[name='c_increase']:checked").removeAttr("checked");
        }
    });

    var giveType = $('select[name=giveRuleType]').val();
    if(giveType =="REGISTER" || giveType == "AUTHENTICATION" || giveType =="REWARD" || giveType =="LOTTERY" ||giveType =="DIRECT") {
        var giftId = $("#giftId").val();
        if(!giftId){
            register();
        }
        $(".span_new").hide();
    }

    function register(){
        $(".rule_xx").remove();
        $(".span_new").hide();
        var htmlText = '';
        htmlText = htmlText +
            '<div class="item fn-clear rule_xx"><input type="hidden" class="u-input w170 mask_money" name="giveRuleAmount" id="giveRuleAmount" /> ' +
             '&nbsp;&nbsp; 送&nbsp;&nbsp;<input type="text" class="u-input w170 mask_money" name="giveAmount" id="giveAmount"/>  元 &nbsp;&nbsp; </div>';
        $("#div_rule").after(htmlText);
        var giveType = $('select[name=giveRuleType]').val();
        if(giveType =="REWARD" || giveType =="LOTTERY" ||giveType =="DIRECT"){
            $(".rule_xx").hide();
            $("#giveRuleAmount").val(0);
            $("#giveAmount").val(0);
        }else{
            $(".rule_xx").show();
        }
        money();
    }


    $(".fn-new").bind("click",function(){
        var c_increase = $("input[name='c_increase']:checked");
        if(c_increase.length >0){
            if($(".rule_xx").length ==1){
                alert("亲，递增，只能新增一条规则!");
                return ;
            }
        }
        var htmlText = '';
         htmlText = htmlText + '  <div class="item fn-clear rule_xx" ><input type="text" class="u-input w170 mask_money" name="giveRuleAmount" id="giveRuleAmount" /> 元' +
             '&nbsp;&nbsp; 送&nbsp;&nbsp;<input type="text" class="u-input w170 mask_money" name="giveAmount" id="giveAmount"/>  元 &nbsp;&nbsp; <span><a href="javascript:;" class="fn-del">删除</a></span> </div>';
        $("#div_rule").after(htmlText);


        $(".fn-del").bind("click",function(){
            $(this).closest("div.rule_xx").remove();
        });

        money();

    })


    $("#c_increase").bind("click", function () {
        var checked =  $("[name = c_increase]:checkbox").attr("checked");
        if(checked){
            if($(".rule_xx").length >1){
                $("[name = c_increase]:checkbox").attr("checked",false);
                alert("亲，递增，只能新增一条规则!");
                return ;
            }
        }
    });


//    $("#amount").bind("change",function(){
//        var amount = $(this).val();
//        var giftNum = $("#giftNum").val();
//        $("#totalAmount").html(amount*giftNum);
//    })
//
//    $("#giftNum").bind("change",function(){
//        var giftNum = $(this).val();
//        var amount = $("#amount").val();
//        $("#totalAmount").html(amount*giftNum);
//    })

//    totalAmount();
//
//    function totalAmount(){
//        var giftNum = $("#giftNum").val();
//        var amount = $("#amount").val();
//        $("#totalAmount").html(amount*giftNum);
//
//        var giveType = $("#giveType").val();
//        if(giveType =="REGISTER"){
//            $("#span_giveAmount").hide();
//        }else{
//            $("#span_giveAmount").show();
//        }
//    }

    var addForm = $('#add_giftMoney');
    if (addForm.length) {
        addForm.validate({
            submitHandler: function () {
                addForm.ajaxSubmit({
                    success: function (res) {
                        alert(res.message);
                        if (res.code == 1) {
                            window.location.href = "/backstage/giftMoney/pageQueryGiftMoney";
                        }
                    }
                });
            }
        });
    }

    function money(){
        var  maskMoney = $('.mask_money');
        maskMoney.css('ime-mode','disabled');
        maskMoney.bind("copy cut paste", function (e) { // 通过空格连续添加复制、剪切、粘贴事件
            e.preventDefault(); // 阻止事件的默认行为
        });
        if (maskMoney.length > 0){
            maskMoney.keypress(function(event){

                var keyCode = event.which, value = $(this).val();
                if(/mask_only_number/.test(this.className) && keyCode === 46){
                    event.preventDefault();
                }
                if (keyCode === 0 || keyCode === 46 || keyCode === 8 || (keyCode >= 48 && keyCode <= 57)){
                    if (value.indexOf('.') !== -1){
                        if (keyCode === 46){
                            return false;
                        }
                        var _this=this;
                        var getCurserIndex = function(){
                            var oTxt1 = _this;
                            var cursurPosition=-1;
                            if(oTxt1.selectionStart){//非IE浏览器
                                cursurPosition= oTxt1.selectionStart;
                            }else{//IE
                                if(document.selection) {
                                    var range = document.selection.createRange();
                                } else {
                                    return -1;
                                }
                                range.moveStart("character",-oTxt1.value.length);
                                cursurPosition=range.text.length;
                            }
                            return cursurPosition;
                        }
                        var cursorIndex = getCurserIndex();
                        var content;
                        if(document.all)
                        {
                            if(document.selection) {
                                content = document.selection.createRange();
                            } else {
                                content = {};
                            }
                        }
                        else
                        {
                            content = window.getSelection();
                            content.text = content.toString();
                        }
                        var selectStr = content.text;
                        if (value.substring(value.indexOf('.') + 1).length > 1 && keyCode !== 8 && cursorIndex>value.indexOf('.') && keyCode!==0 && !selectStr.length){
                            return false;

                        }
                    }
                    return true;
                }
                else {
                    return false;
                }
            }).focus(function(){
                this.style.imeMode = 'disabled';
            });
        }

    }



    $(".fn-submit1").bind("click", function () {

        var giftName = $("#giftName").val();
        if(!$.trim(giftName)){
            alert("亲！请输入红包名称!");
            return;
        }
        var totalAmount = $("#totalAmount").val();
        if(!$.trim(totalAmount)){
            alert("亲！请输入红包总金额!");
            return;
        }





        var startDate = $("#startDate").val();
        if(!$.trim(startDate)){
            alert("亲！请选择活动开始时间!");
            return;
        }
        var endDate = $("#endDate").val();
        if(!$.trim(endDate)){
            alert("亲！请选择活动结束时间!");
            return;
        }

        var giveType = $('select[name=giveRuleType]').val();
        if(giveType !="REGISTER" && giveType !="AUTHENTICATION" && giveType !="REWARD"&&giveType !="LOTTERY"&& giveType !="DIRECT") {
            var giveRuleAmount = $('input[name=giveRuleAmount]');
            if(giveRuleAmount.length ==0){
                alert("亲！请添加发送规则!");
                return;
            }else{
                if(!$(giveRuleAmount[0]).val()){
                    alert("亲！请输入发送规则金额!");
                    return;
                }

                var giveAmount = $('input[name=giveAmount]');
                if(!$(giveAmount[0]).val()){
                    alert("亲！请输入送多少金额!");
                    return;
                }


            }
        }else{
          var giveAmount  = $("#giveAmount").val();
          if(!giveAmount && giveType !="REWARD" && giveType !="LOTTERY" && giveType !="DIRECT"){
              alert("亲！请输入送多少金额!");
              return;
          }
        }

        var  t = $("input[name='r_time']:checked").val();
        if(t =="tzd"){
            var useStartDate = $("#useStartDate").val();
            if(!$.trim(useStartDate)){
                alert("亲！请选择红包有效期的开始时间!");
                return;
            }

            var useEndDate = $("#useEndDate").val();
            if(!$.trim(useEndDate)){
                alert("亲！请选择红包有效期的结束时间!");
                return;
            }


            if(useStartDate < startDate){
                alert("亲！红包有效期的开始时间要大于活动的开始时间!");
                return;
            }

            if(useEndDate < endDate){
                alert("亲！红包有效期的结束时间要大于活动的结束时间!");
                return;
            }
        }else{
            if(!$.trim($("#timeLimit").val())){
                alert("亲！请输入红包有效期!");
                return;
            }

            var timeLimit = $.trim($("#timeLimit").val());
            if(!(/^[0-9]*[1-9][0-9]*$/.test(timeLimit)) || timeLimit< 0){
                alert("天数必须正整数!");
                return;
            }

        }

        var c_increase = $("input[name='c_increase']:checked");
        if(c_increase.length >0){
            $("#increase").val("Y");
        }else{
            $("#increase").val("N");
        }


        addForm.submit();
    });
});