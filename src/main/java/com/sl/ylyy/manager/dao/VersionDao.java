package com.sl.ylyy.manager.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sl.ylyy.manager.entity.VersionInfo;
import org.apache.ibatis.annotations.Mapper;
import com.sl.ylyy.common.utils.PageUtil;

@Mapper
public interface VersionDao {
	
	public ArrayList<VersionInfo> getAllVersion(String gname);
	
	public List<VersionInfo> findPage(PageUtil<VersionInfo> page);
	
	public VersionInfo getVersionById(Integer id);
	
	public VersionInfo getMaxIdVersion();
	
	public int insertVersion(VersionInfo VersionInfo);
	
	public void deleteVersionById(Integer id);
	
	public int updateVersionById(VersionInfo VersionInfo);
	
	public List<VersionInfo> checkName(String name);
	
	public ArrayList<VersionInfo> checkVersion(Map<String, Object> map);
	
	
	public Map<String, Object>  getMaxIdQrCode();
	
	public int insertQrCode(Map<String, Object> map);
	
	
}
