package com.yjf.yrd.front.controller.account;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.yjf.yrd.util.AppConstantsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yjf.common.lang.util.StringUtil;
import com.yjf.yrd.dal.dataobject.InstitutionsInfoDO;
import com.yjf.yrd.dal.dataobject.PersonalInfoDO;
import com.yjf.yrd.dal.dataobject.UserBaseInfoDO;
import com.yjf.yrd.front.controller.trade.base.LoanTradeDetailBaseController;
import com.yjf.yrd.page.PageParam;
import com.yjf.yrd.service.profit.ProfitManager;
import com.yjf.yrd.service.profit.order.BorkerProfitSettingOrder;
import com.yjf.yrd.session.SessionLocal;
import com.yjf.yrd.session.SessionLocalManager;
import com.yjf.yrd.upload.UploadPictures;
import com.yjf.yrd.user.info.InstitutionsInfo;
import com.yjf.yrd.user.info.InvestProfitAsignInfo;
import com.yjf.yrd.user.info.PersonalInfo;
import com.yjf.yrd.user.info.PersonalVOInfo;
import com.yjf.yrd.user.info.UserInfo;
import com.yjf.yrd.user.info.UserRelationInfo;
import com.yjf.yrd.user.order.BrokerOpenInvestorOrder;
import com.yjf.yrd.user.query.order.QueryUserChildrenOrder;
import com.yjf.yrd.user.valueobject.QueryConditions;
import com.yjf.yrd.util.CommonUtil;
import com.yjf.yrd.web.util.AttachmentModuleType;
import com.yjf.yrd.web.util.PageUtil;
import com.yjf.yrd.web.util.WebUtil;
import com.yjf.yrd.web.util.YrdEnumUtil;
import com.yjf.yrd.ws.base.info.BankBasicInfo;
import com.yjf.yrd.ws.enums.SysUserRoleEnum;
import com.yjf.yrd.ws.enums.UserTypeEnum;
import com.yjf.yrd.ws.info.CommonAttachmentInfo;
import com.yjf.yrd.ws.info.LoanDemandInfo;
import com.yjf.yrd.ws.info.TradeDetailVOInfo;
import com.yjf.yrd.ws.order.CommonAttachmentQueryOrder;
import com.yjf.yrd.ws.order.TradeDetailQueryOrder;
import com.yjf.yrd.ws.result.YrdBaseResult;
import com.yjf.yrd.ws.service.CommonAttachmentService;
import com.yjf.yrd.ws.service.query.order.QueryLoanTradeOrder;
import com.yjf.yrd.ws.service.query.result.QueryBaseBatchResult;
import com.yjf.yrd.ws.service.query.result.TradeDetailBatchResult;
import com.yjf.yrd.ws.statistics.TradeAmountStatisticsInfo;
import com.yjf.yrd.ws.statistics.result.TradeStatisticsResult;

/**
 * @Filename InvestorController.java
 * @Description
 * @Version 1.0
 * @Author zjl
 * @Email zjialin@yiji.com
 * @History <li>Author: zjl</li> <li>Date: 2013-7-11</li> <li>Version: 1.0</li>
 * <li>Content: create</li>
 */
@Controller
@RequestMapping("investorManager")
public class BrokerOpenInvestorController extends LoanTradeDetailBaseController {
	@Autowired
	CommonAttachmentService commonAttachmentService;
	
	/**
	 * 返回页面路径
	 */
	String USER_MANAGE_PATH = "/front/user/activation/";
	String BROCKER_MANAGE_PATH = "/front/user/broker/";
	private String jjrRoleId = "11";
	
	@Autowired
	ProfitManager profitManager;
	//-----------------------------------------------------投资者开户----------------------------------------------------------------------
	
	UploadPictures uploadPictures = new UploadPictures();
	
	public String getJjrRoleId() {
		return jjrRoleId;
	}
	
	public void setJjrRoleId(String jjrRoleId) {
		this.jjrRoleId = jjrRoleId;
	}
	
	@RequestMapping("uploadPictures")
	public String uploadPictures(HttpServletRequest request) throws Exception {
		return null;
	}
	
