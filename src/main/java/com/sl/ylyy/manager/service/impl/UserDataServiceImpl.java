package com.sl.ylyy.manager.service.impl;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sl.ylyy.common.utils.*;
import com.sl.ylyy.manager.dao.RongCloudDao;
import com.sl.ylyy.manager.entity.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.github.pagehelper.PageHelper;
import com.sl.ylyy.common.config.UrlConfig;
import com.sl.ylyy.manager.dao.SystemDao;
import com.sl.ylyy.manager.dao.UserDao;
import com.sl.ylyy.manager.service.UserDataService;

import static com.sl.ylyy.common.utils.JwtToken.getUserIdByToken;

/**
 * @author lixuri
 * @date 2019-9-20 17:23:20
 */
@Service("userDataImpl")
public class UserDataServiceImpl implements UserDataService {

	
	@Autowired
	private UserDao userDao;
	@Autowired
	private SystemDao systemDao;
	@Autowired
	private RongCloudDao rongCloudDao;



	@Override
	public ArrayList<UserInfo> getAllUser(Map<String, Object> map) {
		return userDao.getAllUser(map);
	}


	/**
	 * 用户信息
	 */
	@Override
	public Map<String, Object> getUserinfoById(Integer id) {
		Map<String, Object> userinfo = userDao.getUserinfoById(id);
		
		Map<String, Object> mapData =new HashMap<>();
		mapData.put("company_id", userinfo.get("company_id"));
		List<Map<String, Object>> allDepartment = systemDao.getAllDepartment(mapData);
		userinfo.put("departmentList",allDepartment);
		
		if(userinfo.get("department_id")!=null){
			mapData.put("department_id", userinfo.get("department_id"));
			List<Map<String, Object>> allGroup = systemDao.getAllGroup(mapData);
			userinfo.put("groupList",allGroup);
		}
		
		
		return userinfo;
	}


