package com.sl.ylyy.app_1.entity;

import lombok.Data;

@Data
public class MalfunctionWithBLOBs extends Malfunction {
    private String question;

    private String material;

    private String remark;

}