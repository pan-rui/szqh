package com.yjf.yrd.backstage.controller.lotteryActivity;

import com.yjf.yrd.base.BaseAutowiredController;
import com.yjf.yrd.service.query.lottery.PrizeRuleQueryService;
import com.yjf.yrd.web.util.PageUtil;
import com.yjf.yrd.ws.marketing.info.LotteryActivityInfo;
import com.yjf.yrd.ws.marketing.info.PrizeRuleInfo;
import com.yjf.yrd.ws.marketing.query.order.LotteryActivityQueryOrder;
import com.yjf.yrd.ws.marketing.query.order.PrizeRuleQueryOrder;
import com.yjf.yrd.ws.marketing.query.result.LotteryResult;
import com.yjf.yrd.ws.service.query.result.QueryBaseBatchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("backstage/lottery")
public class LotteryBaseController extends BaseAutowiredController {

	String LOTTERY_PAGE_PATH = "/backstage/lotteryActivityManage/";

    @Autowired
    PrizeRuleQueryService prizeRuleQueryService;


	/**
	 * 抽奖活动首页
	 * */
	@RequestMapping("activityList")
	public String activityList(
			LotteryActivityQueryOrder lotteryActivityQueryOrder, Model model) {
		QueryBaseBatchResult<LotteryActivityInfo> page = lotteryActivityQueryService
				.queryLotteryActivity(lotteryActivityQueryOrder);
        model.addAttribute("page", PageUtil.getCovertPage(page));
        model.addAttribute("queryConditions",lotteryActivityQueryOrder);
		return LOTTERY_PAGE_PATH + "activeList.vm";

	}


	/**
	 * 新增活动信息
	 * */
	@RequestMapping("addActivity")
	public String addActivity(Model model) {
        PrizeRuleQueryOrder queryOrder = new PrizeRuleQueryOrder();
        QueryBaseBatchResult<PrizeRuleInfo> page =  prizeRuleQueryService.queryPrizeRuleInfo(queryOrder);
        model.addAttribute("rules",page.getPageList());
		return LOTTERY_PAGE_PATH + "addActivity.vm";

	}


    /**
     * 新增活动信息
     * */
    @RequestMapping("updateActivity")
    public String updateActivity(long activityId,Model model) {
        LotteryResult result = lotteryActivityQueryService.findByActityId(activityId);
        model.addAttribute("info",result.getLotteryActivityInfo());
        PrizeRuleQueryOrder queryOrder = new PrizeRuleQueryOrder();
        QueryBaseBatchResult<PrizeRuleInfo> page =  prizeRuleQueryService.queryPrizeRuleInfo(queryOrder);
        model.addAttribute("rules",page.getPageList());
        return LOTTERY_PAGE_PATH + "updateActivity.vm";
    }






}
