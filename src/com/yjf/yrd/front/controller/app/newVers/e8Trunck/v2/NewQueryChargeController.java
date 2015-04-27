package com.yjf.yrd.front.controller.app.newVers.e8Trunck.v2;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.yjf.common.lang.util.DateUtil;
import com.yjf.common.lang.util.ListUtil;
import com.yjf.yrd.base.UserAccountInfoBaseController;
import com.yjf.yrd.integration.openapi.CustomerService;
import com.yjf.yrd.integration.openapi.IncomOutcomQueryService;
import com.yjf.yrd.integration.openapi.info.DepositInfo;
import com.yjf.yrd.integration.openapi.info.IncomeOutcomeInfoList;
import com.yjf.yrd.integration.openapi.info.QueryWithdrawInfo;
import com.yjf.yrd.integration.openapi.order.QueryDepositOrder;
import com.yjf.yrd.integration.openapi.order.QueryIncomOutcomOrder;
import com.yjf.yrd.integration.openapi.order.WithdrawQueryOrder;
import com.yjf.yrd.integration.openapi.result.DepositQueryResult;
import com.yjf.yrd.integration.openapi.result.QueryIncomOutcomResult;
import com.yjf.yrd.integration.openapi.result.WithdrawQueryResult;
import com.yjf.yrd.session.SessionLocalManager;
import com.yjf.yrd.util.StringUtil;

/**
 * 
 * 
 * @Filename NewQueryChargeController.java
 * 
 * @Description
 * 
 * @Version 2.0
 * 
 * @Author zhaohaibing
 * 
 * @Email abing@yiji.com
 * 
 * @Date: 2015-1-13
 * 
 * 
 */

@Controller
@RequestMapping("app")
public class NewQueryChargeController extends UserAccountInfoBaseController {

	@Autowired
	CustomerService customerService;
	@Autowired
	IncomOutcomQueryService incomOutcomQueryService;

	/**
	 * app-收支明细查询
	 * 
	 * @Param
	 * @param
	 * @throws ParseException
	 * @throws IOException
	 * */
	@ResponseBody
	@RequestMapping("incomOutcomQuery.htm")
	public JSONObject incomOutcomQuery(HttpSession session,
			QueryIncomOutcomOrder queryOrder, Integer pageNumber, Model model)
			throws ParseException, IOException {
		JSONObject json = new JSONObject();
		if (!AppCommonUtil.isLogin()) {
			json.put("code", -1);
			json.put("message", "未登录或登录已失效");
			return json;
		}
		queryOrder.setUserId(SessionLocalManager.getSessionLocal()
				.getAccountId());
		queryOrder.setBeginDate(getAnyMonthBegin(3));
		queryOrder.setEndTime(getAnyMonthBegin(-1));
		if (pageNumber != null) {
			queryOrder.setPageNo(pageNumber);
		}
		QueryIncomOutcomResult result = incomOutcomQueryService
				.IncomOutcomQuery(queryOrder, this.getOpenApiContext());
		List<Map<String, String>> queryList = new ArrayList<Map<String, String>>();
		if (result.isSuccess()) {
			double totalIncome = 0.00;
			double totalOutcome = 0.00;
			if (ListUtil.isNotEmpty(result.getIncomeOutcomeInfoList())) {
				for (IncomeOutcomeInfoList info : result
						.getIncomeOutcomeInfoList()) {
					Map<String, String> map = new HashMap<String, String>();
					map.put("time",
							DateUtil.dtSimpleFormat(getDate(info
									.getTradeBeginTime())) + "");
					if (info.getDirection().equals("OUTCOME")) {
						map.put("inCome", "0.00");
						map.put("outCome", "-"
								+ info.getTransAmount().toStandardString());
						String money = info.getTransAmount().toString();
						totalOutcome += Double.parseDouble(money);
					} else if (info.getDirection().equals("INCOME")) {
						map.put("inCome", "+"
								+ info.getTransAmount().toStandardString());
						map.put("outCome", "0.00");
						String money = info.getTransAmount().toString();
						totalIncome += Double.parseDouble(money);
					} else {
						map.put("inCome", "未知");
						map.put("outCome", "未知");
					}
					map.put("tradeType", info.getTradeType());
					map.put("memo", info.getTradeType() + "|" + info.getMemo());
					map.put("balance", info.getBalance().toStandardString());
					queryList.add(AppCommonUtil.cleanNull(map));
				}
				json.put("code", 1);
				json.put("message", "查询成功");
				json.put("totalIncome", totalIncome);
				json.put("totalOutcome", totalOutcome);
				json.put("totalCount", result.getTotalCount());
				int pagecount = 0;
				if (result.getTotalCount() > 0) {
					pagecount = result.getTotalPageCount();
				}
				json.put("totalPage", pagecount);
				json.put("queryList", queryList);
			} else {
				json.put("code", 1);
				json.put("message", "暂无数据");
				json.put("totalIncome", totalIncome);
				json.put("totalOutcome", totalOutcome);
				json.put("totalCount", 0);
				json.put("totalPage", 0);
				json.put("queryList", queryList);
			}

		} else {
			json.put("code", 0);
			json.put(
					"message",
					StringUtil.isNotEmpty(result.getMessage()) ? result
							.getMessage() : "查询失败");
		}

		return json;

	}

