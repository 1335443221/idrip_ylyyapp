package com.sl.ylyy.manager.service;

import java.util.Map;

import com.sl.ylyy.common.utils.CodeMsg;

public interface ManageDataService {
	
	//app工程列表
	public Object project_list(Map<String, Object> map);

	//app工程类型列表
	public Object project_type_list(Map<String, Object> map);
	
	//app工程新增
	public Object add_project(String json);
	
	//app工程前期信息
	public Object project_early_data(Map<String, Object> map);
	//app工程前期新增
	public CodeMsg add_project_early(String json);

	//通过id删除图片
	public Object deleteFileById(Map<String, Object> map);
	
	//app工程开工新增
	public CodeMsg add_project_start(String json);
	//app工程开工信息
	public Object project_start_data(Map<String, Object> map);
	
	//app启动施工监护
	public CodeMsg start_supervision(int project_id);
	
	//app启动预验收
	public CodeMsg start_before_check(int project_id);

	//app新增预验收
	public CodeMsg add_project_before_check(String json);
	//app预验收信息
	public Object project_before_check_data(Map<String, Object> map);

	//app验收信息
	public Object project_check_data(Map<String, Object> map);
	//app验收新增
	public CodeMsg add_project_check(String json);

	//工程结算类型列表
	public Object project_settlement_type_list(Map<String, Object> map);

	//工程结算信息
	public Object project_settlement_detail(Map<String, Object> map);
	//工程结算新增
	public CodeMsg add_project_settlement(String json);
	//加载更多工程结算
	public Object loadmore_project_settlement(Map<String, Object> map);
	//返回合同总额
	public Object project_contract_amount(Map<String, Object> map);
 
	//施工中 没开启监护
	public Object construction_data(Map<String, Object> map);
	
	//工程详情
	public Object project_detail(Map<String, Object> map);
	
	
}
