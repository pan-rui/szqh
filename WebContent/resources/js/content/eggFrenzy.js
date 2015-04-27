define(function(require) {
  require('../comp/init.js');
  require('../plugins/jquery.SuperSlide.2.1.1')

  /**
   * 中奖列表滚动
   */
  $(".m-table3.rows").slide({
    mainCell: "table",
    autoPlay: true,
    effect: "topMarquee",
    vis: 1,
    interTime: 50,
    trigger: "click"
  });

  /**
   * 砸蛋
   */
  var eggPanel = $('.m-egg'),
    items = eggPanel.find('.itm'),
    rstate = /^state\d+$/,
    messageBox = $('.m-messageBox'),
    messageMsg = messageBox.find('.msg'),
    mask = $('.u-mask'),
    locked = false;

  //锤子落下
  eggPanel.on('mousedown', '.itm.state1', function(e) {
    //变更状态
    if(isLogin()){
        modifyClassName(this, rstate, 'state2');
    }else{
        showMsgBox("亲！请先登录再抽奖！");
    }
  });


    function isLogin(){
        var url = '/login/islogin';
        return $_GLOBAL.ajax(url);
    }

  //锤子抬起
  //发送请求
  eggPanel.on('mouseup', '.itm.state2', function(e) {
    var me = this;

    if (locked) {
      return;
    }

    //变更状态
    modifyClassName(me, rstate, 'state1');
    eggPanel.addClass('showMask');

    //锁定
    locked = true;

    //回调
    function callback(data) {
      locked = false;

      modifyClassName(me, rstate, 'state3');
      eggPanel.removeClass('showMask');
      showMsgBox(data.message);
    }

    //发送请求
    setTimeout(function() {
        var date = new Date();
        var t = date.getTime();
        var url = "/boot/drawAwardSubmit?lotteryActivityInstanceId="+$("#lotteryActivityInstanceId").val() +"&t="+t;
      $.get(url, callback);
    }, 300);
  });




  /**
   * 关闭消息框
   */
  messageBox.on('click', '.hd .tool.close', hideMsgBox);
  messageBox.on('click','.u-btn2.reset',hideMsgBox);



  /****************************************************************************
   *
   * 工具函数
   *
   ****************************************************************************
   */

  /**
   * 变更状态class
   */
  function modifyClassName(elem, rule, stateCls) {
    var classNames = elem.className.split(/\s+/),
      i = 0,
      len = classNames.length,
      ret = [];

    for (; i < len; i++) {
      //过滤所有符合规则的class
      if (!rule.test(classNames[i])) {
        ret.push(classNames[i]);
      }
    }

    ret.push(stateCls);
    elem.className = ret.join(' ');
  }


  /**
   * 打开提示信息
   */
  function showMsgBox(message) {
    messageMsg.text(message);
    messageBox.show();
    mask.show();
  }

  /**
   * 关闭提示信息
   */
  function hideMsgBox() {
    messageBox.hide();
    mask.hide();
  }


  /**
   * 重置
   */
  function reset() {
    hideMsgBox();
    eggPanel.removeClass('showMask');

    items.each(function(_, itm) {
      modifyClassName(itm, rstate, 'state1');
    });
  }

});