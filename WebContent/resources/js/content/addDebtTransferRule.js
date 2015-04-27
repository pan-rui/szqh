define(function (require) {
    var Site = require('../comp/init.js');


    var addForm = $('#addDebtTransferRule');
    if (addForm.length) {
        addForm.validate({
            submitHandler: function () {
                addForm.ajaxSubmit({
                    success: function (res) {
                        alert(res.message);
                        if (res.code == 1) {
                            window.location.href = "/backstage/debtTransfer/addDebtTransferRule";
                        }
                    }
                });
            }
        });
    }





    $(".fn-submit1").bind("click", function () {
           addForm.submit();
    });
});