	@RequestMapping("investorOpenAccount")
	public String investorOpenAccount(HttpSession session, Model model,HttpServletRequest request) throws Exception {
		this.initAccountInfo(model);
		session.setAttribute("current", 3);
		model.addAttribute("uploadHost", "");
        String openType = StringUtil.defaultIfEmpty(request.getParameter("openType"),"investor");
        model.addAttribute("openType",openType);
		long brokerId = SessionLocalManager.getSessionLocal().getUserId();
		UserRelationInfo relationInfo = userRelationQueryService
			.findUserRelationByChildId(brokerId).getQueryUserRelationInfo();
		if (relationInfo != null) {
			logger.info("该经纪人可以给投资人开户");
			model.addAttribute("availabelBroker", true);

		} else {
			model.addAttribute("availabelBroker", false);
			logger.info("该经纪人不可以给投资人开户");
		}
		String token = UUID.randomUUID().toString();
		session.setAttribute("token", token);
		
		return USER_MANAGE_PATH + "investorOpenAccountYrd.vm";
	}
	
	@RequestMapping("realNameAuthentication")
	public String realNameAuthentication(HttpSession session, PersonalInfoDO personalInfo,
											UserBaseInfoDO userBaseInfo, Model model)
																						throws Exception {
		session.setAttribute("personalInfo", personalInfo);
		session.setAttribute("userBaseInfo", userBaseInfo);
		return USER_MANAGE_PATH + "realNameAuthentication.vm";
	}
	
	@RequestMapping("realNameAuthenticationSubmit")
	public String realNameAuthenticationSubmit(HttpSession session, PersonalInfoDO personalInfo,
												UserBaseInfoDO userBaseInfo, Model model)
																							throws Exception {
		model.addAttribute("newDate", new Date());
		return USER_MANAGE_PATH + "realNameAuthenticationOk.vm";
	}
	
	@RequestMapping("bindingBankCard")
	public String bindingBankCard(HttpSession session, PersonalInfoDO personalInfo,
									UserBaseInfoDO userBaseInfo, Model model) throws Exception {
		List<BankBasicInfo> bankBasicInfos = this.bankDataService.getBankBasicInfos();
		model.addAttribute("bankBasicInfos", bankBasicInfos);
		return USER_MANAGE_PATH + "bindingBankCard.vm";
	}
	
	@RequestMapping("investorOpenAccountSubmit")
	public String investorOpenAccountSubmit(HttpSession session, String token,
											InstitutionsInfoDO institutionInfo,
											PersonalInfoDO personalInfo,
											UserBaseInfoDO userBaseInfo, Model model,
											HttpServletRequest request) {
		this.initAccountInfo(model);
		String getToken = (String) session.getAttribute("token");
        String openType = StringUtil.defaultIfEmpty(request.getParameter("openType"),"investor");
		String return_vm_error = USER_MANAGE_PATH + "investorOpenAccountNO.vm";
		String return_vm_success = USER_MANAGE_PATH + "investorOpenAccountOK.vm";
		if (getToken != null) {
			if (getToken.equals(token)) {
				session.removeAttribute("token");
				BrokerOpenInvestorOrder investorOrder = new BrokerOpenInvestorOrder();
				WebUtil.setPoPropertyByRequest(investorOrder, request);
                if(!StringUtil.equalsIgnoreCase(openType,"investor")){
                    List<SysUserRoleEnum>  roles = new ArrayList<SysUserRoleEnum>();
                    roles.add(SysUserRoleEnum.BROKER);
                    roles.add(SysUserRoleEnum.PUBLIC);
                    investorOrder.setRole(roles);
                }
                //房融通，经纪人开户，所开用户和他同地区
    			UserInfo userInfo = userQueryService.queryByUserId(SessionLocalManager.getSessionLocal().getUserId()).getQueryUserInfo();
    			investorOrder.setUserCustomType(userInfo.getUserCustomType());
    			//end
				investorOrder.setBrokerUserId(SessionLocalManager.getSessionLocal().getUserId());
				YrdBaseResult baseResult = registerService.brokerOpenInvestor(investorOrder);
				if (baseResult.isSuccess()) {
					return return_vm_success;
				} else {
					model.addAttribute("reason", baseResult.getMessage());
					return return_vm_error;
				}
			} else {
				model.addAttribute("reason", "请无重复提交！");
				return return_vm_error;
			}
		} else {
			model.addAttribute("reason", "请无重复提交！");
			return return_vm_error;
		}
	}
	
	//-----------------------------------------------------投资者管理----------------------------------------------------------------------
	
