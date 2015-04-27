package com.yjf.yrd.security.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.support.WebApplicationContextUtils;

import com.yjf.bm.remote.boot.Bootstrap;
import com.yjf.bm.remote.service.RemoteService;
import com.yjf.yrd.session.common.Constant;

/**
 * 
 * 
 * @Filename RemoteSessionFilter.java
 * 
 * @Description
 * 
 * @Version 1.0
 * 
 * @Author yhl
 * 
 * @Email yhailong@yiji.com
 * 
 * @History <li>Author: yhl</li> <li>Date: 2013-6-25</li> <li>Version: 1.0</li>
 * <li>Content: create</li> 远程session过滤器
 */
public class RemoteSessionFilter implements Filter {
	
	private final String prefix = "/WEB-INF/";
	/**
	 * 是否启动远程服务
	 */
	private boolean server = false;
	/**
	 * 远程服务配置文件
	 */
	private String remoteConfigLocation = "remote.properties";
	
	@Override
	public void destroy() {
		if (server) {
			Bootstrap.shutdown();
		}
	}
	
	@Override
	public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain arg2)
																						throws IOException,
																						ServletException {
		HttpServletRequest request = (HttpServletRequest) arg0;
		Constant.setSessionId(request.getSession().getId());
		
		arg2.doFilter(arg0, arg1);
	}
	
	@Override
	public void init(FilterConfig arg0) throws ServletException {
		String temp = arg0.getInitParameter("server");
		if (temp != null && temp.length() > 0) {
			server = Boolean.parseBoolean(temp);
		}
		temp = arg0.getInitParameter("timeout");
		if (temp != null && temp.length() > 0) {
			Constant.timeout = Long.parseLong(temp);
		}
		temp = arg0.getInitParameter("host");
		if (temp != null && temp.length() > 0) {
			Constant.host = temp;
		}
		temp = arg0.getInitParameter("port");
		if (temp != null && temp.length() > 0) {
			Constant.port = Integer.parseInt(temp);
		}
		temp = arg0.getInitParameter("name");
		if (temp != null && temp.length() > 0) {
			Constant.name = temp;
		}
		temp = arg0.getInitParameter("remoteConfigLocation");
		if (remoteConfigLocation != null && remoteConfigLocation.length() > 0) {
			remoteConfigLocation = temp;
		}
		ServletContext servletContext = arg0.getServletContext();
		if (server) {
			RemoteService.setApplicationContext(WebApplicationContextUtils
				.getWebApplicationContext(servletContext));
			Bootstrap.startup(servletContext.getRealPath(prefix + remoteConfigLocation),
				Constant.timeout > 0);
		}
	}
	
}
