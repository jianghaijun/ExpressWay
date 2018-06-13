package com.zj.expressway.bean;

import java.util.ArrayList;
import java.util.List;

import cn.hutool.core.util.StrUtil;

/**
 * Create dell By 2018/6/12 11:22
 */

public class PersonnelBean {
    private String value;
    private String label;
    private String title;
    private String type;
    private String valuePid;
    private List<PersonnelBean> children;

    public String getValue() {
        return StrUtil.isEmpty(value) ? "" : value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getLabel() {
        return StrUtil.isEmpty(label) ? "" : label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getTitle() {
        return StrUtil.isEmpty(title) ? "" : title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return StrUtil.isEmpty(type) ? "" : type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValuePid() {
        return StrUtil.isEmpty(valuePid) ? "" : valuePid;
    }

    public void setValuePid(String valuePid) {
        this.valuePid = valuePid;
    }

    public List<PersonnelBean> getChildren() {
        return children == null ? new ArrayList<PersonnelBean>() : children;
    }

    public void setChildren(List<PersonnelBean> children) {
        this.children = children;
    }
}
