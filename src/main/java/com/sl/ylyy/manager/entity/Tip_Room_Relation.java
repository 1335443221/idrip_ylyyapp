package com.sl.ylyy.manager.entity;

import com.sl.ylyy.app_1.entity.Switchingroom;

import java.io.Serializable;

public class Tip_Room_Relation implements Serializable{

	private TipInfo tipInfo;  //角色
	private Switchingroom switchingroom;  //权限
	public TipInfo getTipInfo() {
		return tipInfo;
	}
	public void setTipInfo(TipInfo tipInfo) {
		this.tipInfo = tipInfo;
	}
	public Switchingroom getSwitchingroom() {
		return switchingroom;
	}
	public void setSwitchingroom(Switchingroom switchingroom) {
		this.switchingroom = switchingroom;
	}
	
	
	
	
}
