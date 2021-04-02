package com.sl.ylyy.manager.service.impl;

import java.util.*;

import com.sl.ylyy.manager.dao.RongCloudDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.sl.ylyy.manager.dao.SystemDao;
import com.sl.ylyy.manager.entity.Auth;
import com.sl.ylyy.manager.entity.Organization;
import com.sl.ylyy.manager.entity.RoleInfo;
import com.sl.ylyy.manager.entity.Role_Auth_Relation;
import com.sl.ylyy.manager.service.SystemDataService;
import com.sl.ylyy.common.utils.PageUtil;


@Service("systemDataImpl")
public class SystemDataServiceImpl implements SystemDataService {

	@Autowired
	private SystemDao systemDao;

	@Autowired
	private RongCloudDao rongCloudDao;

	@Override
	public ArrayList<Organization> getAllOrganization(String oname) {
		
		return systemDao.getAllOrganization(oname);
	}

	@Override
	public void deleteOrganizationById(Integer id) {
		
		systemDao.deleteOrganizationById(id);
		
	}

	@Override
	public void insertOrganization(Organization organization) {
		
		systemDao.insertOrganization(organization);
		
	}

	@Override
	public void updateOrganizationById(Organization organization) {
		
		systemDao.updateOrganizationById(organization);
		
	}
	
	@Override
	public PageUtil<Organization> findOrganizationPage(PageUtil<Organization> page) {
		PageHelper.startPage(page.getPageindex(), page.getPagesize());   //分页
		List<Organization> list=systemDao.findOrganizationPage(page);  //分页之后的集合
		PageUtil<Organization> pageInfo=new PageUtil<Organization>();  //存到page里
		pageInfo.setData(list);   //把查询结果存到page里
		pageInfo.setRecordCount(systemDao.findOrganizationPage(page).size());   //总记录数
		return pageInfo;
	}

	@Override
	public Organization getOrganizationById(Integer id) {
		
		return systemDao.getOrganizationById(id);
	}
	
	
	///////////////////////////role/////////////////////
	@Override
	public ArrayList<RoleInfo> getAllRole(String rname) {
		
		return systemDao.getAllRole(rname);
	}

	@Override
	public void deleteRoleById(Integer id) {
		
		systemDao.deleteRole_User_RelationByRoleId(id);  //删除关联的用户表的数据
		systemDao.deleteRole_Auth_RelationByRoleId(id);   //删除关联的权限表的数据
		systemDao.deleteRoleById(id);    //删除角色
		
	}

	@Override
	public void insertRole(RoleInfo roleInfo) {
		
		systemDao.insertRole(roleInfo);
		RoleInfo roleInfo2 =systemDao.getMaxIdRole();   //获取新插入的一个Role
		
		if(roleInfo.getAids()!=null||!"".equals(roleInfo.getAids())){
			String[] array = roleInfo.getAids().split(",");
			Role_Auth_Relation rar=new Role_Auth_Relation();
			rar.setRoleInfo(roleInfo2);
			Auth auth=new Auth();
			for(int i=0;i<array.length;i++){
				auth.setId(Integer.parseInt(array[i]));
				rar.setAuth(auth);
				systemDao.insertRole_Auth_Relation(rar);
			}
		}
		
		
	}

	@Override
	public void updateRoleById(RoleInfo roleInfo) {
		
		systemDao.updateRoleById(roleInfo);   //修改角色信息
		if(roleInfo.getAids().contains(",")){
			systemDao.deleteRole_Auth_RelationByRoleId(roleInfo.getId());  //删除原先角色权限
			String[] array = roleInfo.getAids().split(",");
			Role_Auth_Relation rar=new Role_Auth_Relation();
			rar.setRoleInfo(roleInfo);
			Auth auth=new Auth();
			for(int i=0;i<array.length;i++){
				auth.setId(Integer.parseInt(array[i]));
				rar.setAuth(auth); 
				systemDao.insertRole_Auth_Relation(rar);   //重新添加权限
			}
		}
		
		
	}

	@Override
	public RoleInfo getRoleById(Integer id) {
		
		 RoleInfo roleInfo =systemDao.getRoleById(id);
		 roleInfo.setAuthList(systemDao.getAuthByRoleId(id));
		return roleInfo;
	}

