package com.yjf.yrd.front.controller.business.marketing;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.yjf.common.lang.beans.cglib.BeanCopier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.yjf.common.lang.util.ListUtil;
import com.yjf.yrd.base.UserAccountInfoBaseController;
import com.yjf.yrd.dal.dataobject.PersonalInfoDO;
import com.yjf.yrd.dal.dataobject.UserBaseInfoDO;
import com.yjf.yrd.page.PageParam;
import com.yjf.yrd.service.profit.ProfitPrcocessService;
import com.yjf.yrd.service.profit.order.AddProfitOrder;
import com.yjf.yrd.session.SessionLocalManager;
import com.yjf.yrd.user.info.InstitutionsInfo;
import com.yjf.yrd.user.info.PersonalInfo;
import com.yjf.yrd.user.info.PersonalVOInfo;
import com.yjf.yrd.user.info.UserInfo;
import com.yjf.yrd.user.info.UserRelationInfo;
import com.yjf.yrd.user.order.MarketingOpenBrokerOrder;
import com.yjf.yrd.user.query.order.QueryUserChildrenOrder;
import com.yjf.yrd.user.valueobject.QueryConditions;
import com.yjf.yrd.web.util.PageUtil;
import com.yjf.yrd.web.util.TradeUtil;
import com.yjf.yrd.web.util.WebUtil;
import com.yjf.yrd.ws.enums.DivisionPhaseEnum;
import com.yjf.yrd.ws.enums.SysUserRoleEnum;
import com.yjf.yrd.ws.enums.UserTypeEnum;
import com.yjf.yrd.ws.info.LoanDemandInfo;
import com.yjf.yrd.ws.info.ProfitAsignInfo;
import com.yjf.yrd.ws.info.TradeDetailInfo;
import com.yjf.yrd.ws.info.TradeDetailVOInfo;
import com.yjf.yrd.ws.info.TradeInfo;
import com.yjf.yrd.ws.order.TradeDetailQueryOrder;
import com.yjf.yrd.ws.result.YrdBaseResult;
import com.yjf.yrd.ws.service.query.order.QueryLoanTradeOrder;
import com.yjf.yrd.ws.service.query.order.QueryTradeDetailUserOrder;
import com.yjf.yrd.ws.service.query.result.QueryBaseBatchResult;
import com.yjf.yrd.ws.service.query.result.TradeDetailBatchResult;
import com.yjf.yrd.ws.statistics.TradeAmountStatisticsInfo;
import com.yjf.yrd.ws.statistics.result.TradeStatisticsResult;

@Controller
@RequestMapping("marketingCenter")
public class MarketingAgencyController extends UserAccountInfoBaseController {
	String MARKETING_MANAGE_PATH = "/front/business/marketing/";
	private final static String JGAGENTPREFIX = ""; //机构经纪人前缀 
	private final static int MEMBERSCALEDEFULT = 5; //机构人员规模默认
	private final static int MEMBERSCALEVIP = 8; //机构人员规模高级
	private String yxjgId = "10";
	
	@Autowired
	ProfitPrcocessService profitPrcocessService;
	
	public String getYxjgId() {
		return yxjgId;
	}
	
	public void setYxjgId(String yxjgId) {
		this.yxjgId = yxjgId;
	}
	
	//-----------------------------------------------------经纪人管理----------------------------------------------------------------------
	
	@RequestMapping("brokerManage")
	public String brokerManage(HttpSession session, QueryConditions queryConditions,
								PageParam pageParam, Model model, HttpServletRequest request)
																								throws Exception {
		this.initAccountInfo(model);
		session.setAttribute("current", 7);
		QueryUserChildrenOrder userChildrenOrder = new QueryUserChildrenOrder();
		WebUtil.setPoPropertyByRequest(userChildrenOrder, request);
		userChildrenOrder.setUserId(SessionLocalManager.getSessionLocal().getUserId());
		userChildrenOrder.setPageNumber(pageParam.getPageNo());
		userChildrenOrder.setPageSize(pageParam.getPageSize());
		userChildrenOrder.setHasBrokerDistributionQuota(true);
        userChildrenOrder.setRoleId(SysUserRoleEnum.BROKER.getValue());
		QueryBaseBatchResult<PersonalVOInfo> baseBatchResult = userQueryService
			.queryUserChildren(userChildrenOrder);
		
		model.addAttribute("page", PageUtil.getCovertPage(baseBatchResult));
		model.addAttribute("queryConditions", userChildrenOrder);
		return MARKETING_MANAGE_PATH + "marketing-broker-manage.vm";
	}
	
