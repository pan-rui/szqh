package com.yjf.yrd.base;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import com.yjf.common.lang.util.StringUtil;
import com.yjf.common.lang.util.money.Money;
import com.yjf.common.log.Logger;
import com.yjf.common.log.LoggerFactory;
import com.yjf.yrd.activity.IActivityService;
import com.yjf.yrd.authority.AuthorityService;
import com.yjf.yrd.base.data.BankDataService;
import com.yjf.yrd.certificate.FileCertificateService;
import com.yjf.yrd.common.services.BankBaseInfoService;
import com.yjf.yrd.common.services.BankBaseService;
import com.yjf.yrd.common.services.GuarantorManagerService;
import com.yjf.yrd.common.services.INotifierService;
import com.yjf.yrd.common.services.RealNameAuthenticationService;
import com.yjf.yrd.common.services.SmsManagerService;
import com.yjf.yrd.common.services.SysFunctionConfigService;
import com.yjf.yrd.common.services.SysParameterService;
import com.yjf.yrd.common.services.SystemMessageService;
import com.yjf.yrd.common.services.UserPointsByRuleTypeService;
import com.yjf.yrd.common.services.YrdMessageService;
import com.yjf.yrd.dal.daointerface.ExtraDAO;
import com.yjf.yrd.dal.daointerface.UserExtendDAO;
import com.yjf.yrd.dataobject.viewObject.TradeDetailStatisticsAmountVO;
import com.yjf.yrd.index.IndexTradeService;
import com.yjf.yrd.integration.openapi.CustomerService;
import com.yjf.yrd.integration.openapi.DeductDepositService;
import com.yjf.yrd.integration.openapi.DepositQueryService;
import com.yjf.yrd.integration.openapi.FundsTransferService;
import com.yjf.yrd.integration.openapi.SMSService;
import com.yjf.yrd.integration.openapi.WithdrawQueryService;
import com.yjf.yrd.integration.openapi.WithdrawService;
import com.yjf.yrd.integration.openapi.YjfTradeQueryService;
import com.yjf.yrd.integration.openapi.context.OpenApiContext;
import com.yjf.yrd.loanInfoItem.services.LoanInfoItemService;
import com.yjf.yrd.login.LoginService;
import com.yjf.yrd.mail.IMailSendService;
import com.yjf.yrd.pdf.template.DemandPdfTemplateService;
import com.yjf.yrd.pdf.template.PdfTemplateService;
import com.yjf.yrd.service.directSend.DirectSendService;
import com.yjf.yrd.service.openingbank.api.OpeningBankService;
import com.yjf.yrd.service.pointsRule.PointsRuleService;
import com.yjf.yrd.service.query.lottery.LotteryActivityQueryService;
import com.yjf.yrd.service.query.trade.LoanDemandQueryService;
import com.yjf.yrd.service.query.trade.RepayPlanQueryService;
import com.yjf.yrd.service.query.trade.TradeBizQueryService;
import com.yjf.yrd.service.query.trade.TradeStatisticsQueryService;
import com.yjf.yrd.service.recharge.RechargeFlowService;
import com.yjf.yrd.statistics.AmountStatisticsService;
import com.yjf.yrd.statistics.UserStatisticsService;
import com.yjf.yrd.trade.DeductYrdService;
import com.yjf.yrd.user.LoanerBaseInfoService;
import com.yjf.yrd.user.RegisterService;
import com.yjf.yrd.user.UserBaseInfoManager;
import com.yjf.yrd.user.UserPointsService;
import com.yjf.yrd.user.UserService;
import com.yjf.yrd.user.query.UserAccountQueryService;
import com.yjf.yrd.user.query.UserQueryService;
import com.yjf.yrd.user.query.UserRelationQueryService;
import com.yjf.yrd.util.ApplicationConstant;
import com.yjf.yrd.util.BusinessNumberUtil;
import com.yjf.yrd.util.DateUtil;
import com.yjf.yrd.util.MoneyUtil;
import com.yjf.yrd.ws.enums.DivisionPhaseEnum;
import com.yjf.yrd.ws.enums.SysUserRoleEnum;
import com.yjf.yrd.ws.info.GuaranteeAmountInfo;
import com.yjf.yrd.ws.info.TradeDetailInfo;
import com.yjf.yrd.ws.marketing.services.LotteryActivityService;
import com.yjf.yrd.ws.marketing.services.PrizeRuleService;
import com.yjf.yrd.ws.service.LoanDemandService;
import com.yjf.yrd.ws.service.TradeBizProcessService;
import com.yjf.yrd.ws.service.TradeFlowCodeManager;
import com.yjf.yrd.ws.service.query.order.QueryTradeDetailUserOrder;
import com.yjf.yrd.ws.service.query.result.QueryBaseBatchResult;
import com.yjf.yrd.ws.statistics.TradeStatisticsInfo;
import com.yjf.yrd.ws.statistics.order.TradeStatisticsOrder;

