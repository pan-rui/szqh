package com.yjf.yrd.password;

import java.util.UUID;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.yjf.common.lang.util.StringUtil;
import com.yjf.yrd.base.BaseAutowiredController;
import com.yjf.yrd.base.Image;
import com.yjf.yrd.common.result.SmsCodeResult;
import com.yjf.yrd.session.SessionLocal;
import com.yjf.yrd.session.SessionLocalManager;
import com.yjf.yrd.user.info.UserInfo;
import com.yjf.yrd.user.order.ValidForgetPasswordUrlOrder;
import com.yjf.yrd.util.CommonUtil;
import com.yjf.yrd.util.MD5Util;
import com.yjf.yrd.ws.enums.BooleanEnum;
import com.yjf.yrd.ws.enums.SmsBizType;
import com.yjf.yrd.ws.enums.UserRegisterFromEnum;
import com.yjf.yrd.ws.result.YrdBaseResult;

/**
 * 
 * 
 * @Filename ForgetPassword.java
 * 
 * @Description
 * 
 * @Version 1.0
 * 
 * @Author zjl
 * 
 * @Email zjialin@yiji.com
 * 
 * @History <li>Author: zjl</li> <li>Date: 2013-7-18</li> <li>Version: 1.0</li>
 *          <li>Content: create</li>
 * 
 */
@Controller
@RequestMapping("PasswordManage")
public class ForgetPasswordController extends BaseAutowiredController {

	private final String VM_PATH = "/front/password/";
	private final String md5AddString = "S1as#%DF#@D*(=-@@!";

	@RequestMapping("newLogPasswordMail")
	public String newLogPasswordMail(HttpSession session) throws Exception {
		return VM_PATH + "newLogPasswordMail.vm";
	}

	// 找回密码
	@RequestMapping("forgetLogPassword")
	public String forgetLogPassword(HttpSession session, Model model)
			throws Exception {
		String randomToken = UUID.randomUUID().toString();
		String md5 = MD5Util.getMD5_32(randomToken + md5AddString);
		session.setAttribute("randomMD5", md5);
		model.addAttribute("randomMD5", md5);
		return VM_PATH + "forgetLogPassword.vm";
	}

	@RequestMapping("userNameSubmit")
	public String userNameSubmit(HttpSession session, String userName,
			String randomMD5, String imgCode,
			Model model) throws Exception {

		if (!Image.checkImgCode(session, imgCode)) {
			model.addAttribute("erroMes", "验证码错误");
			return VM_PATH + "forgetLogPassword.vm";
		}
		UserInfo userBaseInfo = userQueryService.queryByUserName(userName)
				.getQueryUserInfo();
		if (userBaseInfo != null) {
			model.addAttribute("isExist", true);
			model.addAttribute("canGetPassword", true);
			if (StringUtil.isNotEmpty(userBaseInfo.getMobile())
					&& userBaseInfo.getMobileBinding() == BooleanEnum.IS) {
				SessionLocal sessionLocal = new SessionLocal();
				sessionLocal.addAttibute("appRegist", true);
				SessionLocalManager.setSessionLocal(sessionLocal);
				session.setAttribute("mobile", userBaseInfo.getMobile());
				session.setAttribute("userBaseId", userBaseInfo.getUserBaseId());
				model.addAttribute("strMobile",
						CommonUtil.viewMoblie(userBaseInfo.getMobile()));
				String randomToken = UUID.randomUUID().toString();
				session.setAttribute("randomToken", randomToken);
				String md5UserBaseId = MD5Util.getMD5_32(userBaseInfo
						.getUserBaseId() + md5AddString + randomToken);
				model.addAttribute("md5UserBaseId", md5UserBaseId);
				model.addAttribute("randomMD5", randomMD5);
				model.addAttribute("userBaseId", userBaseInfo.getUserBaseId());
				return VM_PATH + "toForgetLogPassword.vm";
			}
			if (StringUtil.isNotEmpty(userBaseInfo.getMail())
					&& userBaseInfo.getMailBinding() == BooleanEnum.IS) {
				session.setAttribute("newLogPasswordChoice", userBaseInfo);
				return newLogPasswordMailOk(userName, userBaseInfo.getMail(),
						null, session, model, "mailFind");
			}
			
			model.addAttribute("canGetPassword", false);

		} else {
			model.addAttribute("isExist", false);
		}

		return VM_PATH + "toForgetLogPassword.vm";
	}

