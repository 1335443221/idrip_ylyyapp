package com.sl.ylyy.manager.controller;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpSession;

import com.alibaba.fastjson.JSONObject;
import com.sl.ylyy.common.config.UrlConfig;
import com.sl.ylyy.common.utils.*;
import com.sl.ylyy.manager.entity.RoleInfo;
import com.sl.ylyy.manager.service.UploadConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.sl.ylyy.manager.entity.Operation_log;
import com.sl.ylyy.manager.service.Operation_logDataService;
import com.sl.ylyy.manager.service.SystemDataService;
import com.sl.ylyy.manager.service.UserDataService;

@Controller
@RequestMapping("/gtgx")
public class UserController {
	@Autowired
	UserDataService userDataImpl;
	@Autowired
	SystemDataService systemDataImpl;
	@Autowired
	Operation_logDataService operation_logDataService;
	@Autowired
	private UploadConfigService uploadConfigService;
	
	
	
	/**
	 * app登录
	 * @param 
	 * @return
	 */
	@RequestMapping("/applogin")
	@ResponseBody
	public Object applogin(@RequestParam Map<String, Object> map){
		return userDataImpl.checkAppLogin(map);
	}
	
	

	/**
	 * 获取所有用户信息（分页查询）
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping("/getAllUser")
	public String getAllUser(@RequestParam Map<String, Object> params,Model model){
		PageUtil<Map<String,Object>> page=new PageUtil<Map<String,Object>>();
		
		page.setPageindex(Integer.parseInt(String.valueOf(params.get("pageindex")))); 
		page.setParams(params);
		
		PageUtil<Map<String,Object>> pageData=userDataImpl.findPage(page);  //新的分页信息
		
		List<Map<String,Object>> companyList=systemDataImpl.getAllCompany(null);
		List<Map<String,Object>> departmentList=systemDataImpl.getAllDepartment(null);
		
		params.put("companyList", companyList);   //存入所有公司
		params.put("departmentList", departmentList);   //存入所有公司
		pageData.setPageindex(Integer.parseInt(String.valueOf(params.get("pageindex"))));  //返回当前页
		params.put("lastpage", pageData.getLastPage());  //上一页
		params.put("nextpage", pageData.getNextpage(pageData.getTotalPagecount())); //下一页
		pageData.setParams(params);  //返回模糊查询信息
 		model.addAttribute("page",pageData);    
		return "ylyyPag/userPag/showUser";
	}
	
	
	/**
	 * 通过id查询具体user的信息  并返回修改用户页面
	 * @param
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping("/goUpdateUser")
	public String goUpdateUser(Integer id,Model model) throws UnsupportedEncodingException{
		Map<String, Object> userInfo = userDataImpl.getUserinfoById(id);
		List<RoleInfo> roleInfoList =systemDataImpl.getAllRole("");
		List<Map<String,Object>> companyList=systemDataImpl.getAllCompany(null);
		
		model.addAttribute("user", userInfo);
		model.addAttribute("roleList", roleInfoList);
		model.addAttribute("companyList", companyList);
		model.addAttribute("token",QiniuUpload.getUpToken2());
		model.addAttribute("prefix", UrlConfig.FILE_PREFIX);
		model.addAttribute("QiniuUrl", UrlConfig.QiniuUrl);
		return "ylyyPag/userPag/updateUser";
	}
	
	
	/**
	 * 去增加页面
	 * @return
	 */
	@RequestMapping("/goAddUser")
	public String goAddUser(Model model){
		List<RoleInfo> roleInfoList =systemDataImpl.getAllRole("");
		List<Map<String,Object>> companyList=systemDataImpl.getAllCompany(null);
		model.addAttribute("roleList", roleInfoList);
		model.addAttribute("companyList", companyList);
		model.addAttribute("token",QiniuUpload.getUpToken2());
		model.addAttribute("prefix", UrlConfig.FILE_PREFIX);
		model.addAttribute("QiniuUrl", UrlConfig.QiniuUrl);
		return "ylyyPag/userPag/addUser";
	}
	
	

