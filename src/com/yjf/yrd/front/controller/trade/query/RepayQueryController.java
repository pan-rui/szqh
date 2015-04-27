package com.yjf.yrd.front.controller.trade.query;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.yjf.common.lang.util.ListUtil;
import com.yjf.common.lang.util.money.Money;
import com.yjf.yrd.base.CommonBindingInitializer;
import com.yjf.yrd.front.controller.trade.base.LoanTradeDetailBaseController;
import com.yjf.yrd.page.PageParam;
import com.yjf.yrd.session.SessionLocalManager;
import com.yjf.yrd.user.info.PersonalInfo;
import com.yjf.yrd.user.info.UserInfo;
import com.yjf.yrd.util.AppConstantsUtil;
import com.yjf.yrd.util.CommonUtil;
import com.yjf.yrd.util.DateUtil;
import com.yjf.yrd.web.util.PageUtil;
import com.yjf.yrd.ws.enums.DivisionPhaseEnum;
import com.yjf.yrd.ws.enums.LoanPeriodUnitEnum;
import com.yjf.yrd.ws.enums.SysUserRoleEnum;
import com.yjf.yrd.ws.enums.TradeDetailStatusEnum;
import com.yjf.yrd.ws.info.LoanDemandInfo;
import com.yjf.yrd.ws.info.TradeDetailInfo;
import com.yjf.yrd.ws.info.TradeFlowCodeInfo;
import com.yjf.yrd.ws.info.TradeInfo;
import com.yjf.yrd.ws.service.TradeFlowCodeManager;
import com.yjf.yrd.ws.service.query.order.QueryTradeDetailUserOrder;
import com.yjf.yrd.ws.service.query.result.QueryBaseBatchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * 
 * 
 * @Filename RepayQueryController.java
 * 
 * @Description
 * 
 * @Version 1.0
 * 
 * @Author fc
 * 
 * @Email oyangnuo@yiji.com
 * 
 * @History <li>Author: fc</li> <li>Date: 2014-8-1</li> <li>Version: 1.0</li>
 * <li>Content: create</li>
 * 
 */
@Controller
@RequestMapping("repayQuery")
public class RepayQueryController extends LoanTradeDetailBaseController {
	private final String vm_path = "/front/trade/query/";
	@Autowired
	TradeFlowCodeManager tradeFlowCodeManager;
	
	//		@Autowired
	//		private DebtTransferBizProcessService debtTransferBizProcessService;
	//		@Autowired
	//		private DebtTransferBizQueryService debtTransferBizQueryService;
	
