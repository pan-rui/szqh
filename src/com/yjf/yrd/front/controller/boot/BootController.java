package com.yjf.yrd.front.controller.boot;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.yjf.common.lang.util.ListUtil;
import com.yjf.yrd.integration.web.server.impl.YjfEzmoneyCheckPaytkOrder;
import com.yjf.yrd.service.query.lottery.LotteryActivityQueryService;
import com.yjf.yrd.service.query.lottery.PrizeRuleQueryService;
import com.yjf.yrd.ws.enums.*;
import com.yjf.yrd.ws.info.*;
import com.yjf.yrd.ws.marketing.enums.LotteryActivityStatusEnum;
import com.yjf.yrd.ws.marketing.enums.LotteryTypeEnum;
import com.yjf.yrd.ws.marketing.info.LotteryActivityInfo;
import com.yjf.yrd.ws.marketing.info.LotteryWinnerInfo;
import com.yjf.yrd.ws.marketing.info.PrizeRuleDetailInfo;
import com.yjf.yrd.ws.marketing.order.DrawAwardOrder;
import com.yjf.yrd.ws.marketing.order.PrizeRuleDetailOrder;
import com.yjf.yrd.ws.marketing.query.order.LotteryActivityQueryOrder;
import com.yjf.yrd.ws.marketing.query.order.LotteryWinnerQueryOrder;
import com.yjf.yrd.ws.marketing.query.result.LotteryResult;
import com.yjf.yrd.ws.marketing.result.DrawAwardResult;
import com.yjf.yrd.ws.marketing.services.LotteryActivityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.yjf.common.lang.util.StringUtil;
import com.yjf.common.lang.util.money.Money;
import com.yjf.yrd.common.result.SmsCodeResult;
import com.yjf.yrd.dataobject.viewObject.TradeDetailStatisticsAmountVO;
import com.yjf.yrd.front.controller.trade.base.LoanTradeDetailBaseController;
import com.yjf.yrd.integration.openapi.result.UseGiftMoneyResult;
import com.yjf.yrd.integration.web.server.YjfLoginWebServer;
import com.yjf.yrd.integration.web.server.impl.YjfEzmoneyPayPassUrlOrder;
import com.yjf.yrd.page.PageParam;
import com.yjf.yrd.service.query.giftMoney.GiftMoneyTradeQueryService;
import com.yjf.yrd.service.query.trade.DebtTransferBizQueryService;
import com.yjf.yrd.service.query.trade.TradeBizQueryService;
import com.yjf.yrd.session.SessionLocal;
import com.yjf.yrd.session.SessionLocalManager;
import com.yjf.yrd.user.LoanerBaseInfoService;
import com.yjf.yrd.user.info.UserInfo;
import com.yjf.yrd.util.AppConstantsUtil;
import com.yjf.yrd.util.CommonUtil;
import com.yjf.yrd.util.MoneyUtil;
import com.yjf.yrd.web.util.AttachmentModuleType;
import com.yjf.yrd.web.util.PageUtil;
import com.yjf.yrd.web.util.YrdEnumUtil;
import com.yjf.yrd.ws.order.CommonAttachmentQueryOrder;
import com.yjf.yrd.ws.order.DebtTransferOrder;
import com.yjf.yrd.ws.result.YrdBaseResult;
import com.yjf.yrd.ws.service.CommonAttachmentService;
import com.yjf.yrd.ws.service.DebtTransferBizProcessService;
import com.yjf.yrd.ws.service.LoanDemandExtendService;
import com.yjf.yrd.ws.service.query.order.DebtTransferQueryOrder;
import com.yjf.yrd.ws.service.query.order.IndexQueryOrder;
import com.yjf.yrd.ws.service.query.order.QueryLoanTradeOrder;
import com.yjf.yrd.ws.service.query.result.QueryBaseBatchResult;
import com.yjf.yrd.ws.service.query.result.QueryDebtTransferDetailResult;
import com.yjf.yrd.ws.statistics.TradeStatisticsInfo;
import com.yjf.yrd.ws.statistics.order.TradeStatisticsOrder;

/**
 * 
 * 
 * @Filename BootController.java
 * 
 * @Description
 * 
 * @Version 1.0
 * 
 * @Author yhl
 * 
 * @Email yhailong@yiji.com
 * 
 * @History <li>Author: yhl</li> <li>Date: 2013-6-20</li> <li>Version: 1.0</li>
 * <li>Content: create</li> 引导控制器
 */
