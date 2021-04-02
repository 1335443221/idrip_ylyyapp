package com.sl.ylyy.app_1.dao;

import com.sl.ylyy.app_1.entity.PatrolType;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface PatrolTypeMapper {

    PatrolType selectByPrimaryKey(Integer id);

    List<PatrolType> selectAllPatrolTypes(@Param("departmentId") Integer departmentId,
                                          @Param("companyId")Integer companyId);

    List<Integer> selectPatrolTypeIds(Integer departmentId);

    List<Map<String,Object>> selectPatrolTimes();

    List<Integer> selectTimeTypeByDepartmentId(Integer departmentId);

    String selectTimeTypeAliasById(Integer timeTypeId);
}