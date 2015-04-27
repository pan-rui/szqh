package com.yjf.yrd.base;

import java.util.*;

import com.yjf.common.lang.util.ListUtil;
import com.yjf.yrd.cooperate.ICooperateService;
import com.yjf.yrd.dal.daointerface.ExtraDAO;
import com.yjf.yrd.dataobject.viewObject.TradeDetailStatisticsAmountVO;
import com.yjf.yrd.pop.impl.ComparatorPop;
import com.yjf.yrd.util.MoneyUtil;
import com.yjf.yrd.util.StringUtil;
import com.yjf.yrd.web.util.AttachmentModuleType;
import com.yjf.yrd.web.util.PageUtil;
import com.yjf.yrd.web.util.WebConstant;
import com.yjf.yrd.ws.enums.DivisionPhaseEnum;
import com.yjf.yrd.ws.enums.InsureWayEnum;
import com.yjf.yrd.ws.enums.SysUserRoleEnum;
import com.yjf.yrd.ws.info.*;
import com.yjf.yrd.ws.order.CooperateQueryOrder;
import com.yjf.yrd.ws.order.TradeDetailQueryOrder;
import com.yjf.yrd.ws.service.query.order.QueryTradeDetailUserOrder;
import com.yjf.yrd.ws.service.query.result.TradeDetailBatchResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.yjf.yrd.dataobject.PopInfoDO;
import com.yjf.yrd.page.Page;
import com.yjf.yrd.page.PageParam;
import com.yjf.yrd.pop.IPopModuleService;
import com.yjf.yrd.pop.IPopService;
import com.yjf.yrd.ws.order.CommonAttachmentQueryOrder;
import com.yjf.yrd.ws.service.CommonAttachmentService;
import com.yjf.yrd.ws.service.query.order.QueryLoanTradeOrder;
import com.yjf.yrd.ws.service.query.result.QueryBaseBatchResult;
import com.yjf.yrd.ws.statistics.TradeStatisticsInfo;
import com.yjf.yrd.ws.statistics.order.TradeStatisticsOrder;

/**
 * 
 * @author CHARLEY
 * 
 */
@Controller
@RequestMapping("/")
public class BaseHandelController extends BaseAutowiredController {
	private final String vm_path = "/";
	@Autowired
	IPopService popService;
	
	@Autowired
	IPopModuleService popModuleService;
	@Autowired
	CommonAttachmentService commonAttachmentService;

    @Autowired
    protected ExtraDAO extraDAO;
    
    @Autowired
	ICooperateService cooperateService;
	
	@RequestMapping("error.htm")
	public String signBankCard(Model model) throws Exception {
		return vm_path + "common/error.htm";
	}
	
