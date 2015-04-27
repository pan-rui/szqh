package com.yjf.yrd.front.controller.account;

import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.yjf.common.lang.ip.IPUtil;
import com.yjf.common.lang.util.StringUtil;
import com.yjf.yrd.base.BaseAutowiredController;
import com.yjf.yrd.base.Image;
import com.yjf.yrd.common.result.SmsCodeResult;
import com.yjf.yrd.integration.bornapi.enums.YjfRegesterTypeEnum;
import com.yjf.yrd.integration.web.server.YjfLoginWebServer;
import com.yjf.yrd.integration.web.server.impl.YjfRegisterOrder;
import com.yjf.yrd.session.SessionLocal;
import com.yjf.yrd.session.SessionLocalManager;
import com.yjf.yrd.user.order.InvestorRegisterOrder;
import com.yjf.yrd.user.result.UserRegisterResult;
import com.yjf.yrd.user.result.UserRelationQueryResult;
import com.yjf.yrd.util.AppConstantsUtil;
import com.yjf.yrd.web.util.WebUtil;
import com.yjf.yrd.ws.enums.BooleanEnum;
import com.yjf.yrd.ws.enums.SmsBizType;
import com.yjf.yrd.ws.enums.SysUserRoleEnum;
import com.yjf.yrd.ws.order.AddPointsOrder;
import com.yjf.yrd.ws.result.YrdBaseResult;

/**
 * 
 * 
 * @Filename NewInvestorsOpenController.java
 * 
 * @Description
 * 
 * @Version 1.0
 * 
 * @Author zhaohaibing
 * 
 * @Email abing@yiji.com
 * 
 * @History <li>Author: zjl</li> <li>Date: 2013-8-19</li> <li>Version: 1.0</li>
 * <li>Content: create</li>
 * 
 */
@Controller
@RequestMapping("anon")
public class NewInvestorsOpenController extends BaseAutowiredController {
	private final String vm_path = "/front/anon/newInvestorsOpen/";
	
	@Autowired
	YjfLoginWebServer yjfLoginWebServer;
	
	@RequestMapping("brokerOpenInvestor")
	public String oldRegist(HttpSession session, String NO, Model model) {
		return newinvestorsOpen(session, NO, null, model);
	}
	
	@RequestMapping("investorOpenInvestor")
	public String investorOpenInvestor(HttpSession session, String NO, Model model) {
		return newinvestorsOpen(session, null, NO, model);
	}
	
	@RequestMapping("newInvestorsOpen")
	public String newinvestorsOpen(HttpSession session, String NO, String investorNO, Model model) {
		
		UserRelationQueryResult relationQueryResult = userRelationQueryService
			.findUserRelationByMemberNo(NO);
		if (relationQueryResult.getQueryUserRelationInfo() != null) {
			session.removeAttribute("referNotExist");
			model.addAttribute("referees", NO);
			session.setAttribute("referees", NO);
		} else {
			session.setAttribute("referNotExist", "推荐人不存在");
		}
		String token = UUID.randomUUID().toString();
		session.setAttribute("token", token);
		session.removeAttribute("brokerNo");
		model.addAttribute("investorNO", investorNO);
		SessionLocal sessionLocal = new SessionLocal();
		sessionLocal.addAttibute("appRegist", true);
		SessionLocalManager.setSessionLocal(sessionLocal);
		if (AppConstantsUtil.getProductName().equals("百合贷")
			&& relationQueryResult.getQueryUserRelationInfo() != null) {
			return "redirect:" + "/";
		}if ("Y".equals(AppConstantsUtil.getOnlyYjfLogin())) {
			return  "/front/anon/investorsOpen/investorsOpen.vm";//选用易极付账户登录的账户推广链接返回地址
		}else {
			return vm_path + "newInvestorsOpen.vm";
		}
	}
	
