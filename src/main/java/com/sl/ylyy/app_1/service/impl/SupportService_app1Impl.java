package com.sl.ylyy.app_1.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sl.ylyy.app_1.dao.*;
import com.sl.ylyy.app_1.entity.Support;
import com.sl.ylyy.app_1.service.SupportService_app1;
import com.sl.ylyy.common.config.UrlConfig;
import com.sl.ylyy.common.utils.CodeMsg1;
import com.sl.ylyy.common.utils.Jipush;
import com.sl.ylyy.common.utils.Result1;

import com.sl.ylyy.manager.service.impl.CommonDataServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.sl.ylyy.common.config.UrlConfig.*;
import static com.sl.ylyy.common.utils.JwtToken.*;
/**
 * TODO 添加支援 modify hlc on 2019-8-9 13:36:26 修改指定人员发送方式, env放入tags中  修复bugID = 407
 *
 */
@Service("supportService")
public class SupportService_app1Impl implements SupportService_app1 {
    @Autowired
    SupportMapper supportMapper;
    @Autowired
    MalfunctionMapper malfunctionMapper;
    @Autowired
    ImageDescMapper imageDescMapper;
    @Autowired
    AudioDescMapper audioDescMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    UrlConfig urlConfig;
    @Autowired
    RoleMapper roleMapper;

