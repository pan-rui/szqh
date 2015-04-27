package com.yjf.yrd.backstage.controller.lotteryActivity;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.yjf.yrd.web.util.PageUtil;
import com.yjf.yrd.ws.marketing.enums.WinnerStatusEnum;
import com.yjf.yrd.ws.marketing.info.LotteryActivityInfo;
import com.yjf.yrd.ws.marketing.info.LotteryWinnerInfo;
import com.yjf.yrd.ws.marketing.order.GiveWinnerOrder;
import com.yjf.yrd.ws.marketing.query.order.LotteryActivityQueryOrder;
import com.yjf.yrd.ws.marketing.query.order.LotteryWinnerQueryOrder;
import com.yjf.yrd.ws.service.query.result.QueryBaseBatchResult;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.yjf.yrd.base.BaseAutowiredController;
import com.yjf.yrd.web.util.WebUtil;
import com.yjf.yrd.ws.marketing.enums.LotteryConditionTypeEnum;
import com.yjf.yrd.ws.marketing.enums.LotteryTimesTypeEnum;
import com.yjf.yrd.ws.marketing.enums.LotteryTypeEnum;
import com.yjf.yrd.ws.marketing.order.AddLotteryActivityOrder;
import com.yjf.yrd.ws.marketing.order.LotteryConditionOrder;
import com.yjf.yrd.ws.marketing.order.UpdateLotteryActivityOrder;
import com.yjf.yrd.ws.order.ProcessOrder;
import com.yjf.yrd.ws.result.YrdBaseResult;

@Controller
@RequestMapping("backstage/lottery")
public class LotteryFunctionController extends BaseAutowiredController {
    String LOTTERY_PAGE_PATH = "/backstage/lotteryActivityManage/";

    /**
     * 创建抽奖活动
     */
    @ResponseBody
    @RequestMapping("addActivitySubmit")
    public Object addActivitySubmit(HttpServletRequest request) {

        String time = request.getParameter("time");
        LotteryTypeEnum lotteryType = LotteryTypeEnum.getByCode(request.getParameter("lotteryType"));
        AddLotteryActivityOrder activityOrder = new AddLotteryActivityOrder();
        activityOrder.setLotteryType(lotteryType);

        if (lotteryType == LotteryTypeEnum.SIGNUP) {
            WebUtil.setPoPropertyByRequest(activityOrder, request);
            YrdBaseResult result = lotteryActivityService
                    .addLotteryActivity(activityOrder);
            JSONObject json = new JSONObject();
            if (result.isSuccess()) {
                json.put("code", 1);
                json.put("message", "创建抽奖活动成功！");
            } else {
                json.put("code", 0);
                json.put("message", result.getMessage());
            }
            return json;
        } else {
            String conditionValue = request.getParameter("conditionValue");
            String lotteryTimesType = request.getParameter("lotteryTimesType");
            String lotteryConditionType = request
                    .getParameter("lotteryConditionType");

            LotteryConditionOrder conditionOrder = new LotteryConditionOrder();
            List<LotteryConditionOrder> conditionOrders = new ArrayList<LotteryConditionOrder>();
            conditionOrder.setLotteryConditionType(LotteryConditionTypeEnum
                    .getByCode(lotteryConditionType));

            conditionOrder.setLotteryTimesType(LotteryTimesTypeEnum
                    .getByCode(lotteryTimesType));
            conditionOrder.setTime(Long.parseLong(time));
            conditionOrder.setConditionValue(conditionValue);
            conditionOrders.add(conditionOrder);
            activityOrder.setConditionOrders(conditionOrders);
            WebUtil.setPoPropertyByRequest(activityOrder, request);
            YrdBaseResult result = lotteryActivityService
                    .addLotteryActivity(activityOrder);
            JSONObject json = new JSONObject();
            if (result.isSuccess()) {
                json.put("code", 1);
                json.put("message", "创建抽奖活动成功！");
            } else {
                json.put("code", 0);
                json.put("message", result.getMessage());
            }
            return json;
        }


    }


