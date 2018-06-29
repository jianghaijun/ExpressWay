package com.zj.expressway.bean;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

public class PhotosBean extends DataSupport implements Serializable {
    private int isToBeUpLoad = -1;
    private int isNewAdd = -1;
    private String userId;
    private String modifyUserName;
    private String createUserName;
    private String processId;
    private String thumbUrl;
    private String delFlag;
    private String workId;
    private String photoId;
    private String photoName;
    private long createTime;
    private String url;
    private String uid;
    private String longitude;
    private String latitude;
    private String location;
    private String modifyUser;
    private long modifyTime;
    private String photoDesc;
    private String roleFlag;
    private String checkFlag;
    private String photoType;
    private String createUser;
    private String otherId;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getIsToBeUpLoad() {
        return isToBeUpLoad;
    }

    public void setIsToBeUpLoad(int isToBeUpLoad) {
        this.isToBeUpLoad = isToBeUpLoad;
    }

    public int getIsNewAdd() {
        return isNewAdd;
    }

    public void setIsNewAdd(int isNewAdd) {
        this.isNewAdd = isNewAdd;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getModifyUserName() {
        return modifyUserName;
    }

    public void setModifyUserName(String modifyUserName) {
        this.modifyUserName = modifyUserName;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    public String getWorkId() {
        return workId;
    }

    public void setWorkId(String workId) {
        this.workId = workId;
    }

    public String getPhotoId() {
        return photoId;
    }

    public void setPhotoId(String photoId) {
        this.photoId = photoId;
    }

    public String getPhotoName() {
        return photoName;
    }

    public void setPhotoName(String photoName) {
        this.photoName = photoName;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getModifyUser() {
        return modifyUser;
    }

    public void setModifyUser(String modifyUser) {
        this.modifyUser = modifyUser;
    }

    public long getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(long modifyTime) {
        this.modifyTime = modifyTime;
    }

    public String getPhotoDesc() {
        return photoDesc;
    }

    public void setPhotoDesc(String photoDesc) {
        this.photoDesc = photoDesc;
    }

    public String getRoleFlag() {
        return roleFlag;
    }

    public void setRoleFlag(String roleFlag) {
        this.roleFlag = roleFlag;
    }

    public String getCheckFlag() {
        return checkFlag;
    }

    public void setCheckFlag(String checkFlag) {
        this.checkFlag = checkFlag;
    }

    public String getPhotoType() {
        return photoType;
    }

    public void setPhotoType(String photoType) {
        this.photoType = photoType;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getOtherId() {
        return otherId;
    }

    public void setOtherId(String otherId) {
        this.otherId = otherId;
    }
}
