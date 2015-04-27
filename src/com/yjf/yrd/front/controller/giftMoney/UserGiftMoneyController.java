package com.yjf.yrd.front.controller.giftMoney;

import com.yjf.yrd.ws.enums.GiftMoneyTradeTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.yjf.yrd.base.UserAccountInfoBaseController;
import com.yjf.yrd.page.PageParam;
import com.yjf.yrd.service.query.giftMoney.GiftMoneyTradeQueryService;
import com.yjf.yrd.session.SessionLocal;
import com.yjf.yrd.session.SessionLocalManager;
import com.yjf.yrd.web.util.PageUtil;
import com.yjf.yrd.ws.info.GiftMoneyTradeInfo;
import com.yjf.yrd.ws.service.query.order.GiftMoneyTradeQueryOrder;
import com.yjf.yrd.ws.service.query.result.QueryBaseBatchResult;

/**
 * Created by min on 2014/12/2.
 */
@Controller
@RequestMapping("userGiftMoney")
public class UserGiftMoneyController extends UserAccountInfoBaseController {
	@Autowired
	GiftMoneyTradeQueryService giftMoneyTradeQueryService;
	
	/** 领取
	 * 分页查询
	 */
	@RequestMapping(value = "pageQuery")
	public String pageQueryUserGiftMoneyInfo(GiftMoneyTradeQueryOrder queryOrder,
												PageParam pageParam, Model model) {
		try {
			initAccountInfo(model);
			SessionLocal sessionLocal = SessionLocalManager.getSessionLocal();
			queryOrder.setPageNumber(pageParam.getPageNo());
			queryOrder.setPageSize(pageParam.getPageSize());
            queryOrder.setTradeType(GiftMoneyTradeTypeEnum.ORIGINAL.getCode());
			queryOrder.setUserid(sessionLocal.getUserId());
			QueryBaseBatchResult<GiftMoneyTradeInfo> page = giftMoneyTradeQueryService
				.queryGiftMoneyTrade(queryOrder);
			model.addAttribute("queryConditions", queryOrder);
			model.addAttribute("page", PageUtil.getCovertPage(page));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return "/front/giftMoney/pageQueryUserGiftMoneyInfo.vm";
	}

    /** 领取
     * 分页查询
     */
    @RequestMapping(value = "pageQueryUse")
    public String pageQueryUserUseGiftMoneyInfo(GiftMoneyTradeQueryOrder queryOrder,
                                             PageParam pageParam, Model model) {
        try {
            initAccountInfo(model);
            SessionLocal sessionLocal = SessionLocalManager.getSessionLocal();
            queryOrder.setPageNumber(pageParam.getPageNo());
            queryOrder.setPageSize(pageParam.getPageSize());
            queryOrder.setTradeType(GiftMoneyTradeTypeEnum.USED.getCode());
            queryOrder.setUserid(sessionLocal.getUserId());
            QueryBaseBatchResult<GiftMoneyTradeInfo> page = giftMoneyTradeQueryService
                    .queryGiftMoneyTrade(queryOrder);
            model.addAttribute("queryConditions", queryOrder);
            model.addAttribute("page", PageUtil.getCovertPage(page));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return "/front/giftMoney/pageQueryUserUseGiftMoneyInfo.vm";
    }
	
}
