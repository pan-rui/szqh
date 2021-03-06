/**
 * www.yiji.com Inc.
 * Copyright (c) 2011 All Rights Reserved.
 */
package com.yjf.yrd.web.util;

import javax.servlet.http.HttpServletRequest;

import com.yjf.common.log.Logger;
import com.yjf.common.log.LoggerFactory;
import com.yjf.yrd.util.UploadFileUtils;

/**
 *                       
 * @Filename FileUploadUtil.java
 *
 * @Description 
 *
 * @Version 1.0
 *
 * @Author qichunhai
 *
 * @Email qchunhai@yiji.com
 *       
 * @History
 *<li>Author: qichunhai</li>
 *<li>Date: 2014-4-8</li>
 *<li>Version: 1.0</li>
 *<li>Content: create</li>
 *
 */
public class FileUploadUtils {
	private static final Logger	logger	= LoggerFactory.getLogger(FileUploadUtils.class);
	
	/**
	 * @param req
	 * @return
	 */
	
	public static String[] getStaticFilesImgPath(HttpServletRequest req, String fileOrgName) {
		
		return UploadFileUtils.getFilePath(req, "uploadfile", "images", fileOrgName);
	}
	
	public static String[] getStaticFilesPdfPath(HttpServletRequest req, String fileOrgName) {
		return UploadFileUtils.getFilePath(req, "uploadfile", "pfd", fileOrgName);
	}
	
	public static String getStaticFilesPdfDir() {
		return UploadFileUtils.getStaticFilesPdfDir();
	}
	
	public static String getStaticFilesImgDir() {
		return UploadFileUtils.getStaticFilesImgDir();
	}
	
}
