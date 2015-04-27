package com.yjf.yrd.backstage.controller.usermanage;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.yjf.yrd.user.order.*;
import com.yjf.yrd.user.query.order.UserRoleQueryOrder;
import com.yjf.yrd.ws.enums.*;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.yjf.common.lang.beans.cglib.BeanCopier;
import com.yjf.common.lang.enums.CertTypeEnum;
import com.yjf.common.lang.util.ListUtil;
import com.yjf.common.lang.util.StringUtil;
import com.yjf.yrd.common.info.GuarantorInfo;
import com.yjf.yrd.common.order.GuarantorInfoQueryOrder;
import com.yjf.yrd.common.order.GuarantorOrder;
import com.yjf.yrd.common.order.SendNonMainlandRealNameInfoOrder;
import com.yjf.yrd.dataobject.Role;
import com.yjf.yrd.integration.openapi.enums.OccupationEnum;
import com.yjf.yrd.integration.openapi.info.YjfAccountInfo;
import com.yjf.yrd.integration.openapi.result.CustomerResult;
import com.yjf.yrd.integration.openapi.result.QueryAccountResult;
import com.yjf.yrd.page.Page;
import com.yjf.yrd.page.PageParam;
import com.yjf.yrd.page.Pagination;
import com.yjf.yrd.user.info.InstitutionsInfo;
import com.yjf.yrd.user.info.PersonalInfo;
import com.yjf.yrd.user.info.PersonalVOInfo;
import com.yjf.yrd.user.info.UserInfo;
import com.yjf.yrd.user.info.UserRelationInfo;
import com.yjf.yrd.user.query.order.PersonaCommonQueryOrder;
import com.yjf.yrd.user.query.order.UserQueryOrder;
import com.yjf.yrd.user.result.UserQueryResult;
import com.yjf.yrd.user.result.UserRegisterResult;
import com.yjf.yrd.util.AppConstantsUtil;
import com.yjf.yrd.web.util.PageUtil;
import com.yjf.yrd.web.util.WebUtil;
import com.yjf.yrd.web.util.YrdEnumUtil;
import com.yjf.yrd.ws.info.LoanerBaseInfo;
import com.yjf.yrd.ws.order.LoanerBaseInfoOrder;
import com.yjf.yrd.ws.result.YrdBaseResult;
import com.yjf.yrd.ws.service.query.result.QueryBaseBatchResult;

/**
 * 
 * 
 * @Filename UserManageController.java
 * 
 * @Description
 * 
 * @Version 1.0
 * 
 * @Author zjl
 * 
 * @Email zjialin@yiji.com
 * 
 * @History <li>Author: zjil</li> <li>Date: 2013-6-11</li> <li>Version: 1.0</li>
 * <li>Content: create</li>
 * 
 */
@Controller
@RequestMapping("backstage/userManage")
public class PersonalController extends UserBaseInfoBaseController {
	/** 通用页面路径 */
	String USER_MANAGE_PATH = "/backstage/userManage/";
	
	@Override
	protected String[] getDateInputNameArray() {
		return new String[] { "changeLockTime", "startCreateTime", "endCreateTime",
								"startUpdateTime", "endUpdateTime" };
	}
	
	@RequestMapping("personalManage")
	public String personalManage(HttpServletRequest request, PageParam pageParam,
									HttpServletResponse response, Model model) {
		PersonaCommonQueryOrder commonQueryOrder = new PersonaCommonQueryOrder();
		WebUtil.setPoPropertyByRequest(commonQueryOrder, request);
		commonQueryOrder.setState(UserStateEnum.getByCode(request.getParameter("state")));
		String role = request.getParameter("role");
		if (StringUtil.isNotEmpty(role)) {
			commonQueryOrder.setRoleId(Integer.valueOf(role));
		}
		
		pageParam.setPageSize(20);
		commonQueryOrder.setPageSize(pageParam.getPageSize());
		commonQueryOrder.setPageNumber(pageParam.getPageNo());
		QueryBaseBatchResult<PersonalVOInfo> baseBatchResult = userQueryService
			.commonQueryPersonalInfo(commonQueryOrder);
		Page<PersonalVOInfo> page = PageUtil.getCovertPage(baseBatchResult);
		model.addAttribute("queryConditions", commonQueryOrder);
        model.addAttribute("userLevel", UserLevelEnum.getAllEnum());
		model.addAttribute("page", page);
		response.setHeader("Pragma", "No-cache");
		return USER_MANAGE_PATH + "personalManage.vm";
	}
	
	@ResponseBody
	@RequestMapping("sendActiveUrl")
	public Object sendActiveUrl(HttpServletRequest request, String userBaseId,
								HttpServletResponse response, Model model) {
		
		JSONObject jsonobj = new JSONObject();
		UserInfo userInfo = userQueryService.queryByUserBaseId(userBaseId).getQueryUserInfo();
		if (userInfo == null) {
			jsonobj.put("code", "0");
			jsonobj.put("message", "用户不存在");
			return jsonobj;
		}
		YrdBaseResult activeResult = registerService.resendEmail(userBaseId);
		if (activeResult.isSuccess()) {
			jsonobj.put("code", "1");
			jsonobj.put("message", "发送激活邮件成功");
		} else {
			jsonobj.put("code", "0");
			jsonobj.put("message", "发送激活邮件失败");
		}
		return jsonobj;
	}
	
	@RequestMapping("unRealNamePass")
	public String unRealNamePass(HttpServletRequest request, PageParam pageParam, Model model)
																								throws Exception {
		PersonaCommonQueryOrder commonQueryOrder = new PersonaCommonQueryOrder();
        commonQueryOrder.setRealNameFailReason("Y");
		WebUtil.setPoPropertyByRequest(commonQueryOrder, request);
		commonQueryOrder.setRealNameAuthentication(RealNameAuthStatusEnum.getByCode(request
			.getParameter("realNameAuthentication")));
		commonQueryOrder.setPageSize(pageParam.getPageSize());
		commonQueryOrder.setPageNumber(pageParam.getPageNo());
		QueryBaseBatchResult<PersonalVOInfo> baseBatchResult = userQueryService
			.commonQueryPersonalInfo(commonQueryOrder);
		model.addAttribute("queryConditions", commonQueryOrder);
		model.addAttribute("page", PageUtil.getCovertPage(baseBatchResult));
		return USER_MANAGE_PATH + "unRealNamePass.vm";
	}
	
