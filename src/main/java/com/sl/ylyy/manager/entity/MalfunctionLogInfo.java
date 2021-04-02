package com.sl.ylyy.manager.entity;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.List;

public class MalfunctionLogInfo implements Serializable{
	
	private int id;
	private MalfunctionInfo malfunctionInfo;  //故障id
	private int status;  //状态（1：未接受；2：已接受；3：维修失败；4：维修成功；5：通过审核；6：未通过审核）
	private UserInfo fixUserInfo;  //维修人
	private UserInfo appointUserInfo;  //指派人
	private String reason;  //维修失败原因
	private long appoint_at;  //指派时间
	private long accept_at;  //接受时间
	private long fix_at;  //维修时间
	private long check_at;  //审核时间
	
	
	private List<MaterialInfo> materialList;  //物资
	private List<SupportInfo> supportInfoList;  //支援人
	
	private String fixTime;  //维修时间
	private double conTime;  //耗时
	
	
	
	
	public double getConTime() {
		if(fix_at==0){
			return 0.00;
		}else{
			if((fix_at- malfunctionInfo.getCreate_at())/3600/24<1){
				return 1;
			}else{
				return (fix_at- malfunctionInfo.getCreate_at())/3600/24;
			}
		}
	}
	public void setConTime(double conTime) {
		this.conTime = conTime;
	}
	
	
	public String getFixTime() {
		if(fix_at==0){
			return "未维修";
		}else{
		    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");   //把时间转换成年月日
			 return sdf.format(fix_at*1000);
		}
	}
	public void setFixTime(String fixTime) {
		this.fixTime = fixTime;
	}
	
	
	public List<MaterialInfo> getMaterialList() {
		return materialList;
	}
	public void setMaterialList(List<MaterialInfo> materialList) {
		this.materialList = materialList;
	}
	public List<SupportInfo> getSupportInfoList() {
		return supportInfoList;
	}
	public void setSupportInfoList(List<SupportInfo> supportInfoList) {
		this.supportInfoList = supportInfoList;
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
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	
	public UserInfo getFixUserInfo() {
		return fixUserInfo;
	}
	public void setFixUserInfo(UserInfo fixUserInfo) {
		this.fixUserInfo = fixUserInfo;
	}
	public UserInfo getAppointUserInfo() {
		return appointUserInfo;
	}
	public void setAppointUserInfo(UserInfo appointUserInfo) {
		this.appointUserInfo = appointUserInfo;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public long getAppoint_at() {
		return appoint_at;
	}
	public void setAppoint_at(int appoint_at) {
		this.appoint_at = appoint_at;
	}
	public long getAccept_at() {
		return accept_at;
	}
	public void setAccept_at(int accept_at) {
		this.accept_at = accept_at;
	}
	public long getFix_at() {
		return fix_at;
	}
	public void setFix_at(int fix_at) {
		this.fix_at = fix_at;
	}
	public long getCheck_at() {
		return check_at;
	}
	public void setCheck_at(int check_at) {
		this.check_at = check_at;
	}
	
	
	
}
