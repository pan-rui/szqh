package com.yjf.yrd.front.controller.message;

/**
 * Created by Administrator on 2015/3/10.
 */

import com.alibaba.fastjson.JSONObject;
import com.yjf.yrd.common.info.MessageReceivedInfo;
import com.yjf.yrd.common.services.SiteMessageService;
import com.yjf.yrd.common.services.order.MessageOrder;
import com.yjf.yrd.common.services.order.MyMessageOrder;
import com.yjf.yrd.common.services.order.QueryReceviedMessageOrder;
import com.yjf.yrd.common.services.result.MessageResult;
import com.yjf.yrd.dal.daointerface.MessageReceivedDAO;
import com.yjf.yrd.dal.dataobject.MessageReceivedDO;
import com.yjf.yrd.page.Page;
import com.yjf.yrd.page.PageParam;
import com.yjf.yrd.page.PageParamUtil;
import com.yjf.yrd.session.SessionLocalManager;
import com.yjf.yrd.util.NumberUtil;
import com.yjf.yrd.util.StringUtil;
import com.yjf.yrd.web.util.PageUtil;
import com.yjf.yrd.web.util.WebUtil;
import com.yjf.yrd.ws.enums.MessageReceivedStatusEnum;
import com.yjf.yrd.ws.service.GiftMoneyService;
import com.yjf.yrd.ws.service.YrdResultEnum;
import com.yjf.yrd.ws.service.query.result.QueryBaseBatchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @Filename MessageController.java
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
 *<li>Date: 2015-3-10</li>
 *<li>Version: 1.0</li>
 *<li>Content: create</li>
 *
 */
@Controller
@RequestMapping("userManage")
public class MessagerController {

    @Autowired
    SiteMessageService siteMessageService;

    @Autowired
    MessageReceivedDAO messageReceivedDAO;


    private final String USER_MANAGE_PATH = "/front/user/message/";
    /*
	 * 查詢消息
	 */
    @RequestMapping("messageInfoList")
    public String messageInfoList(HttpSession session, HttpServletRequest request,
                                  PageParam pageParam,Model model,String status) {
        QueryReceviedMessageOrder queryMessageOrder = new QueryReceviedMessageOrder();
        long userId =  SessionLocalManager.getSessionLocal().getUserId();
        queryMessageOrder.setMessageReceivedId(userId);
        List<MessageReceivedStatusEnum> statusList = new ArrayList<>();
        if (StringUtil.isEmpty(status)){
            statusList.add(MessageReceivedStatusEnum.UNREAD);
            statusList.add(MessageReceivedStatusEnum.READ);
            statusList.add(MessageReceivedStatusEnum.COLLECT);
        }else {
            statusList.add(MessageReceivedStatusEnum.getByCode(status));
        }
        queryMessageOrder.setStatusList(statusList);
        queryMessageOrder.setPageNumber(pageParam.getPageNo());
        queryMessageOrder.setPageSize(pageParam.getPageSize());
        QueryBaseBatchResult<MessageReceivedInfo> messageInfoList =  siteMessageService.findReceviedMessage(queryMessageOrder);
        List<MessageReceivedInfo> infos = messageInfoList.getPageList();
        int totalSize = (int) messageInfoList.getPageCount();
        int start = PageParamUtil.startValue(totalSize, (int) messageInfoList.getPageSize(),
                (int) messageInfoList.getPageSize());
        Page<MessageReceivedInfo> page = new Page<MessageReceivedInfo>(start, totalSize,
                (int)messageInfoList.getPageSize(), infos);
        model.addAttribute("page", PageUtil.getCovertPage(messageInfoList));
        model.addAttribute("status",status);
        return USER_MANAGE_PATH + "messageInfoList.vm";
    }


    /*
	 * 查詢消息
	 */
    @ResponseBody
    @RequestMapping("ajaxLoadUnReadData")
    public Object ajaxLoadUnReadData() {
        long userId =  SessionLocalManager.getSessionLocal().getUserId();
        QueryBaseBatchResult<MessageReceivedInfo> baseResult = siteMessageService.loadUnReadMyMessage(userId);
        JSONObject jsonObject = new JSONObject();
        if (YrdResultEnum.EXECUTE_SUCCESS == baseResult.getYrdResultEnum()){
            jsonObject.put("success","true");
        }else {
            jsonObject.put("success","false");
        }
        jsonObject.put("count",baseResult.getTotalCount());
        return jsonObject;
    }

