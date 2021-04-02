package com.sl.ylyy.manager.service;

import java.util.ArrayList;

import com.sl.ylyy.manager.entity.Patrol_Log;
import com.sl.ylyy.common.utils.PageUtil;


public interface Patrol_LogDataService {
	public ArrayList<Patrol_Log> getAllPatrol_Log(String Stg);
	
	public PageUtil<Patrol_Log> findPage(PageUtil<Patrol_Log> page);
	
	public PageUtil<Patrol_Log> report(PageUtil<Patrol_Log> page);
	
	
	public Patrol_Log getPatrol_LogById(Integer id);
	
	public void deletePatrol_LogById(Integer id);
	
	public void insertPatrol_Log(Patrol_Log Patrol_Log);
	
	public void updatePatrol_LogById(Patrol_Log Patrol_Log);
	
}
