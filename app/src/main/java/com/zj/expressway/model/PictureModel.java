package com.zj.expressway.model;

import com.zj.expressway.bean.PictureBean;

import java.util.List;

/**
 *                     _ooOoo_
 *                    o8888888o
 *                    88" . "88
 *                    (| -_- |)
 *                    O\  =  /O
 *                 ____/`---'\____
 *               .'  \\|     |//  `.
 *              /  \\|||  :  |||//  \
 *             /  _||||| -:- |||||-  \
 *             |   | \\\  -  /// |   |
 *             | \_|  ''\---/''  |   |
 *             \  .-\__  `-`  ___/-. /
 *           ___`. .'  /--.--\  `. . __
 *        ."" '<  `.___\_<|>_/___.'  >'"".
 *       | | :  `- \`.;`\ _ /`;.`/ - ` : | |
 *       \  \ `-.   \_ __\ /__ _/   .-` /  /
 * ======`-.____`-.___\_____/___.-`____.-'======
 *                     `=---='
 * ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
 * 			   佛祖保佑       永无BUG
 *       Created by HaiJun on 2018/6/11 17:35
 *       删除照片、审核照片model
 */
public class PictureModel {
    private String selectUserId;
    private String rootLevelId;
    private String processId;
    private String pushMessage;
    private String dismissal;
    private String stateFlag;
    private String recordType;
    private List<PictureBean> sxZlPhotoList;

    public String getPushMessage() {
        return pushMessage;
    }

    public void setPushMessage(String pushMessage) {
        this.pushMessage = pushMessage;
    }

    public String getStateFlag() {
        return stateFlag;
    }

    public void setStateFlag(String stateFlag) {
        this.stateFlag = stateFlag;
    }

    public String getDismissal() {
        return dismissal;
    }

    public void setDismissal(String dismissal) {
        this.dismissal = dismissal;
    }

    public String getRecordType() {
        return recordType;
    }

    public void setRecordType(String recordType) {
        this.recordType = recordType;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public String getSelectUserId() {
        return selectUserId;
    }

    public void setSelectUserId(String selectUserId) {
        this.selectUserId = selectUserId;
    }

    public String getRootLevelId() {
        return rootLevelId;
    }

    public void setRootLevelId(String rootLevelId) {
        this.rootLevelId = rootLevelId;
    }

    public List<PictureBean> getSxZlPhotoList() {
        return sxZlPhotoList;
    }

    public void setSxZlPhotoList(List<PictureBean> sxZlPhotoList) {
        this.sxZlPhotoList = sxZlPhotoList;
    }
}
