define(function (require) {
    var Site = require('../comp/init.js');
    require('../Y-all/Y-script/Y-imgplayer.js');
    require('../plugins/jquery.uploadify.js');
    require('../Y-all/Y-script/Y-htmluploadify.js');


    $(".fn-del").bind("click", function () {
        $(this).closest("tr").remove();
    });





    $(".m-table").find("input[name='prizeNum']").change(function () {
        var prizeNum = $(this).val();
        var zprizeNum = $("#zprizeNum").val();
        if(zprizeNum){
            var probability = prizeNum / zprizeNum;
            if(probability >1){
                alert("奖品数量和概率不匹配");
                return ;
            }
            $(this).closest("tr").find("input[name='probability']").val(probability);
        }else{
          var probability =  $(this).closest("tr").find("input[name='probability']").val();
          if(probability){
              $("#zprizeNum").val(prizeNum/probability);
          }
        }
    });






    $("#zprizeNum").change(function(){
        var zprizeNum = $(this).val();
        $(".m-table").find("input[name='prizeNum']").each(function(){
            var prizeNum = $(this).closest("tr").find("input[name='prizeNum']").val();
            if(prizeNum){
                var probability = prizeNum / zprizeNum;
                $(this).closest("tr").find("input[name='probability']").val(probability);
            }

        });
    });

    $(".m-table").find("input[name='probability']").change(function () {
        var probability = $(this).val();
        var zprizeNum = $("#zprizeNum").val();
        var prizeNum =  $(this).closest("tr").find("input[name='prizeNum']").val();
        if(zprizeNum){
            $(this).closest("tr").find("input[name='prizeNum']").val(probability * zprizeNum);
        }else{
            if(prizeNum){
                $("#zprizeNum").val(prizeNum/probability);
            }
        }
    });

   var i =0;

    $(".fn-new").bind("click", function () {
        i++;
        var htmlText = '<tr id=tr'+i+'>';
        htmlText = htmlText + '<td><input type="text" class="instyle" value="" name="seqNum"/></td>'
            + '<td><div class="instyle"><select name="prizeType" id="prizeType" class="instyle"><option selected value="REDPACKET">红包</option><option value="HIKE_VOUCHER">增益卷</option>                    <option value="USERPOINT">积分</option>'
            + '<option value="PHYSICAL">实物</option><option value="EXPERIENCE_AMOUNT">体验金</option>   </select>'
            + '</div></td><td><input type="text" class="instyle" value="" id="description" name="description"/></td><td><input type="text" class="instyle" value="" id="awards" name="awards"/></td>'
            + '<td><input type="text" class="instyle" value="" id="prizeNum" name="prizeNum"/></td>'
            + '<td><input type="text" class="instyle" value="" id="prizeAmount" name="prizeAmount"/></td>'
            + '<td><input type="text" class="instyle" value="" id="probability" name="probability"/></td><td class="fn-del" style="cursor: pointer;color: #0000ff">删除</td></tr>';
        $(".m-table").append(htmlText);
        $(".m-table").find("input[name='prizeNum']").change(function () {
            var prizeNum = $(this).val();
            var zprizeNum = $("#zprizeNum").val();
            $(this).closest("tr").find("input[name='probability']").val(prizeNum / zprizeNum);
        });

        $(".m-table").find("input[name='probability']").change(function () {
            var probability = $(this).val();
            var zprizeNum = $("#zprizeNum").val();
            $(this).closest("tr").find("input[name='prizeNum']").val(probability * zprizeNum);
        });

        $(".fn-del").bind("click", function () {
            $(this).closest("tr").remove();
        });


        money();
    });


    $("input[name='ac']").bind("click", function () {
        $("#winningRate").val($(this).val());
    });


    var addForm = $('#add_giftMoney');
    if (addForm.length) {
        addForm.validate({
            submitHandler: function () {
                addForm.ajaxSubmit({
                    success: function (res) {
                        alert(res.message);
                        if (res.code == 1) {
                            window.location.href = "/backstage/lottery/prizeRuleList";
                        }
                    }
                });
            }
        });
    }

    function money() {
        var maskMoney = $('.mask_money');
        maskMoney.css('ime-mode', 'disabled');
        maskMoney.bind("copy cut paste", function (e) { // 通过空格连续添加复制、剪切、粘贴事件
            e.preventDefault(); // 阻止事件的默认行为
        });
        if (maskMoney.length > 0) {
            maskMoney.keypress(function (event) {

                var keyCode = event.which, value = $(this).val();
                if (/mask_only_number/.test(this.className) && keyCode === 46) {
                    event.preventDefault();
                }
                if (keyCode === 0 || keyCode === 46 || keyCode === 8 || (keyCode >= 48 && keyCode <= 57)) {
                    if (value.indexOf('.') !== -1) {
                        if (keyCode === 46) {
                            return false;
                        }
                        var _this = this;
                        var getCurserIndex = function () {
                            var oTxt1 = _this;
                            var cursurPosition = -1;
                            if (oTxt1.selectionStart) {//非IE浏览器
                                cursurPosition = oTxt1.selectionStart;
                            } else {//IE
                                if (document.selection) {
                                    var range = document.selection.createRange();
                                } else {
                                    return -1;
                                }
                                range.moveStart("character", -oTxt1.value.length);
                                cursurPosition = range.text.length;
                            }
                            return cursurPosition;
                        }
                        var cursorIndex = getCurserIndex();
                        var content;
                        if (document.all) {
                            if (document.selection) {
                                content = document.selection.createRange();
                            } else {
                                content = {};
                            }
                        }
                        else {
                            content = window.getSelection();
                            content.text = content.toString();
                        }
                        var selectStr = content.text;
                        if (value.substring(value.indexOf('.') + 1).length > 1 && keyCode !== 8 && cursorIndex > value.indexOf('.') && keyCode !== 0 && !selectStr.length) {
                            return false;

                        }
                    }
                    return true;
                }
                else {
                    return false;
                }
            }).focus(function () {
                this.style.imeMode = 'disabled';
            });
        }

    }


    $(".fn-submit1").bind("click", function () {
        var ac = $("input[name='ac']:checked").val();
        if(ac == "1"){
            var probability =0;
            $(".m-table").find("input[name='probability']").each(function(){
                   probability = probability +parseFloat($(this).val());
            });
            if(probability != 1){
                alert("中奖率必须1");
                return;
            }

        }else{
            var probability =0;
            $(".m-table").find("input[name='probability']").each(function(){
                probability = probability +parseFloat($(this).val());
            });
            $("#winningRate").val(probability);
        }

        addForm.submit();
    });
});