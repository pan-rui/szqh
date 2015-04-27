package com.yjf.yrd.front.controller.app.newVers.e8Trunck.v2;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.yjf.common.lang.util.StringUtil;
import com.yjf.yrd.base.BaseAutowiredController;
import com.yjf.yrd.common.result.SmsCodeResult;
import com.yjf.yrd.integration.openapi.result.CustomerResult;
import com.yjf.yrd.session.SessionLocal;
import com.yjf.yrd.session.SessionLocalManager;
import com.yjf.yrd.user.info.UserInfo;
import com.yjf.yrd.user.order.InvestorRegisterOrder;
import com.yjf.yrd.user.result.UserRegisterResult;
import com.yjf.yrd.user.result.UserRelationQueryResult;
import com.yjf.yrd.util.AppConstantsUtil;
import com.yjf.yrd.ws.enums.BooleanEnum;
import com.yjf.yrd.ws.enums.SmsBizType;
import com.yjf.yrd.ws.enums.SysUserRoleEnum;
import com.yjf.yrd.ws.result.YrdBaseResult;

@Controller
@RequestMapping("app")
public class AppRegistController extends BaseAutowiredController {

	/**
	 * @throws IOException
	 *             获取注册前置信息
	 * 
	 * */
	@ResponseBody
	@RequestMapping("toRegisterPage.htm")
	public JSONObject newinvestorsOpen(HttpSession session, String NO,
			String refererNo) throws IOException {
		JSONObject json = new JSONObject();
		String referees = "";
		if (StringUtil.isNotEmpty(NO)) {
			referees = NO;
		} else {
			referees = refererNo;
		}
		UserRelationQueryResult relationQueryResult = userRelationQueryService
				.findUserRelationByMemberNo(referees);
		if (relationQueryResult.getQueryUserRelationInfo() != null) {
			session.removeAttribute("referNotExist");
			session.setAttribute("referees", referees);
		} else {
			session.setAttribute("referNotExist", "推荐人不存在");
		}
		String token = UUID.randomUUID().toString();
		session.setAttribute("token", token);
		session.removeAttribute("brokerNo");
		json.put("code", 1);
		json.put("message", "获取注册前置信息成功");
		json.put("token", token);
		logger.info("app用户进入个人注册页面：获取token={}", token);
		return json;
	}

	/**
	 * 扫二维码注册
	 * 
	 * **/

	@RequestMapping("preRegist.htm")
	public String preRegist(String referees, HttpSession session, Model model) {

		UserRelationQueryResult relationQueryResult = userRelationQueryService
				.findUserRelationByMemberNo(referees);
		if (relationQueryResult.getQueryUserRelationInfo() != null) {
			session.removeAttribute("referNotExist");
			session.setAttribute("referees", referees);
			model.addAttribute("referees", referees);
		} else {
			session.setAttribute("referNotExist", "推荐人不存在");
		}
		SessionLocal sessionLocal = new SessionLocal();
		sessionLocal.addAttibute("appRegist", true);
		SessionLocalManager.setSessionLocal(sessionLocal);
		String token = UUID.randomUUID().toString();
		session.setAttribute("token", token);
		return "/backstage/appManager/appPreRegist/preRegist.vm";
	}
	
	@RequestMapping("preRegistSubmit.htm")
	public String preRegistSubmit(HttpSession session,
			InvestorRegisterOrder investorRegisterOrder, String code,
			HttpServletRequest request, String token, String password,
			Model model) throws Exception {
		investorRegisterOrder.setLogPassword(password);
		JSONObject json = registerSubmit(session, null, investorRegisterOrder,
				code);
		if (json.getInteger("code") == 1) {
			model.addAttribute("registResult", 1);
			model.addAttribute("message", "注册成功");
			String url =json.getString("activeYjfPwdUrl");
			model.addAttribute("activeYjfPwdUrl",url.replace("/app/login.htm", ""));
		} else {
			model.addAttribute("registResult", 0);
			model.addAttribute("referees", investorRegisterOrder.getReferees());
			model.addAttribute("message", json.getString("message"));
		}
		return "/backstage/appManager/appPreRegist/preRegistResult.vm";
	}

