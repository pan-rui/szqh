(function($){
define(function(require, exports, module){
 	require("../Y-script/Y-combobox.js");
    Y.inherit('SelectBranch','component' , {
	    init : function(cfg){
			var provinceOpts , cityOpts , branchOpts;
			var comboOpts = cfg.comboOpts || {};
			var renderDiv = this.renderDiv = $(cfg.renderTo);
			if(!cfg.renderTo) return;
			provinceOpts = this.provinceOpts = $.extend({itemsHeight:180, renderTo: renderDiv, store: [], autoSize: true}, comboOpts.province || {});
			cityOpts = this.cityOpts = $.extend({itemsHeight: 180, renderTo: renderDiv, store: [], autoSize: true}, comboOpts.city || {});
			branchOpts = this.branchOpts = $.extend({itemsHeight: 180, renderTo: renderDiv, store: [], autoSize: true}, comboOpts.branch || {});
			var provinceList = this.provinceList = Y.create('Combobox', provinceOpts);
			var cityList = this.cityList = Y.create('Combobox', cityOpts);
			var branchList = this.branchList = Y.create('Combobox', branchOpts);
			provinceList.show();
			cityList.show();
			branchList.show();
			var _this = this;
			var bankSel = $(cfg.bankSelect || '<input>');
			if(!cfg.noInit){
   			    this.handleProvinceList();
			}
			//bind province change
			provinceList.change(function(value){
				if (value){
					_this.handleCityList(value);
				}
				else {
					_this.reset();
				}
			});
			//bind city change
			function _cityList(value){
				var code = bankSel.getValue?bankSel.getValue():bankSel.val();
				if(code == '请选择银行'){
					setTimeout(function(){
						_cityList(value)
					},10);
					return;
				}
				_this.handleBranchList(value , code);
			}
			cityList.change(function(value){
				if (value){
					
					_cityList(value)
					
				}
				
			});
			if(cfg.provinceCityUrl){
				this.handleProvinceCityList();
			}
			this.callBase('init','component',cfg);
		},
		handleProvinceCityList:function(){
			var cfg = this.cfg;
			var _this = this;
			$.ajax({
				url:_this.cfg.provinceCityUrl,
				dataType:cfg.dataType,
				success: function(res){
					_this.getAreaInfoSts = true;
					_this.setAreaInfo(res);
				}
			});
		},
		handleCityList:function(code,callback){
			var cfg = this.cfg;
		    if(cfg.areaInfo) {
			    this.setCityList(cfg.areaInfo , code);
				if(callback) {
				    callback();
				}
				if(cfg.afterCity)if(cfg.afterCity()===false)return;
				if(this.afterCity) {
					this.afterCity();
				}
				
				return;
			}
			if(!cfg.cityUrl) return;
			var _this = this;
			$.ajax({
				url: cfg.cityUrl + '?code=' + code,
				dataType:cfg.dataType,
				success: function(res){
					var fn = cfg.toCityItems || _this.toCityItems;
					var items = fn(res);
					_this.cityList.clear();
					_this.cityList.addItem(items);
					if(callback) {
					    callback();
					}
					if(_this.afterCity) {
					    _this.afterCity();
					}
				},
				error: function(){
				
				}
			});
		},
		handleProvinceList:function(callback){
			var cfg = this.cfg;
		    if(cfg.areaInfo) {
			    this.setProvinceList(cfg.areaInfo);
				if(callback) {
				    callback();
				}
				if(cfg.afterProvince)if(cfg.afterProvince() === false) return;
				if(this.afterProvince) {
					this.afterProvince();
				}
				return;
			}
			if(!cfg.provinceUrl) return;
			var _this = this;
			$.ajax({
				url: cfg.provinceUrl,
				dataType:cfg.dataType,
				success: function(res){
				    var fn = cfg.toProvinceItems ||  _this.toProvinceItems
				    var items = fn(res);;
					_this.provinceList.clear();
					_this.provinceList.addItem(items);
					if(callback) {
						callback();
					}
					if(_this.afterProvince) {
					    _this.afterProvince();
					}
				},
				error: function(){
				}
			});
		},
		handleBranchList:function(code, bankId,callback,num){
			if(!bankId)return;
			var cfg = this.cfg;
			var _this = this;
			if(num == undefined)num = {num:10};
			var url = _this.cfg.branchUrl + (/\?/.test(_this.cfg.branchUrl)?"&":"?")+'districtNo='+ code +'&bankId='+ bankId + '&_d=' + new Date().getTime();
			$.ajax({
				url: url,
				dataType:cfg.dataType,
				success: function(res){
					var fn = _this.cfg.toBranchItems || _this.toBranchItems;
					if(res.data == null){
						if(!num.num)return;
						num.num--;
						setTimeout(function(){
							_this.handleBranchList.call(_this,code, bankId ,callback,num);
						},500)
						return;
					}
					var items = fn(res);
					_this.branchList.clear();
					_this.branchList.addItem(items);
					if(callback) {
					    callback();
					}
					if(cfg.afterBranch)if(cfg.afterBranch() === false)return;
					if(_this.afterBranch) {
					    _this.afterBranch();
					}
					
					
				},
				error: function(){
				
				}
			});
		},
		setProvinceList:function(areaInfo){
		    var fn = this.cfg.toProvinceItems || this.getProvinceStore;
		    var store = fn(areaInfo);
			this.provinceList.clear();
			this.provinceList.addItem(store);
		},
		reStart:function(){
			this.handleProvinceList();
		},
		reset:function(sts){
			if(sts)this.provinceList.clear();
			this.cityList.clear();
			this.branchList.clear();
		},
		getProvinceStore : function(areaInfo) {
		    var store = [];
			areaInfo = areaInfo.data || areaInfo;
		    $.each(areaInfo,function(i,item){
			    var aitem = item.provinceName;
				if(aitem)store.push(aitem);
			});
			return store;
		},
		setCityList:function(areaInfo,provinceName){
		    var fn = this.cfg.toCityItems || this.getCityStore;
		    var store = fn(areaInfo,provinceName);
			this.cityList.clear();
			this.cityList.addItem(store);
		},
		getCityStore:function(areaInfo,provinceName) {
		    var store = [];
			areaInfo = areaInfo.data || areaInfo;
		    $.each(areaInfo,function(i , item){
			    if(item.provinceName === provinceName) {
			        var citys = item.cityList;
				    $.each(citys , function(j , item){
					    var aitem = {value: item.branchDistrictNo , text: item.cityName};
						store.push(aitem);
					});
					return false;
				}
			});
			return store;
		},
		toProvinceItems:function(res){
		    var items = [];
		    if (res.length){
				for (var i = 0; i < res.length; i++){
					items.push({value: res[i].branchDistrictNo , text: res[i].branchDistrictName});
				}
			}
			return items;
		},
		toCityItems:function (res){
		    var items = [];
		    if (res.length){
				for (var i = 0; i < res.length; i++){
					items.push({value: res[i].branchDistrictNo , text: res[i].areaName});
				}
			}
			return items;
		},
		toBranchItems : function (res){
		    var items = [];
			res = res.data || res;
			for (var i = 0; i < res.length; i++){
				items.push({value: res[i].bankLasalle , text: res[i].branchName});
			}
			return items;
		},
		getValue : function(){
			return {province:this.provinceList.getValue(), city: this.city.getValue(), branch: this.branch.getValue()};
		},
		getText : function(){
			return {province: this.provinceList.getText(), city: this.city.getText(), branch: this.branch.getText()};
		},
		afterProvince : function(){
			if(this.provinceList.getItemList().length>0) {
				this.provinceList.setIndex(0);
			}
		},
		afterCity : function(){
			if(this.cityList.getItemList().length>0) {
				this.cityList.setIndex(0);
			}
		},
		afterBranch : function(){
			if(this.branchList.getItemList().length>0) {
				this.branchList.setIndex(0);
			}
		},
		setValue : function(info,type){
			type = type || 'value';
			if(typeof info != 'object') {
				return;
			}
			var _this = this;
			var fn1 = this.afterProvince, fn2 = this.afterCity,fn3 = this.afterBranch;
			this.afterProvince = function(){
				_this.provinceList.setValue(info.province,type);
				_this.afterProvince = fn1;
			}
			this.afterCity = function(){
				_this.cityList.setValue(info.city, type);
				_this.afterCity = fn2;
			}
			this.afterBranch = function(){
				_this.branchList.setValue(info.branch, type);
				_this.afterBranch = fn3;
			}
			this.handleProvinceList();
			return this;
		},
		setAreaInfo: function(areaInfo, first, noInit){
			this.cfg.areaInfo = areaInfo;
			if(!first || typeof first != 'object') {
				if(!noInit) {
					this.handleProvinceList();
				}
				return;
			}
			if(first && first.values){
				this.province = first.values.province;
				this.city = first.values.city;
				this.branch = first.values.branch;
				this.type = "value";
			} 
			else if(first && first.texts){
				this.province = first.texts.province;
				this.city = first.texts.city;
				this.branch = first.texts.branch;
				this.type = "text";
			}
			var fn1 = this.afterProvince, fn2 = this.afterCity, fn3 = this.afterBranch;
			var _this = this;
			this.afterProvince = function(){
				if(_this.province) {
					_this.provinceList.setValue(_this.province, _this.type);
				}
				_this.afterProvince = fn1;
			}
			this.afterCity = function(){
				if(_this.city) {
					_this.cityList.setValue(_this.city, _this.type);
				}
				_this.afterCity = fn2;
			}
			this.afterBranch = function(){
				if(_this.branch) {
					_this.branchList.setValue(_this.branch, _this.type);
				}
				_this.afterBranch = fn3;
			}
			this.handleProvinceList();
			return this;
		}
	}); 
	Y.SelectBranch.defaults = $.extend({
		renderTo:'',
		bankSelect:'',
		provinceUrl:'',
		cityUrl:'',
		branchUrl:'',
		areaInfo:'',
		comboOpts:'',
		dataType:'json'
	});
	$('.Y-SelectBranch').each(function(index){
		var province = $(this).attr('provinceName') || 'province';
		var city = $(this).attr('cityName') || 'city';
		var branch = $(this).attr('branchName') || 'branch';
		
		var provinceValue = $($(this).attr('provinceValue')).val();
		var cityValue = $($(this).attr('cityValue')).val();
		var branchValue = $($(this).attr('branchValue')).val();
		
		var bank = $($(this).attr('bank') || '.Y-SelectBranch-bank');
		var key = $(this).attr('key') || this.id || 'SelectBranch'+index;
		var sel = Y.create('SelectBranch',{
			renderTo:this,
			bankSelect:bank,
			branchUrl:"/bank/getBankBranch",
			provinceCityUrl:'/bank/getAllDistrict',
			dataType:'json',
			key:key,
			noInit:bank.val(),
			comboOpts:{
				province:{name:province},
				city:{name:city},
				branch:{name:branch}
			},
			afterProvince:function(){
				if(provinceValue) sel.provinceList.setValue(provinceValue);
				return provinceValue && false;
			},
			afterCity:function(){
				if(cityValue) sel.cityList.setText(cityValue);
				return cityValue && false;
			},
			afterBranch:function(){
				if(branchValue) sel.branchList.setText(branchValue);
				return branchValue && false;
			}
		});
		sel.provinceList.on('change',function(value,txt){
			sel.renderDiv.find('input.Y-SelectBranch-province').val(value);
		});
		sel.cityList.on('change',function(value,txt){
			sel.renderDiv.find('input.Y-SelectBranch-city').val(value);
		});
		sel.branchList.on('change',function(value,txt){
			sel.renderDiv.find('input.Y-SelectBranch-branch').val(txt);
		});
	})
});
})($);
