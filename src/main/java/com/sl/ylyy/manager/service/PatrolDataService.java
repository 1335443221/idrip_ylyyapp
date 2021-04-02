package com.sl.ylyy.manager.service;

import java.util.ArrayList;
import java.util.Map;

import com.sl.ylyy.manager.entity.PatrolInfo;
import com.sl.ylyy.common.utils.PageUtil;

import javax.servlet.http.HttpSession;


public interface PatrolDataService {
	public ArrayList<PatrolInfo> getAllPatrol(String rname);
	
	public PageUtil<Map<String,Object>> findPage(Map<String,Object> map, HttpSession session);

	//巡检类型，巡检点，巡检时间
	public ArrayList<Map<String,Object>> getPatrolType(Map<String,Object> map);
	public ArrayList<Map<String,Object>> getPatrolPoint(Map<String,Object> map);
	public ArrayList<Map<String,Object>> patrolTime(Map<String,Object> map);
	public ArrayList<Map<String,Object>> patrolTimeInterval(Map<String,Object> map);
	public ArrayList<Map<String,Object>> departmentList(Map<String,Object> map);
	public ArrayList<Map<String,Object>> getPatrolCountByTime(Map<String,Object> map);
	public String insertPatrol(Map<String,Object> map);



	public PatrolInfo getPatrolById(Integer id);
	
	public void deletePatrolById(Integer id);
	
//	public void insertPatrol(PatrolInfo PatrolInfo);
	
	public void updatePatrolById(PatrolInfo PatrolInfo);
	
}