	/**
	 * app-提现查询接口
	 * 
	 * @Param pageSize
	 * @param currPage
	 * @throws ParseException
	 * */
	@ResponseBody
	@RequestMapping("queryWithdrawals.htm")
	public JSONObject queryWithdrawalsRecordpenApi(String startTimes,
			String endTimes, WithdrawQueryOrder queryOrder, Integer pageNumber,
			HttpServletResponse response) throws IOException, ParseException {
		JSONObject json = new JSONObject();
		if (!AppCommonUtil.isLogin()) {
			json.put("code", -1);
			json.put("message", "未登录或登录已失效");
			return json;
		}
		queryOrder.setStartTime(getDate(startTimes));
		queryOrder.setEndTime(getDate(endTimes));
		if (StringUtil.isNotEmpty(startTimes)) {
			queryOrder.setStartTime(getDate(startTimes));
		}
		if (StringUtil.isNotEmpty(endTimes)) {
			queryOrder.setEndTime(getDate(endTimes));
		}
		if (pageNumber != null) {
			queryOrder.setCurrPage(pageNumber);
		}
		logger.info("查询提现记录,入参：{}", queryOrder);
		WithdrawQueryResult queryResult = null;
		List<Map<String, String>> WithdrawInfo = new ArrayList<Map<String, String>>();
		queryOrder.setUserId(SessionLocalManager.getSessionLocal()
				.getAccountId());
		try {
			queryResult = this.withdrawQueryService.queryWithdrawService(
					queryOrder, this.getOpenApiContext());
		} catch (Exception e) {
			logger.error("queryWithdrawService is error", e);
		}
		if (queryResult.isSuccess()) {
			if (ListUtil.isEmpty(queryResult.getWithdrawInfos())) {
				json.put("code", 1);
				json.put("message", "当前时间段暂无提现记录");
				return json;
			}
			for (QueryWithdrawInfo info : queryResult.getWithdrawInfos()) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("time", DateUtil.simpleFormat(info.getPayTime()));
				map.put("amount", info.getAmout().toStandardString());
				map.put("status", info.getStatus());
				map.put("payNo", info.getPayNo());
				map.put("payChannel", info.getBankName());
				WithdrawInfo.add(AppCommonUtil.cleanNull(map));
			}
			json.put("code", 1);
			json.put("message", "提现记录查询成功");

		} else {
			json.put("code", 0);
			json.put("message", queryResult.getMessage());
		}
		json.put(
				"totalPage",
				AppCommonUtil.getTotalPage(queryResult.getCount(),
						queryOrder.getPageSize()));
		json.put("WithdrawInfo", WithdrawInfo);
		return json;

	}

	/**
	 * app-充值查询接口
	 * 
	 * @Param pageSize
	 * @param currPage
	 * @throws IOException
	 * @throws ParseException
	 * 
	 * */
	@ResponseBody
	@RequestMapping("queryCharge.htm")
	public JSONObject queryRechargeRecordByOpenApi(String startTimes,
			Integer pageNumber, String endTimes, QueryDepositOrder queryOrder,
			HttpServletResponse response) throws IOException, ParseException {

		JSONObject json = new JSONObject();
		if (!AppCommonUtil.isLogin()) {
			json.put("code", -1);
			json.put("message", "未登录或登录已失效");
			return json;
		}

		queryOrder.setStartTime(getDate(startTimes));
		queryOrder.setEndTime(getDate(endTimes));
		if (pageNumber != null) {
			queryOrder.setCurrPage(pageNumber);
		}
		logger.info("查询网银充值记录,入参是：{}", queryOrder);
		DepositQueryResult queryResult = null;
		List<Map<String, String>> chargeRecord = new ArrayList<Map<String, String>>();
		queryOrder.setUserId(SessionLocalManager.getSessionLocal()
				.getAccountId());
		try {
			queryResult = this.depositQueryService.depositQueryService(
					queryOrder, this.getOpenApiContext());
		} catch (Exception e) {
			logger.error("queryWithdrawService is error", e);
		}
		if (queryResult.isSuccess()) {
			if (ListUtil.isEmpty(queryResult.getDepositInfos())) {
				json.put("code", 1);
				json.put("message", "当前时间段暂无充值记录");
				return json;
			}
			for (DepositInfo info : queryResult.getDepositInfos()) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("time", DateUtil.simpleFormat(info.getPayTime()));
				map.put("amount", info.getAmount().toStandardString());
				map.put("status", info.getStatus());
				map.put("payNo", info.getPayNo());
				map.put("memo", info.getMemo());
				map.put("payChannel", info.getBankName());
				chargeRecord.add(AppCommonUtil.cleanNull(map));
			}
			json.put("code", 1);
			json.put("message", "充值记录查询成功");

		} else {
			json.put("code", "0");
			json.put("message", queryResult.getMessage());
		}
		json.put(
				"totalPage",
				AppCommonUtil.getTotalPage(queryResult.getCount(),
						queryOrder.getPageSize()));
		json.put("chargeRecord", chargeRecord);
		return json;

	}

	/**
	 * 获取N个月前的时间
	 */
	public String getAnyMonthBegin(int n) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - n);
		return DateUtil.simpleFormat(cal.getTime());
	}

	/**
	 * 将String转化成Date
	 * 
	 * @return Date
	 * @throws ParseException
	 */
	private java.util.Date getDate(String dString) throws ParseException {
		if (StringUtil.isNotEmpty(dString)) {
			return DateUtil.getFormat("yyyy-MM-dd").parse(dString);
		}
		return null;
	}

}
