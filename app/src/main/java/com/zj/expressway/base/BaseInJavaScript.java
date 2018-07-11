package com.zj.expressway.base;

import android.content.Context;
import android.webkit.JavascriptInterface;

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

}
