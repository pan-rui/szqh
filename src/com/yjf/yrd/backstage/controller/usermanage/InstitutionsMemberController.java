package com.yjf.yrd.backstage.controller.usermanage;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.yjf.common.lang.util.ListUtil;
import com.yjf.yrd.base.BaseAutowiredController;
import com.yjf.yrd.dataobject.Role;
import com.yjf.yrd.page.Page;
import com.yjf.yrd.page.PageParam;
import com.yjf.yrd.page.Pagination;
import com.yjf.yrd.user.info.PersonalVOInfo;
import com.yjf.yrd.user.info.UserInfo;
import com.yjf.yrd.user.order.AddBrokerToOrgOrder;
import com.yjf.yrd.user.query.order.QueryUserChildrenOrder;
import com.yjf.yrd.user.query.order.UserRoleQueryOrder;
import com.yjf.yrd.util.AppConstantsUtil;
import com.yjf.yrd.web.util.PageUtil;
import com.yjf.yrd.web.util.WebUtil;
import com.yjf.yrd.ws.enums.RealNameAuthStatusEnum;
import com.yjf.yrd.ws.enums.SysUserRoleEnum;
import com.yjf.yrd.ws.enums.UserTypeEnum;
import com.yjf.yrd.ws.result.YrdBaseResult;
import com.yjf.yrd.ws.service.query.result.QueryBaseBatchResult;
import com.yjf.yrd.ws.userManage.order.AddMemberOrder;

/**
 * 
 * 
 * @Filename InstitutionsMemberController.java
 * 
 * @Description
 * 
 * @Version 1.0
 * 
 * @Author zjl
 * 
 * @Email zjialin@yiji.com
 * 
 * @History <li>Author: zjl</li> <li>Date: 2013-8-14</li> <li>Version: 1.0</li>
 * <li>Content: create</li>
 * 
 */
@Controller
@RequestMapping("backstage/userManage")
public class InstitutionsMemberController extends BaseAutowiredController {
	/** 统一页面路径 */
	private final String vm_path = "/backstage/userManage/";
	private final static String JGAGENTPREFIX = ""; //机构经纪人前缀 
	private final static String JJRAGENTPREFIX = "K"; //经纪人下的投资人前缀
	
	@RequestMapping("institutions")
	public String institutions(long roleId, String enterpriseName, PageParam pageParam, Model model)
																									throws Exception {
		List<Long> roleIds = new ArrayList<Long>();
		if (roleId == 0) {
			roleIds.add(8L);
			roleIds.add(9L);
			roleIds.add(10L);
		} else {
			roleIds.add(roleId);
		}
		UserRoleQueryOrder queryOrder = new UserRoleQueryOrder();
		queryOrder.setType(UserTypeEnum.JG);
		queryOrder.setRoleEnum(SysUserRoleEnum.getByValue((int) roleId));
		queryOrder.setRealName(enterpriseName);
		queryOrder.setPageNumber(pageParam.getPageNo());
		queryOrder.setPageSize(pageParam.getPageSize());
		QueryBaseBatchResult<UserInfo> batchResult = userQueryService.queryRoleUserInfo(queryOrder);
		Page<UserInfo> page = PageUtil.getCovertPage(batchResult);
		model.addAttribute("roleId", roleId);
		model.addAttribute("enterpriseName", enterpriseName);
		model.addAttribute("page", page);
		return vm_path + "institutions.vm";
	}
	
