package com.yjf.yrd.front.controller.app.newVers.e8Trunck.v2;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.yjf.common.lang.util.StringUtil;
import com.yjf.common.lang.util.money.Money;
import com.yjf.yrd.front.controller.trade.base.LoanTradeDetailBaseController;
import com.yjf.yrd.integration.openapi.info.YjfAccountInfo;
import com.yjf.yrd.integration.openapi.result.UseGiftMoneyResult;
import com.yjf.yrd.integration.web.server.YjfLoginWebServer;
import com.yjf.yrd.integration.web.server.impl.YjfEzmoneyCheckPaytkOrder;
import com.yjf.yrd.service.query.giftMoney.GiftMoneyTradeQueryService;
import com.yjf.yrd.session.SessionLocal;
import com.yjf.yrd.session.SessionLocalManager;
import com.yjf.yrd.user.info.UserInfo;
import com.yjf.yrd.util.AppConstantsUtil;
import com.yjf.yrd.util.MoneyUtil;
import com.yjf.yrd.ws.enums.GiftMoneyTypeEnum;
import com.yjf.yrd.ws.info.LoanDemandInfo;
import com.yjf.yrd.ws.info.TradeInfo;
import com.yjf.yrd.ws.order.InvestBizTradeOrder;
import com.yjf.yrd.ws.result.InvestResult;
import com.yjf.yrd.ws.result.YrdBaseResult;
import com.yjf.yrd.ws.service.InvestBizProcessService;

/**
 * @Filename NewInvestController.java
 * 
 * @Description
 * 
 * @Version 2.0
 * 
 * @Author zhaohaibing
 * 
 * @Email abing@yiji.com
 * 
 * @Date: 2014-01-08
 * 
 */
@Controller
@RequestMapping("app")
public class NewInvestController extends LoanTradeDetailBaseController {

	@Autowired
	private InvestBizProcessService investBizProcessService;
	@Autowired
	YjfLoginWebServer yjfLoginWebServer;
	@Autowired
	GiftMoneyTradeQueryService giftMoneyTradeQueryService;

	/**
	 * 6、 定向投资-验证密码
	 * 
	 * @param demandId
	 * @param password
	 * @return
	 * @throws IOException
	 */
	@ResponseBody
	@RequestMapping("checkInvestPassword.htm")
	public JSONObject checkInvestPassword(long demandId, String password)
			throws IOException {
		JSONObject json = new JSONObject();
		if (AppCommonUtil.isLogin()) {
			LoanDemandInfo result = loanDemandQueryService
					.loadLoanDemandInfo(demandId);
			String loanPassWord = result.getLoanPassword();
			if (StringUtil.equals(password, loanPassWord)) {
				json.put("code", 1);
				json.put("message", "投资密码验证成功");
			} else {
				json.put("code", 0);
				json.put("message", "投资密码验证失败");
			}

		} else {
			json.put("code", -1);
			json.put("message", "登录已失效");
		}
		return json;
	}

