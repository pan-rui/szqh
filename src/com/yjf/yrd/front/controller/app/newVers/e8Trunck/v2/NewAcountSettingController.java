package com.yjf.yrd.front.controller.app.newVers.e8Trunck.v2;

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
import com.yjf.yrd.session.SessionLocalManager;
import com.yjf.yrd.user.info.UserInfo;
import com.yjf.yrd.user.result.UserQueryResult;
import com.yjf.yrd.ws.enums.BooleanEnum;
import com.yjf.yrd.ws.enums.SmsBizType;
import com.yjf.yrd.ws.result.YrdBaseResult;

/**
 * 
 * 
 * @Filename appAcountSettingController.java
 * 
 * @Description
 * 
 * @Version 2.0
 * 
 * @Author zhaohaibing
 * 
 * @Email abing@yiji.com
 * 
 * @Date: 2014-12-26
 * 
 * 
 */
@Controller
@RequestMapping("app")
public class NewAcountSettingController extends BaseAutowiredController {

	/**
	 * 绑定手机
	 * 
	 * @param mobile
	 * @Param business
	 * */
	@ResponseBody
	@RequestMapping("bondingMobile.htm")
	public Object bondingMobile(String mobile, String business,
			HttpSession session) {
		JSONObject json = new JSONObject();
		if (!AppCommonUtil.isLogin()) {
			json.put("code", -1);
			json.put("message", "未登录或登陆失效");
			return json;
		}
		try {
			UserInfo userBaseInfo = userQueryService.queryByUserBaseId(
					SessionLocalManager.getSessionLocal().getUserBaseId())
					.getQueryUserInfo();

			if (StringUtil.isNotEmpty(userBaseInfo.getMobile())) {
				json.put("code", 0);
				json.put("message", "该用户已绑定手机号码");
				return json;
			}
		} catch (NullPointerException e) {
			json.put("code", 0);
			json.put("message", "验证用户是否已绑定手机失败");
			return json;

		}
		YrdBaseResult baseResult = smsManagerService.sendSmsCode(mobile,
				SmsBizType.getByCode(business), null);
		if (baseResult.isSuccess()) {
			json.put("code", 1);
			json.put("message", "获取手机验证码成功");
			session.setAttribute("mobile", mobile);
		} else {
			json.put("code", 0);
			json.put("message", baseResult.getMessage());
		}
		return json;
	}

	/**
	 * 绑定手机提交
	 * 
	 * @param code
	 * 
	 * */
	@ResponseBody
	@RequestMapping("bondingMobileSub.htm")
	public Object bondingMobile(String code, HttpSession session) {
		JSONObject json = new JSONObject();
		if (!AppCommonUtil.isLogin()) {
			json.put("code", -1);
			json.put("message", "未登录或登陆失效");
			return json;
		}
		try {
			String mobile = (String) session.getAttribute("mobile");
			SmsCodeResult smsCodeResult = smsManagerService.verifySmsCode(
					mobile, SmsBizType.CELLPHONE, code, true);
			if (!smsCodeResult.isSuccess()) {
				json.put("code", 0);
				json.put("message", "验证码错误");
				return json;
			}
			UserInfo userBaseInfo = userQueryService.queryByUserBaseId(
					SessionLocalManager.getSessionLocal().getUserBaseId())
					.getQueryUserInfo();
			userBaseInfo.setMobile(mobile);
			MobileBindOrder mobileBindOrder = new MobileBindOrder();
			mobileBindOrder.setUserId(userBaseInfo.getAccountId());
			mobileBindOrder.setMobile(mobile);
			mobileBindOrder.setIsNew(BooleanEnum.YES);
			YrdBaseResult baseResult = this.customerService
					.updateMobileBinding(mobileBindOrder,
							this.getOpenApiContext());
			if (baseResult.isSuccess()) {
				YrdBaseResult returnEnum = new YrdBaseResult();
				returnEnum = userBaseInfoManager.mobileBinding(
						userBaseInfo.getUserBaseId(), mobile);
				if (returnEnum.isSuccess()) {
					json.put("code", 1);
					json.put("message", "绑定手机成功");
				} else {
					json.put("code", 0);
					json.put("message", "绑定手机失败");
				}
			} else {
				json.put("code", 0);
				json.put("message", "绑定手机失败");

			}

		} catch (NullPointerException e) {
			json.put("code", 0);
			json.put("message", "绑定手机号码失败");
			return json;
		}
		return json;
	}

