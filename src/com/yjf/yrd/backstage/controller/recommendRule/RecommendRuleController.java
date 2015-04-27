package com.yjf.yrd.backstage.controller.recommendRule;

import com.alibaba.fastjson.JSONObject;
import com.yjf.yrd.base.BaseAutowiredController;
import com.yjf.yrd.service.query.recommedRule.RecommendRuleQueryService;
import com.yjf.yrd.ws.enums.RecommendRuleGiveTypeEnum;
import com.yjf.yrd.ws.enums.RecommendRuleWayTypeEnum;
import com.yjf.yrd.ws.info.RecommendRuleInfo;
import com.yjf.yrd.ws.order.CreateRecommendRuleOrder;
import com.yjf.yrd.ws.result.YrdBaseResult;
import com.yjf.yrd.ws.service.RecommendRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("backstage/recommendRule")
public class RecommendRuleController extends BaseAutowiredController {
    /**
     * 页面所在路径
     */
    private final String BORROWING_MANAGE__PATH = "/backstage/recommendRule/";

    @Autowired
    RecommendRuleQueryService recommendRuleQueryService;

    @Autowired
    RecommendRuleService recommendRuleService;






    @RequestMapping(value = "addRecommendRule")
    public String addRecommendRule(Model model) {
        RecommendRuleInfo debtTransferRuleInfo = recommendRuleQueryService.findNow();
        model.addAttribute("info",debtTransferRuleInfo);
        model.addAttribute("giveType", RecommendRuleGiveTypeEnum.getAllEnum());
        model.addAttribute("wayType", RecommendRuleWayTypeEnum.getAllEnum());
        return BORROWING_MANAGE__PATH + "addRecommendRule.vm";
    }





    @ResponseBody
    @RequestMapping(value = "addRecommendRuleSubmit")
    public Object addRecommendRuleSubmit(CreateRecommendRuleOrder createDebtTransferRuleOrder) throws Exception {
        JSONObject json = new JSONObject();
        YrdBaseResult result = recommendRuleService.addRecommendRule(createDebtTransferRuleOrder);
        if (result.isSuccess()) {
            json.put("code", 1);
            json.put("message", "成功!");
        } else {
            json.put("code", 0);
            json.put("message", "失败!"+result.getMessage());

        }
        return json;
    }




}
