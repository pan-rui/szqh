package com.yjf.yrd.front.controller.help;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import com.yjf.yrd.cooperate.ICooperateService;
import com.yjf.yrd.util.StringUtil;
import com.yjf.yrd.web.util.PageUtil;
import com.yjf.yrd.ws.info.CooperateInfo;
import com.yjf.yrd.ws.order.CooperateQueryOrder;
import com.yjf.yrd.ws.service.query.result.QueryBaseBatchResult;
import org.aspectj.weaver.NewConstructorTypeMunger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.yjf.common.lang.util.ListUtil;
import com.yjf.common.util.StringUtils;
import com.yjf.yrd.dataobject.PopInfoDO;
import com.yjf.yrd.page.PageParam;
import com.yjf.yrd.page.Page;
import com.yjf.yrd.pop.IPopModuleService;
import com.yjf.yrd.pop.IPopService;
import com.yjf.yrd.pop.impl.ComparatorPop;
import com.yjf.yrd.ws.info.PopModuleVOInfo;

/**
 * 
 * 
 * @Filename HelpCenterController.java
 * 
 * @Description
 * 
 * @Version 1.0
 * 
 * @Author zjl
 * 
 * @Email zjialin@yiji.com
 * 
 * @History <li>Author: zjl</li> <li>Date: 2013-8-2</li> <li>Version: 1.0</li>
 * <li>Content: create</li>
 * 
 */
@Controller
@RequestMapping("help")
public class HelpCenterController {
	
	private String VM_PATH = "/front/help/";
	@Autowired
	IPopService popService;
	
	@Autowired
	IPopModuleService popModuleService;
    @Autowired
    ICooperateService cooperateService;
	
	@RequestMapping("center/{tree}")
	public String center(HttpSession session, @PathVariable int tree, Model model) {
		List<PopInfoDO> showList = getShowList();
		model.addAttribute("helps", showList);
		return vm(tree);
	}
	
	@RequestMapping("center")
	public String helpCenter(HttpSession session, Model model) {
		List<PopInfoDO> showList = getShowList();
		model.addAttribute("helps", showList);
		Map<String, Object> conditions = new HashMap<String, Object>();
		List<Integer> types = new ArrayList<Integer>();
		types.add(5);
		conditions.put("type", types);
		conditions.put("status", 2);
		List<PopInfoDO> moduleList = popService.getListByConditions(conditions);
		model.addAttribute("helpModules", moduleList);
		List<PopInfoDO> list=getShowList();
			boolean  sad=true;
			if(ListUtil.isNotEmpty(moduleList)){
				for(int i=0;i<moduleList.size();i++){
					
					if(sad){
						if(ListUtil.isNotEmpty(list)){
							for(int j=0;j<list.size();j++){
								if(list.get(j).getParentId()==moduleList.get(i).getPopId()){
									model.addAttribute("notetitle",list.get(j).getTitle() );
									model.addAttribute("note",list.get(j).getContent() );
									sad=false;
									break;
								}
							}
						}
						
					}else{
						break;
					}
					
				}
			}

		
		model.addAttribute("helps", getShowList());
		return VM_PATH + "helpCenter.vm";
	}
	
