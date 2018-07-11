package com.zj.expressway.bean;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * Created by HaiJun on 2018/6/11 17:49
 * 用户级别bean
 */
public class UserLevelBean extends DataSupport implements Serializable {
    private String userExtendId;
    private String realName;
    private String rootLevelId;

    public String getUserExtendId() {
        return userExtendId;
    }

    public void setUserExtendId(String userExtendId) {
        this.userExtendId = userExtendId;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getRootLevelId() {
        return rootLevelId;
    }

    public void setRootLevelId(String rootLevelId) {
        this.rootLevelId = rootLevelId;
    }
}
