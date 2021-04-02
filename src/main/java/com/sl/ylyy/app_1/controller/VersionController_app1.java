package com.sl.ylyy.app_1.controller;

import com.sl.ylyy.app_1.service.VersionService_app1;
import com.sl.ylyy.common.utils.Result1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/gaoxin_app/api/v1/app")
public class VersionController_app1 {
	
	@Autowired
	VersionService_app1 versionService;

	/**
	 * 获取最新版本信息
	 * @param version
	 * @return
	 */
	@PostMapping("/update")
	@ResponseBody
	public Result1 getVersionUpdate(@RequestParam String version){
		return versionService.versionUpdate(version);
	}

}
	
