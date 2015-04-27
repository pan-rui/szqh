package com.yjf.yrd.authority;

import javax.servlet.http.HttpServletRequest;

/**
 * 
 *                       
 * @Filename WebAppAuthorityVerifier.java
 *
 * @Description 
 *
 * @Version 1.0
 *
 * @Author yhl
 *
 * @Email yhailong@yiji.com
 *       
 * @History
 *<li>Author: yhl</li>
 *<li>Date: 2013-6-8</li>
 *<li>Version: 1.0</li>
 *<li>Content: create</li>
 * web应用权限验证器接口
 */
public interface WebAppAuthorityVerifier {
	/**
	 * 根据request验证权限
	 * @param request
	 * @return
	 */
	public boolean checkPermission(HttpServletRequest request);
	/**
	 * 获取回调url
	 * @param request
	 * @return
	 */
	public String getBackOperate(HttpServletRequest request);
	/**
	 * 默认条件
	 * @return
	 */
	public String getDefault();

}