	/**
	 * 机构人员管理
	 * @throws Exception
	 * */
	@RequestMapping("institutions/institutionsMember")
	public String institutionMember(HttpServletRequest request, PageParam pageParam, Model model)
																									throws Exception {
		UserRoleQueryOrder userQueryOrder = new UserRoleQueryOrder();
		userQueryOrder.setType(UserTypeEnum.JG);
		userQueryOrder.setRoleEnum(SysUserRoleEnum.MARKETING);
		userQueryOrder.setPageSize(500);
		List<UserInfo> list = userQueryService.queryRoleUserInfo(userQueryOrder).getPageList();
		QueryUserChildrenOrder queryUserChildrenOrder = new QueryUserChildrenOrder();
		WebUtil.setPoPropertyByRequest(queryUserChildrenOrder, request);
		queryUserChildrenOrder.setRoleId(SysUserRoleEnum.BROKER.getValue());
		if (queryUserChildrenOrder.getUserId() == 0) {
			if (ListUtil.isNotEmpty(list)) {
				queryUserChildrenOrder.setUserId(list.get(0).getUserId());
			}
		}
		
		queryUserChildrenOrder.setPageNumber(pageParam.getPageNo());
		queryUserChildrenOrder.setPageSize(pageParam.getPageSize());
		QueryBaseBatchResult<PersonalVOInfo> baseBatchResult = userQueryService
			.queryUserChildren(queryUserChildrenOrder);
		model.addAttribute("page", PageUtil.getCovertPage(baseBatchResult));
		model.addAttribute("list", list);
		model.addAttribute("queryConditions", queryUserChildrenOrder);
		return vm_path + "institutionsMember.vm";
	}
	
	/**
	 * 经纪人人员管理
	 * @throws Exception
	 * */
	@RequestMapping("institutions/personalMember")
	public String personalMember(HttpServletRequest request, PageParam pageParam, Model model)
																								throws Exception {
		QueryUserChildrenOrder queryUserChildrenOrder = new QueryUserChildrenOrder();
		WebUtil.setPoPropertyByRequest(queryUserChildrenOrder, request);
		queryUserChildrenOrder.setPageNumber(pageParam.getPageNo());
		queryUserChildrenOrder.setPageSize(pageParam.getPageSize());
		queryUserChildrenOrder.setRealNameAuthentication(RealNameAuthStatusEnum.getByCode(request
			.getParameter("realNameAuthentication")));
		QueryBaseBatchResult<PersonalVOInfo> baseBatchResult = userQueryService
			.queryUserChildren(queryUserChildrenOrder);
		model.addAttribute("page", PageUtil.getCovertPage(baseBatchResult));
		model.addAttribute("queryConditions", queryUserChildrenOrder);
		return vm_path + "personalMember.vm";
	}
	
	/**
	 * 机构批量关联用户
	 * @param request
	 * @param response
	 * @param modelMap
	 * @return
	 */
	@SuppressWarnings({ "unused", "unchecked" })
	@ResponseBody
	@RequestMapping("institutions/institutionsMemberOpen")
	public Object batchOpenAndAccount(HttpServletRequest request, HttpServletResponse response,
										ModelMap modelMap) {
		//List<InstitutionsMemberOeder> parsedInstitutionsMemberList = null;
		//List<InstitutionsMemberOeder> institutionsMemberList = null;
		String STATICFILESTEMPPATH = AppConstantsUtil.getYrdUploadFolder()
										+ "/files/institutionsMember";
		List<AddMemberOrder> parsedAddMemberOederList = null;
		List<AddMemberOrder> addMemberOederList = null;
		try {
			ServletFileUpload fileUpload = new ServletFileUpload(new DiskFileItemFactory());
			fileUpload.setHeaderEncoding("utf-8");
			List<FileItem> fileList = null;
			try {
				fileList = fileUpload.parseRequest(request);
			} catch (FileUploadException ex) {
				logger.error(ex.getMessage(), ex);
				return "{\"code\":\"1\",\"resData\":\"" + "文件上传异常！" + "\"}";
			}
			Iterator<FileItem> it = fileList.iterator();
			String name = "";
			String extName = "";
			while (it.hasNext()) {
				FileItem item = it.next();
				if (!item.isFormField()) {
					
					// 解析文件  
					name = item.getName();
					long size = item.getSize();
					String type = item.getContentType();
					if (name == null || name.trim().equals("")) {
						continue;
					}
					// 得到文件的扩展名  
					if (name.lastIndexOf(".") >= 0) {
						extName = name.substring(name.lastIndexOf("."));
					}
					File file = null;
					String tempPath = STATICFILESTEMPPATH;
					String savePath = tempPath + "/" + name;
					File fileDir = new File(tempPath);
					if (!fileDir.exists()) {
						fileDir.mkdirs();
					}
					try {
						file = new File(savePath);
						item.write(file);
					} catch (Exception e) {
						e.printStackTrace();
						logger.info("批量添加机构人员文件上传发生异常，异常信息：{}", e.toString(), e);
						return "{\"code\":\"1\",\"resData\":\"" + "文件上传异常" + "\"}";
					}
					//保留导入参数复杂类型
					//institutionsMemberList = parseExcel(file);
					addMemberOederList = parseExcel(file);
				}
			}
			//保留导入参数复杂类型
			//parsedInstitutionsMemberList = institutionsMemberOpenList(institutionsMemberList);
			//parsedAddMemberOederList = addMemberSubmit(addMemberOederList);
		} catch (Exception e) {
			// TODO: handle exception
			logger.info("批量添加机构人员发生异常，异常信息：{}", e.toString(), e);
			return "{\"code\":\"2\",\"resData\":\"" + "数据处理异常" + "\"}";
		}
		JSONObject jsonobj = new JSONObject();
		jsonobj.put("code", "0");
		//保留导入参数复杂类型
		//jsonobj.put("resData",institutionsMemberList);
		jsonobj.put("resData", parsedAddMemberOederList);
		response.setContentType("text/json");
		return jsonobj.toJSONString();
	}
	
