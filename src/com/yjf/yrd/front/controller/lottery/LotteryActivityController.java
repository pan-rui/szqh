package com.yjf.yrd.front.controller.lottery;

import java.util.Random;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;

@Controller
@RequestMapping("lottery")
public class LotteryActivityController {

	@ResponseBody
	@RequestMapping("drawAward")
	public Object lottery(HttpSession session) {
		JSONObject json = new JSONObject();
		Random radon = new Random();
		int a = radon.nextInt(100);
		if (a % 2 == 0) {
			json.put("succes", true);
			json.put("message", "中奖XXX");
		} else {
			json.put("succes", false);
			json.put("message", "未中奖");
		}
		return json;

	}
}
