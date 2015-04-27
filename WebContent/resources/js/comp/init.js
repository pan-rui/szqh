/**

 */


define(function(require, exports, module) {
  
	require('../plugins/jquery.jqtransform.js')($);
	require('../plugins/jquery.dateinput.js')($);
	require('../plugins/jquery.form2.js');
    require('../plugins/jquery.overlay.js')($);
    require('../plugins/jquery.validate.js')($);
	require('../module/validate.extend.js');
	require('../Y-all/Y-script/Y-base.js');
	require('../Y-all/Y-script/Y-window.js');
	//require('../content/leftBar.js');  
	require('../lib/NavigationRules');
	
	
	  NavigationRule.init({
	    activeCls: 'index-bg'
	  })
	  //home
	  .register('/', '.hl-menu a[href="/"]',true)
	  
	  .register('/loan/applyLoan', '.hl-menu a[href^="/loan/applyLoan"]','nemu-bg')
	  
	  //我要投资
	  .register('/boot/moreIndex/10', '.hl-menu a[href^="/boot/moreIndex/10"]','nemu-bg')
	  .register(/\/boot\/lookup\/\d+,\d+/, '.hl-menu a[href^="/boot/invest/10/1"]','nemu-bg')
	  .register(/\/boot\/invest\/\d+\/\d+/, '.hl-menu a[href^="/boot/invest/10/1"]','nemu-bg')
	  
	  .register('/userManage/userHome', '.hl-menu a[href^="/userManage/userHome"]')
	  
	 .register('/help/institutionIntroduction/', '.hl-menu a[href^="/help/institutionIntroduction/"]','nemu-bg')
	 
	 .register(/\/help\/infoCenter\?defaultMCode=[a-zA-Z0-9_]+&popId=\d+/, '.hl-menu a[href^="/help/infoCenter?defaultMCode=ABOUT_US&popId=0"]','nemu-bg')
	 
	 .register('/login/login', '.hl-menu a[href^="/userManage/userHome"]','nemu-bg')
	 .register('/userManage/balanceChangeRecord', '.hl-menu a[href^="/userManage/userHome"]')
	 .register('/userManage/accountSetting', '.hl-menu a[href^="/userManage/userHome"]')
	 .register('/tradeQuery/borrowingRecord', '.hl-menu a[href^="/userManage/userHome"]')
	 .register('/tradeQuery/entries/12/1', '.hl-menu a[href^="/userManage/userHome"]')
	 .register('/tradeQuery/borrowingRecord', '.hl-menu a[href^="/userManage/userHome"]')
	 .register('/boot/applyLoan', '.hl-menu a[href^="/boot/applyLoan"]','nemu-bg')
	 .register('/loan/applyDetails', '.hl-menu a[href^="/boot/applyLoan"]','nemu-bg')
	 .register('/withdrawals/launchWithdrawals', '.hl-menu a[href^="/userManage/userHome"]')
	 .register('/userManage/topUp/withholdingIndex', '.hl-menu a[href^="/userManage/userHome"]')
	 .register('/tradeQuery/borrowingRecord', '.hl-menu a[href^="/userManage/userHome"]')
	 .register('/loanQuery/loanSchedule', '.hl-menu a[href^="/userManage/userHome"]')
	 .register('/userManage/rechargeRecord', '.hl-menu a[href^="/userManage/userHome"]')
	 .register('/userManage/withdrawRecord', '.hl-menu a[href^="/userManage/userHome"]')
	 .register('/guaranteeCenter/guaranteeManager', '.hl-menu a[href^="/userManage/userHome"]')
	 .register('/repayDB/repayDBManager', '.hl-menu a[href^="/userManage/userHome"]')
	 .register('/guaranteeOperator/operatorManager', '.hl-menu a[href^="/userManage/userHome"]')
	 .register('/guaranteeOperator/addOperator', '.hl-menu a[href^="/userManage/userHome"]')
	 .register(/\/guaranteeOperator\/operatorInfo\?userBaseId=[a-zA-Z0-9]+/, '.hl-menu a[href^="/userManage/userHome"]')
	 .register('/userManage/userRealNameInfo', '.hl-menu a[href^="/userManage/userHome"]')
	 .register('/repay/repayManager', '.hl-menu a[href^="/userManage/userHome"]')
	 
	 .register(/\/investorManager\/salesList\?status=\d+/, '.hl-menu a[href^="/userManage/userHome"]')
	 .register('/investorManager/investorManage', '.hl-menu a[href^="/userManage/userHome"]')
	 .register(/\/investorManager\/investorInfo\?userBaseId=\d+/, '.hl-menu a[href^="/userManage/userHome"]')
	 .register(/\/investorManager\/profitAsignInfo\?userBaseId=\d+/, '.hl-menu a[href^="/userManage/userHome"]')
	 .register(/\/investorManager\/investorOpenAccount/, '.hl-menu a[href^="/userManage/userHome"]')
	 .register('/investorManager/investorOpenAccountSubmit', '.hl-menu a[href^="/userManage/userHome"]')
	 .register(/\/investorManager\/salesDetails\/\d+\/\d+\?investDetailId=\d+/, '.hl-menu a[href^="/boot/invest/10/1"]','nemu-bg')
	 .register(/\/tradeQuery\/queryInvestDetails\/\d+\?detailId=\d+/, '.hl-menu a[href^="/userManage/userHome"]')
	 .register(/\/help\/toModule\?moduleCode=[a-zA-Z0-9_]+/,'.hl-menu a[href^="/help/toModule?moduleCode=QUESTIONS"]')
	 .register(/\/help\/showDetail\?popId=\d+&moduleCode=[a-zA-Z0-9_]+/,'.hl-menu a[href^="/help/toModule?moduleCode=QUESTIONS"]')
	 
	 .register(/\/userGiftMoney\/pageQueryUse\?giftId=\d+/, '.hl-menu a[href^="/userManage/userHome"]')
	 .register(/\/repayQuery\/colletions\/\d+\/\d+/,'.hl-menu a[href^="/userManage/userHome"]')
	 .register(/\/repayQuery\/colletionsed\/\d+\/\d+/,'.hl-menu a[href^="/userManage/userHome"]')
	 .register(/\/investorManager\/investorInfo\?userBaseId=\d+/,'.hl-menu a[href^="/userManage/userHome"]')
	 .register(/\/investorManager\/profitAsignInfo\?userBaseId=\d+&pageNo=\d+&userName=&realName=/,'.hl-menu a[href^="/userManage/userHome"]')
	 .register(/\/investorManager\/investorOpenAccount/,'.hl-menu a[href^="/userManage/userHome"]')
	 .register(/\/marketingCenter\/brokerManage/,'.hl-menu a[href^="/userManage/userHome"]')
	 .register(/\/marketingCenter\/addBroker/,'.hl-menu a[href^="/userManage/userHome"]')
	 .register(/\/marketingCenter\/profitAssignInfoBroker\?userBaseId=\d+/,'.hl-menu a[href^="/userManage/userHome"]')
	 .register(/\/guaranteeCenter\/guaranteeDetails\?demandId=\d+&operate=[a-zA-Z0-9_]/, '.hl-menu a[href^="/boot/invest/10/1"]','nemu-bg')
	 .register(/\/guaranteeOperator\/operatorManager/,'.hl-menu a[href^="/userManage/userHome"]')
	 .register(/\/guaranteeOperator\/operatorInfo\?userBaseId=\d+/,'.hl-menu a[href^="/userManage/userHome"]')
	 .register('/userGiftMoney/pageQuery','.hl-menu a[href^="/userManage/userHome"]')
	 .register(/\/help\/infoCenter\?defaultMCode=[a-zA-Z0-9]+/,'.hl-menu a[href^="/help/infoCenter?defaultMCode=ABOUT_US"]')
	 .match();
    
  	//关闭按钮
	$('.close').off('click');
	$('.close').on('click', function(){
		$(this).parents('.f-layer').hide();
		$(this).parents('.n-fn-layer').hide();
		$(this).parents('.add-layer').hide();
		$('.overlay').remove();
		$('body').css('overflow', 'auto').removeClass('overlay-trigger');
	});
  
    
    var Site = {
		hover: function(obj){
				obj.hover(function(){
					$(this).addClass('hover')
				}, function(){
					$(this).removeClass('hover')
				})
		},
		
		/* select search */
		select_search: function(obj, form_obj){
			if (obj.length > 0){
				obj.each(function(){
					$(this).on('click', function(){
						form_obj.submit();
					})
					
				})

				
			}
			
		},

		init: function(){
			
			if ($('.default_form').length){
				$('.default_form').validate({
					errorClass: 'error-tip',
					errorElement: 'b',
					onkeyup: false
					
				});
				
			}
			
			//关闭按钮
			$('#btn_close').off('click');
			$('#btn_close').on('click', function(){
				$(this).parents('.f-layer').hide();
				$(this).parents('.n-fn-layer').hide();
				$(this).parents('.add-layer').hide();
			
				$('.overlay').remove();
				$('body').css('overflow', 'auto').removeClass('overlay-trigger');
			});
			
			if ($('.widget_date').length){
				$('.widget_date').dateinput();
			}
			var dateInputs = $('input[type=date]');
			if(dateInputs.length) {
				dateInputs.each(function(i,item){
					var dateconfig = {trigger: true, selectors:true};
					dateconfig.yearRange = [-10,15];
					if($(item).attr('yearRange')) {
						dateconfig.yearRange = eval($(item).attr('yearRange')) || [-10,15];
					}
				    $(item).dateinput(dateconfig);
				});
			    //dateInputs.dateinput({trigger: true, selectors:true, yearRange: [-10, 15]});
			}
			
			
			
			var $menu = $('.main-menu > ul > li'),
				$customForm = $('.jqtransform'),
				$button = $('button.button');
			if ($menu.length > 0){
				Site.hover($menu);
			}
			if ($button.length > 0){
				Site.hover($button);
			}
			if ($customForm.length > 0){
				$customForm.jqTransform();
			}
			
		
			if ($(".ul-show").length > 0){
				$(".ul-show").hover(function(){
					$('a', this).addClass("cur");
					$('ul', this).show();
				}, function(){
					$('a', this).removeClass("cur");
					$('ul', this).hide();
				});
				$(".ul-show").click(function(){
					$(".ul-show").removeClass('current');
					$(this).addClass('current');
				});
			}

			if ($('#more-h').length > 0){
				$('#more-h').click(function(){
					$('#more-layer').slideToggle("");
				});
			}
			if ($('#inq').length > 0){
				$('#inq').click(function(){
					$('#inq-layer').toggle();
					if($('#inq').attr('class')=='more-s'){
						$('#inq').attr('class','more-i');
						}
					else{
						$('#inq').attr('class','more-s');
						}
				});

			}
				

			
			
			
		}
	};
    
    /* 只能输入数字 */
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
	
    
	   var submitBtn = $('form').find('button[type=submit]');
	    if(submitBtn.length) {
	    	submitBtn.click(function(e){
	    		    e = e || event;
	    		    e.stopPropagation();
	    	});
	    	submitBtn.parent().click(function(e){
			    if($(this).hasClass('fn-g-btn') || $(this).hasClass('fn-h-btn')) {
				    var _this = this;
				    setTimeout(function(){
				        $(_this).parents('form').submit();
				    },1);
				}
	    	});
	    }
	    
	    var resetBtn = $('form').find('button[type=reset]');
	    if(resetBtn.length) {
	    	resetBtn.click(function(e){
	    		    e = e || event;
	    		    e.stopPropagation();
	    	});
	    	resetBtn.parent().click(function(e){
			    if($(this).hasClass('fn-g-btn') || $(this).hasClass('fn-h-btn')) {
				    var _this = this;
				    setTimeout(function(){
				        $(_this).parents('form')[0].reset();
				    },1);
				}
	    	});
	    }
   
	
  /* hide msg_box */
	var msgBox = $('#msg_box');
	if (msgBox.length > 0){
		$('body').click(function(){
			msgBox.hide();
			$('body').unbind('click');
		})
		
	}
	//协议toggle
	var btnLicense = $('#btn_license');
		
	if (btnLicense.length > 0){
		btnLicense.click(function(){
			if ($('.fm-license').is(':hidden')){
				$('.fm-license').show();
			}
			else {
				$('.fm-license').hide();
			}
		});
		$('.fm-license .close').click(function(){
			$('.fm-license').hide();
		});
	}
	//嵌套iframe
	
	var frmbtns = $(".fn-iframe");
	//alert(frmbtns.length);
	if(frmbtns.length) {
		frmbtns.click(function(){
			
			var container = $(".main-content");
			container.empty();
			var frm = $("<iframe>");
			container.append(frm);
			var path = $(this).attr("href");
			frm.attr({
				src: path,
				frameborder: 'no',
				border: '0',
				width:900,
				height:640
			});
			return false;
		});
	}
	
	Site.init();
	
	//动态生成初始input输入提示
    var startInput = $('.start_input');
		if(startInput.length) {
			startInput.each(function(i,item){				
				var tinput = $(item);
				var info = tinput.attr('startValue');
				var off = tinput.offset();
				var over = $('<div class="start_valueEle" for="'+tinput.attr('name')+'">').html("<em style='color:#aaaaaa;font-size:12px;  font-family:'宋体'>"+info || '请输入'+"</em>").css({
					position:'absolute',
					zIndex:10,
					left:off.left+20,
					top:off.top+5
				});
				$('body').append(over);
				
				function hideStart(){
					over.hide();
					tinput.get(0).focus();
				}
				function showStart(){
					over.show();
				}
				if(tinput.val()) {
					hideStart();
				}
				tinput.bind('focus',hideStart);
				over.click(hideStart);
				tinput.blur(function(){
					var val = $(this).val();
					if(!val) {
						showStart();
					}
				});
			});
		}
	
	return Site;
	
  
  
});