define(function(require) {
	var Site = require('../comp/init.js');
	require('../content/chineseAmountExchange.js');
	require('../Y-all/Y-script/Y-imgplayer.js');
	require('../content/fileUpload.js');
	require('../content/pictureUpload.js');
    XHEDITOR.settings.tools = "Cut,Copy,Paste,Pastetext,|,Blocktag,Fontface,FontSize,Bold,Italic,Underline,Strikethrough,FontColor,BackColor,SelectAll,Removeformat,|,Align,List,Outdent,Indent,|,Link,Unlink,Anchor,Img,Flash,Media,Hr,Emot,Table,|,Source,Print,Fullscreen"



	 Y.create('ImgPlayer',{
			eleArr:'#guaranteeLicenseUrl_Img',
			titleInfo: 'alt',
			content:'',
			pathInfo: function(){
			  return $(this).attr('src');
			}
		});
	$('.time').click(function() {
		WdatePicker({
			startDate : '%y-%M-01 HH:mm:ss',
			dateFmt : 'yyyy-MM-dd HH:mm:ss'
		});
	});

	function formatFloat(src, pos)
	{
	    return Math.round(src*Math.pow(10, pos))/Math.pow(10, pos);
	}

   $('textarea[name=content]').xheditor({});



	//$('input[name=realName]').attr("disabled", true);
	var addForm=$('#add_pop_form');
    $(".fn-submit1-preview").bind("click",function(){
        $("#submit_type").val(1)
        addForm.submit();
    })


    $(".fn-submit1").bind("click",function(){
        $("#submit_type").val(0)
        addForm.submit();
    })

    if (addForm.length) {
        addForm.validate({
            errorClass : 'error-tip',
            errorElement : 'b',
            submitHandler : function() {
                addForm.ajaxSubmit({
                    success : function(res) {
                        if(res.code == 1){
                            var submit_type = $("#submit_type").val()
                            if(submit_type == 1){
                                window.open("/help/popHelp/"+res.popNoticeId)
                            }else{
                                alert("更新成功!");
                            }

                        }else{
                            alert("更新失败!");
                        }
                    }
                });
            },
            rules : {
            },
            messages : {
            },
            onkeyup : false

        });

    }

});