package com.sl.ylyy.app_1.controller;

import com.sl.ylyy.app_1.dao.MalfunctionLogMapper;
import com.sl.ylyy.app_1.dao.MalfunctionMapper;
import com.sl.ylyy.app_1.service.MalfunctionService_app1;
import com.sl.ylyy.common.utils.Result1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/gaoxin_app/api/v1/malfunction")
public class MalfunctionController_app1 {
    @Autowired
    MalfunctionService_app1 malfunctionService;
    @Autowired
    MalfunctionMapper malfunctionMapper;
    @Autowired
    MalfunctionLogMapper malfunctionLogMapper;

    /**
     * 故障类型
     * @param params
     * @return
     */
    @PostMapping("/get_type")
    @ResponseBody
    public Result1 getMalfunctionType(@RequestParam Map<String,Object> params){
        return malfunctionService.getMalfunctionType(params);
    }

    /**
     * 故障上报
     * @param params
     * @return
     */
    @PostMapping("/add")
    @ResponseBody
    public Result1 addMalfunction(@RequestParam Map<String,Object> params){
        return malfunctionService.addMalfunction(params);
    }

    /**
     * 故障上报
     * @param params
     * @return
     */
    @PostMapping("/add_two")
    @ResponseBody
    public Result1 addMalfunction2(@RequestParam Map<String,Object> params){
        return malfunctionService.addMalfunction2(params);
    }


    /**
     * 故障列表
     * @param params
     * @return
     */
    @PostMapping("/get_list")
    @ResponseBody
    public Result1 getMalfunctionList(@RequestParam Map<String,Object> params){
        return malfunctionService.getMalfunctionList(params);
    }

    /**
     * 故障详情
     * @param params
     * @return
     */
    @PostMapping("/detail")
    @ResponseBody
    public Result1 getMalfunctionById(@RequestParam Map<String,Object> params){
        return malfunctionService.getMalfunctionById(params);
    }

    @PostMapping("/log")
    @ResponseBody
    public Result1 getMalfunctionLog(@RequestParam Map<String,Object> params){
        return malfunctionService.getMalfunctionLog(params);
    }

    /**
     * 新增物料
     * @param params
     * @return
     */
    @PostMapping("/material")
    @ResponseBody
    public Result1 addMaterial(@RequestParam Map<String,Object> params){
        return malfunctionService.addMaterial(params);
    }

    /**
     * 预约维修
     * @param params
     * @return
     */
    @PostMapping("/hangup")
    @ResponseBody
    public Result1 addHangup(@RequestParam Map<String,Object> params){
        return malfunctionService.addHangup(params);
    }

    /**
     * 维修失败
     * @param params
     * @return
     */
    @PostMapping("/finish")
    @ResponseBody
    public Result1 addFinish(@RequestParam Map<String,Object> params){
        return malfunctionService.addFinish(params);
    }

    /**
     * 补充提交
     * @param params
     * @return
     */
    @PostMapping("/add_to")
    @ResponseBody
    public Result1 addToMalfunction(@RequestParam Map<String,Object> params){
        return malfunctionService.addToMalfunction(params);
    }

    /**
     * 接受任务
     * @param params
     * @return
     */
    @PostMapping("/accept")
    @ResponseBody
    public Result1 updateAccept(@RequestParam Map<String,Object> params){
        return malfunctionService.updateAccept(params);
    }

    /**
     * 故障指派
     * @param params
     * @return
     */
    @PostMapping("/appoint")
    @ResponseBody
    public Result1 updateAppoint(@RequestParam Map<String,Object> params){
        return malfunctionService.updateAppoint(params);
    }

    /**
     * 管理员审核
     * @param params
     * @return
     */
    @PostMapping("/check")
    @ResponseBody
    public Result1 updateCheck(@RequestParam Map<String,Object> params){
        return malfunctionService.updateCheck(params);
    }

}
