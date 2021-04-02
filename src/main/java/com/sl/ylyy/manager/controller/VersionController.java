package com.sl.ylyy.manager.controller;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sl.ylyy.common.config.UrlConfig;
import com.sl.ylyy.manager.entity.VersionInfo;
import com.sl.ylyy.manager.service.Operation_logDataService;
import com.sl.ylyy.manager.service.VersionDataService;
import com.sl.ylyy.common.utils.PageUtil;
import com.sl.ylyy.common.utils.QiniuUpload;

@Controller
@RequestMapping("/gtgx")
public class VersionController {
	@Autowired
	VersionDataService VersionDataImpl;
	
	@Autowired
	Operation_logDataService operation_logDataService;
	

	
	/**
	 * 获取当前版本
	 * @param
	 * @return
	 */
	@RequestMapping("/getActiveVersion")
	public String  getActiveVersion(Model model){
		VersionInfo versionInfo = VersionDataImpl.getMaxIdVersion();
		Map<String, Object> map= VersionDataImpl.getMaxIdQrCode();
		model.addAttribute("version", versionInfo);
		model.addAttribute("crcode", map);
		return "ylyyPag/versionPag/activeVersion";
	}
	
	
	/**
	 * 上传版本
	 * @param
	 * @return
	 * @throws IOException 
	 */
	@RequestMapping("/insertVersion")
	@ResponseBody
	public Object  insertVersion(VersionInfo versionInfo,HttpSession session) throws IOException{
	  	    
	    	Map<String,Object> omap= JSONObject.parseObject(String.valueOf(session.getAttribute("activeAdmin")));
			omap.put("operation", "上传APP版本");
		    operation_logDataService.insertOperation_log(omap);
			
	  	    return VersionDataImpl.insertVersion(versionInfo);
		}
		
	
	
	/**
	 * 获取所有版本
	 * @param
	 * @return
	 */
	@RequestMapping("/getAllVersion")
	public String  getAllVersion(Model model,int pageindex,String key){
		PageUtil<VersionInfo> page=new PageUtil<VersionInfo>();
		if(pageindex==0){
			pageindex=1;
		}
		page.setPageindex(pageindex); 
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("key", key);
		page.setParams(params);
		page.setPagesize(3);
		PageUtil<VersionInfo> pageData=VersionDataImpl.findPage(page);  //新的分页信息
		
		pageData.setPageindex(pageindex);  //返回当前页
		params.put("lastpage", pageData.getLastPage());  //上一页
		params.put("nextpage", pageData.getNextpage(pageData.getTotalPagecount())); //下一页
		
		pageData.setParams(params);  //返回模糊查询信息
		model.addAttribute("page",pageData);  
		return "ylyyPag/versionPag/showVersion";
	}
	
	
	
	/**
	 * 去修改版本页面
	 * @param
	 * @return
	 */
	@RequestMapping("/goUpdateVersion")
	public String  goUpdateVersion(Integer id,Model model){
		
		VersionInfo versionInfo =VersionDataImpl.getVersionById(id);
		model.addAttribute("token",QiniuUpload.getUpToken2());
		model.addAttribute("prefix", UrlConfig.FILE_PREFIX);
		model.addAttribute("QiniuUrl", UrlConfig.QiniuUrl);
		model.addAttribute("version", versionInfo);
		return "ylyyPag/versionPag/updateVersion";
	}
	
	
	/**
	 * 修改版本
	 * @param
	 * @return
	 * @throws IOException 
	 */
	@RequestMapping("/updateVersion")
	@ResponseBody
	public Object  updateVersion(VersionInfo versionInfo){
		
		return VersionDataImpl.updateVersionById(versionInfo);
	}
	
	
	
	/**
	 * 去添加版本页面
	 * @param
	 * @return
	 */
	@RequestMapping("/goAddVersion")
	public String  goAddVersion(Model model){
		model.addAttribute("token",QiniuUpload.getUpToken2());
		model.addAttribute("prefix", UrlConfig.FILE_PREFIX);
		model.addAttribute("QiniuUrl", UrlConfig.QiniuUrl);
		VersionInfo versionInfo = VersionDataImpl.getMaxIdVersion();
		model.addAttribute("version", versionInfo);
		return "ylyyPag/versionPag/addVersion";
	}
	
	
	/**
	 * 判断版本号是否重复
	 * @param
	 * @return
	 */
	@RequestMapping("/checkVersionid")
	@ResponseBody
	public String checkVersionId(@RequestParam Map<String, Object> map){
		if(VersionDataImpl.checkVersion(map).size()>0){
			return "1";
		}else{
			return "0";
		}
	}
	
	/**
	 * 删除版本
	 * @param id
	 * @return
	 */
	@RequestMapping("/deleteVersionById")
	@ResponseBody
	public String deleteVersionById(Integer id){
		
		VersionDataImpl.deleteVersionById(id);  //删除版本
		
		return "1";
	}
	
	
	/**
	 * 获取当前二维码url
	 * @param
	 * @return
	 */
	@RequestMapping("/getActiveQrCode")
	public String  getActiveQrcode(Model model){
		Map<String, Object> map= VersionDataImpl.getMaxIdQrCode();
		model.addAttribute("crcode", map);
		return "ylyyPag/versionPag/activeQrCode";
	}
	
	

	/**
	 * 上传url
	 * @param
	 * @return
	 * @throws IOException 
	 */
	@RequestMapping("/insertQrCode")
	@ResponseBody
	public Object  insertQrCode(@RequestParam Map<String, Object> map){
			return VersionDataImpl.insertQrCode(map);
		}
	
	
	/**
	 * 获取当前二维码url
	 * @param
	 * @return
	 */
	@RequestMapping("/getQrCodeUrl")
	@ResponseBody
	public Object  getQrCodeUrl(@RequestParam Map<String, Object> map){
		
		return VersionDataImpl.getQrCodeUrl(map);
	}
	
}
	