	private List<PopInfoDO> getShowList() {
		Map<String, Object> conditions = new HashMap<String, Object>();
		List<Integer> types = new ArrayList<Integer>();
		types.add(4);
		types.add(5);
		conditions.put("type", types);
		conditions.put("status", 2);
		List<PopInfoDO> showList = popService.getListByConditions(conditions);
		return showList;
	}
	@RequestMapping("popHelp/{popId}")
	public String popHelp(HttpSession session, @PathVariable long popId, Model model,String type) {
		model.addAttribute("popHelp", popService.getByPopId(popId));
		Map<String, Object> conditions = new HashMap<String, Object>();
		List<Integer> types = new ArrayList<Integer>();
		types.add(4);
		conditions.put("type", types);
		conditions.put("parentId", popId);
		conditions.put("status", 2);
		List<PopInfoDO> showList = popService.getListByConditions(conditions);
		model.addAttribute("helps", getShowList());
		model.addAttribute("childs", showList);
		
		Map<String, Object> conditions2 = new HashMap<String, Object>();
		List<Integer> types2 = new ArrayList<Integer>();
		types2.add(5);
		conditions2.put("type", types2);
		conditions2.put("status", 2);
		List<PopInfoDO> moduleList = popService.getListByConditions(conditions2);
		model.addAttribute("helpModules", moduleList);
		model.addAttribute("type",type);
		return VM_PATH + "helpCenter.vm";
	}
	//新帮助中心版常见问题
	@RequestMapping("newCenter")
	public String newHelpCenter(HttpSession session, Model model) {
		List<PopInfoDO> showList = getNewShowList();
		model.addAttribute("helps", showList);
		Map<String, Object> conditions = new HashMap<String, Object>();
		List<Integer> types = new ArrayList<Integer>();
		types.add(7);
		conditions.put("type", types);
		conditions.put("status", 2);
		List<PopInfoDO> moduleList = popService.getListByConditions(conditions);
		model.addAttribute("helpModules", moduleList);
		List<PopInfoDO> list=getNewShowList();
			boolean  sad=true;
			if(ListUtil.isNotEmpty(moduleList)){
				for(int i=0;i<moduleList.size();i++){
					
					if(sad){
						if(ListUtil.isNotEmpty(list)){
							for(int j=0;j<list.size();j++){
								if(list.get(j).getParentId()==moduleList.get(i).getPopId()){
									model.addAttribute("notetitle",list.get(j).getTitle() );
									model.addAttribute("note",list.get(j).getContent() );
									sad=false;
									break;
								}
							}
						}
						
					}else{
						break;
					}
					
				}
			}

		
		model.addAttribute("helps", getNewShowList());
		return VM_PATH + "newHelpCenter.vm";
	}
	
	private List<PopInfoDO> getNewShowList() {
		Map<String, Object> conditions = new HashMap<String, Object>();
		List<Integer> types = new ArrayList<Integer>();
		types.add(6);
		types.add(7);
		conditions.put("type", types);
		conditions.put("status", 2);
		List<PopInfoDO> showList = popService.getListByConditions(conditions);
		return showList;
	}
	
