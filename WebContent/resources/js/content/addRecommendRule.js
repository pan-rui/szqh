define(function (require) {
    var Site = require('../comp/init.js');
    $("input[name='zt']").bind("click", function () {
        $("#status").val($(this).val()) ;
    });

    $("input[name='dx']").bind("click", function () {
        $("#rewardObj").val($(this).val()) ;
    });






    var addForm = $('#addRecommendRule');
    if (addForm.length) {
        addForm.validate({
            submitHandler: function () {
                addForm.ajaxSubmit({
                    success: function (res) {
                        alert(res.message);
                        if (res.code == 1) {
                            window.location.href = "/backstage/recommendRule/addRecommendRule";
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
        var fn = $("input[name='fn']:checked").val();
        if(fn=="t"){
            $("#rewardType").val($("select[name=jlType]").find("option:selected").val());
            $("#giveType").val($("select[name=giveTypeT]").find("option:selected").val());
            $("#giveAmount").val($("#giveAmount_t").val());
        }else if(fn=="o"){
            $("#rewardType").val("ONLY_INVEST");
            $("#giveType").val($("select[name=giveTypeO]").find("option:selected").val());
            $("#giveAmount").val($("#giveAmount_o").val());
        }

        $("#status").val($("input[name='zt']:checked").val());
        $("#rewardObj").val($("input[name='dx']:checked").val());

        addForm.submit();
    });
});