package com.zj.expressway.bean;

/**
 * Created by HaiJun on 2018/6/11 17:52
 * 人员选择bean
 */
public class SelectAuditorsBean {
    private String userId;
    private String realName;
    private String checkLevelId;
    private String checkLevelName;
    private boolean isSelect = false;

    public String getCheckLevelId() {
        return checkLevelId;
    }

    public void setCheckLevelId(String checkLevelId) {
        this.checkLevelId = checkLevelId;
    }

    public String getCheckLevelName() {
        return checkLevelName;
    }

    public void setCheckLevelName(String checkLevelName) {
        this.checkLevelName = checkLevelName;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
