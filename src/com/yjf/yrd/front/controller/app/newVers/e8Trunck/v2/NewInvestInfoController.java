package com.yjf.yrd.front.controller.app.newVers.e8Trunck.v2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
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
 * @Filename appInvestInfoController.java
 * 
 * @Description
 * 
 * @Version 2.0
 * 
 * @Author zhaohaibing
 * 
 * @Email abing@yiji.com
 * 
 * @Date: 2014-01-08
 * 
 */
@Controller
@RequestMapping("app")
public class NewInvestInfoController extends UserAccountInfoBaseController {
	
	@Autowired
	protected IOperatorInfoService operatorInfoService;
	
	/**
	 * 4、 投资明细
	 * 
	 * @param tradeId
	 * @param pageSize
	 * @param currentPage
	 * @throws Exception
	 * 
	 */
	@ResponseBody
	@RequestMapping("appInvestDetail.htm")
	public JSONObject appInvestDetail(long tradeId, int pageSize, int pageNumber,
										HttpServletResponse response) throws Exception {
		QueryTradeDetailUserOrder queryTradeDetailUserOrder = new QueryTradeDetailUserOrder();
		queryTradeDetailUserOrder.setTradeId(tradeId);
		queryTradeDetailUserOrder.setRoleEnum(SysUserRoleEnum.INVESTOR);
		queryTradeDetailUserOrder.setTransferPhase(DivisionPhaseEnum.ORIGINAL_PHASE);
		queryTradeDetailUserOrder.setPageSize(pageSize);
		queryTradeDetailUserOrder.setPageNumber(pageNumber);
		logger.info("手机app进入投资详情查询:queryTradeDetailUserOrder={}", queryTradeDetailUserOrder);
		QueryBaseBatchResult<TradeDetailInfo> batchResult = tradeBizQueryService
			.queryTradeDetailUserPage(queryTradeDetailUserOrder);
		JSONObject json = new JSONObject();
		List<Map<String, String>> investDetail = new ArrayList<Map<String, String>>();
		if (batchResult.isSuccess() && ListUtil.isNotEmpty(batchResult.getPageList())) {
			for (TradeDetailInfo projectInfo : batchResult.getPageList()) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("realName",
					StringUtil.subString(projectInfo.getRealName().toString(), 1, "***"));
				map.put("amount", projectInfo.getAmount().toString());
				map.put("createDate", DateUtil.simpleFormat(projectInfo.getCreateDate()).toString());
				investDetail.add(AppCommonUtil.cleanNull(map));
			}
			json.put("code", 1);
			json.put("totalPage", batchResult.getPageCount());
			json.put("message", "获取投资详情成功");
		} else {
			json.put("code", 1);
			json.put("totalPage", 0);
			json.put("message", "暂无投资详情信息");
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
	 * @param pageNumber
	 * @Param startDate eg:2015-01-15
	 * @Param endDate eg:2015-02-15
	 */
	@ResponseBody
	@RequestMapping("appInvestProject.htm")
	public JSONObject appInvestProject(long pageSize, long pageNumber, String startDate,
										String endDate, HttpServletResponse response)
																						throws IOException {
		JSONObject json = new JSONObject();
		if (!AppCommonUtil.isLogin()) {
			json.put("code", -1);
			json.put("message", "登录已失效");
			return json;
			
		}
		TradeDetailQueryOrder queryOrder = new TradeDetailQueryOrder();
		queryOrder.setPageSize(pageSize);
		queryOrder.setPageNumber(pageNumber);
		queryOrder.setUserId(SessionLocalManager.getSessionLocal().getUserId());
		queryOrder.setRoleId(SysUserRoleEnum.INVESTOR.getValue());
		queryOrder.setTransferPhase(DivisionPhaseEnum.ORIGINAL_PHASE.code());
		List<String> statusList = new ArrayList<String>();
		queryOrder.setStatus(statusList);
		queryOrder.setStartDate(startDate);
		queryOrder.setEndDate(endDate);
		logger.info("新版app请求获取投资的项目:queryOrder={}", queryOrder);
		TradeDetailBatchResult<TradeDetailVOInfo> batchResult = tradeBizQueryService
			.searchTradeDetailQuery(queryOrder);
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		if (batchResult.isSuccess() && ListUtil.isNotEmpty(batchResult.getPageList())) {
			for (TradeDetailVOInfo projectInfo : batchResult.getPageList()) {
				Map<String, String> map = new HashMap<String, String>();
				
				int statues = projectInfo.getTradeStatus().getValue();
				double needMoey = MoneyUtil.getMoneyByw(projectInfo.getTradeAmount(),
					projectInfo.getLoanedAmount()) * 10000;
				boolean limitTime = projectInfo.getDeadline().after(new Date());
				String sta = AppCommonUtil.getAppStatus(statues, needMoey, true, limitTime);
				Date date = projectInfo.getTradeCreateDate();// 创建日期
				if (sta == "5" || sta == "7") {
					date = projectInfo.getTradeExpireDate();// 借款到期日期
				} else if (sta == "4") {
					date = projectInfo.getTradeFinishDate();// 完成日期
				}
				map.put("demandId", String.valueOf(projectInfo.getDemandId()));
				map.put("tradeId", String.valueOf(projectInfo.getTradeId()));
				map.put("loanName", projectInfo.getTradeName());
				map.put("benefitAmount", projectInfo.getBenefitAmount().toStandardString());
				map.put("date", DateUtil.dtSimpleFormat(date));
				map.put("status", sta);
				map.put("benifit", projectInfo.getThisBenefit());
				map.put("interestRate", RateUtil.getRate(projectInfo.getInterestRate()));
				list.add(AppCommonUtil.cleanNull(map));
			}
			json.put("totalPage", batchResult.getPageCount());
			json.put("list", list);
			json.put("code", 1);
			json.put("message", "查询投资项目成功");
			
		} else {
			json.put("code", 1);
			json.put("message", "暂无投资项目");
			json.put("totalSize", 0);
			
		}
		logger.info("新版app请求获取投资的项目:batchResult={}", batchResult);
		return json;
	}
	
	/**
	 * 获取承诺函信息
	 * 
	 * @param demandId
	 * @throws IOException
	 * 
	 * */
	@ResponseBody
	@RequestMapping("getPromiseInfo.htm")
	public JSONObject getChengNuoInfo(long demandId, HttpServletResponse response)
																					throws IOException {
		LoanDemandInfo result = loanDemandQueryService.loadLoanDemandInfo(demandId);
		Map<String, String> map = new HashMap<String, String>();
		JSONObject json = new JSONObject();
		if (result != null) {
			map.put("guaranteeName", result.getGuaranteeName());
			map.put("guaranteeLicenseUrl", result.getGuaranteeLicenseUrl());
			json.put("list", AppCommonUtil.cleanNull(map));
			json.put("code", 1);
			json.put("message", "获取承诺信息成功");
		} else {
			json.put("code", 0);
			json.put("message", "获取承诺信息失败");
		}
		return json;
		
	}
	
}
