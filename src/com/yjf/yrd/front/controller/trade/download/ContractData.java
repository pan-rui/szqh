package com.yjf.yrd.front.controller.trade.download;

import java.util.List;
import java.util.Map;

import com.yjf.yrd.service.trade.download.Text;
import com.yjf.yrd.user.info.InstitutionsInfo;
import com.yjf.yrd.ws.info.LoanDemandInfo;

public class ContractData {
	
	private LoanDemandInfo loanDemandInfo;
	
	//融资流水号
	private String loanFlowCode;
	
	//担保合同号
	private String guaranteeContractCode;
	
	//担保机构
	private InstitutionsInfo institutionsInfo;
	
	//投资流水
	private List<Map<String, Text>> investFlowCodes;
	
	public List<Map<String, Text>> getInvestFlowCodes() {
		return this.investFlowCodes;
	}
	
	public void setInvestFlowCodes(List<Map<String, Text>> investFlowCodes) {
		this.investFlowCodes = investFlowCodes;
	}
	
	public LoanDemandInfo getLoanDemandInfo() {
		return this.loanDemandInfo;
	}
	
	public void setLoanDemandInfo(LoanDemandInfo loanDemandInfo) {
		this.loanDemandInfo = loanDemandInfo;
	}
	
	public String getLoanFlowCode() {
		return this.loanFlowCode;
	}
	
	public void setLoanFlowCode(String loanFlowCode) {
		this.loanFlowCode = loanFlowCode;
	}
	
	public String getGuaranteeContractCode() {
		return this.guaranteeContractCode;
	}
	
	public void setGuaranteeContractCode(String guaranteeContractCode) {
		this.guaranteeContractCode = guaranteeContractCode;
	}
	
	public InstitutionsInfo getInstitutionsInfo() {
		return this.institutionsInfo;
	}
	
	public void setInstitutionsInfo(InstitutionsInfo institutionsInfo) {
		this.institutionsInfo = institutionsInfo;
	}
	
}
