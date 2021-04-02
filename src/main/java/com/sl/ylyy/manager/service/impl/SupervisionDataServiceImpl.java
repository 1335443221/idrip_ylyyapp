package com.sl.ylyy.manager.service.impl;


import java.util.*;

import com.sl.ylyy.common.utils.DateUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.sl.ylyy.manager.dao.CommonDao;
import com.sl.ylyy.manager.dao.ManageDao;
import com.sl.ylyy.manager.dao.SupervisionDao;
import com.sl.ylyy.manager.service.SupervisionDataService;
import com.sl.ylyy.common.utils.CodeMsg;
import com.sl.ylyy.common.utils.Result;

import static com.sl.ylyy.common.utils.JwtToken.*;


@Service("SupervisionDataImpl")
public class SupervisionDataServiceImpl implements SupervisionDataService {

	@Autowired
	private SupervisionDao SupervisionDao;
	@Autowired
	private ManageDao ManageDao;
	@Autowired
	private CommonDao commonDao;
	@Autowired
	private DateUtil dateUtil;

	/**
	 * app施工监护列表
	 */
	@Override
	public Object supervision_list(Map<String, Object> map) {
		Map<String,Object> appData=new HashMap<String,Object>();
		int pageNum=map.get("pageNum")==null?1:Integer.parseInt(map.get("pageNum").toString());
		int pageSize=map.get("pageSize")==null?10:Integer.parseInt(map.get("pageSize").toString());
		
		PageHelper.startPage(pageNum,pageSize); 
		List<Map<String, Object>> supervision_list = SupervisionDao.supervision_list(map);

		Map<String,Object> suMap=new HashMap<>();
		String startTime=dateUtil.parseDateToStr(new Date(),dateUtil.DATE_FORMAT_YYYY_MM_DD)+" 02:00:00";
		String endTime=dateUtil.getLastDayOfTime(startTime,dateUtil.DATE_FORMAT_YYYY_MM_DD_HH_MI_SS);
		suMap.put("startTime",startTime);
		suMap.put("endTime",endTime);

		for(int i=0;i<supervision_list.size();i++){
			suMap.put("supervision_id",supervision_list.get(i).get("supervision_id"));
			supervision_list.get(i).put("timeList",SupervisionDao.getReportTimeBySupervision(suMap));
			if(String.valueOf(supervision_list.get(i).get("type")).equals("1")){  //次/班
				supervision_list.get(i).put("supervision_time",supervision_list.get(i).get("day_count")+"/"+supervision_list.get(i).get("count"));
				if(String.valueOf(supervision_list.get(i).get("day_count")).equals(String.valueOf(supervision_list.get(i).get("count")))){  //未完成
					supervision_list.get(i).put("supervision_state","已完成");
				}else{    //完成
					supervision_list.get(i).put("supervision_state","未完成");
				}
				supervision_list.get(i).remove("day_count");
			}else{    //每几小时
				int count=Integer.parseInt(String.valueOf(supervision_list.get(i).get("count")));
				int count2=12/count;
				supervision_list.get(i).put("supervision_time",supervision_list.get(i).get("day_count")+"/"+count2);
				
				if(String.valueOf(supervision_list.get(i).get("day_count")).equals(String.valueOf(count2))){  //未完成
					supervision_list.get(i).put("supervision_state","已完成");
				}else{    //完成
					supervision_list.get(i).put("supervision_state","未完成");
				}
				supervision_list.get(i).remove("day_count");
			}
		}
		
		
		if(pageNum*pageSize>=SupervisionDao.supervision_count(map)){  //判断是否最后一页
			appData.put("is_lastPage", true);
		}else{
			appData.put("is_lastPage", false);
		}
		
		appData.put("supervision_list", supervision_list);
		
		return Result.success(appData);
	}