	@RequestMapping("unRealNamePassFDL")
	public String unRealNamePassFDL(HttpServletRequest request, PageParam pageParam, Model model)
																									throws Exception {
		PersonaCommonQueryOrder commonQueryOrder = new PersonaCommonQueryOrder();
		WebUtil.setPoPropertyByRequest(commonQueryOrder, request);
		commonQueryOrder.setRealNameAuthentication(RealNameAuthStatusEnum.APPLY);
		commonQueryOrder.setPageSize(pageParam.getPageSize());
		commonQueryOrder.setPageNumber(pageParam.getPageNo());
		QueryBaseBatchResult<PersonalVOInfo> baseBatchResult = userQueryService
			.commonQueryPersonalInfo(commonQueryOrder);
		
		GuarantorInfoQueryOrder queryOrder = new GuarantorInfoQueryOrder();
		QueryBaseBatchResult<GuarantorInfo> list = guarantorManagerService
			.queryGuarantorInfo(queryOrder);
		if (list != null) {
			model.addAttribute("guarantorList", list);
		}
		model.addAttribute("queryConditions", commonQueryOrder);
		model.addAttribute("page", PageUtil.getCovertPage(baseBatchResult));
		return USER_MANAGE_PATH + "unRealNamePassFDL.vm";
	}
	
	@RequestMapping("unRealNamePassFDL/guarantor")
	public String guarantor(HttpServletRequest request, PageParam pageParam, Model model)
																							throws Exception {
		GuarantorInfoQueryOrder queryOrder = new GuarantorInfoQueryOrder();
		
		WebUtil.setPoPropertyByRequest(queryOrder, request);
		
		queryOrder.setPageSize(pageParam.getPageSize());
		queryOrder.setPageNumber(pageParam.getPageNo());
		QueryBaseBatchResult<GuarantorInfo> list = guarantorManagerService
			.queryGuarantorInfo(queryOrder);
		
		model.addAttribute("queryOrder", queryOrder);
		model.addAttribute("page", PageUtil.getCovertPage(list));
		return USER_MANAGE_PATH + "guarantor.vm";
	}
	
	@RequestMapping("unRealNamePassFDL/updateGuarantor")
	public String updateGuarantor(HttpServletRequest request, long id, Model model)
																					throws Exception {
		
		GuarantorInfo guarantor = guarantorManagerService.findById(id);
		if (guarantor != null) {
			model.addAttribute("guarantor", guarantor);
		}
		List<OccupationEnum> guaranteeOccupation = OccupationEnum.getAllEnum();
		model.addAttribute("guaranteeOccupation", guaranteeOccupation);
		return USER_MANAGE_PATH + "updateGuarantor.vm";
	}
	
	@RequestMapping("unRealNamePassFDL/addGuarantor")
	public String addGuarantor(HttpServletRequest request, Model model) throws Exception {
		List<OccupationEnum> guaranteeOccupation = OccupationEnum.getAllEnum();
		model.addAttribute("guaranteeOccupation", guaranteeOccupation);
		return USER_MANAGE_PATH + "addGuarantor.vm";
	}
	
	@ResponseBody
	@RequestMapping("unRealNamePassFDL/addGuarantorSubmit")
	public JSONObject addGuarantorSubmit(HttpServletRequest request, GuarantorOrder order,
											Model model) throws Exception {
		//根据身份证，判断是否已有担保人
		GuarantorInfoQueryOrder queryOrder = new GuarantorInfoQueryOrder();
		queryOrder.setGuaranteeCertNo(order.getGuaranteeCertNo());
		QueryBaseBatchResult<GuarantorInfo> list = guarantorManagerService
			.queryGuarantorInfo(queryOrder);
		JSONObject json = new JSONObject();
		if (list.getTotalCount() == 0) {
			
			order.setGuaranteeCertType(CertTypeEnum.Identity_Card);
			YrdBaseResult baseResult = guarantorManagerService.addGuarantor(order);
			
			logger.info("添加担保人，入参{}", baseResult);
			
			if (baseResult.isSuccess()) {
				json.put("code", 1);
				json.put("message", "添加成功");
			} else {
				json.put("code", 0);
				json.put("message", "添加失败");
			}
		} else {
			json.put("code", 0);
			json.put("message", "添加失败,担保人身份证已存在");
		}
		return json;
		
	}
	
	@ResponseBody
	@RequestMapping("unRealNamePassFDL/updateGuarantorSubmit")
	public JSONObject updateGuarantorSubmit(HttpServletRequest request, GuarantorOrder order,
											Model model) throws Exception {
		
		JSONObject json = new JSONObject();
		
		order.setGuaranteeCertType(CertTypeEnum.Identity_Card);
		YrdBaseResult baseResult = guarantorManagerService.updateGuarantor(order);
		
		logger.info("修改担保人，入参{}", baseResult);
		
		if (baseResult.isSuccess()) {
			json.put("code", 1);
			json.put("message", "修改成功");
		} else {
			json.put("code", 0);
			json.put("message", "修改失败");
		}
		
		return json;
		
	}
	
	@ResponseBody
	@RequestMapping("unRealNamePassFDL/deleteGuarantor")
	public JSONObject updateGuarantorSubmit(HttpServletRequest request, long id, Model model)
																								throws Exception {
		
		JSONObject json = new JSONObject();
		
		YrdBaseResult baseResult = guarantorManagerService.deleteGuarantor(id);
		
		logger.info("删除担保人，入参{}", baseResult);
		
		if (baseResult.isSuccess()) {
			json.put("code", 1);
			json.put("message", "删除成功");
		} else {
			json.put("code", 0);
			json.put("message", "删除失败");
		}
		
		return json;
		
	}
	