    /**
     * TODO 添加支援 modify hlc on 2019-8-9 13:36:26 修改指定人员发送方式, env放入tags中 
     * @param params
     * @return
     */
    @Override
    @Transactional
    public Result1 addNewSupport(Map<String,Object> params) {
        Map<String,Object> user = getUserByWebToken((String)params.get("token"));
        if (user == null) {
			return Result1.error(CodeMsg1.TOKEN_FAILS, 0);
		}
        Integer rid = roleMapper.selectRidByUid((Integer) user.get("user_id"));
        Support support = new Support();
        //故障id
        support.setMalfunctionId(Integer.parseInt(params.get("mid").toString()));
        //图片描述id，多个以英文逗号隔开
        support.setImageDesc((String)params.get("image_desc"));
        //语音描述id，多个以英文逗号隔开
        support.setAudioDesc((String)params.get("audio_desc"));
        //指定类型（1：人员；2：人数）
        int type = Integer.parseInt(params.get("type").toString());
        support.setSupportType(Integer.parseInt(params.get("type").toString()));
        //最好根据type区分
        if (type == 1) {
        	if (params.get("staff") == null) {
     			return Result1.error(CodeMsg1.MISSING_PARAMETER, rid);
     		}else{
     			support.setSupportStaff((String)params.get("staff"));
     		}
		}else if (type == 2) {
			if (params.get("number")==null || Integer.parseInt(params.get("number").toString()) == 0) {
				return Result1.error(CodeMsg1.MISSING_PARAMETER, rid);
			}else{
				support.setSupportNumber(Integer.parseInt(params.get("number").toString()));
			}
		}else{
			return Result1.error(CodeMsg1.MISSING_PARAMETER, rid);
		}
        //支援进展状态默认为1 	支援状态（1：未结案，2：已结案，表里保存1or2, 3：待处理，4：已终止）
        support.setStatus(1);
        //支援关联故障是否下线默认为1
        support.setMStatus(1);
        //支援时间的时间戳
        support.setSupportAt(Integer.parseInt(params.get("support_at").toString()));
        support.setCreateBy(getUserIdByToken((String)params.get("token")));
        //默认终止时间为0
        support.setTerminateAt(0);
        supportMapper.insertNewSupport(support);

        int id = support.getId();
        //极光推送
        String title="收到新的支援请求！";
        //指定人员id，多个以英文逗号隔开(非必传参)
        if(type == 1){
            String[] staffIds = ((String) params.get("staff")).split(",");
            List<String> staffList = Arrays.asList(staffIds);
            //极光推送
            List<String> alias=new ArrayList<>();
            //alias.add(UrlConfig.ENV_TEST);
            alias.addAll(staffList);
            Map<String, String> extras = new HashMap<>();
            extras.put("type","support");
            extras.put("id",String.valueOf(id));
            extras.put("detail","1");
            //modify hlc 函数中已添加 env tag
            Jipush.sendByAliasList(title,extras,alias);
        }else if(params.get("number")!=null){
        	//指定人数
            //极光推送
            //audience:tag_and
            List<String> tags = new ArrayList<>();
            tags.add(UrlConfig.ENV_TEST);
            tags.add("department_id="+user.get("department_id"));
            //notification:alert
            String msg_content = "收到新的支援请求！";
            //notification:title
            String notification_title = "";
            //notification:extras,参照文档格式
            /*JsonObject extras=new JsonObject();
            extras.addProperty("type","support");
            extras.addProperty("id",id);
            extras.addProperty("detail",2);
            Jipush.sendToTagList(tags,msg_content,"",extras,notification_title);*/
            Map<String,String> extras = new HashMap<>();
            extras.put("type","support");
            extras.put("id",String.valueOf(id));
            extras.put("detail",String.valueOf(2));
            Jipush.sendToTagList(tags,msg_content,"",extras,notification_title);
        }
        return new Result1(null,getRoleIdByToken((String)params.get("token")));
    }
    /**
     * TODO 添加支援 modify hlc on 2019-8-9 13:36:26 修改指定人员发送方式, env放入tags中
     * @param params
     * @return
     */
    @Override
    @Transactional
    public Result1 addNewSupport2(Map<String,Object> params) {
        Map<String,Object> user = getUserByWebToken((String)params.get("token"));
        if (user == null) {
			return Result1.error(CodeMsg1.TOKEN_FAILS, 0);
		}
        Integer rid = roleMapper.selectRidByUid((Integer) user.get("user_id"));
        Integer userId =(Integer)user.get("user_id");
        Support support = new Support();
        //故障id
        support.setMalfunctionId(Integer.parseInt(params.get("mid").toString()));

        String result="";
        if(params.get("image_desc")!=null){
            List fileList= JSONObject.parseArray(String.valueOf(params.get("image_desc")));
            result= CommonDataServiceImpl.insertFile(fileList,"image_desc",userId);
        }
        //图片描述id，多个以英文逗号隔开
        support.setImageDesc(result);
        String result2="";
        if(params.get("audio_desc")!=null){
            List fileList=JSONObject.parseArray(String.valueOf(params.get("audio_desc")));
            result2=CommonDataServiceImpl.insertFile(fileList,"audio_desc",userId);
        }
        //语音描述id，多个以英文逗号隔开
        support.setAudioDesc(result2);


        //指定类型（1：人员；2：人数）
        int type = Integer.parseInt(params.get("type").toString());
        support.setSupportType(Integer.parseInt(params.get("type").toString()));
        //最好根据type区分
        if (type == 1) {
        	if (params.get("staff") == null) {
     			return Result1.error(CodeMsg1.MISSING_PARAMETER, rid);
     		}else{
     			support.setSupportStaff((String)params.get("staff"));
     		}
		}else if (type == 2) {
			if (params.get("number")==null || Integer.parseInt(params.get("number").toString()) == 0) {
				return Result1.error(CodeMsg1.MISSING_PARAMETER, rid);
			}else{
				support.setSupportNumber(Integer.parseInt(params.get("number").toString()));
			}
		}else{
			return Result1.error(CodeMsg1.MISSING_PARAMETER, rid);
		}
        //支援进展状态默认为1 	支援状态（1：未结案，2：已结案，表里保存1or2, 3：待处理，4：已终止）
        support.setStatus(1);
        //支援关联故障是否下线默认为1
        support.setMStatus(1);
        //支援时间的时间戳
        support.setSupportAt(Integer.parseInt(params.get("support_at").toString()));
        support.setCreateBy(getUserIdByToken((String)params.get("token")));
        //默认终止时间为0
        support.setTerminateAt(0);
        supportMapper.insertNewSupport(support);

        int id = support.getId();
        //极光推送
        String title="收到新的支援请求！";
        //指定人员id，多个以英文逗号隔开(非必传参)
        if(type == 1){
            String[] staffIds = ((String) params.get("staff")).split(",");
            List<String> staffList = Arrays.asList(staffIds);
            //极光推送
            List<String> alias=new ArrayList<>();
            //alias.add(UrlConfig.ENV_TEST);
            alias.addAll(staffList);
            Map<String, String> extras = new HashMap<>();
            extras.put("type","support");
            extras.put("id",String.valueOf(id));
            extras.put("detail","1");
            //modify hlc 函数中已添加 env tag
            Jipush.sendByAliasList(title,extras,alias);
        }else if(params.get("number")!=null){
        	//指定人数
            //极光推送
            //audience:tag_and
            List<String> tags = new ArrayList<>();
            tags.add(UrlConfig.ENV_TEST);
            tags.add("department_id="+user.get("department_id"));
            //notification:alert
            String msg_content = "收到新的支援请求！";
            //notification:title
            String notification_title = "";
            //notification:extras,参照文档格式
            /*JsonObject extras=new JsonObject();
            extras.addProperty("type","support");
            extras.addProperty("id",id);
            extras.addProperty("detail",2);
            Jipush.sendToTagList(tags,msg_content,"",extras,notification_title);*/
            Map<String,String> extras = new HashMap<>();
            extras.put("type","support");
            extras.put("id",String.valueOf(id));
            extras.put("detail",String.valueOf(2));
            Jipush.sendToTagList(tags,msg_content,"",extras,notification_title);
        }
        return new Result1(null,getRoleIdByToken((String)params.get("token")));
    }

