package com.yjf.yrd.before.topup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.yjf.common.lang.util.StringUtil;
import com.yjf.common.lang.util.money.Money;
import com.yjf.yrd.base.UserAccountInfoBaseController;
import com.yjf.yrd.common.result.SmsCodeResult;
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
import com.yjf.yrd.user.info.UserInfo;
import com.yjf.yrd.util.AppConstantsUtil;
import com.yjf.yrd.util.MiscUtil;
import com.yjf.yrd.util.NumberUtil;
import com.yjf.yrd.util.YrdConstants;
import com.yjf.yrd.web.util.WebUtil;
import com.yjf.yrd.ws.base.info.BankConfigInfo;
import com.yjf.yrd.ws.enums.BooleanEnum;
import com.yjf.yrd.ws.enums.CertTypeEnum;
import com.yjf.yrd.ws.enums.DepositStatusEnum;
import com.yjf.yrd.ws.enums.RealNameAuthStatusEnum;
import com.yjf.yrd.ws.enums.SmsBizType;
import com.yjf.yrd.ws.enums.SysUserRoleEnum;
import com.yjf.yrd.ws.info.UserBankInfo;
import com.yjf.yrd.ws.order.DeleteUserBankOrder;
import com.yjf.yrd.ws.order.UserBankOrder;
import com.yjf.yrd.ws.result.YrdBaseResult;
import com.yjf.yrd.ws.service.GiftMoneyAssignProcessService;
import com.yjf.yrd.ws.service.UserBankService;
import com.yjf.yrd.ws.service.query.result.QueryBaseBatchResult;

/**
 * Created by zjialin@yiji.com on 2014/4/16.
 */
@Controller
@RequestMapping("/userManage/topUp/")
public class TopUpController extends UserAccountInfoBaseController {
	
	@Autowired
	private UserBankService userBankService;
	
	@Autowired
	YjfLoginWebServer yjfLoginWebServer;
	
	@Autowired
	OpeningBankService openingBankService;
	
	@Autowired
	VerifyBankCardService verifyBankCardService;
	
	@Autowired
	GiftMoneyAssignProcessService giftMoneyAssignProcessService;
	