@Controller
@RequestMapping("boot")
public class BootController extends LoanTradeDetailBaseController {
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private TradeBizQueryService tradeBizQueryService;
	@Autowired
	private DebtTransferBizQueryService debtTransferBizQueryService;
	@Autowired
	private DebtTransferBizProcessService debtTransferBizProcessService;
	@Autowired
	CommonAttachmentService commonAttachmentService;

	@Autowired
	YjfLoginWebServer yjfLoginWebServer;
	
	@Autowired
	LoanerBaseInfoService loanerBaseInfoService;
	
	@Autowired
	GiftMoneyTradeQueryService giftMoneyTradeQueryService;
	
	@Autowired
	LoanDemandExtendService loanDemandExtendService;

    @Autowired
    LotteryActivityQueryService lotteryActivityQueryService;

    @Autowired
    PrizeRuleQueryService prizeRuleQueryService;

    @Autowired
    LotteryActivityService lotteryActivityService;
	
	/**
	 * 前段首页
	 * @return
	 */
	@RequestMapping("index/{size}/{page}")
	public String index(@PathVariable int size, @PathVariable int page, Integer status,
						String guarantee, Model model) {
		QueryLoanTradeOrder tradeQueryOrder = new QueryLoanTradeOrder();
		tradeQueryOrder.setPageNumber(page);
		tradeQueryOrder.setPageSize(size);
		List<String> statusList = new ArrayList<String>();
		intiStatusCond(status, statusList);
		tradeQueryOrder.setStatus(statusList);
		tradeQueryOrder.setGuaranteeName(guarantee);
		
		QueryBaseBatchResult<LoanDemandTradeVOInfo> batchResult = tradeBizQueryService
			.searchLoanTradeCommonQuery(tradeQueryOrder);
		
		model.addAttribute("page", PageUtil.getCovertPage(batchResult));
		model.addAttribute("status", status);
		return "front/index/index.vm";
	}
	
	private void intiStatusCond(Integer status, List<String> statusList) {
		if (status != null && status > 0) {
			if (status == 1) {
				statusList.add(String.valueOf(TradeStatusEnum.TRADING.getValue()));
				statusList.add(String.valueOf(TradeStatusEnum.REPAYING.getValue()));
				statusList.add(String.valueOf(TradeStatusEnum.GUARANTEE_AUDITING.getValue()));
				statusList.add(String.valueOf(TradeStatusEnum.REPAY_FINISH.getValue()));
				statusList
					.add(String.valueOf(TradeStatusEnum.COMPENSATORY_REPAY_FINISH.getValue()));
			} else {
				statusList.add(String.valueOf(TradeStatusEnum.REPAY_FINISH.getValue()));
				statusList
					.add(String.valueOf(TradeStatusEnum.COMPENSATORY_REPAY_FINISH.getValue()));
			}
		} else {
			statusList.add(String.valueOf(TradeStatusEnum.REPAY_FINISH.getValue()));
			statusList.add(String.valueOf(TradeStatusEnum.COMPENSATORY_REPAY_FINISH.getValue()));
			
		}
	}
	
