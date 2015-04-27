
define(function(require, exports, module) {
  
	  require('../comp/init.js');
	  require("../Y-all/Y-script/Y-base.js");
	  
	  require("../Y-all/Y-script/Y-marquee.js");
	  require('../plugins/jquery.jqtransform.js')($); 
	  require('../comp/init.js');
	  require('../plugins/jquery.form.js')($); //jquery ajax form插件
	  require('../plugins/jquery.box.js');
	  require('../plugins/jquery.combobox.js');
	  require('../plugins/jquery.countdown.js');
	  require('../plugins/jquery.selectBranch.js');
	  require('../plugins/jquery.window.js');
	  require('../Y-all/Y-script/Y-window.js');
	  
	  require('../lib/jquery.unslider.js');

//	  var v = Y.create('Marquee',{
//		target: '#marquee',//jq选择字符串 或者字符串数组
//		time:20,
//		gotoType:'left',
//		speed:1
//	  });
	  /*
	  $("#bannerBg").Slide({//banner
          effect:"fade",
          speed:600,
          timer:4000
      });
      */
	//异步加载iFrame
		function setIframeSrc(obj) {
		    var s = obj;
		    var $iframe1 = $("#iframe1");
		    $iframe1.attr('src', s);
		    iframe = document.getElementById("iframe1");
		    if (iframe.attachEvent){  
		        iframe.attachEvent("onload", function(){
		        	reinitIframe();
		        	$iframe1.css("visibility","visible");
		        });  
		    } else {  
		        iframe.onload = function(){  
		        	reinitIframe();
		        	$iframe1.css("visibility","visible");
		        };  
		    }
		    reinitIframe();
		}
		
		
		$("#newFoundation").click(function(){
			$('#newFoundation,#newPaid,#ytGuarantee,#eduGuarantee').parents("li").removeClass('focus');
			$('#newFoundation,#newPaid,#ytGuarantee,#eduGuarantee').removeClass('ocur');
			$(this).parents("li").addClass('focus');
			$(this).addClass('ocur');
			var s ="/boot/staticIndex/10/1?status=1";
			setIframeSrc(s);
		});
		$("#newPaid").click(function(){
			$('#newFoundation,#newPaid,#ytGuarantee,#eduGuarantee').parents("li").removeClass('focus');
			$('#newFoundation,#newPaid,#ytGuarantee,#eduGuarantee').removeClass('ocur');
			$(this).parents("li").addClass('focus');
			$(this).addClass('ocur');
			var s ="/boot/staticIndex/10/1?status=2";
			setIframeSrc(s);
		});
		
		$("#ytGuarantee").click(function(){
			$('#newFoundation,#newPaid,#ytGuarantee,#eduGuarantee').parents("li").removeClass('focus');
			$('#newFoundation,#newPaid,#ytGuarantee,#eduGuarantee').removeClass('ocur');
			$(this).parents("li").addClass('focus');
			$(this).addClass('ocur');
			var s ="/boot/staticIndex/10/1?status=1&guarantee=YT";
			setIframeSrc(s);
		});
		
		$("#eduGuarantee").click(function(){
			$('#newFoundation,#newPaid,#ytGuarantee,#eduGuarantee').parents("li").removeClass('focus');
			$('#newFoundation,#newPaid,#ytGuarantee,#eduGuarantee').removeClass('ocur');
			$(this).parents("li").addClass('focus');
			$(this).addClass('ocur');
			var s ="/boot/staticIndex/10/1?status=1&guarantee=EDU";
			setIframeSrc(s);
		});
		
		function reinitIframe(){
			var iframe = document.getElementById("iframe1");
			try{
				var bHeight = iframe.contentWindow.document.body.scrollHeight;
				var dHeight = iframe.contentWindow.document.documentElement.scrollHeight;
				var height = Math.max(bHeight, dHeight);
				iframe.height =  height;
			}catch (ex){}
		}
		
		function refreshAmount(){
			var url = '/boot/refreshAmount.htm';
			var data = {};
			var result = $_GLOBAL.ajax(url, data);
			if(result.dealedAmount != $("#dealedAmount").text()){
				$("#dealedAmount").fadeOut("slow");
				$("#dealedAmount").text("");
				$("#dealedAmount").text(result.dealedAmount);
				$("#dealedAmount").fadeIn("slow");
			}
			
			if(result.repaidAmount != $("#repaidAmount").text()){
				$("#repaidAmount").fadeOut("slow");
				$("#repaidAmount").text("");
				$("#repaidAmount").text(result.repaidAmount);
				$("#repaidAmount").fadeIn("slow");
			}
			
			if(result.dealedSupAmount != $("#dealedSupAmount").text()){
				$("#dealedSupAmount").fadeOut("slow");
				$("#dealedSupAmount").text("");
				$("#dealedSupAmount").text(result.dealedSupAmount);
				$("#dealedSupAmount").fadeIn("slow");
			}
			
			if(result.repaidSupAmount != $("#repaidSupAmount").text()){
				$("#repaidSupAmount").fadeOut("slow");
				$("#repaidSupAmount").text("");
				$("#repaidSupAmount").text(result.repaidSupAmount);
				$("#repaidSupAmount").fadeIn("slow");
			}
    	}
	
		$(window).load(function (){ 
			var intvalAmount=self.setInterval(refreshAmount, 300000);
		});
		
/*		var length  = $('#idSlider li').length;
		var idSlider = $('#idSlider');
		var first = idSlider.find('li:first');
		var height = first.height();
		var timer,selectNum = 0;
		function startTimer(){
			clearTimeout(timer);
			timer = setTimeout(move,10 * 1000);
		}
		startTimer();
		function move(num){
			if(num == null){
				num = ++selectNum % length;
			}
			if(num < 0){
				num = length - 1;
			}
			setCLass(num);
			selectNum = num;
			clearTimeout(timer);
			first.animate({'margin-top' : - num * height},500,function(){
				startTimer();
			});
		}
		function setCLass(i){
			$('#idNum a').removeClass('on');
			$('#idNum a').eq(i).addClass('on');
		}
		$('#idNum a').click(function(){
			move($('#idNum a').index(this));
		});
		
		
		idSlider.mouseenter(function(){
			clearTimeout(timer);
		});
		idSlider.mouseleave(function(){
			startTimer();
		});
		*/
		
		$('.m-banner1').unslider({
	      dots:true,
	      fluid:true,
	      arrows:true,
	      delay:3000,
	      speed:600,
	      prev:'&nbsp;',
	      next:'&nbsp;'
	    });
		
		$('.m-banner2').unslider({
	      dots:true,
	      delay:3000,
	      speed:600,
	      fluid:true
	    });
		
		

		var Marquee=require('../Atom/Marquee');
		new Marquee({
			container:'#rolling',
			el:'ul',
			itemSelector:'li',
			speed:15,
		    delay: 3000
		}).render();
		
		

		/*选项卡切换*/
		var tabStrip=$('.home-newpro h3 span'),
			activeCls="active",
			urls=["/boot/staticIndex/5/1?status=1","/boot/staticIndex/5/1?status=0"];
		
		tabStrip.on('click',function(){
			var index=tabStrip.index(this);
			
			tabStrip.removeClass(activeCls)
				.eq(index).addClass(activeCls);
			

			if(urls[index]){
				setIframeSrc(urls[index]);
			}
		}).eq(0).trigger('click');
		
});