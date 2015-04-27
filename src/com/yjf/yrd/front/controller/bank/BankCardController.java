package com.yjf.yrd.front.controller.bank;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.yjf.common.lang.util.StringUtil;
import com.yjf.yrd.base.BaseAutowiredController;
import com.yjf.yrd.service.openingbank.enums.CardTypeEnum;
import com.yjf.yrd.service.openingbank.order.OpeningBankQueryOrder;
import com.yjf.yrd.service.openingbank.result.BankInfoResult;
import com.yjf.yrd.session.SessionLocalManager;
import com.yjf.yrd.user.info.InstitutionsInfo;
import com.yjf.yrd.user.info.PersonalInfo;
import com.yjf.yrd.user.info.UserInfo;
import com.yjf.yrd.util.YrdConstants;
import com.yjf.yrd.ws.base.info.BankBasicInfo;
import com.yjf.yrd.ws.enums.UserTypeEnum;

/**
 * @Filename BankCardController.java
 * @Description
 * @Version 1.0
 * @Author zjl
 * @Email zjialin@yiji.com
 * @History <li>Author: zjl</li> <li>Date: 2013-8-19</li> <li>Version: 1.0</li>
 * <li>Content: create</li>
 */
@Controller
@RequestMapping("bank")
public class BankCardController extends BaseAutowiredController {
	private final String vm_path = "/front/bank/";
	
	@RequestMapping("signBankCard")
	public String signBankCard(Model model) throws Exception {
		if (SessionLocalManager.getSessionLocal() == null) {
			return "/help/nopermission";
		}
		return vm_path + "signBankCard.vm";
	}
	
	@RequestMapping("addBankCard")
	public String addBankCard(String signed, Model model) throws Exception {
		if (SessionLocalManager.getSessionLocal() == null) {
			return "/help/nopermission";
		}
		if (!"yes".equals(signed)) {
			model.addAttribute("fail", "未签约");
			return vm_path + "signBankCard.vm";
		}
		
		List<BankBasicInfo> bankList = new ArrayList<BankBasicInfo>();
		List<BankBasicInfo> bankListOpenApi = this.bankDataService.getDeductBank();
		if (bankListOpenApi != null && bankListOpenApi.size() > 0) {
			List<String> listBankCode = new ArrayList<String>();
			listBankCode.add("ABC");//农业银行
			listBankCode.add("ICBC");//中国工商银行
			listBankCode.add("CCB");//中国建设银行
			listBankCode.add("CEB");//中国光大银行
			listBankCode.add("CIB");//兴业银行
			listBankCode.add("CMBC");//民生银行
			listBankCode.add("CITIC");//中信银行
			listBankCode.add("CMB");//招商银行
			for (BankBasicInfo bank : bankListOpenApi) {
				if (listBankCode.contains(bank.getBankCode())) {
					bankList.add(bank);
				}
			}
		} else {
			List<BankBasicInfo> bankListPPM = this.bankDataService.getBankBasicInfos();//获取所有银行
			List<String> listBankCode = new ArrayList<String>();
			listBankCode.add("ABC");//农业银行
			listBankCode.add("ICBC");//中国工商银行
			listBankCode.add("CCB");//中国建设银行
			listBankCode.add("CEB");//中国光大银行
			listBankCode.add("CIB");//兴业银行
			listBankCode.add("CMBC");//民生银行
			listBankCode.add("CITIC");//中信银行
			listBankCode.add("CMB");//招商银行
			for (BankBasicInfo bank : bankListPPM) {
				if (listBankCode.contains(bank.getBankCode())) {
					bankList.add(bank);
				}
			}
		}
		
		String userBaseId = SessionLocalManager.getSessionLocal().getUserBaseId();
		UserInfo userBaseInfo = userQueryService.queryByUserBaseId(userBaseId).getQueryUserInfo();
		PersonalInfo personalInfo = null;
		InstitutionsInfo institutionsInfo = null;
		if (userBaseInfo.getType() == UserTypeEnum.GR) {
			try {
				personalInfo = userQueryService.queryPersonalInfoByBaseId(userBaseId)
					.getQueryPersonalInfo();
			} catch (Exception e) {
				logger.error("查询用户信息失败", e);
			}
			model.addAttribute("realName", personalInfo.getRealName());
			
			model.addAttribute("type", "GR");
			
		} else {
			institutionsInfo = userQueryService.queryInstitutionsInfoByBaseId(userBaseId)
				.getQueryInstitutionsInfo();
			model.addAttribute("realName", institutionsInfo.getEnterpriseName());
			model.addAttribute("type", "JG");
		}
		model.addAttribute("bankList", bankList);
		return vm_path + "addBankCard.vm";
	}
	
