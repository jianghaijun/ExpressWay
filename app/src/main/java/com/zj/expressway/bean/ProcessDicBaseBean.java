package com.zj.expressway.bean;

import java.util.List;

/**
 * Create dell By 2018/7/2 11:17
 */

public class ProcessDicBaseBean extends ProcessDictionaryBean {
    private List<ProcessDictionaryBean> gxDictionaryList;

    public List<ProcessDictionaryBean> getGxDictionaryList() {
        return gxDictionaryList;
    }

    public void setGxDictionaryList(List<ProcessDictionaryBean> gxDictionaryList) {
        this.gxDictionaryList = gxDictionaryList;
    }
}
