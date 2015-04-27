package com.yjf.yrd.backstage.controller.usermanage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.yjf.common.lang.beans.cglib.BeanCopier;
import com.yjf.common.lang.util.StringUtil;
import com.yjf.yrd.dal.dataobject.UserExtendDO;
import com.yjf.yrd.dataobject.Role;
import com.yjf.yrd.page.PageParam;
import com.yjf.yrd.user.info.InstitutionsInfo;
import com.yjf.yrd.user.info.UserInfo;
import com.yjf.yrd.user.order.InstitutionsBaskstageRegisterOrder;
import com.yjf.yrd.user.order.UpdateEnterpriseOrder;
import com.yjf.yrd.user.order.UserExtendOrder;
import com.yjf.yrd.user.query.order.UserRoleQueryOrder;
import com.yjf.yrd.util.AppConstantsUtil;
import com.yjf.yrd.web.util.PageUtil;
import com.yjf.yrd.web.util.WebUtil;
import com.yjf.yrd.ws.enums.BooleanEnum;
import com.yjf.yrd.ws.enums.P2PEnterpriseTypeEnum;
import com.yjf.yrd.ws.enums.SysUserRoleEnum;
import com.yjf.yrd.ws.enums.UserExtendEnum;
import com.yjf.yrd.ws.enums.UserStateEnum;
import com.yjf.yrd.ws.enums.UserTypeEnum;
import com.yjf.yrd.ws.order.LoanerBaseInfoOrder;
import com.yjf.yrd.ws.result.YrdBaseResult;
import com.yjf.yrd.ws.service.query.result.QueryBaseBatchResult;

/**
 * 
 * 
 * @Filename InstitutionsController.java
 * 
 * @Description
 * 
 * @Version 1.0
 * 
 * @Author zjl
 * 
 * @Email zjialin@yiji.com
 * 
 * @History <li>Author: zjl</li> <li>Date: 2013-7-5</li> <li>Version: 1.0</li>
 * <li>Content: create</li>
 * 
 */
@Controller
@RequestMapping("backstage/userManage")
public class InstitutionsController extends UserBaseInfoBaseController {
	/** 通用页面路径 */
	String USER_MANAGE_PATH = "/backstage/userManage/";
	
	@Override
	protected String[] getDateInputNameArray() {
		return new String[] { "changeLockTime" };
	}
	
	@RequestMapping("institutionManage")
	public String institutionsManage(HttpServletRequest request, PageParam pageParam, Model model)
																									throws Exception {
		UserRoleQueryOrder roleQueryOrder = new UserRoleQueryOrder();
		WebUtil.setPoPropertyByRequest(roleQueryOrder, request);
		roleQueryOrder.setPageSize(pageParam.getPageSize());
		roleQueryOrder.setPageNumber(pageParam.getPageNo());
		roleQueryOrder.setType(UserTypeEnum.JG);
		roleQueryOrder.setUserStateEnum(UserStateEnum.getByCode(request.getParameter("state")));
		QueryBaseBatchResult<UserInfo> baseBatchResult = userQueryService
			.queryRoleUserInfo(roleQueryOrder);
		
		model.addAttribute("queryConditions", roleQueryOrder);
		model.addAttribute("page", PageUtil.getCovertPage(baseBatchResult));
		return USER_MANAGE_PATH + "institutionsManage.vm";
	}
	
	@RequestMapping("institutionManage/addInstitutionsUser")
	public String addInstitutionsUser(Model model) throws Exception {
		model.addAttribute("uploadHost", "");
		return USER_MANAGE_PATH + "addInstitutionsUser.vm";
	}
	
