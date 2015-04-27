package com.yjf.yrd.backstage.controller.bannerNews;

import com.alibaba.fastjson.JSONObject;
import com.yjf.yrd.base.BaseAutowiredController;
import com.yjf.yrd.dataobject.PopInfoDO;
import com.yjf.yrd.page.PageParam;
import com.yjf.yrd.pop.IPopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.*;

@Controller
@RequestMapping("backstage")
public class BannerNewsCenterController extends BaseAutowiredController {
	private final String VM_PATH = "/backstage/bannerNews/";
	@Autowired
	IPopService popService;
	
	@RequestMapping("bannerNews")
	public String bannerNews(HttpSession session, PageParam pageParam, Model model,String type) {
		Map<String, Object> conditions = new HashMap<String, Object>();
		List<String> types = new ArrayList<String>();
		types.add(type);
	    conditions.put("type", types);
		model.addAttribute("page", popService.getPageByConditions(pageParam, conditions));
        model.addAttribute("type",type);
		return VM_PATH + "bannerNews-center.vm";
	}
	
	@RequestMapping("addBannerNews")
	public String addBannerNews(HttpSession session, Model model,String type) {
        model.addAttribute("type",type);
		return VM_PATH + "add-bannerNews.vm";
	}
	
	@RequestMapping("addBannerNewsSubmit")
	public String addBannerNewsSubmit(PopInfoDO info, HttpSession session) {
		info.setAddTime(new Date());
        info.setModifyTime(info.getAddTime());
		popService.addNotice(info);
		return VM_PATH + "add-bannerNews.vm";
	}
	
	@RequestMapping("updateBannerNews")
	public String updateNotice(long popId, Model model) {
		model.addAttribute("info", popService.getByPopId(popId));
		return VM_PATH + "update-bannerNews.vm";
	}
	
	@RequestMapping("updateBannerNewsSubmit")
	public String updateBannerNewsSubmit(PopInfoDO info, HttpSession session) {
		info.setModifyTime(new Date());
		popService.updateNotice(info);
		return VM_PATH + "update-bannerNews.vm";
	}
	
	@ResponseBody
	@RequestMapping(value = "changeBannerNewsStatus")
	public Object changeBannerNewsStatus(long popId, short status) throws Exception {
		JSONObject jsonobj = new JSONObject();
		PopInfoDO info = popService.getByPopId(popId);
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
}