	@ResponseBody
	@RequestMapping("unRealNamePassFDL/sendNonMainlandRealName")
	public JSONObject sendNonMainlandRealName(	HttpServletRequest request,
												SendNonMainlandRealNameInfoOrder nonMainlandRealNameOrder,
												Model model) throws Exception {
		
		JSONObject json = new JSONObject();
		
		YrdBaseResult baseResult = realNameAuthenticationService
			.sendNonMainlandRealNameInfo(nonMainlandRealNameOrder);
		
		logger.info("非大陆实名认证，入参{}", baseResult);
		
		if (baseResult.isSuccess()) {
			json.put("code", 1);
			json.put("message", "申请成功");
		} else {
			json.put("code", 0);
			json.put("message", "申请失败");
		}
		
		return json;
		
	}
	
	@ResponseBody
	@RequestMapping("unRealNamePassFDL/rejectedGuarante")
	public JSONObject rejectedGuarante(HttpServletRequest request,
										SendNonMainlandRealNameInfoOrder nonMainlandRealNameOrder,
										Model model) throws Exception {
		
		JSONObject json = new JSONObject();
		if (com.yjf.yrd.util.StringUtil.isNotEmpty(nonMainlandRealNameOrder.getUserBaseId())) {
			YrdBaseResult baseResult = realNameAuthenticationService
				.rejectNonMainlandRealName(nonMainlandRealNameOrder.getUserBaseId());
			
			logger.info("非大陆实名认证驳回，入参{}", baseResult);
			
			if (baseResult.isSuccess()) {
				json.put("code", 1);
				json.put("message", "操作成功");
			} else {
				json.put("code", 0);
				json.put("message", "操作失败");
			}
		}
		return json;
		
	}
	
	@ResponseBody
	@RequestMapping(value = "unRealNamePassFDL/checkGuaranteeCertNo")
	public Object checkBorrower(String guaranteeCertNo) throws Exception {
		logger.info("验证担保人，入参[{}]", guaranteeCertNo);
		
		GuarantorInfoQueryOrder queryOrder = new GuarantorInfoQueryOrder();
		queryOrder.setGuaranteeCertNo(guaranteeCertNo);
		QueryBaseBatchResult<GuarantorInfo> list = guarantorManagerService
			.queryGuarantorInfo(queryOrder);
		JSONObject jsonobj = new JSONObject();
		
		if (list.getTotalCount() == 0) {
			jsonobj.put("code", 1);
			jsonobj.put("message", "担保人不存在，可用");
		} else {
			jsonobj.put("code", 0);
			jsonobj.put("message", "该担保人已存在");
		}
		return jsonobj;
	}
	
	@RequestMapping("personalManage/addPersonalUser")
	public String addPersonalUser(long parentId, Model model) throws Exception {
		model.addAttribute("parentId", parentId);
		model.addAttribute("uploadHost", "");
		List<CommonAttachmentTypeEnum> list = YrdEnumUtil.getAttachmentByIndustry();
		model.addAttribute("enumlist", list);
		if (parentId == 1) {
			return USER_MANAGE_PATH + "addAdmin.vm";
		} else {
			return USER_MANAGE_PATH + "addPersonalUser.vm";
		}
	}
	
	@RequestMapping("personalManage/updatePersonalUser")
	public String updatePersonalUser(String userBaseId, long userId, Model model) throws Exception {
		model.addAttribute("uploadHost", "");
		UserInfo userBase = userQueryService.queryByUserBaseId(userBaseId).getQueryUserInfo();
		if (userBase.getType() == UserTypeEnum.JG) {
			return updateInstitutionsUser(userBaseId, userId, model);
		} else {
			PersonalInfo personalInfo = userQueryService.queryPersonalInfoByBaseId(userBaseId)
				.getQueryPersonalInfo();
			UserRelationInfo userRelationInfo = userRelationQueryService.findUserRelationByChildId(
				userBase.getUserId()).getQueryUserRelationInfo();
			if (userRelationInfo != null) {
				
				long borkerParentId = userRelationInfo.getParentId();
				
				UserRelationInfo orgRelationInfo = userRelationQueryService
					.findUserRelationByChildId(borkerParentId).getQueryUserRelationInfo();
				
				if (orgRelationInfo != null) {
					personalInfo.setReferees(orgRelationInfo.getMemberNo());
				}
			}
			
			BeanCopier.staticCopy(userBase, personalInfo);
			List<Role> roleList = authorityService.getRolesByUserId(userId, 0, 12).getResult();
			LoanerBaseInfo loanerBaseInfo = loanerBaseInfoService.findByUserId(userId);
			if (loanerBaseInfo != null) {
				model.addAttribute("role", "0");
			} else {
				model.addAttribute("role", "1");
			}
			model.addAttribute("info", personalInfo);
			model.addAttribute("roleList", roleList);
			model.addAttribute("loanerInfo", loanerBaseInfo);
			if (userBase.getRealNameAuthentication() == null
				|| userBase.getRealNameAuthentication() == RealNameAuthStatusEnum.NO) {
				model.addAttribute("personlRealNameStatus", "N");
				return USER_MANAGE_PATH + "updatePersonalUser.vm";
			} else {
				
				return USER_MANAGE_PATH + "personalUserInfo.vm";
			}
		}
	}
	
