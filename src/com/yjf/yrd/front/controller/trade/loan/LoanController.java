package com.yjf.yrd.front.controller.trade.loan;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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
import com.yjf.yrd.base.Image;
import com.yjf.yrd.base.UserAccountInfoBaseController;
import com.yjf.yrd.loanapply.ILoanApplyService;
import com.yjf.yrd.page.PageParam;
import com.yjf.yrd.session.SessionLocalManager;
import com.yjf.yrd.web.util.PageUtil;
import com.yjf.yrd.web.util.WebUtil;
import com.yjf.yrd.ws.enums.LoanApplyStatusEnum;
import com.yjf.yrd.ws.info.LoanApplyInfo;
import com.yjf.yrd.ws.order.LoanApplyQueryOrder;
import com.yjf.yrd.ws.service.query.result.QueryBaseBatchResult;

/**
 * 
 * 
 * @Filename RepayQueryController.java
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
@RequestMapping("loan")
public class LoanController extends UserAccountInfoBaseController {
	private final String vm_path = "/front/trade/loan/";
	
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
	 * 进入我要借款页面
	 */
	@RequestMapping("applyLoan")
	public String applyLoan(Model model, HttpSession session) {

		return  "front/loanRequest/loan_apply.vm";
	}
	
	/**
	 * 我要借款申请信息详情
	 */
	@RequestMapping("applyDetails")
	public String applyDetails(HttpServletRequest request, HttpServletResponse response,
								PageParam pageParam, Model model) {
		LoanApplyQueryOrder queryOrder = new LoanApplyQueryOrder();
		
		queryOrder.setPageNumber(pageParam.getPageNo());
		queryOrder.setPageSize(4);
		List<LoanApplyStatusEnum> statusList = new ArrayList<LoanApplyStatusEnum>();
		statusList.add(LoanApplyStatusEnum.PASS);
		queryOrder.setStatusList(statusList);
		QueryBaseBatchResult<LoanApplyInfo> result = loanApplyService.queryLoandApply(queryOrder);
		model.addAttribute("totalCount", result.getTotalCount());
		model.addAttribute("page", PageUtil.getCovertPage(result));
		return vm_path + "loan_apply_details.vm";
	}
	
	/**
	 * 提交我要贷款信息
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("applyLoanSubmit")
	@ResponseBody
	public Object applyLoanSubmit(HttpServletRequest request, HttpServletResponse response,
									Model model) throws IOException {
		JSONObject json = new JSONObject();
		LoanApplyQueryOrder info = new LoanApplyQueryOrder();
		WebUtil.setPoPropertyByRequest(info, request);
		Date date = new Date();
		info.setLoanName(DateUtil.longDate(date) + "消费贷款");
		info.setApplyTime(DateUtil.now());
		List<LoanApplyStatusEnum> statusList = new ArrayList<LoanApplyStatusEnum>();
		statusList.add(LoanApplyStatusEnum.WITE);
		info.setStatusList(statusList);
        if(SessionLocalManager.getSessionLocal() != null){
            info.setLoanerId(SessionLocalManager.getSessionLocal().getUserId());
            info.setUserName(SessionLocalManager.getSessionLocal().getUserName());
        }else{
        	info.setLoanerId(0);
            info.setUserName(info.getLoanerName());
        }

		
		try {
			loanApplyService.addLoanApply(info);
			json.put("code", "1");
			json.put("message", "申请成功！");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			json.put("code", "0");
			json.put("message", "申请失败！");
		}
		return json;
		
	}
	
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping("validateCaptcha")
	public Object validateCaptcha(String captcha, HttpSession session) {
		JSONObject json = new JSONObject();
		try {
			if (Image.checkImgCode(session, captcha)) {
				json.put("code", 1);
				json.put("message", "验证码正确");
			} else {
				json.put("code", 0);
				json.put("message", "验证码不正确");
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			json.put("code", 0);
			json.put("message", "验证码校验异常");
		}
		return json;
	}
	
	/**
	 * 我要借款申请成功跳转页面
	 */
	@RequestMapping("applySuccess")
	public String applySuccess(HttpServletRequest request, HttpServletResponse response,
								PageParam pageParam, Model model) {
		LoanApplyQueryOrder queryOrder = new LoanApplyQueryOrder();
		
		queryOrder.setPageNumber(pageParam.getPageNo());
		queryOrder.setPageSize(4);
		List<LoanApplyStatusEnum> statusList = new ArrayList<LoanApplyStatusEnum>();
		statusList.add(LoanApplyStatusEnum.PASS);
		queryOrder.setStatusList(statusList);
		QueryBaseBatchResult<LoanApplyInfo> result = loanApplyService.queryLoandApply(queryOrder);
		model.addAttribute("totalCount", result.getTotalCount());
		model.addAttribute("page", PageUtil.getCovertPage(result));
		
		return vm_path + "loan_apply_success.vm";
	}
}