    /**
     * 接受/拒绝支援
     * @param params
     * @return
     */
    @Override
    public Result1 supportRespond(Map<String, Object> params) {
        Integer userId = getUserIdByToken((String)params.get("token"));
        Integer rid = roleMapper.selectRidByUid(userId);
        if(supportMapper.selectByPrimaryKey(Integer.parseInt(params.get("sid").toString()))==null){
            return new Result1("1005","支援不存在",rid);
        }
        //根据支援id查询对应支援详情
        Support support = supportMapper.selectByPrimaryKey(Integer.parseInt(params.get("sid").toString()));
        if(support.getStatus()==2){
            return new Result1("1006","支援已结案",rid);
        }
        //判断支援是否已接受(当前登陆人员是否接受过该支援)
        if(support.getRespondStaff()!=null&&!support.getRespondStaff().equals("")
                //&&!support.getRefuseStaff().startsWith(",")
        ){
            String[] respondStaff = support.getRespondStaff().split(",");
            for(int i=0;i<respondStaff.length;i++){
                if(respondStaff[i]!=null&&userId==Integer.parseInt(respondStaff[i].split("\\|")[0])){
                    return new Result1("1007","支援已接受",rid);
                }
            }
        }
        //当支援类型是人数的时候，判断接受人员人数是否已经达到要求
        if(support.getSupportNumber()!=null&&support.getRespondStaff()!=null&&
                support.getSupportNumber()==support.getRespondStaff().split(",").length){
            return new Result1("1008","已达到支援所需人数, 报名结束",rid);
        }
        //accept 是否接受/报名（1：接受/报名；2：不接受/不报名），默认2|
        int accept = 0;
        if(params.get("accept")==null){
            accept=2;
        }else{
            accept=Integer.parseInt((String)params.get("accept"));
        }
        //接受支援
        if(accept==1){
            //若响应人字段为空，直接将当前用户id添加到改字段
            if(support.getRespondStaff()==null||support.getRespondStaff().equals("")){
                support.setRespondStaff(userId+"|"+System.currentTimeMillis()/1000);
                //若响应人字段不为空，则将当前用户id拼接到原有响应人中，以,分隔
            }else{
                support.setRespondStaff(support.getRespondStaff()+","+userId+"|"+System.currentTimeMillis()/1000);
            }
        }
        //拒绝支援
        if(accept==2){
            //若拒绝人字段为空，直接将当前用户id添加到改字段
            if(support.getRefuseStaff()==null||support.getRefuseStaff().equals("")){
                support.setRefuseStaff(userId+"|"+System.currentTimeMillis()/1000);
                //若拒绝人字段不为空，则将当前用户id拼接到原有拒绝人中，以,分隔
            }else{
                support.setRefuseStaff(support.getRespondStaff()+","+userId+"|"+System.currentTimeMillis()/1000);
            }
        }
        supportMapper.updateByPrimaryKeySelective(support);
        Map<String,Object> data = new HashMap<>();
        data.put("accept",accept);
        data.put("status",support.getMStatus());
        return new Result1(data,rid);
    }

