package com.yjf.yrd.front.controller.app.newVers.e8Trunck.v2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.yjf.common.lang.util.DateUtil;
import com.yjf.common.lang.util.money.Money;
import com.yjf.yrd.dal.dataobject.LoanerExtendDescriptionDO;
import com.yjf.yrd.front.controller.trade.base.LoanTradeDetailBaseController;
import com.yjf.yrd.rs.util.RateUtil;
import com.yjf.yrd.service.query.trade.TradeBizQueryService;
import com.yjf.yrd.user.LoanerBaseInfoService;
import com.yjf.yrd.user.LoanerExtendService;
import com.yjf.yrd.user.info.UserInfo;
import com.yjf.yrd.util.MoneyUtil;
import com.yjf.yrd.util.StringUtil;
import com.yjf.yrd.web.util.YrdEnumUtil;
import com.yjf.yrd.ws.enums.CommonAttachmentTypeEnum;
import com.yjf.yrd.ws.enums.TradeStatusEnum;
import com.yjf.yrd.ws.info.CommonAttachmentInfo;
import com.yjf.yrd.ws.info.LoanDemandInfo;
import com.yjf.yrd.ws.info.LoanDemandTradeVOInfo;
import com.yjf.yrd.ws.info.LoanerBaseInfo;
import com.yjf.yrd.ws.info.TradeInfo;
import com.yjf.yrd.ws.order.CommonAttachmentQueryOrder;
import com.yjf.yrd.ws.service.CommonAttachmentService;
import com.yjf.yrd.ws.service.query.order.QueryLoanTradeOrder;
import com.yjf.yrd.ws.service.query.result.QueryBaseBatchResult;

/**
 * 
 * 
 * @Filename appBootController.java
 * 
 * @Description
 * 
 * @Version 2.0
 * 
 * @Author zhaohaibing
 * 
 * @Email abing@yiji.com
 * 
 * @Date: 2014-1-6
 * 
 * 
 */
@Controller
@RequestMapping("app")
public class NewBootController extends LoanTradeDetailBaseController {
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private TradeBizQueryService tradeBizQueryService;
	@Autowired
	LoanerBaseInfoService loanerBaseInfoService;
	@Autowired
	CommonAttachmentService commonAttachmentService;
	
	@Autowired
	LoanerExtendService loanerExtendService;
	
	/**
	 * 1、项目列表
	 * 
	 * @param pageNumber
	 * @param pageSize
	 * @param bizType
	 * @param status
	 * @param guaranteeName
	 * @param timeLimit
	 * @param amountLimit
	 * @return
	 * @throws IOException
	 */
	