	@RequestMapping("")
	public String index(PageParam pageParam, Model model) throws Exception {
		//信息统计
		infoStatistics(model);
		
		//shjs首页展示发标信息
		QueryLoanTradeOrder tradeQueryOrderN = new QueryLoanTradeOrder();
		tradeQueryOrderN.setPageNumber(1);
		tradeQueryOrderN.setPageSize(10);
/*		List<String> statusListN = new ArrayList<String>();
		statusListN.add("1");
		statusListN.add("2");
		tradeQueryOrderN.setStatus(statusListN);
*/
		QueryBaseBatchResult<LoanDemandTradeVOInfo> batchResultN = tradeBizQueryService
			.searchLoanTradeCommonQuery(tradeQueryOrderN);
		model.addAttribute("newPage", PageUtil.getCovertPage(batchResultN));
		//end
		
		//添加易票通个人版登陆
		model.addAttribute("yjfUrl", WebConstant.getYjfloginurl());
		
		// 前台首页图片
		CommonAttachmentQueryOrder attachmentQueryOrder = new CommonAttachmentQueryOrder();
		attachmentQueryOrder.setBizNo("0000");
		QueryBaseBatchResult<CommonAttachmentInfo> batchResultlist = commonAttachmentService
			.queryCommonAttachment(attachmentQueryOrder);
		List<AttachmentModuleType> attachmentModuleTypeList = new ArrayList<AttachmentModuleType>();
		for (CommonAttachmentInfo attachmentInfo : batchResultlist.getPageList()) {
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
		//end
		
		// 前台合作机构图片
		CooperateQueryOrder cooperateOrder = new CooperateQueryOrder();
		
		cooperateOrder.setPageSize(1000);
		
		QueryBaseBatchResult<CooperateInfo> cooperateResult = cooperateService.queryCooperate(cooperateOrder);
		
		model.addAttribute("cooperateList", PageUtil.getCovertPage(cooperateResult));
		
		// 前台合作机构图片 end 
		
		//借款成功项目数
		QueryLoanTradeOrder tradeQueryOrder = new QueryLoanTradeOrder();
		List<String> statusList = new ArrayList<String>();
		statusList.add("2");
		statusList.add("3");
		statusList.add("7");
		statusList.add("8");
		tradeQueryOrder.setStatus(statusList);
		QueryBaseBatchResult<LoanDemandTradeVOInfo> batchResult = tradeBizQueryService
			.searchLoanTradeCommonQuery(tradeQueryOrder);
		model.addAttribute("totalCount", batchResult.getTotalCount());
		//end
		
		//新版帮助中心前端
		pageParam.setPageSize(5);
		
		String moduleCode1 = "NEWS"; //新闻公告
		PopModuleVOInfo moduleInfo1 = popModuleService.getPopModule(moduleCode1);
		Map<String, Object> newsConditions = new HashMap<String, Object>();
		newsConditions.put("type", moduleInfo1.getType());
		newsConditions.put("status", 2);
		model.addAttribute("newsIndexs", popService.getPageByConditionsNew(pageParam, newsConditions).getResult());
		
		String moduleCode2 = "HELPS"; //帮助中心
		PopModuleVOInfo moduleInfo2 = popModuleService.getPopModule(moduleCode2);
		Map<String, Object> helpsConditions = new HashMap<String, Object>();
		helpsConditions.put("type", moduleInfo2.getType());
		helpsConditions.put("status", 2);
		model.addAttribute("helpsIndexs", popService.getPageByConditionsNew(pageParam, helpsConditions).getResult());
		
		String moduleCode3 = "STRATEGYS"; //投资攻略
		PopModuleVOInfo moduleInfo3 = popModuleService.getPopModule(moduleCode3);
		Map<String, Object> strategysConditions = new HashMap<String, Object>();
		strategysConditions.put("type", moduleInfo3.getType());
		strategysConditions.put("status", 2);
		model.addAttribute("strategysIndexs", popService.getPageByConditionsNew(pageParam, strategysConditions).getResult());
		
		String moduleCode4 = "QUESTIONS"; //常见问题
		PopModuleVOInfo moduleInfo4 = popModuleService.getPopModule(moduleCode4);
		Map<String, Object> questionsConditions = new HashMap<String, Object>();
		questionsConditions.put("type", moduleInfo4.getType());
		questionsConditions.put("status", 2);
		model.addAttribute("questionsIndexs", popService.getPageByConditionsNew(pageParam, questionsConditions).getResult());
		
		String moduleCode5 = "GUIDES"; //新手指引
		PopModuleVOInfo moduleInfo5 = popModuleService.getPopModule(moduleCode5);
		Map<String, Object> guidesConditions = new HashMap<String, Object>();
		guidesConditions.put("type", moduleInfo5.getType());
		guidesConditions.put("status", 2);
		model.addAttribute("guidesIndexs", popService.getPageByConditionsNew(pageParam, guidesConditions).getResult());
		
		// 新版帮助中心前端 end
		
		
		//资讯动态
		
		Map<String, Object> conditions1 = new HashMap<String, Object>();
		List<Integer> types1 = new ArrayList<Integer>();

		types1.add(10);
		conditions1.put("type", types1);
		conditions1.put("status", 2);
		pageParam.setPageSize(5);
		Page<PopInfoDO> page1 = popService.getPageByConditions(pageParam, conditions1);
		List<PopInfoDO> informationHelp = page1.getResult();
		model.addAttribute("informationHelp", informationHelp);
		//end
		
		//新闻
		Map<String, Object> conditions2 = new HashMap<String, Object>();
		List<Integer> types2 = new ArrayList<Integer>();

		types2.add(11);
		conditions2.put("type", types2);
		conditions2.put("status", 2);
		
		Page<PopInfoDO> page2 = popService.getPageByConditions(pageParam, conditions2);
		List<PopInfoDO> newsHelp = page2.getResult();
		model.addAttribute("newsHelp", newsHelp);
		//end
		
		
		//新闻公告
		Map<String, Object> conditions = new HashMap<String, Object>();
		List<Integer> types = new ArrayList<Integer>();
		types.add(1);
		types.add(2);
		conditions.put("type", types);
		conditions.put("status", 2);
		//pageParam.setPageSize(5);
		Page<PopInfoDO> page = popService.getPageByConditions(pageParam, conditions);
		List<PopInfoDO> popNotices = page.getResult();
		model.addAttribute("noticesIndexs", popNotices);
		
	//将新闻公告分成2个
		//a
		Map<String, Object> conditionsA = new HashMap<String, Object>();
		List<Integer> typesA = new ArrayList<Integer>();
		typesA.add(1);
		conditionsA.put("type", typesA);
		conditionsA.put("status", 2);
		//pageParam.setPageSize(5);
		Page<PopInfoDO> pageA = popService.getPageByConditions(pageParam, conditionsA);
		List<PopInfoDO> popNoticesA = pageA.getResult();
		model.addAttribute("noticesIndexsOne", popNoticesA);
		
		//b
		Map<String, Object> conditionsB = new HashMap<String, Object>();
		List<Integer> typesB = new ArrayList<Integer>();
		typesB.add(2);
		conditionsB.put("type", typesB);
		conditionsB.put("status", 2);
		//pageParam.setPageSize(5);
		Page<PopInfoDO> pageB = popService.getPageByConditions(pageParam, conditionsB);
		List<PopInfoDO> popNoticesB = pageB.getResult();
		model.addAttribute("noticesIndexsTwo", popNoticesB);
		
	//end
		Map<String, Object> helpConditions = new HashMap<String, Object>();
		List<Integer> helpTypes = new ArrayList<Integer>();
		helpTypes.add(4);
		helpConditions.put("type", helpTypes);
		helpConditions.put("status", 2);
		Page<PopInfoDO> pageHelp = popService.getPageByConditions(pageParam, helpConditions);
		List<PopInfoDO> popHelps = pageHelp.getResult();
		model.addAttribute("helpIndexs", popHelps);
//新
		Map<String, Object> newhelpConditions = new HashMap<String, Object>();
		List<Integer> newhelpTypes = new ArrayList<Integer>();
		newhelpTypes.add(6);
		newhelpConditions.put("type", newhelpTypes);
		newhelpConditions.put("status", 2);
		Page<PopInfoDO> newpageHelp = popService.getPageByConditions(pageParam, newhelpConditions);
		List<PopInfoDO> newpopHelps = newpageHelp.getResult();
		model.addAttribute("newHelpIndexs", newpopHelps);
//end		
		
		
        //bannerNews
       Map<String,Object> conditionNews = new HashMap<String, Object>();
        PageParam bannerPageParam = new PageParam(1,15);
       List<Integer> typesNews = new ArrayList<Integer>();
        typesNews.add(100);
        conditionNews.put("type", typesNews);
        conditionNews.put("status", 2);

        Page<PopInfoDO> bannerNewsPage = popService.getPageByConditions(bannerPageParam, conditionNews);
        List<PopInfoDO> bannerNews = bannerNewsPage.getResult();
        if(ListUtil.isNotEmpty(bannerNews)){
            Collections.sort(bannerNews, new ComparatorPop());
        }
        model.addAttribute("bannerNews", bannerNews);

        Map<String,Object> conditionRightNews = new HashMap<String, Object>();
        PageParam rightPageParam = new PageParam(1,2);
        List<Integer> typesRightNews = new ArrayList<Integer>();
        typesRightNews.add(101);
        conditionRightNews.put("type", typesRightNews);
        conditionRightNews.put("status", 2);

        Page<PopInfoDO> bannerRightNewsPage = popService.getPageByConditions(rightPageParam, conditionRightNews);
        List<PopInfoDO> bannerRightNews = bannerRightNewsPage.getResult();
        model.addAttribute("bannerRightNews", bannerRightNews);

        Map<String,Object> conditionFriendLink = new HashMap<String, Object>();
        PageParam linkPageParam = new PageParam(1,100);
        List<Integer> typesFriendLink = new ArrayList<Integer>();
        typesFriendLink.add(102);
        conditionFriendLink.put("type", typesFriendLink);
        conditionFriendLink.put("status", 2);

        Page<PopInfoDO> friendLinkPage = popService.getPageByConditions(linkPageParam, conditionFriendLink);
        List<PopInfoDO> friendLinks = friendLinkPage.getResult();
        if(ListUtil.isNotEmpty(friendLinks)){
            Collections.sort(friendLinks, new ComparatorPop());
        }
        model.addAttribute("friendLinks", friendLinks);
        QueryTradeDetailUserOrder queryTradeDetailUserOrder = new QueryTradeDetailUserOrder();
        queryTradeDetailUserOrder.setRoleEnum(SysUserRoleEnum.INVESTOR);
        queryTradeDetailUserOrder.setTransferPhase(DivisionPhaseEnum.ORIGINAL_PHASE);
        queryTradeDetailUserOrder.setPageSize(10);
        QueryBaseBatchResult<TradeDetailInfo> tradeDetailBatchResult = tradeBizQueryService.queryTradeDetailUserPage(queryTradeDetailUserOrder);
        model.addAttribute("investDetail",tradeDetailBatchResult.getPageList());
        //首页展示项目信息  全本tz
		QueryLoanTradeOrder tradeOrder = new QueryLoanTradeOrder();
		tradeOrder.setPageNumber(1);
		tradeOrder.setPageSize(2);
		List<String> List = new ArrayList<String>();
		List.add("1");
		List.add("2");
		List.add("3");
		List.add("6");
		List.add("8");
		tradeOrder.setStatus(List);
		tradeOrder.setInsureWay(InsureWayEnum.GUARANTEE.code());
		QueryBaseBatchResult<LoanDemandTradeVOInfo> result1 = tradeBizQueryService
			.searchLoanTradeCommonQuery(tradeOrder);
		QueryLoanTradeOrder tradeOrder2 = new QueryLoanTradeOrder();
		tradeOrder2.setPageNumber(1);
		tradeOrder2.setPageSize(2);
		tradeOrder2.setStatus(List);
		tradeOrder2.setInsureWay(InsureWayEnum.CREDIT.code());
		QueryBaseBatchResult<LoanDemandTradeVOInfo> result2 = tradeBizQueryService
				.searchLoanTradeCommonQuery(tradeOrder2);
        
		model.addAttribute("page1", PageUtil.getCovertPage(result1));
		model.addAttribute("page2", PageUtil.getCovertPage(result2));
		//end
		return vm_path + "front/index/indexs.vm";
	}

	@RequestMapping("baidu_verify_zaGcZx4UEc.html")
	public String index2(PageParam pageParam, Model model) throws Exception {
		return vm_path + "front/index/baidu_verify_zaGcZx4UEc.html";
	}
	@RequestMapping("webscan_360_cn.html")
	public String index3(PageParam pageParam, Model model) throws Exception {
		return vm_path + "front/index/webscan_360_cn.html";
	}
	@RequestMapping("sitemap.xml")
	public String index4(PageParam pageParam, Model model) throws Exception {
		return vm_path + "front/index/sitemap1.xml";
	}
	@RequestMapping("sitemap.html")
	public String index5(PageParam pageParam, Model model) throws Exception {
		return vm_path + "front/index/sitemap2.html";
	}
}
