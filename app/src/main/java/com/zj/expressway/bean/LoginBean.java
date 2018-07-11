package com.zj.expressway.bean;

/**
 * Created by HaiJun on 2018/6/11 17:27
 * 登录bean
 */
public class LoginBean {
    private UserInfo userInfo;
    private String token;

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
