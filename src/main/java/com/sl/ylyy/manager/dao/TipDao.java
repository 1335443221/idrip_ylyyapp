package com.sl.ylyy.manager.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sl.ylyy.manager.entity.TipInfo;
import org.apache.ibatis.annotations.Mapper;

import com.sl.ylyy.common.utils.PageUtil;

@Mapper
public interface TipDao {
	
	public ArrayList<TipInfo> getAllTip(String Stg);
	
	public List<TipInfo> findPage(PageUtil<TipInfo> page);
	
	public TipInfo getTipById(Integer id);
	
	public int insertTip(Map<String, Object> map);
	
	public void deleteTipById(Integer id);
	
	public int updateTipById(Map<String, Object> map);
	
	public int getMaxIdTip();
	
	
}
