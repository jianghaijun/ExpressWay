package com.zj.expressway.model;


import com.zj.expressway.bean.PersonnelBean;

/**
 * 人员类别
 */
public class PersonnelListModel {
    private boolean success;
    private String message;
    private int code;
    private PersonnelBean data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public PersonnelBean getData() {
        return data;
    }

    public void setData(PersonnelBean data) {
        this.data = data;
    }
}
