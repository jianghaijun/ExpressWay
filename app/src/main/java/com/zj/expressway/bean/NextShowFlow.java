package com.zj.expressway.bean;

/**
 * Create dell By 2018/6/13 17:13
 */

public class NextShowFlow {
    private String nextNodeId;
    private String nextNodeName;
    private String nextAuthorMapkey;
    private boolean nextDoneNode;

    public String getNextNodeId() {
        return nextNodeId;
    }

    public void setNextNodeId(String nextNodeId) {
        this.nextNodeId = nextNodeId;
    }

    public String getNextNodeName() {
        return nextNodeName;
    }

    public void setNextNodeName(String nextNodeName) {
        this.nextNodeName = nextNodeName;
    }

    public String getNextAuthorMapkey() {
        return nextAuthorMapkey;
    }

    public void setNextAuthorMapkey(String nextAuthorMapkey) {
        this.nextAuthorMapkey = nextAuthorMapkey;
    }

    public boolean isNextDoneNode() {
        return nextDoneNode;
    }

    public void setNextDoneNode(boolean nextDoneNode) {
        this.nextDoneNode = nextDoneNode;
    }
}