	@Override
	public PageUtil<RoleInfo> findRolePage(PageUtil<RoleInfo> page) {
		PageHelper.startPage(page.getPageindex(), page.getPagesize());   //分页
		List<RoleInfo> list=systemDao.findRolePage(page);  //分页之后的集合
		PageUtil<RoleInfo> pageInfo=new PageUtil<RoleInfo>();  //存到page里
		pageInfo.setData(list);   //把查询结果存到page里
		pageInfo.setRecordCount(systemDao.findRolePage(page).size());   //总记录数
		return pageInfo;
	}
	
	
	/////auth///////////////
	@Override
	public ArrayList<Auth> getAllAuth(String rname) {
		
		return systemDao.getAllAuth(rname);
	}

	@Override
	public void deleteAuthById(Integer id) {
		
		systemDao.deleteAuthById(id);
		
	}

	@Override
	public void insertAuth(Auth Auth) {
		
		systemDao.insertAuth(Auth);
		
	}

	@Override
	public void updateAuthById(Auth Auth) {
		
		systemDao.updateAuthById(Auth);
		
	}

	@Override
	public Auth getAuthById(Integer id) {
		
		return systemDao.getAuthById(id);
	}

	@Override
	public PageUtil<Auth> findAuthPage(PageUtil<Auth> page) {
		PageHelper.startPage(page.getPageindex(), page.getPagesize());   //分页
		List<Auth> list=systemDao.findAuthPage(page);  //分页之后的集合
		PageUtil<Auth> pageInfo=new PageUtil<Auth>();  //存到page里
		pageInfo.setData(list);   //把查询结果存到page里
		pageInfo.setRecordCount(systemDao.findAuthPage(page).size());   //总记录数
		return pageInfo;
	}
	
	
	
	
	//group////////////////
	@Override
	public ArrayList<Map<String, Object>> getAllGroup(Map<String, Object> map) {
		
		return systemDao.getAllGroup(map);
	}

	/**
	 * 删除工作组
	 * @param id
	 * @param user_id
	 */
	@Override
	public void deleteGroupById(Integer id,Integer user_id,String rong_user_id) {
		Map<String, Object> group=systemDao.getGroupById(id);
		group.put("dismiss_by",user_id);
		group.put("rong_user_id",rong_user_id);
		CommonDataServiceImpl.rong_group_dismiss(group);  //解散群公共方法
		
		systemDao.deleteGroup_malfunction_type_relation(id);
		systemDao.deleteGroupById(id);
	}

	/**
	 * 新增工作组
	 */
	@Override
	public int insertGroup(Map<String,Object> group) {
		Map<String, Object> map=new HashMap<>();
		map.put("group_name",group.get("name"));
		map.put("create_by",group.get("create_by"));
		map.put("is_structure",1);  //是公司架构
		int result=CommonDataServiceImpl.rong_group_insert(map); //建立群组
		if(result>0){
			group.put("rong_group_id",map.get("rong_group_id"));
		}

		int result2=systemDao.insertGroup(group);
		if(group.get("Malfunction_Type")!=null){
			JSONArray Array=JSONObject.parseArray(String.valueOf(group.get("Malfunction_Type")));
	 		for(int i=0;i<Array.size();i++){
	 			Map<String,Object> tMap=Array.getJSONObject(i);  
	 			tMap.put("group_id",group.get("group_id"));
	 			systemDao.insertGroup_malfunction_type_relation(tMap);//在关联关系关表中添加一条
	 		}
		}
		
		return result2;
		
	}