	//港澳台，查看详情
	@RequestMapping("personalManage/updateunRealNamePassFDL")
	public String updateunRealNamePassFDL(String userBaseId, long userId, Model model)
																						throws Exception {
		model.addAttribute("uploadHost", "");
		UserInfo userBase = userQueryService.queryByUserBaseId(userBaseId).getQueryUserInfo();
		PersonalInfo personalInfo = userQueryService.queryPersonalInfoByBaseId(userBaseId)
			.getQueryPersonalInfo();
		UserRelationInfo userRelationInfo = userRelationQueryService.findUserRelationByChildId(
			userBase.getUserId()).getQueryUserRelationInfo();
		if (userRelationInfo != null) {
			
			long borkerParentId = userRelationInfo.getParentId();
			
			UserRelationInfo orgRelationInfo = userRelationQueryService.findUserRelationByChildId(
				borkerParentId).getQueryUserRelationInfo();
			
			if (orgRelationInfo != null) {
				personalInfo.setReferees(orgRelationInfo.getMemberNo());
			}
		}
		
		BeanCopier.staticCopy(userBase, personalInfo);
		List<Role> roleList = authorityService.getRolesByUserId(userId, 0, 12).getResult();
		LoanerBaseInfo loanerBaseInfo = loanerBaseInfoService.findByUserId(userId);
		if (loanerBaseInfo != null) {
			model.addAttribute("role", "0");
		} else {
			model.addAttribute("role", "1");
		}
		model.addAttribute("info", personalInfo);
		model.addAttribute("roleList", roleList);
		model.addAttribute("loanerInfo", loanerBaseInfo);
		return USER_MANAGE_PATH + "personalUserInfoFDL.vm";
		
	}
	
	public String updateInstitutionsUser(String userBaseId, long userId, Model model)
																						throws Exception {
		model.addAttribute("uploadHost", "");
		List<Role> roleList = authorityService.getRolesByUserId(userId, 1, 12).getResult();
		InstitutionsInfo institutionsInfo = userQueryService.queryInstitutionsInfoByBaseId(
			userBaseId).getQueryInstitutionsInfo();
		UserInfo userInfo = userQueryService.queryByUserBaseId(userBaseId).getQueryUserInfo();
		BeanCopier.staticCopy(userInfo, institutionsInfo);
		model.addAttribute("roleList", roleList);
		model.addAttribute("info", institutionsInfo);
		return USER_MANAGE_PATH + "updateInstitutionsUser.vm";
	}
	
	@RequestMapping("personalManage/detailPersonalInfo")
	public String detailPersonalInfo(String userBaseId, long userId, String memberNo, Model model)
																									throws Exception {
		model.addAttribute("uploadHost", "");
		PersonalInfo personalInfo = userQueryService.queryPersonalInfoByBaseId(userBaseId)
			.getQueryPersonalInfo();
		UserInfo userInfo = userQueryService.queryByUserBaseId(userBaseId).getQueryUserInfo();
		BeanCopier.staticCopy(userInfo, personalInfo);
		List<Role> roleList = authorityService.getRolesByUserId(userId, 1, 12).getResult();
		long parentId = -2;
		if (roleList.get(0).getId() == 12) {
			if (StringUtil.isNotBlank(personalInfo.getReferees())) {
				UserRelationInfo userRelationInfo = userRelationQueryService
					.findUserRelationByChildId(personalInfo.getUserId()).getQueryUserRelationInfo();
				if (userRelationInfo != null) {
					
					long borkerParentId = userRelationInfo.getParentId();
					
					UserRelationInfo orgRelationInfo = userRelationQueryService
						.findUserRelationByChildId(borkerParentId).getQueryUserRelationInfo();
					
					if (orgRelationInfo != null) {
						personalInfo.setReferees(orgRelationInfo.getMemberNo());
						parentId = orgRelationInfo.getParentId();
					}
					UserInfo user = userQueryService.queryByUserId(borkerParentId)
						.getQueryUserInfo();
					if (user != null) {
						model.addAttribute("refereesName", user.getRealName());
					}
				}
			}
		} else if (roleList.get(0).getId() == 11) {
			if (memberNo != null && !"".equals(memberNo)) {
				UserRelationInfo userRelationInfo = userRelationQueryService
					.findUserRelationByChildId(personalInfo.getUserId()).getQueryUserRelationInfo();
				if (userRelationInfo != null)
					parentId = userRelationInfo.getParentId();
			}
		}
		UserInfo userBase = userQueryService.queryByUserId(parentId).getQueryUserInfo();
		LoanerBaseInfo loanerInfo = loanerBaseInfoService.findByUserId(userId);
		if (userBase != null)
			model.addAttribute("institution", userBase.getRealName());
		if (loanerInfo != null) {
			model.addAttribute("loanerInfo", loanerInfo);
			model.addAttribute("role", "1");
		}
		model.addAttribute("info", personalInfo);
		model.addAttribute("roleList", roleList);
		return USER_MANAGE_PATH + "detailPersonalInfo.vm";
	}
	
	@ResponseBody
	@RequestMapping("personalManage/addPersonalUserSubmit")
	public Object addPersonalUserSubmit(HttpServletRequest request) {
		PersonalBackstageRegisterOrder registerOrder = new PersonalBackstageRegisterOrder();
		LoanerBaseInfoOrder loanerOrder = new LoanerBaseInfoOrder();
		WebUtil.setPoPropertyByRequest(registerOrder, request);
		WebUtil.setPoPropertyByRequest(loanerOrder, request);
		setRoles(request, registerOrder);
		JSONObject jsonobj = new JSONObject();
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
		YrdBaseResult baseResult = registerService.personalBackstageRegister(registerOrder);
		if (baseResult.isSuccess()) {
			UserInfo userInfo = userQueryService.queryByUserName(registerOrder.getUserName())
				.getQueryUserInfo();
			for (SysUserRoleEnum roleEnum : registerOrder.getRole()) {
				if (roleEnum == SysUserRoleEnum.LOANER) {
					loanerOrder.setUserId(userInfo.getUserId());
					loanerOrder.setLoanerUserName(request.getParameter("userName"));
					loanerOrder.setLoanerRealName(request.getParameter("realName"));
					loanerBaseInfoService.insert(loanerOrder);
					// addAttachfile(userInfo.getUserBaseId(), request);
				}
			}
			jsonobj.put("code", 1);
			jsonobj.put("message", "个人开户成功");
		} else {
			jsonobj.put("code", 0);
			jsonobj.put("message", baseResult.getMessage());
			
		}
		return jsonobj;
	}
	