	/**
	 * 校验验证码
	 * 
	 * */
	@RequestMapping("checSmsCode")
	public Object checSmsCode(String mobile, String business, String code,
			String randomMD5,
			String md5UserBaseId,String userBaseId, Model model, HttpSession session) {
		try {
			if (StringUtil.isEmpty(business)) {
				business = "ForgetLoginPassWord";
			}
			String mobiles = (String) session.getAttribute("mobile");
			SmsCodeResult smsCodeResult = this.smsManagerService.verifySmsCode(
					mobiles, SmsBizType.getByCode(business), code, false);
			String userbaseId = (String) session.getAttribute("userBaseId");
			if (smsCodeResult.isSuccess()
					&& CheckMd5UserBD.checkMd5UserBD(session, md5UserBaseId,
							userbaseId)) {
				model.addAttribute("checSmsCode", true);
				model.addAttribute("md5UserBaseId", md5UserBaseId);
				model.addAttribute("randomMD5", randomMD5);
				model.addAttribute("userBaseId", userBaseId);
				session.setAttribute("checSmsCode", true);
				return VM_PATH + "newPssaword.vm";
			}
			model.addAttribute("isExist", true);
			model.addAttribute("checSmsCodeFail", true);
			model.addAttribute("strMobile", CommonUtil.viewMoblie(mobiles));
			model.addAttribute("message", smsCodeResult.getMessage());
			model.addAttribute("md5UserBaseId", md5UserBaseId);
			model.addAttribute("userBaseId", userBaseId);
		} catch (NullPointerException e) {
			model.addAttribute("message", "请先获取验证码");
		}
		return VM_PATH + "toForgetLogPassword.vm";
	}

	/**
	 * 更新密码
	 * 
	 * @throws Exception
	 * 
	 * */
	@RequestMapping("updatePassword")
	public Object updatePassword(String newPassword, String newPasswordTo,String randomMD5,
			String md5UserBaseId,String userBaseId, Model model, HttpSession session)
			throws Exception {
		String userbaseId = (String) session.getAttribute("userBaseId");
		boolean checSmsCode = (boolean) session.getAttribute("checSmsCode");
		YrdBaseResult baseResult = new YrdBaseResult();
		String radomMd5old=(String)session.getAttribute("randomMD5");
		if (!(StringUtil.isNotEmpty(radomMd5old) && radomMd5old
				.equals(randomMD5))) {
			return VM_PATH + "chagePasswordFail.vm";
		}
		if (checSmsCode
				&& CheckMd5UserBD.checkMd5UserBD(session, md5UserBaseId,
						userbaseId)) {
			baseResult = userBaseInfoManager.forgetPassword(userbaseId,
					newPassword);
			if (baseResult.isSuccess()) {
				return VM_PATH + "chagePasswordOK.vm";
			} else {
				return VM_PATH + "chagePasswordFail.vm";
			}

		} else {
			return null;
		}

	}

	

	/**
	 * 重新获取验证码
	 * */
	@ResponseBody
	@RequestMapping("reGetSms")
	public Object reGetSms(HttpSession session) {
		YrdBaseResult baseResult = sendSmsUnLogin((String) session
				.getAttribute("mobile"));
		JSONObject jsonobj = new JSONObject();
		if (baseResult.isSuccess()) {
			jsonobj.put("code", 1);
			jsonobj.put("message", baseResult.getMessage());
		} else {
			jsonobj.put("code", 0);
			jsonobj.put("message", baseResult.getMessage());
		}
		return jsonobj;
	}

	/**
	 * 未登陆用户获取验证码
	 * */

