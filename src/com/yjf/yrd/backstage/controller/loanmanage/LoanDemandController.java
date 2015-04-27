package com.yjf.yrd.backstage.controller.loanmanage;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yjf.common.lang.beans.cglib.BeanCopier;
import com.yjf.common.lang.util.ListUtil;
import com.yjf.common.lang.util.StringUtil;
import com.yjf.common.lang.util.money.Money;
import com.yjf.yrd.common.services.SysFunctionConfigService;
import com.yjf.yrd.dal.dataobject.UserBaseInfoDO;
import com.yjf.yrd.page.PageParam;
import com.yjf.yrd.service.certificate.CertificateService;
import com.yjf.yrd.service.exception.ExceptionFactory;
import com.yjf.yrd.service.query.trade.LoanDemandQueryService;
import com.yjf.yrd.service.query.trade.TradeBizQueryService;
import com.yjf.yrd.service.trade.download.ContractData;
import com.yjf.yrd.service.trade.download.PDFParse;
import com.yjf.yrd.session.SessionLocalManager;
import com.yjf.yrd.user.LoanerBaseInfoService;
import com.yjf.yrd.user.info.UserInfo;
import com.yjf.yrd.util.AppConstantsUtil;
import com.yjf.yrd.util.CommonUtil;
import com.yjf.yrd.util.DateUtil;
import com.yjf.yrd.util.NumberUtil;
import com.yjf.yrd.web.util.AttachmentModuleType;
import com.yjf.yrd.web.util.PageUtil;
import com.yjf.yrd.web.util.RateUtil;
import com.yjf.yrd.web.util.WebUtil;
import com.yjf.yrd.web.util.YrdEnumUtil;
import com.yjf.yrd.ws.enums.BooleanEnum;
import com.yjf.yrd.ws.enums.CommonAttachmentTypeEnum;
import com.yjf.yrd.ws.enums.DivisionPhaseEnum;
import com.yjf.yrd.ws.enums.DivisionTemplateStatusEnum;
import com.yjf.yrd.ws.enums.DivisionWayEnum;
import com.yjf.yrd.ws.enums.InsureWayEnum;
import com.yjf.yrd.ws.enums.InternetBankingBizTypeEnum;
import com.yjf.yrd.ws.enums.LoanBizTypeEnum;
import com.yjf.yrd.ws.enums.LoanDemandExpendEnum;
import com.yjf.yrd.ws.enums.LoanDemandStatusEnum;
import com.yjf.yrd.ws.enums.LoanPeriodUnitEnum;
import com.yjf.yrd.ws.enums.PDFTypeCodeEnum;
import com.yjf.yrd.ws.enums.SysUserRoleEnum;
import com.yjf.yrd.ws.enums.TradeFullConditionEnum;
import com.yjf.yrd.ws.enums.UserStateEnum;
import com.yjf.yrd.ws.enums.YrdAuthTypeEnum;
import com.yjf.yrd.ws.info.CommonAttachmentInfo;
import com.yjf.yrd.ws.info.DivisionRuleInfo;
import com.yjf.yrd.ws.info.DivisionTemplateInfo;
import com.yjf.yrd.ws.info.LoanDemandExtendInfo;
import com.yjf.yrd.ws.info.LoanDemandInfo;
import com.yjf.yrd.ws.info.LoanDemandItemDetailInfo;
import com.yjf.yrd.ws.info.LoanInfoItemInfo;
import com.yjf.yrd.ws.info.LoanerBaseInfo;
import com.yjf.yrd.ws.info.PdfTemplateInfo;
import com.yjf.yrd.ws.info.TradeInfo;
import com.yjf.yrd.ws.order.CalculateLoanCostOrder;
import com.yjf.yrd.ws.order.CommonAttachmentOrder;
import com.yjf.yrd.ws.order.CommonAttachmentQueryOrder;
import com.yjf.yrd.ws.order.CreateLoanDemandExtendOrder;
import com.yjf.yrd.ws.order.CreateLoanDemandOrder;
import com.yjf.yrd.ws.order.LoanDemandBizTypeOrder;
import com.yjf.yrd.ws.order.LoanDemandCheckPassOrder;
import com.yjf.yrd.ws.order.LoanDemandGuaranteeOrder;
import com.yjf.yrd.ws.order.LoanDemandItemDetailOrder;
import com.yjf.yrd.ws.order.LoanDemandLoanNoteOrder;
import com.yjf.yrd.ws.order.LoanDemandUploadContractOrder;
import com.yjf.yrd.ws.order.LoanInfoItemOrder;
import com.yjf.yrd.ws.order.LoanerBaseInfoOrder;
import com.yjf.yrd.ws.order.TradeProcessOrder;
import com.yjf.yrd.ws.order.UpdateTradeExpireDateOrder;
import com.yjf.yrd.ws.result.AddLoanDemandResult;
import com.yjf.yrd.ws.result.CalculateLoanCostResult;
import com.yjf.yrd.ws.result.CommonAttachmentResult;
import com.yjf.yrd.ws.result.YrdBaseResult;
import com.yjf.yrd.ws.service.CommonAttachmentService;
import com.yjf.yrd.ws.service.DivisionTemplateManager;
import com.yjf.yrd.ws.service.LoanAuthRecordManager;
import com.yjf.yrd.ws.service.LoanDemandExtendService;
import com.yjf.yrd.ws.service.YrdResultEnum;
import com.yjf.yrd.ws.service.query.order.LoanAuthRecordQueryOrder;
import com.yjf.yrd.ws.service.query.order.LoanDemandQueryOrder;
import com.yjf.yrd.ws.service.query.result.QueryBaseBatchResult;

/**
 * 
 * 
 * @Filename LoanDemandController.java
 * 
 * @Description
 * 
 * @Version 1.0
 * 
 * @Author zjl
 * 
 * @Email zjialin@yiji.com
 * 
 * @History <li>Author: zjil</li> <li>Date: 2013-6-14</li> <li>Version: 1.0</li>
 * <li>Content: create</li>
 * 
 */

@Controller
@RequestMapping("backstage")
public class LoanDemandController extends LoanBaseController {
	/** 页面所在路径 */
	private final String BORROWING_MANAGE__PATH = "/backstage/borrowingManage/";
	
	/** LOAN_DEMAND_INFO_ITEM_4 : 抵押物信息 */
	private final String GUARANTY_PROPERTY_KEY = LoanDemandExpendEnum.LOAN_DEMAND_INFO_ITEM.code() + 4;
	
	/** LOAN_DEMAND_INFO_ITEM_5 : 保障信息 */
	private final String GUARANTEEINFO_PROPERTY_KEY = LoanDemandExpendEnum.LOAN_DEMAND_INFO_ITEM
		.code() + 5;
	
	@Autowired
	LoanDemandQueryService loanDemandQueryService;
	@Autowired
	TradeBizQueryService tradeBizQueryService;
	
	@Autowired
	DivisionTemplateManager divisionTemplateManager;
	@Autowired
	LoanAuthRecordManager loanAuthRecordManager;
	@Autowired
	CommonAttachmentService commonAttachmentService;
	@Autowired
	SysFunctionConfigService sysFunctionConfigService;
	@Autowired
	LoanerBaseInfoService loanerBaseInfoService;
	
	@Autowired
	LoanDemandExtendService loanDemandExtendService;
	@Autowired
	CertificateService certificateService;
	
	/** 封装回执页面 */
	private String return_vm(String module) {
		String return_vm = null;
		if ("WITE".equals(module)) {
			return_vm = BORROWING_MANAGE__PATH + "pageQueryLoanDemandWiteInfo.vm";
		}
		if ("PASSADNDISMISS".equals(module)) {
			return_vm = BORROWING_MANAGE__PATH + "pageQueryLoanDemandPassAanDismissInfo.vm";
		}
		if ("DRAFT".equals(module)) {
			return_vm = BORROWING_MANAGE__PATH + "pageQueryLoanDemandDraftInfo.vm";
		}
		if ("ALL".equals(module)) {
			return_vm = BORROWING_MANAGE__PATH + "pageQueryLoanDemandAllInfo.vm";
		}
		if ("OVER".equals(module)) {
			return_vm = BORROWING_MANAGE__PATH + "pageQueryLoanDemandAllInfo2.vm";
		}
		if ("OVERALL".equals(module)) {
			return_vm = BORROWING_MANAGE__PATH + "pageQueryLoanDemandAllInfo3.vm";
		}
		return return_vm;
	}
	
	/** 封装分页查询参数 */
	private LoanDemandQueryOrder getQueryConditions(LoanDemandQueryOrder queryConditions,
													LoanDemandStatusEnum statusEnum, String module) {
		List<LoanDemandStatusEnum> statuList = new ArrayList<LoanDemandStatusEnum>();
		if ("WITE".equals(module)) {
			statuList.add(LoanDemandStatusEnum.WITE);
		}
		if ("PASSADNDISMISS".equals(module)) {
			if (statusEnum == null) {
				statuList.add(LoanDemandStatusEnum.PASS);
				statuList.add(LoanDemandStatusEnum.DISMISS);
			} else {
				statuList.add(statusEnum);
			}
		}
		if ("DRAFT".equals(module)) {
			statuList.add(LoanDemandStatusEnum.DRAFT);
			statuList.add(LoanDemandStatusEnum.WITE);
		}
		if ("ALL".equals(module)) {
			statuList.add(LoanDemandStatusEnum.WITE);
			statuList.add(LoanDemandStatusEnum.PASS);
			statuList.add(LoanDemandStatusEnum.DISMISS);
			statuList.add(LoanDemandStatusEnum.DRAFT);
		}
		if ("OVER".equals(module)) {
			statuList.add(LoanDemandStatusEnum.PASS);
			statuList.add(LoanDemandStatusEnum.DISMISS);
		}
		if ("OVERALL".equals(module)) {
			
			statuList.add(LoanDemandStatusEnum.PASS);
			statuList.add(LoanDemandStatusEnum.WITE);
			statuList.add(LoanDemandStatusEnum.DISMISS);
			statuList.add(LoanDemandStatusEnum.DRAFT);
			//statuList.add(LoanDemandStatusEnum.DISMISS);
		}
		queryConditions.setStatusList(statuList);
		return queryConditions;
	}
	
