package com.zj.expressway.InJavaScript;

import android.app.Activity;
import android.content.Context;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.zj.expressway.activity.MainActivity;
import com.zj.expressway.base.BaseInJavaScript;
import com.zj.expressway.utils.ConstantsUtil;
import com.zj.expressway.utils.ScreenManagerUtil;
import com.zj.expressway.utils.SpUtil;

/**
 * Create dell By 2018/7/9 9:37
 */

public class MailListInJavaScript extends BaseInJavaScript {
    private Activity mContext;
    private WebView mWebView = null;

    public MailListInJavaScript(Context mContext, WebView webView) {
        super(mContext);
        this.mWebView = webView;
        this.mContext = (Activity) mContext;
    }

    @JavascriptInterface
    public String getLoginToken() {
        String token = (String) SpUtil.get(mContext, ConstantsUtil.TOKEN, "");
        return token;
    }

    @JavascriptInterface
    public void closeActivity() {
        ScreenManagerUtil.popAllActivityExceptOne(MainActivity.class);
    }
}
