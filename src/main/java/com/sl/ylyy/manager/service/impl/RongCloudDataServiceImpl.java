package com.sl.ylyy.manager.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.google.gson.Gson;
import com.sl.ylyy.app_1.dao.RoleMapper;
import com.sl.ylyy.app_1.dao.UserMapper;
import com.sl.ylyy.common.config.UrlConfig;
import com.sl.ylyy.common.utils.CodeMsg;
import com.sl.ylyy.common.utils.Result;
import com.sl.ylyy.common.utils.RongUtil;
import com.sl.ylyy.common.utils.JwtToken;
import com.sl.ylyy.manager.dao.RongCloudDao;
import com.sl.ylyy.manager.dao.SystemDao;
import com.sl.ylyy.manager.dao.UserDao;
import com.sl.ylyy.manager.entity.GroupNotificationMessage;
import com.sl.ylyy.manager.entity.GroupNotificationMessageData;
import com.sl.ylyy.manager.service.RongCloudDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

import static com.sl.ylyy.common.config.UrlConfig.GroupPortrait;
import static com.sl.ylyy.common.utils.JwtToken.*;


@SuppressWarnings({"AlibabaUndefineMagicConstant", "AlibabaRemoveCommentedCode", "AlibabaLowerCamelCaseVariableNaming"})
@Service("rongCloudDataImpl")
public class RongCloudDataServiceImpl implements RongCloudDataService {

	
	@Autowired
	private UserDao userDao;
	@Autowired
	private SystemDao systemDao;
	@Autowired
	private RongCloudDao rongCloudDao;
	@Autowired
	RongUtil rongUtil;
	@Autowired
	UserMapper userMapper;
	@Autowired
	RoleMapper roleMapper;


	/**
	 * 建立聊天关系
	 * @return
	 */
	@Override
	public CodeMsg chat_build(Map<String,Object> map){
		int result=0;
		if(map.get("rong_accept_id")!=null){   //与人
			Map<String,Object> userMap=new HashMap<>();
			map.put("rong_group_id",null);
			userMap.put("rong_send_id",map.get("rong_accept_id")); //发送人
			userMap.put("rong_accept_id",map.get("rong_send_id")); //接收人
			userMap.put("rong_group_id",null);
			if(rongCloudDao.chat_list(map).size()==0){
				result=rongCloudDao.chat_build(map); //建立关系
				if(rongCloudDao.chat_list(userMap).size()==0){
					rongCloudDao.chat_build(userMap); //建立双向关系
				}else{
					rongCloudDao.chat_update(userMap); //修改时间
				}
			}else{
				result=rongCloudDao.chat_update(map); //修改时间
				if(rongCloudDao.chat_list(userMap).size()==0){
					rongCloudDao.chat_build(userMap); //建立双向关系
				}else{
					rongCloudDao.chat_update(userMap); //修改时间
				}
			}
		}else if(map.get("rong_group_id")!=null){   //与群组
			map.put("rong_accept_id",null);
			if(rongCloudDao.chat_list(map).size()==0){
				result=rongCloudDao.chat_build(map); //建立关系
			}else{
				result=rongCloudDao.chat_update(map); //修改时间
			}
		}
		if (result>0){
			return CodeMsg.SUCCESS;
		}else{
			return CodeMsg.OPERATE_ERROR;
		}
	}


	/**
	 * 删除聊天关系
	 * @return
	 */
	@Override
	public CodeMsg chat_delete(Map<String,Object> map){
		int result=0;
		if(map.get("rong_accept_id")!=null){   //与人
				result=rongCloudDao.chat_delete(map); //删除关系
		}else if(map.get("rong_group_id")!=null){   //与群组
				result=rongCloudDao.chat_delete(map); //删除关系
		}

		if (result>0){
			return CodeMsg.SUCCESS;
		}else{
			return CodeMsg.OPERATE_ERROR;
		}
	}

	/**
	 * 聊天列表
	 * @return
	 */
	@Override
	public Result chat_list(Map<String,Object> map){
		map.put("rong_send_id", getRongUserIdByToken(String.valueOf(map.get("token"))));
		List<Map<String,Object>> userList=rongCloudDao.chat_list(map);
		return Result.success(userList);
	}

	/**
	 * 用户基本信息(发送聊天页面)
	 * @return
	 */
	@Override
	public Result user_detail(Map<String, Object> map){
		Map<String,Object> userMap=rongCloudDao.user_detail(String.valueOf(map.get("rong_user_id")));
		if(map.get("is_latest")!=null&&!"".equals(String.valueOf(map.get("is_latest")))){
			Map<String,Object> userResult = userMapper.selectUserMap(Integer.parseInt(String.valueOf(userMap.get("user_id"))));
//			userResult.put("project_id",36); //默认高新 项目
//			String token = JwtToken.getWebToken(userResult);
			//token内容
			JSONObject tokenData = new JSONObject();
			tokenData.put("user_id",userResult.get("user_id"));
//			tokenData.put("rong_user_id",userResult.get("rong_user_id"));
			tokenData.put("role_id",userResult.get("role_id"));
			tokenData.put("role_name",userResult.get("role_name"));
			tokenData.put("company_id",userResult.get("company_id"));
			tokenData.put("department_id",userResult.get("department_id"));
			tokenData.put("group_id",userResult.get("group_id"));
			tokenData.put("portrait",userResult.get("portrait"));
			tokenData.put("user_name",userResult.get("user_name"));
			tokenData.put("cellphone",userResult.get("cellphone"));
//			tokenData.put("notice_rong_group_id",userResult.get("notice_rong_group_id"));
//			tokenData.put("company_rong_group_id",userResult.get("company_rong_group_id"));
//			tokenData.put("rong_token",userResult.get("rong_token"));
			tokenData.put("department_name",userResult.get("department_name"));
			tokenData.put("group_name",userResult.get("group_name"));
//			tokenData.put("role_name",userResult.get("role_name"));
			tokenData.put("company_name",userResult.get("company_name"));

			String token =JwtToken.getWebToken(tokenData);
			userMap.put("token",token);
		}

		return Result.success(userMap);
	}





