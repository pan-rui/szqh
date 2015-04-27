package com.yjf.yrd.web.util;

import java.math.BigDecimal;

import com.yjf.yrd.dataobject.Trade;
import com.yjf.yrd.util.MoneyUtil;
import com.yjf.yrd.util.YrdConstants;
import com.yjf.yrd.ws.enums.DivisionPhaseEnum;
import com.yjf.yrd.ws.enums.TradeFullConditionEnum;
import com.yjf.yrd.ws.enums.TradeStatusEnum;
import com.yjf.yrd.ws.info.TradeDetailInfo;
import com.yjf.yrd.ws.info.TradeInfo;

/**
 * 
 * 
 * @Filename TradeUtil.java
 * 
 * @Description
 * 
 * @Version 1.0
 * 
 * @Author yhl
 * 
 * @Email yhailong@yiji.com
 * 
 * @History <li>Author: yhl<> <li>Date: 2013-9-2<> <li>Version: 1.0<> <li>
 * Content: create<>
 * 
 */
public class TradeUtil {
	
	/**
	 * 获取显示属性
	 * @param method
	 * @param value
	 * @return
	 */
	public static String getSaturationCondition(TradeFullConditionEnum method, String value) {
		if (method.equals(TradeFullConditionEnum.AMOUNT)) {
			return "金额达到" + MoneyUtil.getFormatAmount(Long.parseLong(value)) + "元";
		} else if (method.equals(TradeFullConditionEnum.PERCENTAGE)) {
			BigDecimal bg = new BigDecimal(Double.parseDouble(value) * 100);
			double d = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			return "投资金额比例达到" + d + "%";
		} else {
			return "截止时间至" + value;
		}
	}
	
	/**
	 * 获取显示状态
	 * @param trade
	 * @return
	 */
	public static String getNormalTradeStatus(Trade trade) {
		String tradeStatus = YrdConstants.TradeViewCanstants.TRADE_DEFAULT;
		switch (trade.getStatus()) {
			case YrdConstants.TradeStatus.TRADING:
				tradeStatus = YrdConstants.TradeViewCanstants.TRADE_TRADING;
				break;
			case YrdConstants.TradeStatus.FAILED:
				tradeStatus = YrdConstants.TradeViewCanstants.TRADE_FAILD;
				break;
			case YrdConstants.TradeStatus.REPAY_FINISH:
				tradeStatus = YrdConstants.TradeViewCanstants.TRADE_FINISH;
				break;
			case YrdConstants.TradeStatus.REPAYING_FAILD:
				tradeStatus = YrdConstants.TradeViewCanstants.TRADE_REPAY_FAILD;
				break;
			case YrdConstants.TradeStatus.REPAYING:
				tradeStatus = YrdConstants.TradeViewCanstants.TRADE_REPAYING;
				break;
			case YrdConstants.TradeStatus.GUARANTEE_AUDITING:
				tradeStatus = YrdConstants.TradeViewCanstants.GUARANTEE_AUDITING;
				break;
			case YrdConstants.TradeStatus.DOREPAY:
				tradeStatus = YrdConstants.TradeViewCanstants.DOREPAY;
				break;
			case YrdConstants.TradeStatus.COMPENSATORY_REPAY_FINISH:
				tradeStatus = YrdConstants.TradeViewCanstants.COMPENSATORY_REPAY_FINISH;
				break;
		}
		return tradeStatus;
	}
	
	public static String getRepayStatus(TradeInfo trade, TradeDetailInfo detail) {
		String repayStatus = YrdConstants.TradeViewCanstants.TRADE_DEFAULT;
		if (trade.getTradeStatus() == TradeStatusEnum.REPAYING) {
			if (detail.getTransferPhase() == DivisionPhaseEnum.INVESET_PHASE) {
				repayStatus = YrdConstants.TradeViewCanstants.TRADE_PAID;
			} else if (detail.getTransferPhase() == DivisionPhaseEnum.REPAY_PHASE) {
				repayStatus = YrdConstants.TradeViewCanstants.TRADE_NOT_PAID;
			}
		} else if (trade.getTradeStatus() == TradeStatusEnum.REPAYING_FAILD) {
			if (detail.getTransferPhase() == DivisionPhaseEnum.INVESET_PHASE) {
				repayStatus = YrdConstants.TradeViewCanstants.TRADE_PAID;
			} else if (detail.getTransferPhase() == DivisionPhaseEnum.REPAY_PHASE) {
				repayStatus = YrdConstants.TradeViewCanstants.TRADE_NOT_PAID;
			}
		} else if (trade.getTradeStatus() == TradeStatusEnum.DOREPAY) {
			if (detail.getTransferPhase() == DivisionPhaseEnum.INVESET_PHASE) {
				repayStatus = YrdConstants.TradeViewCanstants.TRADE_PAID;
			} else if (detail.getTransferPhase() == DivisionPhaseEnum.REPAY_PHASE) {
				repayStatus = YrdConstants.TradeViewCanstants.TRADE_NOT_PAID;
			}
		} else {
			repayStatus = trade.getTradeStatus().getMessage();
		}
		return repayStatus;
	}
}
