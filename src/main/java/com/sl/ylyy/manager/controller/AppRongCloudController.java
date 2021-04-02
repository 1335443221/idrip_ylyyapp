package com.sl.ylyy.manager.controller;

import com.sl.ylyy.common.utils.CodeMsg;
import com.sl.ylyy.common.utils.Result;
import com.sl.ylyy.common.utils.RongUtil;
import com.sl.ylyy.manager.service.RongCloudDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@Controller
@RequestMapping("app/rongCloud")
public class AppRongCloudController {
	
	@Autowired
	RongUtil rongUtil;
	@Autowired
	RongCloudDataService rongCloudService;

	
	
	/**
	 * app建立聊天关系
	 */
	@RequestMapping("/chat_build")
	@ResponseBody
	public Object chat_build(@RequestParam Map<String, Object> map) {
		return rongCloudService.chat_build(map);
	}

	/**
	 * app从聊天列表删除
	 */
	@RequestMapping("/chat_delete")
	@ResponseBody
	public Object chat_delete(@RequestParam Map<String, Object> map) {
		return rongCloudService.chat_delete(map);
	}


	/**
	 * app消息列表
	 */
	@RequestMapping("/chat_list")
	@ResponseBody
	public Object chat_list(@RequestParam Map<String, Object> map) {
		return rongCloudService.chat_list(map);
	}

///////////////////////////////////////////////用戶信息
	/**
	 * 用户详细信息
	 */
	@RequestMapping("/user_detail")
	@ResponseBody
	public Object user_detail(@RequestParam Map<String, Object> map) {
		return rongCloudService.user_detail(map);
	}
//=================================group群组=========================//
	/**
	 * app创建群组
	 */
	@RequestMapping(value="/group_create",method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Object group_create(@RequestBody String json) {
		return rongCloudService.group_create(json);
	}


	/**
	 * app修改群组信息
	 */
	@RequestMapping("/group_update")
	@ResponseBody
	public Object group_update(@RequestParam Map<String, Object> map) {
		return rongCloudService.group_update(map);
	}


	/**
	 * app发布群公告
	 */
	@RequestMapping("/group_notice_insert")
	@ResponseBody
	public Object group_notice_insert(@RequestParam Map<String, Object> map) {
		return rongCloudService.group_notice_insert(map);
	}

	/**
	 * app修改群公告
	 */
	@RequestMapping("/group_notice_update")
	@ResponseBody
	public Object group_notice_update(@RequestParam Map<String, Object> map) {
		return rongCloudService.group_notice_update(map);
	}

	/**
	 * app最新群公告
	 */
	@RequestMapping("/group_active_notice")
	@ResponseBody
	public Object group_active_notice(@RequestParam Map<String, Object> map) {
		return rongCloudService.group_active_notice(map);
	}
	/**
	 * app群公告列表
	 */
	@RequestMapping("/group_notice_list")
	@ResponseBody
	public Object group_notice_list(@RequestParam Map<String, Object> map) {
		return rongCloudService.group_notice_list(map);
	}


	/**
	 * app解散群组
	 */
	@RequestMapping("/group_dismiss")
	@ResponseBody
	public Object group_dismiss(@RequestParam Map<String, Object> map) {
		return rongCloudService.group_dismiss(map);
	}

	/**
	 * 群内群员列表
	 */
	@RequestMapping("/user_list_ByGroup")
	@ResponseBody
	public Object group_detail_ByGroup(@RequestParam Map<String, Object> map) {
		return rongCloudService.user_list_ByGroup(map);
	}


	/**
	 * 群信息
	 */
	@RequestMapping("/group_detail")
	@ResponseBody
	public Object group_detail(String rong_group_id) {
		return rongCloudService.group_detail(rong_group_id);
	}


	/**
	 * 邀请人加入群聊
	 */
	@RequestMapping(value="/group_join",method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Object group_join(@RequestBody String json) {
		return rongCloudService.group_join(json);
	}


	/**
	 * 退出群聊
	 */
	@RequestMapping(value="/group_quit",method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Object group_quit(@RequestBody String json) {
		return rongCloudService.group_quit(json);
	}

	/**
	 * app展示用户所加的群组
	 */
	@RequestMapping("/group_list_ByUser")
	@ResponseBody
	public Object group_list_ByUser(@RequestParam Map<String, Object> map) {
		return rongCloudService.group_list_ByUser(map);
	}


	/**
	 * 所有用户列表(添加联系人列表)
	 */
	@RequestMapping("/user_list")
	@ResponseBody
	public Object user_list(@RequestParam Map<String, Object> map) {
		return rongCloudService.user_list(map);
	}

	//=================================Workbench工作台=========================//

	/**
	 * 添加工作任务
	 */
	@RequestMapping("/work_add")
	@ResponseBody
	public Object work_add(@RequestParam Map<String, Object> map) {
		return rongCloudService.work_add(map);
	}

	/**
	 * 工作任务类型列表
	 */
	@RequestMapping("/work_type_list")
	@ResponseBody
	public Object work_type_list(@RequestParam Map<String, Object> map) {
		return rongCloudService.work_type_list(map);
	}

	/**
	 * 工作提醒类型列表
	 */
	@RequestMapping("/work_remind_list")
	@ResponseBody
	public Object work_remind_list(@RequestParam Map<String, Object> map) {
		return rongCloudService.work_remind_list(map);
	}

	/**
	 * 工作任务列表(详情) 通过日期查询
	 */
	@RequestMapping("/work_list_ByDate")
	@ResponseBody
	public Result work_list_ByDate(@RequestParam Map<String, Object> map) {
		return rongCloudService.work_list_ByDate(map);
	}


	/**
	 * 用户的所有工作任务
	 */
	@RequestMapping("/work_list_ByUser")
	@ResponseBody
	public Result work_list_ByUser(@RequestParam Map<String, Object> map) {
		return rongCloudService.work_list_ByUser(map);
	}

	/**
	 * 工作台 通过日期查询
	 */
	@RequestMapping("/Workbench_ByDate")
	@ResponseBody
	public Object Workbench_ByDate(@RequestParam Map<String, Object> map) {
		return rongCloudService.Workbench_ByDate(map);
	}

	//////////////////////////组织架构列表///////////////////////////////////////

	/**
	 * 组织架构(所有)
	 */
	@RequestMapping("/structure_list")
	@ResponseBody
	public Object group_structure_list(@RequestParam Map<String, Object> map) {
		return rongCloudService.structure_list(map);
	}

	/**
	 * 组织架构(当前用户)
	 */
	@RequestMapping("/structure_ByUser")
	@ResponseBody
	public Object structure_ByUser(@RequestParam Map<String, Object> map) {
		return rongCloudService.structure_ByUser(map);
	}


	/**
	 * 天气 限行 黄历
	 */
	@RequestMapping("/almanac_ByDate")
	@ResponseBody
	public Object almanac_ByDate(@RequestParam Map<String, Object> map) {
		return rongCloudService.almanac_ByDate(map);
	}

//==========================================发送通知==========
	/**
	 * 发送通知给当前用户
	 */
	@RequestMapping("/sendNoticeToUser")
	@ResponseBody
	public CodeMsg sendNoticeToUser(@RequestParam Map<String, Object> map) {
		return rongCloudService.sendNoticeToUser(map);
	}



//==========================================DING 消息==========
	/**
	 * DING-消息统计
	 */
	@RequestMapping("/getDingStatistic")
	@ResponseBody
	public Result getDingStatistic(@RequestParam Map<String, Object> map) {
		return rongCloudService.getDingStatistic(map);
	}

	/**
	 * DING-标记为已读
	 */
	@RequestMapping("/setDingToRead")
	@ResponseBody
	public CodeMsg setDingToRead(@RequestParam Map<String, Object> map) {
		return rongCloudService.setDingToRead(map);
	}

	/**
	 * DING-创建DING消息
	 */
	@RequestMapping(value="/createDingMsg",method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public CodeMsg createDingMsg(@RequestBody String json) {
		return rongCloudService.createDingMsg(json);
	}


	/**
	 * DING-查看自己发送的消息集合
	 */
	@RequestMapping("/getDingByUser")
	@ResponseBody
	public Result getDingByUser(@RequestParam Map<String, Object> map) {
		return rongCloudService.getDingByUser(map);
	}
}