    /**
     * 更新支援状态
     * @param params
     * @return
     */
    Result1 updateSupportStatus(Map<String,Object> params){
        Integer userId = getUserIdByToken((String)params.get("token"));
        Integer rid = roleMapper.selectRidByUid(userId);
        int id = Integer.parseInt(params.get("sid").toString());
        if(supportMapper.selectByPrimaryKey(id)==null){
            return new Result1("1005","支援不存在",rid);
        }
        Support support = supportMapper.selectByPrimaryKey(id);
        if(support.getStatus()==2){
            if(support.getTerminateAt()!=null){
                return new Result1("1008","支援已终止",rid);
            }
            return new Result1("1006","支援已结案",rid);
        }
        if(support.getCreateBy()!=userId){
            return new Result1("1007","只有发起人可以结案",rid);
        }
        return new Result1(support,rid);
    }

    /**
     * 终止支援
     * @param params
     * @return
     */
    @Override
    public Result1 terminateSupport(Map<String, Object> params) {
        Support support = (Support) updateSupportStatus(params).getData();
        supportMapper.updateTerminate(support);
        return new Result1(null,roleMapper.selectRidByUid(getUserIdByToken((String)params.get("token"))));
    }

    /**
     * 发起人结案
     * @param params
     * @return
     */
    @Override
    public Result1 statusSupport(Map<String, Object> params) {
        Support support = (Support)updateSupportStatus(params).getData();
        //进展状态改为已结案
        support.setStatus(2);
        supportMapper.updateByPrimaryKeySelective(support);
        return new Result1(null,roleMapper.selectRidByUid(getUserIdByToken((String)params.get("token"))));
    }

    /**
     * 根据以,拼接的图片id字符串，解析并获取每个id对应的图片详情，添加到列表中
     * @param image_desc
     * @return
     */
    List<Map<String,Object>> getImageList(String image_desc){
        List<Map<String,Object>> images = new ArrayList<>();
        if(image_desc.contains(",")){
            String[] imageIds = image_desc.split(",");
            for(int i=0;i<imageIds.length;i++){
                Map<String,Object> image = imageDescMapper.selectByPrimaryKey(Integer.parseInt(imageIds[i]));
                    if(String.valueOf(image.get("content")).contains(QiniuUrl)||String.valueOf(image.get("content")).contains(DOWNURL)){
                        image.replace("content",image.get("content"));
                    }else{
                        image.replace("content",DOWNURL+image.get("content"));
                    }
                images.add(image);
            }
        }else{
            Map<String,Object> image = imageDescMapper.selectByPrimaryKey(Integer.parseInt(image_desc));
                if(String.valueOf(image.get("content")).contains(QiniuUrl)||String.valueOf(image.get("content")).contains(DOWNURL)){
                    image.replace("content",image.get("content"));
                }else{
                    image.replace("content",DOWNURL+image.get("content"));
                }
            images.add(image);
        }
        return images;
    }

    /**
     * 根据以,拼接的语音id字符串，解析并获取每个id对应的语音详情，添加到列表中
     * @param audio_desc
     * @return
     */
    List<Map<String,Object>> getAudioList(String audio_desc){
        List<Map<String,Object>> audios = new ArrayList<>();
        if(audio_desc.contains(",")){
            String[] audioIds = audio_desc.split(",");
            for(int i=0;i<audioIds.length;i++){
                Map<String,Object> audio = audioDescMapper.selectByPrimaryKey(Integer.parseInt(audioIds[i]));
                if(String.valueOf(audio.get("content")).contains(QiniuUrl)||String.valueOf(audio.get("content")).contains(DOWNURL)){
                    audio.replace("content",audio.get("content"));
                }else{
                    audio.replace("content",DOWNURL+audio.get("content"));
                }
                audios.add(audio);
            }
        }else{
            Map<String,Object> audio = audioDescMapper.selectByPrimaryKey(Integer.parseInt(audio_desc));
            if(String.valueOf(audio.get("content")).contains(QiniuUrl)||String.valueOf(audio.get("content")).contains(DOWNURL)){
                audio.replace("content",audio.get("content"));
            }else{
                audio.replace("content",DOWNURL+audio.get("content"));
            }
            audios.add(audio);
        }

        return audios;
    }

