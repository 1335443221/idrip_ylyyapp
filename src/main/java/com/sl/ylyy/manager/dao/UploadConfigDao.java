package com.sl.ylyy.manager.dao;

import java.util.Map;

public interface UploadConfigDao {

    //获取当前设置的文件上传方式
    public Map<String,Object> getActiveUploadConfig();

    //修改文件上传方式
    public int updateUploadConfig(Integer active_id);
}
