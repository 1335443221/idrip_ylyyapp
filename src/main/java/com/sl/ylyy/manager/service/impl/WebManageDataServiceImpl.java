package com.sl.ylyy.manager.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.sl.ylyy.manager.dao.ManageDao;
import com.sl.ylyy.manager.dao.CommonDao;
import com.sl.ylyy.manager.service.WebManageDataService;
import com.sl.ylyy.common.utils.ExcelUtil;
import com.sl.ylyy.common.utils.PageUtil;

@Service("WebManageDataImpl")
public class WebManageDataServiceImpl implements WebManageDataService {

	@Autowired
	private ManageDao ManageDao;
	
	@Autowired
	private CommonDao commonDao;
	
	
	/**
	 * web工程列表
	 */
	@Override
	public PageUtil<Map<String, Object>> web_project_list(Map<String, Object> map,HttpSession session) {
		PageUtil<Map<String, Object>> pageInfo=new PageUtil<Map<String, Object>>();
		int pageindex =Integer.parseInt(String.valueOf(map.get("pageindex")));  //页码
		PageHelper.startPage(pageindex,pageInfo.getPagesize());  //分页
		List<Map<String,Object>> list=ManageDao.web_project_list(map);  //分页后的数据
		
		List<Map<String,Object>> type_data=ManageDao.project_type_list(map);  //type数据
		List<Map<String,Object>> state_data=ManageDao.web_project_state_list(map);  //state数据
		
		Map<String,Object> smap=JSONObject.parseObject(String.valueOf(session.getAttribute("activeAdmin")));
		for(int i=0;i<list.size();i++){   //判斷是否是自己操作的
			int state=Integer.parseInt(String.valueOf(list.get(i).get("project_state")));
			if(state==1||state==2||state==5||state==6){
				Map<String,Object> stateData=new HashMap<String,Object>();
				stateData.put("state", list.get(i).get("project_state"));
				stateData.put("project_id", list.get(i).get("project_id"));
				Integer uid=ManageDao.project_state_create(stateData);
				
				if(uid==null||uid==0){
					list.get(i).put("create_state", 1);
				}else if(String.valueOf(smap.get("id")).equals(String.valueOf(uid))){
					list.get(i).put("create_state", 1);
				}else{
					list.get(i).put("create_state", 2);
				}
			}
		}
		
		map.put("state_data", state_data);
		map.put("type_data", type_data);
		pageInfo.setData(list);  //把数据存到page中
		
		pageInfo.setRecordCount(ManageDao.web_project_list(map).size());  //总记录数
		pageInfo.setPageindex(pageindex);  //返回当前页
		map.put("lastpage", pageInfo.getLastPage());  //上一页
		map.put("nextpage", pageInfo.getNextpage(pageInfo.getTotalPagecount()));  //下一页
		pageInfo.setParams(map);  //返回模糊查询信息
		return pageInfo;
	}

	
	/**
	 * web工程分类列表
	 */
	@Override
	public List<Map<String, Object>> web_project_type_list(Map<String, Object> map) {
		
		return ManageDao.project_type_list(map);
	}

	/**
	 * 工程新增
	 */
	@Override
	public Object web_add_project(String json) {
		Map<String,Object> appData=JSONObject.parseObject(json); //解析数据
		List<Integer> sign_fileList=appData.get("sign_file")!=null?CommonDataServiceImpl.fileStringToList(String.valueOf(appData.get("sign_file"))):new ArrayList<Integer>();  //签报文件.
		List<Integer> owner_consentList=appData.get("owner_consent")!=null?CommonDataServiceImpl.fileStringToList(String.valueOf(appData.get("owner_consent"))):new ArrayList<Integer>();  //签报文件.
		if(appData.get("is_urgency")==null){
        	appData.put("is_urgency", 0);
        }
		if(Integer.parseInt(String.valueOf(appData.get("is_urgency")))==0){  //是否紧急施工
			appData.put("state", 1);   //前期准备阶段
			appData.put("save_state", 2);   //保存工程
		}else{
			appData.put("state", 2);   //开工阶段
			appData.put("save_state", 3);   //提交工程

		}
		appData.put("sign_file",StringUtils.strip(String.valueOf(sign_fileList),"[]"));
		appData.put("owner_consent",StringUtils.strip(String.valueOf(owner_consentList),"[]"));
		ManageDao.add_project(appData);  //新增工程

		Map<String, Object> early_data = ManageDao.project_early_data(appData);  //前期信息
		Map<String, Object> start_data = ManageDao.project_start_data(appData);  //前期信息

		if(Integer.parseInt(String.valueOf(appData.get("is_urgency")))==0){  //是否紧急施工
			Map<String, Object> sMap=new HashMap<String, Object>();
			sMap.put("project_id", appData.get("project_id"));
			if(early_data==null){
				ManageDao.add_project_early(sMap);  //新增前期信息
			}
		}else{
			Map<String, Object> sMap=new HashMap<String, Object>();
			sMap.put("project_id", appData.get("project_id"));
			if(early_data==null){
				ManageDao.add_project_early(sMap);  //新增前期信息
			}
			if(start_data==null){
				ManageDao.add_project_start(sMap);  //新增开工信息
			}
		}
		return appData.get("project_id");
	}
	
