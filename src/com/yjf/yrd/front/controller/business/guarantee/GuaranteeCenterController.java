package com.yjf.yrd.front.controller.business.guarantee;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.yjf.common.lang.util.StringUtil;
import com.yjf.common.lang.util.money.Money;
import com.yjf.yrd.base.CommonBindingInitializer;
import com.yjf.yrd.common.result.SmsCodeResult;
import com.yjf.yrd.dataobject.OperatorInfoDO;
import com.yjf.yrd.dataobject.TradeStatusDO;
import com.yjf.yrd.front.controller.trade.base.LoanTradeDetailBaseController;
import com.yjf.yrd.integration.web.server.YjfLoginWebServer;
import com.yjf.yrd.integration.web.server.impl.YjfEzmoneyCheckPaytkOrder;
import com.yjf.yrd.integration.web.server.impl.YjfEzmoneyPayPassUrlOrder;
import com.yjf.yrd.page.PageParam;
import com.yjf.yrd.service.certificate.CertificateService;
import com.yjf.yrd.service.certificate.order.CreatTradeContractCertificateOrder;
import com.yjf.yrd.service.certificate.result.CreatCertificateResult;
import com.yjf.yrd.session.SessionLocal;
import com.yjf.yrd.session.SessionLocalManager;
import com.yjf.yrd.user.info.UserInfo;
import com.yjf.yrd.util.AppConstantsUtil;
import com.yjf.yrd.util.DateUtil;
import com.yjf.yrd.web.util.AttachmentModuleType;
import com.yjf.yrd.web.util.PageUtil;
import com.yjf.yrd.web.util.WebUtil;
import com.yjf.yrd.web.util.YrdEnumUtil;
import com.yjf.yrd.ws.enums.RepayPlanStatusEnum;
import com.yjf.yrd.ws.enums.SmsBizType;
import com.yjf.yrd.ws.enums.SysUserRoleEnum;
import com.yjf.yrd.ws.enums.TradeStatusEnum;
import com.yjf.yrd.ws.enums.YrdAuthTypeEnum;
import com.yjf.yrd.ws.info.CommonAttachmentInfo;
import com.yjf.yrd.ws.info.LoanDemandInfo;
import com.yjf.yrd.ws.info.LoanDemandTradeVOInfo;
import com.yjf.yrd.ws.info.RepayPlanInfo;
import com.yjf.yrd.ws.info.TradeInfo;
import com.yjf.yrd.ws.order.CommonAttachmentQueryOrder;
import com.yjf.yrd.ws.order.LoanAuthOrder;
import com.yjf.yrd.ws.order.TradeProcessOrder;
import com.yjf.yrd.ws.order.TradeRepayOrder;
import com.yjf.yrd.ws.order.UpdateAuthPasswordOrder;
import com.yjf.yrd.ws.result.YrdBaseResult;
import com.yjf.yrd.ws.service.CommonAttachmentService;
import com.yjf.yrd.ws.service.query.order.QueryLoanTradeOrder;
import com.yjf.yrd.ws.service.query.order.RepayPlanQueryOrder;
import com.yjf.yrd.ws.service.query.result.QueryBaseBatchResult;
import com.yjf.yrd.ws.statistics.TradeAmountStatisticsInfo;
import com.yjf.yrd.ws.statistics.result.TradeStatisticsResult;

/**
 * @author CHARLEY
 */
@Controller
@RequestMapping("guaranteeCenter")
public class GuaranteeCenterController extends LoanTradeDetailBaseController {
	/**
	 * 返回页面路径
	 */
	String GUARANTEE_MANAGE_PATH = "/front/business/guarantee/";
	
	private long guaranteeUserId = 0;
	@Autowired
	YjfLoginWebServer yjfLoginWebServer;
	@Autowired
	CommonAttachmentService commonAttachmentService;
	
	@Autowired
	CertificateService certificateService;
	