	/**
	 * 7、 我要投资-获取数据
	 * 
	 * @param projectId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("gotoInvest.htm")
	public JSONObject getInvetMessage(HttpSession session, long demandId,
			Model model) throws IOException {
		JSONObject json = new JSONObject();
		if (AppCommonUtil.isLogin()) {
			TradeInfo result = tradeBizQueryService.getByLoanDemandId(demandId);
			int statues = result.getTradeStatus().getValue();
			double needMoey = MoneyUtil.getMoneyByw(result.getTradeAmount(),
					result.getLoanedAmount()) * 10000;
			if (1 == statues && needMoey > 0) {
				YjfAccountInfo accountInfo = getAccountInfo(model);
				UserInfo userBaseInfo = getUserBaseInfo(session, model);
				String token = UUID.randomUUID().toString();
				session.setAttribute("token", token);
				Map<String, String> map = new HashMap<String, String>();
				map.put("availableBalance", accountInfo.getAvailableBalance()
						.toStandardString());
				map.put("leastInvestAmount", result.getLeastInvestAmount()
						.toStandardString());
				map.put("avalableAmount",
						result.getTradeAmount()
								.subtract(result.getLoanedAmount())
								.toStandardString());
				map.put("increaseAmount", String.valueOf(AppConstantsUtil
						.getIncreasingAmount() / 100));
				map.put("mobile", userBaseInfo.getMobile());
				map.put("viewMobile", AppCommonUtil.viewStr(
						userBaseInfo.getMobile(), "mobile"));
				map.put("token", token);

				json.put("code", 1);
				json.put("message", "获取当前项目投资信息成功");
				json.put("getInvestInfo", AppCommonUtil.cleanNull(map));
				YrdBaseResult baseResult = yjfLoginWebServer
						.gotoYjfValidatePayPasswordUrl(
								AppCommonUtil.getPayUrlOrder(),
								this.getOpenApiContext());
				json.put("yjfPaypassUrl", baseResult.getUrl());
			} else {
				if (1 == statues) {
					json.put("code", 0);
					json.put("message", "投资已满");
				} else {
					json.put("code", 0);
					json.put("message", "未到投资时间或已过期");
				}
			}

		} else {
			json.put("code", -1);
			json.put("message", "登录已失效");
		}
		return json;
	}

	/**
	 * 投资
	 * 
	 * @param projectId
	 * @param tradeId
	 * @param amount
	 * @param token
	 * @param code
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("appInvest.htm")
	public JSONObject invest(HttpSession session, long demandId, long tradeId,
			Money amount, String token, String code, String paytk,
			String giftMoney, String experienceAmount) throws Exception {
		JSONObject json = new JSONObject();
		String getToken = (String) session.getAttribute("token");
		if (!token.equals(getToken)) {
			json.put("code", 0);
			json.put("message", "请勿重复提交！");
			return json;
		}
		session.removeAttribute("token");
		if (!AppCommonUtil.isLogin()) {
			json.put("code", -1);
			json.put("message", "未登录或登录已失效");
			return json;
		}
		// 支付密码验证
		SessionLocal sessionLocal = SessionLocalManager.getSessionLocal();
		YjfEzmoneyCheckPaytkOrder ezmoneyPayPassUrlOrder = new YjfEzmoneyCheckPaytkOrder();
		ezmoneyPayPassUrlOrder.setUserId(sessionLocal.getAccountId());
		ezmoneyPayPassUrlOrder.setPaytk(paytk);
		YrdBaseResult baseResult = yjfLoginWebServer.gotoYjfCheckPayTk(
				ezmoneyPayPassUrlOrder, this.getOpenApiContext());
		if (!baseResult.isSuccess()) {
			json.put("code", 0);
			json.put("message", "支付令牌错误");
			return json;
		}
		InvestBizTradeOrder investBizTradeOrder = new InvestBizTradeOrder();
		investBizTradeOrder.setAccountId(sessionLocal.getAccountId());
		investBizTradeOrder.setUserId(sessionLocal.getUserId());
		investBizTradeOrder.setInvestAmount(amount);
		investBizTradeOrder.setBizNo(tradeId);
		if (StringUtil.isNotEmpty(giftMoney)) {
			investBizTradeOrder.setGiftMoneyAmount(new Money(giftMoney));
		}

		if (StringUtil.isNotEmpty(experienceAmount)) {
			investBizTradeOrder
					.setExperienceAmount(new Money(experienceAmount));
		}
		InvestResult investResult = investBizProcessService
				.invest(investBizTradeOrder);
		if (investResult.isSuccess()) {
			json.put("code", 1);
			json.put("message", "投资成功");
			json.put("investProgress", getInvestProgress(demandId));
			if (investResult.isGiftMoneySuccess()) {
				json.put("giftMoney", 1);
				json.put("giftMoneyMessage", investResult.getGiftMoneyMessage());
			} else {
				json.put("giftMoney", 0);
				json.put("giftMoneyMessage", "无红包信息");
			}

		} else {
			json.put("code", 0);
			json.put("message", investResult.getMessage());
		}
		logger.info("app用户投资结果 :json={}", json);
		return json;
	}

	@ResponseBody
	@RequestMapping("queryUserGiftMoney.htm")
	public JSONObject queryUserGiftMoney(long demandId, String investAmount) {
		JSONObject jsonObject = new JSONObject();
		if (!AppCommonUtil.isLogin()) {
			jsonObject.put("code", -1);
			jsonObject.put("message", "登录失效");
			return jsonObject;
		}
		String giftMoneys = "";
		String giftMoneyLimitAmount = "";
		String experienceAmounts = "";
		long userId = SessionLocalManager.getSessionLocal().getUserId();
		Money giftMoney = giftMoneyTradeQueryService
				.queryGiftMoneyAmountUserCanUse(demandId, investAmount, userId,
						GiftMoneyTypeEnum.GIFT_MONEY);

		UseGiftMoneyResult result = giftMoneyTradeQueryService
				.queryGiftMoneyLimitAmount(demandId, Money.amout(investAmount),
						GiftMoneyTypeEnum.GIFT_MONEY);
		if (result.isSuccess()) {
			Money limitAmount = result.getAmount();
			if ((limitAmount == null && giftMoney.greaterThan(Money.zero()))) {
				giftMoneys = giftMoney.toStandardString();
			}

			if (limitAmount != null && limitAmount.greaterThan(Money.zero())
					&& giftMoney.greaterThan(Money.zero())) {
				giftMoneys = giftMoney.toStandardString();
			}

			if (limitAmount != null && limitAmount.greaterThan(Money.zero())) {
				giftMoneyLimitAmount = limitAmount.toStandardString();
			}

		}

		Money experienceAmount = giftMoneyTradeQueryService
				.queryGiftMoneyAmountUserCanUse(demandId, investAmount, userId,
						GiftMoneyTypeEnum.EXPERIENCE_AMOUNT);
		if (experienceAmount.greaterThan(Money.zero())) {
			experienceAmounts = experienceAmount.toStandardString();
		}
		jsonObject.put("code", 1);
		jsonObject.put("giftMoney", giftMoneys);
		jsonObject.put("giftMoneyLimitAmount", giftMoneyLimitAmount);
		jsonObject.put("experienceAmount", experienceAmounts);
		jsonObject.put("experienceLimitAmount", experienceAmounts);
		jsonObject.put("message", "查询成功");
		return jsonObject;
	}

	/**
	 * @param demandID
	 *            查询投资进度
	 * */
	private String getInvestProgress(long demandID) {
		if (demandID != 0) {
			TradeInfo trade = tradeBizQueryService.getByLoanDemandId(demandID);
			String progress = StringUtil.replace(
					MoneyUtil.getPercentage(trade.getLoanedAmount(),
							trade.getTradeAmount(),
							trade.getLeastInvestAmount()), "%", "");
			return progress;
		}
		return "";
	}

	@Override
	public String getNoTradeView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTradeView() {
		// TODO Auto-generated method stub
		return null;
	}

}
