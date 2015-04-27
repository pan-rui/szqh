package com.yjf.yrd.backstage.controller.bankInfo;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.yjf.common.lang.util.StringUtil;
import com.yjf.yrd.base.BaseAutowiredController;
import com.yjf.yrd.dataobject.BankInfo;
import com.yjf.yrd.page.Page;
import com.yjf.yrd.page.PageParam;
import com.yjf.yrd.page.PageParamUtil;
import com.yjf.yrd.util.AppConstantsUtil;
import com.yjf.yrd.web.util.AttachmentModuleType;
import com.yjf.yrd.web.util.YrdEnumUtil;
import com.yjf.yrd.ws.base.info.BankBasicInfo;
import com.yjf.yrd.ws.enums.CommonAttachmentTypeEnum;
import com.yjf.yrd.ws.info.CommonAttachmentInfo;
import com.yjf.yrd.ws.order.CommonAttachmentOrder;
import com.yjf.yrd.ws.order.CommonAttachmentQueryOrder;
import com.yjf.yrd.ws.result.YrdBaseResult;
import com.yjf.yrd.ws.service.CommonAttachmentService;
import com.yjf.yrd.ws.service.query.result.QueryBaseBatchResult;

@Controller
@RequestMapping("backstage")
public class BankInfoController extends BaseAutowiredController {
	/** 页面所在路径 */
	private final String	_PATH	= "/backstage/bank/";
	@Autowired
	CommonAttachmentService commonAttachmentService;
	
	
	
	@RequestMapping(value = "bankInfoManage/add_vm")
	public String add_vm(Model model) {
		List<BankBasicInfo> bankList = this.bankDataService.getBankBasicInfos();//获取所有银行
		model.addAttribute("bankList", bankList);
		String s = "bankInfo.vm";
		return _PATH + s;
	}
	
	@RequestMapping(value = "uploadImages2Front")
	public String uploadImages2Front(Model model) {
		String uploadHost = AppConstantsUtil.getYrdUploadFolder();
		model.addAttribute("uploadHost", "");
		List<CommonAttachmentTypeEnum> enumList = YrdEnumUtil.getAttachmentByIndustry();
		model.addAttribute("enumlist", enumList);
		// 前台首页图片
		CommonAttachmentQueryOrder attachmentQueryOrder = new CommonAttachmentQueryOrder();
		attachmentQueryOrder.setBizNo("0000");
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
		return "/backstage/upload/uploadFile.vm";
	}
	
	@ResponseBody
	@RequestMapping("updateFontImg")
	public Object updateImgUrlA(HttpServletResponse response, HttpServletRequest request
								) throws Exception {
		
		YrdBaseResult baseResult = addAttachfile("0000", request);
		
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
	
	
	
	
	@ResponseBody
	@RequestMapping(value = "bankInfoManage/addBankInfoSubmit")
	public Object addBankInfoSubmit(BankInfo bankInfo, Model model) {
		JSONObject json = new JSONObject();
		if (bankInfo != null) {
			try {
				bankBaseService.addBank(bankInfo);
				json.put("code", 1);
				json.put("message", "保存成功");
			} catch (Exception e) {
				logger.error("保存银行信息失败", e);
				json.put("code", 0);
				json.put("message", "保存失败");
			}
			
		}
		return json;
	}
	
	@RequestMapping(value = "bankInfoManage")
	public String getBankInfoList(BankInfo bankInfo, Model model, PageParam pageParam) {
		try {
			List<BankInfo> list = new ArrayList<BankInfo>();
			long totalSize = bankBaseService.queryCountByConditions(bankInfo);
			int start = PageParamUtil.startValue((int) totalSize, pageParam.getPageSize(),
				pageParam.getPageNo());
			list = bankBaseService.queryByConditions(bankInfo, pageParam);
			Page<BankInfo> bankInfoList = new Page<BankInfo>(start, totalSize,
				pageParam.getPageSize(), list);
			model.addAttribute("page", bankInfoList);
		} catch (Exception e) {
			logger.error("查询银行异常", e);
		}
		
		return _PATH + "bankInfoList.vm";
	}
	
	@RequestMapping(value = "bankInfoManage/getBankInfoById")
	public String getBankInfoById(long bankId, Model model, String opt) {
		try {
			BankInfo info = new BankInfo();
			info.setBankId(bankId);
			List<BankBasicInfo> bankList = this.bankDataService.getBankBasicInfos();//获取所有银行
			model.addAttribute("bankList", bankList);
			List<BankInfo> list = bankBaseService.queryByConditions(info, null);
			if (list != null && list.size() > 0) {
				BankInfo bankInfo = list.get(0);
				model.addAttribute("opt", opt);
				model.addAttribute("bankInfo", bankInfo);
			} else {
				return _PATH;
			}
		} catch (Exception e) {
			logger.error("查询银行信息失败", e);
			return _PATH;
		}
		return _PATH + "updateBankInfo.vm";
	}
	
	@ResponseBody
	@RequestMapping(value = "bankInfoManage/updateBankInfoById")
	public Object updateBankInfoById(BankInfo bankInfo, Model model) {
		JSONObject json = new JSONObject();
		try {
			int size = bankBaseService.updateBank(bankInfo);
			if (size > 0) {
				json.put("code", 1);
				json.put("message", "更新银行信息成功");
			} else {
				json.put("code", 0);
				json.put("message", "更新银行信息失败");
			}
		} catch (Exception e) {
			logger.error("更新银行信息失败", e);
			json.put("code", 0);
			json.put("message", "更新银行信息失败");
		}
		return json;
		
	}
	
	@ResponseBody
	@RequestMapping(value = "bankInfoManage/deleteBankInfoById")
	public Object deleteBankInfoById(long bankId, Model model) {
		JSONObject json = new JSONObject();
		try {
			int size = bankBaseService.deleteBankInfoById(bankId);
			if (size > 0) {
				json.put("code", 1);
				json.put("message", "删除银行信息成功");
			} else {
				json.put("code", 0);
				json.put("message", "删除银行信息失败");
			}
		} catch (Exception e) {
			logger.error("删除银行信息失败", e);
			json.put("code", 0);
			json.put("message", "删除银行信息失败");
		}
		return json;
		
	}
	
}