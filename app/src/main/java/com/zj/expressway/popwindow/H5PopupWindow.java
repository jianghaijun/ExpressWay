package com.zj.expressway.popwindow;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.PopupWindow;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.zj.expressway.InJavaScript.H5JavaScript;
import com.zj.expressway.R;
import com.zj.expressway.bean.ProcessDictionaryBean;
import com.zj.expressway.bean.WorkingBean;
import com.zj.expressway.utils.ConstantsUtil;
import com.zj.expressway.utils.WebViewSettingUtil;

import org.litepal.crud.DataSupport;
import org.xutils.common.util.DensityUtil;

import java.util.List;


public class H5PopupWindow extends PopupWindow {
    private Activity mActivity;
    private View mView;
    private boolean isDetails;
    private String processId;
    private WorkingBean deleteWorkingBean;

    public H5PopupWindow(Activity mActivity, boolean isDetails, String processId, WorkingBean deleteWorkingBean) {
        super();
        this.isDetails = isDetails;
        this.processId = processId;
        this.deleteWorkingBean = deleteWorkingBean;
        this.mActivity = mActivity;
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

        WebView wvMailList = (WebView) mView.findViewById(R.id.wvMailList);
        WebViewSettingUtil.setSetting(wvMailList);
        wvMailList.setBackgroundColor(0);
        wvMailList.getBackground().setAlpha(0);
        wvMailList.addJavascriptInterface(new H5JavaScript(mActivity, H5PopupWindow.this, isDetails, processId, deleteWorkingBean), "android_api");
        wvMailList.loadUrl(ConstantsUtil.star);
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
