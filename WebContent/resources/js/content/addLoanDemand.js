define(function(require) {
	var Site = require('../comp/init.js');
	require('../content/chineseAmountExchange.js');
	require('../Y-all/Y-script/Y-imgplayer.js');
	require('../content/fileUpload.js');
	
	var stateType;
 	liveUplodify('upfile'); 	
	var _attach;

    function money(){
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

    }


    $(".fn-new").bind("click",function(){
        var c_increase = $("input[name='c_increase']:checked");
        if(c_increase.length >0){
            if($(".rule_xx").length ==1){
                alert("亲，递增，只能新增一条规则!");
                return ;
            }
        }
        var htmlText = '';
        htmlText = htmlText + ' <div class="rule_xx">  投资满 <input type="text" class="mask_money" name="investGiftMoneyRule"/> 元 使用'
            +'<input type="text"  class="mask_money"  name="useGiftMoneyRule"/> 元 &nbsp;&nbsp; <span><a href="javascript:;" class="fn-del">删除</a></span> <br><br></div>';
        $("#div_rule").after(htmlText);


        $(".fn-del").bind("click",function(){
            $(this).closest("div.rule_xx").remove();
        });

        money();

    })
	
	function liveUplodify(id){
	    var input = $('#'+id);
	    input.uploadify({
			height        : 31,
			width         : 160,
			buttonText : '<span class="u-btn u-btn-gray">选择上传图片</span>',
			fileTypeExts  : '*.jpg; *.jpeg; *.bmp;  *.png',  
			multi           : true,
			queueSizeLimit: '5',
			auto: true,
			queueID: 'queueDiv',
			swf           : '/resources/swf/uploadify.swf?tag='+new Date().getTime(),
			uploader      : '/upload/imagesUpload;jsessionid='+ $_GLOBAL.sessionID,		 	 
			fileSizeLimit : '4MB',
			scriptData: { JSESSIONID: $_GLOBAL.sessionID},
			//formData		: 'oldFilePath',
			onInit: function () {   
	         },
			onUploadSuccess : function(file, data, response) {
	            var info = $.parseJSON(data);
			    if(!info) return;
			    $('.uploadimg').attr('src',info.resData);  
				var _img = $('<img>');
				var imgcss = {
				      width: '50px',
				      height: '50px'
				};
				_img.attr('src',info.resData);
				_img.css(imgcss);
				_img.attr('serverPath',info.serverPath);
				
				_attach.parent().parent().append(_img);
				alert(_img.printArea());
			},
	
			onUploadError : function(file, errorCode, errorMsg, errorString) {
	            
			}, 
			onQueueComplete:function(queueData){
				var successs = queueData.uploadsSuccessful;
				var errors = queueData.uploadsErrored;
				var allnum = input.data('fileNum');
				if(successs >= allnum || errors > 0) {
					//submitUpload();
				}
			},
			onDialogClose : function(swfuploadifyQueue){
				input.data('fileNum',swfuploadifyQueue.queueLength);
			},
		    onCancel : function(file) {		    	
		    }
		});			
	}
	
	$('.attach').click(function(){
		Y.create('Window',{
			content: '.upload-scan',
			title: '上传扫描件',
			key: 'uplodWin'
		}).show();
		_attach = $(this);
	});
	
	$('.upcancel,.loanChecckSubmit').click(function(){
		Y.getCmp('uplodWin').close();
	});	
	
	
	$('#radio002_1').blur(function() {

		if($("#radio002").prop("checked")){
			if($("#radio002_1").val().trim().match(/^\d+$/) && $("#radio002_1").val().substr(0,1)!=0){
				$(".error-tip").remove();
			}else{
				$(".error-tip").remove();
				$("#radio002_1").parent().append("<b class='error-tip'>请认真输入天数</b>");
				//alert("请认真输入天数");
				return false;
			}
		}
	});
	
    // 验证值必须大于特定值(不能等于)
    jQuery.validator.addMethod("lt", function(value, element, param) {
        return value <= $(param).val();
    });

    jQuery.validator.addMethod("gt", function(value, element, param) {
        return value >= $(param).val();
    });
	 Y.create('ImgPlayer',{
			eleArr:'#guaranteeLicenseUrl_Img',
			titleInfo: 'alt',
			content:'',
			pathInfo: function(){
			  return $(this).attr('src');
			}
		});
	$('.time').click(function() {
		WdatePicker({
			startDate : '%y-%M-01 HH:mm:ss',
			dateFmt : 'yyyy-MM-dd HH:mm:ss'
		});
	});
	
	function formatFloat(src, pos)
	{
	    return Math.round(src*Math.pow(10, pos))/Math.pow(10, pos);
	}
	
	//$('textarea[name=loanNote]').xheditor({});
	var editor= KindEditor.create('textarea[name=loanNote],textarea[name=loanStatement],textarea[name=guaranteeStatement],textarea[name=info3],textarea[name=sponsorStatement]',{
		uploadJson : '/upload/imagesUpload.htm;jsessionid='+ $_GLOBAL.sessionID,
        fileManagerJson : '/upload/imagesUpload.htm;jsessionid='+ $_GLOBAL.sessionID,
        allowFileManager : true,
        filterMode:false,
        afterBlur: function(){this.sync();}
	});
	//$('input[name=realName]').attr("disabled", true);
	$('input[name=userName]').blur( function(){
		var _this=$(this).val();
		$.ajax({
			url : '/backstage/getRealName',
			type : 'post',
			dataType : 'json',
			data : {
				userName : _this,
				roleId : 13
			},
			success : function(res) {
				if(res.code==1){
					if(res.message!=null){
						$('input[name=realName]').val(res.message);
						
						$('input[name=loanerUserName]').val(res.message);
						if(res.userType=="GR"){
							$('.qiYe').hide();
							$('.geRen').show();
						}else if(res.userType=="JG"){
							$('.qiYe').show();
							$('.geRen').hide();
						}
					}else{
						$('input[name=realName]').val("");
						$('input[name=loanerUserName]').val("");
					}
					
				}else{
					$('input[name=realName]').val("");
					$('input[name=loanerUserName]').val("");
				//	$('#user_Name').after("<font color='red'>"+res.message+"</font>");
				}
				
			},error:function(e){
				console.log(e);
			}
		})
	});
	
	$('input[name=timeLimitUnit]').click(function(){
		var sel = $(this).parent().parent().find('select');
		var allsel =  $(this).parent().parent().parent().find('select');
		sel.parent().parent().parent().parent().find('.jqTransformSelectOpen').hide();
		sel.parent().find('.jqTransformSelectOpen').show();
		allsel.attr('disabled',true);
		sel.removeAttr('disabled');
	});
	
	$('input[name=saturationConditionMethod]').click(function(){
		var inp = $(this).parent().find('input[type=text]');
		var allinp = $(this).parent().parent().find('input[type=text]');
		allinp.attr('disabled',true);
		inp.removeAttr('disabled');
	});
	
	//$("#investInterestRate").change(function(){
	//	var investInterestRate = $("#investInterestRate").val();
	//	if($.trim(investInterestRate) == ""){
	//		investInterestRate = 0;
	//	}
	//	$("#loanInterest").text(formatFloat(parseFloat(investInterestRate),2));
	//});
	function templateChange(obj){
		var curId = obj;
		$.ajax({
			url : '/backstage/queryRuleInfo',
			type : 'post',
			dataType : 'json',
			data : {
				name : $("#"+curId).val()
			},
			success : function(res) {		
				//p.text(res.message);
				//sel.after("<p class="u-tip mt5">"+res.message+"</p>")
				var loanInterest1 = $("#templateRate1").val();
				var loanInterest2 = $("#templateRate2").val();
				if($.trim(loanInterest1) == ""){
					loanInterest1 = 0;
				}
				if($.trim(loanInterest2) == ""){
					loanInterest2 = 0;
				}
				
				var investRate1 = $("#investRate1").val();
				var investRate2 = $("#investRate2").val();
				if($.trim(investRate1) == ""){
					investRate1 = 0;
				}
				if($.trim(investRate2) == ""){
					investRate2 = 0;
				}
				var totalInterestRate = 0;
				var totalInvestRate = 0;
				if(curId == "divisionTemplateId1"){
					$("#s1").text(res.message);
					$("#templateRate1").val(parseFloat(res.loanInterest));
					$("#investRate1").val(parseFloat(res.investorInterest));
					totalInterestRate = parseFloat(loanInterest2) + parseFloat(res.loanInterest);
					totalInvestRate = parseFloat(investRate2) + parseFloat(res.investorInterest);
				}else{
					$("#templateRate2").val(parseFloat(res.loanInterest));
					$("#investRate2").val(parseFloat(res.investorInterest));
					totalInterestRate = parseFloat(loanInterest1) + parseFloat(res.loanInterest);
					totalInvestRate = parseFloat(investRate1) + parseFloat(res.investorInterest);
					$("#s2").text(res.message);
				}
				$("#loanInterest").val(formatFloat(totalInterestRate, 2));
				$("#investInterestRate").val(formatFloat(totalInvestRate, 2));
			},error:function(e){
				console.log(e);
			}
		})
	}
	document.getElementById("divisionTemplateId1").onchange = function(){
		templateChange("divisionTemplateId1");
	}
	document.getElementById("divisionTemplateId2").onchange = function(){
		templateChange("divisionTemplateId2");
	}
    $('input[name=divisionTimeLimitUnit]').click(function(){
		var sel = $(this).parent().parent().find('select');
		//var p=$(this).parent().parent().find('p').last();
		var allsel =  $(this).parent().parent().parent().find('select');
		sel.parent().parent().parent().parent().find('.jqTransformSelectOpen').hide();
		sel.parent().find('.jqTransformSelectOpen').show();
		//allsel.attr('disabled',true);
		//sel.removeAttr('disabled');
		var s=$(this).next().text();
		sel.get(0).onchange = function(){
			//	alert(sel.val())
			$.ajax({
				url : '/backstage/queryRuleInfo',
				type : 'post',
				dataType : 'json',
				data : {
					name : sel.val()
				},
				success : function(res) {		
					//p.text(res.message);
					//sel.after("<p class="u-tip mt5">"+res.message+"</p>")
					var investInterestRate = $("#investInterestRate").val();
					if($.trim(investInterestRate) == ""){
						investInterestRate = 0;
					}
					var totalInterestRate = parseFloat(investInterestRate) + parseFloat(res.loanInterest);
					if(s=="筹资阶段："){
						$("#s1").text(res.message);
						$("#loanInterest").text(totalInterestRate)
					}else{
						$("#s2").text(res.message);
						$("#loanInterest").text(totalInterestRate)
					}
				},error:function(e){
					console.log(e);
				}
			})
		}
	});
   
	/** 验证发布借款需求FORM表单 */
	var addForm=$('#add_loandemand_form');
	if(addForm.length){
		addForm.validate({
			errorClass: 'error-tip',
			errorElement: 'b',
			errorPlacement: function(error, element) {
				if(element.attr('name') == 'userName'||element.attr('name') == 'interestRate'){
					element.next().after(error);
				}else if(element.attr('name') == 'loanAmount'){
					element.next().next().after(error);
				}else if(element.attr('name') == 'dimensions'||element.attr('name') == 'timeLimitUnit'){
					element.parent().parent().after(error);
				}else if(element.attr('name') == 'timeLimitUnit'||element.attr('name') == 'saturationConditionMethod'){
					element.parent().after(error);
				}else{
					element.after(error);
				}
			},
			rules : {
				userName : {
					required : true,
					customRemote : {
						url : '/backstage/checkBorrower',
						customError : function(element, res) {
							return res.message;
						}
					}
				},
				loanName : {
					required : true
				},
				dimensions : {
					required: true,
					noZh:true
				},
				loanAmount : {
					required:true,
					number : true,
					firstNum : true
				},
				interestRate : {
					required:true,
					number : true
				},
				loanPurpose : {
					required:true
				},
				deadline : {
					required:true,
                    gt:"#investAvlDate"
				},
				investAvlDate : {
					required:true,
                    lt:"#deadline"
				},
				timeLimitUnit : {
					required:true
				},		

				contractTemplateId:{
					required:true,
				},
				receiptTemplateId:{
					required:true,
				},
				
				saturationConditionMethod : {
					required:true
				},
				guaranteeLicenceNo: {
					required:true,
					customRemote : {
						url : '/backstage/checkguaranteeLicenceNo',
						customError : function(element, res) {
							return res.message;
						}
					}
				},
				guaranteeLicenceName : {
					required:true
				}
			},
			messages: {
				userName:{
					required: "请输入借款人用户名"
				},
				loanName : {
					required: "请输入借款标题"
				},
				dimensions : {
					required: "请选择贷款规模",
					noZh:'请选择贷款规模'
				},
				loanAmount : {
					required:'请输入金额',
					number : '输入数字',
					firstNum : '第一个数字必须大于0'
				},
				interestRate : {
					required:'请输入年利率',
					number : '输入数字'
				},
				loanPurpose : {
					required:'请输入融资用途'
				},
				deadline : {
					required:"请选择截止时间",
                    gt:"截止日期要大于可投资时间"
				},
				investAvalibleTime : {
					required:"请选择可投资时间"
				},
				timeLimitUnit : {
					required:'请选择期限'	
				},		
				
				
				contractTemplateId:{
					required:'请选择合同模板'
				},
				receiptTemplateId:{
					required:'请投资凭证模板'
				},
				
				
				saturationConditionMethod : {
					required:'请选择满条件'
				},
				guaranteeLicenceNo: {
					required:"请输入担保函编号"
				},
				guaranteeLicenceName : {
					required:"请输入担保函名称"
				},
                investAvlDate:{
                    required:"请输入可投资时间",
                    lt:"可投资时间要小于截止日期"
                }

			},
			submitHandler:function(){
				addForm.ajaxSubmit({
		    		success:function(res){
		    			alert(res.message);
		    			if(res.code == 1){
		    				if('wite'==$('input[name=status]').val()){
			    				window.location.href="/backstage/pageQueryLoanDemand?module=WITE";
			    			}else{
			    				window.location.href="/backstage/pageQueryLoanDemand?module=DRAFT";
			    			}
		    			}
		    		}
		    	});
			}
		});
	}
	
	function setImageValue()
	{
		for(var i=0;i<$('.attach').length;i++)
		{
			var parentA=$('.attach').eq(i);
			var code=parentA.attr('code');
			var _imgs1 = $('.attach').eq(i).parent().parent().find('img');
			var _attachPaths="";
			for(var j = 0;j<_imgs1.length;j++){
				_attachPaths += ';'+_imgs1.eq(j).attr('src')+','+_imgs1.eq(j).attr('serverPath');
			}
			$('#pathHiddenId_'+(i+1)).val(_attachPaths);
		}
	}
	
	
	$('.fn-submit1').click(function(){
		$('input[name=status]').val('wite');

		if($("#radio002").prop("checked")){
			if($("#radio002_1").val().trim().match(/^\d+$/) && $("#radio002_1").val().substr(0,1)!=0){
				setImageValue();
				submitForm();
			}else{		$(".error-tip").remove();
				$("#radio002_1").parent().append("<b class='error-tip'>请认真输入天数</b>");
				//alert("请认真输入天数");
				return false;
			}
		}else{
			setImageValue();
            submitForm();
        }
	});
    $('.fn-submit2').click(function(){
    	$('input[name=status]').val('draft');

		if($("#radio002").prop("checked")){
			if($("#radio002_1").val().trim().match(/^\d+$/) && $("#radio002_1").val().substr(0,1)!=0){
				setImageValue();
				submitForm();
			}else{
				
                $(".error-tip").remove();
				$("#radio002_1").parent().append("<b class='error-tip'>请认真输入天数</b>");
				//alert("请认真输入天数");
				return false;
			}
		}else{
			setImageValue();
            submitForm();
        }
    	
    	
	});
    //$('[name=interestRate]').val(($('[name=interestRate]').val()|0||''));
    function submitForm(){
        var c_increase = $("input[name='gmIncrease']:checked");
        if(c_increase.length >0){
            $("#giftMoneyIncrease").val("Y");
        }else{
            $("#giftMoneyIncrease").val("N");
        }

    	$('[name=interestRate]').val(($('[name=interestRate]').val()));
    	if($("#leastInvestAmountCkbox").attr('checked')){
    		var leastInvestAmount = $("#leastInvestAmountTxt").val();
    		if($.trim(leastInvestAmount)=="" || isNaN($.trim(leastInvestAmount))){
    			alert("最低投资金额输入错误！");
    			return false;
    		}
    	}

       var saturationConditionMethod =  $('input[name="saturationConditionMethod"]:checked').val();
       if(saturationConditionMethod == "date"){
           var saturationCondition = $('input[name="saturationCondition"].time').val();
           if(saturationCondition > $("#deadline").val()){
               alert("满标条件的固定时间应该投资截止时间之前!");
               return false;
           }
       }
     	addForm.submit();
    }
    
    $('input[name=saturationConditionMethod],input[name=timeLimitUnit],input[name=divisionTimeLimitUnit]').each(function(i,item){
    	var bechecked = $(this).attr('bechecked');
    	if(bechecked) {
    		$(this).click();
    	}
    });
    $('[name=loanAmount]').change(
    		function(){
    			var amount = $(this).val();
    			var result = convertCurrency(amount);
    			if(result.indexOf("error") >= 0 || amount == ""){
    				$("#loanAmountChinese").text("");
    				return;
    			}
    			$("#loanAmountChinese").text(result);
    		}
    );
    $('#saturationConditionAmount').change(
    		function(){
    			var amount = $(this).val();
    			var result = convertCurrency(amount);
    			if(result.indexOf("error") >= 0 || amount == ""){
    				$("#saturationConditionAmountChinese").text("");
    				return;
    			}
    			$("#saturationConditionAmountChinese").text(result);
    		}
    );
    $("#leastInvestAmountCkbox").click(function(){
    	$("#leastInvestAmountTxt").val("");
    	$("#leastInvestAmounttChinese").text("");
    	if($(this).attr('checked')){
    		$("#leastInvestAmountTxt").removeAttr("disabled");
    		$("#leastInvestAmountSel").attr("disabled",true);
    	} else {
    		$("#leastInvestAmountTxt").attr("disabled",true);
    		$("#leastInvestAmountSel").removeAttr("disabled");
    	}
    });
    $("#leastInvestAmountTxt").change(
    	function(){
			var amount = $(this).val();
			var result = convertCurrency(amount);
			if(result.indexOf("error") >= 0 || amount == ""){
				$("#leastInvestAmountChinese").text("");
				return;
			}
			$("#leastInvestAmountChinese").text(result);
		}
    );
    $(".fn-submit10").click(
		 	function(){
		 		if(window.confirm("确认通知投资人？")){
		 			$.ajax({
						url : '/backstage/notifyInvestorPdfOK',
						data : {'demandId' : $(this).attr('data')},
						type : 'post',
						dataType : 'json',
						success : function(res){
							alert(res.message);
							document.location.href = document.location.href;
						}
					});
		 		}
			}
	 );
});