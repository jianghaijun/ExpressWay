package com.zj.expressway.listener;

/**
 * @author Administrator
 * @time 2017/10/11 0011 21:08
 */

public interface FileInfoListener {
    void fileInfo(String engineeringName, String rootNodeName, String parentNodeName, String nodeName, boolean isUploadNow);
}