	private YrdBaseResult sendSmsUnLogin(String mobile) {
		SessionLocal sessionLocal = new SessionLocal();
		sessionLocal.addAttibute("appRegist", true);
		SessionLocalManager.setSessionLocal(sessionLocal);
		YrdBaseResult baseResult = smsManagerService.sendSmsCode(mobile,
				SmsBizType.getByCode("ForgetLoginPassWord"), null);
		return baseResult;
	}

	@RequestMapping("newLogPasswordMailOk")
	public String newLogPasswordMailOk(String userName, String mail,
			String code, HttpSession session, Model model, String findType)
			throws Exception {
		if (StringUtil.equals("mailFind", findType)) {// 找回方式为邮箱找回
			UserInfo userBaseInfo = (UserInfo) session
					.getAttribute("newLogPasswordChoice");
			if (userBaseInfo != null
					&& UserRegisterFromEnum.PLAT != userBaseInfo
							.getUserRegisterFrom()) {
				model.addAttribute("error", "易极付账户请到易极付或重庆银行电子银行界面找回密码");
				return VM_PATH + "newLogPassword.vm";
			}
			mail = userBaseInfo.getMail();
			userBaseInfoManager.sendForgetPassword(userBaseInfo.getUserName());
			String mainUrl = "http://mail."
					+ mail.substring(mail.lastIndexOf('@') + 1,
							mail.lastIndexOf(".")) + ".com";

			String[] strMail = mail.split("@");
			model.addAttribute("userName", userName);
			model.addAttribute("mainUrl", mainUrl);
			model.addAttribute("mail", mail);
			model.addAttribute("mailStr", strMail[0].substring(0, 3)
					+ "********@" + strMail[1]);
			return VM_PATH + "newLogPasswordMailOk.vm";
		} else {// 找回方式为手机校验码找回
			UserInfo userBaseInfo = (UserInfo) session
					.getAttribute("newLogPasswordChoice");
			if (userBaseInfo.getMailBinding() == BooleanEnum.IS
					&& userBaseInfo.getMobileBinding() != BooleanEnum.IS) {
				return "common/error.htm";
			} else {
				if (StringUtil.isNotBlank(userBaseInfo.getMobile())) {
					String mobile = userBaseInfo.getMobile();
					if (mobile != null && !"".equals(mobile)) {
						model.addAttribute(
								"mobile",
								mobile.substring(0, 3)
										+ "*****"
										+ mobile.subSequence(
												mobile.length() - 3,
												mobile.length()));
					} else {
						model.addAttribute("mobile", mobile);
					}
					session.setAttribute("validForgetPasswordMessage", "true");
					model.addAttribute("userName",
							userBaseInfo.getAccountName());
					model.addAttribute("findType", findType);
					return VM_PATH + "newLogPasswordMailOk.vm";
				}
			}
			return "common/error.htm";
		}
	}

	@RequestMapping("newLogPasswordChoice")
	public String newLogPasswordChoice(String userName, HttpSession session,
			String captcha, Model model) throws Exception {
		if (Image.checkImgCode(session, captcha)) {
			UserInfo userBaseInfo = userQueryService.queryByUserName(userName)
					.getQueryUserInfo();
			if (userBaseInfo != null) {
				model.addAttribute("userInfo", userBaseInfo);
				session.setAttribute("newLogPasswordChoice", userBaseInfo);
				if (!StringUtil.equals("", userBaseInfo.getMail())) {
					String[] strMail = userBaseInfo.getMail().split("@");
					model.addAttribute("mailStr", strMail[0].substring(0, 3)
							+ "********@" + strMail[1]);
				}
				if (!StringUtil.equals("", userBaseInfo.getMobile())) {
					String mobile = userBaseInfo.getMobile();
					model.addAttribute(
							"mobile",
							mobile.substring(0, 3)
									+ "*****"
									+ mobile.subSequence(mobile.length() - 3,
											mobile.length()));
				}

				return VM_PATH + "newLogPasswordChoose.vm";

			} else {
				return VM_PATH + "newPasswordNo.vm";
			}
		} else {
			return VM_PATH + "newPasswordNo.vm";
		}
	}

