package com.yjf.yrd.password;

import javax.servlet.http.HttpSession;

import com.yjf.yrd.common.services.order.SystemSendMessageOrder;
import com.yjf.yrd.ws.enums.SysSendMessageTypeEnum;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.yjf.common.lang.util.StringUtil;
import com.yjf.yrd.base.BaseAutowiredController;
import com.yjf.yrd.common.result.SmsCodeResult;
import com.yjf.yrd.integration.openapi.order.MobileBindOrder;
import com.yjf.yrd.integration.openapi.order.RegisterOrder;
import com.yjf.yrd.integration.openapi.result.CustomerResult;
import com.yjf.yrd.session.SessionLocalManager;
import com.yjf.yrd.user.info.UserInfo;
import com.yjf.yrd.util.RSAUtils;
import com.yjf.yrd.ws.enums.BooleanEnum;
import com.yjf.yrd.ws.enums.SmsBizType;
import com.yjf.yrd.ws.result.YrdBaseResult;
import com.yjf.yrd.ws.service.YrdResultEnum;

@Controller
@RequestMapping("security")
public class SecurityCenterController extends BaseAutowiredController {
	
	private final String VM_PATH = "/front/security/";
	
	@ResponseBody
	@RequestMapping("updatePassword")
	public Object updatePassword(String password, String newPassword, String newPasswordTo,
									String type, String userBaseId, String md5UserBaseId,
									HttpSession session) throws Exception {
		JSONObject jsonobj = new JSONObject();
		if (CheckMd5UserBD.checkMd5UserBD(session, md5UserBaseId, userBaseId)) {
			YrdBaseResult returnEnum = new YrdBaseResult();
			password = RSAUtils.decryptStringByJs(password);
			newPassword = RSAUtils.decryptStringByJs(newPassword);
			newPasswordTo = RSAUtils.decryptStringByJs(newPasswordTo);
			returnEnum = userBaseInfoManager.updateLogPassword(SessionLocalManager
				.getSessionLocal().getUserBaseId(), password, newPassword);
			if (returnEnum.isSuccess()) {
				SessionLocalManager.destroy();
				jsonobj.put("code", 1);
				jsonobj.put("message", "设置新密码成功");
			} else if (returnEnum.getYrdResultEnum() == YrdResultEnum.PASSWORD_ERROR) {
				jsonobj.put("code", 0);
				jsonobj.put("message", "旧密码输入不正确");
			} else {
				jsonobj.put("code", 0);
				jsonobj.put("message", returnEnum.getMessage());
			}
		} else {
			jsonobj.put("code", 0);
			jsonobj.put("message", "修改密码失败!");
		}
		return jsonobj;
	}
	
	@ResponseBody
	@RequestMapping("boundMobile")
	public Object boundMobile(String mobile, String code) throws Exception {
		YrdBaseResult returnEnum = new YrdBaseResult();
		JSONObject jsonobj = new JSONObject();
		UserInfo userBaseInfo = userQueryService.queryByUserBaseId(
			SessionLocalManager.getSessionLocal().getUserBaseId()).getQueryUserInfo();
		userBaseInfo.setMobile(mobile);
		MobileBindOrder mobileBindOrder = new MobileBindOrder();
		mobileBindOrder.setUserId(userBaseInfo.getAccountId());
		mobileBindOrder.setMobile(mobile);
		mobileBindOrder.setIsNew(BooleanEnum.YES);
		YrdBaseResult baseResult = this.customerService.updateMobileBinding(mobileBindOrder,
			this.getOpenApiContext());
		
		if (baseResult.isSuccess()) {
			returnEnum = userBaseInfoManager.mobileBinding(userBaseInfo.getUserBaseId(), mobile);
			if (returnEnum.isSuccess()) {
				jsonobj.put("code", 1);
				jsonobj.put("message", "绑定手机成功");
			} else {
				jsonobj.put("code", 0);
				jsonobj.put("message", "绑定手机失败");
			}
		} else {
			jsonobj.put("code", 0);
			jsonobj.put("message", "绑定手机失败");
		}
		
		return jsonobj;
	}
	
	@ResponseBody
	@RequestMapping("updateBoundMobile")
	public Object updateBoundMobile(String confirmUserBaseId, String code, HttpSession session,
									String userBaseId, String md5UserBaseId) throws Exception {
		JSONObject jsonobj = new JSONObject();
		if (CheckMd5UserBD.checkMd5UserBD(session, md5UserBaseId, userBaseId)) {
			session.removeAttribute("updateBoundMobileValid");
			if (!StringUtil.equalsIgnoreCase(SessionLocalManager.getSessionLocal().getUserBaseId(),
				confirmUserBaseId)) {
				jsonobj.put("code", 0);
				jsonobj.put("message", "亲！页面已经过期，请刷新页面");
				return jsonobj;
			}
			
			UserInfo userBaseInfo = userQueryService.queryByUserBaseId(
				SessionLocalManager.getSessionLocal().getUserBaseId()).getQueryUserInfo();
			SmsCodeResult smsCodeResult = smsManagerService.verifySmsCode(userBaseInfo.getMobile(),
				SmsBizType.CELLPHONE, code, true);
			
			if (!smsCodeResult.isSuccess()) {
				jsonobj.put("code", 0);
				jsonobj.put("message", "验证码校验失败");
			} else {
				session.setAttribute("updateBoundMobileValid", "true");
				jsonobj.put("code", 1);
				jsonobj.put("message", "验证码校验成功");
			}
		} else {
			jsonobj.put("code", 0);
			jsonobj.put("message", "验证码校验失败");
		}
		return jsonobj;
	}
	
