package com.yjf.yrd.front.controller.app;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.yjf.yrd.base.BaseAutowiredController;
import com.yjf.yrd.dataobject.AppPopInfoDO;
import com.yjf.yrd.pop.IAppPopService;


@Controller
@RequestMapping("app")
public class AppHelpCenterController extends BaseAutowiredController{
	
	@Autowired
	IAppPopService popService;
	
	@RequestMapping("helpCenter.htm")
	public String HelpCenter(HttpServletRequest request,HttpServletResponse response,HttpSession session,Model model) throws IOException {
		logger.info("手机app请求获取帮助中心信息！");
		JSONObject json = new JSONObject();	
		Map<String, Object> conditions = new HashMap<String, Object>();
		List<Integer> types = new ArrayList<Integer>();
		types.add(4);
		types.add(5);
		conditions.put("type", types);
		conditions.put("status", 2);
		List<AppPopInfoDO> modules = popService.getListByConditions(conditions);
		Map<String, Object> result = new HashMap<String, Object>();
		for (AppPopInfoDO appPopInfoDO : modules) {
			String title = appPopInfoDO.getTitle();
			String content = appPopInfoDO.getContent();
			result.put("title", title);
			result.put("content", content);
		}
		json.put("code","1");
		json.put("message", "App访问帮助中心成功");
		json.put("helpModules", modules);
		response.getWriter().print(json.toJSONString());
		logger.info("手机app请求帮助中心成功:helpModules={}",modules);
		return null;
	}

}