	//=========================group============================//
	/**
	 * 创建群组
	 * @return
	 */
	@Override
	public Object group_create(String json){
		//解析数据
		Map<String,Object> appData= JSONObject.parseObject(json);
		int userId=getUserIdByToken(String.valueOf(appData.get("token")));
		appData.put("create_by", userId);
		appData.put("rong_user_id", getRongUserIdByToken(String.valueOf(appData.get("token"))));
		appData.put("is_structure",2);  //不是公司架构
		if(appData.get("group_portrait")==null|| "".equals(String.valueOf(appData.get("group_portrait")))){
			//默认群头像
			appData.put("group_portrait",GroupPortrait);
		}
		//建立群组
		int result=rongCloudDao.group_insert(appData);
		appData.put("rong_group_id", UrlConfig.RONG_ID_PREFIX+appData.get("group_id"));
		//添加融云id
		rongCloudDao.group_update(appData);
		RongUtil.groupCreate(appData);
		//发送创建群的通知
		Gson gosn = new Gson();
		GroupNotificationMessageData gn=new GroupNotificationMessageData();
		List<String> list=new ArrayList<>();
		String jsonGn ="";
		gn.setOperatorNickname(rongCloudDao.getUserNameById(null,getRongUserIdByToken(String.valueOf(appData.get("token")))));
        GroupNotificationMessage message=new GroupNotificationMessage(getRongUserIdByToken(String.valueOf(appData.get("token"))),GroupNotificationMessage.GROUP_OPERATION_CREATE,gosn.toJson(gn));
		String jsonMessage="";
		jsonMessage=gosn.toJson(message);
		RongUtil.groupPublish(String.valueOf(appData.get("rong_group_id")),jsonMessage);

		JSONArray user_list = JSONArray.parseArray(appData.get("userList").toString());
		List<String> Namelist=new ArrayList<>();
		List<String> Idlist=new ArrayList<>();
		for(int i=0;i< user_list.size();i++){
			Map<String,Object> userData= JSONObject.parseObject(String.valueOf(user_list.get(i))); //解析数据
			userData.put("rong_group_id",appData.get("rong_group_id"));
			if(rongCloudDao.group_user_select(userData).size()==0){  //校验是否已经加群
				rongCloudDao.group_user_insert(userData);
				if(!String.valueOf(userData.get("rong_user_id")).equals(getRongUserIdByToken(String.valueOf(appData.get("token"))))){
					Idlist.add(String.valueOf(userData.get("rong_user_id")));
					Namelist.add(rongCloudDao.getUserNameById(null,String.valueOf(userData.get("rong_user_id"))));
				}
			}
		}
		gn.setTargetUserIds(Idlist);
		gn.setTargetUserDisplayNames(Namelist);
		jsonGn=gosn.toJson(gn);
		message=new GroupNotificationMessage(getRongUserIdByToken(String.valueOf(appData.get("token"))),GroupNotificationMessage.GROUP_OPERATION_ADD,jsonGn);
		jsonMessage= gosn.toJson(message);
		RongUtil.groupPublish(String.valueOf(appData.get("rong_group_id")),jsonMessage);




		if (result>0){
			return Result.success(rongCloudDao.group_select(String.valueOf(appData.get("rong_group_id")),null));
		}else{
			return CodeMsg.OPERATE_ERROR;
		}
	}





	/**
	 * 修改群组信息
	 * @param
	 * @return
	 */
	@Override
	public Object group_update(Map<String,Object> map){
			map.put("rong_user_id",getRongUserIdByToken(String.valueOf(map.get("token"))));
			int result=CommonDataServiceImpl.rong_group_update(map);
		if (result>0){
			return Result.success(rongCloudDao.group_select(String.valueOf(map.get("rong_group_id")),null));
		}else{
			return CodeMsg.OPERATE_ERROR;
		}
	}


