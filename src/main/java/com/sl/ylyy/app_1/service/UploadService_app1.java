package com.sl.ylyy.app_1.service;

import com.sl.ylyy.common.utils.Result1;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface UploadService_app1 {
    public Result1 saveImageAndAudio(Map<String, Object> params, MultipartFile file);
}
