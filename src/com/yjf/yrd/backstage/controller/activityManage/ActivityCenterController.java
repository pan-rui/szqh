package com.yjf.yrd.backstage.controller.activityManage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.yjf.yrd.activity.IActivityService;
import com.yjf.yrd.activity.QueryActivityOrder;
import com.yjf.yrd.base.BaseAutowiredController;
import com.yjf.yrd.dataobject.ActivityDetail;
import com.yjf.yrd.dataobject.ActivityInfo;
import com.yjf.yrd.dataobject.GiftUseRecord;
import com.yjf.yrd.page.Page;
import com.yjf.yrd.page.PageParam;
import com.yjf.yrd.session.SessionLocalManager;

@Controller
@RequestMapping("backstage")
public class ActivityCenterController extends BaseAutowiredController{
	private String VM_PATH = "/backstage/activity/";
	@Autowired
	IActivityService iActivityService;
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Date.class, "startTime",new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"),true));
		binder.registerCustomEditor(Date.class, "endTime",new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"),true));
	}
	
	@RequestMapping("activityCenter")
	public String activityCenter(QueryActivityOrder queryActivityOrder,
			PageParam pageParam, Model model) {
		try{
		
			Page<ActivityInfo> pageActivity = iActivityService.queryListActivityInfo(queryActivityOrder, pageParam);
			model.addAttribute("page", pageActivity);
		}catch(Exception e){
			logger.error("查询活动失败",e);
		}
		return VM_PATH + "activity-center.vm";
	}
	
	@RequestMapping("addActivity")
	public String addActivity(HttpSession session, Model model) {
		return VM_PATH + "add-activity.vm";
	}
	
	@RequestMapping("addActivitySubmit")
	public String addActivitySubmit(ActivityInfo activityInfo, HttpSession session,Model model) {
		try {
			iActivityService.addActivityInfo(activityInfo);
		} catch (Exception e) {
			logger.error("新增活动失败",e);
		}
		return VM_PATH + "add-activity.vm";
	}
	
	@RequestMapping("updateActivity")
	public String updateActivity(long tblBaseId, Model model) {
		ActivityInfo activityInfo = iActivityService.getActivityInfoByTblBaseId(tblBaseId);
		model.addAttribute("activityInfo", activityInfo);
		return VM_PATH + "update-activity.vm";
	}
	@RequestMapping("updateActivitySubmit")
	public String updateActivitySubmit(ActivityInfo activityInfo, HttpSession session) {
		try {
			iActivityService.updateActivityInfo(activityInfo);
		} catch (Exception e) {
			logger.error("更新活动失败",e);
		}
		return VM_PATH + "update-activity.vm";
	}
	
	@ResponseBody
	@RequestMapping("updateStatus")
	public Object updateStatus(long tblBaseId,int status){
		ActivityInfo activityInfo = iActivityService.getActivityInfoByTblBaseId(tblBaseId);
		activityInfo.setStatus(status);
		JSONObject jsonobj = new JSONObject();
		try{
			iActivityService.updateActivityInfo(activityInfo);
			jsonobj.put("code", 1);
			jsonobj.put("message", "更新状态成功");
		}catch(Exception e){
			logger.error("更新状态失败",e);
			jsonobj.put("code", 0);
			jsonobj.put("message", "更新状态失败");
		}
		return jsonobj;
	}
	
	@RequestMapping("activityDetailPage")
	public String activityDetailPage(QueryActivityOrder queryActivityOrder, PageParam pageParam, Model model) {
		try{
			Map<String, Object> giftNewConditions = new HashMap<String, Object>();
			List<Integer> status = new ArrayList<Integer>(); 
			status.add(0);
			status.add(1);
			status.add(2);
			giftNewConditions.put("status", status);
			Page<ActivityDetail> page = iActivityService.getActivityDetailPage(giftNewConditions, pageParam);
			model.addAttribute("page", page);
		}catch(Exception e){
			logger.error("查询失败",e);
		}
		return VM_PATH + "activity-party-page.vm";
	}
}
