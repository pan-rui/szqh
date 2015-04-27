package com.yjf.yrd.backstage.controller.giftMoney;

import com.alibaba.fastjson.JSONObject;
import com.yjf.yrd.base.BaseAutowiredController;
import com.yjf.yrd.page.PageParam;
import com.yjf.yrd.service.query.giftMoney.GiftMoneyQueryService;
import com.yjf.yrd.service.query.giftMoney.GiftMoneyRuleQueryService;
import com.yjf.yrd.session.SessionLocal;
import com.yjf.yrd.session.SessionLocalManager;
import com.yjf.yrd.user.info.UserInfo;
import com.yjf.yrd.util.BusinessNumberUtil;
import com.yjf.yrd.util.StringUtil;
import com.yjf.yrd.web.util.PageUtil;
import com.yjf.yrd.web.util.WebUtil;
import com.yjf.yrd.ws.enums.GiftMoneyGiveTypeEnum;
import com.yjf.yrd.ws.enums.GiftMoneyTypeEnum;
import com.yjf.yrd.ws.enums.GiftMoneyUseTypeEnum;
import com.yjf.yrd.ws.info.GiftMoneyInfo;
import com.yjf.yrd.ws.info.GiftMoneyRuleInfo;
import com.yjf.yrd.ws.order.ChangeGiftMoneyStatusOrder;
import com.yjf.yrd.ws.order.CreateGiftMoneyOrder;
import com.yjf.yrd.ws.order.HandGiftMoneyOrder;
import com.yjf.yrd.ws.result.YrdBaseResult;
import com.yjf.yrd.ws.service.GiftMoneyAssignProcessService;
import com.yjf.yrd.ws.service.GiftMoneyService;
import com.yjf.yrd.ws.service.query.order.GiftMoneyQueryOrder;
import com.yjf.yrd.ws.service.query.result.QueryBaseBatchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


@Controller
@RequestMapping("backstage/giftMoney")
public class GiftMoneyController extends BaseAutowiredController {
    /**
     * 页面所在路径
     */
    private final String BORROWING_MANAGE__PATH = "/backstage/giftMoney/";

    @Autowired
    GiftMoneyQueryService giftMoneyQueryService;

    @Autowired
    GiftMoneyService giftMoneyService;

    @Autowired
    GiftMoneyRuleQueryService giftMoneyRuleQueryService;

    @Autowired
    GiftMoneyAssignProcessService giftMoneyAssignProcessService;



    @Override
    protected String[] getDateInputNameArray() {
        return new String[] { "useStartDate","useEndDate", "startDate", "endDate"};
    }

    @Override
    protected String[] getMoneyInputNameArray() {
        return new String[] { "amount", "totalAmount","balanceAmount","usedAmount" };
    }

    @RequestMapping(value = "addGiftMoney")
    public String addGiftMoney(Model model) {
        List<GiftMoneyTypeEnum> types = GiftMoneyTypeEnum.getAllEnum();
        model.addAttribute("types",types);
        List<GiftMoneyGiveTypeEnum> giveTypes = GiftMoneyGiveTypeEnum.getAllEnum();
        model.addAttribute("giveTypes",giveTypes);
        List<GiftMoneyUseTypeEnum> useTypes = GiftMoneyUseTypeEnum.getAllEnum();
        model.addAttribute("useTypes",useTypes);

        return BORROWING_MANAGE__PATH + "addGiftMoney.vm";
    }


    @RequestMapping(value = "updateGiftMoney")
    public String updateGiftMoney(long giftId, Model model) {
        GiftMoneyInfo info = giftMoneyQueryService.findById(giftId);
        model.addAttribute("info", info);
        List<GiftMoneyTypeEnum> types = GiftMoneyTypeEnum.getAllEnum();
        model.addAttribute("types",types);
        List<GiftMoneyGiveTypeEnum> giveTypes = GiftMoneyGiveTypeEnum.getAllEnum();
        model.addAttribute("giveTypes",giveTypes);
        List<GiftMoneyUseTypeEnum> useTypes = GiftMoneyUseTypeEnum.getAllEnum();
        List<GiftMoneyRuleInfo> rules = giftMoneyRuleQueryService.queryGiftMoneyRuleByGiftId(giftId);
        model.addAttribute("rules",rules);
        model.addAttribute("useTypes",useTypes);
        return BORROWING_MANAGE__PATH + "updateGiftMoney.vm";
    }


