package com.sl.ylyy.app_1.dao;

import com.sl.ylyy.app_1.entity.Tip;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public interface TipMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Tip record);

    int insertSelective(Tip record);

    Tip selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Tip record);

    int updateByPrimaryKey(Tip record);

    Map<String,Object> selectTipById(Integer id);
}