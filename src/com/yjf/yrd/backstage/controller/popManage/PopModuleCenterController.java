package com.yjf.yrd.backstage.controller.popManage;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.yjf.yrd.base.BaseAutowiredController;
import com.yjf.yrd.dataobject.PopInfoDO;
import com.yjf.yrd.page.Page;
import com.yjf.yrd.page.PageParam;
import com.yjf.yrd.pop.IPopModuleService;
import com.yjf.yrd.pop.IPopService;
import com.yjf.yrd.util.DateUtil;
import com.yjf.yrd.ws.info.PopModuleVOInfo;
import com.yjf.yrd.ws.order.PopModuleOrder;

@Controller
@RequestMapping("backstage")
public class PopModuleCenterController extends BaseAutowiredController {
	private final String VM_PATH = "/backstage/publicNotice/popCenter/";
	private final String MODULE_VM_PATH = "/backstage/publicNotice/popModule/";
	@Autowired
	IPopService popService;
	
	@Autowired
	IPopModuleService popModuleService;
	
	@RequestMapping("popModuleCenter")
	public String popModuleCenter(HttpSession session, PageParam pageParam, Model model,PopInfoDO order) {
		Map<String, Object> conditions = new HashMap<String, Object>();
		List<PopModuleVOInfo> moduleTypes = popModuleService.getAllModules();
		model.addAttribute("moduleTypes", moduleTypes);//模块类型下拉框
		conditions.put("type", order.getType());
		conditions.put("status", order.getStatus());
		model.addAttribute("order", order);
		model.addAttribute("page", popService.getPageByConditionsNew(pageParam, conditions));
		return VM_PATH + "notice-center.vm";
	}
	
	
	
	@RequestMapping("popModuleCenter/addNotice")
	public String addNotice(HttpSession session, Model model) {
		List<PopModuleVOInfo> moduleTypes = popModuleService.getAllModules();
		model.addAttribute("moduleTypes", moduleTypes);
		return VM_PATH + "add-notice.vm";
	}
	
	@RequestMapping("popModuleCenter/addNoticeSubmit")
	public String addNoticeSubmit(PopInfoDO info, HttpSession session, Model model) {
		info.setAddTime(new Date());
		popService.addNotice(info);
		List<PopModuleVOInfo> moduleTypes = popModuleService.getAllModules();
		model.addAttribute("moduleTypes", moduleTypes);
		return VM_PATH + "add-notice.vm";
	}
	
	@RequestMapping("popModuleCenter/updateNotice")
	public String updateNotice(long popId, Model model) {
		List<PopModuleVOInfo> moduleTypes = popModuleService.getAllModules();
		model.addAttribute("moduleTypes", moduleTypes);
		model.addAttribute("info", popService.getByPopId(popId));
		return VM_PATH + "update-notice.vm";
	}
	
	@RequestMapping("popModuleCenter/updateNoticeSubmit")
	public String updateNoticeSubmit(PopInfoDO info,String addTime2, HttpSession session, Model model) {
		info.setModifyTime(new Date());
		try {
			info.setAddTime(DateUtil.simpleFormatDate(addTime2));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		popService.updateNotice(info);
		List<PopModuleVOInfo> moduleTypes = popModuleService.getAllModules();
		model.addAttribute("moduleTypes", moduleTypes);
		return VM_PATH + "update-notice.vm";
	}
	
	@ResponseBody
	@RequestMapping(value = "popModuleCenter/changeStatus")
	public Object changeStatus(long popId, short status) throws Exception {
		JSONObject jsonobj = new JSONObject();
		PopInfoDO info = popService.getByPopIdNew(popId);
		info.setStatus(status);
		try {
			popService.updateNotice(info);
			jsonobj.put("code", 1);
			jsonobj.put("message", "执行成功！");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			jsonobj.put("code", 0);
			jsonobj.put("message", "执行失败！");
		}
		return jsonobj;
	}
	
	@RequestMapping("popModuleList")
	public String popModuleList(HttpSession session, PageParam pageParam, Model model,PopModuleOrder popModuleOrder) {

		List<Integer> statusList = new ArrayList<Integer>();
		statusList.add(1);
		statusList.add(2);
				
		Page<PopModuleVOInfo>  page = popModuleService.getPageByConditions(pageParam, statusList, popModuleOrder);
		model.addAttribute("page", page);
		return MODULE_VM_PATH + "pop_module_list.vm";
	}
	
	
	
	@RequestMapping("popModule/toModify")
	public String popModuleToModify(HttpSession session,Model model,long moduleId) {
		PopModuleVOInfo popModule = new PopModuleVOInfo();
		if(moduleId!=0){
			 popModule = popModuleService.getPopModule(moduleId);
		}
		model.addAttribute("popModule", popModule);
		return MODULE_VM_PATH + "pop_module_add.vm";
	}
	
	@ResponseBody
	@RequestMapping("popModule/doModify")
	public Object popModuleModify(HttpSession session, PageParam pageParam, Model model,PopModuleOrder popModuleOrder) {

		JSONObject jsonobj = new JSONObject();
		try {
			long moduleId = popModuleOrder.getModuleId();
			if(moduleId==0){
				popModuleOrder.setStatus(1);
				popModuleService.addPopModule(popModuleOrder);
			}else{
				popModuleService.updatePopModule(popModuleOrder);
			}
			jsonobj.put("code", 1);
			jsonobj.put("message", "执行成功！");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			jsonobj.put("code", 0);
			jsonobj.put("message", "执行失败！");
		}
		return jsonobj;
	}
	
	@ResponseBody
	@RequestMapping("popModule/online")
	public Object popModuleOnline(HttpSession session,Model model,long moduleId) {
		JSONObject jsonobj = new JSONObject();
		try {
			popModuleService.onlinePopModule(moduleId);
			jsonobj.put("code", 1);
			jsonobj.put("message", "执行成功！");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			jsonobj.put("code", 0);
			jsonobj.put("message", "执行失败！");
		}
		return jsonobj;
	}
	
	@ResponseBody
	@RequestMapping("popModule/offline")
	public Object popModuleOffline(HttpSession session,Model model,long moduleId) {
		JSONObject jsonobj = new JSONObject();
		try {
			popModuleService.offlinePopModule(moduleId);
			jsonobj.put("code", 1);
			jsonobj.put("message", "执行成功！");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			jsonobj.put("code", 0);
			jsonobj.put("message", "执行失败！");
		}
		return jsonobj;
	}
	
	@ResponseBody
	@RequestMapping("popModule/delete")
	public Object popModuleDelete(HttpSession session,Model model,long moduleId) {
		JSONObject jsonobj = new JSONObject();
		try {
			popModuleService.deletePopModule(moduleId);
			jsonobj.put("code", 1);
			jsonobj.put("message", "执行成功！");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			jsonobj.put("code", 0);
			jsonobj.put("message", "执行失败！");
		}
		return jsonobj;
	}
	
	
	
}