	/**
	 * 工具类处理金额 /时间
	 */
	@Override
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		dateFormat.setLenient(false);
		binder.registerCustomEditor(Money.class, "minAmountIn", new CommonBindingInitializer());
		binder.registerCustomEditor(Money.class, "maxAmountIn", new CommonBindingInitializer());
		binder
			.registerCustomEditor(Date.class, "startTime", new CustomDateEditor(dateFormat, true));
		binder.registerCustomEditor(Date.class, "endTime", new CustomDateEditor(dateFormat, true));
	}
	
	/**
	 * 查询借款管理
	 */
	@RequestMapping("guaranteeManager")
	public String guaranteeManager(QueryLoanTradeOrder queryConditions, PageParam pageParam,
									Model model, HttpSession session) {
		logger.info("担保函管理,入参：{}", queryConditions);
		session.setAttribute("current", 5);
		SessionLocal sessionLocal = SessionLocalManager.getSessionLocal();
		if (sessionLocal != null && sessionLocal.getAccountId() != null) {
			this.initAccountInfo(model);
		}
		List<TradeStatusDO> guaranteeStatus = buildTradeStatus();
		try {
			queryConditions.setHasBenefitAmount("YES");// 要担保费
			queryConditions.setGuaranteeId(getGuaranteeUserId());
			queryConditions.setUserId(queryConditions.getGuaranteeId());
			queryConditions.setRoleId(SysUserRoleEnum.GUARANTEE.getValue());
			queryConditions.setPageNumber(pageParam.getPageNo());
			queryConditions.setPageSize(pageParam.getPageSize());
			QueryBaseBatchResult<LoanDemandTradeVOInfo> page = tradeBizQueryService
				.searchLoanTradeCommonQuery(queryConditions);
			model.addAttribute("page", PageUtil.getCovertPage(page));
			Money totalAmount = countTotalAmount(page.getPageList());
			model.addAttribute("totalAmount", totalAmount);
		} catch (Exception e) {
			logger.info("担保函管理,查询异常", e.getMessage());
		}
		model.addAttribute("guaranteeStatus", guaranteeStatus);
		model.addAttribute("queryConditions", queryConditions);
		String userBaseId = SessionLocalManager.getSessionLocal().getUserBaseId();
		Map<String, Object> conditions = new HashMap<String, Object>();
		conditions.put("userBaseId", userBaseId);
		conditions.put("limitStart", 0);
		conditions.put("pageSize", 9999);
		List<OperatorInfoDO> operatorInfos = operatorInfoService
			.queryOperatorsByProperties(conditions);
		if (operatorInfos != null && operatorInfos.size() > 0) {
			if (2 == operatorInfos.get(0).getOperatorType()
				|| 3 == operatorInfos.get(0).getOperatorType()) {
				model.addAttribute("auditOperator", "yes");
			} else {
				model.addAttribute("auditOperator", "no");
			}
		} else {
			model.addAttribute("auditOperator", "no");
		}
		logger.info("担保函管理,查询结束");
		return GUARANTEE_MANAGE_PATH + "guarantee_entries.vm";
	}
	
	/**
	 * 还款查询
	 * 
	 * @throws ParseException
	 */
	@RequestMapping("repayPlanGuranetee/{size}/{page}")
	public String repayPlanRecord(Model model, @PathVariable int size, String scope,
									@PathVariable int page, Integer status, String startDate,
									String userName, String tradeName, String endDate,
									HttpSession session) throws ParseException {
		this.initAccountInfo(model);
		RepayPlanQueryOrder repayQuery = new RepayPlanQueryOrder();
		List<RepayPlanStatusEnum> statusList = new ArrayList<RepayPlanStatusEnum>();
		String viewStatus = RepayPlanStatusEnum.NOTPAY.code();
		if (status == null) {
			status = 0;
		}
		if (scope == null) {
			scope = "";
		}
		if (status == 0) {
			if (scope.equals("1")) {
				startDate = DateUtil.formatDay(new Date());
				endDate = DateUtil.formatDay(DateUtil.getDateByMonth(new Date(), 1));
			} else if (scope.equals("2")) {
				startDate = DateUtil.formatDay(new Date());
				endDate = DateUtil.formatDay(DateUtil.getDateByMonth(new Date(), 3));
			} else if (scope.equals("3")) {
				startDate = DateUtil.formatDay(new Date());
				endDate = DateUtil.formatDay(DateUtil.getDateByMonth(new Date(), 12));
			}
			statusList.add(RepayPlanStatusEnum.NOTPAY);
			statusList.add(RepayPlanStatusEnum.PAYISD);
			statusList.add(RepayPlanStatusEnum.REPAY_FAIL);
		} else if (status == 1) {
			if (scope.equals("1")) {
				endDate = DateUtil.formatDay(new Date());
				startDate = DateUtil.formatDay(DateUtil.getDateByMonth(new Date(), -1));
			} else if (scope.equals("2")) {
				endDate = DateUtil.formatDay(new Date());
				startDate = DateUtil.formatDay(DateUtil.getDateByMonth(new Date(), -3));
			} else if (scope.equals("3")) {
				endDate = DateUtil.formatDay(new Date());
				startDate = DateUtil.formatDay(DateUtil.getDateByMonth(new Date(), -12));
			}
			statusList.add(RepayPlanStatusEnum.PAYID);
			statusList.add(RepayPlanStatusEnum.PAYGISD);
			viewStatus = RepayPlanStatusEnum.PAYID.code();
		}
		Date startTime = null;
		Date endTime = null;
		if (startDate != null && endDate != null && !startDate.equals("") && !endDate.equals("")) {
			startTime = DateUtil.parse(startDate);
			endTime = DateUtil.parse(endDate + " 23:59:59", DateUtil.parse(endDate));
		}
		if (userName != null) {
			if (status == 0) {
				repayQuery.setRepayUserRealName(userName);
			} else {
				repayQuery.setActualRepayUserRealName(userName);
			}
		}
		if (tradeName != null) {
			repayQuery.setTradeName(tradeName);
		}
		if (status == 0 && startDate != null && endDate != null && !startDate.equals("")
			&& !endDate.equals("")) {
			repayQuery.setStartRepayDate(startTime);
			repayQuery.setEndRepayDate(endTime);
		} else if (status == 1 && startDate != null && endDate != null && !startDate.equals("")
					&& !endDate.equals("")) {
			repayQuery.setActualStartRepayDate(startTime);
			repayQuery.setActualEndRepayDate(endTime);
		}
		repayQuery.setPageNumber(page);
		repayQuery.setPageSize(size);
		repayQuery.setStatusList(statusList);
		repayQuery.setRepayUserId(SessionLocalManager.getSessionLocal().getUserId());
		
		QueryBaseBatchResult<RepayPlanInfo> batchResult = repayPlanQueryService
			.queryGuaranteeUser(repayQuery);
		Money originalMoney = new Money();
		Money benifitMoney = new Money();
		Map<Long, Boolean> tradeIdMap = new HashMap<Long, Boolean>();
		for (RepayPlanInfo repayPlanInfo : batchResult.getPageList()) {
			TradeInfo tradeInfo = tradeBizQueryService.getByTradeId(repayPlanInfo.getTradeId());
			if (tradeInfo != null) {
				repayPlanInfo.setBankingBizTypeEnum(tradeInfo.getBankingBizTypeEnum());
			}
			/*if (!tradeIdMap.containsKey(repayPlanInfo.getTradeId())) {
				if (page > 1 && (repayPlanInfo.getPeriodNo() - 1) > 0) {
					RepayPlanQueryOrder repayTempOrder = new RepayPlanQueryOrder();
					repayTempOrder.setTradeId(repayPlanInfo.getTradeId());
					repayTempOrder.setPeriodNo(repayPlanInfo.getPeriodNo() - 1);
					repayTempOrder.setStatusList(statusList);
					QueryBaseBatchResult<RepayPlanInfo> batchTempResult = repayPlanQueryService
						.queryTradeDetailUser(repayTempOrder);
					if (batchTempResult.getTotalCount() > 0
						&& batchTempResult.getPageList().get(0).getStatus() == RepayPlanStatusEnum.NOTPAY) {
						repayPlanInfo.setIsPay("false");
					} else {
						repayPlanInfo.setIsPay("true");
					}
				} else {
					repayPlanInfo.setIsPay("true");
				}
				tradeIdMap.put(repayPlanInfo.getTradeId(), true);
			} else {
				repayPlanInfo.setIsPay("false");
			}*/
			
			if (tradeInfo.getTradeStatus() == TradeStatusEnum.REPAYING
				|| tradeInfo.getTradeStatus() == TradeStatusEnum.DOREPAY
				|| tradeInfo.getTradeStatus() == TradeStatusEnum.REPAYING_FAILD) {
				if (repayPlanQueryService.isPrePeriodPayOff(repayPlanInfo.getTradeId(),
					repayPlanInfo.getPeriodNo())) {
					repayPlanInfo.setIsPay("true");
				} else {
					repayPlanInfo.setIsPay("false");
				}
			} else {
				repayPlanInfo.setIsPay("false");
			}
			
		}
		originalMoney = repayPlanQueryService.originalGuaranteeAmount(repayQuery);
		benifitMoney = batchResult.getTotalMoney().subtract(originalMoney);
		if (AppConstantsUtil.canTradeUsePayPassword()) {
			YjfEzmoneyPayPassUrlOrder ezmoneyPayPassUrlOrder = new YjfEzmoneyPayPassUrlOrder();
			ezmoneyPayPassUrlOrder.setUserId(SessionLocalManager.getSessionLocal().getAccountId());
			YrdBaseResult baseResult = yjfLoginWebServer.gotoYjfValidatePayPasswordUrl(
				ezmoneyPayPassUrlOrder, this.getOpenApiContext());
			model.addAttribute("yjfPaypassUrl", baseResult.getUrl());
		}
		model.addAttribute("repayQuery", repayQuery);
		model.addAttribute("viewStatus", viewStatus);
		model.addAttribute("page", PageUtil.getCovertPage(batchResult));
		model.addAttribute("repayMoney", batchResult.getTotalMoney());
		model.addAttribute("originalMoney", originalMoney);
		model.addAttribute("benifitMoney", benifitMoney);
		model.addAttribute("startDate", startTime);
		model.addAttribute("endDate", endTime);
		return GUARANTEE_MANAGE_PATH + "repayPlanGuraneteeQuery.vm";
	}
	
	/**
	 * 获取replanId
	 */
	@ResponseBody
	@RequestMapping("getPlanId/{repayPlanId}")
	public JSONObject getRepayPlanId(Model model, @PathVariable long repayPlanId,
										HttpSession session) {
		this.initAccountInfo(model);
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
	@RequestMapping("guaranteeRepayMoney")
	public Object repayMoney(long tradeId, long repayPlanId, String smsCode, String mobile,
								String business, String token, String paytk, HttpSession session) {
		logger.info("手动还款，入参：[repayPlanId={}],", repayPlanId);
		String getToken = (String) session.getAttribute("token");
		Map<String, Object> map = new HashMap<String, Object>();
		if (AppConstantsUtil.canTradeUsePayPassword()) {
			YjfEzmoneyCheckPaytkOrder ezmoneyPayPassUrlOrder = new YjfEzmoneyCheckPaytkOrder();
			ezmoneyPayPassUrlOrder.setUserId(SessionLocalManager.getSessionLocal().getAccountId());
			ezmoneyPayPassUrlOrder.setPaytk(paytk);
			YrdBaseResult paytkResult = yjfLoginWebServer.gotoYjfCheckPayTk(ezmoneyPayPassUrlOrder,
				this.getOpenApiContext());
			if (!paytkResult.isSuccess()) {
				map.put("status", false);
				map.put("message", "还款时支付令牌错误");
				return map;
			}
		} else {
			SmsCodeResult smsCodeResult = this.smsManagerService.verifySmsCode(mobile,
				SmsBizType.getByCode(business), smsCode, false);
			if (!smsCodeResult.isSuccess()) {
				map.put("code", 0);
				map.put("message", "短信验证码错误");
				return map;
			}
		}
		
		try {
			if (token.equals(getToken) && repayPlanId > 0) {
				session.removeAttribute("token");
				
				RepayPlanInfo repayPlan = new RepayPlanInfo();
				repayPlan = repayPlanQueryService.queryRepayPlan(repayPlanId);
				YrdBaseResult result = null;
				
				TradeRepayOrder processOrder = new TradeRepayOrder();
				processOrder.setRepayPlanId(repayPlanId);
				processOrder.setBizNo(tradeId);
				processOrder.setProcessorId(SessionLocalManager.getSessionLocal().getUserId());
				processOrder.setRepayUserId(processOrder.getProcessorId());
				
				if (repayPlan.getStatus() == RepayPlanStatusEnum.PAYISD) { //针对已平台代偿还款的补充还款
					result = tradeBizProcessService.reRepay(processOrder);
				} else {
					result = tradeBizProcessService.repay(processOrder);
				}
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
	 * 还款详情
	 * 
	 * @param tradeId
	 * @param operate
	 * @param session
	 * @param model
	 * @param request
	 * @param pageParam
	 * @return
	 */
	@RequestMapping("repayGuaranteeDetails")
	public String repayDetails(long tradeId, String operate, HttpSession session, Model model,
								HttpServletRequest request, PageParam pageParam) {
		model.addAttribute("pdfhost", "");
		
		try {
			
			getTradeDetailPageView(0, tradeId, model, session, SysUserRoleEnum.LOANER, request,
				pageParam);
			String token = UUID.randomUUID().toString();
			model.addAttribute("operate", operate);
			session.setAttribute("token", token);
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return GUARANTEE_MANAGE_PATH + "repayGuaranteeDetails.vm";
	}
	
	/**
	 * 统计合计金额
	 * 
	 * @param result
	 * @return
	 */
	private Money countTotalAmount(List<LoanDemandTradeVOInfo> result) {
		Money totalAmount = Money.zero();
		if (result != null && result.size() > 0) {
			for (LoanDemandTradeVOInfo trade : result) {
				totalAmount.addTo(trade.getBenefitAmount());
			}
		}
		return totalAmount;
	}
	
	/**
	 * 查询交易详情
	 * 
	 * @throws Exception
	 */
	@RequestMapping("guaranteeDetails")
	public String guaranteeDetails(long demandId, String operate, Model model, HttpSession session,
									HttpServletRequest request, PageParam pageParam)
																					throws Exception {
		model.addAttribute("pdfhost", "");
		logger.info("担保函详情,查询开始");
		String view = null;
		try {
			SessionLocal sessionLocal = SessionLocalManager.getSessionLocal();
			if (sessionLocal != null && sessionLocal.getAccountId() != null) {
				this.initAccountInfo(model);
			}
			String token = UUID.randomUUID().toString();
			view = getTradeDetailPageView(demandId, 0, model, session, SysUserRoleEnum.GUARANTEE,
				request, pageParam);
			//项目附件
			CommonAttachmentQueryOrder attachmentQueryOrder = new CommonAttachmentQueryOrder();
			attachmentQueryOrder.setBizNo(demandId + "");
			attachmentQueryOrder.setModuleTypeList(YrdEnumUtil.getAttachmentByIndustry());
			QueryBaseBatchResult<CommonAttachmentInfo> batchResult = commonAttachmentService
				.queryCommonAttachment(attachmentQueryOrder);
			List<AttachmentModuleType> attachmentModuleTypeList = new ArrayList<AttachmentModuleType>();
			List<List<CommonAttachmentInfo>> attachmentInfoList = new ArrayList<List<CommonAttachmentInfo>>();
			for (CommonAttachmentInfo attachmentInfo : batchResult.getPageList()) {
				boolean isExist = false;
				for (AttachmentModuleType attachmentModuleType : attachmentModuleTypeList) {
					if (attachmentInfo.getModuleType() == attachmentModuleType.getModuleType()) {
						attachmentModuleType.getAttachmentInfos().add(attachmentInfo);
						isExist = true;
						break;
					}
				}
				if (!isExist) {
					AttachmentModuleType attachmentModuleType = new AttachmentModuleType();
					attachmentModuleType.setModuleType(attachmentInfo.getModuleType());
					attachmentModuleType.getAttachmentInfos().add(attachmentInfo);
					attachmentModuleTypeList.add(attachmentModuleType);
				}
			}
			model.addAttribute("list", attachmentModuleTypeList);
			//end
			model.addAttribute("operate", operate);
			session.setAttribute("token", token);
		} catch (Exception e) {
			logger.info("担保函详情,查询异常", e.getMessage());
		}
		if (StringUtil.isNotBlank(view))
			return view;
		return GUARANTEE_MANAGE_PATH + "guarantee_details.vm";
	}
	
	// -----------------------------------------------------营销统计----------------------------------------------------------------------
	
	@RequestMapping("salesCount")
	public String salesCount(HttpSession session, PageParam pageParam, Model model)
																					throws Exception {
		session.setAttribute("current", 5);
		long guaranteeUserId = getGuaranteeUserId();
		QueryLoanTradeOrder queryConditions = new QueryLoanTradeOrder();
		queryConditions.setUserId(guaranteeUserId);
		queryConditions.setGuaranteeId(guaranteeUserId);
		queryConditions.setRoleId(SysUserRoleEnum.GUARANTEE.getValue());
		queryConditions.setPageSize(5000);
		queryConditions.setHasBenefitAmount("YES");
		TradeStatisticsResult<TradeAmountStatisticsInfo> batchResult = tradeBizQueryService
			.searchLoanTradeSumCommonQuery(queryConditions);
		model.addAttribute("statisticsInfo", batchResult.getStatisticsInfo());
		
		return GUARANTEE_MANAGE_PATH + "guarantee-salesCount.vm";
	}
	
	private List<TradeStatusDO> buildTradeStatus() {
		List<TradeStatusDO> tradeStatus = new ArrayList<TradeStatusDO>();
		List<TradeStatusEnum> statusEnums = TradeStatusEnum.CHECK_PENDING.getAllEnum();
		for (TradeStatusEnum statusEnum : statusEnums) {
			if (statusEnum.getValue() >= 1) {
				TradeStatusDO tradeStatu = new TradeStatusDO();
				tradeStatu.setTradeStateKey(statusEnum.getValue());
				tradeStatu.setTradeStateValue(statusEnum.getGuaranteeStatus());
				tradeStatus.add(tradeStatu);
			}
			
		}
		return tradeStatus;
	}
	
	public long getGuaranteeUserId() {
		try {
			String userBaseId = SessionLocalManager.getSessionLocal().getUserBaseId();
			long userId = SessionLocalManager.getSessionLocal().getUserId();
			
			Map<String, Object> conditions = new HashMap<String, Object>();
			conditions.put("userBaseId", userBaseId);
			conditions.put("limitStart", 0);
			conditions.put("pageSize", 9999);
			List<OperatorInfoDO> operatorInfos = operatorInfoService
				.queryOperatorsByProperties(conditions);
			if (operatorInfos != null && operatorInfos.size() > 0) {
				UserInfo curParentJG = userQueryService.queryByUserId(
					operatorInfos.get(0).getParentId()).getQueryUserInfo();
				if (curParentJG != null) {
					userId = curParentJG.getUserId();
				}
			}
			guaranteeUserId = userId;
		} catch (Exception e) {
			logger.error("获取承诺机构userId失败", e);
		}
		
		return guaranteeUserId;
	}
	
	public void setGuaranteeUserId(long guaranteeUserId) {
		this.guaranteeUserId = guaranteeUserId;
	}
	
	@ResponseBody
	@RequestMapping("validationCheckPwd")
	public Object validationCheckPwd(String checkPassword) throws Exception {
		JSONObject jsonobj = new JSONObject();
		Boolean result = operatorInfoService.validationAuditPassword(SessionLocalManager
			.getSessionLocal().getUserBaseId(), checkPassword);
		if (result) {
			jsonobj.put("code", 1);
			jsonobj.put("message", "审核密码正确");
		} else {
			jsonobj.put("code", 0);
			jsonobj.put("message", "审核密码错误");
		}
		return jsonobj;
	}
	
	/**
	 * 跳转设置密码界面
	 */
	@RequestMapping("anthPasswordPage")
	public String anthPasswordPage(Model model, HttpSession session) {
		logger.info("审核一级密码设置和修改,入参：{}");
		OperatorInfoDO operatorInfoDO = operatorInfoService
			.queryOperatorByUserBaseId(SessionLocalManager.getSessionLocal().getUserBaseId());
		if (operatorInfoDO.getOperatorType() == 2) {
			model.addAttribute("alreadySetOne", "true");
		}
		if (operatorInfoDO.getOperatorType() == 3) {
			model.addAttribute("alreadySetTwo", "true");
		}
		logger.info("审核一级密码设置和修改结束");
		return GUARANTEE_MANAGE_PATH + "guarantee-password.vm";
	}
	
	/**
	 * 新增或者修改审核密码
	 * @param session
	 * @param request
	 * @param model
	 * @return
	 */
	@ResponseBody
	@RequestMapping("authPasswordSubmit")
	public Object authPasswordSubmit(HttpSession session, HttpServletRequest request, Model model) {
		JSONObject jsonobj = new JSONObject();
		UpdateAuthPasswordOrder updateAuthPasswordOrder = new UpdateAuthPasswordOrder();
		WebUtil.setPoPropertyByRequest(updateAuthPasswordOrder, request);
		updateAuthPasswordOrder
			.setUserBaseId(SessionLocalManager.getSessionLocal().getUserBaseId());
		YrdBaseResult baseResult = operatorInfoService.updateAuthPassword(updateAuthPasswordOrder);
		if (baseResult.isSuccess()) {
			jsonobj.put("code", 1);
			if ("lvOne".equals(updateAuthPasswordOrder.getType())) {
				jsonobj.put("message", "一级审核密码修改成功！");
			} else {
				jsonobj.put("message", "二级审核密码修改成功！");
			}
			
		} else {
			jsonobj.put("code", 0);
			if ("lvOne".equals(updateAuthPasswordOrder.getType())) {
				jsonobj.put("message", "一级审核密码修改失败！");
			} else {
				jsonobj.put("message", "二级审核密码修改失败！");
			}
		}
		return jsonobj;
	}
	
	/**
	 * 找回密码
	 * 
	 * @param session
	 * @param level
	 * @return
	 */
	@ResponseBody
	@RequestMapping("findAuthPwd/{level}")
	public Object findAuthPwd(HttpSession session, @PathVariable String level) {
		JSONObject jsonobj = new JSONObject();
		YrdBaseResult result = userBaseInfoManager.resetCheckPassword(SessionLocalManager
			.getSessionLocal().getUserBaseId(), "888888");
		if (result.isSuccess()) {
			jsonobj.put("code", 1);
			if ("lvOne".equals(level)) {
				jsonobj.put("message", "重置一级审核密码为888888,请立即修改！");
			} else {
				jsonobj.put("message", "重置二级审核密码为888888,请立即修改！");
			}
		} else {
			jsonobj.put("code", 0);
			if ("lvOne".equals(level)) {
				jsonobj.put("message", "重置一级审核密码失败！");
			} else {
				jsonobj.put("message", "重置二级审核密码失败！");
			}
			
		}
		
		return jsonobj;
	}
	
	/**
	 * 审核密码校验
	 * @param session
	 * @param pwd
	 * @param type
	 * @param model
	 * @return
	 */
	
	@ResponseBody
	@RequestMapping("authPasswordCheck")
	public Object authPasswordCheck(HttpSession session, String pwd, String type, Model model) {
		JSONObject jsonobj = new JSONObject();
		
		try {
			boolean result = operatorInfoService.validationAuditPassword(SessionLocalManager
				.getSessionLocal().getUserBaseId(), pwd);
			if (result) {
				jsonobj.put("code", 1);
				jsonobj.put("message", "密码匹配！");
			} else {
				jsonobj.put("code", 0);
				jsonobj.put("message", "密码不匹配！");
			}
		} catch (Exception e1) {
			logger.error(e1.getMessage(), e1);
			jsonobj.put("code", 0);
			jsonobj.put("message", "密码不匹配！");
		}
		return jsonobj;
	}
	
	/**
	 * 担保机构审核详情
	 */
	
	@ResponseBody
	@RequestMapping("guaranteeMakeLoanAuditing")
	public Object guaranteeMakeLoanAuditing(HttpSession session, long demandId, long tradeId,
											Model model) throws Exception {
		JSONObject jsonobj = new JSONObject();
		try {
			LoanAuthOrder loanAuthOrder = new LoanAuthOrder();
			loanAuthOrder.setProcessorId(SessionLocalManager.getSessionLocal().getUserId());
			loanAuthOrder.setAuthType(YrdAuthTypeEnum.MAKELOANLVONE);
			loanAuthOrder.setBizNo(tradeId);
			TradeInfo trade = tradeBizQueryService.getByTradeId(tradeId);
			if (trade != null && trade.getTradeEffectiveDate() == null) {
				jsonobj.put("code", 0);
				jsonobj.put("message", "亲，请先生成合同！");
				return jsonobj;
			}
			
			YrdBaseResult baseResult = tradeBizProcessService.tradeCheck(loanAuthOrder);
			if (baseResult.isSuccess()) {
				jsonobj.put("code", 1);
				jsonobj.put("message", "审核成功！");
				LoanDemandInfo loanDemandInfo = loanDemandQueryService.findById(demandId);
				TradeInfo tradeInfo = tradeBizQueryService.getByTradeId(tradeId);
				if (loanDemandInfo != null && tradeInfo != null) {
					tradeBizProcessService.notifyGuaranteeAudit(loanDemandInfo.getGuaranteeId(),
						tradeInfo.getTradeName(), 3);
				}
				
			} else {
				jsonobj.put("code", 0);
				jsonobj.put("message", "审核失败【" + baseResult.getMessage() + "】！");
			}
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			jsonobj.put("code", 0);
			jsonobj.put("message", "审核失败！");
		}
		return jsonobj;
	}
	
	@ResponseBody
	@RequestMapping("guaranteeMakeContract")
	public Object guaranteeMakeContract(HttpSession session, HttpServletRequest request,
										long demandId, long tradeId, Model model) throws Exception {
		JSONObject jsonobj = new JSONObject();
		try {
			
			TradeProcessOrder processOrder = new TradeProcessOrder();
			processOrder.setBizNo(tradeId);
			processOrder.setProcessorId(getGuaranteeUserId());
			
			YrdBaseResult yrdBaseResult = tradeBizProcessService
				.guaranteeMakeContract(processOrder);
			// 增加担保函流水号
			if (yrdBaseResult.isSuccess()) {
				jsonobj.put("code", 1);
				jsonobj.put("message", "成功创建合同！");
			} else {
				jsonobj.put("code", 0);
				jsonobj.put("message", "创建合同失败[" + yrdBaseResult.getMessage() + "]！");
			}
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			jsonobj.put("code", 0);
			jsonobj.put("message", "创建合同失败！");
		}
		return jsonobj;
	}
	
	@ResponseBody
	@RequestMapping("creatCertificate")
	public Object creatCertificate(HttpSession session, HttpServletRequest request, long tradeId,
									String type, Model model) throws Exception {
		JSONObject jsonobj = new JSONObject();
		try {
			
			CreatTradeContractCertificateOrder certificateOrder = new CreatTradeContractCertificateOrder();
			certificateOrder.setTradeId(tradeId);
			certificateOrder.setType(type);
			String serverRealPath = request.getServletContext().getRealPath("/");
			certificateOrder.setServerRealPath(serverRealPath);
			CreatCertificateResult certificateResult = certificateService
				.creatTradeContractCertificate(certificateOrder);
			if (certificateResult.isSuccess()) {
				
				jsonobj.put("code", 1);
				jsonobj.put("message", certificateResult.getMessage());
				jsonobj.put("url", certificateResult.getElecCertiUrl());
			} else {
				jsonobj.put("code", 0);
				jsonobj.put("message", certificateResult.getMessage());
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			jsonobj.put("code", 0);
			jsonobj.put("message", "生成证书失败！");
		}
		return jsonobj;
	}
	
	/**
	 * 担保机构审核完成之后放款进入还款阶段
	 * 
	 * @param tradeId
	 * @param session
	 * @return
	 */
	@ResponseBody
	@RequestMapping("guaranteeAuditingFinish")
	public Object guaranteeAuditingFinish(long tradeId, String token, HttpSession session,
											HttpServletRequest request) {
		logger.info("担保机构审核完成开始放款转账，入参：[tradeId={}],", tradeId);
		Map<String, Object> map = new HashMap<String, Object>();
		
		String getToken = (String) session.getAttribute("token");
		try {
			if (token.equals(getToken)) {
				session.removeAttribute("token");
				logger.info("tradeId为" + tradeId + "计算转账金额，转账中...");
				LoanAuthOrder processOrder = new LoanAuthOrder();
				processOrder.setProcessorId(SessionLocalManager.getSessionLocal().getUserId());
				processOrder.setAuthType(YrdAuthTypeEnum.MAKELOANLVONE);
				processOrder.setBizNo(tradeId);
				String serverRealPath = request.getServletContext().getRealPath("/");
				processOrder.setServerPath(serverRealPath);
				YrdBaseResult baseResult = tradeBizProcessService
					.guaranteeAuditingFinish(processOrder);
				if (baseResult.isSuccess()) {
					map.put("code", 1);
					map.put("message", "放款成功");
				} else {
					map.put("code", 0);
					map.put("message", "审核失败");
				}
				
			} else {
				map.put("code", 0);
				map.put("message", "请勿重复提交");
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			map.put("code", 0);
			map.put("message", "放款失败");
		}
		return map;
	}
	
	@Override
	public String getNoTradeView() {
		return GUARANTEE_MANAGE_PATH + "guarantee_details_noTrade.vm";
	}
	
	@Override
	public String getTradeView() {
		return GUARANTEE_MANAGE_PATH + "guarantee_details.vm";
	}
}