public class BaseAutowiredController {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	/** ZJL */

	@Autowired
	protected UserBaseInfoManager userBaseInfoManager;

	@Autowired
	protected LoanDemandService loanDemandService;
	@Autowired
	protected LoanDemandQueryService loanDemandQueryService;
	@Autowired
	protected GuarantorManagerService guarantorManagerService;
	@Autowired
	protected IMailSendService mailService;
	@Autowired
	protected BankBaseService bankBaseService;

	@Autowired
	protected BankBaseInfoService bankBaseInfoService;
	/** YHL */
	@Autowired
	protected AuthorityService authorityService;

	@Autowired
	protected TradeBizProcessService tradeBizProcessService;
	@Autowired
	protected TradeBizQueryService tradeBizQueryService;
	@Autowired
	protected LoginService loginService;

	@Autowired
	protected UserService userService;
	@Autowired
	protected RegisterService registerService;

	@Autowired
	protected IActivityService iActivityService;

	@Autowired
	protected INotifierService notifierService;

	@Autowired
	protected TradeStatisticsQueryService tradeStatisticsQueryService;
	/** PPMService */

	@Autowired
	protected YrdMessageService yrdMessageService;

	@Autowired
	protected UserStatisticsService userStatisticsService;
	@Autowired
	protected AmountStatisticsService amountStatisticsService;
	@Autowired
	protected RechargeFlowService rechargeFlowService;
	@Autowired
	protected DeductYrdService deductYrdService;
	@Autowired
	protected SysParameterService sysParameterService;
	@Autowired
	protected CustomerService customerService;

	@Autowired
	protected UserAccountQueryService userAccountQueryService;

	@Autowired
	protected OpeningBankService openingBankService;

	@Autowired
	protected RealNameAuthenticationService realNameAuthenticationService;
	@Autowired
	protected FundsTransferService fundsTransferService;
	@Autowired
	protected BankDataService bankDataService;
	@Autowired
	protected WithdrawQueryService withdrawQueryService;

	@Autowired
	protected WithdrawService withdrawService;

	@Autowired
	protected DepositQueryService depositQueryService;
	@Autowired
	protected IndexTradeService indexTradeService;
	@Autowired
	protected SmsManagerService smsManagerService;

	@Autowired
	protected SMSService sMSService;

	@Autowired
	protected DeductDepositService deductDepositService;
	@Autowired
	protected YjfTradeQueryService yjfTradeQueryService;

	@Autowired
	protected RepayPlanQueryService repayPlanQueryService;

	@Autowired
	protected UserPointsService userPointsService;

	@Autowired
	protected FileCertificateService fileCertificateService;

	@Autowired
	protected SysFunctionConfigService sysFunctionConfigService;

	@Autowired
	protected UserQueryService userQueryService;

	@Autowired
	protected UserRelationQueryService userRelationQueryService;

	@Autowired
	protected PdfTemplateService pdfTemplateService;

	@Autowired
	protected DemandPdfTemplateService demandPdfTemplateService;

	@Autowired
	protected LoanerBaseInfoService loanerBaseInfoService;

	@Autowired
	protected LoanInfoItemService loanInfoItemService;

	@Autowired
	TradeFlowCodeManager tradeFlowCodeManager;

	@Autowired
	protected PointsRuleService pointsRuleService;

	@Autowired
	protected UserPointsByRuleTypeService userPointsByRuleTypeService;

	@Autowired
	protected DirectSendService directSendService;

	@Autowired
	protected SystemMessageService systemMessageService;

	@Autowired
	protected ExtraDAO extraDAO;
	@Autowired
	protected UserExtendDAO userExtendDAO;	
	@Autowired
	protected LotteryActivityQueryService lotteryActivityQueryService;
	@Autowired
	protected LotteryActivityService lotteryActivityService;
	@Autowired
	protected PrizeRuleService prizeRuleService;

	private Date nowDate = new Date();

	private Money staticMoney;

