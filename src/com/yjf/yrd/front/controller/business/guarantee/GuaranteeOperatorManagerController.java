package com.yjf.yrd.front.controller.business.guarantee;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.yjf.yrd.base.UserAccountInfoBaseController;
import com.yjf.yrd.dataobject.OperatorInfoDO;
import com.yjf.yrd.page.Page;
import com.yjf.yrd.page.PageParam;
import com.yjf.yrd.session.SessionLocal;
import com.yjf.yrd.session.SessionLocalManager;
import com.yjf.yrd.user.IOperatorInfoService;
import com.yjf.yrd.user.order.OperatorRegisterOrder;
import com.yjf.yrd.user.result.UserRegisterResult;
import com.yjf.yrd.user.valueobject.QueryConditions;
import com.yjf.yrd.web.util.WebUtil;
import com.yjf.yrd.ws.enums.OperatorInfoEnum;

@Controller
@RequestMapping("guaranteeOperator")
public class GuaranteeOperatorManagerController extends UserAccountInfoBaseController {
	/** 返回页面路径 */
	String GUARANTEE_MANAGE_PATH = "/front/business/guarantee/";
	@Autowired
	protected IOperatorInfoService operatorInfoService;
	
	//-----------------------------------------------------操作员管理----------------------------------------------------------------------
	
	@RequestMapping("operatorManager")
	public String operatorManager(HttpSession session, QueryConditions queryConditions,
									PageParam pageParam, Model model) throws Exception {
		this.initAccountInfo(model);
		session.setAttribute("current", 8);
		queryConditions.setUserId(SessionLocalManager.getSessionLocal().getUserId());
		//Page<InstitutionsInfoDO>  page = institutionsInfoManager.pageChildren(queryConditions, pageParam);
		Page<OperatorInfoDO> page = operatorInfoService.queryOperatorPage(queryConditions,
			pageParam);
		model.addAttribute("page", page);
		model.addAttribute("queryConditions", queryConditions);
		return GUARANTEE_MANAGE_PATH + "guarantee-operator-manage.vm";
	}
	
	//-----------------------------------------------------新增操作员----------------------------------------------------------------------
	
	@RequestMapping("addOperator")
	public String addOperator(HttpSession session, QueryConditions queryConditions,
								PageParam pageParam, Model model) throws Exception {
		this.initAccountInfo(model);
		session.setAttribute("current", 8);
		return GUARANTEE_MANAGE_PATH + "guarantee-add-operator.vm";
	}
	
	//-----------------------------------------------------新增操作员提交----------------------------------------------------------------------
	@ResponseBody
	@RequestMapping("addOperatorSubmit")
	public Object addOperatorSubmit(HttpSession session, HttpServletRequest request,
									PageParam pageParam, Model model, int... roleIds) {
		OperatorRegisterOrder operatorRegisterOrder = new OperatorRegisterOrder();
		WebUtil.setPoPropertyByRequest(operatorRegisterOrder, request);
		
		long parentId = SessionLocalManager.getSessionLocal().getUserId();
		operatorRegisterOrder.setParentId(parentId);
		UserRegisterResult userRegisterResult = registerService
			.registerOperator(operatorRegisterOrder);
		session.setAttribute("current", 8);
		JSONObject jsonobj = new JSONObject();
		if (userRegisterResult.isSuccess()) {
			jsonobj.put("code", 1);
			jsonobj.put("message", "创建操作员成功");
		} else {
			jsonobj.put("code", 0);
			jsonobj.put("message", "创建操作员失败");
		}
		return jsonobj;
	}
	
	//-----------------------------------------------------投资者详情----------------------------------------------------------------------
	
	@RequestMapping("operatorInfo")
	public String operatorInfo(String userBaseId, Model model) throws Exception {
		this.initAccountInfo(model);
		Map<String, Object> opConditions = new HashMap<String, Object>();
		opConditions.put("userBaseId", userBaseId);
		opConditions.put("limitStart", 0);
		opConditions.put("pageSize", 100);
		List<OperatorInfoDO> operatorInfos = operatorInfoService
			.queryOperatorsByProperties(opConditions);
		if (operatorInfos != null && operatorInfos.size() > 0) {
			model.addAttribute("info", operatorInfos.get(0));
			if (1 == operatorInfos.get(0).getOperatorType()) {
				model.addAttribute("auth", "yes");
			}
		}
		return GUARANTEE_MANAGE_PATH + "guarantee-operator-info.vm";
	}
	
	//---更新----------------------------------------------------------------------------------
	@ResponseBody
	@RequestMapping("updateOperatorSubmit")
	public Object updateOperatorSubmit(HttpSession session, OperatorInfoDO operatorInfo,
										PageParam pageParam, Model model, HttpServletRequest request)
																										throws Exception {
		
		SessionLocal sessionLocal = SessionLocalManager.getSessionLocal();
		if (sessionLocal != null && sessionLocal.getAccountId() != null) {
			this.initAccountInfo(model);
		}
		
		JSONObject jsonobj = new JSONObject();
		
		try {
			
			String payPassword = request.getParameter("payPassword");
			if (operatorInfo.getOperatorType() == 3) {
				payPassword = request.getParameter("payPassword2");
			}
			String mobile = request.getParameter("mobile");
			
			OperatorInfoEnum oe = operatorInfoService.updateOperatorInfo(operatorInfo, mobile,
				payPassword);
			
			if (oe == OperatorInfoEnum.EXECUTE_SUCCESS) {
				jsonobj.put("code", 1);
				jsonobj.put("message", "更新操作员成功！");
			} else {
				jsonobj.put("code", 0);
				jsonobj.put("message", "更新操作员失败!");
			}
		} catch (Exception e) {
			jsonobj.put("code", 0);
			jsonobj.put("message", "更新操作员失败！");
			return jsonobj;
		}
		
		return jsonobj;
	}
	
}