	@RequestMapping("newPopHelp/{popId}")
	public String newPopHelp(HttpSession session, @PathVariable long popId, Model model) {
		model.addAttribute("popHelp", popService.getByPopId(popId));
		Map<String, Object> conditions = new HashMap<String, Object>();
		List<Integer> types = new ArrayList<Integer>();
		types.add(6);
		conditions.put("type", types);
		conditions.put("parentId", popId);
		conditions.put("status", 2);
		List<PopInfoDO> showList = popService.getListByConditions(conditions);
		model.addAttribute("helps", getNewShowList());
		model.addAttribute("childs", showList);
		
		Map<String, Object> conditions2 = new HashMap<String, Object>();
		List<Integer> types2 = new ArrayList<Integer>();
		types2.add(7);
		conditions2.put("type", types2);
		conditions2.put("status", 2);
		List<PopInfoDO> moduleList = popService.getListByConditions(conditions2);
		model.addAttribute("helpModules", moduleList);
		return VM_PATH + "newHelpCenter.vm";
	}
	
	
	@RequestMapping("yptPopHelp/{popId}")
	public String yptPopHelp(HttpSession session, @PathVariable long popId, Model model ,String type) {
		List<PopInfoDO> leftShowList = getShowList();
		model.addAttribute("helps", leftShowList);
		Map<String, Object> leftConditions = new HashMap<String, Object>();
		List<Integer> leftTypes = new ArrayList<Integer>();
		leftTypes.add(5);
		leftConditions.put("type", leftTypes);
		leftConditions.put("status", 2);
		List<PopInfoDO> moduleList = popService.getListByConditions(leftConditions);
		model.addAttribute("helpModules", moduleList);
		
		
		model.addAttribute("popHelp", popService.getByPopId(popId));
		Map<String, Object> conditions = new HashMap<String, Object>();
		List<Integer> types = new ArrayList<Integer>();
		types.add(4);
		conditions.put("type", types);
		conditions.put("parentId", popId);
		conditions.put("status", 2);
		List<PopInfoDO> showList = popService.getListByConditions(conditions);
		model.addAttribute("type",type);
		model.addAttribute("helps", getShowList());
		model.addAttribute("childs", showList);
		return VM_PATH + "helpCenter1.vm";
	}
	//end
	//服务专区fxd
	@RequestMapping("fwCenter")
	public String fwHelpCenter(HttpSession session, Model model) {
		List<PopInfoDO> showList = getFwShowList();
		model.addAttribute("helps", showList);
		Map<String, Object> conditions = new HashMap<String, Object>();
		List<Integer> types = new ArrayList<Integer>();
		types.add(9);
		conditions.put("type", types);
		conditions.put("status", 2);
		List<PopInfoDO> moduleList = popService.getListByConditions(conditions);
		model.addAttribute("helpModules", moduleList);
		List<PopInfoDO> list=getFwShowList();
			boolean  sad=true;
			if(ListUtil.isNotEmpty(moduleList)){
				for(int i=0;i<moduleList.size();i++){
					
					if(sad){
						if(ListUtil.isNotEmpty(list)){
							for(int j=0;j<list.size();j++){
								if(list.get(j).getParentId()==moduleList.get(i).getPopId()){
									model.addAttribute("notetitle",list.get(j).getTitle() );
									model.addAttribute("note",list.get(j).getContent() );
									sad=false;
									break;
								}
							}
						}
						
					}else{
						break;
					}
					
				}
			}

		
		model.addAttribute("helps", getFwShowList());
		return VM_PATH + "fwHelpCenter.vm";
	}
	
	private List<PopInfoDO> getFwShowList() {
		Map<String, Object> conditions = new HashMap<String, Object>();
		List<Integer> types = new ArrayList<Integer>();
		types.add(8);
		types.add(9);
		conditions.put("type", types);
		conditions.put("status", 2);
		List<PopInfoDO> showList = popService.getListByConditions(conditions);
		return showList;
	}
	
	@RequestMapping("fwPopHelp/{popId}")
	public String fwPopHelp(HttpSession session, @PathVariable long popId, Model model) {
		model.addAttribute("popHelp", popService.getByPopId(popId));
		Map<String, Object> conditions = new HashMap<String, Object>();
		List<Integer> types = new ArrayList<Integer>();
		types.add(8);
		conditions.put("type", types);
		conditions.put("parentId", popId);
		conditions.put("status", 2);
		List<PopInfoDO> showList = popService.getListByConditions(conditions);
		model.addAttribute("helps", getFwShowList());
		model.addAttribute("childs", showList);
		
		Map<String, Object> conditions2 = new HashMap<String, Object>();
		List<Integer> types2 = new ArrayList<Integer>();
		types2.add(9);
		conditions2.put("type", types2);
		conditions2.put("status", 2);
		List<PopInfoDO> moduleList = popService.getListByConditions(conditions2);
		model.addAttribute("helpModules", moduleList);
		return VM_PATH + "fwHelpCenter.vm";
	}
	//end
	