	@ResponseBody
	@RequestMapping("updateBoundMobileSecond")
	public Object updateBoundMobileSecond(String confirmUserBaseId, String newMobile,
											String codeSecond, HttpSession session)
																					throws Exception {
		YrdBaseResult returnEnum = new YrdBaseResult();
		JSONObject jsonobj = new JSONObject();
		if (!StringUtil.equalsIgnoreCase(SessionLocalManager.getSessionLocal().getUserBaseId(),
			confirmUserBaseId)) {
			jsonobj.put("code", 0);
			jsonobj.put("message", "亲！页面已经过期，请刷新页面");
			return jsonobj;
		}
		if (!"true".equals(session.getAttribute("updateBoundMobileValid"))) {
			jsonobj.put("code", 0);
			jsonobj.put("message", "亲！页面已经过期，请刷新页面");
			return jsonobj;
		}
		session.removeAttribute("updateBoundMobileValid");
		UserInfo userBaseInfo = userQueryService.queryByUserBaseId(
			SessionLocalManager.getSessionLocal().getUserBaseId()).getQueryUserInfo();
		SmsCodeResult smsCodeResult = smsManagerService.verifySmsCode(newMobile,
			SmsBizType.NEWCELLPHONE, codeSecond, true);
		if (!smsCodeResult.isSuccess()) {
			jsonobj.put("code", 0);
			jsonobj.put("message", "验证码校验失败");
			return jsonobj;
		}
		
		userBaseInfo.setMobile(newMobile);
		MobileBindOrder mobileBindOrder = new MobileBindOrder();
		mobileBindOrder.setUserId(userBaseInfo.getAccountId());
		mobileBindOrder.setMobile(newMobile);
		mobileBindOrder.setIsNew(BooleanEnum.NO);
		YrdBaseResult baseResult = this.customerService.updateMobileBinding(mobileBindOrder,
			this.getOpenApiContext());
		if (baseResult.isSuccess()) {
			returnEnum = userBaseInfoManager.mobileBinding(userBaseInfo.getUserBaseId(), newMobile);
			if (returnEnum.isSuccess()) {
				jsonobj.put("code", 1);
				jsonobj.put("message", "修改绑定手机成功");

                /** 发送站内信 **/
                SystemSendMessageOrder systemSendMessageOrder = new SystemSendMessageOrder();
                systemSendMessageOrder.setType(SysSendMessageTypeEnum.PHONE_CHANGE);
                systemSendMessageOrder.setUserId(userBaseInfo.getUserId());
                systemMessageService.systemSendMessage(systemSendMessageOrder);
			} else {
				jsonobj.put("code", 0);
				jsonobj.put("message", "修改绑定手机失败");
			}
		} else {
			jsonobj.put("code", 0);
			jsonobj.put("message", "修改绑定手机失败");
		}
		
		return jsonobj;
	}
	
	@ResponseBody
	@RequestMapping("boundUpdateMail")
	public Object boundUpdateMail(String newMail, String mailCode) throws Exception {
		JSONObject jsonobj = new JSONObject();
		YrdBaseResult returnEnum = new YrdBaseResult();
		UserInfo userBaseInfo = userQueryService.queryByUserBaseId(
			SessionLocalManager.getSessionLocal().getUserBaseId()).getQueryUserInfo();
		
		SmsCodeResult smsCodeResult = smsManagerService.verifySmsCode(userBaseInfo.getMobile(),
			SmsBizType.PERSONAL, mailCode, true);
		if (!smsCodeResult.isSuccess()) {
			jsonobj.put("code", 0);
			jsonobj.put("message", "验证码校验失败");
			return jsonobj;
		}
		
		RegisterOrder order = new RegisterOrder();
		order.setUserId(userBaseInfo.getAccountId());
		order.setEmail(newMail);
		CustomerResult customerResult = customerService.openCapitalAccount(order,
			this.getOpenApiContext());
		if (!customerResult.isSuccess()) {
			jsonobj.put("code", 0);
			jsonobj.put("message", "修改绑定邮箱失败");
		}
		returnEnum = userBaseInfoManager.mailBinding(userBaseInfo.getUserBaseId(), newMail);
		if (returnEnum.isSuccess()) {
			jsonobj.put("code", 1);
			jsonobj.put("message", "修改绑定邮箱成功");

            /** 发送站内信 **/
            SystemSendMessageOrder systemSendMessageOrder = new SystemSendMessageOrder();
            systemSendMessageOrder.setType(SysSendMessageTypeEnum.EMAIL_CHANGE);
            systemSendMessageOrder.setUserId(userBaseInfo.getUserId());
            systemMessageService.systemSendMessage(systemSendMessageOrder);
		} else {
			jsonobj.put("code", 0);
			jsonobj.put("message", "修改绑定邮箱失败");
		}
		return jsonobj;
	}
	
	@RequestMapping("boundPhone")
	public String boundPhone(HttpSession session, Model model) throws Exception {
		session.setAttribute("current", 4);
		return VM_PATH + "boundPhone.vm";
	}
	
}
