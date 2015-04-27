package com.yjf.yrd.front.controller.app.newVers.e8Trunck.v2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.yjf.common.lang.util.StringUtil;
import com.yjf.yrd.base.BaseAutowiredService;
import com.yjf.yrd.common.order.BankQueryOrder;
import com.yjf.yrd.common.services.BankBaseInfoService;
import com.yjf.yrd.dal.dataobject.BankBaseInfoDO;
import com.yjf.yrd.integration.openapi.SignService;
import com.yjf.yrd.integration.openapi.VerifyBankCardService;
import com.yjf.yrd.integration.openapi.order.PactApplyOrder;
import com.yjf.yrd.integration.openapi.order.PactSignOrder;
import com.yjf.yrd.integration.openapi.order.VerifyBankCardBinOrder;
import com.yjf.yrd.integration.openapi.result.PactResult;
import com.yjf.yrd.page.PageParam;
import com.yjf.yrd.session.SessionLocal;
import com.yjf.yrd.session.SessionLocalManager;
import com.yjf.yrd.util.AppConstantsUtil;
import com.yjf.yrd.ws.order.DeleteUserBankOrder;
import com.yjf.yrd.ws.result.YrdBaseResult;
import com.yjf.yrd.ws.service.UserBankService;
import com.yjf.yrd.ws.service.query.result.QueryBaseBatchResult;

@Controller
@RequestMapping("app")
public class SingBankCardController extends BaseAutowiredService {
	@Autowired
	SignService signService;
	@Autowired
	VerifyBankCardService verifyBankCardService;
	@Autowired
	BankBaseInfoService bankBaseInfoService;
	@Autowired
	private UserBankService userBankService;
	// 银行图片位置
	String IMGURL = "http://image.yiji.com/upload/frt/uploadfile/images/appimage/";
	
	/**
	 * 获取可签约银行
	 * 
	 * */
	@ResponseBody
	@RequestMapping("getAvalSinBank.htm")
	public JSONObject getAvalSinBank(BankQueryOrder bankQueryOrder, PageParam pageParam) {
		JSONObject json = new JSONObject();
		if (!AppCommonUtil.isLogin()) {
			json.put("code", -1);
			json.put("message", "为登录或登录失效");
			return json;
		}
		bankQueryOrder.setIsStop("1");
		bankQueryOrder.setSignedWay("1");
		QueryBaseBatchResult<BankBaseInfoDO> batchResult = bankBaseInfoService
			.getBanckInfosByCondition(bankQueryOrder);
		if (batchResult.isSuccess()) {
			List<Map<String, String>> list = new ArrayList<Map<String, String>>();
			for (BankBaseInfoDO info : batchResult.getPageList()) {
				Map<String, String> map = new HashMap<String, String>();
				String logoUrl = IMGURL + info.getBankCode() + ".png";
				map.put("bankCode", info.getBankCode());
				map.put("bankName", info.getBankName());
				map.put("bankGifUrl", info.getLogoUrl());
				map.put("bankLogo", logoUrl);
				list.add(AppCommonUtil.cleanNull(map));
			}
			json.put("feeRules", AppConstantsUtil.getChargeRules());
			json.put("singBankList", list);
			json.put("totalPage", batchResult.getTotalCount());
			json.put("code", 1);
			json.put("message", "获取可签约银行列表成功！");
		} else {
			json.put("code", 0);
			json.put("message", "获取可签约银行列表失败！");
		}
		return json;
	}
	
	/**
	 * 验证当前银行卡是否正确
	 * 
	 * */
	@ResponseBody
	@RequestMapping("checkBankCard.htm")
	public JSONObject checkBankCard(VerifyBankCardBinOrder bankCardOrder) {
		JSONObject json = new JSONObject();
		if (!AppCommonUtil.isLogin()) {
			json.put("code", -1);
			json.put("message", "为登录或登录失效");
			return json;
		}
		YrdBaseResult result = verifyBankCardService.verifyBankCardBin(bankCardOrder,
			this.getOpenApiContext());
		if (result.isSuccess()) {
			json.put("code", 1);
			json.put("message", "银行卡号和所属银行正确");
		} else {
			json.put("code", 0);
			if (StringUtil.isNotEmpty(result.getMessage())) {
				json.put("message", result.getMessage());
			} else {
				json.put("message", "验证失败");
			}
		}
		return json;
	}
	