	@RequestMapping("institutionIntroduction/{tree}")
	public String institutionIntroduction(HttpSession session, @PathVariable String tree,Model model) {
		session.setAttribute("tree", tree);
        // 前台合作机构图片
        CooperateQueryOrder cooperateOrder = new CooperateQueryOrder();

        cooperateOrder.setPageSize(1000);

        QueryBaseBatchResult<CooperateInfo> cooperateResult = cooperateService.queryCooperate(cooperateOrder);

        model.addAttribute("cooperateList", PageUtil.getCovertPage(cooperateResult));
        
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

        
		return VM_PATH + "institutionIntroduction" + tree + ".vm";
	}
	
	
	@RequestMapping("institutionIntroduction_yfb/{tree}")
	public String institutionIntroduction_yfb(PageParam pageParam, Model model,HttpSession session, @PathVariable String tree) {
		
		Map<String, Object> conditions = new HashMap<String, Object>();
		List<Integer> types = new ArrayList<Integer>();
		types.add(1);
		types.add(2);
		conditions.put("type", types);
		conditions.put("status", 2);
		pageParam.setPageSize(5);
		Page<PopInfoDO> page = popService.getPageByConditions(pageParam, conditions);
		List<PopInfoDO> popNotices = page.getResult();
		model.addAttribute("noticesIndexs", popNotices);
		Map<String, Object> helpConditions = new HashMap<String, Object>();
		List<Integer> helpTypes = new ArrayList<Integer>();
		helpTypes.add(4);
		helpConditions.put("type", helpTypes);
		helpConditions.put("status", 2);
		Page<PopInfoDO> pageHelp = popService.getPageByConditions(pageParam, helpConditions);
		List<PopInfoDO> popHelps = pageHelp.getResult();
		model.addAttribute("helpIndexs", popHelps);
		session.setAttribute("tree", tree);
		return VM_PATH + "institutionIntroduction" + tree + ".vm";
	}
	
	
	private String vm(int tree) {
		return VM_PATH + "helpCenter" + tree + ".vm";
	}
	
	@RequestMapping("nopermission")
	public String hasNoPermission() {
		return VM_PATH + "nopermission.vm";
	}
	
	@RequestMapping("announcement/{popId}")
	public String announcement(HttpSession session, @PathVariable long popId, Model model) {
		model.addAttribute("popNotice", popService.getByPopId(popId));
		return VM_PATH + "Announcement.vm";
	}
	@RequestMapping("announcementCenter")
	public String announcementCenter(HttpSession session, Model model) {
		Map<String, Object> conditions = new HashMap<String, Object>();
		List<Integer> types = new ArrayList<Integer>();
		types.add(1);
		types.add(2);
		conditions.put("type", types);
		conditions.put("status", 2);
		List<PopInfoDO> announcements = popService.getListByConditions(conditions);
		model.addAttribute("announcements", announcements);
		model.addAttribute("announcementCenter", "true");
		return VM_PATH + "Announcement.vm";
	}
//将公告里面分2个
	//a
	@RequestMapping("announcementCenterOne")
	public String announcementCenter1(HttpSession session, Model model,Long popId) {
		Map<String, Object> conditions = new HashMap<String, Object>();
		List<Integer> types = new ArrayList<Integer>();
		types.add(1);
		conditions.put("type", types);
		conditions.put("status", 2);
		List<PopInfoDO> announcements = popService.getListByConditions(conditions);
		model.addAttribute("announcements", announcements);
		model.addAttribute("announcementCenter1", "true");
		model.addAttribute("announcementCenter", "true");
		if(popId!=null){
			model.addAttribute("popNotice", popService.getByPopId(popId));
		}
		if(ListUtil.isNotEmpty(announcements)&&popId==null){
			model.addAttribute("popNotice", announcements.get(0));
		}
		return VM_PATH + "Announcement.vm";
	}
	//b
	@RequestMapping("announcementCenterTwo")
	public String announcementCenter2(HttpSession session, Model model,Long popId) {
		Map<String, Object> conditions = new HashMap<String, Object>();
		List<Integer> types = new ArrayList<Integer>();
		types.add(2);
		conditions.put("type", types);
		conditions.put("status", 2);
		List<PopInfoDO> announcements = popService.getListByConditions(conditions);
		model.addAttribute("announcements", announcements);
		model.addAttribute("announcementCenter2", "true");
		model.addAttribute("announcementCenter", "true");
		if(popId!=null){
			model.addAttribute("popNotice", popService.getByPopId(popId));
		}
		if(ListUtil.isNotEmpty(announcements)&&popId==null){
			model.addAttribute("popNotice", announcements.get(0));
		}
		return VM_PATH + "Announcement.vm";
	}
	
	
	//资讯动态
	@RequestMapping("informationHelp/{popId}")
	public String informationHelp(HttpSession session, @PathVariable long popId, Model model) {
		model.addAttribute("popInformation", popService.getByPopId(popId));
		Map<String, Object> conditions = new HashMap<String, Object>();
		List<Integer> types = new ArrayList<Integer>();
		types.add(10);
		conditions.put("type", types);
		conditions.put("status", 2);
		List<PopInfoDO> informationHelp = popService.getListByConditions(conditions);
		//model.addAttribute("informationHelpFist", informationHelp.get(1));
		model.addAttribute("informationHelp", informationHelp);

		return VM_PATH + "informationHelp.vm";
	}
	
