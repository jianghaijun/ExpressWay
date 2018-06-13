package com.zj.expressway.bean;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

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
 *       Created by HaiJun on 2018/6/11 17:53
 *       工序bean
 */
public class WorkingBean extends DataSupport implements Serializable {
    private String processId;        // 工序ID
    private String processName;      // 工序名称
    private String processCode;      // 编码
    private String photoContent;     // 拍照内容
    private String photoDistance;    // 距离及角度
    private String photoNumber;      // 拍照最少张数
    private String longitude;        // 经度
    private String latitude;         // 纬度
    private String location;         // 地理位置
    private String levelNameAll;     // 工序部位
    private String levelId;          // 工序部位
    private long enterTime;          // 录入时间
    private String actualNumber;     // 实际照片数量
    private String photoerAll;       // 拍照者all
    private String checkNameAll;     // 确认者姓名all
    private String createUser;       // 拍照者
    private String processState;     // 工序状态
    private String dismissal;        // 驳回原因
    private long sendTime;
    private String content;
    private String taskId;
    private String personalNum;
    private String systemNum;
    private String createUserName;
    private String isRead;
    private String ext1;        // 层厚
    private String ext2;        // 层厚
    private String ext3;        // 层厚
    private String ext4;        // 层厚
    private String ext5;        // 层厚
    private String ext6;        // 层厚
    private String ext7;        // 层厚
    private String ext8;        // 层厚
    private String ext9;        // 层厚
    private String ext10;        // 层厚
    private String canCheck;

    public String getCanCheck() {
        return canCheck;
    }

    public void setCanCheck(String canCheck) {
        this.canCheck = canCheck;
    }

    public String getIsRead() {
        return isRead;
    }

    public void setIsRead(String isRead) {
        this.isRead = isRead;
    }

    public String getPersonalNum() {
        return personalNum;
    }

    public void setPersonalNum(String personalNum) {
        this.personalNum = personalNum;
    }

    public String getSystemNum() {
        return systemNum;
    }

    public void setSystemNum(String systemNum) {
        this.systemNum = systemNum;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    public long getSendTime() {
        return sendTime;
    }

    public void setSendTime(long sendTime) {
        this.sendTime = sendTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getExt1() {
        return ext1 == null ? "" : ext1;
    }

    public void setExt1(String ext1) {
        this.ext1 = ext1;
    }

    public String getExt2() {
        return ext2 == null ? "" : ext2;
    }

    public void setExt2(String ext2) {
        this.ext2 = ext2;
    }

    public String getExt3() {
        return ext3 == null ? "" : ext3;
    }

    public void setExt3(String ext3) {
        this.ext3 = ext3;
    }

    public String getExt4() {
        return ext4 == null ? "" : ext4;
    }

    public void setExt4(String ext4) {
        this.ext4 = ext4;
    }

    public String getExt5() {
        return ext5 == null ? "" : ext5;
    }

    public void setExt5(String ext5) {
        this.ext5 = ext5;
    }

    public String getExt6() {
        return ext6 == null ? "" : ext6;
    }

    public void setExt6(String ext6) {
        this.ext6 = ext6;
    }

    public String getExt7() {
        return ext7 == null ? "" : ext7;
    }

    public void setExt7(String ext7) {
        this.ext7 = ext7;
    }

    public String getExt8() {
        return ext8 == null ? "" : ext8;
    }

    public void setExt8(String ext8) {
        this.ext8 = ext8;
    }

    public String getExt9() {
        return ext9 == null ? "" : ext9;
    }

    public void setExt9(String ext9) {
        this.ext9 = ext9;
    }

    public String getExt10() {
        return ext10 == null ? "" : ext10;
    }

    public void setExt10(String ext10) {
        this.ext10 = ext10;
    }

    public String getDismissal() {
        return dismissal == null ? "" : dismissal;
    }

    public void setDismissal(String dismissal) {
        this.dismissal = dismissal;
    }

    public String getPhotoerAll() {
        return photoerAll == null ? "" : photoerAll;
    }

    public void setPhotoerAll(String photoerAll) {
        this.photoerAll = photoerAll;
    }

    public String getLevelId() {
        return levelId == null ? "" : levelId;
    }

    public void setLevelId(String levelId) {
        this.levelId = levelId;
    }

    public String getProcessId() {
        return processId == null ? "" : processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public String getProcessName() {
        return processName == null ? "" : processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public String getProcessCode() {
        return processCode == null ? "" : processCode;
    }

    public void setProcessCode(String processCode) {
        this.processCode = processCode;
    }

    public String getPhotoContent() {
        return photoContent == null ? "" : photoContent;
    }

    public void setPhotoContent(String photoContent) {
        this.photoContent = photoContent;
    }

    public String getPhotoDistance() {
        return photoDistance == null ? "" : photoDistance;
    }

    public void setPhotoDistance(String photoDistance) {
        this.photoDistance = photoDistance;
    }

    public String getPhotoNumber() {
        return photoNumber == null ? "" : photoNumber;
    }

    public void setPhotoNumber(String photoNumber) {
        this.photoNumber = photoNumber;
    }

    public String getLongitude() {
        return longitude == null ? "" : longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude == null ? "" : latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLocation() {
        return location == null ? "" : location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLevelNameAll() {
        return levelNameAll == null ? "" : levelNameAll;
    }

    public void setLevelNameAll(String levelNameAll) {
        this.levelNameAll = levelNameAll;
    }

    public long getEnterTime() {
        return enterTime;
    }

    public void setEnterTime(long enterTime) {
        this.enterTime = enterTime;
    }

    public String getActualNumber() {
        return actualNumber == null ? "" : actualNumber;
    }

    public void setActualNumber(String actualNumber) {
        this.actualNumber = actualNumber;
    }

    public String getCheckNameAll() {
        return checkNameAll == null ? "" : checkNameAll;
    }

    public void setCheckNameAll(String checkNameAll) {
        this.checkNameAll = checkNameAll;
    }

    public String getCreateUser() {
        return createUser == null ? "" : createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getProcessState() {
        return processState == null ? "" : processState;
    }

    public void setProcessState(String processState) {
        this.processState = processState;
    }
}
