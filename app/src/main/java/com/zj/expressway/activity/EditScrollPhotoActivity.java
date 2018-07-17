package com.zj.expressway.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.webkit.GeolocationPermissions;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.leon.lfilepickerlibrary.LFilePicker;
import com.zj.expressway.InJavaScript.MailListInJavaScript;
import com.zj.expressway.Manifest;
import com.zj.expressway.R;
import com.zj.expressway.base.BaseActivity;
import com.zj.expressway.listener.PermissionListener;
import com.zj.expressway.utils.ConstantsUtil;
import com.zj.expressway.utils.ScreenManagerUtil;
import com.zj.expressway.utils.ToastUtil;
import com.zj.expressway.utils.WebViewSettingUtil;
import com.zj.expressway.view.CustomWebViewClient;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

import cn.hutool.core.util.StrUtil;

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

    private ValueCallback<Uri> mUploadMessage;
    private ValueCallback<Uri[]> mUploadCallbackAboveL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_mail_list);

        x.view().inject(this);
        ScreenManagerUtil.pushActivity(this);

        imgBtnLeft.setVisibility(View.VISIBLE);
        imgBtnLeft.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.back_btn));
        String title = getIntent().getStringExtra("title");
        if (StrUtil.equals(title, "审核管理")) {
            actionBar.setVisibility(View.GONE);
        } else {
            txtTitle.setText(title);
            actionBar.setVisibility(View.VISIBLE);
        }

        WebViewSettingUtil.setSetting(wvMailList);
        wvMailList.addJavascriptInterface(new MailListInJavaScript(this, wvMailList), "android_api");
        wvMailList.setWebViewClient(new CustomWebViewClient(this, this));
        wvMailList.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                // 获取当前位置权限
                callback.invoke(origin, true, false);
                super.onGeolocationPermissionsShowPrompt(origin, callback);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
            }

            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                mUploadCallbackAboveL = filePathCallback;
                selectFile();
                return true;
            }

            // For Android < 3.0
            @SuppressWarnings("unused")
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                openFileChooser(uploadMsg, "");
            }

            // For Android > 4.1.1
            @SuppressWarnings("unused")
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                openFileChooser(uploadMsg, acceptType);
            }

            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
                if (mUploadMessage != null) {
                    return;
                }

                mUploadMessage = uploadMsg;
                selectFile();
            }
        });

        wvMailList.loadUrl(getIntent().getStringExtra("url"));
    }

    /**
     * 申请读取内存卡权限
     */
    private void selectFile() {
        if (Build.VERSION.SDK_INT >= 23) {
            requestAuthority(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE}, new PermissionListener() {
                @Override
                public void agree() {
                    choiceFile();
                }

                @Override
                public void refuse(List<String> refusePermission) {
                    ToastUtil.showShort(EditScrollPhotoActivity.this, "您拒绝了读取内存卡权限!");
                }
            });
        } else {
            choiceFile();
        }
    }

    /**
     * 选择文件
     */
    private void choiceFile() {
        new LFilePicker()
                .withActivity(EditScrollPhotoActivity.this)
                .withRequestCode(1000)
                .withTitle("选择文件")
                .withBackgroundColor("#0DACF4")
                .withStartPath("/storage/emulated/0")//指定初始显示路径
                //.withFileFilter(new String[] {".doc",".docx", ".xls", ".xlsx", ".pdf"}) // 设置文件格式
                //.withMutilyMode(false) // 设置为单选
                //.withIsGreater(false)//过滤文件大小 小于指定大小的文件
                //.withFileSize(2 * 1024 * 1024)//指定文件大小为2M
                .start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1000) {
                List<String> list = data.getStringArrayListExtra("paths");
                if (list.size() <= 0) {
                    return;
                }
                Uri photoUri = Uri.parse(list.get(0));
                if (mUploadCallbackAboveL != null) {
                    Uri[] results = new Uri[]{Uri.parse(list.get(0))};
                    mUploadCallbackAboveL.onReceiveValue(results);
                    mUploadCallbackAboveL = null;
                } else if (mUploadMessage != null) {
                    mUploadMessage.onReceiveValue(photoUri);
                    mUploadMessage = null;
                }
            }
        }
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