	/**
	 * app施工监护新增
	 */
	@Override
	public CodeMsg add_supervision(Map<String, Object> map) {
		map.put("create_by", getUserIdByToken(String.valueOf(map.get("token")))); //create_by userId
		map.put("day_count",0); //day_count 
		int result=SupervisionDao.add_supervision(map);
		if(result>0){
			return CodeMsg.SUCCESS;
		}else{
			return CodeMsg.OPERATE_ERROR;
		}
		
	}
	/**
     * app施工监护新增工程列表
     */
	@Override
	public Object supervision_project_list(Map<String, Object> map) {
		Map<String,Object> appData=new HashMap<String,Object>();
		List<Map<String,Object>> list=new ArrayList<Map<String,Object>>();
		int pageNum=map.get("pageNum")==null?1:Integer.parseInt(String.valueOf(map.get("pageNum")));
		int pageSize=map.get("pageSize")==null?10:Integer.parseInt(String.valueOf(map.get("pageSize")));
		
		if(pageNum*pageSize>=ManageDao.project_list_count(map)){  //判断是否最后一页 
			appData.put("is_lastPage", true);
		}else{
			appData.put("is_lastPage", false);
		}
		
		PageHelper.startPage(pageNum,pageSize); 
		list=SupervisionDao.supervision_project_list(map);
		appData.put("project_data", list);
		
		return Result.success(appData);
	}
	
	
    /**
     * app施工监护时间类型列表
     */
	@Override
	public Object supervision_time_list(Map<String, Object> map) {
		System.out.println(121);
		return Result.success(SupervisionDao.supervision_time_list(map));
	}

	/**
	 * app施工监护上报模板列表
	 */
	@Override
	public Object supervision_model_list(Map<String, Object> map) {
		return Result.success(SupervisionDao.supervision_model_list(map));
	}
	
	/**
	 * app施工监护上报模板内容列表
	 */
	@Override
	public Object supervision_model_item_list(Map<String, Object> map) {
		return Result.success(SupervisionDao.supervision_model_item_list(map));
	}

	/**
	 * app加载施工监护上报页面信息
	 */
	@Override
	public Object load_supervision_report(Map<String, Object> map) {
		if(map.get("supervision_id")==null||map.get("is_irregular")==null){
			return Result.error(CodeMsg.MISSING_PARAMETER, getRoleIdByToken(String.valueOf(map.get("token"))));
		}
		//施工监护信息
		Map<String, Object> supervision_data=SupervisionDao.supervision_data(map);
		//项目信息
		Map<String, Object> _data=ManageDao.getProjectById(supervision_data);
		map.put("supervision_model_id", supervision_data.get("supervision_model_id"));
		String time=dateUtil.parseDateToStr(new Date(),dateUtil.DATE_FORMAT_YYYY_MM_DD);
		String beginTime=time+" 02:00:00";
		String endTime=dateUtil.getLastDayOfTime(beginTime,dateUtil.DATE_FORMAT_YYYY_MM_DD_HH_MI_SS);

		map.put("beginTime",beginTime);
		map.put("endTime",endTime);
		List<Map<String, Object>> item_list = SupervisionDao.supervision_model_item_list(map);
		List<Map<String,Object>> report_data= SupervisionDao.supervision_report_data(map); //正常上报记录
		item_list=getReportData(item_list,report_data);
		_data.put("supervision_model_item_data", item_list);
		return Result.success(_data);
	}
	
	
	