	/**
	 * 公司下的department
	 * @param 
	 * @return
	 */
	@RequestMapping("/departmentByCompany")
	@ResponseBody
	public Object departmentByCompany(@RequestParam Map<String, Object> map){
		return systemDataImpl.getAllDepartment(map);
	}
	
	/**
	 * 公司下的department
	 * @param 
	 * @return
	 */
	@RequestMapping("/groupByDepartment")
	@ResponseBody
	public Object groupByDepartment(@RequestParam Map<String, Object> map){
		return systemDataImpl.getAllGroup(map);
	}
	
	
	/**
	 * 判断工号是否重复
	 * @param
	 * @return
	 */
	@RequestMapping("/checkJob_number")
	@ResponseBody
	public String checkJob_number(String addjob_number){
		if(userDataImpl.checkJob_number(addjob_number).size()>0){
			return "1";
		}else{
			return "0";
		}
	}
	
	
	/**
	 * 判断登录名是否重复
	 * @param
	 * @return
	 */
	@RequestMapping("/checkLogin_name")
	@ResponseBody
	public String checkLogin_name(String addlogin_name){
		if(userDataImpl.checkLogin_name(addlogin_name).size()>0){
			return "1";
		}else{
			return "0";
		}
	}
	
	/**
	 * 判断手机号是否重复
	 * @param
	 * @return
	 */
	@RequestMapping("/checkCellphone")
	@ResponseBody
	public String checkCellphone(@RequestParam Map<String, Object> map){
		if(userDataImpl.checkCellphone(map).size()>0){
			return "1";
		}else{
			return "0";
		}
	}

	
	
	@RequestMapping("/checkDelete")
	@ResponseBody
	public int checkDelete(@RequestParam Map<String, Object> map){
		return userDataImpl.checkDelete(map).size();
	}
	
	
	/**
	 * 新增或修改用户信息
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping("/insertOrUpdateUser")
	@ResponseBody
	public int insertOrUpdateUser(@RequestParam Map<String, Object> map, HttpSession session) throws Exception{
		 String ps=MD5.md5(String.valueOf(map.get("password")), ""); //加密后的密码
		 map.put("password", ps);
		int result=0;
		if(map.get("id")!=null&&!String.valueOf(map.get("id")).equals("0")){  //修改
			if(map.get("password").equals("d41d8cd98f00b204e9800998ecf8427e")){
				map.put("password", null);
			}
			
			result=userDataImpl.updateUserinfo(map);
			
			Map<String,Object> omap= JSONObject.parseObject(String.valueOf(session.getAttribute("activeAdmin")));
			omap.put("operation", "修改用户信息");
	  	    operation_logDataService.insertOperation_log(omap);
		}else{
			result=userDataImpl.insertUser(map);
			
			Map<String,Object> omap= JSONObject.parseObject(String.valueOf(session.getAttribute("activeAdmin")));
			omap.put("operation", "增加新用户");
	  	    operation_logDataService.insertOperation_log(omap);
		}
		return result;
	}
	
	
	/**
	 * 删除用户
	 * @return
	 */
	@RequestMapping("/deleteUser")
	@ResponseBody
	public int deleteUser(Integer id,HttpSession session){
		userDataImpl.deleteUserById(id);
		
  	    Map<String,Object> omap= JSONObject.parseObject(String.valueOf(session.getAttribute("activeAdmin")));
		omap.put("operation", "删除用户");
	    operation_logDataService.insertOperation_log(omap);

		return 1;
	}
	
	/////////////////////////////////用户信息模块//////////////////////////////
	
	/**
	 * 用户信息
	 * @return
	 */
	@RequestMapping("/showMySelf")
	public String showMySelf(int id,Model model){
		Map<String, Object> userInfo =userDataImpl.getUserinfoById(id);
		model.addAttribute("myself", userInfo);
		return "ylyyPag/userPag/showMySelf";
	}
	
