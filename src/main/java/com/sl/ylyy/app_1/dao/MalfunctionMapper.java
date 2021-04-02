package com.sl.ylyy.app_1.dao;

import com.sl.ylyy.app_1.entity.Malfunction;
import com.sl.ylyy.app_1.entity.MalfunctionWithBLOBs;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface MalfunctionMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(MalfunctionWithBLOBs record);

    int insertSelective(MalfunctionWithBLOBs record);

    MalfunctionWithBLOBs selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(MalfunctionWithBLOBs record);

    int updateByPrimaryKeyWithBLOBs(MalfunctionWithBLOBs record);

    int updateByPrimaryKey(Malfunction record);

    List<Map<String,Object>> selectMalfunctionList(@Param("statusList") List<Integer> statusList,
                                                   @Param("userId") int userId,
                                                   @Param("pageSize") int pageSize,
                                                   @Param("creatAt") Integer createAt,
                                                   @Param("fixBys") List<Integer> fixBys,
                                                   @Param("departmentId")Integer departmentId,
                                                   @Param("isCharge")Integer isCharge,
                                                   @Param("companyId")Integer companyId,
                                                   @Param("departmentList")List<String> departmentList);

    Integer selectMalfunctionListCount(@Param("statusList") List<Integer> statusList,
                                       @Param("userId") int userId,
                                       @Param("fixBys") List<Integer> fixBys,
                                       @Param("departmentId")Integer departmentId,
                                       @Param("isCharge")Integer isCharge,
                                       @Param("companyId")Integer companyId,
                                       @Param("departmentList")List<String> departmentList);

    Map<String,Object> selectBymid(Integer mid);

    List<Map<String,Object>> selectMalfunctionLog(@Param("startTime") Long startTime,
                                                  @Param("endTime") Long endTime,
                                                  @Param("userIds") List<Integer> userIds,
                                                  @Param("userIdsByGroup") List<Integer> userIdsByGroup,
                                                  @Param("isCharge")Integer isCharge,
                                                  @Param("isFinished")Integer isFinished);

    List<Map<String,Object>> selectFinishedAccept(@Param("startTime") Long startTime,
                             @Param("endTime") Long endTime,
                             @Param("userIds") List<Integer> userIds,
                             @Param("userIdsByGroup") List<Integer> userIdsByGroup,
                             @Param("isFinished")int isFinished);


    int selectMalfunctionLogCount(@Param("ids") List<Integer> ids,
                                  @Param("isFinished") Integer isFinished,
                                  @Param("user_id") Integer user_id,
                                  @Param("startTime") Long startTime,
                                  @Param("endTime") Long endTime);

    int updateAcceptById(@Param("id") Integer id,
                         @Param("status") Integer status,
                         @Param("fixBy")Integer fixBy);

    int updateAppointById(@Param("id") Integer id,
                          @Param("status") Integer status,
                          @Param("fixBy")Integer fixBy);

    int updateCheckById(@Param("id") Integer id,
                        @Param("status") Integer status);

    int updateHangUp(@Param("id")Integer id,
                     @Param("status")Integer status,
                     @Param("expireAt")String expireAt,
                     @Param("remark")String remark);

    int updateExpireById(@Param("id")Integer id,
                         @Param("status")Integer status);

    int selectCountByPatrolAndSource(Integer createBy);

    Map<String,Integer> selectStatusByLocation(@Param("location")String location,
                               @Param("createBy")Integer createBy);

    List<Map<String,Object>> selectExpiredIdList(
            //@Param("fixBys")List<Integer> fixBys
    );
    void updateIsPush(@Param("id")Integer id,
                      @Param("isPush")Integer isPush);

    List<Integer> selectGroupByMType(int type);

    String selectCheakMalfunctionByDepartmentId(@Param("departmentId")Integer departmentId);
}