	@RequestMapping("investorManage")
	public String investorManage(HttpSession session, QueryConditions queryConditions,
									PageParam pageParam, Model model, HttpServletRequest request)
																									throws Exception {
		this.initAccountInfo(model);
		String vm = "investorManage.vm";

        UserRelationInfo relationInfo = userRelationQueryService.findUserRelationByChildId(SessionLocalManager.getSessionLocal().getUserId()).getQueryUserRelationInfo();
        if(relationInfo != null && relationInfo.getLevel() <=1){
            model.addAttribute("isBroker","Y");
        }



        if(AppConstantsUtil.isTwoLevelBroker()){
            model.addAttribute("twoLevelBroker","Y");
        }

		QueryUserChildrenOrder commonQueryOrder = new QueryUserChildrenOrder();
		WebUtil.setPoPropertyByRequest(commonQueryOrder, request);
        String type = StringUtil.defaultIfEmpty(request.getParameter("type"),"investor");
        if(StringUtil.equalsIgnoreCase("investor",type)){
            commonQueryOrder.setRoleId(SysUserRoleEnum.INVESTOR.getValue());
        }else{
            commonQueryOrder.setRoleId(SysUserRoleEnum.BROKER.getValue());
            vm ="level2BrokerManage.vm";
        }

		commonQueryOrder.setUserId(SessionLocalManager.getSessionLocal().getUserId());
		commonQueryOrder.setPageNumber(pageParam.getPageNo());
		commonQueryOrder.setPageSize(pageParam.getPageSize());
		commonQueryOrder.setHasInvestorDistributionQuota(true);
		QueryBaseBatchResult<PersonalVOInfo> baseBatchResult = userQueryService
			.queryUserChildren(commonQueryOrder);
		model.addAttribute("page", PageUtil.getCovertPage(baseBatchResult));
		model.addAttribute("queryConditions", commonQueryOrder);
		return USER_MANAGE_PATH + vm;
	}
	
	//-----------------------------------------------------投资者详情----------------------------------------------------------------------
	
	@RequestMapping("investorInfo")
	public String investorInfo(String userBaseId, Model model) throws Exception {
		UserInfo userBase = userQueryService.queryByUserBaseId(userBaseId).getQueryUserInfo();
		this.initAccountInfo(model);
		if (userBase.getType() == UserTypeEnum.JG) {
			InstitutionsInfo institutionInfo = userQueryService.queryInstitutionsInfoByBaseId(
				userBaseId).getQueryInstitutionsInfo();
			UserRelationInfo userRelationInfo = userRelationQueryService.findUserRelationByChildId(
				userBase.getUserId()).getQueryUserRelationInfo();
			if (userRelationInfo != null) {
				model.addAttribute("userMemoNo", userRelationInfo.getMemberNo());
			} else {
				model.addAttribute("userMemoNo", "");
			}
			model.addAttribute("userType", "JG");
			model.addAttribute("info", institutionInfo);
		} else {
			UserInfo userInfo = userQueryService.queryByUserBaseId(userBaseId).getQueryUserInfo();
			PersonalInfo personalInfo = userQueryService.queryPersonalInfoByBaseId(userBaseId)
				.getQueryPersonalInfo();
			UserRelationInfo userRelationInfo = userRelationQueryService.findUserRelationByChildId(
				userBase.getUserId()).getQueryUserRelationInfo();
			if (userRelationInfo != null) {
				model.addAttribute("userMemoNo", userRelationInfo.getMemberNo());
			} else {
				model.addAttribute("userMemoNo", "");
			}
			model.addAttribute("certNo", personalInfo.getCertNo());
			model.addAttribute("info", userInfo);
		}
		return USER_MANAGE_PATH + "investorInfo.vm";
	}
	
	//-----------------------------------------------------营销统计----------------------------------------------------------------------
	
	@RequestMapping("salesCount")
	public String salesCount(HttpSession session, PageParam pageParam, Model model)
																					throws Exception {
		session.setAttribute("current", 6);
		QueryLoanTradeOrder queryConditions = new QueryLoanTradeOrder();
		queryConditions.setUserId(SessionLocalManager.getSessionLocal().getUserId());
		queryConditions.setRoleId(SysUserRoleEnum.BROKER.getValue());
		queryConditions.setPageSize(5000);
		queryConditions.setHasBenefitAmount("YES");
		TradeStatisticsResult<TradeAmountStatisticsInfo> batchResult = tradeBizQueryService
			.searchLoanTradeSumCommonQuery(queryConditions);
		model.addAttribute("statisticsInfo", batchResult.getStatisticsInfo());
		return BROCKER_MANAGE_PATH + "broker-salesCount.vm";
	}
	
