package com.yjf.yrd.front.controller.app.newsManager;

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
@RequestMapping("app")
public class AppNewsController extends BaseAutowiredController {
	
	private final String VM_PATH = "/backstage/appManager/newsManager/";
	@Autowired
	IAppPopService popService;
	
	@RequestMapping("appNews")
	public String appNews(HttpSession session, PageParam pageParam, Model model) {
		Map<String, Object> conditions = new HashMap<String, Object>();
		List<Integer> types = new ArrayList<Integer>();
		types.add(6);
		types.add(7);
		conditions.put("type", types);
		model.addAttribute("page", popService.getPageByConditions(pageParam, conditions));
		return VM_PATH + "appNewsList.vm";
	}
	
	@RequestMapping("addNews")
	public String addNews(HttpSession session, Model model) {
		Map<String, Object> conditions = new HashMap<String, Object>();
		List<Integer> types = new ArrayList<Integer>();
		types.add(6);
		conditions.put("type", types);
		List<AppPopInfoDO> modules = popService.getListByConditions(conditions);
		model.addAttribute("helpModules", modules);
		return VM_PATH + "addNews.vm";
	}
	
	@RequestMapping("addNewsSubmit")
	@ResponseBody
	public Object addNewsSubmit(AppPopInfoDO info, HttpSession session) {
		JSONObject json = new JSONObject();
		if (6 == info.getType()) {
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
	
	@RequestMapping("updateNews")
	public String updateNews(long popId, Model model) {
		Map<String, Object> conditions = new HashMap<String, Object>();
		List<Integer> types = new ArrayList<Integer>();
		types.add(6);
		conditions.put("type", types);
		List<AppPopInfoDO> modules = popService.getListByConditions(conditions);
		model.addAttribute("helpModules", modules);
		model.addAttribute("info", popService.getByPopId(popId));
		return VM_PATH + "updateNews.vm";
	}
	
	@RequestMapping("updateNewsSubmit")
	@ResponseBody
	public Object updateNewsSubmit(AppPopInfoDO info, HttpSession session) {
		JSONObject json = new JSONObject();
		info.setModifyTime(new Date());
		if (6 == info.getType()) {
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
	@RequestMapping("deleteNews")
	public Object deleteNews(AppPopInfoDO info, HttpSession session) {
		JSONObject json = new JSONObject();
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
	
	private List<AppPopInfoDO> getShowList() {
		Map<String, Object> conditions = new HashMap<String, Object>();
		List<Integer> types = new ArrayList<Integer>();
		types.add(6);
		conditions.put("type", types);
		conditions.put("status", 2);
		List<AppPopInfoDO> showList = popService.getListByConditions(conditions);
		return showList;
	}
	
	@RequestMapping("appNewsInfo/{popId}")
	public String appNewsInfo(HttpSession session, @PathVariable long popId, Model model) {
		model.addAttribute("popHelp", popService.getByPopId(popId));
		Map<String, Object> conditions = new HashMap<String, Object>();
		List<Integer> types = new ArrayList<Integer>();
		types.add(6);
		conditions.put("type", types);
		conditions.put("parentId", popId);
		conditions.put("status", 2);
		List<AppPopInfoDO> showList = popService.getListByConditions(conditions);
		model.addAttribute("helps", getShowList());
		model.addAttribute("childs", showList);
		return VM_PATH + "viewNewsInfo.vm";
	}
	
	/**
	 * App帮助中心接口
	 */
	public List<AppPopInfoDO> appHelpInterface() {
		Map<String, Object> conditions = new HashMap<String, Object>();
		List<Integer> types = new ArrayList<Integer>();
		types.add(6);
		conditions.put("type", types);
		List<AppPopInfoDO> modules = popService.getListByConditions(conditions);
		//		for (AppPopInfoDO appPopInfoDO : modules) {
		//			System.out.println(appPopInfoDO.getTitle());
		//		}
		return modules;
	}
	
}
