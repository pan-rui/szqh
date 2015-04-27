package com.yjf.yrd.front.controller.business.withdrawals;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.yjf.yrd.integration.bornapi.order.BornWithdrawOrder;
import com.yjf.yrd.util.AppConstantsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.yjf.yrd.base.UserAccountInfoBaseController;
import com.yjf.yrd.integration.openapi.enums.UserStatusEnum;
import com.yjf.yrd.integration.openapi.info.YjfAccountInfo;
import com.yjf.yrd.session.SessionLocalManager;
import com.yjf.yrd.trade.WithdrawYrdService;
import com.yjf.yrd.trade.order.PPMWithdrawOrder;
import com.yjf.yrd.user.info.UserInfo;
import com.yjf.yrd.ws.enums.RealNameAuthStatusEnum;
import com.yjf.yrd.ws.enums.UserStateEnum;
import com.yjf.yrd.ws.result.YrdBaseResult;

@Controller
@RequestMapping("withdrawals")
public class WithdrawalsController extends UserAccountInfoBaseController {
	
	private final String VM_PATH = "/front/withdrawals/";
	
	@Autowired
	WithdrawYrdService withdrawYrdService;
	
	@RequestMapping("launchWithdrawals")
	public String launchWithdrawals(HttpSession session, Model model, HttpServletResponse response)
																									throws Exception {
		this.initAccountInfo(model);
		session.setAttribute("current", 1);
		String userBaseId = SessionLocalManager.getSessionLocal().getUserBaseId();
		UserInfo userBaseInfo = userQueryService.queryByUserBaseId(userBaseId).getQueryUserInfo();
		YjfAccountInfo accountInfo = this.getAccountInfo(model);
        if (userBaseInfo.getRealNameAuthentication()  == null) {
            model.addAttribute("userType", userBaseInfo.getType());
            model.addAttribute("fail", "亲，请实名认证！");
            return VM_PATH + "launchWithdrawals.vm";
        }
		if (accountInfo.getUserStatus() == UserStatusEnum.UNACTIVATED) {
            model.addAttribute("userType", userBaseInfo.getType());
              model.addAttribute("fail", "亲，支付账户未激活！");
			model.addAttribute("noInactive", "支付账户未激活！");
			return VM_PATH + "launchWithdrawals.vm";
		}
		if (userBaseInfo.getRealNameAuthentication() != RealNameAuthStatusEnum.IS) {
			model.addAttribute("userType", userBaseInfo.getType());
			model.addAttribute("fail", "实名认证未通过！");
			return VM_PATH + "launchWithdrawals.vm";
		}
		if (userBaseInfo.getRealNameAuthentication() == RealNameAuthStatusEnum.IN) {
			model.addAttribute("userType", userBaseInfo.getType());
			model.addAttribute("isRealNameAuth", "实名审核中！");
			return VM_PATH + "launchWithdrawals.vm";
		}
		if (userBaseInfo.getState() != UserStateEnum.NORMAL) {
			model.addAttribute("fail", "帐户状态异常,无法提现申请！");
			return VM_PATH + "launchWithdrawals.vm";
		}
		
		if (userBaseInfo.getRealNameAuthentication() == RealNameAuthStatusEnum.IS && SessionLocalManager.getSessionLocal().getCertifyLevel()==1) {
			model.addAttribute("userType", userBaseInfo.getType());
			model.addAttribute("certifyLevel", SessionLocalManager.getSessionLocal().getCertifyLevel());
			model.addAttribute("fail", "请先进行强实名！");
			return VM_PATH + "launchWithdrawals.vm";
		}
        YrdBaseResult withdrawResult;
        if (AppConstantsUtil.withdrawGoBornUrl()) {
            BornWithdrawOrder bornWithdrawOrder = new BornWithdrawOrder();
            bornWithdrawOrder.setUserId(userBaseInfo.getAccountId());
            withdrawResult = withdrawYrdService.applyBornWithdraw(bornWithdrawOrder);
        } else {
            PPMWithdrawOrder order = new PPMWithdrawOrder();
            order.setUserId(userBaseInfo.getUserId());
            withdrawResult = withdrawYrdService.applyPPMWithdraw(order);
        }

		model.addAttribute("withdrawUrl", withdrawResult.getUrl());
		return VM_PATH + "launchWithdrawals.vm";
	}
	
}
