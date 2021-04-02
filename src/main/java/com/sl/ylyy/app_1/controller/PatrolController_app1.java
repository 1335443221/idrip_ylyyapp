package com.sl.ylyy.app_1.controller;

import com.sl.ylyy.app_1.service.PatrolService_app1;
import com.sl.ylyy.common.utils.Result1;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/gaoxin_app/api/v1/patrol")
public class PatrolController_app1 {
    @Resource
    private PatrolService_app1 patrolTypeService;

    /**
     * 巡检类型(当前用户部门)
     * @param params
     * @return
     */
    @PostMapping("/get_type")
    @ResponseBody
    public Result1 getAllPatrolTypes(@RequestParam Map<String,Object> params){
        return patrolTypeService.getAllPatrolTypes(params);
    }

    /**
     * 巡检点列表
     * @param params
     * @return
     */
    @PostMapping("/point_list")
    @ResponseBody
    public Result1 getAllPatrolPoints(@RequestParam Map<String,Object> params){
        return patrolTypeService.getAllPatrolPoints(params);
    }

    /**
     * 巡检列表
     * @param params
     * @return
     */
    @PostMapping("/get_list")
    @ResponseBody
    public Result1 getPatrols(@RequestParam Map<String,Object> params){
        return patrolTypeService.getPatrols(params);
    }

    /**
     * 新增巡检计划
     * @param params
     * @return
     */
    @PostMapping("/add")
    @ResponseBody
    public Result1 addPatrol(@RequestParam Map<String,Object> params){
        return patrolTypeService.addPatrol(params);
    }

    /**
     * 当日巡检日志列表及数量接口
     * @param params
     * @return
     */
    @PostMapping("/log")
    @ResponseBody
    public Result1 getPatrolLogs(@RequestParam Map<String,Object> params){
        return patrolTypeService.getPatrolLog(params);
    }

    /**
     * 巡检报表
     * @param params
     * @return
     */
    @PostMapping("/filtrate")
    @ResponseBody
    public Result1 getPatrolReport(@RequestParam Map<String,Object> params){
        return patrolTypeService.getPatroReport(params);
    }

    /**
     * 记录打卡（现版本已废弃，统一使用批量打卡）
     * @param params
     * @return
     */
    @PostMapping("/clock")
    @ResponseBody
    public Result1 setClock(@RequestParam Map<String,Object> params){
        return patrolTypeService.setClock(params);
    }

    /**
     * 快速打卡（现版本已废弃，统一使用批量打卡）
     * @param params
     * @return
     */
    @PostMapping("/quick_clock")
    @ResponseBody
    public Result1 setQuickClock(@RequestParam Map<String,Object> params){
        return patrolTypeService.setClockOnce(params);
    }

    /**
     * 批量打卡
     * @param params json格式的 打卡人id，打卡编号，打卡时间戳列表
     * @return
     */
    @PostMapping("/lot_clock")
    @ResponseBody
    public Result1 setLotClock(@RequestParam Map<String,Object> params){
        return patrolTypeService.setLotClock_1(params);
    }

    /**
     * 当前部门巡检时间列表
     * @param token
     * @return
     */
    @PostMapping("/patrol_time_1.1")
    public Result1 getPatrolTimes1_1(@RequestParam String token){
        return patrolTypeService.getAllPatrolTimes(token);
    }


    /**
     * 打卡时间间隔
     * @param token
     * @return
     */
    @PostMapping("/patrolTimeInterval")
    public Result1 patrolTimeInterval(@RequestParam String token){
        return patrolTypeService.patrolTimeInterval(token);
    }

}
