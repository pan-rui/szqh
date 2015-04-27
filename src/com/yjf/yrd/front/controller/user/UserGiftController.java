package com.yjf.yrd.front.controller.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.yjf.yrd.activity.IActivityService;
import com.yjf.yrd.activity.QueryActivityOrder;
import com.yjf.yrd.base.BaseAutowiredController;
import com.yjf.yrd.dataobject.ActivityDetail;
import com.yjf.yrd.dataobject.ActivityInfo;
import com.yjf.yrd.dataobject.GiftUseRecord;
import com.yjf.yrd.page.Page;
import com.yjf.yrd.page.PageParam;
import com.yjf.yrd.session.SessionLocalManager;
import com.yjf.yrd.ws.enums.PayTypeEnum;

@Controller
@RequestMapping("userGift")
public class UserGiftController extends BaseAutowiredController {
	/** 返回页面路径 */
	String				USER_GIFT_PATH	= "/front/user/gift/";
	@Autowired
	IActivityService	iActivityService;
	
	@RequestMapping("giftCenter")
	public String giftCenter(HttpSession session, Model model, String checkType, PageParam pageParam)
																										throws Exception {
		session.setAttribute("current", 9);
		Map<String, Object> conditions = new HashMap<String, Object>();
		conditions.put("userId", SessionLocalManager.getSessionLocal().getUserId());
		conditions.put("giftType", PayTypeEnum.WITHDRAW.code());
		long giftCount = iActivityService.getActivityGiftCount(conditions);
		model.addAttribute("withDrawAmout", giftCount);
		if (checkType == null) {
			checkType = "1";
		}
		if ("1".equals(checkType)) {
			Map<String, Object> giftNewConditions = new HashMap<String, Object>();
			List<Integer> status = new ArrayList<Integer>();
			status.add(2);
			giftNewConditions.put("status", status);
			giftNewConditions.put("userId", SessionLocalManager.getSessionLocal().getUserId());
			Page<ActivityDetail> page = iActivityService.getActivityDetailPage(giftNewConditions,
				pageParam);
			model.addAttribute("page", page);
		} else {
			QueryActivityOrder queryActivityOrder = new QueryActivityOrder();
			queryActivityOrder.setUserName(SessionLocalManager.getSessionLocal().getUserName());
			Page<GiftUseRecord> page = iActivityService.getPageGiftUsedRecord(queryActivityOrder,
				pageParam);
			model.addAttribute("page", page);
		}
		model.addAttribute("checkType", checkType);
		return USER_GIFT_PATH + "giftCenter.vm";
	}
	
	@RequestMapping("giftDetail")
	public String giftDetail(HttpSession session, long giftId, Model model) throws Exception {
		session.setAttribute("current", 9);
		ActivityDetail detail = iActivityService.getActivityDetailByBaseId(giftId);
		model.addAttribute("info", detail);
		model.addAttribute("activityInfo",
			iActivityService.getActivityInfoByTblBaseId(detail.getActivityId()));
		return USER_GIFT_PATH + "giftDetail.vm";
	}
	
	@RequestMapping("activityCenter")
	public String activityCenter(HttpSession session, PageParam pageParam, Model model)
																						throws Exception {
		session.setAttribute("current", 9);
		QueryActivityOrder queryOrder = new QueryActivityOrder();
		queryOrder.setStatus(2);
		Page<ActivityInfo> page = iActivityService.queryListActivityInfo(queryOrder, pageParam);
		model.addAttribute("total", page.getResult().size());
		model.addAttribute("page", page);
		return USER_GIFT_PATH + "activityCenter.vm";
	}
	
	@RequestMapping("activityDetail")
	public String activityDetail(HttpSession session, long activityId, Model model)
																					throws Exception {
		session.setAttribute("current", 9);
		model.addAttribute("info", iActivityService.getActivityInfoByTblBaseId(activityId));
		return USER_GIFT_PATH + "activityDetail.vm";
	}
}
