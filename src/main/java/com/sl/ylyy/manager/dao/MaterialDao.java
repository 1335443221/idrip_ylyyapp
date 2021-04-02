package com.sl.ylyy.manager.dao;

import java.util.ArrayList;
import java.util.Map;
import org.apache.ibatis.annotations.Mapper;
import com.sl.ylyy.common.utils.PageUtil;

@Mapper
public interface MaterialDao {
	
	public void insertMaterialAppointment(Map<String, Object> map);
	
	public void insertMaterialComplete(Map<String, Object> map);
	
	public ArrayList<Map<String, Object>> getMaterialLog(Map<String, Object> map);
	
	public int updateMalfunction(Map<String, Object> map);
	
	public int updateMalfunctionLog(Map<String, Object> map);
	
	public int updateSupport(Map<String, Object> map);
	
	
	public int getMaxIdFromMA();
	
	public void insertPushMaterial(Map<String, Object> map);
	
	public ArrayList<Map<String, Object>> findPage(PageUtil<Map<String, Object>> page);
	
	public ArrayList<Map<String, Object>> materiallist_tokeeper(Map<String, Object> map);
	
	public ArrayList<Map<String, Object>> materiallist_tokeeper2(Map<String, Object> map);
	
}
