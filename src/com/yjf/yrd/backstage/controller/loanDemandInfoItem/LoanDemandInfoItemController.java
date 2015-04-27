package com.yjf.yrd.backstage.controller.loanDemandInfoItem;

import java.util.Date;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.yjf.common.lang.beans.cglib.BeanCopier;
import com.yjf.yrd.base.BaseAutowiredController;
import com.yjf.yrd.loanInfoItem.valueObject.LoanItemQueryConditions;
import com.yjf.yrd.page.Page;
import com.yjf.yrd.page.PageParam;
import com.yjf.yrd.ws.info.LoanInfoItemInfo;
import com.yjf.yrd.ws.order.LoanInfoItemOrder;

/**
 * 
 * 
 * @Filename UserPointsController.java
 * 
 * @Description
 * 
 * @Version 1.0
 * 
 * @Author lzb
 * 
 * @Email caigen@yiji.com
 * 
 * @History <li>Author: lzb</li> <li>Date: 2014-8-14</li> <li>Version: 1.0</li>
 * <li>Content: create</li>
 * 
 */
@Controller
@RequestMapping("backstage/loanDemandInfo")
public class LoanDemandInfoItemController extends BaseAutowiredController {
	/** 通用页面路径 */
	String USER_MANAGE_PATH = "/backstage/loanDemandInfo/";
	
	@Override
	protected String[] getDateInputNameArray() {
		return new String[] { "startAddTime","endAddTime" };
	}
	/**
	 * 融资信息模块
	 * @param queryConditions
	 * @param pageParam
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("loadInfoItemManage")
	public String loadInfoItemManage(LoanItemQueryConditions queryConditions, PageParam pageParam,
										Model model) throws Exception {
		Page<LoanInfoItemInfo> page = loanInfoItemService.page(queryConditions, pageParam);
		model.addAttribute("queryConditions", queryConditions);
		model.addAttribute("page", page);
		return USER_MANAGE_PATH + "loadInfoItemManage.vm";
	}
	
	@RequestMapping("processLoanInfoItem")
	public String processLoanInfoItem(Long demandInfoItemId, Model model) throws Exception {
	LoanInfoItemInfo LoanInfoItemInfo = new LoanInfoItemInfo();
		if(demandInfoItemId!=null){
			LoanInfoItemInfo = loanInfoItemService.findById(demandInfoItemId);
		}
		
		model.addAttribute("itemInfo", LoanInfoItemInfo);
		return USER_MANAGE_PATH + "processLoanInfoItem.vm";
	}
	
	@ResponseBody
	@RequestMapping("loanInfoItemSubmit")
	public Object loanInfoItemSubmit(long demandInfoItemId,
			String infoName, String note, int sortNo) throws Exception {
		
		JSONObject jsonobj = new JSONObject();
		LoanInfoItemInfo loanInfoItemInfo = null;
		try {
			loanInfoItemInfo = loanInfoItemService.findById(demandInfoItemId);
		} catch (Exception e) {
			logger.error("当前数据已失效", e);
		}
		
		if(loanInfoItemInfo!=null && loanInfoItemInfo.getDemandInfoItemId()>0){
			LoanInfoItemOrder loanInfoItemOrder = new LoanInfoItemOrder();
			BeanCopier.staticCopy(loanInfoItemInfo, loanInfoItemOrder);
			loanInfoItemOrder.setInfoName(infoName);
			loanInfoItemOrder.setNote(note);
			loanInfoItemOrder.setSortNo(sortNo);
			loanInfoItemOrder.setRawUpdateTime(new Date());
			int retInt = loanInfoItemService.update(loanInfoItemOrder);
			if (retInt == 1) {
				jsonobj.put("code", 1);
				jsonobj.put("message", "信息模块编辑成功！");
			}else{
				jsonobj.put("code", 0);
				jsonobj.put("message", "信息模块编辑失败！");
			}
		}else{
			LoanInfoItemOrder loanInfoItemOrder = new LoanInfoItemOrder();
			loanInfoItemOrder.setInfoName(infoName);
			loanInfoItemOrder.setNote(note);
			loanInfoItemOrder.setSortNo(sortNo);
			loanInfoItemOrder.setInfoType("0");
			loanInfoItemOrder.setStatus("0");
			loanInfoItemOrder.setRawAddTime(new Date());
			loanInfoItemOrder.setRawUpdateTime(new Date());
			try {
				loanInfoItemService.insert(loanInfoItemOrder);
				jsonobj.put("code", 1);
				jsonobj.put("message", "信息模块保存成功！");
			} catch (Exception e) {
				logger.error("当前数据已失效", e);
				jsonobj.put("code", 0);
				jsonobj.put("message", "信息模块保存失败！");
			}
		}
		return jsonobj;
	}
	
	
	/**
	 * 更新状态
	 * @param userPointsId
	 * @param state
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("updateState")
	public Object updateState(long infoItemId, String status) throws Exception {
		JSONObject jsonobj = new JSONObject();
		LoanInfoItemInfo loanInfoItemInfo = null;
		try {
			loanInfoItemInfo = loanInfoItemService.findById(infoItemId);
		} catch (Exception e) {
			logger.error("当前数据已失效", e);
		}
		
		if(loanInfoItemInfo==null){
			jsonobj.put("code", 0);
			jsonobj.put("message", "当前数据已失效");
		}
		
		LoanInfoItemOrder loanInfoItemOrder = new LoanInfoItemOrder();
		BeanCopier.staticCopy(loanInfoItemInfo, loanInfoItemOrder);
		loanInfoItemOrder.setStatus(status);
		int retInt = loanInfoItemService.update(loanInfoItemOrder);
		if (retInt == 1) {
			jsonobj.put("code", 1);
			jsonobj.put("message", "修改状态成功");
		}else{
			jsonobj.put("code", 0);
			jsonobj.put("message", "修改状态失败");
		}

		return jsonobj;
	}
	
}