	/**
	 * 工程前期新增或修改   
	 */
	@Override
	public Object web_add_early_project(String json) {
		Map<String,Object> appData=JSONObject.parseObject(json); //解析数据
        int state=Integer.parseInt(String.valueOf(appData.get("state")));  //2保存 3提交
        int project_id=Integer.parseInt(String.valueOf(appData.get("project_id"))); //工程id
        Map<String,Object> early_map=new HashMap<>();
		early_map.put("project_id", project_id);
        Map<String,Object> appData2=ManageDao.project_early_data(early_map);  //工程前期信息 没有null
        Map<String,Object> start_data=ManageDao.project_start_data(early_map);

        if(state==3){  //如果是提交  进入开工阶段
        	CommonDataServiceImpl.updateProjectState(2,project_id);
				Map<String, Object> sMap=new HashMap<String, Object>();
				sMap.put("project_id",project_id);
				if(start_data==null){
					ManageDao.add_project_start(sMap);  //新增开工信息
				}
        }
           //三元运算符  如果不为空 则把图片传入数据库,返回id拼装的list,为空则返回空list
        	List<Integer> contractList=appData.get("contract")!=null?CommonDataServiceImpl.fileStringToList(String.valueOf(appData.get("contract"))):new ArrayList<Integer>();  //签报文件.
            List<Integer> qualificationList=appData.get("qualification")!=null?CommonDataServiceImpl.fileStringToList(String.valueOf(appData.get("qualification"))):new ArrayList<Integer>();  //XX文件.
            List<Integer> sec_protocolList=appData.get("sec_protocol")!=null?CommonDataServiceImpl.fileStringToList(String.valueOf(appData.get("sec_protocol"))):new ArrayList<Integer>();  //XX文件.
            List<Integer> start_credentialList=appData.get("start_credential")!=null?CommonDataServiceImpl.fileStringToList(String.valueOf(appData.get("start_credential"))):new ArrayList<Integer>();  //XX文件.
            List<Integer> io_credentialList=appData.get("io_credential")!=null?CommonDataServiceImpl.fileStringToList(String.valueOf(appData.get("io_credential"))):new ArrayList<Integer>();  //XX文件.
            List<Integer> constructionfeeList=appData.get("constructionfee")!=null?CommonDataServiceImpl.fileStringToList(String.valueOf(appData.get("constructionfee"))):new ArrayList<Integer>();  //XX文件.
            List<Integer> safety_trainingList=appData.get("safety_training")!=null?CommonDataServiceImpl.fileStringToList(String.valueOf(appData.get("safety_training"))):new ArrayList<Integer>();  //XX文件.
            List<Integer> people_informationList=appData.get("people_information")!=null?CommonDataServiceImpl.fileStringToList(String.valueOf(appData.get("people_information"))):new ArrayList<Integer>();  //XX文件.
            List<Integer> csd_receiptList=appData.get("csd_receipt")!=null?CommonDataServiceImpl.fileStringToList(String.valueOf(appData.get("csd_receipt"))):new ArrayList<Integer>();  //XX文件.
            List<Integer> sop_certificateList=appData.get("sop_certificate")!=null?CommonDataServiceImpl.fileStringToList(String.valueOf(appData.get("sop_certificate"))):new ArrayList<Integer>();  //XX文件.
            List<Integer> recordList=appData.get("record")!=null?CommonDataServiceImpl.fileStringToList(String.valueOf(appData.get("record"))):new ArrayList<Integer>();  //XX文件.
            List<Integer> drawingsList=appData.get("drawings")!=null?CommonDataServiceImpl.fileStringToList(String.valueOf(appData.get("drawings"))):new ArrayList<Integer>();  //XX文件.
            List<Integer> elseList=appData.get("else")!=null?CommonDataServiceImpl.fileStringToList(String.valueOf(appData.get("else"))):new ArrayList<Integer>();  //XX文件.
            
            appData.put("contract", StringUtils.strip(String.valueOf(contractList),"[]"));
    		appData.put("qualification", StringUtils.strip(String.valueOf(qualificationList),"[]"));
    		appData.put("sec_protocol", StringUtils.strip(String.valueOf(sec_protocolList),"[]"));
    		appData.put("start_credential", StringUtils.strip(String.valueOf(start_credentialList),"[]"));
    		appData.put("io_credential", StringUtils.strip(String.valueOf(io_credentialList),"[]"));
    		appData.put("constructionfee", StringUtils.strip(String.valueOf(constructionfeeList),"[]"));
    		appData.put("safety_training",StringUtils.strip(String.valueOf(safety_trainingList),"[]"));
    		appData.put("people_information",StringUtils.strip(String.valueOf(people_informationList),"[]"));
    		appData.put("csd_receipt",StringUtils.strip(String.valueOf(csd_receiptList),"[]"));
    		appData.put("sop_certificate",StringUtils.strip(String.valueOf(sop_certificateList),"[]"));
    		appData.put("record",StringUtils.strip(String.valueOf(recordList),"[]"));
    		appData.put("drawings",StringUtils.strip(String.valueOf(drawingsList),"[]"));
    		appData.put("else",StringUtils.strip(String.valueOf(elseList),"[]"));
        int result=0;
    	if(appData2==null){ //没有前期信息 插入一条前期信息
    		result=ManageDao.add_project_early(appData);  //新增工程前期
        }else{  //已经存在前期信息  则修改前期信息
        	result=ManageDao.update_project_early(appData);  //修改工程前期
        }
        return result;
	}
	
	
	/**
	 * 修改工程以及前期信息
	 */
	@Override
	public Object web_update_project(String json) {
		Map<String,Object> appData=JSONObject.parseObject(json); //解析数据
		Map<String,Object> project_Data=JSONObject.parseObject(String.valueOf(appData.get("project_data"))); //解析工程数据
		Map<String,Object> early_Data=JSONObject.parseObject(String.valueOf(appData.get("early_data"))); //解析前期数据
		
		int project_id=Integer.parseInt(String.valueOf(appData.get("project_id"))); //工程id
		int state=Integer.parseInt(String.valueOf(appData.get("state")));  //2保存 3提交
		if(state==3){  //如果是提交  进入开工阶段
        	CommonDataServiceImpl.updateProjectState(2,project_id);
				Map<String, Object> sMap=new HashMap<String, Object>();
				sMap.put("project_id",project_id);
				Map<String,Object> state_data=ManageDao.project_start_data(sMap);
				if(state_data==null){
					ManageDao.add_project_start(sMap);  //新增开工信息
				}
        }
		if(state==4){ //补录
			appData.put("state",3);
			early_Data.put("state",3);
		}
		
        List<Integer> sign_fileList=CommonDataServiceImpl.fileStringToList(String.valueOf(project_Data.get("sign_file")));  //签报文件.
        List<Integer> owner_consentList=CommonDataServiceImpl.fileStringToList(String.valueOf(project_Data.get("owner_consent")));  //XX文件.
        project_Data.put("sign_file",StringUtils.strip(String.valueOf(sign_fileList),"[]"));
        project_Data.put("owner_consent",StringUtils.strip(String.valueOf(owner_consentList),"[]"));
       
        
        int result=0;
        result= ManageDao.update_project(project_Data);
        
    	List<Integer> contractList=early_Data.get("contract")!=null?CommonDataServiceImpl.fileStringToList(String.valueOf(early_Data.get("contract"))):new ArrayList<Integer>();  //签报文件.
        List<Integer> qualificationList=early_Data.get("qualification")!=null?CommonDataServiceImpl.fileStringToList(String.valueOf(early_Data.get("qualification"))):new ArrayList<Integer>();  //XX文件.
        List<Integer> sec_protocolList=early_Data.get("sec_protocol")!=null?CommonDataServiceImpl.fileStringToList(String.valueOf(early_Data.get("sec_protocol"))):new ArrayList<Integer>();  //XX文件.
        List<Integer> start_credentialList=early_Data.get("start_credential")!=null?CommonDataServiceImpl.fileStringToList(String.valueOf(early_Data.get("start_credential"))):new ArrayList<Integer>();  //XX文件.
        List<Integer> io_credentialList=early_Data.get("io_credential")!=null?CommonDataServiceImpl.fileStringToList(String.valueOf(early_Data.get("io_credential"))):new ArrayList<Integer>();  //XX文件.
        List<Integer> constructionfeeList=early_Data.get("constructionfee")!=null?CommonDataServiceImpl.fileStringToList(String.valueOf(early_Data.get("constructionfee"))):new ArrayList<Integer>();  //XX文件.
        List<Integer> safety_trainingList=early_Data.get("safety_training")!=null?CommonDataServiceImpl.fileStringToList(String.valueOf(early_Data.get("safety_training"))):new ArrayList<Integer>();  //XX文件.
        List<Integer> people_informationList=early_Data.get("people_information")!=null?CommonDataServiceImpl.fileStringToList(String.valueOf(early_Data.get("people_information"))):new ArrayList<Integer>();  //XX文件.
        List<Integer> csd_receiptList=early_Data.get("csd_receipt")!=null?CommonDataServiceImpl.fileStringToList(String.valueOf(early_Data.get("csd_receipt"))):new ArrayList<Integer>();  //XX文件.
        List<Integer> sop_certificateList=early_Data.get("sop_certificate")!=null?CommonDataServiceImpl.fileStringToList(String.valueOf(early_Data.get("sop_certificate"))):new ArrayList<Integer>();  //XX文件.
        List<Integer> recordList=early_Data.get("record")!=null?CommonDataServiceImpl.fileStringToList(String.valueOf(early_Data.get("record"))):new ArrayList<Integer>();  //XX文件.
        List<Integer> drawingsList=early_Data.get("drawings")!=null?CommonDataServiceImpl.fileStringToList(String.valueOf(early_Data.get("drawings"))):new ArrayList<Integer>();  //XX文件.
        List<Integer> elseList=early_Data.get("else")!=null?CommonDataServiceImpl.fileStringToList(String.valueOf(early_Data.get("else"))):new ArrayList<Integer>();  //XX文件.
        
        early_Data.put("contract", StringUtils.strip(String.valueOf(contractList),"[]"));
		early_Data.put("qualification", StringUtils.strip(String.valueOf(qualificationList),"[]"));
		early_Data.put("sec_protocol", StringUtils.strip(String.valueOf(sec_protocolList),"[]"));
		early_Data.put("start_credential", StringUtils.strip(String.valueOf(start_credentialList),"[]"));
		early_Data.put("io_credential", StringUtils.strip(String.valueOf(io_credentialList),"[]"));
		early_Data.put("constructionfee", StringUtils.strip(String.valueOf(constructionfeeList),"[]"));
		early_Data.put("safety_training",StringUtils.strip(String.valueOf(safety_trainingList),"[]"));
		early_Data.put("people_information",StringUtils.strip(String.valueOf(people_informationList),"[]"));
		early_Data.put("csd_receipt",StringUtils.strip(String.valueOf(csd_receiptList),"[]"));
		early_Data.put("sop_certificate",StringUtils.strip(String.valueOf(sop_certificateList),"[]"));
		early_Data.put("record",StringUtils.strip(String.valueOf(recordList),"[]"));
		early_Data.put("drawings",StringUtils.strip(String.valueOf(drawingsList),"[]"));
		early_Data.put("else",StringUtils.strip(String.valueOf(elseList),"[]"));

		Map<String,Object> early_map=new HashMap<>();
		early_map.put("project_id", project_id);
	     Map<String,Object> appData2=ManageDao.project_early_data(early_map);  //工程前期信息 没有null
        
	     if(appData2==null){ //没有前期信息 插入一条前期信息
	    		ManageDao.add_project_early(early_Data);  //新增工程前期
	     }else{  //已经存在前期信息  则修改前期信息
	        	ManageDao.update_project_early(early_Data);  //修改工程前期
	     }
	     
	     
		return result;
	}
	
	
	
	
	/**
	 * web工程信息
	 */
	@Override
	public Map<String, Object> web_project_data_ById(Map<String, Object> map) {
		Map<String, Object> pData=ManageDao.web_project_data_ById(map); //项目信息
		pData.put("sign_file", commonDao.getFileDescById(Arrays.asList(String.valueOf(pData.get("sign_file")).split(",")))); //把XX转换为list
		pData.put("owner_consent", commonDao.getFileDescById(Arrays.asList(String.valueOf(pData.get("owner_consent")).split(",")))); //把xx转换为list
		
		Map<String, Object> early_data = ManageDao.project_early_data(map);  //前期信息
		if(early_data!=null){
			early_data.put("contract", commonDao.getFileDescById(Arrays.asList(String.valueOf(early_data.get("contract")).split(",")))); //把XX转换为list
			early_data.put("qualification", commonDao.getFileDescById(Arrays.asList(String.valueOf(early_data.get("qualification")).split(",")))); //把XX转换为list
			early_data.put("sec_protocol", commonDao.getFileDescById(Arrays.asList(String.valueOf(early_data.get("sec_protocol")).split(",")))); //把XX转换为list
			early_data.put("start_credential", commonDao.getFileDescById(Arrays.asList(String.valueOf(early_data.get("start_credential")).split(",")))); //把XX转换为list
			early_data.put("io_credential", commonDao.getFileDescById(Arrays.asList(String.valueOf(early_data.get("io_credential")).split(",")))); //把XX转换为list
			early_data.put("constructionfee", commonDao.getFileDescById(Arrays.asList(String.valueOf(early_data.get("constructionfee")).split(",")))); //把XX转换为list
			early_data.put("safety_training", commonDao.getFileDescById(Arrays.asList(String.valueOf(early_data.get("safety_training")).split(",")))); //把XX转换为list
			early_data.put("people_information", commonDao.getFileDescById(Arrays.asList(String.valueOf(early_data.get("people_information")).split(",")))); //把XX转换为list
			early_data.put("csd_receipt", commonDao.getFileDescById(Arrays.asList(String.valueOf(early_data.get("csd_receipt")).split(",")))); //把XX转换为list
			early_data.put("sop_certificate", commonDao.getFileDescById(Arrays.asList(String.valueOf(early_data.get("sop_certificate")).split(",")))); //把XX转换为list
			early_data.put("record", commonDao.getFileDescById(Arrays.asList(String.valueOf(early_data.get("record")).split(",")))); //把XX转换为list
			early_data.put("drawings", commonDao.getFileDescById(Arrays.asList(String.valueOf(early_data.get("drawings")).split(",")))); //把XX转换为list
			early_data.put("else", commonDao.getFileDescById(Arrays.asList(String.valueOf(early_data.get("else")).split(",")))); //把XX转换为list
		}
		
		pData.put("early_data", early_data);
		
		return pData;
	}


