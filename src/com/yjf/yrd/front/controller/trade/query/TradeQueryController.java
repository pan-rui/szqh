package com.yjf.yrd.front.controller.trade.query;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
import com.yjf.common.lang.beans.cglib.BeanCopier;
import com.yjf.common.lang.util.ListUtil;
import com.yjf.common.lang.util.StringUtil;
import com.yjf.common.lang.util.money.Money;
import com.yjf.yrd.base.CommonBindingInitializer;
import com.yjf.yrd.common.result.SmsCodeResult;
import com.yjf.yrd.front.controller.trade.base.LoanTradeDetailBaseController;
import com.yjf.yrd.integration.web.server.YjfLoginWebServer;
import com.yjf.yrd.integration.web.server.impl.YjfEzmoneyCheckPaytkOrder;
import com.yjf.yrd.integration.web.server.impl.YjfEzmoneyPayPassUrlOrder;
import com.yjf.yrd.page.PageParam;
import com.yjf.yrd.service.certificate.CertificateService;
import com.yjf.yrd.service.certificate.order.TradeItemCertificateOrder;
import com.yjf.yrd.service.certificate.result.CreatCertificateResult;
import com.yjf.yrd.service.query.debtTransfer.DebtTransferRuleQueryService;
import com.yjf.yrd.service.query.trade.DebtTransferBizQueryService;
import com.yjf.yrd.service.trade.download.ContractData;
import com.yjf.yrd.service.trade.download.InvestReceiptPDFCreator;
import com.yjf.yrd.service.trade.download.PDFParse;
import com.yjf.yrd.session.SessionLocal;
import com.yjf.yrd.session.SessionLocalManager;
import com.yjf.yrd.util.AppConstantsUtil;
import com.yjf.yrd.util.DateUtil;
import com.yjf.yrd.util.NumberUtil;
import com.yjf.yrd.web.util.AttachmentModuleType;
import com.yjf.yrd.web.util.PageUtil;
import com.yjf.yrd.web.util.WebUtil;
import com.yjf.yrd.web.util.YrdEnumUtil;
import com.yjf.yrd.ws.enums.DebtTransferStatus;
import com.yjf.yrd.ws.enums.DivisionPhaseEnum;
import com.yjf.yrd.ws.enums.DivisionWayEnum;
import com.yjf.yrd.ws.enums.PDFTypeCodeEnum;
import com.yjf.yrd.ws.enums.RepayPlanStatusEnum;
import com.yjf.yrd.ws.enums.SmsBizType;
import com.yjf.yrd.ws.enums.SysUserRoleEnum;
import com.yjf.yrd.ws.enums.TradeStatusEnum;
import com.yjf.yrd.ws.info.CommonAttachmentInfo;
import com.yjf.yrd.ws.info.DebtTransferRuleInfo;
import com.yjf.yrd.ws.info.LoanDemandInfo;
import com.yjf.yrd.ws.info.LoanDemandTradeVOInfo;
import com.yjf.yrd.ws.info.RepayPlanInfo;
import com.yjf.yrd.ws.info.TradeDetailTransferInfo;
import com.yjf.yrd.ws.info.TradeDetailVOInfo;
import com.yjf.yrd.ws.info.TradeInfo;
import com.yjf.yrd.ws.order.CommonAttachmentQueryOrder;
import com.yjf.yrd.ws.order.CustomRepayOrder;
import com.yjf.yrd.ws.order.DebtTransferCancelOrder;
import com.yjf.yrd.ws.order.DebtTransferReReleaseOrder;
import com.yjf.yrd.ws.order.DebtTransferReleaseOrder;
import com.yjf.yrd.ws.order.TradeDetailQueryOrder;
import com.yjf.yrd.ws.order.TradeRepayOrder;
import com.yjf.yrd.ws.result.ValidateCanDebtTransferResult;
import com.yjf.yrd.ws.result.YrdBaseResult;
import com.yjf.yrd.ws.service.CommonAttachmentService;
import com.yjf.yrd.ws.service.CustomRepayService;
import com.yjf.yrd.ws.service.DebtTransferBizProcessService;
import com.yjf.yrd.ws.service.TradeFlowCodeManager;
import com.yjf.yrd.ws.service.query.order.DebtTransferQueryOrder;
import com.yjf.yrd.ws.service.query.order.FileElecCertificateOrder;
import com.yjf.yrd.ws.service.query.order.QueryLoanTradeOrder;
import com.yjf.yrd.ws.service.query.order.RepayPlanQueryOrder;
import com.yjf.yrd.ws.service.query.result.QueryBaseBatchResult;
import com.yjf.yrd.ws.service.query.result.TradeDetailBatchResult;

