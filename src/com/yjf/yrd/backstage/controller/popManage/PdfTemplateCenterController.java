package com.yjf.yrd.backstage.controller.popManage;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.yjf.yrd.base.BaseAutowiredController;
import com.yjf.yrd.page.PageParam;
import com.yjf.yrd.pdf.template.DemandPdfTemplateService;
import com.yjf.yrd.pdf.template.PdfTemplateService;
import com.yjf.yrd.service.certificate.CertificateService;
import com.yjf.yrd.service.trade.download.ContractData;
import com.yjf.yrd.service.trade.download.PDFParse;
import com.yjf.yrd.ws.enums.PDFTypeCodeEnum;
import com.yjf.yrd.ws.info.DemandPdfTemplateInfo;
import com.yjf.yrd.ws.order.PdfTemplateOrder;

@Controller
@RequestMapping("backstage")
public class PdfTemplateCenterController extends BaseAutowiredController {
	private final String VM_PATH = "/backstage/report/";
	@Autowired
	PdfTemplateService pdfTemplateService;
	@Autowired
	DemandPdfTemplateService demandPdfTemplateService;
	@Autowired
	CertificateService certificateService;
	
	@RequestMapping("pdftemplate")
	public String templateCenter(HttpSession session, PageParam pageParam, Model model) {
		Map<String, Object> conditions = new HashMap<String, Object>();
		model.addAttribute("page", pdfTemplateService.getPageByConditions(pageParam, conditions));
		return VM_PATH + "pdfTemplate-center.vm";
	}
	
	@RequestMapping("pdftemplate/addPdfTemplate")
	public String addPdfTemplate(HttpSession session, Model model) {
		List<PDFTypeCodeEnum> typeList = PDFTypeCodeEnum.getAllEnum();
		model.addAttribute("typeList", typeList);
		return VM_PATH + "add-pdf-template.vm";
	}
	
	@RequestMapping("pdftemplate/addPdfTemplateSubmit")
	@ResponseBody
	public Object addPdfTemplateSubmit(PdfTemplateOrder pdfTemplate, Model model) {
		JSONObject json = new JSONObject();
		try {
			pdfTemplate.setRawAddTime(new Date());
			pdfTemplateService.insertPdfTemplate(pdfTemplate);
			json.put("code", "1");
			json.put("message", "保存成功");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			json.put("code", "0");
			json.put("message", "保存失败");
		}
		return json;
	}
	
	@RequestMapping("pdftemplate/setPdfTemplate")
	public String setPdfTemplate(long templateId, Model model) {
		List<PDFTypeCodeEnum> typeList = PDFTypeCodeEnum.getAllEnum();
		model.addAttribute("typeList", typeList);
		model.addAttribute("info", pdfTemplateService.getById(templateId));
		return VM_PATH + "update-pdf-template.vm";
	}
	
	@RequestMapping("pdftemplate/delPdfTemplate")
	public String delPdfTemplate(long templateId, Model model, String redirect) {
		JSONObject json = new JSONObject();
		try {
			List<DemandPdfTemplateInfo> list = demandPdfTemplateService
				.geListByPdfTemplateId(templateId);
			
			if (list.size() > 0) {
				json.put("code", "0");
				json.put("message", "删除失败，该模板不可删除");
			} else {
				pdfTemplateService.deletePdfTemplateById(templateId);
				json.put("code", "1");
				json.put("message", "删除成功");
			}
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			json.put("code", "0");
			json.put("message", "删除失败");
		}
		return "redirect:" + redirect;
	}
	
	@RequestMapping("pdftemplate/setPdfTemplateSubmit")
	@ResponseBody
	public Object setPdfTemplateSubmit(PdfTemplateOrder template, Model model) {
		JSONObject json = new JSONObject();
		try {
			pdfTemplateService.updatePdfTemplate(template);
			json.put("code", "1");
			json.put("message", "保存成功");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			json.put("code", "0");
			json.put("message", "保存失败");
		}
		return json;
	}
	
	@RequestMapping("pdftemplate/previewPdf")
	public void previewPdf(HttpServletResponse response, HttpSession session, long templateId,
							Model model) throws Exception {
		//		model.addAttribute("info", pdfTemplateService.getById(templateId));
		
		String pdfFileCode = "_DB";
		String serverRealPath = session.getServletContext().getRealPath("/");
		long tradeId = -1;
		long detailId = -1;
		ContractData contractData = certificateService.getContractData(tradeId, detailId,
			templateId, pdfFileCode, serverRealPath, null);
		logger.debug("PDFParseThread begin>>>>");
		PDFParse pdf = new PDFParse();
		pdf.writPDF(response, contractData, "creatAndPrivew");
		
	}
	
}
