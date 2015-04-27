package com.yjf.yrd.web.controller.openapi;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("anon")
public class YjfPayPassValidateController {
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "yjfPayPassewordReturnUrl.htm")
	public String yjfPayPassewordReturnUrl(HttpServletRequest request,
											HttpServletResponse response, ModelMap modelMap) {
		String paytk = request.getParameter("paytk");
		modelMap.put("paytk", paytk);
		return "test/payPassSuccess.vm";
		
	}
	
	@RequestMapping(value = "yjfPayPassewordCancelUrl.htm")
	public String yjfPayPassewordCancelUrl(HttpServletRequest request,
											HttpServletResponse response, ModelMap modelMap) {
		
		return "test/payPassCancel.vm";
		
	}
}
