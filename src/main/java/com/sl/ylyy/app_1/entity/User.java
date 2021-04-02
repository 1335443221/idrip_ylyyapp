package com.sl.ylyy.app_1.entity;

import lombok.Data;

@Data
public class User {
    private Integer id;

    private String jobNumber;

    private String loginName;

    private String userName;

    private String email;

    private String phone;

    private String cellphone;

    private String password;

    private String portrait;

    private Integer createAt;

    private Integer updateAt;

    private Integer allowLogin;

    private Integer isDelete;

    private Integer companyId;

    private Integer departmentId;

    private Integer groupId;

    public User(Integer id, String jobNumber, String loginName, String userName, String email, String phone, String cellphone, String password, String portrait, Integer createAt, Integer updateAt, Integer allowLogin, Integer isDelete, Integer companyId, Integer departmentId, Integer groupId) {
        this.id = id;
        this.jobNumber = jobNumber;
        this.loginName = loginName;
        this.userName = userName;
        this.email = email;
        this.phone = phone;
        this.cellphone = cellphone;
        this.password = password;
        this.portrait = portrait;
        this.createAt = createAt;
        this.updateAt = updateAt;
        this.allowLogin = allowLogin;
        this.isDelete = isDelete;
        this.companyId = companyId;
        this.departmentId = departmentId;
        this.groupId = groupId;
    }

    public User() {

    }
}