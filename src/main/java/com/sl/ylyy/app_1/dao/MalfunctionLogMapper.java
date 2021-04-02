package com.sl.ylyy.app_1.dao;

import com.sl.ylyy.app_1.entity.MalfunctionLog;
import com.sl.ylyy.app_1.entity.MalfunctionLogWithBLOBs;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public interface MalfunctionLogMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(MalfunctionLogWithBLOBs record);

    int insertSelective(MalfunctionLogWithBLOBs record);

    int insertAppoint(MalfunctionLog malfunctionLog);

    MalfunctionLogWithBLOBs selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(MalfunctionLogWithBLOBs record);

    int updateByPrimaryKeyWithBLOBs(MalfunctionLogWithBLOBs record);

    int updateByPrimaryKey(MalfunctionLog record);

    Integer selectByMidAndFixBy(@Param("mid") Integer mid,
                                @Param("fixBy") Integer fixBy);

    int insertAcceptById(MalfunctionLog log);

    int updateByMidAndFixBy(@Param("mid") Integer mid,
                            @Param("fixBy") Integer fixBy,
                            @Param("status") Integer status);

    Map<String,Object> selectByMid(Integer mid);

    int updateExpireById(@Param("mid") Integer mid,
                         @Param("expireAt")Long expireAt);
}