	/**
	 * 跳转到修改页面
	 * @return
	 */
	@RequestMapping("/goUpdateUserinfo")
	public String goUpdateUserinfo(int id,Model model) {
		Map<String, Object> userInfo =userDataImpl.getUserinfoById(id);
		
		model.addAttribute("myself", userInfo);
		model.addAttribute("token",QiniuUpload.getUpToken2());
		model.addAttribute("prefix", UrlConfig.FILE_PREFIX);
		model.addAttribute("QiniuUrl", UrlConfig.QiniuUrl);
		return "ylyyPag/userPag/updateMySelf";
	}
	
	/**
	 * 修改个人信息
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping("/updateUserinfo")
	@ResponseBody
	public int updateUserinfo(@RequestParam Map<String, Object> map, HttpSession session) throws Exception{
		String ps=MD5.md5(String.valueOf(map.get("password")), ""); //加密后的密码
		map.put("password", ps);
		if(ps.equals("d41d8cd98f00b204e9800998ecf8427e")||map.get("password")==null||String.valueOf(map.get("password")).equals("")){
			map.put("password", null);
		}
		
		
		return userDataImpl.updateUserinfo(map);
	}
	
	
	
	/**
	 * 获取所有操作记录
	 * @param
	 * @return
	 */
	@RequestMapping("/showOperation_log")
	public String  showOperation_log(String key,Model model,int pageindex,HttpSession session){
		PageUtil<Operation_log> page=new PageUtil<Operation_log>();
		if(pageindex==0){
			pageindex=1;
		}
		page.setPageindex(pageindex); 
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("key", key);
		page.setParams(params);
		PageUtil<Operation_log> pageData=null;
		Map<String,Object> u= JSONObject.parseObject(String.valueOf(session.getAttribute("activeAdmin")));
		if(u.get("login_name").equals("test")||u.get("login_name").equals("developer")){
			pageData=operation_logDataService.findPage2(page);  //新的分页信息
		}else{
			pageData=operation_logDataService.findPage(page);  //新的分页信息
		}
		
		pageData.setPageindex(pageindex);  //返回当前页
		params.put("lastpage", pageData.getLastPage());  //上一页
		params.put("nextpage", pageData.getNextpage(pageData.getTotalPagecount())); //下一页
		pageData.setParams(params);  //返回模糊查询信息
		model.addAttribute("page",pageData);   
		
		return "ylyyPag/operation_logPag/showOperation_log";
	}




	/**
	 * 录入融云用户和群组关系数据
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/recordRongUserGroupData")
	@ResponseBody
	public CodeMsg recordRongUserGroupData(@RequestParam Map<String, Object> map){
		return userDataImpl.recordRongUserGroupData(map);
	}


	/**
	 * 录入用户到融云的token
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/recordRongUserToken")
	@ResponseBody
	public CodeMsg recordRongUserToken(@RequestParam Map<String, Object> map){
		return userDataImpl.recordRongUserToken(map);
	}


	/**
	 * 录入群组到融云
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/recordRongGroup")
	@ResponseBody
	public CodeMsg recordRongGroup(@RequestParam Map<String, Object> map){
		return userDataImpl.recordRongGroup(map);
	}

	/**
	 * 检查是否有通知群发通知的权限
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/checkNoticeAuth")
	@ResponseBody
	public Result checkNoticeAuth(@RequestParam Map<String, Object> map){

		return userDataImpl.checkNoticeAuth(map);
	}


	/**
	 * 测试高新
	 * @return
	 */
	@RequestMapping("/testGaoXin")
	public String testGaoXin(){

		return "redirect:"+UrlConfig.DEV_PMP_IDRIP_URL+"/user/deleteAppUserAndUser?user_id=152";
	}


}
