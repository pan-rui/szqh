define(function(require) {
var Site = require('../comp/init.js');
require('../content/chineseAmountExchange.js');
require('../Y-all/Y-script/Y-imgplayer.js');
require('../Y-all/Y-script/Y-htmluploadify.js');
require('../plugins/jquery.uploadify.js');

    $('#rem1PathUpload').uploadify({
        height : 26,
        width : 100,
        buttonClass : 'u-btn u-btn-gray',
        buttonText : '选择上传图片',
        fileTypeExts : '*.png;*.jpg;*.bmp',
        multi : false,
        swf : '/resources/swf/uploadify.swf',
        uploader : '/upload/imagesUpload;jsessionid='+ $_GLOBAL.sessionID,
        fileSizeLimit : '3MB',
        successTimeout : 180000,
        onUploadStart:function(){
            $('body').Y('Window',{
                content:"<span><img src='/resources/images/common/loadingScroll.gif' /><br/>上传中，请稍后...</span>",
                key:'lodding',
                simple:true
            });
        },
        onUploadSuccess : function(file, data, response) {
            handdleResult(data, "rem1Path_imgcontainer", "rem1PathImg", "rem1PathImgLink", "rem1");
            Y.getCmp('lodding').close();
        },
        onUploadError : function(file, errorCode, errorMsg,
                                 errorString) {
            Y.getCmp('lodding').close();
            alert("图片上传失败！异常信息：" + errorString);
        },
        onCancel : function(file) {
            Y.getCmp('lodding').close();
            alert("已取消！");
        }
    });

    function handdleResult(data, containerId, imgId, linkId, storePathId){
        var result = "";
        if (data.indexOf("pre") > 0) {
            var startIndex = data.indexOf(">") + 1;
            var endIndex = data.length - 6;
            data = data.substring(startIndex, endIndex);
            data = eval("(" + data + ")");
            if (data.code == 0) {
                var imgUrl = data.resData;
                $("#"+imgId).attr("src", imgUrl);
                //$("#"+linkId).attr("href", imgUrl);
                $("#"+storePathId).val(imgUrl);

            } else {
                result = "<span style='color:red;'>"+data.resData+"</span>"
            }
        } else {
            data = eval("(" + data + ")");
            if (data.code == 0) {
                var imgUrl =data.resData;
                $("#"+imgId).attr("src", imgUrl);
                //$("#"+linkId).attr("href", imgUrl);
                $("#"+storePathId).val(imgUrl);

                if($('#'+linkId).data('jqzoom')){
                    $('#'+linkId).data('jqzoom').largeimage.node.src=imgUrl;
                };

                $("#"+imgId).parents('.item').css('height',360);
            } else {
                result = "<span style='color:red;'>"+data.resData+"</span>"
            }
        }
        $("#"+containerId).append(result);

        $('#' + containerId).show(1500);
        setTimeout(function(){ $('#'+containerId).height(75);},1000);
    }

    $('textarea[name=content]').xheditor({
        tools:'full',
        skin:'default',
        upMultiple:true,
        upImgUrl: '/upload/imagesUpload?fileName=xxx.gif;jsessionid='+ $_GLOBAL.sessionID,
        upImgExt: "jpg,jpeg,gif,bmp,png",
        onUpload:insertUpload,
        html5Upload:false
    });
    //xbhEditor编辑器图片上传回调函数
    function insertUpload(msg) {
        var _msg = msg.toString();
        var _picture_name = _msg.substring(_msg.lastIndexOf("/")+1);
        var _picture_path = Substring(_msg);
        var _str = "<input type='checkbox' name='_pictures' value='"+_picture_path+"' checked='checked' onclick='return false'/><label>"+_picture_name+"</label><br/>";
        $("#xh_editor").append(_msg);
        $("#uploadList").append(_str);
    }
    //处理服务器返回到回调函数的字符串内容,格式是JSON的数据格式.
    function Substring(s){
        return s.substring(s.substring(0,s.lastIndexOf("/")).lastIndexOf("/"),s.length);
    }

	var addForm=$('#add_pop_form');
	$('.fn-submit1').click(function(){
        if(!$("#title").val()){
            alert("请填写标题!");
            return;
        }
		addForm.ajaxSubmit();
		alert('发布公告成功！');
	});
});