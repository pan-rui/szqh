package com.yjf.yrd.front.controller.app.newVers.e8Trunck.v2;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.yjf.yrd.base.BaseAutowiredController;
import com.yjf.yrd.base.Image;
import com.yjf.yrd.common.result.SmsCodeResult;
import com.yjf.yrd.session.SessionLocal;
import com.yjf.yrd.session.SessionLocalManager;
import com.yjf.yrd.user.result.UserQueryResult;
import com.yjf.yrd.ws.enums.SmsBizType;
import com.yjf.yrd.ws.result.YrdBaseResult;

@Controller
@RequestMapping("app")
public class GetCheckCode extends BaseAutowiredController {

	/**
	 * 验证码
	 * */
	@ResponseBody
	@RequestMapping("getImgCode.htm")
	public JSONObject getImgCode(HttpSession session,
			HttpServletResponse response) throws IOException {
		JSONObject json = new JSONObject();
		BufferedImage bufferedImage = Image.creatImage(session);
		ServletOutputStream out = response.getOutputStream();
		ImageIO.write(bufferedImage, "jpg", out);
		json.put("captcha", bufferedImage);
		json.put("code", "1");
		json.put("message", "获取验证码成功");
		logger.info("app用户获取登陆验证码成功：code={}", session.getAttribute("imgCode"));
		out.flush();
		out.close();
		return json;
	}

	/**
	 * 发送短信 v
	 * 
	 * @param mobile
	 * @Param business 1:cellphone:修改绑定手机号 2:personal：修改绑定邮箱 3:invest：投资 4:
	 *        deposit 充值 5: register 注册 6: ForgetLoginPassWord 找回密码
	 * @param new Data()
	 * 
	 * */
	@ResponseBody
	@RequestMapping("sendSmsCode.htm")
	public JSONObject sendSmsCode(HttpServletResponse response, String mobile,
			String business) throws Exception {
		JSONObject json = new JSONObject();
		// 注册。。。。。。。
		if ("register".equals(business)
				|| "ForgetLoginPassWord".equals(business)) {
			SessionLocal sessionLocal = new SessionLocal();
			sessionLocal.addAttibute("appRegist", true);
			SessionLocalManager.setSessionLocal(sessionLocal);
			YrdBaseResult baseResult = smsManagerService.sendSmsCode(mobile,
					SmsBizType.getByCode(business), null);
			if (baseResult.isSuccess()) {
				json.put("code", 1);
				json.put("message", "发送手机验证码成功");
			} else {
				json.put("code", 0);
				json.put("message", baseResult.getMessage());
			}
			logger.info("手机app注册：请求发送手机验证码成功，结果：{}", baseResult);
			return json;

		}
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
		} else {
			json.put("code", 0);
			json.put("message", baseResult.getMessage());
		}
		logger.info("手机app请求发送手机验证码成功，结果：{}", baseResult);
		return json;
	}

	/**
	 * 校验短信验证码
	 * 
	 * */
	@ResponseBody
	@RequestMapping("verifySmsCode.htm")
	public JSONObject verifySmsCode(String code, String mobile, String business)
			throws Exception {
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
}
