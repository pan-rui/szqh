package com.yjf.yrd.front.controller.trade.download;

import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.yjf.yrd.service.trade.download.PDFParse;
import com.yjf.yrd.ws.base.info.BankBasicInfo;

public class PDFParseThread extends Thread {
	
	private HttpServletResponse response;
	private HttpSession session;
	private ContractData contractData;
	private List<BankBasicInfo> bankBasicInfos;
	private String pdfFileCode;
	private String downType;
	
	@Override
	public void run() {
		PDFParse pdf = new PDFParse();
		//pdf.writPDF(response, session, contractData, bankBasicInfos, pdfFileCode, downType);
	}
	
	public HttpServletResponse getResponse() {
		return response;
	}
	
	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}
	
	public HttpSession getSession() {
		return session;
	}
	
	public void setSession(HttpSession session) {
		this.session = session;
	}
	
	public ContractData getContractData() {
		return contractData;
	}
	
	public void setContractData(ContractData contractData) {
		this.contractData = contractData;
	}
	
	public List<BankBasicInfo> getBankBasicInfos() {
		return bankBasicInfos;
	}
	
	public void setBankBasicInfos(List<BankBasicInfo> bankBasicInfos) {
		this.bankBasicInfos = bankBasicInfos;
	}
	
	public String getPdfFileCode() {
		return pdfFileCode;
	}
	
	public void setPdfFileCode(String pdfFileCode) {
		this.pdfFileCode = pdfFileCode;
	}
	
	public String getDownType() {
		return downType;
	}
	
	public void setDownType(String downType) {
		this.downType = downType;
	}
	
}