    /*
	 * 修改消息
	 */
    @RequestMapping("updateMessageInfo")
    public ModelAndView updateMessageInfo(HttpSession session, HttpServletRequest request,
                                  HttpServletResponse response,Model model,String status) {
        MyMessageOrder myMessageOrder = new MyMessageOrder();
        MessageResult messageResult = null;
        String[] receivedId = request.getParameterValues("receivedId");
        String type = request.getParameter("type");
        WebUtil.setPoPropertyByRequest(myMessageOrder, request);
        if (type.equals(MessageReceivedStatusEnum.COLLECT.code())){
            myMessageOrder.setMessageReceivedStatus(MessageReceivedStatusEnum.COLLECT.code());
        }else if(type.equals(MessageReceivedStatusEnum.READ.code())){
            myMessageOrder.setMessageReceivedStatus(MessageReceivedStatusEnum.READ.code());
        }
        myMessageOrder.setUserId(SessionLocalManager.getSessionLocal().getUserId());
       for (String receivedIds : receivedId){
           myMessageOrder.setReceivedId(NumberUtil.parseLong(receivedIds));
           messageResult =  siteMessageService.readMessageInfo(myMessageOrder);
       }
        if (StringUtil.isNotEmpty(status)){
            return new ModelAndView(new RedirectView("/userManage/messageInfoList?status="+status));
        }else {
            return new ModelAndView(new RedirectView("/userManage/messageInfoList"));
        }
    }

    /*
    * 修改消息
    */
    @ResponseBody
    @RequestMapping("ajaxUpdateMessageInfo")
    public JSONObject ajaxUpdateMessageInfo(HttpSession session, HttpServletRequest request,
                                        HttpServletResponse response,Model model,String status) {
        MyMessageOrder myMessageOrder = new MyMessageOrder();
        MessageResult messageResult = null;
        String[] receivedId = request.getParameterValues("receivedId");
        String type = request.getParameter("type");
        WebUtil.setPoPropertyByRequest(myMessageOrder, request);
        if (type.equals(MessageReceivedStatusEnum.COLLECT.code())){
            myMessageOrder.setMessageReceivedStatus(MessageReceivedStatusEnum.COLLECT.code());
        }else if(type.equals(MessageReceivedStatusEnum.READ.code())){
            myMessageOrder.setMessageReceivedStatus(MessageReceivedStatusEnum.READ.code());
        }
        myMessageOrder.setUserId(SessionLocalManager.getSessionLocal().getUserId());
        for (String receivedIds : receivedId){
            myMessageOrder.setReceivedId(NumberUtil.parseLong(receivedIds));
            messageResult =  siteMessageService.readMessageInfo(myMessageOrder);
        }
        JSONObject json = new JSONObject();
        if (YrdResultEnum.EXECUTE_SUCCESS == messageResult.getYrdResultEnum()){
            json.put("result",true);
        }else {
            json.put("result",false);
        }
        return json;
    }


    /*
	 * 删除收到的消息
	 */
    @RequestMapping("deleteReceivedMessageInfo")
    public void deleteReceivedMessageInfo(HttpSession session, HttpServletRequest request,
                                    HttpServletResponse response,Model model,String status) {
        MyMessageOrder myMessageOrder = new MyMessageOrder();
        MessageResult messageResult = null;
        String[] receivedId = request.getParameterValues("receivedId");
        myMessageOrder.setUserId(SessionLocalManager.getSessionLocal().getUserId());
        for (String receivedIds : receivedId){
            myMessageOrder.setReceivedId(NumberUtil.parseLong(receivedIds));
            messageResult =  siteMessageService.deleteReceivedMessageInfo(myMessageOrder);
        }
        model.addAttribute("messageResult", messageResult);
        try {
            response.sendRedirect("/userManage/messageInfoList");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
