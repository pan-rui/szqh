define(function(require) {
	var Site = require('../comp/init.js');
	require('../plugins/jquery.box.js');
	require('../plugins/jquery.combobox.js');
	require('../plugins/jquery.uploadify.js');
	require('../Y-all/Y-script/Y-htmluploadify.js');
	require('../plugins/jquery.window.js');
	require('../Y-all/Y-script/Y-msg.js');
	var form = $('#institutions_form');
	$('.submit_form').click(function() {
		form.submit();
	});
	$('#addMember_form').find(':text').val('');
	$('#upload_file').uploadify({
						height : 26,
						width : 100,
						buttonText : '<div class="s-btn">导入</div>',
						fileTypeExts : '*.xls',
						multi : false,
						swf : '/resources/swf/uploadify.swf',
						uploader : '/backstage/userManage/institutions/institutionsMemberOpen;jsessionid='+ $_GLOBAL.sessionID,
						fileSizeLimit : '2MB',
						successTimeout : 180000,
						onUploadSuccess : function(file, data, response) {
							var result = "";
							if (data.indexOf("pre") > 0) {
								var startIndex = data.indexOf(">") + 1;
								var endIndex = data.length - 6;
								data = data.substring(startIndex, endIndex);
								data = eval("(" + data + ")");
								if (data.code == 0) {
									result = parseData(data, result);
								} else {
									result = "<span style='color:red;'>数据处理失败！</span>"
									$("#status-window").html(result);
								}
							} else {
								data = eval("(" + data + ")");
								if (data.code == 0) {
									result = parseData(data, result);
								} else {
									result = "<span style='color:red;'>数据处理失败！</span>"
									$("#status-window").html(result);
								}
							}
							$("#status-window").html(result);
							statusShow();
						},
						onUploadError : function(file, errorCode, errorMsg,
								errorString) {
							Y.alert('提示消息', "处理批量机构添加数据失败！异常信息：" + errorString);
						},
						onCancel : function(file) {
							Y.alert('提示消息', "已取消！");
						}
					});
	function parseData(data, result) {
		result += "<div><table width='100%' ><tr><td width='30%'>个人用户名</td><td width='30%'>机构用户名</td><td width='40%'>状态</td></tr>";
		for ( var i = 0; i < data.resData.length; i++) {
			if ("成功" == data.resData[i].state) {
				result += "<tr style='color:green;'>"
			} else {
				result += "<tr style='color:red;'>"
			}
			result += "<td width='30%' >" + data.resData[i].grUserName
					+ "</td><td width='30%' >" + data.resData[i].jgUserName
					+ "</td><td width='40%' >" + data.resData[i].state
					+ "</td>";
		}
		result += "</table></div>";
		return result;
	}
	function statusShow() {
		$('body').Y('Msg', {
			title : '批量导入结果',
			content : '#status-window'
		});
	}

	var changeWnd;
	$('.addMember').click(function() {
		var url = '/backstage/userManage/institutions/validateInstitutionForAdd';
		var data = {'parentId' : $(this).attr('t')};
		var result = $_GLOBAL.ajax(url, data);
		if(result.code != 1){
			Y.alert('提示消息', result.message);
			return false;
		}
        $('#memberRealName').val("");
        $('#memberUserName').val("");
        $("#addMember_form").find("b.error-tip").remove();

		$('input[name=parentId]').val($(this).attr('t'));
		changeWnd = $('body').window({
			content : '#addMember',
			simple : true,
			closeEle : '.cancel'
		});
	});

	var addMemberForm = $('#addMember_form');
	$('.addMemberSubmit').click(function() {
		if(addMemberForm.valid())addMemberForm.ajaxSubmit({
			success:function(res){
				Y.alert('提示消息', res.message, function(){
					location.reload();
				});
				
			}
		});
	});

	$('#memberRealName').attr("disabled", true);
	$('#memberUserName').blur(function(){
		var _this=$(this).val();
		$.ajax({
			url : '/backstage/getRealName',
			type : 'post',
			dataType : 'json',
			data : {
				userName : _this
			},
			success : function(res) {
				if(res.code==1){
					$('#memberRealName').val(res.message);
				}else{
					$('#memberRealName').val("");
				}
				
			},error:function(e){
				console.log(e)
			}
		})
	});
	if (addMemberForm.length) {
		addMemberForm.validate({
			errorClass : 'error-tip',
			errorElement : 'b',
			errorPlacement : function(error, element) {
				element.after(error)
			},
			rules : {
				memberUserName : {
					required : true,
					customRemote : {
						url : '/anon/checkUserNameAndType?dateTag='+ new Date().getTime(),
						data : {
							userName : function() {
								return $('#memberUserName').val()
							},
							type : function() {
								return 'GR'
							}
						},
						customError : function(element, res) {
							return res.message
						}
					}
				},
				memberRealName : {
					required : true,
					customRemote : {
						url : '/anon/checkUserNameAndRealName?dateTag='+ new Date().getTime(),
						data : {
							userName : function() {
								return $('#memberUserName').val()
							},
							realName : function() {
								return $('#memberRealName').val()
							}
						},
						customError : function(element, res) {
							return res.message
						}
					}
				}
				/**,
				code : {
					required : true
//					customRemote : {
//						url : '/anon/checkIdentityNameUserNameAndType?dateTag='+ new Date().getTime(),
//						data : {
//							identityName : function() {
//								return $('#memberUserName').val();
//							},
//							userName : function() {
//								return $('#code').val();
//							},
//							type : function() {
//								return 'GR';
//							}
//						},
//						customError : function(element, res) {
//							return res.message;
//						}
//					}
				}*/
			},
			messages : {
				memberUserName : {
					required : '请输入用户名'
				},
				memberRealName : {
					required : '请输入真实姓名'
				}
				/**,
				code : {
					required : '请输入编码'
				}*/
			},
			onkeyup : false
		});

	}

});