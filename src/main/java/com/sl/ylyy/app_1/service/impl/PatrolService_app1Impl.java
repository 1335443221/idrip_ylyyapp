package com.sl.ylyy.app_1.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sl.ylyy.app_1.dao.*;
import com.sl.ylyy.app_1.entity.Patrol;
import com.sl.ylyy.app_1.entity.PatrolLog;
import com.sl.ylyy.app_1.entity.User;
import com.sl.ylyy.app_1.service.PatrolService_app1;
import com.sl.ylyy.common.Interceptor.GlobalExceptionHandler;
import com.sl.ylyy.common.config.UrlConfig;
import com.sl.ylyy.common.utils.Jipush;
import com.sl.ylyy.common.utils.Result1;
import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.sl.ylyy.common.utils.TimePeriodUtil.*;
import static com.sl.ylyy.common.utils.JwtToken.*;

/**
 * TODO modify hlc on 2019-8-9 10:48:27 修改了一键打卡和批量打卡
 */
@Service("patrolTypeServiceApp1")
public class PatrolService_app1Impl implements PatrolService_app1 {
    @Autowired
    private PatrolTypeMapper patrolTypeMapper;
    @Autowired
    private PatrolPointMapper patrolPointMapper;
    @Autowired
    private PatrolMapper patrolMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PatrolLogMapper patrolLogMapper;
    @Autowired
    private MalfunctionMapper malfunctionMapper;
    @Autowired
    private UrlConfig urlConfig;
    @Autowired
    private RoleMapper roleMapper;
    static Logger logger = (Logger) Logger.getInstance(GlobalExceptionHandler.class);

    @Override
    public Result1 getAllPatrolTypes(Map<String, Object> params) {
        String token = (String) params.get("token");
        Integer uid = getUserIdByToken(token);
        Map<String,Object> user = getUserByWebToken(token);
        Integer departmentId =null;
        if(params.get("department_id")==null){
            departmentId = (Integer)user.get("department_id");
        }else{
            departmentId = (Integer)params.get("department_id");
        }
        Integer companyId = (Integer)user.get("company_id");
        return new Result1(patrolTypeMapper.selectAllPatrolTypes(departmentId,companyId), roleMapper.selectRidByUid(uid));
    }

    @Override
    public Result1 getAllPatrolPoints(Map<String, Object> params) {
        String token = (String) params.get("token");
        Map<String,Object> user = getUserByWebToken(token);
        Integer companyId = (Integer) (user.get("company_id"));
        int uid = (Integer)user.get("user_id");
        Integer createAt = params.get("create_at") == null ? 0 : Integer.parseInt(params.get("create_at").toString());
        return new Result1(patrolPointMapper.selectAllPatrolPoints(companyId, createAt), roleMapper.selectRidByUid(uid));
    }

    /**
     * 根据当前登陆用户角色判断可以查看的用户id集合
     *
     * @return
     */
    List<Integer> getPatrolBys(String token) {
        Map<String, Object> user = getUserByWebToken(token);
        Integer roleId = (Integer) user.get("role_id");
        List<Integer> patrolBys = new ArrayList<>();
        if (roleId == 1||roleId==2) {
            patrolBys = userMapper.selectUidsByCompanyId((Integer) user.get("company_id"));
        } else if (roleId == 3) {
            patrolBys = userMapper.selectUidsByDepartmentId((Integer) user.get("department_id"));
        } else if (roleId == 4) {
            patrolBys = userMapper.selectUidsByGroupId((Integer) user.get("group_id"));
        } else {
            patrolBys.add((Integer) user.get("user_id"));
        }
        return patrolBys;
    }

    /**
     * 根据当前时间判断time_type
     * @param time
     * @return
     */
    public Integer getTimeTypeNow(Long time) {
        Date date = new Date();
        if(time!=0L){
            date = new Date(time*1000);
        }
        String[] now = new SimpleDateFormat("HH:mm").format(date).split(":");
        Integer hour = Integer.parseInt(now[0]);
        Integer minute = Integer.parseInt(now[1]);
        int timeType = 0;
        //在早八点到晚八点间，时间类型是早班
        if (hour < 20 && hour > 8) {
           timeType = 1;
           //在晚八点之后和早八点之前，时间类型是晚班
        } else if (hour > 20 || hour < 8) {
            timeType = 2;
            //小时数等早八点或晚八点的时候，根据分钟判断是否是整点
        } else if (hour == 20 ) {
            if (minute > 0) {
                timeType = 2;
            } else {
                timeType = 1;
            }
        } else if (hour==8){
            if (minute > 0) {
                timeType = 1;
            } else {
                timeType = 2;
            }
        }

            return timeType;
    }

