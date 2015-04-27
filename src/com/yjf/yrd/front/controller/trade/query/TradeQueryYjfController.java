/**
 * www.yiji.com Inc.
 * Copyright (c) 2011 All Rights Reserved.
 */
package com.yjf.yrd.front.controller.trade.query;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.yjf.yrd.base.UserAccountInfoBaseController;
import com.yjf.yrd.session.SessionLocalManager;

/**
 * 
 * @Filename TradeQueryYjfController.java
 * 
 * @Description
 * 
 * @Version 1.0
 * 
 * @Author qichunhai
 * 
 * @Email qchunhai@yiji.com
 * 
 * @History <li>Author: qichunhai</li> <li>Date: 2014-5-19</li> <li>Version: 1.0
 * </li> <li>Content: create</li>
 * 
 */
@Controller
@RequestMapping("userManage")
public class TradeQueryYjfController extends UserAccountInfoBaseController {
	private final String vm_path = "/front/trade/query/";
	
	@RequestMapping("balanceChangeRecord")
	public String balanceChangeRecord(Model model, HttpServletRequest request,
										HttpServletResponse response) {
		
		initAccountInfo(model);
		getUserBaseInfo(request.getSession(), model);
		model.addAttribute("selectedbalance", "class=\"cur\"");
		model.addAttribute("iframeUrl", "balanceChangeRecordIframe");
		
		return vm_path + "tradeQuery.vm";
	}
	
	@RequestMapping("rechargeRecord")
	public String rechargeRecord(Model model, HttpServletRequest request,
									HttpServletResponse response) {
		initAccountInfo(model);
		getUserBaseInfo(request.getSession(), model);
		model.addAttribute("selectedrecharge", "class=\"cur\"");
		model.addAttribute("iframeUrl", "rechargeRecordIframe");
		
		return vm_path + "tradeQuery.vm";
	}
	
	@RequestMapping("withdrawRecord")
	public String withdrawRecord(Model model, HttpServletRequest request,
									HttpServletResponse response) {
		initAccountInfo(model);
		getUserBaseInfo(request.getSession(), model);
		model.addAttribute("selectedwithdraw", "class=\"cur\"");
		model.addAttribute("iframeUrl", "rechargeWithdrawIframe");
		
		return vm_path + "tradeQuery.vm";
	}
	
	@RequestMapping("balanceChangeRecordIframe")
	public String balanceChangeRecordIframe(Model model, HttpServletRequest request,
											HttpServletResponse response) {
		String userId = SessionLocalManager.getSessionLocal().getAccountId();
		String url = yjfTradeQueryService.yjfBalanceQueryService(userId, this.getOpenApiContext())
			.getUrl();
		try {
			response.sendRedirect(url);
		} catch (IOException e) {
			logger.error("IOException ", e);
		}
		return null;
	}
	
	@RequestMapping("rechargeRecordIframe")
	public String rechargeRecordIframe(Model model, HttpServletRequest request,
										HttpServletResponse response) {
		
		String userId = SessionLocalManager.getSessionLocal().getAccountId();
		String url = yjfTradeQueryService.yjfDepositQueryService(userId, this.getOpenApiContext())
			.getUrl();
		try {
			response.sendRedirect(url);
		} catch (IOException e) {
			logger.error("IOException ", e);
		}
		return null;
	}
	
	@RequestMapping("rechargeWithdrawIframe")
	public String withdrawRecordIframe(Model model, HttpServletRequest request,
										HttpServletResponse response) {
		
		String userId = SessionLocalManager.getSessionLocal().getAccountId();
		String url = yjfTradeQueryService.yjfWithdrawQueryService(userId, this.getOpenApiContext())
			.getUrl();
		try {
			response.sendRedirect(url);
		} catch (IOException e) {
			logger.error("IOException ", e);
		}
		return null;
	}
}
