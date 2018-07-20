package com.zj.expressway.bean;

import java.util.List;

/**
 * Created by wdplus02 on 2018/7/18.
 */

public class IDNumberBean {
    private String workerId;
    private String workerName;
    private String gender;
    private String nativePlace;
    private String identity;
    private long enterTime;
    private long trainingTime;
    private long exitTime;
    private String positionId;
    private String projectId;
    private String qrcodeId;
    private List<PositionBean> positionList;
    private List<PhotosBean> attachmentList;

    public List<PhotosBean> getAttachmentList() {
        return attachmentList;
    }

    public void setAttachmentList(List<PhotosBean> attachmentList) {
        this.attachmentList = attachmentList;
    }

    public String getWorkerId() {
        return workerId;
    }

    public void setWorkerId(String workerId) {
        this.workerId = workerId;
    }

    public String getWorkerName() {
        return workerName;
    }

    public void setWorkerName(String workerName) {
        this.workerName = workerName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getNativePlace() {
        return nativePlace;
    }

    public void setNativePlace(String nativePlace) {
        this.nativePlace = nativePlace;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public long getEnterTime() {
        return enterTime;
    }

    public void setEnterTime(long enterTime) {
        this.enterTime = enterTime;
    }

    public long getTrainingTime() {
        return trainingTime;
    }

    public void setTrainingTime(long trainingTime) {
        this.trainingTime = trainingTime;
    }

    public long getExitTime() {
        return exitTime;
    }

    public void setExitTime(long exitTime) {
        this.exitTime = exitTime;
    }

    public String getPositionId() {
        return positionId;
    }

    public void setPositionId(String positionId) {
        this.positionId = positionId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getQrcodeId() {
        return qrcodeId;
    }

    public void setQrcodeId(String qrcodeId) {
        this.qrcodeId = qrcodeId;
    }

    public List<PositionBean> getPositionList() {
        return positionList;
    }

    public void setPositionList(List<PositionBean> positionList) {
        this.positionList = positionList;
    }
}
