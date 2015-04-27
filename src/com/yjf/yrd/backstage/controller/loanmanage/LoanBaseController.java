package com.yjf.yrd.backstage.controller.loanmanage;

import java.util.ArrayList;
import java.util.List;

import com.yjf.yrd.base.BaseAutowiredController;
import com.yjf.yrd.user.info.InstitutionsInfo;
import com.yjf.yrd.user.info.UserInfo;
import com.yjf.yrd.user.query.order.UserRoleQueryOrder;
import com.yjf.yrd.ws.enums.SysUserRoleEnum;
import com.yjf.yrd.ws.enums.UserTypeEnum;

public class LoanBaseController extends BaseAutowiredController {
	@Override
	protected String[] getDateInputNameArray() {
		return new String[] { "minDemandDate", "maxDemandDate", "deadline", "investAvlDate" };
	}
	
	@Override
	protected String[] getMoneyInputNameArray() {
		return new String[] { "minLoanAmount", "maxLoanAmount" };
	}
	
	protected List<InstitutionsInfo> getInfos(long roleId) throws Exception {
		UserRoleQueryOrder userRoleQueryOrder = new UserRoleQueryOrder();
        if(roleId != SysUserRoleEnum.SPONSOR.getValue()){
            userRoleQueryOrder.setType(UserTypeEnum.JG);
        }
		userRoleQueryOrder.setRoleEnum(SysUserRoleEnum.getByValue((int) roleId));
		userRoleQueryOrder.setPageSize(500);
		List<UserInfo> infos = userQueryService.queryRoleUserInfo(userRoleQueryOrder).getPageList();
		List<InstitutionsInfo> institutionsInfos = new ArrayList<InstitutionsInfo>();
		for (UserInfo userInfo : infos) {
			InstitutionsInfo institutionsInfo = new InstitutionsInfo();
			institutionsInfo.setEnterpriseName(userInfo.getRealName() +" "+userInfo.getUserName());
			institutionsInfo.setUserId(userInfo.getUserId());
			institutionsInfos.add(institutionsInfo);
		}
		return institutionsInfos;
	}
}
