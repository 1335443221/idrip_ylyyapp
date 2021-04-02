package com.sl.ylyy.manager.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import com.sl.ylyy.common.config.UrlConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sl.ylyy.common.utils.QiniuUpload;
import com.sl.ylyy.manager.service.WebSupervisionDataService;

@Controller
@RequestMapping("supervision")
public class WebSupervisionController {
	@Autowired
	WebSupervisionDataService supervisionDataService;
	
	/**
	 * 施工监护列表
	 * 
	 * @param
	 * @return
	 */
	@RequestMapping("/web_supervision_list")
	public String web_supervision_list(@RequestParam Map<String, Object> map,Model model) {
		 model.addAttribute("page",supervisionDataService.web_supervision_list(map));
		return "ylyyPag/supervisionPag/supervision_showList";
	}

	/**
	 * 去增加监护页面
	 * 
	 * @return
	 */
	@RequestMapping("/goAddSupervision")
	public String goAddSupervision(@RequestParam Map<String, Object> map,Model model) {
		 model.addAttribute("time_list",supervisionDataService.supervision_time_list(map));  //时间类型
		 model.addAttribute("model_list",supervisionDataService.supervision_model_list(map));  //模板列表
		 map.put("list_type", 1);
		 model.addAttribute("project",supervisionDataService.supervision_project_list(map));  //工程列表
		 
		return "ylyyPag/supervisionPag/supervision_add";
	}
	
	
	
	/**
	 * 模式下项
	 * 
	 * @return
	 */
	@RequestMapping("/itemByModel")
	@ResponseBody
	public List<Map<String,Object>> itemByModel(@RequestParam Map<String, Object> map) {
		return supervisionDataService.supervision_model_item_list(map);
	}
	
	
	/**
	 * 增加监护
	 * 
	 * @return
	 */
	@RequestMapping("/AddSupervision")
	@ResponseBody
	public int AddSupervision(@RequestParam Map<String, Object> map) {
		return supervisionDataService.add_supervision(map);
	}

	
	
	/**
	 * 去查看监护
	 * 
	 * @return
	 */
	@RequestMapping("/web_supervision_data_ById")
	public String web_supervision_data_ById(@RequestParam Map<String, Object> map,Model model) {
		 model.addAttribute("data",supervisionDataService.web_supervision_data_ById(map));  //时间类型
		return "ylyyPag/supervisionPag/supervision_data";
	}
	
	/**
	 * 施工监护历史
	 * @param
	 * @return
	 */
	@RequestMapping("/web_supervision_history")
	public Object web_supervision_history(@RequestParam Map<String, Object> map,Model model) {
		 model.addAttribute("page",supervisionDataService.web_supervision_history(map));
		return "ylyyPag/supervisionPag/supervision_showHistory";
	}
	
	
	/**
	 * 施工监护上报信息
	 * @param
	 * @return
	 */
	@RequestMapping("/web_report_data_ById")
	public Object web_report_data_ById(@RequestParam Map<String, Object> map,Model model) {
		 model.addAttribute("data",supervisionDataService.web_report_data_ById(map));
		return "ylyyPag/supervisionPag/supervision_showReport";
	}

	/**
	 * 去增加施工监护
	 * @param
	 * @return
	 */
	@RequestMapping("/goSupplementReport")
	public Object goSupplementReport(@RequestParam Map<String, Object> map,Model model) {
		model.addAttribute("token",QiniuUpload.getUpToken2());
		model.addAttribute("prefix", UrlConfig.FILE_PREFIX);
		model.addAttribute("QiniuUrl", UrlConfig.QiniuUrl);
		model.addAttribute("data",supervisionDataService.goSupplementReport(map));
		return "ylyyPag/supervisionPag/supervision_supplementReport";
	}

