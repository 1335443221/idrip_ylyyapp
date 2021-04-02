package com.sl.ylyy.app_1.dao;

import com.sl.ylyy.app_1.entity.Support;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface SupportMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Support record);

    int insertSelective(Support record);

    Support selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Support record);

    int updateByPrimaryKey(Support record);

    int insertNewSupport(Support support);

    int updateTerminate(Support support);

    Map<String,Object> selectSupportById(Integer id);

    List<Map<String,Object>> selectSupportList(@Param("pageSize") int pageSize,
                                               @Param("support_at") Integer support_at,
                                               @Param("create_by") Integer create_by,
                                               @Param("response") Integer response,
                                               @Param("department_id")Integer department_id,
                                               @Param("user_id")Integer user_id);

    int countSupportList(@Param("create_by") Integer create_by,
                         @Param("response") Integer response,
                         @Param("department_id")Integer department_id,
                         @Param("user_id")Integer user_id);

    int updateByMid(int malfunctionId);
}