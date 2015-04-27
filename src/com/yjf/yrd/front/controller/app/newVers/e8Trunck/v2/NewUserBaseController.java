package com.yjf.yrd.front.controller.app.newVers.e8Trunck.v2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.yjf.common.lang.util.money.Money;
import com.yjf.yrd.base.UserAccountInfoBaseController;
import com.yjf.yrd.integration.openapi.info.YjfAccountInfo;
import com.yjf.yrd.integration.web.server.YjfLoginWebServer;
import com.yjf.yrd.integration.web.server.impl.YjfPayPwdOrder;
import com.yjf.yrd.session.SessionLocalManager;
import com.yjf.yrd.user.IOperatorInfoService;
import com.yjf.yrd.user.info.PersonalInfo;
import com.yjf.yrd.user.info.UserInfo;
import com.yjf.yrd.user.info.UserRelationInfo;
import com.yjf.yrd.util.AppConstantsUtil;
import com.yjf.yrd.util.StringUtil;
import com.yjf.yrd.web.util.PermissionUtil;
import com.yjf.yrd.web.util.TradeStatisticsUtil;
import com.yjf.yrd.ws.enums.BooleanEnum;
import com.yjf.yrd.ws.enums.SysUserRoleEnum;
import com.yjf.yrd.ws.result.YrdBaseResult;
import com.yjf.yrd.ws.service.query.order.QueryLoanTradeOrder;
import com.yjf.yrd.ws.statistics.TradeAmountStatisticsInfo;
import com.yjf.yrd.ws.statistics.result.TradeStatisticsResult;

/**
 * @Filename appUserBaseController.java
 * 
 * @Description
 * 
 * @Version 2.0
 * 
 * @Author zhaohaibing
 * 
 * @Email abing@yiji.com
 * 
 * @Date: 2014-1-13
 * 
 */
@Controller
@RequestMapping("app")
public class NewUserBaseController extends UserAccountInfoBaseController {
	
	@Autowired
	protected IOperatorInfoService operatorInfoService;
	@Autowired
	YjfLoginWebServer yjfLoginWebServer;
	
