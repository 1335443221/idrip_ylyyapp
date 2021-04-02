package com.sl.ylyy.app_1.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sl.ylyy.app_1.dao.*;
import com.sl.ylyy.app_1.entity.Malfunction;
import com.sl.ylyy.app_1.entity.MalfunctionWithBLOBs;
import com.sl.ylyy.app_1.entity.MalfunctionLog;
import com.sl.ylyy.app_1.entity.MalfunctionLogWithBLOBs;
import com.sl.ylyy.app_1.entity.Material;
import com.sl.ylyy.app_1.service.MalfunctionService_app1;
import com.sl.ylyy.common.Interceptor.GlobalExceptionHandler;
import com.sl.ylyy.common.config.UrlConfig;
import com.sl.ylyy.common.utils.Jipush;
import com.sl.ylyy.common.utils.Result1;
import com.sl.ylyy.manager.service.impl.CommonDataServiceImpl;
import org.apache.log4j.Logger;
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
import java.util.Timer;
import java.util.TimerTask;

import static com.sl.ylyy.common.config.UrlConfig.*;
import static com.sl.ylyy.common.utils.TimePeriodUtil.getTimePeriod;
import static com.sl.ylyy.common.utils.JwtToken.*;
/**
 * TODO modify hlc on 2019-8-9 11:13:23 
 * 添加预约接口, 需要添加定时任务,具体看方法注释 bugID=388(预约解决时间到了的故障, 没有更新状态)
 */
@Service("malfunctionServiceApp1")
public class MalfunctionService_app1Impl implements MalfunctionService_app1 {
    @Autowired
    MalfunctionTypeMapper malfunctionTypeMapper;
    @Autowired
    MalfunctionMapper malfunctionMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    MaterialMapper materialMapper;
    @Autowired
    ImageDescMapper imageDescMapper;
    @Autowired
    AudioDescMapper audioDescMapper;
    @Autowired
    MalfunctionLogMapper malfunctionLogMapper;
    @Autowired
    private UrlConfig urlConfig;
    @Autowired
    private SupportMapper supportMapper;
    @Autowired
    RoleMapper roleMapper;
    static Logger logger = (Logger) Logger.getInstance(GlobalExceptionHandler.class);

    @Override
    public Result1 getMalfunctionType(Map<String,Object> params) {
        String token = (String)params.get("token");
        Integer rid = roleMapper.selectRidByUid(getUserIdByToken(token));
        Integer companyId = (Integer)getUserByWebToken(token).get("company_id");
        return new Result1(malfunctionTypeMapper.selectAllType(companyId),rid);
    }

