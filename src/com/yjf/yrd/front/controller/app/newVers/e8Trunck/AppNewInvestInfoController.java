package com.yjf.yrd.front.controller.app.newVers.e8Trunck;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.yjf.common.lang.util.ListUtil;
import com.yjf.common.lang.util.money.Money;
import com.yjf.yrd.base.UserAccountInfoBaseController;
import com.yjf.yrd.rs.util.RateUtil;
import com.yjf.yrd.session.SessionLocalManager;
import com.yjf.yrd.user.IOperatorInfoService;
import com.yjf.yrd.util.DateUtil;
import com.yjf.yrd.util.MoneyUtil;
import com.yjf.yrd.util.StringUtil;
import com.yjf.yrd.ws.enums.DivisionPhaseEnum;
import com.yjf.yrd.ws.enums.SysUserRoleEnum;
import com.yjf.yrd.ws.info.LoanDemandInfo;
import com.yjf.yrd.ws.info.TradeDetailInfo;
import com.yjf.yrd.ws.info.TradeDetailVOInfo;
import com.yjf.yrd.ws.order.TradeDetailQueryOrder;
import com.yjf.yrd.ws.service.query.order.QueryTradeDetailUserOrder;
import com.yjf.yrd.ws.service.query.result.QueryBaseBatchResult;
import com.yjf.yrd.ws.service.query.result.TradeDetailBatchResult;

/**
 * @Filename appNewInvestInfoController.java
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
 */
@Controller
@RequestMapping("appNew")
public class AppNewInvestInfoController extends UserAccountInfoBaseController {

	@Autowired
	protected IOperatorInfoService operatorInfoService;