	// ------------------------------------------------------------账户首页/账户基本资料查询----------------------------------------------------------------------------
	/**
	 * 12. 我的账户
	 * */
	@ResponseBody
	@RequestMapping("appMeAccount.htm")
	public JSONObject appMeAccount(HttpSession session, HttpServletResponse response, Model model)
																									throws Exception {
		JSONObject json = new JSONObject();
		if (!AppCommonUtil.isLogin()) {
			json.put("code", -1);
			json.put("message", "未登录或登录已失效");
			return json;
		}
		int investor = PermissionUtil.check("/tradeQuery/entries/12/1");
		int investorManager = PermissionUtil.check("/investorManager/*");
		if (investor != 1 && investorManager != 1) {
			json.put("code", -2);
			json.put("message", "你的账户不是经纪人或投资人：请用网页版登陆");
			return json;
		}
		UserInfo userBaseInfo = getUserBaseInfo(session, model);
		YjfAccountInfo accountInfo = getAccountInfo(model);
		if (userBaseInfo == null || accountInfo == null) {
			json.put("code", 0);
			json.put("message", "个人信息或账户信息不存在");
			return json;
			
		}
		session.setAttribute("accountUserId", accountInfo.getUserId());
		List<Map<String, String>> userInfos = new ArrayList<Map<String, String>>();
		Map<String, String> map = new HashMap<String, String>();
		map.put("userName", userBaseInfo.getUserName());
		map.put("realName", userBaseInfo.getRealName());
		map.put("balance", getBalance(accountInfo.getBalance()));
		map.put("freezeAmount", accountInfo.getFreezeAmount().toStandardString());
		map.put("availableBalance", accountInfo.getAvailableBalance().toStandardString());
		
		/* 待收收益 待收收益 */
		Map<String, Object> investCountMap = new HashMap<String, Object>();
		Map<String, Object> conditions = new HashMap<String, Object>();
		conditions.put("userId", SessionLocalManager.getSessionLocal().getUserId());
		conditions.put("roleId", SysUserRoleEnum.INVESTOR.getValue());
		QueryLoanTradeOrder tradeOrder = new QueryLoanTradeOrder();
		tradeOrder.setRoleId(SysUserRoleEnum.INVESTOR.getValue());
		tradeOrder.setUserId(SessionLocalManager.getSessionLocal().getUserId());
		TradeStatisticsResult<TradeAmountStatisticsInfo> statisticsResult = tradeBizQueryService
			.searchLoanTradeSumCommonQuery(tradeOrder);
		TradeStatisticsUtil.countInvestedMoney(statisticsResult, investCountMap);
		map.put("dueinProfit", investCountMap.get("notPaidInvestedAmount").toString());// 待收收益
		map.put("investedProfitAmount", investCountMap.get("investedProfitAmount").toString());// 总收益
		map.put("duein", investCountMap.get("notPaidInvestedProfitAmount").toString());// 待收本金
		map.put("totalInvestedAmount", investCountMap.get("totalInvestedAmount").toString());// 投资总额
		double paidInvestedAmount = Double.parseDouble(investCountMap.get("investedProfitAmount")
			.toString())
									- Double.parseDouble((investCountMap
										.get("notPaidInvestedAmount").toString()));
		map.put("paidInvestedAmount", String.format("%.2f", paidInvestedAmount));
		userInfos.add(AppCommonUtil.cleanNull(map));
		json.put("message", "个人账户信息查询成功!");
		
		// 权限信息:
		Map<String, String> permission = new HashMap<String, String>();
		permission.put("accountInfo", "1");// 账户信息
		permission.put("tradeDetail", "1");// 交易明细
		permission.put("consumerManager", String.valueOf(investorManager));
		permission.put("businessManager", String.valueOf(investorManager));
		permission.put("investProject", PermissionUtil.check("/tradeQuery/entries/12/1") + "");
		long brokerId = SessionLocalManager.getSessionLocal().getUserId();
		UserRelationInfo userRelationInfo = userRelationQueryService.findUserRelationByChildId(
			brokerId).getQueryUserRelationInfo();
		if (userRelationInfo != null) {
			permission.put("QRCode", String.valueOf(investorManager));
			json.put("openInvestorUrl",
				AppConstantsUtil.getHostHttpUrl() + "/app/preRegist.htm?referees="
						+ userRelationInfo.getMemberNo());
		} else {
			permission.put("QRCode", "0");
			json.put("openInvestorUrl", "");
		}
		permission.put("giftMoneyInfo", PermissionUtil.check("/userGiftMoney/*") + "");
		String testAccount = AppConstantsUtil.getTestAccount();
		if (StringUtil.isNotEmpty(testAccount) && userBaseInfo.getUserName().equals(testAccount)) {
			permission.put("accountInfo", "0");
			permission.put("tradeDetail", "1");
			permission.put("investProject", "1");
			permission.put("consumerManager", "0");
			permission.put("businessManager", "0");
			permission.put("QRCode", "0");
			permission.put("giftMoneyInfo", "0");
			
		}
		permission.put("dkCharge", "1");
		userInfos.add(permission);
		json.put("code", 1);
		json.put("list", userInfos);
		json.put("activeAccount", "1");
		return json;
	}
	
