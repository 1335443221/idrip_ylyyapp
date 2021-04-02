package com.sl.ylyy.manager.controller;


import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.alibaba.fastjson.JSONObject;
import com.sl.ylyy.common.config.UrlConfig;
import com.sl.ylyy.common.utils.QiniuUpload;
import com.sl.ylyy.manager.entity.MalfunctionInfo;
import com.sl.ylyy.manager.entity.MalfunctionLogInfo;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.sl.ylyy.manager.service.MalfunctionDataService;
import com.sl.ylyy.manager.service.Malfunction_LogDataService;
import com.sl.ylyy.manager.service.SystemDataService;
import com.sl.ylyy.manager.service.impl.CommonDataServiceImpl;
import com.sl.ylyy.common.utils.ExcelUtil;
import com.sl.ylyy.common.utils.PageUtil;

@Controller
@RequestMapping("/gtgx")
public class MalfunctionController {
	@Autowired
	MalfunctionDataService MalfunctionDataImpl;
	
	@Autowired
	Malfunction_LogDataService Malfunction_LogDataImpl;
	
	@Autowired
	SystemDataService systemDataImpl;
	
	/*  ----------------------------------故障内容-------------------------------     */
	/**
	 * 获取所有故障
	 * @param
	 * @return
	 * @throws ParseException 
	 */
	@RequestMapping("/getAllMalfunction")
	public String  getAllMalfunction(Model model,int pageindex,String type,String status,String endTime,String beginTime) throws ParseException{
		PageUtil<MalfunctionInfo> page=new PageUtil<MalfunctionInfo>();
		if(pageindex==0){
			pageindex=1;
		}
		
		page.setPageindex(pageindex); 
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("type", type);    //故障类型
		params.put("status", status);    //故障状态
		
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
		page.setPagesize(18);
		PageUtil<MalfunctionInfo> pageData=MalfunctionDataImpl.findPage(page);  //新的分页信息
		params.put("beginTime", beginTime);  //页面显示开始时间
		params.put("endTime", endTime);     //页面显示结束时间
		pageData.setPageindex(pageindex);  //返回当前页
		params.put("lastpage", pageData.getLastPage());  //上一页
		params.put("nextpage", pageData.getNextpage(pageData.getTotalPagecount())); //下一页
		pageData.setParams(params);  //返回模糊查询信息
		model.addAttribute("page",pageData); 
		return "ylyyPag/malfunctionPag/showMalfunction";
	}
	
	
	
	/**
	 * 查看故障的具体错误描述信息
	 * @return
	 */
	@RequestMapping("/getMalfunctionDetail")
	public String getMalfunctionDetail(Integer id,Model model){
		model.addAttribute("malfunction", MalfunctionDataImpl.getMalfunctionDetail(id));
		return "ylyyPag/malfunctionPag/showMalfunctionDetail";
	}
	
	
	/*  ----------------------------------报表内容-------------------------------     */
	/**
	 * 获取所有故障报表
	 * @param
	 * @return
	 * @throws ParseException 
	 */
	@RequestMapping("/getAllMalfunction_Log")
	public String  getAllMalfunction_Log(Model model,int pageindex,String type,String status,String beginTime,String endTime,String groupid,HttpSession session) throws ParseException{
		PageUtil<MalfunctionLogInfo> page=new PageUtil<MalfunctionLogInfo>();
		if(pageindex==0){
			pageindex=1;
		}
		page.setPageindex(pageindex); 
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("type", type);    //故障类型
		params.put("status", status);   //故障状态
		params.put("groupid", groupid);   //班组
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
		page.setPagesize(18);
		
		Map<String, Object> groupMap=new HashMap<>();
		Map<String,Object> omap= JSONObject.parseObject(String.valueOf(session.getAttribute("activeAdmin")));
		groupMap.put("department_id", omap.get("department_id"));
		List<Map<String, Object>> groupList=systemDataImpl.getAllGroup(groupMap);
		
		PageUtil<MalfunctionLogInfo> pageData=Malfunction_LogDataImpl.findPage(page);  //新的分页信息
		params.put("beginTime", beginTime); //页面显示开始时间
		params.put("endTime", endTime);     //页面显示结束时间
		
		pageData.setPageindex(pageindex);  //返回当前页
		params.put("groupList", groupList);   //所有班组
		params.put("lastpage", pageData.getLastPage());  //上一页
		params.put("nextpage", pageData.getNextpage(pageData.getTotalPagecount())); //下一页
		pageData.setParams(params);  //返回模糊查询信息
		model.addAttribute("page",pageData); 
		return "ylyyPag/malfunctionPag/showMalfunction_Log";
	}
	