	/**
	 * 4、 投资明细
	 * 
	 * @param tradeId
	 * @param pageSize
	 * @param currentPage
	 * @throws Exception
	 *             investDetail
	 */
	@ResponseBody
	@RequestMapping("appInvestDetail.htm")
	public JSONObject lookup(long tradeId, int pageSize, int currentPage,
			HttpServletResponse response) throws Exception {
		JSONObject json = new JSONObject();
		List<Map<String, String>> investDetail = new ArrayList<Map<String, String>>();
		logger.info("手机app进入投资详情查询:pageSize={}", pageSize, "currentPage={}",
				"tradeId={}", tradeId);
		QueryTradeDetailUserOrder queryTradeDetailUserOrder = new QueryTradeDetailUserOrder();
		queryTradeDetailUserOrder.setTradeId(tradeId);
		queryTradeDetailUserOrder.setRoleEnum(SysUserRoleEnum.INVESTOR);
		queryTradeDetailUserOrder
				.setTransferPhase(DivisionPhaseEnum.ORIGINAL_PHASE);
		queryTradeDetailUserOrder.setPageSize(pageSize);
		queryTradeDetailUserOrder.setPageNumber(currentPage);
		QueryBaseBatchResult<TradeDetailInfo> batchResult = tradeBizQueryService
				.queryTradeDetailUserPage(queryTradeDetailUserOrder);
		if (batchResult.isSuccess()
				&& ListUtil.isNotEmpty(batchResult.getPageList())) {
			for (TradeDetailInfo projectInfo : batchResult.getPageList()) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("name", StringUtil.subString(projectInfo.getRealName()
						.toString(), 1, "****"));
				map.put("money", projectInfo.getAmount().toString());
				map.put("time",
						DateUtil.simpleFormat(projectInfo.getCreateDate())
								.toString());
				investDetail.add(map);
			}
			String totalPage = "0";
			if (batchResult.getTotalCount() % pageSize == 0) {
				totalPage = batchResult.getTotalCount() / pageSize + "";
			} else {
				totalPage = batchResult.getTotalCount() / pageSize + 1 + "";
			}
			json.put("code", "1");
			json.put("totalPage", totalPage);
			json.put("message", "获取投资详情成功");
		} else {
			json.put("code", "1");
			json.put("totalPage", "0");
			json.put("message", "暂无投资");
		}
		logger.info("手机app进入投资详情查询结果{}：", investDetail);
		json.put("list", investDetail);
		return json;

	}

	// -----------投资人---投资的项目------------------------------------------------------------------------------------------
	/**
	 * 11、 投资的项目
	 * 
	 * @param pageSize
	 * @param currentPage
	 */
	@ResponseBody
	@RequestMapping("appInvestProject.htm")
	public JSONObject investProject(long pageSize, long currentPage,
			String startDate, String endDate, HttpServletResponse response)
			throws IOException {
		logger.info("新版app请求获取投资的项目！");
		JSONObject json = new JSONObject();
		List<Map<String, String>> investProjectList = new ArrayList<Map<String, String>>();
		if (isLogin()) {
			TradeDetailQueryOrder queryOrder = new TradeDetailQueryOrder();
			queryOrder.setPageSize(pageSize);
			queryOrder.setPageNumber(currentPage);
			queryOrder.setUserId(SessionLocalManager.getSessionLocal()
					.getUserId());
			queryOrder.setRoleId(SysUserRoleEnum.INVESTOR.getValue());
			queryOrder
					.setTransferPhase(DivisionPhaseEnum.ORIGINAL_PHASE.code());
			List<String> statusList = new ArrayList<String>();
			// statusList.add(String.valueOf(-1));
			queryOrder.setStatus(statusList);
			queryOrder.setStartDate(startDate);
			queryOrder.setEndDate(endDate);
			TradeDetailBatchResult<TradeDetailVOInfo> batchResult = tradeBizQueryService
					.searchTradeDetailQuery(queryOrder);

			if (batchResult.isSuccess()
					&& ListUtil.isNotEmpty(batchResult.getPageList())) {
				for (TradeDetailVOInfo projectInfo : batchResult.getPageList()) {
					Map<String, String> map = new HashMap<String, String>();
					map.put("projectId", "" + projectInfo.getDemandId());
					map.put("projectName", projectInfo.getTradeName());
					map.put("money", projectInfo.getBenefitAmount()
							.toStandardString());
					map.put("date", DateUtil.dtSimpleFormat(projectInfo
							.getCreateDate()));
					map.put("state", projectInfo.getTradeStatus().getMessage());
					map.put("profit",
							getBenifit(projectInfo.getBenefitAmount(),
									projectInfo.getInterestRate(), projectInfo
											.getTimeLimit(), projectInfo
											.getTimeLimitUnit().code()));
					map.put("rate",
							RateUtil.getRate(projectInfo.getInterestRate())
									+ "");
					investProjectList.add(map);
				}

				String totalPage = "0";
				if (batchResult.getTotalCount() % pageSize == 0) {
					totalPage = batchResult.getTotalCount() / pageSize + "";
				} else {
					totalPage = batchResult.getTotalCount() / pageSize + 1 + "";
				}
				json.put("totalPage", totalPage);
				json.put("investProjectList", investProjectList);
				// json.put("totalInvestMoney",
				// batchResult.getTotalAmount().toStandardString());
				json.put("code", "1");
				json.put("message", "投资的项目查询成功!");
				logger.info("新版app用户({})：请求获取投资的项目成功:list={}",
						SessionLocalManager.getSessionLocal().getUserName(),
						investProjectList);

			} else {
				json.put("code", "1");
				json.put("message", "暂无投资项目信息");
				json.put("totalSize", "0");
				logger.info("新版app用户({}):请求获取投资的项目成功:list={}",
						SessionLocalManager.getSessionLocal().getUserName(),
						"暂无投资项目信息");
			}

		} else {
			json.put("code", "-1");
			json.put("message", "未登录或登录已失效");
		}

		return json;

	}

	/**
	 * 获取承诺函信息
	 * 
	 * @throws IOException
	 * 
	 * */
	@ResponseBody
	@RequestMapping("getPromiseInfo.htm")
	public JSONObject getChengNuoInfo(int projectId,
			HttpServletResponse response) throws IOException {
		Map<String, String> map = new HashMap<String, String>();
		JSONObject json = new JSONObject();
		if (projectId > 0) {
			LoanDemandInfo result = loanDemandQueryService
					.loadLoanDemandInfo(projectId);
			map.put("Institution", "" + result.getGuaranteeName());
			map.put("url", "" + result.getGuaranteeLicenseUrl());
			json.put("code", "1");
			json.put("message", "获取承诺信息成功");
			json.put("chengNuoInfo", map);
		} else {
			json.put("code", "0");
			json.put("message", "获取承诺信息失败");
		}

		return json;

	}

	// ------------------------------------其他方法--------------------------------------------------------------------

	/**
	 * 收益计算
	 * 
	 * @param string
	 * */

	private String getBenifit(Money money, double rate, int time, String unit) {
		double timtLimit = 0;
		if (unit.equals("D")) {
			timtLimit = time / 365d;
		}
		if (unit.equals("W") || unit.equals("M")) {
			timtLimit = time / 12d;
		}
		if (unit.equals("Y")) {
			timtLimit = time;
		}
		double bennifit = MoneyUtil.getMoneyByw(money) * 10000 * rate
				* timtLimit;
		return String.format("%.2f", bennifit);

	}

	/**
	 * 判断是否登录
	 * 
	 * @return boolean
	 */
	public boolean isLogin() {
		try {
			if (SessionLocalManager.getSessionLocal().getUserName().toString() != null) {
				logger.info("手机app查询是否登录！");
				return true;
			}
		} catch (NullPointerException e) {
			logger.error("判断是否登录出现空指针异常:", e);
		}
		return false;
	}

}