/**
 * @Filename TradeQueryController.java
 * @Description
 * @Version 1.0
 * @Author zjl
 * @Email zjialin@yiji.com
 * @History <li>Author: zjl</li> <li>Date: 2013-8-21</li> <li>Version: 1.0</li>
 * <li>Content: create</li>
 */
@Controller
@RequestMapping("tradeQuery")
public class TradeQueryController extends LoanTradeDetailBaseController {
	private final String vm_path = "/front/trade/query/";
	@Autowired
	TradeFlowCodeManager tradeFlowCodeManager;
	@Autowired
	private DebtTransferBizProcessService debtTransferBizProcessService;
	@Autowired
	private DebtTransferBizQueryService debtTransferBizQueryService;
	
	@Autowired
	protected InvestReceiptPDFCreator receiptPDFCreator;
	
	@Autowired
	YjfLoginWebServer yjfLoginWebServer;
	@Autowired
	CommonAttachmentService commonAttachmentService;
	
	@Autowired
	CustomRepayService customRepayService;
	@Autowired
	CertificateService certificateService;
	@Autowired
	DebtTransferRuleQueryService debtTransferRuleQueryService;
	
	/**
	 * 工具类处理金额 /时间
	 */
	@Override
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		super.initBinder(binder);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		dateFormat.setLenient(false);
		binder.registerCustomEditor(Money.class, "minAmountIn", new CommonBindingInitializer());
		binder.registerCustomEditor(Money.class, "maxAmountIn", new CommonBindingInitializer());
		binder
			.registerCustomEditor(Date.class, "startTime", new CustomDateEditor(dateFormat, true));
		binder.registerCustomEditor(Date.class, "endTime", new CustomDateEditor(dateFormat, true));
	}
	
	@Override
	protected String[] getMoneyInputNameArray() {
		return new String[] { "minLoanAmount", "maxLoanAmount" };
	}
	
	/**
	 * 查询借款管理
	 */
	@RequestMapping("borrowingRecord")
	public String borrowingRecord(QueryLoanTradeOrder queryTradeOrder, PageParam pageParam,
									Model model, HttpSession session) {
		try {
			
			SessionLocal sessionLocal = SessionLocalManager.getSessionLocal();
			if (sessionLocal != null && sessionLocal.getAccountId() != null) {
				this.initAccountInfo(model);
			}
			queryTradeOrder.setLoanderId(sessionLocal.getUserId());
			queryTradeOrder.setPageNumber(pageParam.getPageNo());
			queryTradeOrder.setPageSize(pageParam.getPageSize());
			QueryBaseBatchResult<LoanDemandTradeVOInfo> batchResult = tradeBizQueryService
				.searchLoanTradeCommonQuery(queryTradeOrder);
			YjfEzmoneyPayPassUrlOrder ezmoneyPayPassUrlOrder = new YjfEzmoneyPayPassUrlOrder();
			ezmoneyPayPassUrlOrder.setUserId(sessionLocal.getAccountId());
			YrdBaseResult baseResult = yjfLoginWebServer.gotoYjfValidatePayPasswordUrl(
				ezmoneyPayPassUrlOrder, this.getOpenApiContext());
			model.addAttribute("yjfPaypassUrl", baseResult.getUrl());
			model.addAttribute("queryTradeOrder", queryTradeOrder);
			model.addAttribute("page", PageUtil.getCovertPage(batchResult));
			String token = UUID.randomUUID().toString();
			session.setAttribute("token", token);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return vm_path + "borrowingRecord.vm";
	}
	
	/**
	 * 查借款详情/和还款
	 */
	@RequestMapping("borrowingDetails")
	public String borrowingDetails(long tradeId, String operate, HttpSession session, Model model,
									HttpServletRequest request, PageParam pageParam) {
		model.addAttribute("pdfhost", "");
		
		try {
			if (AppConstantsUtil.canTradeUsePayPassword()) {
				SessionLocal sessionLocal = SessionLocalManager.getSessionLocal();
				YjfEzmoneyPayPassUrlOrder ezmoneyPayPassUrlOrder = new YjfEzmoneyPayPassUrlOrder();
				ezmoneyPayPassUrlOrder.setUserId(sessionLocal.getAccountId());
				YrdBaseResult baseResult = yjfLoginWebServer.gotoYjfValidatePayPasswordUrl(
					ezmoneyPayPassUrlOrder, this.getOpenApiContext());
				model.addAttribute("yjfPaypassUrl", baseResult.getUrl());
			}
			getTradeDetailPageView(0, tradeId, model, session, SysUserRoleEnum.LOANER, request,
				pageParam);
			//项目附件
			LoanDemandInfo loanDemand = null;
			if (tradeId > 0) {
				loanDemand = loanDemandQueryService.loadLoanDemandInfoByTradeId(tradeId);
			}
			//按月还款的项目到“还款管理”处还款
			if ((loanDemand.getRepayDivisionWay() == DivisionWayEnum.MONTH_PRINCIPAL_INTEREST || loanDemand
				.getRepayDivisionWay() == DivisionWayEnum.MONTH_WAY) && "hk".equals(operate)) {
				return "redirect:/tradeQuery/repay/12/1";
			}
			CommonAttachmentQueryOrder attachmentQueryOrder = new CommonAttachmentQueryOrder();
			attachmentQueryOrder.setBizNo(loanDemand.getDemandId() + "");
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
			String token = UUID.randomUUID().toString();
			model.addAttribute("operate", operate);
			session.setAttribute("token", token);
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return vm_path + "borrowingDetails.vm";
	}
	
	@ResponseBody
	@RequestMapping("repayEquityRaise")
	public Object repayEquityRaise(String token, HttpSession session, HttpServletRequest request) {
		
		SessionLocal sessionLocal = SessionLocalManager.getSessionLocal();
		CustomRepayOrder customRepayOrder = new CustomRepayOrder();
		WebUtil.setPoPropertyByRequest(customRepayOrder, request);
		customRepayOrder.setRepayUserId(sessionLocal.getUserId());
		logger.info("产权还款，入参：[customRepayOrder={}],", customRepayOrder);
		String getToken = (String) session.getAttribute("token");
		String paytk = request.getParameter("paytk");
		Map<String, Object> map = new HashMap<String, Object>();
		YjfEzmoneyCheckPaytkOrder ezmoneyPayPassUrlOrder = new YjfEzmoneyCheckPaytkOrder();
		ezmoneyPayPassUrlOrder.setUserId(sessionLocal.getAccountId());
		ezmoneyPayPassUrlOrder.setPaytk(paytk);
		YrdBaseResult paytkResult = yjfLoginWebServer.gotoYjfCheckPayTk(ezmoneyPayPassUrlOrder,
			this.getOpenApiContext());
		if (!paytkResult.isSuccess()) {
			map.put("status", false);
			map.put("message", "还款时支付令牌错误");
			return map;
		}
		
		try {
			if (token.equals(getToken)) {
				session.removeAttribute("token");
				YrdBaseResult result = customRepayService.customRepay(customRepayOrder);
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
	@RequestMapping("manualReimbursement")
	public Object manualReimbursement(long tradeId, long demandId, String smsCode, String mobile,
										String business, String token, HttpSession session,
										HttpServletRequest request) {
		logger.info("手动还款，入参：[demandId={}],", demandId);
		SessionLocal sessionLocal = SessionLocalManager.getSessionLocal();
		String getToken = (String) session.getAttribute("token");
		String paytk = request.getParameter("paytk");
		Map<String, Object> map = new HashMap<String, Object>();
		LoanDemandInfo loanDemandInfo = loanDemandQueryService.findById(demandId);
		if (loanDemandInfo != null) {
			if (loanDemandInfo.getRepayDivisionWay() == DivisionWayEnum.MONTH_WAY) {
				map.put("code", 0);
				map.put("message", "亲!请到还款管理还款!");
				return map;
			}
		}
		if (AppConstantsUtil.canTradeUsePayPassword()) {
			YjfEzmoneyCheckPaytkOrder ezmoneyPayPassUrlOrder = new YjfEzmoneyCheckPaytkOrder();
			ezmoneyPayPassUrlOrder.setUserId(sessionLocal.getAccountId());
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
			if (token.equals(getToken) && demandId > 0) {
				session.removeAttribute("token");
				TradeRepayOrder processOrder = new TradeRepayOrder();
				processOrder.setBizNo(tradeId);
				processOrder.setRepayUserId(SessionLocalManager.getSessionLocal().getUserId());
				processOrder.setProcessorId(SessionLocalManager.getSessionLocal().getUserId());
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
	 * 还款查询
	 * 
	 * @throws Exception
	 */
	@RequestMapping("repay/{size}/{page}")
	public String repayPlanRecord(Model model, @PathVariable int size, String scope,
									@PathVariable int page, Integer status, String startDate,
									String endDate, HttpSession session) throws Exception {
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
			statusList.add(RepayPlanStatusEnum.REPAY_FAIL);
			statusList.add(RepayPlanStatusEnum.PAYISD);
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
		repayQuery.setRepayUserId(SessionLocalManager.getSessionLocal().getUserId());
		repayQuery.setPageNumber(page);
		repayQuery.setPageSize(size);
		if (status == 0 && startDate != null && endDate != null && !startDate.equals("")
			&& !endDate.equals("")) {
			repayQuery.setStartRepayDate(startTime);
			repayQuery.setEndRepayDate(endTime);
		} else if (status == 1 && startDate != null && endDate != null && !startDate.equals("")
					&& !endDate.equals("")) {
			repayQuery.setActualStartRepayDate(startTime);
			repayQuery.setActualEndRepayDate(endTime);
		}
		if (status == 0) {
			repayQuery.setOrderBy("Y");
		}
		repayQuery.setStatusList(statusList);
		
		QueryBaseBatchResult<RepayPlanInfo> batchResult = repayPlanQueryService
			.queryTradeDetailUser(repayQuery);
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
			
			
			if(tradeInfo.getTradeStatus() == TradeStatusEnum.REPAYING || tradeInfo
					.getTradeStatus() == TradeStatusEnum.DOREPAY   || tradeInfo
	    					.getTradeStatus() == TradeStatusEnum.REPAYING_FAILD){
        		if(repayPlanQueryService.isPrePeriodPayOff(repayPlanInfo.getTradeId(), repayPlanInfo.getPeriodNo())){
        			repayPlanInfo.setIsPay("true");
        		}else{
        			repayPlanInfo.setIsPay("false");
        		}
        	}else{
        		repayPlanInfo.setIsPay("false");
        	}
			
		}
		originalMoney = repayPlanQueryService.originalAmount(repayQuery);
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
		return vm_path + "repayPlanQuery.vm";
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
	@RequestMapping("repayDetails")
	public String repayDetails(long tradeId, String operate, String status, HttpSession session,
								Model model, HttpServletRequest request, PageParam pageParam) {
		model.addAttribute("pdfhost", "");
		
		try {
			
			getTradeDetailPageView(0, tradeId, model, session, SysUserRoleEnum.LOANER, request,
				pageParam);
			String token = UUID.randomUUID().toString();
			model.addAttribute("operate", operate);
			model.addAttribute("status", status);
			session.setAttribute("token", token);
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return vm_path + "repayDetails.vm";
	}
	
	/**
	 * 获取replanId
	 */
	@ResponseBody
	@RequestMapping("getRepayPlanId/{repayPlanId}")
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
	@RequestMapping("repayMoney")
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
				map.put("message", smsCodeResult.getMessage());
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
	 * 查询投资记录
	 */
	@RequestMapping("entries/{size}/{page}")
	public String entries(@PathVariable int size, @PathVariable int page, String status,
							String startDate, String endDate, Model model, HttpSession session) {
		
		this.initAccountInfo(model);
		TradeDetailQueryOrder queryOrder = new TradeDetailQueryOrder();
		queryOrder.setPageSize(size);
		queryOrder.setPageNumber(page);
		queryOrder.setUserId(SessionLocalManager.getSessionLocal().getUserId());
		queryOrder.setRoleId(SysUserRoleEnum.INVESTOR.getValue());
		queryOrder.setTransferPhase(DivisionPhaseEnum.ORIGINAL_PHASE.code());
		List<String> statusList = new ArrayList<String>();
		if (com.yjf.yrd.util.StringUtil.isNotEmpty(status) && status.equals("-1")) {
			statusList.add("1");
			statusList.add("2");
			statusList.add("3");
			statusList.add("4");
			statusList.add("5");
			statusList.add("6");
			statusList.add("7");
			statusList.add("8");
			queryOrder.setStatus(statusList);
		} else if (com.yjf.yrd.util.StringUtil.isNotEmpty(status)) {
			String[] str = status.split(",");
			for (int i = 0; i < str.length; i++) {
				statusList.add(String.valueOf(str[i]));
			}
			queryOrder.setStatus(statusList);
		}
		queryOrder.setStartDate(startDate);
		queryOrder.setEndDate(endDate);
		TradeDetailBatchResult<TradeDetailVOInfo> batchResult = tradeBizQueryService
			.searchTradeDetailQuery(queryOrder);
		model.addAttribute("queryOrder", queryOrder);
		model.addAttribute("page", PageUtil.getCovertPage(batchResult));
		model.addAttribute("allAmount", batchResult.getTotalAmount());
		return vm_path + "invest_entries.vm";
	}
	
	/**
	 * 查询可转让记录
	 */
	@RequestMapping("entries/{size}/{page}/{status}")
	public String transfers(@PathVariable int size, @PathVariable int page,
							@PathVariable Integer status, String startDate, String endDate,
							Model model, HttpSession session) {
		
		this.initAccountInfo(model);
		TradeDetailQueryOrder queryOrder = new TradeDetailQueryOrder();
		queryOrder.setPageSize(size);
		queryOrder.setPageNumber(page);
		queryOrder.setUserId(SessionLocalManager.getSessionLocal().getUserId());
		queryOrder.setRoleId(SysUserRoleEnum.INVESTOR.getValue());
		queryOrder.setTransferPhase(DivisionPhaseEnum.ORIGINAL_PHASE.code());
		List<String> statusList = new ArrayList<String>();
		if (status != null && status > 0) {
			statusList.add(String.valueOf(status));
			queryOrder.setStatus(statusList);
		}
		queryOrder.setStartDate(startDate);
		queryOrder.setEndDate(endDate);
		queryOrder.setNotHasTransfering("Y");
		TradeDetailBatchResult<TradeDetailVOInfo> batchResult = tradeBizQueryService
			.searchTradeDetailQuery(queryOrder);
		if (ListUtil.isNotEmpty(batchResult.getPageList())) {
			for (TradeDetailVOInfo info : batchResult.getPageList()) {
				ValidateCanDebtTransferResult transferResult = debtTransferBizProcessService
					.validateCanDebtTransfer(info.getTradeDetailId());
				
				DebtTransferQueryOrder transferQueryOrder = new DebtTransferQueryOrder();
				transferQueryOrder.setTradeDetailId(info.getTradeDetailId());
				QueryBaseBatchResult<TradeDetailTransferInfo> result = debtTransferBizQueryService
					.queryDebtTransfer(transferQueryOrder);
				if (transferResult.isSuccess()
					&& (ListUtil.isEmpty(result.getPageList()) || StringUtil.equals(
						DebtTransferStatus.getCode(DebtTransferStatus.CANCEL), result.getPageList()
							.get(0).getStatus().getCode()))) {
					info.setCanTrans("Y");
				} else {
					info.setCanTrans("N");
				}
			}
		}
		
		model.addAttribute("page", PageUtil.getCovertPage(batchResult));
		model.addAttribute("allAmount", batchResult.getTotalAmount());
		return vm_path + "invest_transfer.vm";
	}
	
	/**
	 * 转让
	 * 
	 * @throws Exception
	 */
	
	@RequestMapping("entries/transferSubmit")
	public String transferSubmit(Model model, HttpSession session,
									DebtTransferReleaseOrder debtTransferReleaseOrder,
									String mailCode, String paytk) throws Exception {
		YrdBaseResult result = new YrdBaseResult();
		this.initAccountInfo(model);
		
		YjfEzmoneyCheckPaytkOrder ezmoneyPayPassUrlOrder = new YjfEzmoneyCheckPaytkOrder();
		ezmoneyPayPassUrlOrder.setUserId(SessionLocalManager.getSessionLocal().getAccountId());
		ezmoneyPayPassUrlOrder.setPaytk(paytk);
		YrdBaseResult paytkResult = yjfLoginWebServer.gotoYjfCheckPayTk(ezmoneyPayPassUrlOrder,
			this.getOpenApiContext());
		if (!paytkResult.isSuccess()) {
			result.setSuccess(false);
			result.setMessage("支付令牌错误");
			model.addAttribute("result", result);
			return vm_path + "transferResult.vm";
		}
		
		debtTransferReleaseOrder.setApplyUserId(SessionLocalManager.getSessionLocal().getUserId());
		DebtTransferQueryOrder transferQueryOrder = new DebtTransferQueryOrder();
		transferQueryOrder.setTradeDetailId(NumberUtil.parseLong(debtTransferReleaseOrder
			.getTradeDetailId()));
		QueryBaseBatchResult<TradeDetailTransferInfo> batchResult = debtTransferBizQueryService
			.queryDebtTransfer(transferQueryOrder);
		if (ListUtil.isNotEmpty(batchResult.getPageList())) {
			if (StringUtil.equals(DebtTransferStatus.getCode(DebtTransferStatus.CANCEL),
				batchResult.getPageList().get(0).getStatus().getCode())) {
				DebtTransferReReleaseOrder reReleaseOrder = new DebtTransferReReleaseOrder();
				BeanCopier.staticCopy(debtTransferReleaseOrder, reReleaseOrder);
				reReleaseOrder
					.setReReleaseUserId(SessionLocalManager.getSessionLocal().getUserId());
				reReleaseOrder.setTradeTransferId(batchResult.getPageList().get(0)
					.getTradeTransferId());
				result = debtTransferBizProcessService.reReleaselDebtTransfer(reReleaseOrder);
			} else {
				
				result.setSuccess(false);
				result.setMessage("该交易已经申请转让,不能再申请转让");
				model.addAttribute("result", result);
				return vm_path + "transferResult.vm";
			}
		} else {
			result = debtTransferBizProcessService.releaseDebtTransfer(debtTransferReleaseOrder);
		}
		if (result.isSuccess()) {
			result.setMessage("转让申请成功");
		} else {
			result.setMessage("转让申请失败[" + result.getMessage() + "]");
		}
		model.addAttribute("result", result);
		return vm_path + "transferResult.vm";
	}
	
	/**
	 * 转让申请
	 * 
	 * @throws Exception
	 */
	@RequestMapping("entries/transferApply")
	public String transferApply(Model model, HttpSession session, String tradeDetailId)
																						throws Exception {
		this.initAccountInfo(model);
		ValidateCanDebtTransferResult transferResult = debtTransferBizProcessService
			.validateCanDebtTransfer(NumberUtil.parseLong(tradeDetailId));
		DebtTransferRuleInfo debtTransferRuleInfo = debtTransferRuleQueryService.findCanUse();
		model.addAttribute("tradeDetailId", tradeDetailId);
		model.addAttribute("quota", debtTransferRuleInfo.getTransCharge());
		model.addAttribute("transfer", transferResult);
		return vm_path + "transferApply.vm";
	}
	
	/**
	 * 转让申请
	 * 
	 * @throws Exception
	 */
	@RequestMapping("entries/transferConfirm")
	public String transferConfirm(Model model, HttpServletRequest request) throws Exception {
		this.initAccountInfo(model);
		YjfEzmoneyPayPassUrlOrder ezmoneyPayPassUrlOrder = new YjfEzmoneyPayPassUrlOrder();
		ezmoneyPayPassUrlOrder.setUserId(SessionLocalManager.getSessionLocal().getAccountId());
		YrdBaseResult baseResult = yjfLoginWebServer.gotoYjfValidatePayPasswordUrl(
			ezmoneyPayPassUrlOrder, this.getOpenApiContext());
		model.addAttribute("yjfPaypassUrl", baseResult.getUrl());
		String tradeDetailId = request.getParameter("tradeDetailId");
		String transMoney = request.getParameter("transMoney");
		String chargeMoney = request.getParameter("chargeMoney");
		model.addAttribute("tradeDetailId", tradeDetailId);
		model.addAttribute("chargeMoney", new Money(chargeMoney));
		model.addAttribute("transMoney", new Money(transMoney));
		return vm_path + "transferConfirm.vm";
	}
	
	/**
	 * 转让
	 * 
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("entries/transferChargeMoney")
	public Object transferChargeMoney(Model model, HttpSession session, String transferMoney,
										long tradeDetailId) throws Exception {
		JSONObject json = new JSONObject();
		final DebtTransferRuleInfo debtTransferRuleInfo = debtTransferRuleQueryService.findCanUse();
		if (debtTransferRuleInfo == null) {
			json.put("code", 0);
			json.put("message", "平台未配置债权转让规则");
			return json;
		}
		
		Money money = new Money(transferMoney);
		ValidateCanDebtTransferResult result = debtTransferBizProcessService
			.validateCanDebtTransfer(tradeDetailId);
		boolean isExsitRange = false;
		if (!result.getDownMoney().greaterThan(money) && !money.greaterThan(result.getUpMoney())) {
			isExsitRange = true;
		}
		if (!isExsitRange) {
			json.put("code", 0);
			json.put("message", "转让金额超出可转让金额的范围或低于转让金额下限");
			return json;
		}
		
		Money chargeMoney = money.multiply(new BigDecimal(debtTransferRuleInfo.getTransCharge()));
		json.put("code", 1);
		json.put("message", chargeMoney);
		return json;
	}
	
	/**
	 * 查询转让中记录
	 */
	@RequestMapping("entries/transferingSubmit")
	public String transferingSubmit(Model model, HttpSession session) {
		this.initAccountInfo(model);
		List<DebtTransferStatus> statusList = new ArrayList<DebtTransferStatus>();
		statusList.add(DebtTransferStatus.TRANSFERRING);
		DebtTransferQueryOrder transferQueryOrder = new DebtTransferQueryOrder();
		transferQueryOrder.setApplyUserId(SessionLocalManager.getSessionLocal().getUserId());
		transferQueryOrder.setStatusList(statusList);
		QueryBaseBatchResult<TradeDetailTransferInfo> batchResult = debtTransferBizQueryService
			.queryDebtTransfer(transferQueryOrder);
		
		model.addAttribute("page", PageUtil.getCovertPage(batchResult));
		model.addAttribute("allAmount", batchResult.getTotalMoney());
		
		return vm_path + "invest_transfering.vm";
	}
	
	/**
	 * 查询已转让记录
	 */
	@RequestMapping("entries/transferedSubmit")
	public String transferedSubmit(Model model, HttpSession session) {
		this.initAccountInfo(model);
		List<DebtTransferStatus> statusList = new ArrayList<DebtTransferStatus>();
		statusList.add(DebtTransferStatus.SUCCESS);
		DebtTransferQueryOrder transferQueryOrder = new DebtTransferQueryOrder();
		transferQueryOrder.setApplyUserId(SessionLocalManager.getSessionLocal().getUserId());
		transferQueryOrder.setStatusList(statusList);
		QueryBaseBatchResult<TradeDetailTransferInfo> batchResult = debtTransferBizQueryService
			.queryDebtTransfer(transferQueryOrder);
		
		model.addAttribute("page", PageUtil.getCovertPage(batchResult));
		model.addAttribute("allAmount", batchResult.getTotalMoney());
		
		return vm_path + "invest_transfered.vm";
	}


    /**
     * 查询已转让记录
     */
    @RequestMapping("entries/transferBuy")
    public String transferedBuy(Model model, HttpSession session) {
        this.initAccountInfo(model);
        List<DebtTransferStatus> statusList = new ArrayList<DebtTransferStatus>();
        statusList.add(DebtTransferStatus.SUCCESS);
        DebtTransferQueryOrder transferQueryOrder = new DebtTransferQueryOrder();
        transferQueryOrder.setRecipientId(SessionLocalManager.getSessionLocal().getUserId());
        transferQueryOrder.setStatusList(statusList);
        QueryBaseBatchResult<TradeDetailTransferInfo> batchResult = debtTransferBizQueryService
                .queryDebtTransfer(transferQueryOrder);

        model.addAttribute("page", PageUtil.getCovertPage(batchResult));
        model.addAttribute("allAmount", batchResult.getTotalMoney());

        return vm_path + "invest_transfered_buy.vm";
    }
	
	/**
	 * 取消已转让记录
	 */
	@ResponseBody
	@RequestMapping("entries/cancelTransfer")
	public Object cancelTransfer(Model model, HttpSession session,
									DebtTransferCancelOrder cancelOrder) {
		JSONObject json = new JSONObject();
		this.initAccountInfo(model);
		cancelOrder.setCancelUserId(SessionLocalManager.getSessionLocal().getUserId());
		YrdBaseResult result = debtTransferBizProcessService.cancelDebtTransfer(cancelOrder);
		if (result.isSuccess()) {
			json.put("code", 1);
			json.put("message", "取消成功");
		} else {
			json.put("code", 0);
			json.put("message", "取消失败[" + result.getMessage() + "]");
		}
		return json;
	}
	
	/**
	 * 查看投资详情
	 * 
	 * @param tradeId
	 * @param detailId
	 * @param model
	 * @param session
	 * @param request
	 * @param pageParam
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("queryInvestDetails/{tradeId}")
	public String queryInvestDetails(@PathVariable long tradeId, long detailId, Model model,
										HttpSession session, HttpServletRequest request,
										PageParam pageParam) throws Exception {
		this.initAccountInfo(model);
		model.addAttribute("pdfhost", "");
		getTradeDetailPageView(0, tradeId, model, session, SysUserRoleEnum.INVESTOR, request,
			pageParam);
		//项目附件
		LoanDemandInfo loanDemand = null;
		if (tradeId > 0) {
			loanDemand = loanDemandQueryService.loadLoanDemandInfoByTradeId(tradeId);
		}
		CommonAttachmentQueryOrder attachmentQueryOrder = new CommonAttachmentQueryOrder();
		attachmentQueryOrder.setBizNo(loanDemand.getDemandId() + "");
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
		model.addAttribute("detailId", "detailId");
		return "front/trade/query/invest_detail.vm";
	}
	
	/**
	 * 查看投资详情凭证
	 * 
	 * @param tradeId
	 * @param detailId
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("investReceipt/{tradeId}")
	public String investReceipt(@PathVariable long tradeId, long detailId, Model model)
																						throws Exception {
		logger.info("下载投资详情凭证，入参：[tradeId={},detailId={}],", tradeId + "," + detailId);
		
		model.addAttribute("detailId", detailId);
		model.addAttribute("tradeId", tradeId);
		
		return "front/trade/query/invest_receipt.vm";
	}
	
	/**
	 * 查看投资详情凭证
	 * 
	 * @param tradeId
	 * @param detailId
	 * @param model
	 * @param response
	 * @param request
	 * @throws Exception
	 */
	@RequestMapping("toDownLReceipt")
	public void toDownLReceipt(long tradeId, long detailId, Model model,
								HttpServletResponse response, HttpServletRequest request)
																							throws Exception {
		logger.info("下载或预览投资凭证 ：" + tradeId + ":" + detailId);
		
		downLoadInvestReceipt(tradeId, detailId, "privew", response, request, model);
		
		//	model.addAttribute("detailId", detailId);
		//	model.addAttribute("tradeId", tradeId);
		//	return "front/trade/query/invest_receipt.vm";
	}
	
	@ResponseBody
	@RequestMapping("queryReceiptCertificate")
	public Object creatCertificate(long tradeId, long detailId, HttpServletRequest request)
																							throws Exception {
		logger.info("queryReceiptCertificate:tradeId " + tradeId + " detailId " + detailId);
		JSONObject jsonobj = new JSONObject();
		String fileKey = FileElecCertificateOrder.newReceiptFileKey(tradeId, detailId);
		String elecCertiUrl = "#";
		String preservationId = fileCertificateService.geElecCertiPreservationIdByFileKey(fileKey);
		
		if (preservationId == null) {
			String serverRealPath = request.getServletContext().getRealPath("/");
			TradeItemCertificateOrder itemCertificateOrder = new TradeItemCertificateOrder();
			itemCertificateOrder.setTradeDetailId(detailId);
			itemCertificateOrder.setTradeId(tradeId);
			itemCertificateOrder.setServerRealPath(serverRealPath);
			CreatCertificateResult certificateResult = certificateService
				.createTradeItemCertificate(itemCertificateOrder);
			if (certificateResult.isSuccess()) {
				elecCertiUrl = certificateResult.getElecCertiUrl();
			} else {
				jsonobj.put("code", 0);
				jsonobj.put("message", certificateResult.getMessage());
				return jsonobj;
			}
		} else {
			elecCertiUrl = fileCertificateService
				.getCertificateLink(Long.parseLong(preservationId));
		}
		
		jsonobj.put("code", 1);
		jsonobj.put("message", "证书查询成功");
		jsonobj.put("url", elecCertiUrl);
		
		return jsonobj;
		
	}
	
	@RequestMapping("investReceiptDownLoad/{tradeId}")
	public void downLoadInvestReceipt(@PathVariable long tradeId, long detailId, String checkType,
										HttpServletResponse response, HttpServletRequest request,
										Model model) throws Exception {
		
		logger.info("investReceiptDownLoad:" + checkType);
		String serverRealPath = request.getServletContext().getRealPath("/");
		LoanDemandInfo loanDemandInfo = loanDemandQueryService.loadLoanDemandInfoByTradeId(tradeId);
		long pdfTemplateId = 0;
		try { //预防部分平台未更新表结构而报错
			pdfTemplateId = demandPdfTemplateService.getPdfTemplateIdByDemandIdAndCode(
				loanDemandInfo.getDemandId(), PDFTypeCodeEnum.RECEIPT.code());
		} catch (Exception e1) {
			logger.error("downLoadInvestReceipt Exception ", e1);
		}
		
		if (pdfTemplateId > 0) {
			String pdfFileCode = "_DB";
			ContractData contractData = certificateService.getContractData(tradeId, detailId, -1,
				pdfFileCode, serverRealPath, PDFTypeCodeEnum.RECEIPT);
			logger.debug("PDFParseThread begin>>>>");
			PDFParse pdf = new PDFParse();
			pdf.writPDF(response, contractData, checkType);
		} else { //兼容原凭证打印方式
			byte[] data = receiptPDFCreator
				.creatFileData4Receipt(tradeId, detailId, serverRealPath);
			downloadPDF(checkType, response, data);
		}
		
	}
	
	private void downloadPDF(String checkType, HttpServletResponse response, byte[] data)
																							throws IOException {
		OutputStream servletOS;
		String extName = "投资凭证.pdf";
		if ("downLoad".equals(checkType)) {
			response.setContentType("application/pdf");
			try {
				response.addHeader("Content-Disposition", "attachment; filename="
															+ new String(
																extName.getBytes("GB2312"),
																"ISO8859-1"));
			} catch (UnsupportedEncodingException e1) {
				
				logger.error("UnsupportedEncodingException ", e1);
			}
		}
		
		servletOS = response.getOutputStream();
		servletOS.write(data);
		servletOS.flush();
		servletOS.close();
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
