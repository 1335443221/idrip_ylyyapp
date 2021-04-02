package com.sl.ylyy.manager.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sl.ylyy.app_1.entity.Switchingroom;
import com.sl.ylyy.common.utils.PageUtil;


public interface SwitchingroomDataService {
	public ArrayList<Switchingroom> getAllSwitchingroom(String Stg);
	
	public PageUtil<Switchingroom> findPage(PageUtil<Switchingroom> page);
	
	public Switchingroom getSwitchingroomById(Integer id);
	
	public void deleteSwitchingroomById(Integer id);
	
	public void insertSwitchingroom(Map<String, Object> map);
	
	public void updateSwitchingroomById(Map<String, Object> map);
	
	public Switchingroom getSwitchingroomBySequence(Map<String, Object> map);
	
	public int getMaxSequenceSwitchingroom();
	public List<Switchingroom> getSwitchingroombyS(Switchingroom s);
	
	public List<Switchingroom> getSwitchingroombyTip(int tid);
	
	
}
