package com.sl.ylyy.app_1.entity;

import lombok.Data;

@Data
public class Group {
    private Integer id;

    private String name;

    private String remark;

    private Integer departmentId;

    private Integer companyId;

    private Integer groupManager;
}