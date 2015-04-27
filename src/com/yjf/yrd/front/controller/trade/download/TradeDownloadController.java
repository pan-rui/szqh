package com.yjf.yrd.front.controller.trade.download;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
import com.yjf.common.lang.util.ListUtil;
import com.yjf.common.lang.util.StringUtil;
import com.yjf.yrd.base.BaseAutowiredController;
import com.yjf.yrd.dataobject.Trade;
import com.yjf.yrd.dataobject.UserInvestEntry;
import com.yjf.yrd.integration.openapi.info.DepositInfo;
import com.yjf.yrd.integration.openapi.info.QueryWithdrawInfo;
import com.yjf.yrd.integration.openapi.order.WithdrawQueryOrder;
import com.yjf.yrd.integration.openapi.result.WithdrawQueryResult;
import com.yjf.yrd.page.PageParam;
import com.yjf.yrd.service.certificate.CertificateService;
import com.yjf.yrd.service.trade.download.ContractData;
import com.yjf.yrd.service.trade.download.DataMap;
import com.yjf.yrd.service.trade.download.PDFParse;
import com.yjf.yrd.session.SessionLocal;
import com.yjf.yrd.session.SessionLocalManager;
import com.yjf.yrd.user.info.InstitutionsInfo;
import com.yjf.yrd.user.info.PersonalInfo;
import com.yjf.yrd.user.info.UserInfo;
import com.yjf.yrd.util.AppConstantsUtil;
import com.yjf.yrd.util.CommonUtil;
import com.yjf.yrd.util.DateUtil;
import com.yjf.yrd.ws.enums.DivisionPhaseEnum;
import com.yjf.yrd.ws.enums.PDFTypeCodeEnum;
import com.yjf.yrd.ws.enums.SysUserRoleEnum;
import com.yjf.yrd.ws.enums.UserTypeEnum;
import com.yjf.yrd.ws.info.LoanDemandInfo;
import com.yjf.yrd.ws.info.TradeAmountInfo;
import com.yjf.yrd.ws.info.TradeDetailInfo;
import com.yjf.yrd.ws.info.TradeFlowCodeInfo;
import com.yjf.yrd.ws.info.TradeInfo;
import com.yjf.yrd.ws.service.TradeFlowCodeManager;
import com.yjf.yrd.ws.service.query.order.FileElecCertificateOrder;
import com.yjf.yrd.ws.service.query.order.QueryTradeDetailUserOrder;
import com.yjf.yrd.ws.service.query.result.QueryBaseBatchResult;

@Controller
@RequestMapping("tradeDownload")
public class TradeDownloadController extends BaseAutowiredController {
	public final String FILE_PATH = "WEB-INF/doc/";
	public final String LETTER = "担保函.doc";
	public final String CONTRACT = "投资权益回购履约担保合同.doc";
	
	@Autowired
	TradeFlowCodeManager tradeFlowCodeManager;
	@Autowired
	CertificateService certificateService;
	
