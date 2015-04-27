package com.yjf.yrd.front.controller.app.newVers.e8Trunck;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.yjf.common.lang.util.StringUtil;
import com.yjf.common.lang.util.money.Money;
import com.yjf.yrd.common.result.SmsCodeResult;
import com.yjf.yrd.front.controller.trade.base.LoanTradeDetailBaseController;
import com.yjf.yrd.integration.openapi.info.YjfAccountInfo;
import com.yjf.yrd.session.SessionLocal;
import com.yjf.yrd.session.SessionLocalManager;
import com.yjf.yrd.user.info.UserInfo;
import com.yjf.yrd.util.AppConstantsUtil;
import com.yjf.yrd.util.MoneyUtil;
import com.yjf.yrd.ws.enums.SmsBizType;
import com.yjf.yrd.ws.info.LoanDemandInfo;
import com.yjf.yrd.ws.info.TradeInfo;
import com.yjf.yrd.ws.order.InvestBizTradeOrder;
import com.yjf.yrd.ws.result.InvestResult;
import com.yjf.yrd.ws.service.InvestBizProcessService;

/**
 * @Filename appNewInvestController.java
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
public class AppNewInvestController extends LoanTradeDetailBaseController {

	@Autowired
	private InvestBizProcessService investBizProcessService;

	/**
	 * 6、 定向投资-验证密码
	 * 
	 * @param projectId
	 * @param password
	 * @return
	 * @throws IOException
	 */
	@ResponseBody
	@RequestMapping("checkInvestPassword.htm")
	public Object checkInvestPassword(String projectId, String password,
			HttpServletResponse response) throws IOException {
		JSONObject json = new JSONObject();
		logger.info("手机用户进入定向投资：校验密码。。。。");
		if (isLogin()) {
			LoanDemandInfo result = loanDemandQueryService
					.loadLoanDemandInfo(Integer.parseInt(projectId));
			String loanPassWord = result.getLoanPassword();
			if (StringUtil.equals(password, loanPassWord)) {
				json.put("result", "1");
				json.put("code", "1");
				json.put("message", "投资密码验证成功");
				logger.info("app用户({}):验证投资密码成功：demandId={}",
						SessionLocalManager.getSessionLocal().getUserName(),
						projectId);
			} else {
				json.put("code", "0");
				json.put("result", "0");
				json.put("message", "投资密码验证失败");
				logger.info("app用户({}):验证投资密码失败：demandId={}",
						SessionLocalManager.getSessionLocal().getUserName(),
						projectId);
			}

		} else {
			json.put("code", "-1");
			json.put("message", "未登录或登录已失效");
			logger.info("aapp用户验证投资密码失败：未登录或当前登录已失效");
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
	public Object getInvetMessage(HttpSession session, long projectId,
			Model model, HttpServletResponse response) throws IOException {
		JSONObject json = new JSONObject();
		logger.info("新版app用户进入投资面。。。。");
		if (isLogin()) {
			TradeInfo result = tradeBizQueryService
					.getByLoanDemandId(projectId);
			int statues = result.getTradeStatus().getValue();
			double needMoey = MoneyUtil.getMoneyByw(result.getTradeAmount(),
					result.getLoanedAmount()) * 10000;
			if (1 == statues && needMoey > 0) {
				YjfAccountInfo accountInfo = getAccountInfo(model);
				UserInfo userBaseInfo = getUserBaseInfo(session, model);
				String token = UUID.randomUUID().toString();
				session.setAttribute("token", token);
				Map<String, String> map = new HashMap<String, String>();
				if ("W".equals(accountInfo.getUserStatus().getCode())) {
					map.put("activeAccount", "0");
				} else {
					map.put("activeAccount", "1");
				}
				map.put("availableMoney", accountInfo.getAvailableBalance()
						.toStandardString());
				map.put("startMoney", result.getLeastInvestAmount()
						.toStandardString());
				map.put("residueMoney",
						result.getTradeAmount()
								.subtract(result.getLoanedAmount())
								.toStandardString());
				map.put("increaseMoney", String.valueOf(AppConstantsUtil
						.getIncreasingAmount() / 100));
				map.put("phone", userBaseInfo.getMobile());
				map.put("token", token);
				json.put("code", "1");
				json.put("message", "获取当前项目投资信息成功");
				json.put("getInvestInfo", map);
				logger.info("新版app用户获取投资信息：investInfo={}", map);
			} else {
				if (1 == statues) {
					json.put("code", "0");
					json.put("message", "当前项目不可投资：投资已满");
					logger.info(
							"新版app用户({}):获取投资信息：investInfo={}",
							SessionLocalManager.getSessionLocal().getUserName(),
							"当前项目不可投资:投资已满");
				} else {
					json.put("code", "0");
					json.put("message", "当前项目不可投资：未到投资时间或已过期");
					logger.info(
							"新版app用户{}:获取投资信息：investInfo={}",
							SessionLocalManager.getSessionLocal().getUserName(),
							"当前项目不可投资：未到投资时间或已过期");
				}
			}

		} else {
			json.put("code", "-1");
			json.put("message", "未登录或登录已失效");
			logger.info("新版app用户获取项目投资信息失败：未登录或当前登录已失效");
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
	public JSONObject invest(HttpSession session, Long projectId, Long tradeId,
			Money amount, String token, String code, String giftMoney,
			String experienceAmount, HttpServletResponse response)
			throws Exception {
		JSONObject json = new JSONObject();
		SessionLocal sessionLocal = SessionLocalManager.getSessionLocal();
		String getToken = (String) session.getAttribute("token");
		logger.info("手机用户：user={}正在投资。。。。", SessionLocalManager
				.getSessionLocal().getUserName());
		UserInfo userBaseInfo = userQueryService.queryByUserBaseId(
				SessionLocalManager.getSessionLocal().getUserBaseId())
				.getQueryUserInfo();
		SmsCodeResult smsCodeResult = smsManagerService.verifySmsCode(
				userBaseInfo.getMobile(), SmsBizType.INVEST, code, true);
		if (token.equals(getToken) && smsCodeResult.isSuccess()) {
			session.removeAttribute("token");
			InvestBizTradeOrder investBizTradeOrder = new InvestBizTradeOrder();
			investBizTradeOrder.setAccountId(sessionLocal.getAccountId());
			investBizTradeOrder.setUserId(sessionLocal.getUserId());
			investBizTradeOrder.setInvestAmount(amount);
			investBizTradeOrder.setBizNo(tradeId);
			if (StringUtil.isNotEmpty(giftMoney)) {
				investBizTradeOrder.setGiftMoneyAmount(new Money(giftMoney));
			}

			if (StringUtil.isNotEmpty(experienceAmount)) {
				investBizTradeOrder.setExperienceAmount(new Money(
						experienceAmount));
			}
			InvestResult baseResult = investBizProcessService
					.invest(investBizTradeOrder);
			logger.info("baseResult 投资结束{}", baseResult);
			if (baseResult.isSuccess()) {
				json.put("code", "1");
				json.put("message", "投资成功");
				json.put("investProgress", getInvestProgress(projectId));
				if (baseResult.isGiftMoneySuccess()) {
					session.setAttribute("giftMoney",
							baseResult.getGiftMoneyMessage());
				}
			} else {
				json.put("code", "0");
				json.put("message", baseResult.getMessage());

			}

		} else {
			if (!token.equals(getToken)) {
				json.put("code", "0");
				json.put("message", "投资失败，重复提交！");
				logger.info("app用户({}):投资失败，重复提交！", SessionLocalManager
						.getSessionLocal().getUserName());
			} else if (!isLogin()) {
				json.put("code", "0");
				json.put("message", "投资失败：未登录或登录已失效");
				logger.info("app用户投资失败，未登录或登录已失 效！");
			} else if (!smsCodeResult.isSuccess()) {
				json.put("code", "0");
				json.put("message", "投资失败：短信验证码错误！");
				logger.info("app用户({}):投资失败：短信验证码错误！", SessionLocalManager
						.getSessionLocal().getUserName());
			}

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

	/**
	 * 校验投资
	 * 
	 * @param tradeId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "checkinvest/{tradeId}", method = RequestMethod.POST)
	public Object checkInvest(@PathVariable long tradeId) {
		JSONObject json = new JSONObject();
		json.put("status", true);
		return json;
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
		return null;
	}

	@ResponseBody
	@RequestMapping(value = "isInvest")
	public Object isInvest(long demandId) {
		JSONObject json = new JSONObject();
		json.put("status", true);
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
