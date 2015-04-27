package com.yjf.yrd.backstage.controller.trademanage;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.yjf.common.lang.util.StringUtil;
import com.yjf.common.lang.util.money.Money;
import com.yjf.yrd.base.BaseAutowiredController;
import com.yjf.yrd.dataobject.viewObject.AmounFlowVO;
import com.yjf.yrd.integration.web.server.YjfLoginWebServer;
import com.yjf.yrd.integration.web.server.impl.YjfEzmoneyCheckPaytkOrder;
import com.yjf.yrd.integration.web.server.impl.YjfEzmoneyPayPassUrlOrder;
import com.yjf.yrd.page.PageParam;
import com.yjf.yrd.service.query.trade.DivisionDetailQueryService;
import com.yjf.yrd.service.query.trade.TradeBizQueryService;
import com.yjf.yrd.session.SessionLocalManager;
import com.yjf.yrd.user.info.UserInfo;
import com.yjf.yrd.util.AESEncrypt;
import com.yjf.yrd.util.AppConstantsUtil;
import com.yjf.yrd.util.ApplicationConstant;
import com.yjf.yrd.util.DateUtil;
import com.yjf.yrd.web.util.PageUtil;
import com.yjf.yrd.web.util.WebUtil;
import com.yjf.yrd.ws.enums.DivisionPhaseEnum;
import com.yjf.yrd.ws.enums.RepayPlanStatusEnum;
import com.yjf.yrd.ws.enums.SysUserRoleEnum;
import com.yjf.yrd.ws.enums.TradeStatusEnum;
import com.yjf.yrd.ws.info.AmountFlowTradeVOInfo;
import com.yjf.yrd.ws.info.DivisionDetailInfo;
import com.yjf.yrd.ws.info.LoanDemandInfo;
import com.yjf.yrd.ws.info.LoanDemandTradeVOInfo;
import com.yjf.yrd.ws.info.RepayPlanInfo;
import com.yjf.yrd.ws.info.TradeDetailInfo;
import com.yjf.yrd.ws.info.TradeDetailVOInfo;
import com.yjf.yrd.ws.info.TradeInfo;
import com.yjf.yrd.ws.order.TradeDetailQueryOrder;
import com.yjf.yrd.ws.order.TradeProcessOrder;
import com.yjf.yrd.ws.order.TradeRepayOrder;
import com.yjf.yrd.ws.order.UpdateTradeExpireDateOrder;
import com.yjf.yrd.ws.result.YrdBaseResult;
import com.yjf.yrd.ws.service.TradeBizProcessService;
import com.yjf.yrd.ws.service.query.order.AmountFlowQueryOrder;
import com.yjf.yrd.ws.service.query.order.DivisionDetailQueryOrder;
import com.yjf.yrd.ws.service.query.order.QueryLoanTradeOrder;
import com.yjf.yrd.ws.service.query.order.QueryTradeDetailUserOrder;
import com.yjf.yrd.ws.service.query.order.RepayPlanQueryOrder;
import com.yjf.yrd.ws.service.query.result.QueryBaseBatchResult;
import com.yjf.yrd.ws.service.query.result.TradeDetailBatchResult;

/**
 * @Filename TradeController.java
 * @Description
 * @Version 1.0
 * @Author yhl
 * @Email yhailong@yiji.com
 * @History <li>Author: yhl</li> <li>Date: 2013-7-2</li> <li>Version: 1.0</li>
 * <li>Content: create</li>
 */
@Controller
@RequestMapping("backstage/trade")
public class TradeController extends BaseAutowiredController {
	
	@Autowired
	protected TradeBizProcessService tradeBizProcessService = null;
	@Autowired
	protected TradeBizQueryService tradeBizQueryService = null;
	
	@Autowired
	protected DivisionDetailQueryService divisionDetailQueryService;
	@Autowired
	private YjfLoginWebServer yjfLoginWebServer;
	
	@Override
	protected String[] getMoneyInputNameArray() {
		return new String[] { "minLoanAmount", "maxLoanAmount" };
	}
	
