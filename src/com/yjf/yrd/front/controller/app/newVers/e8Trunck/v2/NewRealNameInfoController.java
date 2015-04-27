package com.yjf.yrd.front.controller.app.newVers.e8Trunck.v2;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.yjf.common.lang.util.StringUtil;
import com.yjf.yrd.base.UserAccountInfoBaseController;
import com.yjf.yrd.integration.openapi.enums.AttributionEnum;
import com.yjf.yrd.integration.openapi.enums.PeasonSexEnum;
import com.yjf.yrd.integration.openapi.order.UserQuickCertifyOrder;
import com.yjf.yrd.session.SessionLocal;
import com.yjf.yrd.session.SessionLocalManager;
import com.yjf.yrd.user.info.PersonalInfo;
import com.yjf.yrd.user.info.UserInfo;
import com.yjf.yrd.user.order.ApplyRealNameOrder;
import com.yjf.yrd.user.order.NonMainlandRealAuthenticateOrder;
import com.yjf.yrd.ws.enums.CertTypeEnum;
import com.yjf.yrd.ws.enums.UserTypeEnum;
import com.yjf.yrd.ws.result.YrdBaseResult;

@Controller
@RequestMapping("app")
public class NewRealNameInfoController extends UserAccountInfoBaseController {

	/**
	 * 获取实名认证信息
	 * 
	 * */
	@ResponseBody
	@RequestMapping("getRealNameInfo.htm")
	public JSONObject getRealNameInfo(HttpSession session, Model model)
			throws Exception {
		JSONObject json = new JSONObject();
		if (!AppCommonUtil.isLogin()) {
			json.put("code", -1);
			json.put("message", "未登录或登陆失效");
			return json;
		}
		String userbaseId = SessionLocalManager.getSessionLocal()
				.getUserBaseId();
		PersonalInfo personalInfo = userQueryService.queryPersonalInfoByBaseId(
				userbaseId).getQueryPersonalInfo();
		UserInfo userBaseInfo = getUserBaseInfo(session, model);
		Map<String, String> map = new HashMap<String, String>();
		if (personalInfo != null) {
			map.put("realName", personalInfo.getRealName());
			map.put("certType", "身份证");
			map.put("businessPeriod", personalInfo.getBusinessPeriod());
			map.put("address", personalInfo.getBankAddress());
			map.put("mobile", personalInfo.getMobile());
			map.put("customerSource", personalInfo.getCustomerSource());
			map.put("province", personalInfo.getBankProvince());
			map.put("gender", String.valueOf(personalInfo.getGender()));
			map.put("certFrontPath", personalInfo.getCertFrontPath());
			map.put("certBackPath", personalInfo.getCertBackPath());
			String status = "N";
			if (StringUtil.isNotEmpty(personalInfo.getBankProvince())
					&& !"DL".equals(personalInfo.getBankProvince())) {
				if (personalInfo.getRealNameAuthentication() != null) {
					status = personalInfo.getRealNameAuthentication().getCode();
				}
			}
			if (userBaseInfo.getRealNameAuthentication() != null) {
				status = userBaseInfo.getRealNameAuthentication().getCode();
			}
			map.put("certifyLevel", String.valueOf(SessionLocalManager.getSessionLocal().getCertifyLevel()));		
			map.put("realNameAuthentication", status);
			map.put("certNo",AppCommonUtil.viewStr(personalInfo.getCertNo(), "idCard"));
			map.put("realCertNo",personalInfo.getCertNo());
			json.put("realNameInfo", AppCommonUtil.cleanNull(map));
			String token = UUID.randomUUID().toString();
			session.setAttribute("token", token);
			json.put("token", token);
			json.put("code", 1);
			json.put("message", "获取实名信息成功");
		} else {
			json.put("code", 0);
			json.put("message", "获取实名信息失败");
		}
		return json;

	}

	/**
	 * 实名认证（含港澳台）
	 * */
	@ResponseBody
	@RequestMapping("submitRealNameInfo.htm")
	public JSONObject submitRealNameInfo(HttpSession session,
			String regionType, String realName, String certNo,
			String businessPeriod, String conCertNo, String sex,
			String address, String certFrontPath, String certBackPath)
			throws Exception {
		JSONObject jsonobj = new JSONObject();
		jsonobj = updateRealNameAuthenticationInfo(session, regionType,
				realName, certNo, businessPeriod, conCertNo, sex, address,
				certFrontPath, certBackPath);
		return jsonobj;

	}

