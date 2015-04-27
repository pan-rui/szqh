package com.yjf.yrd.front.controller.report;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.yjf.yrd.base.BaseAutowiredController;
import com.yjf.yrd.page.Page;
import com.yjf.yrd.page.PageParam;
import com.yjf.yrd.report.ReportService;
import com.yjf.yrd.report.order.ReportQueryOrder;
import com.yjf.yrd.report.order.ReportQueryParam;
import com.yjf.yrd.util.DateUtil;
import com.yjf.yrd.util.MiscUtil;
import com.yjf.yrd.util.StringUtil;
import com.yjf.yrd.util.rpt.common.ReportData;
import com.yjf.yrd.util.rpt.common.ReportExcel;
import com.yjf.yrd.util.rpt.common.ReportHead;
import com.yjf.yrd.web.util.PageUtil;
import com.yjf.yrd.ws.data.ReportRuleData;
import com.yjf.yrd.ws.service.query.result.QueryBaseBatchResult;



@Controller
@RequestMapping("/backstage/report")
public class ReportController  extends BaseAutowiredController {
	
	/** 通用页面路径 */
	String	VM_PATH	= "/backstage/report/";
	
	@Autowired
	ReportService reportService;
	
	

	
	@RequestMapping("list")
	public String queryAll(ReportQueryOrder reportQueryOrder, HttpServletResponse response,
									PageParam pageParam, Model model) {
		try {
			 reportQueryOrder.setPageNumber(pageParam.getPageNo());
			 reportQueryOrder.setPageSize(pageParam.getPageSize());
			
			 QueryBaseBatchResult<ReportRuleData> page =  reportService.getQueryRules(reportQueryOrder);
			
			 model.addAttribute("page", PageUtil.getCovertPage(page));
			 model.addAttribute("queryConditions", reportQueryOrder);
			
		} catch (Exception e) {
	            logger.error(e.getMessage(), e);
	    }
		return VM_PATH +"reportList.vm";
	}
	