    /**
     * 根据员工id和时间戳拼接的字符串，解析为员工详情的列表
     * @param staff
     * @return
     */
    List<Map<String,Object>> getStaffList(String staff){
        List<Map<String,Object>> staffs = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if(staff.contains(",")){
            String[] respondStaffIds = staff.split(",");
            for(int i=0;i<respondStaffIds.length;i++){
                int userId = Integer.parseInt(respondStaffIds[i].split("\\|")[0]);
                long timeStamp = Integer.parseInt(respondStaffIds[i].split("\\|")[1]);
                Map<String,Object> staff_detail = new HashMap<>();
                staff_detail.put("name",userMapper.selectByPrimaryKey(userId).getUserName());
                staff_detail.put("data",sdf.format(timeStamp*1000));
                staffs.add(staff_detail);
            }
        }else{
            int userId = Integer.parseInt(staff.split("\\|")[0]);
            long timeStamp = Integer.parseInt(staff.split("\\|")[1]);
            Map<String,Object> staff_detail = new HashMap<>();
            staff_detail.put("name",userMapper.selectByPrimaryKey(userId).getUserName());
            staff_detail.put("data",sdf.format(new Date(timeStamp*1000)));
            staffs.add(staff_detail);
        }
        return staffs;
    }

    @Override
    public Result1 getSupportDetail(Map<String, Object> params) {
        Map<String, Object> user = getUserByWebToken((String)params.get("token"));
        Map<String,Object> results = supportMapper.selectSupportById(Integer.parseInt(params.get("sid").toString()));
        Integer rid = roleMapper.selectRidByUid(getUserIdByToken((String)params.get("token")));

        Integer terminate_at = Integer.parseInt(String.valueOf(results.get("terminate_at")));
        //接受列表
        if(Integer.parseInt(results.get("type").toString())==2){
            if(Integer.parseInt(String.valueOf(results.get("status")))==2){
                //status=2 已结案
                results.replace("status",2);
            }else if(terminate_at!=0){
                //status=4，已终止  终止时间不为空
                results.replace("status",4);
            }else if(results.get("respond_staff")!=null&&((String)results.get("respond_staff")).contains(user.get("user_name").toString())){
                //status=1 已参与 respond_staff响应人中包括当前登录用户
                results.replace("status",1);
            }else{
                results.replace("status",3);
            }
        }else if(Integer.parseInt(results.get("type").toString())==1){
            if(Integer.parseInt(String.valueOf(results.get("status")))==2){
                //status=2 已结案
                results.replace("status",2);
            }else if(terminate_at!=0){
                //status=4，已终止  终止时间不为空
                results.replace("status",4);
            }else{
                results.replace("status",3);
            }
        }

        //判断图片描述是否为空，不为空转为图片详情的列表
        if(results.get("image_desc")!=null&&!results.get("image_desc").equals("")){
            List<Map<String,Object>> images = getImageList((String)results.get("image_desc"));
            results.replace("image_desc",images);
        }else{
            results.replace("image_desc",new    ArrayList<>());
        }
        //判断语音描述是否为空，不为空转为图片详情的列表
        if(results.get("audio_desc")!=null&&!results.get("audio_desc").equals("")){
            List<Map<String,Object>> audios = getAudioList((String)results.get("audio_desc"));
            results.replace("audio_desc",audios);
        }else{
            results.replace("audio_desc",new ArrayList<>());
        }

        if(results.get("respond_staff")!=null&&!results.get("respond_staff").equals("")){
            results.replace("respond_staff",getStaffList((String)results.get("respond_staff")));
        }else{
            results.put("respond_staff",new ArrayList<>());
        }
        if(results.get("refuse_staff")!=null&&!results.get("refuse_staff").equals("")){
            results.replace("refuse_staff",getStaffList((String)results.get("refuse_staff")));
        }else{
            results.put("refuse_staff",new ArrayList<>());
        }
        if (results.get("support_staff")!=null&&!results.get("support_staff").equals("")){
            String support_staff = (String)results.get("support_staff");
            String supportName = "";
            if(support_staff.contains(",")){
                String[] supports = support_staff.split(",");
                for(int i=0;i<supports.length;i++){
                    if(i==supports.length-1){
                        supportName += userMapper.selectUserNameById(Integer.parseInt(supports[i]));
                    }else{
                        supportName+=userMapper.selectUserNameById(Integer.parseInt(supports[i]))+",";
                    }
                }
            }else{
                supportName=userMapper.selectUserNameById(Integer.parseInt(support_staff));
            }
            results.replace("support_staff",supportName);
        }


        //respond_staff	array	响应人
        //refuse_staff	array	拒绝人
        return new Result1(results,rid);
    }
    
