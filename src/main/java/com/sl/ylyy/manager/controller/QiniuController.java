package com.sl.ylyy.manager.controller;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.sl.ylyy.common.config.UrlConfig;
import com.sl.ylyy.common.utils.Result1;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sl.ylyy.manager.service.UserDataService;
import com.sl.ylyy.common.utils.QiniuUpload;
import com.sl.ylyy.common.utils.Result;
import org.springframework.web.multipart.MultipartFile;


@Controller
@RequestMapping("/app/qiniu")
public class QiniuController {
	
	@Autowired
	UserDataService userDataImpl;
	private String sepa = java.io.File.separator;
	
	
	/**
	 * 获取七牛存储空间
	 * @param stg
	 * @return
	 */
	@RequestMapping("/getBucket")
	@ResponseBody
	public Object  getQiNiuBucket(@RequestParam Map<String, Object> map){
		return userDataImpl.getQiNiuBucket(map);
	}
	
	
	
	/**
	 * 获取覆盖凭证
	 * @param stg
	 * @return
	 */
	@RequestMapping("/getCoverUpToken")
	@ResponseBody
	public Object  getCoverUpToken(@RequestParam Map<String, String> map){
		return QiniuUpload.getCoverUpToken(map);
	}
	
	
	
	/**
	 * 获取简单上传凭证
	 * @param stg
	 * @return
	 */
	@RequestMapping("/getUpToken")
	@ResponseBody
	public Object  getUpToken(@RequestParam Map<String, String> map){
		
		return QiniuUpload.getUpToken(map);
	}
	
	
	/**
	 * 获取简单上传凭证2
	 * @param stg
	 * @return
	 */
	@RequestMapping("/ovelec_gt_UpToken")
	@ResponseBody
	public Object  getUpToken2(@RequestParam Map<String, String> map){
		Map<String, String> map2=new HashMap<>();
        map2.put("upToken", QiniuUpload.getUpToken2());
        
		return Result.success(map2);
	}


	/**
	 * 七牛地址
	 * @param
	 * @return
	 */
	@RequestMapping("/url")
	@ResponseBody
	public Result  url(@RequestParam Map<String, String> map){

		return Result.success(UrlConfig.QiniuUrl);
	}
	/**
	 * 替代七牛的公用上传方法(单个上传)
	 * @param
	 * @return
	 */
	@RequestMapping("/singleUpload")
	@ResponseBody
	public Result1  singleUpload(@RequestParam Map<String, String> map, @RequestParam MultipartFile file){
		//上传绝对路径 服务器地址  UrlConfig.UPLOAD  +"qiniu/日期/文件名"
		//访问地址   返回数据  UrlConfig.DOWNURL  +"qiniu/日期/文件名"
		String originalFilename = file.getOriginalFilename();
		String fileTyle=originalFilename.substring(originalFilename.lastIndexOf("."),originalFilename.length());
		String fileName = map.get("fileName");
		if(StringUtils.isEmpty(fileName))
			fileName = Calendar.getInstance().getTimeInMillis()+file.getOriginalFilename();
		else
			fileName += fileTyle;
		System.out.println("fileName:"+fileName);
		String filePath = "qiniu"+sepa+new SimpleDateFormat("yyMMdd").format(new Date())+sepa;
		String path = UrlConfig.UPLOAD +filePath + fileName;
		System.out.println("path:"+path);
		File uploadFile = new File(path);
		// 检测是否存在目录
		if (!uploadFile.getParentFile().exists()) {
			try{
				uploadFile.getParentFile().mkdirs();// 新建文件夹
			}catch (Exception e){
				return new Result1("1010","上传目录不存在或者不可写");
			}
		}
		String downloadUrl = UrlConfig.DOWNURL + filePath + fileName;
		if(uploadFile.exists()) return new Result1(downloadUrl);
		try {
			file.transferTo(uploadFile);// 文件写入
		} catch (IOException e) {
			e.printStackTrace();
			return new Result1("1006","发生系统错误");
		}
		return new Result1(downloadUrl);
	}

	
}
	
