package com.yjf.yrd.front.controller.app.newVers.e8Trunck;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.yjf.common.lang.util.StringUtil;
import com.yjf.yrd.base.BaseAutowiredController;
import com.yjf.yrd.common.result.SmsCodeResult;
import com.yjf.yrd.user.info.UserInfo;
import com.yjf.yrd.user.order.InvestorRegisterOrder;
import com.yjf.yrd.user.result.UserRegisterResult;
import com.yjf.yrd.user.result.UserRelationQueryResult;
import com.yjf.yrd.util.AppConstantsUtil;
import com.yjf.yrd.ws.enums.BooleanEnum;
import com.yjf.yrd.ws.enums.SmsBizType;
import com.yjf.yrd.ws.enums.SysUserRoleEnum;
import com.yjf.yrd.ws.result.YrdBaseResult;

/**
 * app用户个人注册
 * 
 * 
 * 
 * */
@Controller
@RequestMapping("appNew")
public class AppNewRegisterController extends BaseAutowiredController {

	/**
	 * 验证用户名是否存在
	 * 
	 * @param userName
	 * */
	@ResponseBody
	@RequestMapping("verifyUser.htm")
	public JSONObject verifyUser(HttpSession session, String userName,
			HttpServletResponse response) throws Exception {
		JSONObject json = new JSONObject();
		YrdBaseResult queryResult = userBaseInfoManager
				.validationUserName(userName);
		if (queryResult.isSuccess()) {
			json.put("code", 1);
			json.put("message", "用户名不存在");
		} else {
			json.put("code", 0);
			json.put("message", "用户名存在");
		}
		logger.info("app用户进入个人注册页面：校验用户名是否存在：userName={},result={}", userName,
				json.getString("message"));
		return json;
	}

	/**
	 * @throws IOException
	 * 
	 * 
	 * */
	@ResponseBody
	@RequestMapping("toRegisterPage.htm")
	public JSONObject newinvestorsOpen(HttpSession session, String NO,
			HttpServletResponse response) throws IOException {
		JSONObject json = new JSONObject();
		UserRelationQueryResult relationQueryResult = userRelationQueryService
				.findUserRelationByMemberNo(NO);
		if (relationQueryResult.getQueryUserRelationInfo() != null) {
			session.removeAttribute("referNotExist");
			session.setAttribute("referees", NO);
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
	 * 注册提交
	 * 
	 * @param code
	 *            短信验证码
	 * 
	 * */
	@ResponseBody
	@RequestMapping("registerSubmit.htm")
	public JSONObject registerSubmit(HttpSession session,
			InvestorRegisterOrder investorRegisterOrder, String code,
			HttpServletRequest request, String token,
			HttpServletResponse response) throws Exception {
		JSONObject json = new JSONObject();
		logger.info("个人投资者自主注册，入参1：{}", investorRegisterOrder);
		try {
			SmsCodeResult smsCodeResult = smsManagerService.verifySmsCode(
					investorRegisterOrder.getMobile(), SmsBizType.REGISTER,
					code, true);
			if (!smsCodeResult.isSuccess()) {
				json.put("code", 0);
				json.put("message", "短信验证码错误：请检查验证码或手机号码是否正确");

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
		UserRegisterResult registerResult = registerService
				.investorRegister(investorRegisterOrder);
		logger.info("个人投资者自主注册，结果：{}", json);
		if (registerResult.isSuccess()) {
			UserInfo info = registerResult.getUserinfo();
			json.clear();
			Map<String, String> map = new HashMap<String, String>();
			map.put("userId", info.getAccountId());
			map.put("username", info.getAccountName());
			map.put("memo", "userId和username都是易极付那边的");
			json.put("loginUserName", info.getUserName());
			json.put("platformName", AppConstantsUtil.getYrdPrefixion());
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
