package com.yjf.yrd.front.controller.app.newVers.e8Trunck.v2;

import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.yjf.common.lang.util.money.Money;
import com.yjf.yrd.base.UserAccountInfoBaseController;
import com.yjf.yrd.integration.openapi.order.AppWithdrawOrder;
import com.yjf.yrd.trade.AppWithrawByPtkService;
import com.yjf.yrd.user.info.UserInfo;
import com.yjf.yrd.util.YrdConstants;
import com.yjf.yrd.ws.info.UserBankInfo;
import com.yjf.yrd.ws.result.YrdBaseResult;

@Controller
@RequestMapping("app")
public class WithrawByPtkController extends UserAccountInfoBaseController {
	
	@Autowired
	AppWithrawByPtkService appWithrawByPtkService;
	
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping("WithrawByPtk.htm")
	public JSONObject WithrawByPtk(AppWithdrawOrder order, Model model, HttpSession session) {
		JSONObject json = new JSONObject();
		if (!AppCommonUtil.isLogin()) {
			json.put("code", -1);
			json.put("message", "未登录或登录失效！");
			return json;
		}
		UserInfo userBaseInfo = getUserBaseInfo(session, model);
		Map<String, UserBankInfo> signBankMap = (Map<String, UserBankInfo>) session
			.getAttribute(YrdConstants.SesssionKey.SIGN_BANK_KEY);
		if (signBankMap == null) {
			json.put("code", 0);
			json.put("message", "未签约银行");
			return json;
		}
		UserBankInfo signBankInfo = signBankMap.get(order.getBankCode());
		if (signBankInfo == null) {
			json.put("code", 0);
			json.put("message", "未知的银行bankCode=" + order.getBankCode());
			return json;
		}
		order.setUserId(userBaseInfo.getAccountId());
		order.setPlantuserId(String.valueOf(userBaseInfo.getUserId()));
		order.setBankCode(signBankInfo.getBankType());
		order.setBankAccountNo(signBankInfo.getBankCardNo());
		order.setBankProvName(signBankInfo.getBankProvince());
		order.setBankCityName(signBankInfo.getBankCity());
		order.setMoney(Money.amout(order.getMoney()).toStandardString());
		YrdBaseResult result = appWithrawByPtkService.appWithrawByPtkYrd(order);
		if (result.isSuccess()) {
			json.put("code", 1);
			json.put("message", result.getMessage());
		} else {
			json.put("code", 0);
			json.put("message", result.getMessage());
		}
		return json;
		
	}
}
