package com.yjf.yrd.front.controller.app.newVers.e8Trunck;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.yjf.common.lang.util.StringUtil;
import com.yjf.yrd.base.BaseAutowiredController;
import com.yjf.yrd.common.result.SmsCodeResult;
import com.yjf.yrd.integration.openapi.order.MobileBindOrder;
import com.yjf.yrd.integration.openapi.order.RegisterOrder;
import com.yjf.yrd.integration.openapi.result.CustomerResult;
import com.yjf.yrd.session.SessionLocal;
import com.yjf.yrd.session.SessionLocalManager;
import com.yjf.yrd.user.result.UserQueryResult;
import com.yjf.yrd.ws.enums.BooleanEnum;
import com.yjf.yrd.ws.enums.SmsBizType;
import com.yjf.yrd.ws.result.YrdBaseResult;
import com.yjf.yrd.ws.service.YrdResultEnum;

/**
 * 
 * 
 * @Filename appNewAcountSettingController.java
 * 
 * @Description
 * 
 * @Version 2.0
 * 
 * @Author zhaohaibing
 * 
 * @Email abing@yiji.com
 * 
 *        Date: 2014-12-11
 * 
 * 
 */
@Controller
@RequestMapping("appNew")
public class AppNewAcountSettingController extends BaseAutowiredController {
	/**
	 * 修改密码 type=logPassword
	 * */
	@ResponseBody
	@RequestMapping("appChangePwd.htm")
	public Object updatePassword(String oldPassword, String password,
			String newPassword) throws Exception {
		YrdBaseResult returnEnum = new YrdBaseResult();
		JSONObject jsonobj = new JSONObject();
		if (StringUtil.isNotEmpty(oldPassword)) {
			password = oldPassword;
		}
		returnEnum = userBaseInfoManager.updateLogPassword(SessionLocalManager
				.getSessionLocal().getUserBaseId(), password, newPassword);
		jsonobj.put("result", "0");
		if (returnEnum.isSuccess()) {
			SessionLocalManager.destroy();
			jsonobj.put("code", 1);
			jsonobj.put("message", "设置新密码成功");
			jsonobj.put("result", "1");
		} else if (returnEnum.getYrdResultEnum() == YrdResultEnum.PASSWORD_ERROR) {
			jsonobj.put("code", 0);
			jsonobj.put("message", "旧密码输入不正确");
		} else {
			jsonobj.put("code", 0);
			jsonobj.put("message", returnEnum.getMessage());
		}
		return jsonobj;
	}

	/**
	 * 忘记密码 type=ForgetLoginPassWord
	 * 
	 * @throws Exception
	 * */
	@ResponseBody
	@RequestMapping("forgetLoginPwd.htm")
	public JSONObject getPassWord(String userName, Long roleId,
			HttpSession session, HttpServletResponse response) throws Exception {
		JSONObject json = new JSONObject();
		logger.info("app用户忘记密码。。。。");
		UserQueryResult baseResult = userQueryService.queryByUserName(userName);
		if (baseResult.getQueryUserInfo() != null) {
			if ("IS".equals(baseResult.getQueryUserInfo().getMobileBinding()
					.code())) {
				try {
					SessionLocal sessionLocal = SessionLocalManager
							.getSessionLocal();
					sessionLocal.setSessionMobileSend(null);
					SessionLocalManager.setSessionLocal(sessionLocal);
				} catch (NullPointerException e) {
					logger.info("忘记密码提交用户名时清除session中的SmsCode");
				}
				session.setAttribute("mobile", baseResult.getQueryUserInfo()
						.getMobile());
				session.setAttribute("userBaseId", baseResult
						.getQueryUserInfo().getUserBaseId());
				session.setAttribute("userName", baseResult.getQueryUserInfo()
						.getUserName());
				json.put("mobile", baseResult.getQueryUserInfo().getMobile());
				json.put("code", 1);
				json.put("message", "获取电话号码成功");
			} else {
				json.put("code", 0);
				json.put("message", "当前用户未绑定手机号码");
				logger.info("app用户忘记密码：查询结果{}", "当前用户未绑定手机号码");
			}

		} else {
			json.put("code", 0);
			json.put("message", "用户不存在");
			logger.info("app用户忘记密码：查询结果{}", "用户不存在");
		}

		return json;

	}

