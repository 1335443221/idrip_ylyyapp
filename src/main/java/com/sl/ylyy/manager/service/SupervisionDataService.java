package com.sl.ylyy.manager.service;
import java.util.Map;

import com.sl.ylyy.common.utils.CodeMsg;
import com.sl.ylyy.common.utils.Result;


public interface SupervisionDataService {
	
	//app施工监护列表
		public Object supervision_list(Map<String, Object> map);
	//app施工监护新增
		public CodeMsg add_supervision(Map<String, Object> map);
		public Object supervision_project_list(Map<String, Object> map);
		
	//app施工监护时间类型列表
		public Object supervision_time_list(Map<String, Object> map);	
	//app施工监护上报模板列表
		public Object supervision_model_list(Map<String, Object> map);
	//app施工监护上报模板内容列表
		public Object supervision_model_item_list(Map<String, Object> map);
	//app监护上报新增
		public Result add_supervision_report(String json);
	//app施工监护详情
		public Object supervision_detail(Map<String, Object> map);
	//app加载更多监护记录
		public Object loadmore_supervision(Map<String, Object> map);
	//app加载施工监护上报页面信息
		public Object load_supervision_report(Map<String, Object> map);
		
	//app问题整改列表
		public Object abarbeitung_list(Map<String, Object> map);
	//app问题整改处理新增
		public CodeMsg add_abarbeitung_report(String json);
	//app问题整改新增
		public CodeMsg add_abarbeitung(String json);
	//app问题整改详情
		public Object abarbeitung_detail(Map<String, Object> map);
	//app问题整改通知单信息
		public Object abarbeitung_data(Map<String, Object> map);
	//app加载整改通知单(已保存)
		public Object load_abarbeitung(Map<String, Object> map);
	//app加载整改通知单(已保存)
		public Object load_abarbeitung_report(Map<String, Object> map);
			
		
	//app重要施工项列表
		public Object construction_item_list(Map<String, Object> map);
	//app重要施工项新增
		public Object add_construction_item(String json);
	//app重要施工项类型列表
		public Object item_type_list(Map<String, Object> map);
	//app重要施工项证件类型列表
		public Object item_credential_list(Map<String, Object> map);
	//app重要施工项详情信息
		public Object construction_item_data(Map<String, Object> map);	
	//app加载重要施工项(已保存)
		public Object load_construction_item(Map<String, Object> map);	
	//app加载更多重要施工项(历史信息)
		public Object loadmore_construction_item(Map<String, Object> map);
		
	//app工程进度列表
		public Object schedule_list(Map<String, Object> map);
	//app工程进度信息
		public Object schedule_data(Map<String, Object> map);
	//app工程进度新增
		public Object add_schedule(String json);
	//app加载更多工程进度(历史信息)
		public Object loadmore_schedule(Map<String, Object> map);
	//加载工程进度(已保存)
		public Object load_schedule(Map<String, Object> map);
		
		
}
