package com.yjf.yrd.backstage.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.yjf.common.lang.ip.IPUtil;
import com.yjf.common.lang.util.StringUtil;
import com.yjf.yrd.base.BaseAutowiredController;
import com.yjf.yrd.login.order.UserLoginOrder;
import com.yjf.yrd.login.result.LoginResult;
import com.yjf.yrd.session.SessionLocalManager;
import com.yjf.yrd.user.info.UserInfo;
import com.yjf.yrd.util.AESEncrypt;
import com.yjf.yrd.util.AppConstantsUtil;
import com.yjf.yrd.ws.result.YrdBaseResult;
import com.yjf.yrd.ws.service.YrdResultEnum;

/**
 * 
 * 
 * @Filename BackstageLoginController.java
 * 
 * @Description
 * 
 * @Version 1.0
 * 
 * @Author zjl
 * 
 * @Email zjialin@yiji.com
 * 
 * @History <li>Author: zjl</li> <li>Date: 2013-8-21</li> <li>Version: 1.0</li>
 * <li>Content: create</li>
 * 
 */
@Controller
@RequestMapping("backstagelogin")
public class BackstageLoginController extends BaseAutowiredController {
	private final String vm_path = "/backstage/";
	private static String LOGIN_MESSAGE = "用户名或者密码错误";
	private static String LOGIN_MESSAGE_DISABLE = "该用户不可用...";
	/**
	 * 账户已被锁定
	 */
	private static String LOGIN_MESSAGE_LOCKED = "此账户已被锁定，请等待解锁";
	/**
	 * 账户不存在
	 */
	private static String LOGIN_USER_UNKNOWN = "账户不存在";
	
	@SuppressWarnings("unused")
	@RequestMapping("login")
	private String backstageLogin(String userName, String logPassword, Model model,
									HttpSession session, HttpServletRequest request,
									HttpServletResponse response) {
		logger.info("用户登录后台系统，入参:[username={}]{}", userName, AppConstantsUtil.getProductName());
		if (!StringUtil.isNotEmpty(userName)) {
			return vm_path + "backstageLogin.vm";
		}

        UserInfo userInfo = userQueryService.queryByUserName(userName).getQueryUserInfo();
        if(userInfo == null || StringUtil.isNotBlank(userInfo.getAccountId())){
            return vm_path + "backstageLogin.vm";
        }


		String key = (String) session.getAttribute(session.getId());
		AESEncrypt aesEncrypt = new AESEncrypt();
		aesEncrypt.setIvParameter(key);
		aesEncrypt.setsKey(key);
		try {
			logPassword = aesEncrypt.decrypt(logPassword);
		} catch (Exception e) {
			logger.error("密码解密异常---登录用户：" + userName, e);
		}
		UserLoginOrder order = new UserLoginOrder();
		order.setUserName(userName);
		order.setPassword(logPassword);
		order.setIpAddress(IPUtil.getIpAddr(request));
		LoginResult loginResult = loginService.login(order);
		session.setAttribute("sessionInvalidCheck", "31");// 未知项遗留项
		if (loginResult.isSuccess()) {
			return "redirect:/backstage/backstageIdex";
		} else if (loginResult.getYrdResultEnum() == YrdResultEnum.HAVE_NOT_DATA) {
			model.addAttribute("message", "用户不存在");
			return vm_path + "backstageLogin.vm";
		} else if (loginResult.getYrdResultEnum() == YrdResultEnum.USER_DISABLE) {
			model.addAttribute("message", LOGIN_MESSAGE_DISABLE);
			return vm_path + "backstageLogin.vm";
		} else {
			model.addAttribute("message", loginResult.getMessage());
			return vm_path + "backstageLogin.vm";
		}
	}
	
	@SuppressWarnings("unused")
	@RequestMapping("updateGoto")
	private String updateGoto(Model model) {
		String userName = SessionLocalManager.getSessionLocal().getUserName();
		Long userId = SessionLocalManager.getSessionLocal().getUserId();
		model.addAttribute("userName", userName);
		model.addAttribute("userId", userId);
		return vm_path + "backstageLoginUpdate.vm";
	}
	
	@SuppressWarnings("unused")
	@ResponseBody
	@RequestMapping("updateUserPassword")
	private Object updateUserPassword(String newPassword, String oldPassword) {
		JSONObject json = new JSONObject();
		
		try {
			Long userId = SessionLocalManager.getSessionLocal().getUserId();
			UserInfo userInfo = userQueryService.queryByUserId(userId).getQueryUserInfo();
			YrdBaseResult baseResult = userBaseInfoManager.updateLogPassword(
				userInfo.getUserBaseId(), oldPassword, newPassword);
			if (baseResult.isSuccess()) {
				json.put("message", "修改密码成功");
			} else {
				json.put("message", baseResult.getMessage());
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			logger.error("更新密码异常");
			json.put("message", "修改密码失败");
		}
		
		return json;
	}
}
