package com.yjf.yrd.front.controller.app.newVers.e8Trunck.v2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.yjf.common.lang.util.ListUtil;
import com.yjf.common.lang.util.StringUtil;
import com.yjf.yrd.front.controller.trade.base.LoanTradeDetailBaseController;
import com.yjf.yrd.page.PageParam;
import com.yjf.yrd.service.profit.ProfitManager;
import com.yjf.yrd.service.profit.order.BorkerProfitSettingOrder;
import com.yjf.yrd.session.SessionLocalManager;
import com.yjf.yrd.user.info.InvestProfitAsignInfo;
import com.yjf.yrd.user.info.PersonalInfo;
import com.yjf.yrd.user.info.PersonalVOInfo;
import com.yjf.yrd.user.info.UserInfo;
import com.yjf.yrd.user.query.order.QueryUserChildrenOrder;
import com.yjf.yrd.user.valueobject.QueryConditions;
import com.yjf.yrd.util.CommonUtil;
import com.yjf.yrd.util.DateUtil;
import com.yjf.yrd.web.util.WebUtil;
import com.yjf.yrd.ws.enums.SysUserRoleEnum;
import com.yjf.yrd.ws.info.TradeDetailVOInfo;
import com.yjf.yrd.ws.order.TradeDetailQueryOrder;
import com.yjf.yrd.ws.result.YrdBaseResult;
import com.yjf.yrd.ws.service.query.result.QueryBaseBatchResult;
import com.yjf.yrd.ws.service.query.result.TradeDetailBatchResult;

/**
 * 
 * 
 * @Filename appNewBrokerController.java
 * 
 * @Description
 * 
 * @Version 2.0
 * 
 * @Author zhaohaibing
 * 
 * @Email abing@yiji.com
 * 
 * Date: 2014-9-1
 * 
 * 
 */
@Controller
@RequestMapping("app")
public class NewBrokerManagerController extends LoanTradeDetailBaseController {
	
	// -----------------------------------------------------投资者管理----------------------------------------------------------------------
	@Autowired
	ProfitManager profitManager;
	
	/**
	 * 12、客户管理
	 * 
	 * @param pageSize
	 * @param currentPage
	 * @param userName
	 * @param realName
	 * */
	@ResponseBody
	@RequestMapping("appCustomerManager.htm")
	public JSONObject investorManageApp(PageParam pageParam, Long currentPage, Long pageNumber,
										QueryConditions queryConditions,
										HttpServletRequest request, Model model) throws Exception {
		
		JSONObject json = new JSONObject();
		if (!AppCommonUtil.isLogin()) {
			json.put("code", -1);
			json.put("message", "未登陆或登陆失效");
			return json;
		}
		
		QueryUserChildrenOrder commonQueryOrder = new QueryUserChildrenOrder();
		WebUtil.setPoPropertyByRequest(commonQueryOrder, request);
		commonQueryOrder.setUserId(SessionLocalManager.getSessionLocal().getUserId());
		if (pageNumber != null) {
			commonQueryOrder.setPageNumber(pageNumber);
		} else if (currentPage != null) {
			commonQueryOrder.setPageNumber(currentPage);
		} else {
			commonQueryOrder.setPageNumber(pageParam.getPageNo());
		}
		commonQueryOrder.setPageSize(pageParam.getPageSize());
		commonQueryOrder.setHasInvestorDistributionQuota(true);
		QueryBaseBatchResult<PersonalVOInfo> baseBatchResult = userQueryService
			.queryUserChildren(commonQueryOrder);
		
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		if (baseBatchResult.getPageList().size() > 0) {
			for (PersonalVOInfo info : baseBatchResult.getPageList()) {
				Map<String, String> map = new HashMap<String, String>();
				String state = "未激活";
				if ("normal".equalsIgnoreCase(info.getState().getCode())) {
					state = "正常";
				}
				String fenpei = "未分配";
				InvestProfitAsignInfo profitAsignInfo = profitManager.queryByReceiveId(info
					.getUserId());
				if (profitAsignInfo != null) {
					fenpei = CommonUtil.mul(profitAsignInfo.getDistributionQuota(), 100) + "";
				}
				map.put("userName", info.getUserName());
				map.put("customerId", info.getUserBaseId());
				map.put("realName", info.getRealName());
				map.put("profit", fenpei);
				map.put("status", state);
				list.add(AppCommonUtil.cleanNull(map));
			}
			json.put("totalPage", baseBatchResult.getPageCount());
			json.put("investorsCount", baseBatchResult.getTotalCount());
			json.put("code", 1);
			json.put("message", "获取客户信息成功！");
			json.put("investorManage", list);
		} else {
			json.put("investorsCount", 0);
			json.put("code", 1);
			json.put("message", "暂无客户信息！");
		}
		
		return json;
	}
	