	@ResponseBody
	@RequestMapping("realNameAuthentication.htm")
	public JSONObject updateRealNameAuthenticationInfo(HttpSession session,
			String regionType, String realName, String certNo,
			String businessPeriod, String conCertNo, String sex,
			String address, String certFrontPath, String certBackPath)
			throws Exception {

		JSONObject jsonobj = new JSONObject();
		if (!AppCommonUtil.isLogin()) {
			jsonobj.put("code", -1);
			jsonobj.put("message", "未登录或登录已失效");
			return jsonobj;
		}
		String userBaseId = SessionLocalManager.getSessionLocal()
				.getUserBaseId();
		UserInfo userBaseInfo = userQueryService.queryByUserBaseId(userBaseId)
				.getQueryUserInfo();
		if (userBaseInfo.getType() == UserTypeEnum.GR) {
			if ("DL".equals(regionType) || StringUtil.isEmpty(regionType)) {

				// 个人的时候需要同步更新user_base_info的真实名称
				ApplyRealNameOrder order = new ApplyRealNameOrder();
				order.setUserBaseId(userBaseId);
				// 改变值
				order.setRealName(realName);
				order.setCertNo(certNo);

				order.setBusinessPeriod(businessPeriod);
				order.setCertFrontPath(certFrontPath);
				order.setCertBackPath(certBackPath);
				// 调用修改方法

				YrdBaseResult yrdBaseResult = realNameAuthenticationService
						.applyRealName(order);
				if (yrdBaseResult.isSuccess()) {
					jsonobj.put("code", 1);
					jsonobj.put("message", "实名认证发送成功");
				} else {
					jsonobj.put("code", 0);
					jsonobj.put("message", "实名认证发送失败");
				}
			} else {
				NonMainlandRealAuthenticateOrder order = new NonMainlandRealAuthenticateOrder();
				order.setUserBaseId(userBaseId);
				order.setRealName(realName);
				order.setSex(PeasonSexEnum.getByCode(sex));
				order.setCertTypeEnum(CertTypeEnum.Identity_Card);
				order.setAttribution(AttributionEnum.getByCode(regionType));
				order.setCertNo(certNo);
				order.setBusinessPeriod(businessPeriod);
				order.setCertFrontPath(certFrontPath);
				order.setCertBackPath(certBackPath);
				order.setAddress(address);
				YrdBaseResult yrdBaseResult = realNameAuthenticationService
						.applyNonMainlandRealName(order);

				if (yrdBaseResult.isSuccess()) {
					jsonobj.put("code", 1);
					jsonobj.put("message", "实名认证发送成功");
				} else {
					jsonobj.put("code", 0);
					jsonobj.put("message", "实名认证发送失败");
				}
			}

			// 更新SessionLocal
			SessionLocal sessionLocal = SessionLocalManager.getSessionLocal();
			sessionLocal.setRealName(realName);
			SessionLocalManager.setSessionLocal(sessionLocal);

		}
		return jsonobj;
	}
	
	/**
	 * 弱实名
	 * */
	@ResponseBody
	@RequestMapping("quickCertify.htm")
	public JSONObject  quickCertify(UserQuickCertifyOrder order){
		JSONObject json = new JSONObject();
		if(!AppCommonUtil.isLogin()){
			json.put("code", -1);
			json.put("message","未登录或登录失效");
			return json;
		}
		try{
			order.setUserId(SessionLocalManager.getSessionLocal().getAccountId());	
		}catch(NullPointerException e){
			json.put("code", 0);
			json.put("message","请先激活支付账号");
			return json;
		}
		YrdBaseResult result=realNameAuthenticationService.applyUserQuickCertifyOrder(order);
		if(result.isSuccess()){
			json.put("code", 1);
			json.put("message","快速实名申请成功");
		}else{
			json.put("code", 0);
			json.put("message",StringUtil.isNotEmpty(result.getMessage())?result.getMessage():"快速实名申请失败");
		}
		return json;
		
	}
}
