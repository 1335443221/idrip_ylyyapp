package com.sl.ylyy.manager.controller;


import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.alibaba.fastjson.JSONObject;
import com.sl.ylyy.manager.entity.PatrolPointInfo;
import com.sl.ylyy.manager.entity.UserInfo;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.sl.ylyy.manager.entity.Patrol_Log;
import com.sl.ylyy.manager.service.Operation_logDataService;
import com.sl.ylyy.manager.service.PatrolDataService;
import com.sl.ylyy.manager.service.Patrol_LogDataService;
import com.sl.ylyy.manager.service.Patrol_PointDataService;
import com.sl.ylyy.manager.service.SystemDataService;
import com.sl.ylyy.manager.service.UserDataService;
import com.sl.ylyy.common.utils.ExcelUtil;
import com.sl.ylyy.common.utils.PageUtil;

@Controller
@RequestMapping("/gtgx")
public class PatrolController {
	@Autowired
	PatrolDataService PatrolDataImpl;
	@Autowired
	Patrol_LogDataService Patrol_LogDataImpl;
	@Autowired
	UserDataService userDataImpl;
	@Autowired
	Patrol_PointDataService Patrol_PointDataImpl;
	@Autowired
	Operation_logDataService operation_logDataService;
	@Autowired
	SystemDataService systemDataImpl;

	
	/**
	 * 获取所有巡检台账
	 * @param stg
	 * @return
	 * @throws ParseException 
	 */
	@RequestMapping("/getAllPatrol")
	public String  getAllPatrol(@RequestParam Map<String, Object> map,Model model,HttpSession session){


		/*PageUtil<Map<String,Object>> page=new PageUtil<Map<String,Object>>();
		if(pageindex==0){
			pageindex=1;
		}
		page.setPageindex(pageindex); 
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("userid", userid);
		
		if(beginTime!=null&&!beginTime.equals("")){
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
			 Date date = simpleDateFormat.parse(beginTime);
			params.put("beginTime", Math.round(date.getTime()/1000));  //开始时间
		}else{
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date date = simpleDateFormat.parse("1970-10-1");
			params.put("beginTime", Math.round(date.getTime()/1000));  //开始时间
		}
		
		if(endTime!=null&&!endTime.equals("")){
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
			 Date date = simpleDateFormat.parse(endTime);
			params.put("endTime", Math.round(date.getTime()/1000+84600));   //结束时间
		}else{
			params.put("endTime", Math.round(new Date().getTime()/1000+84600));  //结束时间
		}
		
		if(beginTime!=null&&!endTime.equals("")&&beginTime.equals(endTime)){
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
			 Date date = simpleDateFormat.parse(beginTime);
			params.put("beginTime", Math.round(date.getTime()/1000));  //开始时间
			params.put("endTime", Math.round(date.getTime()/1000+84600));  //结束时间
		}
		
		
		page.setParams(params);
		
		Map<String, Object> groupMap=new HashMap<>();
		Map<String,Object> omap= JSONObject.parseObject(String.valueOf(session.getAttribute("activeAdmin")));
		groupMap.put("department_id", omap.get("department_id"));
		ArrayList<UserInfo> userlist =userDataImpl.getAllUser(groupMap);
		
		PageUtil<Map<String,Object>> pageData=PatrolDataImpl.findPage(page);  //新的分页信息
		params.put("beginTime", beginTime);  //页面显示开始时间
		params.put("endTime", endTime);     //页面显示结束时间
		params.put("Userlist", userlist);   //所有巡检人，所有用户
		
		pageData.setPageindex(pageindex);  //返回当前页
		params.put("lastpage", pageData.getLastPage());  //上一页
		params.put("nextpage", pageData.getNextpage(pageData.getTotalPagecount())); //下一页
		pageData.setParams(params);  //返回模糊查询信息
		
		model.addAttribute("page",pageData); */
		model.addAttribute("page",PatrolDataImpl.findPage(map,session));
		return "ylyyPag/patrolPag/showPatrol";
	}

	/**
	 * 去增加巡检计划
	 * @return
	 */
	@RequestMapping("/goAddPatrol")
	public String goAddPatrol(@RequestParam Map<String, Object> map,Model model,HttpSession session){
        Map<String,Object> omap= JSONObject.parseObject(String.valueOf(session.getAttribute("activeAdmin")));
           if(omap.get("rid").toString().equals("2")){
               map.put("department_id", 1);  //公司领导默认1
           }else{
               map.put("department_id", omap.get("department_id"));
           }
        map.put("company_id", omap.get("company_id"));
        model.addAttribute("patrolType",PatrolDataImpl.getPatrolType(map));
        model.addAttribute("patrolPoint",PatrolDataImpl.getPatrolPoint(map));
        model.addAttribute("patrolTime",PatrolDataImpl.patrolTime(map));
        model.addAttribute("departmentList",PatrolDataImpl.departmentList(map));
        model.addAttribute("patrolTimeInterval",PatrolDataImpl.patrolTimeInterval(map));
		return "ylyyPag/patrolPag/addPatrol";
	}


