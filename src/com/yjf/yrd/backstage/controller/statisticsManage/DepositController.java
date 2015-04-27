package com.yjf.yrd.backstage.controller.statisticsManage;

import com.yjf.common.lang.beans.cglib.BeanCopier;
import com.yjf.common.lang.util.ListUtil;
import com.yjf.yrd.base.BaseAutowiredController;
import com.yjf.yrd.integration.bornapi.DepositQueryClient;
import com.yjf.yrd.integration.bornapi.WithdrawAndDepositExport;
import com.yjf.yrd.integration.bornapi.info.DepositInfo;
import com.yjf.yrd.integration.bornapi.info.WithdrawAndDepositExportInfo;
import com.yjf.yrd.integration.bornapi.order.DepositQueryOrder;
import com.yjf.yrd.integration.bornapi.order.WithdrawAndDepositExportOrder;
import com.yjf.yrd.page.PageParam;
import com.yjf.yrd.user.info.UserInfo;
import com.yjf.yrd.user.query.order.UserQueryOrder;
import com.yjf.yrd.util.DateUtil;
import com.yjf.yrd.util.StringUtil;
import com.yjf.yrd.web.util.PageUtil;
import com.yjf.yrd.ws.service.query.result.QueryBaseBatchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;
import java.util.List;

/**
 * 查询用户充值纪录
 * Created by min on 2014/12/13.
 */
@Controller
@RequestMapping("backstage")
public class DepositController extends BaseAutowiredController {

    @Autowired
    DepositQueryClient depositQueryClient;

    @Autowired
    WithdrawAndDepositExport withdrawAndDepositExport;

    final String	VM_PATH	= "/backstage/statistics/";


    @RequestMapping("depositQuery")
    public String depositQuery(DepositQueryOrder queryOrder, PageParam pageParam,Model model) {
        try {
            queryOrder.setPageSize(String.valueOf(pageParam.getPageSize()));
            queryOrder.setCurrPage(String.valueOf(pageParam.getPageNo()));
            if(StringUtil.isNotBlank(queryOrder.getUserName())){
                UserInfo userInfo= userQueryService.queryByUserName(queryOrder.getUserName()).getQueryUserInfo();
                if(userInfo != null){
                    queryOrder.setUserId(userInfo.getAccountId());
                }else if(StringUtil.isNotBlank(queryOrder.getRealName())){
                    UserQueryOrder userQueryOrder = new UserQueryOrder();
                    userQueryOrder.setRealName(queryOrder.getRealName());
                   List<UserInfo> users = userQueryService.commonQueryUserInfo(userQueryOrder).getPageList();
                   if(ListUtil.isNotEmpty(users)){
                       queryOrder.setUserId(users.get(0).getAccountId());
                   }
                }
            }

            if(StringUtil.isNotBlank(queryOrder.getRealName())) {
                UserQueryOrder userQueryOrder = new UserQueryOrder();
                userQueryOrder.setRealName(queryOrder.getRealName());
                List<UserInfo> users = userQueryService.commonQueryUserInfo(userQueryOrder).getPageList();
                if (ListUtil.isNotEmpty(users)) {
                    queryOrder.setUserId(users.get(0).getAccountId());
                } else if (StringUtil.isNotBlank(queryOrder.getUserName())) {
                    UserInfo userInfo = userQueryService.queryByUserName(queryOrder.getUserName()).getQueryUserInfo();
                    if (userInfo != null) {
                        queryOrder.setUserId(userInfo.getAccountId());
                    }
                }
            }
            if(StringUtil.isEmpty(queryOrder.getStartTime())){
                queryOrder.setStartTime(DateUtil.getMonthdayBeforeNow(new Date())+" 00:00:00");
            }

            if(StringUtil.isEmpty(queryOrder.getEndTime())){
                queryOrder.setEndTime(DateUtil.simpleFormat(new Date()));
            }

            QueryBaseBatchResult<DepositInfo> queryBaseBatchResult = depositQueryClient.depositQuery(queryOrder, getOpenApiContext());
            List<DepositInfo> list = queryBaseBatchResult.getPageList();
            if(ListUtil.isNotEmpty(list)){
                for(DepositInfo info : list){
                   UserInfo userInfo = userQueryService.queryByAccountId(info.getUserId()).getQueryUserInfo();
                   if(userInfo != null){
                       info.setUserName(userInfo.getUserName());
                       info.setRealName(userInfo.getRealName());
                   }
                }
            }

            WithdrawAndDepositExportOrder exportOrder = new WithdrawAndDepositExportOrder();
            BeanCopier.staticCopy(queryOrder,exportOrder);
            exportOrder.setExportType("DEPOSIT");
            WithdrawAndDepositExportInfo exportInfo = withdrawAndDepositExport.exportData(exportOrder,getOpenApiContext());
            model.addAttribute("downUrl",exportInfo.getDownloadPath());
            model.addAttribute("page", PageUtil.getCovertPage(queryBaseBatchResult));
            model.addAttribute("queryConditions", queryOrder);
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }


        return VM_PATH + "depositQuery.vm";
    }


}
