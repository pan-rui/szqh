package com.yjf.yrd.authority;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yjf.yrd.session.SessionLocalManager;

/**
 * 
 * 
 * @Filename AuthorityFilter.java
 * 
 * @Description
 * 
 * @Version 1.0
 * 
 * @Author yhl
 * 
 * @Email yhailong@yiji.com
 * 
 * @History <li>Author: yhl</li> <li>Date: 2013-6-9</li> <li>Version: 1.0</li>
 *          <li>Content: create</li> 权限管理控制器
 */
public class AuthorityFilter implements Filter {
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	/**
	 * 正则${:}
	 */
	protected static Pattern pattern = Pattern
			.compile("\\$\\{[a-zA-Z0-9.]{1,}:[a-zA-Z0-9.]{1,}\\}");
	/**
	 * 权限验证器
	 */
	protected WebAppAuthorityVerifier webAppAuthorityVerifier = null;

	protected static final String DEDUCT_OPERATE = "/deduct/*";

	private static final String[] urls = { "/uploadfile/", "/styles/","/img/","/css/",
			"/images/", "/resources/", "/bank/", "/anon/", "/resources/",
			"/help", "/upload/", "/staticFiles/", "/openApi/","/loanRequest/",
			"/backstage/userManage/common/", "/backstage/checkBorrower",
			"/backstage/getRealName", "/backstage/checkguaranteeLicenceNo",
			"/backstage/guaranteeAuthCheck", "/backstage/queryRuleInfo",
			"/backstage/updatePdfUrl", "/backstage/backstageIdex",
			"/backstagelogin/", "/backstage/nopermission",
			"/backstage/updateFileUpLoadUrl", "/appNew/", "/app/",
			"/backstage/lottery/" };

	@Override
	public void destroy() {

	}

	@Override
	public void doFilter(ServletRequest arg0, ServletResponse arg1,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) arg0;
		HttpServletResponse response = (HttpServletResponse) arg1;
		String uri = request.getServletPath();
		logger.info("请求的URI:" + uri);
		for (String s : urls) {
			if (uri.startsWith(s)) {
				chain.doFilter(request, response);
				return;
			}
		}
		if (webAppAuthorityVerifier.checkPermission(request)) {
			Pattern p = Pattern.compile(DEDUCT_OPERATE.replace("*", ".*")
					.replace("?", "\\?"));
			Matcher matcher = p.matcher(uri);
			if (matcher.matches()) {
				if (SessionLocalManager.getSessionLocal() != null) {
					chain.doFilter(request, response);
				} else {
					String backOperate = webAppAuthorityVerifier
							.getBackOperate(request);
					if (backOperate == null || backOperate.length() < 1) {
						backOperate = webAppAuthorityVerifier.getDefault();
					}
					response.sendRedirect(backOperate);
				}
			} else {
				chain.doFilter(request, response);
			}
		} else {
			String backOperate = webAppAuthorityVerifier
					.getBackOperate(request);
			if (backOperate == null || backOperate.length() < 1) {
				backOperate = webAppAuthorityVerifier.getDefault();
			}
			response.sendRedirect(backOperate);
		}
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
		String className = config.getInitParameter("webAppAuthorityVerifier");
		try {
			if (className != null && className.length() > 0) {
				webAppAuthorityVerifier = (WebAppAuthorityVerifier) (Class
						.forName(className, true, Thread.currentThread()
								.getContextClassLoader()).newInstance());
				return;
			}
		} catch (Exception e) {
			logger.error("className={} create error", className);
		}
		webAppAuthorityVerifier = new DefaultWebAppAuthorityVerifier();
	}
}