	@Override
	public ArrayList<Map<String, Object>> checkUser(Map<String, Object> map) {
		return userDao.checkUser(map);
	}

	
	/**
	 * 修改用户信息
	 */
	@Override
	public int updateUserinfo(Map<String, Object> map) {
		
		Map<String, Object> map2=new HashMap<>();   //修改融云  并且存进数据库
		map2.put("rong_user_id",map.get("rong_user_id"));
		map2.put("user_name", map.get("user_name"));
		map2.put("portrait", map.get("portrait"));
		RongUtil.userUpdate(map2);

		//用户信息
		Map<String, Object> user=userDao.getUserinfoById(Integer.parseInt(String.valueOf(map.get("id"))));
		map.put("user_id", Integer.parseInt(String.valueOf(map.get("id"))));
		if(map.get("role_id")!=null&&map.get("role_id")!=""){
			//如果role_id相同 证明角色没有改变
			if (String.valueOf(user.get("role_id")).equals(String.valueOf(map.get("role_id")))){
				user.remove("role_id");  //删掉role_id
			}else{
				systemDao.updateRole_User_Relation(map); //修改角色表
			}
		}
		
		
		Map<String,Object> updateMap=new HashMap<>();
		updateMap.put("user_id", map.get("id"));
		
		if(Integer.parseInt(String.valueOf(map.get("role_id")))==3){  //部门经理
			updateMap.put("department_id", Integer.parseInt(String.valueOf(map.get("department_id"))));
			systemDao.updateDepartmentByDId(updateMap);
		}
		
		if(Integer.parseInt(String.valueOf(map.get("role_id")))==4){  //班组长
			if(map.get("group_id")!=null&&!"".equals(String.valueOf(map.get("group_id")))) {
				updateMap.put("group_id", Integer.parseInt(String.valueOf(map.get("group_id"))));
				systemDao.updateGroupByGId(updateMap);
			}
		}
		
		if(map.get("department_id")==null|| "".equals(String.valueOf(map.get("department_id")))){
			map.put("department_id", null);
		}
		if(map.get("group_id")==null|| "".equals(String.valueOf(map.get("group_id")))){
			map.put("group_id", null);
		}
		if(map.get("portrait")==null|| "".equals(String.valueOf(map.get("portrait")))){
			map.put("portrait", UrlConfig.UserPortrait);
		}

		deleteUserGroupRelation(Integer.parseInt(String.valueOf(map.get("id"))));  //删除关联关系
		insertUserGroupRelation(Integer.parseInt(String.valueOf(map.get("id"))));  //新增关联关系



		int result=userDao.updateUserinfo(map);


		//如果成功..存center表 app用户 和 user表用户
		/*if (result>0){
			HttpClientService hc=new HttpClientService();
			Map<String,String> params=new HashMap<>();
			//用户信息
			Map<String, Object> userParams=userDao.elcmGetUserIdById(String.valueOf(map.get("id")));
			//如果角色没问题 不需要改角色
			if (user.get("role_id")!=null){
				params.put("role_id",userParams.get("center_role_id").toString());
			}
			params.put("uname",userParams.get("cellphone").toString()); //登录账号
			params.put("pwd",userParams.get("password").toString()); //登录密码
			params.put("oper_pwd",userParams.get("password").toString()); //操作密码
			params.put("name",userParams.get("user_name").toString()); //用户名
			params.put("user_id",userParams.get("center_project_user_id").toString()); //用户id
			params.put("phone",userParams.get("cellphone").toString()); //手机号
			String centerUrl = UrlConfig.PMP_IDRIP_URL +"user/updateAppUserAndUser";
			String data = hc.post(centerUrl, params);
		}*/
		return  result;
		
	}

	
	
	
	/**
	 * 新增用户信息
	 */
	@Override
	public int insertUser(Map<String, Object> map) {
		if(map.get("department_id")==null|| "".equals(String.valueOf(map.get("department_id")))){ //部门
			map.put("department_id", null);
		}
		if(map.get("group_id")==null|| "".equals(String.valueOf(map.get("group_id")))){ //工作组
			map.put("group_id", null);
		}
		if(map.get("portrait")==null|| "".equals(String.valueOf(map.get("portrait")))){  //头像
			map.put("portrait", UrlConfig.UserPortrait);
		}
		int result=userDao.insertUser(map);	//存用户
		
		systemDao.insertRole_User_Relation(map); //角色关系表
		Map<String,Object> updateMap=new HashMap<>();
		updateMap.put("user_id", map.get("user_id"));
		
		if(Integer.parseInt(String.valueOf(map.get("role_id")))==3){  //部门经理
			updateMap.put("department_id", Integer.parseInt(String.valueOf(map.get("department_id"))));
			systemDao.updateDepartmentByDId(updateMap);
		}
		
		if(Integer.parseInt(String.valueOf(map.get("role_id")))==4){  //班组长 主管
			if(map.get("group_id")!=null&&!"".equals(String.valueOf(map.get("group_id")))) {
				updateMap.put("group_id", Integer.parseInt(String.valueOf(map.get("group_id"))));
				systemDao.updateGroupByGId(updateMap);
			}
		}

		Map<String, Object> map2=new HashMap<>();
		map2.put("rong_user_id", UrlConfig.RONG_ID_PREFIX+map.get("user_id"));
		map2.put("user_name", map.get("user_name"));
		map2.put("portrait", map.get("portrait"));
		
		Map<String, Object> map3=RongUtil.userRegister(map2);   //注册融云用户
		map.put("rong_token", String.valueOf(map3.get("token")));
		map.put("rong_user_id", map2.get("rong_user_id"));
		userDao.updateUserinfo(map);  //修改用户信息（增加rong_token）

		insertUserGroupRelation(Integer.parseInt(String.valueOf(map.get("user_id"))));  //关联关系

		/*//如果成功..存center表 user表用户
 		if (result>0){

			HttpClientService hc=new HttpClientService();
			Map<String,String> params=new HashMap<>();
			//用户信息
			Map<String, Object> userParams=userDao.elcmGetUserIdById(String.valueOf(map.get("user_id")));
			//如果角色没问题 不需要改角色
			params.put("role_id",userParams.get("center_role_id").toString());
			params.put("uname",userParams.get("cellphone").toString()); //登录账号
			params.put("pwd",userParams.get("password").toString()); //登录密码
			params.put("oper_pwd",userParams.get("password").toString()); //登录密码
			params.put("name",userParams.get("user_name").toString()); //用户名
			params.put("phone",userParams.get("cellphone").toString()); //手机号
			params.put("operate_system","1,2");
			params.put("type","1");
			params.put("project_id","36"); //高新项目
			String centerUrl = UrlConfig.PMP_IDRIP_URL +"user/insertAppUserAndUser";
			String data = hc.post(centerUrl, params);

			//新增 center-user 关联
			Map<String, Object> userMap=new HashMap<>();
			userMap.put("id",map.get("user_id"));
			userMap.put("center_project_user_id",data);
			userDao.elcmUpdateUserinfo(userMap);

		}*/

		return result;
	}

	
	/**
	 * 删除用户信息
	 */
	@Override
	public void deleteUserById(Integer id) {

		Map<String, Object> userParams=userDao.elcmGetUserIdById(String.valueOf(id));
		deleteUserGroupRelation(id);  //删除用户关系
		int result=userDao.deleteUserById(id);  //逻辑删除用户
		//如果成功..存center表 user表用户
		/*if (result>0){
			HttpClientService hc=new HttpClientService();
			Map<String,String> params=new HashMap<>();
			//用户信息
			params.put("user_id",userParams.get("center_project_user_id").toString());
			params.put("is_delete","1");
			String centerUrl = UrlConfig.PMP_IDRIP_URL +"user/deleteAppUserAndUser";
			String data = hc.post(centerUrl, params);
		}*/

	}

	
	/**
	 * 分页查询
	 */
	@Override
	public PageUtil<Map<String,Object>> findPage(PageUtil<Map<String,Object>> page) {
		PageHelper.startPage(page.getPageindex(),page.getPagesize());   //分页
		List<Map<String,Object>> list=userDao.findPage(page);  //分页之后的集合
		PageUtil<Map<String,Object>> pageInfo=new PageUtil<Map<String,Object>>();  //存到page里
		pageInfo.setData(list);   //把查询结果存到page里
		pageInfo.setRecordCount(userDao.findPage(page).size());   //总记录数
		return pageInfo;
	}


