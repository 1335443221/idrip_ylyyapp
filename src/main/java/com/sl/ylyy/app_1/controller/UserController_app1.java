package com.sl.ylyy.app_1.controller;

import com.sl.ylyy.app_1.service.UserService_app1;
import com.sl.ylyy.common.utils.Result1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.sl.ylyy.common.utils.JwtToken.getRoleIdByToken;

@RestController
@RequestMapping("/gaoxin_app/api/v1/user")
public class UserController_app1 {
	@Autowired
	UserService_app1 userService;
	/**
	 * 登陆
	 * @return
	 */
	@PostMapping("/signin")
	@ResponseBody
	public Result1 login(@RequestParam Map<String,Object> params){
		return userService.login(params);
	}

	/**
	 * 扫码提示
	 * @param params
	 * @return
	 */
	@PostMapping("/tip")
	@ResponseBody
	public Result1 getTip(@RequestParam Map<String,Object> params){
		return userService.getTip(params);
	}

	/**
	 * 获取当前用户信息
	 * @param params
	 * @return
	 */
	@PostMapping("/detail")
	@ResponseBody
	public Result1 getUserDetilByLogin(@RequestParam Map<String,Object> params){
		return userService.getUserByLogin(params);
	}

	/**
	 * 获取级联公司、部门、组
	 * @param params
	 * @return
	 */
	@PostMapping("/userIdsv2")
	@ResponseBody
	public Result1 getUserIdsByIds(@RequestParam Map<String,Object> params){
		return new Result1(userService.getUsersByIds(params),getRoleIdByToken((String)params.get("token")));
	}


	/**
	 * 设备管理  树形结构  user_id  为 center表的id
	 * @param params
	 * @return
	 */
	@PostMapping("/elcmUserIdsv2")
	@ResponseBody
	public Result1 getUsersByIdsElcm(@RequestParam Map<String,Object> params){
		return new Result1(userService.getUsersByIdsElcm(params),getRoleIdByToken((String)params.get("token")));
	}





	/**
	 * 库管员列表
	 * @param params
	 * @return
	 */
	@PostMapping("/storekeeper_list")
	@ResponseBody
	public Result1 getStoreKeepers(@RequestParam Map<String,Object> params){
		return new Result1(userService.getStorekeepers(params),getRoleIdByToken((String)params.get("token")));
	}
}