    @ResponseBody
    @RequestMapping(value = "updateGiftMoneySubmit")
    public Object updateGiftMoneySubmit(CreateGiftMoneyOrder createGiftMoneyOrder) throws Exception {
        JSONObject json = new JSONObject();
        SessionLocal sessionLocal = SessionLocalManager.getSessionLocal();
        if (sessionLocal == null) {
            json.put("code", 0);
            json.put("message", "更新红包失败,登录失效!");
            return json;
        }

        GiftMoneyQueryOrder giftMoneyQueryOrder = new GiftMoneyQueryOrder();
        giftMoneyQueryOrder.setStartDate(createGiftMoneyOrder.getStartDate());
        giftMoneyQueryOrder.setEndDate(createGiftMoneyOrder.getEndDate());
        giftMoneyQueryOrder.setGiveType(GiftMoneyGiveTypeEnum.getByCode(createGiftMoneyOrder.getGiveRuleType()));
        giftMoneyQueryOrder.setType(GiftMoneyTypeEnum.getByCode(createGiftMoneyOrder.getType()));
        giftMoneyQueryOrder.setGiftId(createGiftMoneyOrder.getGiftId());
        boolean exists = giftMoneyQueryService.checkExistsSameGiftMoney(giftMoneyQueryOrder);
        if(exists){
            json.put("code", 0);
            json.put("message", "新增红包失败,同一类型的红包，活动时间不能重叠!");
            return json;
        }


        createGiftMoneyOrder.setCreateUserid(sessionLocal.getUserId());
        createGiftMoneyOrder.setCreateUsername(sessionLocal.getUserName());
        YrdBaseResult result = giftMoneyService.updateGiftMoney(createGiftMoneyOrder);

        if (result.isSuccess()) {
            json.put("code", 1);
            json.put("message", "更新红包成功!");
        } else {
            json.put("code", 0);
            json.put("message", "更新红包失败:"+result.getMessage());

        }
        return json;
    }


    @ResponseBody
    @RequestMapping(value = "addGiftMoneySubmit")
    public Object addGiftMoneySubmit(CreateGiftMoneyOrder createGiftMoneyOrder) throws Exception {
        JSONObject json = new JSONObject();
        SessionLocal sessionLocal = SessionLocalManager.getSessionLocal();
        if (sessionLocal == null) {
            json.put("code", 0);
            json.put("message", "新增红包失败,登录失效!");
            return json;
        }
        GiftMoneyQueryOrder giftMoneyQueryOrder = new GiftMoneyQueryOrder();
        giftMoneyQueryOrder.setStartDate(createGiftMoneyOrder.getStartDate());
        giftMoneyQueryOrder.setType(GiftMoneyTypeEnum.getByCode(createGiftMoneyOrder.getType()));
        giftMoneyQueryOrder.setEndDate(createGiftMoneyOrder.getEndDate());
        giftMoneyQueryOrder.setGiveType(GiftMoneyGiveTypeEnum.getByCode(createGiftMoneyOrder.getGiveRuleType()));
        giftMoneyQueryOrder.setGiftId(createGiftMoneyOrder.getGiftId());

        boolean exists = giftMoneyQueryService.checkExistsSameGiftMoney(giftMoneyQueryOrder);
        if(exists){
            json.put("code", 0);
            json.put("message", "新增红包失败,同一类型的红包，活动时间不能重叠!");
            return json;
        }

        createGiftMoneyOrder.setCreateUserid(sessionLocal.getUserId());
        createGiftMoneyOrder.setCreateUsername(sessionLocal.getUserName());
        createGiftMoneyOrder.setOutBizNo(BusinessNumberUtil.gainOutBizNoNumber());
        YrdBaseResult result = giftMoneyService.addGiftMoney(createGiftMoneyOrder);
        if (result.isSuccess()) {
            json.put("code", 1);
            json.put("message", "新增红包成功!");
        } else {
            json.put("code", 0);
            json.put("message", "新增红包失败:"+result.getMessage());

        }
        return json;
    }


    @ResponseBody
    @RequestMapping("checkExistsGiftName")
    public Object checkExistsGiftName(String giftName, long giftId) throws Exception {
        JSONObject json = new JSONObject();
        if (giftMoneyQueryService.checkExistsGiftName(giftId, giftName)) {
            json.put("code", 0);
            json.put("message", "红包名称已经使用");
        } else {
            json.put("code", 1);
            json.put("message", "红包名称可以使用");
        }
        return json;
    }


