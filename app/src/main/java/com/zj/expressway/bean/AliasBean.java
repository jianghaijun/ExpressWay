package com.zj.expressway.bean;

import java.util.List;

/**
 * Created by HaiJun on 2018/6/11 17:18
 * 别名bean
 */
public class AliasBean {
    private String userType;
    private String roleFlag;
    private List<UserLevelBean> sxZlUserExtendList; // 用户级别

    public List<UserLevelBean> getSxZlUserExtendList() {
        return sxZlUserExtendList;
    }

    public void setSxZlUserExtendList(List<UserLevelBean> sxZlUserExtendList) {
        this.sxZlUserExtendList = sxZlUserExtendList;
    }

    public String getRoleFlag() {
        return roleFlag;
    }

    public void setRoleFlag(String roleFlag) {
        this.roleFlag = roleFlag;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
}
