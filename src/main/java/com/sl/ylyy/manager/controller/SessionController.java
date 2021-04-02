package com.sl.ylyy.manager.controller;



import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.alibaba.fastjson.JSONObject;
import com.sl.ylyy.common.utils.UploadConfigUtil;
import com.sl.ylyy.manager.service.UploadConfigService;
import com.sl.ylyy.manager.service.impl.CommonDataServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.sl.ylyy.manager.service.UserDataService;

@Controller
@RequestMapping("/session")
public class SessionController {
	@Autowired
	UserDataService userDataImpl;
	@Autowired
	private UploadConfigService uploadConfigService;
	

	/**
	 * 登陆验证 
	 * @return 成功 返回success  失败 返回 defeat
	 * @throws Exception 
	 */
	@RequestMapping("/checkUser")
	@ResponseBody
	public String checkUser(@RequestParam Map<String, Object> map, HttpSession session, HttpServletRequest request, HttpServletResponse response){
		List<Map<String,Object>> userInfoList = userDataImpl.checkUser(map);
		if(userInfoList.size()==0){
			return "defeat";
		}else{
			Map<String,Object> userMap=userInfoList.get(0);
			session.setAttribute("activeAdmin",userMap);  //把当前登陆用户放入session
			Cookie userCookie=new Cookie("gtgx_session",session.getId());
			userCookie.setMaxAge(24*60*60*30);   //存活期为一个月 24*60*60
			userCookie.setPath("/");
			response.addCookie(userCookie);
			//session存到数据库  主备服务数据同步
			Map<String,Object> sessionMap=new HashMap<>();
			String json=JSONObject.toJSONString(userMap);
			sessionMap.put("gtgx_session",session.getId());
			sessionMap.put("gtgx_value",json);
			CommonDataServiceImpl.insertSession(sessionMap);
			//登录成功同步上传配置到内存中
			Map<String, Object> activeUploadConfig = uploadConfigService.getActiveUploadConfig();
			UploadConfigUtil.setActiveId(Integer.parseInt(String.valueOf(activeUploadConfig.get("active_id"))));
			return "success";
		}
	}
	
	/**
	 * 退出  销毁session
	 * @param session
	 * @return
	 */
	@RequestMapping("/quit")
	public String quit(HttpSession session){
		session.invalidate();
			return "ylyyPag/login";
	}
	

}