	/**
	 * 修改工作组
	 */
	@Override
	public int updateGroupById(Map<String,Object> group) {
		Map<String, Object> groupupdate=systemDao.getGroupById(Integer.parseInt(String.valueOf(group.get("group_id"))));
		Map<String, Object> map=new HashMap<>();
		map.put("group_name",group.get("name"));
		map.put("rong_group_id",groupupdate.get("rong_group_id"));
		CommonDataServiceImpl.rong_group_update(map); //修改群组
		int result =systemDao.updateGroupById(group);
		if (result>0){
			systemDao.updateUserDepartmentByGruopId(group);  //修改改工作组下的用户为之后的部门
		}

		if(group.get("Malfunction_Type")!=null){
			JSONArray Array=JSONObject.parseArray(String.valueOf(group.get("Malfunction_Type")));
			systemDao.deleteGroup_malfunction_type_relation(Integer.parseInt(String.valueOf(group.get("group_id"))));
	 		for(int i=0;i<Array.size();i++){
	 			Map<String,Object> tMap=Array.getJSONObject(i);  
	 			tMap.put("group_id",group.get("group_id"));
	 			systemDao.insertGroup_malfunction_type_relation(tMap);//在关联关系关表中添加一条
	 		}
		}
		
		
		return result;
		
	}
	
	@Override
	public PageUtil<Map<String, Object>> findGroupPage(PageUtil<Map<String, Object>> page) {
		PageHelper.startPage(page.getPageindex(), page.getPagesize());   //分页
		List<Map<String, Object>> list=systemDao.findGroupPage(page);  //分页之后的集合
		PageUtil<Map<String, Object>> pageInfo=new PageUtil<Map<String, Object>>();  //存到page里
		pageInfo.setData(list);   //把查询结果存到page里
		pageInfo.setRecordCount(systemDao.findGroupPage(page).size());   //总记录数
		return pageInfo;
	}
	

	@Override
	public Map<String,Object> getGroupById(Integer id) {
		Map<String,Object> group=systemDao.getGroupById(id);
		Map<String,Object> dMap=new HashMap<String, Object>();
		dMap.put("company_id", group.get("company_id"));
		List<Map<String, Object>> allDepartment = systemDao.getAllDepartment(dMap);
		group.put("departmentList", allDepartment);
		return group;
	}

	
	@Override
	public List<Map<String,Object>> getAllMalfunction_Type(Map<String, Object> map){
		
		if(map.get("group_id")!=null&&map.get("group_id")!=""){
			List<Map<String, Object>> Type = systemDao.getAllMalfunction_Type(map);
			List<Map<String, Object>> group_Type = systemDao.getGroup_Malfunction_Type(map);
			for(int i=0;i<Type.size();i++){
				Type.get(i).put("is_checked", 0);
				for(int j=0;j<group_Type.size();j++){
					if(String.valueOf(group_Type.get(j).get("malfunction_type_id")).equals(String.valueOf(Type.get(i).get("id")))){
						Type.get(i).put("is_checked", 1);
					}
				}
			}
			
			return Type;
			
		}else{
			return systemDao.getAllMalfunction_Type(map);
		}
	}
	
	
	
	
	//////////////Company
	@Override
	public List<Map<String, Object>> getAllCompany(Map<String, Object> map) {
		return systemDao.getAllCompany(map);
	}

	@Override
	public PageUtil<Map<String,Object>> findCompanyPage(PageUtil<Map<String,Object>> page) {
		PageHelper.startPage(page.getPageindex(), page.getPagesize());   //分页
		List<Map<String,Object>> list=systemDao.findCompanyPage(page);  //分页之后的集合
		PageUtil<Map<String,Object>> pageInfo=new PageUtil<Map<String,Object>>();  //存到page里
		pageInfo.setData(list);   //把查询结果存到page里
		pageInfo.setRecordCount(systemDao.findCompanyPage(page).size());   //总记录数
		return pageInfo;
	}
	/**
	 * 获取公司信息
	 */
	@Override
	public Map<String, Object> getCompanyById(Integer id) {
		return systemDao.getCompanyById(id);
	}


	/**
	 * 删除公司
	 * @return
	 */
	@Override
	public int deleteCompanyById(Integer id,Integer user_id,String rong_user_id) {
		Map<String, Object> company=systemDao.getCompanyById(id);
		company.put("dismiss_by",user_id);
		company.put("rong_user_id",rong_user_id);
		//解散群公共方法
		CommonDataServiceImpl.rong_group_dismiss(company);
		company.put("rong_group_id",company.get("notice_rong_group_id"));
		//解散公共群
		CommonDataServiceImpl.rong_group_dismiss(company);
		return systemDao.deleteCompanyById(id);
	}