	/** 分页查询借款管理 */
	@RequestMapping(value = "pageQueryLoanDemand")
	public String pageQueryLoanDemand(HttpServletRequest request, String module,
										PageParam pageParam, Model model) {
		try {
			LoanDemandQueryOrder queryConditions = new LoanDemandQueryOrder();
			WebUtil.setPoPropertyByRequest(queryConditions, request);
			queryConditions.setPageNumber(pageParam.getPageNo());
			queryConditions.setPageSize(pageParam.getPageSize());
			model.addAttribute("guarantee", getInfos(8));
			model.addAttribute("sponsor", getInfos(9));
			
			queryConditions = this.getQueryConditions(queryConditions, null, module);
			// queryConditions.setMaxLoanAmount(String.valueOf(Long.parseLong(queryConditions.getMaxLoanAmount())*100));
			// queryConditions.setminLoanAmount(String.valueOf(Long.parseLong(queryConditions.getminLoanAmount())*100));
			QueryBaseBatchResult<LoanDemandInfo> page = loanDemandQueryService
				.queryLoandDemand(queryConditions);
			model.addAttribute("queryConditions", queryConditions);
			model.addAttribute("page", PageUtil.getCovertPage(page));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return this.return_vm(module);
	}
	
	/** 查询下线需求 */
	@RequestMapping(value = "pageQueryOfflineLoanDemand")
	public String pageQueryOfflineLoanDemand(LoanDemandQueryOrder queryConditions,
												PageParam pageParam, Model model) {
		List<LoanDemandStatusEnum> statuList = new ArrayList<LoanDemandStatusEnum>();
		statuList.add(LoanDemandStatusEnum.OFFLINE);
		queryConditions.setStatusList(statuList);
		queryConditions.setPageNumber(pageParam.getPageNo());
		queryConditions.setPageSize(pageParam.getPageSize());
		try {
			QueryBaseBatchResult<LoanDemandInfo> result = loanDemandQueryService
				.queryLoandDemand(queryConditions);
			model.addAttribute("page", PageUtil.getCovertPage(result));
			model.addAttribute("queryConditions", queryConditions);
			model.addAttribute("guarantee", getInfos(8));
			model.addAttribute("sponsor", getInfos(9));
		} catch (Exception e) {
			
			logger.error("查询失败", e);
		}
		return BORROWING_MANAGE__PATH + "pageQueryLoanDeamndOfflineInfo.vm";
	}
	@ResponseBody
	@RequestMapping("pageQueryOfflineLoanDemand/updateBizType")
	public Object updateState(Long demandId, String bizType) throws Exception {
		JSONObject jsonobj = new JSONObject();
		LoanDemandBizTypeOrder updatrOrder = new LoanDemandBizTypeOrder();
		if("public".equals(bizType)){
			updatrOrder.setBizType(LoanBizTypeEnum.TO_HOME_PAGE);
		}else if ("thp".equals(bizType)){
			updatrOrder.setBizType(LoanBizTypeEnum.PUBLIC);
		}	
		updatrOrder.setBizNo(demandId);

		YrdBaseResult baseResult = loanDemandService.updateBizType(updatrOrder);
		if (baseResult.isSuccess()) {
			jsonobj.put("code", 1);
			jsonobj.put("message", "更改成功");
		}
		if (!baseResult.isSuccess()) {
			jsonobj.put("code", 0);
			jsonobj.put("message", "更改失败");
		}
		return jsonobj;
	}
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping("guaranteeAuthCheck")
	public Object guaranteeAuthCheck(long demandId) {
		LoanDemandInfo loanDemandInfo = null;
		JSONObject jsonobj = new JSONObject();
		try {
			loanDemandInfo = loanDemandQueryService.findById(demandId);
			if (loanDemandInfo != null) {
				jsonobj.put("code", 1);
				jsonobj.put("message", "可以发布");
			} else {
				jsonobj.put("code", 0);
				jsonobj.put("message", "处理失败");
			}
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			jsonobj.put("code", 0);
			jsonobj.put("message", "处理失败");
		}
		return jsonobj;
	}
	
	@RequestMapping(value = "addLoanDemand")
	public String addLoanDemand(String bizType, String amount, Model model) throws Exception {
		model.addAttribute("guarantee", getInfos(8));
		model.addAttribute("sponsor", getInfos(9));
		if (StringUtil.isNotEmpty(bizType)) {
			model.addAttribute("bizType", bizType);
		} else {
			model.addAttribute("bizType", LoanBizTypeEnum.PUBLIC.code());
		}
		
		if (StringUtil.isNotEmpty(amount)) {
			model.addAttribute("amount", Long.parseLong(amount) / 100);
		}
		List<CommonAttachmentTypeEnum> list = YrdEnumUtil.getAttachmentByIndustry();
		model.addAttribute("enumlist", list);
		model.addAttribute("invest", divisionTemplateManager.getDivisionTemplatesByPhase(
			DivisionPhaseEnum.INVESET_PHASE, DivisionTemplateStatusEnum.NORMAL));
		model.addAttribute("repay", divisionTemplateManager.getDivisionTemplatesByPhase(
			DivisionPhaseEnum.REPAY_PHASE, DivisionTemplateStatusEnum.NORMAL));
		model.addAttribute("bankingBizType", InternetBankingBizTypeEnum.getAllEnum());
		model.addAttribute("uploadHost", "");
		model.addAttribute("insureWayList", InsureWayEnum.getAllEnum());
		model.addAttribute("bizTypeList", LoanBizTypeEnum.getAllEnum());
		
		//设置各种合同选项
		List<PdfTemplateInfo> contractTmpList = pdfTemplateService
			.getOnlineListByTypeCode(PDFTypeCodeEnum.CONTRACT.code());
		List<PdfTemplateInfo> receiptTmpList = pdfTemplateService
			.getOnlineListByTypeCode(PDFTypeCodeEnum.RECEIPT.code());
		List<PdfTemplateInfo> letterTmpList = pdfTemplateService
			.getOnlineListByTypeCode(PDFTypeCodeEnum.LETTER.code());
		model.addAttribute("contractTmpList", contractTmpList);
		model.addAttribute("receiptTmpList", receiptTmpList);
		model.addAttribute("letterTmpList", letterTmpList);
		long contractTemplateId = pdfTemplateService
			.getDefaulPdfTemplateIdByCode(PDFTypeCodeEnum.CONTRACT.code());
		long receiptTemplateId = pdfTemplateService
			.getDefaulPdfTemplateIdByCode(PDFTypeCodeEnum.RECEIPT.code());
		model.addAttribute("contractTemplateId", contractTemplateId);
		model.addAttribute("receiptTemplateId", receiptTemplateId);
		
		if (AppConstantsUtil.getLoanInfoItem()) {
			//获取启用的信息模块
			LoanInfoItemOrder loanInfoItemInfo = new LoanInfoItemOrder();
			loanInfoItemInfo.setStatus("0");
			List<LoanInfoItemInfo> itemInfoList = loanInfoItemService.queryList(loanInfoItemInfo);
			for (LoanInfoItemInfo itemInfo : itemInfoList) {
				itemInfo.setSel(true);
			}
			model.addAttribute("itemInfoList", itemInfoList);
			
			List<CommonAttachmentTypeEnum> loanerAttaclist = YrdEnumUtil
				.getAttachmentByLoanerInfo();
			model.addAttribute("loanerAttaclist", loanerAttaclist);
			
			List<CommonAttachmentTypeEnum> loanAttaclist = YrdEnumUtil.getAttachmentByLoanNote();
			model.addAttribute("loanAttaclist", loanAttaclist);
			
			List<CommonAttachmentTypeEnum> guarantyAttaclist = YrdEnumUtil
				.getAttachmentByGuarantyInfo();
			model.addAttribute("guarantyAttaclist", guarantyAttaclist);
			
			model.addAttribute("doType", "add");
			return BORROWING_MANAGE__PATH + "addLoanDemand_new.vm";
		}
		
		return BORROWING_MANAGE__PATH + "addLoanDemand.vm";
	}
	
	/** 添加正式担保函 */
	@ResponseBody
	@RequestMapping("addLoanDemand/updateImg")
	public Object updateImgUrl(Long param_id, String param_name, String guaranteeLicenseUrl1,
								HttpServletResponse response, Model model) throws Exception {
		LoanDemandUploadContractOrder add = new LoanDemandUploadContractOrder();
		add.setBizNo(param_id);
		add.setUploadContractUrl(guaranteeLicenseUrl1);
		add.setProcessorId(SessionLocalManager.getSessionLocal().getUserId());
		add.setProcessName(SessionLocalManager.getSessionLocal().getUserName());
		JSONObject json = new JSONObject();
		logger.info("上传正式担保函，入参{}", add);
		YrdBaseResult result = loanDemandService.uploadContractPdfUrl(add);
		if (result.isSuccess()) {
			json.put("message", "上传正式担保函成功");
		} else {
			json.put("message", "上传正式担保函失败");
		}
		return json;
	}
	
	/** 修改附件 */
	@ResponseBody
	@RequestMapping("updataImg/add/updateImg2")
	public Object updateImgUrl2(Long param_id, String param_name, String guaranteeLicenseUrl1,
								HttpServletResponse response, HttpServletRequest request,
								Model model) throws Exception {
		
		YrdBaseResult baseResult = addAttachfile(param_id.toString(), request);
		
		JSONObject json = new JSONObject();
		logger.info("修改附件，入参{}", baseResult);
		
		if (baseResult.isSuccess()) {
			json.put("code", 1);
			json.put("message", "修改成功");
		} else {
			json.put("code", 0);
			json.put("message", "修改失败");
		}
		return json;
		
	}
	
	private YrdBaseResult addAttachfile(String param_id, HttpServletRequest request) {
		List<CommonAttachmentOrder> orders = new ArrayList<CommonAttachmentOrder>();
		List<CommonAttachmentTypeEnum> list = YrdEnumUtil.getAttachmentByIndustry();
		for (int i = 0; i < list.size(); i++) {
			CommonAttachmentTypeEnum itemEnum = list.get(i);
			String pathValues = request.getParameter("pathName_" + itemEnum.code());
			if (StringUtil.isNotBlank(pathValues)) {
				String[] attachPaths = pathValues.split(";");
				int j = 1;
				for (String path : attachPaths) {
					String[] paths = path.split(",");
					if (paths.length > 1) {
						CommonAttachmentOrder commonAttachmentOrder = new CommonAttachmentOrder();
						commonAttachmentOrder.setBizNo(param_id);
						commonAttachmentOrder.setModuleType(itemEnum);
						commonAttachmentOrder.setIsort(j++);
						commonAttachmentOrder.setFilePhysicalPath(paths[1]);
						commonAttachmentOrder.setRequestPath(paths[0]);
						orders.add(commonAttachmentOrder);
					}
				}
			}
			
		}
		
		YrdBaseResult baseResult = commonAttachmentService.insertAll(orders);
		return baseResult;
	}
	
	private YrdBaseResult addAttachfileByType(String attachType, String param_id,
												HttpServletRequest request) {
		YrdBaseResult baseResult = new YrdBaseResult();
		List<CommonAttachmentOrder> orders = new ArrayList<CommonAttachmentOrder>();
		List<CommonAttachmentTypeEnum> list = null;
		if (CommonAttachmentTypeEnum.LOANER_INFO.getIndustryType().equals(attachType)) {
			list = YrdEnumUtil.getAttachmentByLoanerInfo();
		} else if (CommonAttachmentTypeEnum.LOAN_NOTE.getIndustryType().equals(attachType)) {
			list = YrdEnumUtil.getAttachmentByLoanNote();
		} else if (CommonAttachmentTypeEnum.GUARANTY_INFO.getIndustryType().equals(attachType)) {
			list = YrdEnumUtil.getAttachmentByGuarantyInfo();
		}
		
		if (list != null) {
			for (int i = 0; i < list.size(); i++) {
				CommonAttachmentTypeEnum itemEnum = list.get(i);
				String pathValues = request.getParameter("pathName_" + itemEnum.code());
				if (StringUtil.isNotBlank(pathValues)) {
					String[] attachPaths = pathValues.split(";");
					int j = 1;
					for (String path : attachPaths) {
						String[] paths = path.split(",");
						if (paths.length > 1) {
							CommonAttachmentOrder commonAttachmentOrder = new CommonAttachmentOrder();
							commonAttachmentOrder.setBizNo(param_id);
							commonAttachmentOrder.setModuleType(itemEnum);
							commonAttachmentOrder.setIsort(j++);
							commonAttachmentOrder.setFilePhysicalPath(paths[1]);
							commonAttachmentOrder.setRequestPath(paths[0]);
							orders.add(commonAttachmentOrder);
						}
					}
				}
				
			}
			
			baseResult = commonAttachmentService.insertAll(orders);
		}
		
		return baseResult;
	}
	
	/**
	 * 验证输入内容
	 */
	private JSONObject checkContent(HttpServletRequest request, HttpServletResponse response,
									Model model, JSONObject jsonobj,
									CreateLoanDemandOrder createLoanDemandOrder,
									long... templateIds) {
		if (StringUtil.isEmpty(request.getParameter("timeLimit"))
			|| " ".equals(request.getParameter("timeLimit"))) {
			jsonobj.put("code", 0);
			jsonobj.put("message", "请选择融资期限");
			return jsonobj;
		}
		logger.info("发布借款需求，入参{}", createLoanDemandOrder);
		if (createLoanDemandOrder.getInvestAvlDate().before(new Date())) {
			jsonobj.put("code", 0);
			jsonobj.put("message", "可投资时间应该大于当前日期");
			return jsonobj;
		}
		// 检查融资金额必须在融资规模内
		String dimensions = createLoanDemandOrder.getDimensions();
		if (StringUtil.isNotBlank(dimensions)) {
			String[] dimension = dimensions.replaceAll(",", "").split(" ~ ");
			if (dimension != null && dimension.length == 2) {
				if (createLoanDemandOrder.getLoanAmount().getCent() < Long.parseLong(dimension[0]) * 100
					|| createLoanDemandOrder.getLoanAmount().getCent() > Long
						.parseLong(dimension[1]) * 100) {
					jsonobj.put("code", 0);
					jsonobj.put("message", "融资金额必须在融资规模内");
					return jsonobj;
				}
				
			}
		}
		
		if (createLoanDemandOrder.getLoanAmount().getCent() % 1000 != 0) {
			jsonobj.put("code", 0);
			jsonobj.put("message", "融资金额必须为1000整数倍");
			return jsonobj;
		}
		
		if (StringUtil.isEmpty(createLoanDemandOrder.getSaturationCondition())) {
			jsonobj.put("code", 0);
			jsonobj.put("message", "请输入满标条件");
			return jsonobj;
		}
		
		String isDirectional = request.getParameter("isDirectional");
		if (StringUtil.equals(isDirectional, "Y")) {
			if (StringUtil.isEmpty(createLoanDemandOrder.getLoanPassword())) {
				jsonobj.put("code", 0);
				jsonobj.put("message", "请设置密码");
				return jsonobj;
			}
			if (createLoanDemandOrder.getLoanPassword().length() > 8
				&& createLoanDemandOrder.getLoanPassword().length() < 6) {
				jsonobj.put("code", 0);
				jsonobj.put("message", "定向融资密码长度应在6到8之间");
				return jsonobj;
			}
		} else {
			createLoanDemandOrder.setLoanPassword(null);
		}
		DivisionTemplateInfo divisonTemplateInfo = divisionTemplateManager
			.getByTemplateId(templateIds[1]);
		if ((divisonTemplateInfo.getDivisionWay() == DivisionWayEnum.MONTH_WAY || divisonTemplateInfo.getDivisionWay() == DivisionWayEnum.MONTH_PRINCIPAL_INTEREST)
                && createLoanDemandOrder.getTimeLimitUnit() == LoanPeriodUnitEnum.LOAN_BY_DAY) {
			jsonobj.put("code", 0);
			jsonobj.put("message", "分润模板与融资期限不匹配");
			return jsonobj;
		}
		
		if (createLoanDemandOrder.getSaturationConditionMethod() == TradeFullConditionEnum.AMOUNT) {
			if (Long.parseLong(createLoanDemandOrder.getSaturationCondition()) % 1000 != 0) {
				jsonobj.put("code", 0);
				jsonobj.put("message", "固定金额必须为1000整数倍");
				return jsonobj;
			}
			
			if (StringUtil.isNotBlank(dimensions)) {
				String[] dimension = dimensions.replaceAll(",", "").split(" ~ ");
				if (dimension != null && dimension.length == 2) {
					if (Long.parseLong(createLoanDemandOrder.getSaturationCondition()) < Long
						.parseLong(dimension[0])
						|| Long.parseLong(createLoanDemandOrder.getSaturationCondition()) > Long
							.parseLong(dimension[1])) {
						jsonobj.put("code", 0);
						jsonobj.put("message", "固定金额必须在融资规模内");
						return jsonobj;
					}
					
				}
			}
		}
		if (createLoanDemandOrder.getGuaranteeId() <= 0) {
			jsonobj.put("code", 0);
			jsonobj.put("message", "未选择承诺机构");
			return jsonobj;
		}
		return jsonobj;
	}
	
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping(value = "addLoanDemandSubmit")
	public Object addLoanDemandSubmit(String guaranteeLicenseUrl2, HttpServletRequest request,
										HttpServletResponse response, Model model,
										LoanerBaseInfoOrder loanerBaseInfoOrder,
										long... templateIds) throws Exception {
		JSONObject jsonobj = new JSONObject();
		CreateLoanDemandOrder createLoanDemandOrder = new CreateLoanDemandOrder();
		model.addAllAttributes(WebUtil.getRequestMap(request));
		WebUtil.setPoPropertyByRequest(createLoanDemandOrder, request);
		if (request.getParameter("loanPassword") != null) {
			createLoanDemandOrder.setBizType(LoanBizTypeEnum.PRIVATE);
		} else {
			createLoanDemandOrder.setBizType(LoanBizTypeEnum.getByCode(request
				.getParameter("bizType")));
			
		}
		
		createLoanDemandOrder.setInvestGiftMoneyRule(request
			.getParameterValues("investGiftMoneyRule"));
		createLoanDemandOrder.setUseGiftMoneyRule(request.getParameterValues("useGiftMoneyRule"));
		
		createLoanDemandOrder.setTimeLimitUnit(LoanPeriodUnitEnum.getByCode(request
			.getParameter("timeLimitUnit")));
		createLoanDemandOrder.setSaturationConditionMethod(TradeFullConditionEnum.getByCode(request
			.getParameter("saturationConditionMethod")));
		createLoanDemandOrder.setStatus(LoanDemandStatusEnum.getByCode(request
			.getParameter("status")));
		createLoanDemandOrder.setInsureWay(InsureWayEnum.getByCode(request
			.getParameter("insureWay")));
		createLoanDemandOrder.setPublishUserId(SessionLocalManager.getSessionLocal().getUserId());
		createLoanDemandOrder.setPublishUserName(SessionLocalManager.getSessionLocal()
			.getUserName());
		createLoanDemandOrder.setInvestTemplateId(templateIds[0]);
		createLoanDemandOrder.setRepayTemplateId(templateIds[1]);
		String bankingBizType = request.getParameter("bankingBizType");
		if(com.yjf.yrd.util.StringUtil.isNotEmpty(bankingBizType)){
			if (InternetBankingBizTypeEnum.getByCode(bankingBizType) != null) {
				createLoanDemandOrder.setBankingBizTypeEnum(InternetBankingBizTypeEnum
					.getByCode(bankingBizType));
			}
		}
		jsonobj = this.checkContent(request, response, model, jsonobj, createLoanDemandOrder,
			templateIds);
		if (jsonobj.size() > 0) {
			return jsonobj;
		}
		AddLoanDemandResult result = loanDemandService.addLoanDemand(createLoanDemandOrder);
		
		logger.info("发布借款需求结束：{}", result);
		if (result.isSuccess()) {
			String bizNo = String.valueOf(result.getDemandId());
			//附件
			YrdBaseResult baseResult = addAttachfile(bizNo, request);
			// 项目图片
			if (StringUtil.isNotBlank(guaranteeLicenseUrl2)) {
				CommonAttachmentOrder order1 = new CommonAttachmentOrder();
				order1.setBizNo(bizNo);
				order1.setRequestPath(guaranteeLicenseUrl2);
				order1.setModuleType(null);
				order1.setIsort(10);
				YrdBaseResult baseResult2 = commonAttachmentService.insert(order1);
			}
			/** 存入借款人的信息 ***************************************************/
			if ("Y".equals(AppConstantsUtil.getAddLoanerBaseInfo())) {
				//获取借款人信息
				long userId = userQueryService.queryByUserName(createLoanDemandOrder.getUserName())
					.getQueryUserInfo().getUserId();
				
				LoanerBaseInfo loanerBaseInfo = loanerBaseInfoService.findByUserId(userId);
				//PersonalInfo personalInfo = null;
				if (loanerBaseInfo != null) {//不为空，修改
					loanerBaseInfoOrder.setCreateTime(new Date());
					loanerBaseInfoOrder.setUserId(userId);
					YrdBaseResult addInfoReslut = loanerBaseInfoService
						.updateById(loanerBaseInfoOrder);
				} else {
					loanerBaseInfoOrder.setUserId(userId);
					loanerBaseInfoOrder.setCreateTime(new Date());
					YrdBaseResult addInfoReslut = loanerBaseInfoService.insert(loanerBaseInfoOrder);
				}
				
			}
			if (LoanDemandStatusEnum.DRAFT == createLoanDemandOrder.getStatus()) {
				jsonobj.put("code", 1);
				jsonobj.put("message", "存入草稿成功");
			} else {
				jsonobj.put("code", 1);
				jsonobj.put("message", "发布借款需求成功");
			}
			
		} else {
			if (LoanDemandStatusEnum.DRAFT == createLoanDemandOrder.getStatus()) {
				jsonobj.put("code", 0);
				jsonobj.put("message", "存入草稿失败[" + result.getMessage() + "]");
			} else {
				jsonobj.put("code", 0);
				jsonobj.put("message", "发布借款需求失败[" + result.getMessage() + "]");
			}
		}
		return jsonobj;
	}
	
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping(value = "loanBaseInfoSubmit")
	public Object loanBaseInfoSubmit(String guaranteeLicenseUrl2, HttpServletRequest request,
										HttpServletResponse response, Model model,
										long... templateIds) throws Exception {
		
		JSONObject jsonobj = new JSONObject();
		CreateLoanDemandOrder createLoanDemandOrder = new CreateLoanDemandOrder();
		model.addAllAttributes(WebUtil.getRequestMap(request));
		WebUtil.setPoPropertyByRequest(createLoanDemandOrder, request);
		createLoanDemandOrder
			.setBizType(LoanBizTypeEnum.getByCode(request.getParameter("bizType")));
		
		createLoanDemandOrder.setTimeLimitUnit(LoanPeriodUnitEnum.getByCode(request
			.getParameter("timeLimitUnit")));
		createLoanDemandOrder.setSaturationConditionMethod(TradeFullConditionEnum.getByCode(request
			.getParameter("saturationConditionMethod")));
		createLoanDemandOrder.setStatus(LoanDemandStatusEnum.getByCode(request
			.getParameter("status")));
		createLoanDemandOrder.setInsureWay(InsureWayEnum.getByCode(request
			.getParameter("insureWay")));
		
		createLoanDemandOrder.setInvestTemplateId(templateIds[0]);
		createLoanDemandOrder.setRepayTemplateId(templateIds[1]);
		
		String isDirectional = request.getParameter("isDirectional");
		if (StringUtil.equals(isDirectional, "Y")) {
			if (StringUtil.isEmpty(createLoanDemandOrder.getLoanPassword())) {
				jsonobj.put("code", 0);
				jsonobj.put("message", "请设置密码");
				return jsonobj;
			}
			if (createLoanDemandOrder.getLoanPassword().length() > 8
				&& createLoanDemandOrder.getLoanPassword().length() < 6) {
				jsonobj.put("code", 0);
				jsonobj.put("message", "定向融资密码长度应在6到8之间");
				return jsonobj;
			}
		} else {
			createLoanDemandOrder.setLoanPassword(null);
		}
		
		//设置默认的保障函编号，保障函名称
		if (createLoanDemandOrder.getGuaranteeLicenceNo() == null
			|| "".equals(createLoanDemandOrder.getGuaranteeLicenceNo().trim())) {
			SimpleDateFormat dateformat = new SimpleDateFormat("yyyyMMddHHmmss");
			createLoanDemandOrder.setGuaranteeLicenceNo(dateformat.format(new Date()));
			createLoanDemandOrder.setGuaranteeLicenceName(createLoanDemandOrder.getLoanName());
		}
		
		if (createLoanDemandOrder.getDimensions() == null
			|| "".equals(createLoanDemandOrder.getDimensions().trim())) {
			createLoanDemandOrder.setDimensions("1 ~ 1,000,000,000");
		}
		
		String doType = request.getParameter("doType");
		if ("add".equals(doType) && createLoanDemandOrder.getDemandId() == 0) {
			createLoanDemandOrder.setPublishUserId(SessionLocalManager.getSessionLocal()
				.getUserId());
			createLoanDemandOrder.setPublishUserName(SessionLocalManager.getSessionLocal()
				.getUserName());
			
			jsonobj = this.checkContent(request, response, model, jsonobj, createLoanDemandOrder,
				templateIds);
			if (jsonobj.size() > 0) {
				return jsonobj;
			}
			AddLoanDemandResult result = loanDemandService.addLoanDemand(createLoanDemandOrder);
			logger.info("保存融资基本信息结束：{}", result);
			if (result.isSuccess()) {
				
				String[] selItemIds = createLoanDemandOrder.getSelItemId().split(",");
				processLoanItemDetail(result.getDemandId(), selItemIds);
				
				String bizNo = String.valueOf(result.getDemandId());
				//附件
				YrdBaseResult baseResult = addAttachfile(bizNo, request);
				// 项目图片
				if (StringUtil.isNotBlank(guaranteeLicenseUrl2)) {
					CommonAttachmentOrder order1 = new CommonAttachmentOrder();
					order1.setBizNo(bizNo);
					order1.setRequestPath(guaranteeLicenseUrl2);
					order1.setModuleType(null);
					order1.setIsort(10);
					YrdBaseResult baseResult2 = commonAttachmentService.insert(order1);
				}
				
				if (LoanDemandStatusEnum.DRAFT == createLoanDemandOrder.getStatus()) {
					jsonobj.put("message", "融资基本信息保存成功");
				} else {
					jsonobj.put("message", "发布借款需求成功");
				}
				jsonobj.put("code", 1);
				jsonobj.put("demandId", result.getDemandId());
			} else {
				jsonobj.put("code", 0);
				if (LoanDemandStatusEnum.DRAFT == createLoanDemandOrder.getStatus()) {
					jsonobj.put("message", "融资基本信息保存失败[" + result.getMessage() + "]");
				} else {
					jsonobj.put("message", "发布借款需求失败[" + result.getMessage() + "]");
				}
				
			}
			
		} else { //更新
			YrdBaseResult baseResult = loanDemandService.updateLoanDemand(createLoanDemandOrder);
			if (baseResult.isSuccess()) {
				if (LoanDemandStatusEnum.DRAFT == createLoanDemandOrder.getStatus()) {
					jsonobj.put("code", 1);
					jsonobj.put("message", "融资基本信息保存成功");
				} else {
					jsonobj.put("code", 1);
					jsonobj.put("message", "发布借款需求成功");
				}
				jsonobj.put("demandId", createLoanDemandOrder.getDemandId());
			} else {
				if (LoanDemandStatusEnum.DRAFT == createLoanDemandOrder.getStatus()) {
					jsonobj.put("code", 0);
					jsonobj.put("message", "融资基本信息保存失败[" + baseResult.getMessage() + "]");
				} else {
					jsonobj.put("code", 0);
					jsonobj.put("message", "发布借款需求失败[" + baseResult.getMessage() + "]");
				}
			}
		}
		
		return jsonobj;
	}
	
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping(value = "loanerBaseInfoSubmit")
	public Object loanerBaseInfoSubmit(String guaranteeLicenseUrl2, HttpServletRequest request,
										HttpServletResponse response, Model model,
										long... templateIds) throws Exception {
		
		JSONObject jsonobj = new JSONObject();
		LoanerBaseInfoOrder loanerBaseInfoOrder = new LoanerBaseInfoOrder();
		model.addAllAttributes(WebUtil.getRequestMap(request));
		WebUtil.setPoPropertyByRequest(loanerBaseInfoOrder, request);
		
		UserInfo userInfo = userQueryService.queryByUserName(
			loanerBaseInfoOrder.getLoanerUserName(), SysUserRoleEnum.LOANER.getValue())
			.getQueryUserInfo();
		if (userInfo == null) {
			jsonobj.put("code", 1);
			jsonobj.put("message", "借款人用户不存在");
			return jsonobj;
		}
		
		try {
			loanerBaseInfoOrder.check();
		} catch (IllegalArgumentException ex) {
			jsonobj.put("code", 0);
			jsonobj.put("message", "请求参数异常--" + ex.getLocalizedMessage());
			return jsonobj;
			
		}
		
		String[] selItemIds = loanerBaseInfoOrder.getSelItemId().split(",");
		processLoanItemDetail(loanerBaseInfoOrder.getDemandId(), selItemIds);
		
		YrdBaseResult result = null;
		long userId = userInfo.getUserId();
		loanerBaseInfoOrder.setUserId(userId);
		LoanerBaseInfo loanerBaseInfo = loanerBaseInfoService.findByUserId(userId);
		if (loanerBaseInfo == null) {
			result = loanerBaseInfoService.insert(loanerBaseInfoOrder);
		} else {
			result = loanerBaseInfoService.updateById(loanerBaseInfoOrder);
		}
		
		logger.info("保存借款人信息结束：{}", result);
		if (result != null && result.isSuccess()) {
			String bizNo = String.valueOf(loanerBaseInfoOrder.getDemandId());
			//附件
			String industryType = CommonAttachmentTypeEnum.LOANER_INFO.getIndustryType();
			addAttachfileByType(industryType, bizNo, request);
			
			jsonobj.put("code", 1);
			jsonobj.put("message", "借款人信息保存成功");
		} else {
			jsonobj.put("code", 0);
			if (result != null) {
				jsonobj.put("message", "借款人信息保存失败[" + result.getMessage() + "]");
			} else {
				jsonobj.put("message", "借款人信息保存失败");
			}
		}
		
		return jsonobj;
	}
	
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping(value = "loanNoteSubmit")
	public Object loanNoteSubmit(HttpServletRequest request, HttpServletResponse response,
									Model model) throws Exception {
		
		JSONObject jsonobj = new JSONObject();
		
		CreateLoanDemandOrder loanDemandOrder = new CreateLoanDemandOrder();
		model.addAllAttributes(WebUtil.getRequestMap(request));
		WebUtil.setPoPropertyByRequest(loanDemandOrder, request);
		
		LoanDemandInfo loanDemandInfo = loanDemandQueryService.findById(loanDemandOrder
			.getDemandId());
		if (loanDemandInfo == null) {
			jsonobj.put("code", 0);
			jsonobj.put("message", "不存在要更新的记录");
		} else {
			String[] selItemIds = loanDemandOrder.getSelItemId().split(",");
			processLoanItemDetail(loanDemandOrder.getDemandId(), selItemIds);
			
			LoanDemandLoanNoteOrder updatrOrder = new LoanDemandLoanNoteOrder();
			updatrOrder.setBizNo(loanDemandOrder.getDemandId());
			updatrOrder.setLoanNote(loanDemandOrder.getLoanNote());
			
			YrdBaseResult result = loanDemandService.updateLoanNote(updatrOrder);
			logger.info("保存项目信息结束：{}", result);
			if (result.isSuccess()) {
				String bizNo = String.valueOf(loanDemandOrder.getDemandId());
				//附件
				String industryType = CommonAttachmentTypeEnum.LOAN_NOTE.getIndustryType();
				addAttachfileByType(industryType, bizNo, request);
				
				jsonobj.put("code", 1);
				jsonobj.put("message", "项目信息保存成功");
			} else {
				jsonobj.put("code", 0);
				if (result != null) {
					jsonobj.put("message", "项目信息保存失败[" + result.getMessage() + "]");
				} else {
					jsonobj.put("message", "项目信息保存失败");
				}
			}
		}
		
		return jsonobj;
	}
	
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping(value = "guarantySubmit")
	public Object guarantySubmit(HttpServletRequest request, HttpServletResponse response,
									Model model) throws Exception {
		
		JSONObject jsonobj = new JSONObject();
		
		CreateLoanDemandOrder loanDemandOrder = new CreateLoanDemandOrder();
		model.addAllAttributes(WebUtil.getRequestMap(request));
		WebUtil.setPoPropertyByRequest(loanDemandOrder, request);
		
		long demandId = loanDemandOrder.getDemandId();
		LoanDemandInfo loanDemandInfo = loanDemandQueryService.findById(demandId);
		if (loanDemandInfo == null) {
			jsonobj.put("code", 0);
			jsonobj.put("message", "不存在要更新的记录");
		} else {
			try {
				String[] selItemIds = loanDemandOrder.getSelItemId().split(",");
				processLoanItemDetail(loanDemandOrder.getDemandId(), selItemIds);
				
				CreateLoanDemandExtendOrder loanDemandExtend = new CreateLoanDemandExtendOrder();
				LoanDemandExtendInfo guarantyExtend = loanDemandExtendService
					.findByDemandIdAndProperty(demandId, GUARANTY_PROPERTY_KEY); // LOAN_DEMAND_INFO_ITEM_4:抵押物信息
				if (guarantyExtend != null) {
					BeanCopier.staticCopy(guarantyExtend, loanDemandExtend);
					loanDemandExtend.setPropertyValue(loanDemandOrder.getGuaranty());
					loanDemandExtendService.updateByDemandIdAndProperty(loanDemandExtend);
				} else {
					loanDemandExtend.setLoanDemandId(demandId);
					loanDemandExtend.setPropertyKey(GUARANTY_PROPERTY_KEY);
					loanDemandExtend.setPropertyValue(loanDemandOrder.getGuaranty());
					loanDemandExtendService.addLoanDemandExtend(loanDemandExtend);
				}
				
				String bizNo = String.valueOf(loanDemandOrder.getDemandId());
				//附件
				String industryType = CommonAttachmentTypeEnum.GUARANTY_INFO.getIndustryType();
				addAttachfileByType(industryType, bizNo, request);
				
				jsonobj.put("code", 1);
				jsonobj.put("message", "抵押物信息保存成功");
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				jsonobj.put("code", 0);
				jsonobj.put("message", "抵押物信息保存失败");
			}
		}
		
		return jsonobj;
	}
	
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping(value = "customNoteSubmit")
	public Object customNoteSubmit(HttpServletRequest request, HttpServletResponse response,
									Model model) throws Exception {
		
		JSONObject jsonobj = new JSONObject();
		
		CreateLoanDemandOrder loanDemandOrder = new CreateLoanDemandOrder();
		model.addAllAttributes(WebUtil.getRequestMap(request));
		WebUtil.setPoPropertyByRequest(loanDemandOrder, request);
		
		long demandId = loanDemandOrder.getDemandId();
		LoanDemandInfo loanDemandInfo = loanDemandQueryService.findById(demandId);
		if (loanDemandInfo == null) {
			jsonobj.put("code", 0);
			jsonobj.put("message", "不存在要更新的记录");
		} else {
			try {
				String[] selItemIds = loanDemandOrder.getSelItemId().split(",");
				processLoanItemDetail(loanDemandOrder.getDemandId(), selItemIds);
				
				String propertyKey = LoanDemandExpendEnum.LOAN_DEMAND_INFO_ITEM.code()
										+ loanDemandOrder.getCustomId();
				CreateLoanDemandExtendOrder loanDemandExtend = new CreateLoanDemandExtendOrder();
				LoanDemandExtendInfo guarantyExtend = loanDemandExtendService
					.findByDemandIdAndProperty(demandId, propertyKey);
				if (guarantyExtend != null) {
					BeanCopier.staticCopy(guarantyExtend, loanDemandExtend);
					loanDemandExtend.setPropertyValue(loanDemandOrder.getCustomNote());
					loanDemandExtendService.updateByDemandIdAndProperty(loanDemandExtend);
				} else {
					loanDemandExtend.setLoanDemandId(demandId);
					loanDemandExtend.setPropertyKey(propertyKey);
					loanDemandExtend.setPropertyValue(loanDemandOrder.getCustomNote());
					loanDemandExtendService.addLoanDemandExtend(loanDemandExtend);
				}
				
				jsonobj.put("code", 1);
				jsonobj.put("message", "信息保存成功");
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				jsonobj.put("code", 0);
				jsonobj.put("message", "物信息保存失败");
			}
		}
		
		return jsonobj;
	}
	
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping(value = "guaranteeInfoSubmit")
	public Object guaranteeInfoSubmit(HttpServletRequest request, HttpServletResponse response,
										Model model) throws Exception {
		
		JSONObject jsonobj = new JSONObject();
		
		CreateLoanDemandOrder loanDemandOrder = new CreateLoanDemandOrder();
		model.addAllAttributes(WebUtil.getRequestMap(request));
		WebUtil.setPoPropertyByRequest(loanDemandOrder, request);
		
		long demandId = loanDemandOrder.getDemandId();
		LoanDemandInfo loanDemandInfo = loanDemandQueryService.findById(demandId);
		if (loanDemandInfo == null) {
			jsonobj.put("code", 0);
			jsonobj.put("message", "不存在要更新的记录");
		} else {
			try {
				String[] selItemIds = loanDemandOrder.getSelItemId().split(",");
				processLoanItemDetail(loanDemandOrder.getDemandId(), selItemIds);
				
				LoanDemandGuaranteeOrder updatrOrder = new LoanDemandGuaranteeOrder();
				updatrOrder.setBizNo(loanDemandOrder.getDemandId());
				updatrOrder.setGuaranteeLicenceName(loanDemandOrder.getGuaranteeLicenceName());
				updatrOrder.setGuaranteeLicenseUrl(loanDemandOrder.getGuaranteeLicenseUrl());
				
				YrdBaseResult result = loanDemandService.updateGuaranteeInfo(updatrOrder);
				logger.info("保存保障信息结束：{}", result);
				if (result.isSuccess()) {
					
					CreateLoanDemandExtendOrder loanDemandExtend = new CreateLoanDemandExtendOrder();
					LoanDemandExtendInfo guarantyExtend = loanDemandExtendService
						.findByDemandIdAndProperty(demandId, GUARANTEEINFO_PROPERTY_KEY); // LOAN_DEMAND_INFO_ITEM_5:保障信息
					if (guarantyExtend != null) {
						BeanCopier.staticCopy(guarantyExtend, loanDemandExtend);
						loanDemandExtend.setPropertyValue(loanDemandOrder.getGuaranteeInfo());
						loanDemandExtendService.updateByDemandIdAndProperty(loanDemandExtend);
					} else {
						loanDemandExtend.setLoanDemandId(demandId);
						loanDemandExtend.setPropertyKey(GUARANTEEINFO_PROPERTY_KEY);
						loanDemandExtend.setPropertyValue(loanDemandOrder.getGuaranteeInfo());
						loanDemandExtendService.addLoanDemandExtend(loanDemandExtend);
					}
					
					jsonobj.put("code", 1);
					jsonobj.put("message", "保障信息保存成功");
				} else {
					jsonobj.put("code", 0);
					if (result != null) {
						jsonobj.put("message", "保障信息保存失败[" + result.getMessage() + "]");
					} else {
						jsonobj.put("message", "保障信息保存失败");
					}
				}
				
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				jsonobj.put("code", 0);
				jsonobj.put("message", "保障信息保存失败");
			}
		}
		
		return jsonobj;
	}
	
	@ResponseBody
	@RequestMapping(value = "checkBorrower")
	public Object checkBorrower(String userName) throws Exception {
		logger.info("验证借款人，入参[{}]", userName);
		JSONObject jsonobj = new JSONObject();
		UserInfo userInfo = userQueryService.queryByUserName(userName,
			SysUserRoleEnum.LOANER.getValue()).getQueryUserInfo();
		if (userInfo != null) {
			jsonobj.put("code", 1);
			jsonobj.put("message", userInfo.getRealName());
		} else {
			jsonobj.put("code", 0);
			jsonobj.put("message", "该用户没有权限");
		}
		return jsonobj;
	}
	
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping(value = "checkBorrowerByUserName")
	public Object checkBorrowerByUserName(String userName) throws Exception {
		logger.info("验证借款人，入参[{}]", userName);
		JSONObject jsonobj = new JSONObject();
		UserInfo userInfo = userQueryService.queryByUserName(userName).getQueryUserInfo();
		if (userInfo != null) {
			jsonobj.put("code", 1);
			jsonobj.put("message", userInfo.getRealName());
		} else {
			jsonobj.put("code", 0);
			jsonobj.put("message", "该用户没有权限");
		}
		return jsonobj;
	}
	
	@ResponseBody
	@RequestMapping(value = "getRealName")
	public Object getRealName(String userName) {
		logger.info("验证借款人，入参[{}]", userName);
		JSONObject jsonobj = new JSONObject();
		UserInfo userInfo = userQueryService.queryByUserName(userName).getQueryUserInfo();
		if (userInfo != null) {
			jsonobj.put("code", 1);
			jsonobj.put("message", userInfo.getRealName());
			jsonobj.put("userType", userInfo.getType());
			
			if (AppConstantsUtil.getLoanInfoItem()) {
				//获取借款人信息
				LoanerBaseInfo loanerBaseInfo = loanerBaseInfoService.findByUserId(userInfo
					.getUserId());
				if (loanerBaseInfo != null) {
					jsonobj.put("loanerInfo", loanerBaseInfo);
				}
				
			}
		} else {
			jsonobj.put("code", 0);
			jsonobj.put("message", "该用户没有取款权限 ");
		}
		return jsonobj;
	}
	
	@ResponseBody
	@RequestMapping(value = "getCustomNote")
	public Object getCustomNote(Long demandId, long customId) {
		logger.info("获取自定义模块信息，入参[{}]", demandId, customId);
		JSONObject jsonobj = new JSONObject();
		
		try {
			LoanInfoItemInfo itemInfo = loanInfoItemService.findById(customId);
			jsonobj.put("customName", itemInfo.getInfoName());
			
			if (demandId != null) {
				String propertyKey = LoanDemandExpendEnum.LOAN_DEMAND_INFO_ITEM.code() + customId;
				LoanDemandExtendInfo customNoteExtend = loanDemandExtendService
					.findByDemandIdAndProperty(demandId, propertyKey); // LOAN_DEMAND_INFO_ITEM_4:抵押物信息
				if (customNoteExtend != null) {
					jsonobj.put("code", 1);
					jsonobj.put("message", customNoteExtend.getPropertyValue());
				} else {
					jsonobj.put("code", 0);
					jsonobj.put("message", "");
				}
			}
			
		} catch (Exception e) {
			jsonobj.put("code", 0);
			jsonobj.put("message", "");
		}
		
		return jsonobj;
	}
	
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping(value = "checkguaranteeLicenceNo")
	public Object checkGuaranteeLicenseNo(String guaranteeLicenceNo, String demandId) {
		logger.info("验证借款人，入参[{}]", guaranteeLicenceNo);
		JSONObject jsonobj = new JSONObject();
		try {
			LoanDemandQueryOrder demandQueryOrder = new LoanDemandQueryOrder();
			demandQueryOrder.setGuaranteeLicenceNo(guaranteeLicenceNo);
			QueryBaseBatchResult<LoanDemandInfo> pageList = loanDemandQueryService
				.queryLoandDemand(demandQueryOrder);
			long counts = pageList.getPageList().size();
			if (counts > 0) {
				List<LoanDemandInfo> list = pageList.getPageList();
				if (list.size() == 1) {
					LoanDemandInfo info = list.get(0);
					if (StringUtil.isNotEmpty(demandId)
						&& demandId.equals(String.valueOf(info.getDemandId()))) {
						jsonobj.put("code", 1);
						jsonobj.put("message", "暂无此编号");
						return jsonobj;
					}
				}
				
				jsonobj.put("code", 0);
				jsonobj.put("message", "编号已存在,请重新输入! ");
			} else {
				jsonobj.put("code", 1);
				jsonobj.put("message", "暂无此编号");
			}
		} catch (Exception e) {
			
			logger.error(e.getMessage(), e);
			jsonobj.put("code", 2);
			jsonobj.put("message", "查询错误 ");
		}
		return jsonobj;
	}
	
	@RequestMapping(value = "updateLoanDemand")
	public String updateLoanDemand(long demandId, String info, Model model) throws Exception {
		model.addAttribute("pdfhost", "");
		
		LoanDemandInfo loanDemandInfo = loanDemandQueryService.findById(demandId);
		
		/** 借款人的信息查询 ******/
		if ("Y".equals(AppConstantsUtil.getAddLoanerBaseInfo())) {
			LoanerBaseInfo InfoReslut = loanerBaseInfoService.findByUserId(loanDemandInfo
				.getLoanerId());
			try {
				model.addAttribute("loanerInfo", InfoReslut);
			} catch (Exception e) {
				logger.error("查询借款人信息时出现空指针异常：当前项目没有录入借款人的信息");
			}
		}
		model.addAttribute("info", loanDemandInfo);
		String value = "";
		DecimalFormat df = new DecimalFormat("0.00");
		if (loanDemandInfo.getSaturationConditionMethod() == TradeFullConditionEnum.AMOUNT)
			value = String.valueOf(Long.parseLong(loanDemandInfo.getSaturationCondition()) / 100);
		if (loanDemandInfo.getSaturationConditionMethod() == TradeFullConditionEnum.PERCENTAGE)
			value = String.valueOf(df.format(Double.parseDouble(loanDemandInfo
				.getSaturationCondition()) * 100));
		model.addAttribute("value", value);
		model.addAttribute("guarantee", getInfos(8));
		model.addAttribute("sponsor", getInfos(9));
		model.addAttribute("invest", divisionTemplateManager.getDivisionTemplatesByPhase(
			DivisionPhaseEnum.INVESET_PHASE, DivisionTemplateStatusEnum.NORMAL));
		model.addAttribute("repay", divisionTemplateManager.getDivisionTemplatesByPhase(
			DivisionPhaseEnum.REPAY_PHASE, DivisionTemplateStatusEnum.NORMAL));
		
		DivisionTemplateInfo divisionTemplateLoan = divisionTemplateManager
			.getByTemplateId(loanDemandInfo.getDivisionTempId());
		List<DivisionRuleInfo> investRolelist = divisionTemplateLoan.getRules();
		List<DivisionRuleInfo> repayRolelist = divisionTemplateManager.getByTemplateId(
			loanDemandInfo.getRepayTempId()).getRules();
		double loanInterest1 = 0;
		double investorInterest1 = 0;
		double loanInterest2 = 0;
		double investorInterest2 = 0;
		double totalLoanInterest = 0;
		double totalInvestorInterest = 0;
		// 投资阶段分润信息
		if (investRolelist != null && investRolelist.size() > 0) {
			for (DivisionRuleInfo role : investRolelist) {
				double bg = CommonUtil.mulDI(role.getDivisionRule(), 100);
				totalLoanInterest = CommonUtil.addDD(totalLoanInterest, bg);
				loanInterest1 = CommonUtil.addDD(loanInterest1, bg);
				if (12 == role.getRoleId()) {
					investorInterest1 = CommonUtil.addDD(investorInterest1, bg);
					totalInvestorInterest = CommonUtil.addDD(totalInvestorInterest, bg);
				}
			}
		}
		// 还款阶段分润信息
		if (repayRolelist != null && repayRolelist.size() > 0) {
			for (DivisionRuleInfo role : repayRolelist) {
				double bg1 = CommonUtil.mulDI(role.getDivisionRule(), 100);
				loanInterest2 = CommonUtil.addDD(loanInterest2, bg1);
				totalLoanInterest = CommonUtil.addDD(totalLoanInterest, bg1);
				if (12 == role.getRoleId()) {
					investorInterest2 = CommonUtil.addDD(investorInterest2, bg1);
					totalInvestorInterest = CommonUtil.addDD(totalInvestorInterest, bg1);
				}
			}
		}
		
		//设置各种合同选项
		List<PdfTemplateInfo> contractTmpList = pdfTemplateService
			.getOnlineListByTypeCode(PDFTypeCodeEnum.CONTRACT.code());
		List<PdfTemplateInfo> receiptTmpList = pdfTemplateService
			.getOnlineListByTypeCode(PDFTypeCodeEnum.RECEIPT.code());
		List<PdfTemplateInfo> letterTmpList = pdfTemplateService
			.getOnlineListByTypeCode(PDFTypeCodeEnum.LETTER.code());
		model.addAttribute("contractTmpList", contractTmpList);
		model.addAttribute("receiptTmpList", receiptTmpList);
		model.addAttribute("letterTmpList", letterTmpList);
		
		long contractTemplateId = demandPdfTemplateService.getPdfTemplateIdByDemandIdAndCode(
			demandId, PDFTypeCodeEnum.CONTRACT.code());
		long receiptTemplateId = demandPdfTemplateService.getPdfTemplateIdByDemandIdAndCode(
			demandId, PDFTypeCodeEnum.RECEIPT.code());
		model.addAttribute("contractTemplateId", contractTemplateId);
		model.addAttribute("receiptTemplateId", receiptTemplateId);
		model.addAttribute("investTemplateId", loanDemandInfo.getDivisionTempId());
		model.addAttribute("repayTemplateId", loanDemandInfo.getRepayTempId());
		model.addAttribute("totalLoanInterest", totalLoanInterest);
		model.addAttribute("totalInvestorInterest", String.valueOf(totalInvestorInterest));
		model.addAttribute("loanInterest1", loanInterest1);
		model.addAttribute("loanInterest2", loanInterest2);
		model.addAttribute("investorInterest1", investorInterest1);
		model.addAttribute("investorInterest2", investorInterest2);
		
		UserInfo loanerUser = userQueryService.queryByUserId(loanDemandInfo.getLoanerId()).getQueryUserInfo();
        if(loanerUser != null){
           model.addAttribute("userType", loanerUser.getType());
        }
		
		
		JSONObject jsonobj = (JSONObject) this.queryRuleInfo(String.valueOf(loanDemandInfo
			.getDivisionTempId()), loanDemandInfo.getLoanAmount().toString(), loanDemandInfo
			.getTimeLimitUnit().code(), String.valueOf(loanDemandInfo.getTimeLimit()));
		String str = (String) jsonobj.get("message");
		model.addAttribute("investDivisionInfo", str);
		JSONObject rjsonobj = (JSONObject) this.queryRuleInfo(String.valueOf(loanDemandInfo
			.getRepayTempId()), loanDemandInfo.getLoanAmount().toString(), loanDemandInfo
			.getTimeLimitUnit().code(), String.valueOf(loanDemandInfo.getTimeLimit()));
		String rstr = (String) rjsonobj.get("message");
		model.addAttribute("repayDivisionInfo", rstr);
		
		// 担保公司是否已经二次最终审核
		
		LoanAuthRecordQueryOrder authRecordQueryOrder = new LoanAuthRecordQueryOrder();
		authRecordQueryOrder.setAuthType(YrdAuthTypeEnum.MAKELOANLVTWO);
		authRecordQueryOrder.setLoanDemandId(loanDemandInfo.getDemandId());
		long count = loanAuthRecordManager.countLoanAuthRecordByCondition(authRecordQueryOrder);
		if (count > 0) {
			model.addAttribute("audit", "yes");// 二次审核完成标识
		}
		
		// 项目对应的附件信息
		CommonAttachmentQueryOrder attachmentQueryOrder = new CommonAttachmentQueryOrder();
		attachmentQueryOrder.setBizNo(demandId + "");
		attachmentQueryOrder.setModuleTypeList(YrdEnumUtil.getAttachmentByIndustry());
		QueryBaseBatchResult<CommonAttachmentInfo> batchResult = commonAttachmentService
			.queryCommonAttachment(attachmentQueryOrder);
		List<AttachmentModuleType> attachmentModuleTypeList = new ArrayList<AttachmentModuleType>();
		if (ListUtil.isNotEmpty(batchResult.getPageList()))
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
		
		LoanDemandExtendInfo useLoanExtend = loanDemandExtendService.findByDemandIdAndProperty(
			demandId, LoanDemandExpendEnum.USE_GIFT_MONEY.code());
		if (useLoanExtend != null) {
			model.addAttribute("useGiftMoney", useLoanExtend.getPropertyValue());
		}
		/**收益促销提示*/
		LoanDemandExtendInfo salesPromotionPromptExtend = loanDemandExtendService.findByDemandIdAndProperty(
				demandId, LoanDemandExpendEnum.SALES_PROMOTION_PROMPT.code());
			if (salesPromotionPromptExtend != null) {
				model.addAttribute("salesPromotionPrompt", salesPromotionPromptExtend.getPropertyValue());
			}
		
		
		LoanDemandExtendInfo investmentIncomeExtend = loanDemandExtendService
			.findByDemandIdAndProperty(demandId, LoanDemandExpendEnum.INVESTMENT_INCOME.code());
		if (investmentIncomeExtend != null) {
			model.addAttribute("investmentIncome", investmentIncomeExtend.getPropertyValue());
		}
		
		LoanDemandExtendInfo amountLoanExtend = loanDemandExtendService.findByDemandIdAndProperty(
			demandId, LoanDemandExpendEnum.GIFT_MONEY_AMOUNT.code());
		if (amountLoanExtend != null) {
			model.addAttribute("giftMoneyAmount", amountLoanExtend.getPropertyValue());
		}
		
		LoanDemandExtendInfo increaseLoanExtend = loanDemandExtendService
			.findByDemandIdAndProperty(demandId,
				LoanDemandExpendEnum.USE_GIFT_MONEY_INCREASE.code());
		if (increaseLoanExtend != null) {
			model.addAttribute("giftMoneyIncrease", increaseLoanExtend.getPropertyValue());
		}
		
		LoanDemandExtendInfo useExperienceAmountLoanExtend = loanDemandExtendService
			.findByDemandIdAndProperty(demandId, LoanDemandExpendEnum.USE_EXPERIENCE_AMOUNT.code());
		if (useExperienceAmountLoanExtend != null) {
			model.addAttribute("useExperienceAmount",
				useExperienceAmountLoanExtend.getPropertyValue());
		}
		
		LoanDemandExtendInfo investAddAmountLoanExtend = loanDemandExtendService
			.findByDemandIdAndProperty(demandId, LoanDemandExpendEnum.INVEST_ADD_AMOUNT.code());
		if (investAddAmountLoanExtend != null) {
			model.addAttribute("investAddAmount", investAddAmountLoanExtend.getPropertyValue());
		}
		LoanDemandExtendInfo limitInvestAmount = loanDemandExtendService
				.findByDemandIdAndProperty(demandId, LoanDemandExpendEnum.LIMIT_INVEST_AMOUNT.getCode());
			if (limitInvestAmount != null) {
				model.addAttribute("limitInvestAmount", limitInvestAmount.getPropertyValue());
			}
		
		model.addAttribute("bankingBizType", InternetBankingBizTypeEnum.getAllEnum());
		
		List<LoanDemandExtendInfo> useGiftMoneyRules = loanDemandExtendService
			.findByDemandIdAndPropertyLike(demandId,
				LoanDemandExpendEnum.USE_GIFT_MONEY_RULE.code() + "%");
		if (ListUtil.isNotEmpty(useGiftMoneyRules)) {
			String[] investGiftMoneyAmount = new String[useGiftMoneyRules.size()];
			String[] useGiftMoneyAmount = new String[useGiftMoneyRules.size()];
			for (int i = 0; i < useGiftMoneyRules.size(); i++) {
				LoanDemandExtendInfo extendInfo = useGiftMoneyRules.get(i);
				String[] rules = extendInfo.getPropertyValue().split("->");
				investGiftMoneyAmount[i] = rules[0];
				useGiftMoneyAmount[i] = rules[1];
			}
			model.addAttribute("investGiftMoneyAmount", investGiftMoneyAmount);
			model.addAttribute("useGiftMoneyAmount", useGiftMoneyAmount);
		}
		
		//项目图片
		
		CommonAttachmentQueryOrder attachmentQueryOrder1 = new CommonAttachmentQueryOrder();
		attachmentQueryOrder1.setBizNo(demandId + "");
		attachmentQueryOrder1.setIsort(10);
		QueryBaseBatchResult<CommonAttachmentInfo> batchResult1 = commonAttachmentService
			.queryCommonAttachment(attachmentQueryOrder1);
		if (batchResult1.getPageList().size() > 0) {
			model.addAttribute("imgUrl", batchResult1.getPageList().get(0).getRequestPath());
		}
		
		JSONObject realStr = (JSONObject) this.getRealName(loanDemandInfo.getUserName());
		model.addAttribute("realName", realStr.get("message"));
		model.addAttribute("userType", realStr.get("userType"));
		//获取借款人信息
		long loanerId = loanDemandInfo.getLoanerId();
		LoanerBaseInfo loanerBaseInfo = loanerBaseInfoService.findByUserId(loanerId);
		model.addAttribute("loanerInfo", loanerBaseInfo);
		
		model.addAttribute("uploadHost", "");
		model.addAttribute("insureWayList", InsureWayEnum.getAllEnum());
		model.addAttribute("bizTypeList", LoanBizTypeEnum.getAllEnum());
		
		if (info.equals("info")) {
			TradeInfo trade = tradeBizQueryService.getByLoanDemandId(loanDemandInfo.getDemandId());
			if (trade != null) {
				model.addAttribute("tradeId", trade.getTradeId());
				model.addAttribute("tradeStatus", trade.getTradeStatus().code());
			}
			
			if (AppConstantsUtil.getLoanInfoItem()) {
				//获取启用的信息模块
				LoanInfoItemOrder loanInfoItemInfo = new LoanInfoItemOrder();
				loanInfoItemInfo.setStatus("0");
				List<LoanInfoItemInfo> itemInfoList = loanInfoItemService
					.queryList(loanInfoItemInfo);
				
				LoanDemandItemDetailOrder loanItemDetailOrder = new LoanDemandItemDetailOrder();
				loanItemDetailOrder.setDemandId(demandId);
				List<LoanDemandItemDetailInfo> demandItemDetail = loanInfoItemService
					.queryDetailList(loanItemDetailOrder);
				if (demandItemDetail != null && !demandItemDetail.isEmpty()) {
					for (LoanInfoItemInfo itemInfo : itemInfoList) {
						for (LoanDemandItemDetailInfo itemDetail : demandItemDetail) {
							if (itemInfo.getDemandInfoItemId() == itemDetail.getDemandInfoItemId()) {
								itemInfo.setSel(true);
								break;
							}
						}
					}
					model.addAttribute("itemInfoList", itemInfoList);
					
					setLoanDemandExtendInfo(demandId, model);
					
					model.addAttribute("doType", "info");
					return BORROWING_MANAGE__PATH + "addLoanDemand_new.vm";
				}
				
			}
			
			return BORROWING_MANAGE__PATH + "loanDemandInfo.vm";
		} else {
			if (AppConstantsUtil.getLoanInfoItem()) {
				//获取启用的信息模块
				LoanInfoItemOrder loanInfoItemInfo = new LoanInfoItemOrder();
				loanInfoItemInfo.setStatus("0");
				List<LoanInfoItemInfo> itemInfoList = loanInfoItemService
					.queryList(loanInfoItemInfo);
				
				LoanDemandItemDetailOrder loanItemDetailOrder = new LoanDemandItemDetailOrder();
				loanItemDetailOrder.setDemandId(demandId);
				List<LoanDemandItemDetailInfo> demandItemDetail = loanInfoItemService
					.queryDetailList(loanItemDetailOrder);
				if (demandItemDetail != null && !demandItemDetail.isEmpty()) {
					for (LoanInfoItemInfo itemInfo : itemInfoList) {
						for (LoanDemandItemDetailInfo itemDetail : demandItemDetail) {
							if (itemInfo.getDemandInfoItemId() == itemDetail.getDemandInfoItemId()) {
								itemInfo.setSel(true);
								break;
							}
						}
					}
					model.addAttribute("itemInfoList", itemInfoList);
					
					setLoanDemandExtendInfo(demandId, model);
					
					model.addAttribute("doType", "update");
					return BORROWING_MANAGE__PATH + "addLoanDemand_new.vm";
				}
				
			}
			return BORROWING_MANAGE__PATH + "updateLoanDemand.vm";
		}
	}
	
	@RequestMapping("updateLoanDemand/previewPdf")
	public void previewPdf(HttpServletResponse response, HttpSession session, long demandId,
							long templateId, Model model) throws Exception {
		
		String pdfFileCode = "_DB";
		String serverRealPath = session.getServletContext().getRealPath("/");
		long tradeId = -1;
		long detailId = -1;
		ContractData contractData = certificateService.getContractData(tradeId, detailId,
			templateId, pdfFileCode, serverRealPath, null);
		LoanDemandInfo loanDemandInfo = loanDemandQueryService.findById(demandId);
		TradeInfo tradeInfo = new TradeInfo();
		tradeInfo.setTradeEffectiveDate(loanDemandInfo.getDeadline());
		tradeInfo.setTradeExpireDate(getExpireDateTime(loanDemandInfo));
		tradeInfo.setTimeLimit(loanDemandInfo.getTimeLimit());
		tradeInfo.setTimeLimitUnit(loanDemandInfo.getTimeLimitUnit());
		tradeInfo.setLoanedAmount(loanDemandInfo.getLoanAmount());
		tradeInfo.setInterestRate(loanDemandInfo.getInterestRate());
		loanDemandInfo.setTradeInfo(tradeInfo);
		contractData.setLoanDemandInfo(loanDemandInfo);
		logger.debug("PDFParseThread begin>>>>");
		PDFParse pdf = new PDFParse();
		pdf.writPDF(response, contractData, "creatAndPrivew");
	}
	
	/**
	 * 估计超期时间
	 * @return
	 */
	public Date getExpireDateTime(LoanDemandInfo loanDemandInfo) {
		Date expireDateTime = null;
		LoanPeriodUnitEnum limitUnit = loanDemandInfo.getTimeLimitUnit();
		int timeLimit = loanDemandInfo.getTimeLimit();
		Date currentDate = loanDemandInfo.getDeadline();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(currentDate);
		if (LoanPeriodUnitEnum.LOAN_BY_DAY == limitUnit) {
			calendar.add(Calendar.DATE, timeLimit);
			expireDateTime = calendar.getTime();
		} else if (LoanPeriodUnitEnum.LOAN_BY_MONTH == limitUnit) {
			calendar.add(Calendar.MONTH, timeLimit);
			expireDateTime = calendar.getTime();
			
		} else if (LoanPeriodUnitEnum.LOAN_BY_PEROIDW == limitUnit) {
			calendar.add(Calendar.MONTH, timeLimit);
			expireDateTime = calendar.getTime();
			
		} else if (LoanPeriodUnitEnum.LOAN_BY_YEAR == limitUnit) {
			calendar.add(Calendar.YEAR, timeLimit);
			expireDateTime = calendar.getTime();
		}
		return DateUtil.getEndTimeOfTheDate(expireDateTime);
	}
	
	@RequestMapping("addImg")
	public String addImg(long demandId, HttpServletResponse response, Model model) throws Exception {
		LoanDemandInfo result = loanDemandQueryService.loadLoanDemandInfo(demandId);
		model.addAttribute("info", result);
		return BORROWING_MANAGE__PATH + "addImg.vm";
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping("updataImg/delImg")
	public String delImg(long attachmentId, String filePhysicalPath, HttpServletResponse response,
							Model model, HttpServletRequest request) throws Exception {
		JSONObject jsonobj = new JSONObject();
		
		try {
			
			CommonAttachmentResult baseResult = commonAttachmentService.findById(attachmentId);
			if (baseResult.getAttachmentInfo() != null) {
				deleteServicePicture(baseResult.getAttachmentInfo().getFilePhysicalPath());
				commonAttachmentService.deleteById(attachmentId);
			}
			
			if (StringUtil.isNotEmpty(filePhysicalPath)) {
				deleteServicePicture(filePhysicalPath);
				jsonobj.put("code", 1);
				jsonobj.put("message", "删除成功");
				printHttpResponse(response, jsonobj);
				return null;
			}
			
			if (baseResult.isSuccess()) {
				jsonobj.put("code", 1);
				jsonobj.put("message", "删除成功");
			} else {
				jsonobj.put("code", 0);
				jsonobj.put("message", "删除失败！");
			}
		} catch (Exception e) {
			jsonobj.put("code", 0);
			jsonobj.put("message", "删除异常！");
			logger.error(e.getMessage(), e);
		}
		printHttpResponse(response, jsonobj);
		return null;
		
	}
	
	@RequestMapping("updataImg")
	public String updtaImg(long demandId, HttpServletResponse response, Model model,
							HttpSession session, PageParam pageParam, HttpServletRequest request)
																									throws Exception {
		// 项目的信息
		LoanDemandInfo result = loanDemandQueryService.loadLoanDemandInfo(demandId);
		model.addAttribute("info", result);
		List<CommonAttachmentTypeEnum> enumList = YrdEnumUtil.getAttachmentByIndustry();
		model.addAttribute("enumlist", enumList);
		// 项目对应的附件信息
		CommonAttachmentQueryOrder attachmentQueryOrder = new CommonAttachmentQueryOrder();
		attachmentQueryOrder.setBizNo(demandId + "");
		attachmentQueryOrder.setModuleTypeList(YrdEnumUtil.getAttachmentByIndustry());
		QueryBaseBatchResult<CommonAttachmentInfo> batchResult = commonAttachmentService
			.queryCommonAttachment(attachmentQueryOrder);
		List<AttachmentModuleType> attachmentModuleTypeList = new ArrayList<AttachmentModuleType>();
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
		return BORROWING_MANAGE__PATH + "updataImg.vm";
	}
	
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping(value = "updateLoanDemandSubmit")
	public Object updateLoanDemandSubmit(LoanerBaseInfoOrder loanerBaseInfoOrder,
											HttpServletRequest request,
											HttpServletResponse response, Model model,
											long... templateIds) throws Exception {
		CreateLoanDemandOrder createLoanDemandOrder = new CreateLoanDemandOrder();
		model.addAllAttributes(WebUtil.getRequestMap(request));
		WebUtil.setPoPropertyByRequest(createLoanDemandOrder, request);
		if (request.getParameter("loanPassword") != null) {
			createLoanDemandOrder.setBizType(LoanBizTypeEnum.PRIVATE);
		} else {
			createLoanDemandOrder.setBizType(LoanBizTypeEnum.PUBLIC);
			
		}
		
		createLoanDemandOrder.setInvestGiftMoneyRule(request
			.getParameterValues("investGiftMoneyRule"));
		createLoanDemandOrder.setUseGiftMoneyRule(request.getParameterValues("useGiftMoneyRule"));
		
		createLoanDemandOrder.setTimeLimitUnit(LoanPeriodUnitEnum.getByCode(request
			.getParameter("timeLimitUnit")));
		createLoanDemandOrder.setSaturationConditionMethod(TradeFullConditionEnum.getByCode(request
			.getParameter("saturationConditionMethod")));
		createLoanDemandOrder.setStatus(LoanDemandStatusEnum.getByCode(request
			.getParameter("status")));
		String bankingBizType = request.getParameter("bankingBizType");
		if (InternetBankingBizTypeEnum.getByCode(bankingBizType) != null) {
			createLoanDemandOrder.setBankingBizTypeEnum(InternetBankingBizTypeEnum
				.getByCode(bankingBizType));
		}
		
		createLoanDemandOrder.setInsureWay(InsureWayEnum.getByCode(request
			.getParameter("insureWay")));
		
		if (LoanBizTypeEnum.getByCode(request.getParameter("bizType")) != null) {
			createLoanDemandOrder.setBizType(LoanBizTypeEnum.getByCode(request
				.getParameter("bizType")));
		}
		
		JSONObject jsonobj = new JSONObject();
		createLoanDemandOrder.setInvestTemplateId(templateIds[0]);
		createLoanDemandOrder.setRepayTemplateId(templateIds[1]);
		jsonobj = this.checkContent(request, response, model, jsonobj, createLoanDemandOrder,
			templateIds);
		if (jsonobj.size() > 0) {
			return jsonobj;
		}
		String isDirectional = request.getParameter("isDirectional");
		if (StringUtil.equals(isDirectional, "Y")) {
			if (StringUtil.isEmpty(createLoanDemandOrder.getLoanPassword())) {
				jsonobj.put("code", 0);
				jsonobj.put("message", "请设置密码");
				return jsonobj;
			}
			if (createLoanDemandOrder.getLoanPassword().length() > 8
				&& createLoanDemandOrder.getLoanPassword().length() < 6) {
				jsonobj.put("code", 0);
				jsonobj.put("message", "定向融资密码长度应在6到8之间");
				return jsonobj;
			}
		} else {
			createLoanDemandOrder.setLoanPassword(null);
		}
		
		YrdBaseResult baseResult = loanDemandService.updateLoanDemand(createLoanDemandOrder);
		if (baseResult.isSuccess()) {
			/** 更新借款人的信息 ***************************************************/
			if ("Y".equals(AppConstantsUtil.getAddLoanerBaseInfo())) {
				long userId = userQueryService.queryByUserName(createLoanDemandOrder.getUserName())
					.getQueryUserInfo().getUserId();
				loanerBaseInfoOrder.setCreateTime(new Date());
				loanerBaseInfoOrder.setUserId(userId);
				YrdBaseResult addInfoReslut = loanerBaseInfoService.updateById(loanerBaseInfoOrder);
				
			}
			if (LoanDemandStatusEnum.DRAFT == createLoanDemandOrder.getStatus()) {
				jsonobj.put("code", 1);
				jsonobj.put("message", "存入草稿成功");
			} else {
				jsonobj.put("code", 1);
				jsonobj.put("message", "发布借款需求成功");
			}
			
			if (LoanDemandStatusEnum.DRAFT == createLoanDemandOrder.getStatus()) {
				jsonobj.put("code", 1);
				jsonobj.put("message", "存入草稿成功");
			} else {
				jsonobj.put("code", 1);
				jsonobj.put("message", "发布借款需求成功");
			}
			
		} else {
			if (LoanDemandStatusEnum.DRAFT == createLoanDemandOrder.getStatus()) {
				jsonobj.put("code", 0);
				jsonobj.put("message", "存入草稿失败[" + baseResult.getMessage() + "]");
			} else {
				jsonobj.put("code", 0);
				jsonobj.put("message", "发布借款需求失败[" + baseResult.getMessage() + "]");
			}
		}
		return jsonobj;
		
	}
	
	@ResponseBody
	@RequestMapping("approvalPass")
	public Object approvalPass(long demandId, String status, String publishDate) {
		JSONObject jsonobj = new JSONObject();
		try {
			LoanDemandCheckPassOrder checkPassOrder = new LoanDemandCheckPassOrder();
			checkPassOrder.setBizNo(demandId);
			Date pDate = null;
			if (StringUtil.isNotBlank(publishDate)) {
				pDate = DateUtil.parse(publishDate);
			}
			checkPassOrder.setPassFlag(BooleanEnum.YES);
			checkPassOrder.setPublishDate(pDate);
			YrdBaseResult baseResult = loanDemandService.checkPassLoanDemand(checkPassOrder);
			if (baseResult.isSuccess()) {
				jsonobj.put("code", 1);
				jsonobj.put("message", "借款需求,通过成功！");
			} else {
				jsonobj.put("code", 0);
				jsonobj.put("message", "借款需求,通过失败！");
			}
		} catch (Exception e) {
			jsonobj.put("code", 0);
			jsonobj.put("message", "借款需求,通过异常！");
			logger.error(e.getMessage(), e);
		}
		return jsonobj;
	}
	
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping("approvalDismiss")
	public Object approvalDismiss(long demandId, String status, String dismissReason) {
		JSONObject jsonobj = new JSONObject();
		try {
			TradeProcessOrder processOrder = new TradeProcessOrder();
			processOrder.setBizNo(demandId);
			processOrder.setProcessAdvice(dismissReason);
			YrdBaseResult baseResult = loanDemandService.dismissLoanDemand(processOrder);
			if (baseResult.isSuccess()) {
				jsonobj.put("code", 1);
				jsonobj.put("message", "借款需求,驳回成功！");
			} else {
				jsonobj.put("code", 0);
				jsonobj.put("message", "借款需求,驳回失败！");
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			jsonobj.put("code", 0);
			jsonobj.put("message", "借款需求,驳回异常！");
		}
		return jsonobj;
	}
	
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping("queryRuleInfo")
	public Object queryRuleInfo(String name, String loanAmount, String timeLimitUnit,
								String timeLimit) {
		StringBuffer str = new StringBuffer();
		JSONObject jsonobj = new JSONObject();
		
		double loanInterest = 0;
		double investorInterest = 0;
		try {
			CalculateLoanCostOrder loanCostOrder = new CalculateLoanCostOrder();
			loanCostOrder.setTemplateId(NumberUtil.parseLong(name));
			if (StringUtil.isNotEmpty(loanAmount)) {
				Money loanMoney = new Money(NumberUtil.parseDouble(loanAmount));
				loanCostOrder.setLoanAmount(loanMoney);
			}
			loanCostOrder.setTimeLimitUnit(LoanPeriodUnitEnum.getByCode(timeLimitUnit));
			loanCostOrder.setTimeLimit(NumberUtil.parseInt(timeLimit));
			CalculateLoanCostResult loanCostResult = tradeBizQueryService
				.calculateLoanCost(loanCostOrder);
			if (loanCostResult.getRoleName() != null) {
				for (int i = 0; i < loanCostResult.getRoleName().length; i++) {
					str.append(loanCostResult.getRoleName()[i] + ":")
						.append(RateUtil.getRate(loanCostResult.getDivisionRule()[i])).append(" ");
					
				}
			}
			investorInterest = loanCostResult.getInvestorInterest();
			loanInterest = loanCostResult.getLoanInterest();
			jsonobj.put("message", str.toString());
			jsonobj.put("loanInterest", loanInterest);
			jsonobj.put("investorInterest", investorInterest);
			jsonobj.put("tradeChargeRate", loanCostResult.getTradeChargeRate());
			jsonobj.put("tradeChargeAmount", loanCostResult.getTradeChargeAmount()
				.toStandardString());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			jsonobj.put("code", 0);
			jsonobj.put("message", "查询失败！");
			jsonobj.put("loanInterest", loanInterest);
			jsonobj.put("investorInterest", investorInterest);
		}
		return jsonobj;
	}
	
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping("updateFileUpLoadUrl")
	public Object updateFileUpLoadUrl(long id, String newUrl, long loanAmount, String audit)
																							throws Exception {
		// LoanDemandInfo loanDemandInfo;
		// int result = 0;
		JSONObject jsonobj = new JSONObject();
		// try {
		// loanDemandInfo = loanDemandQueryService.findById(id);
		// if (loanDemandInfo == null) {
		// jsonobj.put("code", 0);
		// jsonobj.put("message", "不存在要更新的记录");
		// } else {
		// result = loanDemandManager.updateFileUploadUrlById(id, newUrl,
		// loanAmount, audit);
		// if (result > 0) {
		// jsonobj.put("code", 1);
		// jsonobj.put("message", "更新担保函成功");
		// } else {
		// jsonobj.put("code", 0);
		// jsonobj.put("message", "更新担保函失败");
		// }
		// }
		// } catch (Exception e) {
		//
		// logger.error(e.getMessage(), e);
		// jsonobj.put("code", 0);
		// jsonobj.put("message", "更新异常");
		// return jsonobj;
		// }
		jsonobj.put("code", 1);
		jsonobj.put("message", "更新担保函成功");
		return jsonobj;
	}
	
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping("fixRepayTime")
	public Object fixRepayTime(long tradeId, String expireDate, Model model) throws Exception {
		JSONObject jsonobj = new JSONObject();
		try {
			if (tradeId > 0 && expireDate != null) {
				UpdateTradeExpireDateOrder expireDateOrder = new UpdateTradeExpireDateOrder();
				expireDateOrder.setBizNo(tradeId);
				expireDateOrder.setExpireDate(DateUtil.parse(expireDate));
				YrdBaseResult baseResult = tradeBizProcessService
					.updateTradeExpireDate(expireDateOrder);
				if (baseResult.isSuccess()) {
					jsonobj.put("code", 1);
					jsonobj.put("message", "修订成功！");
				} else {
					jsonobj.put("code", 0);
					jsonobj.put("message", "修订失败！");
				}
			} else {
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			jsonobj.put("code", 0);
			jsonobj.put("message", "修订失败！");
		}
		return jsonobj;
	}
	
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping("onlineTrade")
	public Object onlineTrade(long loanId, Model model) throws Exception {
		JSONObject jsonobj = new JSONObject();
		try {
			if (loanId > 0) {
				try {
					TradeProcessOrder processOrder = new TradeProcessOrder();
					processOrder.setBizNo(loanId);
					loanDemandService.onlineTrade(processOrder);
					jsonobj.put("code", 1);
					jsonobj.put("message", "交易上线成功！");
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					jsonobj.put("code", 0);
					jsonobj.put("message", "交易上线失败！");
				}
			} else {
				jsonobj.put("code", 0);
				jsonobj.put("message", "交易上线失败！");
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			jsonobj.put("code", 0);
			jsonobj.put("message", "交易上线失败！");
		}
		return jsonobj;
	}
	
	/**
	 * 上传担保函或合同时更新url地址
	 * 
	 * @param id
	 * @param pdfUrl
	 * @param type
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping("updatePdfUrl")
	public Object updatePdfUrl(long id, String pdfUrl, String type) {
		LoanDemandInfo loanDemandInfo;
		JSONObject jsonobj = new JSONObject();
		String message = "更新失败";
		YrdBaseResult result = new YrdBaseResult();
		try {
			loanDemandInfo = loanDemandQueryService.findById(id);
			if (loanDemandInfo == null) {
				jsonobj.put("code", 0);
				jsonobj.put("message", "不存在要更新的记录");
			} else {
				CreateLoanDemandOrder createLoanDemandOrder = new CreateLoanDemandOrder();
				BeanCopier.staticCopy(loanDemandInfo, createLoanDemandOrder);
				if ("contract".equals(type)) {
					createLoanDemandOrder.setContractPdfUrl(pdfUrl);
					message = "投资权益回购履约担保合同";
				} else if ("letter".equals(type)) {
					createLoanDemandOrder.setLetterPdfUrl(pdfUrl);
					message = "担保函";
				}
				result = loanDemandService.updateLoanDemand(createLoanDemandOrder);
				if (result.isSuccess()) {
					// 如果上传了担保函和合同发出消息通知
					if ("IS".equals(loanDemandInfo.getGuaranteeAudit())
						&& StringUtil.isNotBlank(loanDemandInfo.getContractPdfUrl())
						&& StringUtil.isNotBlank(loanDemandInfo.getLetterPdfUrl())
						&& StringUtil.isNotBlank(loanDemandInfo.getGuaranteeLicenseUrl())) {
						message = message + "成功，合同以及担保函均上传";
					}
					jsonobj.put("code", 1);
					jsonobj.put("message", "更新" + message + "成功");
				} else {
					jsonobj.put("code", 0);
					jsonobj.put("message", "更新" + message + "失败");
				}
			}
		} catch (Exception e) {
			
			logger.error("更新异常", e);
			jsonobj.put("code", 0);
			jsonobj.put("message", "更新异常");
			return jsonobj;
		}
		return jsonobj;
	}
	
	@ResponseBody
	@RequestMapping("notifyInvestorPdfOK")
	public Object notifyInvestorPdfOK(long demandId) {
		JSONObject jsonobj = new JSONObject();
		TradeInfo trade = tradeBizQueryService.getByLoanDemandId(demandId);
		try {
			notifierService.notifierInvestorPDFOK(trade.getTradeId());
			jsonobj.put("code", "1");
			jsonobj.put("message", "通知成功");
		} catch (Exception e) {
			logger.error("通知投资人合同已上传失败", e);
			jsonobj.put("code", "0");
			jsonobj.put("message", "通知失败");
		}
		return jsonobj;
	}
	
	protected boolean deleteServicePicture(String picPath) {
		if (picPath != null) {
			File file = new File(picPath);
			if (file.exists()) {
				try {
					file.delete();
					return true;
				} catch (Exception e) {
					logger.error("删除图片出错，图片物理路径{}", picPath, e);
					return false;
				}
			}
		}
		return false;
	}
	
	private void processLoanItemDetail(long demandId, String[] selItemIds) throws Exception {
		LoanDemandItemDetailOrder loanItemDetailOrder = new LoanDemandItemDetailOrder();
		loanItemDetailOrder.setSelItemIds(selItemIds);
		loanItemDetailOrder.setDemandId(demandId);
		loanInfoItemService.processLoanItemDetail(loanItemDetailOrder);
	}
	
	private void setLoanDemandExtendInfo(long demandId, Model model) {
		
		List<LoanDemandExtendInfo> extendInfos = loanDemandExtendService.findByDemandId(demandId);
		for (LoanDemandExtendInfo einfo : extendInfos) {
			if (GUARANTY_PROPERTY_KEY.equals(einfo.getPropertyKey())) {
				//获取抵押物信息
				model.addAttribute("guaranty", einfo.getPropertyValue());
			} else if (GUARANTEEINFO_PROPERTY_KEY.equals(einfo.getPropertyKey())) {
				//获取保障信息
				model.addAttribute("guaranteeInfo", einfo.getPropertyValue());
			} else if (LoanDemandExpendEnum.LOAN_TYPE.getCode().equals(einfo.getPropertyKey())) {
				/** 项目信用类型 */
				model.addAttribute("loanType", einfo.getPropertyValue());
			} else if (LoanDemandExpendEnum.PLATFORM_TYPE.getCode().equals(einfo.getPropertyKey())) {
				/** 平台产品分类 */
				model.addAttribute("platformType", einfo.getPropertyValue());
			} else if (LoanDemandExpendEnum.INVEST_ADD_AMOUNT.getCode().equals(
				einfo.getPropertyKey())) {
				/** 递增金额 */
				model.addAttribute("investAddAmount", einfo.getPropertyValue());
			}
			//			else if(LoanDemandExpendEnum.CONTRACT_TEMPLATE.getCode().equals(einfo.getPropertyKey())){
			//				/** 合同模板 */
			//				model.addAttribute("contractTemplate", einfo.getPropertyValue());
			//			}
			
		}
	}
	
}
