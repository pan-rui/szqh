package com.yjf.yrd.authority;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import com.yjf.yrd.authority.impl.AuthorityServiceImpl;
import com.yjf.yrd.dataobject.Permission;
import com.yjf.yrd.session.SessionLocalManager;
/**
 * 
 *                       
 * @Filename DefaultWebAppAuthorityVerifier.java
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
 * 默认的web应用权限验证器
 */
public class DefaultWebAppAuthorityVerifier implements WebAppAuthorityVerifier {

	/**
	 *正则${:}
	 */
	protected static Pattern pattern = Pattern.compile("\\$\\{[a-zA-Z0-9.]{1,}:[a-zA-Z0-9.]{1,}\\}");
	
	@Override
	public boolean checkPermission(HttpServletRequest request) {
		String uri = request.getServletPath();
		return checkPermission(uri);
	}

	@Override
	public String getBackOperate(HttpServletRequest request) {
		String uri = request.getServletPath();
		String backOperate = getBackOperate(uri);
		return analyze(backOperate, request);
	}

	@Override
	public String getDefault() {
		return "/";
	}

	/**
	 * 解析回执操作的URL
	 * @param backOperate
	 * @return
	 */
	private String analyze(String backOperate, HttpServletRequest request){
		String temp = backOperate;
		int index = temp.indexOf('?');
		if(index == -1){
			return temp;
		}
		Matcher matcher = null;
		while((matcher = pattern.matcher(temp)).find()){
			temp = matcher.replaceFirst(build(matcher.group(), request));
		}
		return temp;
	}
	
	/**
	 * 获取回执操作的url
	 * @return
	 */
	private String getBackOperate(String uri){
		List<Permission> permissons = AuthorityServiceImpl.getAllPermissions();
		for (Permission permission : permissons) {
			if(match(uri, permission)){
				return permission.getBackOperate();
			}
		}
		return "";
	}
	
	/**
	 * 验证权限
	 * @return
	 */
	private boolean checkPermission(String uri){
		List<Permission> permissons = SessionLocalManager.getPermissions();
		for (Permission permission : permissons) {
			if(match(uri, permission)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 匹配
	 * @param uri
	 * @param permission
	 * @return
	 */
	private boolean match(String uri, Permission permission){
		Pattern p = Pattern.compile(permission.getOperate().replace("*", ".*").replace("?", "\\?")); 
		Matcher matcher = p.matcher(uri);
		return matcher.matches();
	}
    
	/**
	 * 根据el表达式构造参数
	 * @param el ${}
	 * @param request
	 * @return
	 */
	private String build(String el, HttpServletRequest request){
		String temp = el.substring(2, el.length() - 1);
		String[] s = temp.split(":");
		return s[0] + "=" + getValue(s[1], request);
	}
    
	/**
	 * 获取值
	 * @param key
	 * @param request
	 * @return
	 */
	private String getValue(String key, HttpServletRequest request) {
		if(key.equalsIgnoreCase("RequestURL")){
			return request.getRequestURL().toString();
		}else if(key.equalsIgnoreCase("RequestURI")){
			return request.getRequestURI();
		}else if(key.equalsIgnoreCase("ServletPath")){
			return request.getServletPath();
		}else{
			String value = request.getParameter(key);
			if(value != null){
				return value;
			}else{
				return "";
			}
		}
	}
}
