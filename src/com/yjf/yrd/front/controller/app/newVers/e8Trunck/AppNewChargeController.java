package com.yjf.yrd.front.controller.app.newVers.e8Trunck;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.yjf.common.lang.util.StringUtil;
import com.yjf.common.lang.util.money.Money;
import com.yjf.yrd.base.UserAccountInfoBaseController;
import com.yjf.yrd.integration.openapi.VerifyBankCardService;
import com.yjf.yrd.integration.openapi.enums.UserStatusEnum;
import com.yjf.yrd.integration.openapi.info.YjfAccountInfo;
import com.yjf.yrd.integration.openapi.order.DepositDeductOrder;
import com.yjf.yrd.integration.openapi.result.DeductDepositResult;
import com.yjf.yrd.integration.web.server.YjfLoginWebServer;
import com.yjf.yrd.integration.web.server.impl.YjfEzmoneyCheckPaytkOrder;
import com.yjf.yrd.integration.web.server.impl.YjfEzmoneyPayPassUrlOrder;
import com.yjf.yrd.service.openingbank.api.OpeningBankService;
import com.yjf.yrd.session.SessionLocal;
import com.yjf.yrd.session.SessionLocalManager;
import com.yjf.yrd.util.AppConstantsUtil;
import com.yjf.yrd.util.BusinessNumberUtil;
import com.yjf.yrd.util.YrdConstants;
import com.yjf.yrd.web.util.WebUtil;
import com.yjf.yrd.ws.base.info.BankConfigInfo;
import com.yjf.yrd.ws.enums.CertTypeEnum;
import com.yjf.yrd.ws.enums.CertifyStatusEnum;
import com.yjf.yrd.ws.enums.DepositStatusEnum;
import com.yjf.yrd.ws.info.UserBankInfo;
import com.yjf.yrd.ws.result.YrdBaseResult;
import com.yjf.yrd.ws.service.UserBankService;
import com.yjf.yrd.ws.service.query.result.QueryBaseBatchResult;

/**
 * 
 * 代扣充值
 * 
 * @Filename AppNewChargeController.java
 * 
 * @Description
 * 
 * @Version 1.0
 * 
 * @Author zhaohaibing
 * 
 * @Email abing@yiji.com
 * 
 *        Date: 2014-9-17
 * 
 * 
 */
@Controller
@RequestMapping("appNew")
public class AppNewChargeController extends UserAccountInfoBaseController {

	@Autowired
	private UserBankService userBankService;

	@Autowired
	OpeningBankService openingBankService;

	@Autowired
	VerifyBankCardService verifyBankCardService;
	@Autowired
	YjfLoginWebServer yjfLoginWebServer;
	// 银行图片位置
	String IMGURL = "http://image.yiji.com/upload/frt/uploadfile/images/appimage/";

