package com.yjf.yrd.front.controller.app.newVers.e8Trunck;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;

@Controller
@RequestMapping("appNew")
public class AppNewScropPicController {
	@RequestMapping("getIndexPicURL")
	public String getPicURL(HttpServletResponse response) throws IOException {
		JSONObject json = new JSONObject();
		json.put("message", "暂无此功能");
		response.getWriter().print(json.toJSONString());
		return null;
		
	}
	
}
