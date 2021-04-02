package com.sl.ylyy.app_1.controller;

import com.sl.ylyy.app_1.service.UploadService_app1;
import com.sl.ylyy.common.utils.Result1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/gaoxin_app/api/v1/upload")
public class UploadController_app1 {
    @Autowired
    UploadService_app1 uploadService;

    @PostMapping("/index")
    @ResponseBody
    public Result1 uploadImageAndAudio(@RequestParam Map<String,Object> params, MultipartFile file){
        return uploadService.saveImageAndAudio(params,file);
    }
}
