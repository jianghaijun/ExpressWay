package com.zj.expressway.model;

import com.zj.expressway.base.BaseModel;
import com.zj.expressway.bean.MainPageBean;

import java.util.List;

/**
 * Create dell By 2018/7/11 11:21
 */

public class MainPageModel extends BaseModel {
    private String todayFinishNum;
    private String todoCount;
    private String hasTodoCount;
    private MainPageModel data;
    private List<MainPageBean> newsList;
    private List<MainPageBean> viewList;

    public MainPageModel getData() {
        return data;
    }

    public void setData(MainPageModel data) {
        this.data = data;
    }

    public String getTodayFinishNum() {
        return todayFinishNum;
    }

    public void setTodayFinishNum(String todayFinishNum) {
        this.todayFinishNum = todayFinishNum;
    }

    public String getTodoCount() {
        return todoCount;
    }

    public void setTodoCount(String todoCount) {
        this.todoCount = todoCount;
    }

    public String getHasTodoCount() {
        return hasTodoCount;
    }

    public void setHasTodoCount(String hasTodoCount) {
        this.hasTodoCount = hasTodoCount;
    }

    public List<MainPageBean> getNewsList() {
        return newsList;
    }

    public void setNewsList(List<MainPageBean> newsList) {
        this.newsList = newsList;
    }

    public List<MainPageBean> getViewList() {
        return viewList;
    }

    public void setViewList(List<MainPageBean> viewList) {
        this.viewList = viewList;
    }
}
