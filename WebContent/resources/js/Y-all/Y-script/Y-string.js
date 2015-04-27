
(function($){

define(function(){
    $.extend(Y,{
	    String: function(param){
		    if(typeof param == 'number') {
			    param = '' + param;
			}
		    var yString = new String(param);
			$.extend(yString,{
			    trim: function(){
				   return $.trim(this);
		        },
				replaceAll: function(oldStr,newStr){
				    var dictStr = "\\;*;^;$;+;-;|;[;];{;};(;)";
					var dict = dictStr.split(";");
					for(var  i =0;i<dict.length;i++) {
					    var reg = new RegExp("\\" + dict[i],"g");
						oldStr = oldStr.replace(reg,"\\"+dict[i]);
					}
				    var reg = new RegExp(oldStr,"g");
					return this.replace(reg,newStr);
				},
				clone: function(){
				   return Y.String(this);
				}
			});
			return yString;
		}
	});
});

})($);