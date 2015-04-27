package com.yjf.yrd.front.controller.account;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.yjf.common.lang.ip.IPUtil;
import com.yjf.common.lang.util.StringUtil;
import com.yjf.yrd.base.BaseAutowiredController;
import com.yjf.yrd.dal.dataobject.InstitutionsInfoDO;
import com.yjf.yrd.dal.dataobject.PersonalInfoDO;
import com.yjf.yrd.dal.dataobject.UserBaseInfoDO;
import com.yjf.yrd.dal.dataobject.UserOriginDO;
import com.yjf.yrd.integration.bornapi.enums.YjfRegesterTypeEnum;
import com.yjf.yrd.integration.web.server.YjfLoginWebServer;
import com.yjf.yrd.integration.web.server.impl.YjfRegisterOrder;
import com.yjf.yrd.session.SessionLocal;
import com.yjf.yrd.session.SessionLocalManager;
import com.yjf.yrd.user.info.UserInfo;
import com.yjf.yrd.user.order.InvestorRegisterOrder;
import com.yjf.yrd.user.order.OnlyRegesiterYjfUserOrder;
import com.yjf.yrd.user.result.UserBaseReturnEnum;
import com.yjf.yrd.user.result.UserRegisterResult;
import com.yjf.yrd.web.util.WebUtil;
import com.yjf.yrd.ws.enums.SysUserRoleEnum;
import com.yjf.yrd.ws.result.YrdBaseResult;
import com.yjf.yrd.ws.service.GiftMoneyAssignProcessService;

/**
 * 
 * 
 * @Filename InvestorsOpenController.java
 * 
 * @Description
 * 
 * @Version 1.0
 * 
 * @Author zjl
 * 
 * @Email zjialin@yiji.com
 * 
 * @History <li>Author: zjl</li> <li>Date: 2013-8-19</li> <li>Version: 1.0</li>
 * <li>Content: create</li>
 * 
 */
@Controller
@RequestMapping("anon")
public class InvestorsOpenController extends BaseAutowiredController {
	private final String vm_path = "/front/anon/investorsOpen/";
	@Autowired
	GiftMoneyAssignProcessService giftMoneyAssignProcessService;
	@Autowired
	YjfLoginWebServer yjfLoginWebServer;
	
	@RequestMapping("investorsOpen")
	public String investorsOpen(HttpSession session) {
		String token = UUID.randomUUID().toString();
		session.setAttribute("token", token);
		session.removeAttribute("brokerNo");
		// return vm_path + "investorsOpen.vm";
		return vm_path + "investorsOpen.vm";
	}
	
	@RequestMapping("investorsOpen2")
	public String investorsOpen2(HttpSession session) {
		String token = UUID.randomUUID().toString();
		session.setAttribute("token", token);
		session.removeAttribute("brokerNo");
		// return vm_path + "investorsOpen.vm";
		return vm_path + "investorsOpen2.vm";
	}
	
