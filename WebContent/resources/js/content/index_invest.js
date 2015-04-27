
define(function(require, exports, module) {
	var filter=$('.u-filter'),
    skuList=filter.find('.m-sku-list'),
    //区间值选择符
    rangeSelector='[data-min][data-max]',
    activeCls='active',
    //名称映射钩子
    mapHooks;

  /**
   * 给各个已选中的筛选项添加激活样式
   */
  skuList.each(function(){
      var me=$(this),activeItem;

      /*获取每个筛选项的值*/
      activeItem=me.is(rangeSelector)?//范围值
          me.find('a[data-min="'+me.attr('data-min')+'"][data-max="'+me.attr('data-max')+'"]').parent()://dd
          me.find('a[data-val="'+me.attr('data-val')+'"]').parent();//dd

      //去除默认激活样式
      me.find('.'+activeCls).removeClass(activeCls);
      //给选中项添加激活样式
      activeItem.addClass(activeCls);
  });

  //名称映射钩子
  mapHooks={
	 'interestRate':{'min':'startRate','max':'endRate'}
  };

  //选中过滤项跳转到相应结果页面
  filter.on('click','.m-sku-list a',function(){
      var me=$(this),
          group;

      //该组根节点值
      group=me.parents('.m-sku-list');

      //给节点重新赋值
      if(me.is(rangeSelector)){//范围值
          group.attr('data-min',me.attr('data-min'));
          group.attr('data-max',me.attr('data-max'));
      }else{
          group.attr('data-val',me.attr('data-val'));
      }

      //跳转
      location.assign(url+'1?'+buildQueryString());
  });

  /**
   * 生成请求字符串
   */
  function buildQueryString(){
    var ret=[];

    //迭代生成
    skuList.each(function(){
      var me=$(this),
        name=me.attr('data-name'),
        val,hook;

        //区间值
        if(me.is(rangeSelector)&&(name in mapHooks)){
          hook=mapHooks[name];

          ret.push(hook.min+'='+me.attr('data-min'));
          ret.push(hook.max+'='+me.attr('data-max'));

        }else if(!me.is(rangeSelector)){
          ret.push(name+'='+me.attr('data-val'));
        }
        
    });

    return ret.join('&')
  }

  
	
	
<!--分页跳转-->
  window.toPage=function (totalPage, pageNo){
      if(totalPage<pageNo){
          return false;
      }
      //跳转
      location.assign(url+pageNo+"?"+buildQueryString());
  }

});