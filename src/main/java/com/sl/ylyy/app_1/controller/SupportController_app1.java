package com.sl.ylyy.app_1.controller;

import com.sl.ylyy.app_1.service.SupportService_app1;
import com.sl.ylyy.common.utils.Result1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/gaoxin_app/api/v1/support")
public class SupportController_app1 {
    @Autowired
    SupportService_app1 supportService;

    /**
     * 发起支援
     * @param params
     * @return
     */
    @PostMapping("/add")
    @ResponseBody
    public Result1 addSupport(@RequestParam Map<String,Object> params){
        return supportService.addNewSupport(params);
    }
    /**
     * 发起支援
     * @param params
     * @return
     */
    @PostMapping("/add_two")
    @ResponseBody
    public Result1 addSupport2(@RequestParam Map<String,Object> params){
        return supportService.addNewSupport2(params);
    }

    /**
     * 接受/拒绝支援
     * @param params
     * @return
     */
    @PostMapping("/respond")
    @ResponseBody
    public Result1 supportRespond(@RequestParam Map<String,Object> params){
        return supportService.supportRespond(params);
    }

    /**
     * 终止支援
     * @param params
     * @return
     */
    @PostMapping("/terminate")
    @ResponseBody
    public Result1 supportTerminate(@RequestParam Map<String,Object> params){
        return supportService.terminateSupport(params);
    }

    /**
     * 发起人结案（传递sid，如果该条支援未结案，则修改支援状态，变为已结案；若已结案，则不做改变）
     * @param params
     * @return
     */
    @PostMapping("/status")
    @ResponseBody
    public Result1 supportStatus(@RequestParam Map<String,Object> params){
        return supportService.statusSupport(params);
    }

    /**
     * 支援详情
     * @param params
     * @return
     */
    @PostMapping("/detail")
    @ResponseBody
    public Result1 supportDetail(@RequestParam Map<String,Object> params){
        return supportService.getSupportDetail(params);
    }

    /**
     * 支援列表
     * @param params
     * @return
     */
    @PostMapping("/get_list")
    @ResponseBody
    public Result1 getSupportList(@RequestParam Map<String,Object> params){
        return supportService.getSupportList(params);
    }

}
