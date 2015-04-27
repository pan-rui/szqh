package com.yjf.yrd.front.controller.app.newVers.e8Trunck.v2;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.yjf.common.lang.util.StringUtil;
import com.yjf.yrd.base.BaseAutowiredController;
import com.yjf.yrd.common.result.SmsCodeResult;
import com.yjf.yrd.session.SessionLocal;
import com.yjf.yrd.session.SessionLocalManager;
import com.yjf.yrd.user.result.UserQueryResult;
import com.yjf.yrd.ws.enums.SmsBizType;
import com.yjf.yrd.ws.result.YrdBaseResult;
import com.yjf.yrd.ws.service.YrdResultEnum;

@Controller
@RequestMapping("app")
public class NewpasswordController extends BaseAutowiredController {

	/**
	 * 修改密码 type=logPassword 0
	 * */
	@ResponseBody
	@RequestMapping("appChangePwd.htm")
	public JSONObject updatePassword(byte[] password, byte[] newPassword,
			byte[] newPasswordTo, String type) throws Exception {
		YrdBaseResult returnEnum = new YrdBaseResult();
		JSONObject jsonobj = new JSONObject();
		if (!AppCommonUtil.isLogin()) {
			jsonobj.put("code", -1);
			jsonobj.put("message", "登录失效！");
			return jsonobj;
		}
		String passwords = AppCommonUtil.decode(password);
		String newPasswords = AppCommonUtil.decode(newPassword);
		if (StringUtil.isNotEmpty(passwords)
				&& StringUtil.isNotEmpty(newPasswords)) {
			returnEnum = userBaseInfoManager.updateLogPassword(
					SessionLocalManager.getSessionLocal().getUserBaseId(),
					passwords, newPasswords);
		} else {
			jsonobj.put("code", 0);
			jsonobj.put("message", "对密码格式验证后再提交吧！");
			return null;
		}
		if (returnEnum.isSuccess()) {
			SessionLocalManager.destroy();
			jsonobj.put("code", 1);
			jsonobj.put("message", "修改密码成功，请重新登录");
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
	 * 忘记密码 type=ForgetLoginPassWord 0
	 * 
	 * @throws Exception
	 * */
	@ResponseBody
	@RequestMapping("forgetLoginPwd.htm")
	public JSONObject getPassWord(String userName, Long roleId,
			HttpSession session) throws Exception {
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
	 * 忘记密码-提交 0
	 * */
	@ResponseBody
	@RequestMapping("forgetLoginPwdSub.htm")
	public JSONObject forgetLoginPassWord(String code, byte[] newPassword,
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
						AppCommonUtil.decode(newPassword));
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
			json.put("code", 1);
			json.put("message", "设置新密码成功");
		} else {
			json.put("code", 0);
			json.put("message", "忘记密码：设置新密码失败:" + baseResult.getMessage());
		}
		logger.info("新版app用户：{}设置新密码失败", session.getAttribute("userName"));
		session.removeAttribute("mobile");
		session.removeAttribute("userBaseId");
		session.removeAttribute("userName");
		return json;
	}

}