	/**
	 * 发布群公告
	 */
	@Override
	public CodeMsg group_notice_insert(Map<String,Object> map){
		map.put("create_by", getUserIdByToken(String.valueOf(map.get("token")))); //send_id userId
		int result=rongCloudDao.group_notice_insert(map); //发布群公告
		if (result>0){
			//发送系统通知
			Gson gosn = new Gson();
			GroupNotificationMessageData gn=new GroupNotificationMessageData();
			gn.setOperatorNickname(rongCloudDao.getUserNameById(null,getRongUserIdByToken(String.valueOf(map.get("token")))));
			GroupNotificationMessage message=new GroupNotificationMessage(getRongUserIdByToken(String.valueOf(map.get("token"))),GroupNotificationMessage.GROUP_OPERATION_BULLETIN,gosn.toJson(gn));
			String jsonMessage= gosn.toJson(message);
			RongUtil.groupPublish(String.valueOf(map.get("rong_group_id")),jsonMessage);

			return CodeMsg.SUCCESS;
		}else{
			return CodeMsg.OPERATE_ERROR;
		}
	}
	/**
	 * 修改群公告
	 */
	@Override
	public CodeMsg group_notice_update(Map<String,Object> map){
		map.put("create_by", getUserIdByToken(String.valueOf(map.get("token")))); //send_id userId
		int result=rongCloudDao.group_notice_update(map); //发布群公告
		if (result>0){
			return CodeMsg.SUCCESS;
		}else{
			return CodeMsg.OPERATE_ERROR;
		}
	}

	/**
	 * 获取最新群公告
	 */
	@Override
	public Result group_active_notice(Map<String,Object> map){
		Map<String,Object> map2 =new HashMap<>();
		if(rongCloudDao.group_notice_list(map).size()>0){
			map2 =rongCloudDao.group_notice_list(map).get(0); //获取最新群公告
		}
			return Result.success(map2);
	}

	/**
	 * 获取所有群公告
	 */
	@Override
	public Result group_notice_list(Map<String,Object> map){
		List<Map<String,Object>> list =rongCloudDao.group_notice_list(map); //获取所有群公告
			return Result.success(list);
	}

	/**
	 * 解散群
	 */
	@Override
	public CodeMsg group_dismiss(Map<String,Object> map){
		int userId=getUserIdByToken(String.valueOf(map.get("token")));
		map.put("dismiss_by",userId);
		map.put("rong_user_id",getRongUserIdByToken(String.valueOf(map.get("token"))));
		int result=CommonDataServiceImpl.rong_group_dismiss(map);  //解散群公共方法
		if (result>0){
			return CodeMsg.SUCCESS;
		}else{
			return CodeMsg.OPERATE_ERROR;
		}
	}


	/**
	 * 加入群
	 */
	@Override
	public CodeMsg group_join(String json){
		Map<String,Object> appData= JSONObject.parseObject(json); //解析数据
		int result =0;
		Map<String,Object> gruopData=rongCloudDao.group_select(String.valueOf(appData.get("rong_group_id")),null);
		appData.put("group_name",gruopData.get("group_name"));
		JSONArray user_list = JSONArray.parseArray(appData.get("userList").toString());
		RongUtil.groupJoin(appData);
		List<String> Namelist=new ArrayList<>();
		List<String> Idlist=new ArrayList<>();
		for(int i=0;i< user_list.size();i++){
			Map<String,Object> userData= JSONObject.parseObject(String.valueOf(user_list.get(i))); //解析数据
			userData.put("rong_group_id",appData.get("rong_group_id"));
			if(rongCloudDao.group_user_select(userData).size()==0){  //校验是否已经加群
				result=rongCloudDao.group_user_insert(userData);
				Idlist.add(String.valueOf(userData.get("rong_user_id")));
				Namelist.add(rongCloudDao.getUserNameById(null,String.valueOf(userData.get("rong_user_id"))));
			}
		}
		GroupNotificationMessageData gn=new GroupNotificationMessageData();
		Gson gosn = new Gson();
		gn.setOperatorNickname(rongCloudDao.getUserNameById(null,getRongUserIdByToken(String.valueOf(appData.get("token")))));
		gn.setTargetUserIds(Idlist);
		gn.setTargetUserDisplayNames(Namelist);
		String jsonGn=gosn.toJson(gn);
		GroupNotificationMessage message=new GroupNotificationMessage(getRongUserIdByToken(String.valueOf(appData.get("token"))),GroupNotificationMessage.GROUP_OPERATION_ADD,jsonGn);
		String jsonMessage= gosn.toJson(message);
		RongUtil.groupPublish(String.valueOf(appData.get("rong_group_id")),jsonMessage);
		if (result>0){
			return CodeMsg.SUCCESS;
		}else{
			return CodeMsg.OPERATE_ERROR;
		}
	}