	/**
	 * 故障角色
	 * @param id
	 * @return
	 */
	@RequestMapping("/deleteMalfunction_LogById")
	@ResponseBody
	public String deleteMalfunction_LogById(Integer id){
		Malfunction_LogDataImpl.deleteMalfunction_LogById(id);
		return "success";
	}
	
	/**
	 * 新增故障
	 * @return
	 */
	@RequestMapping("/insertMalfunction_Log")
	@ResponseBody
	public String insertMalfunction_Log(MalfunctionLogInfo MalfunctionLogInfo){
		Malfunction_LogDataImpl.insertMalfunction_Log(MalfunctionLogInfo);
		return "";
	}
	
	/**
	 * 修改故障信息
	 * @return
	 */
	@RequestMapping("/updateMalfunction_LogById")
	@ResponseBody
	public String updateMalfunction_LogById(MalfunctionLogInfo MalfunctionLogInfo){
		Malfunction_LogDataImpl.updateMalfunction_LogById(MalfunctionLogInfo);
		return "";
	}
	
	 /**
     * 导出报表
     * @return
     */
    @RequestMapping("/exportMalfunction_Log")
    @ResponseBody
    public void export(HttpServletRequest request,HttpServletResponse response,String type,String status,String beginTime,String endTime,String groupid) throws Exception {
           //获取数据
    	PageUtil<MalfunctionLogInfo> page=new PageUtil<MalfunctionLogInfo>();
    	Map<String,Object> params = new HashMap<String,Object>();
		params.put("type", type);    //故障类型
		params.put("status", status);   //故障状态
		params.put("groupid", groupid);   //班组
		if(beginTime!=null&&!beginTime.equals("")){  
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
			 Date date = simpleDateFormat.parse(beginTime);
			params.put("beginTime", Math.round(date.getTime()/1000));  //开始时间
		}
		if(endTime!=null&&!endTime.equals("")){
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
			 Date date = simpleDateFormat.parse(endTime);
			params.put("endTime", Math.round(date.getTime()/1000));   //结束时间
		}
		page.setParams(params);
		
    	PageUtil<MalfunctionLogInfo> pageData = Malfunction_LogDataImpl.report(page);
        //excel标题
        String[] title = {"序号","日期","故障类型","位置","描述","上报人","上报时间","维修人","支援人","使用物料","维修完成时间","处理状态","总耗时","故障来源"};

        //excel文件名
        String fileName = "故障统计报表"+System.currentTimeMillis()+".xls";

        //sheet名
         String sheetName = "故障统计报表";
         String[][] content = new String[pageData.getData().size()][];
         for (int i = 0; i < pageData.getData().size(); i++) {
        	  content[i] = new String[title.length];
        	  MalfunctionLogInfo obj = pageData.getData().get(i);
             content[i][0] = (i+1)+"";
             content[i][1] = obj.getMalfunctionInfo().getCreate_at1();
             
             content[i][2] = obj.getMalfunctionInfo().getType_name();

             content[i][3] = obj.getMalfunctionInfo().getLocation();
             content[i][4] = obj.getMalfunctionInfo().getQuestion();
             content[i][5] = obj.getMalfunctionInfo().getCreateUserInfo().getUser_name();
             content[i][6] = obj.getMalfunctionInfo().getCreate_at1();
             content[i][7] = obj.getMalfunctionInfo().getFixUserInfo().getUser_name();
             
              
            		 String sup="";
            		 if(obj.getSupportInfoList()!=null){
            		 for(int j = 0; j<obj.getSupportInfoList().size(); j++){
            			 if(obj.getSupportInfoList().get(j).getUserInfoList()!=null){
            			 for(int k = 0; k<obj.getSupportInfoList().get(j).getUserInfoList().size(); k++){
            				 sup+=obj.getSupportInfoList().get(j).getUserInfoList().get(k).getUser_name()+",";
            			 }
            			 
            		 }}}
            		 content[i][8] =sup;
            		 
            		 
            		 String mat="";
            		 for(int l=0;l<obj.getMaterialList().size();l++){
            			 mat+=obj.getMaterialList().get(l).getName()+obj.getMaterialList().get(l).getNumber()+',';
            			 }
         			content[i][9] =mat;
             
             content[i][10] = obj.getFixTime();
             
             if(obj.getStatus()==1){
            	 content[i][11]="未指派";
             }
             if(obj.getStatus()==2){
            	 content[i][11]="已指派";
             }
             if(obj.getStatus()==3){
            	 content[i][11]="已接受";
             }
             if(obj.getStatus()==4){
            	 content[i][11]="维修失败";
             }
             if(obj.getStatus()==5){
            	 content[i][11]="维修成功";
             }
             if(obj.getStatus()==6){
            	 content[i][11]="已通过审核";
             }
             if(obj.getConTime()==0){
            	 content[i][12]="无";
             }else{
            	 content[i][12] = obj.getConTime()+"天";
             }
             
             if(obj.getMalfunctionInfo().getSource()==1){
            	 content[i][13] = "员工巡检发现";
             }
             if(obj.getMalfunctionInfo().getSource()==2){
            	 content[i][13] = "通过故障模块上报";
             }
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
                // TODO Auto-generated catch block
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
	
    
	//==========================================故障上报=======================================================
    
    /**
	 * 获取所有故障上报
	 * @param
	 * @return
	 * @throws ParseException 
	 */
	@RequestMapping("/getAllMalfunctionReport")
	public String  getAllMalfunctionReport(@RequestParam Map<String, Object> map,Model model,HttpSession session){
		
		model.addAttribute("page",MalfunctionDataImpl.getAllMalfunctionReport(map,session)); 
		return "ylyyPag/malfunctionPag/MalfunctionReport_list";
	}
    
	
	@RequestMapping("/goAddMalfunctionReport")
	public String  goAddMalfunctionReport(@RequestParam Map<String, Object> map,Model model,HttpSession session){
		Map<String,Object> adminMap= JSONObject.parseObject(String.valueOf(session.getAttribute("activeAdmin")));
		map.put("company_id", adminMap.get("company_id"));
		map.put("department_id", adminMap.get("department_id"));
		List<Map<String, Object>> allMalfunctionType = MalfunctionDataImpl.getAllMalfunctionType(map);
		 List<Map<String, Object>> allDepartment = systemDataImpl.getAllDepartment(map);
		map.put("departmentList", allDepartment);
		map.put("type_list", allMalfunctionType);
		
		model.addAttribute("data",map);
		model.addAttribute("token", QiniuUpload.getUpToken2());
		model.addAttribute("prefix", UrlConfig.FILE_PREFIX);
		model.addAttribute("QiniuUrl", UrlConfig.QiniuUrl);
		return "ylyyPag/malfunctionPag/MalfunctionReport_add";
	}
	
	
	
	@RequestMapping("/addFile")
	@ResponseBody
	public Map<String, Object>  addFile(@RequestParam Map<String, Object> map,HttpSession session,MultipartFile file){
		Map<String,Object> adminMap= JSONObject.parseObject(String.valueOf(session.getAttribute("activeAdmin")));
		map.put("create_by", adminMap.get("id"));
		return CommonDataServiceImpl.addFile(map,file);
	}
	
	
	@RequestMapping("/insertMalfunctionReport")
	@ResponseBody
	public int  insertMalfunctionReport(@RequestParam Map<String, Object> map,HttpSession session){
		Map<String,Object> adminMap= JSONObject.parseObject(String.valueOf(session.getAttribute("activeAdmin")));
		map.put("create_by", adminMap.get("id"));
		return MalfunctionDataImpl.insertMalfunctionReport(map);
	}


	@RequestMapping("/getMalfunctionTypeByDepartment")
	@ResponseBody
	public List<Map<String,Object> >  getMalfunctionTypeByDepartment(@RequestParam Map<String, Object> map){
		return MalfunctionDataImpl.getAllMalfunctionType(map);
	}
	
}
	
