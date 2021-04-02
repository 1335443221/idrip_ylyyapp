package com.sl.ylyy.app_1.service.impl;

import com.sl.ylyy.app_1.dao.VersionMapper;
import com.sl.ylyy.app_1.service.VersionService_app1;
import com.sl.ylyy.common.utils.Result1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service("versionService")
public class VersionService_app1Impl implements VersionService_app1 {
    @Autowired
    VersionMapper versionMapper;
    @Override
    public Result1 versionUpdate(String versionId) {
        Map<String,Object> lastestVersion = versionMapper.selectLastestVersion();
        if(Integer.parseInt(versionId)>=Integer.parseInt(lastestVersion.get("version").toString())){
            lastestVersion.put("is_latest",2);
            return new Result1("1005",lastestVersion,"未发现新版本");
        }
        lastestVersion.put("is_latest",1);
        return new Result1(lastestVersion);
    }
}
