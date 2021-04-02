package com.sl.ylyy.manager.service.impl;

import static com.sl.ylyy.common.config.UrlConfig.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;

import com.google.gson.Gson;
import com.sl.ylyy.common.config.UrlConfig;
import com.sl.ylyy.common.utils.RongUtil;
import com.sl.ylyy.manager.dao.RongCloudDao;
import com.sl.ylyy.manager.dao.UserDao;
import com.sl.ylyy.manager.entity.GroupNotificationMessage;
import com.sl.ylyy.manager.entity.GroupNotificationMessageData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sl.ylyy.manager.dao.ManageDao;
import com.sl.ylyy.app_1.dao.AudioDescMapper;
import com.sl.ylyy.app_1.dao.ImageDescMapper;
import com.sl.ylyy.manager.dao.CommonDao;

@Component
public class CommonDataServiceImpl {

	@Autowired
	private ManageDao ManageDao;
	@Autowired
	private CommonDao commonDao;
	@Autowired
	private UserDao userDao;
	@Autowired
	private AudioDescMapper audioDescMapper;
	@Autowired
	private ImageDescMapper imageDescMapper;
	@Autowired
	private RongCloudDao rongCloudDao;

	public static CommonDataServiceImpl CommonDataServiceImpl;


	@PostConstruct
	public void init() {
		CommonDataServiceImpl = this;
		CommonDataServiceImpl.commonDao = this.commonDao;
		CommonDataServiceImpl.ManageDao = this.ManageDao;
	}


	/**
	 * 把String类型的file文件转换为List
	 *
	 * @param file
	 * @return
	 */
	public static List<Integer> fileStringToList(String file) {
		JSONArray fileArray = JSONObject.parseArray(file);
		List<Integer> fileList = new ArrayList<Integer>();
		for (int i = 0; i < fileArray.size(); i++) {
			Map<String, Object> fileMap = fileArray.getJSONObject(i);
			if (fileMap.get("name") == null || "".equals(String.valueOf(fileMap.get("name")))) {
				fileMap.put("name", "点击查看文件");
			}
			CommonDataServiceImpl.commonDao.add_file_desc(fileMap);//在file表中添加一条
			fileList.add(Integer.parseInt(fileMap.get("id").toString()));  //新插入的id
		}
		return fileList;
	}


	/**
	 * 改变工程状态
	 *
	 * @param
	 * @return
	 */
	public static int updateProjectState(int state, int project_id) {
		Map<String, Object> stateData = new HashMap<String, Object>();
		stateData.put("project_id", project_id);
		stateData.put("state", state);
		stateData.put("save_state", 3);
		return CommonDataServiceImpl.commonDao.update_project_state(stateData);
	}


	/**
	 * 存session
	 *
	 * @param
	 * @return
	 */
	public static int insertSession(Map<String, Object> map) {
		int result = CommonDataServiceImpl.userDao.insertSession(map);
		return result;
	}


	/**
	 * 验收session
	 *
	 * @param
	 * @return
	 */
	public static Map<String, Object> getSession(Map<String, Object> map) {
		Map<String, Object> sessionMap = CommonDataServiceImpl.userDao.getSession(map);
		if (sessionMap == null) {
			return null;
		} else {
			Map<String, Object> valueMap = JSONObject.parseObject(String.valueOf(sessionMap.get("gtgx_value")));
			return valueMap;
		}
	}


