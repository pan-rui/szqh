package com.yjf.yrd.backstage.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yjf.yrd.session.SessionLocal;
import com.yjf.yrd.session.SessionLocalManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.yjf.common.lang.util.ListUtil;
import com.yjf.yrd.base.BaseAutowiredController;
import com.yjf.yrd.dal.daointerface.ExtraDAO;
import com.yjf.yrd.dal.daointerface.LoanApplyDAO;
import com.yjf.yrd.dal.dataobject.LoanApplyDO;
import com.yjf.yrd.user.query.order.UserQueryOrder;
import com.yjf.yrd.util.DateUtil;
import com.yjf.yrd.util.MiscUtil;
import com.yjf.yrd.util.MoneyUtil;
import com.yjf.yrd.ws.enums.LoanApplyStatusEnum;
import com.yjf.yrd.ws.enums.LoanDemandStatusEnum;
import com.yjf.yrd.ws.enums.RealNameAuthStatusEnum;
import com.yjf.yrd.ws.enums.TradeStatusEnum;
import com.yjf.yrd.ws.enums.UserTypeEnum;
import com.yjf.yrd.ws.info.LoanDemandTradeVOInfo;
import com.yjf.yrd.ws.service.query.order.LoanDemandQueryOrder;
import com.yjf.yrd.ws.service.query.order.QueryLoanTradeOrder;
import com.yjf.yrd.ws.service.query.order.TradeQueryOrder;
import com.yjf.yrd.ws.service.query.result.QueryBaseBatchResult;

/**
 * @author Joe
 */
