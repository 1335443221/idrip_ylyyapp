package com.sl.ylyy.manager.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.sl.ylyy.manager.service.ManageDataService;
import com.sl.ylyy.common.utils.CodeMsg;

@Controller
@RequestMapping("app/manage")
public class AppManageController {
	@Autowired
	ManageDataService manageDataService;
	
	/**
	 * 通过id删除图片
	 * @param
	 * @return
	 */
	@RequestMapping("/deleteFileById")
	@ResponseBody
	public Object deleteFileById(@RequestParam Map<String, Object> map) {
	return manageDataService.deleteFileById(map);
	}
	
	
	/**
	 * 工程列表
	 * @param
	 * @return
	 */
	@RequestMapping("/project_list")
	@ResponseBody
	public Object project_list(@RequestParam Map<String, Object> map) {
	return manageDataService.project_list(map);
	}
	
	/**
	 * 工程类型列表
	 * @param
	 * @return
	 */
	@RequestMapping("/project_type_list")
	@ResponseBody
	public Object project_type_list(@RequestParam Map<String, Object> map) {
	return manageDataService.project_type_list(map);
	}
	
	
	/**
	 * 添加工程
	 * @param
	 * @return
	 */
	@RequestMapping(value="/add_project",method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Object add_project(@RequestAttribute String json) {
	return manageDataService.add_project(json);
	}
	
	/**
	 * 工程前期准备阶段信息
	 * @param
	 * @return
	 */
	@RequestMapping("/project_early_data")
	@ResponseBody
	public Object project_early_data(@RequestParam Map<String, Object> map) {
	return manageDataService.project_early_data(map);
	}
	
	/**
	 * 添加工程前期准备阶段
	 * @param
	 * @return
	 */
	@RequestMapping(value="/add_project_early",method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public CodeMsg add_project_early(@RequestAttribute String json) {
	return manageDataService.add_project_early(json);
	}
	
	
	/**
	 * 工程开工阶段信息
	 * @param
	 * @return
	 */
	@RequestMapping("/project_start_data")
	@ResponseBody
	public Object project_start_data(@RequestParam Map<String, Object> map) {
	return manageDataService.project_start_data(map);
	}
	
	/**
	 * 添加工程开工阶段
	 * @param
	 * @return
	 */
	@RequestMapping(value="/add_project_start",method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public CodeMsg add_project_start(@RequestAttribute String json) {
	return manageDataService.add_project_start(json);
	}
	
	
	/**
	 * 启动施工监护
	 * @param
	 * @return
	 */
	@RequestMapping("/start_supervision")
	@ResponseBody
	public CodeMsg start_supervision(int project_id) {
	return manageDataService.start_supervision(project_id);
	}
	
	
	/**
	 * 启动预验收
	 * @param
	 * @return
	 */
	@RequestMapping("/start_before_check")
	@ResponseBody
	public CodeMsg start_before_check(int project_id) {
	return manageDataService.start_before_check(project_id);
	}
	
	
	
	/**
	 * 工程预验收阶段信息
	 * @param
	 * @return
	 */
	@RequestMapping("/project_before_check_data")
	@ResponseBody
	public Object project_before_check_data(@RequestParam Map<String, Object> map) {
	return manageDataService.project_before_check_data(map);
	}
	
	/**
	 * 添加工程预验收阶段
	 * @param
	 * @return
	 */
	@RequestMapping(value="/add_project_before_check",method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public CodeMsg add_project_before_check(@RequestAttribute String json) {
	return manageDataService.add_project_before_check(json);
	}
	
	
	/**
	 * 工程验收阶段信息
	 * @param
	 * @return
	 */
	@RequestMapping("/project_check_data")
	@ResponseBody
	public Object project_check_data(@RequestParam Map<String, Object> map) {
	return manageDataService.project_check_data(map);
	}
	
	/**
	 * 添加工程验收阶段
	 * @param
	 * @return
	 */
	@RequestMapping(value="/add_project_check",method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public CodeMsg add_project_check(@RequestAttribute String json) {
	return manageDataService.add_project_check(json);
	}
	
	
	
	
	/**
	 * 工程结算类型列表
	 * @param
	 * @return
	 */
	@RequestMapping("/project_settlement_type_list")
	@ResponseBody
	public Object project_settlement_type_list(@RequestParam Map<String, Object> map) {
	return manageDataService.project_settlement_type_list(map);
	}
	
	/**
	 * 工程结算详情
	 * @param
	 * @return
	 */
	@RequestMapping("/project_settlement_detail")
	@ResponseBody
	public Object project_settlement_detail(@RequestParam Map<String, Object> map) {
	return manageDataService.project_settlement_detail(map);
	}
	
	/**
	 * 加载更多工程结算
	 * @param
	 * @return
	 */
	@RequestMapping("/loadmore_project_settlement")
	@ResponseBody
	public Object loadmore_project_settlement(@RequestParam Map<String, Object> map) {
	return manageDataService.loadmore_project_settlement(map);
	}
	
	/**
	 * 添加工程结算
	 * @param
	 * @return
	 */
	@RequestMapping(value="/add_project_settlement",method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public CodeMsg add_project_settlement(@RequestAttribute String json) {
	return manageDataService.add_project_settlement(json);
	}
	
	
	/**
	 * 选择 工程进度款 返回合同总额
	 * @param
	 * @return
	 */
	@RequestMapping("/project_contract_amount")
	@ResponseBody
	public Object project_contract_amount(@RequestParam Map<String, Object> map) {
	return manageDataService.project_contract_amount(map);
	}
	
	/**
	 * 施工中信息
	 * @param
	 * @return
	 */
	@RequestMapping("/construction_data")
	@ResponseBody
	public Object construction_data(@RequestParam Map<String, Object> map) {
	return manageDataService.construction_data(map);
	}
	
	/**
	 * 工程详情
	 * @param
	 * @return
	 */
	@RequestMapping("/project_detail")
	@ResponseBody
	public Object project_detail(@RequestParam Map<String, Object> map) {
	return manageDataService.project_detail(map);
	}
}