	// -----------------------------------------------------客户详情----------------------------------------------------------------------
	/**
	 * 13、客户管理-客户详情
	 * 
	 * @author customerId == userBaseId
	 * 
	 * */
	@ResponseBody
	@RequestMapping("appCustomerBaseInfo.htm")
	public JSONObject investorInfo(String customerId, Model model) throws Exception {
		JSONObject json = new JSONObject();
		if (!AppCommonUtil.isLogin()) {
			json.put("code", -1);
			json.put("message", "未登录或登录已失效");
			return json;
		}
		this.initAccountInfo(model);
		logger.info("手机app用户请求获取投资者详情：userBaseId={}", customerId);
		UserInfo userBase = userQueryService.queryByUserBaseId(customerId).getQueryUserInfo();
		if ("JG".equals(userBase.getType())) {
			json.put("code", 0);
			json.put("message", "暂无机构用户信息查询功能");
			return json;
		}
		UserInfo userInfo = userQueryService.queryByUserBaseId(customerId).getQueryUserInfo();
		PersonalInfo personalInfo = userQueryService.queryPersonalInfoByBaseId(customerId)
			.getQueryPersonalInfo();
		Map<String, String> map = new HashMap<String, String>();
		map.put("userName", personalInfo.getUserName());
		String state = "未激活";
		if ("normal".equalsIgnoreCase(userBase.getState().getCode())) {
			state = "正常";
		}
		map.put("userName", userInfo.getUserName());
		map.put("statues", state);
		map.put("realName", userInfo.getRealName());
		map.put("certNo", personalInfo.getCertNo());
		map.put("mobile", userInfo.getMobile());
		map.put("mail", userInfo.getMail());
		
		/******** 查看收益配置 ********/
		UserInfo reciever = userQueryService.queryByUserBaseId(customerId).getQueryUserInfo();
		Long receiveId = reciever.getUserId();
		InvestProfitAsignInfo profitAsignInfo = profitManager.queryByReceiveId(receiveId);
		if (profitAsignInfo != null) {
			map.put("profit", CommonUtil.mul(profitAsignInfo.getDistributionQuota(), 100) + "");
			map.put("tblBaseId", profitAsignInfo.getTblBaseId());
			map.put("note", profitAsignInfo.getNote());
		} else {
			map.put("profit", "0");
			map.put("tblBaseId", "");
			map.put("note", "");
		}
		
		json.put("customerBaseInfo", AppCommonUtil.cleanNull(map));
		json.put("code", 1);
		json.put("message", "获取客户信息成功");
		return json;
	}
	
