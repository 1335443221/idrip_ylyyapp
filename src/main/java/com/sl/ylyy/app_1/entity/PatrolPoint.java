package com.sl.ylyy.app_1.entity;

import lombok.Data;

@Data
public class PatrolPoint {
    private Integer id;

    private String number;

    private String name;

    private String detail;

    private Integer createBy;

    private Integer createAt;

    private Integer companyId;
}