	@RequestMapping("informationHelpCenter")
	public String informationHelpCenter(HttpSession session, Model model) {
		Map<String, Object> conditions = new HashMap<String, Object>();
		List<Integer> types = new ArrayList<Integer>();
		
		types.add(10);
		conditions.put("type", types);
		conditions.put("status", 2);
		List<PopInfoDO> informationHelp = popService.getListByConditions(conditions);
		if(ListUtil.isNotEmpty(informationHelp)){
			model.addAttribute("informationHelpFist", informationHelp.get(0));
		}
		model.addAttribute("informationHelp", informationHelp);
		model.addAttribute("informationHelpCenter", "true");
		return VM_PATH + "informationHelp.vm";
	}
	
	//end
	
	//新闻
	@RequestMapping("newsHelp/{popId}")
	public String newsHelp(HttpSession session, @PathVariable long popId, Model model) {
		model.addAttribute("popNews", popService.getByPopId(popId));
		Map<String, Object> conditions = new HashMap<String, Object>();
		List<Integer> types = new ArrayList<Integer>();
		types.add(11);
		conditions.put("type", types);
		conditions.put("status", 2);
		List<PopInfoDO> informationHelp = popService.getListByConditions(conditions);
		//model.addAttribute("informationHelpFist", informationHelp.get(1));
		model.addAttribute("newsHelp", informationHelp);
		return VM_PATH + "newsHelp.vm";
	}
	
	@RequestMapping("newsHelpCenter")
	public String newsHelpCenter(HttpSession session, Model model) {
		Map<String, Object> conditions = new HashMap<String, Object>();
		List<Integer> types = new ArrayList<Integer>();
		
		types.add(11);
		conditions.put("type", types);
		conditions.put("status", 2);
		List<PopInfoDO> newsHelp = popService.getListByConditions(conditions);
		if(ListUtil.isNotEmpty(newsHelp)){
			model.addAttribute("newsHelpFist", newsHelp.get(0));
		}
		model.addAttribute("newsHelp", newsHelp);
		model.addAttribute("newsHelpCenter", "true");
		return VM_PATH + "newsHelp.vm";
	}
	//end
	
	
	//新版帮助中心前端start
	/**
	 * 在线状态的模块
	 * @param session
	 * @param model
	 * @param defaultMCode
	 * @return
	 */
	@RequestMapping("infoCenter")
	public String infoCenter(HttpSession session,Model model,String defaultMCode) {
		session.setAttribute("defaultMCode", defaultMCode);
		List<PopModuleVOInfo> moduleList = popModuleService.getOnlineModules();
		PopModuleVOInfo module= popModuleService.getPopModule(defaultMCode);
		if(module!=null){
			model.addAttribute("module", module);
		}
		model.addAttribute("moduleList", moduleList);
		//model.addAttribute("popInfoList", popInfoList);
		return VM_PATH + "pop_info_center.vm";
	}
	@RequestMapping("newInfoCenter")
	public String newInfoCenter(HttpSession session,PageParam pageParam,Model model,String defaultMCode,Long popId) {
		session.setAttribute("defaultMCode", defaultMCode);
		List<PopModuleVOInfo> moduleList = popModuleService.getOnlineModules();
		PopModuleVOInfo module= popModuleService.getPopModule(defaultMCode);
		if(module!=null){
			model.addAttribute("module", module);
		}
		model.addAttribute("moduleList", moduleList);
		
		Map<String, Object> conditions = new HashMap<String, Object>();
		List<Integer> types = new ArrayList<Integer>();
		if(ListUtil.isNotEmpty(moduleList)){
			for(PopModuleVOInfo popModule:moduleList){
				types.add(popModule.getType());
			}
			
		}
		conditions.put("type", types);
		conditions.put("status", 2);
		List<PopInfoDO> page = popService.getListByConditions(conditions);
		model.addAttribute("popList", page);
		if(popId!=null){
			PopInfoDO popInfo=popService.getByPopId(popId);
			model.addAttribute("popId", popId);
			if(popInfo!=null){
				model.addAttribute("popInfo", popInfo);
			}
		}else{
			Map<String, Object> conditions2 = new HashMap<String, Object>();
			List<Integer> types2 = new ArrayList<Integer>();
			if(module!=null){
				types2.add(module.getType());
				conditions2.put("types", types2);
			}
			conditions2.put("status", 2);
			List<PopInfoDO> page2 = popService.getListByConditions(conditions2);
			
			if(ListUtil.isNotEmpty(page2)){
			model.addAttribute("popInfo", page2.get(0));
			}
		}

		//model.addAttribute("popInfoList", popInfoList);
		return VM_PATH + "new_pop_info_center.vm";
	}
	
	
	
