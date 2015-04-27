package com.yjf.yrd.front.controller.authority;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yjf.yrd.authority.AuthorityService;
import com.yjf.yrd.dataobject.Permission;
import com.yjf.yrd.dataobject.Role;
import com.yjf.yrd.page.Pagination;

/**
 * 
 * 
 * @Filename AuthorityController.java
 * 
 * @Description
 * 
 * @Version 1.0
 * 
 * @Author yhl
 * 
 * @Email yhailong@yiji.com
 * 
 * @History <li>Author: yhl</li> <li>Date: 2013-6-8</li> <li>Version: 1.0</li>
 * <li>Content: create</li> 权限管理控制器
 */
@Controller
@RequestMapping("backstage/authority")
public class AuthorityController {
	/**
	 * 权限服务接口
	 */
	@Autowired
	private AuthorityService authorityService;
	
	/**
	 * 所有权限列表
	 * @param model
	 * @return
	 */
	@RequestMapping("permissions/{page}/{size}")
	public String getAllPermissions(@PathVariable int page, @PathVariable int size, Model model) {
		model.addAttribute("page", authorityService.getAllPermission((page - 1) * size, size));
		return "authority/permissions.vm";
	}
	
	/**
	 * 删除权限
	 * @param ids
	 * @return
	 */
	@RequestMapping("permissions_del")
	public String delPermissions(int[] ids, String redirect) {
		authorityService.deletePermissions(ids);
		return "redirect:" + redirect;
	}
	
	/**
	 * 修改权限信息页面
	 * @param permission
	 * @return
	 */
	@RequestMapping("toModifyPermission")
	public String toModifyPermission(int permissionId, String redirect, Model model) {
		Permission permission = authorityService.getPermissionById(permissionId);
		model.addAttribute("item", permission);
		model.addAttribute("type", "modify");
		return "authority/permission_add.vm";
	}
	
	/**
	 * 修改权限信息
	 * @param permission
	 * @return
	 */
	@RequestMapping("modifyPermission")
	public String modifyPermission(Permission permission, String redirect) {
		authorityService.modifyPermission(permission);
		return "redirect:" + redirect;
	}
	
	/**
	 * 添加权限
	 * @return
	 */
	@RequestMapping("addPermission")
	public String addPermission(Permission permission, String redirect) {
		authorityService.addPermission(permission);
		return "redirect:" + redirect;
	}
	
	/**
	 * 验证是否权限ID
	 * @param code
	 * @return
	 */
	@ResponseBody
	@RequestMapping("existPermission")
	public Object existPermission(String code) {
		return authorityService.existPermisson(code);
	}
	
	/**
	 * 获取所有角色
	 * @return
	 */
	@RequestMapping("roles/{page}/{size}")
	public String getAllRoles(@PathVariable int page, @PathVariable int size, Model model) {
		model.addAttribute("page", authorityService.getAllRoles((page - 1) * size, size));
		return "authority/roles.vm";
	}
	
	/**
	 * 删除角色
	 * @param ids
	 * @return
	 */
	@RequestMapping("roles_del")
	public String delRoles(int[] ids, String redirect) {
		authorityService.deleteRoles(ids);
		return "redirect:" + redirect;
	}
	
	/**
	 * 修改角色信息
	 * @param role
	 * @return
	 */
	@RequestMapping("modifyRole")
	public String modifyRole(Role role, String redirect) {
		authorityService.modifyRole(role);
		return "redirect:" + redirect;
	}
	
	/**
	 * 添加角色
	 * @param role
	 * @return
	 */
	@RequestMapping("addRole")
	public String addRole(Role role, String redirect) {
		authorityService.addRole(role);
		return "redirect:" + redirect;
	}
	
	/**
	 * 验证是否存在角色
	 * @param code
	 * @return
	 */
	@ResponseBody
	@RequestMapping("existRole")
	public Object existRole(String code) {
		return authorityService.existRoleCode(code);
	}
	
	/**
	 * 根据用户ID获取角色列表
	 * @return
	 */
	@RequestMapping("grbu/{userId}/{page}/{size}")
	public String getRolesByUserId(@PathVariable long userId, @PathVariable int page,
									@PathVariable int size, Model model) {
		model.addAttribute("page",
			authorityService.getRolesByUserId(userId, (page - 1) * size, size));
		return "authority/roles.vm";
	}
	
	/**
	 * 根据角色ID获取子角色
	 * @return
	 */
	@RequestMapping("gcbr/{roleId}/{page}/{size}")
	public String getChildrenByRoleId(@PathVariable int roleId, @PathVariable int page,
										@PathVariable int size, Model model) {
		model.addAttribute("page",
			authorityService.getChildrenByRoleId(roleId, (page - 1) * size, size));
		return "authority/roles.vm";
	}
	
