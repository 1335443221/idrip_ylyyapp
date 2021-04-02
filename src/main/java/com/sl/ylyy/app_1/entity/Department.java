package com.sl.ylyy.app_1.entity;

import lombok.Data;

@Data
public class Department {
    private Integer id;

    private String dname;

    private String remark;

    private Integer companyId;

    private Integer charge;
}