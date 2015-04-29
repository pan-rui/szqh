define(function (require, exports, module) {
    var Site = require('../comp/init.js');
    require('../Y-all/Y-script/Y-msg.js');
    require('../plugins/jquery.window.js');
    require('../Y-all/Y-script/Y-msg.js');
    require('../zeroclipboard/jquery.zclip.min.js');

    $('#a001').removeClass("index-bg");
    $('#a002').removeClass("nemu-bg");
    $('#a003').addClass("nemu-bg");
    $('#a004').removeClass("nemu-bg");
    $('#a005').removeClass("nemu-bg");
    $('#money-add').click(function () {
        show();
    });
    var wndEle;

    function show() {
        wndEle = Y.create('Window', {
            content: '#money',
            simple: true,
            closeEle: '#money-close'
        });
        wndEle.show();
    }

    $('#jscopy').zclip({
        path: '/resources/swf/ZeroClipboard.swf',
        copy: function () {
            return $('#content').val();
        }
    });

    $("#money-submit").click(function () {
        var _this = this;
        var money = $('[name=m]').val();
        var reg = new RegExp("^[1-9][0-9]*$");
        if (reg.test(money)) {
            Y.confirm('请选择', '确定？', function (opn) {
                if (opn == "yes") {
                    wndEle.close();
                    /*	$.ajax({
                     url : '',
                     type : 'post',
                     dataType : 'json',
                     data : {
                     money:money
                     },
                     success : function(res) {
                     $('#money').close();
                     alert(11);

                     }
                     })*/
                }
            })
        } else {
            alert("请输入正确的数字");
        }
    })
    $("#addbankLink").click(function () {
        window.location.href = "/bank/signBankCard";
    });

    var this_timer0, this_timer1, this_timer2, this_timer3;

    function setTimers() {
        var count0 = 0;
        var str = "查询中";
        this_timer0 = setInterval(function () {
            $("#avBalance").html(str + Array(count0++ % 3 + 2).join('.'));
        }, 500);
        var count1 = 0;
        this_timer1 = setInterval(function () {
            $("#balanceTd").html(str + Array(count1++ % 3 + 2).join('.'));
        }, 500);
        var count2 = 0;
        this_timer2 = setInterval(function () {
            $("#avBalanceTd").html(str + Array(count2++ % 3 + 2).join('.'));
        }, 500);
        var count3 = 0;
        this_timer3 = setInterval(function () {
            $("#frzAmountTd").html(str + Array(count3++ % 3 + 2).join('.'));
        }, 500);
    }

    //setTimers();

    function clearTimers() {
        clearInterval(this_timer0);
        clearInterval(this_timer1);
        clearInterval(this_timer2);
        clearInterval(this_timer3);
    }

    function queryAccountInfo(obj) {
        var url = '/userManage/queryAccountInfo';
        var data = {};
        var result = $_GLOBAL.ajax(url, data);
        if (result.code == 1) {
            var banlance = result.balance;
            var freezeAmount = result.freezeAmount;
            var availableBalance = result.availableBalance;
            clearTimers();
            if ("ref" == obj) {
                alert("刷新成功");
            }
            $("#avBalance").text(availableBalance);
            $("#balanceTd").text(banlance);
            $("#avBalanceTd").text(availableBalance);
            $("#frzAmountTd").text(freezeAmount);
        } else {
            alert(result.message + ",请点击刷新重新获取");
        }
        if (result.userStatus == "W") {
            $(".paper-on").text("激活易极付账户");
        }
        else {
            var realNameAnth = result.realNameAuth;
            if ("IS" == realNameAnth) {
                $(".paper-on").text("已认证");
            } else if ("NO" == realNameAnth) {
                $(".paper-on").text("认证未通过");
            } else if ("IN" == realNameAnth) {
                $(".paper-on").text("认证中");
            } else {
                $(".paper-on").text("未认证");
            }
        }

    }

    $("#avBalance").prev().before('<a id="refreshLink" href="javascript:;"> 刷新</a>');
    //加载资金信息
    //queryAccountInfo();
    $("#refreshLink").click(function () {
        setTimers();
        queryAccountInfo("ref");
    });
    /**
     *
     * @param cert 实名状态 1.IS 通过(弱) 2.ISS(强)
     * @param mobileBind  1.IS 通过
     * @param emailBing  1.IS 通过
     */
    function getSecurityLevel(cert, mobileBind, emailBing) {
        var auths = $('#auths').children('.icon-base01');
        var var0 = 0.0;
        var range = [0, 0.3, 0.6, 0.8], result = ['危险', '低', '中', '高'], resultCss = ['low', 'low', 'mid', ''];
        if (cert == 'IS') {
            $(auths[0]).removeClass('os').addClass('os-active');
            var0 += 0.3;
        } else if (cert == 'ISS') {
            $(auths[0]).removeClass('os').addClass('os-active');
            var0 += 0.6;
        }
        ;
        if (mobileBind == 'IS') {
            $(auths[1]).removeClass('phone').addClass('phone-active');
            var0 += 0.2;
        }
        if (emailBing == 'IS') {
            $(auths[2]).removeClass('email').addClass('email-active');
            var0 += 0.2;
        }
        for (var i in range) {
            if (i < range.lenth - 1) {
                if (range[i] <= var0 < range[i + 1])
                    return {level: result[i], css: resultCss[i]};
            } else if (var0 >= range[i]) {
                return {level: result[i], css: resultCss[i]};
            }
        }
    };
    exports.getSecurityLevel = getSecurityLevel;
    //(function(){
    var userInfo = $('#user-info');
    var result = getSecurityLevel(userInfo.attr('cert'), userInfo.attr('mobileBind'), userInfo.attr('emailBind'));
    var safe = $('#safeLevel');
    safe.html(safe.html() + result.level);
    safe.find('.s-c').children().addClass(result.css);

    $("#jscopy").zclip({
        path: '/resources/swf/ZeroClipboard.swf',
        copy: $("input[name=links]").val(),
        beforeCopy: function () {
            console.log("复制之前");
        },
        afterCopy: function () {
            alert("复制成功:\n" + $("input[name=links]").val());
        }
    });
//})();
});