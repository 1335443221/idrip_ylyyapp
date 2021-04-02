package com.sl.ylyy.manager.entity;
import com.sl.ylyy.app_1.entity.User;

public class MaterialInfo {
	private int id;
	private String name;
	private String number;
	private User createUser;
	private MalfunctionInfo malfunction;
	private String out_bound_date;
	
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getOut_bound_date() {
		return out_bound_date;
	}
	public void setOut_bound_date(String out_bound_date) {
		this.out_bound_date = out_bound_date;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public User getCreateUser() {
		return createUser;
	}
	public void setCreateUser(User createUser) {
		this.createUser = createUser;
	}

	public MalfunctionInfo getMalfunction() {
		return malfunction;
	}

	public void setMalfunction(MalfunctionInfo malfunction) {
		this.malfunction = malfunction;
	}
}
