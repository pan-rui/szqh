package com.yjf.yrd.backstage.controller.recommendFriend;

import com.yjf.yrd.base.BaseAutowiredController;
import com.yjf.yrd.page.PageParam;
import com.yjf.yrd.service.query.recommendFriend.RecommendFriendQueryService;
import com.yjf.yrd.web.util.PageUtil;
import com.yjf.yrd.ws.info.RecommendFriendInfo;
import com.yjf.yrd.ws.service.query.order.RecommendFriendQueryOrder;
import com.yjf.yrd.ws.service.query.result.QueryBaseBatchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("backstage/recommendFriend")
public class RecommendFriendController extends BaseAutowiredController {
    /**
     * 页面所在路径
     */
    private final String BORROWING_MANAGE__PATH = "/backstage/recommendFriend/";

    @Autowired
    RecommendFriendQueryService recommendFriendQueryService;








    @RequestMapping(value = "recommendFriendManager")
    public String addRecommendRule(RecommendFriendQueryOrder recommendFriendQueryOrder,PageParam pageParam,Model model) {
        recommendFriendQueryOrder.setPageSize(pageParam.getPageSize());
        recommendFriendQueryOrder.setPageNumber(pageParam.getPageNo());
        QueryBaseBatchResult<RecommendFriendInfo> page =recommendFriendQueryService.queryRecommendFriend(recommendFriendQueryOrder);
        model.addAttribute("page", PageUtil.getCovertPage(page));
        model.addAttribute("queryOrder",recommendFriendQueryOrder);
        return BORROWING_MANAGE__PATH + "recommendFriendManage.vm";
    }








}
