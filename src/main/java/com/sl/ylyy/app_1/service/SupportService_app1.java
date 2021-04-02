package com.sl.ylyy.app_1.service;
import com.sl.ylyy.common.utils.Result1;

import java.util.Map;

public interface SupportService_app1 {
    Result1 addNewSupport(Map<String, Object> params);
    Result1 addNewSupport2(Map<String, Object> params);
    Result1 supportRespond(Map<String, Object> params);
    Result1 terminateSupport(Map<String, Object> params);
    Result1 statusSupport(Map<String, Object> params);
    Result1 getSupportDetail(Map<String, Object> params);
    Result1 getSupportList(Map<String, Object> params);
}
