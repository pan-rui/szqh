package com.yjf.yrd.mjr.controller.member;

import com.alibaba.fastjson.JSONObject;
import com.yjf.common.lang.util.DateUtil;
import com.yjf.common.util.StringUtils;
import com.yjf.yrd.base.BaseAutowiredController;
import com.yjf.yrd.common.info.MessageReceivedInfo;
import com.yjf.yrd.common.services.SiteMessageService;
import com.yjf.yrd.common.services.order.QueryReceviedMessageOrder;
import com.yjf.yrd.front.controller.account.NewInvestorsOpenController;
import com.yjf.yrd.front.controller.trade.query.TradeQueryController;
import com.yjf.yrd.front.controller.user.UserBaseController;
import com.yjf.yrd.mjr.service.RepayManageService;
import com.yjf.yrd.session.SessionLocalManager;
import com.yjf.yrd.user.order.InvestorRegisterOrder;
import com.yjf.yrd.user.result.UserRelationQueryResult;
import com.yjf.yrd.ws.enums.DivisionPhaseEnum;
import com.yjf.yrd.ws.enums.MessageReceivedStatusEnum;
import com.yjf.yrd.ws.enums.SysUserRoleEnum;
import com.yjf.yrd.ws.info.TradeDetailVOInfo;
import com.yjf.yrd.ws.order.TradeDetailQueryOrder;
import com.yjf.yrd.ws.service.query.result.QueryBaseBatchResult;
import com.yjf.yrd.ws.service.query.result.TradeDetailBatchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 会员中心页面
 * Created by panrui on 2015/4/23.
 */
@Controller
public class AccountSetting extends BaseAutowiredController {
    @Autowired
    protected TradeQueryController tradeQueryController;
    @Autowired
    protected UserBaseController userBaseController;
    @Autowired
    protected NewInvestorsOpenController newInvestorsOpenController;
    @Autowired
    SiteMessageService siteMessageService;
    @Autowired
    protected RepayManageService repayManageService;

    /**
     * 账户设置
     *
     * @param request
     * @param response
     * @param session
     * @param model
     * @return
     */
    @RequestMapping("userManage/mjr/accountSetting")
    public String setDataModel(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        String view = userBaseController.userBankInfo(request, response, model, session);
        if (SessionLocalManager.getSessionLocal() != null) {
            long userId = SessionLocalManager.getSessionLocal().getUserId();
            //增加上次登录
            model.addAttribute("prevLoginTime", SessionLocalManager.getSessionLocal().getLastDate());
            //TODO:推荐编号增加
            UserRelationQueryResult userRelationQueryResult = userRelationQueryService.findUserRelationByChildId(userId);
            model.addAttribute("userRelationInfo", userRelationQueryResult.getQueryUserRelationInfo());
        }
        return view;
    }

    /**
     * 投资的项目
     *
     * @param size
     * @param page
     * @param status
     * @param startDate
     * @param endDate
     * @param model
     * @param session
     * @return
     */
    @RequestMapping("userManage/mjr/entries/{size}/{page}")
    public String entries(@PathVariable int size, @PathVariable int page, String status,
                          String startDate, String endDate, Model model, HttpSession session) {
        String view = tradeQueryController.entries(size, page, status, startDate, endDate, model, session);
        TradeDetailQueryOrder queryOrder = new TradeDetailQueryOrder();
        queryOrder.setPageSize(size);
        queryOrder.setPageNumber(page);
        queryOrder.setUserId(SessionLocalManager.getSessionLocal().getUserId());
        queryOrder.setRoleId(SysUserRoleEnum.INVESTOR.getValue());
        queryOrder.setStartDate(startDate);
        queryOrder.setEndDate(endDate);
        queryOrder.setTransferPhase(DivisionPhaseEnum.INVESET_PHASE.code());
        TradeDetailBatchResult<TradeDetailVOInfo> income1 = tradeBizQueryService.searchTradeDetailQuery(queryOrder);
        queryOrder.setTransferPhase(DivisionPhaseEnum.REPAY_PHASE.code());
        TradeDetailBatchResult<TradeDetailVOInfo> income2 = tradeBizQueryService.searchTradeDetailQuery(queryOrder);
        model.addAttribute("income", income1.getTotalAmount().add(income2.getTotalAmount()));
        queryOrder.setTransferPhase(DivisionPhaseEnum.ORIGINAL_PHASE.code());
        queryOrder.setStatus(Arrays.asList("3", "8"));
        TradeDetailBatchResult<TradeDetailVOInfo> investing = tradeBizQueryService.searchTradeDetailQuery(queryOrder);
        model.addAttribute("investing", investing.getTotalAmount());
        return view;
    }