	/**
	 * 忘记密码-提交
	 * */
	@ResponseBody
	@RequestMapping("forgetLoginPwdSub.htm")
	public Object forgetLoginPassWord(String code, String newPassword,
			HttpSession session, HttpServletResponse response) throws Exception {
		JSONObject json = new JSONObject();
		YrdBaseResult baseResult = new YrdBaseResult();
		try {
			SmsCodeResult smsCodeResult = smsManagerService.verifySmsCode(
					(String) session.getAttribute("mobile"),
					SmsBizType.REGISTER, code, true);
			if (smsCodeResult.isSuccess()) {
				baseResult = userBaseInfoManager.forgetPassword(
						(String) session.getAttribute("userBaseId"),
						newPassword);
			} else {
				json.put("code", 0);
				json.put("message", "短信验证码错误");
				logger.info("app用户:忘记密码-提交结果：{}", "短信验证码错误");
				return json;
			}
		} catch (NullPointerException e) {
			json.put("code", 0);
			json.put("message", "请获取短信验证码");

			logger.info("app用户:忘记密码-提交结果：{}", "未获取验证密码");
			return json;
		}
		if (baseResult.isSuccess()) {
			json.put("code", "1");
			json.put("message", "设置新密码成功");
			json.put("result", "1");
			logger.info("用户：{} 忘记密码：设置新密码成功", session.getAttribute("userName"));
		} else {
			json.put("code", "0");
			json.put("message", "忘记密码：设置新密码失败:" + baseResult.getMessage());
			json.put("result", "0");
			logger.info("新版app用户：{}设置新密码失败", session.getAttribute("userName"));
		}
		session.removeAttribute("mobile");
		session.removeAttribute("userBaseId");
		session.removeAttribute("userName");

		return json;
	}

	/**
	 * 18、修改手机号码-验证原手机号 type=cellphone
	 * 
	 * @param code
	 * @Param newPhone
	 * */
	@ResponseBody
	@RequestMapping("appChangeMobile.htm")
	public Object checkBoundMobile(String code, String newPhone,
			HttpSession session) throws Exception {
		JSONObject jsonobj = new JSONObject();
		UserQueryResult baseResult = userQueryService
				.queryByUserName(SessionLocalManager.getSessionLocal()
						.getUserName());
		SmsCodeResult smsCodeResult = smsManagerService.verifySmsCode(
				baseResult.getQueryUserInfo().getMobile(),
				SmsBizType.CELLPHONE, code, true);
		if (smsCodeResult.isSuccess()) {
			YrdBaseResult baseResult1 = smsManagerService.sendSmsCode(newPhone,
					SmsBizType.NEWCELLPHONE, null);
			if (baseResult1.isSuccess()) {
				session.setAttribute("newPhone", newPhone);
				jsonobj.put("code", "1");
				jsonobj.put("message", "原手机号码校验成功:已向你的新手机号码发送验证码");
				jsonobj.put("newPhone", newPhone);
				jsonobj.put("result", "1");
			} else {
				jsonobj.put("code", "0");
				jsonobj.put("message", baseResult1.getMessage());
				jsonobj.put("newPhone", newPhone);
				jsonobj.put("result", "0");
			}
			logger.info("新版app用户：{}验证原手机号码成功：向新手机号码发送验证码：baseResult1={}",
					SessionLocalManager.getSessionLocal().getUserName(),
					baseResult1);

		} else {
			jsonobj.put("code", "0");
			jsonobj.put("message", smsCodeResult.getMessage());
			jsonobj.put("result", "0");
			logger.info("新版app用户：{}原手机号码验证失败：验证码错误", SessionLocalManager
					.getSessionLocal().getUserName());
		}
		return jsonobj;
	}

