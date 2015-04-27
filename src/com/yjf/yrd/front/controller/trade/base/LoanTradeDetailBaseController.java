package com.yjf.yrd.front.controller.trade.base;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.yjf.common.lang.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;

import com.yjf.common.lang.util.ListUtil;
import com.yjf.common.lang.util.money.Money;
import com.yjf.yrd.base.UserAccountInfoBaseController;
import com.yjf.yrd.dal.dataobject.InstitutionsInfoDO;
import com.yjf.yrd.dal.dataobject.LoanerBaseInfoDO;
import com.yjf.yrd.dal.dataobject.UserBaseInfoDO;
import com.yjf.yrd.dataobject.OperatorInfoDO;
import com.yjf.yrd.integration.openapi.info.YjfAccountInfo;
import com.yjf.yrd.page.PageParam;
import com.yjf.yrd.service.query.trade.DivisionDetailQueryService;
import com.yjf.yrd.session.SessionLocal;
import com.yjf.yrd.session.SessionLocalManager;
import com.yjf.yrd.user.IOperatorInfoService;
import com.yjf.yrd.user.info.InstitutionsInfo;
import com.yjf.yrd.user.info.UserInfo;
import com.yjf.yrd.util.DateUtil;
import com.yjf.yrd.web.util.PageUtil;
import com.yjf.yrd.web.util.RateUtil;
import com.yjf.yrd.ws.enums.DivisionPhaseEnum;
import com.yjf.yrd.ws.enums.LoanDemandExpendEnum;
import com.yjf.yrd.ws.enums.SysUserRoleEnum;
import com.yjf.yrd.ws.enums.TradeStatusEnum;
import com.yjf.yrd.ws.enums.UserTypeEnum;
import com.yjf.yrd.ws.enums.YrdAuthTypeEnum;
import com.yjf.yrd.ws.info.LoanAuthRecordInfo;
import com.yjf.yrd.ws.info.LoanDemandExtendInfo;
import com.yjf.yrd.ws.info.LoanDemandInfo;
import com.yjf.yrd.ws.info.TradeDetailInfo;
import com.yjf.yrd.ws.info.TradeDetailVOInfo;
import com.yjf.yrd.ws.info.TradeInfo;
import com.yjf.yrd.ws.order.TradeDetailQueryOrder;
import com.yjf.yrd.ws.service.LoanAuthRecordManager;
import com.yjf.yrd.ws.service.LoanDemandExtendService;
import com.yjf.yrd.ws.service.query.order.LoanAuthRecordQueryOrder;
import com.yjf.yrd.ws.service.query.order.QueryTradeDetailUserOrder;
import com.yjf.yrd.ws.service.query.result.QueryBaseBatchResult;
import com.yjf.yrd.ws.service.query.result.TradeDetailBatchResult;

public abstract class LoanTradeDetailBaseController extends UserAccountInfoBaseController {
	
	@Autowired
	protected LoanAuthRecordManager loanAuthRecordManager;
	@Autowired
	protected IOperatorInfoService operatorInfoService;
	
	@Autowired
	protected DivisionDetailQueryService divisionDetailQueryService;
	
	@Autowired
	LoanDemandExtendService loanDemandExtendService;
	
	public abstract String getNoTradeView();
	
	public abstract String getTradeView();
	
