package com.zj.expressway.model;

import com.zj.expressway.bean.PhotosBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Create dell By 2018/6/14 11:27
 */

public class FileModel {
    private FileModel zxHwGxAttachment;
    private FileModel zxHwZlAttachment;
    private FileModel zxHwAqAttachment;
    private List<PhotosBean> subTableDataObject;
    private String subTableType;

    public FileModel getZxHwZlAttachment() {
        return zxHwZlAttachment == null ? new FileModel() : zxHwZlAttachment;
    }

    public void setZxHwZlAttachment(FileModel zxHwZlAttachment) {
        this.zxHwZlAttachment = zxHwZlAttachment;
    }

    public FileModel getZxHwAqAttachment() {
        return zxHwAqAttachment == null ? new FileModel() : zxHwAqAttachment;
    }

    public void setZxHwAqAttachment(FileModel zxHwAqAttachment) {
        this.zxHwAqAttachment = zxHwAqAttachment;
    }

    public FileModel getZxHwGxAttachment() {
        return zxHwGxAttachment == null ? new FileModel() : zxHwGxAttachment;
    }

    public void setZxHwGxAttachment(FileModel zxHwGxAttachment) {
        this.zxHwGxAttachment = zxHwGxAttachment;
    }

    public List<PhotosBean> getSubTableObject() {
        return subTableDataObject == null ? new ArrayList<PhotosBean>() : subTableDataObject;
    }

    public void setSubTableObject(List<PhotosBean> subTableDataObject) {
        this.subTableDataObject = subTableDataObject;
    }

    public String getSubTableType() {
        return subTableType;
    }

    public void setSubTableType(String subTableType) {
        this.subTableType = subTableType;
    }
}