	@ResponseBody
	@RequestMapping("appProjectList.htm")
	public JSONObject appProjectList(int pageSize, int pageNumber, String bizType, Integer status,
										String guaranteeName, Integer loanPeriod, String insureWay,
										Long startAmount, Long endAmount, String startRate,
										String endRate, String leastInvestAmount,
										String tradeStatus, Model model) throws IOException {
		
		this.initAccountInfo(model);
		QueryLoanTradeOrder tradeQueryOrder = new QueryLoanTradeOrder();
		tradeQueryOrder.setPageNumber(pageNumber);
		tradeQueryOrder.setPageSize(pageSize);
		if (loanPeriod == null) {
			tradeQueryOrder.setLoanPeriod(0);
		} else {
			tradeQueryOrder.setLoanPeriod(loanPeriod);
		}
		
		if (startAmount != null) {
			tradeQueryOrder.setMinLoanAmount(Money.cent(startAmount));
		}
		
		if (endAmount != null) {
			tradeQueryOrder.setMaxLoanAmount(Money.cent(endAmount));
		}
		if (StringUtil.isNotEmpty(bizType)) {
			tradeQueryOrder.setBizType(bizType);
		}
		tradeQueryOrder.setInsureWay(insureWay);
		
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
		tradeQueryOrder.setGuaranteeName(guaranteeName);
		tradeQueryOrder.setInterestRateBegin(startRate);
		tradeQueryOrder.setInterestRateEnd(endRate);
		JSONObject json = new JSONObject();
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		QueryBaseBatchResult<LoanDemandTradeVOInfo> batchdemandInfo = tradeBizQueryService
			.searchLoanTradeCommonQuery(tradeQueryOrder);
		if (batchdemandInfo.isSuccess()) {
			for (LoanDemandTradeVOInfo demandInfo : batchdemandInfo.getPageList()) {
				Map<String, String> map = new HashMap<String, String>();
				int statues = demandInfo.getTradeStatus().getValue();
				double needMoey = MoneyUtil.getMoneyByw(demandInfo.getLoanAmount().subtract(
					demandInfo.getLoanedAmount()));
				boolean investAvlTimeReached = (new Date().after(demandInfo.getInvestAvlDate()));
				boolean limitTime = demandInfo.getDeadline().after(new Date());
				long leftTime = 0;
				String sta = AppCommonUtil.getAppStatus(statues, needMoey, investAvlTimeReached,
					limitTime);
				if (sta == "3") {
					leftTime = (demandInfo.getInvestAvlDate().getTime() - (new Date()).getTime()) / 1000;
				} else if (sta == "1") {
					leftTime = (demandInfo.getDeadline().getTime() - (new Date()).getTime()) / 1000;
				}
				String isDirectional = "0";
				if (StringUtil.isNotEmpty(demandInfo.getLoanPassword()))
					isDirectional = "1";
				String tradeEffectiveDate = DateUtil.dtSimpleFormat(demandInfo
					.getTradeEffectiveDate());
				if (StringUtil.isEmpty(tradeEffectiveDate))
					tradeEffectiveDate = "未成立";
				map.put("status", sta);
				map.put("leftTime", String.valueOf(leftTime));
				map.put("isDirectional", isDirectional);
				map.put("loanName", demandInfo.getLoanName());
				map.put("demandId", String.valueOf(demandInfo.getDemandId()));
				map.put("tradeId", String.valueOf(demandInfo.getTradeId()));
				map.put("guaranteeName", demandInfo.getGuaranteeName());
				map.put("loanAmount", demandInfo.getLoanAmount().toStandardString());
				map.put("tradeEffectiveDate", tradeEffectiveDate);
				map.put("timeLimit", demandInfo.getTimeLimit()
										+ demandInfo.getTimeLimitUnit().getViewName());
				map.put("interestRate", RateUtil.getRate(demandInfo.getInterestRate()));
				map.put("leastInvestAmount", demandInfo.getLeastInvestAmount().toStandardString());
				map.put("investAvlDate", DateUtil.simpleFormat(demandInfo.getInvestAvlDate()));
				map.put("deadline", DateUtil.simpleFormat(demandInfo.getDeadline()));
				map.put("repayDivisionWay", demandInfo.getRepayDivisionWay().getMessage());
				map.put("investProgress", StringUtil.replace(
					MoneyUtil.getPercentage(demandInfo.getLoanedAmount(),
						demandInfo.getLoanAmount(), demandInfo.getLeastInvestAmount()), "%", ""));
				list.add(AppCommonUtil.cleanNull(map));
			}
			
			json.put("totalPage", batchdemandInfo.getPageCount());
			json.put("code", 1);
			json.put("message", "项目列表查询成功");
		} else {
			json.put("code", 0);
			json.put("message", "没有查询到对应的项目列表信息");
			json.put("totalPage", 0);
		}
		json.put("list", list);
		return json;
		
	}
	
