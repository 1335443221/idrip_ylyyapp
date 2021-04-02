package com.sl.ylyy.app_1.dao;

import com.sl.ylyy.app_1.entity.PatrolPoint;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatrolPointMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(PatrolPoint record);

    int insertSelective(PatrolPoint record);

    PatrolPoint selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(PatrolPoint record);

    int updateByPrimaryKey(PatrolPoint record);
    List<PatrolPoint> selectAllPatrolPoints(@Param("companyId") Integer companyId,
                                            @Param("createAt") Integer createAt);

    String selectNumberByPatrolId(int patrolId);

    List<Integer> selectAllIdsByCompany(@Param("companyId") Integer companyId,
                                        @Param("createAt") Integer createAt);
}