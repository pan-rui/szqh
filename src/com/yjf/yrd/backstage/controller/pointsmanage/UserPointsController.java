package com.yjf.yrd.backstage.controller.pointsmanage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.yjf.common.lang.beans.cglib.BeanCopier;
import com.yjf.yrd.base.BaseAutowiredController;
import com.yjf.yrd.page.Page;
import com.yjf.yrd.page.PageParam;
import com.yjf.yrd.user.valueobject.PointsQueryConditions;
import com.yjf.yrd.user.valueobject.RulePointsQueryConditions;
import com.yjf.yrd.ws.enums.PointsRuleTypeEnum;
import com.yjf.yrd.ws.enums.PointsTypeEnum;
import com.yjf.yrd.ws.info.PointsGoodsDetailInfo;
import com.yjf.yrd.ws.info.UserPointInfo;
import com.yjf.yrd.ws.info.UserPointsDetailInfo;
import com.yjf.yrd.ws.info.UserRulePointInfo;
import com.yjf.yrd.ws.order.PointsGoodsDetailOrder;
import com.yjf.yrd.ws.order.UserPointsMainOrder;
import com.yjf.yrd.ws.result.YrdBaseResult;

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
@RequestMapping("backstage/userPoints")
public class UserPointsController extends BaseAutowiredController {
	/** 通用页面路径 */
	String USER_MANAGE_PATH = "/backstage/userPoints/";
	
	@Override
	protected String[] getDateInputNameArray() {
		return new String[] { "startAddTime", "endAddTime" };
	}
	
	/**
	 * 积分管理
	 * @param queryConditions
	 * @param pageParam
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("pointsManage")
	public String pointsManage(PointsQueryConditions queryConditions, PageParam pageParam,
								Model model) throws Exception {
		Page<UserPointInfo> page = userPointsService.page(queryConditions, pageParam);
		model.addAttribute("pointsQueryConditions", queryConditions);
		model.addAttribute("page", page);
		return USER_MANAGE_PATH + "pointsManage.vm";
	}
	
	@RequestMapping("userRulePoints")
	public String userRulePoints(RulePointsQueryConditions queryConditions, PageParam pageParam,
									Model model) throws Exception {
		List<PointsRuleTypeEnum> ruleTypes = PointsRuleTypeEnum.getAllEnum();
		model.addAttribute("ruleTypes", ruleTypes);
		
		Page<UserRulePointInfo> page = userPointsService.pageRulePoint(queryConditions, pageParam);
		model.addAttribute("pointsQueryConditions", queryConditions);
		model.addAttribute("page", page);
		return USER_MANAGE_PATH + "userRulePoints.vm";
	}
	
	/**
	 * 积分详情
	 * @param queryConditions
	 * @param pageParam
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("pointsDetailManage")
	public String pointsDetailManage(PointsQueryConditions queryConditions, PageParam pageParam,
										Model model) throws Exception {
		List<PointsTypeEnum> pointsType = PointsTypeEnum.getAllEnum();
		model.addAttribute("pointsTypes", pointsType);
		
		Page<UserPointsDetailInfo> page = userPointsService.detailUserPoints(queryConditions,
			pageParam);
		model.addAttribute("pointsQueryConditions", queryConditions);
		model.addAttribute("page", page);
		return USER_MANAGE_PATH + "userPointsDetail.vm";
	}
	
	/**
	 * 更新用户积分状态
	 * @param userPointsId
	 * @param state
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("updateState")
	public Object updateState(long userPointsId, String state) throws Exception {
		JSONObject jsonobj = new JSONObject();
		UserPointInfo userPointInfo = null;
		try {
			userPointInfo = userPointsService.findById(userPointsId);
		} catch (Exception e) {
			logger.error("当前用户积分数据已失效", e);
		}
		
		if (userPointInfo == null) {
			jsonobj.put("code", 0);
			jsonobj.put("message", "当前用户积分数据已失效");
		}
		
		userPointInfo.setState(state);
		UserPointsMainOrder userPointsMainOrder = new UserPointsMainOrder();
		BeanCopier.staticCopy(userPointInfo, userPointsMainOrder);
		YrdBaseResult result = userPointsService.update(userPointsMainOrder);
		if (result.isSuccess()) {
			jsonobj.put("code", 1);
			jsonobj.put("message", "修改用户积分状态成功");
		} else {
			jsonobj.put("code", 0);
			jsonobj.put("message", "修改用户积分状态失败");
		}
		
		return jsonobj;
	}
	
	/**
	 * 获取商品寄送信息
	 * @param pointsDetailId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("getGoodsDetail")
	public Map<String, Object> getGoodsDetail(long pointsDetailId) {
		boolean isYXJG = false;
		Map<String, Object> map = new HashMap<String, Object>();
		List<PointsGoodsDetailInfo> pointsGoodsDetailInfos;
		try {
			pointsGoodsDetailInfos = userPointsService.findByPointsDetailId(pointsDetailId);
			if (pointsGoodsDetailInfos != null && pointsGoodsDetailInfos.size() > 0) {
				PointsGoodsDetailInfo goodsDetailDO = pointsGoodsDetailInfos.get(0);
				map.put("deliveryCompany", goodsDetailDO.getDeliveryCompany());
				map.put("billno", goodsDetailDO.getBillno());
				map.put("remarks", goodsDetailDO.getRemarks());
				map.put("pointsGoodsId", goodsDetailDO.getPointsGoodsId());
			}
			isYXJG = true;
		} catch (Exception e) {
			logger.error("获取商品寄送信息失败", e);
		}
		
		if (isYXJG) {
			map.put("code", 1);
			map.put("message", "获取商品寄送信息成功");
		} else {
			map.put("code", 0);
			map.put("message", "无法获取商品寄送信息");
		}
		
		return map;
	}
	
	/**
	 * 处理商品寄送信息
	 * @param request
	 * @param remarks
	 * @param pointsGoodsId
	 * @param billno
	 * @param deliveryCompany
	 * @param pointsDetailId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("processGoodsDetail")
	public Map<String, Object> processGoodsDetail(HttpServletRequest request, String remarks,
													Long pointsGoodsId, String billno,
													String deliveryCompany, long pointsDetailId) {
		
		boolean isYXJG = false;
		Map<String, Object> map = new HashMap<String, Object>();
		PointsGoodsDetailOrder pointsGoodsDetailInfo = new PointsGoodsDetailOrder();
		pointsGoodsDetailInfo.setPointsDetailId(pointsDetailId);
		pointsGoodsDetailInfo.setDeliveryCompany(deliveryCompany);
		pointsGoodsDetailInfo.setBillno(billno);
		pointsGoodsDetailInfo.setRemarks(remarks);
		if (pointsGoodsId != null) {
			pointsGoodsDetailInfo.setPointsGoodsId(pointsGoodsId);
		}
		try {
			//处理商品寄送信息
			userPointsService.processGoodsDetail(pointsGoodsDetailInfo);
			isYXJG = true;
		} catch (Exception e) {
			logger.error("保存商品寄送信息失败", e);
		}
		
		if (isYXJG) {
			map.put("code", 1);
			map.put("message", "保存商品寄送信息成功");
		} else {
			map.put("code", 0);
			map.put("message", "保存商品寄送信息失败");
		}
		
		return map;
	}
}