	/**
	 * 收益配置
	 * 
	 * @param customerId 当前客户的userBaseId
	 * @param tblBaseId
	 * @param limit
	 * @param note
	 * */
	@ResponseBody
	@RequestMapping("appSetQuota.htm")
	public JSONObject setQuota(String customerId, String tblBaseId, double limit, String note)
																								throws Exception {
		
		JSONObject json = new JSONObject();
		if (!AppCommonUtil.isLogin()) {
			json.put("code", -1);
			json.put("message", "未登录或登录已失效");
			return json;
		}
		
		if (limit < 0 || limit > 100) {
			json.put("code", 0);
			json.put("message", "额度介于0-100之间，必须大于0！");
			return json;
		}
		limit = CommonUtil.div(limit, 100);
		try {
			
			UserInfo reciever = userQueryService.queryByUserBaseId(customerId).getQueryUserInfo();
			Long distributionId = SessionLocalManager.getSessionLocal().getUserId();
			Long receiveId = reciever.getUserId();
			if (StringUtil.isNotEmpty(tblBaseId)) {
				BorkerProfitSettingOrder profitSettingOrder = new BorkerProfitSettingOrder();
				profitSettingOrder.setTblBaseId(tblBaseId);
				profitSettingOrder.setDistributionId(distributionId);
				profitSettingOrder.setReceiveId(receiveId);
				profitSettingOrder.setDistributionQuota(limit);
				profitSettingOrder.setNote(note);
				YrdBaseResult baseResult = profitManager.brokerAddProfitSetting(profitSettingOrder);
				if (baseResult.isSuccess()) {
					json.put("code", "1");
					json.put("message", "设置佣金成功");
				} else {
					json.put("code", "0");
					json.put("message", "设置佣金失败");
				}
			} else {
				String uuid = UUID.randomUUID().toString();
				BorkerProfitSettingOrder profitSettingOrder = new BorkerProfitSettingOrder();
				profitSettingOrder.setDistributionId(distributionId);
				profitSettingOrder.setReceiveId(receiveId);
				profitSettingOrder.setDistributionQuota(limit);
				profitSettingOrder.setNote(note);
				profitSettingOrder.setTblBaseId(uuid);
				
				YrdBaseResult baseResult = profitManager.brokerAddProfitSetting(profitSettingOrder);
				if (baseResult.isSuccess()) {
					json.put("code", 1);
					json.put("message", "设置佣金成功!");
				} else {
					json.put("code", 0);
					json.put("message", "设置佣金失败!");
					logger.info("设置佣金失败:{}", baseResult);
				}
			}
		} catch (Exception e) {
			json.put("code", 0);
			json.put("message", "设置佣金异常!");
			logger.error(e.getMessage(), e);
		}
		
		return json;
		
	}
	
	// -------------------------业务管理-经纪人-------------------------营销交易列表----------------------------------------------------------------------
	
	/**
	 * 16、业务管理 V
	 * 
	 * @param
	 * */
	@ResponseBody
	@RequestMapping("appBusinessManager.htm")
	public JSONObject salesList(TradeDetailQueryOrder queryConditions) throws Exception {
		
		JSONObject json = new JSONObject();
		if (!AppCommonUtil.isLogin()) {
			json.put("code", -1);
			json.put("message", "未登录或登录已失效");
			return json;
		}
		queryConditions.setUserId(SessionLocalManager.getSessionLocal().getUserId());
		if (queryConditions.getStatus() != null)
			queryConditions.getStatus().clear();
		queryConditions.setRoleId(SysUserRoleEnum.BROKER.getValue());
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		TradeDetailBatchResult<TradeDetailVOInfo> page = tradeBizQueryService
			.searchTradeDetailQuery(queryConditions);
		if (page.isSuccess() && ListUtil.isNotEmpty(page.getPageList())) {
			for (TradeDetailVOInfo info : page.getPageList()) {
				Map<String, String> map = new HashMap<String, String>();
				String expireDate = "暂无";
				if (null != info.getTradeExpireDate()) {
					expireDate = DateUtil.simpleFormat(info.getTradeExpireDate());
				}
				map.put("originalRealName", info.getOriginalRealName());
				map.put("benefitAmount", info.getBenefitAmount().toStandardString());
				map.put("payDate", expireDate);
				map.put("tradeName", info.getTradeName());
				list.add(AppCommonUtil.cleanNull(map));
			}
			
			json.put("totalPage", page.getPageCount());
			json.put("code", 1);
			json.put("message", "查询交易信息成功！");
			json.put("salesList", list);
			json.put("totalMoney", page.getTotalAmount().toStandardString());
		} else {
			json.put("code", 0);
			json.put("message", "暂无交易信息！");
			json.put("totalpage", 0);
			json.put("totalMoney", page.getTotalAmount().toStandardString());
		}
		
		return json;
	}
	
	private String getViewStatues(String renzheng) {
		if ("IS".equals(renzheng)) {
			renzheng = "已认证";
		} else if ("NO".equals(renzheng)) {
			renzheng = "认证未通过";
		} else if ("IN".equals(renzheng)) {
			renzheng = "认证中";
		} else {
			renzheng = "未认证";
		}
		return renzheng;
	}
	
	@Override
	public String getNoTradeView() {
		return null;
	}
	
	@Override
	public String getTradeView() {
		return null;
	}
}