	@ResponseBody
	@RequestMapping("personalManage/updatePersonalUserSubmit")
	public Object updatePersonalUserSubmit(HttpServletRequest request) throws Exception {
		UpdatePersonalOrder updatePersonalOrder = new UpdatePersonalOrder();
		LoanerBaseInfoOrder loanerOrder = new LoanerBaseInfoOrder();
		// CommonAttachmentOrder attachment = new CommonAttachmentOrder();
		JSONObject jsonobj = new JSONObject();
		WebUtil.setPoPropertyByRequest(updatePersonalOrder, request);
		WebUtil.setPoPropertyByRequest(loanerOrder, request);
		UserInfo userInfo = userQueryService.queryByUserId(loanerOrder.getUserId())
			.getQueryUserInfo();
		updatePersonalOrder.setUserName(userInfo.getUserName());
		loanerOrder.setLoanerUserName(userInfo.getUserName());
		loanerOrder.setLoanerRealName(userInfo.getRealName());
		if (StringUtil.isBlank(request.getParameter("mobileBinding"))) {
			updatePersonalOrder.setMobileBinding(BooleanEnum.NO);
		} else {
			updatePersonalOrder.setMobileBinding(BooleanEnum.getByCode(request
				.getParameter("mobileBinding")));
		}
		if (StringUtil.isBlank(request.getParameter("mailBinding"))) {
			updatePersonalOrder.setMailBinding(BooleanEnum.NO);
		} else {
			updatePersonalOrder.setMailBinding(BooleanEnum.getByCode(request
				.getParameter("mailBinding")));
		}
		setRoles(request, updatePersonalOrder);
		String mobile = null;
		if (StringUtil.isEmpty(updatePersonalOrder.getMobile())) {
			mobile = userInfo.getMobile();
		} else {
			mobile = updatePersonalOrder.getMobile();
		}
		
		String mail = null;
		if (StringUtil.isEmpty(updatePersonalOrder.getMobile())) {
			mail = userInfo.getMail();
		} else {
			mail = updatePersonalOrder.getMail();
		}
		
		updatePersonalOrder.setMobile(mobile);
		updatePersonalOrder.setMail(mail);
		YrdBaseResult baseResult = registerService.updateBackstageRegister(updatePersonalOrder);
		if (baseResult.isSuccess()) {
			if (loanerOrder != null) {
				loanerBaseInfoService.updateById(loanerOrder);
				
			}
			jsonobj.put("code", 1);
			jsonobj.put("message", "修改个人用户信息成功");
		} else {
			jsonobj.put("code", 0);
			jsonobj.put("message", "修改个人用户信息失败,原因[" + baseResult.getMessage() + "]");
		}
		return jsonobj;
	}
	
	@RequestMapping("userBaseInfoManage")
	public String userBaseInfoManage(HttpServletRequest request, PageParam pageParam, Model model)
																									throws Exception {
		UserQueryOrder queryOrder = new UserQueryOrder();
		WebUtil.setPoPropertyByRequest(queryOrder, request);
		queryOrder.setPageSize(pageParam.getPageSize());
		queryOrder.setPageNumber(pageParam.getPageNo());
		String state = request.getParameter("state");
		queryOrder.setState(UserStateEnum.getByCode(state));
		//		queryOrder.setAccountIsNull("NO");
		QueryBaseBatchResult<UserInfo> baseBatchResult = userQueryService
			.commonQueryUserInfo(queryOrder);
		model.addAttribute("queryConditions", queryOrder);
		model.addAttribute("page", PageUtil.getCovertPage(baseBatchResult));
		return USER_MANAGE_PATH + "userManage.vm";
	}
	
	@RequestMapping("userBaseInfoManage/updateUserRole")
	public String updateUserRole(String userBaseId, long userId, Model model) throws Exception {
		UserInfo baseInfo = userQueryService.queryByUserBaseId(userBaseId).getQueryUserInfo();
		List<Role> roleList = authorityService.getRolesByUserId(userId, 0, 12).getResult();
		if (baseInfo.getType() == UserTypeEnum.GR) {
			PersonalInfo personalInfo = userQueryService.queryPersonalInfoByBaseId(userBaseId)
				.getQueryPersonalInfo();
			if (personalInfo != null) {
				model.addAttribute("referees", personalInfo.getReferees());
			}
			
		}
		Pagination<Role> rolePagination = authorityService.getAllRoles(0, 999999);
		List<Role> roleAll = rolePagination.getResult();
		List<Role> roleSelects = new ArrayList<Role>();
		if (ListUtil.isNotEmpty(roleAll)) {
			for (Role role : roleAll) {
				if (role.getId() == 1 || (role.getId() >= 7 && role.getId() <= 14)) {
					continue;
				}
				roleSelects.add(role);
			}
		}
		model.addAttribute("roleSelects", roleSelects);
		
		model.addAttribute("info", baseInfo);
		model.addAttribute("roleList", roleList);
		return USER_MANAGE_PATH + "userBaseInfoUpdate.vm";
	}
	
	@ResponseBody
	@RequestMapping("userBaseInfoManage/updateUserRoleSubmit")
	public Object updateUserRoleSubmit(String userBaseId, String type, String state, int... roleIds)
																									throws Exception {
		JSONObject jsonobj = new JSONObject();
		UserInfo userBaseInfo = userQueryService.queryByUserBaseId(userBaseId).getQueryUserInfo();
		UserGrantStateUpdateOrder stateUpdateOrder = new UserGrantStateUpdateOrder();
		stateUpdateOrder.setUserBaseId(userBaseId);
		stateUpdateOrder.setRoleIds(roleIds);
		stateUpdateOrder.setUserStateEnum(UserStateEnum.getByCode(state));
		YrdBaseResult result = userBaseInfoManager.updateUserGrantAndState(stateUpdateOrder);
		if (userBaseInfo.getType() == UserTypeEnum.JG) {
			
			if (result.isSuccess()) {
				jsonobj.put("code", 1);
				jsonobj.put("message", "修改机构信息成功");
			} else {
				jsonobj.put("code", 0);
				jsonobj.put("message", "修改机构信息失败");
			}
		} else {
			
			if (result.isSuccess()) {
				jsonobj.put("code", 1);
				jsonobj.put("message", "修改个人用户信息成功");
			} else {
				jsonobj.put("code", 0);
				jsonobj.put("message", "修改个人用户信息失败");
			}
		}
		
		return jsonobj;
	}
	
