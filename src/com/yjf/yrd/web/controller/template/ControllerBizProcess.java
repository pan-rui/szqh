/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved.
 */

/*
 * 修订记录：
 * Administrator 上午11:45:15 创建
 */
package com.yjf.yrd.web.controller.template;

import java.util.Map;

import com.yjf.yrd.ws.result.YrdBaseResult;

/**
 * 
 * 
 * @author Administrator
 * 
 */
public interface ControllerBizProcess {
	YrdBaseResult process(Map<String, String> param);
}
