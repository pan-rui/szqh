package com.yjf.yrd.backstage.controller.systemSet;

import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.yjf.common.lang.util.StringUtil;
import com.yjf.yrd.base.BaseAutowiredController;
import com.yjf.yrd.dal.dataobject.SysParamDO;
import com.yjf.yrd.page.PageParam;
import com.yjf.yrd.util.DateUtil;
import com.yjf.yrd.web.util.PageUtil;
import com.yjf.yrd.ws.order.SysParamOrder;
import com.yjf.yrd.ws.result.YrdBaseResult;
import com.yjf.yrd.ws.service.query.order.SysParamQueryOrder;
import com.yjf.yrd.ws.service.query.result.QueryBaseBatchResult;

/**
 * 系统参数
 * Created by wqh on 2014/5/15.
 */
@Controller
@RequestMapping("backstage")
public class SystemParamController extends BaseAutowiredController  {
    private final String vm_path = "/backstage/systemSet/";

    /**
     * 系统参数管理页面
     * @param paramName
     * @param pageParam
     * @param response
     * @param model
     * @return
     * @throws Exception
     */
    @RequestMapping("sysParamManage")
    public String sysParamManage(String paramName, PageParam pageParam,
                                 HttpServletResponse response, Model model) throws Exception {
        SysParamQueryOrder queryOrder = new SysParamQueryOrder();
        queryOrder.setParamName(paramName);
        queryOrder.setPageSize(pageParam.getPageSize());
        queryOrder.setPageNumber(pageParam.getPageNo());
        QueryBaseBatchResult<SysParamDO> queryBaseBatchResult  = sysParameterService.querySysPram(queryOrder);
        model.addAttribute("page",  PageUtil.getCovertPage(queryBaseBatchResult));
        model.addAttribute("queryConditions",queryOrder);
        response.setHeader("Pragma", "No-cache");
        return vm_path + "sysParamManage.vm";
    }

    /**
     * 转到新增页面
     * @param response
     * @param model
     * @return
     * @throws Exception
     */
	@RequestMapping("sysParamManage/toAddSysParam")
    public String toAddSysParamManager(HttpServletResponse response, Model model) throws Exception {
            return vm_path + "addSysParam.vm";
    }

    /**
     * 转到编辑页面
     * @param paramName
     * @param response
     * @param model
     * @return
     * @throws Exception
     */
	@RequestMapping("sysParamManage/editSysParam")
    public String editSysParamManager(String paramName,
                                    HttpServletResponse response, Model model) throws Exception {
        SysParamDO sysParamDO  = sysParameterService.getSysParameterValueDO(paramName);
        model.addAttribute("info", sysParamDO);
        return vm_path + "editSysParam.vm";
    }
	
    /**
     * 更新系统参数
     * @param param_name
     * @param param_value
     * @param extend_attribute_one
     * @param extend_attribute_two
     * @param rawAddTime
     * @param description
     * @param response
     * @param model
     * @return
     * @throws Exception
     */
    @ResponseBody
	@RequestMapping("sysParamManage/updateSysParam")
    public Object updateSystemParamManager(String param_name ,String param_value,String extend_attribute_one,
                                           String extend_attribute_two,String rawAddTime,String description,
                                  HttpServletResponse response, Model model) throws Exception {
        SysParamOrder sysParamOrder = new SysParamOrder();
        sysParamOrder.setParamName(param_name);
        sysParamOrder.setParamValue(param_value);
        sysParamOrder.setExtendAttributeOne(extend_attribute_one);
        sysParamOrder.setExtendAttributeTwo(extend_attribute_two);
        if(StringUtil.isNotBlank(rawAddTime)){
            sysParamOrder.setRawAddTime(DateUtil.parse(rawAddTime));
        }else{
            sysParamOrder.setRawAddTime(new Date());
        }
        sysParamOrder.setDescription(description);
        JSONObject json = new JSONObject();
            logger.info("更新系统参数，入参{}", sysParamOrder);
            YrdBaseResult result = sysParameterService.updateSysParameterValueDO(sysParamOrder);
            if(result.isSuccess()) {
                json.put("message", "更新系统参数成功");
            }else {
                json.put("message", "更新系统参数失败");
            }
        return json;
    }

    /**
     * 新增系统参数
     * @param param_name
     * @param param_value
     * @param extend_attribute_one
     * @param extend_attribute_two
     * @param rawAddTime
     * @param description
     * @param response
     * @param model
     * @return
     * @throws Exception
     */
    @ResponseBody
	@RequestMapping("sysParamManage/addSysParam")
    public Object addSysParamManager(String param_name ,String param_value,String extend_attribute_one,
                                     String extend_attribute_two,String rawAddTime,String description,
                                          HttpServletResponse response, Model model) throws Exception {
        SysParamOrder sysParamOrder = new SysParamOrder();
        sysParamOrder.setParamName(param_name);
        sysParamOrder.setParamValue(param_value);
        sysParamOrder.setExtendAttributeOne(extend_attribute_one);
        sysParamOrder.setExtendAttributeTwo(extend_attribute_two);
        sysParamOrder.setRawAddTime(new Date());
        sysParamOrder.setDescription(description);;
        JSONObject json = new JSONObject();
        SysParamDO sysParamDO = sysParameterService.getSysParameterValueDO(param_name);
        if(sysParamDO != null){
            json.put("message","参数名称已经存在");
            return json;
        }
        logger.info("新增系统参数，入参{}", sysParamOrder);
        YrdBaseResult result = sysParameterService.insertSysParameterValueDO(sysParamOrder);
        if(result.isSuccess()) {
            json.put("message", "新增系统参数成功");
        }else {
            json.put("message", "新增系统参数失败");
        }
        return json;


    }

}
