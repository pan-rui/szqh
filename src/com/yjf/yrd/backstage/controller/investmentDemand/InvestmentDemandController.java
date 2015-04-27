package com.yjf.yrd.backstage.controller.investmentDemand;

import com.alibaba.fastjson.JSONObject;
import com.yjf.common.lang.util.StringUtil;
import com.yjf.yrd.base.BaseAutowiredController;
import com.yjf.yrd.page.PageParam;
import com.yjf.yrd.service.query.trade.InvestmentDemandQueryService;
import com.yjf.yrd.web.util.PageUtil;
import com.yjf.yrd.web.util.WebUtil;
import com.yjf.yrd.ws.info.InvestmentDemandInfo;
import com.yjf.yrd.ws.order.InvestmentDemandChangeStatusOrder;
import com.yjf.yrd.ws.result.YrdBaseResult;
import com.yjf.yrd.ws.service.InvestmentDemandService;
import com.yjf.yrd.ws.service.query.order.InvestmentDemandQueryOrder;
import com.yjf.yrd.ws.service.query.result.QueryBaseBatchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * 投资需求管理
 * Created by wqh on 2014/6/10.
 */
@Controller
@RequestMapping("backstage/investmentDemand")
public class InvestmentDemandController extends BaseAutowiredController {
    @Autowired
    protected InvestmentDemandQueryService investmentDemandQueryService;
    @Autowired
    protected InvestmentDemandService investmentDemandService;


    /**
     * 页面所在路径
     */
    private final String INVESTMENT_DEMAND_MANAGE__PATH = "/backstage/investmentDemand/";

    /**
     * 分页查询借款管理
     */
    @RequestMapping(value = "pageQueryInvestmentDemand")
    public String pageQueryLoanDemand(HttpServletRequest request, String module,
                                      PageParam pageParam, Model model) {
        try {
            InvestmentDemandQueryOrder queryConditions = new InvestmentDemandQueryOrder();
            WebUtil.setPoPropertyByRequest(queryConditions, request);
            queryConditions.setPageNumber(pageParam.getPageNo());
            queryConditions.setPageSize(pageParam.getPageSize());
            QueryBaseBatchResult<InvestmentDemandInfo> page = investmentDemandQueryService
                    .queryInvestmentDemand(queryConditions);
            model.addAttribute("queryConditions", queryConditions);
            model.addAttribute("page", PageUtil.getCovertPage(page));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return INVESTMENT_DEMAND_MANAGE__PATH + "investmentDemand_list.vm";
    }

    @ResponseBody
    @RequestMapping("changeStatus")
    public Object changeStatus(long investmentDemandId, String status, Model model) {
        JSONObject jsonObject = new JSONObject();
        try {
            if (investmentDemandId > 0 && StringUtil.isNotEmpty(status)) {
                InvestmentDemandChangeStatusOrder changeStatusOrder = new InvestmentDemandChangeStatusOrder();
                changeStatusOrder.setId(investmentDemandId);
                changeStatusOrder.setStatus(status);
                YrdBaseResult baseResult = investmentDemandService
                        .changeInvestmentDemandStatus(changeStatusOrder);
                if (baseResult.isSuccess()) {
                    jsonObject.put("code", 1);
                    jsonObject.put("message", "修改成功！");
                } else {
                    jsonObject.put("code", 0);
                    jsonObject.put("message", "修改失败！");
                }
            } else {
                jsonObject.put("code", 0);
                jsonObject.put("message", "修改失败！");
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            jsonObject.put("code", 0);
            jsonObject.put("message", "修改失败！");
        }
        return jsonObject;
    }
}
