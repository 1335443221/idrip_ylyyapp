package com.sl.ylyy.manager.entity;

import java.io.Serializable;

public class Role_Auth_Relation implements Serializable{

	private RoleInfo roleInfo;  //角色
	private Auth auth;  //权限
	
	public RoleInfo getRoleInfo() {
		return roleInfo;
	}
	public void setRoleInfo(RoleInfo roleInfo) {
		this.roleInfo = roleInfo;
	}
	public Auth getAuth() {
		return auth;
	}
	public void setAuth(Auth auth) {
		this.auth = auth;
	}     
	
	
}
