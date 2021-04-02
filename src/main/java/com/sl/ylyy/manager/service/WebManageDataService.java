package com.sl.ylyy.manager.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.sl.ylyy.common.utils.PageUtil;

public interface WebManageDataService {
	
	////////////////////////web//////////////////////////////////////////////////
	
	
	//工程报表
	public PageUtil<Map<String, Object>> showProjectLog(Map<String, Object> map);
	
	//工程信息
	public PageUtil<Map<String, Object>> web_project_list(Map<String, Object> map,HttpSession session);

	//工程分类信息
	public List<Map<String, Object>> web_project_type_list(Map<String, Object> map);
	
	//工程新增
	public Object web_add_project(String json);
	//工程前期新增
	public Object web_add_early_project(String json);
	

	public Map<String, Object> web_project_data_ById(Map<String, Object> map);
	//修改工程以及前期信息
	public Object web_update_project(String json);
	
	//开工信息
	public Map<String, Object> web_start_data_ById(Map<String, Object> map);
	public Object web_update_start(String json);
	
	
	//工程施工中信息
	public Object web_working_data(Map<String, Object> map);

	//预验收信息
	public Map<String, Object> web_before_data_ById(Map<String, Object> map);
	public Object web_update_before(String json);
	
	//验收信息
	public Map<String, Object> web_check_data_ById(Map<String, Object> map);
	public Object web_update_check(String json);
	
	
	//结算列表
	public PageUtil<Map<String, Object>> web_project_settlement_list(Map<String, Object> map);
	//结算编辑列表
	public Object web_update_settlement_list(Map<String, Object> map);
	
	public Object go_add_settlement(Map<String, Object> map);
	public Object goUpdateSettment(Map<String, Object> map);
	
	public Object web_add_update_settment(String json);
	//导出报表
	public HSSFWorkbook export_project_Log(Map<String, Object> map);
	
	
	
	
	
}
