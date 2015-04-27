package com.yjf.yrd.front.controller.trade.query;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;

import com.yjf.yrd.base.UserAccountInfoBaseController;
import com.yjf.yrd.loanapply.ILoanApplyService;
import com.yjf.yrd.page.PageParam;
import com.yjf.yrd.session.SessionLocalManager;
import com.yjf.yrd.web.util.PageUtil;
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
 *          <li>Content: create</li>
 * 
 */
@Controller
@RequestMapping("loanQuery")
public class LoanScheduleQueryController extends UserAccountInfoBaseController {
	private final String vm_path = "/front/trade/query/";
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
		binder.registerCustomEditor(Date.class, "applyTime",
				new CustomDateEditor(dateFormat, true));
	}

	@Override
	protected String[] getMoneyInputNameArray() {
		return new String[] { "minLoanAmount", "maxLoanAmount" };
	}

	/**
	 * 查询贷款申请进度记录
	 */
	@RequestMapping("loanSchedule")
	public String loanSchedule(HttpSession session, PageParam pageParam, Model model) {
		this.initAccountInfo(model);
		LoanApplyQueryOrder queryOrder = new LoanApplyQueryOrder();
		queryOrder.setLoanerId(SessionLocalManager.getSessionLocal().getUserId());
		queryOrder.setPageNumber(pageParam.getPageNo());
		queryOrder.setPageSize(pageParam.getPageSize());
		QueryBaseBatchResult<LoanApplyInfo> result = loanApplyService.queryLoandApply(queryOrder);
		
		model.addAttribute("page", PageUtil.getCovertPage(result));
		return vm_path + "loan_schedule.vm";
	}



}
