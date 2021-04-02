package com.sl.ylyy.app_1.dao;

import com.sl.ylyy.app_1.entity.MalfunctionType;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface MalfunctionTypeMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(MalfunctionType record);

    int insertSelective(MalfunctionType record);

    MalfunctionType selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(MalfunctionType record);

    int updateByPrimaryKey(MalfunctionType record);

    List<Map<String,Object>> selectByDepartmentId(Integer departmentId);

    List<Map<String,Object>> selectAllType(Integer companyId);
}