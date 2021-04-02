package com.sl.ylyy.manager.entity;

import java.io.Serializable;
import java.util.List;

public class SupportInfo implements Serializable{
	
	private int id;  //id
	private MalfunctionInfo malfunctionInfo;  //故障id
	private int support_type;  //指定类型（1：人员；2：人数）
	private String support_staff;  //人员（储存用户id，多个以英文逗号隔开）
	private int support_number;  //人数
	private UserInfo createUserInfo;  //发起人
	private String respond_staff;  //响应人（储存用户id，多个以英文逗号隔开）
	private String image_desc;  //图片描述（储存图片描述id，多个以英文逗号隔开）
	private String audio_desc;  //语音描述（储存语音描述id，多个以英文逗号隔开）
	private int status;  //进展状态（1：未结案；2：已结案）
	private int create_at;  //创建时间
	private int support_at;  //支援时间
	
	
	private List<UserInfo> userInfoList;   //支援人
	
	
	
	public List<UserInfo> getUserInfoList() {
		return userInfoList;
	}
	public void setUserInfoList(List<UserInfo> userInfoList) {
		this.userInfoList = userInfoList;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public MalfunctionInfo getMalfunctionInfo() {
		return malfunctionInfo;
	}
	public void setMalfunctionInfo(MalfunctionInfo malfunctionInfo) {
		this.malfunctionInfo = malfunctionInfo;
	}
	public int getSupport_type() {
		return support_type;
	}
	public void setSupport_type(int support_type) {
		this.support_type = support_type;
	}
	public String getSupport_staff() {
		return support_staff;
	}
	public void setSupport_staff(String support_staff) {
		this.support_staff = support_staff;
	}
	public int getSupport_number() {
		return support_number;
	}
	public void setSupport_number(int support_number) {
		this.support_number = support_number;
	}

	public UserInfo getCreateUserInfo() {
		return createUserInfo;
	}
	public void setCreateUserInfo(UserInfo createUserInfo) {
		this.createUserInfo = createUserInfo;
	}
	public String getRespond_staff() {
		return respond_staff;
	}
	public void setRespond_staff(String respond_staff) {
		this.respond_staff = respond_staff;
	}
	public String getImage_desc() {
		return image_desc;
	}
	public void setImage_desc(String image_desc) {
		this.image_desc = image_desc;
	}
	public String getAudio_desc() {
		return audio_desc;
	}
	public void setAudio_desc(String audio_desc) {
		this.audio_desc = audio_desc;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getCreate_at() {
		return create_at;
	}
	public void setCreate_at(int create_at) {
		this.create_at = create_at;
	}
	public int getSupport_at() {
		return support_at;
	}
	public void setSupport_at(int support_at) {
		this.support_at = support_at;
	}
	
	
	
	
	
	

}
