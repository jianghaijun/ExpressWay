package com.zj.expressway.bean;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * Created by HaiJun on 2018/6/11 17:47
 * 层级bean
 */
public class ContractorBean extends DataSupport implements Serializable {
    private String levelId;         // 层级ID
    private String levelName;       // 层级名称
    private String LevelCode;       // 层级code
    private String parentNameAll;   // 名称
    private String parentId;        // 父ID
    private String parentIdAll;        // 父ID
    private String folderFlag;      // 是否是文件夹flag 0:不是文件夹 1：是文件夹
    private String userId;
    private int processNum;         // 工序数量
    private int finishedNum;        // 已完成工序数量
    private boolean isSelect;
    private String isFinish;        // 是否已审核完
    private String levelType;       // 质量或安全
    private String canExpand;     // 是否有子工序 1:有 0：无
    private int isLocalAdd;     // 是否是本地添加 1：是 2：否

    public String getParentIdAll() {
        return parentIdAll;
    }

    public void setParentIdAll(String parentIdAll) {
        this.parentIdAll = parentIdAll;
    }

    public String getLevelCode() {
        return LevelCode;
    }

    public void setLevelCode(String levelCode) {
        LevelCode = levelCode;
    }

    public int getIsLocalAdd() {
        return isLocalAdd;
    }

    public void setIsLocalAdd(int isLocalAdd) {
        this.isLocalAdd = isLocalAdd;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getParentNameAll() {
        return parentNameAll;
    }

    public void setParentNameAll(String parentNameAll) {
        this.parentNameAll = parentNameAll;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public String getCanExpand() {
        return canExpand;
    }

    public void setCanExpand(String canExpand) {
        this.canExpand = canExpand;
    }

    public String getLevelType() {
        return levelType;
    }

    public void setLevelType(String levelType) {
        this.levelType = levelType;
    }

    public String getIsFinish() {
        return isFinish;
    }

    public void setIsFinish(String isFinish) {
        this.isFinish = isFinish;
    }

    public String getLevelId() {
        return levelId;
    }

    public void setLevelId(String levelId) {
        this.levelId = levelId;
    }

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getFolderFlag() {
        return folderFlag;
    }

    public void setFolderFlag(String folderFlag) {
        this.folderFlag = folderFlag;
    }

    public int getProcessNum() {
        return processNum;
    }

    public void setProcessNum(int processNum) {
        this.processNum = processNum;
    }

    public int getFinishedNum() {
        return finishedNum;
    }

    public void setFinishedNum(int finishedNum) {
        this.finishedNum = finishedNum;
    }
}