	/**
	 * 修改手机号 type=cellphone
	 * */
	@ResponseBody
	@RequestMapping("appChangeToNewPhone.htm")
	public Object updateBoundMobile(String newMobile, String code,
			HttpSession session) throws Exception {
		newMobile = (String) session.getAttribute("newPhone");
		JSONObject jsonobj = new JSONObject();
		UserQueryResult baseResult = userQueryService
				.queryByUserName(SessionLocalManager.getSessionLocal()
						.getUserName());
		SmsCodeResult smsCodeResult = smsManagerService.verifySmsCode(
				baseResult.getQueryUserInfo().getMobile(),
				SmsBizType.CELLPHONE, code, true);
		if (!smsCodeResult.isSuccess()) {
			jsonobj.put("code", "0");
			jsonobj.put("message", "验证码校验失败");
			jsonobj.put("result", "0");
			logger.info("新版app用户：{}修改绑定手机号码失败1：验证码错误", SessionLocalManager
					.getSessionLocal().getUserName());
			return jsonobj;
		}

		baseResult.getQueryUserInfo().setMobile(newMobile);
		MobileBindOrder mobileBindOrder = new MobileBindOrder();
		mobileBindOrder.setUserId(baseResult.getQueryUserInfo().getAccountId());
		mobileBindOrder.setMobile(newMobile);
		mobileBindOrder.setIsNew(BooleanEnum.NO);
		YrdBaseResult baseResults = this.customerService.updateMobileBinding(
				mobileBindOrder, this.getOpenApiContext());
		if (baseResults.isSuccess()) {
			YrdBaseResult result = userBaseInfoManager.mobileBinding(baseResult
					.getQueryUserInfo().getUserBaseId(), newMobile);
			if (result.isSuccess()) {
				jsonobj.put("code", "1");
				jsonobj.put("message", "修改绑定手机成功");
				jsonobj.put("result", "1");
				session.removeAttribute("newPhone");
				logger.info("新版app用户：{}修改绑定手机号码成功", SessionLocalManager
						.getSessionLocal().getUserName());
			} else {
				jsonobj.put("code", "0");
				jsonobj.put("message", "修改绑定手机失败");
				jsonobj.put("result", "0");
				logger.info("新版app用户：{}修改绑定手机号码失败2", SessionLocalManager
						.getSessionLocal().getUserName());
			}
		}
		return jsonobj;
	}

	/**
	 * 修改邮箱 type=personal
	 * */

	@ResponseBody
	@RequestMapping("updateMail.htm")
	public Object UpdateBoundMail(String newMail, String code) throws Exception {
		JSONObject jsonobj = new JSONObject();

		UserQueryResult baseResult = userQueryService
				.queryByUserName(SessionLocalManager.getSessionLocal()
						.getUserName());
		SmsCodeResult smsCodeResult = smsManagerService.verifySmsCode(
				baseResult.getQueryUserInfo().getMobile(), SmsBizType.PERSONAL,
				code, true);
		if (!smsCodeResult.isSuccess()) {
			jsonobj.put("code", 0);
			jsonobj.put("message", "验证码校验失败");
			jsonobj.put("isSuccess", "0");
			logger.info("新版app用户：{}修改绑定邮箱失败1：验证码错误", SessionLocalManager
					.getSessionLocal().getUserName());
			return jsonobj;
		}
		baseResult.getQueryUserInfo().setMail(newMail);
		baseResult.getQueryUserInfo().setMailBinding(BooleanEnum.IS);
		RegisterOrder order = new RegisterOrder();
		order.setUserId(baseResult.getQueryUserInfo().getAccountId());
		order.setEmail(newMail);
		CustomerResult customerResult = customerService.openCapitalAccount(
				order, this.getOpenApiContext());
		if (!customerResult.isSuccess()) {
			jsonobj.put("code", 0);
			jsonobj.put("message", "修改绑定邮箱失败");
			jsonobj.put("isSuccess", "0");
			logger.info("新版app用户：{}修改绑定邮箱失败2", SessionLocalManager
					.getSessionLocal().getUserName());
		}
		YrdBaseResult result = userBaseInfoManager.mailBinding(baseResult
				.getQueryUserInfo().getUserBaseId(), newMail);
		if (result.isSuccess()) {
			jsonobj.put("code", 1);
			jsonobj.put("message", "修改绑定邮箱成功");
			jsonobj.put("isSuccess", "1");
			logger.info("新版app用户：{}修改绑定邮箱成功", SessionLocalManager
					.getSessionLocal().getUserName());
		} else {
			jsonobj.put("code", 0);
			jsonobj.put("message", "修改绑定邮箱失败");
			jsonobj.put("isSuccess", "0");
			logger.info("新版app用户：{}修改绑定邮箱失败3", SessionLocalManager
					.getSessionLocal().getUserName());
		}
		return jsonobj;
	}

