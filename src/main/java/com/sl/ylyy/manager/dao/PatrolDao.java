package com.sl.ylyy.manager.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sl.ylyy.manager.entity.PatrolInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PatrolDao {
	
	public ArrayList<PatrolInfo> getAllPatrol(String rname);
	
	public List<Map<String,Object>> findPage(Map<String,Object> page);

	public List<Map<String,Object>> patrol_type_list(Map<String,Object> page);

	public List<Map<String,Object>> patrol_time_list(Map<String,Object> page);

	public PatrolInfo getPatrolById(Integer id);
	
//	public void insertPatrol(PatrolInfo PatrolInfo);
	
	public void deletePatrolById(Integer id);

	public void deletePatrolLogByPid(Integer id);

	public void deletePatrolByPId(Integer id);
	
	public void updatePatrolById(PatrolInfo PatrolInfo);



	public ArrayList<Map<String,Object>> getPatrolType(Map<String,Object> map);
	public ArrayList<Map<String,Object>> getPatrolPoint(Map<String,Object> map);
	public ArrayList<Map<String,Object>> patrolTime(Map<String,Object> map);
	public ArrayList<Map<String,Object>> patrolTimeInterval(Map<String,Object> map);
	public ArrayList<Map<String,Object>> departmentList(Map<String,Object> map);
	public int insertPatrol(Map<String,Object> map);


	
}