	/**
	 * 添加文件
	 *
	 * @param params
	 * @param file
	 * @return
	 */
	public static Map<String, Object> addFile(Map<String, Object> params, MultipartFile file) {
		Integer userId = Integer.parseInt(String.valueOf(params.get("create_by")));
		Map<String, Object> result = new HashMap<>();
		//上传限制8M
		//1M=1048576字节
		if (file.getSize() > (8 * 1048576)) {
			result.put("code", 1008);
			result.put("msg", "大小超出限制");
			return result;
		}
		String fileName = System.currentTimeMillis() + file.getOriginalFilename();
		String filePath = "";
		String type = "";
		try {
			if (params.get("type") == null || ("image".equals(params.get("type")))) {
				type = "image";
				BufferedImage bi = null;
				bi = ImageIO.read(file.getInputStream());
				if (bi == null) {
					//上传的文件不是图片
					result.put("code", 1007);
					result.put("msg", "非法类型");
					return result;
				}
				filePath = "image/" + new SimpleDateFormat("yyMMdd").format(new Date()) + "/";

			} else if ("audio".equals(params.get("type"))) {
				type = "audio";
				filePath = "audio/" + new SimpleDateFormat("yyMMdd").format(new Date()) + "/";
			} else {
				result.put("code", 1005);
				result.put("msg", "文件类型有误");
				return result;
			}
			String path = UPLOAD + filePath + fileName;
			File uploadFile = new File(path);
			// 检测是否存在目录
			if (!uploadFile.getParentFile().exists()) {
				try {
					uploadFile.getParentFile().mkdirs();// 新建文件夹
				} catch (Exception e) {
					result.put("code", 1009);
					result.put("msg", "上传目录不存在或者不可写");
					return result;
				}
			}
			file.transferTo(uploadFile);// 文件写入
			//写入的文件存入数据库，判断存入图片表还是音频表中
			try {
				Integer id = 0;
				if ("image".equals(type)) {
					CommonDataServiceImpl.imageDescMapper.insertContent(filePath + fileName, userId);
					id = CommonDataServiceImpl.imageDescMapper.selectIdByCreateByAndContent(filePath + fileName, userId);
				} else {
					CommonDataServiceImpl.audioDescMapper.insertContent(filePath + fileName, userId);
					id = CommonDataServiceImpl.audioDescMapper.selectIdByCreateByAndContent(filePath + fileName, userId);
				}

				result.put("id", id);
				result.put("url", DOWNURL + filePath + fileName);
				result.put("code", 1000);
				result.put("msg", "上传成功");
				return result;
			} catch (Exception e) {
				result.put("code", 1010);
				result.put("msg", "文件保存失败");
				return result;
			}

		} catch (IOException e) {
			e.printStackTrace();
			result.put("code", 1006);
			result.put("msg", "发生系统错误");
			return result;
		}
	}


	/**
	 * 解散群组
	 *
	 * @return
	 */
	public static int rong_group_dismiss(Map<String, Object> map) {
		Map<String, Object> group2 = new HashMap<>();
		if (map.get("rong_group_id") != null) {
			group2 = CommonDataServiceImpl.rongCloudDao.group_select(String.valueOf(map.get("rong_group_id")), null);
		} else if (map.get("group_id") != null) {
			group2 = CommonDataServiceImpl.rongCloudDao.group_select(null, Integer.parseInt(String.valueOf(map.get("group_id"))));
		}

		Map<String, Object> group = new HashMap<>();
		group.put("dismiss_by", map.get("dismiss_by"));
		group.put("is_dismiss", 1);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");   //把时间转换成年月日
		String dismiss_at = sdf.format(new Date());
		group.put("dismiss_at", dismiss_at);  //解散时间
		group.put("rong_group_id", group2.get("rong_group_id"));
		int result = CommonDataServiceImpl.rongCloudDao.group_update(group); //修改群
		if (result > 0) {
			Gson gosn = new Gson();
			GroupNotificationMessageData gn=new GroupNotificationMessageData();
			gn.setOperatorNickname(CommonDataServiceImpl.rongCloudDao.getUserNameById(null,String.valueOf(map.get("rong_user_id"))));
			GroupNotificationMessage message=new GroupNotificationMessage(String.valueOf(map.get("rong_user_id")),GroupNotificationMessage.GROUP_OPERATION_DISMISS,gosn.toJson(gn));
			String jsonMessage=gosn.toJson(message);
			RongUtil.groupPublish(String.valueOf(group.get("rong_group_id")),jsonMessage);
			RongUtil.groupDismiss(group);
		}
		return result;
	}


	/**
	 * 新增群组
	 *
	 * @return
	 */
	public static int rong_group_insert(Map<String, Object> map) {
		if (map.get("group_portrait") == null || "".equals(String.valueOf(map.get("group_portrait")))) {
			map.put("group_portrait", GroupPortrait); //默认群头像
		}
		int result = CommonDataServiceImpl.rongCloudDao.group_insert(map); //建立群组
		map.put("rong_group_id", UrlConfig.RONG_ID_PREFIX + map.get("group_id")); //添加融云id
		CommonDataServiceImpl.rongCloudDao.group_update(map);  //添加融云id

		if (result > 0) {
			//在融云中注册群组
			List<Map<String, Object>> userGroup = new ArrayList<>();
			Map<String, Object> userMap = new HashMap<>();
			userMap.put("rong_user_id", map.get("rong_user_id"));
			userGroup.add(userMap);
			map.put("userList", userGroup);
			RongUtil.groupCreate(map);

			Gson gosn = new Gson();
			GroupNotificationMessageData gn=new GroupNotificationMessageData();
			gn.setOperatorNickname(CommonDataServiceImpl.rongCloudDao.getUserNameById(null,String.valueOf(map.get("rong_user_id"))));
			GroupNotificationMessage message=new GroupNotificationMessage(String.valueOf(map.get("rong_user_id")),GroupNotificationMessage.GROUP_OPERATION_DISMISS,gosn.toJson(gn));
			String jsonMessage=gosn.toJson(message);
			RongUtil.groupPublish(String.valueOf(map.get("rong_group_id")),jsonMessage);
		}
		return result;
	}


