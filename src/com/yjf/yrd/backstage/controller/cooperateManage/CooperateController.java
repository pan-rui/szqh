/**
 * 
 */
package com.yjf.yrd.backstage.controller.cooperateManage;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yjf.common.lang.util.DateUtil;
import com.yjf.yrd.base.BaseAutowiredController;
import com.yjf.yrd.cooperate.ICooperateService;
import com.yjf.yrd.page.PageParam;
import com.yjf.yrd.web.util.PageUtil;
import com.yjf.yrd.web.util.WebUtil;
import com.yjf.yrd.ws.info.CooperateInfo;
import com.yjf.yrd.ws.order.CooperateQueryOrder;
import com.yjf.yrd.ws.service.query.result.QueryBaseBatchResult;

/**
 * @author ouyangnuo
 * 
 * 合作机构管理
 * 
 * TradeProgressController
 * 
 */
@Controller
@RequestMapping("backstage")
public class CooperateController extends BaseAutowiredController {

	String PAGE_PATH = "/backstage/cooperateManage/";

	@Autowired
	ICooperateService cooperateService;
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Date.class, "deadline", new CustomDateEditor(
			new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"), true));
		binder.registerCustomEditor(Date.class, "investAvalibleTime", new CustomDateEditor(
			new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"), true));
	}


	/**
	 * 查看合作机构
	 * 
	 * @return
	 */
	@RequestMapping("cooperateManage")
	public String cooperateManage(HttpServletRequest request, PageParam pageParam,Model model) {
		
		CooperateQueryOrder order = new CooperateQueryOrder();
		
		order.setPageNumber(pageParam.getPageNo());
		order.setPageSize(pageParam.getPageSize());
		
		QueryBaseBatchResult<CooperateInfo> result = cooperateService.queryCooperate(order);
		
		model.addAttribute("page", PageUtil.getCovertPage(result));

		return PAGE_PATH + "cooperateManage.vm";
	}

	
	/**
	 * 添加合作机构
	 * 
	 * @return
	 */
	@RequestMapping("cooperateManage/addCooperate")
	public String addCooperate(HttpServletRequest request,Model model) {

		model.addAttribute("uploadHost", "");
		
		return PAGE_PATH + "addCooperate.vm";
	}
	
	/**
	 * 添加合作机构提交
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("cooperateManage/addCooperateSubmit")
	@ResponseBody
	public Object addCooperateSubmit(HttpServletRequest request,Model model) {

		JSONObject json = new JSONObject();
		CooperateQueryOrder order = new CooperateQueryOrder();
		WebUtil.setPoPropertyByRequest(order, request);
		
		order.setAddTime(DateUtil.now());
		
		String urlString = order.getCooHerf();
		
		Boolean f= urlString.startsWith("http://");
		if (f==false) {
			urlString = "http://".concat(urlString);
		}
		order.setCooHerf(urlString);
		
		try {
			cooperateService.addCooperate(order);
			
			json.put("code", "1");
			json.put("message", "添加成功！");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			json.put("code", "0");
			json.put("message", "添加失败！");
		}
		
		return json;
	}

	/**
	 * 修改合作机构
	 * 
	 * @return
	 */
	@RequestMapping("cooperateManage/updateCooperate")
	public String updateCooperate(long cooId,HttpServletRequest request,Model model) {

		CooperateInfo info = cooperateService.getByCooId(cooId);
		
		model.addAttribute("info", info);
		
		model.addAttribute("addTime", DateUtil.simpleFormat(info.getAddTime()));
		
		model.addAttribute("uploadHost", "");
				
		return PAGE_PATH + "updateCooperate.vm";
	}
	
	/**
	 * 修改合作机构提交
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("cooperateManage/updateCooperateSubmit")
	@ResponseBody
	public Object updateCooperateSubmit(HttpServletRequest request, Model model) {
		JSONObject json = new JSONObject();
		CooperateQueryOrder order = new CooperateQueryOrder();
		WebUtil.setPoPropertyByRequest(order, request);
		try {
			cooperateService.updateCooperate(order);
			json.put("code", "1");
			json.put("message", "修改成功！");
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			json.put("code", "0");
			json.put("message", "修改失败！");
		}
		return json;
	}

	/**
	 * 删除某条合作机构
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("cooperateManage/delCooperate")
	@ResponseBody
	public Object delCooperate(long cooId,HttpServletRequest request,Model model) {
		JSONObject json = new JSONObject();
		try {
			cooperateService.deleteCooperate(cooId);
			json.put("code", "1");
			json.put("message", "删除成功！");
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			json.put("code", "0");
			json.put("message", "删除失败！");
		}
		return json;
		
	}

}
