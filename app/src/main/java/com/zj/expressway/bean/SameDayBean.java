package com.zj.expressway.bean;

import java.util.List;

/**
 * Created by HaiJun on 2018/6/11 17:50
 * 统计报表
 */
public class SameDayBean {
    private String rootLevelId;
    private String rootLevelName;
    private String totalNum1;
    private String totalNum2;
    private String totalNum3;
    private String totalNum4;
    private String totalNum5;
    private String totalNum6;
    private String month;
    private String roadbedNum;  // 路基
    private String bridgeNum;   // 桥梁
    private String culvertNum;  // 涵洞
    private String waterNum;    // 排水
    private String protectNum;  // 防护
    private String subtotal;  // 总数
    private String operation;
    private String twoLevelId;
    private String totalNumBy;
    private String totalNumZj;
    private String totalNumCj;
    private String twoLevelName;
    private String processName;
    private String levelNameAll;
    private String totalNum;
    private String finishedNum;
    private String unfinishedNum;
    private List<PhotosBean> sxZlPhotoList;
    private List<SameDayBean> historyTotalList;
    private List<SameDayBean> searchTotalList;

    public String getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(String subtotal) {
        this.subtotal = subtotal;
    }

    public String getTotalNum() {
        return totalNum == null ? "" : totalNum;
    }

    public void setTotalNum(String totalNum) {
        this.totalNum = totalNum;
    }

    public String getFinishedNum() {
        return finishedNum == null ? "" : finishedNum;
    }

    public void setFinishedNum(String finishedNum) {
        this.finishedNum = finishedNum;
    }

    public String getUnfinishedNum() {
        return unfinishedNum == null ? "" : unfinishedNum;
    }

    public void setUnfinishedNum(String unfinishedNum) {
        this.unfinishedNum = unfinishedNum;
    }

    public List<SameDayBean> getHistoryTotalList() {
        return historyTotalList;
    }

    public void setHistoryTotalList(List<SameDayBean> historyTotalList) {
        this.historyTotalList = historyTotalList;
    }

    public List<SameDayBean> getSearchTotalList() {
        return searchTotalList;
    }

    public void setSearchTotalList(List<SameDayBean> searchTotalList) {
        this.searchTotalList = searchTotalList;
    }

    public String getLevelNameAll() {
        return levelNameAll;
    }

    public void setLevelNameAll(String levelNameAll) {
        this.levelNameAll = levelNameAll;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public List<PhotosBean> getSxZlPhotoList() {
        return sxZlPhotoList;
    }

    public void setSxZlPhotoList(List<PhotosBean> sxZlPhotoList) {
        this.sxZlPhotoList = sxZlPhotoList;
    }

    public String getTotalNumBy() {
        return totalNumBy;
    }

    public void setTotalNumBy(String totalNumBy) {
        this.totalNumBy = totalNumBy;
    }

    public String getTotalNumZj() {
        return totalNumZj;
    }

    public void setTotalNumZj(String totalNumZj) {
        this.totalNumZj = totalNumZj;
    }

    public String getTotalNumCj() {
        return totalNumCj;
    }

    public void setTotalNumCj(String totalNumCj) {
        this.totalNumCj = totalNumCj;
    }

    public String getTwoLevelId() {
        return twoLevelId;
    }

    public void setTwoLevelId(String twoLevelId) {
        this.twoLevelId = twoLevelId;
    }

    public String getTwoLevelName() {
        return twoLevelName;
    }

    public void setTwoLevelName(String twoLevelName) {
        this.twoLevelName = twoLevelName;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getRootLevelId() {
        return rootLevelId;
    }

    public void setRootLevelId(String rootLevelId) {
        this.rootLevelId = rootLevelId;
    }

    public String getRootLevelName() {
        return rootLevelName;
    }

    public void setRootLevelName(String rootLevelName) {
        this.rootLevelName = rootLevelName;
    }

    public String getTotalNum1() {
        return totalNum1 == null ? "0" : totalNum1;
    }

    public void setTotalNum1(String totalNum1) {
        this.totalNum1 = totalNum1;
    }

    public String getTotalNum2() {
        return totalNum2 == null ? "0" : totalNum2;
    }

    public void setTotalNum2(String totalNum2) {
        this.totalNum2 = totalNum2;
    }

    public String getTotalNum3() {
        return totalNum3 == null ? "0" : totalNum3;
    }

    public void setTotalNum3(String totalNum3) {
        this.totalNum3 = totalNum3;
    }

    public String getTotalNum4() {
        return totalNum4 == null ? "0" : totalNum4;
    }

    public void setTotalNum4(String totalNum4) {
        this.totalNum4 = totalNum4;
    }

    public String getTotalNum5() {
        return totalNum5 == null ? "0" : totalNum5;
    }

    public void setTotalNum5(String totalNum5) {
        this.totalNum5 = totalNum5;
    }

    public String getTotalNum6() {
        return totalNum6 == null ? "0" : totalNum6;
    }

    public void setTotalNum6(String totalNum6) {
        this.totalNum6 = totalNum6;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getRoadbedNum() {
        return roadbedNum;
    }

    public void setRoadbedNum(String roadbedNum) {
        this.roadbedNum = roadbedNum;
    }

    public String getBridgeNum() {
        return bridgeNum;
    }

    public void setBridgeNum(String bridgeNum) {
        this.bridgeNum = bridgeNum;
    }

    public String getCulvertNum() {
        return culvertNum;
    }

    public void setCulvertNum(String culvertNum) {
        this.culvertNum = culvertNum;
    }

    public String getWaterNum() {
        return waterNum;
    }

    public void setWaterNum(String waterNum) {
        this.waterNum = waterNum;
    }

    public String getProtectNum() {
        return protectNum;
    }

    public void setProtectNum(String protectNum) {
        this.protectNum = protectNum;
    }
}
