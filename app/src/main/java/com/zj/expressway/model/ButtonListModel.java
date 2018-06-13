package com.zj.expressway.model;

import com.zj.expressway.bean.NextShowFlow;

import java.util.List;

/**
 * Create dell By 2018/6/13 17:10
 */

public class ButtonListModel {
    private String buttonId;
    private String buttonName;
    private List<NextShowFlow> nextShowFlowInfoList;

    public String getButtonId() {
        return buttonId;
    }

    public void setButtonId(String buttonId) {
        this.buttonId = buttonId;
    }

    public String getButtonName() {
        return buttonName;
    }

    public void setButtonName(String buttonName) {
        this.buttonName = buttonName;
    }

    public List<NextShowFlow> getNextShowFlowInfoList() {
        return nextShowFlowInfoList;
    }

    public void setNextShowFlowInfoList(List<NextShowFlow> nextShowFlowInfoList) {
        this.nextShowFlowInfoList = nextShowFlowInfoList;
    }
}
