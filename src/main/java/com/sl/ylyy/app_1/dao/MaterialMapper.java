package com.sl.ylyy.app_1.dao;

import com.sl.ylyy.app_1.entity.Material;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface MaterialMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Material record);

    int insertSelective(Material record);

    Material selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Material record);

    int updateByPrimaryKeyWithBLOBs(Material record);

    int updateByPrimaryKey(Material record);

    List<Integer> selectByMalfunctionId(Integer malfunctionId);

    List<Integer> selectKeeperByMidFromAppoint(Integer malfunctionId);

    List<Map<String,Object>> selectMaterialByMalfunctionId(Integer malfunctioniId);


}