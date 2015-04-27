package com.yjf.yrd.backstage.controller.loanmanage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yjf.common.lang.util.DateUtil;
import com.yjf.yrd.loanapply.ILoanApplyService;
import com.yjf.yrd.page.PageParam;
import com.yjf.yrd.web.util.PageUtil;
import com.yjf.yrd.web.util.WebUtil;
import com.yjf.yrd.ws.enums.LoanApplyStatusEnum;
import com.yjf.yrd.ws.info.LoanApplyInfo;
import com.yjf.yrd.ws.order.LoanApplyQueryOrder;
import com.yjf.yrd.ws.service.query.result.QueryBaseBatchResult;

/**
 * 
 * 
 * @Filename LoanScheduleQueryController.java
 * 
 * @Description
 * 
 * @Version 1.0
 * 
 * @Author fc
 * 
 * @Email oyangnuo@yiji.com
 * 
 * @History <li>Author: fc</li> <li>Date: 2014-9-1</li> <li>Version: 1.0</li>
 * <li>Content: create</li>
 * 
 */
@Controller
@RequestMapping("backstage")
public class LoanReviewController extends LoanBaseController {
	private final String vm_path = "/backstage/loanManage/";
	@Autowired
	ILoanApplyService loanApplyService;
	
	/**
	 * 工具类处理金额 /时间
	 * */
	@Override
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		super.initBinder(binder);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		dateFormat.setLenient(false);
		binder
			.registerCustomEditor(Date.class, "applyTime", new CustomDateEditor(dateFormat, true));
	}
	
	@Override
	protected String[] getMoneyInputNameArray() {
		return new String[] { "minLoanAmount", "maxLoanAmount" };
	}
	
	/**
	 * 查询贷款申请进度记录
	 */
	@RequestMapping("loanReview")
	public String loanReview(HttpServletRequest request, PageParam pageParam, Model model) {
		LoanApplyQueryOrder order = new LoanApplyQueryOrder();
		order.setPageNumber(pageParam.getPageNo());
		order.setPageSize(pageParam.getPageSize());
		QueryBaseBatchResult<LoanApplyInfo> result = loanApplyService.queryLoandApply(order);
		model.addAttribute("page", PageUtil.getCovertPage(result));
		return vm_path + "loan_review.vm";
	}
	
	/**
	 * 审核贷款申请
	 * 
	 * @throws Exception
	 */
	@RequestMapping("loanReview/updateLoanApply")
	public String updateLoanApply(long applyId, String info, Model model) throws Exception {
		LoanApplyInfo loanApplyinfo = loanApplyService.getByApplyId(applyId);
		
		model.addAttribute("guarantee", getInfos(8));
		model.addAttribute("info", loanApplyinfo);
		model.addAttribute("applyTime", DateUtil.simpleFormat(loanApplyinfo.getApplyTime()));
		return vm_path + "updateLoanApply.vm";
		
	}
	
	/**
	 * 审核贷款申请提交
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("loanReview/updateLoanApplySubmit")
	@ResponseBody
	public Object updateLoanApplySubmit(HttpServletRequest request, PageParam pageParam, Model model)
																										throws Exception {
		JSONObject json = new JSONObject();
		LoanApplyQueryOrder loanApplyQueryOrder = new LoanApplyQueryOrder();
		WebUtil.setPoPropertyByRequest(loanApplyQueryOrder, request);
		List<LoanApplyStatusEnum> statusList = new ArrayList<LoanApplyStatusEnum>();
		
		statusList.add(LoanApplyStatusEnum.PASS);
		loanApplyQueryOrder.setStatusList(statusList);
		String guaranteeName = userQueryService.queryByUserId(loanApplyQueryOrder.getGuaranteeId())
			.getQueryUserInfo().getRealName();
		loanApplyQueryOrder.setGuaranteeName(guaranteeName);
		try {
			loanApplyService.updateLoanApplyInfo(loanApplyQueryOrder);
			json.put("code", "1");
			json.put("message", "发送成功！");
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			json.put("code", "0");
			json.put("message", "发送失败！");
		}
		return json;
		
	}
	
	/**
	 * 审核贷款申请驳回
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("loanReview/updateLoanApplyRebut")
	@ResponseBody
	public Object updateLoanApplyRebut(HttpServletRequest request, PageParam pageParam, Model model)
																									throws Exception {
		JSONObject json = new JSONObject();
		LoanApplyQueryOrder loanApplyQueryOrder = new LoanApplyQueryOrder();
		WebUtil.setPoPropertyByRequest(loanApplyQueryOrder, request);
		List<LoanApplyStatusEnum> statusList = new ArrayList<LoanApplyStatusEnum>();
		statusList.add(LoanApplyStatusEnum.DISMISS);
		loanApplyQueryOrder.setStatusList(statusList);
		try {
			loanApplyService.updateLoanApplyInfo(loanApplyQueryOrder);
			json.put("code", "1");
			json.put("message", "驳回成功！");
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			json.put("code", "0");
			json.put("message", "驳回失败！");
		}
		return json;
	}
	
	/**
	 * 贷款申请详情
	 */
	@RequestMapping("loanReview/loanApplyInfo")
	public String LoanApplyInfo(long applyId, String info, Model model) {
		LoanApplyInfo loanApplyinfo = loanApplyService.getByApplyId(applyId);
		
		model.addAttribute("info", loanApplyinfo);
		return vm_path + "loanApplyInfo.vm";
	}
	
}