	/**
	 * app监护上报新增
	 */
	@Override
	public Result add_supervision_report(String json) {
		Map<String,Object> appData=JSONObject.parseObject(json); //解析数据
		if(appData.get("supervision_id")==null||appData.get("is_irregular")==null||appData.get("project_id")==null||appData.get("report_data")==null||appData.get("create_time")==null){
			return Result.error(CodeMsg.MISSING_PARAMETER,0);
		}

		int supervision_id=Integer.parseInt(String.valueOf(appData.get("supervision_id")));
		int is_irregular=Integer.parseInt(String.valueOf(appData.get("is_irregular")));
		Map<String,Object> parmes_map=new HashMap<>();
		Map<String,Object> su_map=new HashMap<>();
		List<Map<String, Object>> list=new ArrayList<>();
		parmes_map.put("supervision_id",supervision_id);

		if(is_irregular==0){
			list=SupervisionDao.supervision_list(parmes_map);
			if(list.size()>0){
				su_map=list.get(0);
				if(String.valueOf(su_map.get("type")).equals("1")){  //次/班
					if(String.valueOf(su_map.get("day_count")).equals(String.valueOf(su_map.get("count")))){
						return Result.error(CodeMsg.ALREADY_COMPLETE,0);
					}
				}else{    //每几小时
					int count=Integer.parseInt(String.valueOf(su_map.get("count")));
					int count2=12/count;
					if(String.valueOf(su_map.get("day_count")).equals(String.valueOf(count2))){
						return Result.error(CodeMsg.ALREADY_COMPLETE,0);
					}
				}
			}
		}
		//上报信息集合

		JSONArray report_data_list = JSONArray.parseArray(appData.get("report_data").toString());
		String create_at=String.valueOf(appData.get("create_time"));
		Map<String,Object> reportMap=new HashMap<>();
		reportMap.put("create_at",create_at);
		reportMap.put("is_irregular",appData.get("is_irregular"));
		reportMap.put("create_by",getUserIdByToken(String.valueOf(appData.get("token"))));
		reportMap.put("supervision_id",appData.get("supervision_id"));
		if(SupervisionDao.supervision_report_data(reportMap).size()>0){
			return Result.error(CodeMsg.ALREADY_REPORT,0);
		}

		for (int i = 0; i < report_data_list.size(); i++) {
			//添加一条上报信息
			Map<String, Object> report_data = report_data_list.getJSONObject(i);
			List<Integer> contentList=report_data.get("content")!=null?CommonDataServiceImpl.fileStringToList(report_data.get("content").toString()):new ArrayList<Integer>();
			report_data.put("content",StringUtils.strip(contentList.toString(),"[]"));	
			report_data.put("project_id", appData.get("project_id"));
			report_data.put("supervision_id", appData.get("supervision_id"));
			report_data.put("create_by",getUserIdByToken(String.valueOf(appData.get("token"))));
			report_data.put("is_irregular",appData.get("is_irregular"));
			report_data.put("cteate_at",create_at);
			SupervisionDao.add_supervision_report(report_data);
		}

		if(is_irregular==0){
			//上报次数加一
			SupervisionDao.update_supervision_day_count(supervision_id);
			//判断是否完成
			list=SupervisionDao.supervision_list(parmes_map);
			if(list.size()>0){
				su_map=list.get(0);
				if(String.valueOf(su_map.get("type")).equals("1")){  //次/班
					if(String.valueOf(su_map.get("day_count")).equals(String.valueOf(su_map.get("count")))){
						//状态改为完成
						SupervisionDao.update_supervision_state(supervision_id);
					}
				}else{    //每几小时
					int count=Integer.parseInt(String.valueOf(su_map.get("count")));
					int count2=12/count;
					if(String.valueOf(su_map.get("day_count")).equals(String.valueOf(count2))){
						//状态改为完成
						SupervisionDao.update_supervision_state(supervision_id);
					}
				}
			}
		}

		return Result.success(CodeMsg.SUCCESS,0);
	}

	/**
	 * app施工监护详情
	 */
	@Override
	public Object supervision_detail(Map<String, Object> map) {
		if(map.get("project_id")==null||map.get("supervision_id")==null){
			return CodeMsg.MISSING_PARAMETER;
		}
		Map<String, Object> _data=ManageDao.getProjectById(map);  //项目信息
		Map<String, Object> supervision_data=SupervisionDao.supervision_data(map);  //施工监护信息
		String time=dateUtil.parseDateToStr(new Date(),dateUtil.DATE_FORMAT_YYYY_MM_DD);
		String beginTime=time+" 02:00:00";
		String endTime=dateUtil.getLastDayOfTime(beginTime,dateUtil.DATE_FORMAT_YYYY_MM_DD_HH_MI_SS);

		map.put("supervision_model_id", supervision_data.get("supervision_model_id"));
		map.put("beginTime",beginTime);
		map.put("endTime",endTime);
			//上报信息
			List<Map<String,Object>> report_data= SupervisionDao.supervision_report_data(map);
			List<Map<String, Object>> item_list = SupervisionDao.supervision_model_item_list(map);
			item_list=getReportData(item_list,report_data);
			_data.put("report_data", item_list);
		return Result.success(_data);
	}


