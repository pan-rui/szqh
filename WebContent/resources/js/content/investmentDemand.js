define(function(require) {
	var Site = require('../comp/init.js');
	require('../content/fileUpload.js');
	require('../plugins/jquery.box.js');
	require('../plugins/jquery.combobox.js');
	require('../plugins/jquery.window.js');
	$('.fn-time').click(function() {
		WdatePicker({
			startDate : '%y-%M-01 HH:mm:ss',
			dateFmt : 'yyyy-MM-dd HH:mm:ss'
		});
	});

    $('#changeStatusForm').find('.fn-ok').click(function() {
        var url = '/backstage/investmentDemand/changeStatus';
        var investmentDemandId = $("#investmentDemandId").val();
        var status = $("#changeStatus").val();
        var demandId = $("#demandId").val();
        var data = {'status' : status, 'investmentDemandId' : demandId};
        var result = $_GLOBAL.ajax(url, data);
        if(result.code == 1){
            alert(result.message);
            window.location.href = window.location.href;
        }else{
            alert(result.message);
        }

    });

    $("[name ='changeStatusA']").click(function(){
        var demandId = $(this).attr("investmentDemandId");
        $("#demandId").val(demandId);
        $('body').window({
            content : '#changeStatusDiv',
            simple : true,
            closeEle : '.u-btn-gray'
        });
    });
	

});