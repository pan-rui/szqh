package com.yjf.yrd.before.sign;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.yjf.common.service.enums.customer.CertifyStatusEnum;
import com.yjf.yrd.base.UserAccountInfoBaseController;
import com.yjf.yrd.integration.openapi.info.YjfAccountInfo;
import com.yjf.yrd.session.SessionLocalManager;

/**
 * Created by zjialin@yiji.com on 2014/4/16.
 */
@Controller
@RequestMapping("/userManage/sign/")
public class SignController extends UserAccountInfoBaseController {
	
	@RequestMapping("redirect")
	public String redirect(Model model, HttpServletResponse response) {
		YjfAccountInfo accountInfo = this.getAccountInfo(model);
		if (accountInfo != null
			&& CertifyStatusEnum.AUTHORIZED.message().equals(accountInfo.getCertifyStatus())) {
			String accountId = SessionLocalManager.getSessionLocal().getAccountId();
			String url = rechargeFlowService.getSignUrl(accountId);
			try {
				response.sendRedirect(url);
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
			return null;
		} else {
			model.addAttribute("isSuccess", false);
			model.addAttribute("isAccountNotActive", false);
			model.addAttribute("message", "亲，你的账户还未实名认证！");
			return returnVm("withholdingResult.vm");
		}
		
	}
	
	private String returnVm(String vm) {
		String fullVm = "/before/topup/";
		return fullVm + vm;
	}
}