	@RequestMapping("perfectInfo")
	public String perfectInfo(HttpSession session, InstitutionsInfoDO institution,
								PersonalInfoDO personalInfo, UserBaseInfoDO userBaseInfo,
								String token, Model model, HttpServletRequest request)
																						throws Exception {
		InvestorRegisterOrder investorRegisterOrder = new InvestorRegisterOrder();
		WebUtil.setPoPropertyByRequest(investorRegisterOrder, request);
		YrdBaseResult baseResult = userBaseInfoManager.validationUserName(investorRegisterOrder
			.getUserName());

		// 验证用户存在
		if (!baseResult.isSuccess()) {
			return "redirect:investorsOpen?message=" + URLEncoder.encode("填写的账户已经存在！", "UTF-8");
		}
		List<SysUserRoleEnum> roleEnumList = new ArrayList<SysUserRoleEnum>();
		roleEnumList.add(SysUserRoleEnum.OPERATOR);
		roleEnumList.add(SysUserRoleEnum.INVESTOR);
		investorRegisterOrder.setRole(roleEnumList);
		UserRegisterResult result = registerService.investorRegister(investorRegisterOrder);
		if (!result.isSuccess()) {
			return "redirect:investorsOpen?userName="
					+ URLEncoder.encode(investorRegisterOrder.getUserName(), "UTF-8") + "&message="
					+ URLEncoder.encode(result.getMessage(), "UTF-8");
		} else {
			/* 用户登录 */
			UserInfo userinfo = result.getUserinfo();
			SessionLocal local = new SessionLocal(authorityService.getPermissionsByUserId(userinfo
				.getUserId()), userinfo.getUserId(), userinfo.getUserBaseId(),
				userinfo.getAccountId(), userinfo.getAccountName(), userinfo.getRealName(),
				userinfo.getUserName(), null);
			local.setRemoteAddr(IPUtil.getIpAddr(request));
			SessionLocalManager.setSessionLocal(local);
			
			/* 注册第三方支付 */
			YjfRegisterOrder yjfRegisterOrder = new YjfRegisterOrder();
			yjfRegisterOrder.setUserName(investorRegisterOrder.getUserName());
			yjfRegisterOrder.setYjfRegesterType(YjfRegesterTypeEnum.ACTIVATE);
			YrdBaseResult yrdBaseResult = yjfLoginWebServer.gotoYjfRegisterUrl(yjfRegisterOrder,
				this.getOpenApiContext());
			model.addAttribute("yjfRegisterUrl", yrdBaseResult.getUrl());
			// response.sendRedirect(yrdBaseResult.getUrl());
			return vm_path + "investorsOpenSuccess.vm";
		}
	}
	
	
	@RequestMapping("perfectInfo_e7")
    public String perfectInfo(UserOriginDO userOriginDO, Model model, HttpSession session, HttpServletRequest request, HttpServletResponse response) throws Exception {
//        if (StringUtil.isBlank(userOriginDO.getRefererNo())) {
//            return "redirect:investorsOpen?userName="+URLEncoder.encode(userOriginDO.getUserName(),"UTF-8")
//                    +"&message=" + URLEncoder.encode("请填写推荐人编号！", "UTF-8");
//        }

        if(StringUtil.isBlank(userOriginDO.getRefererNo())){
            userOriginDO.setRefererNo("");
        }
 
        YrdBaseResult baseResult  = userBaseInfoManager.validationUserName(userOriginDO.getUserName());
        //验证用户存在
        if (!baseResult.isSuccess()) {
            return "redirect:investorsOpen?userNameMessage=" + URLEncoder.encode("填写的账户已经存在！", "UTF-8");
        }

        YrdBaseResult result = registerService.userRegister(userOriginDO);
        if (!result.isSuccess()) {

            return "redirect:investorsOpen?userName="+URLEncoder.encode(userOriginDO.getUserName(),"UTF-8")
                    +"&message="+URLEncoder.encode(result.getMessage(), "UTF-8")+"&refererNo="+URLEncoder.encode(userOriginDO.getRefererNo(), "UTF-8");
        } else {
            response.sendRedirect(result.getUrl());
            return null;
        }
    }
	
	
	@RequestMapping("yjfRegisterPerfectInfo")
	public String yptPerfectInfo(OnlyRegesiterYjfUserOrder userOrder, Model model,
									HttpSession session, HttpServletRequest request,
									HttpServletResponse response) throws Exception {
		if (StringUtil.isBlank(userOrder.getRefererNo())) {
			userOrder.setRefererNo("");
		}
		
		YrdBaseResult baseResult = userBaseInfoManager.validationUserName(userOrder.getUserName());
		// 验证用户存在
		if (!baseResult.isSuccess()) {
			return "redirect:investorsOpen?message=" + URLEncoder.encode("填写的账户已经存在！", "UTF-8");
		}
		
		YrdBaseResult result = registerService.userRegisterByOnlyRegisterYjfUser(userOrder);
		if (!result.isSuccess()) {
			
			return "redirect:investorsOpen?userName="
					+ URLEncoder.encode(userOrder.getUserName(), "UTF-8") + "&message="
					+ URLEncoder.encode(result.getMessage(), "UTF-8") + "&refererNo="
					+ URLEncoder.encode(userOrder.getRefererNo(), "UTF-8");
		} else {
			response.sendRedirect(result.getUrl());
			return null;
		}
	}
	
	@RequestMapping("sendActivateEmail")
	public String sendActivateEmail() {
		return vm_path + "sendEmail.vm";
	}
	
	@RequestMapping("resendEmail/{userBaseId}")
	public String resendEmail(HttpSession session, @PathVariable String userBaseId, Model model)
																								throws Exception {
		UserInfo userBaseInfo = userQueryService.queryByUserBaseId(userBaseId).getQueryUserInfo();
		
		YrdBaseResult baseResult = registerService.resendEmail(userBaseInfo.getUserBaseId());
		
		model.addAttribute("userName", userBaseInfo.getUserName());
		if (baseResult.isSuccess()) {
			String email = userBaseInfo.getMail();
			String emailUrl = "http://mail."
								+ email.substring(email.lastIndexOf('@') + 1,
									email.lastIndexOf(".")) + ".com";
			model.addAttribute("emailUrl", emailUrl);
			if (StringUtil.isNotEmpty(email)) {
				String[] strMail = email.split("@");
				model.addAttribute("email", strMail[0].substring(0, 3) + "********@" + strMail[1]);
			}
			
			String resendEmailUrl = "/anon/resendEmail/" + userBaseInfo.getUserBaseId();
			model.addAttribute("resendEmailUrl", resendEmailUrl);
			model.addAttribute("reason", "邮件已经发送成功!请进行激活.");
			return vm_path + "investorsOpenSuccess.vm";
		} else
			model.addAttribute("reason", baseResult.getMessage());
		return vm_path + "investorsOpenFail.vm";
	}
	
	@RequestMapping("sendEmailPage/{userBaseId}")
	public String sendEmailPage(HttpSession session, @PathVariable String userBaseId, Model model)
																									throws Exception {
		UserInfo userBaseInfo = userQueryService.queryByUserBaseId(userBaseId).getQueryUserInfo();
		model.addAttribute("userName", userBaseInfo.getUserName());
		String email = userBaseInfo.getMail();
		String emailUrl = "http://mail."
							+ email.substring(email.lastIndexOf('@') + 1, email.lastIndexOf("."))
							+ ".com";
		model.addAttribute("emailUrl", emailUrl);
		model.addAttribute("email", userBaseInfo.getMail());
		String resendEmailUrl = "/anon/resendEmail/" + userBaseInfo.getUserBaseId();
		model.addAttribute("resendEmailUrl", resendEmailUrl);
		return vm_path + "investorsOpenSuccess.vm";
	}
}