	@RequestMapping("addBroker")
	public String addBroker(HttpSession session, Model model) throws Exception {
		session.setAttribute("current", 7);
		this.initAccountInfo(model);
		String token = UUID.randomUUID().toString();
		session.setAttribute("token", token);
		return MARKETING_MANAGE_PATH + "marketing-broker-add.vm";
	}
	
	@RequestMapping("profitAssignInfoBroker")
	public String profitAssignInfoBroker(String userBaseId, Model model) throws Exception {
		this.initAccountInfo(model);
		UserInfo personalInfo = userQueryService.queryByUserBaseId(userBaseId).getQueryUserInfo();
		ProfitAsignInfo profitAsignInfo = new ProfitAsignInfo();
		
		if (personalInfo != null) {
			profitAsignInfo.setReceiveId(personalInfo.getUserId());
			List<Long> recevieIds = new ArrayList<Long>();
			recevieIds.add(personalInfo.getUserId());
			List<ProfitAsignInfo> list = profitPrcocessService.queryBrokerInfoByReceiveIds(
				recevieIds).getPageList();
			if (ListUtil.isNotEmpty(list)) {
				profitAsignInfo = list.get(0);
			}
		}
		double distributionQuota = profitAsignInfo.getDistributionQuota();
        BigDecimal bg = new BigDecimal(distributionQuota * 100);
        distributionQuota = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
	    profitAsignInfo.setDistributionQuota(distributionQuota);
		model.addAttribute("profitAsignInfo", profitAsignInfo);
		return MARKETING_MANAGE_PATH + "broker-asign-profit.vm";
	}
	
