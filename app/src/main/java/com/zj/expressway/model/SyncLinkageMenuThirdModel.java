package com.zj.expressway.model;

import com.zj.expressway.bean.SyncLinkageMenuBean;

import java.util.List;

/**
 * Create dell By 2018/7/2 13:33
 */

public class SyncLinkageMenuThirdModel extends SyncLinkageMenuBean {
    private List<SyncLinkageMenuBean> thirdLevelList;

    public List<SyncLinkageMenuBean> getThirdLevelList() {
        return thirdLevelList;
    }

    public void setThirdLevelList(List<SyncLinkageMenuBean> thirdLevelList) {
        this.thirdLevelList = thirdLevelList;
    }
}