	@RequestMapping("toQuery")
	public String toQuery(long reportId, HttpServletResponse response, Model model){
		
		try {
			 ReportRuleData queryRule = reportService.getQueryRule(reportId);
			 model.addAttribute("queryRule", queryRule);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		
		return VM_PATH +"reportQuery.vm";
	}
	
	@RequestMapping("toAdd")
	public String toAdd(long reportId, HttpServletResponse response, Model model){
		ReportRuleData queryRule = new ReportRuleData();
		try {
			if(reportId!=0){
				  queryRule = reportService.getQueryRule(reportId);
			 }
			model.addAttribute("queryRule", queryRule);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		
		return VM_PATH +"reportAdd.vm";
	}
	
	@RequestMapping("delete")
	public String doDelete(long reportId, HttpServletResponse response, Model model){
		ReportRuleData queryRule = new ReportRuleData();
		try {
			if(reportId!=0){
				   reportService.deleteQueryRule(reportId);
			 }
			model.addAttribute("queryRule", queryRule);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		
		return "redirect:" + "/backstage/report/list";
	}
	
	
	@ResponseBody
	@RequestMapping("update")
	public Object update(ReportRuleData reportRule, HttpServletResponse response, Model model){
		JSONObject jsonobj = new JSONObject();
		try {
			 long reportId = reportRule.getReportId();
			 
			 //检验json 字符串是否正确
			 if(StringUtil.isNotEmpty(reportRule.getFilter1Options())){
				 MiscUtil.parseJSONWithException(reportRule.getFilter1Options());
			 }
			 if(StringUtil.isNotEmpty(reportRule.getFilter2Options())){
				 MiscUtil.parseJSONWithException(reportRule.getFilter2Options());
			 }
			 if(StringUtil.isNotEmpty(reportRule.getFilter3Options())){
				 MiscUtil.parseJSONWithException(reportRule.getFilter3Options());
			 }
			 if(StringUtil.isNotEmpty(reportRule.getFilter4Options())){
				 MiscUtil.parseJSONWithException(reportRule.getFilter4Options());
			 }
			 if(StringUtil.isNotEmpty(reportRule.getFilter5Options())){
				 MiscUtil.parseJSONWithException(reportRule.getFilter5Options());
			 }
			 if(StringUtil.isNotEmpty(reportRule.getFilter6Options())){
				 MiscUtil.parseJSONWithException(reportRule.getFilter6Options());
			 }
			 
			 if(reportId!=0){
				 reportId = reportService.udpateQueryRule(reportRule);
			 }else{
				 reportId = reportService.addQueryRule(reportRule);
			 }
			 ReportRuleData queryRule = reportService.getQueryRule(reportId);
			 model.addAttribute("queryRule", queryRule);
			 
			 jsonobj.put("code", 1);
			 jsonobj.put("message", "保存成功！");
			 
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			jsonobj.put("code", 0);
			jsonobj.put("message", "保存失败！"+e.getMessage());
		}
		
		//return VM_PATH +"reportAdd.vm";
		return jsonobj;
	}
	
	

	@ResponseBody
	@RequestMapping("downloadResult")
	public Object downloadResult(HttpSession session, HttpServletResponse response,HttpServletRequest request, Model model,
								ReportQueryParam queryParam) {
		JSONObject jsonobj = new JSONObject();
		try {
			ReportRuleData rule = reportService.getQueryRule(queryParam.getReportId());
			
			String reportName = rule.getReportName();
			
			
			List<Map<String, String>> list = reportService.queryReportData(queryParam, rule);
			
			if (list.size() > 0) {
				response.addHeader(
					"Content-Disposition",
					"attachment; filename="
							+ new String((reportName + DateUtil.dtSimpleFormat(new Date()) + ".xls")
								.getBytes("GB2312"), "ISO8859-1"));
				
				String CONTENT_TYPE_EXCEL = "application/vnd.ms-excel";
				String serverRealPath = session.getServletContext().getRealPath("/");
				response.setContentType(CONTENT_TYPE_EXCEL);
				
				Map<String, String> firstRow = list.get(0);
				ReportHead head = new ReportHead();
				
				String filters = reportService.getQueryParam(queryParam, rule);
				
				head.setDate(DateUtil.simpleFormat(new Date()));
				head.setFilters(filters.toString());
				
				ReportData report = new ReportData(head, list);
				ReportExcel excel = new ReportExcel(response.getOutputStream());
				
				excel.print(report.getXMLDocument(),
					excel.getDefaultDocument(serverRealPath, reportName, firstRow));
				jsonobj.put("code", 1);
				return jsonobj;
				
			}else{
				jsonobj.put("code", 1);
				jsonobj.put("message", "未查到满足条件的数据！");
				return jsonobj;
			}
			
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			response.setContentType("application/json");
			jsonobj.put("code", 0);
			jsonobj.put("message", e.getMessage());
		}
		return jsonobj;
		
	}
	
	
	
	@RequestMapping("queryResult")
	public String queryResult(HttpSession session, HttpServletResponse response, Model model,
								ReportQueryParam queryParam,PageParam pageParam) {
 
		try {
			ReportRuleData rule = reportService.getQueryRule(queryParam.getReportId());
			Page<Map<String, String>> page = reportService.queryReportPage(queryParam,pageParam, rule);
			model.addAttribute("page", page);
			model.addAttribute("queryParam", queryParam);
			if(StringUtil.isNotEmpty(rule.getFilter1Options())){
				model.addAttribute("filter1Options", MiscUtil.parseJSON(rule.getFilter1Options()));
			}
			if(StringUtil.isNotEmpty(rule.getFilter2Options())){
				model.addAttribute("filter2Options", MiscUtil.parseJSON(rule.getFilter2Options()));
			}
			if(StringUtil.isNotEmpty(rule.getFilter3Options())){
				model.addAttribute("filter3Options", MiscUtil.parseJSON(rule.getFilter3Options()));
			}
			if(StringUtil.isNotEmpty(rule.getFilter4Options())){
				model.addAttribute("filter4Options", MiscUtil.parseJSON(rule.getFilter4Options()));
			}
			if(StringUtil.isNotEmpty(rule.getFilter5Options())){
				model.addAttribute("filter5Options", MiscUtil.parseJSON(rule.getFilter5Options()));
			}
			if(StringUtil.isNotEmpty(rule.getFilter6Options())){
				model.addAttribute("filter6Options", MiscUtil.parseJSON(rule.getFilter6Options()));
			}
			model.addAttribute("queryRule", rule);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return VM_PATH + "reportQuery.vm";
		
	}
	
	@RequestMapping("test")
	public void testExcel(HttpSession session, HttpServletResponse response, Model model) {
		
		try {
			
			String CONTENT_TYPE_EXCEL = "application/vnd.ms-excel";
			String serverRealPath = session.getServletContext().getRealPath("/");
			response.setContentType(CONTENT_TYPE_EXCEL);
			
			String extName = "Excel报表.xls";
			
			response.addHeader("Content-Disposition",
				"attachment; filename=" + new String(extName.getBytes("GB2312"), "ISO8859-1"));
			
			ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
			
			HashMap<String, String> date = new HashMap<String, String>();
			date.put("appointment_date", "1001");
			date.put("stand_prem", "1001");
			date.put("life_money", "1001");
			date.put("bonus_rate", "1001");
			date.put("agent_code", "1001");
			date.put("money", "1001");
			date.put("dept_id", "1001");
			date.put("real_name", "1001");
			date.put("agent_age", "1001");
			list.add(date);
			list.add(date);
			list.add(date);
			
			ReportHead head = new ReportHead();
			head.setDate("时间2014-08-28");
			
			ReportData report = new ReportData(head, list);
			ReportExcel excel = new ReportExcel(response.getOutputStream());
			excel.print(report.getXMLDocument(), excel.toDocument(serverRealPath, "example1.xml"));
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		
	}
		
		



}
