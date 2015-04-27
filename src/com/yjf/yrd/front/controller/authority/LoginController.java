package com.yjf.yrd.front.controller.authority;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.PublicKey;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.yjf.common.lang.ip.IPUtil;
import com.yjf.common.lang.util.StringUtil;
import com.yjf.common.log.Logger;
import com.yjf.common.log.LoggerFactory;
import com.yjf.yrd.authority.AuthorityService;
import com.yjf.yrd.base.BaseAutowiredController;
import com.yjf.yrd.base.Image;
import com.yjf.yrd.integration.web.server.YjfLoginWebServer;
import com.yjf.yrd.login.LoginService;
import com.yjf.yrd.login.order.UserLoginOrder;
import com.yjf.yrd.login.result.LoginResult;
import com.yjf.yrd.session.SessionLocalManager;
import com.yjf.yrd.user.UserBaseInfoManager;
import com.yjf.yrd.util.AESEncrypt;
import com.yjf.yrd.util.AppConstantsUtil;
import com.yjf.yrd.util.PublicKeyMap;
import com.yjf.yrd.util.RSAUtils;
import com.yjf.yrd.web.util.WebConstant;
import com.yjf.yrd.ws.enums.UserStateEnum;
import com.yjf.yrd.ws.service.YrdResultEnum;

@Controller
@RequestMapping("login")
public class LoginController extends BaseAutowiredController {
	protected static final Logger logger = LoggerFactory.getLogger(LoginController.class);
	/**
	 * 登录提示信息
	 */
	private static String LOGIN_MESSAGE = null;
	/**
	 * 验证码错误提示信息
	 */
	private static String LOGIN_CAPTCHA_MESSAGE = null;
	/**
	 * 用户不可用
	 */
	private static String LOGIN_MESSAGE_DISABLE = null;
	/**
	 * 未激活
	 */
	private static String LOGIN_MESSAGE_UNACTIVE = null;
	/**
	 * 密码解密异常
	 */
	private static String LOGIN_MESSAGE_DECRIPTERR = null;
	/**
	 * 账户已被锁定
	 */
	private static String LOGIN_MESSAGE_LOCKED = null;
	/**
	 * 账户不存在
	 */
	private static String LOGIN_USER_UNKNOWN = null;
	/**
	 * 密码错误提示
	 */
	private static String LOGIN_WONGMSG_TIP = null;
	/**
	 * 验证码
	 */
	private static String KAPTCHA_SESSION_KEY = "kaptcha_session_key";
	