	@ResponseBody
	@RequestMapping("institutionManage/addInstitutionsUserSubmit")
	public Object institutionsManageSubmit(HttpServletRequest request) {
		JSONObject jsonobj = new JSONObject();
		InstitutionsBaskstageRegisterOrder registerOrder = new InstitutionsBaskstageRegisterOrder();
		
		WebUtil.setPoPropertyByRequest(registerOrder, request);
		if(StringUtil.isNotBlank(request.getParameter("guaranteeAccountNo")) ){
			if(AppConstantsUtil.isPayGuaranteeAmount())
			{
				UserExtendOrder  order=new UserExtendOrder();
				order.setUserExtendEnum(UserExtendEnum.GUARANTEE_ACCOUNT_ID);
				order.setValue(request.getParameter("guaranteeAccountNo"));
				List<UserExtendOrder> userExtendOrders=new ArrayList<>();
				userExtendOrders.add(order);
				registerOrder.setUserExtendOrders(userExtendOrders);	
			}
			
		}
		setRoles(request, registerOrder);
		if ("small".equals(request.getParameter("enterpriseType"))) {
			registerOrder.setEnterpriseType(P2PEnterpriseTypeEnum.SMALL_AND_MICRO);
		} else {
			registerOrder.setEnterpriseType(P2PEnterpriseTypeEnum.TICKET);
		}
		if (StringUtil.isEmpty(registerOrder.getCertFrontPath())) {
			jsonobj.put("code", 0);
			jsonobj.put("message", "请上传身份证正面照片");
			return jsonobj;
		}
		
		if (StringUtil.isEmpty(registerOrder.getCertBackPath())) {
			jsonobj.put("code", 0);
			jsonobj.put("message", "请上传身份证反面照片");
			return jsonobj;
		}
		
		if (registerOrder.getRole() == null || registerOrder.getRole().size() <= 1) {
			jsonobj.put("code", 0);
			jsonobj.put("message", "请选择机构角色");
			return jsonobj;
		}
		
		if (StringUtil.isBlank(registerOrder.getBusinessLicenseCachetPath())) {
			jsonobj.put("code", 0);
			jsonobj.put("message", "上传加盖公章的企业营业执照副本扫描件");
			return jsonobj;
		}
		YrdBaseResult baseResult = registerService.InstitutionsBaskstageRegister(registerOrder);
		if (baseResult.isSuccess()) {
			
			jsonobj.put("code", 1);
			jsonobj.put("message", "创建机构用户成功");
		} else {
			jsonobj.put("code", 0);
			jsonobj.put("message", "创建机构用户失败【" + baseResult.getMessage() + "】");
		}
		return jsonobj;
	}
	
	@RequestMapping("institutionManage/updateInstitutionsUser")
	public String updateInstitutionsUser(String userBaseId, long userId, Model model)
																						throws Exception {
		model.addAttribute("uploadHost", "");
		List<Role> roleList = authorityService.getRolesByUserId(userId,
			SysUserRoleEnum.PUBLIC.getValue(), SysUserRoleEnum.INVESTOR.getValue()).getResult();
		InstitutionsInfo institutionsInfo = userQueryService.queryInstitutionsInfoByBaseId(
			userBaseId).getQueryInstitutionsInfo();
		UserInfo userInfo = userQueryService.queryByUserBaseId(userBaseId).getQueryUserInfo();
		
		if (userInfo != null) {
			BeanCopier.staticCopy(userInfo, institutionsInfo);
		}
		//房融通担保机构，查找资金账户
		if(AppConstantsUtil.isPayGuaranteeAmount()){
			UserExtendDO extendDO = userExtendDAO.findByPropertyNameUserId(userId, UserExtendEnum.GUARANTEE_ACCOUNT_ID.code());
			if(extendDO!=null){
				model.addAttribute("guaranteeAccountNo", extendDO.getPropertyValue());
			}
		}
		model.addAttribute("roleList", roleList);
		model.addAttribute("info", institutionsInfo);
		model.addAttribute("userInfo", userInfo);
		return USER_MANAGE_PATH + "updateInstitutionsUser.vm";
	}
	