	protected String getTradeDetailPageView(long demandId, long tradeId, Model model,
											HttpSession session, SysUserRoleEnum role,
											HttpServletRequest request, PageParam pageParam) {
		try {
			LoanDemandInfo loanDemand = null;
			if (demandId > 0) {
				loanDemand = loanDemandQueryService.loadLoanDemandInfo(demandId);
			} else if (tradeId > 0) {
				loanDemand = loanDemandQueryService.loadLoanDemandInfoByTradeId(tradeId);
			}
			//实际还款额
			Money actRepayAmount = loanDemand.getRepayAmount().subtract(loanDemand.getLoanGuaranteeAmount());
			
			
			UserInfo user = userQueryService.queryByUserName(loanDemand.getUserName())
				.getQueryUserInfo();
			model.addAttribute("realName", user.getRealName());
			
			model.addAttribute("loanerUserType", user.getType());
			TradeInfo trade = loanDemand.getTradeInfo();
			if (trade != null) {
				//TODO 优化点 效率
				//TradeDetailBatchResult<TradeDetailVOInfo> searchTradeDetailQuery(	TradeDetailQueryOrder tradeOrder);
				QueryTradeDetailUserOrder queryTradeDetailUserOrder = new QueryTradeDetailUserOrder();
				queryTradeDetailUserOrder.setTradeId(trade.getTradeId());
				queryTradeDetailUserOrder.setRoleEnum(SysUserRoleEnum.INVESTOR);
				queryTradeDetailUserOrder.setTransferPhase(DivisionPhaseEnum.ORIGINAL_PHASE);
				
				//pageParam.setPageSize(1);
				queryTradeDetailUserOrder.setPageSize(pageParam.getPageSize());
				queryTradeDetailUserOrder.setPageNumber(pageParam.getPageNo());
				
				QueryBaseBatchResult<TradeDetailInfo> batchResult = tradeBizQueryService
					.queryTradeDetailUserPage(queryTradeDetailUserOrder);
				model.addAttribute("page", PageUtil.getCovertPage(batchResult));
				model.addAttribute("investIotalCount", batchResult.getTotalCount());
				if (role == SysUserRoleEnum.GUARANTEE || role == SysUserRoleEnum.LOANER) {
					YjfAccountInfo yjfAccountInfo = getAccountInfo(model);
					if (yjfAccountInfo != null) {
						if (loanDemand.getRepayAmount().greaterThan(yjfAccountInfo.getBalance())) {
							model.addAttribute("poorAmount", 1);
						} else {
							model.addAttribute("poorAmount", -1);
						}
					}
				}
				
			}
			Date levelDate=DateUtil.parse("2014-12-16 00:00:00");
			Date createDate=loanDemand.getDemandDate();
			if(createDate.after(levelDate)){
				loanDemand.setNewVersion(true);
			}
			//易7的新旧版本
			Date levelDateYq=DateUtil.parse("2014-10-11 11:00:00");
			if(createDate.after(levelDateYq)){
				loanDemand.setNewVersionYq(true);
			}
			//end
			model.addAttribute("loanDemand", loanDemand);
			if (trade == null) {
				return getNoTradeView();
			}
			model.addAttribute("trade", trade);
			model.addAttribute("tradeId", trade.getTradeId());
			if (trade.getTradeStatus() != TradeStatusEnum.TRADING) {
				loanDemand.setLoanAmount(trade.getLoanedAmount());
			}
			if (role == SysUserRoleEnum.LOANER) {
				if (loanDemand.isFullScale()) {
					logger.info("tradeId为" + trade.getTradeId() + "的交易达到满标");
					model.addAttribute("fitRequiredStatus", "0");
				}
			}
			
			model.addAttribute("tradeStatus", trade.getTradeStatus().message());
			
			model.addAttribute("repayAmount", loanDemand.getRepayAmount());
			
			model.addAttribute("guaranteeAmount", loanDemand.getLoanGuaranteeAmount());
			
			model.addAttribute("actRepayAmount", actRepayAmount);	
			
			model.addAttribute("totalAnnualInterest",
				RateUtil.getRate(loanDemand.getInterestRate()));
			//投资人需要最后投资金额（低于最低投资，刚好满标）
			if (role == SysUserRoleEnum.INVESTOR) {
				boolean lastInvestAvailable = trade.isLastInvestAvailable();
				model.addAttribute("lastInvestAvailable", lastInvestAvailable);
				
			}
			model.addAttribute("investableAmount",
				trade.getTradeAmount().subtract(trade.getLoanedAmount()));
			//单笔投资限额
			LoanDemandExtendInfo limitInvestAmount = loanDemandExtendService
					.findByDemandIdAndProperty(demandId, LoanDemandExpendEnum.LIMIT_INVEST_AMOUNT.getCode());
				if (limitInvestAmount != null) {
					model.addAttribute("limitInvestAmount", limitInvestAmount.getPropertyValue());
				}
			
			
			String auditUserId = "";
			LoanAuthRecordQueryOrder queryOrder = new LoanAuthRecordQueryOrder();
			queryOrder.setLoanDemandId(loanDemand.getDemandId());
			queryOrder.setAuthType(YrdAuthTypeEnum.MAKELOANLVONE);
			// 是否已经一级审核，
			boolean isLevel1Audit = false;
			
			List<LoanAuthRecordInfo> authRecordInfos = loanAuthRecordManager
				.getLoanAuthRecordByCondition(queryOrder);
			if (ListUtil.isNotEmpty(authRecordInfos)) {
				auditUserId = String.valueOf(authRecordInfos.get(0).getAuthUserId());
				UserInfo users = userQueryService.queryByType(UserTypeEnum.JG,
					authRecordInfos.get(0).getAuthUserId()).getQueryUserInfo();
				model.addAttribute("auditUser1", users.getUserName());
				model.addAttribute("auditrecord1", authRecordInfos.get(0));
				isLevel1Audit = true;
			}
			LoanAuthRecordQueryOrder queryOrder2 = new LoanAuthRecordQueryOrder();
			queryOrder2.setLoanDemandId(loanDemand.getDemandId());
			queryOrder2.setAuthType(YrdAuthTypeEnum.MAKELOANLVTWO);
			List<LoanAuthRecordInfo> authRecordInfos2 = loanAuthRecordManager
				.getLoanAuthRecordByCondition(queryOrder2);
			if (ListUtil.isNotEmpty(authRecordInfos2)) {
				
				UserInfo users = userQueryService.queryByType(UserTypeEnum.JG,
					authRecordInfos2.get(0).getAuthUserId()).getQueryUserInfo();
				model.addAttribute("auditUser2", users.getUserName());
				model.addAttribute("auditrecord2", authRecordInfos2.get(0));
				model.addAttribute("audit", "yes");
			}
			if (role == SysUserRoleEnum.GUARANTEE) {
				Map<String, Object> opConditions = new HashMap<String, Object>();
				opConditions.put("userBaseId", SessionLocalManager.getSessionLocal()
					.getUserBaseId());
				opConditions.put("limitStart", 0);
				opConditions.put("pageSize", 9999);
				List<OperatorInfoDO> operatorInfos = operatorInfoService
					.queryOperatorsByProperties(opConditions);
				if (operatorInfos != null && operatorInfos.size() > 0) {
					if (2 == operatorInfos.get(0).getOperatorType()) {
						if (authRecordInfos.size() <= 0) {
							model.addAttribute("authOperator", "level1");
						}
						//fixed :wqh 一级审核过后，才能二级审核。
					}
					if (3 == operatorInfos.get(0).getOperatorType() && isLevel1Audit) {
						if (authRecordInfos2.size() <= 0) {
							model.addAttribute("authOperator", "level2");
						}
					}
				}
			}
			
			boolean investsDownLoadable = false;
			long countInvestTimes = 0;
			boolean investAvlTimeReached = (new Date().after(loanDemand.getInvestAvlDate()));
			SessionLocal sessionLocal = SessionLocalManager.getSessionLocal();
			if (sessionLocal != null) {
				//this.initAccountInfo(model);
				//是否投资过某融资需求，决定是否允许下载合同，担保函 
				/*
				LoanDemandQueryOrder demandQueryOrder = new LoanDemandQueryOrder();
				demandQueryOrder.setLoanerId(SessionLocalManager.getSessionLocal().getUserId());
				demandQueryOrder.setDemandId(loanDemand.getDemandId());
				countInvestTimes = loanDemandQueryService.queryLoandDemandCount(demandQueryOrder);*/
				
				TradeDetailQueryOrder tradeQueryOrder = new TradeDetailQueryOrder();
				tradeQueryOrder.setUserId(SessionLocalManager.getSessionLocal().getUserId());
				tradeQueryOrder.setRoleId(SysUserRoleEnum.INVESTOR.getValue());
				tradeQueryOrder.setTradeId(tradeId);
				TradeDetailBatchResult<TradeDetailVOInfo> batchResult = tradeBizQueryService
					.searchTradeDetailQuery(tradeQueryOrder);
				countInvestTimes = batchResult.getTotalCount();
				
				//是否可下载某融资需求的投资人列表
				investsDownLoadable = operatorInfoService.isFromSameOrgan(
					String.valueOf(SessionLocalManager.getSessionLocal().getUserId()), auditUserId);
				
				//是否是融资人
				
				if (StringUtil.equals(sessionLocal.getRealName(),loanDemand.getLoanerName())) {
					model.addAttribute("isBorrower", "yes");
				}
			}
			model.addAttribute("investsDownLoadable", investsDownLoadable);
			model.addAttribute("countInvestTimes", countInvestTimes);
			model.addAttribute("investAvlTimeReached", investAvlTimeReached);
			model.addAttribute("nowDate", new Date().getTime());
			boolean deadlineReached = (new Date().after(loanDemand.getDeadline()));
			if (trade.getTradeExpireDate() != null) {
				if (DateUtil.formatDay(new Date()).compareTo(
					DateUtil.formatDay(trade.getTradeExpireDate())) >= 0) {
					model.addAttribute("expireRepayDate", true);
				} else
					model.addAttribute("expireRepayDate", false);
			}
			model.addAttribute("deadlineReached", deadlineReached);
			
			/**收益促销提示*/
			LoanDemandExtendInfo salesPromotionPromptExtend = loanDemandExtendService.findByDemandIdAndProperty(
					demandId, LoanDemandExpendEnum.SALES_PROMOTION_PROMPT.code());
				if (salesPromotionPromptExtend != null) {
					model.addAttribute("salesPromotionPrompt", salesPromotionPromptExtend.getPropertyValue());
				}
			//易7展示机构借款人信息
	        UserInfo loanerUser = userQueryService.queryByUserId(loanDemand.getLoanerId()).getQueryUserInfo();
		    if(loanerUser != null){
		        model.addAttribute("userType", loanerUser.getType());
		        if("JG".equals(loanerUser.getType().code())){
		    		InstitutionsInfo institutionsInfo = userQueryService.queryInstitutionsInfoByBaseId(
		    				loanerUser.getUserBaseId()).getQueryInstitutionsInfo();
		            model.addAttribute("enterprise",institutionsInfo);
		        }
		    }
			//end
	
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return getTradeView();
	}
	
	protected UserInfo getBrokerUserBaseInfo(long userId, String userName) {
		return userQueryService.getBrokerUserInfo(userId, userName);
	}
}
