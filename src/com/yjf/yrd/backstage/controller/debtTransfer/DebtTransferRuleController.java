package com.yjf.yrd.backstage.controller.debtTransfer;

import com.alibaba.fastjson.JSONObject;
import com.yjf.yrd.base.BaseAutowiredController;
import com.yjf.yrd.page.PageParam;
import com.yjf.yrd.service.query.debtTransfer.DebtTransferRuleQueryService;
import com.yjf.yrd.service.query.giftMoney.GiftMoneyQueryService;
import com.yjf.yrd.service.query.giftMoney.GiftMoneyRuleQueryService;
import com.yjf.yrd.session.SessionLocal;
import com.yjf.yrd.session.SessionLocalManager;
import com.yjf.yrd.web.util.PageUtil;
import com.yjf.yrd.ws.enums.GiftMoneyGiveTypeEnum;
import com.yjf.yrd.ws.enums.GiftMoneyTypeEnum;
import com.yjf.yrd.ws.enums.GiftMoneyUseTypeEnum;
import com.yjf.yrd.ws.info.DebtTransferRuleInfo;
import com.yjf.yrd.ws.info.GiftMoneyInfo;
import com.yjf.yrd.ws.info.GiftMoneyRuleInfo;
import com.yjf.yrd.ws.order.ChangeGiftMoneyStatusOrder;
import com.yjf.yrd.ws.order.CreateDebtTransferRuleOrder;
import com.yjf.yrd.ws.order.CreateGiftMoneyOrder;
import com.yjf.yrd.ws.result.YrdBaseResult;
import com.yjf.yrd.ws.service.DebtTransferRuleService;
import com.yjf.yrd.ws.service.GiftMoneyService;
import com.yjf.yrd.ws.service.query.order.GiftMoneyQueryOrder;
import com.yjf.yrd.ws.service.query.result.QueryBaseBatchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.List;


@Controller
@RequestMapping("backstage/debtTransfer")
public class DebtTransferRuleController extends BaseAutowiredController {
    /**
     * 页面所在路径
     */
    private final String BORROWING_MANAGE__PATH = "/backstage/debtTransfer/";

    @Autowired
    DebtTransferRuleQueryService debtTransferRuleQueryService;

    @Autowired
    DebtTransferRuleService debtTransferRuleService;






    @RequestMapping(value = "addDebtTransferRule")
    public String addDebtTransferRule(Model model) {
        DebtTransferRuleInfo debtTransferRuleInfo = debtTransferRuleQueryService.findCanUse();
        if(debtTransferRuleInfo != null){
            debtTransferRuleInfo.setPriceOffline(getRateOnly(debtTransferRuleInfo.getPriceOffline()));
            debtTransferRuleInfo.setPriceOnline(getRateOnly(debtTransferRuleInfo.getPriceOnline()));
            debtTransferRuleInfo.setTransCharge(getRateOnly(debtTransferRuleInfo.getTransCharge()));
            model.addAttribute("info",debtTransferRuleInfo);
        }
        return BORROWING_MANAGE__PATH + "addDebtTransferRule.vm";
    }


    public static double getRateOnly(double value) {
        BigDecimal bg = new BigDecimal(value * 100);
        double d = bg.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
        return d;
    }





    @ResponseBody
    @RequestMapping(value = "addDebtTransferRuleSubmit")
    public Object addDebtTransferRuleSubmit(CreateDebtTransferRuleOrder createDebtTransferRuleOrder) throws Exception {
        JSONObject json = new JSONObject();
        double priceOffline =new  BigDecimal(createDebtTransferRuleOrder.getPriceOffline()).divide(new BigDecimal(100),3,BigDecimal.ROUND_HALF_UP).doubleValue();
        createDebtTransferRuleOrder.setPriceOffline(priceOffline);
        double priceOnline = new  BigDecimal(createDebtTransferRuleOrder.getPriceOnline()).divide(new BigDecimal(100),3,BigDecimal.ROUND_HALF_UP).doubleValue();
        createDebtTransferRuleOrder.setPriceOnline(priceOnline);
        double transCharge = new BigDecimal(createDebtTransferRuleOrder.getTransCharge()).divide(new BigDecimal(100),3,BigDecimal.ROUND_HALF_UP).doubleValue();
        createDebtTransferRuleOrder.setTransCharge(transCharge);
        YrdBaseResult result = debtTransferRuleService.addDebtTransferRule(createDebtTransferRuleOrder);
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