	@ResponseBody
	@RequestMapping("yjfPreRegistSubmit.htm")
	public JSONObject preRegistSubmit(InvestorRegisterOrder investorRegisterOrder,
			String registName,
			HttpServletResponse response) throws Exception {
		JSONObject json = new JSONObject();

		UserRegisterResult registerResult = registerService
				.investorRegister(investorRegisterOrder);
		if (registerResult.isSuccess()) {
			String yjfUrl = AppConstantsUtil.getYjfUrl();
			String lockHost = AppConstantsUtil.getHostHttpUrl();
			// "http://ppm.yijifu.net/anon/app/register/index.htm?redirect=http://192.168.45.212:28000/openApi/yjfLogin.htm";
			String registUrl = yjfUrl
					+ "/anon/app/register/index.htm?redirect=" + lockHost
					+ "/openApi/yjfLogin.htm&userName="
					+ investorRegisterOrder.getUserName();
			response.sendRedirect(registUrl);
			json.put("code", 1);
			json.put("message", "添加用户成功");
			return json;

		} else {
			json.put("code", 0);
			json.put("message", registerResult.getMessage());
			return json;
		}
	}
	/**
	 * 验证用户名是否存在
	 * 
	 * @param userName
	 * */
	@ResponseBody
	@RequestMapping("verifyUser.htm")
	public JSONObject verifyUser(String userName) throws Exception {
		JSONObject json = new JSONObject();
		YrdBaseResult queryResult = userBaseInfoManager
				.validationUserName(userName);
		CustomerResult customerResult = customerService.checkUserNameExist(
				AppConstantsUtil.getYrdPrefixion() + userName,
				getOpenApiContext());
		if (queryResult.isSuccess() && !customerResult.isExsit()) {
			json.put("code", 1);
			json.put("message", "用户名不存在");
		} else {
			json.put("code", 0);
			json.put("message", "用户名存在");
		}
		logger.info("app注册：校验用户名是否存在：userName={},result={}", userName, json);
		return json;
	}

	/**
	 * 注册提交
	 * 
	 * @param code
	 *            短信验证码
	 * 
	 * */
	@ResponseBody
	@RequestMapping("registerSubmit.htm")
	public JSONObject registerSubmit(HttpSession session, byte[] password,
			InvestorRegisterOrder investorRegisterOrder, String code)
			throws Exception {
		JSONObject json = new JSONObject();
		logger.info("个人投资者自主注册，入参1：{}", investorRegisterOrder);
		try {
			SmsCodeResult smsCodeResult = smsManagerService.verifySmsCode(
					investorRegisterOrder.getMobile(), SmsBizType.REGISTER,
					code, true);
			if (!smsCodeResult.isSuccess()) {
				json.put("code", 0);
				json.put("message", smsCodeResult.getMessage());
				return json;
			}
			investorRegisterOrder.setMobileBinding(BooleanEnum.IS.code());
		} catch (NullPointerException e) {
			json.put("code", 0);
			json.put("message", "未获取短信验证码");
			return json;
		}
		String referees = (String) session.getAttribute("referees");
		if (StringUtil.isNotEmpty(referees)) {
			// 以经济人给的链接注册的用户不可更改经纪人的编号
			investorRegisterOrder.setReferees(referees);
		}
		if (sysFunctionConfigService.isAllEconomicMan()) {
			investorRegisterOrder.getRole().add(SysUserRoleEnum.BROKER);
		}
		if (password != null && password.length > 0) {
			String logPasswords = AppCommonUtil.decode(password);
			investorRegisterOrder.setLogPassword(logPasswords);
		}
		UserRegisterResult registerResult = registerService
				.investorRegister(investorRegisterOrder);
		logger.info("个人投资者自主注册，结果：{}", json);
		if (registerResult.isSuccess()) {
			UserInfo info = registerResult.getUserinfo();
			json.clear();
			if (registerResult.isGiftMoneySuccess()) {
				json.put("giftMoney", 1);
				json.put("giftMoneyMessage",
						registerResult.getGiftMoneyMessage());
			} else {
				json.put("giftMoney", 0);
				json.put("giftMoneyMessage", "无红包信息");
			}
			Map<String, String> map = new HashMap<String, String>();
			map.put("userId", info.getAccountId());
			map.put("username", info.getAccountName());
			json.put("loginUserName", info.getUserName());
			json.put("platformName", AppConstantsUtil.getYrdPrefixion());
			json.put("activeYjfPwdUrl",
					AppCommonUtil.appAccountActiveUrl(info.getAccountId()));
			json.put("code", 1);
			json.put("message", "注册成功");
			json.put("accountInfo", map);
			logger.info("个人投资者自主注册成功，结果：{}，资金账号信息：{}", "注册成功", map);

		} else {
			json.put("code", 0);
			json.put("message", registerResult.getMessage());
			logger.info("个人投资者自主注册失败，registerResult={}", registerResult);
		}
		return json;
	}
}