	/**
	 * 退出群
	 */
	@Override
	public CodeMsg group_quit(String json){
		Map<String,Object> appData= JSONObject.parseObject(json); //解析数据
		int result =0;
		JSONArray user_list = JSONArray.parseArray(appData.get("userList").toString());

		GroupNotificationMessageData gn=new GroupNotificationMessageData();
		GroupNotificationMessage message=null;
		Gson gosn = new Gson();
		List<String> list=new ArrayList<>();
		String jsonGn="";
		String jsonMessage="";
		List<String> Namelist=new ArrayList<>();
		List<String> Idlist=new ArrayList<>();
		int is_self=0;
		for(int i=0;i< user_list.size();i++){
			Map<String,Object> userData= JSONObject.parseObject(String.valueOf(user_list.get(i))); //解析数据
			userData.put("rong_group_id",appData.get("rong_group_id"));
			if(rongCloudDao.group_user_select(userData).size()!=0){  //校验是否已经加群
				result=rongCloudDao.group_user_delete(userData);
				//如果退出的是本人
				if(String.valueOf(userData.get("rong_user_id")).equals(getRongUserIdByToken(String.valueOf(appData.get("token"))))){
					message=new GroupNotificationMessage(getRongUserIdByToken(String.valueOf(appData.get("token"))),GroupNotificationMessage.GROUP_OPERATION_QUIT,null);
					jsonMessage= gosn.toJson(message);
					RongUtil.groupPublish(String.valueOf(appData.get("rong_group_id")),jsonMessage);
					is_self=1;
				}else{
					Idlist.add(String.valueOf(userData.get("rong_user_id")));
					Namelist.add(rongCloudDao.getUserNameById(null,String.valueOf(userData.get("rong_user_id"))));
				}
			}
		}
		if(is_self==0){
			gn.setTargetUserIds(Idlist);
			gn.setTargetUserDisplayNames(Namelist);
			gn.setOperatorNickname(rongCloudDao.getUserNameById(null,getRongUserIdByToken(String.valueOf(appData.get("token")))));
			jsonGn=gosn.toJson(gn);
			message=new GroupNotificationMessage(getRongUserIdByToken(String.valueOf(appData.get("token"))),GroupNotificationMessage.GROUP_OPERATION_KICKED,jsonGn);
			jsonMessage= gosn.toJson(message);
			RongUtil.groupPublish(String.valueOf(appData.get("rong_group_id")),jsonMessage);
		}
		if (result>0){
			RongUtil.groupQuit(appData);
			return CodeMsg.SUCCESS;
		}else{
			return CodeMsg.OPERATE_ERROR;
		}
	}

	/**
	 * 展示用户所加的群组
	 */
	@Override
	public Result group_list_ByUser(Map<String,Object> map){
		map.put("rong_user_id", getRongUserIdByToken(String.valueOf(map.get("token")))); //send_id userId
		List<Map<String,Object>> groupList=rongCloudDao.group_list_ByUser(map); //展示用户所加的群组
			return Result.success(groupList);
		}

	/**
	 * 群内群员列表
	 */
	@Override
    public Result user_list_ByGroup(Map<String,Object> map){
		List<Map<String,Object>> userList=rongCloudDao.user_list_ByGroup(map); //展示用户所加的群组
			return Result.success(userList);
		}


	/**
	 * 群信息
	 */
	@Override
	public Result group_detail(String rong_group_id){
		Map<String,Object> groupMap=rongCloudDao.group_select(rong_group_id,null);
		Map<String,Object> map =new HashMap<>();
		map.put("rong_group_id",rong_group_id);
		Map<String,Object> map2 =new HashMap<>();
		if(rongCloudDao.group_notice_list(map).size()>0){
			map2 =rongCloudDao.group_notice_list(map).get(0); //获取最新群公告
		}
		groupMap.put("notice",map2);
		return Result.success(groupMap);
	}

	/**
	 * 所有用户列表
	 */
	@Override
	public Result user_list(Map<String,Object> map){
        List<Map<String,Object>> userList=rongCloudDao.user_list(map);
	    if(map.get("rong_group_id")!=null){  //群组 选联系人
            List<Map<String,Object>> groupuserList=rongCloudDao.group_user_select(map); //已经在群组中的人
            for(int i=0;i<userList.size();i++){
                userList.get(i).put("is_join",0);
                for(int j=0;j<groupuserList.size();j++){
                    if(String.valueOf(groupuserList.get(j).get("rong_user_id")).equals(String.valueOf(userList.get(i).get("rong_user_id")))){
                        userList.get(i).put("is_join",1);
                    }
                }

            }
        }
            return Result.success(userList);
		}

	/**
	 * 工作台类型列表
	 */
	@Override
	public Result work_type_list(Map<String,Object> map){
		List<Map<String,Object>> typeList=rongCloudDao.work_type_list(map);
			return Result.success(typeList);
		}

	/**
	 * 工作提醒类型列表
	 */
	@Override
	public Result work_remind_list(Map<String,Object> map){
		List<Map<String,Object>> remindList=rongCloudDao.work_remind_list(map);
			return Result.success(remindList);
		}
	/**
	 * 新增任务
	 */
	@Override
	public CodeMsg work_add(Map<String,Object> map){
		int result=rongCloudDao.work_add(map);
		if (result>0){
			return CodeMsg.SUCCESS;
		}else{
			return CodeMsg.OPERATE_ERROR;
		}
		}


	/**
	 * 工作任务列表 通过日期查询
	 */
	@Override
	public Result work_list_ByDate(Map<String,Object> map){

		if(map.get("execution_at")==null){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");   //把时间转换成年月日
			map.put("execution_at",sdf.format(new Date()));  //默认今天
		}
		map.put("beginTime",map.get("execution_at")+" 00:00:00");
		map.put("endTime",map.get("execution_at")+" 24:00:00");
		map.put("all_by",getUserIdByToken(String.valueOf(map.get("token"))));
		List<Map<String,Object>> workList=rongCloudDao.work_list_ByDate(map);
		for (int i=0;i<workList.size();i++){ //执行人  和  抄送人
			List<String> user_list = Arrays.asList(String.valueOf(workList.get(i).get("execute_by")).split(","));
			workList.get(i).put("execute_by",rongCloudDao.getUserByList(user_list));

			List<String> user_list2 = Arrays.asList(String.valueOf(workList.get(i).get("copy_by")).split(","));
			workList.get(i).put("copy_by",rongCloudDao.getUserByList(user_list2));
		}

		return Result.success(workList);
	}