	/**
	 * 下线状态的模块
	 * @param session
	 * @param model
	 * @param defaultMCode
	 * @return
	 */
	@RequestMapping("offLineInfoCenter")
	public String offLineInfoCenter(HttpSession session,Model model,String defaultMCode) {
		session.setAttribute("defaultMCode", defaultMCode);
		List<PopModuleVOInfo> moduleList = popModuleService.getOfflineModules();
		PopModuleVOInfo module= popModuleService.getPopModule(defaultMCode);
		if(module!=null){
			model.addAttribute("module", module);
		}
		model.addAttribute("moduleList", moduleList);
		model.addAttribute("notOnline", true);
		return VM_PATH + "pop_info_center.vm";
	}
	
	
	
	@RequestMapping("showIndex") 
	public String showIndex(HttpSession session, PageParam pageParam,Model model,String moduleCode) {
		PopModuleVOInfo moduleInfo = popModuleService.getPopModule(moduleCode);
		model.addAttribute("moduleInfo",moduleInfo);
		List<PopModuleVOInfo> moduleList = popModuleService.getOnlineModules();
		Map<String, Object> conditions = new HashMap<String, Object>();
		if(moduleInfo.getType()!=0){
			conditions.put("type", moduleInfo.getType());
		}else{
			if(moduleList.size()!=0){
				conditions.put("type", moduleList.get(0).getType());
			}
		}
		conditions.put("status",2);//上线的
		
		Page<PopInfoDO> page = popService.getPageByConditionsNew(pageParam, conditions);
		model.addAttribute("page", page);
		return VM_PATH + "pop_info_index.vm";
	}
	
