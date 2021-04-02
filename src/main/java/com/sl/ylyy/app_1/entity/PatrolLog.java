package com.sl.ylyy.app_1.entity;

import lombok.Data;

@Data
public class PatrolLog {
    private Integer id;

    private Integer patrolId;

    private Integer status;

    private Integer patrolBy;

    private Integer firstClockAt;

    private Integer secondClockAt;

    private Integer thirdClockAt;

    private Integer fourthClockAt;

}