define(function (require, exports, module) {
    require("../comp/init");
    /*	require("../Y-all/Y-script/Y-countdown");
     require('../Y-all/Y-script/Y-imgplayer.js');
     require('../lib/calculator2.js');*/
    $.extend($.fn, {
        formatMoney: function (money) {
            money /= 100;
            money += '';
            var x = money.split('.');
            var x1 = x[0];
            var x2 = x.length > 1 ? '.' + x[1] : '';
            var rgx = /(\d+)(\d{3})/;

            while (rgx.test(x1)) {
                x1 = x1.replace(rgx, '$1' + ',' + '$2');
            }
            return x1 + x2;
        }
    });

    function nextDateForMonth(date, monthI) {
        var yyyy = date.getFullYear(), month = date.getMonth(), day = date.getDate(), Month = monthI + date.getMonth();
        if (Month >= 12) {
            yyyy++;
            month = (Month - 12);
        }
        return new Date(yyyy, month, day);
    }

    function nextDateByString(str, monthI) {
        if (str == undefined || str == '') return;
        var component = str.split('-', 3);
        return nextDateForMonth(new Date(component[0], parseInt(component[1]) - 1, component[2]), monthI).toLocaleDateString();
    }

    function formateDate(date) {
        return date.getFullYear() + '-' + (date.getMonth() + 1) + '-' + date.getDate() + ' ' + date.getHours() + ':' + date.getMinutes() + ':' + date.getSeconds();
    }

    function setVal(el, tr, data, fn) {
        el.remove('tr');
        if (!isNaN(tr)) {
            tr = '<tr>'
            for (var i = 0; i < tr; i++) {
                tr += '<td></td>'
            }
            tr += '</tr>';
        }
        for (var i = 0; i < data.length; i++) {
            var j = 0, Tr = tr;
            for (var da in data[i]) {
                if (fn == undefined || fn == null)
                    $(Tr).find('td').eq(j).text(data[i][da]);
                else
                    $(Tr).find('td').eq(j).text(fn(da, data[i][da]));
                console.log(Tr);
                j++;
            }
            el.append(Tr);
        }
    }

    function getRealValue(key, value) {
        if (key.lastIndexOf('id') == key.length - 2 || key.lastIndexOf('Id') == key.length - 2)
            return "<a href='/userManage/repayPlanDetail?id=" + value + "' class='' >查看详情</a>|<a href='' class=\"\">收益分配</a>";
        if (value == null)
            return '';
        else if (isNaN(value))
            return value;
        else(isNaN(value))
        return $.formatMoney(value);
    }

    function setContent(el, tr, data, fn) {
        $.ajax({
            url: data.url,
            type: 'POST',
            dataType: 'json',
            data: data,
            success: function (result) {
                if (result.pageList.length > 0) {
                    $('#principal').text($.formatMoney(result.principal));
                    $('#income').text($.formatMoney(result.income));
                    $('#amount').text($.formatMoney(result.amount));
                    $('#count').text(result.count);
                    setVal(el, tr, data, fn);
                    /*                    el.remove('tr');
                     for (var da in result.pageList) {
                     var html = "<tr>\n" +
                     "                                                <td>" + result.pageList[da].trade_name + "</td>\n" +
                     "                                                <td>" + result.pageList[da].repay_date + "</td>\n" +
                     "                                                <td>" + $.formatMoney(result.pageList[da].amount) + "</td>\n" +
                     "                                                <td>" + $.formatMoney(result.pageList[da].repay_principal_amount) + "</td>\n" +
                     "                                                <td>" + $.formatMoney(result.pageList[da].income) + "</td>\n" +
                     "                                                <td><a href='/userManage/repayPlanDetail?id=" + result.pageList[da] + "' class=\"\">查看详情</a>|<a href='' class=\"\">收益分配</a></td>\n" +
                     "\t</tr>";
                     el.append(html);
                     }*/
                }
            }
        });
    }
    function refresh(data) {
        var flag = $('[data-page=pb1]').is(':hidden');
        if (flag)
            data.status = 'PAYID,PAYISD';
        else
            data.status = 'NOTPAY';
        if (data.startDate == undefined || data.startDate == '')
            data.startDate = new Date().toLocaleDateString().replace('/', '-');
        switch ($('#interval').val()) {
            case 'one':
                data.endDate = nextDateByString(data.startDate, 1).replace(/\//g, '-');
                break;
            case 'three':
                data.endDate = nextDateByString(data.startDate, 3).replace(/\//g, '-');
                break;
            case 'year':
                data.endDate = nextDateByString(data.startDate, 12).replace(/\//g, '-');
        }
        $.ajax({
            url: data.url,
            type: 'POST',
            dataType: 'json',
            data: data,
            success: function (result) {
                if (result.pageList.length > 0) {
                    $('#principal').text($.formatMoney(result.principal));
                    $('#income').text($.formatMoney(result.income));
                    $('#amount').text($.formatMoney(result.amount));
                    $('#count').text(result.count);
                    for (var da in result.pageList) {
                        var html = "<tr>\n" +
                            "                                                <td>" + result.pageList[da].trade_name + "</td>\n" +
                            "                                                <td>" + result.pageList[da].repay_date + "</td>\n" +
                            "                                                <td>" + $.formatMoney(result.pageList[da].amount) + "</td>\n" +
                            "                                                <td>" + $.formatMoney(result.pageList[da].repay_principal_amount) + "</td>\n" +
                            "                                                <td>" + $.formatMoney(result.pageList[da].income) + "</td>\n" +
                            "                                                <td><a href='/userManage/repayPlanDetail?id=" + result.pageList[da] + "' class=\"\">查看详情</a>|<a href='' class=\"\">收益分配</a></td>\n" +
                            "\t</tr>";
                        if (flag) {
                            $('[data-page=pb2] tbody').remove('tr');
                            $('[data-page=pb2] tbody').append(html);
                        }
                        else {
                            $('[data-page=pb1] tbody').remove('tr');
                            $('[data-page=pb1] tbody').append(html);
                        }
                    }
                }
            }
        });
    }
    $(document).on('click', '#pb1,#pb2,#pbdata', $_GLOBAL.pageData, function (event) {
        console.log($_GLOBAL.pageData);
        event.data.startDate = $('input[name=startDate]').val();
        event.data.endDate = $('input[name=endDate]').val();
        event.data.interval = $('#interval').val();
        switch ($(this).attr('id')) {
            case 'pb1':
                event.data.status = 'NOTPAY';
                setContent($('[data-page=pb1] tbody'), 7, event.data, getRealValue);
                /*                $.ajax({
                 url: event.data.url,
                 type: 'POST',
                 dataType: 'json',
                 data: event.data,
                 success: function (result) {
                 if (result.pageList.length > 0) {
                 $('#principal').text($.formatMoney(result.principal));
                 $('#income').text($.formatMoney(result.income));
                 $('#amount').text($.formatMoney(result.amount));
                 $('#count').text(result.count);
                 $('[data-page=pb1] tbody').remove('tr');
                 for (var da in result.pageList) {
                 var html = "<tr>\n" +
                 "                                                <td>" + result.pageList[da].trade_name + "</td>\n" +
                 "                                                <td>" + result.pageList[da].repay_date + "</td>\n" +
                 "                                                <td>" + result.pageList[da].repay_division_way + "</td>\n" +
                 "                                                <td>" + $.formatMoney(result.pageList[da].amount) + "</td>\n" +
                 "                                                <td>" + $.formatMoney(result.pageList[da].repay_principal_amount) + "</td>\n" +
                 "                                                <td>" + $.formatMoney(result.pageList[da].income) + "</td>\n" +
                 "                                                <td><a href='/userManage/repayPlanDetail?id=" + result.pageList[da] + "' class=\"\">查看详情</a>|<a href='' class=\"\">收益分配</a></td>\n" +
                 "\t</tr>";
                 $('[data-page=pb1] tbody').append(html);
                 }
                 }
                 }
                 });*/
                $('[data-page]').removeClass('on');
                $('[data-page=pb1]').addClass('on').show();
                $('[data-page=pb2]').hide();
                break;
            case 'pb2':
                event.data.status = 'PAYID,PAYISD';
                setContent($('[data-page=pb2] tbody'), 6, event.data,getRealValue );
/*                $.ajax({
                    url: event.data.url,
                    type: 'POST',
                    dataType: 'json',
                    data: event.data,
                    success: function (result) {
                        if (result.pageList.length > 0) {
                            $('#principal').text($.formatMoney(result.principal));
                            $('#income').text($.formatMoney(result.income));
                            $('#amount').text($.formatMoney(result.amount));
                            $('#count').text(result.count);
                            $('[data-page=pb2] tbody').remove('tr');
                            for (var da in result.pageList) {
                                var html = "<tr>\n" +
                                    "                                                <td>" + result.pageList[da].trade_name + "</td>\n" +
                                    "                                                <td>" + result.pageList[da].repay_date + "</td>\n" +
                                    "                                                <td>" + $.formatMoney(result.pageList[da].amount) + "</td>\n" +
                                    "                                                <td>" + $.formatMoney(result.pageList[da].repay_principal_amount) + "</td>\n" +
                                    "                                                <td>" + $.formatMoney(result.pageList[da].income) + "</td>\n" +
                                    "                                                <td><a href='/userManage/repayPlanDetail?id=" + result.pageList[da] + "' class=\"\">查看详情</a>|<a href='' class=\"\">收益分配</a></td>\n" +
                                    "\t</tr>";
                                $('[data-page=pb2] tbody').append(html);
                            }
                        }
                    }
                });*/
                $('[data-page]').removeClass('on');
                $('[data-page=pb1]').hide();
                $('[data-page=pb2]').addClass('on').show();
                break;
            default:
                refresh(event.data);
                break;
        }
        return false;
    });

    $('#pb1').click();
    exports.formateDate = formateDate;
    exports.nextDateForMonth = nextDateForMonth;
    exports.nextDateByString = nextDateByString;
    exports.setVal = setVal;
    exports.refreshData=refresh;
    exports.setContent=setContent;
});