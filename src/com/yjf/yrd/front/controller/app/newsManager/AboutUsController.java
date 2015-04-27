package com.yjf.yrd.front.controller.app.newsManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yjf.yrd.dataobject.AppPopInfoDO;
import com.yjf.yrd.pop.IAppPopService;

/**
 * 
 * 
 * @Filename AppIndexImgController.java
 * 
 * @Description
 * 
 * @Version 1.0
 * 
 * @Author zhaohaibing
 * 
 * @Email abing@yiji.com
 * 
 * @Date: 2015-1-19
 * 
 * 
 */

//@Controller
@RequestMapping("app")
public class AboutUsController {
	@Autowired
	IAppPopService iAppPopService;

	private final String VM_ = "/backstage/appManager/aboutusManager/";

	/**
	 * app关于我们
	 * */
	@RequestMapping("aboutUsManager")
	public String aboutUsManager(Model model) {
		Map<String, Object> conditions = new HashMap<String, Object>();
		List<Integer> types = new ArrayList<Integer>();
		types.add(120);
		conditions.put("type", types);
		List<AppPopInfoDO> list = iAppPopService
				.getListByConditions(conditions);
		model.addAttribute("list", list);
		return VM_ + "aboutUsIndex.vm";

	}

	@RequestMapping("addAboutUs")
	public String addAboutUs(Model model) {
		Map<String, Object> conditions = new HashMap<String, Object>();
		List<Integer> types = new ArrayList<Integer>();
		types.add(120);
		conditions.put("type", types);
		List<AppPopInfoDO> list = iAppPopService
				.getListByConditions(conditions);
		if (list.size() > 0) {
			model.addAttribute("list", list);
			return VM_ + "aboutUsIndex.vm";
		}
		return VM_ + "addAboutUs.vm";

	}

	@ResponseBody
	@RequestMapping("addAboutUsSubmit")
	public Object addAboutUsSubmit(AppPopInfoDO info) {
		Map<String, String> map = new HashMap<String, String>();
		try {
			info.setType((short) 120);
			iAppPopService.addNotice(info);
			map.put("code", "1");
			map.put("message", "添加成功");
		} catch (Exception e) {
			map.put("code", "0");
			map.put("message", "添加失败");
		}

		return map;
	}

	/**
	 * 更新关于我们
	 * */
	@RequestMapping("upDateAboutUs")
	public String upDateAboutUs(long popId, Model model) {
		AppPopInfoDO info = iAppPopService.getByPopId(popId);
		model.addAttribute("info", info);
		return VM_ + "upDateAboutUs.vm";

	}

	@ResponseBody
	@RequestMapping("upDateAboutUsSubmit")
	public Object upDateAboutUsSubmit(AppPopInfoDO info) {
		Map<String, String> map = new HashMap<String, String>();
		try {
			info.setType((short) 120);
			iAppPopService.updateNotice(info);
			map.put("code", "1");
			map.put("message", "更新成功");
		} catch (Exception e) {
			map.put("code", "0");
			map.put("message", "更新失败");
		}

		return map;

	}

	@ResponseBody
	@RequestMapping("deleteAboutUs")
	public Object deleteImg(AppPopInfoDO info) {

		Map<String, String> map = new HashMap<String, String>();
		try {
			iAppPopService.deleteNotice(info);
			map.put("code", "1");
			map.put("message", "删除成功");
		} catch (Exception e) {
			map.put("code", "0");
			map.put("message", "删除失败");
		}

		return map;
	}

}