	/**
	 * 获取巡检次数通过巡检时间
	 * @return
	 */
	@RequestMapping("/getPatrolCountByTime")
    @ResponseBody
	public List<Map<String,Object>> getPatrolCountByTime(@RequestParam Map<String, Object> map){
		return PatrolDataImpl.getPatrolCountByTime(map);
	}

    /**
     * 巡检类型
     * @return
     */
    @RequestMapping("/getPatrolType")
    @ResponseBody
    public List<Map<String,Object>> getPatrolType(@RequestParam Map<String, Object> map){
        return PatrolDataImpl.getPatrolType(map);
    }

	/**
	 * 新增巡检
	 * @return
	 */
	@RequestMapping("/insertPatrol")
    @ResponseBody
	public String insertPatrol(@RequestParam Map<String, Object> map,HttpSession session){
        Map<String,Object> omap= JSONObject.parseObject(String.valueOf(session.getAttribute("activeAdmin")));
        map.put("company_id", omap.get("company_id"));
        map.put("create_by", omap.get("id"));
		return PatrolDataImpl.insertPatrol(map);
	}

    /**
     * 删除巡检
     * @param id
     * @return
     */
    @RequestMapping("/deletePatrolById")
    @ResponseBody
    public int deletePatrolById(Integer id,HttpSession session){
        PatrolDataImpl.deletePatrolById(id);

        Map<String,Object> omap= JSONObject.parseObject(String.valueOf(session.getAttribute("activeAdmin")));
        omap.put("operation", "删除巡检计划");
        operation_logDataService.insertOperation_log(omap);
        return 1;
    }

	
	/*  -----------------------巡检点-----------------------------*/
	
	
	/**
	 * 获取所有巡检点
	 * @return
	 */
	@RequestMapping("/getAllPatrol_Point")
	public String  getAllPatrol_Point(Model model,int pageindex,String key){
		PageUtil<PatrolPointInfo> page=new PageUtil<PatrolPointInfo>();
		if(pageindex==0){
			pageindex=1;
		}
		page.setPageindex(pageindex);
		page.setPagesize(17);
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("key", key);
		page.setParams(params);
		
		PageUtil<PatrolPointInfo> pageData=Patrol_PointDataImpl.findPage(page);  //新的分页信息
		pageData.setPageindex(pageindex);  //返回当前页
		params.put("lastpage", pageData.getLastPage());  //上一页
		params.put("nextpage", pageData.getNextpage(pageData.getTotalPagecount())); //下一页
		pageData.setParams(params);  //返回模糊查询信息
		model.addAttribute("page",pageData);  
		return "ylyyPag/patrolPag/showPatrol_Point";
	}
	
	/**
	 * 删除巡检点
	 * @param id
	 * @return
	 */
	@RequestMapping("/deletePatrol_PointById")
	@ResponseBody
	public int deletePatrol_PointById(Integer id,HttpSession session){
		Patrol_PointDataImpl.deletePatrol_PointById(id);
		
  	    Map<String,Object> omap= JSONObject.parseObject(String.valueOf(session.getAttribute("activeAdmin")));
		omap.put("operation", "删除巡检点");
	    operation_logDataService.insertOperation_log(omap);
		return 1;
	}
	
	/**
	 * 获取一个
	 * @param id
	 * @return
	 */
	@RequestMapping("/getPatrol_PointById")
	public String getPatrol_PointById(Integer id,Model model){
		PatrolPointInfo patrol_pointInfo = Patrol_PointDataImpl.getPatrol_PointById(id);
		model.addAttribute("patrol_point", patrol_pointInfo);
		return "ylyyPag/patrolPag/addOrUpdatePatrol_Point";
	}
	
	/**
	 * 去增加
	 * @return
	 */
	@RequestMapping("/goAddPatrol_Point")
	public String goAddPatrol_Point(){
		return "ylyyPag/patrolPag/addOrUpdatePatrol_Point";
	}
	
	
	
	/**
	 * 新增或修改巡检点
	 * @return
	 */
	@RequestMapping("/insertOrUpdatePatrol_Point")
	@ResponseBody
	public int insertOrUpdatePatrol_Point(@RequestParam Map<String, Object> map, HttpSession session){
		Map<String,Object> omap= JSONObject.parseObject(String.valueOf(session.getAttribute("activeAdmin")));
		if(map.get("id")!=null&&!String.valueOf(map.get("id")).equals("0")&&!String.valueOf(map.get("id")).equals("")){
			Patrol_PointDataImpl.updatePatrol_PointById(map);
			omap.put("operation", "修改巡检点信息");
		    operation_logDataService.insertOperation_log(omap);
		}else{
			Patrol_PointDataImpl.insertPatrol_Point(map);
			omap.put("operation", "新增巡检点");
		    operation_logDataService.insertOperation_log(omap);
		}
		return 1;
	}
	
	/*-----------------------------巡检报表--------------------------------*/
	
