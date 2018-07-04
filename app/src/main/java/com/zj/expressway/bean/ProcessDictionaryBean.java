package com.zj.expressway.bean;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * Create dell By 2018/7/2 11:04
 */

public class ProcessDictionaryBean extends DataSupport implements Serializable {
    private String delFlag;
    private long createTime;
    private String createUser;
    private String createUserName;
    private long modifyTime;
    private String modifyUser;
    private String modifyUserName;
    private String dictId;
    private String dictName;
    private String dictCode;
    private String parentId;
    private String photoContent;
    private String photoDistance;
    private int photoNumber;
    private String firstLevelId;
    private String firstLevelName;
    private String secondLevelId;
    private String secondLevelName;
    private String thirdLevelId;
    private String thirdLevelName;
    private String operation;
    private String type; // 1:层级 2:工序
    private boolean isSelect; // 1:层级 2:工序

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
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

    public String getDictId() {
        return dictId;
    }

    public void setDictId(String dictId) {
        this.dictId = dictId;
    }

    public String getDictName() {
        return dictName;
    }

    public void setDictName(String dictName) {
        this.dictName = dictName;
    }

    public String getDictCode() {
        return dictCode;
    }

    public void setDictCode(String dictCode) {
        this.dictCode = dictCode;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getPhotoContent() {
        return photoContent;
    }

    public void setPhotoContent(String photoContent) {
        this.photoContent = photoContent;
    }

    public String getPhotoDistance() {
        return photoDistance;
    }

    public void setPhotoDistance(String photoDistance) {
        this.photoDistance = photoDistance;
    }

    public int getPhotoNumber() {
        return photoNumber;
    }

    public void setPhotoNumber(int photoNumber) {
        this.photoNumber = photoNumber;
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
}