	@RequestMapping("ForgetLogPassword/{md5UserBaseId},{sendTime},{userBaseId}")
	public String ForgetLogPassword(HttpSession session,
			@PathVariable String md5UserBaseId, @PathVariable String sendTime,
			@PathVariable String userBaseId, Model model) throws Exception {

		if (sendTime == null || "".equals(sendTime)) {
			model.addAttribute("reason", "邮件链接异常,请重发!");
			return VM_PATH + "sendMailNo.vm";
		}
		UserInfo userInfo = userQueryService.queryByUserBaseId(userBaseId)
				.getQueryUserInfo();
		ValidForgetPasswordUrlOrder urlOrder = new ValidForgetPasswordUrlOrder();
		urlOrder.setMd5Url(md5UserBaseId);
		urlOrder.setSendTime(sendTime);
		urlOrder.setUserBaseId(userBaseId);
		YrdBaseResult baseResult = userBaseInfoManager
				.validForgetPasswordUrl(urlOrder);
		if (baseResult.isSuccess()) {
			String randomToken = UUID.randomUUID().toString();
			session.setAttribute("randomToken", randomToken);
			md5UserBaseId = MD5Util.getMD5_32(userBaseId + md5AddString
					+ randomToken);

			String randomMD5 = "00";
			session.setAttribute("userBaseId", userBaseId);
			session.setAttribute("randomMD5", "00");
			model.addAttribute("checSmsCode", true);
			model.addAttribute("md5UserBaseId", md5UserBaseId);
			model.addAttribute("randomMD5", randomMD5);
			session.setAttribute("checSmsCode", true);
			return VM_PATH + "newPssaword.vm";

		} else {
			model.addAttribute("reason", baseResult.getMessage());
			return VM_PATH + "sendMailNo.vm";
		}

	}

	@RequestMapping(value = "NewLogPassword", method = RequestMethod.POST)
	public String NewLogPassword(HttpSession session, String md5UserBaseId,
			String logPassword, String newLogPassword, String type,
			String token, Model model) throws Exception {
		if (!StringUtil.isEmpty(type)) {
			String getToken = (String) session.getAttribute("token");
			if (getToken == null || !getToken.equalsIgnoreCase(token)) {
				return VM_PATH + "newPasswordNo.vm";// 重复提交
			}
			UserInfo userBaseInfo = (UserInfo) session.getAttribute("userInfo");
			String validForgetPasswordUrl = (String) session
					.getAttribute("validForgetPasswordUrl");
			if (StringUtil.equals(validForgetPasswordUrl, "true")) {
				YrdBaseResult baseResult = userBaseInfoManager.forgetPassword(
						userBaseInfo.getUserBaseId(), logPassword);
				if (baseResult.isSuccess()) {
					session.removeAttribute("userInfo");
					session.removeAttribute("validForgetPasswordUrl");
					return VM_PATH + "newPasswordOk.vm";
				}
			}
			return VM_PATH + "newPasswordNo.vm";// 修改登录密码出错啦
		} else {// 手机方式
			String getToken = (String) session.getAttribute("token");
			if (getToken == null || !getToken.equalsIgnoreCase(token)) {
				return VM_PATH + "newPasswordNo.vm";// 重复提交
			}
			UserInfo userBaseInfo = (UserInfo) session
					.getAttribute("newLogPasswordChoice");
			String validForgetPasswordUrl = (String) session
					.getAttribute("validForgetPasswordMessage");
			if (StringUtil.equals(validForgetPasswordUrl, "true")) {
				YrdBaseResult baseResult = userBaseInfoManager.forgetPassword(
						userBaseInfo.getUserBaseId(), logPassword);
				if (baseResult.isSuccess()) {
					session.removeAttribute("userInfo");
					session.removeAttribute("validForgetPasswordMessage");
					return VM_PATH + "newPasswordOk.vm";
				}
			}
			return VM_PATH + "newPasswordNo.vm";// 修改登录密码出错啦
		}
	}

}