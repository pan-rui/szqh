package com.yjf.yrd.front.controller.app.newVers.e8Trunck.v2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yjf.common.lang.util.DateUtil;
import com.yjf.yrd.dataobject.AppPopInfoDO;
import com.yjf.yrd.dataobject.PopInfoDO;
import com.yjf.yrd.page.Page;
import com.yjf.yrd.page.PageParam;
import com.yjf.yrd.pop.IAppPopService;
import com.yjf.yrd.pop.IPopModuleService;
import com.yjf.yrd.pop.IPopService;
import com.yjf.yrd.ws.info.PopModuleVOInfo;

/**
 * 2015.4.15前拉的平台使用 ，以后不使用
 * 
 * */
@Controller
@RequestMapping("app")
public class NewAppNewsController {
	@Autowired
	IPopService popService;
	@Autowired
	IPopModuleService popModuleService;
	
	@Autowired
	IAppPopService iAppPopService;
	
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping("getAllNews.htm")
	public JSONObject getAllNews(HttpSession session, PageParam pageParam, Integer pageNumber,
									String moduleCode, Integer type) {
		JSONObject json = new JSONObject();
		if (type == null) {
			session.setAttribute("defaultMCode", moduleCode);
			PopModuleVOInfo moduleInfo = popModuleService.getPopModule(moduleCode);
			type = moduleInfo.getType();
		}
		Map<String, Object> conditions = new HashMap<String, Object>();
		conditions.put("type", type);
		conditions.put("status", 2);
		if (pageNumber != null) {
			pageParam.setPageNo(pageNumber);
		}
		Page<PopInfoDO> page = popService.getPageByConditionsNew(pageParam, conditions);
		if (page.getTotalCount() > 0) {
			List<Map<String, String>> list = new ArrayList<Map<String, String>>();
			for (PopInfoDO info : page.getResult()) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("popId", String.valueOf(info.getPopId()));
				map.put("title", info.getTitle());
				map.put("content", info.getContent());
				map.put("addTime", DateUtil.simpleFormat(info.getAddTime()));
				list.add(AppCommonUtil.cleanNull(map));
			}
			json.put("list", list);
			json.put("code", 1);
			json.put("message", "获取新闻信息成功");
			
		} else {
			json.put("code", 0);
			json.put("message", "暂无新闻信息");
		}
		json.put("totalPage", page.getTotalPageCount());
		return json;
	}
	
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping("aboutUs.htm")
	public JSONObject aboutUs() {
		JSONObject json = new JSONObject();
		Map<String, Object> conditions = new HashMap<String, Object>();
		List<Integer> types = new ArrayList<Integer>();
		types.add(120);
		conditions.put("type", types);
		List<AppPopInfoDO> list = iAppPopService.getListByConditions(conditions);
		if (list.size() > 0) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("title", list.get(0).getTitle());
			map.put("content", list.get(0).getContent());
			json.put("aboutUs", map);
			json.put("code", 1);
			json.put("message", "查询成功");
		} else {
			json.put("code", 0);
			json.put("message", "暂无信息");
		}
		return json;
		
	}
}
