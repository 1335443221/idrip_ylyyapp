package com.sl.ylyy.manager.controller;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.sl.ylyy.common.config.UrlConfig;
import com.sl.ylyy.manager.service.WebSupervisionDataService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sl.ylyy.manager.service.WebManageDataService;
import com.sl.ylyy.common.utils.QiniuUpload;
@Controller
@RequestMapping("manage")
public class WebManageController {
	@Autowired
	WebManageDataService manageDataService;
	@Autowired
	WebSupervisionDataService supervisionDataService;

	
	
	/**
	 * 工程前期管理页面
	 * @return
	 */
	@RequestMapping("/web_project_list")
	public String project_list(@RequestParam Map<String, Object> map,Model model,HttpSession session) {
		
		model.addAttribute("page",manageDataService.web_project_list(map,session)); 
		return "ylyyPag/projectPag/earlyShowProject";
	}
	
	
	/**
	 * 去添加工程页面
	 * @return
	 */
	@RequestMapping("/goAddProject")
	public String goAddProject(@RequestParam Map<String, Object> map,Model model) {
		model.addAttribute("token",QiniuUpload.getUpToken2());
		model.addAttribute("prefix", UrlConfig.FILE_PREFIX);
		model.addAttribute("QiniuUrl", UrlConfig.QiniuUrl);
		model.addAttribute("type_list",manageDataService.web_project_type_list(map));
		return "ylyyPag/projectPag/addProject";
	}
	
	
	/**
	 * 添加工程
	 * @param
	 * @return
	 */
	@RequestMapping(value="/add_project",method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Object add_project(@RequestBody String json) {
		
	return manageDataService.web_add_project(json);
	}
	
	
	/**
	 * 去添加工程前期页面
	 * @return
	 */
	@RequestMapping("/goAddEarlyProject")
	public String goAddEarlyProject(@RequestParam Map<String, Object> map,Model model) {
		model.addAttribute("token",QiniuUpload.getUpToken2());
		model.addAttribute("prefix", UrlConfig.FILE_PREFIX);
		model.addAttribute("QiniuUrl", UrlConfig.QiniuUrl);
		model.addAttribute("project_id",map.get("project_id"));
		return "ylyyPag/projectPag/addEarlyProject";
	}
	
	/**
	 * 添加工程前期
	 * @param
	 * @return
	 */
	@RequestMapping(value="/add_early_project",method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Object add_early_project(@RequestBody String json) {
		
	return manageDataService.web_add_early_project(json);
	}
	
	
	
	/**
	 * 去修改_前期页面
	 * @return
	 */
	@RequestMapping("/web_update_project")
	public String web_update_project(@RequestParam Map<String,Object> map,Model model) {
		model.addAttribute("token",QiniuUpload.getUpToken2());
		model.addAttribute("prefix", UrlConfig.FILE_PREFIX);
		model.addAttribute("QiniuUrl", UrlConfig.QiniuUrl);
		model.addAttribute("type_list",manageDataService.web_project_type_list(map));
		model.addAttribute("project",manageDataService.web_project_data_ById(map));

		return "ylyyPag/projectPag/earlyupdateProject";
	}


	/**
	 * 去补录_前期页面
	 * @return
	 */
	@RequestMapping("/goSupplementEarlyProject")
	public String web_supplement_project(@RequestParam Map<String,Object> map,Model model) {
		model.addAttribute("token",QiniuUpload.getUpToken2());
		model.addAttribute("prefix", UrlConfig.FILE_PREFIX);
		model.addAttribute("QiniuUrl", UrlConfig.QiniuUrl);
		model.addAttribute("type_list",manageDataService.web_project_type_list(map));
		model.addAttribute("project",manageDataService.web_project_data_ById(map));
		return "ylyyPag/projectPag/earlySupplementProject";
	}


	/**
	 * 修改_前期工程
	 * @param
	 * @return
	 */
	@RequestMapping(value="/update_project",method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Object update_project(@RequestBody String json,HttpSession session) {
	return manageDataService.web_update_project(json);
	}
	
	
	
	/**
	 * 工程_前期查看
	 * @return
	 */
	@RequestMapping("/web_project_data_ById")
	public String web_project_data_ById(@RequestParam Map<String,Object> map,Model model) {
		model.addAttribute("project",manageDataService.web_project_data_ById(map));
		
		return "ylyyPag/projectPag/earlyProjectData";
	}
	
/////////////////////////////////////////工程过程
	/**
	 * 工程过程页面
	 * @return
	 */
	@RequestMapping("/web_project_course_list")
	public String web_project_course_list(@RequestParam Map<String, Object> map,Model model,HttpSession session) {
		
		model.addAttribute("page",manageDataService.web_project_list(map,session)); 
		return "ylyyPag/projectPag/courseShowProject";
	}
	
	
	/**
	 * 工程过程修改
	 * @return
	 */
	@RequestMapping("/web_go_update_course")
	public String web_go_update_course(@RequestParam Map<String, Object> map,Model model) {
		int project_state=Integer.parseInt(String.valueOf(map.get("project_state"))); //工程阶段
		Map<String, Object> project=manageDataService.web_project_data_ById(map);
		if(project_state==2){  //开工阶段
			map.put("url", "/manage/web_go_update_start?project_state=2&project_id="+map.get("project_id"));
		}else if(project_state==5){  //预验收
			map.put("url", "/manage/web_go_update_before?project_state=6&project_id="+map.get("project_id"));
		}else if(project_state==6){  //验收
			map.put("url", "/manage/web_go_update_check?project_state=6&project_id="+map.get("project_id"));
		}
		model.addAttribute("course",map); 
		model.addAttribute("project",project);
		return "ylyyPag/projectPag/courseIndex";
	}


	/**
	 * 工程过程修改
	 * @return
	 */
	@RequestMapping("/web_go_supplement_course")
	public String web_go_supplement_course(@RequestParam Map<String, Object> map,Model model) {
		//工程阶段
		int project_state=Integer.parseInt(String.valueOf(map.get("project_state")));
		Map<String, Object> project=manageDataService.web_project_data_ById(map);
		if(project_state==2){  //开工阶段
			map.put("url", "/manage/web_go_update_start?project_state=2&project_id="+map.get("project_id"));
		}else if(project_state==3){  //未开启监护
			map.put("url", "/manage/web_go_supplement_start?project_state=6&project_id="+map.get("project_id"));
		}else if(project_state==4){  //开启监护
			map.put("url", "/manage/web_go_supplement_start?project_state=6&project_id="+map.get("project_id"));
		}else if(project_state==5){  //预验收
			map.put("url", "/manage/web_go_supplement_start?project_state=6&project_id="+map.get("project_id"));
		}else if(project_state==6){  //验收
			map.put("url", "/manage/web_go_supplement_before?project_state=6&project_id="+map.get("project_id"));
		}else if(project_state==7){  //归档
			map.put("url", "/manage/web_go_supplement_check?project_state=6&project_id="+map.get("project_id"));
		}
		model.addAttribute("course",map);
		model.addAttribute("project",project);
		return "ylyyPag/projectPag/courseSupplementIndex";
	}



	/**
	 * 工程过程查看
	 * @return
	 */
	@RequestMapping("/web_course_data_ById")
	public String web_course_data_ById(@RequestParam Map<String, Object> map,Model model) {
		int project_state=Integer.parseInt(String.valueOf(map.get("project_state"))); //工程阶段
		Map<String, Object> project=manageDataService.web_project_data_ById(map);
		if(project_state==1){  //开工阶段
			map.put("url", "/manage/web_early_data_ById?project_id="+map.get("project_id"));
		}else if(project_state==2){  //预验收
			map.put("url", "/manage/web_start_data_ById?project_id="+map.get("project_id"));
		}else if(project_state==3||project_state==4){  //施工中
			map.put("url", "/manage/web_working_data_ById?project_id="+map.get("project_id"));
		}else if(project_state==5){  //预验收
			map.put("url", "/manage/web_before_data_ById?project_id="+map.get("project_id"));
		}else if(project_state==6){  //验收
			map.put("url", "/manage/web_check_data_ById?project_id="+map.get("project_id"));
		}else if(project_state==7){  //验收
			map.put("url", "/manage/web_check_data_ById?project_id="+map.get("project_id"));
		}
		
		if(map.get("log")!=null){
			map.put("url", "/manage/web_project_data?project_id="+map.get("project_id"));
		}
		model.addAttribute("course",map); 
		model.addAttribute("project",project);
		return "ylyyPag/projectPag/courseDataIndex";
	}
	
	
	/**
	 * 工程_查看
	 * @return
	 */
	@RequestMapping("/web_project_data")
	public String web_project_data(@RequestParam Map<String,Object> map,Model model) {
		model.addAttribute("project",manageDataService.web_project_data_ById(map));
		
		return "ylyyPag/projectPag/Data_Project";
	}
	
	/**
	 * 工程_前期查看
	 * @return
	 */
	@RequestMapping("/web_early_data_ById")
	public String web_early_data(@RequestParam Map<String,Object> map,Model model) {
		model.addAttribute("project",manageDataService.web_project_data_ById(map));
		
		return "ylyyPag/projectPag/Data_Early";
	}
	
	/**
	 * 去工程开工修改
	 * @return
	 */
	@RequestMapping("/web_go_update_start")
	public String web_go_update_start(@RequestParam Map<String, Object> map,Model model) {
		model.addAttribute("token",QiniuUpload.getUpToken2());
		model.addAttribute("prefix", UrlConfig.FILE_PREFIX);
		model.addAttribute("QiniuUrl", UrlConfig.QiniuUrl);
		Map<String, Object> _data = manageDataService.web_start_data_ById(map);
		model.addAttribute("project",_data);
		model.addAttribute("param",map);
		
		if(_data!=null&&String.valueOf(_data.get("state")).equals("3")){  //已提交
			return "ylyyPag/projectPag/Data_Start";
		}else{
			return "ylyyPag/projectPag/update_Start";
		}
		
	}

	/**
	 * 去工程开工补录
	 * @return
	 */
	@RequestMapping("/web_go_supplement_start")
	public String web_go_supplement_start(@RequestParam Map<String, Object> map,Model model) {
		model.addAttribute("token",QiniuUpload.getUpToken2());
		model.addAttribute("prefix", UrlConfig.FILE_PREFIX);
		model.addAttribute("QiniuUrl", UrlConfig.QiniuUrl);
		Map<String, Object> _data = manageDataService.web_start_data_ById(map);
		model.addAttribute("project",_data);
		model.addAttribute("param",map);

		if(_data!=null&&String.valueOf(_data.get("state")).equals("3")){  //已提交
			return "ylyyPag/projectPag/supplement_Start";
		}else{
			return "ylyyPag/projectPag/update_Start";
		}

	}

	/**
	 * 修改_开工
	 * @param
	 * @return
	 */
	@RequestMapping(value="/web_update_start",method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Object web_update_start(@RequestBody String json,HttpSession session) {
	return manageDataService.web_update_start(json);
	}
	
	/**
	 * 工程_施工中查看
	 * @return
	 */
	@RequestMapping("/web_start_data_ById")
	public String web_start_data_ById(@RequestParam Map<String,Object> map,Model model) {
		model.addAttribute("project",manageDataService.web_start_data_ById(map));
		
		return "ylyyPag/projectPag/Data_Start";
	}
	
	
	/**
	 * 工程_施工中查看
	 * @return
	 */
	@RequestMapping("/web_working_data_ById")
	public String web_working_data(@RequestParam Map<String,Object> map,Model model) {
		model.addAttribute("data",supervisionDataService.web_working_data_ById(map));
		return "ylyyPag/projectPag/Data_supervision_report";
	}
	
	

	/**
	 * 去工程预验收修改
	 * @return
	 */
	@RequestMapping("/web_go_update_before")
	public String web_go_update_before(@RequestParam Map<String, Object> map,Model model) {
		model.addAttribute("token",QiniuUpload.getUpToken2());
		model.addAttribute("prefix", UrlConfig.FILE_PREFIX);
		model.addAttribute("QiniuUrl", UrlConfig.QiniuUrl);
		Map<String, Object> _data = manageDataService.web_before_data_ById(map);
		model.addAttribute("project",_data);
		model.addAttribute("param",map);
		
		if(_data!=null&&String.valueOf(_data.get("state")).equals("3")){  //已提交
			return "ylyyPag/projectPag/Data_Before";
		}else{
			return "ylyyPag/projectPag/update_Before";
		}
	}

	/**
	 * 去工程预验收修改
	 * @return
	 */
	@RequestMapping("/web_go_supplement_before")
	public String web_go_supplement_before(@RequestParam Map<String, Object> map,Model model) {
		model.addAttribute("token",QiniuUpload.getUpToken2());
		model.addAttribute("prefix", UrlConfig.FILE_PREFIX);
		model.addAttribute("QiniuUrl", UrlConfig.QiniuUrl);
		Map<String, Object> _data = manageDataService.web_before_data_ById(map);
		model.addAttribute("project",_data);
		model.addAttribute("param",map);

		if(_data!=null&&String.valueOf(_data.get("state")).equals("3")){  //已提交
			return "ylyyPag/projectPag/supplement_Before";
		}else{
			return "ylyyPag/projectPag/update_Before";
		}
	}

	/**
	 * 修改_预验收
	 * @param
	 * @return
	 */
	@RequestMapping(value="/web_update_before",method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Object update_before(@RequestBody String json,HttpSession session) {
	return manageDataService.web_update_before(json);
	}
	
	
	/**
	 * 工程_预验收查看
	 * @return
	 */
	@RequestMapping("/web_before_data_ById")
	public String web_before_data_ById(@RequestParam Map<String,Object> map,Model model) {
		model.addAttribute("project",manageDataService.web_before_data_ById(map));
		
		return "ylyyPag/projectPag/Data_Before";
	}
	
	
	
	
	
	
	/**
	 * 去工程验收修改
	 * @return
	 */
	@RequestMapping("/web_go_update_check")
	public String web_go_update_check(@RequestParam Map<String, Object> map,Model model) {
		model.addAttribute("token",QiniuUpload.getUpToken2());
		model.addAttribute("prefix", UrlConfig.FILE_PREFIX);
		model.addAttribute("QiniuUrl", UrlConfig.QiniuUrl);
		Map<String, Object> _data = manageDataService.web_check_data_ById(map);
		model.addAttribute("project",_data); 
		model.addAttribute("param",map);
		
		if(_data!=null&&String.valueOf(_data.get("state")).equals("3")){  //已提交
			return "ylyyPag/projectPag/Data_Check";
		}else{
			return "ylyyPag/projectPag/update_Check";
		}
	}

	/**
	 * 去工程验收修改
	 * @return
	 */
	@RequestMapping("/web_go_supplement_check")
	public String web_go_supplement_check(@RequestParam Map<String, Object> map,Model model) {
		model.addAttribute("token",QiniuUpload.getUpToken2());
		model.addAttribute("prefix", UrlConfig.FILE_PREFIX);
		model.addAttribute("QiniuUrl", UrlConfig.QiniuUrl);
		Map<String, Object> _data = manageDataService.web_check_data_ById(map);
		model.addAttribute("project",_data);
		model.addAttribute("param",map);

		if(_data!=null&&String.valueOf(_data.get("state")).equals("3")){  //已提交
			return "ylyyPag/projectPag/supplement_Check";
		}else{
			return "ylyyPag/projectPag/update_Check";
		}
	}

	/**
	 * 修改_验收
	 * @param
	 * @return
	 */
	@RequestMapping(value="/web_update_check",method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Object update_check(@RequestBody String json,HttpSession session) {
	return manageDataService.web_update_check(json);
	}
	
	/**
	 * 工程_验收查看
	 * @return
	 */
	@RequestMapping("/web_check_data_ById")
	public String web_check_data_ById(@RequestParam Map<String,Object> map,Model model) {
		model.addAttribute("project",manageDataService.web_check_data_ById(map));
		
		return "ylyyPag/projectPag/Data_Check";
	}
	
	
	
	/////////////////////////////////工程结算
	
	/**
	 * 工程结算页面
	 * @return
	 */
	@RequestMapping("/web_project_settlement_list")
	public String web_project_settlement_list(@RequestParam Map<String, Object> map,Model model) {
		
			model.addAttribute("page",manageDataService.web_project_settlement_list(map)); 
		return "ylyyPag/projectPag/settlementShowProject";
	}
	
	
	/**
	 * 工程结算编辑列表
	 * @return
	 */
	@RequestMapping("/web_update_settlement_list")
	public String web_update_settlement_list(@RequestParam Map<String, Object> map,Model model) {
		model.addAttribute("data",manageDataService.web_update_settlement_list(map)); 
		return "ylyyPag/projectPag/settementUpdateList";
	}
	
	
	
	/**
	 * 工程结算记录
	 * @return
	 */
	@RequestMapping("/web_settlement_data_ById")
	public String web_settlement_data_ById(@RequestParam Map<String, Object> map,Model model) {
		model.addAttribute("data",manageDataService.web_update_settlement_list(map)); 
		return "ylyyPag/projectPag/Data_Settlement";
	}
	
	
	/**
	 * 去新增结算
	 * @return
	 */
	@RequestMapping("/goAddSettment")
	public String goAddSettment(@RequestParam Map<String, Object> map,Model model) {
		model.addAttribute("token",QiniuUpload.getUpToken2());
		model.addAttribute("prefix", UrlConfig.FILE_PREFIX);
		model.addAttribute("QiniuUrl", UrlConfig.QiniuUrl);
		model.addAttribute("data",manageDataService.go_add_settlement(map)); //分类列表
		return "ylyyPag/projectPag/addSettment";
	}
	
	/**
	 * 去修改结算
	 * @return
	 */
	@RequestMapping("/goUpdateSettment")
	public String goUpdateSettment(@RequestParam Map<String, Object> map,Model model) {
		model.addAttribute("token",QiniuUpload.getUpToken2());
		model.addAttribute("prefix", UrlConfig.FILE_PREFIX);
		model.addAttribute("QiniuUrl", UrlConfig.QiniuUrl);
		model.addAttribute("data",manageDataService.goUpdateSettment(map)); //分类列表
		return "ylyyPag/projectPag/update_Settment";
	}
	
	
	/**
	 * 新增或者修改_结算
	 * @param
	 * @return
	 */
	@RequestMapping(value="/web_add_update_settment",method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Object web_add_update_settment(@RequestBody String json,HttpSession session) {
	return manageDataService.web_add_update_settment(json);
	}
	
	
/*	
	*//**
	 * 工程_结算查看
	 * @return
	 *//*
	@RequestMapping("/web_settlement_data_ById")
	public String web_settlement_data_ById(@RequestParam Map<String,Object> map,Model model) {
		model.addAttribute("project",manageDataService.web_check_data_ById(map));
		
		return "ylyyPag/projectPag/Data_Check";
	}*/
	
	
	/**
	 * 工程结算查看列表
	 * @return
	 */
	@RequestMapping("/web_show_settlement_list")
	public String web_show_settlement_list(@RequestParam Map<String, Object> map,Model model) {
		model.addAttribute("data",manageDataService.web_update_settlement_list(map)); 
		return "ylyyPag/projectPag/settementShowList";
	}
	
	
	
	/**
	 * 工程报表页面
	 * @return
	 */
	@RequestMapping("/web_project_log")
	public String web_project_log(@RequestParam Map<String, Object> map,Model model) {
		
		model.addAttribute("page",manageDataService.showProjectLog(map)); 
		return "ylyyPag/projectPag/showProjectLog";
	}
	
	
	
	/**
     * 导出工程报表
     * @return
     */
    @RequestMapping("/export_project_Log")
    @ResponseBody
    public void export_project_Log(@RequestParam Map<String, Object> map,HttpServletRequest request,HttpServletResponse response) throws Exception {
    	//获取数据
    	HSSFWorkbook wb=manageDataService.export_project_Log(map);
    	 //excel文件名
        String fileName = "工程报表"+System.currentTimeMillis()+".xls";

       //响应到客户端
         try {
			this.setResponseHeader(response, fileName);
			OutputStream os = response.getOutputStream();
			wb.write(os);
			os.flush();
			os.close();
			} catch (Exception e) {
			e.printStackTrace();
			  }
    }

    
    //发送响应流方法
    public void setResponseHeader(HttpServletResponse response, String fileName) {
        try {
            try {
                fileName = new String(fileName.getBytes(),"ISO8859-1");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            response.setContentType("application/octet-stream;charset=ISO8859-1");
            response.setHeader("Content-Disposition", "attachment;filename="+ fileName);
            response.addHeader("Pargam", "no-cache");
            response.addHeader("Cache-Control", "no-cache");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
	
