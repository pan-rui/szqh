package com.yjf.yrd.backstage.controller.statisticsManage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;

import com.yjf.common.lang.util.StringUtil;
import com.yjf.yrd.base.BaseAutowiredController;
import com.yjf.yrd.dataobject.RechargeStatistics;
import com.yjf.yrd.dataobject.viewObject.AmountStatisticsInfoVO;
import com.yjf.yrd.page.Page;
import com.yjf.yrd.page.PageParam;
import com.yjf.yrd.statistics.order.StatisticsQueryOrder;
import com.yjf.yrd.util.CommonUtil;
import com.yjf.yrd.util.DateUtil;
import com.yjf.yrd.ws.enums.PayTypeEnum;

/**
 * 用户数据统计信息查询
 * @author CHARLEY
 *
 */

@Controller
@RequestMapping("backstage")
public class AmountStatisticsController extends BaseAutowiredController {
	/** 通用页面路径 */
	String	VM_PATH	= "/backstage/statistics/";
	
	@Override
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Date.class, "startDate", new CustomDateEditor(
			new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"), true));
		binder.registerCustomEditor(Date.class, "endDate", new CustomDateEditor(
			new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"), true));
	}
	
	@RequestMapping("amountStatistics")
	public String amountStatistics(StatisticsQueryOrder queryOrder, HttpServletResponse response,
									PageParam pageParam, Model model) throws Exception {
		if (StringUtil.isBlank(queryOrder.getQueryType())) {
			queryOrder.setQueryType("yearly");
			model.addAttribute("dimension", "年份");
		} else {
			if ("yearly".equals(queryOrder.getQueryType())) {
				model.addAttribute("dimension", "年份");
			} else if ("monthly".equals(queryOrder.getQueryType())) {
				model.addAttribute("dimension", "月份");
			} else {
				model.addAttribute("dimension", "营销机构名称");
			}
		}
		Page<AmountStatisticsInfoVO> page = amountStatisticsService.amountStatisticsPage(
			queryOrder, pageParam);
		Map<String, Object> conditions = new HashMap<String, Object>();
		if (StringUtil.isBlank(queryOrder.getStartDate())) {
			String startDate = "2013-10-15 00:00:00";
			conditions.put("startDate", startDate);
		} else {
			String startDate = queryOrder.getStartDate() + " 00:00:00";
			conditions.put("startDate", startDate);
		}
		if (StringUtil.isBlank(queryOrder.getEndDate())) {
			String endDate = DateUtil.simpleFormat(new Date()) + " 23:59:59";
			conditions.put("endDate", endDate);
			queryOrder.setEndDate(DateUtil.simpleFormat(new Date()));
		} else {
			String endDate = queryOrder.getEndDate() + " 23:59:59";
			conditions.put("endDate", DateUtil.parse(endDate));
		}
		long totalAmountCount = amountStatisticsService.sumAmountByConditions(conditions);
		model.addAttribute("totalAmountCount", totalAmountCount);
		List<Integer> stats = new ArrayList<Integer>();
		stats.add(3);
		stats.add(7);
		conditions.put("status", stats);
		long totalPaidAmountCount = amountStatisticsService.sumAmountByConditions(conditions);
		stats = new ArrayList<Integer>();
		stats.add(4);
		conditions.put("status", stats);
		long totalFaildAmountCount = amountStatisticsService.sumAmountByConditions(conditions);
		model.addAttribute("totalPaidAmountCount", totalPaidAmountCount);
		model.addAttribute("totalTopayAmountCount", totalAmountCount - totalPaidAmountCount
													- totalFaildAmountCount);
		//清除map
		CommonUtil.clearMap(conditions);
		model.addAttribute("queryOrder", queryOrder);
		model.addAttribute("page", page);
		response.setHeader("Pragma", "No-cache");
		return VM_PATH + "amountStatistics.vm";
	}
	
	@RequestMapping("rechargeStatistics")
	public String rechargeStatistics(StatisticsQueryOrder queryOrder, PageParam pageParam,
										String queryType, Model model) {
		String retVM = VM_PATH + "withdrawStatistics.vm";
		if (StringUtil.isBlank(queryOrder.getStartDate())) {
			String startDate = "2013-10-15 00:00:00";
			queryOrder.setStartDate(startDate);
		}
		if (StringUtil.isBlank(queryOrder.getEndDate())) {
			queryOrder.setEndDate(DateUtil.simpleFormat(new Date()) + " 23:59:59");
		}
		List<String> payType = new ArrayList<String>();
		if (PayTypeEnum.WITHDRAW.code().equals(queryType)) {
			payType.add(PayTypeEnum.WITHDRAW.code());
			queryOrder.setPayType(payType);
		} else if (PayTypeEnum.RECHARGE.code().equals(queryType)) {
			retVM = VM_PATH + "rechargeStatistics.vm";
			if (queryOrder.getStatus() == null) {
				payType.add(PayTypeEnum.DEDUCT.code());
				payType.add(PayTypeEnum.EBANK.code());
			}
			queryOrder.setPayType(payType);
		} else if (PayTypeEnum.DEDUCT.code().equals(queryType)
					|| PayTypeEnum.EBANK.code().equals(queryType)) {
			retVM = VM_PATH + "rechargeStatistics.vm";
			payType.add(queryType);
			queryOrder.setPayType(payType);
		}
		Page<RechargeStatistics> page = amountStatisticsService.rechargeStatisticsPage(queryOrder,
			pageParam);
		queryOrder.setStatus("1");//成功
		RechargeStatistics recharge1 = amountStatisticsService.getRechargeStatistics(queryOrder);
		queryOrder.setStatus("0");//失败
		RechargeStatistics recharge2 = amountStatisticsService.getRechargeStatistics(queryOrder);
		queryOrder.setStatus("0,1");//失败
		RechargeStatistics recharge3 = amountStatisticsService.getRechargeStatistics(queryOrder);
		model.addAttribute("bankCounts", page.getTotalCount());
		//		model.addAttribute("allcounts", Integer.parseInt(recharge1.getCountFildTwo())+Integer.parseInt(recharge2.getCountFildTwo()));
		//		model.addAttribute("allamount", CommonUtil.addDD(Double.parseDouble(recharge1.getCountFildThree()), 
		//				Double.parseDouble(recharge2.getCountFildThree())));
		model.addAttribute("recharge3", recharge3);
		model.addAttribute("page", page);
		model.addAttribute("recharge1", recharge1);
		model.addAttribute("recharge2", recharge2);
		model.addAttribute("queryOrder", queryOrder);
		return retVM;
	}
}