	/**
	 * 开工信息
	 */
	@Override
	public Map<String, Object> web_start_data_ById(Map<String, Object> map) {
		
		Map<String, Object> project_start_data = ManageDao.project_start_data(map);  //开工信息
		if(project_start_data!=null){
			project_start_data.put("cpce_site", commonDao.getFileDescById(Arrays.asList(String.valueOf(project_start_data.get("cpce_site")).split(",")))); //把XX转换为list
			project_start_data.put("st_site", commonDao.getFileDescById(Arrays.asList(String.valueOf(project_start_data.get("st_site")).split(",")))); //把xx转换为list
			project_start_data.put("cstd_site", commonDao.getFileDescById(Arrays.asList(String.valueOf(project_start_data.get("cstd_site")).split(",")))); //把xx转换为list
			project_start_data.put("ts_form", commonDao.getFileDescById(Arrays.asList(String.valueOf(project_start_data.get("ts_form")).split(",")))); //把XX转换为list
			project_start_data.put("situation_site", commonDao.getFileDescById(Arrays.asList(String.valueOf(project_start_data.get("situation_site")).split(",")))); //把XX转换为list
			project_start_data.put("material", commonDao.getFileDescById(Arrays.asList(String.valueOf(project_start_data.get("material")).split(",")))); //把XX转换为list
			project_start_data.put("office_site", commonDao.getFileDescById(Arrays.asList(String.valueOf(project_start_data.get("office_site")).split(",")))); //把XX转换为list
			project_start_data.put("drawings", commonDao.getFileDescById(Arrays.asList(String.valueOf(project_start_data.get("drawings")).split(",")))); //把XX转换为list
			project_start_data.put("mie_inspection", commonDao.getFileDescById(Arrays.asList(String.valueOf(project_start_data.get("mie_inspection")).split(",")))); //把XX转换为list
			project_start_data.put("cpsa_supervisor", commonDao.getFileDescById(Arrays.asList(String.valueOf(project_start_data.get("cpsa_supervisor")).split(",")))); //把XX转换为list
			project_start_data.put("else", commonDao.getFileDescById(Arrays.asList(String.valueOf(project_start_data.get("else")).split(",")))); //把XX转换为list
		}
		
		return project_start_data;
	}