	/**
	 * 按日期查看监护记录
	 */
	@Override
	public Object loadmore_supervision(Map<String, Object> map) {
		//如果没有传日期 默认当天
		if(map.get("create_at")==null){
			map.put("create_at",dateUtil.parseDateToStr(new Date(),dateUtil.DATE_FORMAT_YYYY_MM_DD));
		}
		//上报信息
		Map<String, Object> supervision_data=new LinkedHashMap <>();
		String beginTime=String.valueOf(map.get("create_at"))+" 02:00:00";
		String endTime=dateUtil.getLastDayOfTime(beginTime,dateUtil.DATE_FORMAT_YYYY_MM_DD_HH_MI_SS);
		map.put("beginTime",beginTime);
		map.put("endTime",endTime);
		supervision_data.put("create_at",map.get("create_at"));
		map.remove("create_at");
		//一条监护的监护记录
		if(map.get("supervision_id")!=null){
			//施工监护信息
			Map<String, Object> supervision=SupervisionDao.supervision_data(map);
			supervision_data.put("department",supervision.get("dname"));
			map.put("supervision_model_id", supervision.get("supervision_model_id"));

			//正常监护
			List<Map<String, Object>> item_list = SupervisionDao.supervision_model_item_list(map);
			List<Map<String,Object>> report_data= SupervisionDao.supervision_report_data(map); //正常上报记录
			item_list=getReportData(item_list,report_data);
			supervision_data.put("report_data",item_list);
		}else if(map.get("project_id")!=null){
		    if(map.get("department_id")==null){
                return CodeMsg.MISSING_PARAMETER;
            }
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
                    for(int i=0;i<list.size();i++){
                        map.put("supervision_id",list.get(i).get("id"));
                        report_data= SupervisionDao.supervision_report_data(map);
                        item_list=getReportData(item_list,report_data);
                    }
                }
					supervision_data.put("report_data",item_list);
		}

		return Result.success(supervision_data);
	}
