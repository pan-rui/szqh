package com.yjf.yrd.backstage.controller.trademanage;

import java.util.List;
import java.util.Map;

import com.yjf.yrd.util.AppConstantsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.yjf.common.lang.beans.cglib.BeanCopier;
import com.yjf.common.lang.util.ArrayUtil;
import com.yjf.common.lang.util.StringUtil;
import com.yjf.yrd.base.BaseAutowiredController;
import com.yjf.yrd.page.Page;
import com.yjf.yrd.util.LoanUtil;
import com.yjf.yrd.util.MiscUtil;
import com.yjf.yrd.web.util.PageUtil;
import com.yjf.yrd.ws.enums.DivisionPhaseEnum;
import com.yjf.yrd.ws.enums.DivisionWayEnum;
import com.yjf.yrd.ws.enums.SysUserRoleEnum;
import com.yjf.yrd.ws.info.DivisionRuleInfo;
import com.yjf.yrd.ws.info.DivisionTemplateInfo;
import com.yjf.yrd.ws.order.DivisionTemplateOrder;
import com.yjf.yrd.ws.service.DivisionTemplateManager;
import com.yjf.yrd.ws.service.query.result.QueryBaseBatchResult;

/**
 * 
 * 
 * 
 * @Filename DivisionTemplateController.java
 * 
 * @Description
 * 
 * @Version 1.0
 * 
 * @Author yhl
 * 
 * @Email yhailong@yiji.com
 * 
 * @History <li>Author: yhl</li> <li>Date: 2013-6-18</li> <li>Version: 1.0</li>
 * <li>Content: create</li> 分润模版控制器
 */
@Controller
@RequestMapping("backstage/divisiontemplate")
public class DivisionTemplateController extends BaseAutowiredController {
	
	@Autowired
	private DivisionTemplateManager divisionTemplateManager;
	
	/**
	 * 根据条件查询分润模板列表
	 * @param page 页码
	 * @param size 页大小
	 * @param name 模版查询条件：模版名称
	 * @param status 模版查询条件：模版状态
	 * @param model 视图参数
	 * @return
	 */
	@RequestMapping("conditions/{page}/{size}")
	public String getByConditions(@PathVariable int page, @PathVariable int size, String name,
									String status, Model model) {
		DivisionTemplateOrder searchOrder = new DivisionTemplateOrder();
		if ("all".equals(status)) {
			status = "";
		}
		searchOrder.setTemplateName(name);
		searchOrder.setTemplateStatus(status);
		searchOrder.setPageNumber(page);
		searchOrder.setPageSize(size);
		QueryBaseBatchResult<DivisionTemplateInfo> batchResult = divisionTemplateManager
			.findByCondition(searchOrder);
		Page<DivisionTemplateInfo> pages = PageUtil.getCovertPage(batchResult);
		model.addAttribute("page", pages);
        if(AppConstantsUtil.isTwoLevelBroker()){
            model.addAttribute("twoLevelBroker","Y");
        }
		model.addAttribute("divisionWay_month", DivisionWayEnum.MONTH_WAY);
		model.addAttribute("divisionWay_sit", DivisionWayEnum.SIT_WAY);
        model.addAttribute("divisionWay_interest", DivisionWayEnum.MONTH_PRINCIPAL_INTEREST);
        model.addAttribute("divisionWay_averageCapital", DivisionWayEnum.MONTH_AVERAGE_CAPITAL);
		return "backstage/divisiontemplate/divisiontemplate_list.vm";
	}
	
	/**
	 * 查看分润模版详情
	 * @param id 分润模版ID
	 * @return
	 */
	@ResponseBody
	@RequestMapping("lookup/{id}")
	public Object lookup(@PathVariable long id) {
		DivisionTemplateInfo d = divisionTemplateManager.getByTemplateId(id);
		List<DivisionRuleInfo> list = d.getRules();
		for (DivisionRuleInfo l : list) {
			String rule = LoanUtil.getRate_1(String.valueOf(l.getDivisionRule()));
			l.setDivisionRule(Double.parseDouble(rule));
		}
		d.setRules(list);
		Map<String, Object> map = MiscUtil.covertPoToMapJson(d);
		map.put("divisionPhase", d.getDivisionPhase().code());
		map.put("divisionWay", d.getDivisionWay().code());
		
		return map;
	}
	
	/**
	 * 修改分润模版
	 * @param template
	 * @return
	 */
	@ResponseBody
	@RequestMapping("modify")
	public Object modify(DivisionTemplateOrder template, int[] roleIds, double[] percentages) {
		try {
			DivisionTemplateOrder divisionTemplateOrder = new DivisionTemplateOrder();
			BeanCopier.staticCopy(template, divisionTemplateOrder);
			divisionTemplateOrder.setRoleIds(roleIds);
			divisionTemplateOrder.setPercentages(percentages);
			if (StringUtil.equalsIgnoreCase(DivisionPhaseEnum.INVESET_PHASE.code(),
				divisionTemplateOrder.getDivisionPhase())) {
				if (!ArrayUtil.contains(roleIds, SysUserRoleEnum.GUARANTEE.getValue())) {
					logger.error("没有选择担保机构");
					return false;
				}
			}
			divisionTemplateManager.modifyDivisionTemplate(divisionTemplateOrder);
			return true;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return false;
		}
	}
	
	/**
	 * 添加分润模版
	 * @param divisionTemplateOrder
	 * @param roleIds
	 * @param percentages
	 * @return
	 */
	@ResponseBody
	@RequestMapping("add")
	public Object add(DivisionTemplateOrder divisionTemplateOrder, int[] roleIds,
						double[] percentages) {
		try {
			
			divisionTemplateOrder.setRoleIds(roleIds);
			divisionTemplateOrder.setPercentages(percentages);
			if (StringUtil.equalsIgnoreCase(DivisionPhaseEnum.INVESET_PHASE.code(),
				divisionTemplateOrder.getDivisionPhase())) {
				if (!ArrayUtil.contains(roleIds, SysUserRoleEnum.GUARANTEE.getValue())) {
					logger.error("没有选择担保机构");
					return false;
				}
			}
			divisionTemplateOrder.getDivisionPhase();
			divisionTemplateManager.addDivisionTemplate(divisionTemplateOrder);
			return true;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return false;
		}
	}
	
	@ResponseBody
	@RequestMapping("changeStatus")
	public Object changeStatus(long templateId, String status, Model model) {
		JSONObject jsonObject = new JSONObject();
		try {  
			if (templateId > 0 && StringUtil.isNotEmpty(status)) {
				boolean isUse = divisionTemplateManager.isUseDivisionTemplate(templateId);
				if (isUse) {
					jsonObject.put("code", 0);
					jsonObject.put("message", "分润模板已经使用不能修改状态！");
					return jsonObject;
				}
				divisionTemplateManager.changeDivisionTemplateStatus(templateId, status);
				jsonObject.put("code", 1);
				jsonObject.put("message", "修改成功！");
			}
		} catch (Exception e) {
			logger.error("修改分润模板状态出错", e.getMessage(), e);
			jsonObject.put("code", 0);
			jsonObject.put("message", "修改失败！");
		}
		return jsonObject;
	}
}
