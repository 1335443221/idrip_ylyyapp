package com.sl.ylyy.manager.service.impl;


import java.text.SimpleDateFormat;
import java.util.*;
import javax.servlet.http.HttpSession;

import com.alibaba.fastjson.JSONArray;
import com.sl.ylyy.common.utils.DateUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.sl.ylyy.manager.dao.CommonDao;
import com.sl.ylyy.manager.dao.ManageDao;
import com.sl.ylyy.manager.dao.SupervisionDao;
import com.sl.ylyy.manager.service.WebSupervisionDataService;
import com.sl.ylyy.common.utils.PageUtil;


@Service("WebSupervisionDataImpl")
public class WebSupervisionDataServiceImpl implements WebSupervisionDataService {

	@Autowired
	private SupervisionDao SupervisionDao;
	@Autowired
	private ManageDao ManageDao;
	@Autowired
	private CommonDao commonDao;
	@Autowired
	private DateUtil dateUtil;

	/**
	 * 施工监护列表
	 */
	@Override
	public PageUtil<Map<String, Object>> web_supervision_list(Map<String, Object> map) {
		PageUtil<Map<String, Object>> pageInfo=new PageUtil<Map<String, Object>>();
		int pageindex =Integer.parseInt(String.valueOf(map.get("pageindex")));  //页码
		PageHelper.startPage(pageindex,pageInfo.getPagesize());  //分页
		List<Map<String,Object>> list=SupervisionDao.web_supervision_list(map);  //分页后的数据
		
		List<Map<String,Object>> type_data=ManageDao.project_type_list(map);  //type数据
		
		map.put("type_data", type_data);
		pageInfo.setData(list);  //把数据存到page中
		
		pageInfo.setRecordCount(SupervisionDao.web_supervision_list(map).size());  //总记录数
		pageInfo.setPageindex(pageindex);  //返回当前页
		map.put("lastpage", pageInfo.getLastPage());  //上一页
		map.put("nextpage", pageInfo.getNextpage(pageInfo.getTotalPagecount()));  //下一页
		pageInfo.setParams(map);  //返回模糊查询信息
		return pageInfo;
	}

	 /**
     * 施工监护时间类型列表
     */
	@Override
	public List<Map<String,Object>> supervision_time_list(Map<String, Object> map) {
		return SupervisionDao.supervision_time_list(map);
	}

	
	/**
     * 施工监护工程列表
     */
	@Override
	public List<Map<String,Object>> supervision_project_list(Map<String, Object> map) {
		return SupervisionDao.supervision_project_list(map);
	}
	
	
	
	/**
	 * app施工监护新增
	 */
	@Override
	public int add_supervision(Map<String, Object> map) {
		int result=SupervisionDao.add_supervision(map);
			return result;
	}
	
	

	/**
	 * app施工监护上报模板列表
	 */
	@Override
	public List<Map<String,Object>> supervision_model_list(Map<String, Object> map) {
		return SupervisionDao.supervision_model_list(map);
	}
	
	
	/**
	 * app施工监护上报模板内容列表
	 */
	@Override
	public List<Map<String,Object>> supervision_model_item_list(Map<String, Object> map) {
		return SupervisionDao.supervision_model_item_list(map);
	}

	
	/**
	 * 施工监护信息
	 */
	@Override
	public Map<String, Object> web_supervision_data_ById(Map<String, Object> map) {
		Map<String, Object> dataMap=new HashMap<>();
		Map<String, Object> modelMap2=new HashMap<>();
		Map<String, Object> supervision_data= SupervisionDao.web_supervision_data_ById(map);
		dataMap.put("supervision", supervision_data);
		modelMap2.put("supervision_model_id", supervision_data.get("supervision_model_id"));
		dataMap.put("model", SupervisionDao.supervision_model_item_list(modelMap2));
		return dataMap;
	}
	
	/**
	 * 施工监护历史
	 */
	@Override
	public PageUtil<Map<String, Object>> web_supervision_history(Map<String, Object> map) {
		PageUtil<Map<String, Object>> pageInfo=new PageUtil<Map<String, Object>>();
		int pageindex =Integer.parseInt(String.valueOf(map.get("pageindex")));  //页码
		PageHelper.startPage(pageindex,pageInfo.getPagesize());  //分页
		List<Map<String,Object>> list=SupervisionDao.web_supervision_history(map);  //分页后的数据
		
		List<Map<String,Object>> type_data=ManageDao.project_type_list(map);  //type数据
		
		
		for(int i=0;i<list.size();i++){
			int total_count=Integer.parseInt(String.valueOf(list.get(i).get("total_count")));
			int time_count=1;
			if(String.valueOf(list.get(i).get("time_type")).equals("1")){ //每班多少次 
				time_count=Integer.parseInt(String.valueOf(list.get(i).get("time_count")));
			}else{
				time_count=Integer.parseInt(String.valueOf(list.get(i).get("time_count")));
				time_count=12/time_count;
			}
			int days=total_count%time_count==0?total_count/time_count:total_count/time_count+1;
			list.get(i).put("days", days);  //监护了几天
			list.get(i).put("model_name", String.valueOf(list.get(i).get("model_name")).replace("模板",""));
		}
		
		map.put("type_data", type_data);
		pageInfo.setData(list);  //把数据存到page中
		
		pageInfo.setRecordCount(SupervisionDao.web_supervision_history(map).size());  //总记录数
		pageInfo.setPageindex(pageindex);  //返回当前页
		map.put("lastpage", pageInfo.getLastPage());  //上一页
		map.put("nextpage", pageInfo.getNextpage(pageInfo.getTotalPagecount()));  //下一页
		pageInfo.setParams(map);  //返回模糊查询信息
		return pageInfo;
	}