	@Override
	public ArrayList<UserInfo> checkJob_number(String sta) {
		return userDao.checkJob_number(sta);
	}


	@Override
	public ArrayList<UserInfo> checkLogin_name(String sta) {
		return userDao.checkLogin_name(sta);
	}

	@Override
	public ArrayList<UserInfo> checkCellphone(Map<String, Object> map) {
		return userDao.checkCellphone(map);
	}

	@Override
	public ArrayList<Map<String, Object>> checkDelete(Map<String, Object> map) {
		return userDao.checkDelete(map);
	}


	
	/**
	 * app登录接口
	 */
	@Override
	public Object checkAppLogin(Map<String, Object> map) {
		Map<String, Object> map2=userDao.checkAppLogin(map);
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");   //把时间转换成年月日
		if(map2.get("create_at")!=null&&!"".equals(map2.get("create_at"))){
			long a=Integer.parseInt(map2.get("create_at").toString());
			long a2=a*1000;
			map2.put("create_at", sdf.format(a2));
		}
		if(map2.get("update_at")!=null&&!"".equals(map2.get("update_at"))){
			long a=Integer.parseInt(map2.get("update_at").toString());
			long a2=a*1000;
			map2.put("update_at", sdf.format(a2));
		}
		
		map2.put("portrait", UrlConfig.DOWNURL+map2.get("portrait"));
		
		map2.put("token", "");
		return Result.success(map2);
	}


	/**
	 * 获取七牛服务器bucket
	 */
	@Override
	public Object getQiNiuBucket(Map<String, Object> map) {
		
		return Result.success(userDao.getQiNiuBucket(map));
	}



	/**
	 * 删除用户 群组 关联关系
	 */
	public void deleteUserGroupRelation(int id) {
		Map<String, Object> user=userDao.getUserinfoById(id);
		Map<String, Object> relationMap=new HashMap<>();
		relationMap.put("rong_user_id",user.get("rong_user_id"));

		List<Map<String,Object>> userList=new ArrayList<>();
		//公司群删除
		Map<String, Object> company=systemDao.getCompanyById(Integer.parseInt(String.valueOf(user.get("company_id"))));
		relationMap.put("rong_group_id",company.get("rong_group_id"));
		//本地删除 群组
		rongCloudDao.group_user_delete(relationMap);
		userList.add(relationMap);
		relationMap.put("userList",userList);
		//融云 删除 群组关系
		RongUtil.groupQuit(relationMap);

		//通知群
		relationMap.put("rong_group_id",company.get("notice_rong_group_id"));
		rongCloudDao.group_user_delete(relationMap);
		userList.add(relationMap);
		relationMap.put("userList",userList);
		RongUtil.groupQuit(relationMap);


		if(user.get("department_id")!=null&&!"".equals(String.valueOf(user.get("department_id")))){ //部门
			Map<String, Object> department=systemDao.getDepartmentById(Integer.parseInt(String.valueOf(user.get("department_id"))));  //
			relationMap.put("rong_group_id",department.get("rong_group_id"));
			rongCloudDao.group_user_delete(relationMap);
			RongUtil.groupQuit(relationMap);
		}
		if(user.get("group_id")!=null&&!"".equals(String.valueOf(user.get("group_id")))){ //工作组
			Map<String, Object> group=systemDao.getGroupById(Integer.parseInt(String.valueOf(user.get("group_id"))));  //
			relationMap.put("rong_group_id",group.get("rong_group_id"));
			rongCloudDao.group_user_delete(relationMap);
			RongUtil.groupQuit(relationMap);
		}
	}


