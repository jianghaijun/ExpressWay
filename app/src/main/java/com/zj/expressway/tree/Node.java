package com.zj.expressway.tree;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 树节点
 */
public class Node {
    private Node parent;// 父节点
    private List<Node> children = new ArrayList<>();    // 子节点
    private String levelName;                           // 层级名称
    private String levelId;                             // 层级ID
    private String parentId;                            // 父ID
    private String folderFlag;                          // 是否是文件夹flag 0:不是文件夹 1：是文件夹
    private String isFinish;                            // 是否已完成
    private boolean isCanClick;                         // 是否有子工序
    private boolean isLoading = false;                  // 是否已经加载
    private boolean isExpanded = true;                  // 是否处于展开状态
    private boolean isChoice = false;                   // 是否选中

    public boolean isChoice() {
        return isChoice;
    }

    public void setChoice(boolean choice) {
        isChoice = choice;
    }

    public boolean isCanClick() {
        return isCanClick;
    }

    public void setCanClick(boolean canClick) {
        isCanClick = canClick;
    }

    public String getIsFinish() {
        return TextUtils.isEmpty(isFinish) ? "" : isFinish;
    }

    public void setIsFinish(String isFinish) {
        this.isFinish = isFinish;
    }

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public String getLevelId() {
        return levelId;
    }

    public void setLevelId(String levelId) {
        this.levelId = levelId;
    }

    public String getFolderFlag() {
        return folderFlag;
    }

    public void setFolderFlag(String folderFlag) {
        this.folderFlag = folderFlag;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    /**
     * 设置父节点
     *
     * @param node
     */
    public void setParent(Node node) {
        this.parent = node;
    }

    /**
     * 获得父节点
     *
     * @return
     */
    public Node getParent() {
        return this.parent;
    }


    public void setChildren(List<Node> children) {
        this.children = children;
    }

    /**
     * 是否根节点
     *
     * @return
     */
    public boolean isRoot() {
        return parent == null ? true : false;
    }

    /**
     * 获得子节点
     *
     * @return
     */
    public List<Node> getChildren() {
        return this.children == null ? new ArrayList<Node>() : this.children;
    }

    /**
     * 添加子节点
     *
     * @param node
     */
    public void add(Node node) {
        if (!children.contains(node)) {
            children.add(node);
        }
    }

    /**
     * 清除所有子节点
     */
    public void clear() {
        children.clear();
    }

    /**
     * 删除一个子节点
     *
     * @param node
     */
    public void remove(Node node) {
        if (!children.contains(node)) {
            children.remove(node);
        }
    }

    /**
     * 删除指定位置的子节点
     *
     * @param location
     */
    public void remove(int location) {
        children.remove(location);
    }

    /**
     * 获得节点的级数,根节点为0
     *
     * @return
     */
    public int getLevel() {
        return parent == null ? 0 : parent.getLevel() + 1;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    /**
     * 是否叶节点,即没有子节点的节点
     *
     * @return
     */
    public boolean isLeaf() {
        return children.size() < 1 ? true : false;
    }

    /**
     * 当前节点是否处于展开状态
     *
     * @return
     */
    public boolean isExpanded() {
        return isExpanded;
    }

    /**
     * 设置节点展开状态
     *
     * @return
     */
    public void setExpanded(boolean isExpanded) {
        this.isExpanded = isExpanded;
    }

    /**
     * 递归判断父节点是否处于折叠状态,有一个父节点折叠则认为是折叠状态
     *
     * @return
     */
    public boolean isParentCollapsed() {
        if (parent == null)
            return !isExpanded;
        if (!parent.isExpanded())
            return true;
        return parent.isParentCollapsed();
    }
}