    @RequestMapping("userManage/mjr/userHome")
    public String userHome(HttpSession session, Model model) {
        String view = userBaseController.userHome(session, model);
        model.addAttribute("prevLoginTime", SessionLocalManager.getSessionLocal().getLastDate());
        QueryReceviedMessageOrder queryMessageOrder = new QueryReceviedMessageOrder();
        long userId = SessionLocalManager.getSessionLocal().getUserId();
        queryMessageOrder.setMessageReceivedId(userId);
        List<MessageReceivedStatusEnum> statusList = new ArrayList<>();
        statusList.add(MessageReceivedStatusEnum.UNREAD);
        queryMessageOrder.setStatusList(statusList);
        queryMessageOrder.setPageNumber(1);
        queryMessageOrder.setPageSize(10);
        QueryBaseBatchResult<MessageReceivedInfo> messageInfoList = siteMessageService.findReceviedMessage(queryMessageOrder);
        session.setAttribute("msgCount", messageInfoList.getTotalCount());
        return view;
    }

    @RequestMapping("anon/mjr/newInvestorsOpen")
    public String newinvestorsOpen(HttpSession session, String NO, String investorNO, Model model) {
        String view = newInvestorsOpenController.newinvestorsOpen(session, NO, investorNO, model);

        return view;
    }

    @RequestMapping("/anon/mjr/newPerfectInfo")
    public
    @ResponseBody
    Object phoneRegist(HttpServletRequest request, HttpServletResponse response, HttpSession session, String imgCode,
                       InvestorRegisterOrder investorRegisterOrder, String token,
                       String code, Model model) throws Exception {
        String Token = String.valueOf(session.getAttribute("token"));
        session.removeAttribute("token");
        if (!token.equals(Token)) {
            JSONObject json = new JSONObject();
            json.put("code", 0);
            json.put("message", "重复提交或非法提交..");
            logger.info("重复提交或非法提交..");
            return null;
        }
        String view = newInvestorsOpenController.phoneRegist(request, session, imgCode, investorRegisterOrder, token, code, model);
        JSONObject jsonObject = new JSONObject();
        if (StringUtils.isNotEmpty(view)) {
//            return "redirect:" + request.getHeader("referer") + "?message=" + URLEncoder.encode("验证码错误.").toString();
            jsonObject.put("code", 1);
            return jsonObject;
        } else {
            jsonObject.put("code", 1);
            jsonObject.put("msg", "验证码错误.");
        }
        return jsonObject;
    }

    @RequestMapping("/userManage/mjr/investRecord/{pageSize}/{pageNo}")
    public String investRecord(HttpServletRequest request, HttpServletResponse response, Model model, @PathVariable String pageSize, @PathVariable String pageNo, String startDate, String endDate) {

        return "front/user/activation/investRecord.vm";
    }

    @RequestMapping("/userManage/mjr/repayManage")
    public @ResponseBody Map<String,Object> repayManage(HttpServletRequest request, HttpServletResponse response, Model model, String pageSize, String pageNo, String startDate, String endDate, String status) {
        if(SessionLocalManager.getSessionLocal()!=null) {
            Date startD=com.yjf.yrd.util.DateUtil.parse(startDate, new Date());
            Date endD=com.yjf.yrd.util.DateUtil.parse(endDate, new Date());
            List<String> startS = Arrays.asList(status.split(","));
            Map<String, Object> params = new HashMap<>();
            long userId=SessionLocalManager.getSessionLocal().getUserId();
            params.put("userId",userId );
            params.put("startDate", startDate);
            params.put("endDate", endDate);
            params.put("status", status);
           List<Map<String,Object>> result= repayManageService.findRepayPlanByCondition(params);
            params.put("pageNo", pageNo);
            params.put("pageSize", pageSize);
           List<Map<String,Object>> totalResult= repayManageService.findRepayPlanByConditionTotal(params);
            model.addAttribute("pageList", result);
            model.addAllAttributes(totalResult.get(0));
            return model.asMap();
        }else
            return new HashMap<>();

    }
}
