package com.yjf.yrd.web.util;

import java.math.BigDecimal;

import com.yjf.yrd.dataobject.Trade;
import com.yjf.yrd.util.YrdConstants;
import com.yjf.yrd.ws.enums.LoanPeriodUnitEnum;

public class RateProcessorUtil {
	
	//按天计算利率
	public static double getDaysRuleRate(double rule, Trade trade) {
		String timeLimitUnit = trade.getTimeLimitUnit();
		double timeLimit = trade.getTimeLimit();
		double days = 0;
		if (LoanPeriodUnitEnum.LOAN_BY_DAY.code().equals(timeLimitUnit)) {
			days = timeLimit;
		} else if (LoanPeriodUnitEnum.LOAN_BY_YEAR.code().equals(timeLimitUnit)) {
			days = timeLimit * YrdConstants.TimeRelativeConstants.DAYSOFAYEAR;
		} else {
			days = Math.round(timeLimit * YrdConstants.TimeRelativeConstants.DAYSOFAYEAR / 12);
		}
		BigDecimal bg = new BigDecimal(rule / YrdConstants.TimeRelativeConstants.DAYSOFAYEAR * days);
		double daysRate = bg.setScale(10, BigDecimal.ROUND_HALF_UP).doubleValue();
		return daysRate;
	}
}