    @ResponseBody
    @RequestMapping("changeGiftMoneyStatus")
    public Object changeGiftMoneyRuleStatus(String status, long giftId) throws Exception {
        JSONObject json = new JSONObject();
        ChangeGiftMoneyStatusOrder statusOrder = new ChangeGiftMoneyStatusOrder();
        SessionLocal sessionLocal = SessionLocalManager.getSessionLocal();
        if (sessionLocal == null) {
            json.put("code", 0);
            json.put("message", "更改红包状态失败,登录失效!");
            return json;
        }
        statusOrder.setCreateUserid(sessionLocal.getUserId());
        statusOrder.setCreateUsername(sessionLocal.getUserName());
        statusOrder.setGiftId(giftId);
        statusOrder.setStatus(status);
        YrdBaseResult result = giftMoneyService.updateGiftMoneyStatus(statusOrder);

        if (result.isSuccess()) {
            json.put("code", 1);
            json.put("message", "更新红包状态成功!");
        } else {
            json.put("code", 0);
            json.put("message", "更新红包状态失败:"+result.getMessage());
        }
        return json;
    }


    /**
     * 分页查询
     */
    @RequestMapping(value = "pageQueryGiftMoney")
    public String pageQueryGiftMoney(GiftMoneyQueryOrder queryOrder,
                                      PageParam pageParam, Model model) {
        try {
            queryOrder.setPageNumber(pageParam.getPageNo());
            queryOrder.setPageSize(pageParam.getPageSize());
            String giftName = null;
            if(StringUtil.isNotEmpty(queryOrder.getGiftName())){
                giftName = queryOrder.getGiftName();
                queryOrder.setGiftName("%"+queryOrder.getGiftName()+"%");
            }
            QueryBaseBatchResult<GiftMoneyInfo> page = giftMoneyQueryService
                    .queryGiftMoneyRule(queryOrder);
            queryOrder.setGiftName(giftName);
            model.addAttribute("queryConditions", queryOrder);
            model.addAttribute("page", PageUtil.getCovertPage(page));

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return BORROWING_MANAGE__PATH + "pageQueryGiftMoneyInfo.vm";
    }


    @RequestMapping(value = "handGiftMoneyAssign")
    public String handGiftMoneyAssign(Model model) {
        model.addAttribute("giveRules", GiftMoneyGiveTypeEnum.getAllEnum());
        return BORROWING_MANAGE__PATH + "handGiftMoney.vm";
    }


    @ResponseBody
    @RequestMapping("handGiftMoneyAssignSubmit")
    public Object handGiftMoneyAssignSubmit(HttpServletRequest request) throws Exception {
        JSONObject json = new JSONObject();
        HandGiftMoneyOrder giftMoneyOrder = new HandGiftMoneyOrder();
        WebUtil.setPoPropertyByRequest(giftMoneyOrder, request);
        giftMoneyOrder.setGiveTpe(GiftMoneyGiveTypeEnum.getByCode(request.getParameter("giveType")));
        giftMoneyOrder.setType(GiftMoneyTypeEnum.getByCode(request.getParameter("type")));
        SessionLocal sessionLocal = SessionLocalManager.getSessionLocal();
        if (sessionLocal == null) {
            json.put("code", 0);
            json.put("message", "手动发红包失败,登录失效!");
            return json;
        }
        String userName = request.getParameter("userName");
        UserInfo userInfo  = userQueryService.queryByUserName(userName).getQueryUserInfo();
        if (userInfo == null) {
            json.put("code", 0);
            json.put("message", "用户未查到");
            return json;
        }
        giftMoneyOrder.setUserId(userInfo.getUserId());
        try{
            YrdBaseResult result = giftMoneyAssignProcessService.handGiftMoney(giftMoneyOrder);
            if (result.isSuccess()) {
                json.put("code", 1);
                json.put("message", "手动发红包成功!");
                return json;
            } else {
                json.put("code", 0);
                json.put("message",result.getMessage());
                return  json;
            }
        }catch (Exception e){
            logger.error("手动发红包",e);
            json.put("code", 0);
            json.put("message",e.getMessage());
        }

        return json;
    }


}
