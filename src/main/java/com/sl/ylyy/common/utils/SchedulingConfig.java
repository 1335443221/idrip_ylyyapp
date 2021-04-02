package com.sl.ylyy.common.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sl.ylyy.app_1.dao.MalfunctionLogMapper;
import com.sl.ylyy.app_1.dao.MalfunctionMapper;
import com.sl.ylyy.manager.dao.RongCloudDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import com.sl.ylyy.manager.dao.SupervisionDao;
import org.springframework.stereotype.Component;

@Configuration
@EnableScheduling
@Component
public class SchedulingConfig {
    @Autowired
    private SupervisionDao SupervisionDao;
    @Autowired
    private RongCloudDao rongCloudDao;
    @Autowired
    private DateUtil dateUtil;
    @Autowired
    private MalfunctionMapper malfunctionMapper;
    @Autowired
    private MalfunctionLogMapper malfunctionLogMapper;
    
  /*//  @Scheduled(cron = "0 0 2 * * ?")   //早上两点重置 施工监护上报次数
    private void reset_supervision_day_count() {
         SupervisionDao.reset_supervision_day_count(new HashMap<>());
         System.out.println(new Date()+"早上两点");
    }

  //  @Scheduled(cron = "0 0 5 * * ?")   //早上五点存黄历内容
    private void updateAlmanac() {
        String date=dateUtil.parseDateToStr(new Date(),dateUtil.DATE_FORMAT_YYYY_MM_DD);
        Map<String,Object> map=new HashMap<>();
        map.put("date",date);
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
         System.out.println(new Date()+"每日五点更新当日黄历");
    }

    */
    /*@Scheduled(cron = "0 0 20 * * ?")
    public void pushJgAlermData1() {
    	SupervisionDao.reset_supervision_day_count(new HashMap<>());
    	 System.out.println(new Date()+"晚上八点");
    }*/

    //早晨六点启动定时打卡
    /*@Scheduled(cron="0 0/20 6 * * ?")
    public void setClock(){
        Map<String,Object> params = new HashMap<>();
        params.put("token","eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ0VXNlciI6eyJjb21wYW55X2lkIjoyLCJkZXBhcnRtZW50X2lkIjoxLCJncm91cF9uYW1lIjoi5by655S154-t57uEIiwidXNlcl9uYW1lIjoi56em5bu65YabIiwiZGVwYXJ0bWVudF9uYW1lIjoi5bel56iL6YOoIiwicG9ydHJhaXQiOiJodHRwOi8vZ3Qub3ZlbGVjLmNvbS8xNTY0MDM0Njk5NzIzNzE3LmpwZWciLCJyb2xlX25hbWUiOiLnj63nu4Tplb8iLCJsb2dpbl9uYW1lIjoi56em5bu65YabIiwidXNlcl9pZCI6NzcsInBob25lIjoiIiwiZ3JvdXBfaWQiOjEsInJvbGVfaWQiOjQsImNvbXBhbnlfbmFtZSI6IuWIhuWFrOWPuCIsInVwZGF0ZV9hdCI6IjE5NzAtMDEtMDEgMDg6MDg6MzkiLCJjZWxscGhvbmUiOiIxNTAxMTMwODMyMSIsImpvYl9udW1iZXIiOiIwOTIiLCJjcmVhdGVfYXQiOiIyMDE5LTAzLTExIDExOjExOjQ0IiwiZW1haWwiOiIifSwiaXNzIjoidGVhbSBiZWlqaW5nIiwiZXhwIjoxNTY3MDc0MzgzLCJuYmYiOjE1NjU3NzgzODN9.fhl0ISrb7e3TaymyEqIwH4sdV0mszHjzlEqKCRuatic");
        params.put("number","0020");
        patrolService_app1.setClockOnce(params);
        System.out.println("定时器启动打卡");
    }*/

    /**
     * 添加预约
     * TODO 需要启动个定时器, 每天早上八点, 遍历一下预约时间是否到期, 如果到期, 需要更新状态, 并且发推送
     */
    @Scheduled(cron = "0 0 8 * * ?")
    public void expire(){
        List<Map<String,Object>> mIds = malfunctionMapper.selectExpiredIdList();
        if(mIds!=null&&mIds.size()>0){
            String title = "您预约解决的维修任务已到期，请尽快解决！";
            for(Map<String,Object> malfunction:mIds){
                //极光推送
                List<String> alias=new ArrayList<>();
                //alias.add(UrlConfig.ENV_TEST);
                Integer mid = Integer.parseInt(String.valueOf(malfunction.get("id")));
                System.out.println("！！！！故障"+mid+"预约维修任务已经到期！！！！");
                alias.add(String.valueOf(malfunction.get("fix_by")));
                Map<String, String> extras = new HashMap<>();
                extras.put("type","malfunction");
                extras.put("id",String.valueOf(mid));
                extras.put("detail","1");
                //modify hlc 函数中已添加 env tag
                Jipush.sendByAliasList(title,extras,alias);

                //更新故障表状态
                malfunctionMapper.updateExpireById(mid,8);
                //更新故障记录表状态
                malfunctionLogMapper.updateExpireById(mid,(Long)malfunction.get("hangup_at"));
            }
        }
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
}
