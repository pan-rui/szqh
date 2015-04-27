package com.yjf.yrd.front.controller.app.newVers.e8Trunck.v2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.yjf.yrd.dataobject.AppPopInfoDO;
import com.yjf.yrd.pop.IAppPopService;

@Controller
@RequestMapping("app")
public class NewAppIndexImgController {

	@Autowired
	IAppPopService iAppPopService;

	/**
	 * 获取轮播图地址
	 * 
	 * */
	@ResponseBody
	@RequestMapping("getIndexImg.htm")
	public JSONObject getIndexImg() {
		JSONObject json = new JSONObject();
		Map<String, Object> conditions = new HashMap<String, Object>();
		List<Integer> types = new ArrayList<Integer>();
		types.add(110);
		conditions.put("type", types);
		conditions.put("status", "1");
		List<AppPopInfoDO> result = iAppPopService
				.getListByConditions(conditions);
		if (result.size() > 0) {
			List<Map<String, String>> list = new ArrayList<Map<String, String>>();
			for (AppPopInfoDO info : result) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("name", info.getTitle());
				map.put("imgUrl", info.getContent());
				map.put("linkUrl", info.getRemark());
				list.add(AppCommonUtil.cleanNull(map));
			}
			json.put("imgInfo", list);
			json.put("code", 1);
			json.put("message", "获取轮播图成功");
		} else {
			json.put("code", 0);
			json.put("message", "暂无轮播图");
		}
		return json;

	}

}
