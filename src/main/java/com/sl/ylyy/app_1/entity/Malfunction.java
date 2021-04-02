package com.sl.ylyy.app_1.entity;

import lombok.Data;

@Data
public class Malfunction {
    private Integer id;

    private String location;

    private Integer fixBy;

    private Integer status;

    private Integer type;

    private Integer source;

    private Integer createBy;

    private Integer storekeeper;

    private String textDesc;

    private String imageDesc;

    private String audioDesc;

    private String finishDesc;

    private Integer createAt;

    private Integer appointAt;

    private String acceptAt;

    private Integer hangupAt;

    private String expireAt;

    private Integer fixAt;

    private Integer checkAt;

    private Integer isPush;

    private Integer departmentId;

    private Integer companyId;

    private int isCompanyManager;

    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getFixBy() {
        return fixBy;
    }

    public void setFixBy(Integer fixBy) {
        this.fixBy = fixBy;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getSource() {
        return source;
    }

    public void setSource(Integer source) {
        this.source = source;
    }

    public Integer getCreateBy() {
        return createBy;
    }

    public void setCreateBy(Integer createBy) {
        this.createBy = createBy;
    }

    public Integer getStorekeeper() {
        return storekeeper;
    }

    public void setStorekeeper(Integer storekeeper) {
        this.storekeeper = storekeeper;
    }

    public String getTextDesc() {
        return textDesc;
    }

    public void setTextDesc(String textDesc) {
        this.textDesc = textDesc;
    }

    public String getImageDesc() {
        return imageDesc;
    }

    public void setImageDesc(String imageDesc) {
        this.imageDesc = imageDesc;
    }

    public String getAudioDesc() {
        return audioDesc;
    }

    public void setAudioDesc(String audioDesc) {
        this.audioDesc = audioDesc;
    }

    public String getFinishDesc() {
        return finishDesc;
    }

    public void setFinishDesc(String finishDesc) {
        this.finishDesc = finishDesc;
    }

    public Integer getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Integer createAt) {
        this.createAt = createAt;
    }

    public Integer getAppointAt() {
        return appointAt;
    }

    public void setAppointAt(Integer appointAt) {
        this.appointAt = appointAt;
    }

    public String getAcceptAt() {
        return acceptAt;
    }

    public void setAcceptAt(String acceptAt) {
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
        this.expireAt = expireAt;
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

    public Integer getIsPush() {
        return isPush;
    }

    public void setIsPush(Integer isPush) {
        this.isPush = isPush;
    }

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    public int getIsCompanyManager() {
        return isCompanyManager;
    }

    public void setIsCompanyManager(int isCompanyManager) {
        this.isCompanyManager = isCompanyManager;
    }
}