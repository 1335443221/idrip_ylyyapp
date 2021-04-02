package com.sl.ylyy.manager.controller;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpSession;
import com.sl.ylyy.manager.entity.RoleInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.sl.ylyy.manager.entity.Auth;
import com.sl.ylyy.manager.service.Operation_logDataService;
import com.sl.ylyy.manager.service.SystemDataService;
import com.alibaba.fastjson.JSONObject;
import com.sl.ylyy.common.utils.PageUtil;

@Controller
@RequestMapping("/gtgx")
public class SystemController {
	
	@Autowired
	SystemDataService systemDataImpl;
	@Autowired
	Operation_logDataService operation_logDataService;

	
	
	
	/**
	 * 获取所有公司
	 */
	@RequestMapping("/getAllCompany")
	public String  getAllCompany(Model model,int pageindex,String key){
		PageUtil<Map<String, Object>> page=new PageUtil<Map<String, Object>>();
		if(pageindex==0){
			pageindex=1;
		}
		page.setPageindex(pageindex); 
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("key", key);
		page.setParams(params);
		
		PageUtil<Map<String, Object>> pageData=systemDataImpl.findCompanyPage(page);  //新的分页信息
		
		pageData.setPageindex(pageindex);  //返回当前页
		params.put("lastpage", pageData.getLastPage());  //上一页
		params.put("nextpage", pageData.getNextpage(pageData.getTotalPagecount())); //下一页
		
		
		pageData.setParams(params);  //返回模糊查询信息
		model.addAttribute("page",pageData);  
		return "ylyyPag/systemPag/showCompany";
	}
	
	/**
	 * 根据id获取公司
	 */
	@RequestMapping("/getCompanyById")
	public String  getCompanyById(Integer id,Model model){
		Map<String, Object> company= systemDataImpl.getCompanyById(id);
		model.addAttribute("company", company);
		List<Map<String, Object>> companyList = systemDataImpl.getAllCompany(null);
		model.addAttribute("companyList", companyList);
		return "ylyyPag/systemPag/addOrUpdateCompany";
	}
	
	/**
	 * 去添加公司页面
	 */
	@RequestMapping("/goAddCompany")
	public String  goAddCompany(Model model){
		List<Map<String, Object>> companyList = systemDataImpl.getAllCompany(null);
		model.addAttribute("companyList", companyList);
		return "ylyyPag/systemPag/addOrUpdateCompany";
	}
	
	
	
	/**
	 * 删除公司
	 */
	@RequestMapping("/deleteCompanyById")
	@ResponseBody
	public int deleteCompanyById(Integer id,HttpSession session){

		Map<String,Object> omap= JSONObject.parseObject(String.valueOf(session.getAttribute("activeAdmin")));
		omap.put("operation", "删除公司");
		systemDataImpl.deleteCompanyById(id,Integer.parseInt(String.valueOf(omap.get("id"))),String.valueOf(omap.get("rong_user_id")));  //删除公司
		operation_logDataService.insertOperation_log(omap);
		return 1;
	}
	
	/**
	 * 新增或者修改公司
	 */
	@RequestMapping("/insertOrUpdateCompany")
	@ResponseBody
	public int insertOrUpdateCompany(@RequestParam Map<String,Object> map,HttpSession session){
		Map<String,Object> omap= JSONObject.parseObject(String.valueOf(session.getAttribute("activeAdmin")));
		map.put("create_by",omap.get("id"));
		map.put("rong_user_id",omap.get("rong_user_id"));

		if(map.get("id")!=null){
		systemDataImpl.updateCompanyById(map);  //修改公司
		omap.put("operation", "修改公司信息");
	    operation_logDataService.insertOperation_log(omap);
		}else{
		systemDataImpl.insertCompany(map);  //新增公司
		omap.put("operation", "新增公司");
	    operation_logDataService.insertOperation_log(omap);
		}
		return 1;
	}
	
	
	
	/**
	 * 获取所有部门
	 */
	@RequestMapping("/getAllDepartment")
	public String  getAllDepartment(@RequestParam Map<String, Object> params,Model model,int pageindex){
		PageUtil<Map<String, Object>> page=new PageUtil<Map<String, Object>>();
		if(pageindex==0){
			pageindex=1;
		}
		page.setPageindex(pageindex); 
		page.setParams(params);
		
		PageUtil<Map<String, Object>> pageData=systemDataImpl.findDepartmentPage(page);  //新的分页信息
		
		pageData.setPageindex(pageindex);  //返回当前页
		params.put("lastpage", pageData.getLastPage());  //上一页
		params.put("nextpage", pageData.getNextpage(pageData.getTotalPagecount())); //下一页
		List<Map<String,Object>> companyList=systemDataImpl.getAllCompany(null);
		params.put("companyList", companyList);   //存入所有公司
		
		pageData.setParams(params);  //返回模糊查询信息
		model.addAttribute("page",pageData);  
		return "ylyyPag/systemPag/showDepartment";
	}
	
