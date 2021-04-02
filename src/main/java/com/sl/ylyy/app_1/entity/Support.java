package com.sl.ylyy.app_1.entity;

import lombok.Data;

@Data
public class Support {
    private Integer id;

    private Integer malfunctionId;

    private Integer supportType;

    private String supportStaff;

    private Integer supportNumber;

    private Integer createBy;

    private String respondStaff;

    private String refuseStaff;

    private String imageDesc;

    private String audioDesc;

    private Integer mStatus;

    private Integer status;

    private Integer createAt;

    private Integer supportAt;

    private Integer terminateAt;
}