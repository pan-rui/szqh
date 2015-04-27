define(function(require){
	var a = $(".show-info"),
		aa = $(".gi-slide"),
		b = $("input[class*='date']"),
		c = $(".cupt"),
		d = '<p>项目期限: <span>1</span>个月</p><p>还款方式: 一次性还款</p><p>年化收益: <span>15%</span></p><p>投资奖励: <span>2%</span></p><label for="tender" class="input-text-default">投资金额：<input type="text" name="tender" value="" placeholder="1,000">元,预期获得<span>185.53</span>元的收益</label><a class="flat-full">计算收益</a>',
		e = $(".bank-select"),
		f = $(".kkextend");
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
	if (aa[0]) {
		aa.slide( { 
			mainCell:"ul",
			effect:"leftLoop",
			autoPlay:false,
			vis:1,
			prevCell:".sPrev",
			nextCell:".sNext" 
		});
	};
	if (b[0]) {
		$.each(b, function(index, el) {
			var n = el.id;
			laydate({
				elem: "#"+n
			}).skin('yalan')
		});
	};
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
	if (e) {
		$.each(e , function(index, el) {
			var b = $(el).find(".bottom-array"),
				m = $(el).find("img.show"),
				k = $(el).find("input[name='bank']");
			b.on('click', function(event) {
				toggle(b.next(".b-s-b"));
				b.next(".b-s-b").find("li").on('click', function(event) {
					m.attr("src",$(this).find("img").attr("src"));
					k.val($(this).find("input").val());
					b.next(".b-s-b").css("display","none");
					event.stopPropagation();
				});
				event.stopPropagation();
			});
			$(document).click(function(){
				b.next(".b-s-b").hide();
			});
		})
		function toggle (e) {
			e.css("display") == "block" ? e.css("display","none") : e.css("display", "block")
		}
	};
	if (f) {
		$.each(f ,function(index, el) {
			var a = $(el).find('[data-e="extend"]'),
				l = $(el).find('.kk-c-c'),
				b = $(el).find('.kk-back');
			a.on('click', function(event) {
				event.stopPropagation();
				l.animate({left:-l.width()/2},200);
			})
			b.on('click', function(){
				event.stopPropagation();
				l.animate({left:0},200);
			})
		})
	};
	/**
	 * #dataList为table id属性
	 */
	kkpager.generPageHtml({
		pno 				: $.trim($('#dataList').attr('pageNo')),
		total 				: $.trim($('#dataList').attr('total')), 			//总页码
		totalRecords 		: $('#dataList').attr('totalRecords'),				//总数据条数
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
});