	/**
	 * 代扣充值获取绑定银行卡信息
	 * 
	 * */
	@ResponseBody
	@RequestMapping("dkCharge.htm")
	public JSONObject dkCharge(HttpServletRequest request, HttpSession session,
			Model model, HttpServletResponse response) throws IOException {
		logger.info("appnew获取");
		JSONObject json = new JSONObject();
		if (!isLogin()) {
			json.put("code", "-1");
			json.put("message", "获取绑定银行信息失败:未登录或登录已失效");
			return json;
		}
		logger.info("appnew用户：userName={}进入代扣充值：获取绑定银行卡信息。。",
				SessionLocalManager.getSessionLocal().getUserName());
		YjfAccountInfo accountInfo = this.getAccountInfo(model);
		json.put("mobile", accountInfo.getMobile());
		if (accountInfo.getUserStatus() == UserStatusEnum.NORMAL) {
			SessionLocal sessionLocal = SessionLocalManager.getSessionLocal();
			QueryBaseBatchResult<UserBankInfo> batchResult = userBankService
					.queryUserBankInfo(sessionLocal.getUserBaseId());
			List<Map<String, Object>> signBankInfos = new ArrayList<Map<String, Object>>();
			Map<String, UserBankInfo> signBankMap = new HashMap<String, UserBankInfo>();
			for (UserBankInfo bankInfo : batchResult.getPageList()) {
				BankConfigInfo bankConfigInfo = bankDataService
						.loadBankConfigInfo(bankInfo.getBankType());
				if (bankConfigInfo != null) {
					String logoUrl = IMGURL + bankInfo.getBankType() + ".png";
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("bankCode", bankInfo.getId());
					map.put("bankType", bankInfo.getBankType());
					map.put("bankGifUrl", bankConfigInfo.getLogoUrl());
					map.put("bankLogo", logoUrl);
					map.put("bankName", bankConfigInfo.getBankName());
					map.put("thisCardLastNum", bankInfo.getBankCardNo()
							.substring(bankInfo.getBankCardNo().length() - 4));
					signBankMap.put(String.valueOf(bankInfo.getId()), bankInfo);
					map.put("openCardPrivence", bankInfo.getBankProvince());
					map.put("isDefault", bankInfo.getIsDefault());
					signBankInfos.add(map);
				}

			}
			request.getSession().setAttribute(
					YrdConstants.SesssionKey.SIGN_BANK_KEY, signBankMap);
			json.put("signBankInfos", signBankInfos);
			YjfEzmoneyPayPassUrlOrder ezmoneyPayPassUrlOrder = new YjfEzmoneyPayPassUrlOrder();
			ezmoneyPayPassUrlOrder.setUserId(sessionLocal.getAccountId());
			ezmoneyPayPassUrlOrder.setIsPhone("1");
			ezmoneyPayPassUrlOrder.setPlatformName(AppConstantsUtil
					.getYrdPrefixion());
			YrdBaseResult baseResult = yjfLoginWebServer
					.gotoYjfValidatePayPasswordUrl(ezmoneyPayPassUrlOrder,
							this.getOpenApiContext());
			json.put("yjfPaypassUrl", baseResult.getUrl());
			String token = BusinessNumberUtil.gainOutBizNoNumber();
			request.getSession().setAttribute("token", token);
			json.put("token", token);
			json.put("isAccountActive", true);
			json.put("code", "1");
			json.put("message", "获取绑定银行信息成功");

			logger.info("appnew用户：userName={}进入代扣充值：获取绑定银行卡信息成功：bankInfo={}",
					SessionLocalManager.getSessionLocal().getUserName(),
					signBankInfos);

		} else {
			json.put("code", "0");
			json.put("isAccountActive", false);
			json.put("message", "该用户第三方支付账户未激活\n请用电脑版登录并完成激活。");

		}
		return json;

	}

	/**
	 * 代扣充值-提交
	 * 
	 * @throws Exception
	 * */
	@ResponseBody
	@RequestMapping("dkChargeSubmit.htm")
	public JSONObject dkChargeSubmit(HttpServletRequest request,
			String rechargeAmount, String bankCode, Model model, String code,
			HttpSession session, HttpServletResponse response) throws Exception {
		JSONObject json = new JSONObject();
		String paytk = request.getParameter("paytk");
		String token = request.getParameter("token");
		if (!isLogin()) {
			json.put("code", "-1");
			json.put("message", "充值失败:未登录或登录已失效");
			logger.info("充值失败:未登录或登录已失效 json={}", json);
			return json;
		}
		logger.info("token return ::::::::::" + token + " requestMap="
				+ WebUtil.getRequestMap(request));
		if (StringUtil.isNotBlank(token)) {
			String sessionToken = (String) request.getSession().getAttribute(
					"token");
			request.getSession().removeAttribute("token");
			if (!token.equals(sessionToken)) {
				json.put("code", "0");
				json.put("message", "token错误：请勿重复提交");
				return json;
			}
		} else {
			json.put("code", "0");
			json.put("message", "token错误:没有找到token值");
			return json;
		}
		SessionLocal sessionLocal = SessionLocalManager.getSessionLocal();
		YjfEzmoneyCheckPaytkOrder ezmoneyPayPassUrlOrder = new YjfEzmoneyCheckPaytkOrder();
		ezmoneyPayPassUrlOrder.setUserId(sessionLocal.getAccountId());
		ezmoneyPayPassUrlOrder.setPaytk(paytk);
		YrdBaseResult baseResult = yjfLoginWebServer.gotoYjfCheckPayTk(
				ezmoneyPayPassUrlOrder, this.getOpenApiContext());
		if (!baseResult.isSuccess()) {
			json.put("code", "0");
			json.put("message", "支付令牌错误");
			return json;
		}

		Money money = Money.zero();
		try {
			money = new Money(rechargeAmount);
		} catch (Exception e) {
			logger.error("rechargeAmount==" + rechargeAmount, e);
			json.put("code", "0");
			json.put("message", "金额输入有误，请再次确认后重新录入");
			return json;
		}

		initAccountInfo(model);
		if (!CertifyStatusEnum.AUTHORIZED.message().equals(
				getAccountInfo(model).getCertifyStatus())) {
			json.put("code", "0");
			json.put("message", "亲，你还未实名认证，请先进行实名认证");
			return json;
		}

		json = deduct(money, bankCode, model, session);
		return json;
	}