	/**
	 * @description 解析excel文件到InstitutionsMemberOeder对象
	 * @param file
	 * @return
	 */
	public List<AddMemberOrder> parseExcel(File file) {
		//List<InstitutionsMemberOeder> institutionsMemberList=new ArrayList<InstitutionsMemberOeder>();
		//InstitutionsMemberOeder institutionsMemberOeder = null;
		List<AddMemberOrder> addMemberOederList = new ArrayList<AddMemberOrder>();
		AddMemberOrder addMemberOeder = null;
		
		try {
			InputStream is = new FileInputStream(file);
			HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);
			
			// 循环工作表Sheet
			for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
				HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
				if (hssfSheet == null) {
					continue;
				}
				// 循环行Row
				for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
					
					//institutionsMemberOeder = new InstitutionsMemberOeder();
					addMemberOeder = new AddMemberOrder();
					HSSFRow hssfRow = hssfSheet.getRow(rowNum);
					if (hssfRow == null) {
						continue;
					}
					// 循环列Cell
					// 0用户名 1真实姓名 2证件号码 3手机电话 4常用电子邮箱
					// for (int cellNum = 0; cellNum <=4; cellNum++) {
					HSSFCell usercell = hssfRow.getCell(0);
					if (usercell == null) {
						continue;
					}
					//institutionsMemberOeder.setUserName(getValue(usercell));
					addMemberOeder.setGrUserName(getValue(usercell));//个人用户名
					HSSFCell realNameCell = hssfRow.getCell(1);
					if (realNameCell == null) {
						continue;
					}
					addMemberOeder.setJgUserName(getValue(realNameCell));//机构用户名
					// institutionsMemberOeder.setRealName(getValue(realNameCell));
					//                    HSSFCell certNoCell = hssfRow.getCell(2);
					//                    if (certNoCell == null) {
					//                        continue;
					//                    }
					//                    institutionsMemberOeder.setCertNo(getValue(certNoCell));
					//                    HSSFCell mobileCell = hssfRow.getCell(3);
					//                    if (mobileCell == null) {
					//                        continue;
					//                    }
					//                    institutionsMemberOeder.setMobile(getValue(mobileCell));
					//                    HSSFCell maillCell = hssfRow.getCell(4);
					//                    if (maillCell == null) {
					//                        continue;
					//                    }
					//                    institutionsMemberOeder.setMail(getValue(maillCell));
					//                    HSSFCell institutionCell = hssfRow.getCell(5);
					//                    if (institutionCell == null) {
					//                        continue;
					//                    }
					//                    institutionsMemberOeder.setInstitutionName(getValue(institutionCell));
					// institutionsMemberList.add(institutionsMemberOeder);
					addMemberOederList.add(addMemberOeder);
				}
			}
			is.close();
		} catch (Exception e) {
			logger.info("解析机构人员数据发生异常，异常信息：{}", e.toString(), e);
		}
		return addMemberOederList;
	}
	
	/**
	 * 得到Excel表中的值
	 * 
	 * @param hssfCell Excel中的每一个格子
	 * @return Excel中每一个格子中的值
	 */
	@SuppressWarnings("static-access")
	private String getValue(HSSFCell hssfCell) {
		if (hssfCell.getCellType() == hssfCell.CELL_TYPE_BOOLEAN) {
			// 返回布尔类型的值
			return String.valueOf(hssfCell.getBooleanCellValue());
		} else if (hssfCell.getCellType() == hssfCell.CELL_TYPE_NUMERIC) {
			// 返回数值类型的值
			return String.valueOf(hssfCell.getNumericCellValue());
		} else {
			// 返回字符串类型的值
			return String.valueOf(hssfCell.getStringCellValue());
		}
	}
	
	@ResponseBody
	@RequestMapping("institution/addMember")
	public Map<String, Object> addMember(HttpServletRequest request, Long parentId,
											String memberUserName, String memberRealName,
											String code) {
		logger.info("进入添加机构成员，入参：[{" + parentId + "}]，[{" + memberUserName + "}]，[{"
					+ memberRealName + "}],[{" + code + "}]");
		Map<String, Object> map = new HashMap<String, Object>();
		String validNo = null;
		try {
			AddBrokerToOrgOrder brokerToOrgOrder = new AddBrokerToOrgOrder();
			brokerToOrgOrder.setMemberUserName(memberUserName);
			brokerToOrgOrder.setParentId(parentId);
			brokerToOrgOrder.setServletPath(request.getServletContext().getRealPath("/"));
			YrdBaseResult baseResult = userBaseInfoManager.addBrokerToOrg(brokerToOrgOrder);
			
			if (baseResult.isSuccess()) {
				map.put("code", 1);
				map.put("message", "添加机构成员成功");
			} else {
				map.put("code", 0);
				map.put("message", "添加机构成员失败(" + baseResult.getMessage() + ")");
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			map.put("code", 0);
			map.put("message", "添加机构成员失败");
			logger.error("结束添加机构成员，发生异常：{}", e.toString());
		}
		logger.error("结束添加机构成员，结果：{}", map);
		return map;
	}
	
	//只有经纪人才能通过
	private boolean checkChildIdForAddMember(long userId) {
		boolean isJJR = false;
		Pagination<Role> rolesPage = authorityService.getRolesByUserId(userId, 0, 99);
		if (rolesPage.getResult() != null && rolesPage.getResult().size() > 0) {
			for (Role role : rolesPage.getResult()) {
				if (SysUserRoleEnum.BROKER.getRoleCode().equals(role.getCode())) {
					isJJR = true;
					return isJJR;
				}
			}
		}
		
		return isJJR;
	}
	
	/**
	 * 验证只有营销机构才能添加
	 * @param parentId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("institutions/validateInstitutionForAdd")
	public Map<String, Object> validateInstitutionForAdd(Long parentId) {
		logger.info("进入添加机构成员，入参：[{" + parentId + "}]");
		boolean isYXJG = false;
		Map<String, Object> map = new HashMap<String, Object>();
		Pagination<Role> rolesPage = authorityService.getRolesByUserId(parentId, 0, 99);
		if (rolesPage.getResult() != null && rolesPage.getResult().size() > 0) {
			for (Role role : rolesPage.getResult()) {
				if (SysUserRoleEnum.MARKETING.getRoleCode().equals(role.getCode())) {
					isYXJG = true;
				}
			}
		}
		if (isYXJG) {
			map.put("code", 1);
			map.put("message", "添加机构成员验证成功");
		} else {
			map.put("code", 0);
			map.put("message", "无法为该机构添加成员");
		}
		logger.error("结束添加机构成员验证，结果：{}", map);
		return map;
	}
}