	/**
	 * 修改开工信息
	 */
	@Override
	public Object web_update_start(String json) {
		Map<String,Object> appData=JSONObject.parseObject(json); //解析数据
		
		int project_id=Integer.parseInt(String.valueOf(appData.get("project_id"))); //工程id
		int state=Integer.parseInt(String.valueOf(appData.get("state")));  //2保存 3提交
		if(state==3){  //如果是提交  进入施工中阶段
        	CommonDataServiceImpl.updateProjectState(3,project_id);
        }
		if(state==4){ //补录
			appData.put("state",3);
		}
		
    	List<Integer> cpce_siteList=appData.get("cpce_site")!=null?CommonDataServiceImpl.fileStringToList(String.valueOf(appData.get("cpce_site"))):new ArrayList<Integer>();  //签报文件.
    	List<Integer> st_siteList=appData.get("st_site")!=null?CommonDataServiceImpl.fileStringToList(String.valueOf(appData.get("st_site"))):new ArrayList<Integer>();  //签报文件.
    	List<Integer> cstd_siteList=appData.get("cstd_site")!=null?CommonDataServiceImpl.fileStringToList(String.valueOf(appData.get("cstd_site"))):new ArrayList<Integer>();  //签报文件.
    	List<Integer> ts_formList=appData.get("ts_form")!=null?CommonDataServiceImpl.fileStringToList(String.valueOf(appData.get("ts_form"))):new ArrayList<Integer>();  //签报文件.
    	List<Integer> situation_siteList=appData.get("situation_site")!=null?CommonDataServiceImpl.fileStringToList(String.valueOf(appData.get("situation_site"))):new ArrayList<Integer>();  //签报文件.
    	List<Integer> materialList=appData.get("material")!=null?CommonDataServiceImpl.fileStringToList(String.valueOf(appData.get("material"))):new ArrayList<Integer>();  //签报文件.
    	List<Integer> office_siteList=appData.get("office_site")!=null?CommonDataServiceImpl.fileStringToList(String.valueOf(appData.get("office_site"))):new ArrayList<Integer>();  //签报文件.
    	List<Integer> drawingsList=appData.get("drawings")!=null?CommonDataServiceImpl.fileStringToList(String.valueOf(appData.get("drawings"))):new ArrayList<Integer>();  //签报文件.
    	List<Integer> mie_inspectionList=appData.get("mie_inspection")!=null?CommonDataServiceImpl.fileStringToList(String.valueOf(appData.get("mie_inspection"))):new ArrayList<Integer>();  //签报文件.
    	List<Integer> cpsa_supervisorList=appData.get("cpsa_supervisor")!=null?CommonDataServiceImpl.fileStringToList(String.valueOf(appData.get("cpsa_supervisor"))):new ArrayList<Integer>();  //签报文件.
    	List<Integer> elseList=appData.get("else")!=null?CommonDataServiceImpl.fileStringToList(String.valueOf(appData.get("else"))):new ArrayList<Integer>();  //XX文件.
        
    	appData.put("cpce_site", StringUtils.strip(String.valueOf(cpce_siteList),"[]"));
    	appData.put("st_site", StringUtils.strip(String.valueOf(st_siteList),"[]"));
    	appData.put("cstd_site", StringUtils.strip(String.valueOf(cstd_siteList),"[]"));
    	appData.put("ts_form", StringUtils.strip(String.valueOf(ts_formList),"[]"));
    	appData.put("situation_site", StringUtils.strip(String.valueOf(situation_siteList),"[]"));
    	appData.put("material", StringUtils.strip(String.valueOf(materialList),"[]"));
    	appData.put("office_site", StringUtils.strip(String.valueOf(office_siteList),"[]"));
    	appData.put("drawings", StringUtils.strip(String.valueOf(drawingsList),"[]"));
    	appData.put("mie_inspection", StringUtils.strip(String.valueOf(mie_inspectionList),"[]"));
    	appData.put("cpsa_supervisor", StringUtils.strip(String.valueOf(cpsa_supervisorList),"[]"));
    	appData.put("else",StringUtils.strip(String.valueOf(elseList),"[]"));

    	
    	 Map<String,Object> pMap=new HashMap<>();
         pMap.put("project_id", project_id);
		//工程开工信息 没有null
	     Map<String,Object> appData2=ManageDao.project_start_data(pMap);
        
	     int result=0;
	     if(appData2==null){ //没有开工信息 插入一条前期信息
	    	 result=ManageDao.add_project_start(appData);
	     }else{  //已经存在开工信息  则修改开工信息
	    	 result=ManageDao.update_project_start(appData);
	     }
	     
		return result;
	}

	

