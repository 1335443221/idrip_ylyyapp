package com.sl.ylyy.manager.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sl.ylyy.manager.entity.Auth;
import com.sl.ylyy.manager.entity.Organization;
import com.sl.ylyy.manager.entity.RoleInfo;
import com.sl.ylyy.common.utils.PageUtil;


public interface SystemDataService {
	//Organization
	public ArrayList<Organization> getAllOrganization(String oname);
	
	public PageUtil<Organization> findOrganizationPage(PageUtil<Organization> page);
	
	public void deleteOrganizationById(Integer id);
	
	public void insertOrganization(Organization Organization);
	
	public void updateOrganizationById(Organization Organization);
	
	public Organization getOrganizationById(Integer id);
	
	
	//role
	public ArrayList<RoleInfo> getAllRole(String rname);
	
	public PageUtil<RoleInfo> findRolePage(PageUtil<RoleInfo> page);
	
	public RoleInfo getRoleById(Integer id);
	
	public void deleteRoleById(Integer id);
	
	public void insertRole(RoleInfo RoleInfo);
	
	public void updateRoleById(RoleInfo RoleInfo);
	
	
	//group
	public ArrayList<Map<String, Object>> getAllGroup(Map<String, Object> map);
	public PageUtil<Map<String, Object>> findGroupPage(PageUtil<Map<String, Object>> page);
	public Map<String,Object> getGroupById(Integer id);
	public void deleteGroupById(Integer id,Integer user_id,String rong_user_id);
	public int insertGroup(Map<String,Object> map);
	public int updateGroupById(Map<String,Object> map);
	public List<Map<String,Object>> getAllMalfunction_Type(Map<String, Object> map);
	
	
	
	
	//auth
	public ArrayList<Auth> getAllAuth(String rname);
	
	public PageUtil<Auth> findAuthPage(PageUtil<Auth> page);
	
	public Auth getAuthById(Integer id);
	
	public void deleteAuthById(Integer id);
	
	public void insertAuth(Auth Auth);
	
	public void updateAuthById(Auth Auth);
	
	
	//company
	public List<Map<String,Object>> getAllCompany(Map<String, Object> map);
	public PageUtil<Map<String,Object>> findCompanyPage(PageUtil<Map<String,Object>> page);
	public Map<String, Object>  getCompanyById(Integer id);
	public int deleteCompanyById(Integer id,Integer user_id,String rong_user_id);
	public int insertCompany(Map<String, Object> map);
	public int updateCompanyById(Map<String, Object> map);
	
	//department
	public List<Map<String,Object>> getAllDepartment(Map<String, Object> map);
	public PageUtil<Map<String,Object>> findDepartmentPage(PageUtil<Map<String,Object>> page);
	public Map<String, Object>  getDepartmentById(Integer id);
	public int deleteDepartmentById(Integer id,Integer user_id,String rong_user_id);
	public int insertDepartment(Map<String, Object> map);
	public int updateDepartmentById(Map<String, Object> map);
}