	@ResponseBody
	@RequestMapping("saveProfitAssignInfoBroker")
	public Object save(AddProfitOrder addProfitOrder) {
		JSONObject jsonObject = new JSONObject();
		try {
			addProfitOrder.setDistributionId(SessionLocalManager.getSessionLocal().getUserId());
			YrdBaseResult baseResult = profitPrcocessService.addBrokerProfitSetting(addProfitOrder);
			if (baseResult.isSuccess()) {
				jsonObject.put("code", 1);
				jsonObject.put("message", "修改成功");
			} else {
				jsonObject.put("code", 0);
				jsonObject.put("message", baseResult.getMessage());
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return jsonObject;

	}


    @ResponseBody
    @RequestMapping("cancelProfitAssignInfoBroker")
    public Object cancel(long receiveId) {
        JSONObject jsonObject = new JSONObject();
        try {

            YrdBaseResult baseResult = profitPrcocessService.cancelBrokerProfitSetting(receiveId);
            if (baseResult.isSuccess()) {
                jsonObject.put("code", 1);
                jsonObject.put("message", "修改成功");
            } else {
                jsonObject.put("code", 0);
                jsonObject.put("message", baseResult.getMessage());
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return jsonObject;

    }

	
	@RequestMapping("addBrokerSubmit")
	public String addBrokerSubmit(HttpServletRequest request, HttpSession session, String token,
									PersonalInfoDO personalInfo, UserBaseInfoDO userBaseInfo,
									Model model) throws Exception {
        this.initAccountInfo(model);
		session.setAttribute("current", 7);
		String return_vm = MARKETING_MANAGE_PATH + "marketing-broker-addNO.vm";
		String getToken = (String) session.getAttribute("token");
		if (getToken != null) {
			if (getToken.equals(token)) {
				session.removeAttribute("token");
				MarketingOpenBrokerOrder brokerOrder = new MarketingOpenBrokerOrder();
				WebUtil.setPoPropertyByRequest(brokerOrder, request);
                //房融通，营销机构开经纪人开户，所开用户和他同地区
    			UserInfo userInfo = userQueryService.queryByUserId(SessionLocalManager.getSessionLocal().getUserId()).getQueryUserInfo();
    			brokerOrder.setUserCustomType(userInfo.getUserCustomType());
    			//end
				brokerOrder.setBrokerUserId(SessionLocalManager.getSessionLocal().getUserId());
				brokerOrder.setServletPath(request.getServletContext().getRealPath("/"));
				YrdBaseResult baseResult = registerService.marketingOpenBroker(brokerOrder);
				if (baseResult.isSuccess()) {
					String tokenOk = UUID.randomUUID().toString();
					session.setAttribute("tokenOk", tokenOk);
				}
			}
			
		}
		String getTokenOk = (String) session.getAttribute("tokenOk");
		if (getTokenOk != null) {
			model.addAttribute("res","1");
			return_vm = MARKETING_MANAGE_PATH + "marketing-broker-addOK.vm";
		}
		model.addAttribute("reason", "处理新增账户信息失败！");
		return return_vm;
	}
	
	//-----------------------------------------------------投资人管理----------------------------------------------------------------------
	@RequestMapping("investorManage/{brokerId}")
	public String investorManage(@PathVariable long brokerId, HttpSession session,
									PageParam pageParam, Model model, HttpServletRequest request)
																									throws Exception {
		this.initAccountInfo(model);
		session.setAttribute("current", 7);
		QueryUserChildrenOrder commonQueryOrder = new QueryUserChildrenOrder();
		WebUtil.setPoPropertyByRequest(commonQueryOrder, request);
		commonQueryOrder.setUserId(brokerId);
		commonQueryOrder.setPageNumber(pageParam.getPageNo());
		commonQueryOrder.setPageSize(pageParam.getPageSize());
        commonQueryOrder.setHasInvestorDistributionQuota(true);
		QueryBaseBatchResult<PersonalVOInfo> baseBatchResult = userQueryService
			.queryUserChildren(commonQueryOrder);
		model.addAttribute("page", PageUtil.getCovertPage(baseBatchResult));
		model.addAttribute("queryConditions", commonQueryOrder);
		return MARKETING_MANAGE_PATH + "marketing-broker-investor-manage.vm";
	}
	
	//-----------------------------------------------------经纪人详情----------------------------------------------------------------------
	
	@RequestMapping("brokerInfo")
	public String brokerInfo(String userBaseId, Model model) throws Exception {
		this.initAccountInfo(model);
		UserInfo userBase = userQueryService.queryByUserBaseId(userBaseId).getQueryUserInfo();
		if (userBase.getType() == UserTypeEnum.JG) {
			InstitutionsInfo institutionInfo = userQueryService.queryInstitutionsInfoByBaseId(
				userBaseId).getQueryInstitutionsInfo();
			UserRelationInfo userRelationInfo = userRelationQueryService.findUserRelationByChildId(
				userBase.getUserId()).getQueryUserRelationInfo();
			
			if (userRelationInfo != null) {
				model.addAttribute("userMemoNo", userRelationInfo.getMemberNo());
			} else {
				model.addAttribute("userMemoNo", "");
			}
            BeanCopier.staticCopy(userBase,institutionInfo);
			model.addAttribute("userType", UserTypeEnum.JG.code());
			model.addAttribute("info", institutionInfo);
		} else {
			PersonalInfo personalInfo = userQueryService.queryPersonalInfoByBaseId(userBaseId)
				.getQueryPersonalInfo();
			UserRelationInfo userRelationInfo = userRelationQueryService.findUserRelationByChildId(
				userBase.getUserId()).getQueryUserRelationInfo();
			if (userRelationInfo != null) {
				model.addAttribute("userMemoNo", userRelationInfo.getMemberNo());
			} else {
				model.addAttribute("userMemoNo", "");
			}
			model.addAttribute("userType", "GR");
            BeanCopier.staticCopy(userBase,personalInfo);
			model.addAttribute("info", personalInfo);
		}
		return MARKETING_MANAGE_PATH + "marketing-brokerInfo.vm";
	}
	
	//-----------------------------------------------------营销统计----------------------------------------------------------------------
	
	@RequestMapping("salesCount")
	public String salesCount(HttpSession session, PageParam pageParam, Model model)
	    																				throws Exception {
		session.setAttribute("current", 6);
		QueryLoanTradeOrder queryConditions = new QueryLoanTradeOrder();
        this.initAccountInfo(model);
		queryConditions.setUserId(SessionLocalManager.getSessionLocal().getUserId());
		queryConditions.setRoleId(SysUserRoleEnum.MARKETING.getValue());
		queryConditions.setPageSize(5000);
		queryConditions.setHasBenefitAmount("YES");
		TradeStatisticsResult<TradeAmountStatisticsInfo> batchResult = tradeBizQueryService
			.searchLoanTradeSumCommonQuery(queryConditions);
		model.addAttribute("statisticsInfo", batchResult.getStatisticsInfo());
		return MARKETING_MANAGE_PATH + "marketing-salesCount.vm";
	}
	
	/**
	 * 营销详情
	 * @param tradeId
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("salesDetails/{tradeId}/{detailId}")
	public String salesDetails(@PathVariable long tradeId, @PathVariable long detailId,
								long investDetailId, Model model) throws Exception {
        this.initAccountInfo(model);
		LoanDemandInfo demandInfo = loanDemandQueryService.loadLoanDemandInfoByTradeId(tradeId);
		TradeInfo trade = demandInfo.getTradeInfo();
		QueryTradeDetailUserOrder queryTradeDetailUserOrder = new QueryTradeDetailUserOrder();
		queryTradeDetailUserOrder.setTradeDetailId(String.valueOf(investDetailId));
		queryTradeDetailUserOrder.setTradeId(tradeId);
		List<TradeDetailInfo> userInvests = tradeBizQueryService.queryTradeDetailUser(
			queryTradeDetailUserOrder).getPageList();
		
		model.addAttribute("loanDemand", demandInfo);
		
		if (userInvests != null && userInvests.size() > 0) {
			for (TradeDetailInfo invest : userInvests) {
				if (DivisionPhaseEnum.ORIGINAL_PHASE.code().equals(invest.getTransferPhase())) {
					model.addAttribute("tradeItem", invest);
				}
			}
		}
		
		TradeDetailInfo yxjgTrade = tradeBizQueryService.getByTradeDetailId(detailId,
			SysUserRoleEnum.MARKETING);
		if (yxjgTrade != null && yxjgTrade.getTradeId() == tradeId) {
			
			model.addAttribute("yxjgTrade", yxjgTrade);
			String repayStatus = TradeUtil.getRepayStatus(trade, yxjgTrade);
			model.addAttribute("repayStatus", repayStatus);
		}
		
		model.addAttribute("yxjgDivisionInterest",
			demandInfo.getBrokerInterest().setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
		model.addAttribute("trade", trade);
		return MARKETING_MANAGE_PATH + "marketing-sales-detail.vm";
	}
	
	//-----------------------------------------------------营销交易列表----------------------------------------------------------------------
	
	@RequestMapping("salesList")
	public String salesList(TradeDetailQueryOrder queryConditions, HttpSession session,
							PageParam pageParam, Model model) throws Exception {
		session.setAttribute("current", 6);
        this.initAccountInfo(model);
		queryConditions.setUserId(SessionLocalManager.getSessionLocal().getUserId());
		queryConditions.setRoleId(SysUserRoleEnum.MARKETING.getValue());
		queryConditions.setPageSize(pageParam.getPageSize());
		queryConditions.setPageNumber(pageParam.getPageNo());
		TradeDetailBatchResult<TradeDetailVOInfo> page = tradeBizQueryService
			.searchTradeDetailQuery(queryConditions);
		model.addAttribute("queryConditions", queryConditions);
		model.addAttribute("page", PageUtil.getCovertPage(page));
		model.addAttribute("totalAmount", page.getTotalAmount());
		return MARKETING_MANAGE_PATH + "marketing-salesList.vm";
	}
	
	@RequestMapping("salesInvestList")
	public String salesInvestList(TradeDetailQueryOrder queryConditions, HttpSession session,
									PageParam pageParam, Model model) throws Exception {
		this.initAccountInfo(model);
		session.setAttribute("current", 6);
		queryConditions.setUserId(SessionLocalManager.getSessionLocal().getUserId());
		queryConditions.setPageSize(pageParam.getPageSize());
		queryConditions.setPageNumber(pageParam.getPageNo());
		TradeDetailBatchResult<TradeDetailVOInfo> page = tradeBizQueryService
			.searchTradeInvestDetailQuery(queryConditions);
		model.addAttribute("queryConditions", queryConditions);
		List<TradeDetailVOInfo> list = page.getPageList();
		if (ListUtil.isNotEmpty(list)) {
			for (TradeDetailVOInfo info : list) {
				// 如果当前人是经济人
				UserInfo curParentJjr = null;
				UserInfo userBaseInfoDO = userQueryService.queryByUserId(info.getUserId())
					.getQueryUserInfo();
				curParentJjr = userQueryService.getBrokerUserInfo(userBaseInfoDO.getUserId(),
					userBaseInfoDO.getUserName());
				if (curParentJjr != null) {
					info.setBrokerUserName(curParentJjr.getUserName());
					info.setBrokerRealName(curParentJjr.getRealName());
				}
				
			}
			
		}
		
		model.addAttribute("page", PageUtil.getCovertPage(page));
		model.addAttribute("totalAmount", page.getTotalAmount());
		return MARKETING_MANAGE_PATH + "marketing-salesInvestList.vm";
	}
	
	public static void main(String[] args) {
		String s = null;
		System.out.println(s.length());
	}
}