	/**
	 * 施工中 上报信息
	 */
	@Override
	public Map<String, Object> web_working_data_ById(Map<String, Object> map) {
		Map<String, Object> _data=ManageDao.getProjectById(map);  //项目信息
		String time=dateUtil.parseDateToStr(new Date(),dateUtil.DATE_FORMAT_YYYY_MM_DD);
		String beginTime=time+" 02:00:00";
		String endTime=dateUtil.getLastDayOfTime(beginTime,dateUtil.DATE_FORMAT_YYYY_MM_DD_HH_MI_SS);
		map.put("beginTime",beginTime);
		map.put("endTime",endTime);
		List<Map<String, Object>> supervision_list=new ArrayList<>();
		List<Map<String, Object>> supervision_model_list=SupervisionDao.supervision_model_list(map);
			for(int i=0;i<supervision_model_list.size();i++){
				Map<String, Object> supervision_data=new HashMap<String, Object>(); //上报信息
				map.put("department_id",supervision_model_list.get(i).get("department_id"));
				List<Map<String,Object>> list=SupervisionDao.supervision_listByParams(map);
				List<Map<String, Object>> item_list = SupervisionDao.supervision_model_item_list(map);
				List<Map<String,Object>> report_data=new ArrayList<>();
				if(list.size()==0){
					Map<String,Object> department=SupervisionDao.getDepartmentById(map);
					supervision_data.put("department",department.get("dname"));
					item_list=getReportData(item_list,report_data);
				}else {
					supervision_data.put("department",list.get(0).get("dname"));
					//监护记录
					for(int j=0;j<list.size();j++){
						map.put("supervision_id",list.get(j).get("id"));
						report_data= SupervisionDao.supervision_report_data(map);
						item_list=getReportData(item_list,report_data);
					}
				}
				supervision_data.put("department",supervision_model_list.get(i).get("department_name")); //部门
				supervision_data.put("create_at",time); //时间
				supervision_data.put("report_data",item_list);
				supervision_list.add(supervision_data);
			}
		_data.put("supervision_list",supervision_list);
		return _data;
	}



	/**
	 * 施工监护上报信息
	 */
	@Override
	public Map<String, Object> web_report_data_ById(Map<String, Object> map) {
		Map<String, Object> supervision= SupervisionDao.web_supervision_data_ById(map);
		String time=dateUtil.parseDateToStr(new Date(),dateUtil.DATE_FORMAT_YYYY_MM_DD);
		String beginTime=time+" 02:00:00";
		String endTime=dateUtil.getLastDayOfTime(beginTime,dateUtil.DATE_FORMAT_YYYY_MM_DD_HH_MI_SS);
		supervision.put("create_at",time); //时间
			map.remove("create_at");
			map.put("supervision_model_id", supervision.get("supervision_model_id"));
			map.put("beginTime",beginTime);
			map.put("endTime",endTime);
			List<Map<String,Object>> report_data= SupervisionDao.supervision_report_data(map); //最新正常上报记录
			List<Map<String, Object>> item_list = SupervisionDao.supervision_model_item_list(map);
			item_list=getReportData(item_list,report_data);
			supervision.put("report_data", item_list);
		return supervision;
	}



	/**
	 * 获取监护上报信息
	 */
	public List<Map<String,Object>> getReportData(List<Map<String,Object>> item_list,List<Map<String,Object>> report_data) {
		for(int i=0;i<item_list.size();i++){
			if(item_list.get(i).get("content")==null){
				item_list.get(i).put("content",new ArrayList<>());
			}
		}
		List<Map<String,Object>> content=new ArrayList<>();
		List<Map<String,Object>> content2=new ArrayList<>();
		for(int i=0;i<item_list.size();i++){
			content=new ArrayList<>();
			for(int j=0;j<report_data.size();j++){
				if(item_list.get(i).get("item_id")==report_data.get(j).get("item_id")){
					content.addAll(commonDao.getFileDescById(Arrays.asList(report_data.get(j).get("content").toString().split(","))));
				}
			}
			content2=(List<Map<String,Object>>)item_list.get(i).get("content");
			content2.addAll(content);
			item_list.get(i).put("content",content2);

		}
		return item_list;
	}













	/**
	 * 去补录施工监护页面
	 */
	@Override
	public Map<String, Object> goSupplementReport(Map<String, Object> map) {
		Map<String, Object> supervision= SupervisionDao.web_supervision_data_ById(map);
		map.put("supervision_model_id", supervision.get("supervision_model_id"));
		List<Map<String, Object>> supervision_model_item_list = SupervisionDao.supervision_model_item_list(map);
		supervision.put("item_data", supervision_model_item_list);
		return supervision;
	}