	@RequestMapping("userBaseInfoManage/addAdmin")
	public String addAdmin(long parentId, Model model) throws Exception {
		model.addAttribute("parentId", parentId);
		model.addAttribute("uploadHost", "");
		Pagination<Role> rolePagination = authorityService.getAllRoles(0, 999999);
		List<Role> roleAll = rolePagination.getResult();
		List<Role> roleSelects = new ArrayList<Role>();
		if (ListUtil.isNotEmpty(roleAll)) {
			for (Role role : roleAll) {
				if (role.getId() == 1 || (role.getId() >= 7 && role.getId() <= 14)) {
					continue;
				}
				roleSelects.add(role);
			}
		}
		model.addAttribute("roleSelects", roleSelects);
		return USER_MANAGE_PATH + "addAdmin.vm";
	}
	
	/**
	 * 添加后台管理员
	 * 
	 * @return
	 */
	@ResponseBody
	@RequestMapping("userBaseInfoManage/addAdminSubmit")
	public Object addAdminSubmit(HttpSession session, HttpServletRequest request, int... roleIds) {
		AdminUserRegisterOrder userRegisterOrder = new AdminUserRegisterOrder();
		WebUtil.setPoPropertyByRequest(userRegisterOrder, request);
		JSONObject json = new JSONObject();
		try {
			userRegisterOrder.setRoles(roleIds);
			YrdBaseResult baseResult = registerService.adminUserRegister(userRegisterOrder);
			if (baseResult.isSuccess()) {
				json.put("code", 1);
				json.put("message", "新增管理员成功");
			} else {
				json.put("code", 0);
				json.put("message", baseResult.getYrdResultEnum().getMessage());
			}
		} catch (Exception e) {
			logger.error("新增管理员异常", e);
			json.put("code", 0);
			json.put("message", "新增管理员异常");
		}
		return json;
	}
	
	@RequestMapping("changeBroker")
	public String changeBroker(HttpSession session,Model model) {
		String token = UUID.randomUUID().toString();
		session.setAttribute("token", token);
        UserRoleQueryOrder userRoleQueryOrder = new UserRoleQueryOrder();
        userRoleQueryOrder.setPageSize(99999999);
        userRoleQueryOrder.setUserStateEnum(UserStateEnum.NORMAL);
        userRoleQueryOrder.setRoleEnum(SysUserRoleEnum.MARKETING);
        List<UserInfo> users = userQueryService.queryRoleUserInfo(userRoleQueryOrder).getPageList();
        model.addAttribute("users",users);
		return USER_MANAGE_PATH + "changeBroker.vm";
	}
	
	@ResponseBody
	@RequestMapping("changeBrokerSubmit")
	public Object changeBrokerSubmit(String userName, String borkerNo, String token,
										HttpSession session) {
		String getToken = (String) session.getAttribute("token");
		
		JSONObject json = new JSONObject();
		
		try {
			if (token != null && token.equals(getToken)) {
				session.removeAttribute("token");
				UserInfo userInfo = userQueryService.queryByUserName(userName).getQueryUserInfo();
				ChangeBrokerOrder changeBrokerOrder = new ChangeBrokerOrder();
				changeBrokerOrder.setChildId(userInfo.getUserId());
				changeBrokerOrder.setReferees(borkerNo);
				YrdBaseResult result = userBaseInfoManager.changeBroker(changeBrokerOrder);
				if (result.isSuccess()) {
					json.put("code", 1);
					json.put("message", "更改成功");
				} else {
					json.put("code", 0);
					json.put("message", "更改失败");
				}
			} else {
				json.put("code", 0);
				json.put("message", "重复提交");
			}
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			json.put("code", 0);
			json.put("message", "更改失败");
		}
		return json;
	}


