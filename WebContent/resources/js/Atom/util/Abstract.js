/**
 *
 * @authors 熊洋 (xywindows@gmail.com)
 * @date    2014-07-22 19:32:10
 * @description 抽象基类
 *              主要实现callParent方法
 *              便于在子类中调用父类方法
 */
define(function(require, exports, module) {
  var Abstract;

  Abstract = module.exports = function() {};

  Abstract.prototype.xtype = 'Abstract';

  /**
   * 调用基类中的方法
   * @param  {String}     method      方法名
   * @param  {Array}      args        参数列表
   *
   *
   * 1.调用当前方法的基类版本
   * this.callParent(arguments);
   *
   * 2.调用基类中的render方法
   * this.callParent('render',[container,position]);
   *
   */
  Abstract.prototype.callParent = function(method, args) {
    var methodName = $.type(method) == 'string' ? method : false,
      caller,
      superPorto;

    //没有提供方法名
    if (!methodName) {
      args = method;
      caller = method.callee.caller;

      methodName = caller.$name;
      superPorto = caller.$type.superclass.prototype;
    }

    //没有找到方法名直接返回
    if (!methodName) {
      return;
    }

    if (!superPorto) {
      caller = arguments.callee.caller.caller;
      superPorto = caller.$type.superclass.prototype;

      //没有找到 直接gohome
      if (!superPorto) {
        return;
      }
    }

    return methodName in superPorto ?
      superPorto[methodName].apply(this, args || []) : null;
  };
})