	/**
	 * 交易管理
	 * 
	 * @return
	 */
	@RequestMapping("mainTrade/{size}/{pageNo}")
	public String mainTradeGetByProperties(@PathVariable int size, @PathVariable int pageNo,
											QueryLoanTradeOrder queryTradeOrder, Model model,
											HttpSession session) {
		PageParam pageParam = new PageParam();
		pageParam.setPageNo(pageNo);
		pageParam.setPageSize(size);
		queryTradeOrder.setPageSize(size);
		//过滤下：状态参数(理空、双引号参数)
		List<String> status_param = queryTradeOrder.getStatus();
		queryTradeOrder.setStatus(new ArrayList<String>());
		for (String status : status_param) {
			if (StringUtil.isNotEmpty(status)) {
				queryTradeOrder.getStatus().add(status);
			}
		}
		queryTradeOrder.setPageNumber(pageNo);
		
		QueryBaseBatchResult<LoanDemandTradeVOInfo> page = null;
		try {
			page = tradeBizQueryService.searchLoanTradeCommonQuery(queryTradeOrder);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("交易查询失败！", e);
		}
		model.addAttribute("totalLoanAmount", page.getTotalLoanAmount());
		model.addAttribute("totalLoanedAmount", page.getTotalLoanedAmount());
		model.addAttribute("queryTradeOrder", queryTradeOrder);
		model.addAttribute("page", PageUtil.getCovertPage(page));
		return "backstage/trade/trade_main_list.vm";
	}
	
	/**
	 * 交易管理
	 * 
	 * @return
	 */
	@RequestMapping("payTrade/{size}/{pageNo}")
	public String payTradeGetByProperties(Model model, @PathVariable int size,
											@PathVariable int pageNo, Integer status,
											String startDate, String endDate, String tradeName,
											HttpSession session) {
		RepayPlanQueryOrder repayQuery = new RepayPlanQueryOrder();
		List<RepayPlanStatusEnum> statusList = new ArrayList<RepayPlanStatusEnum>();
		if (status == null) {
			status = 0;
			statusList.add(RepayPlanStatusEnum.NOTPAY);
		}
		
		if (status == 0) {
			statusList.add(RepayPlanStatusEnum.NOTPAY);
		} else if (status == 1) {
			statusList.add(RepayPlanStatusEnum.PAYID);
		} else if (status == 2) {
			statusList.add(RepayPlanStatusEnum.PAYISD);
		} else if (status == 3) {
			statusList.add(RepayPlanStatusEnum.PAYGISD);
		}
		Date startTime = null;
		Date endTime = null;
		if (startDate != null && endDate != null && !startDate.equals("") && !endDate.equals("")) {
			startTime = DateUtil.parse(startDate);
			endTime = DateUtil.parse(endDate + " 23:59:59", DateUtil.parse(endDate));
		}
		
		repayQuery.setPageNumber(pageNo);
		repayQuery.setPageSize(size);
		repayQuery.setTradeName(tradeName);
		if (status == 0 && startDate != null && endDate != null && !startDate.equals("")
			&& !endDate.equals("")) {
			repayQuery.setStartRepayDate(startTime);
			repayQuery.setEndRepayDate(endTime);
		} else if (status == 1 && startDate != null && endDate != null && !startDate.equals("")
					&& !endDate.equals("")) {
			repayQuery.setActualStartRepayDate(startTime);
			repayQuery.setActualEndRepayDate(endTime);
		}
		
		repayQuery.setStatusList(statusList);
		repayQuery.setOrderBy("repay_date");
		QueryBaseBatchResult<RepayPlanInfo> batchResult = repayPlanQueryService
			.queryTradeDetailUser(repayQuery);
		
		//设置是否可还款
		for (RepayPlanInfo repayPlanInfo : batchResult.getPageList()) {
			TradeInfo trade = tradeBizQueryService.getByTradeId(repayPlanInfo.getTradeId());
			repayPlanInfo.setDemandId(trade.getDemandId());
			long day = (DateUtil.getDateBetween(DateUtil.now(), repayPlanInfo.getRepayDate())) / 86400000;
			// if (day <= 7) {   //提前一周，以及过后都可以还
			if (trade.getTradeStatus() == TradeStatusEnum.REPAYING
				|| trade.getTradeStatus() == TradeStatusEnum.DOREPAY
				|| trade.getTradeStatus() == TradeStatusEnum.REPAYING_FAILD) {
				if (repayPlanQueryService.isPrePeriodPayOff(repayPlanInfo.getTradeId(),
					repayPlanInfo.getPeriodNo())) {
					repayPlanInfo.setNote("true");
				} else {
					repayPlanInfo.setNote("false");
				}
			} else {
				repayPlanInfo.setNote("false");
			}
			// } else {
			//     repayPlanInfo.setNote("false");
			// }
		}
		UserInfo platformUser = getYrdPlatformRepayUser();
		YjfEzmoneyPayPassUrlOrder ezmoneyPayPassUrlOrder = new YjfEzmoneyPayPassUrlOrder();
		ezmoneyPayPassUrlOrder.setUserId(platformUser.getAccountId()); //平台还款账号
		YrdBaseResult baseResult = yjfLoginWebServer.gotoYjfValidatePayPasswordUrl(
			ezmoneyPayPassUrlOrder, this.getOpenApiContext());
		model.addAttribute("yjfPaypassUrl", baseResult.getUrl());
		model.addAttribute("repayQuery", repayQuery);
		model.addAttribute("statusList", statusList);
		model.addAttribute("status", status);
		model.addAttribute("page", PageUtil.getCovertPage(batchResult));
		model.addAttribute("repayMoney", batchResult.getTotalMoney());
		model.addAttribute("startDate", startTime);
		model.addAttribute("endDate", endTime);
		return "backstage/trade/pay_trade_list.vm";
	}
	
