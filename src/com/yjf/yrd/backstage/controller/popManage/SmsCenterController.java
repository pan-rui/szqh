package com.yjf.yrd.backstage.controller.popManage;

import com.alibaba.fastjson.JSONObject;
import com.yjf.common.lang.util.StringUtil;
import com.yjf.yrd.base.BaseAutowiredController;
import com.yjf.yrd.integration.openapi.SMSService;
import com.yjf.yrd.page.PageParam;
import com.yjf.yrd.session.SessionLocal;
import com.yjf.yrd.session.SessionLocalManager;
import com.yjf.yrd.util.AppConstantsUtil;
import com.yjf.yrd.ws.order.OperationJournalAddOrder;
import com.yjf.yrd.ws.result.YrdBaseResult;
import com.yjf.yrd.ws.service.OperationJournalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("backstage")
public class SmsCenterController extends BaseAutowiredController {
	
	@Autowired
    SMSService smsService;
	
	@Autowired
    OperationJournalService operationLog;
	
	private final String VM_PATH = "/backstage/publicNotice/smsCenter/";
	
	@RequestMapping("smsCenter")
	public String noticeCenter(HttpSession session, PageParam pageParam, Model model) {
		
		return VM_PATH + "sms-center.vm";
	}
	
	@ResponseBody
	@RequestMapping(value = "smsCenter/doTest")
	public Object doTest(String testMobile, String smsContent) throws Exception {
		JSONObject jsonobj = new JSONObject();
		
		if (testMobile.length() != 11) {
			jsonobj.put("code", 0);
			jsonobj.put("message", "手机号码有误！");
			return jsonobj;
		}
		
		try {
			String[] moilesArray = testMobile.split(",");
			YrdBaseResult result = smsService.sendBroadcastSMS(moilesArray, smsContent);
			if (result.isSuccess()) {
				jsonobj.put("code", 1);
				jsonobj.put("message", "测试短信发送成功！");
			} else {
				jsonobj.put("code", 0);
				jsonobj.put("message", "测试短信发送失败！[" + result.getYrdResultEnum().code() + "]");
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			jsonobj.put("code", 0);
			jsonobj.put("message", "测试短信发送失败！");
		}
		return jsonobj;
	}
	
	@ResponseBody
	@RequestMapping(value = "smsCenter/doSend")
	public Object doBatchSend(String testMobile, String mobiles, String smsContent)
																					throws Exception {
		JSONObject jsonobj = new JSONObject();
		
		if (StringUtil.isEmpty(mobiles)) {
			jsonobj.put("code", 0);
			jsonobj.put("message", "请录入要正式发送的手机号！");
			return jsonobj;
		}
		
		String[] moilesArray = mobiles.split(",");
		
		try {
			YrdBaseResult result = smsService.sendBroadcastSMS(moilesArray, smsContent); 
			if (result.isSuccess()) {
				//添加日志
				SessionLocal local = SessionLocalManager.getSessionLocal();
				OperationJournalAddOrder operationJournalAddOrder = new OperationJournalAddOrder();
				operationJournalAddOrder.setPermissionName("短信服务");
				operationJournalAddOrder.setOperatorId(local.getUserId());
				operationJournalAddOrder.setOperatorName(local.getRealName());
				operationJournalAddOrder.setOperatorIp(local.getRemoteAddr());
				operationJournalAddOrder.setBaseModuleName(AppConstantsUtil.getProductName());
				operationJournalAddOrder.setOperationContent("手工短信");
				operationJournalAddOrder.setMemo("短信条数：" + moilesArray.length + "条  ,内容："
													+ smsContent);
				operationLog.addOperationJournalInfo(operationJournalAddOrder);
				
				jsonobj.put("code", 1);
				jsonobj.put("message", "短信发送成功！共" + moilesArray.length + "条");
			} else {
				jsonobj.put("code", 0);
				jsonobj.put("message", "短信发送失败！[" + result.getYrdResultEnum().code() + "]");
			}
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			jsonobj.put("code", 0);
			jsonobj.put("message", "短信发送失败！");
		}
		return jsonobj;
	}
	
}
