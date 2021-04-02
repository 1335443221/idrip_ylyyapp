package com.sl.ylyy.manager.service;

import java.util.ArrayList;
import java.util.Map;

import com.sl.ylyy.common.utils.CodeMsg;
import com.sl.ylyy.common.utils.Result;
import com.sl.ylyy.manager.entity.UserInfo;
import com.sl.ylyy.common.utils.PageUtil;
public interface UserDataService {
	
	public ArrayList<UserInfo> getAllUser(Map<String, Object> map);
	
	public Map<String, Object> getUserinfoById(Integer id);
	
	public ArrayList<Map<String, Object>> checkUser(Map<String, Object> map);
	
	public ArrayList<UserInfo> checkJob_number(String sta);
	
	public ArrayList<UserInfo> checkLogin_name(String sta);
	
	public ArrayList<UserInfo> checkCellphone(Map<String, Object> map);
	
	
	public int updateUserinfo(Map<String, Object> map);
	
	public int insertUser(Map<String, Object> map);
	
	public void deleteUserById(Integer id);
	
	public PageUtil<Map<String,Object>> findPage(PageUtil<Map<String,Object>> page);
	

	public ArrayList<Map<String,Object>> checkDelete(Map<String,Object> map);
	
	
	public Object checkAppLogin(Map<String,Object> map);

	public Object getQiNiuBucket(Map<String,Object> map);

	public CodeMsg recordRongUserGroupData(Map<String,Object> map);
	public CodeMsg recordRongUserToken(Map<String,Object> map);
	public CodeMsg recordRongGroup(Map<String,Object> map);

	public Result checkNoticeAuth(Map<String,Object> map);

}
