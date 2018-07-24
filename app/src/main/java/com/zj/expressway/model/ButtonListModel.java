package com.zj.expressway.model;

import com.zj.expressway.bean.NextShowFlow;

import java.util.List;

/**
 * Create dell By 2018/6/13 17:10
 */

public class ButtonListModel {
    private String buttonId;
    private String buttonName;
    private String buttonClass;
    private String buttonFun;
    private String icon;
    private List<NextShowFlow> nextShowFlowInfoList;

    public String getButtonClass() {
        return buttonClass;
    }

    public void setButtonClass(String buttonClass) {
        this.buttonClass = buttonClass;
    }

    public String getButtonFun() {
        return buttonFun;
    }

    public void setButtonFun(String buttonFun) {
        this.buttonFun = buttonFun;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

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
