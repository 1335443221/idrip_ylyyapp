package com.sl.ylyy.app_1.dao;

import com.sl.ylyy.app_1.entity.AudioDesc;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public interface AudioDescMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(AudioDesc record);

    int insertSelective(AudioDesc record);

    Map<String,Object> selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(AudioDesc record);

    int updateByPrimaryKeyWithBLOBs(AudioDesc record);

    int updateByPrimaryKey(AudioDesc record);

    int insertContent(@Param("content") String content,
                      @Param("createBy") Integer createBy);

    int selectIdByCreateByAndContent(@Param("content") String content,
                                     @Param("createBy") Integer createBy);
}