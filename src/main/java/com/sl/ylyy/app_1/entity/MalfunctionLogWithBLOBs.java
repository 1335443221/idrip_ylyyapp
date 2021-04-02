package com.sl.ylyy.app_1.entity;

public class MalfunctionLogWithBLOBs extends MalfunctionLog {
    private String reason;

    private String remark;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason == null ? null : reason.trim();
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }
}