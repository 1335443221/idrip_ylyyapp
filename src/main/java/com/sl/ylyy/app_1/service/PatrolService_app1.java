package com.sl.ylyy.app_1.service;

import com.sl.ylyy.common.utils.Result1;

import java.util.Map;

public interface PatrolService_app1 {
    Result1 getAllPatrolTypes(Map<String, Object> params);
    Result1 getAllPatrolPoints(Map<String, Object> params);
    Result1 getPatrols(Map<String, Object> params);
    Result1 addPatrol(Map<String, Object> params);
    Result1 getPatrolLog(Map<String, Object> params);
    Result1 getPatroReport(Map<String, Object> params);
    Result1 setClock(Map<String, Object> params);
    Result1 setClockOnce(Map<String, Object> params);
//    Result1 setLotClock(Map<String, Object> params);
    Result1 setLotClock_1(Map<String,Object> params);
    Result1 getAllPatrolTimes(String token);

    Result1 patrolTimeInterval(String token);
}