	/**
	 * 工作任务列表 通过用户查询
	 */
	@Override
	public Result work_list_ByUser(Map<String,Object> map){
		Map<String,Object> result=new LinkedHashMap<>();
		Map<String,Object> user=getUserByWebToken(String.valueOf(map.get("token")));
		result.put("user_id",user.get("user_id"));
		result.put("user_name",user.get("user_name"));
		result.put("cellphone",user.get("cellphone"));
		result.put("rong_user_id",user.get("rong_user_id"));
		map.put("all_by",user.get("user_id"));
		List<Map<String,Object>> allList=rongCloudDao.work_list_ByUser(map);
		for (int i=0;i<allList.size();i++){ //执行人  和  抄送人
			List<String> user_list = Arrays.asList(String.valueOf(allList.get(i).get("execute_by")).split(","));
			allList.get(i).put("is_execute",0);
			for(int j=0;j<user_list.size();j++){
					if(user_list.get(j).equals(String.valueOf(getUserIdByToken(String.valueOf(map.get("token")))))){
						allList.get(i).put("is_execute",1);
					}
				}
			allList.get(i).put("execute_by",rongCloudDao.getUserByList(user_list));
			List<String> user_list2 = Arrays.asList(String.valueOf(allList.get(i).get("copy_by")).split(","));
			allList.get(i).put("copy_by",rongCloudDao.getUserByList(user_list2));
		}
		result.put("workList",allList);
		return Result.success(result);







		/*for (int i=0;i<executeList.size();i++){ //执行人  和  抄送人
			List<String> user_list = Arrays.asList(String.valueOf(executeList.get(i).get("execute_by")).split(","));
			executeList.get(i).put("execute_by",rongCloudDao.getUserByList(user_list));

			List<String> user_list2 = Arrays.asList(String.valueOf(executeList.get(i).get("copy_by")).split(","));
			executeList.get(i).put("copy_by",rongCloudDao.getUserByList(user_list2));
		}

		map.remove("execute_by");
		map.put("copy_by",getUserIdByToken(String.valueOf(map.get("token"))));
		List<Map<String,Object>> copyList=rongCloudDao.work_list_ByUser(map);
		for (int i=0;i<copyList.size();i++){ //执行人  和  抄送人
			List<String> user_list = Arrays.asList(String.valueOf(copyList.get(i).get("execute_by")).split(","));
			copyList.get(i).put("execute_by",rongCloudDao.getUserByList(user_list));

			List<String> user_list2 = Arrays.asList(String.valueOf(copyList.get(i).get("copy_by")).split(","));
			copyList.get(i).put("copy_by",rongCloudDao.getUserByList(user_list2));
		}
		result.put("copyData",copyList);
		result.put("executeData",executeList);*/

	}

	/**
	 * 工作台列表 通过日期查询
	 */
	@Override
	public Result Workbench_ByDate(Map<String,Object> map){
		Map<String,Object> workbench=new HashMap<>(3);
		Map<String,Object> user=getUserByWebToken(String.valueOf(map.get("token")));
		if(map.get("execution_at")==null){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");   //把时间转换成年月日
			map.put("execution_at",sdf.format(new Date()));  //默认今天
		}
		map.put("beginTime",map.get("execution_at")+" 00:00:00");
		map.put("endTime",map.get("execution_at")+" 24:00:00");
		map.put("all_by",user.get("user_id"));
		map.put("department_id",user.get("department_id"));
		List<Map<String,Object>> workList=rongCloudDao.work_list_ByDate(map);
		for (int i=0;i<workList.size();i++){ //执行人  和  抄送人
			List<String> user_list = Arrays.asList(String.valueOf(workList.get(i).get("execute_by")).split(","));
			workList.get(i).put("execute_by",rongCloudDao.getUserByList(user_list));
			List<String> user_list2 = Arrays.asList(String.valueOf(workList.get(i).get("copy_by")).split(","));
			workList.get(i).put("copy_by",rongCloudDao.getUserByList(user_list2));
		}
		workbench.put("workList",workList); //工作任务
		workbench.put("supervisionlist",supervision_list(map)); //监护列表
		workbench.put("malfunctionList",getMalfunctionList(map)); //故障列表

		return Result.success(workbench);
	}


