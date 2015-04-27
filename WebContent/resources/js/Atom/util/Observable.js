/**
 *
 * @authors 熊洋 (xywindows@gmail.com)
 * @date    2014-07-22 19:36:30
 * @description 简易版观察者模式
 */
define(function(require, exports, module) {
  var Observable,

    ClassFactory = require('../ClassFactory'),

    arraySlice = Array.prototype.slice;

  Observable = module.exports = ClassFactory.define('Observable', {

    /**
     * @private
     * 事件
     * @type {Object}
     */
    events: undefined,

    /**
     * @private
     * 初始化
     */
    init: function() {
      var me = this,
        events = me.events;

      me.events = {};

      if (events) {
        me.on(events);
      }
    },

    /**
     * 绑定事件
     * @param  {String}       event           事件名称
     * @param  {Function}     handler         事件处理函数
     * @param  {Object}       scope           事件处理函数上下文
     *
     * 1. observable.on('render',func,observer)
     *
     * 2. observable.on({
     *     'render':func
     *   })
     */
    on: function(event, handler, scope) {
      var me = this,
        eventName, eventObj, cache;

      //传入的为对象
      if ($.type(event) === 'object') {

        scope = event.scope;

        for (eventName in event) {

          if (eventName === 'scope') {
            continue;
          }

          me.on(eventName, event[eventName], scope)
        }

        return me;
      }

      //确认handler有效
      if (!handler) {
        return me;
      }

      //事件缓存
      cache = me.events;

      eventObj = {
        event: event,
        handler: handler,
        scope: scope,
        guid: handler.guid //$.proxy()会给函数添加guid
      };

      if (!cache[event]) {
        cache[event] = [];
      }

      cache[event].push(eventObj);

      return me;
    },

    /**
     * 触发事件
     * @param  {String}       eventName         事件名称
     * @return {Bool}         如果其中有一个事件返回false 那么返回值就为false
     */
    trigger: function(eventName /*,param1,param2,...*/ ) {
      var me = this,
        eventQueue = me.events[eventName],
        params, eventObj, i,
        len, handler, ret;

      //如果事件队列不存在
      //说明没该事件没有被订阅
      if (!eventQueue) {
        return;
      }

      params = arraySlice.call(arguments, 1);

      for (i = 0, len = eventQueue.length; i < len; i++) {
        eventObj = eventQueue[i];
        //处理函数
        handler = eventObj.handler;

        //确认处理函数是否有效
        if (!handler) {
          continue;
        }

        ret = ret !== false && handler.apply(eventObj.scope || me, params) !== false;
      }

      return ret;
    },

    /**
     * 解除事件绑定
     * @param  {String}         event             事件名称
     * @param  {Function}       handler           事件处理函数
     * @param  {Object}         scope             事件处理函数上下文
     *
     * 1.  observable.un();                       //解除该对象的所有事件
     *
     * 2.  observable.un({                        //解除render和request事件处理函数
     *       render:handler,
     *       request:handler2,
     *       scope:observer
     *     });
     *
     * 3.  observable.un('render');               //解除该对象参数对应的事件处理函数
     *     observable.un('render',fn)
     *     observable.un('render',scope)
     *     observable.un('render',fn,scope)
     */
    off: function(eventName, handler, scope) {
      var me = this,
        cache, event, eventQueue,
        i, len, eventObj;

      //整理参数
      if ($.type(handler) === 'object') {
        scope = handler;
        handler = undefined;
      }

      //例子2的情景：传入的为对象
      if ($.type(eventName) === 'object') {
        scope = eventName.scope;

        for (event in eventName) {

          if (event === 'scope') {
            continue;
          }

          me.un(event, eventName[event].handler, eventName.scope);
        }

        return me;

      }
      //例子1的情景：没有传入任何参数
      else if (eventName === undefined) {
        //浅度复制
        cache = $.extend({}, me.cache);

        for (event in cache) {
          me.un(event);
        }

        return me;
      }

      eventQueue = me.events[eventName];

      //该事件没有被订阅;
      if (!eventQueue || eventQueue.length === 0) {
        return me;
      }

      //例子3的情景：需要解除的handler和scope都不存在
      if (!handler || !scope) {
        eventQueue.length = 0;
        delete me.events[eventName];
        return me;
      }

      for (i = 0, len = eventQueue.length; i < len; i++) {
        //事件对象
        eventObj = eventQueue[i];

        if (
          ((handler === undefined || (eventObj.handler === handler)) ||
            ((eventObj.guid !== undefined && (eventObj.guid === handler.guid)))) &&
          (scope === undefined || (eventObj.scope === scope))
        ) {
          eventQueue.splice(i);
          i--;
          len--;

          //如果该事件下已经木有绑定的事件处理函数了
          //删除该事件的队列
          if (eventQueue.length === 0) {
            delete me.events[eventName];
          }
        }
      }

      return me;
    },

    /**
     * 销毁
     */
    destroy: function() {
      me.un();
      delete me.events;
    }
  });

  return Observable;
})