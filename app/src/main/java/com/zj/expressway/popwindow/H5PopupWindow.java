package com.zj.expressway.popwindow;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.widget.PopupWindow;

import com.zj.expressway.InJavaScript.H5JavaScript;
import com.zj.expressway.R;
import com.zj.expressway.bean.WorkingBean;
import com.zj.expressway.listener.PromptListener;
import com.zj.expressway.utils.WebViewSettingUtil;

import org.xutils.common.util.DensityUtil;


public class H5PopupWindow extends PopupWindow {
    private Activity mActivity;
    private View mView;
    private boolean isDetails;
    private String url;
    private String processId;
    private WorkingBean deleteWorkingBean;
    private PromptListener promptListener;

    public H5PopupWindow(Activity mActivity, boolean isDetails, String processId, WorkingBean deleteWorkingBean, String url, PromptListener promptListener) {
        super();
        this.isDetails = isDetails;
        this.processId = processId;
        this.promptListener = promptListener;
        this.deleteWorkingBean = deleteWorkingBean;
        this.mActivity = mActivity;
        this.url = url;
        this.initPopupWindow();
    }

    /**
     * 初始化
     */
    private void initPopupWindow() {
        LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mView = inflater.inflate(R.layout.dialog_h5, null);
        this.setContentView(mView);
        this.setWidth(DensityUtil.getScreenWidth());
        this.setHeight(DensityUtil.getScreenHeight());
        this.setTouchable(true);
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        this.setAnimationStyle(R.style.PopupAnimation);
        ColorDrawable background = new ColorDrawable(0x4f000000);
        this.setBackgroundDrawable(background);
        this.draw();

        final WebView wvMailList = (WebView) mView.findViewById(R.id.wvMailList);
        WebViewSettingUtil.setSettingNoZoom(mActivity,wvMailList);
        wvMailList.setBackgroundColor(0);
        wvMailList.getBackground().setAlpha(0);
        wvMailList.clearHistory();
        wvMailList.clearFormData();
        wvMailList.clearCache(true);
        CookieSyncManager.createInstance(mActivity);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
        wvMailList.addJavascriptInterface(new H5JavaScript(mActivity, new PromptListener() {
            @Override
            public void returnTrueOrFalse(boolean trueOrFalse) {
                if (trueOrFalse) {
                    promptListener.returnTrueOrFalse(true);
                }
            }
        }, isDetails, processId, deleteWorkingBean), "android_api");
        wvMailList.loadUrl(url);
    }

    /**
     * 绘制
     */
    private void draw() {
        this.mView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
    }

    /**
     * 显示在控件下右方
     *
     * @param parent
     */
    public void showAtDropDownRight(View parent) {
        this.showAsDropDown(parent);
    }
}
