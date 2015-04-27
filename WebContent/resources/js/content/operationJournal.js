define(function(require) {
    var Site = require('../comp/init.js');
    require('../plugins/jquery.box.js');
    require('../plugins/jquery.combobox.js');
    require('../plugins/jquery.window.js');
    $('.fn-time').click(function() {
        WdatePicker({
            startDate : '%y-%M-01 HH:mm:ss',
            dateFmt : 'yyyy-MM-dd HH:mm:ss'
        });
    });


});