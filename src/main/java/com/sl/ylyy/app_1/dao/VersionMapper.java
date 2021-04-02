package com.sl.ylyy.app_1.dao;

import com.sl.ylyy.app_1.entity.Version;
import com.sl.ylyy.app_1.entity.VersionWithBLOBs;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public interface VersionMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(VersionWithBLOBs record);

    int insertSelective(VersionWithBLOBs record);

    VersionWithBLOBs selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(VersionWithBLOBs record);

    int updateByPrimaryKeyWithBLOBs(VersionWithBLOBs record);

    int updateByPrimaryKey(Version record);

    Map<String,Object> selectLastestVersion();
}