	/**
	 * 2、 项目详情
	 * 
	 * @param demandId
	 * @return
	 * @throws Exception
	 * 
	 * 
	 */
	@ResponseBody
	@RequestMapping("appProjectDetail.htm")
	public JSONObject appProjectDetail(long demandId, HttpServletResponse response)
																					throws Exception {
		LoanDemandInfo demandInfo = loanDemandQueryService.loadLoanDemandInfo(demandId);
		TradeInfo tradeInfo = tradeBizQueryService.getByLoanDemandId(demandId);
		
		JSONObject json = new JSONObject();
		Map<String, String> map = new HashMap<String, String>();
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		if (demandInfo != null && tradeInfo != null) {
			int statues = tradeInfo.getTradeStatus().getValue();
			double needMoey = MoneyUtil.getMoneyByw(tradeInfo.getTradeAmount(),
				tradeInfo.getLoanedAmount()) * 10000;
			boolean investAvlTimeReached = (new Date().after(demandInfo.getInvestAvlDate()));
			boolean limitTime = demandInfo.getDeadline().after(new Date());
			long leftTime = 0;
			String isDirectional = "0";
			if (StringUtil.isNotEmpty(demandInfo.getLoanPassword()))
				isDirectional = "1";
			String sta = AppCommonUtil.getAppStatus(statues, needMoey, investAvlTimeReached,
				limitTime);
			if (sta == "3") {
				leftTime = (demandInfo.getInvestAvlDate().getTime() - (new Date()).getTime()) / 1000;
			} else if (sta == "1") {
				leftTime = (demandInfo.getDeadline().getTime() - (new Date()).getTime()) / 1000;
			}
			String tradeEffectiveDate = DateUtil.dtSimpleFormat(tradeInfo.getTradeEffectiveDate());
			if (StringUtil.isEmpty(tradeEffectiveDate))
				tradeEffectiveDate = "未成立";
			map.put("status", sta);
			map.put("leftTime", String.valueOf(leftTime));
			map.put("isDirectional", isDirectional);
			map.put("demandId", String.valueOf(tradeInfo.getDemandId()));
			map.put("tradeId", String.valueOf(tradeInfo.getTradeId()));
			map.put("loanName", demandInfo.getLoanName());
			map.put("loanPurpose", demandInfo.getLoanPurpose());
			map.put("guaranteeName", demandInfo.getGuaranteeName());
			map.put("loanAmount", tradeInfo.getTradeAmount().toStandardString());
			map.put("tradeEffectiveDate", tradeEffectiveDate);
			map.put("investCount", String.valueOf(tradeInfo.getInvestCount()));
			map.put("guaranteeLicenceNo", demandInfo.getGuaranteeLicenceNo());
			map.put("loanedAmount", tradeInfo.getLoanedAmount().toStandardString());
			map.put("interestRate", RateUtil.getRate(tradeInfo.getInterestRate()));
			map.put("timeLimit", tradeInfo.getTimeLimit()
									+ tradeInfo.getTimeLimitUnit().getViewName());
			map.put("deadline", DateUtil.simpleFormat(tradeInfo.getDeadline()));
			map.put("investAvlDate", DateUtil.simpleFormat(demandInfo.getInvestAvlDate()));
			map.put("repayDivisionWay", demandInfo.getRepayDivisionWay().getMessage());
			map.put("leastInvestAmount", tradeInfo.getLeastInvestAmount().toStandardString());
			map.put("guaranteeAudit", demandInfo.getGuaranteeAudit().getMessage());
			map.put("avalableAmount",
				tradeInfo.getTradeAmount().subtract(tradeInfo.getLoanedAmount()).toStandardString());
			map.put(
				"investProgress",
				StringUtil.replace(MoneyUtil.getPercentage(tradeInfo.getLoanedAmount(),
					tradeInfo.getTradeAmount(), tradeInfo.getLeastInvestAmount()), "%", ""));
			
			list.add(AppCommonUtil.cleanNull(map));
			json.put("code", 1);
			json.put("message", "项目详情查询成功");
		} else {
			json.put("code", 0);
			json.put("message", "项目详情查询失败");
		}
		json.put("ProjectDetail", list);
		
		return json;
		
	}
	
	/**
	 * 3、 项目信息
	 * 
	 * @param demandId
	 * @return
	 * @throws IOException
	 * 
	 */
	@ResponseBody
	@RequestMapping("projectInfo.htm")
	public JSONObject gettradeInfo(long demandId, HttpServletResponse response) throws IOException {
		
		LoanDemandInfo demandInfo = loanDemandQueryService.loadLoanDemandInfo(demandId);
		TradeInfo tradeInfo = tradeBizQueryService.getByLoanDemandId(demandId);
		JSONObject json = new JSONObject();
		Map<String, String> map = new HashMap<String, String>();
		if (demandInfo != null && tradeInfo != null) {
			int statues = tradeInfo.getTradeStatus().getValue();
			double needMoey = MoneyUtil.getMoneyByw(tradeInfo.getTradeAmount(),
				tradeInfo.getLoanedAmount()) * 10000;
			boolean investAvlTimeReached = (new Date().after(demandInfo.getInvestAvlDate()));
			boolean limitTime = demandInfo.getDeadline().after(new Date());
			String sta = AppCommonUtil.getAppStatus(statues, needMoey, investAvlTimeReached,
				limitTime);
			map.put("status", sta);
			map.put("loanPurpose", demandInfo.getLoanPurpose());
			map.put("deadline", DateUtil.simpleFormat(demandInfo.getDeadline()));
			map.put("repayDivisionWay", demandInfo.getRepayDivisionWay().getMessage());
			map.put("loanedAmount", tradeInfo.getLoanedAmount().toStandardString());
			map.put("investAvlDate", DateUtil.simpleFormat(demandInfo.getInvestAvlDate()));
			map.put("avalableAmount",
				tradeInfo.getTradeAmount().subtract(tradeInfo.getLoanedAmount()).toStandardString());
			json.put("code", 1);
			json.put("message", "查询项目的信息成功");
			json.put("projectInfo", AppCommonUtil.cleanNull(map));
		} else {
			json.put("code", 0);
			json.put("message", "未查到该项目的信息");
		}
		return json;
		
	}
	
