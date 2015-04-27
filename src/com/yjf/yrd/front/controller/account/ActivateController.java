package com.yjf.yrd.front.controller.account;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.yjf.common.lang.util.StringUtil;
import com.yjf.yrd.base.BaseAutowiredController;
import com.yjf.yrd.dal.daointerface.InstitutionsInfoDAO;
import com.yjf.yrd.dal.daointerface.PersonalInfoDAO;
import com.yjf.yrd.integration.bornapi.enums.YjfRegesterTypeEnum;
import com.yjf.yrd.integration.openapi.SMSService;
import com.yjf.yrd.integration.openapi.result.CustomerResult;
import com.yjf.yrd.integration.web.server.YjfLoginWebServer;
import com.yjf.yrd.integration.web.server.impl.YjfRegisterOrder;
import com.yjf.yrd.session.SessionLocalManager;
import com.yjf.yrd.user.RegisterService;
import com.yjf.yrd.user.info.InstitutionsInfo;
import com.yjf.yrd.user.info.PersonalInfo;
import com.yjf.yrd.user.info.UserInfo;
import com.yjf.yrd.user.order.LoanActivateUserInfoOrder;
import com.yjf.yrd.user.order.UserActivateOrder;
import com.yjf.yrd.user.result.UserQueryResult;
import com.yjf.yrd.util.AppConstantsUtil;
import com.yjf.yrd.util.RSAUtils;
import com.yjf.yrd.ws.enums.BooleanEnum;
import com.yjf.yrd.ws.enums.UserTypeEnum;
import com.yjf.yrd.ws.result.YrdBaseResult;

/**
 * 
 * 
 * @Filename ActivateController.java
 * 
 * @Description
 * 
 * @Version 1.0
 * 
 * @Author zjl
 * 
 * @Email zjialin@yiji.com
 * 
 * @History <li>Author: zjl</li> <li>Date: 2013-8-19</li> <li>Version: 1.0</li>
 * <li>Content: create</li>
 * 
 */
@Controller
@RequestMapping("anon")
public class ActivateController extends BaseAutowiredController {
	private final String vm_path = "/front/anon/activate/";
	
	private static final String PHONE_VALIDATE_CODE = "PHONE_VALIDATE_CODE";
	private static final String PHONE_VALIDATE = "access_phone_validate";
	@Autowired
	RegisterService registerService;
	
	@Autowired
	YjfLoginWebServer yjfLoginWebServer;
	
	@Autowired
	SMSService smsService;
	
	@Autowired
	PersonalInfoDAO personalInfoDAO;
	
	@Autowired
	InstitutionsInfoDAO institutionsInfoDAO;
	
	@RequestMapping("activate/{MD5UserBaseId}")
	public String activationUser(HttpSession session, @PathVariable String MD5UserBaseId,
									Model model, HttpServletResponse response,
									HttpServletRequest request) {
		LoanActivateUserInfoOrder activateUserInfoOrder = new LoanActivateUserInfoOrder();
		activateUserInfoOrder.setDate(request.getParameter("date"));
		activateUserInfoOrder.setUserBaseId(request.getParameter("userBaseId"));
		activateUserInfoOrder.setMd5Url(MD5UserBaseId);
		UserQueryResult userQResult = this.registerService
			.getActivateUserInfo(activateUserInfoOrder);
		if (userQResult.getQueryUserInfo() == null) {
			// return 激活链接失效
			UserInfo userInfo = userQueryService.queryByUserBaseId(
				activateUserInfoOrder.getUserBaseId()).getQueryUserInfo();
			if (userInfo != null) {
				model.addAttribute("userName", userInfo.getUserName());
				model.addAttribute("reason", "激活链接失效");
			} else {
				model.addAttribute("reason", "用户信息不存在");
			}
			return vm_path + "activateFail.vm";
		} else {
			if ("Y".equals(AppConstantsUtil.getOnlyYjfLogin())) {
				YrdBaseResult yrdBaseResult = registerService
					.onlyRegesiterYjfUserActive(userQResult.getQueryUserInfo().getAccountId());
				if (yrdBaseResult.isSuccess()) {
					try {
						response.sendRedirect(yrdBaseResult.getUrl());
					} catch (IOException e) {
						logger.error(e.getMessage(), e);
					}
				} else {
					model.addAttribute("userName", userQResult.getQueryUserInfo().getUserName());
					model.addAttribute("reason", yrdBaseResult.getMessage());
					return vm_path + "activateFail.vm";
				}
				return null;
			} else {
				String token = UUID.randomUUID().toString();
				session.setAttribute("token", token);
				session.setAttribute("activationUser", userQResult.getQueryUserInfo());
				model.addAttribute("userName", userQResult.getQueryUserInfo().getUserName());
				model.addAttribute("token", token);
				return vm_path + "activate.vm";
			}
		}
		
		// }
	}
	
