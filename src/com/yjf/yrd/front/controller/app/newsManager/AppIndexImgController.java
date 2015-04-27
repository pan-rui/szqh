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
@RequestMapping("backstage")
public class AppIndexImgController {
	@Autowired
	IAppPopService iAppPopService;

	private final String VM = "/backstage/appManager/indexImg/";

	/**
	 * 后台轮播图首页
	 * */
	@RequestMapping("appIndexImgManager_Tmp")
	public String appIndexImgManager(Model model) {
		Map<String, Object> conditions = new HashMap<String, Object>();
		List<Integer> types = new ArrayList<Integer>();
		types.add(110);
		conditions.put("type", types);
		List<AppPopInfoDO> list = iAppPopService
				.getListByConditions(conditions);
		model.addAttribute("list", list);
		return VM + "indexImgManager.vm";

	}

	/**
	 * 添加轮播图
	 * */
	@RequestMapping("addImgs")
	public String addImg() {
		return VM + "addImg.vm";

	}

	@ResponseBody
	@RequestMapping("addImgSubmit")
	public Object addImgSubmit(AppPopInfoDO info) {
		Map<String, String> map = new HashMap<String, String>();
		try {
			info.setType((short) 110);
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
	 * 更新轮播图
	 * */
	@RequestMapping("upDateImg")
	public String upDateImg(long popId, Model model) {
		AppPopInfoDO info = iAppPopService.getByPopId(popId);
		model.addAttribute("info", info);
		return VM + "upDateImg.vm";

	}

	@ResponseBody
	@RequestMapping("upDateImgSubmit")
	public Object upDateImgSubmit(AppPopInfoDO info) {
		Map<String, String> map = new HashMap<String, String>();
		try {
			info.setType((short) 110);
			iAppPopService.updateNotice(info);
			map.put("code", "1");
			map.put("message", "更新成功");
		} catch (Exception e) {
			map.put("code", "0");
			map.put("message", "更新失败");
		}

		return map;

	}

	/**
	 * 删除轮播图
	 * */
	@ResponseBody
	@RequestMapping("deleteImg")
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
