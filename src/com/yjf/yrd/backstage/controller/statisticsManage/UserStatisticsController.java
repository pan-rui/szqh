package com.yjf.yrd.backstage.controller.statisticsManage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
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
import com.yjf.yrd.page.Page;
import com.yjf.yrd.page.PageParam;
import com.yjf.yrd.statistics.order.StatisticsQueryOrder;
import com.yjf.yrd.util.DateUtil;

/**
 * 用户数据统计信息查询
 * @author CHARLEY
 *
 */

@Controller
@RequestMapping("backstage")
public class UserStatisticsController extends BaseAutowiredController {
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
	
	@RequestMapping("userStatistics")
	public String userStatistics(StatisticsQueryOrder queryOrder, HttpServletResponse response,
									PageParam pageParam, Model model) throws Exception {
		if (StringUtil.isBlank(queryOrder.getQueryType())) {
			queryOrder.setQueryType("userRole");
		}
		Page<Object> page = userStatisticsService.userStatisticsPage(queryOrder, pageParam);
		Map<String, Object> conditions = new HashMap<String, Object>();
		if (StringUtil.isBlank(queryOrder.getStartDate())) {
			//String startDate=DateUtil.getWeekdayBeforeNow(new Date())+" 00:00:00";
			//condition.put("maiDemandDate",startDate );
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
		long totalUserCount = userStatisticsService.countUserByConditions(conditions);
		model.addAttribute("totalUserCount", totalUserCount);
		conditions.put("isRealNamePass", "IS");
		long realPassUserCount = userStatisticsService.countUserByConditions(conditions);
		model.addAttribute("realPassUserCount", realPassUserCount);
		model.addAttribute("queryOrder", queryOrder);
		model.addAttribute("page", page);
		response.setHeader("Pragma", "No-cache");
		return VM_PATH + "userStatistics.vm";
	}
}