	/**
	 * 增加施工监护
	 * @param
	 * @return
	 */
	@RequestMapping(value="/add_supervision_report",method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public int add_supervision_report(@RequestBody String json) {
		return supervisionDataService.add_supervision_report(json);
	}

	
	
	/**
	 * 施工监护上报加载更多
	 * @param
	 * @return
	 */
	@RequestMapping("/web_load_more_report")
	@ResponseBody
	public Map<String, Object> web_load_more_report(@RequestParam Map<String, Object> map) {
		return supervisionDataService.web_load_more_report(map);
	}


	/**
	 * 施工监护检查是否已够三次
	 * @param
	 * @return
	 */
	@RequestMapping("/web_check_report")
	@ResponseBody
	public int web_check_report(@RequestParam Map<String, Object> map) {
		return supervisionDataService.web_check_report(map);
	}

	
	/**
	 * 问题整改列表
	 * 
	 * @param
	 * @return
	 */
	@RequestMapping("/web_abarbeitung_list")
	public Object web_abarbeitung_list(@RequestParam Map<String, Object> map,Model model,HttpSession session){
		 model.addAttribute("page",supervisionDataService.web_abarbeitung_list(map,session));
		return "ylyyPag/supervisionPag/abarbeitung_showList";
	}
	
	
	/**
	 * 去增加问题整改页面
	 * 
	 * @return
	 */
	@RequestMapping("/goAddAbarbeitung")
	public String goAddAbarbeitung(@RequestParam Map<String, Object> map,Model model) {
		 map.put("list_type",2);
		 model.addAttribute("project",supervisionDataService.supervision_project_list(map));  //工程列表
		model.addAttribute("token",QiniuUpload.getUpToken2());
		model.addAttribute("prefix", UrlConfig.FILE_PREFIX);
		model.addAttribute("QiniuUrl", UrlConfig.QiniuUrl);
		return "ylyyPag/supervisionPag/abarbeitung_add";
	}
	
	/**
	 * 添加问题整改单
	 * @param
	 * @return
	 */
	@RequestMapping(value="/web_add_abarbeitung",method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Object web_add_abarbeitung(@RequestBody String json) {
		
	return supervisionDataService.web_add_abarbeitung(json);
	}
	
	/**
	 * 去修改问题整改页面
	 * 
	 * @return
	 */
	@RequestMapping("/goUpdateAbarbeitung")
	public String goUpdateAbarbeitung(@RequestParam Map<String, Object> map,Model model) {
		 map.put("list_type",2);
		 model.addAttribute("project",supervisionDataService.supervision_project_list(map));  //工程列表
		 model.addAttribute("data",supervisionDataService.abarbeitung_data_ById(map));  //整改信息
		model.addAttribute("token",QiniuUpload.getUpToken2());
		model.addAttribute("prefix", UrlConfig.FILE_PREFIX);
		model.addAttribute("QiniuUrl", UrlConfig.QiniuUrl);
		return "ylyyPag/supervisionPag/abarbeitung_update";
	}
	
	
	/**
	 * 去增加问题上报页面
	 * @return
	 */
	@RequestMapping("/goAddAbarbeitungReport")
	public String goAddAbarbeitungReport(@RequestParam Map<String, Object> map,Model model) {
		 model.addAttribute("data",supervisionDataService.abarbeitung_report_data_ById(map));  //整改报告信息
		model.addAttribute("token",QiniuUpload.getUpToken2());
		model.addAttribute("prefix", UrlConfig.FILE_PREFIX);
		model.addAttribute("QiniuUrl", UrlConfig.QiniuUrl);
		return "ylyyPag/supervisionPag/abarbeitung_report_add";
	}
	
	
	/**
	 * 去修改问题上报页面
	 * @return
	 */
	@RequestMapping("/goUpdateAbarbeitungReport")
	public String goUpdateAbarbeitungReport(@RequestParam Map<String, Object> map,Model model) {
		 model.addAttribute("data",supervisionDataService.abarbeitung_report_data_ById(map));  //整改报告信息
		model.addAttribute("token",QiniuUpload.getUpToken2());
		model.addAttribute("prefix", UrlConfig.FILE_PREFIX);
		model.addAttribute("QiniuUrl", UrlConfig.QiniuUrl);
		return "ylyyPag/supervisionPag/abarbeitung_report_update";
	}
	
	/**
	 * 增加问题上报
	 * @param
	 * @return
	 */
	@RequestMapping(value="/web_add_abarbeitung_report",method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public int web_add_abarbeitung_report(@RequestBody String json) {
		
	return supervisionDataService.web_add_abarbeitung_report(json);
	}
	
	
	
	/**
	 * 问题整改详情
	 * @param
	 * @return
	 */
	@RequestMapping("/web_abarbeitung_detail")
	public String web_abarbeitung_detail(@RequestParam Map<String, Object> map,Model model) {
		 model.addAttribute("data",supervisionDataService.web_abarbeitung_detail(map));
		return "ylyyPag/supervisionPag/abarbeitung_data";
	}
	
	
	
	
	/**
	 * 重要施工项列表
	 * 
	 * @param
	 * @return
	 */
	@RequestMapping("/web_item_list")
	public Object web_item_list(@RequestParam Map<String, Object> map,Model model,HttpSession session){
		 model.addAttribute("page",supervisionDataService.web_item_list(map,session));
		return "ylyyPag/supervisionPag/item_showList";
	}
	
	
	/**
	 * 去增加重要施工项页面
	 * 
	 * @return
	 */
	@RequestMapping("/goAddItem")
	public String goAddItem(@RequestParam Map<String, Object> map,Model model) {
		 map.put("list_type",2);
		 model.addAttribute("project",supervisionDataService.supervision_project_list(map));  //工程列表
		 model.addAttribute("type",supervisionDataService.item_type_list(map));  //施工项类型列表
		 model.addAttribute("credential",supervisionDataService.item_credential_list(map));  //施工项证书列表
		model.addAttribute("token",QiniuUpload.getUpToken2());
		model.addAttribute("prefix", UrlConfig.FILE_PREFIX);
		model.addAttribute("QiniuUrl", UrlConfig.QiniuUrl);
		return "ylyyPag/supervisionPag/item_add";
	}
	
	
	/**
	 * 增加重要施工项
	 * @param
	 * @return
	 */
	@RequestMapping(value="/web_add_item",method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public int web_add_item(@RequestBody String json) {
		
	return supervisionDataService.web_add_item(json);
	}
	
	
	/**
	 * 去修改重要施工项页面
	 * 
	 * @return
	 */
	@RequestMapping("/goUpdateItem")
	public String goUpdateItem(@RequestParam Map<String, Object> map,Model model) {
		 map.put("list_type",2);
		 model.addAttribute("data",supervisionDataService.web_item_data(map));  //重要施工项信息
		 model.addAttribute("project",supervisionDataService.supervision_project_list(map));  //工程列表
		 model.addAttribute("type",supervisionDataService.item_type_list(map));  //施工项类型列表
		 model.addAttribute("credential",supervisionDataService.item_credential_list(map));  //施工项证书列表
		model.addAttribute("token",QiniuUpload.getUpToken2());
		model.addAttribute("prefix", UrlConfig.FILE_PREFIX);
		model.addAttribute("QiniuUrl", UrlConfig.QiniuUrl);
		return "ylyyPag/supervisionPag/item_update";
	}
	
	
	/**
	 * 去重要施工项详情页面
	 * 
	 * @return
	 */
	@RequestMapping("/web_item_data")
	public String web_item_data(@RequestParam Map<String, Object> map,Model model) {
		 model.addAttribute("data",supervisionDataService.web_item_data(map));  //施工项信息
		return "ylyyPag/supervisionPag/item_data";
	}
	
	
	/**
	 * 重要施工项加载更多
	 * @param
	 * @return
	 */
	@RequestMapping("/web_load_more_item")
	@ResponseBody
	public Map<String, Object> web_load_more_item(@RequestParam Map<String, Object> map) {
		return supervisionDataService.web_load_more_item(map);
	}
	
	
	
	
	/**
	 * 工程进度列表
	 * 
	 * @param
	 * @return
	 */
	@RequestMapping("/web_schedule_list")
	public Object web_schedule_list(@RequestParam Map<String, Object> map,Model model,HttpSession session){
		 model.addAttribute("page",supervisionDataService.web_schedule_list(map,session));
		return "ylyyPag/supervisionPag/schedule_showList";
	}
	
	
	/**
	 * 去增加工程进度页面
	 * 
	 * @return
	 */
	@RequestMapping("/goAddSchedule")
	public String goAddSchedule(@RequestParam Map<String, Object> map,Model model) {
		 map.put("list_type",2);
		 model.addAttribute("project",supervisionDataService.supervision_project_list(map));  //工程列表
		model.addAttribute("token",QiniuUpload.getUpToken2());
		model.addAttribute("prefix", UrlConfig.FILE_PREFIX);
		model.addAttribute("QiniuUrl", UrlConfig.QiniuUrl);
		return "ylyyPag/supervisionPag/schedule_add";
	}
	
	
	
	/**
	 * 去修改工程进度页面
	 * 
	 * @return
	 */
	@RequestMapping("/goUpdateSchedule")
	public String goUpdateSchedule(@RequestParam Map<String, Object> map,Model model) {
		 map.put("list_type",2);
		 model.addAttribute("data",supervisionDataService.web_schedule_data(map));  //工程进度信息
		 model.addAttribute("project",supervisionDataService.supervision_project_list(map));  //工程列表
		model.addAttribute("token",QiniuUpload.getUpToken2());
		model.addAttribute("prefix", UrlConfig.FILE_PREFIX);
		model.addAttribute("QiniuUrl", UrlConfig.QiniuUrl);
		return "ylyyPag/supervisionPag/schedule_update";
	}
	
	
	/**
	 * 增加工程进度
	 * @param
	 * @return
	 */
	@RequestMapping(value="/web_add_schedule",method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public int web_add_schedule(@RequestBody String json) {
		
	return supervisionDataService.web_add_schedule(json);
	}
	
	
	/**
	 * 去工程进度详情页面
	 * 
	 * @return
	 */
	@RequestMapping("/web_schedule_data")
	public String web_schedule_data(@RequestParam Map<String, Object> map,Model model) {
		 model.addAttribute("data",supervisionDataService.web_schedule_data(map));  //施工项信息
		return "ylyyPag/supervisionPag/schedule_data";
	}
	
	
	/**
	 * 工程进度加载更多
	 * @param
	 * @return
	 */
	@RequestMapping("/web_load_more_schedule")
	@ResponseBody
	public Map<String, Object> web_load_more_schedule(@RequestParam Map<String, Object> map) {
		return supervisionDataService.web_load_more_schedule(map);
	}
	
	
	
}