	/**
	 * 施工中信息
	 */
	@Override
	public Object web_working_data(Map<String, Object> map) {

		return null;
	}

	

	/**
	 * 预验收信息
	 */
	@Override
	public Map<String, Object> web_before_data_ById(Map<String, Object> map) {
		Map<String, Object> state_data = ManageDao.project_before_check_data(map);  //信息
		
		if(state_data!=null){
			state_data.put("before_check_bill", commonDao.getFileDescById(Arrays.asList(String.valueOf(state_data.get("before_check_bill")).split(",")))); //把XX转换为list
			state_data.put("issue_list", commonDao.getFileDescById(Arrays.asList(String.valueOf(state_data.get("issue_list")).split(",")))); //把xx转换为list
			state_data.put("problem_spot", commonDao.getFileDescById(Arrays.asList(String.valueOf(state_data.get("problem_spot")).split(",")))); //把xx转换为list
			state_data.put("ppss_form", commonDao.getFileDescById(Arrays.asList(String.valueOf(state_data.get("ppss_form")).split(",")))); //把XX转换为list
			state_data.put("else", commonDao.getFileDescById(Arrays.asList(String.valueOf(state_data.get("else")).split(",")))); //把XX转换为list
		}
		
		return state_data;
	}


	
	/**
	 * 修改预验收
	 */
	@Override
	public Object web_update_before(String json) {
		Map<String,Object> appData=JSONObject.parseObject(json); //解析数据
		int project_id=Integer.parseInt(String.valueOf(appData.get("project_id"))); //工程id
		int state=Integer.parseInt(String.valueOf(appData.get("state")));  //2保存 3提交
		 Map<String,Object> pMap=new HashMap<>();
         pMap.put("project_id", project_id);
		Map<String,Object> appData2=ManageDao.project_before_check_data(pMap);  //工程预验收信息 没有null
		if(state==3){  //如果是提交  进入验收阶段
        	CommonDataServiceImpl.updateProjectState(6,project_id);
				Map<String, Object> sMap=new HashMap<String, Object>();
				sMap.put("project_id",project_id);
				Map<String,Object> state_data=ManageDao.project_check_data(sMap);
				if(state_data==null){
					ManageDao.add_project_check(sMap);  //新增验收信息
				}
        }
		if(state==4){ //补录
			appData.put("state",3);
		}
		
		//三元运算符  如果不为空 则把图片传入数据库,返回id拼装的list,为空则返回空list
        List<Integer> fileList1=appData.get("before_check_bill")!=null?CommonDataServiceImpl.fileStringToList(String.valueOf(appData.get("before_check_bill"))):new ArrayList<Integer>();
        List<Integer> fileList2=appData.get("issue_list")!=null?CommonDataServiceImpl.fileStringToList(String.valueOf(appData.get("issue_list"))):new ArrayList<Integer>();  //XX文件..
        List<Integer> fileList3=appData.get("problem_spot")!=null?CommonDataServiceImpl.fileStringToList(String.valueOf(appData.get("problem_spot"))):new ArrayList<Integer>();  //XX文件.
        List<Integer> fileList4=appData.get("ppss_form")!=null?CommonDataServiceImpl.fileStringToList(String.valueOf(appData.get("ppss_form"))):new ArrayList<Integer>();  //XX文件.
        List<Integer> fileList5=appData.get("else")!=null?CommonDataServiceImpl.fileStringToList(String.valueOf(appData.get("else"))):new ArrayList<Integer>();  //XX文件.
        
        //把list集合去掉[],转化成list存到数据库中
        appData.put("before_check_bill", StringUtils.strip(String.valueOf(fileList1),"[]"));
		appData.put("issue_list", StringUtils.strip(String.valueOf(fileList2),"[]"));
		appData.put("problem_spot", StringUtils.strip(String.valueOf(fileList3),"[]"));
		appData.put("ppss_form", StringUtils.strip(String.valueOf(fileList4),"[]"));
		appData.put("else", StringUtils.strip(String.valueOf(fileList5),"[]"));
		
	    int result=0;
		if(appData2==null){ //没有预验收信息 插入一条预验收信息
			result=ManageDao.add_project_before_check(appData);  //新增工程预验收
	    }else{  //已经存在预验收信息  则修改预验收信息
	    	result=ManageDao.update_project_before_check(appData);  //修改工程预验收
	    }
		
		return result;
	}

	
	/**
	 * 验收信息
	 */
	@Override
	public Map<String, Object> web_check_data_ById(Map<String, Object> map) {
		Map<String, Object> state_data = ManageDao.project_check_data(map);  //验收信息
		if(state_data!=null){
			state_data.put("cpsp_doc", commonDao.getFileDescById(Arrays.asList(String.valueOf(state_data.get("cpsp_doc")).split(",")))); //把XX转换为list
			state_data.put("spare_part", commonDao.getFileDescById(Arrays.asList(String.valueOf(state_data.get("spare_part")).split(",")))); //把xx转换为list
			state_data.put("qa_form", commonDao.getFileDescById(Arrays.asList(String.valueOf(state_data.get("qa_form")).split(",")))); //把xx转换为list
			state_data.put("problem_form", commonDao.getFileDescById(Arrays.asList(String.valueOf(state_data.get("problem_form")).split(",")))); //把XX转换为list
			state_data.put("problem_site", commonDao.getFileDescById(Arrays.asList(String.valueOf(state_data.get("problem_site")).split(",")))); //把XX转换为list
			state_data.put("else", commonDao.getFileDescById(Arrays.asList(String.valueOf(state_data.get("else")).split(",")))); //把XX转换为list
		}
		return state_data;
	}


