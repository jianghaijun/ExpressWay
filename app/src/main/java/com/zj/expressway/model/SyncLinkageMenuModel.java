package com.zj.expressway.model;

import com.zj.expressway.base.BaseModel;

import java.util.List;

/**
 * Create dell By 2018/7/2 13:18
 */

public class SyncLinkageMenuModel extends BaseModel {
    private List<SyncLinkageMenuSecondModel> data;

    public List<SyncLinkageMenuSecondModel> getData() {
        return data;
    }

    public void setData(List<SyncLinkageMenuSecondModel> data) {
        this.data = data;
    }
}
