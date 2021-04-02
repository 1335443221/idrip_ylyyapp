package com.sl.ylyy.manager.service.impl;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sl.ylyy.common.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.sl.ylyy.common.config.UrlConfig;
import com.sl.ylyy.manager.dao.MaterialDao;
import com.sl.ylyy.manager.service.MaterialDataService;


@Service("MaterialDataImpl")
public class MaterialDataServiceImpl implements MaterialDataService {

	
	@Autowired
	private MaterialDao materialDao;
	
	@Autowired
	private UrlConfig  urlConfig;
	

	
	/**
	 * 增加预约物料
	 */
	@Override
	public Object addMaterialAppointment(String json) {
		Map<String, Object> map= new HashMap<String, Object>();
		
		Map<String,Object> appData=JSONObject.parseObject(json); //解析数据
		double d1=Double.parseDouble(appData.get("user_id").toString());
    	Double D1=new Double(d1); 
    	int i1=D1.intValue(); 
    	map.put("uid",i1);
    	
		double d2=Double.parseDouble(appData.get("mid").toString());
    	Double D2=new Double(d2); 
    	int i2=D2.intValue(); 
    	map.put("mid",i2);
    	
		double d3=Double.parseDouble(appData.get("skid").toString());
    	Double D3=new Double(d3); 
    	int i3=D3.intValue(); 
    	map.put("skid",i3);
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
        String d=dateFormat.format(new Date());
        map.put("create_at",d);
       
        JSONArray appList=JSONArray.parseArray(appData.get("material_list").toString());  //解析数据集合
		for(int i=0;i<appList.size();i++){
			Map<String,Object> appData2=appList.getJSONObject(i);
				map.put("number",appData2.get("count"));
				map.put("name", appData2.get("name"));
			materialDao.insertMaterialAppointment(map);
		}
		
		
		//推送
		String title="收到新的物料需求！";
    	List<String> Alias=new ArrayList<>();
    	Alias.add(UrlConfig.ENV_TEST);
    	Alias.add(i3+"");
    	Map<String, String> map5= new HashMap<String, String>();
    	map5.put("type", "material");
    	map5.put("detail", "");
    	int i=materialDao.getMaxIdFromMA();
    	map5.put("id",i+"");
    	int r=Jipush.sendByAliasList(title,map5,Alias);
		
    	
    	//推送表
    	Map<String, Object> map6= new HashMap<String, Object>();
    	map6.put("status",r);
    	map6.put("push_type",4);
    	map6.put("malfunction_id",i2);
    	map6.put("accept_type", 2);
    	map6.put("accept_staff",i3);
    	long ts = Math.round(new Date().getTime()/1000);
    	map6.put("push_at",ts);
    	materialDao.insertPushMaterial(map6);
		
		return CodeMsg.SUCCESS;
	}

	
	
	
	/**
	 * 维修完成
	 */
	@Override
	public Object addMaterialComplete(String json) {
		Map<String, Object> map= new HashMap<String, Object>();
		Map<String, Object> map2= new HashMap<String, Object>();
		Map<String, Object> map3= new HashMap<String, Object>();
		Map<String, Object> map4= new HashMap<String, Object>();

		Map<String,Object> appData=JSONObject.parseObject(json); //解析数据
		double d1=Double.parseDouble(appData.get("user_id").toString());
    	Double D1=new Double(d1); 
    	int i1=D1.intValue(); 
    	map.put("uid",i1);
    	
		double d2=Double.parseDouble(appData.get("mid").toString());
    	Double D2=new Double(d2); 
    	int i2=D2.intValue(); 
    	map.put("mid",i2);
    	int result=0;
    	//修改故障表
    	map2.put("finish_desc",appData.get("image_finish_desc"));
    	map2.put("mid", i2);
    	map2.put("status", 5);
    	long ts = Math.round(new Date().getTime()/1000);
    	map2.put("fix_at", ts);
    	result=materialDao.updateMalfunction(map2);
    	if(result==0){
    		return CodeMsg.OPERATE_ERROR;
    	}
    	
    	//故障日志表
    	map3.put("fix_at",ts);
    	map3.put("mid", i2);
    	map3.put("status", 5);
    	map3.put("fix_by", i1);
    	materialDao.updateMalfunctionLog(map3);
    	
    	//支援表
    	map4.put("m_status", 2);
    	map4.put("mid", i2);
    	materialDao.updateSupport(map4);
    	
    	
    	if(appData.get("material_list")!=null&&appData.get("out_bound_date")!=null){
    		
    		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); 
        	Date date = null;
        	try {
    			date = dateFormat.parse(appData.get("out_bound_date").toString());
    		} catch (ParseException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
        	
        	String out_bound_date=dateFormat.format(date);
        	
        	map.put("out_bound_date",out_bound_date);
        	JSONArray appList=JSONArray.parseArray(appData.get("material_list").toString());  //解析数据集合
    		for(int i=0;i<appList.size();i++){
    			Map<String,Object> appData2=appList.getJSONObject(i);
    				map.put("number",appData2.get("count"));
    				map.put("name", appData2.get("name"));
    			materialDao.insertMaterialComplete(map);
    		}
    		
    	}
		
		return CodeMsg.SUCCESS;
	}
	/**
	 * 维修完成
	 */
	@Override
	public Object addMaterialComplete2(String json) {
		Map<String, Object> map= new HashMap<String, Object>();
		Map<String, Object> map2= new HashMap<String, Object>();
		Map<String, Object> map3= new HashMap<String, Object>();
		Map<String, Object> map4= new HashMap<String, Object>();

		Map<String,Object> appData=JSONObject.parseObject(json); //解析数据
		double d1=Double.parseDouble(appData.get("user_id").toString());
    	Double D1=new Double(d1);
    	int i1=D1.intValue();
    	map.put("uid",i1);

		double d2=Double.parseDouble(appData.get("mid").toString());
    	Double D2=new Double(d2);
    	int i2=D2.intValue();
    	map.put("mid",i2);
    	int result=0;
    	//修改故障表

		String imageresult="";
		if(appData.get("image_finish_desc")!=null){
			List fileList=JSONObject.parseArray(String.valueOf(appData.get("image_finish_desc")));
			imageresult=CommonDataServiceImpl.insertFile(fileList,"image_desc",i1);
		}
    	map2.put("finish_desc",imageresult);
    	map2.put("mid", i2);
    	map2.put("status", 5);
    	long ts = Math.round(new Date().getTime()/1000);
    	map2.put("fix_at", ts);
    	result=materialDao.updateMalfunction(map2);
    	if(result==0){
    		return CodeMsg.OPERATE_ERROR;
    	}

    	//故障日志表
    	map3.put("fix_at",ts);
    	map3.put("mid", i2);
    	map3.put("status", 5);
    	map3.put("fix_by", i1);
    	materialDao.updateMalfunctionLog(map3);

    	//支援表
    	map4.put("m_status", 2);
    	map4.put("mid", i2);
    	materialDao.updateSupport(map4);


    	if(appData.get("material_list")!=null&&appData.get("out_bound_date")!=null){

    		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        	Date date = null;
        	try {
    			date = dateFormat.parse(appData.get("out_bound_date").toString());
    		} catch (ParseException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}

        	String out_bound_date=dateFormat.format(date);

        	map.put("out_bound_date",out_bound_date);
        	JSONArray appList=JSONArray.parseArray(appData.get("material_list").toString());  //解析数据集合
    		for(int i=0;i<appList.size();i++){
    			Map<String,Object> appData2=appList.getJSONObject(i);
    				map.put("number",appData2.get("count"));
    				map.put("name", appData2.get("name"));
    			materialDao.insertMaterialComplete(map);
    		}

    	}

		return CodeMsg.SUCCESS;
	}

	
	/**
	 * 所有物料
	 */
	@Override
	public Object getMaterialLog(Map<String, Object> map) {
		Map<String, Object> map2= new HashMap<String, Object>();
		int gid;
		int uid;
		if(map.get("gid")==null){
			gid=0;uid=0;
		}else{
			gid=Integer.parseInt(map.get("gid").toString());
			if(map.get("uid")==null){
				uid=0;
			}else{
				uid=Integer.parseInt(map.get("uid").toString());
			}
		}
		
		
		
		if(gid!=0){
			if(uid==0){
				map2.put("gid",gid);
			}else{
				map2.put("uid",uid);
			}
		}
		
		if(map.get("date")!=null){
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM"); 
			String date=null;
			try {
				Date date1=dateFormat.parse(map.get("date").toString());
				date=dateFormat.format(date1);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			map2.put("beginTime",date+"-01");
			map2.put("endTime", date+"-31");
		}else{
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM"); 
			String date=dateFormat.format(new Date());
			map2.put("beginTime",date+"-01");
			map2.put("endTime", date+"-31");
		}
		
		
		List<Map<String, Object>> list=materialDao.getMaterialLog(map2);
		
		return Result.success(list);
	}

	/**
	 * web 端分页
	 */
	@Override
	public PageUtil<Map<String, Object>> findPage(PageUtil<Map<String, Object>> page) {
		PageHelper.startPage(page.getPageindex(),page.getPagesize());    //分页
		List<Map<String, Object>> list=materialDao.findPage(page);  //分页之后的集合
		
		
		PageUtil<Map<String, Object>> pageInfo=new PageUtil<Map<String, Object>>();  //存到page里
		pageInfo.setData(list);   //把查询结果存到page里
		pageInfo.setPagesize(page.getPagesize());
		pageInfo.setRecordCount(materialDao.findPage(page).size());   //总记录数
		return pageInfo;
	}



	/**
	 * 推送物料
	 */
	@Override
	public Object materiallist_tokeeper(Map<String, Object> map) {
		if(map.get("uid")==null){
			return CodeMsg.MISSING_PARAMETER;
		}
		Map<String, Object> map2= new HashMap<String, Object>();
		Map<String, Object> map3= new HashMap<String, Object>();
		Map<String, Object> map4= new HashMap<String, Object>();
		List<Map<String, Object>> list3=new ArrayList<>();
		
		map2.put("skid", map.get("uid"));
		List<Map<String, Object>> list=materialDao.materiallist_tokeeper(map2);
		
		for(int i=0;i<list.size();i++){
			map3= new HashMap<String, Object>();
			map3.put("create_at", list.get(i).get("create_at"));
			map3.put("create_by", list.get(i).get("create_by"));
			map3.put("skid", map.get("uid"));
			List<Map<String, Object>> list2=new ArrayList<>();
			list2=materialDao.materiallist_tokeeper2(map3);
			map4= new HashMap<String, Object>();
			map4.put("create_by", list.get(i).get("user"));
			map4.put("create_at", list.get(i).get("create_at"));
			map4.put("material_list", list2);
			list3.add(map4);
		}
		
		return Result.success(list3);
	}
	

	
	

}
