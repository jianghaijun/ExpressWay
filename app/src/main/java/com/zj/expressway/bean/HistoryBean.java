package com.zj.expressway.bean;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * Create dell By 2018/6/14 14:46
 */

public class HistoryBean extends DataSupport implements Serializable {
    private String title;
    private String realName;
    private String nodeName;
    private long actionTime;
    private String doTimeShow;
    private String historyFlag;
    private String processId;

    public String getHistoryFlag() {
        return historyFlag;
    }

    public void setHistoryFlag(String historyFlag) {
        this.historyFlag = historyFlag;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

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