	/**
	 * 获取所有巡检报表
	 * @param stg
	 * @return
	 * @throws ParseException 
	 */
	@RequestMapping("/getAllPatrol_Log")
	public String  getAllPatrol_Log(Model model,int pageindex,String groupid,String userid,String beginTime,String endTime,HttpSession session) throws ParseException{
		PageUtil<Patrol_Log> page=new PageUtil<Patrol_Log>();
		if(pageindex==0){
			pageindex=1;
		}
		page.setPageindex(pageindex); 
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("userid", userid);
		params.put("groupid", groupid);
		
		if(beginTime!=null&&!beginTime.equals("")){
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
			 Date date = simpleDateFormat.parse(beginTime);
			params.put("beginTime", Math.round(date.getTime()/1000));  //开始时间
		}else{
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date date = simpleDateFormat.parse("1970-10-1");
			params.put("beginTime", Math.round(date.getTime()/1000));  //开始时间
		}
		
		if(endTime!=null&&!endTime.equals("")){
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
			 Date date = simpleDateFormat.parse(endTime);
			params.put("endTime", Math.round(date.getTime()/1000+84600));   //结束时间
		}else{
			params.put("endTime", Math.round(System.currentTimeMillis()/1000+84600));  //结束时间
		}
		
		if(beginTime!=null&&!endTime.equals("")&&beginTime.equals(endTime)){
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
			 Date date = simpleDateFormat.parse(beginTime);
			params.put("beginTime", Math.round(date.getTime()/1000));  //开始时间
			params.put("endTime", Math.round(date.getTime()/1000+84600));  //结束时间
		}
		
		
		page.setParams(params);
		
		Map<String, Object> groupMap=new HashMap<>();
		Map<String,Object> omap= JSONObject.parseObject(String.valueOf(session.getAttribute("activeAdmin")));
		groupMap.put("department_id", omap.get("department_id"));
		List<Map<String, Object>> groupList=systemDataImpl.getAllGroup(groupMap);
		ArrayList<UserInfo> userlist =userDataImpl.getAllUser(groupMap);
		PageUtil<Patrol_Log> pageData=Patrol_LogDataImpl.findPage(page);  //新的分页信息
		params.put("beginTime", beginTime);  //页面显示开始时间
		params.put("endTime", endTime);     //页面显示结束时间
		params.put("Userlist", userlist);   //所有巡检人，所有用户
		params.put("groupList", groupList);   //所有班组
		pageData.setPageindex(pageindex);  //返回当前页
		params.put("lastpage", pageData.getLastPage());  //上一页
		params.put("nextpage", pageData.getNextpage(pageData.getTotalPagecount())); //下一页
		pageData.setParams(params);  //返回模糊查询信息
		model.addAttribute("page",pageData); 
		
		return "ylyyPag/patrolPag/showPatrol_Log";
	}
	
	/**
     * 导出报表
     * @return
     */
    @RequestMapping("/exportPatrol_Log")
    @ResponseBody
    public void export(HttpServletRequest request,HttpServletResponse response,String userid,String beginTime,String endTime,String groupid) throws Exception {
           //获取数据
    	PageUtil<Patrol_Log> page=new PageUtil<Patrol_Log>();
    	Map<String,Object> params = new HashMap<String,Object>();
		params.put("userid", userid);
		params.put("groupid", groupid);
		if(beginTime!=null&&!beginTime.equals("")){
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
			 Date date = simpleDateFormat.parse(beginTime);
			params.put("beginTime", Math.round(date.getTime()/1000));  //开始时间
		}
		
		if(endTime!=null&&!beginTime.equals("")){
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
			 Date date = simpleDateFormat.parse(endTime);
			params.put("endTime", Math.round(date.getTime()/1000));   //结束时间
		}
		page.setParams(params);
    	PageUtil<Patrol_Log> pageData = Patrol_LogDataImpl.report(page);
        //excel标题
        String[] title = {"序号","日期","巡检人","巡检开始时间","巡检结束时间","巡检次数","巡检点数量","巡检正常","巡检故障"};

        //excel文件名
        String fileName = "巡检统计报表"+System.currentTimeMillis()+".xls";

        //sheet名
         String sheetName = "巡检统计报表";
         String[][] content = new String[pageData.getData().size()][];
         for (int i = 0; i < pageData.getData().size(); i++) {
        	  content[i] = new String[title.length];
        	  Patrol_Log obj = pageData.getData().get(i);
             content[i][0] = (i+1)+"";
             content[i][1] = obj.getTime();
             content[i][2] = obj.getPatrolUserInfo().getUser_name();
             content[i][3] = obj.getFirst_clock_at1();
             content[i][4] = obj.getSecond_clock_at1();
             content[i][5] = obj.getPatrolInfo().getCount()+"";
             content[i][6] = "1";
             content[i][7] = (obj.getMalcount()==0?1:0)+"";
             content[i][8] = obj.getMalcount()+"";
         }
       //创建HSSFWorkbook 
         HSSFWorkbook wb = ExcelUtil.getHSSFWorkbook(sheetName, title, content, null);

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
	
