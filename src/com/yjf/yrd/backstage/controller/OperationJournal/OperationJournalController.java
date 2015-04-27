package com.yjf.yrd.backstage.controller.OperationJournal;

import com.yjf.yrd.base.BaseAutowiredController;
import com.yjf.yrd.page.PageParam;
import com.yjf.yrd.web.util.PageUtil;
import com.yjf.yrd.ws.info.OperationJournalInfo;
import com.yjf.yrd.ws.service.OperationJournalService;
import com.yjf.yrd.ws.service.query.order.OperationJournalQueryOrder;
import com.yjf.yrd.ws.service.query.result.QueryBaseBatchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;

/**
 * 短信日志
 * Created by zhaohaibing on 2014/7/3.
 */
@Controller
@RequestMapping("backstage")
public class OperationJournalController extends BaseAutowiredController {
    private final String vm_path = "/backstage/OperationJournal/";
    @Autowired
    OperationJournalService operationJournalService;


    /**
     * 日志管理页面
     */
    @RequestMapping("OperationJournal")
    public String queryOperationJournalPageList(OperationJournalQueryOrder queryOrder, PageParam pageParam, HttpServletResponse response, Model model) {
        try {
            queryOrder.setPageSize(pageParam.getPageSize());
            queryOrder.setPageNumber(pageParam.getPageNo());
            QueryBaseBatchResult<OperationJournalInfo> queryBaseBatchResult = operationJournalService.queryOperationJournalInfo(queryOrder);
            model.addAttribute("page", PageUtil.getCovertPage(queryBaseBatchResult));
            model.addAttribute("queryConditions", queryOrder);
        } catch (Exception e) {
               logger.error("查询短信出错",e.getMessage(),e);
        }


        return vm_path + "OperationJournalManage.vm";
    }


    protected String[] getDateInputNameArray() {
        return new String[]{"operatorTimeStart","operatorTimeEnd"};
    }


}