	/**
	 * 新增公司
	 */
	@Override
	public int insertCompany(Map<String, Object> company) {
		Map<String, Object> map=new HashMap<>();
		map.put("group_name",company.get("cname"));
		map.put("create_by",company.get("create_by"));
		map.put("is_structure",1);  //是公司架构
		int result=0;
		//建立公司群组
		result=	CommonDataServiceImpl.rong_group_insert(map);
		if(result>0){
			company.put("rong_group_id",map.get("rong_group_id"));
		}
		//建立通知群组
		map.put("group_name","工作通知:"+company.get("cname"));
		result=	CommonDataServiceImpl.rong_group_insert(map);
		if(result>0){
			company.put("notice_rong_group_id",map.get("rong_group_id"));
		}
		return systemDao.insertCompany(company);
	}


	/**
	 * 修改公司信息
	 */
	@Override
	public int updateCompanyById(Map<String, Object> company) {
		Map<String, Object> companyupdate=systemDao.getCompanyById(Integer.parseInt(String.valueOf(company.get("id"))));
		Map<String, Object> map=new HashMap<>();
		map.put("group_name",company.get("cname"));
		map.put("rong_group_id",companyupdate.get("rong_group_id"));
		CommonDataServiceImpl.rong_group_update(map); //修改群组
		return systemDao.updateCompanyById(company);
	}
	
	
	////////////Department
	@Override
	public List<Map<String, Object>> getAllDepartment(Map<String, Object> map) {
		return systemDao.getAllDepartment(map);
	}
	
	@Override
	public PageUtil<Map<String,Object>>findDepartmentPage(PageUtil<Map<String,Object>> page) {
		PageHelper.startPage(page.getPageindex(), page.getPagesize());   //分页
		List<Map<String,Object>> list=systemDao.findDepartmentPage(page);  //分页之后的集合
		PageUtil<Map<String,Object>> pageInfo=new PageUtil<Map<String,Object>>();  //存到page里
		pageInfo.setData(list);   //把查询结果存到page里
		pageInfo.setRecordCount(systemDao.findCompanyPage(page).size());   //总记录数
		return pageInfo;
	}


	@Override
	public Map<String, Object> getDepartmentById(Integer id) {
		return systemDao.getDepartmentById(id);
	}

	/**
	 * 删除部门
	 * @param
	 * @return
	 */
	@Override
	public int deleteDepartmentById(Integer id,Integer user_id,String rong_user_id) {
		Map<String, Object> department=systemDao.getDepartmentById(id);
		department.put("dismiss_by",user_id);
		department.put("rong_user_id",rong_user_id);
		CommonDataServiceImpl.rong_group_dismiss(department);  //解散群公共方法
		return systemDao.deleteDepartmentById(id);
	}

	/**
	 * 新增部门
	 * @param
	 * @return
	 */
	@Override
	public int insertDepartment(Map<String, Object> department) {
		Map<String, Object> map=new HashMap<>();
		map.put("group_name",department.get("dname"));
		map.put("create_by",department.get("create_by"));
		map.put("is_structure",1);  //是公司架构
		int result=CommonDataServiceImpl.rong_group_insert(map); //建立群组
		if(result>0){
			department.put("rong_group_id",map.get("rong_group_id"));
		}
		return systemDao.insertDepartment(department);
	}

	/**
	 * 修改部门
	 * @param
	 * @return
	 */
	@Override
	public int updateDepartmentById(Map<String, Object> department) {
		Map<String, Object> departmentupdate=systemDao.getDepartmentById(Integer.parseInt(String.valueOf(department.get("id"))));
		Map<String, Object> map=new HashMap<>();
		map.put("group_name",department.get("dname"));
		map.put("rong_group_id",departmentupdate.get("rong_group_id"));
		CommonDataServiceImpl.rong_group_update(map); //修改群组
		int result =systemDao.updateDepartmentById(department);
		if (result>0){
			systemDao.updateUserCompanyByDepartmentId(department);  //修改部门下的用户为之后的公司
		}
		return result;
	}



}
