package com.yjf.yrd.front.controller.app.newVers.e8Trunck;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.yjf.common.lang.util.ListUtil;
import com.yjf.common.lang.util.StringUtil;
import com.yjf.yrd.front.controller.app.newVers.e8Trunck.v2.AppCommonUtil;
import com.yjf.yrd.front.controller.trade.base.LoanTradeDetailBaseController;
import com.yjf.yrd.service.profit.ProfitManager;
import com.yjf.yrd.service.profit.order.BorkerProfitSettingOrder;
import com.yjf.yrd.session.SessionLocal;
import com.yjf.yrd.session.SessionLocalManager;
import com.yjf.yrd.user.info.InvestProfitAsignInfo;
import com.yjf.yrd.user.info.PersonalInfo;
import com.yjf.yrd.user.info.PersonalVOInfo;
import com.yjf.yrd.user.info.UserInfo;
import com.yjf.yrd.user.query.order.QueryUserChildrenOrder;
import com.yjf.yrd.user.valueobject.QueryConditions;
import com.yjf.yrd.util.CommonUtil;
import com.yjf.yrd.util.DateUtil;
import com.yjf.yrd.web.util.PageUtil;
import com.yjf.yrd.web.util.WebUtil;
import com.yjf.yrd.ws.enums.RealNameAuthStatusEnum;
import com.yjf.yrd.ws.enums.SysUserRoleEnum;
import com.yjf.yrd.ws.enums.UserTypeEnum;
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
 *        Date: 2014-9-1
 * 
 * 
 */
@Controller
@RequestMapping("appNew")
public class AppNewBrokerController extends LoanTradeDetailBaseController {
	/**
	 * 返回页面路径
	 */
	@Autowired
	ProfitManager profitManager;

