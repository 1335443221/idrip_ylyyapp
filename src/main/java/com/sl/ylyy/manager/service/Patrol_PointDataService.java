package com.sl.ylyy.manager.service;

import java.util.ArrayList;
import java.util.Map;

import com.sl.ylyy.manager.entity.PatrolPointInfo;
import com.sl.ylyy.common.utils.PageUtil;


public interface Patrol_PointDataService {
	public ArrayList<PatrolPointInfo> getAllPatrol_Point(String ppname);
	
	public PageUtil<PatrolPointInfo> findPage(PageUtil<PatrolPointInfo> page);
	
	public PatrolPointInfo getPatrol_PointById(Integer id);
	
	public void deletePatrol_PointById(Integer id);
	
	public int insertPatrol_Point(Map<String, Object> map);
	
	public int updatePatrol_PointById(Map<String, Object> map);
	
}