	/**
	 * 获取replanId
	 */
	@ResponseBody
	@RequestMapping("getRepayPlanId/{repayPlanId}")
	public JSONObject getRepayPlanId(Model model, @PathVariable long repayPlanId,
										HttpSession session) {
		//        this.initAccountInfo(model);
		JSONObject json = new JSONObject();
		RepayPlanInfo repayPlan = new RepayPlanInfo();
		repayPlan = repayPlanQueryService.queryRepayPlan(repayPlanId);
		String token = UUID.randomUUID().toString();
		session.setAttribute("token", token);
		json.toJSON(repayPlan);
		json.put("message", repayPlan.getAmount().getAmount().toString());
		json.put("amoutCN", repayPlan.getAmount().toCNString());
		json.put("tradeId", repayPlan.getTradeId());
		json.put("repayPlanId", repayPlan.getRepayPlanId());
		json.put("token", token);
		return json;
	}
	
	/**
	 * 还款
	 */
	@ResponseBody
	@RequestMapping("repayMoney")
	public Object repayMoney(long tradeId, long repayPlanId, String smsCode, String mobile,
								String business, String token, String paytk, HttpSession session) {
		logger.info("手动还款，入参：[repayPlanId={}],", repayPlanId);
		String getToken = (String) session.getAttribute("token");
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			if (token.equals(getToken) && repayPlanId > 0) {
				YjfEzmoneyCheckPaytkOrder ezmoneyPayPassUrlOrder = new YjfEzmoneyCheckPaytkOrder();
				ezmoneyPayPassUrlOrder.setUserId(SessionLocalManager.getSessionLocal()
					.getAccountId());
				ezmoneyPayPassUrlOrder.setPaytk(paytk);
				YrdBaseResult paytkResult = yjfLoginWebServer.gotoYjfCheckPayTk(
					ezmoneyPayPassUrlOrder, this.getOpenApiContext());
				if (!paytkResult.isSuccess()) {
					map.put("status", false);
					map.put("message", "还款时支付令牌错误");
					return map;
				}
				session.removeAttribute("token");
				TradeRepayOrder processOrder = new TradeRepayOrder();
				processOrder.setRepayPlanId(repayPlanId);
				processOrder.setBizNo(tradeId);
				processOrder.setProcessorId(SessionLocalManager.getSessionLocal().getUserId());
				processOrder.setRepayUserId(processOrder.getProcessorId());
				YrdBaseResult result = tradeBizProcessService.repay(processOrder);
				if (result.isSuccess()) {
					map.put("code", 1);
					map.put("message", "还款成功");
				} else {
					map.put("code", 0);
					map.put("message", "还款失败[" + result.getMessage() + "]");
				}
			} else {
				map.put("code", 0);
				map.put("message", "还款失败重复提交");
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			map.put("code", 0);
			map.put("message", "还款失败");
		}
		return map;
	}
	
