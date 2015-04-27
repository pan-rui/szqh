package com.yjf.yrd.backstage.controller.trademanage;

import com.alibaba.fastjson.JSONObject;
import com.yjf.common.lang.util.ListUtil;
import com.yjf.yrd.base.BaseAutowiredController;
import com.yjf.yrd.page.PageParam;
import com.yjf.yrd.rs.util.RateUtil;
import com.yjf.yrd.service.profit.ProfitPrcocessService;
import com.yjf.yrd.service.profit.order.AddProfitOrder;
import com.yjf.yrd.service.profit.order.UpdateProfitOrder;
import com.yjf.yrd.user.info.InstitutionsUserInfo;
import com.yjf.yrd.user.query.order.UserRoleQueryOrder;
import com.yjf.yrd.web.util.PageUtil;
import com.yjf.yrd.ws.enums.SysUserRoleEnum;
import com.yjf.yrd.ws.enums.UserTypeEnum;
import com.yjf.yrd.ws.result.YrdBaseResult;
import com.yjf.yrd.ws.service.query.result.QueryBaseBatchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by min on 2014/12/16.
 */
@Controller
@RequestMapping("backstage/marketingProfit")
public class ProfitAssignInfoMarketingController extends BaseAutowiredController {
    @Autowired
    ProfitPrcocessService profitPrcocessService;

    @RequestMapping("queryProfitAssignInfoMarketing")
    public String queryProfitAssignInfoMarketing(UserRoleQueryOrder queryOrder, PageParam pageParam, Model model) {
        queryOrder.setType(UserTypeEnum.JG);
        queryOrder.setRoleEnum(SysUserRoleEnum.MARKETING);
        queryOrder.setPageNumber(pageParam.getPageNo());
        queryOrder.setPageSize(pageParam.getPageSize());
        queryOrder.setHasDistributionQuota(true);
        QueryBaseBatchResult<InstitutionsUserInfo> batchResult = userQueryService
                .queryRoleInstitutionsUserInfo(queryOrder);
        List<InstitutionsUserInfo> userInfoList = batchResult.getPageList();
        if(ListUtil.isNotEmpty(userInfoList)){
            for(InstitutionsUserInfo info : userInfoList){
                if(info.getOrgDistributionQuota() != -1.0){
                BigDecimal bg = new BigDecimal(info.getOrgDistributionQuota() * 100);
                double d = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                info.setOrgDistributionQuota(d);
             }
            }
        }

        model.addAttribute("page", PageUtil.getCovertPage(batchResult));
        model.addAttribute("queryConditions", queryOrder);
        return "backstage/trade/profitAssignInfoMarketing_list.vm";
    }

    /**
     * 修改
     *
     * @param updateProfitOrder
     * @return
     */
    @ResponseBody
    @RequestMapping("modifyProfitAssignInfoMarketing")
    public Object modify(UpdateProfitOrder updateProfitOrder) {
        JSONObject jsonObject = new JSONObject();
        try {
            YrdBaseResult baseResult = profitPrcocessService.updateMarketingProfitSetting(updateProfitOrder);
            if (baseResult.isSuccess()) {

                jsonObject.put("code", 1);
                jsonObject.put("message", "修改成功");
            } else {
                jsonObject.put("code", 0);
                jsonObject.put("message", baseResult.getMessage());

            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);

        }
        return jsonObject;

    }


    /**
     * 修改
     *
     * @param receiveId
     * @return
     */
    @ResponseBody
    @RequestMapping("cancelProfitAssignInfoMarketing")
    public Object cancel(long receiveId) {
        JSONObject jsonObject = new JSONObject();
        try {
            YrdBaseResult baseResult = profitPrcocessService.cancelMarketingProfitSetting(receiveId);
            if (baseResult.isSuccess()) {

                jsonObject.put("code", 1);
                jsonObject.put("message", "取消成功");
            } else {
                jsonObject.put("code", 0);
                jsonObject.put("message", baseResult.getMessage());

            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);

        }
        return jsonObject;

    }

    /**
     * 添加
     *
     * @param addProfitOrder
     * @return
     */
    @ResponseBody
    @RequestMapping("addProfitAssignInfoMarketing")
    public Object add(AddProfitOrder addProfitOrder) {
        JSONObject jsonObject = new JSONObject();
        try {
            addProfitOrder.setDistributionId(-1);
            addProfitOrder.setDistributionQuota(addProfitOrder.getDistributionQuota());
            YrdBaseResult baseResult = profitPrcocessService.addMarketingProfitSetting(addProfitOrder);
            if (baseResult.isSuccess()) {

                jsonObject.put("code", 1);
                jsonObject.put("message", "添加成功");
            } else {
                jsonObject.put("code", 0);
                jsonObject.put("message", baseResult.getMessage());

            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);

        }

        return jsonObject;
    }


}
