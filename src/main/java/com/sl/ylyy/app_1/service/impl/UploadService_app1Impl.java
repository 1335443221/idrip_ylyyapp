package com.sl.ylyy.app_1.service.impl;

import com.sl.ylyy.app_1.dao.AudioDescMapper;
import com.sl.ylyy.app_1.dao.ImageDescMapper;
import com.sl.ylyy.app_1.dao.RoleMapper;
import com.sl.ylyy.app_1.service.UploadService_app1;
import com.sl.ylyy.common.utils.Result1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.sl.ylyy.common.config.UrlConfig.*;
import static com.sl.ylyy.common.utils.JwtToken.getUserIdByToken;


@Service("uploadService")
public class UploadService_app1Impl implements UploadService_app1 {
    @Autowired
    AudioDescMapper audioDescMapper;
    @Autowired
    ImageDescMapper imageDescMapper;
    @Autowired
    RoleMapper roleMapper;
    //Integer userId = 1;
    @Override
    @Transactional
    public Result1 saveImageAndAudio(Map<String, Object> params, MultipartFile file) {
        Integer userId = getUserIdByToken((String)params.get("token"));
        Integer rid = roleMapper.selectRidByUid(userId);
        //上传限制8M
        //1M=1048576字节
        if(file.getSize()>(8*1048576)){
            return new Result1("1008","大小超出限制",rid);
        }
        String fileName = Calendar.getInstance().getTimeInMillis()+file.getOriginalFilename();
        System.out.println("fileName:"+fileName);
        String filePath = "";
        String type = "";
        try {
            if(params.get("type")==null||("image".equals(params.get("type")))){
                type = "image";
                BufferedImage bi = null;
                bi = ImageIO.read(file.getInputStream());
                if(bi == null){
                    //上传的文件不是图片
                    return new Result1("1007", "非法类型",rid);
                }
                filePath = "image/"+new SimpleDateFormat("yyMMdd").format(new Date())+"/";

            }else if("audio".equals(params.get("type"))){
                type = "audio";
                filePath = "audio/"+new SimpleDateFormat("yyMMdd").format(new Date())+"/";
            }else{
                return new Result1("1005","文件类型有误",rid);
            }
            String path = UPLOAD +filePath + fileName;
            System.out.println("path:"+path);
            File uploadFile = new File(path);
            // 检测是否存在目录
            if (!uploadFile.getParentFile().exists()) {
                try{
                    uploadFile.getParentFile().mkdirs();// 新建文件夹
                }catch (Exception e){
                    return new Result1("1009","上传目录不存在或者不可写",rid);
                }
            }
            file.transferTo(uploadFile);// 文件写入
            //写入的文件存入数据库，判断存入图片表还是音频表中
            try {
                Integer id = 0;
                if(type.equals("image")){
                    imageDescMapper.insertContent(filePath+fileName,userId);
                    id = imageDescMapper.selectIdByCreateByAndContent(filePath+fileName,userId);
                }else{
                    audioDescMapper.insertContent(filePath+fileName,userId);
                    id = audioDescMapper.selectIdByCreateByAndContent(filePath+fileName,userId);
                }
                Map<String,Object> result = new HashMap<>();
                result.put("id",id);
                result.put("path",filePath+fileName);
                return new Result1(result,rid);
            }catch(Exception e){
                return new Result1("1010","文件保存失败",rid);
            }

        } catch (IOException e) {
            e.printStackTrace();
            return new Result1("1006","发生系统错误",rid);
        }
    }
}
