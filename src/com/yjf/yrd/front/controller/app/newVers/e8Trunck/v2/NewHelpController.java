package com.yjf.yrd.front.controller.app.newVers.e8Trunck.v2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yjf.common.lang.util.ListUtil;
import com.yjf.yrd.dataobject.AppPopInfoDO;
import com.yjf.yrd.pop.IAppPopService;

/**
 * @Filename appNewHelpController.java
 * 
 * @Description
 * 
 * @Version 2.0
 * 
 * @Author zhaohaibing
 * 
 * @Email abing@yiji.com
 * 
 * @Date: 2014-12-30
 * 
 */
@Controller
@RequestMapping("app")
public class NewHelpController {
	
	@Autowired
	IAppPopService popService;
	
	/**
	 * @throws IOException
	 * */
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping("HelpContents.htm")
	public JSONObject helpContents(HttpServletResponse response) throws IOException {
		JSONObject json = new JSONObject();
		List<Map<String, String>> helpList = new ArrayList<Map<String, String>>();
		Map<String, Object> conditions = new HashMap<String, Object>();
		List<Integer> types = new ArrayList<Integer>();
		types.add(4);
		types.add(5);
		conditions.put("type", types);
		conditions.put("status", 2);
		List<AppPopInfoDO> showList = popService.getListByConditions(conditions);
		long updateTime = 0;
		if (ListUtil.isNotEmpty(showList)) {
			for (AppPopInfoDO info : showList) {
				Map<String, String> map = new HashMap<String, String>();
				if (info.getAddTime().getTime() > updateTime) {
					updateTime = info.getAddTime().getTime();
				}
				map.put("title", info.getTitle());
				map.put("content", popService.getByPopId(info.getPopId()).getContent());
				helpList.add(AppCommonUtil.cleanNull(map));
			}
			json.put("updateTime", updateTime);
			json.put("helList", helpList);
			json.put("code", 1);
			json.put("message", "获取帮助信息成功");
		} else {
			json.put("code", 0);
			json.put("message", "暂无帮助信息");
		}
		return json;
		
	}
	
	/**
	 * 帮助中心内容是否有更新
	 * 
	 * @throws IOException
	 * 
	 * */
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping("appHelpRefresh.htm")
	public JSONObject appHelpRefresh(HttpServletResponse response, Long updateTime) {
		JSONObject json = new JSONObject();
		if (updateTime != null && updateTime < getUpdateTime()) {
			json.put("code", 1);
			json.put("message", "当前有可更新内容");
		} else {
			json.put("code", 0);
			json.put("message", "当前没有可更新内容");
		}
		
		return json;
		
	}
	
	/**
	 * 获取帮助中心最近更新时间
	 * */
	public long getUpdateTime() {
		Map<String, Object> conditions = new HashMap<String, Object>();
		List<Integer> types = new ArrayList<Integer>();
		types.add(4);
		types.add(5);
		conditions.put("type", types);
		conditions.put("status", 2);
		List<AppPopInfoDO> showList = popService.getListByConditions(conditions);
		long leastTime = 0;
		if (ListUtil.isNotEmpty(showList)) {
			for (AppPopInfoDO info : showList) {
				if (info.getModifyTime() != null && info.getModifyTime().getTime() > leastTime) {
					leastTime = info.getModifyTime().getTime();
				} else if (info.getAddTime().getTime() > leastTime) {
					leastTime = info.getAddTime().getTime();
				}
			}
		}
		return leastTime;
		
	}
	
}
