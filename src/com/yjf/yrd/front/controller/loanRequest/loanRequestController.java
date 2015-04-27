package com.yjf.yrd.front.controller.loanRequest;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.yjf.yrd.base.BaseAutowiredController;
import com.yjf.yrd.biz.util.SendInformation;
import com.yjf.yrd.util.AppConstantsUtil;

/**
 * 
 * 
 * @Filename loanRequestController.java
 * 
 * @Description
 * 
 * @Version 1.0
 * 
 * @Author zhb
 * 
 * @Email abing@yiji.com
 * 
 * 
 */
@Controller
@RequestMapping("loanRequest")
public class loanRequestController extends BaseAutowiredController {
	
	/**
	 * 客户创建借款请求
	 * 
	 * */
	
	@RequestMapping("toLoanRequest/{T}")
	public String loanRequesstP(@PathVariable String T, HttpSession session, Model model) {
		String token = UUID.randomUUID().toString();
		logger.info("用户进入借款请求页面");
		model.addAttribute("token", token);
		session.setAttribute("token", token);
		model.addAttribute("uploadHost", "");
		return "/front/loanRequest/loanRequest" + T + ".vm";
	}
	
	/**
	 * 发送借款请求邮件 1
	 * */
	@RequestMapping("sendLoanRequestMail/{T}")
	public String sendLoanRequest(@PathVariable String T, String type, String token,
									HttpSession session, Model model, HttpServletRequest request)
																									throws Exception {
		
		long templateId = 555;
		if ("2".equalsIgnoreCase(type)) {
			templateId = 666;
		}
		String getToken = (String) session.getAttribute("token");
		if ("" != token && token.equals(getToken)) {
			session.removeAttribute("token");
			boolean result = mailService.sendLoanRequestMail(SendInformation.sendLoanRequestMail(
				AppConstantsUtil.getLoanRequestMail(), templateId, request, type), type, session);
			if (result) {
				model.addAttribute("message", "true");
				logger.info("用户请求借款的邮件发送成功。。");
			} else {
				model.addAttribute("massage", "false");
				logger.info("用户请求借款的邮件发送失败。。");
			}
			
		} else {
			model.addAttribute("message", "false");
		}
		
		return "/front/loanRequest/loanRequest" + T + "Result.vm";
	}
}