    /**
     * 故障上报
     * @param params
     * @return
     */
    @Override
    @Transactional
    public Result1 addMalfunction(Map<String, Object> params) {
        String token = (String)params.get("token");
        Map<String,Object> user = getUserByWebToken(token);
        Integer userId =(Integer)user.get("user_id");
        Integer companyId = (Integer) user.get("company_id");
        Integer departmentId = (Integer)user.get("department_id");
        Integer rid = roleMapper.selectRidByUid(userId);
        MalfunctionWithBLOBs malfunction = new MalfunctionWithBLOBs();
        //故障来源(1、来自巡检；2、来自故障入口),默认2
        if(params.get("source") == null) {
            malfunction.setSource(2);
        } else {
            malfunction.setSource(Integer.parseInt(params.get("source").toString()));
        }
        malfunction.setType(Integer.parseInt(params.get("type").toString()));
        malfunction.setLocation((String)params.get("location"));
        if(params.get("question")!=null);malfunction.setQuestion((String)params.get("question"));
        if(params.get("image_desc")!=null){
            String image_desc = (String)params.get("image_desc");
            if(image_desc.split(",").length>3){
                return new Result1("1007","图片数量限制：3张",rid);
            }
            malfunction.setImageDesc(image_desc);
        }
        if(params.get("audio_desc")==null);malfunction.setAudioDesc((String)params.get("audio_desc"));

        //极光推送
        //audience:tag_and
        List<String> tags = new ArrayList<>();
        tags.add(UrlConfig.ENV_TEST);
        if(params.get("department_id").equals("0")){
            tags.add("company_id="+companyId);
        }else{
            tags.add("department_id="+Integer.parseInt((String)params.get("department_id")));
        }
        //默认故障状态为未指派
        malfunction.setStatus(1);
        malfunction.setCreateBy(userId);
        malfunction.setDepartmentId(Integer.parseInt((String)params.get("department_id")));
        malfunction.setIsPush(0);
        malfunction.setCompanyId(companyId);

        malfunctionMapper.insertSelective(malfunction);
        int mid = malfunction.getId();

        String msg_content = "发现新的故障!";
        String notification_title = "";
        //notification:extras,参照文档格式
        /*JsonObject extras=new JsonObject();
        extras.addProperty("type","malfunction");
        extras.addProperty("id",mid);
        extras.addProperty("detail","");
        int staus = Jipush.sendToTagList(tags,msg_content,"",extras,notification_title);*/
        Map<String,String> extras = new HashMap<>();
        extras.put("type","malfunction");
        extras.put("id",String.valueOf(mid));
        extras.put("detail","");
        int staus = Jipush.sendToTagList(tags,msg_content,"",extras,notification_title);
        malfunction.setIsPush(staus);
        malfunctionMapper.updateIsPush(mid,staus);
        Timer nTimer = new Timer();

//        if(departmentId!=null&&departmentId==1){
            //设置定时器，5分钟新支援未接单，推送给管理员
            nTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if(malfunctionMapper.selectByPrimaryKey(mid).getStatus()==1){
                        List<Integer> managers = malfunctionMapper.selectGroupByMType(malfunctionMapper.selectByPrimaryKey(mid).getType());
                        List<String> mangerList = new ArrayList<>();
                        for(Integer manager:managers){
                            mangerList.add(manager+"");
                        }
                        //极光推送 故障五分钟未接单，推送给组长
                        List<String> alias=new ArrayList<>();
                        //alias.add(UrlConfig.ENV_TEST);
                        alias.addAll(mangerList);
                        //modify hlc 函数中已添加 env tag
                        Jipush.sendByAliasList("新故障五分钟未处理，请指派！",extras,alias);
                        List<String> tagList = new ArrayList<>();
                        //tagList.add(UrlConfig.ENV_TEST);
                        //
                        //tagList.add("role=4");
                        //Jipush.sendToTagList(tagList,"新故障五分钟未处理，请指派！","",extras,notification_title);
                    }
                }
            },1000*60*5);
            nTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if(malfunctionMapper.selectByPrimaryKey(mid).getStatus()==1){
                        List<String> tagList = new ArrayList<>();
                        tagList.add(UrlConfig.ENV_TEST);
                        //故障三十分钟未接单，推送给部门经理
                        tagList.add("role=3");
                        Jipush.sendToTagList(tagList,"新故障三十分钟未处理，请指派！","",extras,notification_title);

                        //分公司领导推送
                        List<String> tagList2 = new ArrayList<>();
                        tagList2.add(UrlConfig.ENV_TEST);
                        tagList2.add("role=2");
                        Jipush.sendToTagList(tagList2,"新故障三十分钟未处理，请指派！","",extras,notification_title);
                    }
                }
            },1000*60*30);
        /*}else if(departmentId!=null&&departmentId==2){
            nTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if(malfunctionMapper.selectByPrimaryKey(mid).getStatus()==1){
                        List<String> tagList = new ArrayList<>();
                        tagList.add(UrlConfig.ENV_TEST);
                        //故障24小时未接单，推送给部门经理
                        tagList.add("role=3");
                        Jipush.sendToTagList(tagList,"新故障24小时未处理，请指派！","",extras,notification_title);

                        //分公司领导推送
                        List<String> tagList2 = new ArrayList<>();
                        tagList2.add(UrlConfig.ENV_TEST);
                        tagList2.add("role=2");
                        Jipush.sendToTagList(tagList2,"新故障24小时未处理，请指派！","",extras,notification_title);
                    }
                }
            },1000*60*60*24);
        }else if(departmentId!=null&&departmentId==4){
            nTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if(malfunctionMapper.selectByPrimaryKey(mid).getStatus()==1){
                        List<String> tagList = new ArrayList<>();
                        tagList.add(UrlConfig.ENV_TEST);
                        //故障一小时未接单，推送给部门经理
                        tagList.add("role=3");
                        Jipush.sendToTagList(tagList,"新故障1小时未处理，请指派！","",extras,notification_title);

                        //分公司领导推送
                        List<String> tagList2 = new ArrayList<>();
                        tagList2.add(UrlConfig.ENV_TEST);
                        tagList2.add("role=2");
                        Jipush.sendToTagList(tagList2,"新故障1小时未处理，请指派！","",extras,notification_title);
                    }
                }
            },1000*60*60);
            nTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    //48小时未处理的故障公司领导可见
                    if(malfunctionMapper.selectByPrimaryKey(mid).getStatus()==1){
                        malfunction.setIsCompanyManager(1);
                        malfunctionMapper.updateByPrimaryKeySelective(malfunction);
                    }
                }
            },1000*60*60*24);
        }*/

        return new Result1(null,rid);
    }
    /**
     * 故障上报
     * @param params
     * @return
     */
    @Override
    @Transactional
    public Result1 addMalfunction2(Map<String, Object> params) {
        String token = (String)params.get("token");
        Map<String,Object> user = getUserByWebToken(token);
        Integer userId =(Integer)user.get("user_id");
        Integer companyId = (Integer) user.get("company_id");
        Integer departmentId = (Integer)user.get("department_id");
        Integer rid = roleMapper.selectRidByUid(userId);
        MalfunctionWithBLOBs malfunction = new MalfunctionWithBLOBs();
        //故障来源(1、来自巡检；2、来自故障入口),默认2
        if(params.get("source") == null) {
            malfunction.setSource(2);
        } else {
            malfunction.setSource(Integer.parseInt(params.get("source").toString()));
        }
        malfunction.setType(Integer.parseInt(params.get("type").toString()));
        malfunction.setLocation((String)params.get("location"));
        if(params.get("question")!=null);malfunction.setQuestion((String)params.get("question"));

        String result="";
        if(params.get("image_desc")!=null){
            List fileList=JSONObject.parseArray(String.valueOf(params.get("image_desc")));
            if(fileList.size()>3){
                return new Result1("1007","图片数量限制：3张",rid);
            }
            result=CommonDataServiceImpl.insertFile(fileList,"image_desc",userId);
        }
        malfunction.setImageDesc(result);
        String result2="";
        if(params.get("audio_desc")!=null){
            List fileList=JSONObject.parseArray(String.valueOf(params.get("audio_desc")));
            result2=CommonDataServiceImpl.insertFile(fileList,"audio_desc",userId);
        }
        malfunction.setAudioDesc(result2);

        //极光推送
        //audience:tag_and
        List<String> tags = new ArrayList<>();
        tags.add(UrlConfig.ENV_TEST);
        if(params.get("department_id").equals("0")){
            tags.add("company_id="+companyId);
        }else{
            tags.add("department_id="+Integer.parseInt((String)params.get("department_id")));
        }
        //默认故障状态为未指派
        malfunction.setStatus(1);
        malfunction.setCreateBy(userId);
        malfunction.setDepartmentId(Integer.parseInt((String)params.get("department_id")));
        malfunction.setIsPush(0);
        malfunction.setCompanyId(companyId);

        malfunctionMapper.insertSelective(malfunction);
        int mid = malfunction.getId();

        String msg_content = "发现新的故障!";
        String notification_title = "";
        //notification:extras,参照文档格式
        /*JsonObject extras=new JsonObject();
        extras.addProperty("type","malfunction");
        extras.addProperty("id",mid);
        extras.addProperty("detail","");
        int staus = Jipush.sendToTagList(tags,msg_content,"",extras,notification_title);*/
        Map<String,String> extras = new HashMap<>();
        extras.put("type","malfunction");
        extras.put("id",String.valueOf(mid));
        extras.put("detail","");
        int staus = Jipush.sendToTagList(tags,msg_content,"",extras,notification_title);
        malfunction.setIsPush(staus);
        malfunctionMapper.updateIsPush(mid,staus);
        Timer nTimer = new Timer();

//        if(departmentId!=null&&departmentId==1){
            //设置定时器，5分钟新支援未接单，推送给管理员
            nTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if(malfunctionMapper.selectByPrimaryKey(mid).getStatus()==1){
                        List<Integer> managers = malfunctionMapper.selectGroupByMType(malfunctionMapper.selectByPrimaryKey(mid).getType());
                        List<String> mangerList = new ArrayList<>();
                        for(Integer manager:managers){
                            mangerList.add(manager+"");
                        }
                        //极光推送 故障五分钟未接单，推送给组长
                        List<String> alias=new ArrayList<>();
                        //alias.add(UrlConfig.ENV_TEST);
                        alias.addAll(mangerList);
                        //modify hlc 函数中已添加 env tag
                        Jipush.sendByAliasList("新故障五分钟未处理，请指派！",extras,alias);
                        List<String> tagList = new ArrayList<>();
                        //tagList.add(UrlConfig.ENV_TEST);
                        //
                        //tagList.add("role=4");
                        //Jipush.sendToTagList(tagList,"新故障五分钟未处理，请指派！","",extras,notification_title);
                    }
                }
            },1000*60*5);
            nTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if(malfunctionMapper.selectByPrimaryKey(mid).getStatus()==1){
                        List<String> tagList = new ArrayList<>();
                        tagList.add(UrlConfig.ENV_TEST);
                        //故障三十分钟未接单，推送给部门经理
                        tagList.add("role=3");
                        Jipush.sendToTagList(tagList,"新故障三十分钟未处理，请指派！","",extras,notification_title);

                        //分公司领导推送
                        List<String> tagList2 = new ArrayList<>();
                        tagList2.add(UrlConfig.ENV_TEST);
                        tagList2.add("role=2");
                        Jipush.sendToTagList(tagList2,"新故障三十分钟未处理，请指派！","",extras,notification_title);
                    }
                }
            },1000*60*30);
