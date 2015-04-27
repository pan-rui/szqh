package com.yjf.yrd.backstage.controller.popManage;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.yjf.yrd.base.BaseAutowiredController;
import com.yjf.yrd.dataobject.PopInfoDO;
import com.yjf.yrd.page.PageParam;
import com.yjf.yrd.pop.IPopService;

@Controller
@RequestMapping("backstage")
public class HelpPopCenterController extends BaseAutowiredController {
	private final String VM_PATH = "/backstage/publicNotice/helpCenter/";
	private final String NEWVM_PATH = "/backstage/publicNotice/newHelpCenter/";
	private final String FWVM_PATH = "/backstage/publicNotice/fwHelpCenter/";
	@Autowired
	IPopService popService;
	
	@RequestMapping("popHelp")
	public String helpCenter(HttpSession session, PageParam pageParam, Model model) {
		Map<String, Object> conditions = new HashMap<String, Object>();
		List<Integer> types = new ArrayList<Integer>();
		types.add(4);
		types.add(5);
		conditions.put("type", types);
		model.addAttribute("page", popService.getPageByConditions(pageParam, conditions));
		return VM_PATH + "help-center.vm";
	}
	
	@RequestMapping("popHelp/addHelp")
	public String addHelp(HttpSession session, Model model) {
		Map<String, Object> conditions = new HashMap<String, Object>();
		List<Integer> types = new ArrayList<Integer>();
		types.add(5);
		conditions.put("type", types);
		List<PopInfoDO> modules = popService.getListByConditions(conditions);
		model.addAttribute("helpModules", modules);
		return VM_PATH + "add-help.vm";
	}
	
	@RequestMapping("popHelp/addHelpSubmit")
	@ResponseBody
	public Object addHelpSubmit(PopInfoDO info, HttpSession session) {
		JSONObject json = new JSONObject();
		if (5 == info.getType()) {
			info.setParentId(-1);
		}
		info.setAddTime(new Date());
        info.setModifyTime(info.getAddTime());
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
	
	@RequestMapping("popHelp/updateHelp")
	public String updateHelp(long popId, Model model) {
		Map<String, Object> conditions = new HashMap<String, Object>();
		List<Integer> types = new ArrayList<Integer>();
		types.add(5);
		conditions.put("type", types);
		List<PopInfoDO> modules = popService.getListByConditions(conditions);
		model.addAttribute("helpModules", modules);
		model.addAttribute("info", popService.getByPopId(popId));
		return VM_PATH + "update-help.vm";
	}
	//新帮助中心
	@RequestMapping("newPopHelp")
	public String newHelpCenter(HttpSession session, PageParam pageParam, Model model) {
		Map<String, Object> conditions = new HashMap<String, Object>();
		List<Integer> types = new ArrayList<Integer>();
		types.add(6);
		types.add(7);
		conditions.put("type", types);
		model.addAttribute("page", popService.getPageByConditions(pageParam, conditions));
		return NEWVM_PATH + "help-center.vm";
	}
	
	@RequestMapping("newPopHelp/addHelp")
	public String newAddHelp(HttpSession session, Model model) {
		Map<String, Object> conditions = new HashMap<String, Object>();
		List<Integer> types = new ArrayList<Integer>();
		types.add(7);
		conditions.put("type", types);
		List<PopInfoDO> modules = popService.getListByConditions(conditions);
		model.addAttribute("helpModules", modules);
		return NEWVM_PATH + "add-help.vm";
	}
	@RequestMapping("newPopHelp/updateHelp")
	public String newUpdateHelp(long popId, Model model) {
		Map<String, Object> conditions = new HashMap<String, Object>();
		List<Integer> types = new ArrayList<Integer>();
		types.add(7);
		conditions.put("type", types);
		List<PopInfoDO> modules = popService.getListByConditions(conditions);
		model.addAttribute("helpModules", modules);
		model.addAttribute("info", popService.getByPopId(popId));
		return NEWVM_PATH + "update-help.vm";
	}
	//end
	//服务专区fxd
	@RequestMapping("fwPopHelp")
	public String fwHelpCenter(HttpSession session, PageParam pageParam, Model model) {
		Map<String, Object> conditions = new HashMap<String, Object>();
		List<Integer> types = new ArrayList<Integer>();
		types.add(8);
		types.add(9);
		conditions.put("type", types);
		model.addAttribute("page", popService.getPageByConditions(pageParam, conditions));
		return FWVM_PATH + "help-center.vm";
	}
	
	@RequestMapping("fwPopHelp/addHelp")
	public String fwAddHelp(HttpSession session, Model model) {
		Map<String, Object> conditions = new HashMap<String, Object>();
		List<Integer> types = new ArrayList<Integer>();
		types.add(9);
		conditions.put("type", types);
		List<PopInfoDO> modules = popService.getListByConditions(conditions);
		model.addAttribute("helpModules", modules);
		return FWVM_PATH + "add-help.vm";
	}
	@RequestMapping("fwPopHelp/updateHelp")
	public String fwUpdateHelp(long popId, Model model) {
		Map<String, Object> conditions = new HashMap<String, Object>();
		List<Integer> types = new ArrayList<Integer>();
		types.add(9);
		conditions.put("type", types);
		List<PopInfoDO> modules = popService.getListByConditions(conditions);
		model.addAttribute("helpModules", modules);
		model.addAttribute("info", popService.getByPopId(popId));
		return FWVM_PATH + "update-help.vm";
	}
	//end
	
	
	@RequestMapping("popHelp/updateHelpSubmit")
	@ResponseBody
	public Object updateHelpSubmit(PopInfoDO info, HttpSession session) {
		JSONObject json = new JSONObject();
		info.setModifyTime(new Date());
		if (5 == info.getType()) {
			info.setParentId(-1);
		}
		try {
            PopInfoDO infoDO = popService.getByPopId(info.getPopId());
            if(infoDO != null){
                info.setAddTime(infoDO.getAddTime());
            }
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
}