	/**
	 * 新建用户 群组 关联关系
	 */
	public void insertUserGroupRelation(int id) {
		Map<String, Object> user=userDao.getUserinfoById(id);

		List<Map<String,Object>> userList=new ArrayList<>();
		Map<String, Object> map4=new HashMap<>();
		Map<String, Object> company=systemDao.getCompanyById(Integer.parseInt(String.valueOf(user.get("company_id"))));
		map4.put("rong_group_id",company.get("rong_group_id"));
		map4.put("rong_user_id",user.get("rong_user_id"));
		//本地加入群
		rongCloudDao.group_user_insert(map4);
		map4.put("group_name",company.get("cname"));
		userList.add(map4);
		map4.put("userList",userList);
		//注册融云  加入群
		RongUtil.groupJoin(map4);

		map4.put("rong_group_id",company.get("notice_rong_group_id"));
		map4.put("group_name","工作通知:"+company.get("cname"));
		rongCloudDao.group_user_insert(map4);
		RongUtil.groupJoin(map4);

		if(user.get("department_id")!=null&&!"".equals(String.valueOf(user.get("department_id")))){ //部门
			Map<String, Object> department=systemDao.getDepartmentById(Integer.parseInt(String.valueOf(user.get("department_id"))));  //
			map4.put("rong_group_id",department.get("rong_group_id"));
			map4.put("group_name",department.get("dname"));
			rongCloudDao.group_user_insert(map4);  //本地加入群
			RongUtil.groupJoin(map4); //注册融云  加入群
		}
		if(user.get("group_id")!=null&&!"".equals(String.valueOf(user.get("group_id")))){ //工作组
			Map<String, Object> group=systemDao.getGroupById(Integer.parseInt(String.valueOf(user.get("group_id"))));  //
			map4.put("rong_group_id",group.get("rong_group_id"));
			map4.put("group_name",group.get("name"));
			rongCloudDao.group_user_insert(map4); //本地加入群
			RongUtil.groupJoin(map4); //注册融云  加入群
		}
	}




	/**
	 * 录入融云用户和群组关系数据
	 */
	@Override
	public CodeMsg recordRongUserGroupData(Map<String, Object> map) {
		List<Map<String, Object>> userList=userDao.getAllRongUser(map);
		for (int i=0;i<userList.size();i++){
			//删除关联关系
			deleteUserGroupRelation(Integer.parseInt(String.valueOf(userList.get(i).get("id"))));
			//新增关联关系
			insertUserGroupRelation(Integer.parseInt(String.valueOf(userList.get(i).get("id"))));
		}
		return CodeMsg.SUCCESS;
	}


	/**
	 * 录入融云用户
	 */
	@Override
	public CodeMsg recordRongUserToken(Map<String, Object> map) {
		CommonDataServiceImpl.testInsertUser(map);
		return CodeMsg.SUCCESS;
	}

	/**
	 * 录入融云群组
	 */
	@Override
	public CodeMsg recordRongGroup(Map<String, Object> map) {
		List<Map<String,Object>> companyList=systemDao.getAllCompany(map);
		List<Map<String,Object>> groupList=systemDao.getAllGroup(map);
		List<Map<String,Object>> departmentList=systemDao.getAllDepartment(map);

		for(int i=0;i<companyList.size();i++){
			Map<String,Object> company=new HashMap<>();
			company.put("group_portrait",UrlConfig.GroupPortrait);
			company.put("group_name","工作通知:"+companyList.get(i).get("cname"));
			company.put("rong_user_id","rong_sale_27");
			CommonDataServiceImpl.rong_group_insert(company);
		}

		for(int i=0;i<companyList.size();i++){
			Map<String,Object> company=new HashMap<>();
			company.put("group_portrait",UrlConfig.GroupPortrait);
			company.put("group_name",companyList.get(i).get("cname"));
			company.put("rong_user_id","rong_sale_27");
			CommonDataServiceImpl.rong_group_insert(company);
		}

		for(int i=0;i<groupList.size();i++){
			Map<String,Object> group=new HashMap<>();
			group.put("group_portrait",UrlConfig.GroupPortrait);
			group.put("group_name",groupList.get(i).get("name"));
			group.put("rong_user_id","rong_sale_27");
			CommonDataServiceImpl.rong_group_insert(group);
		}

		for(int i=0;i<departmentList.size();i++){
			Map<String,Object> department=new HashMap<>();
			department.put("group_portrait",UrlConfig.GroupPortrait);
			department.put("group_name",departmentList.get(i).get("dname"));
			department.put("rong_user_id","rong_sale_27");
			CommonDataServiceImpl.rong_group_insert(department);
		}



		return CodeMsg.SUCCESS;
	}

	/**
	 * 检查是否有通知群发通知的权限
	 */
	@Override
	public Result checkNoticeAuth(Map<String, Object> map) {
		int user_id=getUserIdByToken(String.valueOf(map.get("token")));
		Map<String, Object> user=userDao.getUserinfoById(user_id);
		int rid=Integer.parseInt(String.valueOf(user.get("role_id")));
		if("1".equals(String.valueOf(user.get("is_notice_auth")))){
			return Result.success(CodeMsg.SUCCESS,rid);
		}else{
			return Result.success(CodeMsg.MIMSSING_AUTH,rid);
		}
	}





}
