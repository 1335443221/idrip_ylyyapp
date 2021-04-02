package com.sl.ylyy.app_1.dao;

import com.sl.ylyy.app_1.entity.Patrol;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface PatrolMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Patrol record);

    int insertSelective(Patrol record);

    Patrol selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Patrol record);

    int updateByPrimaryKeyWithBLOBs(Patrol record);

    int updateByPrimaryKey(Patrol record);

    List<Map<String,Object>> selectPatrols(@Param("companyId") Integer companyId,
                                           @Param("startTime") long startTime,
                                           @Param("endTime") long endTime,
                                           @Param("startNum") int startNum,
                                           @Param("getNum") int getNum,
                                           @Param("timeType")int timeType,
                                           @Param("departmentId")Integer departmentId,
                                           @Param("weekStart")long weekStart,
                                           @Param("weekEnd")long weekEnd,
                                           @Param("monthStart")long monthStart,
                                           @Param("monthEnd")long monthEnd,
                                           @Param("userId")long user_id);

    Integer selectPatrolCount(@Param("companyId") Integer companyId,
                                               @Param("startTime") long startTime,
                                               @Param("endTime") long endTime,
                                               @Param("timeType")int timeType,
                                               @Param("departmentId")Integer departmentId,
                                               @Param("weekStart")long weekStart,
                                               @Param("weekEnd")long weekEnd,
                                               @Param("monthStart")long monthStart,
                                               @Param("monthEnd")long monthEnd,
                                               @Param("userId")long user_id);

    List<Map<String,Object>> selectPatrolLog(@Param("startTime") long startTime,
                                             @Param("endTime") long endTime,
                                             @Param("uid") Integer uid,
                                             @Param("params") Map<String,Object> params);

    List<Map<String,Object>> selectPatrolReport(@Param("startTime") long startTime,
                                                @Param("endTime") long endTime,
                                                @Param("params") Map<String,Object> params,
                                                @Param("type") Integer type);

    Patrol selectPatrolByNumber(@Param("number") String number,
                                @Param("patrolBy") Integer patrolBy,
                                @Param("timeType")Integer timeType,
                                @Param("departmentId")Integer departmentId);

    List<String> selectTimeTypes(@Param("departmentId") Integer departmentId,
                                      @Param("patrolPointId") Integer patrolPointId);




    List<Double> patrolTimeInterval();
}