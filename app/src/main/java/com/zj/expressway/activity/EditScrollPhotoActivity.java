package com.zj.expressway.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.zj.expressway.InJavaScript.MailListInJavaScript;
import com.zj.expressway.R;
import com.zj.expressway.base.BaseActivity;
import com.zj.expressway.listener.PermissionListener;
import com.zj.expressway.utils.ScreenManagerUtil;
import com.zj.expressway.utils.ToastUtil;
import com.zj.expressway.utils.WebViewSettingUtil;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

import cn.hutool.core.util.StrUtil;

/**
 * 轮播图编辑页
 */
public class EditScrollPhotoActivity extends BaseActivity {
    @ViewInject(R.id.imgBtnLeft)
    private ImageView imgBtnLeft;
    @ViewInject(R.id.txtTitle)
    private TextView txtTitle;
    @ViewInject(R.id.actionBar)
    private View actionBar;
    @ViewInject(R.id.wvMailList)
    private WebView wvMailList;

    private ValueCallback<Uri> uploadMessage;
    private ValueCallback<Uri[]> uploadMessageAboveL;
    private final static int FILE_CHOOSER_RESULT_CODE = 10000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_mail_list);

        x.view().inject(this);
        ScreenManagerUtil.pushActivity(this);

        imgBtnLeft.setVisibility(View.VISIBLE);
        imgBtnLeft.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.back_btn));
        String title = getIntent().getStringExtra("title");
        if (StrUtil.equals(title, "审核管理") || StrUtil.equals(title, "详情")) {
            actionBar.setVisibility(View.GONE);
        } else {
            txtTitle.setText(title);
            actionBar.setVisibility(View.VISIBLE);
        }

        WebViewSettingUtil.setSetting(wvMailList);
        wvMailList.addJavascriptInterface(new MailListInJavaScript(this, wvMailList), "android_api");

        wvMailList.setWebChromeClient(new WebChromeClient() {
            // For Android < 3.0
            public void openFileChooser(ValueCallback<Uri> valueCallback) {
                uploadMessage = valueCallback;
                selectFile();
            }

            // For Android  >= 3.0
            public void openFileChooser(ValueCallback valueCallback, String acceptType) {
                uploadMessage = valueCallback;
                selectFile();
            }

            //For Android  >= 4.1
            public void openFileChooser(ValueCallback<Uri> valueCallback, String acceptType, String capture) {
                uploadMessage = valueCallback;
                selectFile();
            }

            // For Android >= 5.0
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
                uploadMessageAboveL = filePathCallback;
                selectFile();
                return true;
            }
        });
        wvMailList.loadUrl(getIntent().getStringExtra("url"));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == FILE_CHOOSER_RESULT_CODE) {
            if (null == uploadMessage && null == uploadMessageAboveL) return;
            /*Uri result;
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
                result = Uri.fromFile(new File(list.get(0)));
            } else {
                result = FileProvider.getUriForFile(this, ProviderUtil.getFileProviderName(this), new File(list.get(0)));
            }*/

            Uri result = data.getData();

            if (uploadMessageAboveL != null) {
                Uri[] results = new Uri[1];
                results[0] = result;
                uploadMessageAboveL.onReceiveValue(results);
                uploadMessageAboveL = null;
            } else if (uploadMessage != null) {
                uploadMessage.onReceiveValue(result);
                uploadMessage = null;
            }
        }
    }

    @Override
    protected void onResume() {
        if (uploadMessage != null) {
            uploadMessage.onReceiveValue(null);
            uploadMessage = null;
        }
        if (uploadMessageAboveL != null) {
            uploadMessageAboveL.onReceiveValue(null);
            uploadMessageAboveL = null;
        }
        super.onResume();
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
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, FILE_CHOOSER_RESULT_CODE);
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