@Controller
@RequestMapping("backstage")
public class BackstageIndexController extends BaseAutowiredController {
	@Autowired
	protected ExtraDAO extraDAO;
	@Autowired
	protected LoanApplyDAO loanApplyDAO;
	@RequestMapping("backstageIdex")
	public String backstageIdex(Model model) {
        SessionLocal sessionLocal = SessionLocalManager.getSessionLocal();
        if(sessionLocal == null){
            return "redirect:/backstagelogin/login";
        }
		UserQueryOrder userQueryOrder = new UserQueryOrder();
		userQueryOrder.setRealNameAuthentication(RealNameAuthStatusEnum.IN);
		userQueryOrder.setType(UserTypeEnum.GR);
		userQueryOrder.setPageSize(1);
		long inRealNamAuthentication = userQueryService.commonQueryUserInfo(userQueryOrder)
			.getTotalCount();// 实名认证中
		model.addAttribute("inRealNamAuthentication", inRealNamAuthentication);
		LoanDemandQueryOrder demandQueryOrder = new LoanDemandQueryOrder();
		
		demandQueryOrder.setStatusList(MiscUtil.toList(LoanDemandStatusEnum.WITE));
		long witeLoanDemand = loanDemandQueryService.queryLoandDemandCount(demandQueryOrder);//待审核项目
		model.addAttribute("witeLoanDemand", witeLoanDemand);
		LoanDemandQueryOrder queryOrder = new LoanDemandQueryOrder();
		queryOrder.setLetterPdfUrl("Y");
		queryOrder.setContractPdfUrl("Y");
		long noLetterContract = loanDemandQueryService.queryLoandDemandCount(queryOrder);//待上传合同、担保函项目
		model.addAttribute("noLetterContract", noLetterContract);
		//
		//		status.remove(1);
		TradeQueryOrder tradeQueryOrder = new TradeQueryOrder();
		tradeQueryOrder.setTradeEndExpireDate(DateUtil.getEndTimeOfTheDate(new Date()));
		tradeQueryOrder.setTradeBeginExpireDate(DateUtil.getStartTimeOfTheDate(DateUtil
			.getWeekdayBeforeDate(new Date())));
		//fixed 加个待还款的状态
		List<TradeStatusEnum> statusList = new ArrayList<TradeStatusEnum>();
		statusList.add(TradeStatusEnum.DOREPAY);
		tradeQueryOrder.setStatusList(statusList);
		long waitRepay = tradeBizQueryService.queryTradeCount(tradeQueryOrder);//一周内待还款项目
		
		model.addAttribute("tradeBeginExpireDate", tradeQueryOrder.getTradeBeginExpireDate());
		model.addAttribute("tradeEndExpireDate", tradeQueryOrder.getTradeEndExpireDate());
		model.addAttribute("waitRepay", waitRepay);
		//发布的产品
		QueryLoanTradeOrder queryLoanTradeOrder = new QueryLoanTradeOrder();
		List<String> status = new ArrayList<String>();
		queryLoanTradeOrder.setStatus(status);
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("status", status);
		queryLoanTradeOrder.setPageSize(extraDAO.searchLoanTradeCountCommonQuery(paramMap));
		QueryBaseBatchResult batchResult = tradeBizQueryService
			.searchLoanTradeCommonQuery(queryLoanTradeOrder);
		List<LoanDemandTradeVOInfo> loanDemandTradeVOs = batchResult.getPageList();
		Long issueAmounts = 0l;
		if (ListUtil.isNotEmpty(loanDemandTradeVOs)) {
			for (LoanDemandTradeVOInfo info : loanDemandTradeVOs) {
				issueAmounts = issueAmounts + info.getLoanAmount().getCent();
				
			}
		}
		
		model.addAttribute("issueLoanDemand", batchResult.getTotalCount());
		model.addAttribute("issueAmounts", MoneyUtil.getFormatAmount(issueAmounts));
		//已经成立的产品
		status = new ArrayList<String>();
		queryLoanTradeOrder = new QueryLoanTradeOrder();
		status.add("2");
		queryLoanTradeOrder.setStatus(status);
		paramMap = new HashMap<String, Object>();
		paramMap.put("status", status);
		queryLoanTradeOrder.setPageSize(extraDAO.searchLoanTradeCountCommonQuery(paramMap));
		batchResult = tradeBizQueryService.searchLoanTradeCommonQuery(queryLoanTradeOrder);
		loanDemandTradeVOs = batchResult.getPageList();
		issueAmounts = 0l;
		if (ListUtil.isNotEmpty(loanDemandTradeVOs)) {
			for (LoanDemandTradeVOInfo info : loanDemandTradeVOs) {
				issueAmounts = issueAmounts + info.getLoanAmount().getCent();
			}
		}
		model.addAttribute("establishLoanDemand", batchResult.getTotalCount());
		model.addAttribute("establishAmounts", MoneyUtil.getFormatAmount(issueAmounts));
		//解除担保的产品
		status = new ArrayList<String>();
		queryLoanTradeOrder = new QueryLoanTradeOrder();
		status.add("3");
		queryLoanTradeOrder.setStatus(status);
		paramMap = new HashMap<String, Object>();
		paramMap.put("status", status);
		queryLoanTradeOrder.setPageSize(extraDAO.searchLoanTradeCountCommonQuery(paramMap));
		batchResult = tradeBizQueryService.searchLoanTradeCommonQuery(queryLoanTradeOrder);
		loanDemandTradeVOs = batchResult.getPageList();
		issueAmounts = 0l;
		if (ListUtil.isNotEmpty(loanDemandTradeVOs)) {
			for (LoanDemandTradeVOInfo info : loanDemandTradeVOs) {
				issueAmounts = issueAmounts + info.getLoanAmount().getCent();
			}
			
		}
		
		model.addAttribute("relieveLoanDemand", batchResult.getTotalCount());
		model.addAttribute("relieveAmounts", MoneyUtil.getFormatAmount(issueAmounts));
		
		//正在担保中的产品
		status = new ArrayList<String>();
		queryLoanTradeOrder = new QueryLoanTradeOrder();
		status.add("8");
		queryLoanTradeOrder.setStatus(status);
		paramMap = new HashMap<String, Object>();
		paramMap.put("status", status);
		queryLoanTradeOrder.setPageSize(extraDAO.searchLoanTradeCountCommonQuery(paramMap));
		batchResult = tradeBizQueryService.searchLoanTradeCommonQuery(queryLoanTradeOrder);
		loanDemandTradeVOs = batchResult.getPageList();
		issueAmounts = 0l;
		if (ListUtil.isNotEmpty(loanDemandTradeVOs)) {
			for (LoanDemandTradeVOInfo info : loanDemandTradeVOs) {
				issueAmounts = issueAmounts + info.getLoanAmount().getCent();
			}
		}
		model.addAttribute("guaranteeLoanDemand", batchResult.getTotalCount());
		model.addAttribute("guaranteeAmounts", MoneyUtil.getFormatAmount(issueAmounts));
		//借款申请处理
		List<String> LoanStatusList = new ArrayList<String>();
		LoanApplyDO  loanApplyDO =new LoanApplyDO();
		LoanStatusList.add(LoanApplyStatusEnum.WITE.code());
		long LoanTotalCount = loanApplyDAO.findCountByCondition(loanApplyDO,LoanStatusList);
		model.addAttribute("LoanTotalCount", LoanTotalCount);
		//end
		return "/backstage/index.vm";
	}
	
	@RequestMapping("nopermission")
	public String hasNoPermission() {
		return "/backstage/nopermission.vm";
	}
	
}