	// ----------appAccountInfo----------------------------------------------------------------------------------------------
	/**
	 * 13.账户信息
	 * */
	@ResponseBody
	@RequestMapping("appAccountInfo.htm")
	public JSONObject appAccountInfo(HttpServletResponse response, HttpSession session, Model model)
																									throws Exception {
		JSONObject json = new JSONObject();
		if (!AppCommonUtil.isLogin()) {
			json.put("code", -1);
			json.put("message", "未登录或登录已失效");
			return json;
		}
		UserInfo userBaseInfo = getUserBaseInfo(session, model);
		YjfAccountInfo accountInfo = getAccountInfo(model);
		if (userBaseInfo == null && accountInfo == null) {
			json.put("code", 0);
			json.put("message", "个人信息或账户信息不存在");
			return json;
		}
		
		String status = "N";
		String modifyYjfPwdUrl = "";
		String activeYjfPwdUrl = "";
		String modifyYjfLogUrl = "";
		String openState = "已激活";
		
		String userbaseId = SessionLocalManager.getSessionLocal().getUserBaseId();
		PersonalInfo personalInfo = userQueryService.queryPersonalInfoByBaseId(userbaseId)
			.getQueryPersonalInfo();
		if (userBaseInfo.getRealNameAuthentication() != null) {
			status = userBaseInfo.getRealNameAuthentication().getCode();
		} else if (personalInfo.getRealNameAuthentication() != null) {
			status = personalInfo.getRealNameAuthentication().getCode();
		}
		if (accountInfo.getUserStatus() != null) {
			if ("W".equals(accountInfo.getUserStatus().getCode())) {
				openState = "未激活";
				activeYjfPwdUrl = AppCommonUtil.appAccountActiveUrl(userBaseInfo.getAccountId());
			} else {
				modifyYjfPwdUrl = getChangePwd(userBaseInfo.getAccountId(), "payPwd").getUrl();
				modifyYjfLogUrl = getChangePwd(userBaseInfo.getAccountId(), "loginPwd").getUrl();
			}
		}
		Map<String, String> map = new HashMap<String, String>();
		map.put("pswState", "已设置");
		map.put("openState", openState);
		map.put("realName", userBaseInfo.getRealName());
		map.put("mobile", userBaseInfo.getMobile());
		map.put("realNameAuthentication", status);
		map.put("userId", accountInfo.getUserId());
		map.put("modifyYjfPwdUrl", modifyYjfPwdUrl);
		map.put("modifyYjfLogUrl", modifyYjfLogUrl);
		map.put("accountName", userBaseInfo.getAccountName());
		map.put("platformName", AppConstantsUtil.getYrdPrefixion());
		map.put("mail", AppCommonUtil.viewStr(userBaseInfo.getMail(), "mail"));
		map.put("certNo", AppCommonUtil.viewStr(accountInfo.getCertNo(), "idCard"));
		map.put("viewMobile", AppCommonUtil.viewStr(userBaseInfo.getMobile(), "mobile"));
		map.put("certifyLevel",
			String.valueOf(SessionLocalManager.getSessionLocal().getCertifyLevel()));
		map.put("mobileBinding",
			StringUtil.isNotEmpty(userBaseInfo.getMobile()) ? BooleanEnum.IS.getCode()
				: BooleanEnum.NO.getCode());
		map.put("mailBinding",
			StringUtil.isNotEmpty(userBaseInfo.getMail()) ? BooleanEnum.IS.getCode()
				: BooleanEnum.NO.getCode());
		
		json.put("activeYjfPwdUrl", activeYjfPwdUrl);
		json.put("code", 1);
		json.put("message", "个人账户信息查询成功!");
		json.put("accountInfo", AppCommonUtil.cleanNull(map));
		return json;
	}
	
	private YrdBaseResult getChangePwd(String userId, String type) {
		YjfPayPwdOrder payPwdOrder = new YjfPayPwdOrder();
		payPwdOrder.setUserId(userId);
		payPwdOrder.setPasswordType(type);
		payPwdOrder.setSources("APP");
		payPwdOrder.setReturnUrl(AppConstantsUtil.getHostHttpUrl() + "/app/changePwd.htm");
		payPwdOrder.setErrorNotifyUrl(AppConstantsUtil.getHostHttpUrl() + "/app/changePwd.htm");
		String btnColor = AppConstantsUtil.getYjfModifyPwdBtnColor();
		if (StringUtil.isNotEmpty(btnColor)) {
			payPwdOrder.setBtnColor(btnColor);
		}
		YrdBaseResult yrdBaseResult = yjfLoginWebServer.gotoYjfModifyPayPwdUrl(payPwdOrder,
			this.getOpenApiContext());
		return yrdBaseResult;
	}
	
	private String getBalance(Money balance) {
		// TODO 投资统计//借款统计
		Map<String, Object> investCountMap = new HashMap<String, Object>();
		QueryLoanTradeOrder tradeOrder = new QueryLoanTradeOrder();
		tradeOrder.setRoleId(SysUserRoleEnum.INVESTOR.getValue());
		tradeOrder.setUserId(SessionLocalManager.getSessionLocal().getUserId());
		TradeStatisticsResult<TradeAmountStatisticsInfo> statisticsResult = tradeBizQueryService
			.searchLoanTradeSumCommonQuery(tradeOrder);
		TradeStatisticsUtil.countInvestedMoney(statisticsResult, investCountMap);
		
		Money ResultBalance = balance
			.add((Money) investCountMap.get("notPaidInvestedProfitAmount")).add(
				(Money) investCountMap.get("notPaidInvestedAmount"));
		return ResultBalance.toStandardString();
	}
}