    /**
     * TODO 需要重写type=2时的查询条件 未处理完 
     */
    @Override
    public Result1 getSupportList(Map<String, Object> params) {
        Map<String, Object> user = getUserByWebToken((String)params.get("token"));
        if (user == null) {
        	return new Result1(CodeMsg1.TOKEN_FAILS, 0);
		}
        Integer userId = (Integer)user.get("user_id");
        Integer rid = roleMapper.selectRidByUid(userId);
        Integer departmentId = (Integer)user.get("department_id"); 
        //列表类型（1：发起；2：接收），默认1
        int type=params.get("type")==null?1:Integer.parseInt(params.get("type").toString());
        if (type < 1 || type > 2) {
        	return new Result1(CodeMsg1.MISSING_PARAMETER, rid);
        }
        Integer creat_by = null;
        Integer response = null;
        if(type==1){
            creat_by = userId;
        }else{
            response = userId;
        }
        //页码，默认1
        int page=params.get("page")==null?1:Integer.parseInt(params.get("page").toString());
        //每页条数，默认20
        int limit_num=params.get("limit_num")==null?20:Integer.parseInt(params.get("limit_num").toString());
        //最后一条数据的支援时间时间戳，默认0
        Integer support_at=(params.get("support_at")==null||Integer.parseInt((String)params.get("support_at"))==0)
                ?null:Integer.parseInt(params.get("support_at").toString());
        int total = supportMapper.countSupportList(creat_by,response,departmentId,userId);
        List<Map<String,Object>> results = new ArrayList<>();
        if(total>0){
            results = supportMapper.selectSupportList(limit_num,support_at,creat_by,response,departmentId,userId);
            for(Map<String,Object> result:results){
                String user_name = "";
                if(result.get("respond_staff")!=null&&!result.get("respond_staff").equals("")){
                    String[] responds = ((String)result.get("respond_staff")).split(",");
                    for(int i=0;i<responds.length;i++) {
                        if(i<responds.length-1){
                            String uid = responds[i].split("\\|")[0];
                            String name = userMapper.selectUserNameById(Integer.parseInt(uid));
                            user_name+= name + ",";
                        }
                        if(i==responds.length-1){
                            String uid = responds[i].split("\\|")[0];
                            user_name+=userMapper.selectUserNameById(Integer.parseInt(uid));
                        }
                    }
                    result.put("respond_id",result.get("respond_staff"));
                    result.replace("respond_staff",user_name);
                }
                Integer terminate_at = (Integer)result.get("terminate_at");
                //接受列表
                if(Integer.parseInt((String)params.get("type"))==2){
                    if(Integer.parseInt(String.valueOf(result.get("status")))==2){
                        //status=2 已结案
                        result.replace("status",2);
                    }else if(terminate_at!=0){
                        //status=4，已终止  终止时间不为空
                        result.replace("status",4);
                    }else if(result.get("respond_staff")!=null&&((String)result.get("respond_staff")).contains(user.get("user_name").toString())){
                        //status=1 已参与 respond_staff响应人中包括当前登录用户
                        result.replace("status",1);
                    }else{
                        result.replace("status",3);
                    }
                }else if(Integer.parseInt((String)params.get("type"))==1){
                    if(Integer.parseInt(String.valueOf(result.get("status")))==2){
                        //status=2 已结案
                        result.replace("status",2);
                    }else if(terminate_at!=0){
                        //status=4，已终止  终止时间不为空
                        result.replace("status",4);
                    }else{
                        result.replace("status",3);
                    }
                }

        }
            /*if((Long)result.get("type")==1){
                if((Long)result.get("status")==2){
                    //status=2 已结案
                    result.replace("status",2);
                }else if(terminate_at!=0) {
                    //status=4，已终止  终止时间不为空
                    result.replace("status", 4);
                }else{
                    result.replace("status",1);
                }
            }else{
                if((Long)result.get("status")==2){
                    //status=2 已结案
                    result.replace("status",2);
                }else if(terminate_at!=0){
                    //status=4，已终止  终止时间不为空
                    result.replace("status",4);
                }else if(result.get("respond_staff")!=null&&((String)result.get("respond_id")).contains(userId.toString())){
                    //status=1 已参与 respond_staff响应人中包括当前登录用户
                    result.replace("status",1);
                }else{
                    result.replace("status",3);
                }
            }*/
        }
        Map<String,Object> supportResult = new HashMap<>();
        supportResult.put("list",results);
        supportResult.put("total",total);
        return new Result1(supportResult,rid);
    }
}
