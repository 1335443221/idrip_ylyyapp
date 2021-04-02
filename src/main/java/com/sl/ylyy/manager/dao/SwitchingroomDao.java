package com.sl.ylyy.manager.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sl.ylyy.app_1.entity.Switchingroom;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.sl.ylyy.common.utils.PageUtil;

@Mapper
public interface SwitchingroomDao {
	
	public ArrayList<Switchingroom> getAllSwitchingroom(String Stg);
	
	public List<Switchingroom> findPage(PageUtil<Switchingroom> page);
	
	public Switchingroom getSwitchingroomById(Integer id);
	
	public void deleteSwitchingroomById(Integer id);
	
	public void insertSwitchingroom(Map<String, Object> map);
	
	public void updateSwitchingroomById(Map<String, Object> map);
	public Switchingroom getSwitchingroomBySequence(Map<String, Object> map);
	
	public int getMaxSequenceSwitchingroom();
	
	public List<Switchingroom> getSwitchingroombyTip(int tid);
	public List<Switchingroom> getSwitchingroombyS(Switchingroom s);
	
	
	public void updateSwitchingroomByTid(@Param("tid") Integer tid);
	
	public void deleteSwitchingroomByTid(@Param("tid") Integer tid);
}