	/**
	 * 营销详情
	 * 
	 * @param tradeId
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("salesDetails/{tradeId}/{detailId}")
	public String salesDetails(@PathVariable long tradeId, @PathVariable long detailId,
								long investDetailId, Model model, HttpSession session,
								HttpServletRequest request, PageParam pageParam) throws Exception {
		
		getTradeDetailPageView(0, tradeId, model, session, SysUserRoleEnum.BROKER, request,
			pageParam);
		//项目附件
		LoanDemandInfo loanDemand = null;
		if (tradeId > 0) {
			loanDemand = loanDemandQueryService.loadLoanDemandInfoByTradeId(tradeId);
		}
		CommonAttachmentQueryOrder attachmentQueryOrder = new CommonAttachmentQueryOrder();
		attachmentQueryOrder.setBizNo(loanDemand.getDemandId() + "");
		attachmentQueryOrder.setModuleTypeList(YrdEnumUtil.getAttachmentByIndustry());
		QueryBaseBatchResult<CommonAttachmentInfo> batchResult = commonAttachmentService
			.queryCommonAttachment(attachmentQueryOrder);
		List<AttachmentModuleType> attachmentModuleTypeList = new ArrayList<AttachmentModuleType>();
		List<List<CommonAttachmentInfo>> attachmentInfoList = new ArrayList<List<CommonAttachmentInfo>>();
		for (CommonAttachmentInfo attachmentInfo : batchResult.getPageList()) {
			boolean isExist = false;
			for (AttachmentModuleType attachmentModuleType : attachmentModuleTypeList) {
				if (attachmentInfo.getModuleType() == attachmentModuleType.getModuleType()) {
					attachmentModuleType.getAttachmentInfos().add(attachmentInfo);
					isExist = true;
					break;
				}
			}
			if (!isExist) {
				AttachmentModuleType attachmentModuleType = new AttachmentModuleType();
				attachmentModuleType.setModuleType(attachmentInfo.getModuleType());
				attachmentModuleType.getAttachmentInfos().add(attachmentInfo);
				attachmentModuleTypeList.add(attachmentModuleType);
			}
		}
		model.addAttribute("list", attachmentModuleTypeList);
		//end
		return BROCKER_MANAGE_PATH + "/broker-sales-detail.vm";
	}
	
	//-----------------------------------------------------营销交易列表----------------------------------------------------------------------
	
	@RequestMapping("salesList")
	public String salesList(TradeDetailQueryOrder queryConditions, HttpSession session,
							PageParam pageParam, Integer timelimits, Long starMoney, Long endMoney,
							Model model) throws Exception {
		
		SessionLocal sessionLocal = SessionLocalManager.getSessionLocal();
		if (sessionLocal != null && sessionLocal.getAccountId() != null) {
			this.initAccountInfo(model);
		}
		
		model.addAttribute("queryConditions", queryConditions);
		queryConditions.setUserId(SessionLocalManager.getSessionLocal().getUserId());
		if (queryConditions.getStatus() != null)
			queryConditions.getStatus().clear();
		queryConditions.setRoleId(SysUserRoleEnum.BROKER.getValue());
		queryConditions.setPageSize(pageParam.getPageSize());
		queryConditions.setPageNumber(pageParam.getPageNo());
		TradeDetailBatchResult<TradeDetailVOInfo> page = tradeBizQueryService
			.searchTradeDetailQuery(queryConditions);
		model.addAttribute("queryConditions", queryConditions);
		model.addAttribute("page", PageUtil.getCovertPage(page));
		model.addAttribute("allAmount", page.getTotalAmount());
		
		if (queryConditions.getStatus() != null && queryConditions.getStatus().size() > 0) {
			model.addAttribute("status", queryConditions.getStatus().get(0));
		}
		return BROCKER_MANAGE_PATH + "broker-salesList.vm";
	}
	
	@RequestMapping("profitAsignInfo")
	public String profitAsignInfo(String userBaseId, String pageNo, String userName,
									String realName, HttpSession session, Model model)
																						throws Exception {
		
		SessionLocal sessionLocal = SessionLocalManager.getSessionLocal();
		if (sessionLocal != null && sessionLocal.getAccountId() != null) {
			this.initAccountInfo(model);
		}
		
		model.addAttribute("customerId", userBaseId);
		UserInfo reciever = userQueryService.queryByUserBaseId(userBaseId).getQueryUserInfo();
		Long receiveId = reciever.getUserId();
		InvestProfitAsignInfo profitAsignInfo = profitManager.queryByReceiveId(receiveId);
		if (profitAsignInfo != null) {
			model.addAttribute("alreadySet", "yes");
			model.addAttribute("distributionQuota",
				CommonUtil.mul(profitAsignInfo.getDistributionQuota(), 100));
			model.addAttribute("customer", reciever.getRealName());
			model.addAttribute("tblBaseId", profitAsignInfo.getTblBaseId());
			model.addAttribute("note", profitAsignInfo.getNote());
		}
		model.addAttribute("pageNo", pageNo);
		model.addAttribute("userName", userName);
		model.addAttribute("realName", realName);
		
		String token = UUID.randomUUID().toString();
		session.setAttribute("token", token);
		return BROCKER_MANAGE_PATH + "broker-asign-profit.vm";
	}
	
	@ResponseBody
	@RequestMapping("setQuota")
	public Object setQuota(String customerId, String tblBaseId, double distributionQuota,
							String note, String token, HttpSession session) {
		logger.info("设置佣金额度，入参：[customerId={},tblBaseId={}],", customerId, tblBaseId);
		Map<String, Object> map = new HashMap<String, Object>();
		String getToken = (String) session.getAttribute("token");
		if (distributionQuota >= 0 && distributionQuota <= 100) {
			distributionQuota = CommonUtil.div(distributionQuota, 100);
			try {
				if (token.equals(getToken)) {
					session.removeAttribute("token");
					UserInfo reciever = userQueryService.queryByUserBaseId(customerId)
						.getQueryUserInfo();
					Long distributionId = SessionLocalManager.getSessionLocal().getUserId();
					Long receiveId = reciever.getUserId();
					if (StringUtil.isNotEmpty(tblBaseId)) {
						BorkerProfitSettingOrder profitSettingOrder = new BorkerProfitSettingOrder();
                        profitSettingOrder.setTblBaseId(tblBaseId);
						profitSettingOrder.setDistributionId(distributionId);
						profitSettingOrder.setReceiveId(receiveId);
						profitSettingOrder.setDistributionQuota(distributionQuota);
						profitSettingOrder.setNote(note);
						YrdBaseResult baseResult = profitManager
							.brokerAddProfitSetting(profitSettingOrder);
						
						if (baseResult.isSuccess()) {
							map.put("code", 1);
							map.put("message", "设置佣金成功");
						} else {
							map.put("code", 0);
							map.put("message", "设置佣金失败");
						}
					} else {
						
						String uuid = UUID.randomUUID().toString();
						BorkerProfitSettingOrder profitSettingOrder = new BorkerProfitSettingOrder();
						profitSettingOrder.setDistributionId(distributionId);
						profitSettingOrder.setReceiveId(receiveId);
						profitSettingOrder.setDistributionQuota(distributionQuota);
						profitSettingOrder.setNote(note);
						profitSettingOrder.setTblBaseId(uuid);
						
						YrdBaseResult baseResult = profitManager
							.brokerAddProfitSetting(profitSettingOrder);
						if (baseResult.isSuccess()) {
							map.put("code", 1);
							map.put("message", "设置佣金成功");
						} else {
							map.put("code", 0);
							map.put("message", "设置佣金失败");
						}
					}
				} else {
					map.put("code", 0);
					map.put("message", "请勿重复提交");
				}
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				map.put("code", 0);
				map.put("message", "设置佣金异常");
			}
		} else {
			map.put("code", 0);
			map.put("message", "额度介于0-100之间, 且必须大于0");
		}
		return map;
	}
	
	@ResponseBody
	@RequestMapping("distroyQuota")
	public Object distroyQuota(String tblBaseId, String token, HttpSession session) {
		logger.info("取消佣金额度，入参：[tblBaseId={}],", tblBaseId);
		Map<String, Object> map = new HashMap<String, Object>();
		String getToken = (String) session.getAttribute("token");
		try {
			if (token.equals(getToken)) {
				session.removeAttribute("token");
				if (StringUtil.isNotEmpty(tblBaseId)) {
					BorkerProfitSettingOrder profitSettingOrder = new BorkerProfitSettingOrder();
					profitSettingOrder.setTblBaseId(tblBaseId);
					profitSettingOrder.setDistributionId(SessionLocalManager.getSessionLocal()
						.getUserId());
					YrdBaseResult baseResult = profitManager
						.removeBrokerProfitSetting(profitSettingOrder);
					if (baseResult.isSuccess()) {
						map.put("code", 1);
						map.put("message", "取消佣金分配成功");
					} else {
						map.put("code", 0);
						map.put("message", "取消佣金分配失败");
					}
				}
			} else {
				map.put("code", 0);
				map.put("message", "请勿重复提交");
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			map.put("code", 0);
			map.put("message", "取消佣金分配异常");
		}
		return map;
	}
	
	@Override
	public String getNoTradeView() {
		return null;
	}
	
	@Override
	public String getTradeView() {
		return null;
	}
}