    /**
     * 新增活动信息
     */
    @ResponseBody
    @RequestMapping("updateActivitySubmit")
    public Object updateActivitySubmit(HttpServletRequest request) {
        JSONObject jsonObject = new JSONObject();
        UpdateLotteryActivityOrder activityOrder = new UpdateLotteryActivityOrder();
        String time = request.getParameter("time");
        LotteryTypeEnum lotteryType = LotteryTypeEnum.getByCode(request.getParameter("lotteryType"));
        activityOrder.setLotteryType(lotteryType);

        if (lotteryType == LotteryTypeEnum.SIGNUP) {
            WebUtil.setPoPropertyByRequest(activityOrder, request);
            YrdBaseResult result = lotteryActivityService
                    .updateLotteryActivity(activityOrder);
            JSONObject json = new JSONObject();
            if (result.isSuccess()) {
                json.put("code", 1);
                json.put("message", "创建抽奖活动成功！");
            } else {
                json.put("code", 0);
                json.put("message", result.getMessage());
            }
            return json;
        } else {
            String conditionValue = request.getParameter("conditionValue");
            String lotteryTimesType = request.getParameter("lotteryTimesType");
            String lotteryConditionType = request
                    .getParameter("lotteryConditionType");
            LotteryConditionOrder conditionOrder = new LotteryConditionOrder();
            List<LotteryConditionOrder> conditionOrders = new ArrayList<LotteryConditionOrder>();
            conditionOrder.setLotteryConditionType(LotteryConditionTypeEnum
                    .getByCode(lotteryConditionType));
            conditionOrder.setLotteryTimesType(LotteryTimesTypeEnum
                    .getByCode(lotteryTimesType));
            conditionOrder.setTime(Long.parseLong(time));
            conditionOrder.setConditionValue(conditionValue);
            conditionOrders.add(conditionOrder);
            activityOrder.setConditionOrders(conditionOrders);
            WebUtil.setPoPropertyByRequest(activityOrder, request);
            YrdBaseResult result = lotteryActivityService.updateLotteryActivity(activityOrder);
            if (result.isSuccess()) {
                jsonObject.put("code", 1);
                jsonObject.put("message", "更新活动成功！");
            } else {
                jsonObject.put("code", 0);
                jsonObject.put("message", result.getMessage());
            }
            return jsonObject;

        }


    }

    /**
     * 删除抽奖活动
     */
    @ResponseBody
    @RequestMapping("removeActivity")
    public Object removeActivity(ProcessOrder processOrder) {
        JSONObject jsonobj = new JSONObject();
        YrdBaseResult result = lotteryActivityService
                .removeLotteryActivity(processOrder);
        if (result.isSuccess()) {
            jsonobj.put("code", 1);
            jsonobj.put("message", "抽奖活动已删除！");
        } else {
            jsonobj.put("code", 0);
            jsonobj.put("message", result.getMessage());
        }
        return jsonobj;
    }


    /**
     * 抽奖活动首页
     */
    @RequestMapping("pageQueryWinner")
    public String pageQueryWinner(HttpServletRequest request, Model model) {
        LotteryWinnerQueryOrder lotteryWinnerQueryOrder = new LotteryWinnerQueryOrder();
        long activityId = Long.valueOf(request.getParameter("activityId"));
        List<Long> activityIds = new ArrayList<Long>();
        activityIds.add(activityId);
        lotteryWinnerQueryOrder.setActivityIdList(activityIds);
        lotteryWinnerQueryOrder.setPageSize(200);
        lotteryWinnerQueryOrder.setPageNumber(1);
        QueryBaseBatchResult<LotteryWinnerInfo> winnerResult = lotteryActivityQueryService.queryLotteryWinner(lotteryWinnerQueryOrder);
        model.addAttribute("page", PageUtil.getCovertPage(winnerResult));
        model.addAttribute("activityId", activityId);
        model.addAttribute("queryConditions", lotteryWinnerQueryOrder);
        return LOTTERY_PAGE_PATH + "pageQueryWinner.vm";

    }

    @ResponseBody
    @RequestMapping("giveWinner")
    public Object giveWinner(HttpServletRequest request, Model model) {
        JSONObject jsonObject = new JSONObject();
        GiveWinnerOrder giveWinnerOrder = new GiveWinnerOrder();
        WebUtil.setPoPropertyByRequest(giveWinnerOrder, request);
        giveWinnerOrder.setStatus(WinnerStatusEnum.ISSUED);
        YrdBaseResult winnerResult = lotteryActivityService.giveWinner(giveWinnerOrder);
        if (winnerResult.isSuccess()) {
            jsonObject.put("code", 1);
            jsonObject.put("message", "奖品发放成功");
        } else {
            jsonObject.put("code", 0);
            jsonObject.put("message", winnerResult.getMessage());
        }
        return jsonObject;

    }

}
