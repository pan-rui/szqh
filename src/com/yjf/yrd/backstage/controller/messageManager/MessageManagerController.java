/**
 * www.househood.net Inc.
 * Copyright (c) 2011 All Rights Reserved.
 */
package com.yjf.yrd.backstage.controller.messageManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.alibaba.fastjson.JSONObject;
import com.yjf.common.lang.util.money.Money;
import com.yjf.yrd.common.info.MessageReceivedInfo;
import com.yjf.yrd.common.services.SystemMessageService;
import com.yjf.yrd.common.services.order.QueryReceviedMessageOrder;
import com.yjf.yrd.common.services.order.SystemSendMessageOrder;
import com.yjf.yrd.integration.openapi.info.QueryWithdrawInfo;
import com.yjf.yrd.page.Page;
import com.yjf.yrd.page.PageParam;
import com.yjf.yrd.page.PageParamUtil;
import com.yjf.yrd.session.SessionLocalManager;
import com.yjf.yrd.user.info.PersonalVOInfo;
import com.yjf.yrd.user.query.UserQueryService;
import com.yjf.yrd.user.result.UserQueryResult;
import com.yjf.yrd.util.MiscUtil;
import com.yjf.yrd.util.NumberUtil;
import com.yjf.yrd.web.util.PageUtil;
import com.yjf.yrd.ws.enums.NotificationTypeEnum;
import com.yjf.yrd.ws.enums.SysSendMessageSubTypeEnum;
import com.yjf.yrd.ws.enums.SysSendMessageTypeEnum;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.yjf.common.lang.util.StringUtil;
import com.yjf.yrd.common.info.MessageInfo;
import com.yjf.yrd.common.services.SiteMessageService;
import com.yjf.yrd.common.services.order.MessageOrder;
import com.yjf.yrd.common.services.order.QueryMessageOrder;
import com.yjf.yrd.web.util.WebUtil;
import com.yjf.yrd.ws.enums.BooleanEnum;
import com.yjf.yrd.ws.result.YrdBaseResult;
import com.yjf.yrd.ws.service.query.result.QueryBaseBatchResult;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 *                       
 * @Filename MessageManagerController.java
 *
 * @Description 站内消息管理
 *
 * @Version 1.0
 *
 * @Author sxiaomeng
 *
 * @Email sxm@yiji.com
 *       
 * @History
 *<li>Author: sxiaomeng</li>
 *<li>Date: 2015-3-9</li>
 *<li>Version: 1.0</li>
 *<li>Content: create</li>
 *
 */
@Controller
@RequestMapping("backstage")
public class MessageManagerController {
	
	/** 通用页面路径 */
	String USER_MANAGE_PATH = "/backstage/message/";
	
	@Autowired
	SiteMessageService siteMessageService;

    @Autowired
    UserQueryService userQueryService;
	
	/*
	 * 站内信列表
	 */
	@RequestMapping("messageList")
	public String messageList(HttpSession session, HttpServletRequest request,
                              PageParam pageParam,Model model) {
		QueryMessageOrder queryMessageOrder = new QueryMessageOrder();
        WebUtil.setPoPropertyByRequest(queryMessageOrder,request);
        queryMessageOrder.setPageNumber(pageParam.getPageNo());
        queryMessageOrder.setPageSize(pageParam.getPageSize());
		QueryBaseBatchResult<MessageInfo> messageInfoList =  siteMessageService.findMessage(queryMessageOrder);

        List<MessageInfo> infos = messageInfoList.getPageList();
        int totalSize = (int) messageInfoList.getPageCount();
        int start = PageParamUtil.startValue(totalSize, (int)messageInfoList.getPageSize(),
                (int)messageInfoList.getPageSize());
        Page<MessageInfo> page = new Page<MessageInfo>(start, totalSize,
                (int)messageInfoList.getPageSize(), infos);
        model.addAttribute("page", PageUtil.getCovertPage(messageInfoList));
        model.addAttribute("queryMessageOrder",queryMessageOrder);
		return USER_MANAGE_PATH + "messageList.vm";
	}

    /*
	 * 站内信詳情
	 */
    @RequestMapping("messageInfo")
    public String messageInfo(HttpSession session, HttpServletRequest request,
                              HttpServletResponse response,Model model) {
        QueryMessageOrder queryMessageOrder = new QueryMessageOrder();
        WebUtil.setPoPropertyByRequest(queryMessageOrder,request);
        QueryBaseBatchResult<MessageInfo> messageInfoList =  siteMessageService.findMessage(queryMessageOrder);
        QueryReceviedMessageOrder queryMessageReceviedOrder = new QueryReceviedMessageOrder();
        queryMessageReceviedOrder.setMessageId(queryMessageOrder.getMessageId());
        QueryBaseBatchResult<MessageReceivedInfo>  messageReceivedInfoList = siteMessageService.findReceviedMessage(queryMessageReceviedOrder);
        if (messageInfoList != null){
            model.addAttribute("info",messageInfoList.getPageList().get(0));
            model.addAttribute("receivedNum",messageReceivedInfoList.getTotalCount());
        }
        return USER_MANAGE_PATH + "messagerInfo.vm";
    }

    @RequestMapping("addMessage")
    public String addMessage(HttpSession session, HttpServletRequest request,
                                 HttpServletResponse response,Model model,String flag) {
        model.addAttribute("flag",flag);
        return USER_MANAGE_PATH + "addMessage.vm";
    }
	
	/*
	 * 添加站内消息
	 */
    @ResponseBody
	@RequestMapping("addMessageInfo")
	public JSONObject addMessageInfo(HttpSession session, HttpServletRequest request) {
		MessageOrder messageOrder = new MessageOrder();
		WebUtil.setPoPropertyByRequest(messageOrder, request);
        JSONObject jsonObject = new JSONObject();
		if(StringUtil.hasBlank(messageOrder.getMessageTitle())){
            jsonObject.put("msg","信息录入不全");
			return jsonObject;
		}
		messageOrder.setIsSendMessage(BooleanEnum.YES);
        messageOrder.setMessageSenderName(SessionLocalManager.getSessionLocal().getRealName());
        messageOrder.setMessageSenderId(SessionLocalManager.getSessionLocal().getUserId());
        messageOrder.setMessageType(messageOrder.getMessageType());
        messageOrder.setNotificationType(NotificationTypeEnum.getByCode(request.getParameter("notificationType")));
        String  sendUserName = request.getParameter("sendUserId");
        if (sendUserName != null){
            sendUserName = sendUserName.replaceAll("，",",");
            String[] sendUserNameSplit = sendUserName.split(",");
            String[] sendUserNames = new String[sendUserNameSplit.length];
            long[] sendUserIdLong = new long[sendUserNameSplit.length];
            if (sendUserNameSplit.length == 1){
                sendUserNames[0] = sendUserNameSplit[0];
            }else{
                for (int i = 0;i<sendUserNameSplit.length;i++){
                    UserQueryResult userQueryResult =  userQueryService.queryByUserName(sendUserNameSplit[i]);
                    if (userQueryResult.getQueryUserInfo() != null){
                        sendUserIdLong[i] = userQueryResult.getQueryUserInfo().getUserId();
                    }
                }
            }
            messageOrder.setSendUserId(sendUserIdLong);
        }
		YrdBaseResult baseResult =  siteMessageService.addMessageInfo(messageOrder);
		if (baseResult.isSuccess()){
            jsonObject.put("msg", "成功");
		}else{
            jsonObject.put("msg", baseResult.getMessage());
		}
		return jsonObject;
	}
}