	/**
	 * 18、修改手机号码-验证原手机号 type=cellphone v
	 * 
	 * @param code
	 * @Param newPhone
	 * */
	@ResponseBody
	@RequestMapping("appChangeMobile.htm")
	public Object appChangeMobile(String code, String newMobile,
			HttpSession session) throws Exception {
		JSONObject jsonobj = new JSONObject();
		UserQueryResult baseResult = userQueryService
				.queryByUserName(SessionLocalManager.getSessionLocal()
						.getUserName());
		SmsCodeResult smsCodeResult = smsManagerService.verifySmsCode(
				baseResult.getQueryUserInfo().getMobile(),
				SmsBizType.CELLPHONE, code, true);
		if (smsCodeResult.isSuccess()) {
			YrdBaseResult baseResult1 = smsManagerService.sendSmsCode(
					newMobile, SmsBizType.NEWCELLPHONE, null);
			if (baseResult1.isSuccess()) {
				session.setAttribute("newPhone", newMobile);
				jsonobj.put("code", 1);
				jsonobj.put("message", "原手机号码校验成功:已向你的新手机号码发送验证码");
				jsonobj.put("newPhone", newMobile);
				jsonobj.put("md5UserBaseId", AppCommonUtil.getMD5(session,
						SessionLocalManager.getSessionLocal().getUserBaseId()));
			} else {
				jsonobj.put("code", 0);
				jsonobj.put("message", baseResult1.getMessage());
				jsonobj.put("newPhone", newMobile);
			}

		} else {
			jsonobj.put("code", 0);
			jsonobj.put("message", smsCodeResult.getMessage());
		}
		return jsonobj;
	}

	/**
	 * 19.修改手机号 type=cellphone v
	 * */
	@ResponseBody
	@RequestMapping("appChangeToNewPhone.htm")
	public Object updateBoundMobile(String newMobile, String code,
			String md5UserBaseId, HttpSession session) throws Exception {
		newMobile = (String) session.getAttribute("newPhone");
		JSONObject jsonobj = new JSONObject();
		UserQueryResult baseResult = userQueryService
				.queryByUserName(SessionLocalManager.getSessionLocal()
						.getUserName());
		SmsCodeResult smsCodeResult = smsManagerService.verifySmsCode(
				baseResult.getQueryUserInfo().getMobile(),
				SmsBizType.CELLPHONE, code, true);
		if (!smsCodeResult.isSuccess()) {
			jsonobj.put("code", 0);
			jsonobj.put("message", "验证码校验失败");
			return jsonobj;
		}
		if (!AppCommonUtil.checkMD5(session, md5UserBaseId)) {
			jsonobj.put("code", 0);
			jsonobj.put("message", "md5UserBaseId错误");
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
				jsonobj.put("code", 1);
				jsonobj.put("message", "修改绑定手机成功");
				session.removeAttribute("newPhone");
			} else {
				jsonobj.put("code", 0);
				jsonobj.put("message", "修改绑定手机失败");
			}
		}
		return jsonobj;
	}

	/**
	 * 20.修改邮箱 type=personal v
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
			jsonobj.put("message", smsCodeResult.getMessage());
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
		}
		YrdBaseResult result = userBaseInfoManager.mailBinding(baseResult
				.getQueryUserInfo().getUserBaseId(), newMail);
		if (result.isSuccess()) {
			jsonobj.put("code", 1);
			jsonobj.put("message", "修改绑定邮箱成功");
		} else {
			jsonobj.put("code", 0);
			jsonobj.put("message", "修改绑定邮箱失败");
		}
		return jsonobj;
	}

}