	/**
	 * 加载更多监护记录
	 */
	@Override
	public Map<String, Object> web_load_more_report(Map<String, Object> map) {
		Map<String, Object> supervision_data=new HashMap<>();
		String time=String.valueOf(map.get("create_at"));
		String beginTime=time+" 02:00:00";
		String endTime=dateUtil.getLastDayOfTime(beginTime,dateUtil.DATE_FORMAT_YYYY_MM_DD_HH_MI_SS);
		map.put("beginTime",beginTime);
		map.put("endTime",endTime);
		map.remove("create_at");
		if(map.get("supervision_id")!=null){
			Map<String, Object> supervision=SupervisionDao.web_supervision_data_ById(map);
			SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
			try {
				if (!isEffectiveDate(sdf2.parse(time), sdf2.parse(String.valueOf(supervision.get("begin_time"))), new Date())) {
					supervision_data.put("is_true",0);
					return supervision_data;
				}
			}catch (Exception e){

			}

			supervision_data.put("department",supervision.get("dname")); //部门
			supervision_data.put("supervision_id",supervision.get("supervision_id")); //施工监护id
			supervision_data.put("project_id",supervision.get("project_id")); //项目id
			supervision_data.put("create_at",time);
			map.put("supervision_model_id", supervision.get("supervision_model_id"));
			List<Map<String, Object>> item_list = SupervisionDao.supervision_model_item_list(map);
			List<Map<String,Object>> report_data= SupervisionDao.supervision_report_data(map);
			item_list=getReportData(item_list,report_data);
			supervision_data.put("report_data",item_list);
		}else if(map.get("project_id")!=null){
			List<Map<String, Object>> supervision_list=new ArrayList<>();
			List<Map<String, Object>> supervision_model_list=SupervisionDao.supervision_model_list(map);
			for(int i=0;i<supervision_model_list.size();i++){
				Map<String, Object> supervision=new HashMap<String, Object>(); //上报信息
				map.put("department_id",supervision_model_list.get(i).get("department_id"));
				List<Map<String,Object>> list=SupervisionDao.supervision_listByParams(map);
				List<Map<String, Object>> item_list = SupervisionDao.supervision_model_item_list(map);
				List<Map<String,Object>> report_data=new ArrayList<>();
				if(list.size()==0){
					Map<String,Object> department=SupervisionDao.getDepartmentById(map);
					supervision.put("department",department.get("dname"));
					item_list=getReportData(item_list,report_data);
				}else {
					supervision.put("department",list.get(0).get("dname"));
					//监护记录
					for(int j=0;j<list.size();j++){
						map.put("supervision_id",list.get(j).get("id"));
						report_data= SupervisionDao.supervision_report_data(map);
						item_list=getReportData(item_list,report_data);
					}
				}
				supervision.put("department",supervision_model_list.get(i).get("department_name")); //部门
				supervision.put("create_at",time); //时间
				supervision.put("report_data",item_list);
				supervision_list.add(supervision);
			}
			supervision_data.put("supervision_list",supervision_list);
		}
		return supervision_data;
	}

	/**
	 * 是否完成监护
	 */
	@Override
	public int web_check_report(Map<String, Object> map) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
		Map<String,Object> supervision_data=SupervisionDao.web_supervision_list(map).get(0);
		Map<String,Object> parmes_map=new HashMap<>();
		Date create_at=null;
		String day=null;
		String start=null;
		String end=null;
		int count =0;
		List<Map<String, Object>> list=new ArrayList<>();