	/**
	 * 修改群组
	 *
	 * @return
	 */
	public static int rong_group_update(Map<String, Object> map) {
		Map<String, Object> oldgroup = new HashMap<>();
		if (map.get("rong_group_id") != null) {
			oldgroup = CommonDataServiceImpl.rongCloudDao.group_select(String.valueOf(map.get("rong_group_id")), null);
		}
		if (map.get("group_id") != null) {
			oldgroup = CommonDataServiceImpl.rongCloudDao.group_select(null, Integer.parseInt(String.valueOf(map.get("group_id"))));
		}

		int result = CommonDataServiceImpl.rongCloudDao.group_update(map); //修改群组
		Map<String, Object> group = new HashMap<>();
		if (map.get("rong_group_id") != null) {
			group = CommonDataServiceImpl.rongCloudDao.group_select(String.valueOf(map.get("rong_group_id")), null);
		}
		if (map.get("group_id") != null) {
			group = CommonDataServiceImpl.rongCloudDao.group_select(null, Integer.parseInt(String.valueOf(map.get("group_id"))));
		}
		if (result > 0) {
			//在融云中修改群组
			RongUtil.groupUpdate(group);
			if(!String.valueOf(oldgroup.get("group_name")).equals(String.valueOf(group.get("group_name")))){
				Gson gosn = new Gson();
				GroupNotificationMessageData gn=new GroupNotificationMessageData();
				gn.setTargetGroupName(String.valueOf(group.get("group_name")));
				gn.setOperatorNickname(CommonDataServiceImpl.rongCloudDao.getUserNameById(null,String.valueOf(map.get("rong_user_id"))));
				String json=gosn.toJson(gn);
				GroupNotificationMessage message=new GroupNotificationMessage(String.valueOf(map.get("rong_user_id")),GroupNotificationMessage.GROUP_OPERATION_RENAME,json);
				String jsonMessage=gosn.toJson(message);
				RongUtil.groupPublish(String.valueOf(group.get("rong_group_id")),jsonMessage);
			}
		}
		return result;
	}


	/**
	 * 录入融云数据方法
	 */
	public static int testInsertUser(Map<String, Object> map) {
		Map<String, Object> user3 = new HashMap<>();
		List<Map<String, Object>> userList = CommonDataServiceImpl.rongCloudDao.user_list(user3);
		for (int i = 0; i < userList.size(); i++) {
			Map<String, Object> user = new HashMap<>();
			user.put("portrait", userList.get(i).get("portrait"));
			user.put("rong_user_id", userList.get(i).get("rong_user_id"));
			user.put("user_name", userList.get(i).get("user_name"));
			Map<String, Object> token = RongUtil.userRegister(user);

			Map<String, Object> user2 = userList.get(i);
			user2.put("rong_token", token.get("token"));
			CommonDataServiceImpl.userDao.updateUserinfo(user2);
			System.out.println(i);
		}
		return 1;
	}


	/**
	 * 上传list[]  返回String
	 */
	public static String insertFile(List filelist, String type, int userid) {
		Map<String, Object> map = new HashMap<>();
		map.put("createBy", userid);
		String result = "";
		if ("image_desc".equals(type)) {
			for (int i = 0; i < filelist.size(); i++) {
				map.put("content",filelist.get(i));
				CommonDataServiceImpl.commonDao.insertImage(map);
				if (i == 0) {
					result = String.valueOf(map.get("id"));
				} else {
					result = result + "," + String.valueOf(map.get("id"));
				}
			}
		}
		if ("audio_desc".equals(type)) {
			for (int i = 0; i < filelist.size(); i++) {
				map.put("content",filelist.get(i));
				CommonDataServiceImpl.commonDao.insertAudio(map);
				if (i == 0) {
					result = String.valueOf(map.get("id"));
				} else {
					result = result + "," + String.valueOf(map.get("id"));
				}
			}
		}
		return result;
	}
}