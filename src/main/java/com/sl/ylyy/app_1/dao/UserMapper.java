package com.sl.ylyy.app_1.dao;

import com.sl.ylyy.app_1.entity.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    String selectUserNameById(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);
    List<Map<String,Object>> selectUsersByGroupId(@Param("groupId") Integer groupId);
    List<Map<String,Object>> selectUsersByRid(@Param("roleId") Integer roleId);





    List<Map<String,Object>> selectDeparmentManagersByRid(@Param("roleId") Integer roleId,
                                                          @Param("departmentId")Integer departmentId);



    Map<String,Object> selectUserById(Integer id);
    List<Map<String,Object>> selectStorekeepers(Integer companyId);
    List<Integer> selectUidsByCompanyId(Integer companyId);
    List<Integer> selectUidsByDepartmentId(Integer departmentId);
    List<Integer> selectUidsByGroupId(Integer groupId);
    List<Map<String,String>> selectUserNameByIds(List<Integer> ids);

    User selectUserByCellPhone(String account);

    Map<String,Object> selectUserMap(Integer id);



    //=======================设备管理=============================//
    List<Map<String,Object>> elcmSelectUsersByRid(@Param("roleId") Integer roleId);
    List<Map<String,Object>> elcmSelectDeparmentManagersByRid(@Param("roleId") Integer roleId,
                                                              @Param("departmentId")Integer departmentId);
    List<Map<String,Object>> elcmSelectUsersByGroupId(@Param("groupId") Integer groupId);
    Map<String,Object> elcmSelectUserById(Integer id);
    //设备管理结束
}