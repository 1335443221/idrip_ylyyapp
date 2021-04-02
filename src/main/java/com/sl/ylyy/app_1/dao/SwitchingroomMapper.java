package com.sl.ylyy.app_1.dao;

import com.sl.ylyy.app_1.entity.Switchingroom;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SwitchingroomMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Switchingroom record);

    int insertSelective(Switchingroom record);

    Switchingroom selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Switchingroom record);

    int updateByPrimaryKey(Switchingroom record);

    List<Switchingroom> selectByTid(Integer tid);
}