//=========================================================问题整改
	/**
	 * 问题整改列表
	 */
	@Override
	public Object abarbeitung_list(Map<String, Object> map) {
		Map<String,Object> appData=new HashMap<String,Object>();
		int pageNum=map.get("pageNum")==null?1:Integer.parseInt(map.get("pageNum").toString());
		int pageSize=map.get("pageSize")==null?10:Integer.parseInt(map.get("pageSize").toString());
		
		int user_id=getUserIdByToken(String.valueOf(map.get("token")));  //当前登录人
		List<Map<String, Object>> abarbeitung_list2=new ArrayList<>(); //未提交
		
		
		PageHelper.startPage(pageNum,pageSize); 
		List<Map<String, Object>> abarbeitung_list = SupervisionDao.abarbeitung_list(map);
		
		
		if(pageNum==1){  //第一个加上未提交的
			map.put("create_by", user_id);
			abarbeitung_list2=SupervisionDao.abarbeitung_list(map);
			map.remove("create_by");
			}
		
		abarbeitung_list2.addAll(abarbeitung_list);  //把未提交的已提交的拼接起来
		Map<String,Object> aData=new HashMap<>();
		for(int i=0;i<abarbeitung_list2.size();i++){  //查看是否有人上报
			if(String.valueOf(abarbeitung_list2.get(i).get("abarbeitung_state")).equals("1")){  //进行中
				aData.put("abarbeitung_id", abarbeitung_list2.get(i).get("abarbeitung_id"));
				aData.put("save",1);
				List<Map<String, Object>> report_data = SupervisionDao.abarbeitung_report_data(aData);  //保存的上报列表
				if(report_data==null||report_data.size()==0){
					abarbeitung_list2.get(i).put("create_state",0);  //没有人上报
				}else{
					abarbeitung_list2.get(i).put("create_state",2);  //不是自己上报
				}
				for(int j=0;j<report_data.size();j++){  //遍历
					if(String.valueOf(report_data.get(j).get("create_by")).equals(String.valueOf(user_id))){  //当前人有保存状态
						abarbeitung_list2.get(i).put("create_state",1);  //当前登录人上报
					}
				}
			}else{
				abarbeitung_list2.get(i).put("create_state",3);  //已整改或者未提交
			}
		}
		
		if(pageNum*pageSize>=SupervisionDao.abarbeitung_list_count(map)){  //判断是否最后一页
			appData.put("is_lastPage", true);
		}else{
			appData.put("is_lastPage", false);
		}
		
		appData.put("abarbeitung_data", abarbeitung_list2);
		return Result.success(appData);
	}

	/**
	 * 新增问题整改
	 */
	@Override
	public CodeMsg add_abarbeitung(String json) {
		Map<String,Object> appData=JSONObject.parseObject(json); //解析数据
		appData.put("create_by", getUserIdByToken(String.valueOf(appData.get("token")))); //create_by userId
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
		if(appData.get("abarbeitung_id")==null||String.valueOf(appData.get("abarbeitung_id")).equals("0")){  //没有信息 第一次添加  添加信息
			result=SupervisionDao.add_abarbeitung(appData);
		}else{
			result=SupervisionDao.update_abarbeitung(appData);  //有信息 修改
		}
        
        if(result>0){
			return CodeMsg.SUCCESS;
		}else{
			return CodeMsg.OPERATE_ERROR;
		}
	}
	
	/**
	 * 问题整改通知单信息
	 */
	@Override
	public Object abarbeitung_data(Map<String, Object> map) {
		Map<String, Object> abarbeitung_data = SupervisionDao.abarbeitung_data(map); //整改信息
		Map<String, Object> _data=ManageDao.getProjectById(abarbeitung_data);  //项目信息
	
		abarbeitung_data.put("site_photo", commonDao.getFileDescById(Arrays.asList(abarbeitung_data.get("site_photo").toString().split(",")))); //把XX转换为list
		abarbeitung_data.put("sign", commonDao.getFileDescById(Arrays.asList(abarbeitung_data.get("sign").toString().split(",")))); //把xx转换为list
		abarbeitung_data.put("else", commonDao.getFileDescById(Arrays.asList(abarbeitung_data.get("else").toString().split(",")))); //把xx转换为list
		
		_data.put("abarbeitung_data", abarbeitung_data);
		
		return Result.success(_data);
	}
	
	/**
	 * 问题整改处理新增
	 */
	@Override
	public CodeMsg add_abarbeitung_report(String json) {
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
		appData.put("submit", 1); //提交的信息
		List<Map<String, Object>> abarbeitung_report_data = SupervisionDao.abarbeitung_report_data(appData);
		if(abarbeitung_report_data==null||abarbeitung_report_data.size()==0){  //没有信息 第一次添加  添加信息
			appData.put("create_by", getUserIdByToken(String.valueOf(appData.get("token")))); //create_by userId
			appData.remove("submit");
			List<Map<String, Object>> abarbeitung_report_data2 = SupervisionDao.abarbeitung_report_data(appData);
			if(abarbeitung_report_data2==null||abarbeitung_report_data2.size()==0){  //本人没有保存
				result=SupervisionDao.add_abarbeitung_report(appData);  //添加一条
			}else{
				appData.put("report_id", abarbeitung_report_data2.get(0).get("id"));
				result=SupervisionDao.update_abarbeitung_report(appData);  //有信息 修改
			}
		}else{
			return CodeMsg.ALREADY_SUBMIT;
		}
		
		if(String.valueOf(appData.get("state")).equals("2")){  //提交 
			Map<String,Object> aData=new HashMap<>();
			aData.put("abarbeitung_id", appData.get("abarbeitung_id"));
			aData.put("state", 2);  //已整改
			SupervisionDao.update_abarbeitung(aData);
			
			SupervisionDao.delete_save_abarbeitung_report(aData);  //删除保存的整改报告
		}
			
        if(result>0){
			return CodeMsg.SUCCESS;
		}else{
			return CodeMsg.OPERATE_ERROR;
		}
	}

	
	
	/**
	 * 问题整改详情
	 */
	@Override
	public Object abarbeitung_detail(Map<String, Object> map) {
		Map<String, Object> abarbeitung_data = SupervisionDao.abarbeitung_data(map); //整改通知单信息
		Map<String, Object> _data=ManageDao.getProjectById(abarbeitung_data);  //项目信息
		abarbeitung_data.remove("project_id");
		abarbeitung_data.put("site_photo", commonDao.getFileDescById(Arrays.asList(abarbeitung_data.get("site_photo").toString().split(",")))); //把XX转换为list
		abarbeitung_data.put("sign", commonDao.getFileDescById(Arrays.asList(abarbeitung_data.get("sign").toString().split(",")))); //把xx转换为list
		abarbeitung_data.put("else", commonDao.getFileDescById(Arrays.asList(abarbeitung_data.get("else").toString().split(",")))); //把xx转换为list
		
		map.put("submit",1);  //提交的上报信息
		List<Map<String, Object>> report_data_List = SupervisionDao.abarbeitung_report_data(map); //整改报告信息
		Map<String, Object> abarbeitung_report_data=new HashMap<>();
		if(report_data_List!=null&&report_data_List.size()!=0){
			abarbeitung_report_data=report_data_List.get(0);
			abarbeitung_report_data.put("site_photo", commonDao.getFileDescById(Arrays.asList(abarbeitung_report_data.get("site_photo").toString().split(",")))); //把XX转换为list
			abarbeitung_report_data.put("report", commonDao.getFileDescById(Arrays.asList(abarbeitung_report_data.get("report").toString().split(",")))); //把xx转换为list
			abarbeitung_report_data.put("else", commonDao.getFileDescById(Arrays.asList(abarbeitung_report_data.get("else").toString().split(",")))); //把xx转换为list
		}
		_data.put("abarbeitung_data", abarbeitung_data);
		_data.put("abarbeitung_report_data", abarbeitung_report_data);
		return Result.success(_data);
	}

	
	/**
	 * 加载整改通知单(已保存)
	 */
	@Override
	public Object load_abarbeitung(Map<String, Object> map) {
		Map<String, Object> abarbeitung_data = SupervisionDao.abarbeitung_data(map); //整改信息
	
		abarbeitung_data.put("site_photo", commonDao.getFileDescById(Arrays.asList(abarbeitung_data.get("site_photo").toString().split(",")))); //把XX转换为list
		abarbeitung_data.put("sign", commonDao.getFileDescById(Arrays.asList(abarbeitung_data.get("sign").toString().split(",")))); //把xx转换为list
		abarbeitung_data.put("else", commonDao.getFileDescById(Arrays.asList(abarbeitung_data.get("else").toString().split(",")))); //把xx转换为list
		
		return Result.success(abarbeitung_data);
	}
	
	/**
	 * 加载整改报告(已保存)
	 */
	@Override
	public Object load_abarbeitung_report(Map<String, Object> map) {
		 
		map.put("create_by", getUserIdByToken(String.valueOf(map.get("token")))); //create_by userId
		List<Map<String, Object>> report_dataList = SupervisionDao.abarbeitung_report_data(map); //整改报告信息保存的
		Map<String, Object> abarbeitung_report_data=new HashMap<>();
		if(report_dataList!=null&&report_dataList.size()!=0){
			abarbeitung_report_data=report_dataList.get(0);
			abarbeitung_report_data.put("site_photo", commonDao.getFileDescById(Arrays.asList(abarbeitung_report_data.get("site_photo").toString().split(",")))); //把XX转换为list
			abarbeitung_report_data.put("report", commonDao.getFileDescById(Arrays.asList(abarbeitung_report_data.get("report").toString().split(",")))); //把xx转换为list
			abarbeitung_report_data.put("else", commonDao.getFileDescById(Arrays.asList(abarbeitung_report_data.get("else").toString().split(",")))); //把xx转换为list
		 }
		return Result.success(abarbeitung_report_data);
	}
	
	/**
	 * 重要施工项列表
	 */
	@Override
	public Object construction_item_list(Map<String, Object> map) {
		Map<String,Object> appData=new HashMap<String,Object>();
		int pageNum=map.get("pageNum")==null?1:Integer.parseInt(map.get("pageNum").toString());
		int pageSize=map.get("pageSize")==null?10:Integer.parseInt(map.get("pageSize").toString());
		
		int user_id=getUserIdByToken(String.valueOf(map.get("token")));  //当前登录人
		
		PageHelper.startPage(pageNum,pageSize); 
		List<Map<String, Object>> item_data = SupervisionDao.construction_item_list(map);
		
		List<Map<String, Object>> _list2=new ArrayList<>(); //未提交
		if(pageNum==1){  //第一个加上为提交的
		map.put("create_by", user_id);
		_list2=SupervisionDao.construction_item_list(map);
		map.remove("create_by");
		}
		_list2.addAll(item_data);
		
		if(pageNum*pageSize>=SupervisionDao.construction_item_count(map)){  //判断是否最后一页
			appData.put("is_lastPage", true);
		}else{
			appData.put("is_lastPage", false);
		}
		
		appData.put("item_data", _list2);
		return Result.success(appData);
	}

	/**
	 * 新增重要施工项
	 */
	@Override
	public Object add_construction_item(String json) {
		Map<String,Object> appData=JSONObject.parseObject(json); //解析数据
		Map<String,Object> idData=new HashMap<String,Object>(); //解析数据
		appData.put("create_by", getUserIdByToken(String.valueOf(appData.get("token")))); //create_by userId
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
        
        if(result>0){
        	idData.put("item_id", appData.get("item_id"));
        	Map<String,Object> item_Data=SupervisionDao.construction_item_data(idData);
        	item_Data.put("photo", commonDao.getFileDescById(Arrays.asList(item_Data.get("photo").toString().split(",")))); //把xx转换为list
        	item_Data.put("ossa", commonDao.getFileDescById(Arrays.asList(item_Data.get("ossa").toString().split(",")))); //把xx转换为list
        	item_Data.put("charge", commonDao.getFileDescById(Arrays.asList(item_Data.get("charge").toString().split(",")))); //把xx转换为list
        	item_Data.put("request", commonDao.getFileDescById(Arrays.asList(item_Data.get("request").toString().split(",")))); //把xx转换为list
        	item_Data.put("construction_before", commonDao.getFileDescById(Arrays.asList(item_Data.get("construction_before").toString().split(",")))); //把xx转换为list
        	item_Data.put("special_deal", commonDao.getFileDescById(Arrays.asList(item_Data.get("special_deal").toString().split(",")))); //把xx转换为list
        	item_Data.put("construction_after", commonDao.getFileDescById(Arrays.asList(item_Data.get("construction_after").toString().split(",")))); //把xx转换为list
        	item_Data.put("else", commonDao.getFileDescById(Arrays.asList(item_Data.get("else").toString().split(",")))); //把xx转换为list
			return Result.success(item_Data);
		}else{
			return CodeMsg.OPERATE_ERROR;
		}
	}

	/**
	 * 重要施工项分类列表
	 */
	@Override
	public Object item_type_list(Map<String, Object> map) {
		
		return Result.success(SupervisionDao.item_type_list(map));
	}

	/**
	 * 证书列表
	 */
	@Override
	public Object item_credential_list(Map<String, Object> map) {

		return Result.success(SupervisionDao.item_credential_list(map));
	}

	/**
	 * 重要施工项详情
	 */
	@Override
	public Object construction_item_data(Map<String, Object> map) {
		
		Map<String,Object> item_Data=SupervisionDao.construction_item_data(map);
		Map<String, Object> _data=ManageDao.getProjectById(map);  //项目信息
		
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
    	
    	return Result.success(_data);
	}

	/**
	 * 加载重要施工项(已保存)
	 */
	@Override
	public Object load_construction_item(Map<String, Object> map) {
		Map<String,Object> item_Data=SupervisionDao.load_construction_item_data(map);
    	
		
		
		item_Data.put("photo", commonDao.getFileDescById(Arrays.asList(item_Data.get("photo").toString().split(",")))); //把xx转换为list
    	item_Data.put("ossa", commonDao.getFileDescById(Arrays.asList(item_Data.get("ossa").toString().split(",")))); //把xx转换为list
    	item_Data.put("charge", commonDao.getFileDescById(Arrays.asList(item_Data.get("charge").toString().split(",")))); //把xx转换为list
    	item_Data.put("request", commonDao.getFileDescById(Arrays.asList(item_Data.get("request").toString().split(",")))); //把xx转换为list
    	item_Data.put("construction_before", commonDao.getFileDescById(Arrays.asList(item_Data.get("construction_before").toString().split(",")))); //把xx转换为list
    	item_Data.put("special_deal", commonDao.getFileDescById(Arrays.asList(item_Data.get("special_deal").toString().split(",")))); //把xx转换为list
    	item_Data.put("construction_after", commonDao.getFileDescById(Arrays.asList(item_Data.get("construction_after").toString().split(",")))); //把xx转换为list
    	item_Data.put("else", commonDao.getFileDescById(Arrays.asList(item_Data.get("else").toString().split(",")))); //把xx转换为list
		
    	return Result.success(item_Data);
	}

	/**
	 * 加载更多重要施工项(历史施工项)
	 */
	@Override
	public Object loadmore_construction_item(Map<String, Object> map) {
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
	    	return Result.success(item_Data);
		}else{
			return null;
		}
	}

	/**
	 * 工程进度列表
	 */
	@Override
	public Object schedule_list(Map<String, Object> map) {
		Map<String,Object> appData=new HashMap<String,Object>();
		int pageNum=map.get("pageNum")==null?1:Integer.parseInt(map.get("pageNum").toString());
		int pageSize=map.get("pageSize")==null?10:Integer.parseInt(map.get("pageSize").toString());
		
		int user_id=getUserIdByToken(String.valueOf(map.get("token")));  //当前登录人
		List<Map<String, Object>> _list2=new ArrayList<>(); //未提交
		if(pageNum==1){  //第一个加上为提交的
		map.put("create_by", user_id);
		_list2=SupervisionDao.schedule_list(map);
		map.remove("create_by");
		}
		
		
		PageHelper.startPage(pageNum,pageSize); 
		List<Map<String, Object>> schedule_data = SupervisionDao.schedule_list(map);
		
		_list2.addAll(schedule_data);
		if(pageNum*pageSize>=SupervisionDao.schedule_count(map)){  //判断是否最后一页
			appData.put("is_lastPage", true);
		}else{
			appData.put("is_lastPage", false);
		}
		
		appData.put("schedule_data", _list2);
		
		return Result.success(appData);
	}

	/**
	 * 工程进度信息
	 */
	@Override
	public Object schedule_data(Map<String, Object> map) {
		Map<String, Object> _data=ManageDao.getProjectById(map);  //项目信息
		Map<String, Object> schedule_data=SupervisionDao.schedule_newData(map);
	
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
		return Result.success(_data);
	}

	/**
	 * 添加工程进度
	 */
	@Override
	public Object add_schedule(String json) {
		Map<String,Object> appData=JSONObject.parseObject(json); //解析数据
		Map<String,Object> idData=new HashMap<String,Object>(); //解析数据
		appData.put("create_by", getUserIdByToken(String.valueOf(appData.get("token")))); //create_by userId
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
        if(result>0){
			return CodeMsg.SUCCESS;
		}else{
			return CodeMsg.OPERATE_ERROR;
		}
	}

	/**
	 * 加载更多计划进度
	 */
	@Override
	public Object loadmore_schedule(Map<String, Object> map) {
		
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
		return Result.success(schedule_data);
	}

	
	/**
	 * 加载计划进度
	 */
	@Override
	public Object load_schedule(Map<String, Object> map) {
		Map<String, Object> schedule_data=SupervisionDao.load_schedule(map);
	
		schedule_data.put("meeting_summary", commonDao.getFileDescById(Arrays.asList(schedule_data.get("meeting_summary").toString().split(",")))); //把xx转换为list
		schedule_data.put("site_photo", commonDao.getFileDescById(Arrays.asList(schedule_data.get("site_photo").toString().split(",")))); //把xx转换为list
		schedule_data.put("schedule", commonDao.getFileDescById(Arrays.asList(schedule_data.get("schedule").toString().split(",")))); //把xx转换为list
		schedule_data.put("else", commonDao.getFileDescById(Arrays.asList(schedule_data.get("else").toString().split(",")))); //把xx转换为list
		
		return Result.success(schedule_data);
	}


//===================================公共方法===============================


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

}
