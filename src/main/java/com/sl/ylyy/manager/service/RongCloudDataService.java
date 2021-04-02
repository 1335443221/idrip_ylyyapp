package com.sl.ylyy.manager.service;

import com.sl.ylyy.common.utils.CodeMsg;
import com.sl.ylyy.common.utils.Result;

import java.util.Map;

public interface RongCloudDataService {

    public CodeMsg chat_build(Map<String,Object> map); //建立聊天关系
    public CodeMsg chat_delete(Map<String,Object> map); //删除聊天关系

    public Result chat_list(Map<String,Object> map);  //消息列表

    public Result user_detail(Map<String, Object> map);  //用户基本信息(发送聊天页面)

    public Object group_create(String json); //创建群

    public Object group_update(Map<String,Object> map); //修改群信息

    public CodeMsg group_notice_insert(Map<String,Object> map); //发布群公告
    public CodeMsg group_notice_update(Map<String,Object> map); //修改群公告
    public Result group_active_notice(Map<String,Object> map); //最新群公告
    public Result group_notice_list(Map<String,Object> map); //群公告列表

    public CodeMsg group_dismiss(Map<String,Object> map); //解散群
    public CodeMsg group_join(String json); //加入群
    public Result group_detail(String rong_group_id); //群信息
    public CodeMsg group_quit(String json); //加入群

    public Result group_list_ByUser(Map<String,Object> map); //展示用户所加的群组

    public Result user_list_ByGroup(Map<String,Object> map); //群内群员列表

    public Result user_list(Map<String,Object> map); //所有用户列表

    public Result work_type_list(Map<String,Object> map); //所有任务类型列表
    public Result work_remind_list(Map<String,Object> map); //所有提醒类型列表

    public CodeMsg work_add(Map<String,Object> map); //发布任务

    public Result work_list_ByDate(Map<String,Object> map); //工作列表当天
    public Result work_list_ByUser(Map<String,Object> map); //工作列表当天
    public Result Workbench_ByDate(Map<String,Object> map); //工作台 通过日期查询


    public Result structure_list(Map<String,Object> map);
    public Result structure_ByUser(Map<String,Object> map);


    public Result almanac_ByDate(Map<String,Object> map);  //黄历 天气 限行


    /**
     *发送通知给当前用户
     * @param
     * @return
     */
    public CodeMsg sendNoticeToUser(Map<String,Object> map);



    public CodeMsg setDingToRead(Map<String,Object> map);
    public Result getDingStatistic(Map<String,Object> map);
    public Result getDingByUser(Map<String,Object> map);
    public CodeMsg createDingMsg(String json);




}
