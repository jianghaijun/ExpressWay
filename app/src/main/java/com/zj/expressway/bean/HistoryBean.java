package com.zj.expressway.bean;

/**
 * Create dell By 2018/6/14 14:46
 */

public class HistoryBean {
    /**
     * title : 路基第一分部→路基工程→路基→填方→K0-260-K0-241→1层→填方
     * realName : 邵军
     * nodeName : 开始
     * actionTime : 1528958369000
     * doTimeShow : 0 天 0 时 0 分
     */
    private String title;
    private String realName;
    private String nodeName;
    private long actionTime;
    private String doTimeShow;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public long getActionTime() {
        return actionTime;
    }

    public void setActionTime(long actionTime) {
        this.actionTime = actionTime;
    }

    public String getDoTimeShow() {
        return doTimeShow;
    }

    public void setDoTimeShow(String doTimeShow) {
        this.doTimeShow = doTimeShow;
    }
}
