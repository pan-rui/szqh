package com.yjf.yrd.backstage.controller.pointsmanage;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.yjf.yrd.base.BaseAutowiredController;
import com.yjf.yrd.page.PageParam;
import com.yjf.yrd.session.SessionLocal;
import com.yjf.yrd.session.SessionLocalManager;
import com.yjf.yrd.util.StringUtil;
import com.yjf.yrd.web.util.PageUtil;
import com.yjf.yrd.ws.enums.PointsRuleStateEnum;
import com.yjf.yrd.ws.enums.PointsRuleTypeEnum;
import com.yjf.yrd.ws.info.PointsRuleDetailInfo;
import com.yjf.yrd.ws.info.PointsRuleInfo;
import com.yjf.yrd.ws.order.CreatePointsRuleOrder;
import com.yjf.yrd.ws.order.UpdatePointsRuleStateOrder;
import com.yjf.yrd.ws.result.YrdBaseResult;
import com.yjf.yrd.ws.service.query.order.PointsRuleQueryOrder;
import com.yjf.yrd.ws.service.query.result.QueryBaseBatchResult;

@Controller
@RequestMapping("backstage/pointsRule")
public class PointsRuleController extends BaseAutowiredController {
	/**
	 * 页面所在路径
	 */
	private final String BORROWING_MANAGE__PATH = "/backstage/pointsRule/";
	
