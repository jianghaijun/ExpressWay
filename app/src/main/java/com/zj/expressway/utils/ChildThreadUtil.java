package com.zj.expressway.utils;

import android.app.Activity;
import android.content.Intent;

import com.zj.expressway.activity.LoginActivity;
import com.zj.expressway.listener.ILoadCallback;

import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Create dell By 2018/6/14 21:41
 */

public class ChildThreadUtil {

    /**
     * 弹出消息---不隐藏加载框
     * @param mContext
     * @param msg
     */
    public static void toastMsgNoHidden(final Activity mContext, final String msg) {
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtil.showShort(mContext, msg);
            }
        });
    }

    /**
     * 弹出消息---隐藏加载框
     * @param mContext
     * @param msg
     */
    public static void toastMsgHidden(final Activity mContext, final String msg) {
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LoadingUtils.hideLoading();
                ToastUtil.showShort(mContext, msg);
            }
        });
    }

    /**
     * 关闭加载
     * @param mContext
     * @param callback
     */
    public static void closeLoading(Activity mContext, final ILoadCallback callback) {
        if (callback != null) {
            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    callback.onFailure();
                }
            });
        }
    }

    /**
     * 弹出消息(判断token是否过期)---隐藏加载框
     * @param mContext
     * @param msg   提示信息
     * @param code  返回code码
     */
    public static void checkTokenHidden(final Activity mContext, final String msg, final int code) {
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LoadingUtils.hideLoading();
                switch (code) {
                    case 3003:
                    case 3004:
                        // Token异常重新登录
                        ToastUtil.showLong(mContext, "Token过期请重新登录！");
                        SpUtil.put(mContext, ConstantsUtil.IS_LOGIN_SUCCESSFUL, false);
                        ScreenManagerUtil.popAllActivityExceptOne();
                        mContext.startActivity(new Intent(mContext, LoginActivity.class));
                        break;
                    default:
                        ToastUtil.showLong(mContext, msg);
                        break;
                }
            }
        });
    }

    /**
     * 获取Request
     * @param mContext
     * @param strUrl
     * @param param
     * @return
     */
    public static Request getRequest(Activity mContext, String strUrl, String param) {
        RequestBody requestBody = RequestBody.create(ConstantsUtil.JSON, param);
        Request request = new Request.Builder()
                .url(ConstantsUtil.BASE_URL + strUrl)
                .addHeader("token", (String) SpUtil.get(mContext, ConstantsUtil.TOKEN, ""))
                .post(requestBody)
                .build();
        return request;
    }
}