	@RequestMapping("downloadExcel")
	public void downloadExcel(HttpServletResponse response, String type) {
		Object obj = DataMap.getMap().get(type);
		try {
			this.createExcel(response, obj, type);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(), e);
		}
	}
	
	@SuppressWarnings({ "unchecked", "deprecation" })
	public void createExcel(HttpServletResponse response, Object obj, String type)
																					throws IOException {
		HSSFWorkbook wb = new HSSFWorkbook();//建立新HSSFWorkbook对象
		HSSFSheet sheet = wb.createSheet("查询数据");//建立新的sheet对象
		if (type.equals("UserInvestEntry")) {
			List<UserInvestEntry> invert = (List<UserInvestEntry>) obj;
			
			if (invert.size() > 0) {
				String[] title = { "创建时间", "名称/交易号", "交易对方", "交易金额（元）", "交易状态" };
				HSSFRow row = sheet.createRow(0);
				HSSFCellStyle style = wb.createCellStyle();
				style.setAlignment(HSSFCellStyle.ALIGN_LEFT);
				for (int i = 0; i < title.length; i++) {
					HSSFCell cell = row.createCell((short) i);
					cell.setCellValue(title[i]);
					cell.setCellStyle(style);
				}
				for (int n = 0; n < invert.size(); n++) {
					row = sheet.createRow(n + 1);
					row.createCell(0).setCellValue(
						new SimpleDateFormat("yyyy-mm-dd").format(invert.get(n).getDate()));
					row.createCell(1).setCellValue(
						invert.get(n).getTradeName() + "/" + invert.get(n).getTradeCode());
					row.createCell(2).setCellValue(invert.get(n).getLoanerRealName());
					row.createCell(3).setCellValue(invert.get(n).getAmount());
					row.createCell(4).setCellValue(invert.get(n).getStatus());
				}
			}
		} else if (type.equals("withdraw")) {
			List<QueryWithdrawInfo> withdraw = (List<QueryWithdrawInfo>) obj;
			if (withdraw != null && withdraw.size() > 0) {
				String[] title = { "申请提现时间", "提现流水号", "提现金额（元）", "提现手续费（元）", "提现银行", "提现状态" };
				HSSFRow row = sheet.createRow(0);
				HSSFCellStyle style = wb.createCellStyle();
				style.setAlignment(HSSFCellStyle.ALIGN_LEFT);
				for (int i = 0; i < title.length; i++) {
					HSSFCell cell = row.createCell((short) i);
					cell.setCellValue(title[i]);
					cell.setCellStyle(style);
				}
				for (int n = 0; n < withdraw.size(); n++) {
					row = sheet.createRow(n + 1);
					row.createCell(0).setCellValue(
						new SimpleDateFormat("yyyy-mm-dd").format(withdraw.get(n).getPayTime()));
					row.createCell(1).setCellValue(withdraw.get(n).getOutBizNo());
					row.createCell(2).setCellValue(withdraw.get(n).getAmountIn().toCNString());
					row.createCell(3).setCellValue(withdraw.get(n).getCharge().toCNString());
					row.createCell(4).setCellValue(withdraw.get(n).getBankName());
					row.createCell(5).setCellValue(withdraw.get(n).getStatus());
				}
			}
		} else if (type.equals("recharge")) {
			List<DepositInfo> deposit = (List<DepositInfo>) obj;
			if (deposit != null && deposit.size() > 0) {
				String[] title = { "充值时间", "充值流水号", "充值金额（元）", "充值使用银行", "状态" };
				HSSFRow row = sheet.createRow(0);
				HSSFCellStyle style = wb.createCellStyle();
				style.setAlignment(HSSFCellStyle.ALIGN_LEFT);
				for (int i = 0; i < title.length; i++) {
					HSSFCell cell = row.createCell((short) i);
					cell.setCellValue(title[i]);
					cell.setCellStyle(style);
				}
				for (int n = 0; n < deposit.size(); n++) {
					row = sheet.createRow(n + 1);
					row.createCell(0).setCellValue(
						new SimpleDateFormat("yyyy-mm-dd").format(deposit.get(n).getPayTime()));
					row.createCell(1).setCellValue(deposit.get(n).getPayNo());
					row.createCell(2).setCellValue(deposit.get(n).getAmount().toString());
					row.createCell(3).setCellValue(deposit.get(n).getBankName());
					String status = deposit.get(n).getStatus();
					if (status.equals("DEPOSITED") || status.equals("SUCCESS")) {
						row.createCell(4).setCellValue("成功");
					} else if (status.equals("FAILURE") || status.equals("CANCELED")) {
						row.createCell(4).setCellValue("失败");
					} else {
						row.createCell(4).setCellValue("处理中");
					}
				}
			}
		} else if (type.equals("trade")) {
			List<Trade> tradeList = (List<Trade>) obj;
			if (tradeList != null && tradeList.size() > 0) {
				String[] title = { "申请号", "申请时间", "借款金额(元)", "已借款金额(元)", "状态" };
				HSSFRow row = sheet.createRow(0);
				HSSFCellStyle style = wb.createCellStyle();
				style.setAlignment(HSSFCellStyle.ALIGN_LEFT);
				for (int i = 0; i < title.length; i++) {
					HSSFCell cell = row.createCell((short) i);
					cell.setCellValue(title[i]);
					cell.setCellStyle(style);
				}
				for (int n = 0; n < tradeList.size(); n++) {
					row = sheet.createRow(n + 1);
					row.createCell(0).setCellValue(tradeList.get(n).getCode());
					row.createCell(1)
						.setCellValue(
							new SimpleDateFormat("yyyy-mm-dd").format(tradeList.get(n)
								.getCreateDate()));
					row.createCell(2).setCellValue(tradeList.get(n).getAmount());
					row.createCell(3).setCellValue(tradeList.get(n).getLoanedAmount());
					short status = tradeList.get(n).getStatus();
					if (status == 0) {
						row.createCell(4).setCellValue("待审核");
					} else if (status == 1) {
						row.createCell(4).setCellValue("募集中");
					} else if (status == 2) {
						row.createCell(4).setCellValue("还款中");
					} else if (status == 3) {
						row.createCell(4).setCellValue("交易完成");
					} else {
						row.createCell(4).setCellValue("交易失败");
					}
				}
			}
		}
		OutputStream out = null;
		out = response.getOutputStream();
		response
			.setHeader("Content-disposition",
				"attachment; filename=" + new String("数据查询信息".getBytes("GB2312"), "ISO8859-1")
						+ ".xls");
		response.setContentType("application/msexcel");
		wb.write(out);
		out.close();
		out.flush();
	}
	
	/**
	 * 担保公司下载投资信息
	 * @param demandId
	 * @param session
	 * @param response
	 */
	@RequestMapping("downLoaduserInvests")
	public void downLoaduserInvests(long demandId, HttpSession session, HttpServletResponse response) {
		logger.info("下载投资信息");
		try {
			LoanDemandInfo loanDemand = loanDemandQueryService.loadLoanDemandInfo(demandId);
			UserInfo user = userQueryService.queryByUserName(loanDemand.getUserName())
				.getQueryUserInfo();
			
			TradeInfo trade = loanDemand.getTradeInfo();
			if (trade != null) {
				
				QueryTradeDetailUserOrder queryTradeDetailUserOrder = new QueryTradeDetailUserOrder();
				queryTradeDetailUserOrder.setRoleEnum(SysUserRoleEnum.INVESTOR);
				queryTradeDetailUserOrder.setTradeId(trade.getTradeId());
				queryTradeDetailUserOrder.setTransferPhase(DivisionPhaseEnum.ORIGINAL_PHASE);
				QueryBaseBatchResult<TradeDetailInfo> details = tradeBizQueryService
					.queryTradeDetailUser(queryTradeDetailUserOrder);
				if (ListUtil.isNotEmpty(details.getPageList())) {
					this.creatInvestsExcel(response, details.getPageList(), trade,
						user.getRealName(), loanDemand);
				}
			}
		} catch (Exception e) {
			logger.error("下载投资信息异常", e);
		}
	}
	
	@SuppressWarnings("deprecation")
	public void creatInvestsExcel(HttpServletResponse response, List<TradeDetailInfo> objList,
									TradeInfo trade, String name, LoanDemandInfo demandInfo)
																							throws IOException {
		HSSFWorkbook wb = new HSSFWorkbook();//建立新HSSFWorkbook对象
		HSSFSheet sheet = wb.createSheet("查询数据");//建立新的sheet对象
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		//		style.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
		//		style.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
		//		style.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
		//		style.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框
		if (objList.size() > 0 && objList != null) {
			HSSFFont font = wb.createFont();
			font.setFontName("黑体");
			font.setFontHeightInPoints((short) 20);//设置字体大小
			style.setFont(font);
			HSSFRow row1 = sheet.createRow(0);
			HSSFRow row2 = sheet.createRow(1);
			HSSFCell cell1 = row1.createCell((short) 0);
			HSSFCell cell2 = row2.createCell((short) 0);
			cell1.setCellValue("投资人名单");
			cell1.setCellStyle(style);
			for (int k1 = 1; k1 < 6; k1++) {
				cell1 = row1.createCell((short) k1);
				cell1.setCellStyle(style);
			}
			for (int k2 = 0; k2 < 6; k2++) {
				cell2 = row2.createCell((short) k2);
				cell2.setCellStyle(style);
			}
			CellRangeAddress region1 = new CellRangeAddress(0, 1, (short) 0, (short) 5);
			sheet.addMergedRegion(region1);
			
			HSSFFont font3 = wb.createFont();
			font3.setFontName("黑体");
			font3.setFontHeightInPoints((short) 13);//设置字体大小
			HSSFCellStyle style3 = wb.createCellStyle();
			style3.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
			//			style3.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
			//			style3.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
			//			style3.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
			//			style3.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框
			style3.setFont(font3);
			HSSFRow row3 = sheet.createRow(2);
			HSSFCell cell3 = row3.createCell((short) 0);
			cell3.setCellStyle(style3);
			//担保合同号
			String guaranteeContractCode = "";
			QueryTradeDetailUserOrder queryTradeDetailUserOrder = new QueryTradeDetailUserOrder();
			queryTradeDetailUserOrder.setRoleEnum(SysUserRoleEnum.GUARANTEE);
			queryTradeDetailUserOrder.setTradeId(trade.getTradeId());
			QueryBaseBatchResult<TradeDetailInfo> details = tradeBizQueryService
				.queryTradeDetailUser(queryTradeDetailUserOrder);
			if (ListUtil.isNotEmpty(details.getPageList())) {
				TradeFlowCodeInfo tradeFlow = tradeFlowCodeManager
					.queryInvestFlowCodesByTradeDetailId(details.getPageList().get(0)
						.getTradeDetailId());
				if (tradeFlow != null) {
					guaranteeContractCode = tradeFlow.getTradeFlowCode();
				}
			}
			
			if (StringUtil.isEmpty(guaranteeContractCode)) {
				guaranteeContractCode = demandInfo.getGuaranteeLicenceNo();
				
			}
			String number = "名单编号:"
							+ guaranteeContractCode.substring(0,
								guaranteeContractCode.lastIndexOf("号") + 1) + "M";
			cell3.setCellValue(number);
			for (int k1 = 1; k1 < 6; k1++) {
				cell3 = row3.createCell((short) k1);
				cell3.setCellStyle(style3);
			}
			CellRangeAddress region2 = new CellRangeAddress(2, 2, (short) 0, (short) 5);
			sheet.addMergedRegion(region2);
			
			String[] title = { "投资流水号", "投资人姓名", "投资人身份证号", "投资金额", "投资日期", "成立日期" };
			HSSFRow row = sheet.createRow(3);
			row.setHeightInPoints(20f);
			HSSFFont font1 = wb.createFont();
			font1.setFontName("黑体");
			font1.setFontHeightInPoints((short) 11);//设置字体大小
			HSSFCellStyle style1 = wb.createCellStyle();
			style1.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			style1.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
			style1.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
			style1.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
			style1.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框
			style1.setFont(font1);
			for (int i = 0; i < title.length; i++) {
				HSSFCell cell = row.createCell((short) i);
				cell.setCellValue(title[i]);
				cell.setCellStyle(style1);
			}
			HSSFCellStyle setBorder = wb.createCellStyle();
			setBorder.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
			setBorder.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
			setBorder.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
			setBorder.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框
			for (int n = 0; n < objList.size(); n++) {
				TradeDetailInfo tradeDetailInfo = objList.get(n);
				TradeFlowCodeInfo tradeFlow = tradeFlowCodeManager
					.queryInvestFlowCodesByTradeDetailId(tradeDetailInfo.getTradeDetailId());
				UserInfo userBaseInfo = userQueryService.queryByUserId(tradeDetailInfo.getUserId())
					.getQueryUserInfo();
				
				String certNo = "";
				try {
					
					PersonalInfo person = null;
					InstitutionsInfo institutions = null;
					if (userBaseInfo.getType() == UserTypeEnum.GR) {
						person = userQueryService.queryPersonalInfoByBaseId(
							userBaseInfo.getUserBaseId()).getQueryPersonalInfo();
						certNo = person.getCertNo();
					} else {
						institutions = userQueryService.queryInstitutionsInfoByBaseId(
							userBaseInfo.getUserBaseId()).getQueryInstitutionsInfo();
						certNo = institutions.getLegalRepresentativeCardNo();
					}
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
				row = sheet.createRow(n + 4);
				row.setHeightInPoints(20f);
				row.createCell(0).setCellValue(tradeFlow.getTradeFlowCode());
				row.getCell(0).setCellStyle(setBorder);
				row.createCell(1).setCellValue(userBaseInfo.getRealName());
				row.getCell(1).setCellStyle(setBorder);
				row.createCell(2).setCellValue(certNo);
				row.getCell(2).setCellStyle(setBorder);
				row.createCell(3)
					.setCellValue("￥" + tradeDetailInfo.getAmount().toStandardString());
				row.getCell(3).setCellStyle(setBorder);
				row.createCell(4).setCellValue(
					DateUtil.simpleFormatYmdhms(tradeDetailInfo.getCreateDate()));
				row.getCell(4).setCellStyle(setBorder);
				row.createCell(5).setCellValue(
					DateUtil.simpleFormatYmdhms(trade.getTradeEffectiveDate()));
				row.getCell(5).setCellStyle(setBorder);
			}
			sheet.setColumnWidth((short) 0, 24 * 320); //调整第一列宽度
			sheet.setColumnWidth((short) 1, 24 * 140);//调整第二列宽度
			sheet.setColumnWidth((short) 2, 24 * 220);//调整第三列宽度
			sheet.setColumnWidth((short) 3, 24 * 164);//调整第四列宽度
			sheet.setColumnWidth((short) 4, 24 * 184);//调整第五列宽度
			sheet.setColumnWidth((short) 5, 24 * 184);//调整第六列宽度
		}
		OutputStream out = null;
		out = response.getOutputStream();
		response.setHeader("Content-disposition",
			"attachment; filename="
					+ new String((trade.getTradeName() + "投资信息").getBytes("GB2312"), "ISO8859-1")
					+ ".xls");
		response.setContentType("application/msexcel");
		wb.write(out);
		out.close();
		out.flush();
	}
	
	/**
	 * 企付通 下载或预览合同、担保函
	 * @param response
	 * @param session
	 * @param tradeId
	 * @param type
	 * @param detailID
	 * @throws Exception
	 */
	@RequestMapping("toDownLContract")
	public void toDownLContract(HttpServletResponse response, HttpSession session, long tradeId)
																								throws Exception {
		logger.info("下载或预览合同、担保函 ：" + tradeId);
		downLoadWord(response, session, tradeId, "contract_DB", 0, "privew");
	}
	
	/**
	 * 下载或预览合同、担保函
	 * @param response
	 * @param session
	 * @param tradeId
	 * @param type
	 * @param detailID
	 * @throws Exception
	 */
	@RequestMapping("downLoadWord")
	public void downLoadWord(HttpServletResponse response, HttpSession session, long tradeId,
								String type, long detailID, String downType) throws Exception {
		
		logger.info("downLoadWord:" + downType + "_" + type);
		LoanDemandInfo loanDemandInfo = loanDemandQueryService.loadLoanDemandInfoByTradeId(tradeId);
		long pdfTemplateId = 0;
		try { //预防部分平台未更新表结构而报错
			pdfTemplateId = demandPdfTemplateService.getPdfTemplateIdByDemandIdAndCode(
				loanDemandInfo.getDemandId(), PDFTypeCodeEnum.CONTRACT.code());
		} catch (Exception e1) {
			logger.error("downLoadInvestReceipt Exception ", e1);
		}
		
		String pdfFileCode = "";
		if (pdfTemplateId > 0 && type.indexOf("_DB") > 0) { //新发布的需求有设置模板
			pdfFileCode = type;
		} else { //历史数据无配置模板，模板类型从配置文件中取
			pdfFileCode = sysFunctionConfigService.getHistoryPDFTypeCode();
		}
		String serverRealPath = session.getServletContext().getRealPath("/");
		ContractData contractData = certificateService.getContractData(tradeId, detailID, -1,
			pdfFileCode, serverRealPath, PDFTypeCodeEnum.CONTRACT);
		logger.debug("PDFParseThread begin>>>>");
		PDFParse pdf = new PDFParse();
		pdf.writPDF(response, contractData, downType);
	}
	
	@RequestMapping("guaranteeContract")
	public String guaranteeContract(HttpServletResponse response, HttpSession session,
									long tradeId, Long detailId, Model model) {
		logger.info("查看投资担保合同，入参：[tradeId={} ],", tradeId);
		long countInvestTimes = 0;
		SessionLocal sessionLocal = SessionLocalManager.getSessionLocal();
		if (sessionLocal != null) {
			countInvestTimes = 1;
		}
		
		String elecCertiUrl = null;
		try {
			String fileKey = FileElecCertificateOrder.newContractFileKey(tradeId);
			elecCertiUrl = fileCertificateService.geElecCertiURLByFileKey(fileKey);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		model.addAttribute("elecCertiUrl", elecCertiUrl);
		model.addAttribute("tradeId", tradeId);
		model.addAttribute("detailId", detailId);
		model.addAttribute("countInvestTimes", countInvestTimes);
		return "front/trade/query/guaranteeContract.vm";
	}
	
	/**
	 * 预览
	 * @param response
	 * @param session
	 * @param tradeId
	 * @param type
	 * @param detailID
	 * @param read
	 * @param model
	 * @return
	 */
	@RequestMapping("contractPreview")
	public String contractPreview(HttpServletResponse response, HttpSession session, long tradeId,
									String type, long detailID, String read, Model model) {
		//this.downLoadWordParse(response, session, tradeId, type, detailID, read, model);
		return "/front/business/contract/previewData.vm";
	}
	
	@RequestMapping("contractPassTradeId")
	public String contractPassTradeId(HttpSession session, long tradeId, String letterType,
										Model model) {
		TradeInfo trade = tradeBizQueryService.getByTradeId(tradeId);
		model.addAttribute("trade", trade);
		model.addAttribute("letterType", letterType);
		return "/front/business/contract/contractPreview.vm";
	}
	
	@RequestMapping("withdrawalsDtail")
	public String withdrawalsDtail(String payNo, Model model) {
		model.addAttribute("payNo", payNo);
		return "/front/trade/query/withdrawals_receipt.vm";
	}
	
	/**
	 * 下载或预览划出凭据
	 * @param payNo(划出流水号)
	 * @param downType(downLoad-下载)
	 * @param response
	 * @param request
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
	@RequestMapping("downLoadWithdrawals/{payNo}")
	public void downLoadWithdrawals(@PathVariable String payNo, String downType,
									HttpServletResponse response, HttpServletRequest request)
																								throws Exception {
		WithdrawQueryOrder queryOrder = new WithdrawQueryOrder();
		PageParam pageParam = new PageParam();
		pageParam.setPageNo(1);
		pageParam.setPageSize(10);
		
		queryOrder.setUserId(SessionLocalManager.getSessionLocal().getAccountId());
		queryOrder.setPayNo(payNo);
		WithdrawQueryResult queryResult = this.withdrawQueryService.queryWithdrawService(
			queryOrder, this.getOpenApiContext());
		List<QueryWithdrawInfo> infos = queryResult.getWithdrawInfos();
		if (infos != null && infos.size() > 0) {
			QueryWithdrawInfo withdrawalsInfo = infos.get(0);
			String servletPath = request.getServletContext().getRealPath("/");
			String filePath = servletPath + "/resources/pdf/withdrawalsDetail"
								+ System.currentTimeMillis() + ".pdf";
			String fileOutPath = servletPath + "/resources/pdf/withdrawals"
									+ System.currentTimeMillis() + ".pdf";
			String imagePath = servletPath + "/resources/themes/default/image/main_logo.gif";
			FileOutputStream fos = null;
			String extName = "划出凭据.pdf";
			Document doc = new Document(PageSize.A4, 20, 20, 140, 20);
			PdfReader reader = null;
			fos = new FileOutputStream(filePath);
			try {
				PdfWriter writer = PdfWriter.getInstance(doc, fos);
				doc.open();
				// 解决中文问题  
				BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H",
					BaseFont.NOT_EMBEDDED);
				Font titleChinese = new Font(bfChinese, 20, Font.BOLD); // 模板抬头的字体     
				Paragraph title = new Paragraph("划出凭据", titleChinese);// 抬头
				title.setAlignment(Element.ALIGN_CENTER); // 居中设置
				title.setLeading(1f);//设置行间距//设置上面空白宽度
				doc.add(title);
				
				Font fontZH = new Font(bfChinese, 12, Font.NORMAL);
				float[] widths = { 20f, 30f, 20f, 25f };
				PdfPTable table = new PdfPTable(widths);
				table.setSpacingBefore(20f);// 设置表格上面空白宽度
				table.setTotalWidth(400);// 设置表格的宽度
				table.setWidthPercentage(100);//设置表格宽度为%100
				PdfPCell cell;
				
				cell = new PdfPCell(new Paragraph("平台服务商：", fontZH));
				cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
				table.addCell(cell);
				cell = new PdfPCell(new Paragraph("重庆" + AppConstantsUtil.getProductName()
													+ "金融服务有限公司", fontZH));
				table.addCell(cell);
				
				cell = new PdfPCell(new Paragraph("业务类别：", fontZH));
				cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
				table.addCell(cell);
				cell = new PdfPCell(new Paragraph("", fontZH));
				table.addCell(cell);
				
				cell = new PdfPCell(new Paragraph("支付服务商：", fontZH));
				cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
				table.addCell(cell);
				cell = new PdfPCell(new Paragraph("重庆易极付科技有限公司", fontZH));
				table.addCell(cell);
				
				cell = new PdfPCell(new Paragraph("客服电话：", fontZH));
				cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
				table.addCell(cell);
				cell = new PdfPCell(new Paragraph("18502324357", fontZH));
				table.addCell(cell);
				
				cell = new PdfPCell(new Paragraph("划出金额(元)：", fontZH));
				cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
				table.addCell(cell);
				cell = new PdfPCell(new Paragraph(withdrawalsInfo.getAmountIn().toString(), fontZH));
				table.addCell(cell);
				
				cell = new PdfPCell(new Paragraph("手续费(元)：", fontZH));
				cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
				table.addCell(cell);
				cell = new PdfPCell(new Paragraph(withdrawalsInfo.getCharge().toString(), fontZH));
				table.addCell(cell);
				
				cell = new PdfPCell(new Paragraph("划出使用的银行：", fontZH));
				cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
				table.addCell(cell);
				cell = new PdfPCell(new Paragraph(withdrawalsInfo.getBankName(), fontZH));
				table.addCell(cell);
				
				cell = new PdfPCell(new Paragraph("划出使用的卡号：", fontZH));
				cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
				table.addCell(cell);
				cell = new PdfPCell(new Paragraph(withdrawalsInfo.getBankAccountNo(), fontZH));
				table.addCell(cell);
				
				cell = new PdfPCell(new Paragraph(AppConstantsUtil.getProductName() + "金融划出信息",
					fontZH));
				cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
				cell.setColspan(2);
				table.addCell(cell);
				
				cell = new PdfPCell(new Paragraph("易极付提现信息", fontZH));
				cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
				cell.setColspan(2);
				table.addCell(cell);
				
				Paragraph iparas = new Paragraph("用户名："
													+ SessionLocalManager.getSessionLocal()
														.getUserName(), fontZH);
				iparas.add(Chunk.NEWLINE);
				iparas.add("划出流水号：" + withdrawalsInfo.getOutBizNo());
				iparas.add(Chunk.NEWLINE);
				SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				iparas.add("申请划出时间：" + simple.format(withdrawalsInfo.getPayTime()));
				iparas.add(Chunk.NEWLINE);
				
				cell = new PdfPCell(iparas);
				cell.setColspan(2);
				cell.setRowspan(3);
				table.addCell(cell);
				
				Paragraph paras = new Paragraph("账户名：" + withdrawalsInfo.getAccountName(), fontZH);
				paras.add(Chunk.NEWLINE);
				paras.add("提现流水号：" + withdrawalsInfo.getPayNo());
				paras.add(Chunk.NEWLINE);
				paras.add("提现成功时间：" + simple.format(withdrawalsInfo.getPayTime()));
				paras.add(Chunk.NEWLINE);
				
				cell = new PdfPCell(paras);
				cell.setColspan(2);
				cell.setRowspan(3);
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
												+ AppConstantsUtil.getPlatformName() + "进行人工盖章",
					fontZH);// 抬头
				tips.setLeading(1f);//设置行间距//设置上面空白宽度
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
				tips = new Paragraph("  公司地址：重庆市北部新区青枫北路12号双子座B-1801", fontZH);// 抬头
				tips.setLeading(1f);//设置行间距//设置上面空白宽度
				doc.add(tips);
				doc.close();
				logger.info("创建文档完成");
				reader = new PdfReader(filePath, "yijiu".getBytes());//选择需要pdf
				fos = new FileOutputStream(fileOutPath);
				PdfStamper stamp = new PdfStamper(reader, fos);//加完图片后的pdf
				PdfContentByte over = stamp.getOverContent(1);//设置在第几页打印
				Image img = Image.getInstance(imagePath);//选择图片
				img.setAlignment(1);
				img.scaleAbsolute(70, 20);//控制图片大小
				img.setAbsolutePosition(135, 700);//控制图片位置
				over.addImage(img);
				stamp.close();
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			} finally {
				if (reader != null) {
					reader.close();
				}
				if (fos != null) {
					fos.close();
				}
			}
			CommonUtil.downLoadOrPreview(request, response, downType, fileOutPath, extName);
			File file = new File(fileOutPath);
			file.delete();
			File fileInit = new File(filePath);
			fileInit.delete();
		}
		
	}
	
	/**
	 * 下载投资接受费用凭据
	 * @param demandId
	 * @param downType
	 * @param response
	 * @param request
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
	@RequestMapping("downLoadLoanRecipt/{demandId}")
	public void downLoadLoanRecipt(@PathVariable long demandId, String downType,
									HttpServletResponse response, HttpServletRequest request)
	
	throws Exception {
		LoanDemandInfo loanDemand = loanDemandQueryService.loadLoanDemandInfo(demandId);
		TradeInfo trade = loanDemand.getTradeInfo();
		UserInfo loaner = userQueryService.queryByUserId(loanDemand.getLoanerId())
			.getQueryUserInfo();
		String loanerCertNo = "";
		if (loaner.getType() == UserTypeEnum.JG) {
			InstitutionsInfo institution = userQueryService.queryInstitutionsInfoByBaseId(
				loaner.getUserBaseId()).getQueryInstitutionsInfo();
			loanerCertNo = institution.getLegalRepresentativeCardNo();
		} else {
			PersonalInfo loanerPerson = userQueryService.queryPersonalInfoByBaseId(
				loaner.getUserBaseId()).getQueryPersonalInfo();
			loanerCertNo = loanerPerson.getCertNo();
		}
		String timeLimit = "";
		if ("W".equals(loanDemand.getTimeLimitUnit()) || "M".equals(loanDemand.getTimeLimitUnit())) {
			timeLimit = loanDemand.getTimeLimit() + "个月";
		} else if ("Y".equals(loanDemand.getTimeLimitUnit())) {
			timeLimit = loanDemand.getTimeLimit() + "年";
		} else {
			timeLimit = loanDemand.getTimeLimit() + "天";
		}
		//资金计算map
		TradeAmountInfo amountMap = tradeBizQueryService.queryAmountRoleByTradeId(trade
			.getTradeId());
		
		String loanFlowCode = "";
		
		QueryTradeDetailUserOrder queryTradeDetailUserOrder = new QueryTradeDetailUserOrder();
		queryTradeDetailUserOrder.setRoleEnum(SysUserRoleEnum.LOANER);
		queryTradeDetailUserOrder.setTradeId(trade.getTradeId());
		QueryBaseBatchResult<TradeDetailInfo> detail1s = tradeBizQueryService
			.queryTradeDetailUser(queryTradeDetailUserOrder);
		
		if (ListUtil.isNotEmpty(detail1s.getPageList())) {
			TradeFlowCodeInfo tradeFlow = tradeFlowCodeManager
				.queryInvestFlowCodesByTradeDetailId(detail1s.getPageList().get(0)
					.getTradeDetailId());
			if (tradeFlow != null) {
				loanFlowCode = tradeFlow.getTradeFlowCode();
			}
		}
		//		if ("".equals(loanFlowCode)) {
		//			String str = investFlowCodes.get(0).get("TRLOWNO").toString();
		//			loanFlowCode = str.substring(0, str.length() - 4) + "R";
		//		}
		double guaranteePct = loanDemand.getGuaranteePct().doubleValue();
		double brokerPct = loanDemand.getBrokerInterest().doubleValue();
		double marketingPct = loanDemand.getMarketingPct().doubleValue();
		double plateformPct = loanDemand.getPlateformPct().doubleValue();
		double investPct = new BigDecimal(loanDemand.getInterestRate()).multiply(
			new BigDecimal(100)).doubleValue();
		
		String servletPath = request.getServletContext().getRealPath("/");
		String filePath = servletPath + "/resources/pdf/loanReciptInit"
							+ System.currentTimeMillis() + ".pdf";
		String fileOutPath = servletPath + "/resources/pdf/loanRecipt" + System.currentTimeMillis()
								+ ".pdf";
		String imagePath = servletPath + "/resources/themes/default/image/main_logo.gif";
		FileOutputStream fos = null;
		String extName = "投资接收人相关费用凭据.pdf";
		Document doc = new Document(PageSize.A4, 20, 20, 140, 20);
		PdfReader reader = null;
		fos = new FileOutputStream(filePath);
		try {
			PdfWriter writer = PdfWriter.getInstance(doc, fos);
			doc.open();
			// 解决中文问题  
			BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H",
				BaseFont.NOT_EMBEDDED);
			Font titleChinese = new Font(bfChinese, 20, Font.BOLD); // 模板抬头的字体     
			Paragraph title = new Paragraph("投资接受人相关费用凭据", titleChinese);// 抬头
			title.setAlignment(Element.ALIGN_CENTER); // 居中设置
			title.setLeading(1f);//设置行间距//设置上面空白宽度
			doc.add(title);
			
			Font fontZH = new Font(bfChinese, 12, Font.NORMAL);
			float[] widths = { 20f, 30f, 25f, 25f };
			PdfPTable table = new PdfPTable(widths);
			table.setSpacingBefore(20f);// 设置表格上面空白宽度
			table.setTotalWidth(300);// 设置表格的宽度
			table.setWidthPercentage(100);//设置表格宽度为%100
			PdfPCell cell;
			
			cell = new PdfPCell(new Paragraph("平台服务商", fontZH));
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			table.addCell(cell);
			cell = new PdfPCell(new Paragraph(AppConstantsUtil.getPlatformName(), fontZH));
			table.addCell(cell);
			
			cell = new PdfPCell(new Paragraph("业务类别", fontZH));
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			table.addCell(cell);
			cell = new PdfPCell(new Paragraph("", fontZH));
			table.addCell(cell);
			cell = new PdfPCell(new Paragraph("投资接受金额(元)", fontZH));
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			table.addCell(cell);
			cell = new PdfPCell(
				new Paragraph(loanDemand.getLoanAmount().toStandardString(), fontZH));
			table.addCell(cell);
			cell = new PdfPCell(new Paragraph("投资期限", fontZH));
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			table.addCell(cell);
			cell = new PdfPCell(new Paragraph(timeLimit, fontZH));
			table.addCell(cell);
			cell = new PdfPCell(new Paragraph("投资接受人用户名", fontZH));
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			table.addCell(cell);
			cell = new PdfPCell(new Paragraph(loaner.getUserName(), fontZH));
			table.addCell(cell);
			cell = new PdfPCell(new Paragraph("身份证号", fontZH));
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			table.addCell(cell);
			cell = new PdfPCell(new Paragraph(loanerCertNo, fontZH));
			table.addCell(cell);
			cell = new PdfPCell(new Paragraph("真实姓名", fontZH));
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			table.addCell(cell);
			cell = new PdfPCell(new Paragraph(loaner.getRealName(), fontZH));
			table.addCell(cell);
			cell = new PdfPCell(new Paragraph("应付总计(元)", fontZH));
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			table.addCell(cell);
			cell = new PdfPCell(new Paragraph(amountMap.getTotalPayAmount().toStandardString(),
				fontZH));
			table.addCell(cell);
			cell = new PdfPCell(new Paragraph("成立日", fontZH));
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			table.addCell(cell);
			cell = new PdfPCell(new Paragraph(DateUtil.simpleFormat(trade.getTradeEffectiveDate()),
				fontZH));
			table.addCell(cell);
			cell = new PdfPCell(new Paragraph("兑付日", fontZH));
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			table.addCell(cell);
			cell = new PdfPCell(new Paragraph(DateUtil.simpleFormat(trade.getTradeExpireDate()),
				fontZH));
			table.addCell(cell);
			cell = new PdfPCell(new Paragraph("担保公司", fontZH));
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			table.addCell(cell);
			cell = new PdfPCell(new Paragraph(loanDemand.getGuaranteeName(), fontZH));
			cell.setColspan(3);
			table.addCell(cell);
			cell = new PdfPCell(new Paragraph("投资接受流水号", fontZH));
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			table.addCell(cell);
			cell = new PdfPCell(new Paragraph(loanFlowCode, fontZH));
			cell.setColspan(3);
			table.addCell(cell);
			
			cell = new PdfPCell(new Paragraph("投资接受成本组成", fontZH));
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell.setColspan(2);
			table.addCell(cell);
			cell = new PdfPCell(new Paragraph("投资接受成本明细", fontZH));
			cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell.setColspan(2);
			table.addCell(cell);
			Paragraph iparas = new Paragraph("担保费(年化)：" + guaranteePct + "%", fontZH);
			iparas.add(Chunk.NEWLINE);
			iparas.add("平台费(年化)：" + plateformPct + "%");
			iparas.add(Chunk.NEWLINE);
			iparas.add("营销费用(年化)：" + (marketingPct + brokerPct) + "%");
			iparas.add(Chunk.NEWLINE);
			iparas.add("投资人收益(年化)：" + investPct + "%");
			cell = new PdfPCell(iparas);
			cell.setColspan(2);
			cell.setRowspan(5);
			table.addCell(cell);
			Paragraph iparasfee = new Paragraph("担保费(元)："
												+ amountMap.getPayGuaranteeAmount()
													.toStandardString(), fontZH);
			iparasfee.add(Chunk.NEWLINE);
			iparasfee.add("平台费(元)：" + amountMap.getPayPlateformAmount().toStandardString());
			iparasfee.add(Chunk.NEWLINE);
			iparasfee.add("营销费用(元)："
							+ (amountMap.getPayBrokerAmount().add(
								amountMap.getPayMarkettingAmount()).add(amountMap
								.getPayInvestorAmountSend())).toStandardString());
			iparasfee.add(Chunk.NEWLINE);
			iparasfee.add("投资人收益(元)：" + amountMap.getPayInvestorAmount().toStandardString());
			iparasfee.add(Chunk.NEWLINE);
			iparasfee.add("其它费用(元)：" + "暂无");
			cell = new PdfPCell(iparasfee);
			cell.setColspan(2);
			cell.setRowspan(5);
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
			tips.setLeading(1f);//设置行间距//设置上面空白宽度
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
			tips.setLeading(1f);//设置行间距//设置上面空白宽度
			doc.add(tips);
			doc.close();
			logger.info("创建文档完成");
			reader = new PdfReader(filePath, "yrd".getBytes());//选择需要pdf
			fos = new FileOutputStream(fileOutPath);
			PdfStamper stamp = new PdfStamper(reader, fos);//加完图片后的pdf
			PdfContentByte over = stamp.getOverContent(1);//设置在第几页打印
			Image img = Image.getInstance(imagePath);//选择图片
			img.setAlignment(1);
			img.scaleAbsolute(70, 20);//控制图片大小
			img.setAbsolutePosition(100, 700);//控制图片位置
			over.addImage(img);
			stamp.close();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			if (reader != null) {
				reader.close();
			}
			if (fos != null) {
				fos.close();
			}
		}
		CommonUtil.downLoadOrPreview(request, response, downType, fileOutPath, extName);
		File file = new File(fileOutPath);
		file.delete();
		File fileInit = new File(filePath);
		fileInit.delete();
		
	}
}
