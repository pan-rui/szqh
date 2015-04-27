package com.yjf.yrd.front.controller.user;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.yjf.common.lang.util.StringUtil;
import com.yjf.common.lang.util.money.Money;
import com.yjf.yrd.base.UserAccountInfoBaseController;
import com.yjf.yrd.dataobject.ActivityDetail;
import com.yjf.yrd.dataobject.OperatorInfoDO;
import com.yjf.yrd.dataobject.Role;
import com.yjf.yrd.dataobject.UserLoginLog;
import com.yjf.yrd.integration.bornapi.enums.YjfRegesterTypeEnum;
import com.yjf.yrd.integration.openapi.enums.AttributionEnum;
import com.yjf.yrd.integration.openapi.enums.PeasonSexEnum;
import com.yjf.yrd.integration.openapi.enums.RealNameBusinessStatusEnum;
import com.yjf.yrd.integration.openapi.info.YjfAccountInfo;
import com.yjf.yrd.integration.openapi.order.UserQuickCertifyOrder;
import com.yjf.yrd.integration.openapi.result.CustomerResult;
import com.yjf.yrd.integration.openapi.result.RealNameStatusResult;
import com.yjf.yrd.integration.web.server.YjfLoginWebServer;
import com.yjf.yrd.integration.web.server.impl.YjfPayPwdOrder;
import com.yjf.yrd.integration.web.server.impl.YjfRegisterOrder;
import com.yjf.yrd.page.Page;
import com.yjf.yrd.page.PageParam;
import com.yjf.yrd.page.Pagination;
import com.yjf.yrd.session.SessionLocal;
import com.yjf.yrd.session.SessionLocalManager;
import com.yjf.yrd.trade.order.EBankDepositOrder;
import com.yjf.yrd.user.IOperatorInfoService;
import com.yjf.yrd.user.info.UserInfo;
import com.yjf.yrd.user.info.UserLoginLogInfo;
import com.yjf.yrd.user.info.UserRelationInfo;
import com.yjf.yrd.user.order.ApplyRealNameOrder;
import com.yjf.yrd.user.order.NonMainlandRealAuthenticateOrder;
import com.yjf.yrd.user.order.UpdateRealNameStatusOrder;
import com.yjf.yrd.user.valueobject.PointsQueryConditions;
import com.yjf.yrd.util.AppConstantsUtil;
import com.yjf.yrd.util.MD5Util;
import com.yjf.yrd.web.util.PageUtil;
import com.yjf.yrd.web.util.TradeStatisticsUtil;
import com.yjf.yrd.ws.enums.CertTypeEnum;
import com.yjf.yrd.ws.enums.DivisionPhaseEnum;
import com.yjf.yrd.ws.enums.RealNameAuthStatusEnum;
import com.yjf.yrd.ws.enums.SysUserRoleEnum;
import com.yjf.yrd.ws.enums.TradeStatusEnum;
import com.yjf.yrd.ws.enums.UserTypeEnum;
import com.yjf.yrd.ws.info.LoanDemandTradeVOInfo;
import com.yjf.yrd.ws.info.TradeDetailVOInfo;
import com.yjf.yrd.ws.info.UserPointInfo;
import com.yjf.yrd.ws.order.AddPointsOrder;
import com.yjf.yrd.ws.order.TradeDetailQueryOrder;
import com.yjf.yrd.ws.result.YrdBaseResult;
import com.yjf.yrd.ws.service.GiftMoneyAssignProcessService;
import com.yjf.yrd.ws.service.YrdResultEnum;
import com.yjf.yrd.ws.service.query.order.QueryLoanTradeOrder;
import com.yjf.yrd.ws.service.query.result.QueryBaseBatchResult;
import com.yjf.yrd.ws.service.query.result.TradeDetailBatchResult;
import com.yjf.yrd.ws.statistics.TradeAmountStatisticsInfo;
import com.yjf.yrd.ws.statistics.result.TradeStatisticsResult;

