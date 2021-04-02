package com.sl.ylyy.manager.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import com.alibaba.fastjson.JSONObject;
import com.sl.ylyy.manager.entity.UserInfo;
import com.sl.ylyy.manager.service.impl.MaterialDataServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.sl.ylyy.manager.service.UserDataService;
import com.sl.ylyy.common.utils.PageUtil;



@Controller
@RequestMapping("/material")
public class MaterialController {

	@Autowired
	MaterialDataServiceImpl materialDataImpl;
	
	@Autowired
	UserDataService userDataService;
	
	
	
	@RequestMapping("/getAllMaterial_Log")
	public String  getMaterial_Log(Model model,int pageindex,String beginTime,String endTime,String userid,HttpSession session) throws ParseException{
		PageUtil<Map<String, Object>> page=new PageUtil<Map<String, Object>>();
		if(pageindex==0){
			pageindex=1;
		}
		page.setPageindex(pageindex); 
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("userid", userid);   //人
		
		if(beginTime!=null&&!beginTime.equals("")){
			params.put("beginTime",beginTime);  //开始时间
			if(endTime==null||endTime.equals("")){
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
				params.put("endTime", simpleDateFormat.format(new Date()));  //结束时间
			}
		}
		
		if(endTime!=null&&!endTime.equals("")){
			params.put("endTime", endTime);   //结束时间
			if(beginTime==null||beginTime.equals("")){
				params.put("beginTime","1970-10-01");  //开始时间
		}
		}
		page.setParams(params);
		page.setPagesize(18);
		
		Map<String, Object> groupMap=new HashMap<>();
		Map<String,Object> omap= JSONObject.parseObject(String.valueOf(session.getAttribute("activeAdmin")));
		groupMap.put("department_id", omap.get("department_id"));
		List<UserInfo> userInfoList =userDataService.getAllUser(groupMap);
		PageUtil<Map<String, Object>> pageData=materialDataImpl.findPage(page);  //新的分页信息
		params.put("beginTime", beginTime); //页面显示开始时间
		params.put("endTime", endTime);     //页面显示结束时间
		pageData.setPageindex(pageindex);  //返回当前页
		params.put("userList", userInfoList);   //所有用户
		params.put("lastpage", pageData.getLastPage());  //上一页
		params.put("nextpage", pageData.getNextpage(pageData.getTotalPagecount())); //下一页
		pageData.setParams(params);  //返回模糊查询信息
		model.addAttribute("page",pageData); 
		return "ylyyPag/materialPag/showMaterial_Log";
	}
	
	
	/**
	 * 预约物料
	 * @param addjob_number
	 * @return
	 */
	@RequestMapping(value="/material_appointment",method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Object material_appointment(@RequestBody String json){
		
		return materialDataImpl.addMaterialAppointment(json);
	}
	
	
	/**
	 * 完成物料
	 * @param
	 * @return
	 */
	@RequestMapping(value="/material_complete",method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Object material_complete(@RequestBody String json){
		return materialDataImpl.addMaterialComplete(json);
	}

	/**
	 * 完成物料
	 * @param
	 * @return
	 */
	@RequestMapping(value="/material_complete_two",method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Object material_complete2(@RequestBody String json){
		return materialDataImpl.addMaterialComplete2(json);
	}

	/**
	 * 物料推送
	 * @param addjob_number
	 * @return
	 */
	@RequestMapping(value="/materiallist_tokeeper",method = RequestMethod.POST)
	@ResponseBody
	public Object materiallist_tokeeper(@RequestParam Map<String, Object> map){
		return materialDataImpl.materiallist_tokeeper(map);
	}
	
	
	
	
	/**
	 * 物料报表
	 * @param addjob_number
	 * @return
	 */
	@RequestMapping(value="/material_log",method = RequestMethod.POST)
	@ResponseBody
	public Object getMaterialLog(@RequestParam Map<String, Object> map){
		return materialDataImpl.getMaterialLog(map);
	}
	
	
}