	@RequestMapping("activateSubmit")
	public String activateSubmit(HttpSession session, String token, String userBaseId,
									String logPassword, String business, String mobile,
									String code, Model model, HttpServletRequest request)
																							throws Exception {
		String getToken = (String) session.getAttribute("token");
		UserInfo userBaseInfo = null;
		logPassword = RSAUtils.decryptStringByJs(logPassword);
		if (getToken != null && getToken.equals(token)) {
			session.removeAttribute("token");
			userBaseInfo = (UserInfo) session.getAttribute("activationUser");
			model.addAttribute("userName", userBaseInfo.getUserName());
			userBaseInfo.setMobile(mobile);
			userBaseInfo.setMobileBinding(BooleanEnum.IS);
			UserActivateOrder activateOrder = new UserActivateOrder();
			activateOrder.setLogPassword(logPassword);
			activateOrder.setMobile(mobile);
			activateOrder.setUserBaseId(userBaseInfo.getUserBaseId());
			YrdBaseResult yrdBaseResult = registerService.userActivate(activateOrder);
			if (yrdBaseResult.isSuccess()) {
				model.addAttribute("userType", userBaseInfo.getType());
				model.addAttribute("userName", userBaseInfo.getUserName());
				return vm_path + "activateSuccess.vm";
			} else {
				model.addAttribute("userName", userBaseInfo.getUserName());
				model.addAttribute("reason", "激活处理失败");
				return vm_path + "activateFail.vm";
			}
		} else {
			userBaseInfo = (UserInfo) session.getAttribute("activationUser");
			model.addAttribute("reason", "重复提交");
			if (userBaseInfo != null) {
				model.addAttribute("userName", userBaseInfo.getUserName());
			}
			return vm_path + "activateFail.vm";
		}
		
	}
	
	@RequestMapping("realNameAuth")
	public String realNameAuth(Model model, HttpSession session) {
		if (SessionLocalManager.getSessionLocal() == null) {
			return "/help/nopermission";
		}
		UserInfo userBaseInfo = null;
		try {
			this.queryUserInfo(model);
			userBaseInfo = userQueryService.queryByUserBaseId(
				SessionLocalManager.getSessionLocal().getUserBaseId()).getQueryUserInfo();
		} catch (Exception e) {
			logger.error("查询银行信息异常", e);
		}
		session.setAttribute("token", UUID.randomUUID().toString());
		if (userBaseInfo.getType() == UserTypeEnum.GR)
			return vm_path + "realNameAuthentication.vm";
		else
			return vm_path + "enterpriserealNameAuthentication.vm";
	}
	
