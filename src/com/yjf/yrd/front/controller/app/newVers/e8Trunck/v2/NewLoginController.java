package com.yjf.yrd.front.controller.app.newVers.e8Trunck.v2;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.yjf.common.lang.ip.IPUtil;
import com.yjf.yrd.base.BaseAutowiredController;
import com.yjf.yrd.login.order.UserLoginOrder;
import com.yjf.yrd.login.result.LoginResult;
import com.yjf.yrd.session.SessionLocal;
import com.yjf.yrd.session.SessionLocalManager;
import com.yjf.yrd.ws.service.YrdResultEnum;

@Controller
@RequestMapping("app")
public class NewLoginController extends BaseAutowiredController {

	/**
	 * 用户登录
	 * 
	 * @param userName
	 * @param passWord
	 * @return
	 * @throws Exception
	 * @throws UnsupportedEncodingException
	 */
	@ResponseBody
	@RequestMapping("login.htm")
	public JSONObject doAppLogin(String userName, byte[] passWord,
			String captcha, HttpSession session, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		JSONObject json = new JSONObject();
		UserLoginOrder userLoginOrder = new UserLoginOrder();
		userLoginOrder.setUserName(userName);
		userLoginOrder.setPassword(AppCommonUtil.decode(passWord));
		userLoginOrder.setIpAddress(IPUtil.getIpAddr(request));
		LoginResult loginResult = loginService.login(userLoginOrder);
		if (loginResult.isSuccess()
				&& loginResult.getYrdResultEnum() == YrdResultEnum.EXECUTE_SUCCESS) {
			if (loginResult.getUserInfo() != null) {
				SessionLocal local = new SessionLocal(
						authorityService.getPermissionsByUserId(loginResult
								.getUserInfo().getUserId()), loginResult
								.getUserInfo().getUserId(), loginResult
								.getUserInfo().getUserBaseId(), loginResult
								.getUserInfo().getAccountId(), loginResult
								.getUserInfo().getAccountName(), loginResult
								.getUserInfo().getRealName(), loginResult
								.getUserInfo().getUserName(),
						authorityService.rolesNameByUserId(loginResult
								.getUserInfo().getUserId()));
				local.setRemoteAddr(IPUtil.getIpAddr(request));
				SessionLocalManager.setSessionLocal(local);

				json.put("userId", loginResult.getUserInfo().getUserId());
				json.put("realName", loginResult.getUserInfo().getRealName());
				json.put("userType", loginResult.getUserInfo().getType());
				json.put("code", 1);
				json.put("message", "登录成功");
			} else {
				json.put("code", 0);
				json.put("message", loginResult.getMessage());
			}

		} else {
			json.put("code", 0);
			json.put("message", loginResult.getMessage());
		}
		logger.info("手机app登录成功:loginResult={}", loginResult);
		return json;
	}

	@ResponseBody
	@RequestMapping("logout.htm")
	public JSONObject loginOut(HttpSession session) {
		JSONObject json = new JSONObject();
		session.removeAttribute("sessionInvalidCheck");
		session.invalidate();
		SessionLocalManager.destroy();
		json.put("code", 1);
		json.put("message", "退出登录成功");

		return json;
	}

	@ResponseBody
	@RequestMapping("keepConnecting.htm")
	public JSONObject keepConnecting(HttpSession session) {
		JSONObject json = new JSONObject();
		if (SessionLocalManager.getSessionLocal().getUserName().toString() != null) {
			json.put("code", 1);
			json.put("message", "会话保持");
		} else {
			json.put("code", 0);
			json.put("message", "会话中断");
		}
		return json;
	}

}