	/**
	 * 还款
	 */
	@ResponseBody
	@RequestMapping("palatFormRepayMoney")
	public Object palatFormRepayMoney(long tradeId, long repayPlanId, String smsCode,
										String mobile, String business, String token, String paytk,
										HttpSession session) {
		logger.info("手动还款，入参：[repayPlanId={}],", repayPlanId);
		String getToken = (String) session.getAttribute("token");
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			if (token.equals(getToken) && repayPlanId > 0) {
				YjfEzmoneyCheckPaytkOrder ezmoneyPayPassUrlOrder = new YjfEzmoneyCheckPaytkOrder();
				UserInfo repayUser = getYrdPlatformRepayUser();
				ezmoneyPayPassUrlOrder.setUserId(repayUser.getAccountId()); //平台还款账号
				ezmoneyPayPassUrlOrder.setPaytk(paytk);
				YrdBaseResult paytkResult = yjfLoginWebServer.gotoYjfCheckPayTk(
					ezmoneyPayPassUrlOrder, this.getOpenApiContext());
				if (!paytkResult.isSuccess()) {
					map.put("status", false);
					map.put("message", "还款时支付令牌错误");
					return map;
				}
				session.removeAttribute("token");
				TradeRepayOrder processOrder = new TradeRepayOrder();
				processOrder.setRepayPlanId(repayPlanId);
				processOrder.setBizNo(tradeId);
				processOrder.setProcessorId(repayUser.getUserId());
				processOrder.setRepayUserId(processOrder.getProcessorId());
				YrdBaseResult result = tradeBizProcessService.repay(processOrder);
				if (result.isSuccess()) {
					map.put("code", 1);
					map.put("message", "还款成功");
				} else {
					map.put("code", 0);
					map.put("message", "还款失败[" + result.getMessage() + "]");
				}
			} else {
				map.put("code", 0);
				map.put("message", "还款失败重复提交");
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			map.put("code", 0);
			map.put("message", "还款失败");
		}
		return map;
	}
	
	@ResponseBody
	@RequestMapping("validationPayPassword")
	public Object validationPayPassword(String payPassword, HttpSession session) throws Exception {
		
		JSONObject jsonobj = new JSONObject();
		String key = (String) session.getAttribute(session.getId());
		AESEncrypt aesEncrypt = new AESEncrypt();
		aesEncrypt.setIvParameter(key);
		aesEncrypt.setsKey(key);
		try {
			payPassword = aesEncrypt.decrypt(payPassword);
		} catch (Exception e) {
			logger.error("解密支付密码异常:", e);
		}
		/*	returnEnum = userBaseInfoManager.validationPayPassword(SessionLocalManager
				.getSessionLocal().getUserBaseId(), payPassword);
			if (returnEnum == UserBaseReturnEnum.PASSWORD_SUCCESS) {
				jsonobj.put("code", 1);
				jsonobj.put("message", "支付密码正确");
			}
			if (returnEnum == UserBaseReturnEnum.PASSWORD_ERROR) {
				jsonobj.put("code", 0);
				jsonobj.put("message", "支付密码错误");
			}*/
		return jsonobj;
	}
	
