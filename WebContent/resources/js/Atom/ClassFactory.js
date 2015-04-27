/**
 *
 * @authors 熊洋 (xywindows@gmail.com)
 * @date    2014-07-22 19:04:02
 * @description 类工厂
 */
define(function(require, exports, module) {
  var ClassFactory,

    Abstract = require('./util/Abstract'),

    array_shift = Array.prototype.shift;

  ClassFactory = module.exports = {
    
    /**
     * 定义一个类型
     * @param  {type}      parent      父类(可选，默认为Abstract)
     * @return {type}      类型
     */
    define: function( /*partial1,partial2,...*/ ) {
      var params = arguments,
        xtype, parent, klass,
        i, len, partial;

      //传入了类型名称
      if ($.type(params[0]) === 'string') {
        xtype = array_shift.call(params);
      }

      if (params.length > 1 && $.isFunction(params[0])) {
        parent = array_shift.call(params);
      }

      //类型
      klass = function() {
        this.init && this.init.apply(this, arguments);
      };

      klass.prototype = new proto();
      klass.prototype.constructor = klass;

      //类型名称
      if (xtype) {
        klass.prototype.xtype = xtype;
      }

      klass.superclass = parent;

      for (i = 0, len = params.length; i < len; i++) {
        this.addMember(klass, params[i]);
      }
      return klass;
    },

    /**
     * 添加成员
     * @param  {type}      type      需要扩展的类型(可选)
     * @param  {type}      type      需要扩展的类型(可选)
     * @return {void}      无返回值
     */
    addMember: function(type, proto) {
      var i, member, method, memberType;

      for (i in proto) {
        //不复制原型链 构造函数 
        if (i == "prototype" || i == "constructor" || i == 'subTypes' || i == 'superclass' || !proto.hasOwnProperty(i)) {
          continue;
        }

        member = proto[i];
        memberType = $.type(member);

        //静态方法
        if (i == 'statics' && memberType == 'object') {
          $.extend(type, member);
          continue;
        }

        if (memberType == 'function') {
          method = (function(m) {
            return function() {
              return m.apply(this, arguments);
            };
          })(member);

          method.$type = type;
          method.$name = i;

          type.prototype[i] = method;
          continue;
        }

        //公共属性
        type.prototype[i] = member;
      }

    }
  };

  /*原型*/
  function proto() {}
})