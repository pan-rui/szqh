package com.yjf.yrd.front.controller.app.newVers.e8Trunck;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yjf.common.lang.util.ListUtil;
import com.yjf.yrd.base.BaseAutowiredController;
import com.yjf.yrd.dal.dataobject.SysParamDO;
import com.yjf.yrd.dataobject.AppPopInfoDO;
import com.yjf.yrd.pop.IAppPopService;

/**
 * @Filename appNewUpdateController.java
 * 
 * @Description
 * 
 * @Version 2.0
 * 
 * @Author zhaohaibing
 * 
 * @Email abing@yiji.com
 * 
 *        Date: 2014-9-1
 * 
 */

@Controller
@RequestMapping("appNew")
public class AppNewUpdateController extends BaseAutowiredController {

	@Autowired
	IAppPopService popService;

	/**
	 * app版本更新:暂无功能
	 * 
	 * @throws IOException
	 * 
	 * */
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping("appUpdateInfo.htm")
	public JSONObject appUpdatVers(HttpServletResponse response, int verCode,
			String verName, String channelCode, String type) throws IOException {

		JSONObject json = new JSONObject();
		Map<String, String> updateInfo = new HashMap<String, String>();
		SysParamDO result = sysParameterService
				.getSysParameterValueDO("SYS_APP_UPDATE_" + type);
		// int
		// forceUpdateCode0=Integer.parseInt(result.getExtendAttributeOne());
		// int
		// optionalUpdateCode0=Integer.parseInt(result.getExtendAttributeTwo());
		// if(verCode < forceUpdateCode0){
		updateInfo.put("updateUrl", result.getParamValue());
		updateInfo.put("optionalUpdateCode", result.getExtendAttributeTwo());
		updateInfo.put("forceUpdateCode", result.getExtendAttributeOne());
		updateInfo.put("updateInfo", result.getDescription());
		json.put("updateInfo", updateInfo);
		json.put("code", "1");
		json.put("message", "有最新版本：版本号（" + result.getExtendAttributeTwo() + "）");
		logger.info("更新地址：{}", result.getParamValue());
		// }else {
		// json.put("code","0");
		// json.put("message","已经是最新版本");
		// }

		return json;

	}

	/**
	 * 帮助中心内容是否有更新
	 * 
	 * @throws IOException
	 * 
	 * */
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping("appHelpRefresh.htm")
	public JSONObject appHelpRefresh(HttpServletResponse response,
			long updateTime) throws IOException {
		JSONObject json = new JSONObject();
		if (updateTime < getUpdateTime()) {
			json.put("code", "1");
			json.put("result", "1");
			json.put("message", "当前有可更新内容");
		} else {
			json.put("code", "0");
			json.put("result", "0");
			json.put("message", "当前没有可更新内容");
		}

		return json;

	}

	/**
	 * 获取帮助中心最近更新时间
	 * */
	public long getUpdateTime() {
		Map<String, Object> conditions = new HashMap<String, Object>();
		List<Integer> types = new ArrayList<Integer>();
		types.add(4);
		types.add(5);
		conditions.put("type", types);
		conditions.put("status", 2);
		List<AppPopInfoDO> showList = popService
				.getListByConditions(conditions);
		long leastTime = 0;
		if (ListUtil.isNotEmpty(showList)) {
			for (AppPopInfoDO info : showList) {
				if (info.getAddTime().getTime() > leastTime) {
					leastTime = info.getAddTime().getTime();
				}
			}
		}
		return leastTime;

	}

	/**
	 * 跳转到上传版本页面
	 * */
	@RequestMapping("updateVers/{type}")
	public String updateVersController(@PathVariable String type, Model model) {
		SysParamDO sysParamDO = sysParameterService
				.getSysParameterValueDO("SYS_APP_UPDATE_" + type);
		model.addAttribute("info", sysParamDO);
		return "backstage/appUpLoade/" + type + "_update.vm";
	}

}
