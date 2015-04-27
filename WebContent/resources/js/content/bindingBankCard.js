define(function(require) {

	var Site = require('../comp/init.js');
	require('../plugins/jquery.box.js');
	require('../plugins/jquery.combobox.js');
	require('../plugins/jquery.selectBranch.js');
	var bankObjList = $('#select_bank_list').find('li');
	var count = 1;
	bankObjList.hover(function() {
		$(this).addClass('cur');
	}, function() {
		if (this.clickNumber != count)
			$(this).removeClass('cur');
	});
	bankObjList.click(function() {
		bankObjList.removeClass('cur');
		$(this).addClass('cur');
		count++;
		this.clickNumber = count;
		var liobj = $(this);
		var bankName = liobj.find('[data=bankName]').html();
		var bankCardNo = liobj.find('[data=bankCardNo]').html();
		$('input[data=bankName]').val(bankName);
		$('input[data=bankCardNo]').val(bankCardNo);
	});
	var addBank_form = $('#addBank_form');
	if (addBank_form.length) {
		addBank_form.validate({
			errorClass : 'error-tip',
			errorElement : 'b',
			ignore :'',
			errorPlacement : function(error, element) {
				element.after(error);
			},
			rules : {
				bankOpenName : {
					required : true
				},
				bankType : {
					required : true
				},
				bankCardNo : {
					required : true
				}

			},
			messages : {
				bankOpenName : {
					required : '请输入银行开户名'
				},
				bankType : {
					required : '请选择开户银行'
				},
				bankCardNo : {
					required : '请输入银行卡号'
				}

			},
			onkeyup : false

		});

	}
	var bankSel = $('#selectBranchApan');
	var bankItem = [];
	var obj = $('#selectBranchApanList').selectBranch({
		bankSelect : bankSel,
		branchUrl : '/bank/getBankBranch',
		toBranchItems : function(res) {
			var items = [];
			res = res.data;
			if(!res)return [];
			for ( var i = 0; i < res.length; i++) {
				items.push({
					value : res[i].bankLasalle,
					text : res[i].branchName
				});
			}
			return items;
		},
		comboOpts : {
			province : {
				name : 'bankProvince'
			},
			city : {
				name : 'bankCity'
			},
			branch : {
				name : 'bankAddress'
			}
		},
		noInit : true
	});
	$.ajax({
		url : '/bank/getAllDistrict',
		type : 'post',
		dataType : 'json',
		success : function(res) {
			obj.setAreaInfo(res.data, null, true);
			setTimeout(function() {
				$(bankObjList[0]).click();
				$("#addBankInfo").removeAttr("disabled");
			}, 1);
		}
	});

	obj.branchList.change(function(value, text, item) {
		$("[name=bankKey]").val(value);
	});
	var selectBankcCell = $("#selectBankList").parent().next();
	$("#selectBankList").click(function() {
		if (selectBankcCell[0].style.display == "none")
			selectBankcCell.show();
		else
			selectBankcCell.hide();
	});
	selectBankcCell.find("li").click(function() {
		var key = $(this).find("img").attr("alt");
		$("#selectBankList").prev().attr('src', $(this).find("img")[0].src);
		bankSel.val(key);
		obj.reStart();
		$("[name=bankKey]").val('');
		$("input[name=bankName]").val($(this).find("[name=bankName]").html());
		selectBankcCell.hide();
	});
	$("#addBankInfo").click(function() {
		if ($(this).attr("disabled"))
			return;
		$("#mainFormBankInfoDiv").show();
		$("input[name=bankCardNo]").val('');
		if (selectBankcCell.find("li")[0])
			selectBankcCell.find("li")[0].click();
	});
	$("#addBankInfo").parent().prevAll().click(
			function() {
				var arr = [ 'bankCardNo', 'bankKey', 'bankType', 'province','city', 'address', 'bankName' ];
				for ( var i = 0; i < arr.length; i++) {
					$("[name=" + arr[i] + "]").val($(this).find("[data=" + arr[i] + "]").html());
				}
				$("#mainFormBankInfoDiv").hide();
	});
});