	/**
	 * 申请绑定银行卡
	 * 
	 * @Param userPhoneNo 预留手机号
	 * @Param cardNo 卡号
	 * @param userId易极付
	 * @Return tradeNo 流水号
	 * */
	@ResponseBody
	@RequestMapping("singBank.htm")
	public JSONObject sinBank(PactApplyOrder applyOrder, HttpSession session) {
		JSONObject json = new JSONObject();
		if (!AppCommonUtil.isLogin()) {
			json.put("code", -1);
			json.put("message", "为登录或登录失效");
			return json;
		}
		applyOrder.setUserId(SessionLocalManager.getSessionLocal().getAccountId());
		PactResult singResult = signService.pactApply(applyOrder, this.getOpenApiContext());
		if (singResult.isSuccess()) {
			session.setAttribute("tradeNo", singResult.getTradeNo());
			session.setAttribute("pactType", singResult.getPactType());
			json.put("code", 1);
			json.put("message", "申请绑卡成功！");
		} else {
			json.put("code", 0);
			
			json.put("message", singResult.getMessage());
			
		}
		return json;
	}
	
	/**
	 * 申请绑定银行卡
	 * 
	 * @Param tradeNo
	 * @param mobileCode
	 * @param
	 * */
	@ResponseBody
	@RequestMapping("singBankCheck.htm")
	public JSONObject sinBankCheck(PactSignOrder pactSignOrder, HttpSession session) {
		JSONObject json = new JSONObject();
		if (!AppCommonUtil.isLogin()) {
			json.put("code", -1);
			json.put("message", "为登录或登录失效");
			return json;
		}
		String tradeNo = (String) session.getAttribute("tradeNo");
		String pactType = (String) session.getAttribute("pactType");
		if (StringUtil.isEmpty(tradeNo) || StringUtil.isEmpty(pactType)) {
			json.put("code", 0);
			json.put("message", "当前操作失效");
			return json;
		}
		pactSignOrder.setPactType(pactType);
		pactSignOrder.setTradeNo(tradeNo);
		YrdBaseResult singResult = signService.pactSign(pactSignOrder, this.getOpenApiContext());
		if (singResult.isSuccess()) {
			json.put("code", 1);
			json.put("message", "绑卡成功!");
		} else {
			json.put("code", 0);
			if (StringUtil.isNotEmpty(singResult.getMessage())) {
				json.put("message", singResult.getMessage());
			} else {
				json.put("message", "绑卡失败！");
			}
			
		}
		return json;
	}
	
	/**
	 * 删除绑定的银行卡
	 * 
	 * */
	@ResponseBody
	@RequestMapping("deleteUserBank.htm")
	public JSONObject deleteUserBank(HttpServletRequest request, Long bankCode) {
		JSONObject json = new JSONObject();
		if (null == bankCode) {
			json.put("code", 0);
			json.put("message", "未传入参数：bankCode");
			return json;
		}
		SessionLocal sessionLocal = SessionLocalManager.getSessionLocal();
		String userBaseId = sessionLocal.getUserBaseId();
		DeleteUserBankOrder deleteUserBankOrder = new DeleteUserBankOrder();
		deleteUserBankOrder.setId(bankCode);
		deleteUserBankOrder.setUserBaseId(userBaseId);
		YrdBaseResult baseResult = userBankService.removeUserBankInfo(deleteUserBankOrder);
		if (baseResult.isSuccess()) {
			json.put("code", 1);
			json.put("message", "删除成功");
		} else {
			json.put("code", 0);
			json.put("message", baseResult.getMessage());
		}
		return json;
	}
	
}