	@RequestMapping("activeYjfAccount")
	public String activeYjfAccount(Model model, HttpSession session, HttpServletResponse response) {
		if (SessionLocalManager.getSessionLocal() == null) {
			return "/help/nopermission";
		}
		UserInfo userBaseInfo = null;
		try {
			userBaseInfo = userQueryService.queryByUserBaseId(
				SessionLocalManager.getSessionLocal().getUserBaseId()).getQueryUserInfo();
			YjfRegisterOrder yjfRegisterOrder = new YjfRegisterOrder();
			yjfRegisterOrder.setUserName(AppConstantsUtil.getYrdPrefixion()
											+ userBaseInfo.getUserName());
			if (userBaseInfo != null) {
				yjfRegisterOrder.setUserName(userBaseInfo.getAccountName());
			}
			yjfRegisterOrder.setYjfRegesterType(YjfRegesterTypeEnum.ACTIVATE);
			if (StringUtil.isNotBlank(AppConstantsUtil.getSystem()))
				yjfRegisterOrder.setSystem(AppConstantsUtil.getSystem());
			YrdBaseResult yrdBaseResult = yjfLoginWebServer.gotoYjfRegisterUrl(yjfRegisterOrder,
				this.getOpenApiContext());
			response.sendRedirect(yrdBaseResult.getUrl());
		} catch (Exception e) {
			logger.error("查询银行信息异常", e);
		}
		return null;
	}
	
	@RequestMapping("goto3Acount")
	public String goto3Acount(Model model, HttpSession session, HttpServletResponse response) {
		if (SessionLocalManager.getSessionLocal() == null) {
			return "/help/nopermission";
		}
		UserInfo userBaseInfo = null;
		try {
			userBaseInfo = userQueryService.queryByUserBaseId(
				SessionLocalManager.getSessionLocal().getUserBaseId()).getQueryUserInfo();
			CustomerResult customerResult = this.customerService.gotoYjfSit(
				userBaseInfo.getAccountId(), this.getOpenApiContext());
			response.sendRedirect(customerResult.getUrl());
		} catch (Exception e) {
			logger.error("查询银行信息异常", e);
		}
		return null;
	}
	
	@RequestMapping("allFlowSuccess")
	public String allFlowSuccess(HttpSession session, Model model) throws Exception {
		if (SessionLocalManager.getSessionLocal() == null) {
			return "/help/nopermission";
		}
		model.addAttribute("allFlowSuccess", "true");
		String userBaseId = SessionLocalManager.getSessionLocal().getUserBaseId();
		UserInfo userBaseInfo = userQueryService.queryByUserBaseId(
			SessionLocalManager.getSessionLocal().getUserBaseId()).getQueryUserInfo();
		model.addAttribute("userName", userBaseInfo.getUserName());
		return vm_path + "activateSuccess.vm";
	}
	
	private void queryUserInfo(Model model) throws Exception {
		String userBaseId = SessionLocalManager.getSessionLocal().getUserBaseId();
		UserInfo userBaseInfoDO = userQueryService.queryByUserBaseId(userBaseId).getQueryUserInfo();
		if (userBaseInfoDO.getRealNameAuthentication() == null) {
			model.addAttribute("realNameStatus", "N");
		} else {
			model.addAttribute("realNameStatus", userBaseInfoDO.getRealNameAuthentication().code());
		}
		
		if (userBaseInfoDO.getType() == UserTypeEnum.GR) {
			PersonalInfo personalInfo = userQueryService.queryPersonalInfoByBaseId(userBaseId)
				.getQueryPersonalInfo();
			
			model.addAttribute("info", personalInfo);
		}
		if (userBaseInfoDO.getType() == UserTypeEnum.JG) {
			InstitutionsInfo institutionsInfo = userQueryService.queryInstitutionsInfoByBaseId(
				userBaseId).getQueryInstitutionsInfo();
			model.addAttribute("info", institutionsInfo);
		}
		
		model.addAttribute("type", userBaseInfoDO.getType());
	}
	
	@RequestMapping("signBankCard")
	public String signBankCard(Model model) throws Exception {
		if (SessionLocalManager.getSessionLocal() == null) {
			return "/help/nopermission";
		}
		return vm_path + "signBankCard.vm";
	}
	
}
