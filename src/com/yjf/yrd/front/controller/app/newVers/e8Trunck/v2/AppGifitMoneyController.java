package com.yjf.yrd.front.controller.app.newVers.e8Trunck.v2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.yjf.common.lang.util.StringUtil;
import com.yjf.yrd.base.UserAccountInfoBaseController;
import com.yjf.yrd.page.PageParam;
import com.yjf.yrd.service.query.giftMoney.GiftMoneyTradeQueryService;
import com.yjf.yrd.session.SessionLocalManager;
import com.yjf.yrd.util.DateUtil;
import com.yjf.yrd.ws.info.GiftMoneyTradeInfo;
import com.yjf.yrd.ws.service.query.order.GiftMoneyTradeQueryOrder;
import com.yjf.yrd.ws.service.query.result.QueryBaseBatchResult;

@Controller
@RequestMapping("app")
public class AppGifitMoneyController extends UserAccountInfoBaseController {
	@Autowired
	GiftMoneyTradeQueryService giftMoneyTradeQueryService;

	/**
	 * 可用红包和体验金的列表展示
	 * 
	 * */
	@ResponseBody
	@RequestMapping("getGifitMoneyList.htm")
	public JSONObject getGiftMoneyList(GiftMoneyTradeQueryOrder queryOrder,
			PageParam pageParam, Model model, Integer currentPage,
			Integer pageNumber) {
		JSONObject json = new JSONObject();
		if (!AppCommonUtil.isLogin()) {
			json.put("code", -1);
			json.put("message", "为登录或登录失效");
			return json;
		}
		initAccountInfo(model);
		if (pageNumber != null) {
			queryOrder.setPageNumber(pageNumber);
		} else if (currentPage != null) {
			queryOrder.setPageNumber(currentPage);
		} else {
			queryOrder.setPageNumber(pageParam.getPageNo());
		}

		queryOrder.setPageSize(pageParam.getPageSize());
		// queryOrder.setTradeType(GiftMoneyTradeTypeEnum.ORIGINAL.getCode());
		queryOrder.setUserid(SessionLocalManager.getSessionLocal().getUserId());
		QueryBaseBatchResult<GiftMoneyTradeInfo> page1 = null;
		QueryBaseBatchResult<GiftMoneyTradeInfo> page2 = null;
		long totalCount = 0;
		if (StringUtil.isNotEmpty(queryOrder.getStatus())
				&& "AVAILABLE".equalsIgnoreCase(queryOrder.getStatus())) {
			logger.info("获取红包信息 status0={}", queryOrder.getStatus());
			queryOrder.setStatus("NORMAL");
			page1 = giftMoneyTradeQueryService.queryGiftMoneyTrade(queryOrder);
			logger.info("获取红包信息 status1={}", queryOrder.getStatus());
			queryOrder.setStatus("USED");
			page2 = giftMoneyTradeQueryService.queryGiftMoneyTrade(queryOrder);
			logger.info("获取红包信息 status2={}", queryOrder.getStatus());
			totalCount = page1.getTotalCount() + page2.getTotalCount();

		} else {
			page1 = giftMoneyTradeQueryService.queryGiftMoneyTrade(queryOrder);
			logger.info("获取红包信息 status={}", queryOrder.getStatus());
			totalCount = page1.getTotalCount();
		}

		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		if (page1.isSuccess()) {
			for (GiftMoneyTradeInfo info : page1.getPageList()) {
				list.add(getGiftMoneyMap(info));
			}
			if (page2 != null && page2.isSuccess()) {
				for (GiftMoneyTradeInfo info : page2.getPageList()) {
					list.add(getGiftMoneyMap(info));
				}

			}
			json.put(
					"totalPage",
					AppCommonUtil.getTotalPage(totalCount,
							pageParam.getPageSize()));
			json.put("giftMoneyList", list);
			json.put("code", 1);
			json.put("message", "查询红包和体验金成功");

		} else {
			json.put("code", 0);
			json.put("message", "查询红包和体验金失败");
		}

		return json;
	}

	@ResponseBody
	@RequestMapping("giftMoneyDetail.htm")
	public JSONObject giftMoneyDetail(Long giftTradeId) {
		JSONObject json = new JSONObject();
		if (!AppCommonUtil.isLogin()) {
			json.put("code", -1);
			json.put("message", "为登录或登录失效");
			return json;
		}
		GiftMoneyTradeInfo giftMoneyDetail = giftMoneyTradeQueryService
				.queryGiftMoneyTradeById(giftTradeId);
		if (giftMoneyDetail != null) {
			json.put("giftMoneyDetail", getGiftMoneyMap(giftMoneyDetail));
			json.put("code", 1);
			json.put("message", "查询成功");
		} else {
			json.put("code", 0);
			json.put("message", "查询失败");
		}

		return json;

	}

	/**
	 * 获取红包查询结果Map
	 * */
	private Map<String, String> getGiftMoneyMap(GiftMoneyTradeInfo info) {
		Map<String, String> map = new HashMap<String, String>();
		String useType = "投资";
		if (info.getUseType() != null) {
			useType = info.getUseType().getMessage();
		}
		map.put("useType", useType);
		map.put("status", info.getStatus());
		map.put("giftName", info.getGiftName());
		map.put("type", info.getType().getMessage());
		map.put("amount", info.getAmount().toStandardString());
		map.put("usedAmount", info.getUsedAmount().toStandardString());
		map.put("startDate", DateUtil.simpleFormat(info.getStartDate()));
		map.put("endDate", DateUtil.simpleFormat(info.getEndDate()));
		map.put("rawUpdateTime", DateUtil.simpleFormat(info.getRawUpdateTime()));
		map.put("giftId", String.valueOf(info.getGiftId()));
		map.put("giftTradeId", String.valueOf(info.getGiftTradeId()));
		map.put("useAmount", info.getUseAmount());

		return AppCommonUtil.cleanNull(map);
	}
}