	/**
	 * 投资交易管理
	 * 
	 * @return
	 */
	@RequestMapping("investTrade/{tradeId}")
	public String getByProperties(@PathVariable long tradeId, PageParam pageParam,
									String payRealName, String payUserName, Money startAmount,
									Money endAmount, String startDate, String endDate, Model model) {
		LoanDemandInfo loanDemand = loanDemandQueryService.loadLoanDemandInfoByTradeId(tradeId);
		QueryTradeDetailUserOrder queryTradeDetailUserOrder = new QueryTradeDetailUserOrder();
		queryTradeDetailUserOrder.setTradeId(tradeId);
		if (endAmount != null && endAmount.greaterThan(Money.zero())) {
			
			queryTradeDetailUserOrder.setEndAmount(endAmount);
		}
		if (startAmount != null) {
			queryTradeDetailUserOrder.setStartAmount(startAmount);
		}
		if (StringUtil.isNotBlank(startDate)) {
			queryTradeDetailUserOrder.setStartDate(DateUtil.getStartTimeOfTheDate(DateUtil
				.parse(startDate)));
		}
		if (StringUtil.isNotBlank(endDate)) {
			queryTradeDetailUserOrder.setEndDate(DateUtil.getEndTimeOfTheDate(DateUtil
				.parse(endDate)));
		}
		queryTradeDetailUserOrder.setPageNumber(pageParam.getPageNo());
		queryTradeDetailUserOrder.setPageSize(pageParam.getPageSize());
		queryTradeDetailUserOrder.setRoleEnum(SysUserRoleEnum.INVESTOR);
		queryTradeDetailUserOrder.setTransferPhase(DivisionPhaseEnum.ORIGINAL_PHASE);
		queryTradeDetailUserOrder.setRealName(payRealName);
		queryTradeDetailUserOrder.setUserName(payUserName);
		QueryBaseBatchResult<TradeDetailInfo> baseBatchResult = tradeBizQueryService
			.queryTradeDetailUserPage(queryTradeDetailUserOrder);
		model.addAttribute("page", PageUtil.getCovertPage(baseBatchResult));
		
		model.addAttribute("tradeId", tradeId);
		model.addAttribute("loanDemand", loanDemand);
		model.addAttribute("trade", loanDemand.getTradeInfo());
		return "backstage/trade/trade_invest_list.vm";
	}
	
	/**
	 * 根据ID查看
	 * 
	 * @param id
	 * @param model
	 * @return
	 */
	@RequestMapping("tradeFlow/{tradeId}")
	public String tradeFlow(@PathVariable long tradeId, String receiveRealName,
							String receiveUserName, String payRealName, String payUserName,
							Long startAmount, Long endAmount, String startDate, String endDate,
							PageParam pageParam, Model model) throws Exception {
		TradeInfo trade = tradeBizQueryService.getByTradeId(tradeId);
		model.addAttribute("trade", trade);
		
		AmountFlowQueryOrder detailQueryOrder = new AmountFlowQueryOrder();
		detailQueryOrder.setTradeId(tradeId);
		int pageSize = pageParam.getPageSize();
		int pageNo = pageParam.getPageNo();
		detailQueryOrder.setPageSize(pageSize);
		detailQueryOrder.setPageNumber(pageNo);
		
		if (StringUtil.isNotBlank(receiveRealName)) {
			detailQueryOrder.setInUserRealName(receiveRealName);
		}
		if (StringUtil.isNotBlank(receiveUserName)) {
			detailQueryOrder.setInUserName(receiveUserName);
		}
		if (StringUtil.isNotBlank(payRealName)) {
			detailQueryOrder.setOutUserRealName(payRealName);
		}
		if (StringUtil.isNotBlank(payUserName)) {
			detailQueryOrder.setOutUserName(payUserName);
		}
		if (StringUtil.isNotBlank(startDate)) {
			detailQueryOrder.setStartDate(DateUtil.getStartTimeOfTheDate(DateUtil
				.strToDtSimpleFormat(startDate)));
		}
		if (StringUtil.isNotBlank(endDate)) {
			detailQueryOrder.setEndDate(DateUtil.getEndTimeOfTheDate(DateUtil
				.strToDtSimpleFormat(endDate)));
		}
		QueryBaseBatchResult<AmountFlowTradeVOInfo> batchResult = divisionDetailQueryService
			.getAmountFlowTrade(detailQueryOrder);
		model.addAttribute("page", PageUtil.getCovertPage(batchResult));
		return "backstage/trade/trade_flow_list.vm";
	}
	
