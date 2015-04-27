define(function(require) {
	  require("../Y-all/Y-script/Y-base.js");
	  require('../Y-all/Y-script/Y-window.js');
	
	var rquery=/([^=&?]*)=((?:[^&]*)?)/g,
		decodeURICmp=decodeURIComponent,
		urlParams=getQueryParams(location.search),
		nav=$('ul.usmenu'),
		iframe=$('#aboutusifr'),
		id=urlParams.id,
		category=urlParams.category;
	
		
		if(id!=null){
			category=urlParams.category;
			
			nav.find('li[data-id="'+category+'"]').addClass('usmenuon');
			iframe.attr('src','/help/popInfoDetail?popId='+id+'&moduleCode=NEWS');
		}
	
	
	/**	
	 * 将location.search转换为JSON
	 */
	function getQueryParams(input){
		var ret={},match;
		
		rquery.lastIndex=0;
		
		while(match=rquery.exec(input)){
			ret[decodeURICmp(match[1])]=decodeURICmp(match[2]);
		}
		return ret;
	}
});