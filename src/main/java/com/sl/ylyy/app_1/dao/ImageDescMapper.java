package com.sl.ylyy.app_1.dao;

import com.sl.ylyy.app_1.entity.ImageDesc;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public interface ImageDescMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ImageDesc record);

    int insertSelective(ImageDesc record);

    Map<String,Object> selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ImageDesc record);

    int updateByPrimaryKeyWithBLOBs(ImageDesc record);

    int updateByPrimaryKey(ImageDesc record);

    int insertContent(@Param("content") String content,
                      @Param("createBy") Integer createBy);

    int selectIdByCreateByAndContent(@Param("content") String content,
                                     @Param("createBy") Integer createBy);
}