	/**
	 * 修改验收
	 */
	@Override
	public Object web_update_check(String json) {
        Map<String,Object> appData=JSONObject.parseObject(json); //解析数据
        int state=Integer.parseInt(String.valueOf(appData.get("state")));  //1初始  2保存 3提交
        int project_id=Integer.parseInt(String.valueOf(appData.get("project_id"))); //工程id
        
        Map<String,Object> pMap=new HashMap<>();
        pMap.put("project_id", project_id);
        Map<String,Object> appData2=ManageDao.project_check_data(pMap);  //工程验收信息 没有null
        
        if(state==3){  //如果是提交  进入归档阶段
        	CommonDataServiceImpl.updateProjectState(7,project_id);
        }
		if(state==4){ //补录
			appData.put("state",3);
		}
        	//三元运算符  如果不为空 则把图片传入数据库,返回id拼装的list,为空则返回空list
            List<Integer> fileList1=appData.get("cpsp_doc")!=null?CommonDataServiceImpl.fileStringToList(String.valueOf(appData.get("cpsp_doc"))):new ArrayList<Integer>();
            List<Integer> fileList2=appData.get("spare_part")!=null?CommonDataServiceImpl.fileStringToList(String.valueOf(appData.get("spare_part"))):new ArrayList<Integer>();  //XX文件..
            List<Integer> fileList3=appData.get("qa_form")!=null?CommonDataServiceImpl.fileStringToList(String.valueOf(appData.get("qa_form"))):new ArrayList<Integer>();  //XX文件.
            List<Integer> fileList4=appData.get("problem_form")!=null?CommonDataServiceImpl.fileStringToList(String.valueOf(appData.get("problem_form"))):new ArrayList<Integer>();  //XX文件.
            List<Integer> fileList5=appData.get("problem_site")!=null?CommonDataServiceImpl.fileStringToList(String.valueOf(appData.get("problem_site"))):new ArrayList<Integer>();  //XX文件.
            List<Integer> fileList6=appData.get("else")!=null?CommonDataServiceImpl.fileStringToList(String.valueOf(appData.get("else"))):new ArrayList<Integer>();  //XX文件.
            
            //把list集合去掉[],转化成list存到数据库中
            appData.put("cpsp_doc", StringUtils.strip(String.valueOf(fileList1),"[]"));
    		appData.put("spare_part", StringUtils.strip(String.valueOf(fileList2),"[]"));
    		appData.put("qa_form", StringUtils.strip(String.valueOf(fileList3),"[]"));
    		appData.put("problem_form", StringUtils.strip(String.valueOf(fileList4),"[]"));
    		appData.put("problem_site", StringUtils.strip(String.valueOf(fileList5),"[]"));
    		appData.put("else", StringUtils.strip(String.valueOf(fileList6),"[]"));
        int result=0;
    	if(appData2==null){ //没有预验收信息 插入一条预验收信息
    		result=ManageDao.add_project_check(appData);  //新增工程预验收
        }else{  //已经存在预验收信息  则修改预验收信息
        	result=ManageDao.update_project_check(appData);  //修改工程预验收
        }
        return result;
	}

////////////////////////////////工程结算
	/**
	 * web工程结算列表列表
	 */
	@Override
	public PageUtil<Map<String, Object>> web_project_settlement_list(Map<String, Object> map) {
		PageUtil<Map<String, Object>> pageInfo=new PageUtil<Map<String, Object>>();
		int pageindex =Integer.parseInt(String.valueOf(map.get("pageindex")));  //页码
		PageHelper.startPage(pageindex,pageInfo.getPagesize());  //分页
		List<Map<String,Object>> list=ManageDao.web_project_settlement_list(map);  //分页后的数据
		
		List<Map<String,Object>> type_data=ManageDao.project_type_list(map);  //type数据
		List<Map<String,Object>> state_data=ManageDao.web_project_state_list(map);  //state数据
		
		map.put("state_data", state_data);
		map.put("type_data", type_data);
		pageInfo.setData(list);  //把数据存到page中
		
		pageInfo.setRecordCount(ManageDao.web_project_settlement_list(map).size());  //总记录数
		pageInfo.setPageindex(pageindex);  //返回当前页
		map.put("lastpage", pageInfo.getLastPage());  //上一页
		map.put("nextpage", pageInfo.getNextpage(pageInfo.getTotalPagecount()));  //下一页
		pageInfo.setParams(map);  //返回模糊查询信息
		return pageInfo;
	}
	
	
	
