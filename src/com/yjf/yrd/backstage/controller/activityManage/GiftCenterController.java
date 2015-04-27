package com.yjf.yrd.backstage.controller.activityManage;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;

import com.yjf.common.service.enums.PayTypeEnum;
import com.yjf.yrd.activity.IActivityService;
import com.yjf.yrd.activity.QueryActivityOrder;
import com.yjf.yrd.base.BaseAutowiredController;
import com.yjf.yrd.dataobject.GiftInfo;
import com.yjf.yrd.dataobject.GiftUseRecord;
import com.yjf.yrd.page.Page;
import com.yjf.yrd.page.PageParam;

@Controller
@RequestMapping("backstage")
public class GiftCenterController extends BaseAutowiredController {
	private final String	VM_PATH	= "/backstage/activity/gift/";
	@Autowired
	IActivityService		iActivityService;
	
	@Override
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Date.class, "startTime", new CustomDateEditor(
			new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"), true));
		binder.registerCustomEditor(Date.class, "endTime", new CustomDateEditor(
			new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"), true));
	}
	
	@RequestMapping("giftCenter")
	public String giftCenter(HttpSession session, PageParam pageParam, Model model) {
		model.addAttribute("page", iActivityService.getGiftInfos(pageParam));
		return VM_PATH + "gift-center.vm";
	}
	
	@RequestMapping("addGift")
	public String addGift(HttpSession session, Model model) {
		return VM_PATH + "add-gift.vm";
	}
	
	@RequestMapping("addGiftSubmit")
	public String addGiftSubmit(GiftInfo info, HttpSession session) {
		iActivityService.addGiftInfo(info);
		return "forward:giftCenter";
	}
	
	@RequestMapping("updateGift")
	public String updateGift(long tblBaseId, Model model) {
		String[] giftTypes = new String[] { PayTypeEnum.WITHDRAW.code() };
		model.addAttribute("giftTypes", giftTypes);
		model.addAttribute("info", iActivityService.getGift(tblBaseId));
		return VM_PATH + "update-gift.vm";
	}
	
	@RequestMapping("updateGiftSubmit")
	public String updateGiftSubmit(GiftInfo info, HttpSession session) {
		try {
			iActivityService.updateGiftSubmit(info);
		} catch (Exception e) {
			logger.error("修改礼品失败", e);
		}
		return "forward:giftCenter";
	}
	
	@RequestMapping("giftUseRecord")
	public String giftUseRecord(QueryActivityOrder queryActivityOrder, PageParam pageParam,
								Model model) {
		try {
			Page<GiftUseRecord> page = iActivityService.getPageGiftUsedRecord(queryActivityOrder,
				pageParam);
			model.addAttribute("page", page);
		} catch (Exception e) {
			logger.error("查询失败", e);
		}
		return VM_PATH + "gift-use-record.vm";
	}
}