	/**
	 * 搜索借款需求
	 * @param size
	 * @param page
	 * @param timeUnit
	 * @param startTime
	 * @param endTime
	 * @param startAmount
	 * @param endAmount
	 * @param startRate
	 * @param endRate
	 * @param model
	 * @return
	 */
	@RequestMapping("invest/{size}/{page}")
	public String invest(@PathVariable int size, @PathVariable int page, String guarantee,
							String timeUnit, Integer startTime, Integer endTime, Long startAmount,
							Long endAmount, String startRate, String endRate, Integer loanPeriod,Integer minLoanPeriod,Integer maxLoanPeriod,
							String leastInvestAmount, Model model, String insureWay,String bizType,String areaCode,
							String tradeStatus,String bankingBizTypeEnum,HttpServletRequest request) {

		this.initAccountInfo(model);
		QueryLoanTradeOrder tradeQueryOrder = new QueryLoanTradeOrder();
		tradeQueryOrder.setPageNumber(page);
		tradeQueryOrder.setPageSize(size);
		if (loanPeriod == null) {
			tradeQueryOrder.setLoanPeriod(0);
		} else {
			tradeQueryOrder.setLoanPeriod(loanPeriod);
		}
		if (minLoanPeriod == null) {
			tradeQueryOrder.setMinLoanPeriod(0);
		} else {
			tradeQueryOrder.setMinLoanPeriod(minLoanPeriod);
		}
		if (maxLoanPeriod == null) {
			tradeQueryOrder.setMaxLoanPeriod(0);
		} else {
			tradeQueryOrder.setMaxLoanPeriod(maxLoanPeriod);
		}
		if (startAmount != null) {
			tradeQueryOrder.setMinLoanAmount(Money.cent(startAmount));
		}
		
		if (endAmount != null) {
			tradeQueryOrder.setMaxLoanAmount(Money.cent(endAmount));
		}
		
		tradeQueryOrder.setInsureWay(insureWay);
		if(com.yjf.yrd.util.StringUtil.isNotEmpty(areaCode)){
			tradeQueryOrder.setAreaCode(areaCode);
		}
		List<String> statusList = new ArrayList<String>();
		if (com.yjf.yrd.util.StringUtil.isNotEmpty(tradeStatus)) {
			String[] str = tradeStatus.split(",");
			for (int i = 0; i < str.length; i++) {
				statusList.add(str[i]);
			}
		} else {
			statusList.add(String.valueOf(TradeStatusEnum.TRADING.getValue()));
			statusList.add(String.valueOf(TradeStatusEnum.REPAYING.getValue()));
			statusList.add(String.valueOf(TradeStatusEnum.GUARANTEE_AUDITING.getValue()));
			statusList.add(String.valueOf(TradeStatusEnum.REPAY_FINISH.getValue()));
			statusList.add(String.valueOf(TradeStatusEnum.REPAYING_FAILD.getValue()));
			statusList.add(String.valueOf(TradeStatusEnum.COMPENSATORY_REPAY_FINISH.getValue()));
			statusList.add(String.valueOf(TradeStatusEnum.DOREPAY.getValue()));
			
		}
		
		String[] moneys = null;
		if (StringUtil.isNotEmpty(leastInvestAmount)) {
			moneys = leastInvestAmount.split("-");
			if (moneys != null && moneys.length > 0) {
				tradeQueryOrder.setMinLeastInvestAmount(Money.cent(Long.valueOf(moneys[0])));
				if (moneys.length > 1) {
					tradeQueryOrder.setMaxLeastInvestAmount(Money.cent(Long.valueOf(moneys[1])));
				}
			}
		}
		
		tradeQueryOrder.setStatus(statusList);
		tradeQueryOrder.setGuaranteeName(guarantee);
		tradeQueryOrder.setInterestRateBegin(startRate);
		tradeQueryOrder.setInterestRateEnd(endRate);
        tradeQueryOrder.setBankingBizTypeEnum(bankingBizTypeEnum);
        if(com.yjf.yrd.util.StringUtil.isNotEmpty(bizType)){
        	tradeQueryOrder.setBizType(bizType);
        }
		QueryBaseBatchResult<LoanDemandTradeVOInfo> batchResult = tradeBizQueryService
			.searchLoanTradeCommonQuery(tradeQueryOrder);
		model.addAttribute("leastInvestAmount", leastInvestAmount);
		model.addAttribute("loanPeriod", loanPeriod);
		model.addAttribute("startAmount", startAmount);
		model.addAttribute("endAmount", endAmount);
		model.addAttribute("insureWay", insureWay);
		model.addAttribute("bizType", bizType);
		model.addAttribute("minLoanPeriod", minLoanPeriod);
		model.addAttribute("maxLoanPeriod", maxLoanPeriod);
		model.addAttribute("areaCode", areaCode);
		model.addAttribute("startRate", startRate);
		model.addAttribute("endRate", endRate);
		model.addAttribute("tradeStatus", tradeStatus);
		model.addAttribute("currentDate", new Date());
        model.addAttribute("bankingBizTypeEnum",bankingBizTypeEnum);
		model.addAttribute("page", PageUtil.getCovertPage(batchResult));
		infoStatistics(model);
        String returnUrl = request.getParameter("returnPage");
        if(StringUtil.isNotBlank(returnUrl)){
            model.addAttribute("returnPage","investDetail");
           return "front/index/" +returnUrl+".vm";
        }
		return "front/index/index_invest.vm";
	}
	
