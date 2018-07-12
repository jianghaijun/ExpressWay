package com.zj.expressway.activity;

import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.zj.expressway.InJavaScript.MailListInJavaScript;
import com.zj.expressway.R;
import com.zj.expressway.base.BaseActivity;
import com.zj.expressway.utils.ConstantsUtil;
import com.zj.expressway.utils.ScreenManagerUtil;
import com.zj.expressway.utils.WebViewSettingUtil;
import com.zj.expressway.view.CustomWebViewClient;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * 轮播图编辑页
 */
public class EditScrollPhotoActivity extends BaseActivity implements CustomWebViewClient.WebViewClientListener {
    @ViewInject(R.id.imgBtnLeft)
    private ImageView imgBtnLeft;
    @ViewInject(R.id.txtTitle)
    private TextView txtTitle;
    @ViewInject(R.id.actionBar)
    private View actionBar;
    @ViewInject(R.id.wvMailList)
    private WebView wvMailList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_mail_list);

        x.view().inject(this);
        ScreenManagerUtil.pushActivity(this);

        actionBar.setVisibility(View.VISIBLE);
        imgBtnLeft.setVisibility(View.VISIBLE);
        imgBtnLeft.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.back_btn));
        txtTitle.setText("编辑");

        WebViewSettingUtil.setSetting(wvMailList);
        wvMailList.addJavascriptInterface(new MailListInJavaScript(this, wvMailList), "android_api");
        wvMailList.setWebViewClient(new CustomWebViewClient(this, this));
        wvMailList.loadUrl(ConstantsUtil.Scroll_Photo + "?" + getIntent().getStringExtra("viewId"));
    }

    @Override
    public void onPageStared(WebView view, String url, Bitmap favicon) {

    }

    @Override
    public void onPageFinished(WebView view, String url) {

    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {

    }

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {

    }

    @Event({R.id.imgBtnLeft, R.id.btnQuery})
    private void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgBtnLeft:
                this.finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ScreenManagerUtil.popActivity(this);
    }
}
