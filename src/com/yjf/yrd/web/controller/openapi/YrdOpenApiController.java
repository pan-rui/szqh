package com.yjf.yrd.web.controller.openapi;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.yjf.common.lang.ip.IPUtil;
import com.yjf.common.lang.security.DigestUtil.DigestALGEnum;
import com.yjf.yrd.base.BaseAutowiredController;
import com.yjf.yrd.dal.daointerface.UserLoginLogDAO;
import com.yjf.yrd.dal.dataobject.UserLoginLogDO;
import com.yjf.yrd.service.openapi.ReceiveOpenApiService;
import com.yjf.yrd.service.openapi.result.YjfLoginResult;
import com.yjf.yrd.session.SessionLocal;
import com.yjf.yrd.session.SessionLocalManager;
import com.yjf.yrd.user.UserBaseInfoManager;
import com.yjf.yrd.user.info.UserInfo;
import com.yjf.yrd.util.AppConstantsUtil;
import com.yjf.yrd.util.BusinessNumberUtil;
import com.yjf.yrd.util.CommonUtil;
import com.yjf.yrd.util.IPAddressUtils;
import com.yjf.yrd.util.SignUtil;
import com.yjf.yrd.util.StringUtil;
import com.yjf.yrd.web.controller.template.ControllerBizProcess;
import com.yjf.yrd.web.util.WebConstant;
import com.yjf.yrd.web.util.WebUtil;
import com.yjf.yrd.ws.enums.UserRegisterFromEnum;
import com.yjf.yrd.ws.result.InvestResult;
import com.yjf.yrd.ws.result.YrdBaseResult;
import com.yjf.yrd.ws.service.YrdResultEnum;

@Controller
public class YrdOpenApiController extends BaseAutowiredController {
	@Autowired
	ReceiveOpenApiService receiveOpenApiService;
	
	@Autowired
	UserBaseInfoManager userBaseInfoManager;
	
	@Autowired
	UserLoginLogDAO userLoginLogDAO;
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/openApi/receiveInvestmentDemand.htm")
	public String receiveInvestmentDemand(HttpServletRequest request, HttpServletResponse response,
											ModelMap modelMap) throws ServletException {
		Map<String, String> param = WebUtil.getRequestMap(request);
		logger.info("进入OpenApiBackController的receiveInvestmentDemand方法!添加一条日志信息,入参: {}", param);
		YrdBaseResult result = receiveOpenApiService.receiveInvestmentDemand(param);
		logger.info("进入OpenApiBackController的receiveInvestmentDemand方法!添加一条日志信息,结果: {}", result);
		response.setCharacterEncoding("utf-8");
		JSONObject json = new JSONObject();
		if (result.isSuccess()) {
			json.put("resultCode", YrdResultEnum.EXECUTE_SUCCESS.code());
			json.put("resultMessage", result.getMessage());
			json.put("code", result.getYrdResultEnum().code());
		} else {
			if (result.getYrdResultEnum() == YrdResultEnum.OPENAPI_REPEAT_NOTIFY) {
				json.put("resultCode", YrdResultEnum.EXECUTE_SUCCESS.code());
				json.put("resultMessage", result.getMessage());
				json.put("code", result.getYrdResultEnum().code());
				
			} else {
				json.put("resultCode", result.getYrdResultEnum().code());
				json.put("resultMessage", result.getMessage());
				json.put("code", result.getYrdResultEnum().code());
			}
		}
		this.printHttpResponse(response, json);
		return null;
	}
	
