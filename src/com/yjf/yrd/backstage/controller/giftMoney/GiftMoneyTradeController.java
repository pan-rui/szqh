package com.yjf.yrd.backstage.controller.giftMoney;

import com.yjf.yrd.base.BaseAutowiredController;
import com.yjf.yrd.page.PageParam;
import com.yjf.yrd.service.query.giftMoney.GiftMoneyTradeQueryService;
import com.yjf.yrd.util.StringUtil;
import com.yjf.yrd.web.util.PageUtil;
import com.yjf.yrd.ws.enums.GiftMoneyTradeTypeEnum;
import com.yjf.yrd.ws.info.GiftMoneyTradeInfo;
import com.yjf.yrd.ws.service.query.order.GiftMoneyTradeQueryOrder;
import com.yjf.yrd.ws.service.query.result.QueryBaseBatchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("backstage/giftMoney")
public class GiftMoneyTradeController extends BaseAutowiredController {
    /**
     * 页面所在路径
     */
    private final String BORROWING_MANAGE__PATH = "/backstage/giftMoney/";

    @Autowired
    GiftMoneyTradeQueryService giftMoneyTradeQueryService;

    /**
     * 领取
     * 分页查询
     */
    @RequestMapping(value = "pageQueryGiftMoneyTrade")
    public String pageQueryGiftMoneyTrade(GiftMoneyTradeQueryOrder queryOrder,
                                     PageParam pageParam, Model model) {
        try {
            queryOrder.setPageNumber(pageParam.getPageNo());
            queryOrder.setPageSize(pageParam.getPageSize());
            queryOrder.setTradeType(GiftMoneyTradeTypeEnum.ORIGINAL.getCode());
            String username = null;
            if(StringUtil.isNotEmpty(queryOrder.getUsername())){
                username = queryOrder.getUsername();
                queryOrder.setUsername("%"+queryOrder.getUsername()+"%");
            }
            QueryBaseBatchResult<GiftMoneyTradeInfo> page = giftMoneyTradeQueryService
                    .queryGiftMoneyTrade(queryOrder);
            queryOrder.setUsername(username);
            model.addAttribute("queryConditions", queryOrder);
            model.addAttribute("page", PageUtil.getCovertPage(page));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return BORROWING_MANAGE__PATH + "pageQueryGiftMoneyTradeInfo.vm";
    }


    /**
     * 使用领取
     * 分页查询
     */
    @RequestMapping(value = "pageQueryUseGiftMoneyTrade")
    public String pageQueryUseGiftMoneyTrade(GiftMoneyTradeQueryOrder queryOrder,
                                          PageParam pageParam, Model model) {
        try {
            queryOrder.setPageNumber(pageParam.getPageNo());
            queryOrder.setPageSize(pageParam.getPageSize());
            String username = null;
            if(StringUtil.isNotEmpty(queryOrder.getUsername())){
                username = queryOrder.getUsername();
                queryOrder.setUsername("%"+queryOrder.getUsername()+"%");
            }
            queryOrder.setTradeType(GiftMoneyTradeTypeEnum.USED.getCode());
            QueryBaseBatchResult<GiftMoneyTradeInfo> page = giftMoneyTradeQueryService
                    .queryGiftMoneyTrade(queryOrder);
            queryOrder.setUsername(username);
            model.addAttribute("queryConditions", queryOrder);
            model.addAttribute("page", PageUtil.getCovertPage(page));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return BORROWING_MANAGE__PATH + "pageQueryUseGiftMoneyTradeInfo.vm";
    }


}
