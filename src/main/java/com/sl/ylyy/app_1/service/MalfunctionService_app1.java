package com.sl.ylyy.app_1.service;


import com.sl.ylyy.common.utils.Result1;

import java.util.Map;

public interface MalfunctionService_app1 {
    Result1 getMalfunctionType(Map<String,Object> params);
    Result1 addMalfunction(Map<String, Object> params);
    Result1 addMalfunction2(Map<String, Object> params);
    Result1 getMalfunctionList(Map<String, Object> params);
    Result1 getMalfunctionById(Map<String, Object> params);
    Result1 getMalfunctionLog(Map<String, Object> params);
    Result1 addMaterial(Map<String, Object> params);
    Result1 addHangup(Map<String, Object> params);
    Result1 addFinish(Map<String, Object> params);
    Result1 addToMalfunction(Map<String, Object> params);
    Result1 updateAccept(Map<String, Object> params);
    Result1 updateAppoint(Map<String, Object> params);
    Result1 updateCheck(Map<String, Object> params);
}