//        }

        /*else if(departmentId!=null&&departmentId==2){
            nTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if(malfunctionMapper.selectByPrimaryKey(mid).getStatus()==1){
                        List<String> tagList = new ArrayList<>();
                        tagList.add(UrlConfig.ENV_TEST);
                        //故障24小时未接单，推送给部门经理
                        tagList.add("role=3");
                        Jipush.sendToTagList(tagList,"新故障24小时未处理，请指派！","",extras,notification_title);

                        //分公司领导推送
                        List<String> tagList2 = new ArrayList<>();
                        tagList2.add(UrlConfig.ENV_TEST);
                        tagList2.add("role=2");
                        Jipush.sendToTagList(tagList2,"新故障24小时未处理，请指派！","",extras,notification_title);
                    }
                }
            },1000*60*60*24);
        }else if(departmentId!=null&&departmentId==4){
            nTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if(malfunctionMapper.selectByPrimaryKey(mid).getStatus()==1){
                        List<String> tagList = new ArrayList<>();
                        tagList.add(UrlConfig.ENV_TEST);
                        //故障一小时未接单，推送给部门经理
                        tagList.add("role=3");
                        //部门经理推送
                        Jipush.sendToTagList(tagList,"新故障1小时未处理，请指派！","",extras,notification_title);
                        //分公司领导推送
                        List<String> tagList2 = new ArrayList<>();
                        tagList2.add(UrlConfig.ENV_TEST);
                        tagList2.add("role=2");
                        Jipush.sendToTagList(tagList2,"新故障1小时未处理，请指派！","",extras,notification_title);
                    }
                }
            },1000*60*60);
            nTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    //48小时未处理的故障公司领导可见
                    if(malfunctionMapper.selectByPrimaryKey(mid).getStatus()==1){
                        malfunction.setIsCompanyManager(1);
                        malfunctionMapper.updateByPrimaryKeySelective(malfunction);
                    }
                }
            },1000*60*60*24);
        }*/

        return new Result1(null,rid);
    }

    /**
     * 判断当前登陆用户权限下，可以查看的员工范围
     * @return
     */
    List<Integer> getfixBysByRoleId(String token){
        Map<String,Object> user = getUserByWebToken(token);
        Integer roleId = (Integer)user.get("role_id");
        List<Integer> fixBys = new ArrayList<>();
       /* if(roleId==1||roleId==2){
            fixBys.addAll(userMapper.selectUidsByCompanyId((Integer)user.get("company_id")));
            //fixBys.add((Integer)user.get("user_id"));
        }else if(roleId==3||roleId==4){
            fixBys.addAll(userMapper.selectUidsByDepartmentId((Integer)user.get("department_id")));
       *//* }else if(roleId==4){
            fixBys.addAll(userMapper.selectUidsByDepartmentId((Integer)user.get("department_id")));*//*
//            fixBys.addAll(userMapper.selectUidsByGroupId((Integer)user.get("group_id")));
        }else */
        if(roleId==5||roleId==6){
           fixBys.add((Integer)user.get("user_id"));
        }
        return fixBys;
    }

    @Override
    public Result1 getMalfunctionList(Map<String, Object> params) {
        List<Integer> fixBys = getfixBysByRoleId((String)params.get("token"));
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
        Integer roleId = (Integer)user.get("role_id");

        //可以查看的故障的部门
        List<String> departmentList=new ArrayList<>();
        //不是公司领导 并且 部门不为空
        if (roleId!=1&&roleId!=2&&departmentId!=null&&departmentId!=0){
          String  cheakMalfunction=malfunctionMapper.selectCheakMalfunctionByDepartmentId(departmentId);
            departmentList=Arrays.asList(cheakMalfunction.split(","));
        }


        //最后一条数据的创建时间时间戳，默认0
        Integer create_at = (params.get("create_at")==null||Integer.parseInt((String)params.get("create_at"))==0)?
                null:Integer.parseInt(params.get("create_at").toString());
        //每页条数，默认为20
        int limitNum = (params.get("limit_num")!=null)?Integer.parseInt(params.get("limit_num").toString()):20;
        int total = 0;
        if(rid==1||rid==2){
            //isCharge=2 表示公司领导可见is_company_manager=1的未处理故障
            total = malfunctionMapper.selectMalfunctionListCount(statusList,uid,fixBys,departmentId,2,companyId,departmentList);
        }else{
            //isCharge=3 表示其他情况
            total = malfunctionMapper.selectMalfunctionListCount(statusList,uid,fixBys,departmentId,3,companyId,departmentList);
        }

        //符合查询条件的总条数
        Map<String,Object> result = new HashMap<>();
        List<Map<String,Object>> malfunctions = new ArrayList<>();
        if(total>0){
            if(rid==1||rid==2){
                malfunctions = malfunctionMapper.selectMalfunctionList(statusList,uid,limitNum,create_at,fixBys,departmentId,2,companyId,departmentList);
            }else{
                malfunctions = malfunctionMapper.selectMalfunctionList(statusList,uid,limitNum,create_at,fixBys,departmentId,3,companyId,departmentList);
            }

            for(Map<String,Object> malfunction:malfunctions){
                if(malfunction.get("fix_by")!=null&&!malfunction.get("fix_by").equals("")){
                    Long fix_by = (Long)malfunction.get("fix_by");
                    malfunction.replace("fix_by",userMapper.selectByPrimaryKey(fix_by.intValue()).getUserName());
                }
                Long create_by = (Long)malfunction.get("create_by");
                if(userMapper.selectByPrimaryKey(create_by.intValue())==null){
                    malfunction.replace("create_by","");
                }else{
                    malfunction.replace("create_by",userMapper.selectByPrimaryKey(create_by.intValue()).getUserName());
                }
            }
        }
        result.put("list",malfunctions);
        result.put("total",total);
        return new Result1(result,rid);
    }


    /**
     * 根据以,隔开的字符串获取image_desc的详细信息
     * @param desc
     * @return
     */
    List<Map<String,Object>> getImageDescs(String desc){
        List<Map<String,Object>> imageDescs = new ArrayList<>();
        if(desc!=null&&desc.length()>0) {
            if (desc.contains(",")) {
                String[] imageDescIds = desc.split(",");
                for (int i = 0; i < imageDescIds.length; i++) {
                    Map<String,Object> image = imageDescMapper.selectByPrimaryKey(Integer.parseInt((imageDescIds[i])));
                    if(image!=null){
                        if(String.valueOf(image.get("content")).contains(QiniuUrl)||String.valueOf(image.get("content")).contains(DOWNURL)){
                            image.replace("content",image.get("content"));
                        }else{
                            image.replace("content",DOWNURL+image.get("content"));
                        }
                        imageDescs.add(image);
                    }
                }
            } else {
                Map<String,Object> image = imageDescMapper.selectByPrimaryKey(Integer.parseInt(desc));
                if(image!=null){
                    if(String.valueOf(image.get("content")).contains(QiniuUrl)||String.valueOf(image.get("content")).contains(DOWNURL)){
                        image.replace("content",image.get("content"));
                    }else{
                        image.replace("content",DOWNURL+image.get("content"));
                    }
                    imageDescs.add(image);
                }
            }
        }
        return imageDescs;
    }

    /**
     * 根据以,隔开的字符串获取audio_desc的详细信息
     * @param audio_desc
     * @return
     */
    List<Map<String,Object>> getAudioDescs(String audio_desc) {
        List<Map<String,Object>> audioDescs = new ArrayList<>();
        if (audio_desc != null && audio_desc.length() > 0) {
            if (audio_desc.contains(",")) {
                String[] audioDescIds = audio_desc.split(",");
                for (int i = 0; i < audioDescIds.length; i++) {
                    Map<String,Object> audio = audioDescMapper.selectByPrimaryKey(Integer.parseInt((audioDescIds[i])));
                    if(audio!=null){
                        if(String.valueOf(audio.get("content")).contains(QiniuUrl)||String.valueOf(audio.get("content")).contains(DOWNURL)){
                            audio.replace("content",audio.get("content"));
                        }else{
                            audio.replace("content",DOWNURL+audio.get("content"));
                        }
                        audioDescs.add(audio);
                    }
                }
            } else {
                Map<String,Object> audio = audioDescMapper.selectByPrimaryKey(Integer.parseInt(audio_desc));
                if(audio!=null){
                    if(String.valueOf(audio.get("content")).contains(QiniuUrl)||String.valueOf(audio.get("content")).contains(DOWNURL)){
                        audio.replace("content",audio.get("content"));
                    }else{
                        audio.replace("content",DOWNURL+audio.get("content"));
                    }
                    audioDescs.add(audio);
                }
            }
        }
        return audioDescs;
    }

    @Override
    public Result1 getMalfunctionById(Map<String, Object> params) {
        int malfunctionId = Integer.parseInt(params.get("mid").toString());
        //根据故障id查询故障详情
        Map<String,Object> malfunction = malfunctionMapper.selectBymid(malfunctionId);
        //根据故障id查询全部库管员id
        List<Integer> storeKeeperIds = materialMapper.selectKeeperByMidFromAppoint(malfunctionId);
        //根据故障id查询全部物料信息
        List<Map<String,Object>> materials = materialMapper.selectMaterialByMalfunctionId(malfunctionId);
        malfunction.put("material",materials);
        Map<String,Object> reason = malfunctionLogMapper.selectByMid(malfunctionId);
        malfunction.put("reason",reason);
        if(storeKeeperIds!=null&&storeKeeperIds.size()>0){
            //根据库管员id查询库管员名字
            List<Map<String,String>> storeKeepers = userMapper.selectUserNameByIds(storeKeeperIds);
            malfunction.put("storekeeper",storeKeepers);
        }
        //将查询到的图片描述转为列表存储到“image_desc”字段中
        String image_desc = (String)malfunction.get("image_desc");
        malfunction.replace("image_desc",getImageDescs(image_desc));

        //将查询到的完成时图片描述转为列表存储到“finish_desc”字段中
        String finish_desc = (String)malfunction.get("finish_desc");
        malfunction.replace("finish_desc",getImageDescs(finish_desc));

        //将查询到的语音描述转为列表存储到“audio_desc”字段中
        String audio_desc = (String)malfunction.get("audio_desc");
        malfunction.replace("audio_desc",getAudioDescs(audio_desc));
        Integer rid = roleMapper.selectRidByUid(getUserIdByToken((String)params.get("token")));
        return new Result1(malfunction,rid);
    }

    @Override
    public Result1 getMalfunctionLog(Map<String, Object> params) {
        //日期（如：20181101），默认今天|
        Map<String,Long> timePeroid = getTimePeriod(params,"00:00:00");
        List<Integer> fixBys = getfixBysByRoleId((String)params.get("token"));
        List<Integer> userIds = new ArrayList<>();
        Map<String,Object> user = getUserByWebToken((String)params.get("token"));
        Integer roleId = roleMapper.selectRidByUid((Integer)user.get("user_id"));
        if(params.get("uid")!=null){
            int uid = Integer.parseInt(params.get("uid").toString());
          //当前用户为普通用户时，传递uid无效）
            if(roleId==5){
            userIds.add((Integer)user.get("user_id"));
          }
          //当前用户为管理员时，不传uid返回全部，传uid返回指定人员数据
          if(fixBys.contains(uid)){
              userIds.add(uid);
          }
        }else{
            userIds.addAll(fixBys);
        }
        //班组id（传0取所有）
        List<Integer> userIdsByGroup = Integer.parseInt(params.get("gid").toString())==0?new ArrayList<>():
                userMapper.selectUidsByGroupId(Integer.parseInt(params.get("gid").toString()));
        Map<String,Object> results = new HashMap<>();
        long startTime = timePeroid.get("startTime");
        long endTime = timePeroid.get("endTime");
        //当日接单列表
        List<Map<String,Object>> list = new ArrayList<>();
        if(roleId==1||roleId==2){
            list = malfunctionMapper.selectMalfunctionLog(startTime,endTime,userIds,userIdsByGroup,2,0);
        }else{
            list = malfunctionMapper.selectMalfunctionLog(startTime,endTime,userIds,userIdsByGroup,3,0);
        }

        //isFinished=0表示未完成，isFinished=1表示已完成
        int finished = 0;
        if(roleId==1||roleId==2){
            finished = malfunctionMapper.selectMalfunctionLog(startTime,endTime,userIds,userIdsByGroup,2,1).size();
        }else{
            finished = malfunctionMapper.selectMalfunctionLog(startTime,endTime,userIds,userIdsByGroup,3,1).size();
        }
        //当日接单总数
        //int total = finished+unfinished;
        int total = list.size();
        int unfinished = total-finished;
        //接单列表的故障id集合
        List<Integer> ids = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        for(Map<String,Object> malfunction:list){
            ids.add(((Long)malfunction.get("id")).intValue());
            Integer status = ((Long)malfunction.get("status")).intValue();
            if(status==5||status==6){
                malfunction.replace("status",1);
            }else{
                malfunction.replace("status",0);
            }
            //fix_at 返回要求为HH:mm
            Long time = (Long)malfunction.get("time");
            if(time!=null){
                malfunction.replace("time",sdf.format(new Date(time*1000)));
            }
        }
        Integer rid = getRoleIdByToken((String)params.get("token"));
        int appoint_num = rid>4?0:(list.size()-total);
        results.put("total",total);
        results.put("finish",finished);
        results.put("unfinish",unfinished);
        results.put("appoint_num",appoint_num);
        results.put("list",list);

        return new Result1(results,rid);
    }

    @Override
    public Result1 addMaterial(Map<String,Object> params) {
        Material material1 = new Material();
        Integer storekeeper = Integer.parseInt(params.get("skid").toString());
        Integer mid = Integer.parseInt(params.get("mid").toString());
        material1.setMalfunctionId(mid);
        material1.setStorekeeper(storekeeper);
        material1.setContent((String)params.get("content"));
        material1.setCreateBy(getUserIdByToken((String)params.get("token")));
        materialMapper.insertSelective(material1);
        MalfunctionWithBLOBs malfunction = new MalfunctionWithBLOBs();
        malfunction.setStorekeeper(storekeeper);
        malfunction.setId(mid);
        malfunctionMapper.updateByPrimaryKeySelective(malfunction);
        return new Result1(null,roleMapper.selectRidByUid(getUserIdByToken((String)params.get("token"))));
    }

    /*php源码
     * 预约到期（定时脚本每天早上八点检测）
    public function expire()
    {
        $date_at = date('Y-m-d',time());
        $select_res = $this->DB_model->query("select * FROM malfunction WHERE status=7 and expire_at='{$date_at}'");
        $list     = [];
        foreach ($select_res->result_array() as $row) {
            $list[] = $row;
        }
        if(count($list)>0){
            foreach($list as $k=>$v){
                if($this->jpush->push(["alias"=>[$v['fix_by'],'env='.ENV]],'您预约解决的维修任务已到期，请尽快解决！','malfunction',$v['id'])){
                    $this->DB_model->update("malfunction",['status'=>8,'remark'=>'','hangup_at'=>'','expire_at'=>''],"id=".$v['id']);
                    $this->DB_model->update("malfunction_log",['status'=>8],"id=".$v['id']);
                    $status = 1;
                }else{
                    $status = 2;
                }
                $this->DB_model->add('push_log', ['status' => $status, 'push_type' => 4, 'malfunction_id' => $v['id'], 'accept_type' => 2,'accept_staff'=>$v['fix_by'], 'push_at' => time()]);
            }
        }
    }
     */
    @Override
    public Result1 addHangup(Map<String, Object> params) {
        int mid = Integer.parseInt(params.get("mid").toString());
        Integer id = malfunctionLogMapper.selectByMidAndFixBy(
                mid,getUserIdByToken((String)params.get("token")));
        Integer rid = roleMapper.selectRidByUid(getUserIdByToken((String)params.get("token")));
        if(id!=null){
            MalfunctionLogWithBLOBs malfunctionLog = new MalfunctionLogWithBLOBs();
            malfunctionLog.setId(id);
            //状态修改为7，挂起
            malfunctionLog.setStatus(7);
            malfunctionLog.setExpireAt((String)params.get("expire_at"));
            malfunctionLog.setRemark((String)params.get("remark"));
            //设置预约时间不为空，在sql语句中插入当前系统时间
            malfunctionLog.setHangupAt(1);
            malfunctionLogMapper.updateByPrimaryKeySelective(malfunctionLog);

            //更新故障表中的故障状态
            malfunctionMapper.updateHangUp(mid,7,(String)params.get("expire_at"),(String)params.get("remark"));
            return new Result1(null,rid);
        }else{
            return new Result1("1005","非当前维修人",rid);
        }
    }

    /**
     * 维修失败
     * @param params
     * @return
     */
    @Override
    public Result1 addFinish(Map<String, Object> params) {
        int mid = Integer.parseInt(params.get("mid").toString());
        int uid = getUserIdByToken((String)params.get("token"));
        Integer rid = roleMapper.selectRidByUid(uid);
        Integer id = malfunctionLogMapper.selectByMidAndFixBy(mid,getUserIdByToken((String)params.get("token")));
        if(id!=null){
            //状态修改为4，维修失败
            MalfunctionLogWithBLOBs malfunctionLog = new MalfunctionLogWithBLOBs();
            malfunctionLog.setStatus(4);
            malfunctionLog.setReason((String)params.get("reason"));
            malfunctionLog.setId(id);
            malfunctionLog.setFixBy(uid);

            //更新故障表中的状态字段
            MalfunctionWithBLOBs malfunction = new MalfunctionWithBLOBs();
            malfunction.setId(mid);
            malfunction.setStatus(4);
            malfunctionMapper.updateByPrimaryKeySelective(malfunction);
            malfunctionLogMapper.updateByPrimaryKeySelective(malfunctionLog);

            //将支援表中的故障状态改为已下线
            supportMapper.updateByMid(mid);

            return new Result1(null,rid);
        }else{
            return new Result1("1005","非当前维修人",rid);
        }
    }

    @Override
    public Result1 addToMalfunction(Map<String, Object> params) {
        Integer rid = roleMapper.selectRidByUid(getUserIdByToken((String)params.get("token")));
        if(params.get("text_desc")==null&&params.get("image_desc")==null&&params.get("audio_desc")==null){
            return new Result1("1005","补充内容不能为空",rid);
        }else{
            List<Map<String,Object>> error_text = new ArrayList<>();
            MalfunctionWithBLOBs malfunction = new MalfunctionWithBLOBs();
            try {
                //若参数中文字描述不为空，进行格式转换并合并到对象该字段中
                List<Map> text=params.get("text_desc")==null?null:
                        JSONObject.parseArray((String)params.get("text_desc"),Map.class);
                if(text!=null&&text.size()>0){
                    malfunction.setTextDesc(malfunction.getTextDesc()+params.get("text_desc"));
                    text.addAll(text);
                }
            }catch(Exception e){
                return new Result1("1006","文字格式描述有误",rid);
            }

            //若参数中图片描述不为空，合并到对象图片描述字段中
            if(params.get("image_desc")!=null){
                if(malfunction.getImageDesc()!=null){
                    malfunction.setImageDesc(malfunction.getImageDesc()+params.get("image_desc"));
                }else{
                    malfunction.setImageDesc((String)params.get("image_desc"));
                }
                List<Map<String,Object>> imageDescs = getImageDescs((String)params.get("image_desc"));
                for(Map<String,Object> imageDesc:imageDescs){
                    Map<String,Object> image = new HashMap<>();
                    image.put("content",imageDesc.get("content"));
                    image.put("create_at",imageDesc.get("creat_at"));
                    error_text.add(image);
                }
            }

            //若参数中语音描述不为空，合并到对象语音描述字段中
            if(params.get("audio_desc")!=null){
                if(malfunction.getAudioDesc()!=null){
                    malfunction.setAudioDesc(malfunction.getAudioDesc()+params.get("audio_desc"));
                }else{
                    malfunction.setAudioDesc((String)params.get("audio_desc"));
                }
                List<Map<String,Object>> audioDescs = getAudioDescs((String)params.get("audio_desc"));
                for(Map<String,Object> audioDesc:audioDescs){
                    Map<String,Object> desc = new HashMap<>();
                    desc.put("content",audioDesc.get("content"));
                    desc.put("create_at",audioDesc.get("create_at"));
                    error_text.add(desc);
                }
            }
            malfunction.setId(Integer.parseInt(params.get("mid").toString()));
            malfunctionMapper.updateByPrimaryKeySelective(malfunction);
            return new Result1(error_text,rid);
        }
    }

    @Override
    public Result1 updateAccept(Map<String, Object> params) {
        String token = (String)params.get("token");
        Integer uid = getUserIdByToken(token);
        Integer rid = roleMapper.selectRidByUid(uid);
        //更新故障表的记录
        malfunctionMapper.updateAcceptById(Integer.parseInt(params.get("mid").toString()),2,uid);
        MalfunctionLog malfunctionLog = addMalfunctionLog(
                uid,Integer.parseInt(params.get("mid").toString()));
        malfunctionLogMapper.insertAppoint(malfunctionLog);
        return new Result1("",rid);
    }

    public MalfunctionLog addMalfunctionLog(Integer uid, Integer mid){
        //向故障log表添加新记录
        MalfunctionLog malfunctionLog = new MalfunctionLog();
        //维修人为参数中uid或当前登陆用户（接受任务）
        malfunctionLog.setFixBy(uid);
        //故障id
        malfunctionLog.setMalfunctionId(mid);
        //故障状态为已指派，2
        malfunctionLog.setStatus(2);
        //故障指派人默认为0
        malfunctionLog.setAppointBy(0);
        return malfunctionLog;
    }

    /**
     * 故障指派
     * @param params
     * @return
     */
    @Override
    public Result1 updateAppoint(Map<String, Object> params) {
        String token = (String)params.get("token");
        Integer rid = roleMapper.selectRidByUid(getUserIdByToken(token));
        Integer appointId = getUserIdByToken(token);
        Integer fixBy = Integer.parseInt(params.get("uid").toString());
        int mid = Integer.parseInt(params.get("mid").toString());
        //更新故障表的记录
        malfunctionMapper.updateAppointById(mid,2,fixBy);
        MalfunctionLog malfunctionLog = addMalfunctionLog(fixBy, Integer.parseInt(params.get("mid").toString()));
        //指派人为当前登陆用户id
        malfunctionLog.setAppointBy(appointId);
        malfunctionLogMapper.insertAppoint(malfunctionLog);

        //极光推送
        String title = "收到新的维修任务！";
        List<String> alias=new ArrayList<>();
        alias.add(UrlConfig.ENV_TEST);
        alias.add(params.get("uid").toString());
        Map<String, String> extras = new HashMap<>();
        extras.put("type","malfunction");
        extras.put("id", String.valueOf(mid));
        extras.put("detail","");
        Jipush.sendByAliasList(title,extras,alias);
        return new Result1("",rid);
    }

    @Override
    public Result1 updateCheck(Map<String, Object> params) {
        String token = (String)params.get("token");
        Integer rid = roleMapper.selectRidByUid(getUserIdByToken(token));
        int mid = Integer.parseInt(params.get("mid").toString());
        Malfunction malfunction = malfunctionMapper.selectByPrimaryKey(mid);
        //故障状态修改为已通过审核，sql语句中生成审核时间戳
        malfunctionMapper.updateCheckById(mid,6);
        //故障记录表状态修改为已通过审核，sql语句中生成审核时间戳
        malfunctionLogMapper.updateByMidAndFixBy(mid, malfunction.getFixBy(),6);
        return new Result1("",rid);
    }
}
