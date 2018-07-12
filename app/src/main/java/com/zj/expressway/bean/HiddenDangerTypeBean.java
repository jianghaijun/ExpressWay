package com.zj.expressway.bean;

/**
 * Create dell By 2018/7/9 15:53
 */

public class HiddenDangerTypeBean {
    private String typeTitle;
    private int typeId;
    private boolean isSelect;

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public String getTypeTitle() {
        return typeTitle;
    }

    public void setTypeTitle(String typeTitle) {
        this.typeTitle = typeTitle;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }
}
