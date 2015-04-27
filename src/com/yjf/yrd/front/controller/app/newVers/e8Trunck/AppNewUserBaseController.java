package com.yjf.yrd.front.controller.app.newVers.e8Trunck;

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
import com.yjf.common.lang.util.StringUtil;
import com.yjf.yrd.base.UserAccountInfoBaseController;
import com.yjf.yrd.integration.openapi.info.YjfAccountInfo;
import com.yjf.yrd.session.SessionLocalManager;
import com.yjf.yrd.user.IOperatorInfoService;
import com.yjf.yrd.user.info.UserInfo;
import com.yjf.yrd.util.AppConstantsUtil;
import com.yjf.yrd.web.util.PermissionUtil;
import com.yjf.yrd.web.util.TradeStatisticsUtil;
import com.yjf.yrd.ws.enums.SysUserRoleEnum;
import com.yjf.yrd.ws.service.query.order.QueryLoanTradeOrder;
import com.yjf.yrd.ws.statistics.TradeAmountStatisticsInfo;
import com.yjf.yrd.ws.statistics.result.TradeStatisticsResult;

/**
 * @Filename appNewUserBaseController.java
 * 
 * @Description
 * 
 * @Version 2.0
 * 
 * @Author zhaohaibing
 * 
 * @Email abing@yiji.com
 * 
 *        Date: 2014-9-1
 * 
 */
@Controller
@RequestMapping("appNew")
public class AppNewUserBaseController extends UserAccountInfoBaseController {

	@Autowired
	protected IOperatorInfoService operatorInfoService;

	// ------------------------------------------------------------账户首页/账户基本资料查询----------------------------------------------------------------------------
	@ResponseBody
	@RequestMapping("appMeAccount.htm")
	public JSONObject userHome(HttpServletResponse response,
			HttpSession session, Model model) throws Exception {
		JSONObject json = new JSONObject();
		logger.info("新版app请求获取个人用户信息!");
		List<Map<String, String>> userInfos = new ArrayList<Map<String, String>>();
		if (isLogin()) {
			int investor = PermissionUtil.check("/tradeQuery/entries/12/1");/* 投资的项目 */
			int investorManager = PermissionUtil.check("/investorManager/*");/*
																			 * 客户管理/
																			 * 业务管理
																			 */
			if (investor != 1 && investorManager != 1) {
				json.put("code", -2);
				json.put("message", "你的账户不是经纪人或投资人：\n请用网页版登录");
				logger.info("app用户获取账户信息失败：该用户不是经纪人或投资人！");
				response.getWriter().print(json.toJSONString());
				return null;
			}
			UserInfo userBaseInfo = getUserBaseInfo(session, model);
			YjfAccountInfo accountInfo = getAccountInfo(model);
			if ("W".equals(accountInfo.getUserStatus().getCode())) {
				json.put("activeAccount", "0");
			} else {
				json.put("activeAccount", "1");
			}
			// else{
			Map<String, String> map = new HashMap<String, String>();
			session.setAttribute("accountUserId", accountInfo.getUserId());
			map.put("userName", userBaseInfo.getUserName());// 账户名
			map.put("realName", userBaseInfo.getRealName());// 真实姓名
			map.put("total", accountInfo.getBalance().toStandardString());// 资产总额
			map.put("balance", accountInfo.getBalance().toStandardString());// 资产总额
			map.put("available", accountInfo.getAvailableBalance()
					.toStandardString());// 可用余额
			map.put("availableBalance", accountInfo.getAvailableBalance()
					.toStandardString());// 可用余额
			map.put("freeze", accountInfo.getFreezeAmount().toStandardString());// 冻结金额
			map.put("freezeAmount", accountInfo.getFreezeAmount()
					.toStandardString());// 冻结金额
			/* 待收收益 待收收益 */
			Map<String, Object> investCountMap = new HashMap<String, Object>();
			Map<String, Object> conditions = new HashMap<String, Object>();
			conditions.put("userId", SessionLocalManager.getSessionLocal()
					.getUserId());
			conditions.put("roleId", SysUserRoleEnum.INVESTOR.getValue());
			QueryLoanTradeOrder tradeOrder = new QueryLoanTradeOrder();
			tradeOrder.setRoleId(SysUserRoleEnum.INVESTOR.getValue());
			tradeOrder.setUserId(SessionLocalManager.getSessionLocal()
					.getUserId());
			TradeStatisticsResult<TradeAmountStatisticsInfo> statisticsResult = tradeBizQueryService
					.searchLoanTradeSumCommonQuery(tradeOrder);
			TradeStatisticsUtil.countInvestedMoney(statisticsResult,
					investCountMap);
			map.put("dueinProfit", investCountMap.get("notPaidInvestedAmount")
					.toString());// 待收收益
			map.put("duein", investCountMap.get("notPaidInvestedProfitAmount")
					.toString());// 待收本金
			userInfos.add(map);
			json.put("message", "个人账户信息查询成功!");
			// 权限信息:
			Map<String, String> permission = new HashMap<String, String>();
			permission.put("consumerManager",
					PermissionUtil.check("/investorManager/*") + "");// 客户管理
			permission.put("businessManager",
					PermissionUtil.check("/investorManager/*") + "");// 业务管理
			permission.put("investProject",
					PermissionUtil.check("/tradeQuery/entries/12/1") + "");// 投资的项目
			String closeDkCharge = AppConstantsUtil.getCloseDkCharge();
			permission.put("dkCharge", "1");
			if ("Y".equals(closeDkCharge)) {
				permission.put("dkCharge", "0");
			}
			permission.put("accountInfo", "1");// 账户信息
			permission.put("tradeDetail", "1");// 交易明细
			String testAccount = AppConstantsUtil.getTestAccount();
			if (StringUtil.isNotEmpty(testAccount)
					&& testAccount.equalsIgnoreCase(userBaseInfo.getUserName())) {
				permission.put("accountInfo", "0");// 账户信息
				permission.put("tradeDetail", "1");// 交易明细
				permission.put("investProject", "1");// 投资的项目
				permission.put("consumerManager", "0");// 客户管理
				permission.put("businessManager", "0");// 业务管理

			}

			userInfos.add(permission);
			json.put("code", "1");
			json.put("list", userInfos);
			logger.info("新版app进入账户信息查询结果成功{}：", userInfos);
			// }

		} else {
			json.put("code", "-1");
			json.put("message", "个人账户信息查询失败：未登录或登录已失效");
			logger.info("新版app进入账户信息查询失败:未登录或登录已失效");
		}

		return json;
	}