	/**
	 * 结算编辑列表
	 */
	@Override
	public Object web_update_settlement_list(Map<String, Object> map) {
		Map<String, Object> data=ManageDao.getProjectById(map);  //项目信息
		List<Map<String,Object>> list=ManageDao.web_update_settlement_list(map);  //结算数据
		for(int i=0;i<list.size();i++){
			list.get(i).put("receipt", commonDao.getFileDescById(Arrays.asList(String.valueOf(list.get(i).get("receipt")).split(",")))); //把XX转换为list
			list.get(i).put("refund", commonDao.getFileDescById(Arrays.asList(String.valueOf(list.get(i).get("refund")).split(",")))); //把XX转换为list
			list.get(i).put("payment", commonDao.getFileDescById(Arrays.asList(String.valueOf(list.get(i).get("payment")).split(",")))); //把XX转换为list
		}

		data.put("settlement_list", list);
		return data;
	}


	/**
	 * 去新增结算
	 */
	@Override
	public Object go_add_settlement(Map<String, Object> map) {
		//项目信息
		Map<String, Object> data=ManageDao.getProjectById(map);
		data.put("type_list", ManageDao.project_settlement_type_list(map));
		return data;
	}

	/**
	 * 去修改结算
	 */
	@Override
	public Object goUpdateSettment(Map<String, Object> map) {
		//项目信息
		Map<String, Object> data=ManageDao.getProjectById(map);
		data.put("type_list", ManageDao.project_settlement_type_list(map));
		Map<String, Object> settlement_data = ManageDao.project_settlement_ById(map);
		settlement_data.put("receipt", commonDao.getFileDescById(Arrays.asList(String.valueOf(settlement_data.get("receipt")).split(",")))); //把xx转换为list
		settlement_data.put("refund", commonDao.getFileDescById(Arrays.asList(String.valueOf(settlement_data.get("refund")).split(",")))); //把xx转换为list
		settlement_data.put("payment", commonDao.getFileDescById(Arrays.asList(String.valueOf(settlement_data.get("payment")).split(",")))); //把XX转换为list
		data.put("settment",settlement_data);
		return data;
	}
	
	

	/**
	 * 上传或者修改结算
	 */
	@Override
	public Object web_add_update_settment(String json) {
		Map<String,Object> appData=JSONObject.parseObject(json); //解析数据
		
		//三元运算符  如果不为空 则把图片传入数据库,返回id拼装的list,为空则返回空list
        List<Integer> fileList1=appData.get("receipt")!=null?CommonDataServiceImpl.fileStringToList(String.valueOf(appData.get("receipt"))):new ArrayList<Integer>();
        List<Integer> fileList2=appData.get("refund")!=null?CommonDataServiceImpl.fileStringToList(String.valueOf(appData.get("refund"))):new ArrayList<Integer>();  //XX文件..
        List<Integer> fileList3=appData.get("payment")!=null?CommonDataServiceImpl.fileStringToList(String.valueOf(appData.get("payment"))):new ArrayList<Integer>();  //XX文件.
        
        //把list集合去掉[],转化成list存到数据库中
        appData.put("receipt", StringUtils.strip(String.valueOf(fileList1),"[]"));
		appData.put("refund", StringUtils.strip(String.valueOf(fileList2),"[]"));
		appData.put("payment", StringUtils.strip(String.valueOf(fileList3),"[]"));
		
	    int result=0;
		if(appData.get("id")==null){ //没有信息 插入一条预验收信息
			result=ManageDao.add_project_settlement(appData);  //新增工程预验收
	    }else{  //修改预验收信息
	    	result=ManageDao.update_project_settlement(appData);  //修改工程预验收
	    }
		
		return result;
	}