	@ResponseBody
	@RequestMapping("queryUserGiftMoney")
	public JSONObject queryUserGiftMoney(long demandId, String investAmount) {
		JSONObject jsonObject = new JSONObject();
		long userId = SessionLocalManager.getSessionLocal().getUserId();
		Money giftMoney = giftMoneyTradeQueryService.queryGiftMoneyAmountUserCanUse(demandId,
			investAmount, userId, GiftMoneyTypeEnum.GIFT_MONEY);
		
		UseGiftMoneyResult result = giftMoneyTradeQueryService.queryGiftMoneyLimitAmount(demandId,
			Money.amout(investAmount), GiftMoneyTypeEnum.GIFT_MONEY);
		if (result.isSuccess()) {
			Money limitAmount = result.getAmount();
			if ((limitAmount == null && giftMoney.greaterThan(Money.zero()))) {
				jsonObject.put("giftMoney", giftMoney);
			}
			
			if (limitAmount != null && limitAmount.greaterThan(Money.zero())
				&& giftMoney.greaterThan(Money.zero())) {
				jsonObject.put("giftMoney", giftMoney);
			}
			
			if (limitAmount != null && limitAmount.greaterThan(Money.zero())) {
				jsonObject.put("giftMoneyLimitAmount", limitAmount);
			}
			
		}
		
		Money experienceAmount = giftMoneyTradeQueryService.queryGiftMoneyAmountUserCanUse(
			demandId, investAmount, userId, GiftMoneyTypeEnum.EXPERIENCE_AMOUNT);
		if (experienceAmount.greaterThan(Money.zero())) {
			jsonObject.put("experienceAmount", experienceAmount);
		}
		
		return jsonObject;
	}
	
	/**
	 * 进入我要借款页面
	 */
	@RequestMapping("applyLoan")
	public String applyLoan(Model model, HttpSession session) {
		
		return "front/trade/loan/loan_apply.vm";
	}
	
	@RequestMapping("transfer/{size}/{page}")
	public String transfer(@PathVariable int size, @PathVariable int page, Model model) {
		
		this.initAccountInfo(model);
		List<DebtTransferStatus> statusList = new ArrayList<DebtTransferStatus>();
		statusList.add(DebtTransferStatus.TRANSFERRING);
        statusList.add(DebtTransferStatus.SUCCESS);
		DebtTransferQueryOrder transferQueryOrder = new DebtTransferQueryOrder();
		transferQueryOrder.setPageNumber(page);
		transferQueryOrder.setPageSize(size);
		transferQueryOrder.setStatusList(statusList);
		QueryBaseBatchResult<TradeDetailTransferInfo> batchResult = debtTransferBizQueryService
			.queryDebtTransfer(transferQueryOrder);
		
		model.addAttribute("page", PageUtil.getCovertPage(batchResult));
		
		return "front/index/index_transfer.vm";
	}

    @RequestMapping("drawAward")
    public  String drawAward(HttpServletRequest request,Model model){
         long activityId =Long.valueOf(request.getParameter("activityId"));
        LotteryResult result = lotteryActivityService.getStartedLotteryActivity(activityId);
        LotteryActivityInfo activityInfo =result.getLotteryActivityInfo();
        if(activityInfo != null){
            List<PrizeRuleDetailInfo> ruleDetails = prizeRuleQueryService.findByPrizeRuleId(activityInfo.getPrizeRuleId()).getPrizeRuleDetailInfos();
            model.addAttribute("ruleDetails",ruleDetails);
            model.addAttribute("lotteryActivity",activityInfo);
        }
        LotteryWinnerQueryOrder lotteryWinnerQueryOrder = new LotteryWinnerQueryOrder();
        List<Long> activityIds = new ArrayList<Long>();
        activityIds.add(activityId);
        lotteryWinnerQueryOrder.setActivityIdList(activityIds);
        lotteryWinnerQueryOrder.setPageSize(200);
        lotteryWinnerQueryOrder.setPageNumber(1);
        QueryBaseBatchResult<LotteryWinnerInfo> winnerResult = lotteryActivityQueryService.queryLotteryWinner(lotteryWinnerQueryOrder);
        List<LotteryWinnerInfo> winners = winnerResult.getPageList();
        model.addAttribute("winners",winners);

        return "front/lotteryActivity/lotteryActivity.vm";
    }



