define(function (require, exports, module) {
    require('../comp/init.js');
    require('../Y-all/Y-script/Y-tip.js');
    require('../Y-all/Y-script/Y-countdown.js');
    require('../Y-all/Y-script/Y-window.js');

    $('input[name=realName]').Y('RareWordTip', {
        showEle: $('b.fn-tip a')
    });


    $('input:radio').click(function () {
        var registMethord = $('input:radio:checked').val();
        if (registMethord == 1) {
            $('.mobileRegist').css('display', 'none');
            $('.mailRegist').css('display', 'block');
        } else {
            $('.mobileRegist').css('display', 'block');
            $('.mailRegist').css('display', 'none');
        }
    });

    var newImgCod = $('.newImgCod');
    newImgCod.click(function () {
        $('#newImgCod').attr('src', '/anon/getImgCode?dateTag=' + new Date().getTime());
    });


    /*用户名手机号字段同步*/
    var userNameInput = $('input[name=userName]'),
        mobileInput = $('input[name=mobile]');

    userNameInput.on('keyup', debounce(function () {
        var value = userNameInput.val();
        mobileInput.val(value);
    }, 300))

    /**
     * 节流阀
     */
    function debounce(func, delay) {
        var timer, scope, params,
            handler;

        handler = function () {
            return func.apply(scope, params);
        }

        return function () {
            //关闭定时器
            if (timer) {
                clearTimeout(timer);
                timer = null;
            }
            scope = this;
            params = arguments;

            timer = setTimeout(handler, delay);
        }
    }

    var investorsOpen_form = $('#investorsOpen_form');
    if (investorsOpen_form.length) {
        investorsOpen_form.validate({

            errorClass: 'error-tip',
            errorElement: 'b',
            errorPlacement: function (error, element) {
                if (element.attr('name') == 'imgCode') {
                    element.next().next().after(error);
                } else if (element.attr('name') == 'license') {
                    element.next().after(error);
                } else {
                    element.after(error);
                }
            },
            rules: {
                userName: {
                    required: true,
                    isMobile: true,
                    NumandLetter_: true,
                    customRemote: {
                        url: '/anon/checkUserName?dateTag=' + new Date().getTime(),
                        customError: function (element, res) {
                            return res.message;
                        }
                    }
                },

                logPassword: {
                    required: true,
                    rangelength: [6, 20],
                    mustNotInclude: ' ',
                    notAllNum: true,
                    notAllSame: true,
                    noZh: true
                },
                logPasswordTO: {
                    required: true,
                    equalTo: '[name=logPassword]'
                },
                				mail : {
                 required : true,
                 customRemote : {
                 url : '/anon/checkEmailOrMobile?dateTag=' + new Date().getTime(),
                 data : {
                 email : function() {
                 return $('input[name=mail]').val();
                 },
                 checkType : 'investor'
                 },
                 customError : function(element, res) {
                 return res.message;
                 }
                 }
                 },
                mobile: {
                    required: true
                },
                code: {
                    required: true,
                    customRemote: {
                        url: '/anon/checkSmsCode',
                        data: {
                            mobile: function () {
                                return $('#bundPhone').val();
                            },
                            business: function () {
                                return $('#smsBizType').val();
                            },
                            code: function () {
                                return $('#code').val();
                            }
                        },
                        customError: function (element, res) {
                            return res.message;
                        }
                    }
                },
                imgCode: {
                    required: true,
                    customRemote: {
                        url: '/anon/checkImgCode?dateTag=' + new Date().getTime(),
                        customError: function (element, res) {
                            return res.message;
                        }
                    }
                },
                referees: {
                    customRemote: {
                        url: '/anon/checkReferees?dateTag=' + new Date().getTime(),
                        customError: function (element, res) {
                            return res.message;
                        }
                    }
                },
                license: {
                    required: true
                }

            },
            messages: {
                userName: {
                    required: '请输入用户名',
                    isMobile: '请输入正确的手机号码'

                },
                logPassword: {
                    required: '请输入登录密码',
                    rangelength: '登录密码为6-20位',
                    mustNotInclude: '登录密码不允许包含空格',
                    notAllNum: '登录密码不能全为数字',
                    notAllSame: '不能使用完全相同的数字、字母或符号',
                    noZh: '不允许中文'
                },
                logPasswordTO: {
                    required: '请再次确认登录密码',
                    equalTo: '两次输入的登录密码不一致，请重新输入'
                },
                				mail : {
                 required : '请输入常用电子邮箱'
                 },
                mobile: {
                    required: '请输入手机号'
                },
                code: {
                    required: '请输入验证码'
                },
                imgCode: {
                    required: '请输入验证码'
                },
                license: {
                    required: '请先阅读并同意服务协议'
                }

            },
            onkeyup: false

        });
    }


//	login_form.submit(function(){
//		return validateForm();
//	});
//	/* validate */
//	function validateForm(){		
//		var securityData = getSecurityData();
//		var user = $('input[name=userName]');
//		if ($.trim(user.val()) === '' || securityData === 'invalid'){
//			$('.err').eq(0).html('请填写账户或密码，密码不小于6位!');
//			$('.err').show();
//			return false;
//		}
//		$('#password').val(securityData);
//		return true;
//	}

    // -------------------------------------发送手机验证码-----------------------------------------------
    $('#getCode').click(function () {
        var business = $("#smsBizType").val();
        var mobile = $("#bundPhone").val();
        var countdown = Y.getCmp('getCode');
        if (!$("#bundPhone").valid()) {
            countdown.close(0);
            return;
        } else {
            sendMobile(business, mobile, countdown);
            $('.getCodeWrap').Y('countdown', {
                message: "{0}秒后重新发送"
            });
        }
    });

    function sendMobile(business, mobile, countdown) {
        $.ajax({
            url: '/anon/sendSmsCode',
            dataType: 'json',
            data: {
                mobile: mobile,
                business: business
            },
            cache: false,
            success: function (res) {
                if (res.code == 0) {
                    alert(res.message);
                    if (countdown!==undefined) {
                        countdown.close();
                    }
                }
            },
            error: function () {
                alert('获取动态验证码失败');
                if (countdown) {
                    countdown.close();
                }
            }
        });
    }

    $('.btn_license').click(function () {
        $('body').Y('Window', {
            content: ".j-license"
        });
        return false;
    });
    $('#step1-next').click(function () {
        var userVer = investorsOpen_form.validate().element($('[name=userName]'))|| ($('[name=userName]').attr('class').indexOf('valid') !== -1), passVer = investorsOpen_form.validate().element($('[name=logPassword]')), passTVer = investorsOpen_form.validate().element($('[name=logPasswordTO]')),
            refresV = investorsOpen_form.validate().element($('[name=referees]')) || ($('[name=referees]').attr('class').indexOf('valid') !== -1), listenerV = investorsOpen_form.validate().element($('[name=license]'));
        if (!userVer || !passVer || !passTVer || !refresV || !listenerV) return;
        $('#step1').hide();
        $('#step2').show();
    });
    var intR;
    function IntR() {
        var secondE = $('.f-c-rad:first'), range = secondE.text().substring(0, secondE.text().length - 1);
        if (range == 0) {
            clearInterval(intR);
            window.location.href = '/';
        }
        secondE.text(range - 1 + '秒');
    }
    $('#step2-next').click(function () {
        //investorsOpen_form.submit();
        if(investorsOpen_form.validate().form()){
            console.log(investorsOpen_form.attr('action'));
            $.ajax({
                url: investorsOpen_form.attr('action'),
                type:'POST',
                dataType:'json',
                data:investorsOpen_form.serialize(),
                cache:false,
                async:false,
                success:function(data){
                    console.log(data);
                    if(data.code==1) {
                        $('#step2').hide();
                        $('#step3').show();
                         intR= setInterval(IntR, 1000);
                    }else alert(data.msg);
                },
                error:function(data){
                    alert(data.msg);
                }
            });
        }
    })
});