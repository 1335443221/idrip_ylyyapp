package com.sl.ylyy.manager.service;

import java.util.ArrayList;
import java.util.Map;

import com.sl.ylyy.manager.entity.VersionInfo;
import com.sl.ylyy.common.utils.PageUtil;


public interface VersionDataService {
	public ArrayList<VersionInfo> getAllVersion(String gname);
	
	public PageUtil<VersionInfo> findPage(PageUtil<VersionInfo> page);
	
	public VersionInfo getVersionById(Integer id);
	
	public ArrayList<VersionInfo> checkVersion(Map<String, Object> map);
	
	public void deleteVersionById(Integer id);
	
	public Object insertVersion(VersionInfo VersionInfo);
	
	public Object updateVersionById(VersionInfo VersionInfo);
	
	public VersionInfo getMaxIdVersion();
	
    public Map<String, Object>  getMaxIdQrCode();
  
    public int insertQrCode(Map<String, Object> map);
    
    public Object  getQrCodeUrl(Map<String, Object> map);
	
}
