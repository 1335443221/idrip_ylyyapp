package com.sl.ylyy.manager.service.impl;

import static com.sl.ylyy.common.utils.JwtToken.getUserIdByToken;

import java.util.*;

import com.sl.ylyy.common.utils.DateUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.sl.ylyy.manager.dao.ManageDao;
import com.sl.ylyy.manager.dao.SupervisionDao;
import com.sl.ylyy.manager.dao.CommonDao;
import com.sl.ylyy.manager.service.ManageDataService;
import com.sl.ylyy.common.utils.CodeMsg;
import com.sl.ylyy.common.utils.Result;

@Service("ManageDataImpl")
public class ManageDataServiceImpl implements ManageDataService {

	@Autowired
	private ManageDao ManageDao;
	
	@Autowired
	private CommonDao commonDao;
	
	@Autowired
	private SupervisionDao SupervisionDao;

	@Autowired
	private DateUtil dateUtil;

    
    /**
     * 通过id删除图片
     */
	@Override
	public Object deleteFileById(Map<String, Object> map) {
		commonDao.deleteFileById(map);
		return CodeMsg.SUCCESS;
	}
	
	/**
	 * app工程列表
	 */
	@Override
	public Object project_list(Map<String, Object> map) {
		int list_type=Integer.parseInt(String.valueOf(map.get("list_type")));  //列表分类
		Map<String,Object> appData=new HashMap<String,Object>();
		List<Map<String,Object>> list=new ArrayList<Map<String,Object>>();
		int pageNum=map.get("pageNum")==null?1:Integer.parseInt(String.valueOf(map.get("pageNum")));
		int pageSize=map.get("pageSize")==null?10:Integer.parseInt(String.valueOf(map.get("pageSize")));
		if(list_type==1){
			map.put("start", 1);
			if(pageNum*pageSize>=ManageDao.project_list_count(map)){  //判断是否最后一页 
				appData.put("is_lastPage", true);
			}else{
				appData.put("is_lastPage", false);
			}
			PageHelper.startPage(pageNum,pageSize); 
			list=ManageDao.project_list(map);
			
			for(int i=0;i<list.size();i++){   //判斷是否是自己操作的
				int state=Integer.parseInt(String.valueOf(list.get(i).get("project_state")));
				if(state==1||state==2||state==5||state==6){
					Map<String,Object> stateData=new HashMap<String,Object>();
					stateData.put("state", list.get(i).get("project_state"));
					stateData.put("project_id", list.get(i).get("project_id"));
					Integer uid=ManageDao.project_state_create(stateData);
					int user_id=getUserIdByToken(String.valueOf(map.get("token")));
					if(uid==null||uid==0){
						list.get(i).put("create_state", 0);
					}else if(uid==user_id){
						list.get(i).put("create_state", 1);
					}else{
						list.get(i).put("create_state", 2);
					}
				}else{
					list.get(i).put("create_state", 0);
				}
			}
			
		}
       if(list_type==2){
    	   map.put("filing", 1);
    	   
    	   if(pageNum*pageSize>=ManageDao.project_list_count(map)){  //判断是否最后一页
				appData.put("is_lastPage", true);
			}else{
				appData.put("is_lastPage", false);
			}
    	   PageHelper.startPage(pageNum,pageSize); 
    	   list=ManageDao.project_list(map);
		}
       if(list_type==3){
    	   if(pageNum*pageSize>=ManageDao.project_settlement_list_count(map)){  //判断是否最后一页
				appData.put("is_lastPage", true);
			}else{
				appData.put("is_lastPage", false);
			}
    	   PageHelper.startPage(pageNum,pageSize); 
    	   list=ManageDao.project_list(map);
    	   
    	   for(int i=0;i<list.size();i++){   //
    		  Map<String,Object> settlementData=new HashMap<String,Object>();
    		  settlementData.put("project_id", list.get(i).get("project_id"));
    		 String pay_state=  ManageDao.project_settlement_list_name(settlementData);
    		 list.get(i).put("state_name", pay_state);
    	   }
    	   
    	   
        }
       appData.put("project_data", list);
		return Result.success(appData);
	}

	/**
	 * app工程类型列表
	 */
	@Override
	public Object project_type_list(Map<String, Object> map) {
		return Result.success(ManageDao.project_type_list(map));
	}
	