	// ----------appAccountInfo----------------------------------------------------------------------------------------------
	@ResponseBody
	@RequestMapping("appAccountInfo.htm")
	public JSONObject accountInfo(HttpServletResponse response,
			HttpSession session, Model model) throws Exception {
		JSONObject json = new JSONObject();
		logger.info("新版app请求获取个人用户信息!");
		if (isLogin()) {
			UserInfo userBaseInfo = getUserBaseInfo(session, model);
			YjfAccountInfo accountInfo = getAccountInfo(model);
			String renzheng = "未认证";
			try {
				renzheng = userBaseInfo.getRealNameAuthentication().code();
				if ("IS".equals(renzheng)) {
					renzheng = "已认证";
				} else if ("NO".equals(renzheng)) {
					renzheng = "认证未通过";
				} else if ("IN".equals(renzheng)) {
					renzheng = "认证中";
				}
			} catch (NullPointerException e) {
				logger.info("实名认证未认证");
			}
			Map<String, String> map = new HashMap<String, String>();
			map.put("realName", userBaseInfo.getRealName());
			map.put("idCard",
					hideMiddleString(accountInfo.getCertNo(), "IDCARD"));
			map.put("authState", renzheng);
			map.put("phone",
					StringUtil.isNotEmpty(userBaseInfo.getMobile()) ? userBaseInfo
							.getMobile() : "");
			map.put("email", hideMiddleString(userBaseInfo.getMail(), "MAIL"));
			map.put("payAccount", userBaseInfo.getAccountName());
			map.put("pswState", "已设置");
			YjfAccountInfo accountInfos = userAccountQueryService
					.getAccountInfo(SessionLocalManager.getSessionLocal())
					.getYjfAccountInfo();
			if ("W".equals(accountInfos.getUserStatus().getCode())) {
				map.put("openState", "未开通");
			} else {
				map.put("openState", "已开通");
			}
			json.put("code", "1");
			json.put("message", "个人账户信息查询成功!");
			json.put("accountInfo", map);
			logger.info("新版app进入账户信息查询结果成功{}：", map);
		} else {
			json.put("code", "-1");
			json.put("message", "个人账户信息查询失败：未登录或登录已失效");
			logger.info("新版app进入账户信息查询失败:未登录或已过期");
		}

		return json;
	}

	/**
	 * 隐藏str中间部分
	 * 
	 * @author str0 原字符串
	 * @author str1 开始截取部分
	 * @return String
	 */
	private String hideMiddleString(String str0, String str1) {
		String result = "";
		if (StringUtil.isNotBlank(str0) && "MAIL".equals(str1)) {
			return result = str0.substring(0, 3) + "***"
					+ str0.subSequence(str0.indexOf("@"), str0.length());
		} else if (StringUtil.isNotBlank(str0) && "IDCARD".equals(str1)) {
			return result = str0.substring(0, 3) + "***********"
					+ str0.subSequence(str0.length() - 4, str0.length());
		} else {
			return result;
		}
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
			logger.error("判断是否登录时出现空指针异常:未登录");
		}
		return false;
	}

}
