define(function (require) {
    var Site = require('../comp/init.js');
    $('.fn-time').click(function () {
        WdatePicker({
            startDate: '%y-%M-01 HH:mm:ss',
            dateFmt: 'yyyy-MM-dd HH:mm:ss'
        });
    });


    var lotteryType = $('select[name=lotteryType]').val();
    if(lotteryType == "LOTTERY"){
        $(".SIGNUP").css('display','none');
        $(".reward").css('display','block');
    }else{
        $(".SIGNUP").css('display','block');
        $(".reward").css("display",'none');
    }

    var add_lotteryActivity = $('#add_lotteryActivity');
    if (add_lotteryActivity.length) {
    	add_lotteryActivity.validate({
			errorClass: 'error-tip',
			errorElement: 'b',
			errorPlacement: function(error, element) {
				element.after(error);
			},
            submitHandler: function () {
                add_lotteryActivity.ajaxSubmit({
                    success: function (res) {
                        alert(res.message);
                        if (res.code == 1) {
                            window.location.href = "/backstage/lottery/activityList";
                        }
                    }
                });
            },


			
			onkeyup : false
        });
    }

   //-----------------------------------页面触发效果------------------------------------------
   
    $('select[name=lotteryType]').each(function(i,item){
        item.onchange = function(){
            var lotteryType = $(this).val();
            if(lotteryType =="SIGNUP"){
            	$(".SIGNUP").css('display','block');
                $(".reward").css("display",'none');
            }else{
            	$(".SIGNUP").css('display','none');
                $(".reward").css('display','block');

            }
        }
    });


    $('.fn-submit').click(function(){
        add_lotteryActivity.submit();
    });


    $('select[name=lotteryConditionType]').each(function(i,item){
        item.onchange = function(){
            var lotteryConditionType = $(this).val();
            if(lotteryConditionType =="INVESTMENT"){
            	$(".isLimitAmount").css('display','block');
                $(".limitAmount").css('display','block');
            }else{
            	$(".isLimitAmount").css('display','none'); 
            	$(".limitAmount").css('display','none'); 

            }
        }
    });

    var lotteryConditionType = $('select[name=lotteryConditionType]').val();
    if(lotteryConditionType =="INVESTMENT"){
        $(".isLimitAmount").css('display','block');
        $(".limitAmount").css('display','block');
    }else{
        $(".isLimitAmount").css('display','none');
        $(".limitAmount").css('display','none');

    }

});