	/**
	 * 根据ID查看分润转账流水
	 * 
	 * @param id
	 * @param model
	 * @return
	 */
	@RequestMapping("tradeDivisionFlow/{tradeId}")
	public String tradeDivisionFlow(@PathVariable long tradeId, String receiveRealName,
									String receiveUserName, Long startAmount, Long endAmount,
									String startDate, String endDate, PageParam pageParam,
									Model model) throws Exception {
		TradeInfo trade = tradeBizQueryService.getByTradeId(tradeId);
		model.addAttribute("trade", trade);
		DivisionDetailQueryOrder detailQueryOrder = new DivisionDetailQueryOrder();
		detailQueryOrder.setTradeId(tradeId);
		if (StringUtil.isNotBlank(receiveRealName)) {
			detailQueryOrder.setRealName(receiveRealName);
		}
		if (StringUtil.isNotBlank(receiveUserName)) {
			detailQueryOrder.setUserName(receiveUserName);
		}
		if (StringUtil.isNotBlank(startDate)) {
			detailQueryOrder
				.setStartDate(DateUtil.getStartTimeOfTheDate(DateUtil.parse(startDate)));
		}
		if (StringUtil.isNotBlank(endDate)) {
			detailQueryOrder.setEndDate(DateUtil.getEndTimeOfTheDate(DateUtil.parse(endDate)));
		}
		detailQueryOrder.setPageNumber(pageParam.getPageNo());
		detailQueryOrder.setPageSize(pageParam.getPageSize());
		QueryBaseBatchResult<DivisionDetailInfo> batchResult = divisionDetailQueryService
			.getDivisionTradeFlow(detailQueryOrder);
		model.addAttribute("page", PageUtil.getCovertPage(batchResult));
		return "backstage/trade/trade_flow_division_list.vm";
	}
	
