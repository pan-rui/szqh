package com.yjf.yrd.backstage.controller.usermanage;

import com.yjf.yrd.base.BaseAutowiredController;
import com.yjf.yrd.user.order.RegisterBaseOrder;
import com.yjf.yrd.util.NumberUtil;
import com.yjf.yrd.ws.enums.SysUserRoleEnum;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

public class UserBaseInfoBaseController extends BaseAutowiredController {
	protected void setRoles(HttpServletRequest request,
			RegisterBaseOrder registerOrder) {
		String[] roleIds = request.getParameterValues("roleIds");
		if (roleIds != null) {
			List<SysUserRoleEnum> roles = new ArrayList<SysUserRoleEnum>();
			boolean isPublic =false; 
			for (int i = 0; i < roleIds.length; i++) {
				roles.add(SysUserRoleEnum.getByValue(NumberUtil
                        .parseInt(roleIds[i])));
				if(SysUserRoleEnum.PUBLIC.getValue()== NumberUtil
						.parseInt(roleIds[i]))
				{
					isPublic=true;
				}
			}
			if(!isPublic)
			{
				roles.add(SysUserRoleEnum.PUBLIC);
			}
			registerOrder.setRole(roles);
		}
	}
}
