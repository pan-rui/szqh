define(function(require) {
	var newData;
	var obj;
	init();
	function init() {
		$.ajax({
			url : '/bank/getAllBank',
			type : 'post',
			dataType : 'json',
			success : function(res) {
				if (res.code == 1) {
					$("[data=selectBranch]").html('');
					$("[data=selectBranch]").append(
							'<option value="">请选择银行</option>');
					for ( var i = 0; i < res.data.length; i++) {
						var data = res.data[i];
						$("[data=selectBranch]").append(
								'<option value="' + data.bankCode + '">'
										+ data.bankName + '</option>')
					}
					if ($('#bankType').val())
						$("[data=selectBranch]").val($('#bankType').val())
					fix_select("[data=selectBranch]");
				}
			}
		});
		obj = $('[data=selectBranchApanList]').selectBranch({
			bankSelect : "[data=selectBranch]",
			branchUrl : '/bank/getBankBranch',
			toBranchItems : function(res) {
				var items = [];
				if (!res)
					return items;
				res = res.data;
				if (!res)
					return items;
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
		obj.branchList.change(function(val) {
			$('#bankKey').val(val);
		});
		$.ajax({
				url : '/bank/getAllDistrict',
				type : 'post',
				dataType : 'json',
				success : function(res) {
					var data = res.data;
					newData = {};
					$("[data=thisProvice]").html(
							'<option value="">请选择省份</option>');
					for ( var i = 0; i < data.length; i++) {
						var row = data[i];
						var id = row.cityList[0].branchDistrictNo.slice(0,
								6);
						newData[id] = row.cityList;
						$("[data=thisProvice]").append(
								'<option value="' + id + '">'
										+ row.provinceName + '</option>');
					}

					fix_select("[data=thisProvice]");
					obj.setAreaInfo(res.data, null, true);

					var bankProvince = $('#bankProvince').val();
					var bankCity = $('#bankCity').val();
					var bankAddress = $('#bankAddress').val();
					if (bankAddress) {
						obj.setValue({
							province : bankProvince,
							city : bankCity,
							branch : bankAddress
						}, 'text');
					} else {
						obj.reStart();
					}
				}
		});
		
		if ($("[data=thisProvice]").get(0))
			$($("[data=thisProvice]").get(0)).change(function() {
				thisProviceChange(this);
			});
		
		obj.branchList.change(function(value, text, item) {
			$("div:not(:hidden)").find("[name=bankKey]").val(value);
		});

		if ($("[data=selectBranch]").get(0))
			$("[data=selectBranch]").get(0).onchange = function() {
				selectBranchChange(this);
			}
	}

	function thisProviceChange(_this) {
		var val;
		var ele = $("[data=thisCity]");
		if (val = $(_this).val()) {
			var data = newData[val];
			ele.html('');
			ele.append('<option value="">请选择城市</option>');
			for ( var i = 0; i < data.length; i++) {
				ele.append('<option value="' + data[i].branchDistrictNo + '">' + data[i].cityName + '</option>');
			}
			fix_select(ele);
		} else
			ele.hide();
	}
	
	function selectBranchChange(_this) {
		if ($(_this).val()) {
			$('[data=selectBranchApanList]').show();
			obj.reStart();
		} else
			$('[data=selectBranchApanList]').hide();
	}
	
	function fix_select(selector) {
		var i = $(selector).parent().find('div,ul').remove().css('zIndex');
		$(selector).unwrap().removeClass('jqTransformHidden').jqTransSelect();
		$(selector).parent().css('zIndex', i);
	}
	
});