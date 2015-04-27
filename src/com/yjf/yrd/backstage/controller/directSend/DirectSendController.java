package com.yjf.yrd.backstage.controller.directSend;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.yjf.yrd.base.BaseAutowiredController;
import com.yjf.yrd.session.SessionLocal;
import com.yjf.yrd.session.SessionLocalManager;
import com.yjf.yrd.user.result.UserQueryResult;
import com.yjf.yrd.util.StringUtil;
import com.yjf.yrd.ws.enums.DirectSendTypeEnum;
import com.yjf.yrd.ws.enums.DirectTypeEnum;
import com.yjf.yrd.ws.enums.NotificationTypeEnum;
import com.yjf.yrd.ws.order.DirectSendOrder;
import com.yjf.yrd.ws.result.YrdBaseResult;

@Controller
@RequestMapping("backstage/directSend")
public class DirectSendController extends BaseAutowiredController {
	/**
	 * 页面所在路径
	 */
	private final String BORROWING_MANAGE__PATH = "/backstage/directSend/";
	
	@RequestMapping(value = "directSend")
	public String directSend(Model model) {
		
		List<DirectTypeEnum> directTypes = DirectTypeEnum.getAllEnum();
		model.addAttribute("directTypes", directTypes);
		
		List<NotificationTypeEnum> notiTypeEnums = NotificationTypeEnum.getAllEnum();
		List<NotificationTypeEnum> userTypes = new ArrayList<NotificationTypeEnum>();
		for (NotificationTypeEnum ntype : notiTypeEnums) {
			if (NotificationTypeEnum.ALL.code().equals(ntype.code())
				|| NotificationTypeEnum.VIP.code().equals(ntype.code())
				|| NotificationTypeEnum.GOLD.code().equals(ntype.code())
				|| NotificationTypeEnum.DIAMOND.code().equals(ntype.code())
				|| NotificationTypeEnum.PLATINUM.code().equals(ntype.code())) {
				userTypes.add(ntype);
			}
		}
		model.addAttribute("userTypes", userTypes);
		
		model.addAttribute("sendType1", DirectSendTypeEnum.USERGROUP.code());
		model.addAttribute("sendType2", DirectSendTypeEnum.SINGLEUSER.code());
		
		return BORROWING_MANAGE__PATH + "directSend.vm";
	}
	
	@ResponseBody
	@RequestMapping(value = "directSendSubmit")
	public Object directSendSubmit(DirectSendOrder directSendOrder) throws Exception {
		JSONObject json = new JSONObject();
		SessionLocal sessionLocal = SessionLocalManager.getSessionLocal();
		if (sessionLocal == null) {
			json.put("code", 0);
			json.put("message", "赠送失败,登录失效!");
			return json;
		}
		
		YrdBaseResult result = directSendService.directSend(directSendOrder);
		if (result.isSuccess()) {
			json.put("code", 1);
			json.put("message", "赠送成功!");
		} else {
			json.put("code", 0);
			json.put("message", "赠送失败:" + result.getMessage());
			
		}
		return json;
	}
	
	@ResponseBody
	@RequestMapping("getRealName")
	public Object getRealName(String userName) throws Exception {
		JSONObject json = new JSONObject();
		if (StringUtil.isEmpty(userName)) {
			json.put("code", 0);
			return json;
		}
		
		UserQueryResult userResult = userQueryService.queryByUserName(userName);
		if (userResult == null || userResult.getQueryUserInfo() == null) {
			json.put("code", 0);
			return json;
		} else {
			json.put("code", 1);
			json.put("message", userResult.getQueryUserInfo().getRealName());
			return json;
		}
	}
	
}
