define(function(){
	/*计算器2*/
	var calculator = $('.m-calculator2'),
	
		inputFild = $('input[name="investMoney"]'),
		
		resultFild = $('.result p'), //显示结果
		
		tableResult = calculator.find('.m-table2 tbody'),

		investMode = String(calculator.attr('data-mode')), //投资模式：2:按月计算  1: 按天计算
		
		rate = parseFloat(calculator.attr('data-rate')) / 100, //年利率
		
		limit = parseInt(calculator.attr('data-limit')), //期限
		
		repaymode = calculator.attr('data-repaymode'), //还款方式     sit:到期归还本金及利息      month:按月还息，到期还本

		computedHooks, outPutHooks, printTableHooks;


	//计算收益钩子
	computedHooks = {
		//按月计算
		'2': function(money /*本金*/ , rate /*年化利率*/ ) {
			return (money * (rate / 12));
		},
		//按天计算
		'1': function(money /*本金*/ , rate /*年化利率*/ ) {
			return (money * (rate / 360));
		}
	};

	//结果输出钩子
	outPutHooks = {
		//按月计算
		'2': function(money /*本金*/ , income /*月收益*/ ) {
			var totalIncome = (limit * income).toFixed(2);
			return '<span>本息合计：<em>' + (money + limit * income).toFixed(2) + '</em>元</span><span>利息收入共：<em>' + totalIncome + '</em>元</span><span>每月收款：<em>' + income.toFixed(2) + '</em>元</span>';
		},
		//按天计算
		'1': function(money /*本金*/ , income /*天收益*/ ) {
			var totalIncome = (limit * income).toFixed(2);
			return '<span>本息合计：<em>' + (money + limit * income).toFixed(2) + '</em>元</span><span>利息收入共：<em>' + totalIncome + '</em>元</span>';
		}
	};


	printTableHooks = {
		//输出方式1 只显示一行数据
		"1": function(money /*本金*/ , income /*单位收益*/ ) {
			return '<tr class="odd"><td>1</td><td>' + (money + limit * income).toFixed(2) + '</td><td>' + (money).toFixed(2) + '</td><td>' + (limit * income).toFixed(2) + '</td></tr>';
		},
		"2": function(money /*本金*/ , income /*单位收益*/ ) {
			var ret = [],
				i, len;

			for (i = 0, len = limit; i < len; i++) {
				
				//前几期不计算本金
				if(i<len-1){
					ret.push('<tr class="odd"><td>'+(i+1)+'</td><td>' + (income).toFixed(2) + '</td><td>' + 0 + '</td><td>' + (income).toFixed(2) + '</td></tr>');
					continue;
				}
				
				ret.push('<tr class="odd"><td>'+(i+1)+'</td><td>' + (money +  Number(income)).toFixed(2) + '</td><td>' + (money).toFixed(2) + '</td><td>' + (income).toFixed(2) + '</td></tr>')
				
			}

			return ret.join('');
		},
	}

	//当用户输入数值变更的时候
	//计算当前收益
	//添加节流阀 性能棒棒哒！
	inputFild.on('keydown', debounce(function() {
		var me = $(this),
			money = Number(me.val()), //本金
			computeFunc, income, result1,
			result2,computeMode;

		if (isNaN(money)) {
			alert('请输入正确的金额!');
			return;
		}

		//收益计算
		income = computedHooks[investMode](money, rate);

		//输出计算结果
		result1 = outPutHooks[investMode](money, income);
		resultFild.html(result1);
		
		//输出方式  按月计算  和   按月还息   按模板2输出  
		computeMode=(investMode=="2")&&(repaymode="month")?"2":"1";
		
		//输出计算结果明细表格
		result2 = printTableHooks[computeMode](money, income);
		tableResult.find('.odd').remove();
		tableResult.append(result2);


	}, 300)).trigger('keydown');

	//节流阀
	function debounce(func, wait) {
		var timer, handler, scope,
			params;

		handler = function() {
			return func.apply(scope, params);
		};

		return function() {
			scope = this;
			params = arguments;

			//如果定时器存在
			//清空之
			if (timer) {
				clearTimeout(timer);
				timer = null;
			}

			timer = setTimeout(handler, wait);
		};
	}
})