    /**
     * 巡检列表
     * TODO hlc 请查看map.xml文件中的TODO
     */
    @Override
    public Result1 getPatrols(Map<String, Object> params) {

        Map<String, Object> result = new HashMap<>();
        String token = (String) params.get("token");
        //计算查询的开始和结束时间
        Map<String, Long> timePeroid = getTimePeriod(params, "08:00:00");
        Map<String, Object> user = getUserByWebToken(token);
        Integer rid = roleMapper.selectRidByUid((Integer)user.get("user_id"));
        Integer companyId = (Integer)user.get("company_id");
        Integer departmentId = (Integer)user.get("department_id");
        Integer userId = (Integer)user.get("user_id");
        Long start = timePeroid.get("startTime");
        Long end =  timePeroid.get("endTime");
        int timeType = 0;
       // List<Integer> uIds = new ArrayList<>();
        if(rid==1||rid==2){
            //公司领导可以查看全公司巡检计划
            departmentId=null;
          //  uIds = userMapper.selectUidsByCompanyId((Integer)user.get("company_id"));
        }else{
            timeType = getTimeTypeNow(0L);
            companyId=null;
            //巡检计划可以在部门范围内可见
         //   uIds = userMapper.selectUidsByDepartmentId(departmentId);
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try{
            date = (params.get("date")!=null)?simpleDateFormat.parse((String)params.get("date")+" "+" 00:00:00")
                    :simpleDateFormat.parse(simpleDateFormat.format(new Date()));
        }catch (ParseException e) {
            e.printStackTrace();
        }
        Map<String,Long> weekPeriod = getWeekOrMonthPeriod(date,1);
        Map<String,Long> monthPeriod = getWeekOrMonthPeriod(date,2);
        int total = patrolMapper.selectPatrolCount(companyId,start,end,timeType,departmentId,
                weekPeriod.get("startTime"),weekPeriod.get("endTime"),
                monthPeriod.get("startTime"),monthPeriod.get("endTime"),userId);

        //当前页数，默认为1
        int page = (params.get("page") != null) ? Integer.parseInt(params.get("page").toString()) : 1;
        //每页条数，默认为20
        int limitNum = (params.get("limit_num") != null) ? Integer.parseInt(params.get("limit_num").toString()) : 20;
        int startNum = (page - 1) * limitNum;
        int pageSize = (page*limitNum) > total ? total-((page-1)*limitNum) : limitNum;

        List<Map<String, Object>> patrolList = patrolMapper.selectPatrols(
                companyId,start,end,startNum,pageSize,timeType,departmentId,
                weekPeriod.get("startTime"),weekPeriod.get("endTime"),
                monthPeriod.get("startTime"),monthPeriod.get("endTime"),userId);

        result.put("total", total);
        result.put("list", patrolList);
        return new Result1(result, rid);
    }

    @Override
    @Transactional
    public Result1 addPatrol(Map<String, Object> params) {
        String token = (String) params.get("token");
        Map<String, Object> user = getUserByWebToken(token);
        Integer companyId = (Integer)user.get("company_id");
        Integer departmentId = Integer.parseInt(String.valueOf(params.get("department_id")));
        Integer rid = roleMapper.selectRidByUid((Integer)user.get("user_id"));
        //判断巡检类型是否包含在全部巡检类型列表中
        if (!patrolTypeMapper.selectPatrolTypeIds(departmentId).contains(Integer.parseInt(params.get("type").toString()))) {
            return new Result1("1005", "巡检类型参数有误", rid);
        }
        //判断巡检点是否包含在全部巡检点列表中
        if (!patrolPointMapper.selectAllIdsByCompany(companyId, 0).contains(Integer.parseInt(params.get("patrol_point_id").toString()))) {
            return new Result1("1006", "巡检点参数有误", rid);
        }
        //判断巡检时间段参数是否合规(根据不同部门判断)
        Integer time_type = Integer.parseInt(params.get("time_type").toString());

        //if (!patrolTypeMapper.selectTimeTypeByDepartmentId(departmentId).contains(time_type)) {
      /*  if(!patrolTypeMapper.selectTimeTypeByDepartmentId(departmentId).contains(time_type)){
            return new Result1("1007", "巡检时间段参数有误", rid);
        }*/

        /*//判断巡检次数是否合规(1部门不大于2次；2部门只能为1，2，4次；4部门只能为1次)
        int count = Integer.parseInt(params.get("count").toString());
        if (((departmentId==1)&&(count != 1) && (count != 2))||
                ((departmentId==2)&&(count!=1)&& (count != 2)&& (count != 4))||
                ((departmentId==4)&&(count != 1))) {
            return new Result1("1008", "巡检次数参数有误", rid);
        }*/

        Integer user_id = (Integer) user.get("user_id");
        Integer patrol_point_id = Integer.parseInt(params.get("patrol_point_id").toString());

        String alais = patrolTypeMapper.selectTimeTypeAliasById(time_type);
        List<String> alaisList = patrolMapper.selectTimeTypes(departmentId,patrol_point_id);
        if(alaisList.contains(alais)){
            return new Result1("1009","本部门该时段和巡检点已存在巡检任务",rid);
        }

        //早班晚班、周巡检、月巡检不能同时存在在一个巡检计划中
        if(((alais.equals("early")||alais.equals("evening"))&&(alaisList.contains("weekly")||alaisList.contains("monthly")))
        ||((alais.equals("weekly")||alais.equals("monthly"))&&(alaisList.contains("early")||alaisList.contains("evening")))||
                ((alais.equals("weekly"))&&alaisList.contains("monthly"))||
                ((alais.equals("monthly"))&&alaisList.contains("weekly"))){
            return new Result1("1010","巡检时间段有重叠",rid);
        }

        String content = (String) params.get("content");
        Patrol patrol = new Patrol();
        patrol.setCreateBy(user_id);
        patrol.setContent(content);
        patrol.setCount(Integer.parseInt(params.get("count").toString()));
        patrol.setPatrolPointId(patrol_point_id);
        patrol.setTimeType(time_type);
        patrol.setType(Integer.parseInt(params.get("type").toString()));
        patrol.setDepartmentId(departmentId);
        patrol.setTimeInterval(Double.parseDouble(params.get("time_interval").toString()));  //间隔时间

        patrolMapper.insert(patrol);

        //极光推送巡检任务给全部用户
        //audience:tag_and
        List<String> tags = new ArrayList<>();
        tags.add(UrlConfig.ENV_TEST);
        if (params.get("department_id").equals("0")) {
            tags.add("company_id=" + companyId);
        } else {
            tags.add("department_id=" + departmentId);
        }
        //notification:alert
        String msg_content = "收到新的巡检计划！";
        //notification:title
        String notification_title = "";
        //notification:extras,参照文档格式
        /*JsonObject extras=new JsonObject();
        extras.addProperty("type","patrol");
        extras.addProperty("id",patrol.getId());
        extras.addProperty("detail",(String)params.get("content"));*/
        Map<String, String> extras = new HashMap<>();
        extras.put("type", "patrol");
        extras.put("id", String.valueOf(patrol.getId()));
        extras.put("detail", (String) params.get("content"));
        Jipush.sendToTagList(tags, msg_content, "", extras, notification_title);

        return new Result1(null, rid);
    }

    @Override
    public Result1 getPatrolLog(Map<String, Object> params) {
        String token = (String) params.get("token");
        Map<String, Object> user = getUserByWebToken(token);
        Integer roleId = roleMapper.selectRidByUid((Integer)user.get("user_id"));
        Map<String,Object> paramsMap=new HashMap<>();
        Integer uid = null;
        List<Integer> userIds = new ArrayList<>();
        if (params.get("uid") != null) {
            //非管理员权限的人员传uid无效，默认为当前登陆账号的uid
            if (roleId == 5 || roleId == 6) {
                uid=(Integer) user.get("user_id");
                //判断管理员指定的uid是否为自己权限下的员工id
            } else if (getPatrolBys(token).contains(Integer.parseInt(params.get("uid").toString()))) {
                uid=Integer.parseInt(params.get("uid").toString());
            }
            //uid传空，默认查询当前登陆账号下的权限内的全部员工id
        } else {
            if (roleId == 1||roleId==2) {
                paramsMap.put("company_id",user.get("company_id"));
            } else if (roleId == 3) {
                paramsMap.put("department_id",user.get("department_id"));
            } else if (roleId == 4) {
                paramsMap.put("group_id",user.get("group_id"));
            } else {
                paramsMap.put("user_id",user.get("user_id"));
            }
        }
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        try {
            date = (params.get("date")!=null)?sd.parse((String)params.get("date")):sd.parse(sd.format(new Date()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //条件查询时间段，默认为当天
        Map<String, Long> timePeroid = getTimePeriod(params, "08:00:00");
        Map<String,Long> weekPeriod = getWeekOrMonthPeriod(date,1);
        Map<String,Long> monthPeroid = getWeekOrMonthPeriod(date,2);
        //过滤时间
        List<Map<String, Object>> patrolLogResult = new ArrayList<>();
        List<Map<String, Object>> patrolLogs = (patrolMapper.selectPatrolLog(
                timePeroid.get("startTime"), timePeroid.get("endTime"),
                uid,paramsMap));
        for (Map<String, Object> result : patrolLogs) {
            //time是最后一次打卡时间
            Integer time = ((Long) result.get("time")).intValue();
            Integer status = ((Long) result.get("status")).intValue();
            String alais = (String)result.get("alais");

            //根据最后一次打卡时间，判断该条巡检记录是否在查询范围之内
            if ((alais.equals("early")||alais.equals("evening"))&&
                    time > timePeroid.get("startTime") && time < timePeroid.get("endTime") ){
                Long timestamp = Long.valueOf(time) * 1000;
                result.replace("time", new SimpleDateFormat("HH:mm").format(new Date(timestamp)));
                patrolLogResult.add(result);
            }

            if (alais.equals("weekly")&&time>weekPeriod.get("startTime")&&time<weekPeriod.get("endTime") ){
                Long timestamp = Long.valueOf(time) * 1000;
                result.replace("time", new SimpleDateFormat("HH:mm").format(new Date(timestamp)));
                patrolLogResult.add(result);
            }
            if (alais.equals("monthly")&&time>monthPeroid.get("startTime")&&time<monthPeroid.get("endTime") ){
                Long timestamp = Long.valueOf(time) * 1000;
                result.replace("time", new SimpleDateFormat("HH:mm").format(new Date(timestamp)));
                patrolLogResult.add(result);
            }
        }
        Map<String, Object> results = new HashMap<>();
        results.put("finish", patrolLogResult.size());
        results.put("list", patrolLogResult);
        return new Result1(results, roleId);
    }

    /**
     * 巡检报表
     *
     * @param params
     * @return
     */
    @Override
    public Result1 getPatroReport(Map<String, Object> params) {
        String token = (String) params.get("token");
        Integer rid = roleMapper.selectRidByUid(getUserIdByToken(token));
        Map<String, Object> user = getUserByWebToken(token);
        Map<String,Object> paramsMap=new HashMap<>();
        if (rid <= 4) {
            if (Integer.parseInt((String) params.get("gid")) == 0) {
                if (rid == 1||rid==2) {
                    paramsMap.put("company_id",user.get("company_id"));
                } else if (rid == 3) {
                    paramsMap.put("department_id",user.get("department_id"));
                } else if (rid == 4) {
                    paramsMap.put("group_id",user.get("group_id"));
                }
            } else {
                Integer gid = Integer.parseInt((String) params.get("gid"));
                //根据组id查询组内全部员工id
                List<Integer> groupUserIds = userMapper.selectUidsByGroupId(gid);
                if (params.get("uid") == null) {
                    paramsMap.put("group_id",gid);
                }
                //当gid和uid都不为空的时候，判断传入的uid是否合规
                if (params.get("uid") != null) {
                    if (groupUserIds.contains(Integer.parseInt((String) params.get("uid")))) {
                        paramsMap.put("user_id",params.get("uid"));
                    } else {
                        return new Result1("1005", "没有访问此数据权限", rid);
                    }
                }
            }
        } else {
            paramsMap.put("user_id",user.get("user_id"));
        }
        Integer type = params.get("type") != null ? Integer.parseInt(params.get("type").toString()) : null;
        //条件查询时间段，默认为当天
        Map<String, Long> timePeroid = getTimePeriod(params, "08:00:00");
        List<Map<String, Object>> patrolReportResult = new ArrayList<>();
        List<Map<String, Object>> patrolReports = (patrolMapper.selectPatrolReport(
                timePeroid.get("startTime"), timePeroid.get("endTime"), paramsMap, type));
        String date = (params.get("date")) == null ? new SimpleDateFormat("yyyy-MM-dd").format(new Date()) :
                (String) params.get("date");
        for (Map<String, Object> patrolReport : patrolReports) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //设置格式
            patrolReport.replace("begin_at", sdf.format((Long) (patrolReport.get("begin_at")) * 1000));
            patrolReport.put("date", date);
            Long patrol_by = (Long) patrolReport.get("user_id");
            //根据巡检人和故障来源，查询出的故障数量
            patrolReport.put("malfunction_count", malfunctionMapper.selectCountByPatrolAndSource(patrol_by.intValue()));
            if (patrolReport.get("end_at") == null) {
                patrolReport.put("end_at", null);
            } else {
                patrolReport.replace("end_at", sdf.format((Long) patrolReport.get("end_at") * 1000));
            }
            patrolReportResult.add(patrolReport);
        }
        return new Result1(patrolReportResult, rid);
    }

    @Override
    @Transactional
    public Result1 setClock(Map<String, Object> params) {
        String token = (String) params.get("token");
        Integer rid = getRoleIdByToken(token);
        PatrolLog patrolLog = new PatrolLog();
        int patrolId = Integer.parseInt(params.get("patrol_id").toString());
        patrolLog.setPatrolId(patrolId);
        //根据参数中巡检id获取对应的巡检点编号
        String number = patrolPointMapper.selectNumberByPatrolId(patrolId);
        if (((String) params.get("number")).equals(number)) {
            Patrol patrol = patrolMapper.selectByPrimaryKey(patrolId);
            int hourNow = Integer.parseInt(new SimpleDateFormat("HH").format(new Date()));
            int timeType = patrol.getTimeType();
            String alias = patrolTypeMapper.selectTimeTypeAliasById(timeType);
            if((alias.equals("early")&&(hourNow<8||hourNow>=20))||(alias.equals("evening")&&(hourNow>=8&&hourNow<20))){
                return new Result1("1008","打卡时间与巡检班次不一致");
            }
            return isLegalByPatrolId(null,token, patrolId, alias, patrol.getCount(),null);
        } else {
            return new Result1("1005", "打卡编号与该巡检不一致", rid);
        }
    }

    /**
     * 一键打卡
     * TODO modify hlc on 2019-8-9 10:44:12 isLegalByPatrolId函数 设置first和second bugID=382
     */
    @Override
    @Transactional
    public Result1 setClockOnce(Map<String, Object> params) {
        String token = (String) params.get("token");
        Map<String,Object> user = getUserByWebToken(token);
        int userId = (Integer)user.get("user_id");
        int rid = getRoleIdByToken(token);
        int timeType = getTimeTypeNow(0L);
        Patrol patrol = patrolMapper.selectPatrolByNumber((String) params.get("number"), userId, timeType,
                (Integer)user.get("department_id"));
        if (patrol != null) {
            String alais = patrolTypeMapper.selectTimeTypeAliasById(patrol.getTimeType());
            return isLegalByPatrolId(null,token, patrol.getId(),alais, patrol.getCount(),null);
        } else {
            return new Result1("1005", "打卡编号与该巡检不一致", rid);
        }
    }

    /**
     *1.1版本批量打卡，参数为字符串形式的user_id,number,time集合
     * @param params
     * @return
     */
    @Override
    @Transactional
    public Result1 setLotClock_1(Map<String, Object> params) {
        List<Map<String,Object>> results = new ArrayList<>();
        String token = (String)params.get("token");
        int rid = roleMapper.selectRidByUid(getUserIdByToken(token));
        String str = (String)params.get("clock_str");
        List<Map> clocks = (List)getClocksByParams(token,str).getData();
        if(clocks!=null&&clocks.size()>0){
            //遍历全部参数集合
            for(Map clock:clocks){
                int userId = (int)clock.get("user_id");
                User user = userMapper.selectByPrimaryKey(userId);
                Integer departmentId = user.getDepartmentId();
                String number = (String)clock.get("number");
                long time =Long.parseLong((String)clock.get("time"));
                int timeType = getTimeTypeNow(time);

                //根据巡检点和巡检时间类型判断是否存在巡检计划
                Patrol patrol = patrolMapper.selectPatrolByNumber(number, userId, timeType,departmentId);
                if (patrol != null) {
                    //巡检计划对应的时间类型：early、evening、weekly、monthly
                    String alais = patrolTypeMapper.selectTimeTypeAliasById(patrol.getTimeType());
                    Result1 result = isLegalByPatrolId(userId,token, patrol.getId(),alais, patrol.getCount(),time);
                    //将该条巡检打卡的结果错误码存入map中
                    if(result.getCode().equals("1006")){
                        clock.put("code","1006");
                    }else if(result.getCode().equals("1007")){
                        clock.put("code","1007");
                    }else{
                        clock.put("code","1000");
                    }
                } else {
                    clock.put("code","1005");
                }
                results.add(clock);
            }
        }
        return new Result1(results,rid);
    }


    //根据参数判断批量打卡集合
    public Result1 getClocksByParams(String token,String str){
        Integer rid = roleMapper.selectRidByUid(getUserIdByToken(token));
        List<Map> clocks = new ArrayList<>();
        try {
            clocks = JSONArray.parseArray(str, Map.class);
        } catch (Exception e) {
            return new Result1("1005", "数据格式有误", rid);
        }
        //判断数据格式
        if (clocks == null) {
            return new Result1("1005", "数据格式有误", rid);
        }
        return new Result1(clocks);
    }


    @Override
    public Result1 getAllPatrolTimes(String token) {
        Map<String,Object> user = getUserByWebToken(token);
        Integer rid = roleMapper.selectRidByUid((Integer)user.get("user_id"));
        List<Map<String,Object>> patrolTimes = patrolTypeMapper.selectPatrolTimes();
        for(Map<String,Object> patrolTime:patrolTimes){
            List<Integer> counts = new ArrayList<>();
            String count = (String)patrolTime.get("count");
            if(count.contains(",")){
                String[] countArray = ((String) patrolTime.get("count")).split(",");
                //Lambda表达式 String数组转int
                int[] array = Arrays.stream(countArray).mapToInt(Integer::parseInt).toArray();
                Integer[] list = ArrayUtils.toObject(array);
                counts = Arrays.asList(list);
            }else{
                counts.add(Integer.parseInt(count));
            }
            patrolTime.replace("count",counts);
        }

        return new Result1(patrolTimes,rid);
    }

 /*   *//**
     * 批量打卡
     *//*
    @Override
    @Transactional
    public Result1 setLotClock(Map<String, Object> params) {
        List<Map<String, Integer>> results = new ArrayList<>();
        String token = (String) params.get("token");
        String str = (String)params.get("clock_str");
        Integer rid = roleMapper.selectRidByUid(getUserIdByToken(token));
        List<Map> clocks = (List)getClocksByParams(token,str).getData();
        //遍历全部参数集合
        if(clocks!=null&&clocks.size()>0){
            for (Map<String, Object> clock : clocks) {
                //count 只能=1 or =2
                int count = (int) clock.get("count");
                //判断参数格式，如果count大于2或者没有打卡时间，则参数无效
                if ((count > 2 || count < 1) ||
                        ((clock.get("second")!=null&&!clock.get("second").equals(""))&& count == 1)) {
                    return new Result1("1005", "数据格式有误", rid);
                }
                Map<String, Integer> map = new HashMap<>();
                int patrol_id = (int) clock.get("patrol_id");
                String alais = patrolTypeMapper.selectTimeTypeAliasById(patrolMapper.selectByPrimaryKey(patrol_id).getTimeType());
                Map<String, Long> timePeroid = getHalfDayTime(0L,alais);
                long startTime = timePeroid.get("startTime");
                long endTime = timePeroid.get("endTime");
                //先检测该巡检的巡检状态
                Map<String,Object> patrolLogMap = patrolLogMapper.selectByPatrolId(patrolBy,patrol_id, startTime, endTime,0L,0L
                ,0L,0L);
                //若没有该巡检记录，则新增一条巡检记录
                if (patrolLogMap == null) {
                    PatrolLog patrolLog = new PatrolLog();
                    patrolLog.setPatrolId((int) clock.get("patrol_id"));
                    patrolLog.setPatrolBy(getUserIdByToken(token));
                    map.put("code",1000);
                    //根据总次数count 设置status
                    if (count == 1) {
                        patrolLog.setStatus(2);
                    } else if (count == 2) {
                        //此处应该设置为1 未巡检完成, status=2代表完成当前班次所有打卡次数
                        if (StringUtils.isEmpty(clock.get("second"))) {
                            patrolLog.setStatus(1);
                        } else {
                            patrolLog.setStatus(2);
                        }
                    }
                    //如果存在first, 存入
                    if (!StringUtils.isEmpty(clock.get("first"))) {
                        patrolLog.setFirstClockAt(Integer.parseInt(clock.get("first").toString()));
                    }
                    //如果存在second, 存入
                    if (!StringUtils.isEmpty(clock.get("second"))) {
                        //如果first为null , 只存入second
                        if (!StringUtils.isEmpty(clock.get("first"))) {
                            //如果不为null, 判断符合间隔半小时打卡条件的, 存入
                            int diff = Integer.parseInt(clock.get("second").toString()) - Integer.parseInt(clock.get("first").toString());
                            int day = diff / (24 * 60 * 60);
                            Double hour = (diff / (60.0 * 60.0) - day * 24);
                            if (day == 0 && hour < 0.5) {
                                //return new Result1("1007", "两次打卡时间需间隔半小时以上", rid);
                                map.replace("code",1007);
                            }
                        }
                        patrolLog.setSecondClockAt(Integer.parseInt(clock.get("second").toString()));
                        patrolLog.setStatus(2);
                    }
                    patrolLog.setPatrolBy(getUserIdByToken((String) params.get("token")));

                    patrolLogMapper.insert(patrolLog);
                    map.put("partol_id", Integer.parseInt(clock.get("patrol_id").toString()));
                    results.add(map);
                }else{
                    //批量打卡请求接口传first, 服务器写入数据库成功后, 手机断网了, app没有接收到返回, 下次联网时, 会再次请求的传first的
                    //数据比对, 如果传的first与库里存在的first相同, 就提示第一次打卡已完成就行了

                    //如果表里存在数据, 更新一下
                    if ((Integer)patrolLogMap.get("status") == 1 && !StringUtils.isEmpty(clock.get("second"))) {
                        long diff = 0;
                        //如果存在first, 需要判断一下
                        if (!StringUtils.isEmpty(clock.get("first"))) {
                            diff = Integer.parseInt(clock.get("second").toString()) - Integer.parseInt(clock.get("first").toString());
                        }else if(patrolLogMap.get("first_clock_at")!=null){
                            diff = Integer.parseInt(clock.get("second").toString()) - (Long)patrolLogMap.get("first_clock_at");
                        }
                        map.put("code",1000);
                        long day = diff / (24 * 60 * 60);
                        Double hour = (diff / (60.0 * 60.0) - day * 24);
                        if (day == 0 && hour < 0.5) {
                            //return new Result1("1007", "两次打卡时间需间隔半小时以上", rid);
                            map.replace("code",1007);
                        }
                        //更新已有log 并且更新状态
                        patrolLogMap.put("second_clock_at",Integer.parseInt(clock.get("second").toString()));
                        patrolLogMap.put("status",2);
                        patrolLogMapper.updatePatrolLog(patrolLogMap);
                    }else{
                        //return new Result1("1006", "打卡已完成", rid);
                        map.put("code",1006);
                    }
                    map.put("partol_id", Integer.parseInt(clock.get("patrol_id").toString()));
                    logger.info(JSONObject.toJSONString(map));
                    results.add(map);
                }
            }
        }
        return new Result1(results, rid);
    }
*/

    /**
     * 判断打卡时间
     *
     * @param patrolId
     * @param count    巡检总次数
     * @return Result1
     */
    Result1 isLegalByPatrolId(Integer patrolBy,String token, int patrolId, String alais, int count,Long time) {
        int userId = getUserIdByToken(token);
        Integer rid = roleMapper.selectRidByUid(userId);
        if (patrolBy==null||patrolBy==0){
            patrolBy=userId;
        }
        //获取Unix时间戳
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Long unixTimestamp = null;
        if(time==null){
            try {
                unixTimestamp = (long) sdf.parse(sdf.format(new Date())).getTime() / 1000;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }else{
            unixTimestamp = time;
        }
        //unixTimestamp = Long.parseLong("1565970525");
        Long startTime = null;
        Long endTime = null;
        Long weekStart = null;
        Long weekEnd = null;
        Long monthStart = null;
        Long monthEnd = null;
        //若根据已知时间戳和时间类型，得到这个时间段内的班次起始和终止时间
        //早班晚班，周巡检，月巡检分别计算
        if(alais.equals("early")||alais.equals("evening")){
            Map<String, Long> timePeroid = getHalfDayTime(time,alais);
            startTime = timePeroid.get("startTime");
            endTime = timePeroid.get("endTime");
        }
        if(alais.equals("weekly")){
            Map<String,Long> weekPeriod = getWeekOrMonthPeriod(new Date(time*1000),1);
            weekStart = weekPeriod.get("startTime");
            weekEnd = weekPeriod.get("endTime");
        }
        if(alais.equals("monthly")){
            Map<String,Long> monthPeriod = getWeekOrMonthPeriod(new Date(time*1000),2);
            monthStart = monthPeriod.get("startTime");
            monthEnd = monthPeriod.get("endTime");
        }

        //先检测该巡检的巡检状态
        Map<String,Object> patrolLogMap = patrolLogMapper.selectByPatrolId
                (patrolBy,patrolId, startTime, endTime,weekStart,weekEnd,monthStart,monthEnd);
        //若该巡检还未完成，生成新的巡检记录
        if (patrolLogMap == null) {
            PatrolLog patrolLog_new = new PatrolLog();
            patrolLog_new.setPatrolId(patrolId);
            patrolLog_new.setPatrolBy(patrolBy);
            //根据总次数设置是否已完成巡检
            if (count == 1) {
                patrolLog_new.setStatus(2);
            } else {
                patrolLog_new.setStatus(1);
            }

            patrolLogMapper.insertSelective(patrolLog_new);  //插入报表
            insertPatrolLogClock(patrolLog_new.getId(),unixTimestamp.intValue()); //插入打卡时间
            logger.info(JSONObject.toJSONString(patrolLog_new));

            //若已有该巡检的巡检记录，判断此次打卡的有效性
        } else {
            //判断打卡次数
            if(((Long)(patrolLogMap.get("status"))).intValue()==2){
                return new Result1("1006", "打卡已完成", rid);
            }

            //判断两次打卡间隔是否低于30分钟，若低于则抛出异常
            Long diff = unixTimestamp - (Long) patrolLogMap.get("clock_at2");
            double time_interval=Double.parseDouble(String.valueOf(patrolLogMap.get("time_interval")));

            Long day = diff / (24 * 60 * 60);
            Double hour = ((diff / (60.0 * 60.0) - day * 24));
            if (day == 0 && hour < time_interval) {
                return new Result1("1007", "两次打卡时间相隔太短", rid);
            }
            PatrolLog patrolLog = new PatrolLog();

            int patrolLogId=Integer.parseInt(String.valueOf(patrolLogMap.get("id")));
            int nowCount=patrolLogMapper.getClockCountByPatrolLogId(patrolLogId);
            nowCount=nowCount+1;
            if (count==nowCount){  //如果次数相同 则已完成
                patrolLog.setStatus(2);
            }

            if(patrolBy!=null){
                patrolLog.setPatrolBy(patrolBy);
            }else{
                patrolLog.setPatrolBy(userId);
            }
            patrolLog.setId(((Long)patrolLogMap.get("id")).intValue());
            patrolLog.setPatrolId(((Long)patrolLogMap.get("patrol_id")).intValue());
            patrolLogMapper.updatePatrolLogById(patrolLog);
            insertPatrolLogClock(patrolLogId,unixTimestamp.intValue()); //插入打卡时间

            logger.info(JSONObject.toJSONString(patrolLog));
        }

        return new Result1("", rid);
    }



    /**
     * 打卡时间间隔列表
     * @param token
     * @return
     */
    @Override
    public Result1 patrolTimeInterval(String token) {
        int userId = getUserIdByToken(token);
        Integer rid = roleMapper.selectRidByUid(userId);

        List<Double> list= patrolMapper.patrolTimeInterval();

        return Result1.success(list,rid);
    }


    /**
     * 插入打卡记录
     * @param patrol_log_id
     * @param clock_at
     * @return
     */
    public int insertPatrolLogClock(int patrol_log_id,int clock_at) {
        Map<String,Object> clockMap=new HashMap<>();
        clockMap.put("patrol_log_id",patrol_log_id);
        clockMap.put("clock_at",clock_at);
        return patrolLogMapper.insertPatrolLogClock(clockMap);
    }





}