	@RequestMapping("addBankCardSubmit")
	public String addBankCardSubmit(String bankOpenName, String bankCardNo, String bankType,
									String bankKey, String bankProvince, String bankCity,
									String bankAddress, Model model) throws Exception {
		if (SessionLocalManager.getSessionLocal() == null) {
			return "/help/nopermission";
		}
		if (StringUtil.isEmpty(bankOpenName) || StringUtil.isEmpty(bankCardNo)
			|| StringUtil.isEmpty(bankType) || StringUtil.isEmpty(bankProvince)
			|| StringUtil.isEmpty(bankCity)) {
			model.addAttribute("reason", "绑定银行卡信息不全，请检查重新填写！");
			return vm_path + "addBankCardFail.vm";
		}
		String userBaseId = SessionLocalManager.getSessionLocal().getUserBaseId();
		UserInfo userBaseInfo = userQueryService.queryByUserBaseId(userBaseId).getQueryUserInfo();
		PersonalInfo personalInfo = null;
		InstitutionsInfo institutionsInfo = null;
		boolean bool = false;
		if (userBaseInfo.getType() == UserTypeEnum.GR) {
			personalInfo = userQueryService.queryPersonalInfoByBaseId(userBaseId)
				.getQueryPersonalInfo();
			if (personalInfo.getCertNo() == null || "".equals(personalInfo.getCertNo())) {
				model.addAttribute("reason", "处理绑卡失败,未进行实名认证申请！");
				return vm_path + "addBankCardFail.vm";
			}
			if (YrdConstants.BankCodes.filteredBankCodes.indexOf(bankType) < 0) {
				OpeningBankQueryOrder queryOrder = new OpeningBankQueryOrder();
				queryOrder.setCardNumber(bankCardNo);
				queryOrder.setCardNumber(bankCardNo);
				queryOrder.setAccountName(bankOpenName);
				queryOrder.setBankCode(bankType);
				queryOrder.setCardType(CardTypeEnum.JJ);
				queryOrder.setCertNo(personalInfo.getCertNo());
				BankInfoResult bankInfoResult = openingBankService.findCardByCardNo(queryOrder,
					null);
				boolean isValidate = false;
				if (bankInfoResult.getCardInfo() != null) {
					if (bankInfoResult.getCardInfo().getCardType() == CardTypeEnum.JJ) {
						isValidate = true;
					}
				}
				if (!isValidate) {
					logger.error("绑定银行卡失败原因:" + bankInfoResult);
					model.addAttribute("reason", "验证银行卡信息失败！");
					return vm_path + "addBankCardFail.vm";
				}
			}
		} else {
			institutionsInfo = userQueryService.queryInstitutionsInfoByBaseId(userBaseId)
				.getQueryInstitutionsInfo();
		}
		
		if (StringUtil.isNotBlank(bankCardNo)) {
			model.addAttribute("bankCardNo",
				bankCardNo.substring(bankCardNo.length() - 4, bankCardNo.length()));
		}
		if (bool) {
			String sign = this.sign(userBaseInfo.getAccountId(), bankType, bankCardNo,
				bankOpenName, userBaseInfo.getRealName(), userBaseInfo.getMobile());
			//            return "redirect:" + sign;
			return vm_path + "addBankCardSuccess.vm";
		} else {
			model.addAttribute("reason", "处理绑卡失败！");
			return vm_path + "addBankCardFail.vm";
		}
	}
	
	private String sign(String userId, String bankNo, String cardNo, String cardName,
						String certNo, String userPhoneNo) {
		StringBuilder builder = new StringBuilder();
		builder.append("http://192.168.45.210:8099/sign/mayBank.html");
		builder.append("?upUserNo=").append("yzz");
		builder.append("&userId=").append(userId);
		builder.append("&unionBusinessNo=").append("easy_trade-cqyy");
		builder.append("&protocalName=").append("《易周转用户协议》");
		builder.append("&protocalUrl=").append("http://127.0.0.1:8083/userManage/userHome");
		builder.append("&bankNo=").append(bankNo);
		builder.append("&cardNo=").append(cardNo);
		builder.append("&cardName=").append(cardName);
		builder.append("&certNo=").append(certNo);
		builder.append("&userPhoneNo=").append(userPhoneNo);
		return builder.toString();
	}
}