/**
 * 
 * 
 * @Filename UserBaseController.java
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
@RequestMapping("userManage")
public class UserBaseController extends UserAccountInfoBaseController {
	/** 返回页面路径 */
	String USER_MANAGE_PATH = "/front/user/activation/";
	String USER_MANAGE_FRIEND_PATH = "/front/user/friend/";
	private final String md5AddString = "S1as#%DF#@D*(=-@@!";
	
	@Autowired
	protected IOperatorInfoService operatorInfoService;
	@Autowired
	YjfLoginWebServer yjfLoginWebServer;
	
	@Autowired
	GiftMoneyAssignProcessService giftMoneyAssignProcessService;
	
	// -----------------------------------------------------------------验证用户信息------------------------------------------------------------------------
	
	@ResponseBody
	@RequestMapping("validationUserName")
	public Object validationUserName(String userName) throws Exception {
		logger.info("验证" + AppConstantsUtil.getProductName() + "金融用户不存在，入参：{}", userName);
		JSONObject jsonobj = new JSONObject();
		YrdBaseResult returnEnum = new YrdBaseResult();
		returnEnum = userBaseInfoManager.validationUserName(userName);
		// 验证用户不存在
		if (returnEnum.isSuccess()) {
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
	@RequestMapping("getBindEmail")
	public Object getBindEmail(String userName) throws Exception {
		logger.info("获取" + AppConstantsUtil.getProductName() + "金融用户邮箱，入参：{}", userName);
		JSONObject jsonobj = new JSONObject();
		UserInfo baseUser = userQueryService.queryByUserName(userName).getQueryUserInfo();
		if (baseUser != null) {
			String mail = baseUser.getMail();
			if (!StringUtil.isEmpty(mail)) {
				String[] strMail = mail.split("@");
				
				jsonobj.put("mail", mail);
				jsonobj.put("message", strMail[0].substring(0, 3) + "********@" + strMail[1]);
				
			}
			jsonobj.put("code", 1);
			jsonobj.put("mobile", baseUser.getMobile());
		} else {
			jsonobj.put("code", 0);
			jsonobj.put("message", "");
		}
		logger.info("获取" + AppConstantsUtil.getProductName() + "金融用户邮箱，出参：{}", userName);
		return jsonobj;
	}
	
	@ResponseBody
	@RequestMapping("validationIsUserName")
	public Object validationIsUserName(String userName) throws Exception {
		logger.info("验证" + AppConstantsUtil.getProductName() + "金融用户存在，入参：{}", userName);
		JSONObject jsonobj = new JSONObject();
		YrdBaseResult returnEnum = new YrdBaseResult();
		returnEnum = userBaseInfoManager.validationUserName(userName);
		// 验证用户已存在
		if (returnEnum.isSuccess()) {
			jsonobj.put("code", 0);
			jsonobj.put("message", "用户不存在");
		} else {
			jsonobj.put("code", 1);
			jsonobj.put("message", "用户已存在");
		}
		logger.info("验证" + AppConstantsUtil.getProductName() + "金融用户存在，出参：{}", jsonobj);
		return jsonobj;
	}
	
	@ResponseBody
	@RequestMapping("validationAccountName")
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
	
	@RequestMapping("investReserve")
	public String InvestReserve(String certifyType, String balanceType, ModelMap model) {
		model.addAttribute("certifyType", certifyType);
		model.addAttribute("balanceType", balanceType);
		return USER_MANAGE_PATH + "investReserve.vm";
	}
	
	// ------------------------------------------------------------账户首页----------------------------------------------------------------------------
	@RequestMapping("userHome")
	public String userHome(HttpSession session, Model model) {
		UserRelationInfo relationInfo = userRelationQueryService.findUserRelationByChildId(
			SessionLocalManager.getSessionLocal().getUserId()).getQueryUserRelationInfo();
		if (relationInfo != null) {
			model.addAttribute("userMemoNo", relationInfo.getMemberNo());
		} else {
			model.addAttribute("userMemoNo", null);
		}
		//model.addAttribute("loginTime", SessionLocalManager.getSessionLocal().getLastDate());
		//获取登录信息
		List<UserLoginLogInfo> loginLog = userQueryService.queryUserLoginLog(SessionLocalManager.getSessionLocal().getUserId());
			
		if (loginLog != null) {
			if (loginLog.size() > 1) {
				model.addAttribute("loginAddress", loginLog.get(1).getLoginAddress());
				model.addAttribute("loginTime", loginLog.get(1).getLoginTime());
			} else if (loginLog.size() == 0) {
				model.addAttribute("noLoginLog", "true");
			} else {
				model.addAttribute("loginAddress", loginLog.get(0).getLoginAddress());
				model.addAttribute("loginTime", loginLog.get(0).getLoginTime());
				model.addAttribute("firstLogin", "true");
			}
		} else {
			model.addAttribute("noLoginLog", "true");
		}
		//用户信息
		UserInfo userBaseInfo = getUserBaseInfo(session, model);
		
		//操作员查询
		Map<String, Object> conditions = new HashMap<String, Object>();
		conditions.put("userBaseId", userBaseInfo.getUserBaseId());
		conditions.put("limitStart", 0);
		conditions.put("pageSize", 9999);
		List<OperatorInfoDO> operatorInfos = operatorInfoService
			.queryOperatorsByProperties(conditions);
		if (operatorInfos != null && operatorInfos.size() > 0) {
			session.setAttribute("operator", "yes");
			if (2 == operatorInfos.get(0).getOperatorType()
				|| 3 == operatorInfos.get(0).getOperatorType()) {
				
			} else {
				session.setAttribute("tasks", "two");
			}
		} else {
			session.setAttribute("operator", "no");
		}
		initAccountInfo(model, false);
		
		//易7用户首页，项目推荐
		QueryLoanTradeOrder tradeQueryOrder = new QueryLoanTradeOrder();
		tradeQueryOrder.setPageSize(3);
		List<String> statusList = new ArrayList<String>();
		statusList.add(String.valueOf(TradeStatusEnum.TRADING.getValue()));
		tradeQueryOrder.setStatus(statusList);
		QueryBaseBatchResult<LoanDemandTradeVOInfo> batchResult1 = tradeBizQueryService
			.searchLoanTradeCommonQuery(tradeQueryOrder);
		model.addAttribute("tradePage", PageUtil.getCovertPage(batchResult1));
		//end
		
		
		YjfAccountInfo accountInfo = getAccountInfo(model);
		//更新本地账户的实名认证状态
		updateLocalAccountByRemote(userBaseInfo, accountInfo);
		//投资详情页的统计投资金额方式
		TradeDetailQueryOrder queryOrder = new TradeDetailQueryOrder();
		queryOrder.setUserId(SessionLocalManager.getSessionLocal().getUserId());
		queryOrder.setRoleId(SysUserRoleEnum.INVESTOR.getValue());
		queryOrder.setTransferPhase(DivisionPhaseEnum.ORIGINAL_PHASE.code());
		TradeDetailBatchResult<TradeDetailVOInfo> batchResult = tradeBizQueryService
			.searchTradeDetailQuery(queryOrder);
		model.addAttribute("allAmount", batchResult.getTotalAmount());
		//end
		
		if (accountInfo != null && accountInfo.getUserStatus().getCode().equals("W")
			&& StringUtil.isNotBlank(userBaseInfo.getMobile())) {
			YjfRegisterOrder yjfRegisterOrder = new YjfRegisterOrder();
			yjfRegisterOrder.setUserName(AppConstantsUtil.getYrdPrefixion()
											+ userBaseInfo.getUserName());
			yjfRegisterOrder.setYjfRegesterType(YjfRegesterTypeEnum.ACTIVATE);
			yjfRegisterOrder.setSystem("common");
			YrdBaseResult yrdBaseResult = yjfLoginWebServer.gotoYjfRegisterUrl(yjfRegisterOrder,
				this.getOpenApiContext());
			model.addAttribute("activeAccountURL", yrdBaseResult.getUrl());
			model.addAttribute("accountInactive", true);
		}
		
		//积分信息
		if (AppConstantsUtil.getISUserPoints()) {
			AddPointsOrder addPointsOrder = new AddPointsOrder();
			addPointsOrder.setUserId(userBaseInfo.getUserId());
			//强实名送积分
			userPointsByRuleTypeService.addAuthenticationPouints(addPointsOrder);
			//激活第三方账户送积分
			userPointsByRuleTypeService.addAccountPouints(addPointsOrder);
			//绑定银行卡送积分
			userPointsByRuleTypeService.addBankcardPouints(addPointsOrder);
			
			UserPointInfo pointInfo = new UserPointInfo();
			PointsQueryConditions pointsQueryConditions = new PointsQueryConditions();
			pointsQueryConditions.setUserName(userBaseInfo.getUserName());
			PageParam pageParam = new PageParam();
			pageParam.setPageNo(1);
			pageParam.setPageSize(1);
			Page<UserPointInfo> userPointInfo = userPointsService.page(pointsQueryConditions,
				pageParam);
			if (userPointInfo != null && userPointInfo.getTotalCount() > 0) {
				pointInfo = userPointInfo.getResult().get(0);
			}
			model.addAttribute("pointInfo", pointInfo);
		}
		
		try {
			setRole(userBaseInfo.getUserId(), model);
			countInvestAndLoan(session, model);
			setCommissionList(session, model);
			
		} catch (Exception e) {
			logger.error("countInvestAndLoan errror", e);
		}
		//统计总资产
		YjfAccountInfo account = userAccountQueryService.getAccountInfo(
				SessionLocalManager.getSessionLocal()).getYjfAccountInfo();
			model.addAttribute("yjfAccountInfo", account);
			QueryLoanTradeOrder tradeOrder = new QueryLoanTradeOrder();
			if (SessionLocalManager.getSessionLocal().getUserId() == tradeBizProcessService
				.getYrdUserId()) {
				tradeOrder.setRoleId(SysUserRoleEnum.YRD.getValue());
			} else {
				tradeOrder.setRoleId(SysUserRoleEnum.INVESTOR.getValue());
			}
			
			tradeOrder.setUserId(SessionLocalManager.getSessionLocal().getUserId());
			TradeStatisticsResult<TradeAmountStatisticsInfo> statisticsResult = tradeBizQueryService
				.searchLoanTradeSumCommonQuery(tradeOrder);
			Money totolAmount=account.getBalance().add(statisticsResult.getStatisticsInfo().getWaitSuccessPrincipleAmount()).add(statisticsResult.getStatisticsInfo().getWaitToPayInvestAmount());
			model.addAttribute("totolAmount", totolAmount);
		//end
		
		return USER_MANAGE_PATH + "userHome1.vm";
	}
	
	// ------------------------------------------------------------跳转收银台----------------------------------------------------------------------------
	@RequestMapping("rechargePage")
	public String rechargePage(HttpSession session, HttpServletResponse response)
																					throws UnsupportedEncodingException {
		String userId = SessionLocalManager.getSessionLocal().getAccountId();
		session.setAttribute("current", 1);
		EBankDepositOrder deductOrder = new EBankDepositOrder();
		deductOrder.setUserId(SessionLocalManager.getSessionLocal().getUserId());
		deductOrder.setYjfAccountId(userId);
		YrdBaseResult depositResult = deductYrdService.applyEBankDeposit(deductOrder);
		
		try {
			response.sendRedirect(depositResult.getUrl());
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		
		return null;
	}
	
	// ------------------------------------------------------------账户基本资料查询----------------------------------------------------------------------------
	
	@RequestMapping("userBasicInfo")
	public String userBasicInfo(HttpSession session, Model model) {
		session.setAttribute("current", 1);
		try {
			this.queryUserInfo(session, model);
		} catch (Exception e) {
			logger.error("查询银行信息异常", e);
		}
		return USER_MANAGE_PATH + "userBasicInfo.vm";
	}
	
	@RequestMapping("userRealNameInfo")
	public String userRealNameInfo(Model model, HttpSession session) throws Exception {
		this.queryUserInfo(session, model);
		SessionLocal sessionLocal = SessionLocalManager.getSessionLocal();
		String userBaseId = sessionLocal.getUserBaseId();
		UserInfo userBaseInfoDO = userQueryService.queryByUserBaseId(userBaseId).getQueryUserInfo();
		this.initAccountInfo(model);
		sessionLocal = SessionLocalManager.getSessionLocal();
		if (userBaseInfoDO.getRealNameAuthentication() != RealNameAuthStatusEnum.IS
			&& (sessionLocal.getCertifyLevel() >= 3 || sessionLocal.getCertifyLevel() == 1)) {
			RealNameStatusResult statusResult = this.customerService.queryRealNameCert(
				userBaseInfoDO.getAccountId(), this.getOpenApiContext());
			if (statusResult.getBusinessStatusEnum() == RealNameBusinessStatusEnum.CHECK_PASSED) {
				UpdateRealNameStatusOrder realNameStatusOrder = new UpdateRealNameStatusOrder();
				realNameStatusOrder.setAccountId(userBaseInfoDO.getAccountId());
				realNameStatusOrder.setYjfReturnStatus(RealNameAuthStatusEnum.IS.getYjfStatus());
				userBaseInfoManager.updateRealNameStatus(realNameStatusOrder);
			}
		}
		model.addAttribute("uploadHost", "");
		model.addAttribute("certifyLevel", sessionLocal.getCertifyLevel());
		if (userBaseInfoDO.getRealNameAuthentication() != null) {
			model
				.addAttribute("realNameAuthentication", userBaseInfoDO.getRealNameAuthentication());
		}
		
		session.setAttribute("token", UUID.randomUUID().toString());
		
		if (userBaseInfoDO.getType() == UserTypeEnum.GR) {
			
			return USER_MANAGE_PATH + "userRealNameInfo.vm";
		} else {
			
			return USER_MANAGE_PATH + "enterpriseInfo.vm";
		}
	}
	
	@RequestMapping("accountSetting")
	public String userBankInfo(HttpServletRequest request, HttpServletResponse response,
								Model model, HttpSession session) {
		
		try {
			initAccountInfo(model);
			
			YjfPayPwdOrder payPwdOrder = new YjfPayPwdOrder();
			payPwdOrder.setUserId(SessionLocalManager.getSessionLocal().getAccountId());
			
			YrdBaseResult yrdBaseResult = yjfLoginWebServer.gotoYjfModifyPayPwdUrl(payPwdOrder,
				this.getOpenApiContext());
			String userBaseId = SessionLocalManager.getSessionLocal().getUserBaseId();
			String randomToken = UUID.randomUUID().toString();
			String md5UserBaseId = MD5Util.getMD5_32(userBaseId + md5AddString + randomToken);
			session.setAttribute("userBaseId", userBaseId);
			session.setAttribute("randomToken", randomToken);
			model.addAttribute("modifyPayPwdUrl", yrdBaseResult.getUrl());
			model.addAttribute("md5UserBaseId", md5UserBaseId);
			model.addAttribute("userBaseId", userBaseId);
			model.addAttribute("confirmUserBaseId", SessionLocalManager.getSessionLocal()
				.getUserBaseId());
		} catch (Exception e) {
			logger.error("initAccountInfo error", e);
		}
		
		return USER_MANAGE_PATH + "accountSetting.vm";
	}
	
	@RequestMapping("userBankInfo")
	public String userBankInfo(HttpSession session, Model model) {
		try {
			this.queryUserInfo(session, model);
		} catch (Exception e) {
			logger.error("查询银行信息异常", e);
		}
		return USER_MANAGE_PATH + "userBankInfo.vm";
	}
	
	// ------------------------------------------------------------账户基本资料修改----------------------------------------------------------------------------
	
	@ResponseBody
	@RequestMapping("updateUserRealNameInfo")
	public Object updateUserRealNameInfo(HttpSession session, String realName, String certNo,
											String businessPeriod, String conCertNo,
											String certFrontPath, String certBackPath, String token)
																									throws Exception {
		
		JSONObject jsonobj = new JSONObject();
		String userBaseId = SessionLocalManager.getSessionLocal().getUserBaseId();
		UserInfo userBaseInfo = userQueryService.queryByUserBaseId(userBaseId).getQueryUserInfo();
		// Map<String,Object> resultMap = new HashMap<String, Object>();
		String getToken = (String) session.getAttribute("token");
		if (userBaseInfo.getType() == UserTypeEnum.GR && token.equals(getToken)) {
			session.removeAttribute("token");
			// 个人的时候需要同步更新user_base_info的真实名称
			ApplyRealNameOrder order = new ApplyRealNameOrder();
			order.setUserBaseId(userBaseId);
			// 改变值
			order.setRealName(realName);
			order.setCertNo(certNo);
			
			order.setBusinessPeriod(businessPeriod);
			order.setCertFrontPath(certFrontPath);
			order.setCertBackPath(certBackPath);
			// personalInfo.setCertFrontPath("http://192.168.45.212:30000//uploadfile/images/2014-06/03/110_2039062487.png");
			// personalInfo.setCertBackPath("http://192.168.45.212:30000//uploadfile/images/2014-06/03/110_2048199935.png");
			// 调用修改方法
			
			YrdBaseResult yrdBaseResult = realNameAuthenticationService.applyRealName(order);
			if (yrdBaseResult.isSuccess()) {
				jsonobj.put("code", 1);
				jsonobj.put("message", "实名认证发送成功");
			} else {
				jsonobj.put("code", 0);
				if (yrdBaseResult.getYrdResultEnum() == YrdResultEnum.DO_ACTION_STATUS_ERROR) {
					jsonobj.put("message", "你已经提交了申请，请不要重复提交");
				} else {
					jsonobj.put("message", "实名认证发送失败");
				}
				
			}
			
			// 更新SessionLocal
			SessionLocal sessionLocal = SessionLocalManager.getSessionLocal();
			sessionLocal.setRealName(realName);
			SessionLocalManager.setSessionLocal(sessionLocal);
			
		}
		return jsonobj;
	}
	
	@ResponseBody
	@RequestMapping("updateWeakUserRealNameInfo")
	public Object updateWeakUserRealNameInfo(HttpSession session, UserQuickCertifyOrder order,
												String token) throws Exception {
		
		JSONObject jsonobj = new JSONObject();
		String userBaseId = SessionLocalManager.getSessionLocal().getUserBaseId();
		UserInfo userBaseInfo = userQueryService.queryByUserBaseId(userBaseId).getQueryUserInfo();
		// Map<String,Object> resultMap = new HashMap<String, Object>();
		String getToken = (String) session.getAttribute("token");
		if (userBaseInfo.getType() == UserTypeEnum.GR && token.equals(getToken)) {
			session.removeAttribute("token");
			order.setUserId(userBaseInfo.getAccountId());
			YrdBaseResult yrdBaseResult = realNameAuthenticationService
				.applyUserQuickCertifyOrder(order);
			if (yrdBaseResult.isSuccess()) {
				jsonobj.put("code", 1);
				jsonobj.put("message", "实名认证发送成功");
			} else {
				jsonobj.put("code", 0);
				jsonobj.put("message", "实名认证发送失败");
			}
			
			// 更新SessionLocal
			SessionLocal sessionLocal = SessionLocalManager.getSessionLocal();
			sessionLocal.setRealName(order.getRealName());
			SessionLocalManager.setSessionLocal(sessionLocal);
			
		}
		return jsonobj;
	}
	
	@ResponseBody
	@RequestMapping("updateRealNameAuthenticationInfo")
	public Object updateRealNameAuthenticationInfo(HttpSession session, String regionType,
													String realName, String certNo,
													String businessPeriod, String conCertNo,
													String sex, String address,
													String certFrontPath, String certBackPath,
													String token) throws Exception {
		
		JSONObject jsonobj = new JSONObject();
		String userBaseId = SessionLocalManager.getSessionLocal().getUserBaseId();
		UserInfo userBaseInfo = userQueryService.queryByUserBaseId(userBaseId).getQueryUserInfo();
		// Map<String,Object> resultMap = new HashMap<String, Object>();
		String getToken = (String) session.getAttribute("token");
		if (userBaseInfo.getType() == UserTypeEnum.GR && token.equals(getToken)) {
			session.removeAttribute("token");
			
			if (regionType.equals("DL")) {
				
				// 个人的时候需要同步更新user_base_info的真实名称
				ApplyRealNameOrder order = new ApplyRealNameOrder();
				order.setUserBaseId(userBaseId);
				// 改变值
				order.setRealName(realName);
				order.setCertNo(certNo);
				
				order.setBusinessPeriod(businessPeriod);
				order.setCertFrontPath(certFrontPath);
				order.setCertBackPath(certBackPath);
				// 调用修改方法
				
				YrdBaseResult yrdBaseResult = realNameAuthenticationService.applyRealName(order);
				if (yrdBaseResult.isSuccess()) {
					jsonobj.put("code", 1);
					jsonobj.put("message", "实名认证发送成功");
				} else {
					jsonobj.put("code", 0);
					jsonobj.put("message", "实名认证发送失败");
				}
			} else {
				NonMainlandRealAuthenticateOrder order = new NonMainlandRealAuthenticateOrder();
				order.setUserBaseId(userBaseId);
				order.setRealName(realName);
				order.setSex(PeasonSexEnum.getByCode(sex));
				order.setCertTypeEnum(CertTypeEnum.Identity_Card);
				order.setAttribution(AttributionEnum.getByCode(regionType));
				order.setCertNo(certNo);
				order.setBusinessPeriod(businessPeriod);
				order.setCertFrontPath(certFrontPath);
				order.setCertBackPath(certBackPath);
				order.setAddress(address);
				YrdBaseResult yrdBaseResult = realNameAuthenticationService
					.applyNonMainlandRealName(order);
				
				if (yrdBaseResult.isSuccess()) {
					jsonobj.put("code", 1);
					jsonobj.put("message", "实名认证发送成功");
				} else {
					jsonobj.put("code", 0);
					jsonobj.put("message", "实名认证发送失败");
				}
			}
			
			// 更新SessionLocal
			SessionLocal sessionLocal = SessionLocalManager.getSessionLocal();
			sessionLocal.setRealName(realName);
			SessionLocalManager.setSessionLocal(sessionLocal);
			
		}
		return jsonobj;
	}
	
	@ResponseBody
	@RequestMapping("validationReferees")
	public Object validationReferees(String referees) throws Exception {
		YrdBaseResult returnEnum = new YrdBaseResult();
		JSONObject jsonobj = new JSONObject();
		returnEnum = userBaseInfoManager.validationReferees(referees);
		if (returnEnum.isSuccess()) {
			jsonobj.put("code", 1);
			jsonobj.put("message", "该推荐人可用");
		} else {
			jsonobj.put("code", 0);
			jsonobj.put("message", "该推荐人不可用");
		}
		return jsonobj;
	}
	
	@RequestMapping("shareWithFriends")
	public String shareWithFriends(HttpSession session, Model model) throws Exception {
		String host = AppConstantsUtil.getHostHttpUrl();
		String recomandUrl = host;
		if (SessionLocalManager.getSessionLocal() != null) {
			boolean isJJR = false;
			boolean isTZR = false;
			String memNo = null;
			long userId = SessionLocalManager.getSessionLocal().getUserId();
			Pagination<Role> rolesPage = authorityService.getRolesByUserId(userId, 0, 99);
			if (rolesPage.getResult() != null && rolesPage.getResult().size() > 0) {
				for (Role role : rolesPage.getResult()) {
					UserRelationInfo userRelationInfo = userRelationQueryService
						.findUserRelationByChildId(
							SessionLocalManager.getSessionLocal().getUserId())
						.getQueryUserRelationInfo();
					if (userRelationInfo != null) {
						memNo = userRelationInfo.getMemberNo();
					}
					if (SysUserRoleEnum.BROKER.getRoleCode().equals(role.getCode())) {
						isJJR = true;
					} else if (SysUserRoleEnum.INVESTOR.getRoleCode().equals(role.getCode())) {
						isTZR = true;
					}
				}
				if (isJJR) {
					recomandUrl += "/anon/brokerOpenInvestor?NO=" + memNo;
				} else if (isTZR) {
					recomandUrl += "/anon/brokerOpenInvestor?NO=" + memNo;
				} else {
					recomandUrl += "/anon/investorsOpen";
				}
			}
		} else {
			recomandUrl += "/anon/investorsOpen";
		}
		model.addAttribute("recomandUrl", recomandUrl);
		return USER_MANAGE_FRIEND_PATH + "shareWithFriends.vm";
	}
	
	@RequestMapping("countInvestAndLoan")
	public String countInvestAndLoan(HttpSession session, Model model) throws Exception {
		//TODO 投资统计//借款统计
		Map<String, Object> investCountMap = new HashMap<String, Object>();
		Map<String, Object> loanedCountMap = new HashMap<String, Object>();
		Map<String, Object> conditions = new HashMap<String, Object>();
		QueryLoanTradeOrder tradeOrder = new QueryLoanTradeOrder();
		if (SessionLocalManager.getSessionLocal().getUserId() == tradeBizProcessService
			.getYrdUserId()) {
			tradeOrder.setRoleId(SysUserRoleEnum.YRD.getValue());
		} else {
			tradeOrder.setRoleId(SysUserRoleEnum.INVESTOR.getValue());
		}
		
		tradeOrder.setUserId(SessionLocalManager.getSessionLocal().getUserId());
		TradeStatisticsResult<TradeAmountStatisticsInfo> statisticsResult = tradeBizQueryService
			.searchLoanTradeSumCommonQuery(tradeOrder);
		conditions.put("userId", SessionLocalManager.getSessionLocal().getUserId());
		conditions.put("roleId", SysUserRoleEnum.INVESTOR.getValue());
		//conditions.put("transferPhase", DivisionPhaseEnum.ORIGINAL_PHASE.code());
		
		TradeStatisticsUtil.countInvestedMoney(statisticsResult, investCountMap);
		
		QueryLoanTradeOrder tradeOrder1 = new QueryLoanTradeOrder();
		tradeOrder1.setRoleId(SysUserRoleEnum.LOANER.getValue());
		tradeOrder1.setUserId(SessionLocalManager.getSessionLocal().getUserId());
		
		TradeStatisticsResult<TradeAmountStatisticsInfo> statisticsResult2 = tradeBizQueryService
			.searchLoanTradeSumCommonQuery(tradeOrder1);
		
		TradeStatisticsUtil.countLoanedMoney(statisticsResult2, loanedCountMap);
		model.addAttribute("investCount", investCountMap);
		model.addAttribute("loanedCount", loanedCountMap);
		return USER_MANAGE_PATH + "countInvestAndLoan.vm";
	}
	
	@RequestMapping("refreeCenter")
	public String refreeCenter(HttpSession session, PageParam pageParam, Model model)
																						throws Exception {
		Map<String, Object> giftNewConditions = new HashMap<String, Object>();
		List<Integer> status = new ArrayList<Integer>();
		status.add(0);
		status.add(1);
		status.add(2);
		giftNewConditions.put("status", status);
		giftNewConditions.put("type", 1);
		giftNewConditions.put("relationId", SessionLocalManager.getSessionLocal().getUserId());
		Page<ActivityDetail> page = iActivityService.getActivityDetailPage(giftNewConditions,
			pageParam);
		model.addAttribute("page", page);
		return USER_MANAGE_FRIEND_PATH + "refreeCenter.vm";
	}
	
	private void setCommissionList(HttpSession session, Model model) {
		TradeDetailQueryOrder queryConditions = new TradeDetailQueryOrder();
		queryConditions.setUserId(SessionLocalManager.getSessionLocal().getUserId());
		if (queryConditions.getStatus() != null)
			queryConditions.getStatus().clear();
		queryConditions.setRoleId(SysUserRoleEnum.BROKER.getValue());
		queryConditions.setPageSize(5);
		queryConditions.setPageNumber(1);
		TradeDetailBatchResult<TradeDetailVOInfo> page = tradeBizQueryService
			.searchTradeDetailQuery(queryConditions);
		
		model.addAttribute("commissionList", PageUtil.getCovertPage(page));
		
	}
	
	/**
	 * 设置用户角色
	 * @param userId
	 * @param model
	 */
	private void setRole(long userId, Model model) {
		
		Pagination<Role> page = authorityService.getRolesByUserId(userId, 0, 99999);
		List<Role> roles = page.getResult();
		
		model.addAttribute("isBroker", false);
		model.addAttribute("isOperator", false);
		
		for (Role role : roles) {
			if ("broker".equals(role.getCode())) {
				model.addAttribute("isBroker", true);
			}
			
			if ("operator".equals(role.getCode())) {
				model.addAttribute("isOperator", true);
			}
			
			if (SysUserRoleEnum.LOANER.getRoleCode().equals(role.getCode())) {
				model.addAttribute("isLoaner", true);
			}
			
			if (SysUserRoleEnum.INVESTOR.getRoleCode().equals(role.getCode())) {
				model.addAttribute("isInvestor", true);
			}
		}
		
	}
	
}
