package com.sl.ylyy.app_1.entity;

public class MalfunctionLog {
    private Integer id;

    private Integer malfunctionId;

    private Integer status;

    private Integer fixBy;

    private Integer appointBy;

    private Integer appointAt;

    private Integer acceptAt;

    private Integer hangupAt;

    private String expireAt;

    private Integer fixAt;

    private Integer checkAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getMalfunctionId() {
        return malfunctionId;
    }

    public void setMalfunctionId(Integer malfunctionId) {
        this.malfunctionId = malfunctionId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getFixBy() {
        return fixBy;
    }

    public void setFixBy(Integer fixBy) {
        this.fixBy = fixBy;
    }

    public Integer getAppointBy() {
        return appointBy;
    }

    public void setAppointBy(Integer appointBy) {
        this.appointBy = appointBy;
    }

    public Integer getAppointAt() {
        return appointAt;
    }

    public void setAppointAt(Integer appointAt) {
        this.appointAt = appointAt;
    }

    public Integer getAcceptAt() {
        return acceptAt;
    }

    public void setAcceptAt(Integer acceptAt) {
        this.acceptAt = acceptAt;
    }

    public Integer getHangupAt() {
        return hangupAt;
    }

    public void setHangupAt(Integer hangupAt) {
        this.hangupAt = hangupAt;
    }

    public String getExpireAt() {
        return expireAt;
    }

    public void setExpireAt(String expireAt) {
        this.expireAt = expireAt == null ? null : expireAt.trim();
    }

    public Integer getFixAt() {
        return fixAt;
    }

    public void setFixAt(Integer fixAt) {
        this.fixAt = fixAt;
    }

    public Integer getCheckAt() {
        return checkAt;
    }

    public void setCheckAt(Integer checkAt) {
        this.checkAt = checkAt;
    }
}