	/**
	 * 根据id获取部门
	 */
	@RequestMapping("/getDepartmentById")
	public String  getDepartmentById(Integer id,Model model){
		Map<String, Object> department= systemDataImpl.getDepartmentById(id);
		model.addAttribute("department", department);
		List<Map<String, Object>> companyList = systemDataImpl.getAllCompany(null);
		model.addAttribute("companyList", companyList);
		return "ylyyPag/systemPag/addOrUpdateDepartment";
	}
	
	/**
	 * 去添加部门页面
	 */
	@RequestMapping("/goAddDepartment")
	public String  goAddDepartment(Model model){
		List<Map<String, Object>> companyList = systemDataImpl.getAllCompany(null);
		model.addAttribute("companyList", companyList);
		return "ylyyPag/systemPag/addOrUpdateDepartment";
	}
	
	
	
	/**
	 * 删除部门
	 */
	@RequestMapping("/deleteDepartmentById")
	@ResponseBody
	public int deleteDepartmentById(Integer id,HttpSession session){

		Map<String,Object> omap= JSONObject.parseObject(String.valueOf(session.getAttribute("activeAdmin")));
		systemDataImpl.deleteDepartmentById(id,Integer.parseInt(String.valueOf(omap.get("id"))),String.valueOf(omap.get("rong_user_id")));

		omap.put("operation", "删除部门");
	    operation_logDataService.insertOperation_log(omap);
		return 1;
	}
	
	/**
	 * 新增或者修改部门
	 */
	@RequestMapping("/insertOrUpdateDepartment")
	@ResponseBody
	public int insertOrUpdateDepartment(@RequestParam Map<String,Object> map,HttpSession session){
		Map<String,Object> omap= JSONObject.parseObject(String.valueOf(session.getAttribute("activeAdmin")));
		map.put("create_by",omap.get("id"));
		map.put("rong_user_id",omap.get("rong_user_id"));
		if(map.get("id")!=null){
		systemDataImpl.updateDepartmentById(map);
		omap.put("operation", "修改部门信息");
	    operation_logDataService.insertOperation_log(omap);
		}else{
		systemDataImpl.insertDepartment(map);
		omap.put("operation", "新增部门");
	    operation_logDataService.insertOperation_log(omap);
		}
		return 1;
	}
	
	///////////////////////////////////工作组
	/**
	 * 获取所有工作组
	 */
	@RequestMapping("/getAllGroup")
	public String  getAllGroup(@RequestParam Map<String, Object> params,Model model,int pageindex){
		PageUtil<Map<String, Object>> page=new PageUtil<Map<String, Object>>();
		if(pageindex==0){
			pageindex=1;
		}
		page.setPageindex(pageindex); 
		page.setParams(params);
		
		PageUtil<Map<String, Object>> pageData=systemDataImpl.findGroupPage(page);  //新的分页信息
		
		pageData.setPageindex(pageindex);  //返回当前页
		params.put("lastpage", pageData.getLastPage());  //上一页
		params.put("nextpage", pageData.getNextpage(pageData.getTotalPagecount())); //下一页
		
		List<Map<String,Object>> companyList=systemDataImpl.getAllCompany(null);
		List<Map<String,Object>> departmentList=systemDataImpl.getAllDepartment(null);
		params.put("companyList", companyList);   //存入所有公司
		params.put("departmentList", departmentList);   //存入所有公司
		
		pageData.setParams(params);  //返回模糊查询信息
		model.addAttribute("page",pageData);  
		
		return "ylyyPag/systemPag/showGroup";
	}
	
	/**
	 * 根据id获取工作组
	 */
	@RequestMapping("/getGroupById")
	public String  getGroupById(Integer id,Model model){
		Map<String,Object> group= systemDataImpl.getGroupById(id);
		model.addAttribute("group", group);
		List<Map<String, Object>> companyList = systemDataImpl.getAllCompany(null);
		model.addAttribute("companyList", companyList);
		Map<String,Object> tMap=new HashMap<>();
		tMap.put("group_id", id);
		tMap.put("department_id", group.get("department_id"));
		List<Map<String, Object>> typeList = systemDataImpl.getAllMalfunction_Type(tMap);
		model.addAttribute("typeList", typeList);
		return "ylyyPag/systemPag/group_update";
	}
	
