package com.yjf.yrd.front.controller.app.newVers.e8Trunck;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.yjf.common.lang.ip.IPUtil;
import com.yjf.yrd.base.BaseAutowiredController;
import com.yjf.yrd.base.Image;
import com.yjf.yrd.login.order.UserLoginOrder;
import com.yjf.yrd.login.result.LoginResult;
import com.yjf.yrd.session.SessionLocal;
import com.yjf.yrd.session.SessionLocalManager;
import com.yjf.yrd.web.util.PermissionUtil;
import com.yjf.yrd.ws.service.YrdResultEnum;

/**
 * @Filename appNewLoginController.java
 * 
 * @Description
 * 
 * @Version 2.0
 * 
 * @Author zhaohaibing
 * 
 * @Email abing@yiji.com
 * 
 *        Date: 2014-9-1
 * 
 */

@Controller
@RequestMapping("appNew")
public class AppNewLoginController extends BaseAutowiredController {

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
	public JSONObject doAppLogin(String userName, String passWord,
			String captcha, HttpSession session, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		JSONObject json = new JSONObject();
		logger.info("手机app请求登录:userName={}", userName);
		UserLoginOrder userLoginOrder = new UserLoginOrder();
		userLoginOrder.setUserName(userName);
		userLoginOrder.setPassword(passWord);
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
				logger.info("手机app登录成功:loginResult={}", loginResult);
			} else {
				json.put("code", 0);
				json.put("message", loginResult.getMessage());
				logger.info("手机app登录失败：loginResult={}", loginResult);
			}

		} else {
			json.put("code", 0);
			json.put("message", loginResult.getMessage());
			logger.info("手机app登录失败：loginResult={}", loginResult);
		}
		return json;
	}

	/**
	 * 权限查询接口
	 * 
	 * @param session
	 * @throws IOException
	 * **/
	@ResponseBody
	@RequestMapping("checkPermission.htm")
	public JSONObject checkPermission(HttpServletResponse response,
			HttpSession session) throws IOException {
		logger.info("手机用户查询当前用户的权限.....");
		JSONObject json = new JSONObject();
		if (null != SessionLocalManager.getSessionLocal().getUserName()) {
			int userHome = 1;/* 账户总览? */
			int invest = PermissionUtil.check("/tradeQuery/entries/12/1");/* 投资的项目 */
			int borrowing = PermissionUtil.check("/tradeQuery/borrowingRecord");/* 投资接受记录 */
			int investorManager = PermissionUtil.check("/investorManager/*");/*
																			 * 客户管理/
																			 * 业务管理
																			 */
			int guaranteeCenter = PermissionUtil.check("/guaranteeCenter/*");/* 业务管理 */
			int guaranteeOperator = PermissionUtil
					.check("/guaranteeOperator/*");/* 操作员管理 */

			Map<String, String> map = new HashMap<String, String>();
			map.put("userHome", "" + userHome);
			map.put("invest", "" + invest);
			map.put("borrowing", "" + borrowing);
			map.put("investorManager", "" + investorManager);
			map.put("guaranteeCenter", "" + guaranteeCenter);
			map.put("guaranteeOperator", "" + guaranteeOperator);

			json.put("code", "1");
			json.put("message", "权限查询成功");
			json.put("permission", map);
			logger.info("手机app查询当前用户的权限成功：userName={}", SessionLocalManager
					.getSessionLocal().getUserName());
		} else {
			json.put("code", "0");
			json.put("message", "权限查询失败:未登录或登录已失效");
			logger.info("手机app权限查询失败:未登录或登录已失效。。");
		}

		return json;

	}

	/**
	 * app退出登录
	 * 
	 * @return
	 * @throws IOException
	 */
	@ResponseBody
	@RequestMapping("logout.htm")
	public JSONObject logout(HttpSession session, HttpServletResponse response)
			throws IOException {
		JSONObject json = new JSONObject();
		session.removeAttribute("sessionInvalidCheck");
		session.invalidate();
		SessionLocalManager.destroy();
		json.put("code", "1");
		json.put("result", "1");
		json.put("message", "成功退出登录");

		return json;
	}

	/**
	 * 验证码
	 * */
	@ResponseBody
	@RequestMapping("getCheckCode.htm")
	public String getImgCode(HttpSession session, HttpServletResponse response)
			throws IOException {
		JSONObject json = new JSONObject();
		BufferedImage bufferedImage = Image.creatImage(session);
		ServletOutputStream out = response.getOutputStream();
		ImageIO.write(bufferedImage, "jpg", out);
		json.put("captcha", bufferedImage);
		json.put("code", "1");
		json.put("message", "获取验证码成功");
		logger.info("app用户获取登录验证码成功：code={}", session.getAttribute("imgCode"));
		out.flush();
		out.close();
		return null;
	}

}
