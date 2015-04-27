package com.yjf.yrd.front.controller.app.newVers.e8Trunck.v2;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yjf.yrd.dal.dataobject.InstitutionsInfoDO;
import com.yjf.yrd.dal.dataobject.PersonalInfoDO;
import com.yjf.yrd.dal.dataobject.UserBaseInfoDO;
import com.yjf.yrd.front.controller.trade.base.LoanTradeDetailBaseController;
import com.yjf.yrd.session.SessionLocalManager;
import com.yjf.yrd.user.info.UserRelationInfo;
import com.yjf.yrd.user.order.BrokerOpenInvestorOrder;
import com.yjf.yrd.web.util.WebUtil;
import com.yjf.yrd.ws.result.YrdBaseResult;

/**
 * @Filename appNewOpenInvestorController.java
 * 
 * @Description
 * 
 * @Version 2.0
 * 
 * @Author zhaohaibing
 * 
 * @Email abing@yiji.com
 * 
 * @Date: 2015-1-14
 * 
 */
@Controller
@RequestMapping("app")
public class NewOpenInvestorController extends LoanTradeDetailBaseController {

	/**
	 * 进入投资人开户 页面 获取前置信息
	 * 
	 * */
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping("investorOpenAccount.htm")
	public JSONObject appInvestorOpenAccount(HttpSession session)
			throws Exception {
		JSONObject json = new JSONObject();
		if (AppCommonUtil.isLogin()) {
			session.setAttribute("current", 3);
			json.put("uploadHost", "");
			long brokerId = SessionLocalManager.getSessionLocal().getUserId();
			UserRelationInfo userRelationInfo = userRelationQueryService
					.findUserRelationByChildId(brokerId)
					.getQueryUserRelationInfo();
			if (userRelationInfo != null) {
				logger.info("该经纪人可以给投资人开户");
				json.put("availabelBroker", true);
				json.put("type", "GR");
				json.put("code", 1);
				json.put("message", "该经纪人可以给投资人开户");

			} else {
				json.put("availabelBroker", false);
				logger.info("该经纪人不可以给投资人开户");
				json.put("code", 0);
				json.put("message", "该经纪人不可以给投资人开户");
			}
			String token = UUID.randomUUID().toString();
			session.setAttribute("token", token);
			json.put("token", token);
		} else {
			json.put("availabelBroker", false);
			json.put("code", -1);
			json.put("message", "未登录或登录已失效");
		}
		logger.info("经纪人给投资人开户，获取信息：json={}", json);
		return json;
	}

	/**
	 * 15、投资人开户
	 * 
	 * @param userName
	 * @param realName
	 * @param email
	 * @throws IOException
	 * */
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping("appOpenInvestor.htm")
	public JSONObject investorOpenAccountSubmit(HttpServletRequest request,
			HttpSession session, String token,
			InstitutionsInfoDO institutionInfo, PersonalInfoDO personalInfo,
			UserBaseInfoDO userBaseInfo, Model model) throws IOException {
		this.initAccountInfo(model);
		JSONObject json = new JSONObject();
		String getToken = (String) session.getAttribute("token");
		if (getToken != null) {
			if (getToken.equals(token)) {
				session.removeAttribute("token");
				BrokerOpenInvestorOrder investorOrder = new BrokerOpenInvestorOrder();
				WebUtil.setPoPropertyByRequest(investorOrder, request);
				investorOrder.setBrokerUserId(SessionLocalManager
						.getSessionLocal().getUserId());
				YrdBaseResult baseResult = registerService
						.brokerOpenInvestor(investorOrder);
				if (baseResult.isSuccess()) {
					json.put("code", 1);
					json.put("message", "开户成功");
					json.put("result", "1");
				} else {
					json.put("code", 0);
					json.put("message", baseResult.getMessage());
					json.put("result", "0");
				}
			} else {
				json.put("code", 0);
				json.put("message", "请无重复提交");
				json.put("result", "0");
			}
		} else {
			json.put("code", 0);
			json.put("message", "请无重复提交");
			json.put("result", "0");
		}
		return json;
	}

	@Override
	public String getNoTradeView() {
		return null;
	}

	@Override
	public String getTradeView() {
		return null;
	}

}
