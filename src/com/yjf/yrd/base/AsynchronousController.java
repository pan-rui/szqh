package com.yjf.yrd.base;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import com.yjf.common.log.Logger;
import com.yjf.common.log.LoggerFactory;
import com.yjf.yrd.common.services.order.SystemSendMessageOrder;
import com.yjf.yrd.ws.enums.ExtPayTypeEnum;
import com.yjf.yrd.ws.enums.GiftMoneyTypeEnum;
import com.yjf.yrd.ws.enums.SysSendMessageTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yjf.common.lang.util.StringUtil;
import com.yjf.common.lang.util.money.Money;
import com.yjf.yrd.service.recharge.DepositFlowService;
import com.yjf.yrd.service.recharge.WithdrawFlowService;
import com.yjf.yrd.service.recharge.order.DepositFlowOrder;
import com.yjf.yrd.service.recharge.order.WithdrawFlowOrder;
import com.yjf.yrd.user.order.UpdateRealNameStatusOrder;
import com.yjf.yrd.ws.info.RechargeFlowInfo;
import com.yjf.yrd.ws.result.YrdBaseResult;

/**
 * 调用易极付相关接口的异步返回方法类
 * 
 * @author Joe
 * 
 */

@Controller
@RequestMapping("asynchronous")
public class AsynchronousController extends BaseAutowiredController {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	DepositFlowService depositFlowService;
	@Autowired
	WithdrawFlowService withdrawFlowService;
	
