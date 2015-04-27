package com.yjf.yrd.backstage.controller.lotteryActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.yjf.yrd.service.query.lottery.PrizeRuleQueryService;
import com.yjf.yrd.web.util.PageUtil;
import com.yjf.yrd.ws.marketing.info.PrizeRuleInfo;
import com.yjf.yrd.ws.marketing.order.UpdatePrizeRuleOrder;
import com.yjf.yrd.ws.marketing.query.order.PrizeRuleQueryOrder;
import com.yjf.yrd.ws.marketing.query.result.PrizeRuleResult;
import com.yjf.yrd.ws.service.query.result.QueryBaseBatchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.yjf.yrd.base.BaseAutowiredController;
import com.yjf.yrd.web.util.WebUtil;
import com.yjf.yrd.ws.marketing.enums.PrizeTypeEnum;
import com.yjf.yrd.ws.marketing.order.AddPrizeRuleOrder;
import com.yjf.yrd.ws.marketing.order.PrizeRuleDetailOrder;
import com.yjf.yrd.ws.marketing.order.UpdateLotteryActivityOrder;
import com.yjf.yrd.ws.marketing.query.order.LotteryActivityQueryOrder;
import com.yjf.yrd.ws.order.ProcessOrder;
import com.yjf.yrd.ws.result.YrdBaseResult;

@Controller
@RequestMapping("backstage/lottery")
public class PrizeRuleController extends BaseAutowiredController {

    @Autowired
    PrizeRuleQueryService prizeRuleQueryService;

	String LOTTERY_PAGE_PATH = "/backstage/lotteryPrizeRuleManage/";

	/**
	 * 抽奖活动首页
	 * */
	@RequestMapping("prizeRuleList")
	public String prizeRuleList(
            PrizeRuleQueryOrder queryOrder, Model model) {
        QueryBaseBatchResult<PrizeRuleInfo> page =  prizeRuleQueryService.queryPrizeRuleInfo(queryOrder);
        model.addAttribute("queryConditions", queryOrder);
        model.addAttribute("page", PageUtil.getCovertPage(page));
		return LOTTERY_PAGE_PATH + "prizeRuleList.vm";

	}


    @RequestMapping("addPrizeRule")
    public String prizeRuleList(Model model) {
        return LOTTERY_PAGE_PATH + "addPrizeRule.vm";
    }


    @RequestMapping("updatePrizeRule")
    public String updatePrizeRule(long ruleId,Model model) {
        PrizeRuleResult result = prizeRuleQueryService.findByPrizeRuleId(ruleId);
        model.addAttribute("prizeRuleInfo",result.getPrizeRuleInfo());
        model.addAttribute("prizeRuleDetailInfos",result.getPrizeRuleDetailInfos());
        return LOTTERY_PAGE_PATH + "updatePrizeRule.vm";
    }

    /**
     * 创建抽奖活动
     * */
    @ResponseBody
    @RequestMapping("updatePrizeRuleSubmit")
    public Object updatePrizeRuleSubmit(HttpServletRequest request) {
        UpdatePrizeRuleOrder prizeRuleOrder = new UpdatePrizeRuleOrder();
        WebUtil.setPoPropertyByRequest(prizeRuleOrder,request);
        prizeRuleOrder.setPrizeNum(Long.valueOf(request.getParameter("zprizeNum")));
        List<PrizeRuleDetailOrder> prizeRuleDetailOrders = getPrizeRuleDetailOrders(request);
        prizeRuleOrder.setPrizeRuleDetailOrders(prizeRuleDetailOrders);
        YrdBaseResult result = prizeRuleService.updatePrizeRule(prizeRuleOrder);
        JSONObject json = new JSONObject();
        if (result.isSuccess()) {
            json.put("code", 1);
            json.put("message", "更新奖品设置成功！");
        } else {
            json.put("code", 0);
            json.put("message", result.getMessage());
        }
        return json;

    }


	/**
	 * 创建抽奖活动
	 * */
	@ResponseBody
	@RequestMapping("addPrizeRuleSubmit")
	public Object addPrizeRuleSubmit(HttpServletRequest request) {
        AddPrizeRuleOrder prizeRuleOrder = new AddPrizeRuleOrder();
        WebUtil.setPoPropertyByRequest(prizeRuleOrder,request);
        prizeRuleOrder.setPrizeNum(Long.valueOf(request.getParameter("zprizeNum")));
        List<PrizeRuleDetailOrder> prizeRuleDetailOrders = getPrizeRuleDetailOrders(request);
        prizeRuleOrder.setPrizeRuleDetailOrders(prizeRuleDetailOrders);
		YrdBaseResult result = prizeRuleService.addPrizeRule(prizeRuleOrder);
		JSONObject json = new JSONObject();
		if (result.isSuccess()) {
            json.put("code", 1);
            json.put("message", "创建奖品设置成功！");
		} else {
            json.put("code", 0);
            json.put("message", result.getMessage());
		}
		return json;

	}

    private List<PrizeRuleDetailOrder> getPrizeRuleDetailOrders(HttpServletRequest request) {
        String[] prizeType = request.getParameterValues("prizeType");
        String [] description = request.getParameterValues("description");
        String [] awards = request.getParameterValues("awards");
        String [] prizeNum = request.getParameterValues("prizeNum");
        String [] prizeAmount = request.getParameterValues("prizeAmount");
        String [] probability = request.getParameterValues("probability");
        String [] seqNum = request.getParameterValues("seqNum");
//        String [] prizeImg = request.getParameterValues("prizeImg");
        List<PrizeRuleDetailOrder> prizeRuleDetailOrders = null;
        if(prizeType != null && prizeType.length >0){
            prizeRuleDetailOrders = new ArrayList<PrizeRuleDetailOrder>(prizeType.length);
            for(int i= 0;i<prizeType.length;i++){
                 PrizeRuleDetailOrder detailOrder = new PrizeRuleDetailOrder();
                 detailOrder.setPrizeType(PrizeTypeEnum.getByCode(prizeType[i]));
                 detailOrder.setPrizeName(PrizeTypeEnum.getByCode(prizeType[i]).message());
                 detailOrder.setDescription(description[i]);
                 detailOrder.setAwards(awards[i]);
                 detailOrder.setPrizeNum(Long.valueOf(prizeNum[i]));
                 detailOrder.setPrizeAmount(Double.valueOf(prizeAmount[i]));
                 detailOrder.setProbability(Double.valueOf(probability[i]));
                 detailOrder.setSeqNum(Long.valueOf(seqNum[i]));
//                 detailOrder.setPrizeImage(prizeImg[i]);
                 prizeRuleDetailOrders.add(detailOrder);
            }

        }
        return prizeRuleDetailOrders;
    }

    /**
	 * 删除抽奖活动
	 * */
	@ResponseBody
	@RequestMapping("removePrizeRule")
	public Object removePrizeRule(ProcessOrder processOrder) {
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

}