    @ResponseBody
    @RequestMapping("drawAwardSubmit")
    public  Object drawAwardSubmit(HttpServletRequest request,Model model){
        JSONObject jsonObject = new JSONObject();
        if(SessionLocalManager.getSessionLocal() == null){
            jsonObject.put("code",0);
            jsonObject.put("message","亲！请先登录再抽奖^_^");
            return jsonObject;
        }

        if(StringUtil.isEmpty(request.getParameter("lotteryActivityInstanceId"))){
            jsonObject.put("code",0);
            jsonObject.put("message","亲！抽奖还没有开始，请耐心等待^_^");
            return jsonObject;
        }


        long lotteryActivityInstanceId =Long.valueOf(request.getParameter("lotteryActivityInstanceId"));

        if(lotteryActivityInstanceId ==0){
            jsonObject.put("code",0);
            jsonObject.put("message","亲！抽奖还没有开始，请耐心等待^_^");
            return jsonObject;
        }

        DrawAwardOrder awardOrder = new DrawAwardOrder();
        awardOrder.setLotteryActivityInstanceId(lotteryActivityInstanceId);
        awardOrder.setBizNo(lotteryActivityInstanceId);
        awardOrder.setProcessorId(SessionLocalManager.getSessionLocal().getUserId());
        DrawAwardResult result =  lotteryActivityService.drawAward(awardOrder);
        if(result.isSuccess()){
            jsonObject.put("code",1);
            jsonObject.put("isWinner",result.getIsWinner());
            jsonObject.put("message",result.getWinnerDesc());
        }else{
            jsonObject.put("code",1);
            jsonObject.put("message",result.getMessage());
        }

        return jsonObject;
    }






	
	@RequestMapping("newTransfer/{size}/{page}")
	public String newTransfer(@PathVariable int size, @PathVariable int page, Model model) {
		
		this.initAccountInfo(model);
		List<DebtTransferStatus> statusList = new ArrayList<DebtTransferStatus>();
		statusList.add(DebtTransferStatus.TRANSFERRING);
        statusList.add(DebtTransferStatus.SUCCESS);
		DebtTransferQueryOrder transferQueryOrder = new DebtTransferQueryOrder();
		transferQueryOrder.setPageNumber(page);
		transferQueryOrder.setPageSize(size);
		transferQueryOrder.setStatusList(statusList);
		QueryBaseBatchResult<TradeDetailTransferInfo> batchResult = debtTransferBizQueryService
			.queryDebtTransfer(transferQueryOrder);
		
		model.addAttribute("page", PageUtil.getCovertPage(batchResult));
		
		return "front/index/common/new_entries.vm";
	}
	
	/**
	 * 查看借款需求详情
	 * @param demandId
	 * @param tradeId
	 * @param session
	 * @param model
	 * @param pageParam
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("lookup/{demandId},{tradeId}")
	public String lookup(@PathVariable long demandId, @PathVariable long tradeId,
							HttpSession session, Model model, PageParam pageParam,
							HttpServletRequest request) throws Exception {
		SessionLocal sessionLocal = SessionLocalManager.getSessionLocal();
		if (sessionLocal != null && sessionLocal.getAccountId() != null) {
			this.initAccountInfo(model);
			if (AppConstantsUtil.canTradeUsePayPassword()) {
				YjfEzmoneyPayPassUrlOrder ezmoneyPayPassUrlOrder = new YjfEzmoneyPayPassUrlOrder();
				ezmoneyPayPassUrlOrder.setUserId(sessionLocal.getAccountId());
				YrdBaseResult baseResult = yjfLoginWebServer.gotoYjfValidatePayPasswordUrl(
					ezmoneyPayPassUrlOrder, this.getOpenApiContext());
				model.addAttribute("yjfPaypassUrl", baseResult.getUrl());
			}
			
		}
		String token = UUID.randomUUID().toString();
		getTradeDetailPageView(demandId, 0, model, session, SysUserRoleEnum.INVESTOR, request,
			pageParam);
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
		
		//项目图片
		CommonAttachmentQueryOrder attachmentQueryOrder1 = new CommonAttachmentQueryOrder();
		attachmentQueryOrder1.setBizNo(demandId + "");
		attachmentQueryOrder1.setIsort(10);
		QueryBaseBatchResult<CommonAttachmentInfo> batchResult1 = commonAttachmentService
			.queryCommonAttachment(attachmentQueryOrder1);
		if (batchResult1.getPageList().size() > 0) {
			model.addAttribute("imgurl", batchResult1.getPageList().get(0).getRequestPath());
		}
		//end
		
		/** 借款人的信息查询 ******/
		if ("Y".equals(AppConstantsUtil.getAddLoanerBaseInfo())) {
			LoanDemandInfo loanDemand = (LoanDemandInfo) model.asMap().get("loanDemand");
			if (loanDemand != null) {
				LoanerBaseInfo loanerBaseInfo = loanerBaseInfoService.findByUserId(loanDemand
					.getLoanerId());
				model.addAttribute("loanerBaseInfo", loanerBaseInfo);
			} else {
				logger.error("loanDemand 为空demandId=" + demandId);
			}
			
		}
		
