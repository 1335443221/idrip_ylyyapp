package com.sl.ylyy.app_1.entity;

import lombok.Data;

@Data
public class Company {
    private Integer id;

    private String cname;

    private String remark;

    private Integer parentId;
}