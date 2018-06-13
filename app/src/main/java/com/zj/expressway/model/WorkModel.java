package com.zj.expressway.model;

import com.zj.expressway.base.BaseModel;

/**
 * Create dell By 2018/6/13 16:41
 */

public class WorkModel extends BaseModel {
    private WorkFlowModel data;

    public WorkFlowModel getData() {
        return data;
    }

    public void setData(WorkFlowModel data) {
        this.data = data;
    }
}
