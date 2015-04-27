package com.yjf.yrd.backstage.controller.statisticsManage;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.yjf.yrd.dataobject.ProjectStatisticsInfo;
import com.yjf.yrd.page.Page;
import com.yjf.yrd.page.PageParam;
import com.yjf.yrd.statistics.ProjectOrder;
import com.yjf.yrd.statistics.ProjectStatisticsService;
import com.yjf.yrd.util.DateUtil;


/**
 * 项目统计类
 * @author Joe
 *
 */
@RequestMapping(value="backstage")
@Controller
public class ProjectStatistics {
	@Autowired
	ProjectStatisticsService projectStatisticsService;
	private final String VM_PATH = "/backstage/statistics/";
	/**
	 * 根据不同维度统计项目数
	 * @param dimension(year-年度,month-月份,loaner-投资接受人)
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	@RequestMapping(value="queryProjectStatistics")
	public String queryProjectStatistics(ProjectOrder projectOrder,PageParam pageParam,Model model){
		if(projectOrder.getDimension() == null || "".equals(projectOrder.getDimension())){
			projectOrder.setDimension("year");
		}
		if(projectOrder.getStartTime() == null || "".equals(projectOrder.getStartTime())){
			projectOrder.setStartTime("2013-11-25");
		}
		if(projectOrder.getEndTime() == null || "".equals(projectOrder.getEndTime())){
			projectOrder.setEndTime(DateUtil.simpleFormat(new Date()));
		}
		String retDimension = "年度";
		pageParam.setPageSize(15);
		Page<ProjectStatisticsInfo> page = projectStatisticsService.queryProjectsStatistics(projectOrder,pageParam);
		if("month".equals(projectOrder.getDimension())){
			retDimension = "月份"; 
		}else if("loaner".equals(projectOrder.getDimension())){
			retDimension = "投资接受人";
		}
		model.addAttribute("retDimension", retDimension);
		model.addAttribute("page", page);
		projectOrder.setStatus(3);//已还款项目
		long repayCount = projectStatisticsService.getProjectCounts(projectOrder);
		projectOrder.setStatus(2);//待还款项目
		long pendingCount = projectStatisticsService.getProjectCounts(projectOrder);
		projectOrder.setStatus(8);
		pendingCount = pendingCount+projectStatisticsService.getProjectCounts(projectOrder);
		model.addAttribute("repayCount", repayCount);
		model.addAttribute("pendingCount", pendingCount);
		model.addAttribute("projectOrder", projectOrder);
		return VM_PATH+"projectStatistics.vm";
	}

}