	/**
	 * 根据ID查看
	 * 
	 * @param id
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("devisionDetails/{tradeId}")
	public String devisionDetails(@PathVariable long tradeId, String realName, String startDate,
									String endDate, Long roleId, PageParam pageParam, Model model)
																									throws Exception {
		LoanDemandInfo info = loanDemandQueryService.loadLoanDemandInfoByTradeId(tradeId);
		TradeInfo trade = info.getTradeInfo();
		model.addAttribute("trade", trade);
		model.addAttribute("loanDemand", info);
		model.addAttribute("loaner", userQueryService.queryByUserId(info.getLoanerId())
			.getQueryUserInfo());
		TradeDetailQueryOrder detailQueryOrder = new TradeDetailQueryOrder();
		
		if (roleId != null) {
			if (roleId > 0) {
				detailQueryOrder.setRoleId(roleId);
			}
		}
		detailQueryOrder.setTradeId(tradeId);
		detailQueryOrder.setUserName(realName);
		detailQueryOrder.setStartDate(startDate);
		detailQueryOrder.setEndDate(endDate);
		detailQueryOrder.setIsDivision("YES");
		detailQueryOrder.setPageNumber(pageParam.getPageNo());
		detailQueryOrder.setPageSize(pageParam.getPageSize());
		TradeDetailBatchResult<TradeDetailVOInfo> batchResult = tradeBizQueryService
			.searchTradeDetailQuery(detailQueryOrder);
		model.addAttribute("page", PageUtil.getCovertPage(batchResult));
		return "backstage/trade/trade_division_list.vm";
	}
	
	/**
	 * 查看flow详情
	 * 
	 * @param flowId
	 * @param tradeflow
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("tradeFlowInfo/{tradeId}")
	public String tradeFlowInfo(@PathVariable long tradeId, String payDate, String tradeCreateDate,
								String tradeFinishDate, String tradeType, Model model,
								HttpServletRequest request) throws Exception {
		AmounFlowVO tradeflow = new AmounFlowVO();
		WebUtil.setPoPropertyByRequest(tradeflow, request);
		boolean status = Boolean.parseBoolean(request.getParameter(("status")));
		tradeflow.setStatus(status);
		TradeInfo trade = tradeBizQueryService.getByTradeId(tradeId);
		if (StringUtil.isNotEmpty(payDate)) {
			tradeflow.setDate(DateUtil.parse(payDate));
		}
		if (StringUtil.isNotEmpty(tradeCreateDate)) {
			trade.setTradeCreateDate(DateUtil.parse(tradeCreateDate));
		}
		
		if (StringUtil.isNotEmpty(tradeFinishDate)) {
			trade.setTradeFinishDate(DateUtil.parse(tradeFinishDate));
		}
		model.addAttribute("tradeType", tradeType);
		model.addAttribute("trade", trade);
		model.addAttribute("item", tradeflow);
		return "backstage/trade/trade_flow_info.vm";
	}
	
	@ResponseBody
	@RequestMapping("offLineTrade")
	public Object offLineTrade(long tradeId, Model model) throws Exception {
		JSONObject jsonobj = new JSONObject();
		try {
			if (tradeId > 0) {
				TradeProcessOrder processOrder = new TradeProcessOrder();
				processOrder.setBizNo(tradeId);
				YrdBaseResult baseResult = tradeBizProcessService.offLineTrade(processOrder);
				if (baseResult.isSuccess()) {
					jsonobj.put("code", 1);
					jsonobj.put("message", "交易下线成功！");
				} else {
					jsonobj.put("code", 0);
					
					jsonobj.put("message", StringUtil.isEmpty(baseResult.getMessage()) ? "此状态不允许下线"
						: baseResult.getMessage() + "!");
				}
			} else {
				jsonobj.put("code", 0);
				jsonobj.put("message", "交易下线失败！");
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			jsonobj.put("code", 0);
			jsonobj.put("message", "交易下线失败！");
		}
		return jsonobj;
	}
	
	@ResponseBody
	@RequestMapping("completeTrade")
	public Object completeTrade(long tradeId, Model model) throws Exception {
		JSONObject jsonobj = new JSONObject();
		try {
			if (tradeId > 0) {
				TradeProcessOrder processOrder = new TradeProcessOrder();
				processOrder.setBizNo(tradeId);
				YrdBaseResult baseResult = tradeBizProcessService.completeTrade(processOrder);
				if (baseResult.isSuccess()) {
					jsonobj.put("code", 1);
					jsonobj.put("message", "手工交易结束成功！");
				} else {
					jsonobj.put("code", 0);
					
					jsonobj.put("message", baseResult.getMessage());
				}
			} else {
				jsonobj.put("code", 0);
				jsonobj.put("message", "手工交易结束失败！");
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			jsonobj.put("code", 0);
			jsonobj.put("message", "手工交易结束失败！");
		}
		return jsonobj;
	}
	
	/**
	 * 还款查询
	 */
	@RequestMapping("repayQuery/{size}/{pageNo}")
	public String repayQuery(@PathVariable int size, @PathVariable int pageNo, Model model,
								HttpSession session, HttpServletRequest request) {
		List<RepayPlanStatusEnum> statusList = new ArrayList<RepayPlanStatusEnum>();
		model.addAllAttributes(WebUtil.getRequestMap(request));
		RepayPlanQueryOrder repayQueryOrder = new RepayPlanQueryOrder();
		WebUtil.setPoPropertyByRequest(repayQueryOrder, request);
		String dateFlag = request.getParameter("dateFlag");
		String startDate = DateUtil.dtSimpleFormat(repayQueryOrder.getStartRepayDate());
		String endDate = DateUtil.dtSimpleFormat(repayQueryOrder.getEndRepayDate());
		String status = "";
		if (StringUtil.isNotEmpty(dateFlag) && !"all".equals(dateFlag)) {
			startDate = DateUtil.dtSimpleFormat(new Date());
			if ("one".equals(dateFlag)) {
				endDate = DateUtil.dtSimpleFormat(DateUtil.getDateByMonth(new Date(), 1));
			} else if ("three".equals(dateFlag)) {
				endDate = DateUtil.dtSimpleFormat(DateUtil.getDateByMonth(new Date(), 3));
			} else if ("year".equals(dateFlag)) {
				endDate = DateUtil.dtSimpleFormat(DateUtil.getDateByMonth(new Date(), 12));
			}
		}
		if (StringUtil.isNotEmpty(startDate)) {
			repayQueryOrder.setStartRepayDate(DateUtil.parse(startDate + " 00:00:00"));
		}
		if (StringUtil.isNotEmpty(endDate)) {
			repayQueryOrder.setEndRepayDate(DateUtil.parse(endDate + " 23:59:59"));
		}
		if (StringUtil.isNotEmpty(request.getParameter("status"))) {
			if (("NOTPAY").equals(request.getParameter("status"))) {
				statusList.add(RepayPlanStatusEnum.NOTPAY);
				status = "NOTPAY";
			} else {
				statusList.add(RepayPlanStatusEnum.PAYID);
				status = "PAYID";
			}
			repayQueryOrder.setStatusList(statusList);
			model.addAttribute("status", repayQueryOrder.getStatusList());
		}
		if (StringUtil.isNotEmpty(request.getParameter("flag"))) {
			model.addAttribute("flag", request.getParameter("flag"));
		} else {
			model.addAttribute("flag", request.getParameter("dateFlag"));
		}
		repayQueryOrder.setPageNumber(pageNo);
		repayQueryOrder.setPageSize(size);
		QueryBaseBatchResult<RepayPlanInfo> batchResult = repayPlanQueryService
			.queryTradeDetailUser(repayQueryOrder);
		Money originalMoney = new Money();
		Money benifitMoney = new Money();
		originalMoney = repayPlanQueryService.originalAmount(repayQueryOrder);
		benifitMoney = batchResult.getTotalMoney().subtract(originalMoney);
		model.addAttribute("repayQuery", repayQueryOrder);
		model.addAttribute("page", PageUtil.getCovertPage(batchResult));
		model.addAttribute("repayMoney", batchResult.getTotalMoney());
		model.addAttribute("originalMoney", originalMoney);
		model.addAttribute("benifitMoney", benifitMoney);
		model.addAttribute("status", status);
		return "/backstage/repayManage/repayQuery.vm";
	}
	
