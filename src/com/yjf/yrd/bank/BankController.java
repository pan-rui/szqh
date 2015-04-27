package com.yjf.yrd.bank;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.yjf.common.lang.context.OperationContext;
import com.yjf.yrd.base.BaseAutowiredController;
import com.yjf.yrd.service.openingbank.info.BankInfo;
import com.yjf.yrd.service.openingbank.info.ProvinceInfo;
import com.yjf.yrd.service.openingbank.order.OpeningBankQueryByDistrictOrder;
import com.yjf.yrd.ws.base.info.BankBasicInfo;

@Controller
@RequestMapping("bank")
public class BankController extends BaseAutowiredController {
	/** 查询所有银行渠道信息 */
	@ResponseBody
	@RequestMapping("getAllBank")
	public Object getAllBank() {
		JSONObject jsonobj = new JSONObject();
		List<BankBasicInfo> bankBasicInfos = this.bankDataService.getBankBasicInfos();
		if (bankBasicInfos.size() <= 0) {
			jsonobj.put("code", 0);
			jsonobj.put("data", bankBasicInfos);
		} else {
			jsonobj.put("code", 1);
			jsonobj.put("data", bankBasicInfos);
		}
		return jsonobj;
	}
	
	/** 查询所有省、市 */
	@ResponseBody
	@RequestMapping("getAllDistrict")
	public Object getAllDistrict() {
		logger.debug("【查询所有省、市】");
		JSONObject jsonobj = new JSONObject();
		List<ProvinceInfo> provinceList = openingBankService.getAllDistrict(new OperationContext())
			.getProvinceInfos();
		
		if (provinceList.size() <= 0) {
			jsonobj.put("code", 0);
			jsonobj.put("data", provinceList);
		} else {
			jsonobj.put("code", 1);
			jsonobj.put("data", provinceList);
		}
		return jsonobj;
	}
	
	/** 根据地区号和银行编号查询支行信息 */
	@ResponseBody
	@RequestMapping("getBankBranch")
	public Object getBankBranch(String bankId, String districtNo, ModelMap modelMap) {
		JSONObject jsonobj = new JSONObject();
		OpeningBankQueryByDistrictOrder order = new OpeningBankQueryByDistrictOrder();
		order.setBankId(bankId);
		order.setDistrictNo(districtNo);
		List<BankInfo> branchList = openingBankService.findBankInfoByDistrictNo(order,
			new OperationContext()).getBankInfos();
		if (branchList == null || branchList.size() <= 0) {
			jsonobj.put("code", 0);
			jsonobj.put("data", branchList);
		} else {
			jsonobj.put("code", 0);
			jsonobj.put("data", branchList);
		}
		return jsonobj;
	}
}