	@RequestMapping("withholdingIndex")
	public String withholdingIndex(HttpServletRequest request, Model model, HttpSession session) {
		YjfAccountInfo accountInfo = this.getAccountInfo(model);
		UserInfo userBaseInfo = this.getUserBaseInfo(session, model);
		if (!"已认证".equals(accountInfo.getCertifyStatus())
			|| userBaseInfo.getRealNameAuthentication() != RealNameAuthStatusEnum.IS) {
			updateLocalAccountByRemote(userBaseInfo, accountInfo);
		}
		if (accountInfo.getUserStatus() == UserStatusEnum.NORMAL) {
			SessionLocal sessionLocal = SessionLocalManager.getSessionLocal();
			if (StringUtil.isEmpty(sessionLocal.getRealName())) {
				if (StringUtil.isNotEmpty(userBaseInfo.getRealName())) {
					sessionLocal.setRealName(userBaseInfo.getRealName());
					SessionLocalManager.setSessionLocal(sessionLocal);
				}
			}
			String accountId = sessionLocal.getAccountId();
			QueryBaseBatchResult<UserBankInfo> batchResult = userBankService
				.queryUserBankInfo(sessionLocal.getUserBaseId());
			
			List<Map<String, Object>> signBankInfos = new ArrayList<Map<String, Object>>();
			Map<String, UserBankInfo> signBankMap = new HashMap<String, UserBankInfo>();
			for (UserBankInfo bankInfo : batchResult.getPageList()) {
				BankConfigInfo bankConfigInfo = bankDataService.loadBankConfigInfo(bankInfo
					.getBankType());
				if (bankConfigInfo != null) {
					Map<String, Object> map = MiscUtil.covertPoToMap(bankInfo);
					map.put("bankGifUrl", bankConfigInfo.getLogoUrl());
					map.put("bankCardView",
						bankInfo.getBankCardNo().substring(bankInfo.getBankCardNo().length() - 4));
					signBankMap.put(String.valueOf(bankInfo.getId()), bankInfo);
					signBankInfos.add(map);
				}
				
			}
			if (AppConstantsUtil.canTradeUsePayPassword()) {
				YjfEzmoneyPayPassUrlOrder ezmoneyPayPassUrlOrder = new YjfEzmoneyPayPassUrlOrder();
				ezmoneyPayPassUrlOrder.setUserId(sessionLocal.getAccountId());
				YrdBaseResult baseResult = yjfLoginWebServer.gotoYjfValidatePayPasswordUrl(
					ezmoneyPayPassUrlOrder, this.getOpenApiContext());
				model.addAttribute("yjfPaypassUrl", baseResult.getUrl());
			}
			
			request.getSession().setAttribute(YrdConstants.SesssionKey.SIGN_BANK_KEY, signBankMap);
			model.addAttribute("signBankInfos", signBankInfos);
			UserInfo loaner = userQueryService.queryByUserName(sessionLocal.getUserName(),
				SysUserRoleEnum.LOANER.getValue()).getQueryUserInfo();
			if (loaner == null) {
				model.addAttribute("signBankInfoCount", signBankInfos.size());
			} else {
				model.addAttribute("isLoanerUser", true);
				model.addAttribute("signBankInfoCount", 0);
			}
			
			model.addAttribute("selectBankInfos", bankDataService.loadAllBankConfigInfo());
			return returnVm("withholdingIndex.vm");
		} else {
			model.addAttribute("isSuccess", false);
			model.addAttribute("isAccountNotActive", true);
			model.addAttribute("message", "支付账号未激活用户！");
			return returnVm("withholdingResult.vm");
		}
		
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping("addUserBank")
	public String addUserBank(HttpServletRequest request, HttpServletResponse response, Model model) {
		JSONObject json = new JSONObject();
		
		UserBankOrder order = new UserBankOrder();
		WebUtil.setPoPropertyByRequest(order, request);
		order.setBankType(request.getParameter("bankCode"));
		order.setUserBaseId(SessionLocalManager.getSessionLocal().getUserBaseId());
		YrdBaseResult baseResult = userBankService.addUserBankInfo(order);
		if (baseResult.isSuccess()) {
			json.put("code", "1");
			json.put("message", "添加成功");
		} else {
			json.put("code", "0");
			json.put("message", baseResult.getMessage());
		}
		this.printHttpResponse(response, json);
		return null;
	}
	
	/**
	 * 删除银行
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("deleteUserBank")
	public String deleteUserBank(HttpServletRequest request, HttpServletResponse response,
									Model model) {
		JSONObject json = new JSONObject();
		
		SessionLocal sessionLocal = SessionLocalManager.getSessionLocal();
		String userBaseId = sessionLocal.getUserBaseId();
		
		String bankId = request.getParameter("bankId");
		DeleteUserBankOrder deleteUserBankOrder = new DeleteUserBankOrder();
		deleteUserBankOrder.setId(NumberUtil.parseLong(bankId));
		deleteUserBankOrder.setUserBaseId(userBaseId);
		YrdBaseResult baseResult = userBankService.removeUserBankInfo(deleteUserBankOrder);
		if (baseResult.isSuccess()) {
			json.put("code", "1");
			json.put("message", "删除成功");
		} else {
			json.put("code", "0");
			json.put("message", baseResult.getMessage());
		}
		this.printHttpResponse(response, json);
		return null;
	}
	
	@RequestMapping("withholdingSubmit")
	public String withholdingIndex(String rechargeAmount, String bankCode, String validateCode,
									String paytk, Model model, HttpSession session) {
		Money money = new Money(rechargeAmount);
		initAccountInfo(model);
		if (AppConstantsUtil.canTradeUsePayPassword()) {
			SessionLocal sessionLocal = SessionLocalManager.getSessionLocal();
			YjfEzmoneyCheckPaytkOrder ezmoneyPayPassUrlOrder = new YjfEzmoneyCheckPaytkOrder();
			ezmoneyPayPassUrlOrder.setUserId(sessionLocal.getAccountId());
			ezmoneyPayPassUrlOrder.setPaytk(paytk);
			YrdBaseResult baseResult = yjfLoginWebServer.gotoYjfCheckPayTk(ezmoneyPayPassUrlOrder,
				this.getOpenApiContext());
			if (!baseResult.isSuccess()) {
				model.addAttribute("isSuccess", false);
				model.addAttribute("message", "支付令牌错误");
				return returnVm("withholdingResult.vm");
			}
		} else {
			UserInfo userBaseInfo = (UserInfo) model.asMap().get("userBaseInfo");
			SmsCodeResult smsCodeResult = smsManagerService.verifySmsCode(userBaseInfo.getMobile(),
				SmsBizType.DEPOSIT, validateCode, true);
			if (!smsCodeResult.isSuccess()) {
				model.addAttribute("isSuccess", false);
				model.addAttribute("message", smsCodeResult.getMessage());
				return returnVm("withholdingResult.vm");
			}
		}
		deduct(money, bankCode, model, session);
		return returnVm("withholdingResult.vm");
	}
	
	@SuppressWarnings("unchecked")
	public void deduct(Money money, String bankCode, Model model, HttpSession session) {
		SessionLocal local = SessionLocalManager.getSessionLocal();
		Map<String, UserBankInfo> signBankMap = (Map<String, UserBankInfo>) session
			.getAttribute(YrdConstants.SesssionKey.SIGN_BANK_KEY);
		if (signBankMap == null) {
			model.addAttribute("isSuccess", false);
			model.addAttribute("message", "未签约银行");
			return;
		}
		UserBankInfo signBankInfo = signBankMap.get(bankCode);
		if (signBankInfo == null) {
			model.addAttribute("isSuccess", false);
			model.addAttribute("message", "未知的银行bankCode=" + bankCode);
			return;
		}
		
		DepositDeductOrder deductOrder = new DepositDeductOrder();
		deductOrder.setAmount(money);
		deductOrder.setAccountName(local.getAccountName());
		deductOrder.setBankAccountName(local.getRealName());
		deductOrder.setBankAccountNo(signBankInfo.getBankCardNo());
		deductOrder.setBankCode(signBankInfo.getBankType());
		deductOrder.setCertType(CertTypeEnum.Identity_Card);
		deductOrder.setProvName("重庆市");
		deductOrder.setCityName("重庆市");
		deductOrder.setUserId(local.getAccountId());
		deductOrder.setMemo("快捷充值");
		BankConfigInfo bankConfigInfo = bankDataService.loadBankConfigInfo(signBankInfo
			.getBankType());
		deductOrder.setBankName(bankConfigInfo.getBankName());
		
		DeductDepositResult baseResult = new DeductDepositResult();
		try {
			baseResult = this.deductYrdService.deductDeposit(deductOrder);
			if (baseResult.isSuccess()
				&& baseResult.getDepositStatusEnum() == DepositStatusEnum.SUCCESS) {
				model.addAttribute("isSuccess", baseResult.isSuccess());
				initAccountInfo(model, false);
				
			} else if (baseResult.isSuccess()) {
				model.addAttribute("isProcessing", true);
			} else {
				model.addAttribute("message", baseResult.getMessage());
			}
			
		} catch (Exception e) {
			logger.error("deductDeposit Exception", e);
			model.addAttribute("isSuccess", false);
			model.addAttribute("message", "系统异常");
		}
		
	}
	
	private String returnVm(String vm) {
		String fullVm = "/before/topup/";
		return fullVm + vm;
	}
	
	@RequestMapping("signRedirect")
	public String redirect(HttpServletResponse response, Model model) {
		String accountId = SessionLocalManager.getSessionLocal().getAccountId();
		
		try {
			UserInfo userInfo = userQueryService.queryByUserId(
				SessionLocalManager.getSessionLocal().getUserId()).getQueryUserInfo();
			QueryBaseBatchResult<UserBankInfo> batchResult = userBankService
				.queryUserBankInfo(SessionLocalManager.getSessionLocal().getUserBaseId());
			boolean isHasBank = false;
			for (UserBankInfo info : batchResult.getPageList()) {
				if (info.getIsDel() == BooleanEnum.NO) {
					isHasBank = true;
				}
			}
			if (userInfo != null) {
				if (userInfo.getRealNameAuthentication() != RealNameAuthStatusEnum.IS) {
					model.addAttribute("userType", userInfo.getType());
					model.addAttribute("realName", "error");
					return "/before/topup/signbank.vm";
				}
				if (isHasBank) {
					model.addAttribute("cardNumber", "error");
					return "/before/topup/signbank.vm";
				}
			}
		} catch (Exception e) {
			logger.error("查询用户信息出错:e={}", e.getMessage(), e);
		}
		String url = rechargeFlowService.getSignUrl(accountId);
		try {
			response.sendRedirect(url);
		} catch (IOException e) {
			logger.error("" + e.getMessage(), e);
		}
		return null;
	}
	
}
