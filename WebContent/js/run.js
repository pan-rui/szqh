$(function(){
	kkpager.generPageHtml({
		pno 				: '1', 					
		total 				: '10', 			//总页码
		totalRecords 		: '4',				//总数据条数
		mode 				: 'click',			//默认值是link，可选link或者click
		isShowFirstPageBtn	: false, 
		isShowLastPageBtn	: false,
		isShowTotalPage 	: false,
		isShowCurrPage		: false,
		isShowTotalRecords 	: false, 
		isGoPage 			: false,
		click : function(n){
			this.selectPage(n);
			return false;
		}
	});
	var a = $(".show-info");
	if (a[0]) {
		a.slide( { 
			mainCell:"ul",
			effect:"leftLoop",
			autoPlay:false,
			vis:3,
			prevCell:".sPrev",
			nextCell:".sNext" 
		});
	};
	
	var c = $(".cupt"),
		d = '<p>项目期限: <span>1</span>个月</p><p>还款方式: 一次性还款</p><p>年化收益: <span>15%</span></p><p>投资奖励: <span>2%</span></p><label for="tender" class="input-text-default">投资金额：<input type="text" name="tender" value="" placeholder="1,000">元,预期获得<span>185.53</span>元的收益</label><a class="flat-full">计算收益</a>';
	if (c) {
		$.each(c, function(index, el) {
			$(el).on('click', function(event) {
				event.preventDefault();
				SimplePop.prompt('',{
		            type: "confirm",
		            title: "收益计算器",
		            content: d,
		            cancel: function(){
		                alert("cancle");
		            },
		            confirm: function(msg){
		                alert(msg);
		            }
		        });
			});
		});
	};

	var b = $("input[class*='date']");
	if (b[0]) {
		$.each(b, function(index, el) {
			var n = el.id;
			laydate({
				elem: "#"+n
			}).skin('yalan')
		});
	};
});