	@SuppressWarnings("unchecked")
	public JSONObject deduct(Money money, String bankCode, Model model,
			HttpSession session) {
		SessionLocal local = SessionLocalManager.getSessionLocal();
		JSONObject json = new JSONObject();
		Map<String, UserBankInfo> signBankMap = (Map<String, UserBankInfo>) session
				.getAttribute(YrdConstants.SesssionKey.SIGN_BANK_KEY);
		if (signBankMap == null) {
			json.put("code", "0");
			json.put("message", "未签约银行");
			return json;
		}
		UserBankInfo signBankInfo = signBankMap.get(bankCode);
		if (signBankInfo == null) {
			json.put("code", "0");
			json.put("message", "未知的银行bankCode=" + bankCode);
			return json;
		}
		DepositDeductOrder deductOrder = new DepositDeductOrder();
		deductOrder.setAmount(money);
		deductOrder.setAccountName(local.getAccountName());
		deductOrder.setBankAccountName(local.getRealName());
		deductOrder.setBankAccountNo(signBankInfo.getBankCardNo());
		deductOrder.setBankCode(signBankInfo.getBankType());
		// deductOrder.setCertNo(baseInfoDO.getCertNo());
		deductOrder.setCertType(CertTypeEnum.Identity_Card);
		deductOrder.setProvName("重庆市");
		deductOrder.setCityName("重庆市");
		deductOrder.setUserId(local.getAccountId());
		deductOrder.setMemo("快捷充值");
		BankConfigInfo bankConfigInfo = bankDataService
				.loadBankConfigInfo(signBankInfo.getBankType());
		deductOrder.setBankName(bankConfigInfo.getBankName());

		DeductDepositResult baseResult = new DeductDepositResult();
		try {
			baseResult = this.deductYrdService.deductDeposit(deductOrder);
			if (baseResult.isSuccess()
					&& baseResult.getDepositStatusEnum() == DepositStatusEnum.SUCCESS) {
				json.put("code", "1");
				json.put("message", baseResult.getMessage());
				initAccountInfo(model, false);
			} else if (baseResult.isSuccess()) {
				json.put("code", "1");
				json.put("isProcessing", true);
				json.put("message", "处理中。。。");
			} else {
				json.put("code", "0");
				json.put("message", baseResult.getMessage());
			}

		} catch (Exception e) {
			logger.error("deductDeposit Exception", e);
			json.put("code", "0");
			json.put("message", "系统异常");
		}
		return json;
	}

	/**
	 * 判断是否登录
	 * 
	 * @return boolean
	 */
	private boolean isLogin() {

		try {
			if (null != SessionLocalManager.getSessionLocal().getUserName()) {
				return true;
			}
		} catch (NullPointerException e) {
			logger.error("判断是否登录时出现空指针异常");
		}
		return false;
	}
}
