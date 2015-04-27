
(function($){

define(function(){
    $.extend(Y,{
	    Date: function(param){
		    if(typeof param == 'string') {
			    param = $.trim(param);
			    param = param.replace(/-/g,"\/").replace(/\./g,"\/").replace(/\s/g,"\/");
			}
		    var yDate = new Date(param);
			if(isNaN(yDate)) {
			    Y.handlerError("参数无法正确转换为日期");
			    return false;
			}
			$.extend(yDate,{
			    isLeapYear: function(){
				    return (0==this.getYear()%4&&((this.getYear()%100!=0)||(this.getYear()%400==0)));
				},
				add: function(strInterval, Number) {   
                    var dtTmp = this;  
                    switch (strInterval) {   
					    case 'second':
                        case 's' :
						    return new Date(dtTmp.getTime() + (1000 * Number));  
						case 'minute':
                        case 'n' :
						    return new Date(dtTmp.getTime() + (60000 * Number));  
						case 'hour':
                        case 'h' :
						    return new Date(dtTmp.getTime() + (3600000 * Number)); 
                        case 'day':							
                        case 'd' :
						    return new Date(dtTmp.getTime() + (86400000 * Number)); 
                        case 'week':							
                        case 'w' :
						    return new Date(dtTmp.getTime() + ((86400000 * 7) * Number));
						case 'month':
                        case 'm' :
						    return new Date(dtTmp.getFullYear(), (dtTmp.getMonth()) + Number, dtTmp.getDate(), dtTmp.getHours(), dtTmp.getMinutes(), dtTmp.getSeconds());  
                        case 'year':
						case 'y' :
						    return new Date((dtTmp.getFullYear() + Number), dtTmp.getMonth(), dtTmp.getDate(), dtTmp.getHours(), dtTmp.getMinutes(), dtTmp.getSeconds());  
                    }  
                },
				diff: function(strInterval, dtEnd) {   
                    var dtStart = this;  
                    if (typeof dtEnd == 'string' ) 
                    {   
                        dtEnd = Y.Date(dtEnd);  
                    }  
                    switch (strInterval) {   
                        case 'second':
                        case 's' :return parseInt((dtEnd - dtStart) / 1000);  
                        case 'minute':
                        case 'n' :return parseInt((dtEnd - dtStart) / 60000);  
                        case 'hour':
                        case 'h' :return parseInt((dtEnd - dtStart) / 3600000);  
                        case 'day':							
                        case 'd' :return parseInt((dtEnd - dtStart) / 86400000);  
                        case 'week':							
                        case 'w' :return parseInt((dtEnd - dtStart) / (86400000 * 7));  
                        case 'month':
                        case 'm' :return (dtEnd.getMonth()+1)+((dtEnd.getFullYear()-dtStart.getFullYear())*12) - (dtStart.getMonth()+1);  
                        case 'year':
						case 'y' :return dtEnd.getFullYear() - dtStart.getFullYear();  
                    }  
                },
				translate: function(type){
				    var myDate = this;  
                    var myArray = Array();  
                    myArray[0] = myDate.getFullYear();  
                    myArray[1] = myDate.getMonth() + 1;  
                    myArray[2] = myDate.getDate();  
                    myArray[3] = myDate.getHours();  
                    myArray[4] = myDate.getMinutes();  
                    myArray[5] = myDate.getSeconds();
					if(type == 'array') {
					    return myArray;
					} else {
					    return {
						    year: myArray[0], month: myArray[1], day: myArray[2],
							hour: myArray[3], minute: myArray[4], second: myArray[5]
						}
					}
                      
				},
				toString: function(type)  
                {   
                    var obj = this.translate();
					var str = obj.year + '-' + obj.month + '-' + obj.day;
					if(type == 'time') {
					    str += " " + obj.hour + ":" + obj.minute + ":" + obj.second;
					}
					return str;
                },
				clone: function(){
				   return Y.Date(this.getTime());
				}
			});
			return yDate;
		}
	});
});

})($);