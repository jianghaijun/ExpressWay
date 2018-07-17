package com.zj.expressway.bean;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * Create dell By 2018/7/11 11:23
 */

public class MainPageBean extends DataSupport implements Serializable {
    private String viewId;
    private String viewSummary;
    private String fileUrl;

    public String getViewId() {
        return viewId;
    }

    public void setViewId(String viewId) {
        this.viewId = viewId;
    }

    public String getViewSummary() {
        return viewSummary == null ? "" : viewSummary;
    }

    public void setViewSummary(String viewSummary) {
        this.viewSummary = viewSummary;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }
}
