package com.sl.ylyy.manager.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.sl.ylyy.manager.entity.Auth;
import com.sl.ylyy.manager.entity.Organization;
import com.sl.ylyy.manager.entity.Organization_User_Relation;
import com.sl.ylyy.manager.entity.RoleInfo;
import com.sl.ylyy.manager.entity.Role_Auth_Relation;
import com.sl.ylyy.manager.entity.Role_User_Relation;
import com.sl.ylyy.app_1.entity.Group;
import com.sl.ylyy.common.utils.PageUtil;

@Mapper
public interface SystemDao {
	
	//Organization
	public ArrayList<Organization> getAllOrganization(String oname);
	
	public List<Organization> findOrganizationPage(PageUtil<Organization> page);
	
	public Organization getOrganizationById(Integer id);
	
	public void insertOrganization(Organization Organization);
	
	public void deleteOrganizationById(Integer id);
	
	public void updateOrganizationById(Organization Organization);
	
	public List<Organization> checkOrganizationName(String name);
	
	public void updateOrganization_User_Relation(Organization_User_Relation organization_user_relation);
	
	public void deleteOrganization_User_Relation(Organization_User_Relation organization_user_relation);
	
	public void insertOrganization_User_Relation(Organization_User_Relation organization_user_relation);
	
	public void deleteOrganization_User_RelationByOid(Integer oid);
	
	
	
	
	//role
	public ArrayList<RoleInfo> getAllRole(String rname);
	public List<RoleInfo> findRolePage(PageUtil<RoleInfo> page);
	public RoleInfo getMaxIdRole();
	public RoleInfo getRoleById(Integer id);
	public void insertRole(RoleInfo RoleInfo);
	public void deleteRoleById(Integer id);
	public void updateRoleById(RoleInfo RoleInfo);
	public List<RoleInfo> checkRoleName(String name);
	
	public int updateRole_User_Relation(Map<String, Object> map);
	public int insertRole_User_Relation(Map<String, Object> map);
	public void deleteRole_User_Relation(Role_User_Relation role_user_relation);
	public void deleteRole_User_RelationByRoleId(Integer rid);
	
	public void deleteRole_Auth_Relation(Role_Auth_Relation Role_Auth_Relation);
	public void deleteRole_Auth_RelationByRoleId(Integer rid);
	public void updateRole_Auth_Relation(Role_Auth_Relation Role_Auth_Relation);
	public void insertRole_Auth_Relation(Role_Auth_Relation Role_Auth_Relation);
	
	
	
	//group
	public ArrayList<Map<String, Object>> getAllGroup(Map<String, Object> map);
	
	public List<Map<String, Object>> findGroupPage(PageUtil<Map<String, Object>> page);
	
	public Map<String,Object> getGroupById(Integer id);
	
	public int insertGroup(Map<String,Object> map);
	
	public void deleteGroupById(Integer id);
	
	public int updateGroupById(Map<String,Object> map);
	public int updateUserDepartmentByGruopId(Map<String,Object> map);
	public int updateGroupByGId(Map<String,Object> map);
	
	public List<Group> checkGroupName(String name);
	
	public List<Map<String,Object>> getAllMalfunction_Type(Map<String, Object> map);
	public List<Map<String,Object>> getGroup_Malfunction_Type(Map<String, Object> map);
	
	public int insertGroup_malfunction_type_relation(Map<String,Object> map);
	
	public int deleteGroup_malfunction_type_relation(Integer group_id);
	
	
	
	//auth
	public ArrayList<Auth> getAllAuth(String rname);
	
	public ArrayList<Auth> getAuthByRoleId(Integer rid);
	
	public List<Auth> findAuthPage(PageUtil<Auth> page);
	
	public Auth getAuthById(Integer id);
	
	public void insertAuth(Auth Auth);
	
	public void deleteAuthById(Integer id);
	
	public void updateAuthById(Auth Auth);
	
	
	//company
	public List<Map<String,Object>> getAllCompany(Map<String, Object> map);
	public List<Map<String,Object>> findCompanyPage(PageUtil<Map<String,Object>> page);
	public Map<String, Object>  getCompanyById(Integer id);
	public int deleteCompanyById(Integer id);
	public int insertCompany(Map<String, Object> map);
	public int updateCompanyById(Map<String, Object> map);
	
	//department
	public List<Map<String,Object>> getAllDepartment(Map<String, Object> map);
	public List<Map<String,Object>> findDepartmentPage(PageUtil<Map<String,Object>> page);
	public Map<String, Object>  getDepartmentById(Integer id);
	public int deleteDepartmentById(Integer id);
	public int insertDepartment(Map<String, Object> map);
	public int updateDepartmentById(Map<String, Object> map);
	public int updateUserCompanyByDepartmentId(Map<String, Object> map);
	public int updateDepartmentByDId(Map<String, Object> map);
	
}
