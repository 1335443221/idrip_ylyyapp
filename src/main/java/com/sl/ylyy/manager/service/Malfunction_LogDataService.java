package com.sl.ylyy.manager.service;

import java.util.ArrayList;

import com.sl.ylyy.manager.entity.MalfunctionLogInfo;
import com.sl.ylyy.common.utils.PageUtil;


public interface Malfunction_LogDataService {
	public ArrayList<MalfunctionLogInfo> getAllMalfunction_Log(String mname);
	
	public PageUtil<MalfunctionLogInfo> findPage(PageUtil<MalfunctionLogInfo> page);
	
	public PageUtil<MalfunctionLogInfo> report(PageUtil<MalfunctionLogInfo> page);
	
	public void deleteMalfunction_LogById(Integer id);
	
	public void insertMalfunction_Log(MalfunctionLogInfo MalfunctionLogInfo);
	
	public void updateMalfunction_LogById(MalfunctionLogInfo MalfunctionLogInfo);
	
}
