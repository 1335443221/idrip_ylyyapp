package com.sl.ylyy.manager.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import com.sl.ylyy.manager.entity.MalfunctionInfo;
import com.sl.ylyy.common.utils.PageUtil;


public interface MalfunctionDataService {
	public ArrayList<MalfunctionInfo> getAllMalfunction(String mname);
	
	public PageUtil<MalfunctionInfo> findPage(PageUtil<MalfunctionInfo> page);
	
	public void deleteMalfunctionById(Integer id);
	
	public void insertMalfunction(MalfunctionInfo MalfunctionInfo);
	
	public void updateMalfunctionById(MalfunctionInfo malfunctionInfo);
	
	public MalfunctionInfo getMalfunctionDetail(Integer id);
	
	
	
	public int insertMalfunctionReport(Map<String, Object> map);
	
	public PageUtil<Map<String, Object>> getAllMalfunctionReport(Map<String, Object> map,HttpSession session);
	
	public List<Map<String,Object>> getAllMalfunctionType(Map<String, Object> map);
	
}