	@RequestMapping("toModule")
	public String toModule(HttpSession session, PageParam pageParam,Model model,String moduleCode,Long popId) {
		session.setAttribute("defaultMCode", moduleCode);
		PopModuleVOInfo moduleInfo = popModuleService.getPopModule(moduleCode);
		model.addAttribute("moduleInfo",moduleInfo);
		List<PopModuleVOInfo> moduleList = popModuleService.getOnlineModules();
		Map<String, Object> conditions = new HashMap<String, Object>();
		if(moduleInfo.getType()!=0){
			conditions.put("type", moduleInfo.getType());
		}else{
			if(moduleList.size()!=0){
				conditions.put("type", moduleList.get(0).getType());
			}
		}
		
		conditions.put("status", 2);//上线的
		
		model.addAttribute("showTop",moduleInfo.getShowTop());
		Page<PopInfoDO> page = popService.getPageByConditionsNew(pageParam, conditions);
		String vm ="";
		
		if("N".equals(moduleInfo.getShowTop())){  //显示列表
			model.addAttribute("page", page);
			if(page.getResult().size()>0){
				model.addAttribute("fristContent",page.getResult().get(0));
			}
			PopInfoDO popInfo = new PopInfoDO();
			if(popId!=null){
				popInfo= popService.getByPopIdNew(popId);
			}else{
				if(page.getResult().size()>0){
					popInfo=page.getResult().get(0);
				}
			}
			model.addAttribute("popInfo",popInfo);
			
			if(StringUtils.isEmpty(moduleInfo.getVmPage())){
				vm = "pop_info_list.vm"; //默认页面
			}else{
				vm = moduleInfo.getVmPage();
			}
			
		}else{ //只显示头一条详细信息
			List<PopInfoDO> list = page.getResult();
			PopInfoDO popInfo = new PopInfoDO();
			if(list.size()>0){
				popInfo = list.get(0);
			}
			model.addAttribute("popInfo",popInfo);
			
			if(StringUtils.isEmpty(moduleInfo.getVmPage())){
				vm = "pop_info_detail.vm";  //默认页面
			}else{
				vm = moduleInfo.getVmPage();
			}
		}
		return VM_PATH + vm;
	}
	
	@ResponseBody
	@RequestMapping("increaseHit")
	public Object increaseHit(HttpSession session, long popId) {
		JSONObject jsonobj = new JSONObject();
		try {
			popService.increaseHit(popId);
			jsonobj.put("code", 1);
			jsonobj.put("message", "执行成功！");
		} catch (Exception e) {
			jsonobj.put("code", 0);
			jsonobj.put("message", "执行失败！");
		}
		return jsonobj;
		
		
	}
	
	@RequestMapping("showDetail")
	public String showDetail(HttpSession session, long popId,String moduleCode, Model model) {
		PopModuleVOInfo moduleInfo = popModuleService.getPopModule(moduleCode);
		model.addAttribute("moduleInfo",moduleInfo);
		model.addAttribute("popInfo", popService.getByPopIdNew(popId));
		popService.increaseHit(popId);
		return VM_PATH + "pop_info_single.vm";
	}
	@RequestMapping("newShowDetail")
	public String newShowDetail(HttpSession session,PageParam pageParam,long popId,String moduleCode, Model model) {
		PopModuleVOInfo moduleInfo = popModuleService.getPopModule(moduleCode);
		model.addAttribute("moduleInfo",moduleInfo);
		model.addAttribute("popId",popId);
		model.addAttribute("popInfo", popService.getByPopIdNew(popId));
		Map<String, Object> conditions = new HashMap<String, Object>();
		if(moduleInfo!=null){
			conditions.put("type", moduleInfo.getType());
		}
		conditions.put("status", 2);
		model.addAttribute("page", popService.getPageByConditionsNew(pageParam, conditions));
		
		return VM_PATH + "pop_info_hidden_list.vm";
	}
	
	@RequestMapping("popInfoDetail")
	public String popInfoDetail(HttpSession session, long popId,String moduleCode, Model model) {
		PopModuleVOInfo moduleInfo = popModuleService.getPopModule(moduleCode);
		model.addAttribute("moduleInfo",moduleInfo);
		model.addAttribute("showTop",moduleInfo.getShowTop());
		model.addAttribute("popInfo", popService.getByPopIdNew(popId));
		popService.increaseHit(popId);
		return VM_PATH + "pop_info_detail.vm";
	}
	@RequestMapping("newPopInfoDetail")
	public String newPopInfoDetail(HttpSession session, long popId,String moduleCode, Model model) {
		PopModuleVOInfo moduleInfo = popModuleService.getPopModule(moduleCode);
		model.addAttribute("moduleInfo",moduleInfo);
		model.addAttribute("showTop",moduleInfo.getShowTop());
		model.addAttribute("popInfo", popService.getByPopIdNew(popId));
		popService.increaseHit(popId);
		return VM_PATH + "new_pop_info_detail.vm";
	}
	//end
	
	@RequestMapping("trb")
	public String trb() {
		return VM_PATH + "trb" + ".vm";
	}
	
}