	/**
	 * 实时投资
	 * 
	 * @param request
	 * @param response
	 * @param modelMap
	 * @return
	 * @throws ServletException
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/openApi/receiveRTInvestmentDemand.htm")
	public String receiveRTInvestmentDemand(HttpServletRequest request,
											HttpServletResponse response, ModelMap modelMap)
																							throws ServletException {
		Map<String, String> param = WebUtil.getRequestMap(request);
		logger.info("进入OpenApiBackController的receiveRTInvestmentDemand方法!添加一条日志信息,入参: {}", param);
		InvestResult result = receiveOpenApiService.receiveRTInvestmentDemand(param);
		logger.info("进入OpenApiBackController的receiveRTInvestmentDemand方法!添加一条日志信息,结果: {}", result);
		response.setCharacterEncoding("utf-8");
		JSONObject json = new JSONObject();
		if (result.isSuccess()) {
			json.put("resultCode", YrdResultEnum.EXECUTE_SUCCESS.code());
			json.put("resultMessage", result.getMessage());
			json.put("tradeDetailId", result.getTradeDetailId());
		} else {
			if (result.getYrdResultEnum() == YrdResultEnum.OPENAPI_REPEAT_NOTIFY) {// 重复请求
				json.put("resultCode", YrdResultEnum.EXECUTE_SUCCESS.code());
				json.put("resultMessage", result.getMessage());
				json.put("tradeDetailId", result.getTradeDetailId());
				
			} else {
				json.put("resultCode", result.getYrdResultEnum().code());
				json.put("resultMessage", result.getMessage());
				json.put("tradeDetailId", result.getTradeDetailId());
			}
		}
		this.printHttpResponse(response, json);
		return null;
	}
	
	@RequestMapping(value = "/openApi/yjfCaneclInvestmentDemand.htm")
	public String yjfCaneclInvestmentDemand(HttpServletRequest request,
											HttpServletResponse response, ModelMap modelMap)
																							throws ServletException {
		openApiReturnTemplate(request, response, new ControllerBizProcess() {
			
			@Override
			public YrdBaseResult process(Map<String, String> param) {
				return receiveOpenApiService.yjfCaneclInvestmentDemand(param);
			}
		});
		return null;
	}
	
	/**
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("unchecked")
	protected void openApiReturnTemplate(HttpServletRequest request, HttpServletResponse response,
											ControllerBizProcess controllerBizProcess) {
		Map<String, String> param = WebUtil.getRequestMap(request);
		logger.info("进入openApiReturnTemplate的param方法!添加一条日志信息,入参: {}", param);
		YrdBaseResult result = new YrdBaseResult();
		if (controllerBizProcess != null) {
			result = controllerBizProcess.process(param);
		}
		logger.info("进入OpenApiBackController的param方法!添加一条日志信息,结果: {}", result);
		response.setCharacterEncoding("utf-8");
		JSONObject json = new JSONObject();
		if (result.isSuccess()) {
			json.put("resultCode", YrdResultEnum.EXECUTE_SUCCESS.code());
			json.put("resultMessage", result.getMessage());
			json.put("code", result.getYrdResultEnum().code());
		} else {
			if (result.getYrdResultEnum() == YrdResultEnum.OPENAPI_REPEAT_NOTIFY) {
				json.put("resultCode", YrdResultEnum.EXECUTE_SUCCESS.code());
				json.put("resultMessage", result.getMessage());
				json.put("code", result.getYrdResultEnum().code());
				
			} else {
				json.put("resultCode", result.getYrdResultEnum().code());
				json.put("resultMessage", result.getMessage());
				json.put("code", result.getYrdResultEnum().code());
			}
		}
		this.printHttpResponse(response, json);
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/openApi/returnEBankDepositResult.htm")
	public String returnEBankDepositResult(HttpServletRequest request,
											HttpServletResponse response, ModelMap modelMap)
																							throws ServletException {
		openApiReturnTemplate(request, response, new ControllerBizProcess() {
			
			@Override
			public YrdBaseResult process(Map<String, String> param) {
				return receiveOpenApiService.netBankDepositComplete(param);
			}
		});
		return null;
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/openApi/yjfRegisterNotifyUrl.htm")
	public String yjfRegisterNotifyUrl(HttpServletRequest request, HttpServletResponse response,
										ModelMap modelMap) {
		openApiReturnTemplate(request, response, new ControllerBizProcess() {
			
			@Override
			public YrdBaseResult process(Map<String, String> param) {
				String loginId = param.get("userName");
				String userId = param.get("userId");
				return receiveOpenApiService.yjfUserRegister(loginId, userId,
					UserRegisterFromEnum.YJF); //20150409
			}
		});
		return null;
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/openApi/pageWithdrawReturn.htm")
	public String pageWithdrawReturn(HttpServletRequest request, HttpServletResponse response,
										ModelMap modelMap) {
		openApiReturnTemplate(request, response, new ControllerBizProcess() {
			
			@Override
			public YrdBaseResult process(Map<String, String> param) {
				return receiveOpenApiService.pageWithdrawReturn(param);
			}
		});
		return null;
	}
	
	@RequestMapping(value = "/openApi/appWithrawByPtk.htm")
	public String appWithrawByPtkReturn(HttpServletRequest request, HttpServletResponse response,
										ModelMap modelMap) {
		openApiReturnTemplate(request, response, new ControllerBizProcess() {
			
			@Override
			public YrdBaseResult process(Map<String, String> param) {
				return receiveOpenApiService.appWithrawByPtkReturn(param);
			}
		});
		return null;
	}
	
	@RequestMapping(value = "/openApi/yjfLogin.htm")
	public String yjfLoginOpenApi(HttpServletRequest request, HttpServletResponse response,
									ModelMap modelMap) throws ServletException {
		Map<String, String> param = WebUtil.getRequestMap(request);
		logger.info("进入OpenApiBackController的yjfLoginOpenApi方法!添加一条日志信息,入参: {}", param);
		String accessToken = request.getParameter("accessToken");
		if (StringUtil.isNotBlank(accessToken)) {
			YjfLoginResult loginResult = receiveOpenApiService.yjfLogin(accessToken);
			logger.info("进入OpenApiBackController的yjfLoginOpenApi方法!添加一条日志信息,结果: {}", loginResult);
			try {
				if (loginResult.isSuccess()) {
					UserInfo baseInfoDO = loginResult.getUserInfo();
					SessionLocal local = new SessionLocal(
						authorityService.getPermissionsByUserId(baseInfoDO.getUserId()),
						baseInfoDO.getUserId(), baseInfoDO.getUserBaseId(),
						baseInfoDO.getAccountId(), baseInfoDO.getAccountName(),
						baseInfoDO.getRealName(), baseInfoDO.getUserName(),
						authorityService.rolesNameByUserId(baseInfoDO.getUserId()));
					local.setRemoteAddr(IPUtil.getIpAddr(request));
					SessionLocalManager.setSessionLocal(local);
					
					String ip = CommonUtil.getIp(request);
					String externalId = BusinessNumberUtil.gainNumber();
					try {
						String address = IPAddressUtils.getAddresses("ip=" + ip, "utf-8");
						UserLoginLogDO userLoginLog = new UserLoginLogDO();
						userLoginLog.setTblBaseId(externalId);
						userLoginLog.setLoginAddress(address);
						userLoginLog.setLoginIp(ip);
						userLoginLog.setUserId(baseInfoDO.getUserId());
						userLoginLog.setLoginTime(new Date());
						userLoginLogDAO.insert(userLoginLog);
					} catch (Exception e) {
						logger.error("新增用户登录信息异常", e);
					}
					modelMap.put("localUrl", AppConstantsUtil.getHostHttpUrl());
					modelMap.put("sessionScope", SessionLocalManager.getSessionLocal());
					return "front/yjf/loginSuccess.vm";
				}
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
			
		}
		String yjfLoginUrl = WebConstant.getYjfloginurl();
		try {
			response.sendRedirect(yjfLoginUrl);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}
	
	/**
	 * 易结汇信任登录
	 * @param request http://192.168.49.121:8085/openApi/yjhLogin.htm?sign=
	 * f743c3234063d39f77f0f5bd8ede4e7f
	 * &accessTime=1415178721733&userId=20140903010000055410
	 * &loginId=touzi090302&returnUrl=http://192.168.45.212:28000
	 * @param response
	 * @param modelMap
	 * @return
	 * @throws ServletException
	 */
	@RequestMapping(value = "/openApi/yjhLogin.htm")
	public String yjhLoginOpenApi(HttpServletRequest request, HttpServletResponse response,
									ModelMap modelMap) throws ServletException {
		Map<String, String> param = WebUtil.getRequestMap(request);
		logger.info("进入OpenApiBackController的yjhLoginOpenApi方法!添加一条日志信息,入参: {}", param);
		String returnUrl = request.getParameter("returnUrl");
		YjfLoginResult loginResult = receiveOpenApiService.yjhLogin(param);
		logger.info("进入OpenApiBackController的yjhLoginOpenApi方法!添加一条日志信息,结果: {}", loginResult);
		try {
			if (loginResult.isSuccess()) {
				
				UserInfo baseInfoDO = loginResult.getUserInfo();
				SessionLocal local = new SessionLocal(
					authorityService.getPermissionsByUserId(baseInfoDO.getUserId()),
					baseInfoDO.getUserId(), baseInfoDO.getUserBaseId(), baseInfoDO.getAccountId(),
					baseInfoDO.getAccountName(), baseInfoDO.getRealName(),
					baseInfoDO.getUserName(), authorityService.rolesNameByUserId(baseInfoDO
						.getUserId()));
				local.setRemoteAddr(IPUtil.getIpAddr(request));
				SessionLocalManager.setSessionLocal(local);
				String ip = CommonUtil.getIp(request);
				String externalId = BusinessNumberUtil.gainNumber();
				try {
					String address = IPAddressUtils.getAddresses("ip=" + ip, "utf-8");
					UserLoginLogDO userLoginLog = new UserLoginLogDO();
					userLoginLog.setTblBaseId(externalId);
					userLoginLog.setLoginAddress(address);
					userLoginLog.setLoginIp(ip);
					userLoginLog.setUserId(baseInfoDO.getUserId());
					userLoginLog.setLoginTime(new Date());
					userLoginLogDAO.insert(userLoginLog);
					
				} catch (Exception e) {
					logger.error("YJH新增用户登录信息异常", e);
				}
				modelMap
					.put("localUrl", AppConstantsUtil.getHostHttpUrl() + "/userManage/userHome");
				return "test/loginSuccess.vm";
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		
		try {
			if (StringUtil.isEmpty(returnUrl)) {
				returnUrl = AppConstantsUtil.getHostHttpUrl();
			}
			response.sendRedirect(returnUrl);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}
	
	public static void main(String[] args) {
		//{amount=[10.00],sign=[bd0df6295b074ad4d36440c14ab5cffe],batchNo=[123456789987654321],userId=[investorId],partnerId=[20130304020004233676],sendTime=[2014-06-05 18:45:44],period=[6],annualRate=[95]}
		Map<String, String> param = new HashMap<String, String>();
		param.put("amount", "10.00");
		param.put("batchNo", "123456789987654321");
		param.put("userId", "investorId");
		param.put("partnerId", "20130304020004233676");
		param.put("sendTime", "2014-06-05 18:45:44");
		param.put("period", "6");
		param.put("annualRate", "95");
		param.put("sign", "bd0df6295b074ad4d36440c14ab5cffe");
		SignUtil.validateSign(param, "10b5d040fbc300331b94150d8b670c6e", DigestALGEnum.MD5);
		
		Map<String, String> param2 = new HashMap<String, String>();
		param2.put("accessTime", "1428553360768");
		param2.put("userId", "20140518010000011730");
		param2.put("loginId", "tzwy_hftouzi01");
		param2.put("returnUrl", "http://192.168.49.121:8084");
		param2.put("sign", "2e9eda8f74cf0b59de29968aa5e16854");
		System.out.println(SignUtil.validateSign(param2, "10b5d040fbc300331b94150d8b670c6e",
			DigestALGEnum.MD5));
	}
	
}
