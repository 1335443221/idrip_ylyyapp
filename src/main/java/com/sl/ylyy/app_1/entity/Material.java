package com.sl.ylyy.app_1.entity;

import lombok.Data;

@Data
public class Material {
    private Integer id;

    private Integer createBy;

    private Integer malfunctionId;

    private Integer storekeeper;

    private Integer createAt;

    private String content;
}