	/**
	 * 异步返回提现结果
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping("asysGetWithdrawalRsult")
	public String sysGetWithdrawalRsult(HttpServletRequest request) {
		String strReturn = "";
		try {
			String payType = request.getParameter("payType");
			String amount = request.getParameter("amount");
			String outBizNo = request.getParameter("outBizNo");
			String success = request.getParameter("success");
			logger.info("提现异步返回结果:payType=" + payType + ",amount=" + amount + ",outBizNo="
						+ outBizNo + ",isSuccess=" + success);
			
			WithdrawFlowOrder depositFlowOrder = new WithdrawFlowOrder();
			depositFlowOrder.setAmount(new Money(amount));
			depositFlowOrder.setOutBizNo(outBizNo);
			RechargeFlowInfo rechargeFlow = withdrawFlowService.queryByOutBizNo(outBizNo);
			if ("true".equals(success)) {
				depositFlowOrder.setStatus(1);
				logger.info("提现成功");
			} else {
				depositFlowOrder.setStatus(0);
				logger.info("提现失败");
			}
			int flow = withdrawFlowService.update(depositFlowOrder);
			if (flow >= 0) {
				strReturn = "success";
			} else {
				strReturn = "";
				logger.error("用户:" + rechargeFlow.getOutUserId() + "的" + rechargeFlow.getOutBizNo()
								+ "划出更新状态失败");
			}
		} catch (Exception e) {
			logger.error("提现异步返回时,参数异常", e);
		}
		return strReturn;
	}
	
	@ResponseBody
	@RequestMapping("asysGetWithdrawalRsultByToBank")
	public String asysGetWithdrawalRsultByToBank(HttpServletRequest request) {
		String strReturn = "";
		int status = 0;
		try {
			String payType = request.getParameter("payType");
			String amount = request.getParameter("amount");
			String outBizNo = request.getParameter("outBizNo");
			String success = request.getParameter("success");
			logger.info("提现异步返回结果:payType=" + payType + ",amount=" + amount + ",outBizNo="
						+ outBizNo + ",isSuccess=" + success);
			RechargeFlowInfo rechargeFlow = rechargeFlowService.queryByOutBizNo(outBizNo);
			if ("true".equals(success)) {
				status = 1;
				rechargeFlow.setStatus(1);
				logger.info("提现成功");
			} else {
				status = 0;
				rechargeFlow.setStatus(0);
				logger.info("提现失败");
			}
			int flow = rechargeFlowService.update(rechargeFlow);
			strReturn = "success";
			if (flow <= 0) {
				strReturn = "";
				logger.error("用户:" + rechargeFlow.getOutUserId() + "的" + rechargeFlow.getOutBizNo()
								+ "划出更新状态失败");
			}
			
		} catch (Exception e) {
			logger.error("提现异步返回时,参数异常", e);
		}
		return strReturn;
	}
	
	/**
	 * 异步返回充值结果
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("asysGetDeductResult")
	public String asysGetDeductResult(HttpServletRequest request) {
		String strReturn = "";
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			Date notifyTime = null;
			try {
				notifyTime = sdf.parse(request.getParameter("notifyTime"));
			} catch (Exception e) {
				logger.error("e==" + e.getMessage(), e);
				notifyTime = new Date();
			}
			
			String signType = request.getParameter("signType");
			String sign = request.getParameter("sign");
			String resultCode = request.getParameter("resultCode");
			String resultMessage = request.getParameter("resultMessage");
			String payNo = request.getParameter("payNo");
			String outBizNo = request.getParameter("outBizNo");
			String amount = request.getParameter("amount");
			String amountIn = request.getParameter("amountIn");
			String success = request.getParameter("success");
			String message = request.getParameter("message");
			logger.info("充值异步返回结果信息:notifyTime=" + notifyTime + ",signType=" + signType + ",sign="
						+ sign + ",resultCode=" + resultCode + ",resultMessage=" + resultMessage
						+ ",payNo=" + payNo + ",outBizNo=" + outBizNo + ",amount=" + amount
						+ ",success=" + success + ",message=" + message);
			if (StringUtil.equals("success", success) || StringUtil.equals("true", success)) {
				Money chargeAmount = new Money();
				Money depositAmountMoney = new Money(amount);
				if (StringUtil.isNotEmpty(amountIn)) {
					chargeAmount = depositAmountMoney.subtract(new Money(amountIn));
				}
				DepositFlowOrder depositFlowOrder = new DepositFlowOrder();
				depositFlowOrder.setAmount(new Money(amount));
				depositFlowOrder.setAmountCharge(chargeAmount);
				depositFlowOrder.setOutBizNo(outBizNo);
				depositFlowOrder.setStatus(1);
				depositFlowService.update(depositFlowOrder);


			} else {
				Money chargeAmount = new Money();
				Money depositAmountMoney = new Money(amount);
				if (StringUtil.isNotEmpty(amountIn)) {
					chargeAmount = depositAmountMoney.subtract(new Money(amountIn));
				}
				DepositFlowOrder depositFlowOrder = new DepositFlowOrder();
				depositFlowOrder.setAmount(new Money(amount));
				depositFlowOrder.setAmountCharge(chargeAmount);
				depositFlowOrder.setOutBizNo(outBizNo);
				depositFlowOrder.setStatus(0);
				depositFlowService.update(depositFlowOrder);
			}
			strReturn = "success";
			
		} catch (Exception e) {
			logger.error("充值异步调用方法参数或网络异常", e);
		}
		return strReturn;
	}
	
	/**
	 * 异步调用转账到卡结果
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("asysGetTransferBankAccountResult")
	public String asysGetTransferBankAccountResult(HttpServletRequest request) {
		String strReturn = "";
		try {
			String sign = request.getParameter("sign");
			String notifyTime = request.getParameter("notifyTime");
			String tradeSimpleInfos = request.getParameter("tradeSimpleInfos");
			logger.info("转账到卡异步调用方法返回参数:sign=" + sign + ",notifyTime=" + notifyTime
						+ ",tradeSimpleInfos" + tradeSimpleInfos);
			strReturn = "success";
		} catch (Exception e) {
			logger.error("转账到卡异步调用方法参数或网络异常", e);
		}
		return strReturn;
	}
	
	/**
	 * 异步返回实名结果
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("asysGetRealNameStatus")
	public String asysGetRealNameStatus(HttpServletRequest request) {
		String strReturn = " ";
		
		try {
			String status = StringUtil.trim(request.getParameter("status"));
			String accountId = StringUtil.trim(request.getParameter("userId"));
			String message = request.getParameter("message");
			String authNo = StringUtil.trim(request.getParameter("authNo"));
			logger.info("实名异步接收到的参数信息,status=" + status + ",accountId=" + accountId + ",authNo="
						+ authNo + ",message=" + message);
			UpdateRealNameStatusOrder realNameStatusOrder = new UpdateRealNameStatusOrder();
			realNameStatusOrder.setAccountId(accountId);
			realNameStatusOrder.setMessage(message);
			if ("precess".equals(status)) {
				strReturn = "success";
			} else {
				realNameStatusOrder.setYjfReturnStatus(status);
				YrdBaseResult baseResult = userBaseInfoManager
					.updateRealNameStatus(realNameStatusOrder);
				if (baseResult.isSuccess())
					strReturn = "success";
				else {
					strReturn = "{error:'" + baseResult.getMessage() + "'}";
				}
			}
			
		} catch (Exception e) {
			logger.error("异常获取参数异常", e);
		}
		return strReturn;
	}
	
	public static void main(String[] args) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		try {
			sdf.parse("2014-05-26 18:28");
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
}
