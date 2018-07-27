package com.zj.expressway.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.zj.expressway.R;
import com.zj.expressway.utils.ConstantsUtil;
import com.zj.expressway.utils.DownloadUtil;
import com.zj.expressway.utils.ProviderUtil;
import com.zj.expressway.utils.ToastUtil;

import java.io.File;

/**
 *                     _ooOoo_
 *                    o8888888o
 *                    88" . "88
 *                    (| -_- |)
 *                    O\  =  /O
 *                 ____/`---'\____
 *               .'  \\|     |//  `.
 *              /  \\|||  :  |||//  \
 *             /  _||||| -:- |||||-  \
 *             |   | \\\  -  /// |   |
 *             | \_|  ''\---/''  |   |
 *             \  .-\__  `-`  ___/-. /
 *           ___`. .'  /--.--\  `. . __
 *        ."" '<  `.___\_<|>_/___.'  >'"".
 *       | | :  `- \`.;`\ _ /`;.`/ - ` : | |
 *       \  \ `-.   \_ __\ /__ _/   .-` /  /
 * ======`-.____`-.___\_____/___.-`____.-'======
 *                     `=---='
 * ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
 * 			   佛祖保佑       永无BUG
 *       Created by HaiJun on 2018/6/11 17:54
 *       下载APK dialog
 */
public class DownloadApkDialog extends Dialog {
    private Context mContext;
    private Activity mActivity;
    private TextView txtResult;
    private ProgressBar progressBarDownload;
    private Long fileLength;

    public DownloadApkDialog(Context context, Long fileLength) {
        super(context);
        this.fileLength = fileLength;
        this.mActivity = (Activity) context;
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_donload_apk);

        progressBarDownload = (ProgressBar) this.findViewById(R.id.progressBarDownload);
        txtResult = (TextView) this.findViewById(R.id.txtResult);

        progressBarDownload.setMax(100);
        txtResult.setText("已下载" + 0 + "%");

        downloadApk();
    }

    /**
     * 下载apk
     */
    private void downloadApk() {
        DownloadUtil.getInstance().download(fileLength, ConstantsUtil.BASE_URL + ConstantsUtil.DOWNLOAD_APK, ConstantsUtil.SAVE_PATH, new DownloadUtil.OnDownloadListener() {
            @Override
            public void onDownloadSuccess(String path) {
                dismiss();
                //安装
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri uri;
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
                    uri = Uri.fromFile(new File(ConstantsUtil.SAVE_PATH + ConstantsUtil.APK_NAME));
                } else {
                    /**
                     * 7.0 调用系统相机拍照不再允许使用Uri方式，应该替换为FileProvider
                     * 并且这样可以解决MIUI系统上拍照返回size为0的情况
                     */
                    uri = FileProvider.getUriForFile(mContext, ProviderUtil.getFileProviderName(mActivity), new File(ConstantsUtil.SAVE_PATH + ConstantsUtil.APK_NAME));
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
                intent.setDataAndType(uri, "application/vnd.android.package-archive");
                ConstantsUtil.isDownloadApk = false;
                mActivity.startActivity(intent);
            }

            @Override
            public void onDownloading(int progress) {
                progressBarDownload.setProgress(progress);
                txtResult.setText("已下载" + progress + "%");
            }

            @Override
            public void onDownloadFailed() {
                ConstantsUtil.isDownloadApk = false;
                ToastUtil.showShort(mContext, "文件下载失败！");
            }
        });
    }

    @Override
    public void show() {
        super.show();
        getWindow().setWindowAnimations(R.style.DialogInAndOutAnim);
    }
}