	@ResponseBody
	@RequestMapping("institutionManage/updateInstitutionsUserSubmit")
	public Object updateInstitutionsUserSubmit(HttpServletRequest request) throws Exception {
		UpdateEnterpriseOrder updateOrder = new UpdateEnterpriseOrder();
		LoanerBaseInfoOrder loanerOrder = new LoanerBaseInfoOrder();
		//	CommonAttachmentOrder attachment = new CommonAttachmentOrder();
		JSONObject jsonobj = new JSONObject();
		WebUtil.setPoPropertyByRequest(updateOrder, request);
		WebUtil.setPoPropertyByRequest(loanerOrder, request);
		setRoles(request, updateOrder);
		//保证金账户
		if(AppConstantsUtil.isPayGuaranteeAmount()){
			UserExtendOrder  order=new UserExtendOrder();
			order.setUserExtendEnum(UserExtendEnum.GUARANTEE_ACCOUNT_ID);
			order.setValue(request.getParameter("guaranteeAccountNo"));
			List<UserExtendOrder> userExtendOrders=new ArrayList<>();
			userExtendOrders.add(order);
			updateOrder.setUserExtendOrders(userExtendOrders);	
		}
		
		if (StringUtil.isBlank(request.getParameter("mobileBinding"))) {
			updateOrder.setMobileBinding(BooleanEnum.NO);
		} else {
			updateOrder.setMobileBinding(BooleanEnum.getByCode(request
				.getParameter("mobileBinding")));
		}
		if (StringUtil.isBlank(request.getParameter("mailBinding"))) {
			updateOrder.setMailBinding(BooleanEnum.NO);
		} else {
			updateOrder.setMailBinding(BooleanEnum.getByCode(request.getParameter("mailBinding")));
		}
		
		if (updateOrder.getRole() == null || updateOrder.getRole().size() <= 1) {
			jsonobj.put("code", 0);
			jsonobj.put("message", "请选择机构角色");
			return jsonobj;
		}
		
		UserInfo userInfo = userQueryService.queryByUserId(loanerOrder.getUserId())
			.getQueryUserInfo();
		updateOrder.setUserName(userInfo.getUserName());
		updateOrder.setIdentityName(userInfo.getIdentityName());
		YrdBaseResult yrdBaseResult = registerService.updateOrgBackstageRegister(updateOrder);
		
		if (yrdBaseResult.isSuccess()) {
			if (loanerOrder != null) {
				loanerBaseInfoService.updateById(loanerOrder);
				//				attachment.setBizNo(loanerOrder.getUserBaseId());
				//				QueryBaseBatchResult<CommonAttachmentInfo>result = commonAttachmentService.queryByBizNoModuleType(attachment);
				//				if(result.isSuccess()){
				//					if(result.getPageList().size()>0){
				//						for(CommonAttachmentInfo attachmentInfo:result.getPageList()){
				//							commonAttachmentService.deleteById(attachmentInfo.getAttachmentId());
				//						}
				//					}
				//						addAttachfile(loanerOrder.getUserBaseId(),request);
				//				}
			}
			jsonobj.put("code", 1);
			jsonobj.put("message", "修改机构信息成功");
		}
		if (!yrdBaseResult.isSuccess()) {
			jsonobj.put("code", 0);
			jsonobj.put("message", "修改机构信息失败,原因（" + yrdBaseResult.getMessage() + "）");
		}
		return jsonobj;
	}
	
	/**
	 * 后台企业实名认证
	 * @param userBaseId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("institutionManage/updateInstitutionsUser/backEnterpriseRealNameAuth")
	public Object backEnterpriseRealNameAuth(String userBaseId) {
		JSONObject jsonobj = new JSONObject();
		try {
			
			YrdBaseResult baseResult = this.realNameAuthenticationService
				.sendEnterpriseRealNameInfo(userBaseId);
			if (baseResult.isSuccess()) {
				jsonobj.put("code", 1);
				jsonobj.put("message", baseResult.getMessage());
			} else {
				jsonobj.put("code", 0);
				jsonobj.put("message", baseResult.getMessage());
			}
		} catch (Exception e) {
			logger.error("后台企业实名发送或更新实名状态异常", e);
			jsonobj.put("code", 0);
			jsonobj.put("message", "后台企业实名发送或更新实名状态异常");
		}
		return jsonobj;
	}
	
}
