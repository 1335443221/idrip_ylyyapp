package com.sl.ylyy.manager.service;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import com.sl.ylyy.common.utils.PageUtil;



public interface WebSupervisionDataService {
	
	   //app施工监护列表
		public PageUtil<Map<String, Object>> web_supervision_list(Map<String, Object> map);
		//施工监护时间类型列表
		public List<Map<String,Object>> supervision_time_list(Map<String, Object> map);
		
		public List<Map<String,Object>> supervision_project_list(Map<String, Object> map);
		
		//施工监护上报模板列表
		public List<Map<String,Object>> supervision_model_list(Map<String, Object> map);
		//施工监护上报模板内容列表
		public List<Map<String,Object>> supervision_model_item_list(Map<String, Object> map);
		//施工监护新增
		public int add_supervision(Map<String, Object> map);
		
		//施工监护
		public Map<String, Object> web_supervision_data_ById(Map<String, Object> map);
		
		//施工监护历史
		public PageUtil<Map<String, Object>> web_supervision_history(Map<String, Object> map);
		
		//施工监护上报历史
	    public Map<String, Object> web_report_data_ById(Map<String, Object> map);
	    public Map<String, Object> goSupplementReport(Map<String, Object> map);

	    public Map<String, Object> web_working_data_ById(Map<String, Object> map);

	   //加载更多监护记录
	   public Map<String, Object> web_load_more_report(Map<String, Object> map);
	   public int web_check_report(Map<String, Object> map);

	   //问题整改列表
	 	public PageUtil<Map<String, Object>> web_abarbeitung_list(Map<String, Object> map,HttpSession session);
		
	   //增加问题整改
	 	public int web_add_abarbeitung(String json);
	 	
	 	 //整改信息
		public Map<String, Object> abarbeitung_data_ById(Map<String, Object> map);
		
		//整改报告信息
		public Map<String, Object> abarbeitung_report_data_ById(Map<String, Object> map);
	 	
		 //增加问题整改上报
		public int web_add_abarbeitung_report(String json);
		//问题整改详情
		public Map<String, Object> web_abarbeitung_detail(Map<String, Object> map);
		
		
		//重要施工项列表
	 	public PageUtil<Map<String, Object>> web_item_list(Map<String, Object> map,HttpSession session);
	 	
	 	//重要施工项类型列表
	 	public List<Map<String,Object>> item_type_list(Map<String, Object> map);
	 	//重要施工项证件类型列表
	 	public List<Map<String,Object>> item_credential_list(Map<String, Object> map);
		
	 	//重要施工项新增
	 	public int web_add_item(String json);
	 	//重要施工项详情信息
	 	public Map<String,Object> web_item_data(Map<String, Object> map);
	 	//app加载更多重要施工项(历史信息)
	 	public Map<String,Object> web_load_more_item(Map<String, Object> map);
	 	
	 	
	 	
	 	
	 	//工程进度列表
	 	public PageUtil<Map<String, Object>> web_schedule_list(Map<String, Object> map,HttpSession session);
	 	
	 	//工程进度信息
	 	public Map<String,Object> web_schedule_data(Map<String, Object> map);
	 	
	 	//工程进度新增
	 	public int web_add_schedule(String json);
	 	//加载更多工程进度(历史信息)
	 	public Map<String, Object> web_load_more_schedule(Map<String, Object> map);

	//监护上报新增
	public int add_supervision_report(String json);
	 	
	 	
	 	
		
}
