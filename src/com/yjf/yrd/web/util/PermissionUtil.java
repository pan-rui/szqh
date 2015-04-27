package com.yjf.yrd.web.util;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.yjf.yrd.dataobject.Permission;
import com.yjf.yrd.session.SessionLocalManager;


public class PermissionUtil {
	
	public static int check(String code){
		List<Permission> permissions = SessionLocalManager.getPermissions();
		for (Permission permission : permissions) {
			Pattern p = Pattern.compile(permission.getOperate().replace("*", ".*").replace("?", "\\?"));
			Matcher matcher = p.matcher(code);
			if(matcher.matches()){
				return 1;
			}
		}
		return 0;
	}

}