	// -----------------------------------------------------投资者管理----------------------------------------------------------------------

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
	public JSONObject investorManageApp(String userName, String realName,
			HttpServletRequest request, HttpServletResponse response,
			QueryConditions queryConditions, long currentPage, long pageSize,
			Model model) throws Exception {
		logger.info("手机app用户请求获取客户信息！");
		JSONObject json = new JSONObject();
		if (AppCommonUtil.isLogin()) {
			// this.initAccountInfo(model);
			QueryUserChildrenOrder commonQueryOrder = new QueryUserChildrenOrder();
			WebUtil.setPoPropertyByRequest(commonQueryOrder, request);
			commonQueryOrder.setUserId(SessionLocalManager.getSessionLocal()
					.getUserId());
			commonQueryOrder.setPageNumber(currentPage);
			commonQueryOrder.setPageSize(pageSize);
			commonQueryOrder.setHasInvestorDistributionQuota(true);
			QueryBaseBatchResult<PersonalVOInfo> baseBatchResult = userQueryService
					.queryUserChildren(commonQueryOrder);
			model.addAttribute("page", PageUtil.getCovertPage(baseBatchResult));

			List<Map<String, String>> list = new ArrayList<Map<String, String>>();
			if (baseBatchResult.getPageList().size() > 0) {
				for (PersonalVOInfo info : baseBatchResult.getPageList()) {
					Map<String, String> map = new HashMap<String, String>();
					String fenpei = "未分配";
					String state = "未激活";
					if ("normal".equalsIgnoreCase(info.getState().getCode())) {
						state = "正常";
					}
					InvestProfitAsignInfo profitAsignInfo = profitManager
							.queryByReceiveId(info.getUserId());
					if (profitAsignInfo != null) {
						fenpei = CommonUtil.mul(
								profitAsignInfo.getDistributionQuota(), 100)
								+ "";
					}
					map.put("customerId", info.getUserBaseId());
					map.put("realName", info.getRealName());
					map.put("profit", fenpei);
					map.put("state", state);
					list.add(map);
				}

			}
			json.put("investNum", baseBatchResult.getTotalCount());
			json.put("code", 1);
			json.put("message", "获取客户信息成功！");
			json.put("investorManage", list);
			logger.info("新版app用户:经纪人({})请求获取客户信息成功：list={}",
					SessionLocalManager.getSessionLocal().getUserName(), list);
		} else {
			json.put("code", -1);
			json.put("message", "获取客户信息失败：未登录或登录失效！");
			logger.info("手机app用户请求获取客户信信息失败：list={}", "未登录或登录已失效");
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
	public JSONObject investorInfo(HttpServletResponse response,
			String customerId, Model model) throws Exception {
		JSONObject json = new JSONObject();
		logger.info("手机app用户请求获取投资者详情：userBaseId={}", customerId);
		UserInfo userBase = userQueryService.queryByUserBaseId(customerId)
				.getQueryUserInfo();
		if (!AppCommonUtil.isLogin()) {
			json.put("code", -1);
			json.put("message", "未登录或登录已失效");
			return json;
		}
		this.initAccountInfo(model);
		if (userBase.getType() == UserTypeEnum.JG) {
			json.put("code", 0);
			json.put("message", "暂未对该用户类型开发功能");
		} else {
			UserInfo userInfo = userQueryService.queryByUserBaseId(customerId)
					.getQueryUserInfo();
			PersonalInfo personalInfo = userQueryService
					.queryPersonalInfoByBaseId(customerId)
					.getQueryPersonalInfo();
			Map<String, String> map = new HashMap<String, String>();
			String status = "未认证";
			RealNameAuthStatusEnum statu = userInfo.getRealNameAuthentication();
			if (statu != null) {
				status = statu.getMessage();
			}
			map.put("icCard", personalInfo.getCertNo());
			map.put("userName", personalInfo.getUserName());
			map.put("state", status);
			map.put("realName", personalInfo.getRealName());
			map.put("phone", personalInfo.getMobile());
			map.put("email", personalInfo.getMail());

			/******** 查看收益配置 ********/

			UserInfo reciever = userQueryService.queryByUserBaseId(customerId)
					.getQueryUserInfo();
			Long receiveId = reciever.getUserId();
			InvestProfitAsignInfo profitAsignInfo = profitManager
					.queryByReceiveId(receiveId);
			if (profitAsignInfo != null) {
				map.put("profit",
						CommonUtil.mul(profitAsignInfo.getDistributionQuota(),
								100) + "");
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

		}
		logger.info("新版app用户:经纪人({})获取客户信息成功：json={}", SessionLocalManager
				.getSessionLocal().getUserName(), json);
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
	public JSONObject salesList(TradeDetailQueryOrder queryConditions,
			int pageSize, int currentPage, HttpServletRequest request,
			HttpServletResponse response, Model model) throws Exception {

		SessionLocal sessionLocal = SessionLocalManager.getSessionLocal();
		if (sessionLocal != null && sessionLocal.getAccountId() != null) {
			this.initAccountInfo(model);
		}
		JSONObject json = new JSONObject();
		if (AppCommonUtil.isLogin()) {
			model.addAttribute("queryConditions", queryConditions);
			queryConditions.setUserId(SessionLocalManager.getSessionLocal()
					.getUserId());
			if (queryConditions.getStatus() != null)
				queryConditions.getStatus().clear();
			queryConditions.setRoleId(SysUserRoleEnum.BROKER.getValue());
			queryConditions.setPageSize(pageSize);
			queryConditions.setPageNumber(currentPage);
			queryConditions.setEndDate(request.getParameter("endDate"));
			queryConditions.setStartDate(request.getParameter("startDate"));
			List<Map<String, String>> list = new ArrayList<Map<String, String>>();
			TradeDetailBatchResult<TradeDetailVOInfo> page = tradeBizQueryService
					.searchTradeDetailQuery(queryConditions);
			model.addAttribute("queryConditions", queryConditions);
			if (page.isSuccess() && ListUtil.isNotEmpty(page.getPageList())) {
				for (TradeDetailVOInfo info : page.getPageList()) {
					Map<String, String> map = new HashMap<String, String>();
					String expireDate = "暂无";
					if (null != info.getTradeExpireDate()) {
						expireDate = DateUtil.dtSimpleFormat(info
								.getTradeExpireDate());
					}

					map.put("name", info.getOriginalRealName());
					map.put("profit", info.getBenefitAmount()
							.toStandardString());
					map.put("payDate", expireDate);
					map.put("tradeName", info.getTradeName());
					list.add(map);
				}
				int totalPage = 0;
				if (page.getTotalCount() % pageSize == 0) {
					totalPage = (int) (page.getTotalCount() / pageSize);
				} else {
					totalPage = (int) (page.getTotalCount() / pageSize + 1);
				}

				json.put("totalPage", totalPage);
				json.put("code", 1);
				json.put("message", "查询交易信息成功！");
				json.put("salesList", list);
				json.put("totalMoney", page.getTotalAmount().toStandardString());
				logger.info("手机app用户:{}查询交易信息成功：salesList={}",
						SessionLocalManager.getSessionLocal().getUserName(),
						list);
			} else {
				json.put("code", 1);
				json.put("message", "暂无交易信息！");
				json.put("totalpage", "0");
				json.put("totalMoney", page.getTotalAmount().toStandardString());
				logger.info("手机app用户:{}查询交易信息成功：salesList={}",
						SessionLocalManager.getSessionLocal().getUserName(),
						"暂无交易信息");
			}

		} else {
			json.put("code", -1);
			json.put("message", "未登录或登录已失效");
		}

		return json;
	}

	/**
	 * 收益配置
	 * 
	 * @param customerId
	 *            当前客户的userBaseId
	 * @param tblBaseId
	 * @param limit
	 * @param note
	 * */
	@ResponseBody
	@RequestMapping("appSetQuota.htm")
	public JSONObject setQuota(String customerId, String tblBaseId,
			double limit, String note, HttpServletResponse response)
			throws Exception {
		logger.info("手机app用户设置佣金额度，入参：[customerId={},tblBaseId={}],",
				customerId, tblBaseId);
		JSONObject json = new JSONObject();
		if (limit >= 0 && limit <= 100) {
			limit = CommonUtil.div(limit, 100);
			try {
				UserInfo reciever = userQueryService.queryByUserBaseId(
						customerId).getQueryUserInfo();
				Long distributionId = SessionLocalManager.getSessionLocal()
						.getUserId();
				Long receiveId = reciever.getUserId();
				if (StringUtil.isNotEmpty(tblBaseId)) {
					BorkerProfitSettingOrder profitSettingOrder = new BorkerProfitSettingOrder();
					profitSettingOrder.setTblBaseId(tblBaseId);
					profitSettingOrder.setDistributionId(distributionId);
					profitSettingOrder.setReceiveId(receiveId);
					profitSettingOrder.setDistributionQuota(limit);
					profitSettingOrder.setNote(note);
					YrdBaseResult baseResult = profitManager
							.brokerAddProfitSetting(profitSettingOrder);
					if (baseResult.isSuccess()) {
						json.put("code", 1);
						json.put("message", "设置佣金成功");
					} else {
						json.put("code", 0);
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

					YrdBaseResult baseResult = profitManager
							.brokerAddProfitSetting(profitSettingOrder);
					if (baseResult.isSuccess()) {
						json.put("code", 1);
						json.put("message", "设置佣金成功");
					} else {
						json.put("code", 0);
						json.put("message", "设置佣金失败");
					}
				}
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				json.put("code", 0);
				json.put("message", "设置佣金异常");
			}
		} else {
			json.put("code", 0);
			json.put("message", "额度介于0-100之间, 且必须大于0");
		}

		return json;
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
