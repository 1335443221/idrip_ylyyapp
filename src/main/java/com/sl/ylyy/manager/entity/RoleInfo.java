package com.sl.ylyy.manager.entity;

import java.io.Serializable;
import java.util.List;

public class RoleInfo implements Serializable{
	private static final long serialVersionUID = 4702946019240523593L;
	private int id; //id
	private String name; //角色名称
	private String remark;  //备注
	private String center_role_id;  //备注

	private String aids;  //对应权限id字符串

	private List<Auth> authList;  //id对应的权限


	public String getCenter_role_id() {
		return center_role_id;
	}

	public void setCenter_role_id(String center_role_id) {
		this.center_role_id = center_role_id;
	}

	public List<Auth> getAuthList() {
		return authList;
	}
	public void setAuthList(List<Auth> authList) {
		this.authList = authList;
	}
	public String getAids() {
		return aids;
	}
	public void setAids(String aids) {
		this.aids = aids;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	
	
}