	private int staticCount;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		String[] nameArray = getDateInputNameArray();
		if (nameArray != null && nameArray.length > 0) {
			for (int i = 0; i < nameArray.length; i++) {
				binder.registerCustomEditor(Date.class, nameArray[i],
						new CustomDateEditor(new SimpleDateFormat(
								"yyyy-MM-dd HH:mm:ss"), true));

			}
		}
		String[] dateDayNameArray = getDateInputDayNameArray();
		if (dateDayNameArray != null && dateDayNameArray.length > 0) {
			for (int i = 0; i < dateDayNameArray.length; i++) {
				binder.registerCustomEditor(Date.class, dateDayNameArray[i],
						new CustomDateEditor(
								new SimpleDateFormat("yyyy-MM-dd"), true));

			}
		}
		String[] moneyNameArray = getMoneyInputNameArray();
		if (dateDayNameArray != null && moneyNameArray.length > 0) {
			for (int i = 0; i < moneyNameArray.length; i++) {
				binder.registerCustomEditor(Money.class, moneyNameArray[i],
						new CommonBindingInitializer());
			}
		}
	}

	protected String[] getDateInputNameArray() {
		return new String[0];
	}

	protected String[] getDateInputDayNameArray() {
		return new String[0];
	}

	protected String[] getMoneyInputNameArray() {
		return new String[0];
	}

	protected OpenApiContext getOpenApiContext() {
		OpenApiContext openApiContext = new OpenApiContext();
		openApiContext
				.setPartnerId(sysParameterService
						.getSysParameterValue(ApplicationConstant.SYS_PARAM_YJF_BUSINESS_USER_ID));
		openApiContext
				.setSecurityCheckKey(sysParameterService
						.getSysParameterValue(ApplicationConstant.SYS_PARAM_SECURITY_CHECK_KEY));
		openApiContext
				.setOpenApiUrl(sysParameterService
						.getSysParameterValue(ApplicationConstant.SYS_PARAM_OPEN_API_URL_KEY));

		openApiContext
				.setNotifyUrl(sysParameterService
						.getSysParameterValue(ApplicationConstant.SYS_PARAM_RETURN_URL_KEY));
		openApiContext.setOpenApiBizNo(BusinessNumberUtil.gainOutBizNoNumber());
		return openApiContext;
	}

	protected void printHttpResponse(HttpServletResponse response,
			JSONObject json) {
		try {

			response.setContentType("json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().println(json.toJSONString());
		} catch (IOException e) {
			logger.error("response. getWriter print error ", e);
		}
	}

	protected void infoStatistics(Model model) {
		QueryTradeDetailUserOrder queryTradeDetailUserOrder = new QueryTradeDetailUserOrder();
		Date beforeDate = DateUtil.getBeforeDay(new Date());
		Date startDate = DateUtil.getStartTimeOfTheDate(beforeDate);
		Date endDate = DateUtil.getEndTimeOfTheDate(beforeDate);
		queryTradeDetailUserOrder.setRoleEnum(SysUserRoleEnum.INVESTOR);
		queryTradeDetailUserOrder
				.setTransferPhase(DivisionPhaseEnum.ORIGINAL_PHASE);
		queryTradeDetailUserOrder.setStartDate(startDate);
		queryTradeDetailUserOrder.setEndDate(endDate);
		QueryBaseBatchResult<TradeDetailInfo> result = null;

		result = tradeBizQueryService
				.queryTradeDetailUser(queryTradeDetailUserOrder);
		Money totalAmount = new Money();
		for (TradeDetailInfo tt : result.getPageList()) {
			totalAmount.addTo(tt.getAmount());
		}
		this.nowDate = new Date();
		this.staticMoney = totalAmount;
		this.staticCount = result.getPageList().size();

		model.addAttribute("totalCount", staticCount);// 昨日成交数量
		model.addAttribute("totalAmount", staticMoney);// 昨日成交金额

		long totalUserCount = userStatisticsService
				.countUserByConditions(new HashMap<String, Object>());
		Map<String, Object> params = new HashMap<String, Object>();
		TradeStatisticsOrder statisticsOrder = new TradeStatisticsOrder();
		TradeStatisticsInfo statisticsInfo = indexTradeService
				.countTradeData(statisticsOrder);
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("isDivision", "Y");
		paramMap.put("roleId", SysUserRoleEnum.INVESTOR.getValue());
		TradeDetailStatisticsAmountVO statisticsAmountVO = extraDAO
				.searchTradeDetailCountQuery(paramMap);
		
		Map<String, Object> paramMap2 = new HashMap<String, Object>();
		paramMap2.put("isDivision", "Y");
		List<String> status = new ArrayList<String>();
		status.add("7");
		status.add("3");
		paramMap2.put("status", status);
		paramMap2.put("roleId", SysUserRoleEnum.INVESTOR.getValue());
		TradeDetailStatisticsAmountVO statisticsAmountVO2 = extraDAO
				.searchTradeDetailCountQuery(paramMap2);
		if (statisticsAmountVO2 != null) {
			String repayBenefitAmount=statisticsAmountVO2.getBenefitAmount()
					.toStandardString();
			model.addAttribute("repayBenefitAmount", repayBenefitAmount);
		}
		if (statisticsAmountVO != null) {// divideBy(10000)
			String benefitAmount = statisticsAmountVO.getBenefitAmount()
					.toStandardString();
			String benefitAmount2 = null;
			if (StringUtil.isNotEmpty(benefitAmount)) {
				benefitAmount2 = benefitAmount;
				benefitAmount = benefitAmount.substring(0,
						benefitAmount.indexOf("."));
			}
			// 收益金额
			if (MoneyUtil.getMoneyInt(statisticsAmountVO.getBenefitAmount()) > 100000000) {
				model.addAttribute(
						"benefitAmountBwy2",
						MoneyUtil.getMoneyInt(statisticsAmountVO
								.getBenefitAmount().divide(100000000))
								+ " 亿   "
								+ (MoneyUtil.getMoneyInt(statisticsAmountVO
										.getBenefitAmount().divide(10000)) - (MoneyUtil
										.getMoneyInt(statisticsAmountVO
												.getBenefitAmount().divide(
														100000000)) * 10000))
								+ "万");
			} else {
				model.addAttribute("benefitAmountBwy2", statisticsAmountVO
						.getBenefitAmount().divide(10000).toStandardString()
						+ "万");
			}
			model.addAttribute("benefitAmount2", benefitAmount2);
			model.addAttribute("benefitAmount", benefitAmount);
		}
		model.addAttribute("dealedAmount", statisticsInfo.getDealAmount()
				.toStandardString());
		if (MoneyUtil.getMoneyInt(statisticsInfo.getDealAmount()) > 100000000) {
			model.addAttribute(
					"dealedAmount1",
					MoneyUtil.getMoneyInt(statisticsInfo.getDealAmount()
							.divide(100000000))
							+ " 亿   "
							+ (MoneyUtil.getMoneyInt(statisticsInfo
									.getDealAmount().divide(10000)) - (MoneyUtil
									.getMoneyInt(statisticsInfo.getDealAmount()
											.divide(100000000)) * 10000)) + "万");
		} else {
			model.addAttribute("dealedAmount1", statisticsInfo.getDealAmount()
					.toStandardString());
		}
		model.addAttribute("repaidAmount", statisticsInfo.getRepayAmount()
				.toStandardString());
		model.addAttribute("repaidAmount1", (statisticsInfo.getRepayAmount()
				.add(statisticsAmountVO.getBenefitAmount())).toStandardString());// 算利息

		if (MoneyUtil.getMoneyInt(statisticsInfo.getDealAmount()) > 100000000) {
			model.addAttribute(
					"dealedAmountBwy",
					MoneyUtil.getMoneyInt(statisticsInfo.getDealAmount()
							.divide(100000000))
							+ " 亿   "
							+ (MoneyUtil.getMoneyInt(statisticsInfo
									.getDealAmount().divide(10000)) - (MoneyUtil
									.getMoneyInt(statisticsInfo.getDealAmount()
											.divide(100000000)) * 10000)) + "万");
		} else {
			model.addAttribute("dealedAmountBwy", statisticsInfo
					.getDealAmount().divide(10000).toStandardString()
					+ "万");
		}

		if (MoneyUtil.getMoneyInt(statisticsInfo.getRepayAmount()) > 100000000) {
			model.addAttribute(
					"repaidAmountBwy",
					MoneyUtil.getMoneyInt(statisticsInfo.getRepayAmount()
							.divide(100000000))
							+ " 亿   "
							+ (MoneyUtil.getMoneyInt(statisticsInfo
									.getRepayAmount().divide(10000)) - (MoneyUtil
									.getMoneyInt(statisticsInfo
											.getRepayAmount().divide(100000000)) * 10000))
							+ "万");
		} else {
			model.addAttribute("repaidAmountBwy", statisticsInfo
					.getRepayAmount().divide(10000).toStandardString()
					+ "万");
		}
		model.addAttribute("totalUserCount", totalUserCount);
		//房融通 保证金
		GuaranteeAmountInfo  guaranteeAmountInfo=tradeStatisticsQueryService.loanPlatformAllGuaranteeAmount();
		model.addAttribute("guaranteeAmount",guaranteeAmountInfo.getGuaranteeAmount().divide(10000).toStandardString());
		
	}

}