		if("1".equals(String.valueOf(supervision_data.get("time_type")))){  //次/班
			count=Integer.parseInt(String.valueOf(supervision_data.get("time_count")));
		}else{    //每几小时
			count=Integer.parseInt(String.valueOf(supervision_data.get("time_count")));
			count=12/count;
		}
		try {
			if(!isEffectiveDate(sdf2.parse(String.valueOf(map.get("create_at"))),sdf2.parse(String.valueOf(supervision_data.get("begin_time"))),new Date())){
				return -1;
			}
			create_at=sdf2.parse(String.valueOf(map.get("create_at")));
			day=sdf2.format(create_at);
			start=day+" 04:00:00";
			end=day+" 23:59:59";
			parmes_map.put("beginTime",start);
			parmes_map.put("endTime",end);
			parmes_map.put("supervision_id",map.get("supervision_id"));
			list=SupervisionDao.web_check_report(parmes_map);
			//完成
			if (list.size()>=count){
				return 0;
			}
		}catch (Exception e){

		}
		return count-list.size();

	}


	/**
	 * 监护上报新增
	 */
	@Override
	public int add_supervision_report(String json) {
		Map<String,Object> appData=JSONObject.parseObject(json); //解析数据
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
		Map<String,Object> parmas=new HashMap<>();
		parmas.put("supervision_id",appData.get("supervision_id"));
		Map<String,Object> supervision=SupervisionDao.web_supervision_data_ById(parmas);
		try {
			if(!isEffectiveDate(sdf2.parse(String.valueOf(appData.get("create_at"))),sdf2.parse(String.valueOf(supervision.get("begin_time"))),sdf2.parse(sdf2.format(new Date())))){
				return -1;
			}
		}catch (Exception e){

		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		int result=0;
		Date date=null;
		if(sdf2.format(new Date()).equals(String.valueOf(appData.get("create_at")))) {
			date=new Date();
		}else{
			date=randomDate(appData.get("create_at")+" 08:00:00",appData.get("create_at")+" 20:00:00");
		}
		JSONArray report_data_list = JSONArray.parseArray(appData.get("report_data").toString()); //上报信息集合
		String cteate_at=sdf.format(date);
		String supplement_at=sdf.format(new Date());
		for (int i = 0; i < report_data_list.size(); i++) {
			Map<String, Object> report_data = report_data_list.getJSONObject(i);  //一条上报信息
			List<Integer> contentList=report_data.get("content")!=null?CommonDataServiceImpl.fileStringToList(report_data.get("content").toString()):new ArrayList<Integer>();  //上报文件.
			report_data.put("content",StringUtils.strip(contentList.toString(),"[]"));
			report_data.put("project_id", appData.get("project_id"));
			report_data.put("supervision_id", appData.get("supervision_id"));
			report_data.put("create_by",appData.get("create_by"));
			report_data.put("cteate_at",cteate_at);
			report_data.put("is_supplement",1);  //表示为补录信息
			report_data.put("supplement_at",supplement_at);
			result=SupervisionDao.add_supervision_report(report_data); //添加一条上报项
		}
		return result;
	}


		private  Date randomDate(String beginDate,String  endDate){
		try {
			SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date start = format.parse(beginDate);//构造开始日期
			Date end = format.parse(endDate);//构造结束日期
			//getTime()表示返回自 1970 年 1 月 1 日 00:00:00 GMT 以来此 Date 对象表示的毫秒数。
			if(start.getTime() >= end.getTime()){
				return null;
			}
			long date = random(start.getTime(),end.getTime());
			return new Date(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	private static long random(long begin,long end){
		long rtn = begin + (long)(Math.random() * (end - begin));
		//如果返回的是开始时间和结束时间，则递归调用本函数查找随机值
		if(rtn == begin || rtn == end){
			return random(begin,end);
		}
		return rtn;
	}

	/**
	 * 判断当前时间是否在[startTime, endTime]区间，注意时间格式要一致
	 *
	 * @param nowTime 当前时间
	 * @param startTime 开始时间
	 * @param endTime 结束时间
	 * @return
	 * @author jqlin
	 */
	public static boolean isEffectiveDate(Date nowTime, Date startTime, Date endTime) {
		if (nowTime.getTime() == startTime.getTime()
				|| nowTime.getTime() == endTime.getTime()) {
			return true;
		}
		Calendar date = Calendar.getInstance();
		date.setTime(nowTime);
		Calendar begin = Calendar.getInstance();
		begin.setTime(startTime);
		Calendar end = Calendar.getInstance();
		end.setTime(endTime);
		if (date.after(begin) && date.before(end)) {
			return true;
		} else {
			return false;
		}
	}

















	/**
	 * 问题整改列表
	 */
	@Override
	public PageUtil<Map<String, Object>> web_abarbeitung_list(Map<String, Object> map,HttpSession session) {
		PageUtil<Map<String, Object>> pageInfo=new PageUtil<Map<String, Object>>();
		int pageindex =Integer.parseInt(String.valueOf(map.get("pageindex")));  //页码
		PageHelper.startPage(pageindex,pageInfo.getPagesize());  //分页
		List<Map<String,Object>> list=SupervisionDao.web_abarbeitung_list(map);  //分页后的数据

		
		List<Map<String,Object>> type_data=ManageDao.project_type_list(map);  //type数据
		Map<String,Object> smap=JSONObject.parseObject(String.valueOf(session.getAttribute("activeAdmin")));
		List<Map<String, Object>> abarbeitung_list2=new ArrayList<>(); //未提交
		if(pageindex==1){  //第一页加上未提交的
		map.put("create_by", smap.get("id"));
		abarbeitung_list2=SupervisionDao.web_abarbeitung_list(map);
		map.remove("create_by");
		}
		pageInfo.setRecordCount(SupervisionDao.web_abarbeitung_list(map).size()+abarbeitung_list2.size());  //总记录数
		
		abarbeitung_list2.addAll(list);
		Map<String,Object> aData=new HashMap<>();
		for(int i=0;i<abarbeitung_list2.size();i++){  //查看是否有人上报
			if(String.valueOf(abarbeitung_list2.get(i).get("state")).equals("1")){  //进行中
				
				
				aData.put("abarbeitung_id", abarbeitung_list2.get(i).get("abarbeitung_id"));
				aData.put("save",1);
				List<Map<String, Object>> report_data = SupervisionDao.abarbeitung_report_data(aData);  //保存的上报列表
				if(report_data==null||report_data.size()==0){
					abarbeitung_list2.get(i).put("create_state",0);  //没有人上报
				}else{
					abarbeitung_list2.get(i).put("create_state",2);  //不是自己上报
				}
				
				for(int j=0;j<report_data.size();j++){  //遍历
					if(String.valueOf(report_data.get(j).get("create_by")).equals(String.valueOf(smap.get("id")))){  //当前人有保存状态
						abarbeitung_list2.get(i).put("create_state",1);  //当前登录人上报
						abarbeitung_list2.get(i).put("report_id",report_data.get(j).get("id"));  //上报id
					}
				}
				
			}else{
				abarbeitung_list2.get(i).put("create_state",3);  //已整改或者未提交
			}
		}
		
		
		map.put("type_data", type_data);
		pageInfo.setData(abarbeitung_list2);  //把数据存到page中
		
		pageInfo.setPageindex(pageindex);  //返回当前页
		map.put("lastpage", pageInfo.getLastPage());  //上一页
		map.put("nextpage", pageInfo.getNextpage(pageInfo.getTotalPagecount()));  //下一页
		pageInfo.setParams(map);  //返回模糊查询信息
		return pageInfo;
	}
	
	/**
	 * 新增问题整改
	 */
	@Override
	public int web_add_abarbeitung(String json) {
		Map<String,Object> appData=JSONObject.parseObject(json); //解析数据
        int state=Integer.parseInt(appData.get("state").toString());  //1保存 2提交
        if(state==1){ //保存
        	appData.put("state", "3");  //待提交状态
        }else{   //提交
        	appData.put("state", "1");  //提交后进入进行中状态
        }
        
        //把文件集合存到数据库  转换成 id集合
        List<Integer> fileList1=appData.get("site_photo")!=null?CommonDataServiceImpl.fileStringToList(appData.get("site_photo").toString()):new ArrayList<Integer>();
        List<Integer> fileList2=appData.get("sign")!=null?CommonDataServiceImpl.fileStringToList(appData.get("sign").toString()):new ArrayList<Integer>();
        List<Integer> fileList3=appData.get("else")!=null?CommonDataServiceImpl.fileStringToList(appData.get("else").toString()):new ArrayList<Integer>();
     
        //把list集合去掉[],转化成list存到数据库中
        appData.put("site_photo", StringUtils.strip(fileList1.toString(),"[]"));
		appData.put("sign", StringUtils.strip(fileList2.toString(),"[]"));
		appData.put("else", StringUtils.strip(fileList3.toString(),"[]"));
        
		int result=0;
		if(appData.get("abarbeitung_id")==null){  //没有信息 第一次添加  添加信息
			result=SupervisionDao.add_abarbeitung(appData);
		}else{
			result=SupervisionDao.update_abarbeitung(appData);  //有信息 修改
		}
        return result;
       
	}
	
	
	/**
	 * 问题整改通知单信息
	 */
	@Override
	public Map<String, Object> abarbeitung_data_ById(Map<String, Object> map) {
		Map<String, Object> abarbeitung_data = SupervisionDao.abarbeitung_data(map); //整改信息
		Map<String, Object> _data=new HashMap<>();
		if(abarbeitung_data!=null){
			map.put("project_id",abarbeitung_data.get("project_id"));
			_data=ManageDao.getProjectById(map);  //项目信息
			abarbeitung_data.put("site_photo", commonDao.getFileDescById(Arrays.asList(abarbeitung_data.get("site_photo").toString().split(",")))); //把XX转换为list
			abarbeitung_data.put("sign", commonDao.getFileDescById(Arrays.asList(abarbeitung_data.get("sign").toString().split(",")))); //把xx转换为list
			abarbeitung_data.put("else", commonDao.getFileDescById(Arrays.asList(abarbeitung_data.get("else").toString().split(",")))); //把xx转换为list

		}
		_data.put("abarbeitung_data", abarbeitung_data);
		
		return _data;
	}
	
	
	
	/**
	 * 问题报告信息
	 */
	@Override
	public Map<String, Object> abarbeitung_report_data_ById(Map<String, Object> map) {
		List<Map<String, Object>> report_data = SupervisionDao.abarbeitung_report_data(map); //整改信息
		Map<String, Object> abarbeitung_report_data=new HashMap<>();
		Map<String, Object> _data=ManageDao.getProjectById(map);  //项目信息
	if(report_data!=null&&report_data.size()!=0){
		abarbeitung_report_data=report_data.get(0);
		abarbeitung_report_data.put("site_photo", commonDao.getFileDescById(Arrays.asList(abarbeitung_report_data.get("site_photo").toString().split(",")))); //把XX转换为list
		abarbeitung_report_data.put("report", commonDao.getFileDescById(Arrays.asList(abarbeitung_report_data.get("report").toString().split(",")))); //把xx转换为list
		abarbeitung_report_data.put("else", commonDao.getFileDescById(Arrays.asList(abarbeitung_report_data.get("else").toString().split(",")))); //把xx转换为list
	}
		
		_data.put("report_data", abarbeitung_report_data);
		_data.put("param", map);
		return _data;
	}
	
	
	/**
	 * 新增问题整改报告
	 */
	@Override
	public int web_add_abarbeitung_report(String json) {
		Map<String,Object> appData=JSONObject.parseObject(json); //解析数据
        //把文件集合存到数据库  转换成 id集合
        List<Integer> fileList1=appData.get("site_photo")!=null?CommonDataServiceImpl.fileStringToList(appData.get("site_photo").toString()):new ArrayList<Integer>();
        List<Integer> fileList2=appData.get("report")!=null?CommonDataServiceImpl.fileStringToList(appData.get("report").toString()):new ArrayList<Integer>();
        List<Integer> fileList3=appData.get("else")!=null?CommonDataServiceImpl.fileStringToList(appData.get("else").toString()):new ArrayList<Integer>();
     
        //把list集合去掉[],转化成list存到数据库中
        appData.put("site_photo", StringUtils.strip(fileList1.toString(),"[]"));
		appData.put("report", StringUtils.strip(fileList2.toString(),"[]"));
		appData.put("else", StringUtils.strip(fileList3.toString(),"[]"));
        
		int result=0;
		if(appData.get("report_id")==null){  //没有信息 第一次添加  添加信息
			result=SupervisionDao.add_abarbeitung_report(appData);
		}else{
			result=SupervisionDao.update_abarbeitung_report(appData);  //有信息 修改
		}
		
		if(String.valueOf(appData.get("state")).equals("2")&&result!=0){  //提交
			Map<String,Object> aData=new HashMap<>();
			aData.put("abarbeitung_id", appData.get("abarbeitung_id"));
			aData.put("state", 2);  //已整改
			SupervisionDao.update_abarbeitung(aData);
			
			SupervisionDao.delete_save_abarbeitung_report(aData);  //删除保存的整改报告
		}
        return result;
       
	}
	
	
	/**
	 * 问题整改详情
	 */
	@Override
	public Map<String, Object> web_abarbeitung_detail(Map<String, Object> map) {
		Map<String, Object> abarbeitung_data = SupervisionDao.abarbeitung_data(map); //整改通知单信息
		Map<String, Object> _data=ManageDao.getProjectById(abarbeitung_data);  //项目信息
		
		abarbeitung_data.put("site_photo", commonDao.getFileDescById(Arrays.asList(String.valueOf(abarbeitung_data.get("site_photo")).split(",")))); //把XX转换为list
		abarbeitung_data.put("sign", commonDao.getFileDescById(Arrays.asList(String.valueOf(abarbeitung_data.get("sign")).split(",")))); //把xx转换为list
		abarbeitung_data.put("else", commonDao.getFileDescById(Arrays.asList(String.valueOf(abarbeitung_data.get("else")).split(",")))); //把xx转换为list
		
		map.put("submit", 1);
		List<Map<String, Object>> report_data = SupervisionDao.abarbeitung_report_data(map); //整改报告信息
		Map<String, Object> abarbeitung_report_data=new HashMap<String, Object>();
		if(report_data!=null&&report_data.size()!=0){
			abarbeitung_report_data=report_data.get(0);
			abarbeitung_report_data.put("site_photo", commonDao.getFileDescById(Arrays.asList(String.valueOf(abarbeitung_report_data.get("site_photo")).split(",")))); //把XX转换为list
			abarbeitung_report_data.put("report", commonDao.getFileDescById(Arrays.asList(String.valueOf(abarbeitung_report_data.get("report")).split(",")))); //把xx转换为list
			abarbeitung_report_data.put("else", commonDao.getFileDescById(Arrays.asList(String.valueOf(abarbeitung_report_data.get("else")).split(",")))); //把xx转换为list
		}
		
		_data.put("abarbeitung_data", abarbeitung_data);
		_data.put("report_data", abarbeitung_report_data);
		return _data;
	}
	
	
	/**
	 * 重要施工项列表
	 */
	@Override
	public PageUtil<Map<String, Object>> web_item_list(Map<String, Object> map,HttpSession session) {
		PageUtil<Map<String, Object>> pageInfo=new PageUtil<Map<String, Object>>();
		int pageindex =Integer.parseInt(String.valueOf(map.get("pageindex")));  //页码
		
		PageHelper.startPage(pageindex,pageInfo.getPagesize());  //分页
		List<Map<String,Object>> list=SupervisionDao.web_item_list(map);  //分页后的数据


		Map<String,Object> smap=JSONObject.parseObject(String.valueOf(session.getAttribute("activeAdmin")));
		List<Map<String, Object>> list2=new ArrayList<>(); //未提交
		if(pageindex==1){  //第一页加上未提交的
		map.put("create_by", smap.get("id"));
		list2=SupervisionDao.web_item_list(map);
		map.remove("create_by");
		}
		pageInfo.setRecordCount(SupervisionDao.web_item_list(map).size()+list2.size());  //总记录数
		
		list2.addAll(list);
		pageInfo.setData(list2);  //把数据存到page中
		
		map.put("type_data", SupervisionDao.item_type_list(map));
		pageInfo.setPageindex(pageindex);  //返回当前页
		map.put("lastpage", pageInfo.getLastPage());  //上一页
		map.put("nextpage", pageInfo.getNextpage(pageInfo.getTotalPagecount()));  //下一页
		pageInfo.setParams(map);  //返回模糊查询信息
		return pageInfo;
	}
	
	
	/**
	 * 重要施工项分类列表
	 */
	@Override
	public List<Map<String,Object>> item_type_list(Map<String, Object> map) {
		
		return SupervisionDao.item_type_list(map);
	}

	/**
	 * 证书列表
	 */
	@Override
	public List<Map<String,Object>> item_credential_list(Map<String, Object> map) {

		return SupervisionDao.item_credential_list(map);
	}
	
	
	

	/**
	 * 新增重要施工项
	 */
	@Override
	public int web_add_item(String json) {
		Map<String,Object> appData=JSONObject.parseObject(json); //解析数据
        //把文件集合存到数据库  转换成 id集合
        List<Integer> fileList1=appData.get("photo")!=null?CommonDataServiceImpl.fileStringToList(appData.get("photo").toString()):new ArrayList<Integer>();
        List<Integer> fileList3=appData.get("ossa")!=null?CommonDataServiceImpl.fileStringToList(appData.get("ossa").toString()):new ArrayList<Integer>();
        List<Integer> fileList4=appData.get("charge")!=null?CommonDataServiceImpl.fileStringToList(appData.get("charge").toString()):new ArrayList<Integer>();
        List<Integer> fileList5=appData.get("request")!=null?CommonDataServiceImpl.fileStringToList(appData.get("request").toString()):new ArrayList<Integer>();
        List<Integer> fileList6=appData.get("construction_before")!=null?CommonDataServiceImpl.fileStringToList(appData.get("construction_before").toString()):new ArrayList<Integer>();
        List<Integer> fileList7=appData.get("special_deal")!=null?CommonDataServiceImpl.fileStringToList(appData.get("special_deal").toString()):new ArrayList<Integer>();
        List<Integer> fileList8=appData.get("construction_after")!=null?CommonDataServiceImpl.fileStringToList(appData.get("construction_after").toString()):new ArrayList<Integer>();
        List<Integer> fileList9=appData.get("else")!=null?CommonDataServiceImpl.fileStringToList(appData.get("else").toString()):new ArrayList<Integer>();
     
        //把list集合去掉[],转化成list存到数据库中
        appData.put("photo", StringUtils.strip(fileList1.toString(),"[]"));
		appData.put("ossa", StringUtils.strip(fileList3.toString(),"[]"));
		appData.put("charge", StringUtils.strip(fileList4.toString(),"[]"));
		appData.put("request", StringUtils.strip(fileList5.toString(),"[]"));
		appData.put("construction_before", StringUtils.strip(fileList6.toString(),"[]"));
		appData.put("special_deal", StringUtils.strip(fileList7.toString(),"[]"));
		appData.put("construction_after", StringUtils.strip(fileList8.toString(),"[]"));
		appData.put("else", StringUtils.strip(fileList9.toString(),"[]"));
        
		if(appData.get("is_read")==null){
			appData.put("is_read", 0);
		}
		
		int result=0;
		if(appData.get("item_id")==null){  //没有信息 第一次添加  添加信息
			result=SupervisionDao.add_construction_item(appData);
		}else{
			result=SupervisionDao.update_construction_item(appData);  //有信息 修改
		}
		
		return result;
	}


	/**
	 * 重要施工项详情
	 */
	@Override
	public Map<String,Object> web_item_data(Map<String, Object> map) {
		
		Map<String,Object> item_Data=SupervisionDao.construction_item_data(map);
		Map<String, Object> _data=ManageDao.getProjectById(item_Data);  //项目信息
		
		item_Data.put("photo", commonDao.getFileDescById(Arrays.asList(item_Data.get("photo").toString().split(",")))); //把xx转换为list
    	item_Data.put("ossa", commonDao.getFileDescById(Arrays.asList(item_Data.get("ossa").toString().split(",")))); //把xx转换为list
    	item_Data.put("charge", commonDao.getFileDescById(Arrays.asList(item_Data.get("charge").toString().split(",")))); //把xx转换为list
    	item_Data.put("request", commonDao.getFileDescById(Arrays.asList(item_Data.get("request").toString().split(",")))); //把xx转换为list
    	item_Data.put("construction_before", commonDao.getFileDescById(Arrays.asList(item_Data.get("construction_before").toString().split(",")))); //把xx转换为list
    	item_Data.put("special_deal", commonDao.getFileDescById(Arrays.asList(item_Data.get("special_deal").toString().split(",")))); //把xx转换为list
    	item_Data.put("construction_after", commonDao.getFileDescById(Arrays.asList(item_Data.get("construction_after").toString().split(",")))); //把xx转换为list
    	item_Data.put("else", commonDao.getFileDescById(Arrays.asList(item_Data.get("else").toString().split(",")))); //把xx转换为list
		
    	int lastId=SupervisionDao.construction_item_lastId(map);  //上一个id
    	if(lastId!=0){  //有上一个
    		item_Data.put("is_and_more", 1);
    	}else{
    		item_Data.put("is_and_more", 0);
    	}
    	_data.put("item_Data", item_Data);
    	
    	return _data;
	}

	/**
	 * 加载更多重要施工项(历史施工项)
	 */
	@Override
	public Map<String, Object> web_load_more_item(Map<String, Object> map) {
		int lastId=SupervisionDao.construction_item_lastId(map);  //上一个id
		if(lastId!=0){ //有上一个
			map.put("item_id", lastId);
			Map<String,Object> item_Data=SupervisionDao.construction_item_data(map);
			item_Data.put("photo", commonDao.getFileDescById(Arrays.asList(item_Data.get("photo").toString().split(",")))); //把xx转换为list
	    	item_Data.put("ossa", commonDao.getFileDescById(Arrays.asList(item_Data.get("ossa").toString().split(",")))); //把xx转换为list
	    	item_Data.put("charge", commonDao.getFileDescById(Arrays.asList(item_Data.get("charge").toString().split(",")))); //把xx转换为list
	    	item_Data.put("request", commonDao.getFileDescById(Arrays.asList(item_Data.get("request").toString().split(",")))); //把xx转换为list
	    	item_Data.put("construction_before", commonDao.getFileDescById(Arrays.asList(item_Data.get("construction_before").toString().split(",")))); //把xx转换为list
	    	item_Data.put("special_deal", commonDao.getFileDescById(Arrays.asList(item_Data.get("special_deal").toString().split(",")))); //把xx转换为list
	    	item_Data.put("construction_after", commonDao.getFileDescById(Arrays.asList(item_Data.get("construction_after").toString().split(",")))); //把xx转换为list
	    	item_Data.put("else", commonDao.getFileDescById(Arrays.asList(item_Data.get("else").toString().split(",")))); //把xx转换为list
			
	    	
	    	lastId=SupervisionDao.construction_item_lastId(map);  //上一个id
	    	if(lastId!=0){  //有上一个
	    		item_Data.put("is_and_more", 1);
	    	}else{
	    		item_Data.put("is_and_more", 0);
	    	}
	    	return item_Data;
		}else{
			return null;
		}
	}
	
	
	
	
	
	
	/**
	 * 工程进度列表
	 */
	@Override
	public PageUtil<Map<String, Object>> web_schedule_list(Map<String, Object> map,HttpSession session) {
		PageUtil<Map<String, Object>> pageInfo=new PageUtil<Map<String, Object>>();
		int pageindex =Integer.parseInt(String.valueOf(map.get("pageindex")));  //页码
		
		PageHelper.startPage(pageindex,pageInfo.getPagesize());  //分页
		List<Map<String,Object>> list=SupervisionDao.web_schedule_list(map);  //分页后的数据


		Map<String,Object> smap=JSONObject.parseObject(String.valueOf(session.getAttribute("activeAdmin")));
		List<Map<String, Object>> list2=new ArrayList<>(); //未提交
		
		if(pageindex==1){  //第一页加上未提交的
		map.put("create_by", smap.get("id"));
		list2=SupervisionDao.web_schedule_list(map);
		map.remove("create_by");
		}
		pageInfo.setRecordCount(SupervisionDao.web_schedule_list(map).size()+list2.size());  //总记录数
		
		list2.addAll(list);
		
		
		for(int i=0;i<list2.size();i++){
			list2.get(i).put("schedule", commonDao.getFileDescById(Arrays.asList(list2.get(i).get("schedule").toString().split(",")))); //把xx转换为list
		}
		List<Map<String,Object>> type_data=ManageDao.project_type_list(map);  //type数据
		map.put("type_data", type_data);
		pageInfo.setData(list2);  //把数据存到page中
		
		pageInfo.setPageindex(pageindex);  //返回当前页
		map.put("lastpage", pageInfo.getLastPage());  //上一页
		map.put("nextpage", pageInfo.getNextpage(pageInfo.getTotalPagecount()));  //下一页
		pageInfo.setParams(map);  //返回模糊查询信息
		return pageInfo;
	}
	
	
	/**
	 * 添加工程进度
	 */
	@Override
	public int web_add_schedule(String json) {
		Map<String,Object> appData=JSONObject.parseObject(json); //解析数据
		Map<String,Object> idData=new HashMap<String,Object>(); //解析数据
        //把文件集合存到数据库  转换成 id集合
        List<Integer> fileList1=appData.get("meeting_summary")!=null?CommonDataServiceImpl.fileStringToList(appData.get("meeting_summary").toString()):new ArrayList<Integer>();
        List<Integer> fileList2=appData.get("site_photo")!=null?CommonDataServiceImpl.fileStringToList(appData.get("site_photo").toString()):new ArrayList<Integer>();
        List<Integer> fileList3=appData.get("schedule")!=null?CommonDataServiceImpl.fileStringToList(appData.get("schedule").toString()):new ArrayList<Integer>();
        List<Integer> fileList4=appData.get("else")!=null?CommonDataServiceImpl.fileStringToList(appData.get("else").toString()):new ArrayList<Integer>();
     
        //把list集合去掉[],转化成list存到数据库中
        appData.put("meeting_summary", StringUtils.strip(fileList1.toString(),"[]"));
		appData.put("site_photo", StringUtils.strip(fileList2.toString(),"[]"));
		appData.put("schedule", StringUtils.strip(fileList3.toString(),"[]"));
		appData.put("else", StringUtils.strip(fileList4.toString(),"[]"));
        
		int result=0;
		if(appData.get("schedule_id")==null){  //没有信息 第一次添加  添加信息
			result=SupervisionDao.add_schedule(appData);
		}else{
			result=SupervisionDao.update_schedule(appData);  //有信息 修改
		}
        return result;
	}
	
	
	
	/**
	 * 工程进度信息 project_id
	 */
	@Override
	public Map<String, Object> web_schedule_data(Map<String, Object> map) {
		
		Map<String, Object> schedule_data=SupervisionDao.schedule_data(map);
		Map<String, Object> _data=ManageDao.getProjectById(schedule_data);  //项目信息
		
		schedule_data.put("meeting_summary", commonDao.getFileDescById(Arrays.asList(schedule_data.get("meeting_summary").toString().split(",")))); //把xx转换为list
		schedule_data.put("site_photo", commonDao.getFileDescById(Arrays.asList(schedule_data.get("site_photo").toString().split(",")))); //把xx转换为list
		schedule_data.put("schedule", commonDao.getFileDescById(Arrays.asList(schedule_data.get("schedule").toString().split(",")))); //把xx转换为list
		schedule_data.put("else", commonDao.getFileDescById(Arrays.asList(schedule_data.get("else").toString().split(",")))); //把xx转换为list
		
		int id=Integer.parseInt(schedule_data.get("schedule_id").toString());
		Integer lastid=SupervisionDao.schedule_lastId(map);
      	if(lastid==null){
			lastid=0;
		}
		if(id==lastid){
			schedule_data.put("is_and_more", 0);
		}else{
			schedule_data.put("is_and_more", 1);
		}
		
		_data.put("schedule_data",schedule_data);
		return _data;
	}
	
	
	
	
	/**
	 * 加载更多计划进度
	 */
	@Override
	public Map<String, Object> web_load_more_schedule(Map<String, Object> map) {
		
		Map<String, Object> schedule_data=SupervisionDao.loadmore_schedule(map);
		if(schedule_data==null){
			return null;
		}
		schedule_data.put("meeting_summary", commonDao.getFileDescById(Arrays.asList(schedule_data.get("meeting_summary").toString().split(",")))); //把xx转换为list
		schedule_data.put("site_photo", commonDao.getFileDescById(Arrays.asList(schedule_data.get("site_photo").toString().split(",")))); //把xx转换为list
		schedule_data.put("schedule", commonDao.getFileDescById(Arrays.asList(schedule_data.get("schedule").toString().split(",")))); //把xx转换为list
		schedule_data.put("else", commonDao.getFileDescById(Arrays.asList(schedule_data.get("else").toString().split(",")))); //把xx转换为list
		
		int id=Integer.parseInt(schedule_data.get("schedule_id").toString());

		Integer lastid=SupervisionDao.schedule_lastId(map);
		if(lastid==null){
			lastid=0;
		}
		if(id==lastid){
			schedule_data.put("is_and_more", 0);
		}else{
			schedule_data.put("is_and_more", 1);
		}
		return schedule_data;
	}
	
	
	
	
	
	
	
	

}
