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
public class NoticePopCenterController extends BaseAutowiredController {
	private final String VM_PATH = "/backstage/publicNotice/noticeCenter/";
	@Autowired
	IPopService popService;
	
	@RequestMapping("noticeCenter")
	public String noticeCenter(HttpSession session, PageParam pageParam, Model model) {
		Map<String, Object> conditions = new HashMap<String, Object>();
		List<Integer> types = new ArrayList<Integer>();
		types.add(1);
		types.add(2);
		types.add(10);
		types.add(11);
		conditions.put("type", types);
		model.addAttribute("page", popService.getPageByConditions(pageParam, conditions));
		return VM_PATH + "notice-center.vm";
	}
	
	@RequestMapping("noticeCenter/addNotice")
	public String addNotice(HttpSession session) {
		return VM_PATH + "add-notice.vm";
	}
	
	@RequestMapping("noticeCenter/addNoticeSubmit")
	public String addNoticeSubmit(PopInfoDO info, HttpSession session) {
		info.setAddTime(new Date());
        info.setModifyTime(info.getAddTime());
		popService.addNotice(info);
		return VM_PATH + "add-notice.vm";
	}
	
	@RequestMapping("noticeCenter/updateNotice")
	public String updateNotice(long popId, Model model) {
		model.addAttribute("info", popService.getByPopId(popId));
		return VM_PATH + "update-notice.vm";
	}

//	@RequestMapping("noticeCenter/delNotice")
//	public String delNotice(long popId, Model model) {
//
//		return VM_PATH + "update-notice.vm";
//	}	
	
	@RequestMapping("noticeCenter/updateNoticeSubmit")
	public String updateNoticeSubmit(PopInfoDO info, HttpSession session) {
		info.setModifyTime(new Date());
        PopInfoDO infoDO = popService.getByPopId(info.getPopId());
        if(infoDO != null){
            info.setAddTime(infoDO.getAddTime());
        }
		popService.updateNotice(info);
		return VM_PATH + "update-notice.vm";
	}
	
	@ResponseBody
	@RequestMapping(value = "noticeCenter/changeStatus")
	public Object changeStatus(long popId, short status) throws Exception {
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
	@ResponseBody
	@RequestMapping(value = "noticeCenter/delNotice")
	public Object delNotice(long popId, short status) throws Exception {
		JSONObject jsonobj = new JSONObject();

		try {
			popService.delNotice(popId);
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
