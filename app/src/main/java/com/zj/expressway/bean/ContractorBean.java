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
 *       Created by HaiJun on 2018/6/11 17:47
 *       工序bean
 */
public class ContractorBean extends DataSupport implements Serializable {
    private String levelId;         // 层级ID
    private String levelName;       // 层级名称
    private String parentId;        // 父ID
    private String folderFlag;      // 是否是文件夹flag 0:不是文件夹 1：是文件夹
    private int processNum;         // 工序数量
    private int finishedNum;        // 已完成工序数量
    private String isFinish;        // 是否已审核完
    private String levelType;       // 质量或安全
    private String haveProcess;     // 是否有子工序 0:有 1：无

    public String getHaveProcess() {
        return haveProcess;
    }

    public void setHaveProcess(String haveProcess) {
        this.haveProcess = haveProcess;
    }

    public String getLevelType() {
        return levelType;
    }

    public void setLevelType(String levelType) {
        this.levelType = levelType;
    }

    public String getIsFinish() {
        return isFinish;
    }

    public void setIsFinish(String isFinish) {
        this.isFinish = isFinish;
    }

    public String getLevelId() {
        return levelId;
    }

    public void setLevelId(String levelId) {
        this.levelId = levelId;
    }

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getFolderFlag() {
        return folderFlag;
    }

    public void setFolderFlag(String folderFlag) {
        this.folderFlag = folderFlag;
    }

    public int getProcessNum() {
        return processNum;
    }

    public void setProcessNum(int processNum) {
        this.processNum = processNum;
    }

    public int getFinishedNum() {
        return finishedNum;
    }

    public void setFinishedNum(int finishedNum) {
        this.finishedNum = finishedNum;
    }
}
