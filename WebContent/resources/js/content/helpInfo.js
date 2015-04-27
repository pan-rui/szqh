define(function(require) {
	  require("../Y-all/Y-script/Y-base.js");
	  require('../Y-all/Y-script/Y-window.js');
	var rquery=/([^=&?]*)=((?:[^&]*)?)/g,
		decodeURICmp=decodeURIComponent,
		urlParams=getQueryParams(location.search),
		id=urlParams.id,
		wrap=$('.questlist'),
		items= $('.questlist li'),
		lastActiveItem;
		
		wrap.on('click','li',function(){
			var me=$(this);
			
			//已展开
			if(me.is('.questclik')||(lastActiveItem&&lastActiveItem.is(me))){
				closeQAListItem(me);
				lastActiveItem=null;
			}
			//未展开
			else{
				//关闭上次激活的
				if(lastActiveItem&&!lastActiveItem.is(me)){
					closeQAListItem(lastActiveItem);
					lastActiveItem=null;
				}
				
				me.children('.answer').slideDown(500);
				me.children('.quest').addClass('questclik');
				lastActiveItem=me;
			}
		});
		
		/**	
		 * 关闭问答列表项
		 */
		function closeQAListItem($li){
			$li.children('.answer').slideUp(500);
			$li.children('.quest').removeClass('questclik');
		}
	
		items.filter('li[data-id="'+id+'"]').trigger('click');
		
	
	/**	
	 * 将location.search转换为JSON
	 */
	function getQueryParams(input){
		var ret={},match;
		
		
		while(match=rquery.exec(input)){
			ret[decodeURICmp(match[1])]=decodeURICmp(match[2])
		}
		return ret;
	}
});