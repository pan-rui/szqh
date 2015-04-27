package com.yjf.yrd.backstage.controller.popManage;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.yjf.yrd.base.BaseAutowiredController;
import com.yjf.yrd.dataobject.AppPopInfoDO;
import com.yjf.yrd.page.PageParam;
import com.yjf.yrd.pop.IAppPopService;

@Controller
@RequestMapping("backstage")
public class AppHelpPopCenterController extends BaseAutowiredController {
	private final String VM_PATH = "/backstage/publicNotice/appHelpCenter/";
	private String VM_PATH1 = "/front/help/";
	@Autowired
	IAppPopService popService;
	
	@RequestMapping("appPopHelp")
	public String appHelpCenter(HttpSession session, PageParam pageParam, Model model) {
		Map<String, Object> conditions = new HashMap<String, Object>();
		List<Integer> types = new ArrayList<Integer>();
		types.add(4);
		types.add(5);
		conditions.put("type", types);
		model.addAttribute("page", popService.getPageByConditions(pageParam, conditions));
		return VM_PATH + "app-help-center.vm";
	}
	
	@RequestMapping("appPopHelp/addHelp")
	public String addHelp(HttpSession session, Model model) {
		Map<String, Object> conditions = new HashMap<String, Object>();
		List<Integer> types = new ArrayList<Integer>();
		types.add(5);
		conditions.put("type", types);
		List<AppPopInfoDO> modules = popService.getListByConditions(conditions);
		model.addAttribute("helpModules", modules);
		return VM_PATH + "add-app-help.vm";
	}
	
	@RequestMapping("appPopHelp/addHelpSubmit")
	@ResponseBody
	public Object addHelpSubmit(AppPopInfoDO info, HttpSession session) {
		JSONObject json = new JSONObject();
		if (5 == info.getType()) {
			info.setParentId(-1);
		}
		info.setAddTime(new Date());
		try {
			popService.addNotice(info);
			json.put("code", "1");
			json.put("message", "新增成功！");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			json.put("code", "0");
			json.put("message", "新增失败！");
		}
		
		return json;
	}
	
	@RequestMapping("appPopHelp/updateHelp")
	public String updateHelp(long popId, Model model) {
		Map<String, Object> conditions = new HashMap<String, Object>();
		List<Integer> types = new ArrayList<Integer>();
		types.add(5);
		conditions.put("type", types);
		List<AppPopInfoDO> modules = popService.getListByConditions(conditions);
		model.addAttribute("helpModules", modules);
		model.addAttribute("info", popService.getByPopId(popId));
		return VM_PATH + "update-app-help.vm";
	}
	
	@RequestMapping("appPopHelp/updateHelpSubmit")
	@ResponseBody
	public Object updateHelpSubmit(AppPopInfoDO info, HttpSession session) {
		JSONObject json = new JSONObject();
		info.setModifyTime(new Date());
		if (5 == info.getType()) {
			info.setParentId(-1);
		}
		try {
			popService.updateNotice(info);
			json.put("code", "1");
			json.put("message", "更新成功！");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			json.put("code", "0");
			json.put("message", "更新失败！");
		}
		return json;
	}
	
	@ResponseBody
	@RequestMapping("appPopHelp/deleteHelp")
	public Object deleteHelp(AppPopInfoDO info,HttpSession session) {
		JSONObject json = new JSONObject();
//		if (5 == info.getType()) {
//			info.setParentId(-1);
//		}
		try {
			popService.deleteNotice(info);
			json.put("message", "删除成功！");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			e.printStackTrace();
			json.put("message", "删除失败！");
		}
		return json;
	}
	
	@ResponseBody
	@RequestMapping(value = "appPopHelp/changeStatus")
	public Object changeStatus(long popId, short status) throws Exception {
		JSONObject jsonobj = new JSONObject();
		AppPopInfoDO info = popService.getByPopId(popId);
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
	
	private List<AppPopInfoDO> getShowList() {
		Map<String, Object> conditions = new HashMap<String, Object>();
		List<Integer> types = new ArrayList<Integer>();
		types.add(4);
		types.add(5);
		conditions.put("type", types);
		conditions.put("status", 2);
		List<AppPopInfoDO> showList = popService.getListByConditions(conditions);
		return showList;
	}
	
	@RequestMapping("appPopHelp/{popId}")
	public String popHelp(HttpSession session, @PathVariable long popId, Model model) {
		model.addAttribute("popHelp", popService.getByPopId(popId));
		Map<String, Object> conditions = new HashMap<String, Object>();
		List<Integer> types = new ArrayList<Integer>();
		types.add(4);
		conditions.put("type", types);
		conditions.put("parentId", popId);
		conditions.put("status", 2);
		List<AppPopInfoDO> showList = popService.getListByConditions(conditions);
		model.addAttribute("helps", getShowList());
		model.addAttribute("childs", showList);
		return VM_PATH1 + "appHelpCenter.vm";
	}
	
	/**
	 * App帮助中心接口
	 */
	public List<AppPopInfoDO> appHelpInterface() {
		Map<String, Object> conditions = new HashMap<String, Object>();
		List<Integer> types = new ArrayList<Integer>();
		types.add(5);
		conditions.put("type", types);
		List<AppPopInfoDO> modules = popService.getListByConditions(conditions);
//		for (AppPopInfoDO appPopInfoDO : modules) {
//			System.out.println(appPopInfoDO.getTitle());
//		}
		return modules;
	}
	
}