	@ResponseBody
	@RequestMapping("fixRepayTime")
	public Object fixRepayTime(long tradeId, String expireDate, Model model) throws Exception {
		JSONObject jsonobj = new JSONObject();
		try {
			if (tradeId > 0 && expireDate != null) {
				UpdateTradeExpireDateOrder expireDateOrder = new UpdateTradeExpireDateOrder();
				expireDateOrder.setBizNo(tradeId);
				expireDateOrder.setExpireDate(DateUtil.parse(expireDate));
				YrdBaseResult baseResult = tradeBizProcessService
					.updateTradeExpireDate(expireDateOrder);
				if (baseResult.isSuccess()) {
					jsonobj.put("code", 1);
					jsonobj.put("message", "修订成功！");
				} else {
					jsonobj.put("code", 0);
					jsonobj.put("message", "修订失败！");
				}
			} else {
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			jsonobj.put("code", 0);
			jsonobj.put("message", "修订失败！");
		}
		return jsonobj;
	}
	
	/**
	 * 获取平台还款用户
	 * @return
	 */
	public UserInfo getYrdPlatformRepayUser() {
		
		UserInfo brokerUser = null;
		try {
			brokerUser = userQueryService.queryByUserName(
				AppConstantsUtil.getPlatformRepayUserName(), ApplicationConstant.ROLE_ID_PLATFORM)
				.getQueryUserInfo();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return brokerUser;
		
	}
	
}
