define(function(require) {
	var newData;
	$.ajax({
		url : '/bank/getAllDistrict',
		type : 'post',
		dataType : 'json',
		success : function(res) {
			var data = res.data;
			newData = {};
			$("[data=thisProviceList]").html('<option value="">请选择省份</option>');
			for ( var i = 0; i < data.length; i++) {
				var row = data[i];
				var id = row.cityList[0].branchDistrictNo.slice(0, 6);
				newData[id] = row.cityList;
				$("[data=thisProviceList]").append('<option value="' + id + '">' + row.provinceName + '</option>');
			}
			$("[data=thisProviceList]").find("option:contains('" + $('#thisProviceList').val() + "')").attr('selected', true);
			fix_select("[data=thisProviceList]");
			$("[data=thisProviceList]").get(0).onchange();
		}
	});
	$("[data=thisProviceList]").get(0).onchange = function() {
		$('[name=businessLicenseProvince]').val($(this).find("option:selected").html())
		thisProviceChange(this);
		
	}
	$("[data=thisCity]").get(0).onchange = function() {
		$('[name=businessLicenseCity]').val($(this).find("option:selected").html())
	}
	
	function thisProviceChange(_this) {
		var val;
		var ele = $("[data=thisCity]");
		if (val = $(_this).val()) {
			var data = newData[val];
			ele.html('');
			ele.append('<option value="">请选择城市</option>');
			for ( var i = 0; i < data.length; i++) {ele.append('<option value="' + data[i].cityName + '">' + data[i].cityName + '</option>');
			}
			$("[data=thisCity]").val($('#thisCity').val());
			fix_select(ele);
		} else
			ele.hide();
	}
	
	function fix_select(selector) {
		console.log($(selector).val());
		var i = $(selector).parent().find('div,ul').remove().css('zIndex');
		$(selector).unwrap().removeClass('jqTransformHidden').jqTransSelect();
		$(selector).parent().css('zIndex', i);
	}

});