// JavaScript Document// JavaScript Document
define(function(require, exports, module){
	require("../Y-script/Y-base.js");
	require("../Y-script/Y-jrotate.js");
	var rotate = Y.create('Jrotate',{
		renderTo: '#showDiv',//显示效果的对象
		startNumber: 1211,
		showNumber: 12121,
		maxLength : 8,
		startLength : 4,
		class: 'jrotate',//样式
		speed: 500 //动画速度
	});
	$('#input').keyup(function(){
	  rotate.setNumber($(this).val())
	})	
});