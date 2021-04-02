package com.sl.ylyy.manager.service;
import java.util.Map;
import com.sl.ylyy.common.utils.PageUtil;


public interface MaterialDataService {
	
	public PageUtil<Map<String, Object>> findPage(PageUtil<Map<String, Object>> page);
	
	public Object addMaterialAppointment(String json);
	
	public Object addMaterialComplete(String json);
	public Object addMaterialComplete2(String json);

	public Object getMaterialLog(Map<String, Object> map);
	
	public Object materiallist_tokeeper(Map<String, Object> map);
	
}
