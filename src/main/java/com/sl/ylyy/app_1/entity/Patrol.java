package com.sl.ylyy.app_1.entity;

import lombok.Data;

@Data
public class Patrol {

    private int id;

    private int patrolPointId;

    private int type;

    private int timeType;

    private int count;

    private int createBy;

    private int createAt;

    private String content;

    private Integer departmentId;
    private double timeInterval;
}