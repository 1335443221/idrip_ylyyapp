package com.sl.ylyy.manager.controller;

import java.util.Map;

import com.sl.ylyy.common.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.sl.ylyy.manager.service.SupervisionDataService;
import com.sl.ylyy.common.utils.CodeMsg;


@Controller
@RequestMapping("app/supervision")
public class AppSupervisionController {
	
	@Autowired
	SupervisionDataService supervisionDataService;
	
	
	
	/**
	 * app施工监护列表
	 * @param
	 * @return
	 */
	@RequestMapping("/supervision_list")
	@ResponseBody
	public Object supervision_list(@RequestParam Map<String, Object> map) {
	return supervisionDataService.supervision_list(map);
	}
	
	/**
	 * app新增施工监护
	 * @param
	 * @return
	 */
	@RequestMapping("/add_supervision")
	@ResponseBody
	public CodeMsg add_supervision(@RequestParam Map<String, Object> map) {
	return supervisionDataService.add_supervision(map);
	}
	
	
	/**
	 * app施工监护工程列表
	 * @param
	 * @return
	 */
	@RequestMapping("/supervision_project_list")
	@ResponseBody
	public Object supervision_project_list(@RequestParam Map<String, Object> map) {
	return supervisionDataService.supervision_project_list(map);
	}
	
	/**
	 * app施工监护时间类型列表
	 * @param
	 * @return
	 */
	@RequestMapping("/supervision_time_list")
	@ResponseBody
	public Object supervision_time_list(@RequestParam Map<String, Object> map) {
	return supervisionDataService.supervision_time_list(map);
	}
	
	/**
	 * app施工监护上报模板列表
	 * @param
	 * @return
	 */
	@RequestMapping("/supervision_model_list")
	@ResponseBody
	public Object supervision_model_list(@RequestParam Map<String, Object> map) {
	return supervisionDataService.supervision_model_list(map);
	}
	
	/**
	 * app施工监护上报模板内容列表
	 * @param
	 * @return
	 */
	@RequestMapping("/supervision_model_item_list")
	@ResponseBody
	public Object supervision_model_item_list(@RequestParam Map<String, Object> map) {
	return supervisionDataService.supervision_model_item_list(map);
	}
	
