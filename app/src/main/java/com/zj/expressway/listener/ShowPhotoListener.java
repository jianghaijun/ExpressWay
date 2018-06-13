package com.zj.expressway.listener;

/**
 * @author Administrator
 * @time 2017/10/11 0011 21:08
 */

public interface ShowPhotoListener {
    void selectWayOrShowPhoto(boolean isShowPhoto, String thumbUrl, String photoUrl, int isUpLoad);
}
