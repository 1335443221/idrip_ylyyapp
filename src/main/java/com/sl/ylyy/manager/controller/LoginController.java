package com.sl.ylyy.manager.controller;

import com.alibaba.fastjson.JSONObject;
import com.sl.ylyy.common.config.UrlConfig;
import com.sl.ylyy.common.utils.*;
import com.sl.ylyy.manager.entity.Operation_log;
import com.sl.ylyy.manager.entity.RoleInfo;
import com.sl.ylyy.manager.service.Operation_logDataService;
import com.sl.ylyy.manager.service.SystemDataService;
import com.sl.ylyy.manager.service.UploadConfigService;
import com.sl.ylyy.manager.service.UserDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/ylyy")
public class LoginController {
	@Autowired
	UserDataService userDataImpl;
	@Autowired
	SystemDataService systemDataImpl;
	@Autowired
	Operation_logDataService operation_logDataService;
	@Autowired
	private UploadConfigService uploadConfigService;
	

	/**
	 * 登陆页面
	 * @return
	 */
	@RequestMapping("/login")
	public String login(){
		return "ylyyPag/login";
	}
	
	
	
	
	/**
	 * 后台管理主页面
	 * @param
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping("/index")
	public String index(String tg, Model model) throws UnsupportedEncodingException{
		//登录成功同步上传配置到内存中
		Map<String, Object> activeUploadConfig = uploadConfigService.getActiveUploadConfig();
		model.addAttribute("active",activeUploadConfig);
		UploadConfigUtil.setActiveId(Integer.parseInt(String.valueOf(activeUploadConfig.get("active_id"))));
		return "ylyyPag/index";
	}
	

}