	/**
	 * app加载施工监护上报页面信息
	 * @param
	 * @return
	 */
	@RequestMapping("/load_supervision_report")
	@ResponseBody
	public Object load_supervision_report(@RequestParam Map<String, Object> map) {
	return supervisionDataService.load_supervision_report(map);
	}
	
	
	/**
	 * 监护上报新增
	 * @param
	 * @return
	 */
	@RequestMapping(value="/add_supervision_report",method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Result add_supervision_report(@RequestAttribute String json) {
	return supervisionDataService.add_supervision_report(json);
	}
	
	/**
	 * app施工监护详情(最新上报记录)
	 * @param
	 * @return
	 */
	@RequestMapping("/supervision_detail")
	@ResponseBody
	public Object supervision_detail(@RequestParam Map<String, Object> map) {
	return supervisionDataService.supervision_detail(map);
	}
	
	
	/**
	 * app加载更多监护记录
	 * @param
	 * @return
	 */
	@RequestMapping("/loadmore_supervision")
	@ResponseBody
	public Object loadmore_supervision(@RequestParam Map<String, Object> map) {
	return supervisionDataService.loadmore_supervision(map);
	}
	
	/**
	 * app问题整改列表
	 * @param
	 * @return
	 */
	@RequestMapping("/abarbeitung_list")
	@ResponseBody
	public Object abarbeitung_list(@RequestParam Map<String, Object> map) {
	return supervisionDataService.abarbeitung_list(map);
	}
	
	/**
	 * app问题整改新增
	 * @param
	 * @return
	 */
	@RequestMapping(value="/add_abarbeitung",method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public CodeMsg add_abarbeitung(@RequestAttribute String json) {
	return supervisionDataService.add_abarbeitung(json);
	}
	
	/**
	 * app问题整改报告新增
	 * @param
	 * @return
	 */
	@RequestMapping(value="/add_abarbeitung_report",method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public CodeMsg add_abarbeitung_report(@RequestAttribute String json) {
	return supervisionDataService.add_abarbeitung_report(json);
	}
	
	/**
	 * app问题整改通知单信息
	 * @param
	 * @return
	 */
	@RequestMapping("/abarbeitung_data")
	@ResponseBody
	public Object abarbeitung_data(@RequestParam Map<String, Object> map) {
	return supervisionDataService.abarbeitung_data(map);
	}
	
	/**
	 * app加载整改通知单(已保存)
	 * @param
	 * @return
	 */
	@RequestMapping("/load_abarbeitung")
	@ResponseBody
	public Object load_abarbeitung(@RequestParam Map<String, Object> map) {
	return supervisionDataService.load_abarbeitung(map);
	}
	
	/**
	 * app加载整改报告(已保存)
	 * @param
	 * @return
	 */
	@RequestMapping("/load_abarbeitung_report")
	@ResponseBody
	public Object load_abarbeitung_report(@RequestParam Map<String, Object> map) {
	return supervisionDataService.load_abarbeitung_report(map);
	}
	/**
	 * app整改详情
	 * @param
	 * @return
	 */
	@RequestMapping("/abarbeitung_detail")
	@ResponseBody
	public Object abarbeitung_detail(@RequestParam Map<String, Object> map) {
	return supervisionDataService.abarbeitung_detail(map);
	}
	
	
	/**
	 * app 重要施工项列表
	 * @param
	 * @return
	 */
	@RequestMapping("/construction_item_list")
	@ResponseBody
	public Object construction_item_list(@RequestParam Map<String, Object> map) {
	return supervisionDataService.construction_item_list(map);
	}
	/**
	 * app重要施工项新增
	 * @param
	 * @return
	 */
	@RequestMapping(value="/add_construction_item",method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Object add_construction_item(@RequestAttribute String json) {
	return supervisionDataService.add_construction_item(json);
	}
	/**
	 * app重要施工项类型列表
	 * @param
	 * @return
	 */
	@RequestMapping("/item_type_list")
	@ResponseBody
	public Object item_type_list(@RequestParam Map<String, Object> map) {
	return supervisionDataService.item_type_list(map);
	}
	/**
	 * app重要施工项证件类型列表
	 * @param
	 * @return
	 */
	@RequestMapping("/item_credential_list")
	@ResponseBody
	public Object item_credential_list(@RequestParam Map<String, Object> map) {
	return supervisionDataService.item_credential_list(map);
	}
	/**
	 * app重要施工项详情信息
	 * @param
	 * @return
	 */
	@RequestMapping("/construction_item_data")
	@ResponseBody
	public Object construction_item_data(@RequestParam Map<String, Object> map) {
	return supervisionDataService.construction_item_data(map);
	}
	/**
	 * app加载更多重要施工项(历史信息)
	 * @param
	 * @return
	 */
	@RequestMapping("/loadmore_construction_item")
	@ResponseBody
	public Object loadmore_construction_item(@RequestParam Map<String, Object> map) {
	return supervisionDataService.loadmore_construction_item(map);
	}
	/**
	 * app加载重要施工项(已保存)
	 * @param
	 * @return
	 */
	@RequestMapping("/load_construction_item")
	@ResponseBody
	public Object load_construction_item(@RequestParam Map<String, Object> map) {
	return supervisionDataService.load_construction_item(map);
	}
	
	
	/**
	 * app 工程进度列表
	 * @param
	 * @return
	 */
	@RequestMapping("/schedule_list")
	@ResponseBody
	public Object schedule_list(@RequestParam Map<String, Object> map) {
	return supervisionDataService.schedule_list(map);
	}
	/**
	 * app工程进度新增
	 * @param
	 * @return
	 */
	@RequestMapping(value="/add_schedule",method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Object add_schedule(@RequestAttribute String json) {
	return supervisionDataService.add_schedule(json);
	}
	/**
	 * app 工程进度信息
	 * @param
	 * @return
	 */
	@RequestMapping("/schedule_data")
	@ResponseBody
	public Object schedule_data(@RequestParam Map<String, Object> map) {
	return supervisionDataService.schedule_data(map);
	}
	/**
	 * app 加载工程进度
	 * @param
	 * @return
	 */
	@RequestMapping("/load_schedule")
	@ResponseBody
	public Object load_schedule(@RequestParam Map<String, Object> map) {
	return supervisionDataService.load_schedule(map);
	}
	/**
	 * app 加载更多工程进度（历史信息）
	 * @param
	 * @return
	 */
	@RequestMapping("/loadmore_schedule")
	@ResponseBody
	public Object loadmore_schedule(@RequestParam Map<String, Object> map) {
	return supervisionDataService.loadmore_schedule(map);
	}
	
}
