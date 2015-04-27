package com.yjf.yrd.front.controller.trade.invest;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.yjf.yrd.util.StringUtil;
import com.yjf.yrd.ws.result.InvestResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.yjf.common.lang.util.money.Money;
import com.yjf.yrd.common.result.SmsCodeResult;
import com.yjf.yrd.front.controller.trade.base.LoanTradeDetailBaseController;
import com.yjf.yrd.integration.web.server.YjfLoginWebServer;
import com.yjf.yrd.integration.web.server.impl.YjfEzmoneyCheckPaytkOrder;
import com.yjf.yrd.page.PageParam;
import com.yjf.yrd.session.SessionLocal;
import com.yjf.yrd.session.SessionLocalManager;
import com.yjf.yrd.util.AppConstantsUtil;
import com.yjf.yrd.ws.enums.SmsBizType;
import com.yjf.yrd.ws.enums.SysUserRoleEnum;
import com.yjf.yrd.ws.order.InvestBizTradeOrder;
import com.yjf.yrd.ws.result.YrdBaseResult;
import com.yjf.yrd.ws.service.InvestBizProcessService;

/**
 * 
 * 
 * @Filename InvestController.java
 * 
 * @Description
 * 
 * @Version 1.0
 * 
 * @Author yhl
 * 
 * @Email yhailong@yiji.com
 * 
 * @History <li>Author: yhl</li> <li>Date: 2013-7-4</li> <li>Version: 1.0</li>
 * <li>Content: create</li>
 * 
 */
@Controller
@RequestMapping("invest")
public class InvestController extends LoanTradeDetailBaseController {
	
	@Autowired
	private InvestBizProcessService investBizProcessService;
	
    @Autowired
    YjfLoginWebServer yjfLoginWebServer;
	
	/**
	 * 投资
	 * @param tradeId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "invest", method = RequestMethod.POST)
	public Object invest(Long tradeId, Money amount, String password, Long demandId, String token,
							String mobile, String smsCode, String business,String paytk,String giftMoney,String experienceAmount, HttpSession session) {
		SessionLocal sessionLocal = SessionLocalManager.getSessionLocal();
		String getToken = (String) session.getAttribute("token");
		Map<String, Object> result = new HashMap<String, Object>();
		if (token.equals(getToken)) {
            if(AppConstantsUtil.canTradeUsePayPassword()){
                YjfEzmoneyCheckPaytkOrder ezmoneyPayPassUrlOrder = new YjfEzmoneyCheckPaytkOrder();
                ezmoneyPayPassUrlOrder.setUserId(sessionLocal.getAccountId());
                ezmoneyPayPassUrlOrder.setPaytk(paytk);
                YrdBaseResult paytkResult = yjfLoginWebServer.gotoYjfCheckPayTk(
                        ezmoneyPayPassUrlOrder, this.getOpenApiContext());
                if (!paytkResult.isSuccess()) {
                    result.put("status", false);
                    result.put("message", "支付令牌错误");
                    return result;
                }
            }else{
                SmsCodeResult smsCodeResult = this.smsManagerService.verifySmsCode(mobile,
                        SmsBizType.getByCode(business), smsCode, true);
                if (!smsCodeResult.isSuccess()) {
                    result.put("status", false);
					result.put("message", smsCodeResult.getMessage());
                    return result;
                }
            }
			session.removeAttribute("token");
			try {
				InvestBizTradeOrder investBizTradeOrder = new InvestBizTradeOrder();
				investBizTradeOrder.setAccountId(sessionLocal.getAccountId());
				investBizTradeOrder.setUserId(sessionLocal.getUserId());
				investBizTradeOrder.setInvestAmount(amount);
				investBizTradeOrder.setBizNo(tradeId);
                if(StringUtil.isNotEmpty(giftMoney)){
                    investBizTradeOrder.setGiftMoneyAmount(new Money(giftMoney));
                }

                if(StringUtil.isNotEmpty(experienceAmount)){
                    investBizTradeOrder.setExperienceAmount(new Money(experienceAmount));
                }

                InvestResult baseResult = investBizProcessService.invest(investBizTradeOrder);
				logger.info("baseResult 投资结束{}", baseResult);
				if (baseResult.isSuccess()) {
					result.put("status", true);
					result.put("message", "投资成功");
                    if(baseResult.isGiftMoneySuccess()){
                        session.setAttribute("giftMoney",baseResult.getGiftMoneyMessage());
                    }


				} else {
					result.put("status", false);
					result.put("message", baseResult.getMessage());	
				}
				
			} catch (Exception e) {
				logger.error("处理投资失败！投资人id：{}" + sessionLocal.getUserId(), e);
				result.put("status", false);
				result.put("message", "投资发生异常");
			}
		} else {
			result.put("message", "投资失败，重复提交！");
		}
		return result;
	}
	
	/**
	 * 校验是否可以投资
	 * @param tradeId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "checktrade/{tradeId}", method = RequestMethod.POST)
	public Object checkTrade(@PathVariable long tradeId) {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("status", true);
		
		return result;
	}
	
	/**
	 * 校验支付密码
	 * @param password
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "checkpaypassword", method = RequestMethod.POST)
	public Object checkPayPassword(String password) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("status", true);
		
		return result;
	}
	
	/**
	 * 校验支付金额
	 * @param amount
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "checkamount/{tradeId}", method = RequestMethod.POST)
	public Object checkAmount(@PathVariable long tradeId, Money amount) {
		
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("status", true);
		
		return result;
	}

    /**
     * 查看借款需求详情
     * @param demandId
     * @param tradeId
     * @param session
     * @param model
     * @param request
     * @param pageParam
     * @return
     * @throws Exception
     */
	@RequestMapping("lookup/{demandId},{tradeId}")
	public String lookup(@PathVariable long demandId, @PathVariable long tradeId,
							HttpSession session, Model model, HttpServletRequest request,
							PageParam pageParam) throws Exception {
		String token = UUID.randomUUID().toString();
		getTradeDetailPageView(demandId, 0, model, session, SysUserRoleEnum.INVESTOR, request,
			pageParam);
		session.setAttribute("token", token);
		return "front/index/index_invest_detail.vm";
	}
	
	@Override
	public String getNoTradeView() {
		return "front/index/index_invest_detail.vm";
	}
	
	@Override
	public String getTradeView() {
		return "front/index/index_invest_detail.vm";
	}
	
	/**
	 * 校验投资
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
	
	@ResponseBody
	@RequestMapping(value = "isInvest")
	public Object isInvest(long demandId) {
		JSONObject json = new JSONObject();
		json.put("status", true);
		return json;
		
	}
	
	
	
}
