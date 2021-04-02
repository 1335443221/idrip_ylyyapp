package com.sl.ylyy.manager.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sl.ylyy.manager.entity.MalfunctionInfo;
import org.apache.ibatis.annotations.Mapper;

import com.sl.ylyy.manager.entity.Audio_Desc;
import com.sl.ylyy.manager.entity.Image_Desc;
import com.sl.ylyy.manager.entity.Text_Desc;
import com.sl.ylyy.common.utils.PageUtil;

@Mapper
public interface MalfunctionDao {
	
	public ArrayList<MalfunctionInfo> getAllMalfunction(String mname);
	
	public List<MalfunctionInfo> findPage(PageUtil<MalfunctionInfo> page);
	
	public void insertMalfunction(MalfunctionInfo MalfunctionInfo);
	
	public void deleteMalfunctionById(Integer id);
	
	public void updateMalfunctionById(MalfunctionInfo MalfunctionInfo);
	
	/**
	 * 获取巡检故障总数
	 * @param map
	 * @return
	 */
	public int getMalPatrolCount(Map<String, Object> map);
	
	
	public MalfunctionInfo getMalfunctionDetail(Integer id);
	
	
	public List<Image_Desc> getImage_DescById(List list);
	
	public List<Text_Desc> getText_DescById(List list);
	
	public List<Audio_Desc> getAudio_DescById(List list);
	
	//故障上报
	public List<Map<String,Object>> getAllMalfunctionReport(Map<String, Object> map);
	
	public List<Map<String,Object>> getAllMalfunctionType(Map<String, Object> map);
	
	public int insertMalfunctionReport(Map<String, Object> map);
	
	public int insertTextDesc(Map<String, Object> map);
	public int insertImageDesc(Map<String, Object> map);
}
