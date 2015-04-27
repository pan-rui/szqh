package com.yjf.yrd.front.controller.app.newVers.e8Trunck;

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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.yjf.common.lang.util.DateUtil;
import com.yjf.common.lang.util.ListUtil;
import com.yjf.yrd.front.controller.trade.base.LoanTradeDetailBaseController;
import com.yjf.yrd.rs.util.RateUtil;
import com.yjf.yrd.service.query.trade.TradeBizQueryService;
import com.yjf.yrd.util.MoneyUtil;
import com.yjf.yrd.util.StringUtil;
import com.yjf.yrd.ws.enums.TradeStatusEnum;
import com.yjf.yrd.ws.info.LoanDemandInfo;
import com.yjf.yrd.ws.info.LoanDemandTradeVOInfo;
import com.yjf.yrd.ws.info.LoanerBaseInfo;
import com.yjf.yrd.ws.info.TradeInfo;
import com.yjf.yrd.ws.service.query.order.QueryLoanTradeOrder;
import com.yjf.yrd.ws.service.query.result.QueryBaseBatchResult;

/**
 * 
 * 
 * @Filename appNewBootController.java
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
public class AppNewBootController extends LoanTradeDetailBaseController {
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private TradeBizQueryService tradeBizQueryService;

	/**
	 * 1、项目列表
	 * 
	 * @param currentPage
	 * @param pageSize
	 * @param bizType
	 * @param status
	 * @param guarantee
	 * @return
	 * @throws IOException
	 */
	@ResponseBody
	@RequestMapping("appProjectList.htm")
	public JSONObject staticAPPIndex(int pageSize, int currentPage,
			Integer status, String guarantee, Model model,
			HttpServletResponse response) throws IOException {
		logger.info("新版手机app进入项目列表查询。。");
		JSONObject json = new JSONObject();
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		this.initAccountInfo(model);
		List<String> statusList = new ArrayList<String>();
		intiStatusCond(status, statusList);
		QueryLoanTradeOrder tradeQueryOrder = new QueryLoanTradeOrder();
		tradeQueryOrder.setPageNumber(currentPage);
		tradeQueryOrder.setPageSize(pageSize);
		tradeQueryOrder.setStatus(statusList);
		tradeQueryOrder.setGuaranteeName(guarantee);

		QueryBaseBatchResult<LoanDemandTradeVOInfo> batchResult = tradeBizQueryService
				.searchLoanTradeCommonQuery(tradeQueryOrder);

		if (batchResult.isSuccess()
				&& ListUtil.isNotEmpty(batchResult.getPageList())) {
			for (LoanDemandTradeVOInfo projectInfo : batchResult.getPageList()) {
				Map<String, String> map = new HashMap<String, String>();
				int statues = projectInfo.getTradeStatus().getValue();
				double needMoey = MoneyUtil.getMoneyByw(
						projectInfo.getTradeAmount(),
						projectInfo.getLoanedAmount()) * 10000;
				boolean investAvlTimeReached = (new Date().after(projectInfo
						.getInvestAvlDate()));
				String sta = null;
				if (1 == statues && needMoey > 0 && investAvlTimeReached) {
					sta = "1";// 正在投资
				} else if (!investAvlTimeReached) {
					sta = "3";// 未到时间
				} else if (7 == statues || 3 == statues) {
					sta = "4";// 交易完成
				} else if (8 == statues) {
					sta = "5";// 等待还款
				} else if (5 == statues) {
					sta = "8";// 交易失败
				} else {
					sta = "2";// 投资已满
				}
				String isDirectional = "0";
				if (StringUtil.isNotEmpty(projectInfo.getLoanPassword()))
					isDirectional = "1";
				map.put("projectName", projectInfo.getLoanName());
				map.put("projectId", "" + projectInfo.getDemandId());
				map.put("borrowMoney", projectInfo.getTradeAmount()
						.toStandardString());
				map.put("promiseInstitution",
						"" + projectInfo.getGuaranteeName());
				map.put("timeLimit", "" + projectInfo.getTimeLimit()
						+ projectInfo.getTimeLimitUnit().getViewName());
				map.put("yearRate",
						"" + RateUtil.getRate(projectInfo.getInterestRate()));
				map.put("investProgress",
						StringUtil.replace(MoneyUtil.getPercentage(
								projectInfo.getLoanedAmount(),
								projectInfo.getLoanAmount(),
								projectInfo.getLeastInvestAmount()), "%", ""));
				map.put("state", sta);
				map.put("isDirectional", isDirectional);
				map.put("startTime",
						DateUtil.simpleFormat(projectInfo.getInvestAvlDate()));

				map.put("purpose", projectInfo.getLoanPurpose());
				list.add(map);
			}
			long totalPage = 0;
			if (batchResult.getTotalCount() % pageSize == 0) {
				totalPage = batchResult.getTotalCount() / pageSize;
			} else {
				totalPage = batchResult.getTotalCount() / pageSize + 1;
			}
			json.put("totalPage", totalPage);
			json.put("code", "1");
			json.put("message", "项目列表查询成功");
		} else {
			json.put("code", "1");
			json.put("message", "没有查询到对应的项目列表信息");
			json.put("totalPage", "0");
		}
		logger.info("手机app进入项目查询结果{}：", list);
		json.put("list", list);

		return json;

	}

	/**
	 * 2、 项目详情
	 * 
	 * @param projectId
	 * @return
	 * @throws Exception
	 *             projectDetail
	 * 
	 */
	@ResponseBody
	@RequestMapping("appProjectDetail.htm")
	public JSONObject lookupapp(long projectId, HttpServletResponse response)
			throws Exception {

		JSONObject json = new JSONObject();
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		if (projectId > 0) {
			LoanDemandInfo result = loanDemandQueryService
					.loadLoanDemandInfo(projectId);
			TradeInfo projectInfo = tradeBizQueryService
					.getByLoanDemandId(projectId);

			Map<String, String> map = new HashMap<String, String>();
			int statues = projectInfo.getTradeStatus().getValue();
			double needMoey = MoneyUtil
					.getMoneyByw(projectInfo.getTradeAmount(),
							projectInfo.getLoanedAmount()) * 10000;
			boolean investAvlTimeReached = (new Date().after(result
					.getInvestAvlDate()));
			String sta = null;
			if (1 == statues && needMoey > 0 && investAvlTimeReached) {
				sta = "1";// 正在投资
			} else if (!investAvlTimeReached) {
				sta = "3";// 未到投资时间
			} else if (7 == statues || 8 == statues || 3 == statues) {
				sta = "4";// 交易完成
			} else if (8 == statues) {
				sta = "5";// 等待还款
			} else if (5 == statues) {
				sta = "8";// 交易失败
			} else {
				sta = "2";// 投资已满
			}
			String isDirectional = "0";
			if (StringUtil.isNotEmpty(result.getLoanPassword()))
				isDirectional = "1";
			map.put("state", sta);
			map.put("isDirectional", isDirectional);
			map.put("promiseInstitution", result.getGuaranteeName());
			map.put("borrowMoney", result.getLoanAmount().toStandardString());
			map.put("investProgress", StringUtil.replace(MoneyUtil
					.getPercentage(projectInfo.getLoanedAmount(),
							projectInfo.getTradeAmount(),
							projectInfo.getLeastInvestAmount()), "%", ""));
			map.put("projectName", projectInfo.getTradeName());
			map.put("yearRate", RateUtil.getRate(projectInfo.getInterestRate()));
			map.put("payMethod", result.getRepayDivisionWay().getMessage());
			map.put("deadline",
					"" + DateUtil.dtSimpleFormat(projectInfo.getDeadline()));
			map.put("timeLimit", "" + projectInfo.getTimeLimit()
					+ projectInfo.getTimeLimitUnit().getViewName());
			map.put("startMoney", "" + projectInfo.getLeastInvestAmount());
			map.put("promiseNum", "" + result.getGuaranteeLicenceNo());
			map.put("tradeId", "" + projectInfo.getTradeId());
			map.put("projectId", String.valueOf(projectInfo.getDemandId()));

			list.add(map);
			json.put("code", "1");
			json.put("message", "项目详情查询成功");
			logger.info("app查询项目详情成功：demandId={},list={}", projectId, list);
		} else {
			json.put("code", "0");
			json.put("message", "没有查询到对应的项目信息");
			logger.info("新版app用户请求获取项目失败：demandId={}:id不正确", projectId);
		}
		json.put("ProjectDetail", list);

		return json;

	}

	/**
	 * 3、 借款详情
	 * 
	 * @param projectId
	 * @return
	 * @throws Exception
	 *             projectDetail
	 * 
	 */
	@RequestMapping("appLoandemandInfo.htm")
	public JSONObject appLoandemandInfo(long projectId,
			HttpServletResponse response) throws Exception {
		JSONObject json = new JSONObject();
		if (projectId > 0) {
			LoanDemandInfo result = loanDemandQueryService
					.loadLoanDemandInfo(projectId);
			TradeInfo trade = tradeBizQueryService.getByLoanDemandId(projectId);
			LoanerBaseInfo loanerBaseInfo = loanerBaseInfoService
					.findByUserId(result.getLoanerId());
			Map<String, String> map = new HashMap<String, String>();
			try {
				map.put("userName", loanerBaseInfo.getLoanerUserName() + "");
				map.put("realName", loanerBaseInfo.getLoanerRealName() + "");
				map.put("sex", loanerBaseInfo.getGender() + "");
				map.put("age", String.valueOf(loanerBaseInfo.getAge()));
			} catch (Exception e) {
				map.put("userName", "");
				map.put("realName", "");
				map.put("sex", "");
				map.put("age", "");
			}
			map.put("purpose", result.getLoanPurpose());
			map.put("payMethod", result.getRepayDivisionWay().getMessage());
			map.put("investedMoney", trade.getLoanedAmount().toStandardString());
			map.put("loandAmount", trade.getLoanedAmount().toStandardString());
			map.put("realName", result.getLoanerName());
			map.put("needMoney",
					trade.getTradeAmount().subtract(trade.getLoanedAmount())
							.toStandardString());
			map.put("borrowState", trade.getTradeStatus().message());
			map.put("startTime",
					DateUtil.simpleFormat(result.getInvestAvlDate()));
			map.put("endTime", DateUtil.simpleFormat(result.getDeadline()));
			map.put("guarantyInstitution", result.getGuaranteeName());
			map.put("guarantyWay", "100%本息保障");
			json.put("code", "1");
			json.put("message", "获取借款详情成功");
			json.put("demandInfo", map);
			logger.info("新版app用户获取项目的借款情况成功：demandInfo={}", map);
		} else {
			json.put("code", "0");
			json.put("message", "获取借款详情失败");
			logger.info("新版app用户获取项目的借款情况失败：无此项目");
		}

		return json;

	}

	private void intiStatusCond(Integer status, List<String> statusList) {
		if (status != null && status > 0) {
			if (status == 1) {
				statusList.add(String.valueOf(TradeStatusEnum.TRADING
						.getValue()));
				statusList.add(String.valueOf(TradeStatusEnum.REPAYING
						.getValue()));
				statusList
						.add(String.valueOf(TradeStatusEnum.GUARANTEE_AUDITING
								.getValue()));
				statusList.add(String.valueOf(TradeStatusEnum.REPAY_FINISH
						.getValue()));
				statusList.add(String
						.valueOf(TradeStatusEnum.COMPENSATORY_REPAY_FINISH
								.getValue()));
			} else {
				statusList.add(String.valueOf(TradeStatusEnum.REPAY_FINISH
						.getValue()));
				statusList.add(String
						.valueOf(TradeStatusEnum.COMPENSATORY_REPAY_FINISH
								.getValue()));
			}
		} else {
			statusList.add(String.valueOf(TradeStatusEnum.REPAY_FINISH
					.getValue()));
			statusList.add(String
					.valueOf(TradeStatusEnum.COMPENSATORY_REPAY_FINISH
							.getValue()));

		}
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