	@RequestMapping("newPerfectInfo")
	public String phoneRegist(HttpServletRequest request, HttpSession session, String imgCode,
								InvestorRegisterOrder investorRegisterOrder, String token,
								String code, Model model) throws Exception {
		
		JSONObject json = new JSONObject();
		if (!Image.checkImgCode(session, imgCode)) {
			json.put("code", 0);
			json.put("message", "短信验证码错误");
			logger.info("注册图片验证码错误:非正常提交导致");
			return null;
		}
		logger.info("新版个人投资者自主注册，入参1：{}，", investorRegisterOrder);
		if (StringUtil.isNotEmpty(investorRegisterOrder.getMobile())) {
			try {
				SmsCodeResult smsCodeResult = smsManagerService.verifySmsCode(
					investorRegisterOrder.getMobile(), SmsBizType.REGISTER, code, true);
				if (!smsCodeResult.isSuccess()) {
					json.put("code", 0);
					json.put("message", smsCodeResult.getMessage());
					return null;
				}
				investorRegisterOrder.setMobileBinding(BooleanEnum.IS.code());
			} catch (NullPointerException e) {
				json.put("code", 0);
				json.put("message", "未获取短信验证码");
				logger.info("注册非正常提交");
				return null;
			}
		} else if (StringUtil.isNotEmpty(investorRegisterOrder.getMail())) {
			investorRegisterOrder.setMailBinding(BooleanEnum.IS.code());
		} else {
			logger.info("注册失败：非正常提交");
			return null;
		}
		
		String referees = (String) session.getAttribute("referees");
		if (StringUtil.isNotEmpty(referees)) {
			// 以经济人给的链接注册的用户不可更改经纪人的编号
			investorRegisterOrder.setReferees(referees);
		}
		if (sysFunctionConfigService.isAllEconomicMan()) {
			investorRegisterOrder.getRole().add(SysUserRoleEnum.BROKER);
		}
		
		UserRegisterResult result = registerService.investorRegister(investorRegisterOrder);
		
		logger.info("新版个人投资者自主注册，结果：{}", json);
		if (result.isSuccess()) {
			if ("IS" == investorRegisterOrder.getMobileBinding()) {
				createloginStatus(request, result);
				YrdBaseResult yrdBaseResult = getYJFActiveUrl(investorRegisterOrder);
				model.addAttribute("activeAccountURL", yrdBaseResult.getUrl());
				model.addAttribute("successPage", "moblieRegit");
			} else if ("IS" == investorRegisterOrder.getMailBinding()) {
				YrdBaseResult senfActiveMail = registerService.resendEmail(result.getUserinfo()
					.getUserBaseId());
				if (senfActiveMail.isSuccess()) {
					model.addAttribute("mail", investorRegisterOrder.getMail());
					model.addAttribute("successPage", "mailRegit");
				} else {
					model.addAttribute("userName", result.getUserinfo().getUserName());
					model.addAttribute("message", senfActiveMail.getMessage());
					logger.info("个人投资者自主注册失败，结果：{}", senfActiveMail.getMessage());
					return vm_path + "newInvestorsOpenFail.vm";
				}
				
			}
			model.addAttribute("userName", investorRegisterOrder.getUserName());
			model.addAttribute("accountName", result.getUserinfo().getAccountName());
			logger.info("个人投资者自主注册成功:{}", result);
			if (result.isGiftMoneySuccess()) {
				model.addAttribute("giftMoney", result.getGiftMoneyMessage());
			}
			
			//注册成功生成积分
			AddPointsOrder addPointsOrder = new AddPointsOrder();
			addPointsOrder.setUserId(result.getUserinfo().getUserId());
			userPointsByRuleTypeService.addRregisterPouints(addPointsOrder);
			
			return vm_path + "newInvestorsOpenSuccess.vm";
		} else {
			model.addAttribute("userName", result.getUserinfo().getUserName());
			model.addAttribute("message", json.get("message"));
			logger.info("个人投资者自主注册失败:{}", json);
			return vm_path + "newInvestorsOpenFail.vm";
		}
	}
	
	@RequestMapping(value = "yjfRegisterReturnUrl.htm")
	public String yjfRegisterReturnUrl(HttpServletRequest request, HttpServletResponse response,
										ModelMap modelMap, Model model) {
		Map<String, String> param = WebUtil.getRequestMap(request);
		String result = param.get("result");
		if (StringUtil.equalsIgnoreCase(result, "success")) {
			return vm_path + "newOpenAccountSunccess.vm";
		} else {
			model.addAttribute("message", param.get("masesge"));
			return vm_path + "newOpenAccountFail.vm";
		}
		
	}
	
	/**
	 * 注册成功后创建登陆状态
	 * **/
	private void createloginStatus(HttpServletRequest request, UserRegisterResult result) {
		SessionLocal local = new SessionLocal(authorityService.getPermissionsByUserId(result
			.getUserinfo().getUserId()), result.getUserinfo().getUserId(), result.getUserinfo()
			.getUserBaseId(), result.getUserinfo().getAccountId(), result.getUserinfo()
			.getAccountName(), result.getUserinfo().getRealName(), result.getUserinfo()
			.getUserName(), authorityService.rolesNameByUserId(result.getUserinfo().getUserId()));
		local.setRemoteAddr(IPUtil.getIpAddr(request));
		SessionLocalManager.setSessionLocal(local);
	}
	
	/**
	 * 注册成功后生成易极付账户激活链接
	 * */
	private YrdBaseResult getYJFActiveUrl(InvestorRegisterOrder investorRegisterOrder) {
		YjfRegisterOrder yjfRegisterOrder = new YjfRegisterOrder();
		yjfRegisterOrder.setUserName(AppConstantsUtil.getYrdPrefixion()
										+ investorRegisterOrder.getUserName());
		yjfRegisterOrder.setYjfRegesterType(YjfRegesterTypeEnum.ACTIVATE);
		yjfRegisterOrder.setSystem("common");
		YrdBaseResult yrdBaseResult = yjfLoginWebServer.gotoYjfRegisterUrl(yjfRegisterOrder,
			this.getOpenApiContext());
		
		return yrdBaseResult;
		
	}
	
}