	/**
	 * 获取工程报表
	 */
	@Override
	public PageUtil<Map<String, Object>> showProjectLog(Map<String, Object> map) {
		String  beginTime=String.valueOf(map.get("beginTime"));
		String  endTime=String.valueOf(map.get("endTime"));
		
		if(map.get("beginTime")==null|| "".equals(map.get("beginTime"))){
			beginTime="";
			map.put("beginTime","1970-10-1 10:00:00");
		}
		
		if(map.get("endTime")==null|| "".equals(map.get("endTime"))){
			endTime="";
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH：mm：ss");
			map.put("endTime",simpleDateFormat.format(new Date()));
		}
		
		
		PageUtil<Map<String, Object>> pageInfo=new PageUtil<Map<String, Object>>();
		int pageindex =Integer.parseInt(String.valueOf(map.get("pageindex")));  //页码
		PageHelper.startPage(pageindex,pageInfo.getPagesize());  //分页
		List<Map<String,Object>> list=ManageDao.showProjectLog(map);  //分页后的数据
		
		for(int i=0;i<list.size();i++){
			Map<String, Object> pmap=new HashMap<String, Object>();
			pmap.put("project_id", list.get(i).get("project_id"));
			list.get(i).put("abarbeitungCount", ManageDao.abarbeitungCountByPid(pmap));//整改次数
			pmap.put("type_id", 3);
			Map<String,Object> settlement=ManageDao.settlementAmountByPid(pmap);  //结算
			if(settlement!=null){
				
				double contract_amount=settlement.get("contract_amount")==null?0.00:Double.parseDouble(String.valueOf(settlement.get("contract_amount")));
				double no_tax_amount=settlement.get("no_tax_amount")==null?0.00:Double.parseDouble(String.valueOf(settlement.get("no_tax_amount")));
				
				list.get(i).put("contract_amount",contract_amount);   //合同总额
				list.get(i).put("no_tax_amount",no_tax_amount); //已付总额
				double unpaid=contract_amount-no_tax_amount;
				list.get(i).put("unpaid",unpaid); //未付总额
				
			}
			
			pmap.put("type_id", 5);//质保金
			settlement=ManageDao.settlementAmountByPid(pmap);  //结算
			if(settlement!=null){
				double no_tax_amount=settlement.get("no_tax_amount")==null?0.00:Double.parseDouble(String.valueOf(settlement.get("no_tax_amount")));
				list.get(i).put("guarantee",no_tax_amount);   //质保金
			}
		}
		List<Map<String,Object>> type_data=ManageDao.project_type_list(map);  //type数据
		map.put("type_data",type_data);
		pageInfo.setData(list);  //把数据存到page中
		pageInfo.setRecordCount(ManageDao.showProjectLog(map).size());  //总记录数
		pageInfo.setPageindex(pageindex);  //返回当前页
		map.put("lastpage", pageInfo.getLastPage());  //上一页
		map.put("nextpage", pageInfo.getNextpage(pageInfo.getTotalPagecount()));  //下一页
		
		map.put("beginTime", beginTime);
		map.put("endTime", endTime);
		pageInfo.setParams(map);  //返回模糊查询信息
		return pageInfo;
	}
	
	
	/**
	 * 获取工程报表
	 */
	@Override
	public HSSFWorkbook export_project_Log(Map<String, Object> map) {
		
		if(map.get("beginTime")==null|| "".equals(map.get("beginTime"))){
			map.put("beginTime","1970-10-1 10:00:00");
		}
		
		if(map.get("endTime")==null|| "".equals(map.get("endTime"))){
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH：mm：ss");
			map.put("endTime",simpleDateFormat.format(new Date()));
		}
		
		
		
		List<Map<String,Object>> list=ManageDao.showProjectLog(map);  //分页后的数据
		
		for(int i=0;i<list.size();i++){
			Map<String, Object> pmap=new HashMap<String, Object>();
			pmap.put("project_id", list.get(i).get("project_id"));
			list.get(i).put("abarbeitungCount", ManageDao.abarbeitungCountByPid(pmap));//整改次数
			pmap.put("type_id", 3);
			Map<String,Object> settlement=ManageDao.settlementAmountByPid(pmap);  //结算
			list.get(i).put("no_tax_amount", settlement.get("no_tax_amount"));   //已付总额
			list.get(i).put("contract_amount", settlement.get("contract_amount")); //合同总额
			
			double unpaid=Double.parseDouble(String.valueOf(settlement.get("contract_amount")))-Double.parseDouble(String.valueOf(settlement.get("no_tax_amount")));
			list.get(i).put("unpaid",unpaid); //未付总额
			
			pmap.put("type_id", 5);//质保金
			settlement=ManageDao.settlementAmountByPid(pmap);  //结算
			list.get(i).put("guarantee", settlement.get("no_tax_amount"));   //质保金
		}
		
		 //excel标题
        String[] title = {"序号","工程类型","工程名称","施工单位","监理单位","监理负责人","开工日期","竣工日期","项目经理","联系电话","施工违规/处罚记录","合同总款（未税）","已支付（未税）","未支付（未税）","质保金（未税）","质保结束日期"};


        //sheet名
         String sheetName = "工程报表";
         String[][] content = new String[list.size()][];
         for (int i = 0; i < list.size(); i++) {  //遍历
        	  content[i] = new String[title.length];
        	  Map<String,Object> obj = list.get(i);  //一个
             content[i][0] = (i+1)+"";
             content[i][1] = String.valueOf(obj.get("type_name"));
             content[i][2] = String.valueOf(obj.get("name")); 
             content[i][3] = String.valueOf(obj.get("construction_org")); 
             content[i][4] = String.valueOf(obj.get("supervising_org")); 
             content[i][5] = String.valueOf(obj.get("supervisor")); 
             content[i][6] = String.valueOf(obj.get("start_time")); 
             content[i][7] = String.valueOf(obj.get("end_time")); 
             content[i][8] = String.valueOf(obj.get("PM")); 
             content[i][9] = String.valueOf(obj.get("PM_phone")); 
             content[i][10] = String.valueOf(obj.get("abarbeitungCount")); 
             content[i][11] = String.valueOf(obj.get("contract_amount")); 
             content[i][12] = String.valueOf(obj.get("no_tax_amount")); 
             content[i][13] = String.valueOf(obj.get("unpaid")); 
             content[i][14] = String.valueOf(obj.get("guarantee")); 
             content[i][15] = String.valueOf(obj.get("expiration_end")); 
         }
       //创建HSSFWorkbook 
         HSSFWorkbook wb = ExcelUtil.getHSSFWorkbook(sheetName, title, content, null);
         
		return wb;
	}
	
}