	/**
	 * 根据角色获取权限列表
	 * @return
	 */
	@RequestMapping("gpbr/{roleId}/{page}/{size}")
	public String getPermissionsByRoleId(@PathVariable int roleId, @PathVariable int page,
											@PathVariable int size, Model model) {
		model.addAttribute("page",
			authorityService.getPermissionsByRoleId(roleId, (page - 1) * size, size));
		return "authority/permissions.vm";
	}
	
	/**
	 * 根据用户ID获取可授予的角色
	 * @return
	 */
	@RequestMapping("ggrbu/{userId}/{page}/{size}")
	public String getGrantableRolesByUserId(@PathVariable long userId, @PathVariable int page,
											@PathVariable int size, Model model) {
		model.addAttribute("page",
			authorityService.getGrantableRolesByUserId(userId, (page - 1) * size, size));
		return "authority/roles.vm";
	}
	
	/**
	 * 根据角色ID获取可授予的权限
	 * @return
	 */
	@ResponseBody
	@RequestMapping("ggpbr/{roleId}/{page}/{size}")
	public Object getGrantablePermissionsByRoleId(@PathVariable int roleId, @PathVariable int page,
													@PathVariable int size, Model model) {
		Pagination<Permission> pagination = authorityService.getGrantablePermissionsByRoleId(
			roleId, (page - 1) * size, size);
		model.addAttribute("page", pagination);
		List<Permission> permissions = new ArrayList<>();
		//只展示后台的权限
		for (Permission p : pagination.getResult()) {
			if (p.getOperate().startsWith("/backstage")) {
				permissions.add(p);
			}
		}
		pagination.setResult(permissions);
		return pagination;
	}
	
	/**
	 * 根据角色ID获取授予的权限
	 * @return
	 */
	@ResponseBody
	@RequestMapping("permissionsHad/{roleId}/{page}/{size}")
	public Object getpermissionsHadByRoleId(@PathVariable int roleId, @PathVariable int page,
											@PathVariable int size, Model model) {
		model.addAttribute("page",
			authorityService.getPermissionsByRoleId(roleId, (page - 1) * size, size));
		return authorityService.getPermissionsByRoleId(roleId, (page - 1) * size, size);
	}
	
	/**
	 * 给用户授予角色
	 * @param userId
	 * @param roleIds
	 * @return
	 */
	@RequestMapping("grantrtu/{userId}")
	public String grantRolesToUser(@PathVariable long userId, int[] roleIds) {
		authorityService.grantRolesToUser(userId, roleIds);
		return "";
	}
	
	/**
	 * 给角色授予权限
	 * @param roleId
	 * @param permissionIds
	 * @return
	 */
	@ResponseBody
	@RequestMapping("grantptr/{roleId}")
	public String grantPermissionsToRole(@PathVariable int roleId, int[] permissionIds) {
		authorityService.grantPermisssionsToRole(roleId, permissionIds);
		return "";
	}
	
	/**
	 * 给角色授予权限(先删除角色原来的权限，再赋予新权限)
	 * @param roleId
	 * @param permissionIds
	 * @return
	 */
	@ResponseBody
	@RequestMapping("grantptroldd/{roleId}")
	public String grantPermissionsToRoleOldDelete(@PathVariable int roleId, int[] permissionIds) {
		authorityService.grantPermisssionsToRoleDeleteOld(roleId, permissionIds);
		return "";
	}
	
	/**
	 * 给角色解除权限
	 * @param roleId
	 * @param permissionIds
	 * @return
	 */
	@ResponseBody
	@RequestMapping("removeptr/{roleId}")
	public Object removePermissions(@PathVariable int roleId, int[] permissionIds) {
		authorityService.removePermissions(roleId, permissionIds);
		return "";
	}
	
	/**
	 * 给用户解除角色
	 * @return
	 */
	@ResponseBody
	@RequestMapping("unbindrtu/{userId}")
	public Object unbind(@PathVariable int userId, int[] roleIds) {
		authorityService.unbindRoles(userId, roleIds);
		return "";
	}
	
	@RequestMapping("toModifyRole")
	public String toModifyRole(int roleId, Model model) {
		Role role = authorityService.getRoleByRoleId(roleId);
		model.addAttribute("item", role);
		model.addAttribute("type", "modify");
		return "authority/role_add.vm";
	}
	
	@RequestMapping("toAddRole")
	public String toAddRole() {
		return "authority/role_add.vm";
	}
	
	@RequestMapping("toAddPermission")
	public String toAddPermission() {
		return "authority/permission_add.vm";
	}
}
