package com.zj.expressway.model;

import com.zj.expressway.bean.PhotosBean;

import java.util.List;

/**
 * Create dell By 2018/6/14 11:27
 */

public class FileModel {
    private FileModel sx_zl_photo;
    private List<PhotosBean> subTableObject;
    private String subTableType;

    public FileModel getSx_zl_photo() {
        return sx_zl_photo;
    }

    public void setSx_zl_photo(FileModel sx_zl_photo) {
        this.sx_zl_photo = sx_zl_photo;
    }

    public List<PhotosBean> getSubTableObject() {
        return subTableObject;
    }

    public void setSubTableObject(List<PhotosBean> subTableObject) {
        this.subTableObject = subTableObject;
    }

    public String getSubTableType() {
        return subTableType;
    }

    public void setSubTableType(String subTableType) {
        this.subTableType = subTableType;
    }
}
