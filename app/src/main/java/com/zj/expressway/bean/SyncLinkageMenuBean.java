package com.zj.expressway.bean;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * Create dell By 2018/7/2 13:14
 */

public class SyncLinkageMenuBean extends DataSupport implements Serializable {
    private String delFlag;
    private long createTime;
    private String createUser;
    private String createUserName;
    private long modifyTime;
    private String modifyUser;
    private String modifyUserName;
    private String firstLevelId;
    private String firstLevelName;
    private String firstLevelCode;
    private String secondLevelId;
    private String secondLevelName;
    private String secondLevelCode;
    private String thirdLevelId;
    private String thirdLevelName;
    private String thirdLevelCode;
    private int sortFlag;
    private String selectFlag;
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSecondLevelId() {
        return secondLevelId;
    }

    public void setSecondLevelId(String secondLevelId) {
        this.secondLevelId = secondLevelId;
    }

    public String getSecondLevelName() {
        return secondLevelName;
    }

    public void setSecondLevelName(String secondLevelName) {
        this.secondLevelName = secondLevelName;
    }

    public String getSecondLevelCode() {
        return secondLevelCode;
    }

    public void setSecondLevelCode(String secondLevelCode) {
        this.secondLevelCode = secondLevelCode;
    }

    public String getThirdLevelId() {
        return thirdLevelId;
    }

    public void setThirdLevelId(String thirdLevelId) {
        this.thirdLevelId = thirdLevelId;
    }

    public String getThirdLevelName() {
        return thirdLevelName;
    }

    public void setThirdLevelName(String thirdLevelName) {
        this.thirdLevelName = thirdLevelName;
    }

    public String getThirdLevelCode() {
        return thirdLevelCode;
    }

    public void setThirdLevelCode(String thirdLevelCode) {
        this.thirdLevelCode = thirdLevelCode;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    public long getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(long modifyTime) {
        this.modifyTime = modifyTime;
    }

    public String getModifyUser() {
        return modifyUser;
    }

    public void setModifyUser(String modifyUser) {
        this.modifyUser = modifyUser;
    }

    public String getModifyUserName() {
        return modifyUserName;
    }

    public void setModifyUserName(String modifyUserName) {
        this.modifyUserName = modifyUserName;
    }

    public String getFirstLevelId() {
        return firstLevelId;
    }

    public void setFirstLevelId(String firstLevelId) {
        this.firstLevelId = firstLevelId;
    }

    public String getFirstLevelName() {
        return firstLevelName;
    }

    public void setFirstLevelName(String firstLevelName) {
        this.firstLevelName = firstLevelName;
    }

    public String getFirstLevelCode() {
        return firstLevelCode;
    }

    public void setFirstLevelCode(String firstLevelCode) {
        this.firstLevelCode = firstLevelCode;
    }

    public int getSortFlag() {
        return sortFlag;
    }

    public void setSortFlag(int sortFlag) {
        this.sortFlag = sortFlag;
    }

    public String getSelectFlag() {
        return selectFlag;
    }

    public void setSelectFlag(String selectFlag) {
        this.selectFlag = selectFlag;
    }
}