	public Map<String,Object> getMalfunctionList(Map<String, Object> params) {
		//List<Integer> fixBys = getfixBysByRoleId((String)params.get("token"));
		List<Integer> fixBys = new ArrayList<>();
		fixBys.add(getUserIdByToken(String.valueOf(params.get("token"))));
		Map<String,Object> user = getUserByWebToken((String)params.get("token"));
		Integer companyId = (Integer)user.get("company_id");
		int uid = (Integer)user.get("user_id");
		Integer rid = roleMapper.selectRidByUid((Integer)user.get("user_id"));
		//判断登陆用户是否有部门编号
		Integer departmentId = user.get("department_id")==null?null:(Integer)user.get("department_id");
		//故障状态，默认1
		int status =(params.get("status")==null)?1:Integer.parseInt(params.get("status").toString());
		List<Integer> statusList = new ArrayList<>();
		if(status==1){
			statusList = Arrays.asList(1,2,3,4,7,8);
		}else{
			statusList = Arrays.asList(5,6);
		}
		//最后一条数据的创建时间时间戳，默认0
		Integer create_at = (params.get("create_at")==null||Integer.parseInt((String)params.get("create_at"))==0)?
				null:Integer.parseInt(params.get("create_at").toString());
		//每页条数，默认为20
		int limitNum = (params.get("limit_num")!=null)?Integer.parseInt(params.get("limit_num").toString()):20;
		int total = 0;
		if(rid==1||rid==2||(departmentId==2&&rid==3)){
			//isCharge=2 表示公司领导可见is_company_manager=1的未处理故障
			total = rongCloudDao.selectMalfunctionListCount(statusList,uid,fixBys,departmentId,2,companyId);
		}else{
			//isCharge=3 表示其他情况
			total = rongCloudDao.selectMalfunctionListCount(statusList,uid,fixBys,departmentId,3,companyId);
		}

		//符合查询条件的总条数
		Map<String,Object> result = new HashMap<>();
		List<Map<String,Object>> malfunctions = new ArrayList<>();
		if(total>0){
            /*if((departmentId==2&&rid==3)||(departmentId!=2)){
                malfunctions=malfunctionMapper.selectMalfunctionList(statusList,limitNum,create_at,fixBys,departmentId,1);
            }else{
                malfunctions=malfunctionMapper.selectMalfunctionList(statusList,limitNum,create_at,fixBys,departmentId,0);
            }*/
			if(rid==1||rid==2||(departmentId==2&&rid==3)){
				malfunctions = rongCloudDao.selectMalfunctionList(statusList,uid,limitNum,create_at,fixBys,departmentId,2,companyId);
			}else{
				malfunctions = rongCloudDao.selectMalfunctionList(statusList,uid,limitNum,create_at,fixBys,departmentId,3,companyId);
			}

			for(Map<String,Object> malfunction:malfunctions){
				if(malfunction.get("fix_by")!=null&&!malfunction.get("fix_by").equals("")){
					Long fix_by = (Long)malfunction.get("fix_by");
					malfunction.replace("fix_by",userMapper.selectByPrimaryKey(fix_by.intValue()).getUserName());
				}
				Long create_by = (Long)malfunction.get("create_by");
				if(userMapper.selectByPrimaryKey(create_by.intValue())==null){
					return result;
				}
				malfunction.replace("create_by",userMapper.selectByPrimaryKey(create_by.intValue()).getUserName());
			}
		}
		result.put("list",malfunctions);
		result.put("total",total);
		return result;
	}


	/**
	 * 判断当前登陆用户权限下，可以查看的员工范围
	 * @return
	 */
	List<Integer> getfixBysByRoleId(String token){
		Map<String,Object> user = getUserByWebToken(token);
		Integer roleId = (Integer)user.get("role_id");
		List<Integer> fixBys = new ArrayList<>();
		if(roleId==1||roleId==2){
			fixBys.addAll(userMapper.selectUidsByCompanyId((Integer)user.get("company_id")));
			//当前需求为，公司领导仅能看到自己提交的(通过is_company_manager判断)
			//fixBys.add((Integer)user.get("user_id"));
		}else if(roleId==3){
			fixBys.addAll(userMapper.selectUidsByDepartmentId((Integer)user.get("department_id")));
		}else if(roleId==4){
			fixBys.addAll(userMapper.selectUidsByGroupId((Integer)user.get("group_id")));
		}else if(roleId==5||roleId==6){
			fixBys.add((Integer)user.get("user_id"));
		}
		return fixBys;
	}



