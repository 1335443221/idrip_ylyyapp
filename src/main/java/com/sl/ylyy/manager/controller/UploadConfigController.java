package com.sl.ylyy.manager.controller;

import com.sl.ylyy.common.config.UrlConfig;
import com.sl.ylyy.common.utils.Result1;
import com.sl.ylyy.common.utils.UploadConfigUtil;
import com.sl.ylyy.manager.service.UploadConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * 文件上传位置配置控制器，可选七牛服务器或本地
 */
@Controller
@RequestMapping("gtgx")
public class UploadConfigController {

    @Autowired
    private UploadConfigService uploadConfigService;

    //跳转到文件上传配置页面
    @RequestMapping("uploadConfPage")
    public String goUploadConfigPage(Model model){
        Map<String, Object> activeUploadConfig = uploadConfigService.getActiveUploadConfig();
        UploadConfigUtil.setActiveId(Integer.parseInt(String.valueOf(activeUploadConfig.get("active_id"))));
        model.addAttribute("active",activeUploadConfig);
        return "ylyyPag/systemPag/upload_config";
    }

    //修改文件上传配置
    @RequestMapping("updateUploadConfig")
    @ResponseBody
    public int updateUploadConfig(@RequestParam("active_id") Integer active_id){
        return uploadConfigService.updateUploadConfig(active_id);
    }

    //获取当前文件上传配置
    @RequestMapping("getUploadConfig")
    @ResponseBody
    public Result1 getUploadConfig(){
        Map<String, Object> activeUploadConfig = uploadConfigService.getActiveUploadConfig();
        activeUploadConfig.put("qiniuUrl", UrlConfig.QiniuUrl);
        return new Result1(activeUploadConfig);
    }

}