	@Autowired
	YjfLoginWebServer yjfLoginWebServer;
	static {
		try {
			LOGIN_MESSAGE = URLEncoder.encode("用户名或者密码连续错误", "UTF-8");
			LOGIN_CAPTCHA_MESSAGE = URLEncoder.encode("验证码错误...", "UTF-8");
			LOGIN_MESSAGE_DISABLE = URLEncoder.encode("该用户不可用...", "UTF-8");
			LOGIN_MESSAGE_UNACTIVE = URLEncoder.encode("该用户未激活...", "UTF-8");
			LOGIN_MESSAGE_DECRIPTERR = URLEncoder.encode("密码解密异常...", "UTF-8");
			LOGIN_MESSAGE_LOCKED = URLEncoder.encode("此账户已被锁定，请等待解锁...", "UTF-8");
			LOGIN_USER_UNKNOWN = URLEncoder.encode("账户不存在...", "UTF-8");
			LOGIN_WONGMSG_TIP = URLEncoder.encode("同一用户名密码连续输入错误5次后账户会被锁定，次日将会解锁，也可通过找回密码解锁。",
				"UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage(), e);
			e.printStackTrace();
		}
	}
	
	@Autowired
	private final LoginService loginService = null;
	
	@Autowired
	private final AuthorityService authorityService = null;
	@Autowired
	UserBaseInfoManager userBaseInfoManager;
	
	/**
	 * 跳转到登录页面
	 * 
	 * @return
	 */
	@RequestMapping("login")
	public String login(HttpServletRequest request,Model model) {
		// CommonUtil.removeCookie(request);
		model.addAttribute("yjfUrl", WebConstant.getYjfloginurl());
		return "test/login.vm";
	}
	
	/**
	 * 跳转到商户登录页面
	 * 
	 * @return
	 */
	@RequestMapping("merchantLogin")
	public String merchantLogin(HttpServletRequest request,Model model,String type) {
		return "test/merchantLogin.vm";
	}
	
	/**
	 * 用户登录
	 * 
	 * @param userName
	 * @param password
	 * @param redirect
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping("dologin")
	public String doLogin(String userName, String password, String redirect, String captcha,
							HttpSession session, HttpServletRequest request, Model model,
							String whithControl, String isRememberPassword)
																			throws UnsupportedEncodingException {
		redirect = checkRedirect(redirect);
		String withOutcheck = AppConstantsUtil.getLoginWithOutCheckCode();
		boolean needcheck = false;
		try {
			
			needcheck = (boolean) session.getAttribute("needcheck");
		} catch (NullPointerException e) {
			logger.error("session中无此参数:作用是判断登陆密码错误3次后是否需要输入验证码");
		}
		if (!(StringUtil.isNotEmpty(withOutcheck) && withOutcheck.indexOf("Y") > -1) || needcheck) {
			if (!Image.checkImgCode(session, captcha)) {
				return "redirect:login?message=" + LOGIN_CAPTCHA_MESSAGE + "&redirect=" + redirect;
			}
		}
		// int i = 4 / 0;
		session.removeAttribute(KAPTCHA_SESSION_KEY);
		AESEncrypt aesEncrypt = new AESEncrypt();
		if (!"1".equals(whithControl)) {
			String key = (String) session.getAttribute(session.getId());
			aesEncrypt.setIvParameter(key);
			aesEncrypt.setsKey(key);
		}
		try {
			if (!"1".equals(whithControl)) {
				password = aesEncrypt.decrypt(password);
			} else {
				password = RSAUtils.decryptStringByJs(password);
			}
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return "redirect:login?message=" + LOGIN_MESSAGE_DECRIPTERR + "&redirect=" + redirect;
		}
		
		UserLoginOrder userLoginOrder = new UserLoginOrder();
		userLoginOrder.setUserName(userName);
		userLoginOrder.setPassword(password);
		userLoginOrder.setIpAddress(IPUtil.getIpAddr(request));
		LoginResult loginResult = loginService.login(userLoginOrder);
		session.setAttribute("isRememberPassword", isRememberPassword);
		int allowErrorTimes = 3;
		String errorTimes = AppConstantsUtil.getAllowErrorTimes();
		if (StringUtil.isNotEmpty(errorTimes)) {
			allowErrorTimes = Integer.parseInt(errorTimes);
		}
		if ("YY".equals(withOutcheck) && loginResult.getPwdErrorCount() >= allowErrorTimes) {
			session.setAttribute("needcheck", true);
			model.addAttribute("needcheck", true);
		}
		if (loginResult.isSuccess()
			&& loginResult.getYrdResultEnum() == YrdResultEnum.EXECUTE_SUCCESS) {
			if (loginResult.getUserInfo() != null) {
				if (loginResult.getUserInfo().getState() == UserStateEnum.INACTIVE) {
					return "redirect:/anon/sendEmailPage/"
							+ loginResult.getUserInfo().getUserBaseId();
				} else {
					if (redirect == null || redirect.length() < 1) {
						redirect = "../";
					}
					return "redirect:" + redirect;
				}
			} else {
				if (StringUtil.equals("yes", isRememberPassword)) {
					return "redirect:login?message="
							+ URLEncoder.encode(loginResult.getMessage(), "UTF-8") + "&redirect="
							+ redirect + "&userName=" + userName + "&isRememberPassword="
							+ isRememberPassword;
				} else {
					return "redirect:login?message="
							+ URLEncoder.encode(loginResult.getMessage(), "UTF-8") + "&redirect="
							+ redirect;
				}
			}
		} else if (loginResult.getYrdResultEnum() == YrdResultEnum.PASSWORD_ERROR) {
			if (StringUtil.equals("yes", isRememberPassword)) {
				return "redirect:login?message="
						+ URLEncoder.encode(loginResult.getMessage(), "UTF-8") + "&redirect="
						+ redirect + "&userName=" + userName + "&isRememberPassword="
						+ isRememberPassword;
			} else {
				return "redirect:login?message="
						+ URLEncoder.encode(loginResult.getMessage(), "UTF-8") + "&redirect="
						+ redirect;
			}
		} else {
			if (StringUtil.equals("yes", isRememberPassword)) {
				return "redirect:login?message="
						+ URLEncoder.encode(loginResult.getMessage(), "UTF-8") + "&redirect="
						+ redirect + "&userName=" + userName + "&isRememberPassword="
						+ isRememberPassword;
			} else {
				return "redirect:login?message="
						+ URLEncoder.encode(loginResult.getMessage(), "UTF-8") + "&redirect="
						+ redirect;
			}
		}
	}
	
	
	/**
	 * 商户,担保机构登录
	 * 
	 * @param userName
	 * @param password
	 * @param redirect
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping("merchantDoLogin")
	public String merchantDoLogin(String userName, String password, String redirect, String captcha,
							HttpSession session, HttpServletRequest request, Model model,
							String whithControl, String isRememberPassword)
																			throws UnsupportedEncodingException {
		redirect = checkRedirect(redirect);
		String withOutcheck = AppConstantsUtil.getLoginWithOutCheckCode();
		boolean needcheck = false;
		try {
			
			needcheck = (boolean) session.getAttribute("needcheck");
		} catch (NullPointerException e) {
			logger.error("session中无此参数:作用是判断登陆密码错误3次后是否需要输入验证码");
		}
		if (!(StringUtil.isNotEmpty(withOutcheck) && withOutcheck.indexOf("Y") > -1) || needcheck) {
			if (!Image.checkImgCode(session, captcha)) {
				return "redirect:merchantLogin?message=" + LOGIN_CAPTCHA_MESSAGE + "&redirect=" + redirect;
			}
		}
		// int i = 4 / 0;
		session.removeAttribute(KAPTCHA_SESSION_KEY);
		AESEncrypt aesEncrypt = new AESEncrypt();
		if (!"1".equals(whithControl)) {
			String key = (String) session.getAttribute(session.getId());
			aesEncrypt.setIvParameter(key);
			aesEncrypt.setsKey(key);
		}
		try {
			if (!"1".equals(whithControl)) {
				password = aesEncrypt.decrypt(password);
			} else {
				password = RSAUtils.decryptStringByJs(password);
			}
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return "redirect:merchantLogin?message=" + LOGIN_MESSAGE_DECRIPTERR + "&redirect=" + redirect;
		}
		
		UserLoginOrder userLoginOrder = new UserLoginOrder();
		userLoginOrder.setUserName(userName);
		userLoginOrder.setPassword(password);
		userLoginOrder.setIpAddress(IPUtil.getIpAddr(request));
		LoginResult loginResult = loginService.login(userLoginOrder);
		session.setAttribute("isRememberPassword", isRememberPassword);
		int allowErrorTimes = 3;
		String errorTimes = AppConstantsUtil.getAllowErrorTimes();
		if (StringUtil.isNotEmpty(errorTimes)) {
			allowErrorTimes = Integer.parseInt(errorTimes);
		}
		if ("YY".equals(withOutcheck) && loginResult.getPwdErrorCount() >= allowErrorTimes) {
			session.setAttribute("needcheck", true);
			model.addAttribute("needcheck", true);
		}
		if (loginResult.isSuccess()
			&& loginResult.getYrdResultEnum() == YrdResultEnum.EXECUTE_SUCCESS) {
			if (loginResult.getUserInfo() != null) {
				if (loginResult.getUserInfo().getState() == UserStateEnum.INACTIVE) {
					return "redirect:/anon/sendEmailPage/"
							+ loginResult.getUserInfo().getUserBaseId();
				} else {
					if (redirect == null || redirect.length() < 1) {
						redirect = "../";
					}
					return "redirect:" + redirect;
				}
			} else {
				if (StringUtil.equals("yes", isRememberPassword)) {
					return "redirect:merchantLogin?message="
							+ URLEncoder.encode(loginResult.getMessage(), "UTF-8") + "&redirect="
							+ redirect + "&userName=" + userName + "&isRememberPassword="
							+ isRememberPassword;
				} else {
					return "redirect:merchantLogin?message="
							+ URLEncoder.encode(loginResult.getMessage(), "UTF-8") + "&redirect="
							+ redirect;
				}
			}
		} else if (loginResult.getYrdResultEnum() == YrdResultEnum.PASSWORD_ERROR) {
			if (StringUtil.equals("yes", isRememberPassword)) {
				return "redirect:merchantLogin?message="
						+ URLEncoder.encode(loginResult.getMessage(), "UTF-8") + "&redirect="
						+ redirect + "&userName=" + userName + "&isRememberPassword="
						+ isRememberPassword;
			} else {
				return "redirect:merchantLogin?message="
						+ URLEncoder.encode(loginResult.getMessage(), "UTF-8") + "&redirect="
						+ redirect;
			}
		} else {
			if (StringUtil.equals("yes", isRememberPassword)) {
				return "redirect:merchantLogin?message="
						+ URLEncoder.encode(loginResult.getMessage(), "UTF-8") + "&redirect="
						+ redirect + "&userName=" + userName + "&isRememberPassword="
						+ isRememberPassword;
			} else {
				return "redirect:merchantLogin?message="
						+ URLEncoder.encode(loginResult.getMessage(), "UTF-8") + "&redirect="
						+ redirect;
			}
		}
	}
	
	/**
	 * 是否登录
	 * 
	 * @return
	 */
	@ResponseBody
	@RequestMapping("islogin")
	public Object isLogin() {
		return SessionLocalManager.getSessionLocal() != null;
	}
	
	/**
	 * 退出登录
	 * 
	 * @param redirect
	 * @return
	 */
	@RequestMapping("logout")
	public String logout(String redirect, HttpSession session) {
		session.removeAttribute("sessionInvalidCheck");
		session.invalidate();
		SessionLocalManager.destroy();
		if (redirect != null && redirect.length() > 0) {
			return "redirect:" + redirect;
		}
		return "redirect:../";
	}
	
	/**
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("encryptionPwd")
	public Object encryptionPwd(String pwd) {
		JSONObject json = new JSONObject();
		try {
			PublicKey key = RSAUtils.getKeyPair().getPublic();
			String rsaPwd = RSAUtils.encryptString(key, pwd);
			json.put("code", 1);
			json.put("password", rsaPwd);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			json.put("code", 0);
			json.put("message", "加密异常");
			e.printStackTrace();
		}
		return json;
	}
	
	public String decryptionPwd(String pwd) {
		return RSAUtils.decryptStringByJs(pwd);
	}
	
	@ResponseBody
	@RequestMapping("keyPair")
	public Object keyPair() {
		JSONObject json = new JSONObject();
		try {
			PublicKeyMap publicKeyMap = RSAUtils.getPublicKeyMap();
			json.put("code", 1);
			json.put("modulus", publicKeyMap.getModulus());
			json.put("exponent", publicKeyMap.getExponent());
			System.out.println(publicKeyMap);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			json.put("code", 0);
			json.put("message", "获取Key异常");
		}
		return json;
	}
	
	@ResponseBody
	@RequestMapping("validateCaptcha")
	public Object validateCaptcha(String captcha, HttpSession session) {
		JSONObject json = new JSONObject();
		try {
			if (Image.checkImgCode(session, captcha)) {
				json.put("code", 1);
				json.put("message", "验证码正确");
			} else {
				json.put("code", 0);
				json.put("message", "验证码不正确");
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			json.put("code", 0);
			json.put("message", "验证码校验异常");
		}
		return json;
	}
	
	private String checkRedirect(String redirect) {
		if (redirect != null) {
			if (redirect.startsWith(AppConstantsUtil.getHostHttpUrl())) {
				return redirect;
			} else if (redirect.contains("http://") || redirect.contains("https://")) {
				return "";
			} else {
				return redirect;
			}
		}
		return redirect;
	}
}
