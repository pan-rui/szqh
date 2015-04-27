/**
 *
 * @authors 熊洋 (xywindows@gmail.com)
 * @date    2014-08-27 11:01:32
 * @description 跑马灯
 */

define(function(require, exports, module) {
  //require('../lib/jquery-1.8.2.min.js');

  var ClassFactory = require('./ClassFactory'),

    Observable = require('./util/Observable'),

    Marquee, directionHooks;

  Marquee = module.exports = ClassFactory.define(Observable, {
    /*容器*/
    container: undefined,

    /*dom元素*/
    el: undefined,

    /*子元素选择符*/
    itemSelector: 'div',
    
    /*容器高度*/
    ctHieght:0,

    /*停顿时间*/
    delay: 3000,

    /*滚动速度*/
    speed: 40,

    /*滚动方向*/
    direction: 'bottomup',

    /*定时器ID*/
    timerId: undefined,

    timeoutId: undefined,

    /*目标偏移量*/
    destOffset: 0,

    scrollOffset: 20,

    stop: false,

    init: function(config) {
      var me = this;

      $.extend(me, config);

      me.direction = me.direction.toLowerCase();

      document.execCommand("BackgroundImageCache", false, true);

      //me.callParent(arguments);

      //触发初始化完成事件
      //me.trigger('initcomplete', me);
    },

    render: function(container, position) {
      var me = this;

      me.container = $(container || me.container);
      me.el = $(me.el, me.container[0]);
      me.clone = me.el.clone(); //复制元素

      //预处理
      directionHooks[me.direction].preprocess.call(me);

      //触发绘制完成事件
      //me.trigger('rendercomplete', me);

      me.initEvents();
      me.run();
    },

    /*执行*/
    run: function(keep /*private*/ ) {
      var me = this;

      if (me.timerId) {
        clearInterval(me.timerId);
      }


      directionHooks[me.direction].run.call(me, keep);
    },

    /*滚动*/
    roll: function(keep /*private*/ ) {
      var me = this;

      if (!keep) {
        me.destOffset += me.scrollOffset;
      }

      me.timerId = setInterval(function() {

        directionHooks[me.direction].roll.call(me);
      }, me.speed);
    },

    /*重置*/
    reset: function() {
      var me = this;

      if (me.timerId) {
        clearInterval(me.timerId);
        me.timerId = null;
      }

      me.destOffset = 0;
      me.container.scrollTop(0);
    },

    /**
     * 暂停滚动等待{@param delay}毫秒后再执行
     */
    hold: function() {
      var me = this,
        container = me.container,
        clone = me.clone;

      //停止两个定时器
      if (me.timerId) {
        clearInterval(me.timerId);
        me.timerId = null;
      }

      if (me.timeoutId) {
        clearInterval(me.timeoutId);
        me.timeoutId = null;
      }

      //第一页翻滚完成
      if (directionHooks[me.direction].condition.call(me)) {
        me.reset();
      }

      if (!me.stop) {
        me.timeoutId = setTimeout(function() {
          me.roll();
        }, me.delay);
      }
    },

    initEvents: function() {
      var me = this;

      me.container.on('mouseenter', $.proxy(me.onMousenter, me));
      me.container.on('mouseleave', $.proxy(me.onMouseleave, me))

    },
    /*鼠标进入*/
    onMousenter: function() {
      this.stop = true;
    },
    /*鼠标移出*/
    onMouseleave: function() {
      var me = this;
      me.stop = false;
      me.run(true);
    }
  });

  /*滚动方向钩子*/
  directionHooks = {
    'bottomup': {

      /*预处理*/
      preprocess: function() {
        var me = this;


        me.container.height(me.ctHieght||me.container.height());
        me.scrollOffset = me.el.children(me.itemSelector).outerHeight();
        //滚动方向决定克隆元素位置
        me.clone.insertAfter(me.el);
        me.container.scrollTop(0);
      },

      run: function(keep) {
        var me=this;
        
        if (!keep) {
          me.container.scrollTop(0);
        }

        me.roll(keep);
      },

      condition: function() {
        var me = this;
        return me.container.scrollTop() == me.clone[0].offsetTop;
      },

      /*滚动*/
      roll: function() {
        var me = this,
          container = me.container,
          ctScrollTop = container.scrollTop(),
          clone = me.clone;
        
        if (ctScrollTop == me.destOffset) {
          me.hold();
        } else {
          me.container.scrollTop(++ctScrollTop)
        }

      }
    },

    'topdown': {

      /*预处理*/
      preprocess: function() {
        var me = this;
        me.container.height(me.ctHieght||me.container.height());
        me.scrollOffset = me.el.children(me.itemSelector).outerHeight();

        //滚动方向决定克隆元素位置
        me.clone.insertBefore(me.el);
      },

      run: function(keep) {
        var me=this;

        if (!keep) {
          me.container.scrollTop(0);
        }

        me.roll(keep);
      },

      condition: function() {
        var me = this;
        return me.container.scrollTop() == me.el[0].offsetTop;
      },

      /*滚动*/
      roll: function() {
        var me = this,
          container = me.container,
          ctScrollTop = container.scrollTop(),
          clone = me.clone;

        if (ctScrollTop === me.destOffset) {
          me.hold();
        } else {
          me.container.scrollTop(++ctScrollTop)
        }

      }
    }
  }

});