	/**
	 * 去添加工作组页面
	 */
	@RequestMapping("/goAddGroup")
	public String  goAddGroup(Model model,HttpSession session){
		List<Map<String, Object>> companyList = systemDataImpl.getAllCompany(null);
		Map<String,Object> smap= JSONObject.parseObject(String.valueOf(session.getAttribute("activeAdmin")));
		Map<String,Object> map=new HashMap<>();
		map.put("department_id", smap.get("department_id"));
		model.addAttribute("companyList", companyList);
		model.addAttribute("Malfunction_TypeList", systemDataImpl.getAllMalfunction_Type(map));
		return "ylyyPag/systemPag/group_add";
	}

	/**
	 * 根据条件查询故障类型
	 */
	@RequestMapping("/malfunction_TypeByMap")
	@ResponseBody
	public List<Map<String,Object>>  malfunction_TypeByMap(@RequestParam Map<String,Object> map){
		return systemDataImpl.getAllMalfunction_Type(map);
	}




	/**
	 * 删除工作组
	 */
	@RequestMapping("/deleteGroupById")
	@ResponseBody
	public int deleteGroupById(Integer id,HttpSession session){
		Map<String,Object> omap= JSONObject.parseObject(String.valueOf(session.getAttribute("activeAdmin")));

		systemDataImpl.deleteGroupById(id,Integer.parseInt(String.valueOf(omap.get("id"))),String.valueOf(omap.get("rong_user_id")));

		omap.put("operation", "删除工作组");
	    operation_logDataService.insertOperation_log(omap);
		return 1;
	}


	/**
	 * 新增或者修改工作组
	 */
	@RequestMapping(value="/insertOrUpdateGroup",method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public int insertOrUpdateGroup(@RequestBody String json,HttpSession session){
		Map<String,Object> map=JSONObject.parseObject(json); //解析数据
		int result=0;
		Map<String,Object> omap= JSONObject.parseObject(String.valueOf(session.getAttribute("activeAdmin")));
		map.put("create_by",omap.get("id"));
		map.put("rong_user_id",omap.get("rong_user_id"));
		if(map.get("group_id")!=null){
		result=systemDataImpl.updateGroupById(map);
		omap.put("operation", "修改工作组信息");
	    operation_logDataService.insertOperation_log(omap);
		}else{
		result=systemDataImpl.insertGroup(map);	
		omap.put("operation", "新增工作组");
	    operation_logDataService.insertOperation_log(omap);
		}
		
		return result;
	}
	
	
	/**
	 * 获取所有角色
	 */
	@RequestMapping("/getAllRole")
	public String  getAllRole(String key,Model model,int pageindex){
		PageUtil<RoleInfo> page=new PageUtil<RoleInfo>();
		if(pageindex==0){
			pageindex=1;
		}
		page.setPageindex(pageindex); 
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("key", key);
		page.setParams(params);
		
		PageUtil<RoleInfo> pageData=systemDataImpl.findRolePage(page);  //新的分页信息
		pageData.setPageindex(pageindex);  //返回当前页
		params.put("lastpage", pageData.getLastPage());  //上一页
		params.put("nextpage", pageData.getNextpage(pageData.getTotalPagecount())); //下一页
		pageData.setParams(params);  //返回模糊查询信息
		model.addAttribute("page",pageData);   
		
		return "ylyyPag/systemPag/showRole";
	}
	
	/**
	 * 删除角色
	 */
	@RequestMapping("/deleteRoleById")
	@ResponseBody
	public int deleteRoleById(Integer id){
		systemDataImpl.deleteRoleById(id);
		return 1;
	}
	
	/**
	 * 去增加页面
	 */
	@RequestMapping("/goAddRole")
	public String goAddRole(Model model){
		List<Auth> authList=systemDataImpl.getAllAuth("");
		model.addAttribute("authList", authList);
		return "ylyyPag/systemPag/addRole";
	}
	
	
	
	/**
	 * 获取一个角色  去修改页面
	 */
	@RequestMapping("/getRoleById")
	public String getRoleById(Integer id,Model model){
		RoleInfo roleInfo =systemDataImpl.getRoleById(id);
		List<Auth> authList=systemDataImpl.getAllAuth("");
		model.addAttribute("authList", authList);
		model.addAttribute("role", roleInfo);
		return "ylyyPag/systemPag/updateRole";
	}
	
	
	/**
	 * 新增或修改角色
	 */
	@RequestMapping("/insertOrUpdateRole")
	@ResponseBody
	public int insertOrUpdateRole(RoleInfo roleInfo){
		if(roleInfo.getId()!=0){
			systemDataImpl.updateRoleById(roleInfo);
		}else{
			systemDataImpl.insertRole(roleInfo);
		}
		return 1;
	}
	
}
	
