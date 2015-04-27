package com.yjf.yrd.web.util;

import com.yjf.yrd.util.AppConstantsUtil;

public class WebConstant {

	public static String getYjfloginurl() {
		return AppConstantsUtil.getYjfUrl()
				+ "/anon/oauth/index.htm?redirect="
				+ AppConstantsUtil.getHostHttpUrl()
				+ "/openApi/yjfLogin.htm&registerUrl="
				+ AppConstantsUtil.getHostHttpUrl()
				+ "/anon/investorsOpen";
	}
	public static String getYjfregisterurl() {
		return AppConstantsUtil.getYjfUrl()
				+ "/anon/oauth/index.htm?redirect="
				+ AppConstantsUtil.getHostHttpUrl()
				+ "/openApi/yjfLogin.htm&registerUrl="
				+ AppConstantsUtil.getHostHttpUrl()
				+ "/anon/investorsOpen";
	}
	
}