	/**
	 * app工程新增
	 */
	@Override
	public Object add_project(String json) {
		Map<String,Object> appData=JSONObject.parseObject(json); //解析数据
        
		
		appData.put("create_by", getUserIdByToken(String.valueOf(appData.get("token")))); //create_by userId
		List<Integer> sign_fileList=appData.get("sign_file")!=null?CommonDataServiceImpl.fileStringToList(String.valueOf(appData.get("sign_file"))):new ArrayList<Integer>();  //签报文件.
		List<Integer> owner_consentList=appData.get("owner_consent")!=null?CommonDataServiceImpl.fileStringToList(String.valueOf(appData.get("owner_consent"))):new ArrayList<Integer>();  //签报文件.
        if(appData.get("is_urgency")==null){
        	appData.put("is_urgency", 0);
        }
		if(Integer.parseInt(String.valueOf(appData.get("is_urgency")))==0){  //是否紧急施工
			appData.put("state", 1);   //前期准备阶段
			appData.put("save_state", 2);   //工程保存
		}else{
			appData.put("state", 2);   //开工阶段
			appData.put("save_state", 3);   //工程提交
		}
		
		appData.put("sign_file",StringUtils.strip(String.valueOf(sign_fileList),"[]"));
		appData.put("owner_consent",StringUtils.strip(String.valueOf(owner_consentList),"[]"));
		int i=ManageDao.add_project(appData);  //新增工程

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
		
		
		if(i>0){
			return CodeMsg.SUCCESS;
		}else{
			return CodeMsg.OPERATE_ERROR;
		}
	}
	
	
	/**
	 * app工程前期信息
	 */
	@Override
	public Object project_early_data(Map<String, Object> map) {
		Map<String, Object> project_early_data=ManageDao.getProjectById(map);  //项目信息
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
		project_early_data.put("early_data",early_data);  //把前期信息存到项目信息中
		return Result.success(project_early_data);
	}
	
	
	/**
	 * app工程前期保存或提交
	 */
	@Override
	public CodeMsg add_project_early(String json) {
        Map<String,Object> appData=JSONObject.parseObject(json); //解析数据
        appData.put("create_by", getUserIdByToken(String.valueOf(appData.get("token")))); //create_by userId
        int state=Integer.parseInt(String.valueOf(appData.get("state")));  //1初始  2保存 3提交
        int project_id=Integer.parseInt(String.valueOf(appData.get("project_id"))); //工程id
        
        Map<String,Object> pMap=new HashMap<>();
        pMap.put("project_id", project_id);
        Map<String,Object> appData2=ManageDao.project_early_data(pMap);  //工程前期信息 没有null
		Map<String,Object> start_data=ManageDao.project_start_data(pMap);
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
            List<Integer> qualificationList=appData.get("qualification")!=null?CommonDataServiceImpl.fileStringToList(String.valueOf(appData.get("qualification"))):new ArrayList<Integer>();  
            List<Integer> sec_protocolList=appData.get("sec_protocol")!=null?CommonDataServiceImpl.fileStringToList(String.valueOf(appData.get("sec_protocol"))):new ArrayList<Integer>();  
            List<Integer> start_credentialList=appData.get("start_credential")!=null?CommonDataServiceImpl.fileStringToList(String.valueOf(appData.get("start_credential"))):new ArrayList<Integer>();  
            List<Integer> io_credentialList=appData.get("io_credential")!=null?CommonDataServiceImpl.fileStringToList(String.valueOf(appData.get("io_credential"))):new ArrayList<Integer>();  
            List<Integer> constructionfeeList=appData.get("constructionfee")!=null?CommonDataServiceImpl.fileStringToList(String.valueOf(appData.get("constructionfee"))):new ArrayList<Integer>();  
            List<Integer> safety_trainingList=appData.get("safety_training")!=null?CommonDataServiceImpl.fileStringToList(String.valueOf(appData.get("safety_training"))):new ArrayList<Integer>();  
            List<Integer> people_informationList=appData.get("people_information")!=null?CommonDataServiceImpl.fileStringToList(String.valueOf(appData.get("people_information"))):new ArrayList<Integer>();  
            List<Integer> csd_receiptList=appData.get("csd_receipt")!=null?CommonDataServiceImpl.fileStringToList(String.valueOf(appData.get("csd_receipt"))):new ArrayList<Integer>();  
            List<Integer> sop_certificateList=appData.get("sop_certificate")!=null?CommonDataServiceImpl.fileStringToList(String.valueOf(appData.get("sop_certificate"))):new ArrayList<Integer>();  
            List<Integer> recordList=appData.get("record")!=null?CommonDataServiceImpl.fileStringToList(String.valueOf(appData.get("record"))):new ArrayList<Integer>();  
            List<Integer> drawingsList=appData.get("drawings")!=null?CommonDataServiceImpl.fileStringToList(String.valueOf(appData.get("drawings"))):new ArrayList<Integer>();  
            List<Integer> elseList=appData.get("else")!=null?CommonDataServiceImpl.fileStringToList(String.valueOf(appData.get("else"))):new ArrayList<Integer>();  
            
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
    		result=ManageDao.add_project_early(appData);  //新增工程
        }else{  //已经存在前期信息  则修改前期信息
        	result=ManageDao.update_project_early(appData);  //修改工程
        }
        if(result>0){
			return CodeMsg.SUCCESS;
		}else{
			return CodeMsg.OPERATE_ERROR;
		}
        
	}
	
	/**
	 * app工程开工信息
	 */
	@Override
	public Object project_start_data(Map<String, Object> map) {
		Map<String, Object> _data=ManageDao.getProjectById(map);  //项目信息
		Map<String, Object> state_data = ManageDao.project_start_data(map);  //开工信息
		if(state_data!=null){
			state_data.put("cpce_site", commonDao.getFileDescById(Arrays.asList(String.valueOf(state_data.get("cpce_site")).split(",")))); //把XX转换为list
			state_data.put("st_site", commonDao.getFileDescById(Arrays.asList(String.valueOf(state_data.get("st_site")).split(",")))); //把xx转换为list
			state_data.put("cstd_site", commonDao.getFileDescById(Arrays.asList(String.valueOf(state_data.get("cstd_site")).split(",")))); //把xx转换为list
			state_data.put("ts_form", commonDao.getFileDescById(Arrays.asList(String.valueOf(state_data.get("ts_form")).split(",")))); //把XX转换为list
			state_data.put("situation_site", commonDao.getFileDescById(Arrays.asList(String.valueOf(state_data.get("situation_site")).split(",")))); //把XX转换为list
			state_data.put("material", commonDao.getFileDescById(Arrays.asList(String.valueOf(state_data.get("material")).split(",")))); //把XX转换为list
			state_data.put("office_site", commonDao.getFileDescById(Arrays.asList(String.valueOf(state_data.get("office_site")).split(",")))); //把XX转换为list
			state_data.put("drawings", commonDao.getFileDescById(Arrays.asList(String.valueOf(state_data.get("drawings")).split(",")))); //把XX转换为list
			state_data.put("mie_inspection", commonDao.getFileDescById(Arrays.asList(String.valueOf(state_data.get("mie_inspection")).split(",")))); //把XX转换为list
			state_data.put("cpsa_supervisor", commonDao.getFileDescById(Arrays.asList(String.valueOf(state_data.get("cpsa_supervisor")).split(",")))); //把XX转换为list
			state_data.put("else", commonDao.getFileDescById(Arrays.asList(String.valueOf(state_data.get("else")).split(",")))); //把XX转换为list
		}
		_data.put("start_data",state_data);  //把开工信息存到项目信息中
		return Result.success(_data);
	}
	
	
	/**
	 * app工程开工保存或提交
	 */
	@Override
	public CodeMsg add_project_start(String json) {
        Map<String,Object> appData=JSONObject.parseObject(json); //解析数据
        appData.put("create_by", getUserIdByToken(String.valueOf(appData.get("token")))); //create_by userId
        int state=Integer.parseInt(String.valueOf(appData.get("state")));  //1初始  2保存 3提交
        int project_id=Integer.parseInt(String.valueOf(appData.get("project_id"))); //工程id
        
        Map<String,Object> pMap=new HashMap<>();
        pMap.put("project_id", project_id);
        Map<String,Object> appData2=ManageDao.project_start_data(pMap);  //工程开工信息 没有null
        
        if(state==3){  //如果是提交  进入施工阶段
        	CommonDataServiceImpl.updateProjectState(3,project_id);
        }
        	//三元运算符  如果不为空 则把图片传入数据库,返回id拼装的list,为空则返回空list
            List<Integer> fileList1=appData.get("cpce_site")!=null?CommonDataServiceImpl.fileStringToList(String.valueOf(appData.get("cpce_site"))):new ArrayList<Integer>();
            List<Integer> fileList2=appData.get("st_site")!=null?CommonDataServiceImpl.fileStringToList(String.valueOf(appData.get("st_site"))):new ArrayList<Integer>();
            List<Integer> fileList3=appData.get("cstd_site")!=null?CommonDataServiceImpl.fileStringToList(String.valueOf(appData.get("cstd_site"))):new ArrayList<Integer>();  
            List<Integer> fileList4=appData.get("ts_form")!=null?CommonDataServiceImpl.fileStringToList(String.valueOf(appData.get("ts_form"))):new ArrayList<Integer>();  
            List<Integer> fileList5=appData.get("material")!=null?CommonDataServiceImpl.fileStringToList(String.valueOf(appData.get("material"))):new ArrayList<Integer>();
            List<Integer> fileList6=appData.get("office_site")!=null?CommonDataServiceImpl.fileStringToList(String.valueOf(appData.get("office_site"))):new ArrayList<Integer>();  
            List<Integer> fileList7=appData.get("drawings")!=null?CommonDataServiceImpl.fileStringToList(String.valueOf(appData.get("drawings"))):new ArrayList<Integer>();  
            List<Integer> fileList8=appData.get("mie_inspection")!=null?CommonDataServiceImpl.fileStringToList(String.valueOf(appData.get("mie_inspection"))):new ArrayList<Integer>();
            List<Integer> fileList9=appData.get("cpsa_supervisor")!=null?CommonDataServiceImpl.fileStringToList(String.valueOf(appData.get("cpsa_supervisor"))):new ArrayList<Integer>();  
            List<Integer> fileList10=appData.get("else")!=null?CommonDataServiceImpl.fileStringToList(String.valueOf(appData.get("else"))):new ArrayList<Integer>();  
			List<Integer> fileList11=appData.get("situation_site")!=null?CommonDataServiceImpl.fileStringToList(String.valueOf(appData.get("situation_site"))):new ArrayList<Integer>();

		//把list集合去掉[],转化成list存到数据库中
            appData.put("cpce_site", StringUtils.strip(String.valueOf(fileList1),"[]"));
    		appData.put("st_site", StringUtils.strip(String.valueOf(fileList2),"[]"));
    		appData.put("cstd_site", StringUtils.strip(String.valueOf(fileList3),"[]"));
    		appData.put("ts_form", StringUtils.strip(String.valueOf(fileList4),"[]"));
    		appData.put("material", StringUtils.strip(String.valueOf(fileList5),"[]"));
    		appData.put("office_site", StringUtils.strip(String.valueOf(fileList6),"[]"));
    		appData.put("drawings",StringUtils.strip(String.valueOf(fileList7),"[]"));
    		appData.put("mie_inspection",StringUtils.strip(String.valueOf(fileList8),"[]"));
    		appData.put("cpsa_supervisor",StringUtils.strip(String.valueOf(fileList9),"[]"));
    		appData.put("else",StringUtils.strip(String.valueOf(fileList10),"[]"));
    		appData.put("situation_site",StringUtils.strip(String.valueOf(fileList11),"[]"));
        int result=0;
    	if(appData2==null){ //没有开工信息 插入一条开工信息
    		result=ManageDao.add_project_start(appData);
        }else{  //已经存在开工信息  则修改开工信息
        	result=ManageDao.update_project_start(appData);
        }
        if(result>0){
			return CodeMsg.SUCCESS;
		}else{
			return CodeMsg.OPERATE_ERROR;
		}
        
	}
	
	/**
	 * 启动施工监护
	 * @param project_id
	 * @return
	 */
	@Override
	public CodeMsg start_supervision(int project_id){
		if(CommonDataServiceImpl.updateProjectState(4, project_id)>0){
			return CodeMsg.SUCCESS;
		}else{
			return CodeMsg.OPERATE_ERROR;
		}
	}
	
	/**
	 * 启动预验收
	 * @param project_id
	 * @return
	 */
	@Override
	public CodeMsg start_before_check(int project_id){
		//改为预验收状态
		int a=CommonDataServiceImpl.updateProjectState(5, project_id);
		
		Map<String, Object> sMap=new HashMap<String, Object>();
		sMap.put("project_id",project_id);
		//新增预验收信息
		Map<String,Object> state_data=ManageDao.project_before_check_data(sMap);
		if(state_data==null){
			ManageDao.add_project_before_check(sMap);
		}
		if(a>0){
			return CodeMsg.SUCCESS;
		}else{
			return CodeMsg.OPERATE_ERROR;
		}
	}
	
	
	
	/**
	 * app工程预验收信息
	 */
	@Override
	public Object project_before_check_data(Map<String, Object> map) {
		Map<String, Object> _data=ManageDao.getProjectById(map);  //项目信息
		Map<String, Object> state_data = ManageDao.project_before_check_data(map);  //预验收信息
		if(state_data!=null){
			state_data.put("before_check_bill", commonDao.getFileDescById(Arrays.asList(String.valueOf(state_data.get("before_check_bill")).split(",")))); //把XX转换为list
			state_data.put("issue_list", commonDao.getFileDescById(Arrays.asList(String.valueOf(state_data.get("issue_list")).split(",")))); //把xx转换为list
			state_data.put("problem_spot", commonDao.getFileDescById(Arrays.asList(String.valueOf(state_data.get("problem_spot")).split(",")))); //把xx转换为list
			state_data.put("ppss_form", commonDao.getFileDescById(Arrays.asList(String.valueOf(state_data.get("ppss_form")).split(",")))); //把XX转换为list
			state_data.put("else", commonDao.getFileDescById(Arrays.asList(String.valueOf(state_data.get("else")).split(",")))); //把XX转换为list
		}
		_data.put("before_check_data",state_data);  //把预验收信息存到项目信息中
		return Result.success(_data);
	}
	
	
	/**
	 * app工程预验收保存或提交
	 */
	@Override
	public CodeMsg add_project_before_check(String json) {
        Map<String,Object> appData=JSONObject.parseObject(json); //解析数据
        appData.put("create_by", getUserIdByToken(String.valueOf(appData.get("token")))); //create_by userId
        int state=Integer.parseInt(String.valueOf(appData.get("state")));  //1初始  2保存 3提交
        int project_id=Integer.parseInt(String.valueOf(appData.get("project_id"))); //工程id
        
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
        	//三元运算符  如果不为空 则把图片传入数据库,返回id拼装的list,为空则返回空list
            List<Integer> fileList1=appData.get("before_check_bill")!=null?CommonDataServiceImpl.fileStringToList(String.valueOf(appData.get("before_check_bill"))):new ArrayList<Integer>();
            List<Integer> fileList2=appData.get("issue_list")!=null?CommonDataServiceImpl.fileStringToList(String.valueOf(appData.get("issue_list"))):new ArrayList<Integer>();
            List<Integer> fileList3=appData.get("problem_spot")!=null?CommonDataServiceImpl.fileStringToList(String.valueOf(appData.get("problem_spot"))):new ArrayList<Integer>();  
            List<Integer> fileList4=appData.get("ppss_form")!=null?CommonDataServiceImpl.fileStringToList(String.valueOf(appData.get("ppss_form"))):new ArrayList<Integer>();  
            List<Integer> fileList5=appData.get("else")!=null?CommonDataServiceImpl.fileStringToList(String.valueOf(appData.get("else"))):new ArrayList<Integer>();  
            
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
        if(result>0){
			return CodeMsg.SUCCESS;
		}else{
			return CodeMsg.OPERATE_ERROR;
		}
        
	}
	
	
	/**
	 * app工程验收信息
	 */
	@Override
	public Object project_check_data(Map<String, Object> map) {
		Map<String, Object> _data=ManageDao.getProjectById(map);  //项目信息
		Map<String, Object> state_data = ManageDao.project_check_data(map);  //验收信息
		if(state_data!=null){
			state_data.put("cpsp_doc", commonDao.getFileDescById(Arrays.asList(String.valueOf(state_data.get("cpsp_doc")).split(",")))); //把XX转换为list
			state_data.put("spare_part", commonDao.getFileDescById(Arrays.asList(String.valueOf(state_data.get("spare_part")).split(",")))); //把xx转换为list
			state_data.put("qa_form", commonDao.getFileDescById(Arrays.asList(String.valueOf(state_data.get("qa_form")).split(",")))); //把xx转换为list
			state_data.put("problem_form", commonDao.getFileDescById(Arrays.asList(String.valueOf(state_data.get("problem_form")).split(",")))); //把XX转换为list
			state_data.put("problem_site", commonDao.getFileDescById(Arrays.asList(String.valueOf(state_data.get("problem_site")).split(",")))); //把XX转换为list
			state_data.put("else", commonDao.getFileDescById(Arrays.asList(String.valueOf(state_data.get("else")).split(",")))); //把XX转换为list
		}
		_data.put("check_data",state_data);  //把预验收信息存到项目信息中
		return Result.success(_data);
	}
	
	
	/**
	 * app工程验收保存或提交
	 */
	@Override
	public CodeMsg add_project_check(String json) {
        Map<String,Object> appData=JSONObject.parseObject(json); //解析数据
        appData.put("create_by", getUserIdByToken(String.valueOf(appData.get("token")))); //create_by userId
        int state=Integer.parseInt(String.valueOf(appData.get("state")));  //1初始  2保存 3提交
        int project_id=Integer.parseInt(String.valueOf(appData.get("project_id"))); //工程id
        
        Map<String,Object> pMap=new HashMap<>();
        pMap.put("project_id", project_id);
        Map<String,Object> appData2=ManageDao.project_check_data(pMap);  //工程验收信息 没有null
        
        if(state==3){  //如果是提交  进入归档阶段
        	CommonDataServiceImpl.updateProjectState(7,project_id);
        }
        	//三元运算符  如果不为空 则把图片传入数据库,返回id拼装的list,为空则返回空list
            List<Integer> fileList1=appData.get("cpsp_doc")!=null?CommonDataServiceImpl.fileStringToList(String.valueOf(appData.get("cpsp_doc"))):new ArrayList<Integer>();
            List<Integer> fileList2=appData.get("spare_part")!=null?CommonDataServiceImpl.fileStringToList(String.valueOf(appData.get("spare_part"))):new ArrayList<Integer>();
            List<Integer> fileList3=appData.get("qa_form")!=null?CommonDataServiceImpl.fileStringToList(String.valueOf(appData.get("qa_form"))):new ArrayList<Integer>();  
            List<Integer> fileList4=appData.get("problem_form")!=null?CommonDataServiceImpl.fileStringToList(String.valueOf(appData.get("problem_form"))):new ArrayList<Integer>();  
            List<Integer> fileList5=appData.get("problem_site")!=null?CommonDataServiceImpl.fileStringToList(String.valueOf(appData.get("problem_site"))):new ArrayList<Integer>();  
            List<Integer> fileList6=appData.get("else")!=null?CommonDataServiceImpl.fileStringToList(String.valueOf(appData.get("else"))):new ArrayList<Integer>();  
            
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
        if(result>0){
			return CodeMsg.SUCCESS;
		}else{
			return CodeMsg.OPERATE_ERROR;
		}
	}
	
	
	/**
	 * app工程结算类型列表
	 */
	@Override
	public Object project_settlement_type_list(Map<String, Object> map) {
		return Result.success(ManageDao.project_settlement_type_list(map));
	}
	
	/**
	 * app工程结算信息
	 */
	@Override
	public Object project_settlement_detail(Map<String, Object> map) {
		Map<String, Object> _data=ManageDao.getProjectById(map);  //项目信息
		Map<String, Object> project_settlement_detail = ManageDao.project_settlement_detail(map);  //结算信息
		if(project_settlement_detail!=null){
			project_settlement_detail.put("receipt", commonDao.getFileDescById(Arrays.asList(String.valueOf(project_settlement_detail.get("receipt")).split(",")))); //把XX转换为list
			project_settlement_detail.put("refund", commonDao.getFileDescById(Arrays.asList(String.valueOf(project_settlement_detail.get("refund")).split(",")))); //把XX转换为list
			project_settlement_detail.put("payment", commonDao.getFileDescById(Arrays.asList(String.valueOf(project_settlement_detail.get("payment")).split(",")))); //把XX转换为list
			if(Integer.parseInt(String.valueOf(project_settlement_detail.get("id")))==ManageDao.project_settlement_lastId(map)){
				project_settlement_detail.put("is_and_more", 0); //当前id=最小id时 不在有加载更多
				}else{
				project_settlement_detail.put("is_and_more", 1);
				}
		}
		_data.put("settlement_data",project_settlement_detail);  //把结算信息存到项目信息中
		
		return Result.success(_data);
	}
	
	/**
	 * app加载更多工程结算
	 */
	@Override
	public Object loadmore_project_settlement(Map<String, Object> map) {
		Map<String, Object> project_settlement_detail = ManageDao.project_settlement_detail(map);  //结算信息
		project_settlement_detail.put("receipt", commonDao.getFileDescById(Arrays.asList(String.valueOf(project_settlement_detail.get("receipt")).split(",")))); //把XX转换为list
		project_settlement_detail.put("refund", commonDao.getFileDescById(Arrays.asList(String.valueOf(project_settlement_detail.get("refund")).split(",")))); //把XX转换为list
		project_settlement_detail.put("payment", commonDao.getFileDescById(Arrays.asList(String.valueOf(project_settlement_detail.get("payment")).split(",")))); //把XX转换为list
		
		if(Integer.parseInt(String.valueOf(project_settlement_detail.get("id")))==ManageDao.project_settlement_lastId(map)){
		project_settlement_detail.put("is_and_more", 0); //当前id=最小id时 不在有加载更多
		}else{
		project_settlement_detail.put("is_and_more", 1);
		}
		return Result.success(project_settlement_detail);
	}
	
	/**
	 * 合同总额
	 */
	@Override
	public Object project_contract_amount(Map<String, Object> map) {
		Map<String, Object> data = ManageDao.project_contract_amount(map);
	    if(data==null||data.get("contract_amount")==null){
	    	data=new HashMap<>();
	    	data.put("contract_amount", null);
	    }
		return Result.success(data);
	}
	
	/**
	 * app工程结算提交
	 */
	@Override
	public CodeMsg add_project_settlement(String json) {
        Map<String,Object> appData=JSONObject.parseObject(json); //解析数据
        appData.put("create_by", getUserIdByToken(String.valueOf(appData.get("token")))); //create_by userId
        appData.put("state", 2); //app端只有提交
       
        	//三元运算符  如果不为空 则把图片传入数据库,返回id拼装的list,为空则返回空list
            List<Integer> fileList1=appData.get("receipt")!=null?CommonDataServiceImpl.fileStringToList(String.valueOf(appData.get("receipt"))):new ArrayList<Integer>();
            List<Integer> fileList2=appData.get("refund")!=null?CommonDataServiceImpl.fileStringToList(String.valueOf(appData.get("refund"))):new ArrayList<Integer>();
            List<Integer> fileList3=appData.get("payment")!=null?CommonDataServiceImpl.fileStringToList(String.valueOf(appData.get("payment"))):new ArrayList<Integer>();  
            
            //把list集合去掉[],转化成list存到数据库中
            appData.put("receipt", StringUtils.strip(String.valueOf(fileList1),"[]"));
    		appData.put("refund", StringUtils.strip(String.valueOf(fileList2),"[]"));
    		appData.put("payment", StringUtils.strip(String.valueOf(fileList3),"[]"));
        int result=ManageDao.add_project_settlement(appData);  //新增工程结算
        
        if(String.valueOf(appData.get("type_id")).equals("3")){   //工程结算款
        	Map<String,Object> amountMap=new HashMap<String,Object>();
        	amountMap.put("project_id", appData.get("project_id"));
        	if(ManageDao.project_contract_amount(amountMap)!=null&&appData.get("contract_amount")!=null&&!appData.get("contract_amount").equals("")){  //有工程款这一项
        		amountMap.put("contract_amount", appData.get("contract_amount"));
            	ManageDao.update_project_contract_amount(amountMap);
        	}
        }
        
        if(result>0){
			return CodeMsg.SUCCESS;
		}else{
			return CodeMsg.OPERATE_ERROR;
		}
	}
	
	
	/** 
	 * app施工中信息
	 */
	@Override
	public Object construction_data(Map<String, Object> map) {
		if(map.get("department_id")==null||map.get("project_id")==null){
			return CodeMsg.MISSING_PARAMETER;
		}
		Map<String, Object> _data=ManageDao.getProjectById(map);  //项目信息
        String time=dateUtil.parseDateToStr(new Date(),dateUtil.DATE_FORMAT_YYYY_MM_DD);
        String beginTime=time+" 02:00:00";
        String endTime=dateUtil.getLastDayOfTime(beginTime,dateUtil.DATE_FORMAT_YYYY_MM_DD_HH_MI_SS);
        map.put("beginTime",beginTime);
        map.put("endTime",endTime);
        Map<String, Object> supervision_data=new LinkedHashMap <>(); //上报信息
        supervision_data.put("create_at",time);
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
        _data.put("supervision_data", supervision_data);
        return Result.success(_data);
	}
	
	/**
	 * app工程详情
	 */
	@Override
	public Object project_detail(Map<String, Object> map) {
		if(map.get("department_id")==null||map.get("project_id")==null){
			return CodeMsg.MISSING_PARAMETER;
		}

		int project_id=Integer.parseInt(String.valueOf(map.get("project_id")));
		Map<String, Object> _data=ManageDao.getProjectById(map);  //项目信息

		Map<String,Object> pMap=new HashMap<>();
        pMap.put("project_id", project_id);
        pMap.put("detail_data", 1);
		Map<String, Object> check_data = ManageDao.project_check_data(pMap);  //验收信息
		if(check_data!=null){
			check_data.put("cpsp_doc", commonDao.getFileDescById(Arrays.asList(String.valueOf(check_data.get("cpsp_doc")).split(",")))); //把XX转换为list
			check_data.put("spare_part", commonDao.getFileDescById(Arrays.asList(String.valueOf(check_data.get("spare_part")).split(",")))); //把xx转换为list
			check_data.put("qa_form", commonDao.getFileDescById(Arrays.asList(String.valueOf(check_data.get("qa_form")).split(",")))); //把xx转换为list
			check_data.put("problem_form", commonDao.getFileDescById(Arrays.asList(String.valueOf(check_data.get("problem_form")).split(",")))); //把XX转换为list
			check_data.put("problem_site", commonDao.getFileDescById(Arrays.asList(String.valueOf(check_data.get("problem_site")).split(",")))); //把XX转换为list
			check_data.put("else", commonDao.getFileDescById(Arrays.asList(String.valueOf(check_data.get("else")).split(",")))); //把XX转换为list
		}
		_data.put("check_data",check_data);  //把预验收信息存到项目信息中
		
		
		Map<String, Object> before_check_data = ManageDao.project_before_check_data(pMap);  //预验收信息
		if(before_check_data!=null){
			before_check_data.put("before_check_bill", commonDao.getFileDescById(Arrays.asList(String.valueOf(before_check_data.get("before_check_bill")).split(",")))); //把XX转换为list
			before_check_data.put("issue_list", commonDao.getFileDescById(Arrays.asList(String.valueOf(before_check_data.get("issue_list")).split(",")))); //把xx转换为list
			before_check_data.put("problem_spot", commonDao.getFileDescById(Arrays.asList(String.valueOf(before_check_data.get("problem_spot")).split(",")))); //把xx转换为list
			before_check_data.put("ppss_form", commonDao.getFileDescById(Arrays.asList(String.valueOf(before_check_data.get("ppss_form")).split(",")))); //把XX转换为list
			before_check_data.put("else", commonDao.getFileDescById(Arrays.asList(String.valueOf(before_check_data.get("else")).split(",")))); //把XX转换为list
		}
		_data.put("before_check_data",before_check_data);  //把预验收信息存到项目信息中


		String time=dateUtil.parseDateToStr(new Date(),dateUtil.DATE_FORMAT_YYYY_MM_DD);
		String beginTime=time+" 02:00:00";
		String endTime=dateUtil.getLastDayOfTime(beginTime,dateUtil.DATE_FORMAT_YYYY_MM_DD_HH_MI_SS);
		map.put("beginTime",beginTime);
		map.put("endTime",endTime);
		Map<String, Object> supervision_data=new LinkedHashMap <>(); //上报信息
		supervision_data.put("create_at",time);
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
			_data.put("supervision_data", supervision_data);

		Map<String, Object> project_start_data = ManageDao.project_start_data(pMap);  //开工信息
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
		_data.put("start_data",project_start_data);  //把开工信息存到项目信息中
		
		
		Map<String, Object> early_data = ManageDao.project_early_data(pMap);  //前期信息
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
		_data.put("early_data",early_data);  //把前期信息存到项目信息中
		
		return Result.success(_data);
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

	
}