	/**
	 * 发送短信
	 * 
	 * @param mobile
	 * @Param business 1:cellphone:修改绑定手机号 2:personal：修改绑定邮箱 3:invest：投资 4:
	 *        deposit 充值 5: register 注册 6: ForgetLoginPassWord 找回密码
	 * @param new Data()
	 * 
	 * */
	@ResponseBody
	@RequestMapping("sendSmsCode.htm")
	public Object sendSmsCode(HttpServletResponse response, String mobile,
			String business) throws Exception {
		JSONObject json = new JSONObject();
		// 注册。。。。。。。
		if ("register".equals(business)
				|| "ForgetLoginPassWord".equals(business)) {
			SessionLocal sessionLocal = new SessionLocal();
			sessionLocal.addAttibute("appRegist", true);
			SessionLocalManager.setSessionLocal(sessionLocal);
			logger.info("手机app用户注册请求发送手机验证码，入参[{}],[{}]", mobile, business);
			YrdBaseResult baseResult = smsManagerService.sendSmsCode(mobile,
					SmsBizType.getByCode(business), null);
			if (baseResult.isSuccess()) {
				json.put("code", 1);
				json.put("message", "发送手机验证码成功");
				json.put("isSuccess", "1");
				logger.info("app用户注册请求发送验证码成功：code={},business={}",
						baseResult.getMessage(), business);
			} else {
				json.put("code", 0);
				json.put("message", baseResult.getMessage());
				json.put("isSuccess", "0");
			}
			logger.info("手机app注册：请求发送手机验证码成功，结果：{}", baseResult.getMessage());
			return json;

		}

		logger.info("手机app用户(user:{})请求发送手机验证码，入参[{}],[{}]",
				SessionLocalManager.getSessionLocal().getUserName(), mobile,
				business);
		if (SessionLocalManager.getSessionLocal() != null) {
			UserQueryResult baseResult = userQueryService
					.queryByUserName(SessionLocalManager.getSessionLocal()
							.getUserName());
			mobile = baseResult.getQueryUserInfo().getMobile();
		}
		YrdBaseResult baseResult = smsManagerService.sendSmsCode(mobile,
				SmsBizType.getByCode(business), null);
		if (baseResult.isSuccess()) {
			json.put("code", 1);
			json.put("message", "发送手机验证码成功");
			json.put("isSuccess", "1");
			logger.info("新版app用户：{}请求发送验证码成功：code={},business={}",
					SessionLocalManager.getSessionLocal().getUserName(),
					baseResult.getMessage(), business);
		} else {
			json.put("code", 0);
			json.put("message", baseResult.getMessage());
			json.put("isSuccess", "0");
			logger.info("新版app用户：{}请求发送验证码失败:business={}", SessionLocalManager
					.getSessionLocal().getUserName(), business);
		}
		logger.info("手机app请求发送手机验证码成功，结果：{}", baseResult.getMessage());
		return json;
	}

	/**
	 * 验证短信
	 * */
	@ResponseBody
	@RequestMapping("checkSmsCode.htm")
	public Object checkSmsCode(String mobile, String business, String code)
			throws Exception {
		logger.info("验证手机验证码，入参[{}],[{}]", mobile, business);
		JSONObject json = new JSONObject();
		try {
			SmsCodeResult smsCodeResult = this.smsManagerService.verifySmsCode(
					mobile, SmsBizType.getByCode(business), code, false);

			if (smsCodeResult.isSuccess()) {
				json.put("code", 1);
				json.put("message", "验证手机验证码成功");
			} else {
				json.put("code", 0);
				json.put("message", smsCodeResult.getMessage());
			}
		} catch (NullPointerException e) {
			json.put("code", 0);
			json.put("message", "请先获取验证码");
		}
		logger.info("验证手机验证码，结果：{}", json);
		return json;
	}

	/**
	 * 判断是否登录
	 * 
	 * @return boolean
	 */
	public boolean isLogin() {
		try {
			if (SessionLocalManager.getSessionLocal().getUserName().toString() != null) {
				logger.info("手机app查询是否登录！");
				return true;
			}
		} catch (NullPointerException e) {
			logger.error("判断是否登录出现空指针异常:", e);
		}
		return false;
	}
}