	public Map<String,Object> supervision_list(Map<String, Object> map) {
		Map<String,Object> appData=new HashMap<String,Object>();
		int pageNum=map.get("pageNum")==null?1:Integer.parseInt(map.get("pageNum").toString());
		int pageSize=map.get("pageSize")==null?20:Integer.parseInt(map.get("pageSize").toString());

		PageHelper.startPage(pageNum,pageSize);
		List<Map<String, Object>> supervision_list = rongCloudDao.supervision_list(map);

		for(int i=0;i<supervision_list.size();i++){
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
		if(pageNum*pageSize>=rongCloudDao.supervision_count(map)){  //判断是否最后一页
			appData.put("is_lastPage", true);
		}else{
			appData.put("is_lastPage", false);
		}

		appData.put("supervision_list", supervision_list);

		return appData;
	}
	//=========================================组织架构=============================


	/**
	 * 全部组织架构
	 */
	@Override
	public Result structure_list(Map<String,Object> map){
		Map<String,Object> user=getUserByWebToken(String.valueOf(map.get("token"))); //当前登录用户

		Map<String,Object> userMap=new HashMap<>();
		userMap.put("company_id",user.get("company_id"));
		userMap.put("role_id",1);
		List<Map<String,Object>> userList=rongCloudDao.user_list(userMap); //总公司领导
		userMap.put("role_id",2);
		List<Map<String,Object>> userList2=rongCloudDao.user_list(userMap); //分公司领导
		userList.addAll(userList2);
		Map<String,Object> company=rongCloudDao.getCompanyById(Integer.parseInt(String.valueOf(user.get("company_id")))); //公司
		company.put("is_inside",true);  //是否在公司中
		List<Map<String,Object>> departmentList=new ArrayList<>();
		List<Map<String,Object>>  groupList=new ArrayList<>();
		if(company!=null){
			departmentList=rongCloudDao.getDepartmentById(null,Integer.parseInt(String.valueOf(company.get("company_id")))); //部门
			for (int i=0;i<departmentList.size();i++){
				if(user.get("department_id")!=null&&String.valueOf(departmentList.get(i).get("department_id")).equals(String.valueOf(user.get("department_id")))){
					departmentList.get(i).put("is_inside",true);  //是否在部门中
				}else{
					departmentList.get(i).put("is_inside",false);  //是否在部门中
				}
				Map<String,Object> userMap2=new HashMap<>();
				userMap2.put("department_id",departmentList.get(i).get("department_id"));
				userMap2.put("role_id",3); //部门经理
				List<Map<String,Object>> userList3=rongCloudDao.user_list(userMap2); //总公司领导
				groupList=rongCloudDao.getGroupById(null,Integer.parseInt(String.valueOf(departmentList.get(i).get("department_id")))); //工作组
				for (int j=0;j<groupList.size();j++){
					if(user.get("group_id")!=null&&String.valueOf(groupList.get(j).get("group_id")).equals(String.valueOf(user.get("group_id")))){
						groupList.get(j).put("is_inside",true);  //是否在工作组中
					}else{
						groupList.get(j).put("is_inside",false);  //是否在工作组中
					}
					Map<String,Object> userGroupMap=new HashMap<>();
					userGroupMap.put("group_id",groupList.get(j).get("group_id"));
					groupList.get(j).put("userList",rongCloudDao.user_list(userGroupMap));
				}
				departmentList.get(i).put("group",groupList);
				departmentList.get(i).put("leader",userList3);  //部门领导们
			}
		}

		company.put("department",departmentList);  //部门
		company.put("leader",userList);  //领导
		return Result.success(company);
	}


	/**
	 * 登录用户组织架构
	 */
	@Override
	public Result structure_ByUser(Map<String,Object> map){
		Map<String,Object> user=getUserByWebToken(String.valueOf(map.get("token"))); //当前登录用户
		Map<String,Object> company=rongCloudDao.getCompanyById(Integer.parseInt(String.valueOf(user.get("company_id")))); //公司
		Map<String,Object> department=new HashMap<>();
		Map<String,Object> group=new HashMap<>();
		if(user.get("department_id")!=null){
			department=rongCloudDao.getDepartmentById(Integer.parseInt(String.valueOf(user.get("department_id"))),null).get(0); //部门
			if(user.get("group_id")!=null){
				group=rongCloudDao.getGroupById(Integer.parseInt(String.valueOf(user.get("group_id"))),null).get(0); //工作组
			}
		}
		department.put("group",group);
		company.put("department",department);

		return Result.success(company);
	}


	/**
	 * 黄历 天气 限行
	 */
	@Override
	public Result almanac_ByDate(Map<String,Object> map){
		String date=String.valueOf(map.get("date"));
		List<Map<String,Object>> almanacList=rongCloudDao.getAlmanac(map);
		Map<String,Object>  almanac=new HashMap<>();
		if(almanacList.size()>0){
			almanac=almanacList.get(0);  //一条黄历
			if(almanac.get("weather_type")==null||"".equals(String.valueOf(almanac.get("weather_type")))){   //黄历中没weather
				Map<String,Object>  weather=RongUtil.Weather(date);
				almanac.put("weather_type",weather.get("weather_type")==null?"":weather.get("weather_type"));
				almanac.put("weather_temperature",weather.get("weather_temperature")==null?"":weather.get("weather_temperature"));
				almanac.put("weather_wind",weather.get("weather_wind")==null?"":weather.get("weather_wind"));
			}
			if(almanac.get("huangli_yi")==null||"".equals(String.valueOf(almanac.get("huangli_yi")))){   //黄历中没huangli_yi
				Map<String,Object>  HuangLi=RongUtil.HuangLi(date);
				almanac.put("huangli_yi",HuangLi.get("huangli_yi")==null?"":HuangLi.get("huangli_yi"));
				almanac.put("huangli_ji",HuangLi.get("huangli_ji")==null?"":HuangLi.get("huangli_ji"));
			}
			if(almanac.get("limit_row")==null||"".equals(String.valueOf(almanac.get("limit_row")))){   //黄历中没limit_row
				almanac.put("limit_row",getLimitRow(date));  //获取限行
			}

			rongCloudDao.updateAlmanac(almanac); //修改黄历
		}else {  //mysql 没有
			almanac.put("date",date); //日期
			almanac.put("limit_row",getLimitRow(date)); //获取限行


			Map<String,Object>  weather=RongUtil.Weather(date);
			almanac.put("weather_type",weather.get("weather_type")==null?"":weather.get("weather_type"));
			almanac.put("weather_temperature",weather.get("weather_temperature")==null?"":weather.get("weather_temperature"));
			almanac.put("weather_wind",weather.get("weather_wind")==null?"":weather.get("weather_wind"));


			Map<String,Object>  HuangLi=RongUtil.HuangLi(date);
			almanac.put("huangli_yi",HuangLi.get("huangli_yi")==null?"":HuangLi.get("huangli_yi"));
			almanac.put("huangli_ji",HuangLi.get("huangli_ji")==null?"":HuangLi.get("huangli_ji"));

			rongCloudDao.insertAlmanac(almanac);  //把黄历插入数据库
		}
		return Result.success(almanac);
	}


	public String getLimitRow(String date){
		Map<String,Object> map=new HashMap<>();
		map.put("date",date);
		int date_number= RongUtil.dayForWeek(String.valueOf(map.get("date")));  //星期几
		map.put("date_number",date_number);
		List<Map<String,Object>> rowList=rongCloudDao.getLimitRow(map);
		if (rowList.size()>0){
			return String.valueOf(rowList.get(0).get("limit_row_type"));   //限行
		}else{
			return "";
		}
	}


	/**
	 * 发送通知给当前用户
	 * @param
	 * @return
	 */
	@Override
	public CodeMsg sendNoticeToUser(Map<String,Object> map){
		//当前登录用户
		Map<String,Object> user=getUserByWebToken(String.valueOf(map.get("token")));
		map.put("rong_user_id",user.get("rong_user_id"));
		map.put("rong_group_id",user.get("notice_rong_group_id"));
		//给通知群发送
		Map<String, Object> rongResult=RongUtil.sendGroupDirection(map);
		map.put("rong_group_id",user.get("company_rong_group_id"));
		//给公司群发送
		RongUtil.sendGroupDirection(map);
		if ("200".equals(String.valueOf(rongResult.get("code")))){
		//插入一条信息 表示发过通知
		return CodeMsg.SUCCESS;
		}else{
		return CodeMsg.RONG_NOTICE_SEND_ERRAY;
		}
	}


	/**
	 * DING-创建DING消息
	 * @param
	 * @return
	 */
	@Override
	public CodeMsg createDingMsg(String json){
		//解析数据
		Map<String,Object> appData= JSONObject.parseObject(json);
		Map<String,Object> dingData=new HashMap<>();
		Map<String,Object> userData=new HashMap<>();
		dingData.put("send_user_id",appData.get("send_user_id"));
		dingData.put("ding_time",appData.get("ding_time"));
		dingData.put("is_read",0);
		 int result=0;
		JSONArray user_list = JSONObject.parseArray(String.valueOf(appData.get("receiveUserList")));
		for(int i=0;i<user_list.size();i++) {
			userData= JSONObject.parseObject(String.valueOf(user_list.get(i)));
			dingData.put("receive_user_id",userData.get("receive_user_id"));
			result=rongCloudDao.insertDingUser(dingData);
		}
		if (result>0){
			return CodeMsg.SUCCESS;
		}else{
			return CodeMsg.OPERATE_ERROR;
		}
	}


	/**
	 * DING-标记为已读
	 * @param
	 * @return
	 */
	@Override
	public CodeMsg setDingToRead(Map<String,Object> map){
		map.put("is_read",1);
		int result=rongCloudDao.updateDingUser(map);
		if (result>0){
			return CodeMsg.SUCCESS;
		}else{
			return CodeMsg.OPERATE_ERROR;
		}
	}

	/**
	 * DING-消息统计
	 * @param
	 * @return
	 */
	@Override
	public Result getDingStatistic(Map<String,Object> map){
		Map<String,Object> dingData=new HashMap<>();
		List<Map<String,Object>> receiveList=rongCloudDao.selectDingReceiveUser(map);
		Map<String,Object> sendUserData=rongCloudDao.getUserInfoByRongId(String.valueOf(map.get("send_user_id")));
		dingData.put("send_user_id",map.get("send_user_id"));
		dingData.put("send_user_name",sendUserData.get("user_name"));
		dingData.put("ding_time",map.get("ding_time"));
		dingData.put("receiveUserList",receiveList);
		return Result.success(dingData);
	}


	/**
	 * DING-查看自己发送的消息集合
	 * @param
	 * @return
	 */
	@Override
	public Result getDingByUser(Map<String,Object> map){
		//send_id userId
		map.put("send_user_id", getRongUserIdByToken(String.valueOf(map.get("token"))));
		List<Map<String,Object>> dingList=rongCloudDao.selectDingInfo(map);

		List<Map<String,Object>> resultList=new ArrayList<>();
		Map<String,Object> dingData=new HashMap<>();
		for (int i=0;i<dingList.size();i++){
			dingData=new HashMap<>();
			dingData.put("send_user_id",dingList.get(i).get("send_user_id"));
			dingData.put("ding_time",dingList.get(i).get("ding_time"));
			Map<String,Object> sendUserData=rongCloudDao.getUserInfoByRongId(String.valueOf(dingList.get(i).get("send_user_id")));
			dingData.put("send_user_name",sendUserData.get("user_name"));
			List<Map<String,Object>> receiveList=rongCloudDao.selectDingReceiveUser(dingData);
			dingData.put("receiveUserList",receiveList);
			resultList.add(dingData);
		}
		return Result.success(resultList);
	}

}
