package com.zj.expressway.base;

import android.content.Context;
import android.content.Intent;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.zj.expressway.activity.LoginActivity;
import com.zj.expressway.utils.ConstantsUtil;
import com.zj.expressway.utils.ScreenManagerUtil;
import com.zj.expressway.utils.SpUtil;
import com.zj.expressway.utils.ToastUtil;

/**
 * h5调用java方法
 */
public class BaseInJavaScript {
    protected Context mContext;

    public BaseInJavaScript(Context context) {
        mContext = context;
    }

    @JavascriptInterface
    public void showToast(String str) {
        ToastUtil.showShort(mContext, str);
    }

    @JavascriptInterface
    public void showToast(String str, int time) {
        ToastUtil.show(mContext, str, time);
    }

    @JavascriptInterface
    public void tokenBeOverdue(String msg) {
        ToastUtil.showLong(mContext, msg);
        SpUtil.put(mContext, ConstantsUtil.IS_LOGIN_SUCCESSFUL, false);
        ScreenManagerUtil.popAllActivityExceptOne();
        mContext.startActivity(new Intent(mContext, LoginActivity.class));
    }

}
