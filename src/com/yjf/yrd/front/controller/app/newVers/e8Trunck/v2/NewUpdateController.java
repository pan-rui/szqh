package com.yjf.yrd.front.controller.app.newVers.e8Trunck.v2;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yjf.yrd.base.BaseAutowiredController;
import com.yjf.yrd.dal.dataobject.SysParamDO;
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
 * @Date: 2014-12-30
 * 
 */

@Controller
@RequestMapping("app")
public class NewUpdateController extends BaseAutowiredController {

	@Autowired
	IAppPopService popService;

	/**
	 * app版本更新
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
		Map<String, String> map = new HashMap<String, String>();
		SysParamDO result = sysParameterService
				.getSysParameterValueDO("SYS_APP_UPDATE_" + type);
		if (result != null) {
			map.put("updateUrl", result.getParamValue());
			map.put("optionalUpdateCode", result.getExtendAttributeTwo());
			map.put("forceUpdateCode", result.getExtendAttributeOne());
			map.put("updateInfo", result.getDescription());
			json.put("updateInfo", AppCommonUtil.cleanNull(map));
			json.put("code", 1);
			json.put("message", "有最新版本：版本号（" + result.getExtendAttributeTwo()
					+ "）");
		} else {
			json.put("code", 0);
			json.put("message", "无版本信息");
		}
		return json;

	}

	/**
	 * 跳转到上传版本页面
	 * */
	@RequestMapping("updateVers/{type}")
	public String updateVersController(@PathVariable String type, Model model) {
		SysParamDO sysParamDO = sysParameterService
				.getSysParameterValueDO("SYS_APP_UPDATE_" + type);
		model.addAttribute("info", sysParamDO);
		return "backstage/appManager/upDate/" + type + "_update.vm";
	}

}
