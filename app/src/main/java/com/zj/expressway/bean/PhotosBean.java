package com.zj.expressway.bean;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

public class PhotosBean extends DataSupport implements Serializable {
    private int isToBeUpLoad = -1;
    private int isNewAdd = -1;
    private String userId;
    private String modify_user_name;
    private String create_user_name;
    private String process_id;
    private String thumb_path;
    private String del_flag;
    private String work_id;
    private String photo_id;
    private String photo_name;
    private long create_time;
    private String photo_address;
    private String longitude;
    private String latitude;
    private String location;
    private String modify_user;
    private long modify_time;
    private String photo_desc;
    private String role_flag;
    private String check_flag;
    private String photo_type;
    private String create_user;
    private String other_id;

    public int getIsToBeUpLoad() {
        return isToBeUpLoad;
    }

    public void setIsToBeUpLoad(int isToBeUpLoad) {
        this.isToBeUpLoad = isToBeUpLoad;
    }

    public int getIsNewAdd() {
        return isNewAdd;
    }

    public void setIsNewAdd(int isNewAdd) {
        this.isNewAdd = isNewAdd;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getModify_user_name() {
        return modify_user_name;
    }

    public void setModify_user_name(String modify_user_name) {
        this.modify_user_name = modify_user_name;
    }

    public String getCreate_user_name() {
        return create_user_name;
    }

    public void setCreate_user_name(String create_user_name) {
        this.create_user_name = create_user_name;
    }

    public String getProcess_id() {
        return process_id;
    }

    public void setProcess_id(String process_id) {
        this.process_id = process_id;
    }

    public String getThumb_path() {
        return thumb_path;
    }

    public void setThumb_path(String thumb_path) {
        this.thumb_path = thumb_path;
    }

    public String getDel_flag() {
        return del_flag;
    }

    public void setDel_flag(String del_flag) {
        this.del_flag = del_flag;
    }

    public String getWork_id() {
        return work_id;
    }

    public void setWork_id(String work_id) {
        this.work_id = work_id;
    }

    public String getPhoto_id() {
        return photo_id;
    }

    public void setPhoto_id(String photo_id) {
        this.photo_id = photo_id;
    }

    public String getPhoto_name() {
        return photo_name;
    }

    public void setPhoto_name(String photo_name) {
        this.photo_name = photo_name;
    }

    public long getCreate_time() {
        return create_time;
    }

    public void setCreate_time(long create_time) {
        this.create_time = create_time;
    }

    public String getPhoto_address() {
        return photo_address;
    }

    public void setPhoto_address(String photo_address) {
        this.photo_address = photo_address;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getModify_user() {
        return modify_user;
    }

    public void setModify_user(String modify_user) {
        this.modify_user = modify_user;
    }

    public long getModify_time() {
        return modify_time;
    }

    public void setModify_time(long modify_time) {
        this.modify_time = modify_time;
    }

    public String getPhoto_desc() {
        return photo_desc;
    }

    public void setPhoto_desc(String photo_desc) {
        this.photo_desc = photo_desc;
    }

    public String getRole_flag() {
        return role_flag;
    }

    public void setRole_flag(String role_flag) {
        this.role_flag = role_flag;
    }

    public String getCheck_flag() {
        return check_flag;
    }

    public void setCheck_flag(String check_flag) {
        this.check_flag = check_flag;
    }

    public String getPhoto_type() {
        return photo_type;
    }

    public void setPhoto_type(String photo_type) {
        this.photo_type = photo_type;
    }

    public String getCreate_user() {
        return create_user;
    }

    public void setCreate_user(String create_user) {
        this.create_user = create_user;
    }

    public String getOther_id() {
        return other_id;
    }

    public void setOther_id(String other_id) {
        this.other_id = other_id;
    }
}