    @ResponseBody
    @RequestMapping("changeInvestorToBroker")
    public Object changeInvestorToBroker(HttpServletRequest request, String token,
                                     HttpSession session) {
        String getToken = (String) session.getAttribute("token");

        JSONObject json = new JSONObject();

        try {
            if (token != null && token.equals(getToken)) {
                session.removeAttribute("token");
                String userName = request.getParameter("userName");
                String orgId = request.getParameter("orgId");
                UserInfo userInfo = userQueryService.queryByUserName(userName).getQueryUserInfo();
                InvestorToBrokerOrder changeBrokerOrder = new InvestorToBrokerOrder();
                changeBrokerOrder.setInvestorId(userInfo.getUserId());
                changeBrokerOrder.setOrgId(Long.valueOf(orgId));
                changeBrokerOrder.setServletPath(request.getServletContext().getRealPath("/"));
                YrdBaseResult result = userBaseInfoManager.changeInvestorToBroker(changeBrokerOrder);
                if (result.isSuccess()) {
                    json.put("code", 1);
                    json.put("message", "更改成功");
                } else {
                    json.put("code", 0);
                    json.put("message", "更改失败");
                }
            } else {
                json.put("code", 0);
                json.put("message", "重复提交");
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            json.put("code", 0);
            json.put("message", "更改失败");
        }
        return json;
    }


    @ResponseBody
    @RequestMapping("changeBrokerToInvestor")
    public Object changeBrokerToInvestor(HttpServletRequest request, String token,
                                         HttpSession session) {
        String getToken = (String) session.getAttribute("token");

        JSONObject json = new JSONObject();

        try {
            if (token != null && token.equals(getToken)) {
                session.removeAttribute("token");
                String userName = request.getParameter("userName");
                String referees = request.getParameter("referees");
                UserInfo userInfo = userQueryService.queryByUserName(userName).getQueryUserInfo();
                BrokerToInvestorOrder changeBrokerOrder = new BrokerToInvestorOrder();
                changeBrokerOrder.setBrokerId(userInfo.getUserId());
                changeBrokerOrder.setReferees(referees);
                YrdBaseResult result = userBaseInfoManager.changeBrokerToInvestor(changeBrokerOrder);
                if (result.isSuccess()) {
                    json.put("code", 1);
                    json.put("message", "更改成功");
                } else {
                    json.put("code", 0);
                    json.put("message", "更改失败");
                }
            } else {
                json.put("code", 0);
                json.put("message", "重复提交");
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            json.put("code", 0);
            json.put("message", "更改失败");
        }
        return json;
    }


	
	@ResponseBody
	@RequestMapping("changeMarkettingSubmit")
	public Object changeMarkettingSubmit(String brokerUserName, String markettingUserName,
											String token, HttpSession session) {
		String getToken = (String) session.getAttribute("token");
		
		JSONObject json = new JSONObject();
		try {
			if (token != null && token.equals(getToken)) {
				session.removeAttribute("token");
				ChangeMarkettingOrder changeMarkettingOrder = new ChangeMarkettingOrder();
				changeMarkettingOrder.setMarkettingUserName(markettingUserName);
				changeMarkettingOrder.setBrokerUserName(brokerUserName);
				YrdBaseResult result = userBaseInfoManager.changeMarketting(changeMarkettingOrder);
				if (result.isSuccess()) {
					json.put("code", 1);
					json.put("message", "更改成功");
				} else {
					json.put("code", 0);
					json.put("message", "更改失败");
				}
			} else {
				json.put("code", 0);
				json.put("message", "重复提交");
			}
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			json.put("code", 0);
			json.put("message", "更改失败");
		}
		return json;
	}
	
	/**
	 * 后台实名认证
	 * 
	 * @param userBaseId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "backRealNameAut")
	public JSONObject backRealNameAut(String userBaseId) {
		JSONObject jsonobj = new JSONObject();
		try {
			YrdBaseResult baseResult = realNameAuthenticationService
				.sendPersonalRealNameInfo(userBaseId);
			if (baseResult.isSuccess()) {
				jsonobj.put("code", 1);
				jsonobj.put("message", baseResult.getMessage());
			} else {
				jsonobj.put("code", 0);
				jsonobj.put("message", baseResult.getMessage());
			}
		} catch (Exception e) {
			logger.error("后台个人实名发送异常", e);
			jsonobj.put("code", 0);
			jsonobj.put("message", "后台个人实名发送异常");
		}
		return jsonobj;
	}
	
	@ResponseBody
	@RequestMapping("userBaseInfoManage/queryBalance")
	public Object queryBalance(String userBaseId, Model model) throws Exception {
		JSONObject json = new JSONObject();
		UserQueryResult queryResult = userQueryService.queryByUserBaseId(userBaseId);
		UserInfo userInfo = queryResult.getQueryUserInfo();
		// yrd资金资料
		QueryAccountResult accountResult = userAccountQueryService.getAccountInfo(userInfo
			.getUserId());
		YjfAccountInfo accountServiceInfo = accountResult.getYjfAccountInfo();
		logger.info("调用open查找资金账户返回，{}", accountServiceInfo);
		if (accountServiceInfo != null) {
			// 不可以余额
			json.put("code", "1");
			json.put("freezeAmount", accountServiceInfo.getFreezeAmount().toStandardString());
			json.put("availableBalance", accountServiceInfo.getAvailableBalance()
				.toStandardString());
			json.put("balance", accountServiceInfo.getBalance().toStandardString());
			return json;
		}
		json.put("code", "0");
		return json;
	}
	
	@ResponseBody
	@RequestMapping("updateState")
	public Object updateState(String userBaseId, String state) throws Exception {
		JSONObject jsonobj = new JSONObject();
		
		UserStateEnum stateEnum = UserStateEnum.getByCode(state);
		YrdBaseResult baseResult = userBaseInfoManager.updateUserState(userBaseId, stateEnum);
		if (baseResult.isSuccess()) {
			jsonobj.put("code", 1);
			jsonobj.put("message", "修改用户状态成功");
		}
		if (!baseResult.isSuccess()) {
			jsonobj.put("code", 0);
			jsonobj.put("message", "修改用户状态失败[" + baseResult.getMessage() + "]");
		}
		return jsonobj;
	}


    @ResponseBody
    @RequestMapping("updateUserLevel")
    public Object updateUserLevel(UpdateUserLevelOrder levelOrder) throws Exception {
        JSONObject json = new JSONObject();


        YrdBaseResult baseResult = userBaseInfoManager.updateUserLevel(levelOrder);
        if (baseResult.isSuccess()) {
            json.put("code", 1);
            json.put("message", "修改用户等级成功");
        }
        if (!baseResult.isSuccess()) {
            json.put("code", 0);
            json.put("message", "修改用户等级失败[" + baseResult.getMessage() + "]");
        }
        return json;
    }


	
	@ResponseBody
	@RequestMapping("common/validationAccountName")
	public Object validationAccountName(String accountName) throws Exception {
		logger.info("验证易极付帐户不存在，入参：{}", accountName);
		JSONObject jsonobj = new JSONObject();
		CustomerResult returnEnum = this.customerService.checkUserNameExist(accountName,
			this.getOpenApiContext());
		if (!returnEnum.isExsit()) {
			jsonobj.put("code", 1);
			jsonobj.put("message", "用户可以用");
		} else {
			jsonobj.put("code", 0);
			jsonobj.put("message", "用户已存在");
		}
		logger.info("验证易极付帐户不存在，出参：{}", jsonobj);
		return jsonobj;
	}
	
	@ResponseBody
	@RequestMapping("common/validationUserName")
	public Object validationUserName(String userName) throws Exception {
		logger.info("验证" + AppConstantsUtil.getProductName() + "金融用户不存在，入参：{}", userName);
		JSONObject jsonobj = new JSONObject();
		
		YrdBaseResult result = userBaseInfoManager.validationUserName(userName);
		// 验证用户不存在
		if (result.isSuccess()) {
			jsonobj.put("code", 1);
			jsonobj.put("message", "用户可以用");
		} else {
			jsonobj.put("code", 0);
			jsonobj.put("message", "用户已存在");
		}
		logger.info("验证" + AppConstantsUtil.getProductName() + "金融用户不存在，入参：{}", userName);
		return jsonobj;
	}
	
	@ResponseBody
	@RequestMapping("common/validationReferees")
	public Object validationReferees(String referees) throws Exception {
		
		JSONObject jsonobj = new JSONObject();
		YrdBaseResult returnEnum = userBaseInfoManager.validationReferees(referees);
		if (returnEnum.isSuccess()) {
			jsonobj.put("code", 1);
			jsonobj.put("message", "该推荐人可用");
		} else {
			jsonobj.put("code", 0);
			jsonobj.put("message", "该推荐人不可用");
		}
		return jsonobj;
	}
	
	
	
	@RequestMapping("userImport")
	public String userImport(HttpSession session, PageParam pageParam, Model model) {
		
		return USER_MANAGE_PATH + "user_import.vm";
	}
	
	@ResponseBody
	@RequestMapping(value = "userImport/submit")
	public Object userImportSubmit(String usersInfoText) throws Exception {
		JSONObject jsonobj = new JSONObject();
		int count = 0;
		logger.info("userImportSubmit:"+usersInfoText.length());
		try {
			if(usersInfoText.length()>0){
				YrdBaseResult rs = doFormatCheck(usersInfoText);
				if(rs.isSuccess()){
					List<InvestorRegisterOrder> investorList = creatInvestorRegisterOrder(usersInfoText);
					if(investorList.size()>0){
						for(InvestorRegisterOrder order : investorList){
							UserRegisterResult regResult = registerService.investorRegister(order);
							if(!regResult.isSuccess()){//遇到失败就退出
								jsonobj.put("code", 0);
								jsonobj.put("message", "导入"+count+"个后失败 :"+order.toString()+regResult.getMessage());
								return jsonobj;
							}else{
								count++;
							}
						}
						
						jsonobj.put("code", 1);
						jsonobj.put("message", "批量导入完成！");
						
					}else{
						jsonobj.put("code", 0);
						jsonobj.put("message", "未取到有效的用户信息！");
					}
				}else{
					jsonobj.put("code", 0);
					jsonobj.put("message",rs.getMessage());
				}
				
			}else{
				jsonobj.put("code", 0);
				jsonobj.put("message", "无用户信息！");
			}
		 
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			jsonobj.put("code", 0);
			jsonobj.put("message", "导入用户失败！");
		}
		return jsonobj;
	}

	/**
	 * 校验格式
	 * @param usersInfoText
	 * @return
	 */
	private YrdBaseResult doFormatCheck(String usersInfoText) {
		
		
		String sqlStr2 =  usersInfoText.toUpperCase();
		YrdBaseResult rs = new YrdBaseResult();
		if(sqlStr2.indexOf("DELETE")>=0||sqlStr2.indexOf("UPDATE")>=0||sqlStr2.indexOf("INSERT")>=0){
			rs.setSuccess(false);
			rs.setMessage("有SQL注入风险");
			return rs;
		}
		
		String[] userInfoes = usersInfoText.split("#");
		if(userInfoes.length<=0){
			rs.setSuccess(false);
			rs.setMessage("请用#隔开每个用户的注册信息");
			return rs;
		}
		for(int i = 0;i<userInfoes.length;i++){
			String userInfoStr = userInfoes[i];
			String[] userInfo = userInfoStr.split(",");
			if(userInfo.length!=4&&i!=userInfoes.length-1){
				rs.setSuccess(false);
				rs.setMessage("用户信息格式异常："+userInfoStr);
				return rs;
			}
		}
		rs.setSuccess(true);
		return rs;
	}

	/**
	 * 转换为InvestorRegisterOrder
	 * @param usersInfoText
	 * @return
	 */
	private List<InvestorRegisterOrder> creatInvestorRegisterOrder(
			String usersInfoText) {
		
		 List<InvestorRegisterOrder> investorList = new ArrayList<InvestorRegisterOrder>();
		
		String[] userInfoes = usersInfoText.split("#");
		
		for(int i = 0;i<userInfoes.length;i++){
			String userInfoStr = userInfoes[i];
			String[] userInfo = userInfoStr.split(",");
			if(userInfo.length==4){
				InvestorRegisterOrder  investorRegisterOrder = new InvestorRegisterOrder();
				List<SysUserRoleEnum> role = new ArrayList<SysUserRoleEnum>();
				role.add(SysUserRoleEnum.INVESTOR);
				role.add(SysUserRoleEnum.PUBLIC);
				String userName = StringUtil.isEmpty(userInfo[0]) ?null:userInfo[0].trim();
				String realName = StringUtil.isEmpty(userInfo[1]) ?null:userInfo[1].trim();
				String mobile = StringUtil.isEmpty(userInfo[2]) ?null:userInfo[2].trim();
				String mail = StringUtil.isEmpty(userInfo[3]) ?null:userInfo[3].trim();
				investorRegisterOrder.setUserName(userName);
				investorRegisterOrder.setRealName(realName);
				investorRegisterOrder.setMobile(mobile);
				investorRegisterOrder.setMail(mail);
				investorRegisterOrder.setLogPassword("888888");
				investorRegisterOrder.setMobileBinding("IS");
				investorRegisterOrder.setRole(role);
				investorList.add(investorRegisterOrder);
			}
		}
		return investorList;
	}
	
}