	/**
	 * 工具类处理金额 /时间
	 * */
	@Override
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		super.initBinder(binder);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		dateFormat.setLenient(false);
		binder.registerCustomEditor(Money.class, "minAmountIn", new CommonBindingInitializer());
		binder.registerCustomEditor(Money.class, "maxAmountIn", new CommonBindingInitializer());
		binder
			.registerCustomEditor(Date.class, "startTime", new CustomDateEditor(dateFormat, true));
		binder.registerCustomEditor(Date.class, "endTime", new CustomDateEditor(dateFormat, true));
	}
	
	@Override
	protected String[] getMoneyInputNameArray() {
		return new String[] { "minLoanAmount", "maxLoanAmount" };
	}
	
	/**
	 * 查询待收款明细记录
	 */
	@RequestMapping("colletions/{size}/{page}")
	public String colletions(@PathVariable int size, @PathVariable int page, String scope,
								Integer status, String startRepayDate, String endRepayDate,
								Model model, HttpSession session) {
		
		this.initAccountInfo(model);
		QueryTradeDetailUserOrder queryOrder = new QueryTradeDetailUserOrder();
		if (scope != null && !scope.equals("")) {
			startRepayDate = DateUtil.formatDay(new Date());
			if (scope.equals("1")) {
				endRepayDate = DateUtil.formatDay(DateUtil.getDateByMonth(new Date(), 1));
			} else if (scope.equals("2")) {
				endRepayDate = DateUtil.formatDay(DateUtil.getDateByMonth(new Date(), 3));
			} else {
				endRepayDate = DateUtil.formatDay(DateUtil.getDateByMonth(new Date(), 12));
			}
			Date startTime = DateUtil.parse(startRepayDate);
			Date endTime = DateUtil.parse(endRepayDate);
			queryOrder.setStartRepayDate(startTime);
			queryOrder.setEndRepayDate(endTime);
		} else {
			Date startTime = null;
			Date endTime = null;
			if (startRepayDate != null && endRepayDate != null && !startRepayDate.equals("")
				&& !endRepayDate.equals("")) {
				startTime = DateUtil.parse(startRepayDate);
				endTime = DateUtil.parse(endRepayDate);
			}
			queryOrder.setStartRepayDate(startTime);
			queryOrder.setEndRepayDate(endTime);
		}
		
		queryOrder.setPageSize(size);
		queryOrder.setPageNumber(page);
		queryOrder.setUserId(SessionLocalManager.getSessionLocal().getUserId());
		queryOrder.setRoleEnum(SysUserRoleEnum.INVESTOR);
		queryOrder.setTransferPhase(DivisionPhaseEnum.REPAY_PHASE);
		queryOrder.setDetailStatus(TradeDetailStatusEnum.UN_PROCESS.code());
        queryOrder.setNotHasTradeFail("N");
        queryOrder.setIsCollection(true);
		
		QueryBaseBatchResult<TradeDetailInfo> batchResult = tradeBizQueryService
			.queryTradeDetailUserPage(queryOrder);
		for (TradeDetailInfo tradeDetailInfo : batchResult.getPageList()) {
			TradeInfo tradeInfo=tradeBizQueryService.getByTradeId(tradeDetailInfo.getTradeId());
			if(tradeInfo!=null){
				tradeDetailInfo.setDemandId(tradeInfo.getDemandId());
			}
		}
		model.addAttribute("page", PageUtil.getCovertPage(batchResult));
		model.addAttribute("queryOrder", queryOrder);
		
		QueryBaseBatchResult<TradeDetailInfo> batchResult1 = tradeBizQueryService // 不分页结果
			.queryTradeDetailUser(queryOrder);
		
		Money totalAmount = new Money();
		Money totalOriginalAmount = new Money();
		for (TradeDetailInfo tradeInfo : batchResult1.getPageList()) {
			totalAmount = totalAmount.addTo(tradeInfo.getAmount());
			if (tradeInfo.getRepayPeriodNo() == tradeInfo.getRepayPeriodCount()) {
				totalOriginalAmount = totalOriginalAmount.addTo(tradeInfo.getOriginalAmount());
			}
		}
		
		model.addAttribute("page1", PageUtil.getCovertPage(batchResult1));
		model.addAttribute("totalAmount", totalAmount.getAmount()); // 总收益
		model.addAttribute("totalOriginalAmount", totalOriginalAmount.getAmount()); // 总收益
		model.addAttribute("totalMoney",
			totalAmount.getAmount().add(totalOriginalAmount.getAmount()));// 收款金额
		
		return vm_path + "invest_colletions.vm";
	}
	
	/**
	 * 查询待已款明细记录
	 */
	@RequestMapping("colletionsed/{size}/{page}")
	public String colletionsed(@PathVariable int size, @PathVariable int page, String scope,
								Integer status, String startActualRepayDate,
								String endActualRepayDate, Model model, HttpSession session) {
		
		this.initAccountInfo(model);
		
		QueryTradeDetailUserOrder queryOrder = new QueryTradeDetailUserOrder();
		if (scope != null && !scope.equals("")) {
			endActualRepayDate = DateUtil.formatDay(new Date());
			if (scope.equals("1")) {
				startActualRepayDate = DateUtil.formatDay(DateUtil.getDateByMonth(new Date(), -1));
			} else if (scope.equals("2")) {
				startActualRepayDate = DateUtil.formatDay(DateUtil.getDateByMonth(new Date(), -3));
			} else {
				startActualRepayDate = DateUtil.formatDay(DateUtil.getDateByMonth(new Date(), -12));
			}
			Date startTime = DateUtil.parse(startActualRepayDate);
			Date endTime = DateUtil.parse(endActualRepayDate);
			queryOrder.setStartActualRepayDate(startTime);
			queryOrder.setEndActualRepayDate(endTime);
		} else {
			Date startTime = null;
			Date endTime = null;
			if (startActualRepayDate != null && endActualRepayDate != null
				&& !startActualRepayDate.equals("") && !endActualRepayDate.equals("")) {
				startTime = DateUtil.parse(startActualRepayDate);
				endTime = DateUtil.parse(endActualRepayDate);
			}
			queryOrder.setStartActualRepayDate(startTime);
			queryOrder.setEndActualRepayDate(endTime);
		}
		queryOrder.setPageSize(size);
		queryOrder.setPageNumber(page);
		queryOrder.setUserId(SessionLocalManager.getSessionLocal().getUserId());
		queryOrder.setRoleEnum(SysUserRoleEnum.INVESTOR);
		queryOrder.setTransferPhase(DivisionPhaseEnum.REPAY_PHASE);
		queryOrder.setDetailStatus(TradeDetailStatusEnum.PROCESSED.code());
        queryOrder.setIsCollection(true);
		
		QueryBaseBatchResult<TradeDetailInfo> batchResult = tradeBizQueryService
			.queryTradeDetailUserPage(queryOrder);
		for (TradeDetailInfo tradeDetailInfo : batchResult.getPageList()) {
			TradeInfo tradeInfo=tradeBizQueryService.getByTradeId(tradeDetailInfo.getTradeId());
			if(tradeInfo!=null){
				tradeDetailInfo.setDemandId(tradeInfo.getDemandId());
			}
		}
		model.addAttribute("page", PageUtil.getCovertPage(batchResult));
		model.addAttribute("queryOrder", queryOrder);
		
		QueryBaseBatchResult<TradeDetailInfo> batchResult1 = tradeBizQueryService // 不分页结果
			.queryTradeDetailUser(queryOrder);
		
		Money totalAmount = new Money();
		Money totalOriginalAmount = new Money();
		for (TradeDetailInfo tradeInfo : batchResult1.getPageList()) {
			totalAmount = totalAmount.addTo(tradeInfo.getAmount());
			if (tradeInfo.getRepayPeriodNo() == tradeInfo.getRepayPeriodCount()) {
				totalOriginalAmount = totalOriginalAmount.addTo(tradeInfo.getOriginalAmount());
			}
		}
		
		model.addAttribute("page1", PageUtil.getCovertPage(batchResult1));
		model.addAttribute("totalAmount", totalAmount.getAmount()); // 总收益
		model.addAttribute("totalOriginalAmount", totalOriginalAmount.getAmount()); // 总收益
		model.addAttribute("totalMoney",
			totalAmount.getAmount().add(totalOriginalAmount.getAmount()));// 收款金额
		return vm_path + "invest_colletionsed.vm";
	}

    /**
     * 查看投资详情
     * @param tradeId
     * @param operate
     * @param status
     * @param detailId
     * @param model
     * @param session
     * @param request
     * @param pageParam
     * @return
     * @throws Exception
     */
	@RequestMapping("queryInvestDetails/{tradeId}")
	public String queryInvestDetails(@PathVariable long tradeId,String operate,String status, long detailId, Model model,
										HttpSession session, HttpServletRequest request,
										PageParam pageParam) throws Exception {
		this.initAccountInfo(model);
		model.addAttribute("pdfhost", "");
		
		try {
			
			getTradeDetailPageView(0, tradeId, model, session, SysUserRoleEnum.LOANER, request,
				pageParam);
			String token = UUID.randomUUID().toString();
			model.addAttribute("operate", operate);
			model.addAttribute("status", status);
			session.setAttribute("token", token);
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return "front/trade/query/colletionDetails.vm";
	}

    /**
     * 查看投资详情凭证
     * @param tradeId
     * @param detailId
     * @param model
     * @return
     * @throws Exception
     */
	@RequestMapping("investReceipt/{tradeId}")
	public String investReceipt(@PathVariable long tradeId, long detailId, Model model)
																						throws Exception {
		logger.info("下载投资详情凭证，入参：[tradeId={},detailId={}],", tradeId + "," + detailId);
		
		model.addAttribute("detailId", detailId);
		model.addAttribute("tradeId", tradeId);
		
		return "front/trade/query/invest_receipt.vm";
	}


    /**
     * 查看投资详情凭证
     * @param tradeId
     * @param detailId
     * @param model
     * @param response
     * @param request
     * @throws Exception
     */
	@RequestMapping("toDownLReceipt")
	public void toDownLReceipt(long tradeId, long detailId, Model model,
								HttpServletResponse response, HttpServletRequest request)
																							throws Exception {
		logger.info("下载或预览投资凭证 ：" + tradeId + ":" + detailId);
		
		downLoadInvestReceipt(tradeId, detailId, "privew", response, request, model);
		
		// model.addAttribute("detailId", detailId);
		// model.addAttribute("tradeId", tradeId);
		// return "front/trade/query/invest_receipt.vm";
	}
	
	@RequestMapping("investReceiptDownLoad/{tradeId}")
	public void downLoadInvestReceipt(@PathVariable long tradeId, long detailId, String checkType,
										HttpServletResponse response, HttpServletRequest request,
										Model model) throws Exception {
		String timeLimit = "";
		String interestRate = "";
		String guaranteeName = "";
		String investFlowCode = null;
		String investor = "";
		String investorReal = "";
		String investorCertNo = "";
		String loanner = "";
		String loannerReal = "";
		String loannerCertNo = "";
		String investAmount = "";
		String totalAmountStr = "";
		String effectiveDate = "";
		String expireDate = "";
		PdfReader reader = null;
		Money interest = new Money(0);
		
		LoanDemandInfo loanDemand = this.loanDemandQueryService
			.loadLoanDemandInfoByTradeId(tradeId);
		TradeInfo trade = loanDemand.getTradeInfo();
		effectiveDate = DateUtil.dtSimpleFormat(trade.getTradeEffectiveDate());
		expireDate = DateUtil.dtSimpleFormat(trade.getTradeExpireDate());
		
		timeLimit = loanDemand.getTimeLimit() + loanDemand.getTimeLimitUnit().getViewName();
		
		TradeDetailInfo detailInfo = tradeBizQueryService.getByTradeDetailId(detailId,
			SysUserRoleEnum.INVESTOR);
		
		interestRate = CommonUtil.mul(loanDemand.getInterestRate(), 100) + "%";
		
		Money totalAmount = new Money();
		if (detailInfo != null) {
			
			investAmount = detailInfo.getAmount().toStandardString();
			long investorId = detailInfo.getUserId();
			long loannerId = loanDemand.getLoanerId();
			UserInfo user = userQueryService.queryByUserId(investorId).getQueryUserInfo();
			try {
                PersonalInfo person = userQueryService.queryPersonalInfoByBaseId(user.getUserBaseId()).getQueryPersonalInfo();
				investorCertNo =person.getCertNo();
			} catch (Exception e) {
				logger.error("getCertNoByUserId is error", e);
			}
            UserInfo userLoanner = userQueryService.queryByUserId(loannerId).getQueryUserInfo();
			try {
                PersonalInfo person = userQueryService.queryPersonalInfoByBaseId(userLoanner.getUserBaseId()).getQueryPersonalInfo();
				loannerCertNo = person.getCertNo();
			} catch (Exception e) {
				logger.error("getCertNoByUserId is error", e);
			}
			
			investor = user.getUserName();
			investorReal = user.getRealName();
			loannerReal = userLoanner.getRealName();
			loanner = userLoanner.getUserName();
			totalAmount = detailInfo.getAmount();
			
			// 计算应收利息
			interest = caculateInterest(detailInfo.getAmount(), loanDemand.getInterestRate(),
				loanDemand.getTimeLimit(), loanDemand.getTimeLimitUnit());
			
		}
		
		Money divisionAmount = new Money(0);
		Money profitAmount = new Money(0);
		QueryTradeDetailUserOrder queryTradeDetailUserOrder = new QueryTradeDetailUserOrder();
		queryTradeDetailUserOrder.setTradeDetailId(String.valueOf(detailId));
		queryTradeDetailUserOrder.setTradeId(tradeId);
		QueryBaseBatchResult<TradeDetailInfo> batchResult = tradeBizQueryService
			.queryTradeDetailUser(queryTradeDetailUserOrder);
		if (ListUtil.isNotEmpty(batchResult.getPageList())) {
			for (TradeDetailInfo detail : batchResult.getPageList()) {
				divisionAmount.addTo(detail.getAmount());
				if (detail.isProfitType()) {
					profitAmount.addTo(detail.getAmount());
				}
			}
		}
		// totalAmount.addTo(divisionAmount); //原来为什么要加?
		TradeFlowCodeInfo tradeFlowCodeInfo = tradeFlowCodeManager
			.queryInvestFlowCodesByTradeDetailId(detailId);
		if (tradeFlowCodeInfo != null) {
			investFlowCode = tradeFlowCodeInfo.getTradeFlowCode();
		}
		
		// LoanAuthRecordQueryOrder queryOrder = new LoanAuthRecordQueryOrder();
		// queryOrder.setAuthType(YrdAuthTypeEnum.MAKELOANLVTWO);
		// queryOrder.setLoanDemandId(trade.getDemandId());
		// long count =
		// loanAuthRecordManager.countLoanAuthRecordByCondition(queryOrder);
		// if (count > 0) {
		// model.addAttribute("audit", "yes");
		// }
		
		totalAmountStr = totalAmount.add(interest).toStandardString();
		
		guaranteeName = loanDemand.getGuaranteeName();
		
		FileInputStream fis = null;
		BufferedInputStream buff = null;
		OutputStream servletOS = null;
		String servletPath = request.getServletContext().getRealPath("/");
		String filePath = servletPath + "/resources/pdf/investReceiptInitial"
							+ System.currentTimeMillis() + ".pdf";
		String fileOutPath = servletPath + "/resources/pdf/investReceipt"
								+ System.currentTimeMillis() + ".pdf";
		String imagePath = servletPath + "/styles/images/common/logo.jpg";
		// String imageZhangPath = servletPath +
		// "/styles/images/common/logo.jpg";
		FileOutputStream fos = null;
		String extName = "投资凭证.pdf";
		Document doc = new Document(PageSize.A4, 20, 20, 140, 20);
		try {
			fos = new FileOutputStream(filePath);
			PdfWriter writer = PdfWriter.getInstance(doc, fos);
			doc.open();
			// 解决中文问题
			BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H",
				BaseFont.NOT_EMBEDDED);
			Font titleChinese = new Font(bfChinese, 20, Font.BOLD); // 模板抬头的字体
			Paragraph title = new Paragraph("投资凭证", titleChinese);// 抬头
			title.setAlignment(Element.ALIGN_CENTER); // 居中设置
			title.setLeading(1f);// 设置行间距//设置上面空白宽度
			doc.add(title);
			
			Font fontZH = new Font(bfChinese, 12, Font.NORMAL);
			float[] widths = { 20f, 30f, 25f, 25f };
			PdfPTable table = new PdfPTable(widths);
			table.setSpacingBefore(20f);// 设置表格上面空白宽度
			table.setTotalWidth(500);// 设置表格的宽度
			table.setWidthPercentage(100);// 设置表格宽度为%100
			// table.getDefaultCell().setBorder(0);//设置表格默认为无边框
			PdfPCell cell;
			// cell = new PdfPCell(new Paragraph("投资凭证",fontZH));
			// cell.setColspan(4);
			// table.addCell(cell);
			
			cell = new PdfPCell(new Paragraph("平台服务商：", fontZH));
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			
			table.addCell(cell);
			
			cell = new PdfPCell(new Paragraph(AppConstantsUtil.getPlatformName(), fontZH));
			table.addCell(cell);
			
			cell = new PdfPCell(new Paragraph("还款方式：", fontZH)); // //??
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			table.addCell(cell);
			
			cell = new PdfPCell(
				new Paragraph(loanDemand.getRepayDivisionWay().getMessage(), fontZH));
			table.addCell(cell);
			
			cell = new PdfPCell(new Paragraph("年化收益率：", fontZH));
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			table.addCell(cell);
			
			cell = new PdfPCell(new Paragraph(interestRate, fontZH));
			table.addCell(cell);
			
			cell = new PdfPCell(new Paragraph("投资期限：", fontZH));
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			table.addCell(cell);
			
			cell = new PdfPCell(new Paragraph(timeLimit, fontZH));
			table.addCell(cell);
			
			cell = new PdfPCell(new Paragraph("承诺公司：", fontZH));
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			table.addCell(cell);
			
			cell = new PdfPCell(new Paragraph(guaranteeName, fontZH));
			cell.setColspan(3);
			table.addCell(cell);
			
			cell = new PdfPCell(new Paragraph("投资流水号：", fontZH));
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			table.addCell(cell);
			
			cell = new PdfPCell(new Paragraph(investFlowCode, fontZH));
			cell.setColspan(3);
			table.addCell(cell);
			
			cell = new PdfPCell(new Paragraph("投资人信息", fontZH));
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell.setColspan(2);
			table.addCell(cell);
			
			cell = new PdfPCell(new Paragraph("借款人信息", fontZH));
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell.setColspan(2);
			table.addCell(cell);
			Paragraph iparas = new Paragraph("用户名：" + investor, fontZH);
			iparas.add(Chunk.NEWLINE);
			iparas.add("姓名：" + investorReal);
			iparas.add(Chunk.NEWLINE);
			iparas.add("身份证号：" + investorCertNo);
			iparas.add(Chunk.NEWLINE);
			iparas.add("成立日：" + effectiveDate);
			iparas.add(Chunk.NEWLINE);
			iparas.add("到期日：" + expireDate);
			iparas.add(Chunk.NEWLINE);
			iparas.add("投资本金(元)：" + investAmount);
			iparas.add(Chunk.NEWLINE);
			iparas.add("利息(元)：" + interest);
			iparas.add(Chunk.NEWLINE);
			iparas.add("应收总计(元)：" + totalAmountStr);
			
			cell = new PdfPCell(iparas);
			cell.setColspan(2);
			cell.setRowspan(8);
			
			cell.setMinimumHeight(120);
			table.addCell(cell);
			Paragraph paras = new Paragraph("用户名：" + loanner, fontZH);
			paras.add(Chunk.NEWLINE);
			paras.add("姓名：" + loannerReal);
			paras.add(Chunk.NEWLINE);
			paras.add("身份证号：" + loannerCertNo);
			paras.add(Chunk.NEWLINE);
			paras.add("成立日：" + effectiveDate);
			paras.add(Chunk.NEWLINE);
			paras.add("兑付日：" + expireDate);
			paras.add(Chunk.NEWLINE);
			paras.add(Chunk.NEWLINE);
			paras.add(Chunk.NEWLINE);
			paras.add("应付总计(元)：" + totalAmountStr);
			cell = new PdfPCell(paras);
			cell.setColspan(2);
			cell.setRowspan(8);
			table.addCell(cell);
			doc.add(table);
			doc.add(Chunk.NEWLINE);
			doc.add(Chunk.NEWLINE);
			doc.add(Chunk.NEWLINE);
			doc.add(Chunk.NEWLINE);
			doc.add(Chunk.NEWLINE);
			doc.add(Chunk.NEWLINE);
			doc.add(Chunk.NEWLINE);
			doc.add(Chunk.NEWLINE);
			doc.add(Chunk.NEWLINE);
			doc.add(Chunk.NEWLINE);
			doc.add(Chunk.NEWLINE);
			doc.add(Chunk.NEWLINE);
			doc.add(Chunk.NEWLINE);
			doc.add(Chunk.NEWLINE);
			doc.add(Chunk.NEWLINE);
			doc.add(Chunk.NEWLINE);
			doc.add(Chunk.NEWLINE);
			doc.add(Chunk.NEWLINE);
			Paragraph tips = new Paragraph("  温馨提示： 需要盖章的客户，请亲临"
											+ AppConstantsUtil.getPlatformName() + "进行人工盖章", fontZH);// 抬头
			tips.setLeading(1f);// 设置行间距//设置上面空白宽度
			doc.add(tips);
			doc.add(Chunk.NEWLINE);
			doc.add(Chunk.NEWLINE);
			doc.add(Chunk.NEWLINE);
			doc.add(Chunk.NEWLINE);
			doc.add(Chunk.NEWLINE);
			doc.add(Chunk.NEWLINE);
			doc.add(Chunk.NEWLINE);
			doc.add(Chunk.NEWLINE);
			doc.add(Chunk.NEWLINE);
			doc.add(Chunk.NEWLINE);
			doc.add(Chunk.NEWLINE);
			doc.add(Chunk.NEWLINE);
			doc.add(Chunk.NEWLINE);
			doc.add(Chunk.NEWLINE);
			tips = new Paragraph("  公司地址：" + AppConstantsUtil.getPlatformAddress(), fontZH);// 抬头
			tips.setLeading(1f);// 设置行间距//设置上面空白宽度
			doc.add(tips);
			// XMLWorkerHelper.getInstance().parseXHtml(writer, doc,
			// new ByteArrayInputStream(str.getBytes()));
			doc.close();
			logger.info("创建文档完成");
			reader = new PdfReader(filePath, "yijiu".getBytes());// 选择需要印章的pdf
			fos = new FileOutputStream(fileOutPath);
			PdfStamper stamp = new PdfStamper(reader, fos);// 加完印章后的pdf
			// stamp.setEncryption(PdfWriter.STANDARD_ENCRYPTION_128, "yijiu",
			// "123456", PdfWriter.ALLOW_PRINTING );
			/*
			 * PdfContentByte over = stamp.getOverContent(1);//设置在第几页打印印章 Image
			 * img = Image.getInstance(imagePath);//选择图片 img.setAlignment(1);
			 * img.scaleAbsolute(124, 22);//控制图片大小 img.setAbsolutePosition(50,
			 * 700);//控制图片位置 over.addImage(img);
			 */
			// Image imgZhang = Image.getInstance(imageZhangPath);//选择图片
			// imgZhang.setAlignment(1);
			// imgZhang.scaleAbsolute(181, 190);//控制图片大小
			// imgZhang.setAbsolutePosition(185, 578);//控制图片位置
			// over.addImage(imgZhang);
			stamp.close();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
		File file = new File(fileOutPath);
		try {
			fis = new FileInputStream(file);
			buff = new BufferedInputStream(fis);
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage(), e);
		}
		if ("downLoad".equals(checkType)) {
			response.setContentType("application/pdf");
			try {
				response.addHeader("Content-Disposition", "attachment; filename="
															+ new String(
																extName.getBytes("GB2312"),
																"ISO8859-1"));
			} catch (UnsupportedEncodingException e1) {
				logger.error("UnsupportedEncodingException ", e1);
			}
		}
		byte[] b = new byte[1024];
		long k = 0;
		try {
			servletOS = response.getOutputStream();
			while (k < file.length()) {
				int j = buff.read(b, 0, 1024);
				k += j;
				servletOS.write(b, 0, j);
			}
			servletOS.flush();
			servletOS.close();
			fis.close();
			file.delete();
			File fileInit = new File(filePath);
			fileInit.delete();
		} catch (IOException e) {
			logger.error("delete file", e);
		} finally {
			if (fis != null) {
				fis.close();
			}
			if (fos != null) {
				fos.close();
			}
		}
	}
	
	/**
	 * 计算利息
	 * 
	 * @param capital
	 * @param interestRate
	 * @param timeLimit
	 * @param timeLimitUnit
	 * @return
	 */
	private Money caculateInterest(Money capital, double interestRate, int timeLimit,
									LoanPeriodUnitEnum timeLimitUnit) {
		Money interest = new Money(0);
		
		BigDecimal interestAnnual = capital.getAmount().multiply(new BigDecimal(interestRate));
		
		if (timeLimitUnit == LoanPeriodUnitEnum.LOAN_BY_DAY) { // 天
			interest = new Money(interestAnnual.multiply(new BigDecimal(timeLimit)).divide(
				new BigDecimal(360), 4, BigDecimal.ROUND_HALF_EVEN));
		} else if (timeLimitUnit == LoanPeriodUnitEnum.LOAN_BY_MONTH
					|| timeLimitUnit == LoanPeriodUnitEnum.LOAN_BY_PEROIDW) {// 月
			interest = new Money(interestAnnual.multiply(new BigDecimal(timeLimit)).divide(
				new BigDecimal(12), 4, BigDecimal.ROUND_HALF_EVEN));
		} else {// 年
			interest = new Money(interestAnnual.multiply(new BigDecimal(timeLimit)));
		}
		return interest;
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