	/**
	 * 分页查询
	 */
	@RequestMapping(value = "pageQueryPointsRule")
	public String pageQueryPointsRule(PointsRuleQueryOrder queryOrder, PageParam pageParam,
										Model model) {
		try {
			List<PointsRuleTypeEnum> ruleTypes = PointsRuleTypeEnum.getAllEnum();
			model.addAttribute("ruleTypes", ruleTypes);
			
			queryOrder.setPageNumber(pageParam.getPageNo());
			queryOrder.setPageSize(pageParam.getPageSize());
			QueryBaseBatchResult<PointsRuleInfo> page = pointsRuleService
				.queryPointsRule(queryOrder);
			model.addAttribute("queryConditions", queryOrder);
			model.addAttribute("page", PageUtil.getCovertPage(page));
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return BORROWING_MANAGE__PATH + "pageQueryPointsRuleInfo.vm";
	}
	
	@RequestMapping(value = "addPointsRule")
	public String addPointsRule(Model model) {
		
		List<PointsRuleTypeEnum> ruleTypes = PointsRuleTypeEnum.getAllEnum();
		model.addAttribute("ruleTypes", ruleTypes);
		
		return BORROWING_MANAGE__PATH + "addPointsRule.vm";
	}
	
	@ResponseBody
	@RequestMapping(value = "addPointsRuleSubmit")
	public Object addPointsRuleSubmit(CreatePointsRuleOrder createOrder) throws Exception {
		JSONObject json = new JSONObject();
		SessionLocal sessionLocal = SessionLocalManager.getSessionLocal();
		if (sessionLocal == null) {
			json.put("code", 0);
			json.put("message", "新增积分规则失败,登录失效!");
			return json;
		}
		
		YrdBaseResult result = pointsRuleService.addPointsRule(createOrder);
		if (result.isSuccess()) {
			json.put("code", 1);
			json.put("message", "新增积分规则成功!");
		} else {
			json.put("code", 0);
			json.put("message", "新增积分规则失败:" + result.getMessage());
			
		}
		return json;
	}
	
	@RequestMapping(value = "updatePointsRule")
	public String updatePointsRule(long pointsRuleId, Boolean isCopy, Model model) {
		List<PointsRuleTypeEnum> ruleTypes = PointsRuleTypeEnum.getAllEnum();
		model.addAttribute("ruleTypes", ruleTypes);
		
		PointsRuleQueryOrder queryOrder = new PointsRuleQueryOrder();
		queryOrder.setPageNumber(1);
		queryOrder.setPageSize(1);
		queryOrder.setPointsRuleId(pointsRuleId);
		QueryBaseBatchResult<PointsRuleInfo> page = pointsRuleService.queryPointsRule(queryOrder);
		PointsRuleInfo info = page.getPageList().get(0);
		model.addAttribute("info", info);
		
		List<PointsRuleDetailInfo> dInfos = pointsRuleService.queryRuleDetailByRuleId(pointsRuleId);
		
		String ruleType = info.getRuleType().code();
		if (PointsRuleTypeEnum.INVEST.code().equalsIgnoreCase(ruleType)) {//投资送积分
		
			if (StringUtil.isEmpty(dInfos.get(0).getInvestPeriodType())) {
				//投资所有项目
				long pointsValue = dInfos.get(0).getPointsValue();
				long investAmount = dInfos.get(0).getInvestAmount();
				model.addAttribute("i_investAmount", investAmount);
				model.addAttribute("i_pointsValue", pointsValue);
			} else {
				//按投资周期长短设置积分规则
				model.addAttribute("dInfos", dInfos);
			}
		} else {
			long pointsValue = dInfos.get(0).getPointsValue();
			model.addAttribute("s_pointsValue", pointsValue);
		}
		
		if (isCopy == null || !isCopy.booleanValue()) {
			return BORROWING_MANAGE__PATH + "updatePointsRule.vm";
		} else {
			return BORROWING_MANAGE__PATH + "copyPointsRule.vm";
		}
		
	}
	
	@ResponseBody
	@RequestMapping(value = "updatePointsRuleSubmit")
	public Object updatePointsRuleSubmit(CreatePointsRuleOrder createOrder) throws Exception {
		JSONObject json = new JSONObject();
		SessionLocal sessionLocal = SessionLocalManager.getSessionLocal();
		if (sessionLocal == null) {
			json.put("code", 0);
			json.put("message", "修改积分规则,登录失效!");
			return json;
		}
		
		YrdBaseResult result = pointsRuleService.updatePointsRule(createOrder);
		if (result.isSuccess()) {
			json.put("code", 1);
			json.put("message", "修改积分规则成功!");
		} else {
			json.put("code", 0);
			json.put("message", result.getMessage());
			
		}
		return json;
	}
	
	@ResponseBody
	@RequestMapping("processPointsRule")
	public Object processPointsRule(String proType, long pointsRuleId) throws Exception {
		JSONObject json = new JSONObject();
		if ("stop".equals(proType)) {//停用
			String state = PointsRuleStateEnum.STOP.code();
			UpdatePointsRuleStateOrder updateOrder = new UpdatePointsRuleStateOrder();
			updateOrder.setPointsRuleId(pointsRuleId);
			updateOrder.setState(state);
			YrdBaseResult result = pointsRuleService.updatePointsRuleState(updateOrder);
			if (result.isSuccess()) {
				json.put("code", 1);
				json.put("message", "停用成功!");
			} else {
				json.put("code", 0);
				json.put("message", "停用失败:" + result.getMessage());
			}
		} else if ("delete".equals(proType)) {//删除
			YrdBaseResult result = pointsRuleService.deletePointsRule(pointsRuleId);
			if (result.isSuccess()) {
				json.put("code", 1);
				json.put("message", "删除成功!");
			} else {
				json.put("code", 0);
				json.put("message", "删除失败:" + result.getMessage());
			}
		}
		
		return json;
	}
	
	@ResponseBody
	@RequestMapping(value = "checkRuleName")
	public Object checkRuleName(String ruleName) throws Exception {
		logger.info("验证借款人，入参[{}]", ruleName);
		JSONObject jsonobj = new JSONObject();
		PointsRuleInfo ruleInfo = pointsRuleService.queryByRuleName(ruleName);
		if (ruleInfo == null) {
			jsonobj.put("code", 1);
		} else {
			jsonobj.put("code", 0);
			jsonobj.put("message", "该规则名称已存在！");
		}
		return jsonobj;
	}
	
}