	/**
	 * 3、 借款人详情
	 * 
	 * @param demandId
	 * @return
	 * @throws IOException
	 * @throws Exception
	 * 
	 */
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping("loanerInfo.htm")
	public JSONObject getLoanerInfo(long demandId, HttpServletResponse response) throws IOException {
		JSONObject json = new JSONObject();
		Map<String, String> map = new HashMap<String, String>();
		LoanDemandInfo demandInfo = loanDemandQueryService.loadLoanDemandInfo(demandId);
		LoanerBaseInfo loanerBaseInfo = loanerBaseInfoService
			.findByUserId(demandInfo.getLoanerId());
		if (loanerBaseInfo != null) {
			map = BeanMap.create(loanerBaseInfo);
			map = getLoanerExtendInfo(map);
			String viewUserName = loanerBaseInfo.getLoanerUserName();
			map.put("showLoanerUserName", viewUserName);
			if (StringUtil.isNotEmpty(viewUserName)) {
				viewUserName = loanerBaseInfo.getLoanerUserName().substring(0, 1) + "**";
			}
			map.put("loanerUserName", viewUserName);
			UserInfo userInfo = userQueryService.queryByUserId(loanerBaseInfo.getUserId())
				.getQueryUserInfo();
			if (userInfo != null) {
				json.put("userType", userInfo.getType());
			} else {
				json.put("userType", "GR");
			}
			
			try {
				List<LoanerExtendDescriptionDO> extendsinfo = loanerExtendService.findAll();
				Map<String, String> map1 = new HashMap<String, String>();
				map1.put("demo", "这里是对扩展字段的注释");
				for (LoanerExtendDescriptionDO infos : extendsinfo) {
					map1.put(infos.getUsedName(), infos.getDescription());
				}
				json.put("dec", map1);
			} catch (Exception e) {
				
			}
			
			json.put("list", AppCommonUtil.cleanNull(map));
			json.put("certUrl", getLoanerInfoUrl(demandId));
			json.put("code", 1);
			json.put("message", "查询借款人的信息成功");
			
		} else {
			json.put("code", 0);
			json.put("message", "未查到该项目的借款人信息");
		}
		return json;
		
	}
	
	private List<String> getLoanerInfoUrl(long demandId) {
		// 项目附件
		String requestPath = "";
		List<String> list = new ArrayList<>();
		CommonAttachmentQueryOrder attachmentQueryOrder = new CommonAttachmentQueryOrder();
		attachmentQueryOrder.setBizNo(demandId + "");
		attachmentQueryOrder.setModuleTypeList(YrdEnumUtil.getAttachmentByIndustry());
		QueryBaseBatchResult<CommonAttachmentInfo> batchResult = commonAttachmentService
			.queryCommonAttachment(attachmentQueryOrder);
		for (CommonAttachmentInfo attachmentInfo : batchResult.getPageList()) {
			if (attachmentInfo.getModuleType() == CommonAttachmentTypeEnum.LOAN_MANAGEMENT) {
				requestPath = attachmentInfo.getRequestPath();
				if (StringUtil.isNotEmpty(requestPath)) {
					list.add(requestPath);
				}
			}
		}
		return list;
	}
	
	private Map<String, String> getLoanerExtendInfo(Map<String, String> map) {
		
		Map<String, String> resMap = new HashMap<String, String>();
		
		Object sMap[] = map.keySet().toArray();
		for (int i = 0; i < map.size(); i++) {
			String key = (String) sMap[i];
			Object value = map.get(sMap[i]);
			if (value != null) {
				String class0 = String.valueOf(value.getClass());
				if (class0.indexOf("Date") <= -1) {
					resMap.put(key, String.valueOf(value));
				}
				
			} else {
				resMap.put(key, "");
			}
			
		}
		try {
			List<LoanerExtendDescriptionDO> extendsinfo = loanerExtendService.findAll();
			for (LoanerExtendDescriptionDO infos : extendsinfo) {
				try {
					String key = infos.getUsedName();
					String value = resMap.get(infos.getOriginalName());
					resMap.put(key, value);
					resMap.remove(infos.getOriginalName());
				} catch (Exception e) {
					logger.error("查询借款人信息扩展表失败");
				}
				
			}
		} catch (Exception e) {
			
		}
		
		return resMap;
	}
	
	@RequestMapping("getNotify")
	public String getNotify() {
		return "/test/notify.html";
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