		session.setAttribute("token", token);
		String tab = request.getParameter("tab");
		model.addAttribute("tab", tab); //分页跳转后显示正确的tab页面
		Object giftMoney = session.getAttribute("giftMoney");
		if (giftMoney != null && StringUtil.isNotEmpty((String) giftMoney)) {
			model.addAttribute("giftMoney", giftMoney);
			session.removeAttribute("giftMoney");
		}
        String returnUrl = request.getParameter("returnPage");
        if(StringUtil.isNotBlank(returnUrl)){
            return "front/index/" +returnUrl+".vm";
        }
		return "front/index/index_invest_detail.vm";
	}


    @RequestMapping("movieLookup/{demandId},{tradeId}")
    public String movieLookup(@PathVariable long demandId, @PathVariable long tradeId,
                         HttpSession session, Model model, PageParam pageParam,
                         HttpServletRequest request) throws Exception {
        SessionLocal sessionLocal = SessionLocalManager.getSessionLocal();
        if (sessionLocal != null && sessionLocal.getAccountId() != null) {
            this.initAccountInfo(model);
            if (AppConstantsUtil.canTradeUsePayPassword()) {
                YjfEzmoneyPayPassUrlOrder ezmoneyPayPassUrlOrder = new YjfEzmoneyPayPassUrlOrder();
                ezmoneyPayPassUrlOrder.setUserId(sessionLocal.getAccountId());
                YrdBaseResult baseResult = yjfLoginWebServer.gotoYjfValidatePayPasswordUrl(
                        ezmoneyPayPassUrlOrder, this.getOpenApiContext());
                model.addAttribute("yjfPaypassUrl", baseResult.getUrl());
            }

        }
        String token = UUID.randomUUID().toString();
        getTradeDetailPageView(demandId, 0, model, session, SysUserRoleEnum.INVESTOR, request,
                pageParam);
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

        //项目图片
        CommonAttachmentQueryOrder attachmentQueryOrder1 = new CommonAttachmentQueryOrder();
        attachmentQueryOrder1.setBizNo(demandId + "");
        attachmentQueryOrder1.setIsort(10);
        QueryBaseBatchResult<CommonAttachmentInfo> batchResult1 = commonAttachmentService
                .queryCommonAttachment(attachmentQueryOrder1);
        if (batchResult1.getPageList().size() > 0) {
            model.addAttribute("imgurl", batchResult1.getPageList().get(0).getRequestPath());
        }
        //end

        /** 借款人的信息查询 ******/
        if ("Y".equals(AppConstantsUtil.getAddLoanerBaseInfo())) {
            LoanDemandInfo loanDemand = (LoanDemandInfo) model.asMap().get("loanDemand");
            if (loanDemand != null) {
                LoanerBaseInfo loanerBaseInfo = loanerBaseInfoService.findByUserId(loanDemand
                        .getLoanerId());
                model.addAttribute("loanerBaseInfo", loanerBaseInfo);
            } else {
                logger.error("loanDemand 为空demandId=" + demandId);
            }

        }

        session.setAttribute("token", token);
        String tab = request.getParameter("tab");
        model.addAttribute("tab", tab); //分页跳转后显示正确的tab页面
        Object giftMoney = session.getAttribute("giftMoney");
        if (giftMoney != null && StringUtil.isNotEmpty((String) giftMoney)) {
            model.addAttribute("giftMoney", giftMoney);
            session.removeAttribute("giftMoney");
        }

        LoanDemandExtendInfo extendInfo = loanDemandExtendService.findByDemandIdAndProperty(demandId, LoanDemandExpendEnum.MOVIE_IMG);
        if(extendInfo != null){
            model.addAttribute("movie",extendInfo.getPropertyValue());
        }
        return "front/index/index_movie_detail.vm";
    }

	/**
	 * 查看需求详情
	 * @param tradeTransferId
	 * @param session
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("lookupTransfer/{tradeTransferId}")
	public String lookupT(@PathVariable long tradeTransferId, HttpSession session, Model model)
																								throws Exception {
		SessionLocal sessionLocal = SessionLocalManager.getSessionLocal();
		if (sessionLocal != null && sessionLocal.getAccountId() != null) {
			this.initAccountInfo(model);
            YjfEzmoneyPayPassUrlOrder ezmoneyPayPassUrlOrder = new YjfEzmoneyPayPassUrlOrder();
            ezmoneyPayPassUrlOrder.setUserId(sessionLocal.getAccountId());
            YrdBaseResult baseResult = yjfLoginWebServer.gotoYjfValidatePayPasswordUrl(
                    ezmoneyPayPassUrlOrder, this.getOpenApiContext());

            model.addAttribute("yjfPaypassUrl", baseResult.getUrl());
		}
		String token = UUID.randomUUID().toString();
		QueryDebtTransferDetailResult result = debtTransferBizQueryService
			.queryDebtTransferDetailInfo(tradeTransferId);

		session.setAttribute("token", token);

		model.addAttribute("trade", result.getDebtTransferDetailInfo());
		model.addAttribute("loanDemand", result.getDebtTransferDetailInfo().getLoanDemandInfo());
		return "front/index/index_transfer_detail.vm";
	}
	
	@RequestMapping("getNotify")
	public String getNotify() {
		return "/test/notify.html";
	}
	
	/**
	 * 前段首页
	 * @return
	 */
	@RequestMapping("staticIndex/{size}/{page}")
	public String staticIndex(@PathVariable int size, @PathVariable int page,
								IndexQueryOrder queryOrder, Model model) {
		
		Integer status = queryOrder.getStatus();
		String guarantee = queryOrder.getGuarantee();
		
		QueryLoanTradeOrder tradeQueryOrder = new QueryLoanTradeOrder();
		tradeQueryOrder.setPageNumber(page);
		tradeQueryOrder.setPageSize(size);
		List<String> statusList = new ArrayList<String>();
		intiStatusCond(status, statusList);
		tradeQueryOrder.setStatus(statusList);
		tradeQueryOrder.setGuaranteeName(guarantee);
		tradeQueryOrder.setInterestRateBegin(queryOrder.getInterestRateBegin());
		tradeQueryOrder.setInterestRateEnd(queryOrder.getInterestRateEnd());
		tradeQueryOrder.setAreaCode(queryOrder.getAreaCode());
		tradeQueryOrder.setInsureWay(queryOrder.getInsureWay());
		tradeQueryOrder.setBizType(queryOrder.getBizType());
        tradeQueryOrder.setBankingBizTypeEnum(queryOrder.getBankingBizTypeEnum());
		QueryBaseBatchResult<LoanDemandTradeVOInfo> batchResult = tradeBizQueryService
			.searchLoanTradeCommonQuery(tradeQueryOrder);
		for (LoanDemandTradeVOInfo i : batchResult.getPageList()) {
			
			//项目图片
			
			CommonAttachmentQueryOrder attachmentQueryOrder1 = new CommonAttachmentQueryOrder();
			attachmentQueryOrder1.setBizNo(i.getDemandId() + "");
			attachmentQueryOrder1.setIsort(10);
			QueryBaseBatchResult<CommonAttachmentInfo> batchResult1 = commonAttachmentService
				.queryCommonAttachment(attachmentQueryOrder1);
			if (batchResult1.getPageList().size() > 0) {
				i.setLoanDemandImgUrl(batchResult1.getPageList().get(0).getRequestPath());
			}
			
		}
		
		//房融通专用
		if(com.yjf.yrd.util.StringUtil.isNotEmpty(queryOrder.getBizType())&&AppConstantsUtil.isPayGuaranteeAmount()){
			 if(batchResult.getTotalCount()==0){
					QueryLoanTradeOrder tradeQueryOrder2 = new QueryLoanTradeOrder();
					tradeQueryOrder2.setPageNumber(page);
					tradeQueryOrder2.setPageSize(size);
					tradeQueryOrder2.setStatus(statusList);
					tradeQueryOrder2.setGuaranteeName(guarantee);
					tradeQueryOrder2.setInterestRateBegin(queryOrder.getInterestRateBegin());
					tradeQueryOrder2.setInterestRateEnd(queryOrder.getInterestRateEnd());
					tradeQueryOrder2.setAreaCode(queryOrder.getAreaCode());
					tradeQueryOrder2.setInsureWay(queryOrder.getInsureWay());
//					tradeQueryOrder2.setBizType(null);
			        tradeQueryOrder2.setBankingBizTypeEnum(queryOrder.getBankingBizTypeEnum());
					QueryBaseBatchResult<LoanDemandTradeVOInfo> batchResult2 = tradeBizQueryService
							.searchLoanTradeCommonQuery(tradeQueryOrder2);
										
					model.addAttribute("page", PageUtil.getCovertPage(batchResult2));
			 }else{
				 model.addAttribute("page", PageUtil.getCovertPage(batchResult));
			 }
			
		}else{
			model.addAttribute("page", PageUtil.getCovertPage(batchResult));
		}
		//end
		model.addAttribute("status", status);
		model.addAttribute("queryOrder", queryOrder);
		model.addAttribute("currentDate", new Date());
		return "front/index/common/entries.vm";
	}
	
	/**
	 * 前段首页 更多
	 * @return
	 */
	@RequestMapping("moreIndex/{size}/{page}")
	public String moreIndex(@PathVariable int size, @PathVariable int page,
							IndexQueryOrder queryOrder, Model model) {
		Integer status = queryOrder.getStatus();
		String guarantee = queryOrder.getGuarantee();
		QueryLoanTradeOrder tradeQueryOrder = new QueryLoanTradeOrder();
		tradeQueryOrder.setPageNumber(page);
		tradeQueryOrder.setPageSize(size);
		List<String> statusList = new ArrayList<String>();
		intiStatusCond(status, statusList);
		tradeQueryOrder.setStatus(statusList);
		tradeQueryOrder.setGuaranteeName(guarantee);
		tradeQueryOrder.setInterestRateBegin(queryOrder.getInterestRateBegin());
		tradeQueryOrder.setInterestRateEnd(queryOrder.getInterestRateEnd());
		this.initAccountInfo(model);
		QueryBaseBatchResult<LoanDemandTradeVOInfo> batchResult = tradeBizQueryService
			.searchLoanTradeCommonQuery(tradeQueryOrder);
		
		model.addAttribute("page", PageUtil.getCovertPage(batchResult));
		model.addAttribute("status", status);
		model.addAttribute("queryOrder", queryOrder);
		return "front/index/index_more.vm";
	}
	
    @ResponseBody
    @RequestMapping("refreshAmount.htm")
    public Object refreshAmount(Model model) {
        JSONObject jsonobj = new JSONObject();
		TradeStatisticsOrder statisticsOrder = new TradeStatisticsOrder();
		TradeStatisticsInfo statisticsInfo = indexTradeService
				.countTradeData(statisticsOrder);
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("isDivision", "Y");
		paramMap.put("roleId", SysUserRoleEnum.INVESTOR.getValue());
		TradeDetailStatisticsAmountVO statisticsAmountVO = extraDAO
				.searchTradeDetailCountQuery(paramMap);
        String dealedAmount = statisticsInfo.getDealAmount().toStandardString();
        String repaidAmount = (statisticsInfo.getRepayAmount().add(statisticsAmountVO.getBenefitAmount())).toStandardString();//算利息了
        jsonobj.put("dealedAmount", dealedAmount.substring(0, dealedAmount.lastIndexOf(".") + 1));
        jsonobj.put("repaidAmount", repaidAmount.substring(0, repaidAmount.lastIndexOf(".") + 1));
        jsonobj.put("dealedSupAmount", dealedAmount.substring(dealedAmount.lastIndexOf(".") + 1));
        jsonobj.put("repaidSupAmount", repaidAmount.substring(repaidAmount.lastIndexOf(".") + 1));
        return jsonobj;
    }

	
	@ResponseBody
	@RequestMapping("investTransfer")
	public Object investTransfer(DebtTransferOrder debtTransferOrder, HttpSession session,
									String token, String paytk) throws Exception {
		JSONObject jsonobj = new JSONObject();

        YjfEzmoneyCheckPaytkOrder ezmoneyPayPassUrlOrder = new YjfEzmoneyCheckPaytkOrder();
        ezmoneyPayPassUrlOrder.setUserId(SessionLocalManager.getSessionLocal().getAccountId());
        ezmoneyPayPassUrlOrder.setPaytk(paytk);
        YrdBaseResult paytkResult = yjfLoginWebServer.gotoYjfCheckPayTk(
                ezmoneyPayPassUrlOrder, this.getOpenApiContext());
        if (!paytkResult.isSuccess()) {
            jsonobj.put("status", false);
            jsonobj.put("message", "支付令牌错误");
            return jsonobj;
        }

		String getToken = (String) session.getAttribute("token");
		debtTransferOrder.setRecipientId(SessionLocalManager.getSessionLocal().getUserId());
		if (StringUtil.equals(getToken, token)) {
			YrdBaseResult result = debtTransferBizProcessService.transferDebt(debtTransferOrder);
			if (result.isSuccess()) {
				jsonobj.put("code", 1);
				jsonobj.put("message", "投资成功");
			} else {
				jsonobj.put("code", 0);
				jsonobj.put("message", result.getMessage());
			}
		} else {
			jsonobj.put("code", 0);
			jsonobj.put("message", "投资失败，重复提交！");
		}
		return jsonobj;
	}
	
	@ResponseBody
	@RequestMapping("currentTime.htm")
	public Object currentTime(Model model) {
		JSONObject jsonobj = new JSONObject();
		try {
			Date current = new Date();
			DateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			String timeStr = format.format(current);
			Long timeLong = current.getTime();
			jsonobj.put("code", 1);
			jsonobj.put("message", "执行成功！");
			jsonobj.put("TimeStr", timeStr);
			jsonobj.put("TimeLong", timeLong);
		} catch (Exception e) {
			jsonobj.put("code", 0);
			jsonobj.put("message", "执行失败！");
		}
		return jsonobj;
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
