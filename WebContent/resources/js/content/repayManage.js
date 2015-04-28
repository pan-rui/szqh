define(function(require,exports,module) {
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
        var yyyy, month, day, Month = monthI + date.getMonth();
        if (Month >= 12) {
            yyyy = date.getFullYear() + 1;
            month = (Month - 12) + 1;
        }
        return new Date(yyyy, month, date.getDate());
    }

    function formateDate(date) {
        return date.getFullYear() + '-' + (date.getMonth() + 1) + '-' + date.getDate() + ' ' + date.getHours() + ':' + date.getMinutes() + ':' + date.getSeconds();
    }

    $.on('click', '#pb1,#pb2,#pbdata', {
        url: '/userManage/mjr/repayManage',
        pageNo: 1,
        pageSize: 10,
        startDate: $('input[name=startDate]').val(),
        endDate: $('input[name=endDate]').val(),
        interval: $('#interval').val()
    }, function (data) {
        console.log(data.startDate);
        console.log(data.endDate);
        switch ($(this).attr('id')) {
            case 'pb1':
                data.status = 'NOTPAY';
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
                });
                $('[data-page=pb1]').show();
                $('[data-page=pb2]').hide();
                break;
            case 'pb2':
                data.status = 'PAYID,PAYISD';
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
                });
                $('[data-page=pb1]').hide();
                $('[data-page=pb2]').show();
                break;
            default:
                var flag = $('[data-page=pb1]').is(':hideen');
                if (flag)
                    data.status = 'PAYID,PAYISD';
                else
                    data.status = 'NOTPAY';
                switch ($('#interval').val()) {
                    case 'one':
                        data.startDate = new Date();
                        data.endDate = nextDateForMonth(data.startDate, 1);
                        break;
                    case 'three':
                        data.startDate = new Date();
                        data.endDate = nextDateForMonth(data.startDate, 3);
                        break;
                    case 'year':
                        data.startDate = new Date();
                        data.endDate = nextDateForMonth(data.startDate, 12);
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
                break;
        }

    });
    $('#pb1').click();
exports.formateDate=formateDate;

});