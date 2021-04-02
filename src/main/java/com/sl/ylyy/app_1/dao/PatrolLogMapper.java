package com.sl.ylyy.app_1.dao;

import com.sl.ylyy.app_1.entity.PatrolLog;
import com.sl.ylyy.app_1.info.PatrolLogInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public interface PatrolLogMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(PatrolLog record);

    int insertSelective(PatrolLog record);

    PatrolLog selectByPrimaryKey(Integer id);

    //更新巡检记录，并将第二次打卡时间更新完当前系统时间
    //int updateByPrimaryKeySelective(PatrolLog record);
    int updateByPrimaryKeySelective(PatrolLogInfo patrolLogInfo);

    int updateByPrimaryKey(PatrolLog record);

    Map<String,Object> selectByPatrolId(@Param("patrolBy") int patrolBy,
                                        @Param("patrolId") int patrolId,
                               @Param("startTime")Long startTime,
                               @Param("endTime")Long endTime,
                               @Param("weekStart")Long weekStart,
                               @Param("weekEnd")Long weekEnd,
                               @Param("monthStart")Long monthStart,
                               @Param("monthEnd")Long monthEnd);

    //更新巡检记录
    //int updatePatrolLog(PatrolLog patrolLog);
    int updatePatrolLog(Map<String,Object> patrolLog);

    int updatePatrolLogById(PatrolLog patrolLog);

    int getClockCountByPatrolLogId(@Param("patrolLogId")int patrolLogId);
    int insertPatrolLogClock(Map<String,Object> record);

}