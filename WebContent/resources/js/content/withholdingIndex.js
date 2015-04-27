define(function(require){
	require("../comp/init");
	require("../Y-all/Y-script/Y-msg");
	require("../Y-all/Y-script/Y-tip");
	require("../Y-all/Y-script/Y-countdown");
	require("../Y-all/Y-script/Y-selectarea");

  	$('#a001').removeClass("index-bg"); 
	 $('#a002').removeClass("nemu-bg");  
	 $('#a003').addClass("nemu-bg");  
	 $('#a004').removeClass("nemu-bg");  
	 $('#a005').removeClass("nemu-bg"); 
 
	/*充值*/
	$('#funSelect a').click(function(){
		$('#funSelect a').removeClass('select-on');
		$(this).addClass('select-on');
		var fun = $(this).attr('fun');
		$('#curType').val(fun);
		if(fun == 'daikou'){
			$('.daikouDiv').show();
			
			$('#fn-submit-a').unbind('click').bind('click',function(){
				
				$(".error-tip").remove();
				
				if($("#bankCode").val()==""){
					//Y.alert("请点击银行图片,选择充值银行");
					$(".cyber-bank").append("<b class='error-tip'>请点击银行图片,选择充值银行</b>");
					return false;
				}
				
				if($("#account-in").val()==""||$("#account-in").val()==0){
					//Y.alert("请输入充值金额");
					$(".money").append("<b class='error-tip'>请输入充值金额</b>");
					return false;
				}
				if(!!$(".checkbox").attr("checked")){
				
				}else{
					//Y.alert("请阅读资金管理规定");
					$("#btn_license").append("<b class='error-tip'>请阅读投资人承诺书</b>");
					return false;
				}
				
				$(".error-tip").remove();
				
				$(this).attr('href','javascript:;');
				$('body').Y('Window',{
					simple: true,
                    key:"wbndwindow",
					content: '#wbndwindow',
					closeEle: '.u-close,.consider',
					show: function(){
						$('input[name=rechargeAmount]').val($('#account-in').val());
						
					}
				});
				return false;
			});
			
		} else {
			$('.daikouDiv').hide();
			$('#fn-submit-a').unbind('click').bind('click',function(){
				if(!!$(".checkbox").attr("checked")){
					
				}else{
					//Y.alert("请阅读资金管理规定");
					$("#btn_license").append("<b class='error-tip'>请阅读投资人承诺书</b>");
					return false;
				}
				$(this).attr('href','/userManage/rechargePage?rechargeAmount='+$("#rechargeAmount").val()).attr('target','_blank');
			});
			
		}
	}).eq(0).trigger('click');
	
	$('#getCode1').click(function(){
		var countdown = Y.getCmp('getCode1');
		$.ajax({
			url: '/anon/sendSmsCode',
			data: {business: 'deposit'},
			dataType: 'json',
			success: function(res){
				if(res.code!=1) {
					Y.alert(res.message);
					countdown.close();
					return;
				}
				$("#sendCode").val('yes');
			}
		});
	});

    if(!canPayPass){
        $("#btnConfirm").click(function(){
            if($("#validateCode").val()!="")
            {
                if($("#sendCode").val()!="yes")
                {
                    alert("请发送验证码！");
                }
                else
                {
                    deductForm1.submit();
                    //window.setTimeout(deductForm1.submit(),100);
                }
            }
            else
            {
                alert("请输入验证码！");
            }
            return false;
        });
    }

    window.confirmClick = function(key){
        $('input[name=paytk]').attr('value',key);
        $('body').Y('Window',{
            content:"<span>处理中，请您耐心等候...</span>",
            key:'lodding',
            simple:true
        });
        deductForm1.submit();
    }
	

	
	$('#account-in').keyup(function(){
		var val = $(this).val();
        if(!val) return;
		var str = Y.TranslateTip.prototype.digitUppercase(val);
        $(".balance-money").find("span").html("金额大写：");
		$('i.money').html(str);
	}).trigger('keyup');
	
	$(".bank-state").click(function(){
		$(".bank-state").each(function(index){
			$(this).removeClass("signing");
		});
		$(this).addClass("signing");
		$("#bankCode").val($(this).attr("bankcode"));
		renderLimit1($(this));
		
	}); 
	
	
	$('#addForm').validate({
		errorClass: 'error-tip',
		errorElement: 'b',
		errorPlacement: function(err,ele){
			var name = ele.attr('name');
			if(name == 'bankProvince' || name == 'bankCity') {
				$('.Y-selectarea').parent().append(err);
			} else if(name='bankCardNo') {
				ele.after(err);
			} 
			else {
				ele.parent().append(err);
			}
		},
		submitHandler: function() {
			
			$('#submitBtn').html("处理中...").attr('disabled',true);
			
			$('#addForm').ajaxSubmit({
				dataType:'json',
				success: function(res){
					if(res.code == 1) {
						window.location.reload(true);
					} else {
						Y.alert(res.message);
					}
					$('#submitBtn').html("添加").attr('disabled',false);
				}
			});
		},
		rules:{
			bankCode: 'required',
			bankProvince: 'required',
			bankCity: 'required',
			bankCardNo: {
				required: true,
				number: true,
				maxlength: 30,
				minlength: 10
			}
		},
		messages: {
			bankCode: '请选择银行',
			bankProvince: '请选择省',
			bankCity: '请选择市',
			bankCardNo: {
				required: '请输入银行卡号',
				number: '银行卡号必须是数字'
			}
		},
		onkeyup: false,
		ignore: ''
	});
	
	$(".TranslateTip_fix").each(function(){
		var _this = $(this);
		 Y.create('TranslateTip',{
			 key:'translatetip1',
			 target:_this,
			 content:'<div></div>',
			 //simple:true,
			 translateType:['fmoney',4,0],//第一个数字是格几位数字加逗号默认3 第二个数字是保留的小数位数 默认2 间隔符号 默认空格
			 fixedString: 'xxxx xxxx xxxx xxxx xxxx',
			 spacing:5,
			 align:'top'
	    }); 
	});
	
	$('#btn_license').click(function(){
		$('body').Y('Window',{
			simple: true,
			content: '.fm-license',
			closeEle: '.close,[type=reset]'
		});
	});
	
	$('.add-bank').click(function(){
        if("true"==$(this).attr('data')){
            $('body').Y('Window',{
                simple: true,
                content: '.add-card',
                closeEle: '.close,[type=reset]'
            });
        }else{
            window.location.href ="/userManage/topUp/signRedirect";
        }

	});
	
	var bankObj = $('.choose-bank');
	if (bankObj.length > 0) {
		var bankObjList = bankObj.find('.bank-list').find('li');
		$('.select-box', bankObj).find('img').attr('src',
				bankObjList.eq(0).find('img').attr('src')).attr('alt',
				bankObjList.eq(0).find('img').attr('alt'));

		$('#bank_id').val(bankObjList.eq(0).find('span').html());
		$('#bank_url').val(bankObjList.eq(0).find('img').attr('src'));

		$('.select-right', bankObj).click(function(e) {
			e.preventDefault();
			bankObj.find('.bank-list').toggle();
		});

		bankObjList.hover(function() {
			$(this).addClass('cur')
		},function() {
			$(this).removeClass('cur')
		});

		
		$('body').click(function(e) {
			if (bankObj.length && !$(e.target).hasClass('select-right')) {
				bankObj.find('.bank-list').hide();
			}
		});

		bankObjList.click(function() {
			var src = $(this).find('img').attr('src');
			var alt = $(this).find('img').attr('alt');
			$('.select-box', bankObj).find('img').attr('src', src).attr('alt',alt);
			bankObj.find('.bank-list').hide();
			$('[name=bankType]').val($(this).attr('t'));
			$('#addBankCode').val(alt);
			renderLimit($(this));
			
			
		});
		if($('input[name=bankType]').val()) {
		
			var startBankCode = $('input[name=bankType]').val();
			bankObjList.filter('[t='+startBankCode+']').triggerHandler('click');
		} else bankObjList.eq(0).click();
		
		
		
	}
	
	
	function renderLimit(o){
		var ta = $(o).find('img').attr('timeAmount');
		var da = $(o).find('img').attr('dayAmount');
		var memo = $(o).find('img').attr('memo');
		var tab = $('#bankLimit');
		tab.find('.cont td').eq(1).html(ta);
		tab.find('.cont td').eq(2).html(da);
		tab.find('.cont td').eq(3).html(memo);
	}

	function renderLimit1(o){
		var ta = $(o).find('#img1').attr('timeAmount');
		var da = $(o).find('#img1').attr('dayAmount');
		var memo = $(o).find('#img1').attr('memo');
		var tab = $('#bankLimit1');
		tab.find('.cont1 td').eq(1).html(ta);
		tab.find('.cont1 td').eq(2).html(da);
		tab.find('.cont1 td').eq(3).html(memo);
	}

    $('.del').click(function() {
        var delUrl = $(this).attr('delUrl');
        Y.confirm('请选择','确定删除该银行？',function(opn) {
            if (opn == "yes") {
                $.ajax({
                    url: delUrl,
                    success: function (res) {
                        if (res.code == 1) {
                            window.location.reload(true);
                        } else {
                            Y.alert(res.message);
                        }
                    }
                });
            }});

    });
    //显示限额
	var length  = $('#idSlider li').length;
	if(length>1){
		$('#idSlider').find('li:first').find('.bank-state').addClass("signing");
		$("#bankCode").val($('#idSlider').find('li:first a').attr("bankcode"));
		
		var ta1 = $('#idSlider').find('li:first').find('img').attr('timeAmount');
		var da1 = $('#idSlider').find('li:first').find('img').attr('dayAmount');
		var memo1 = $('#idSlider').find('li:first').find('img').attr('memo');
		$('#bankLimit1').find('.cont1 td').eq(1).html(ta1);
		$('#bankLimit1').find('.cont1 td').eq(2).html(da1);
		$('#bankLimit1').find('.cont1 td').eq(3).html(memo1);
	}
	
});