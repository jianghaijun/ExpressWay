package com.zj.expressway.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.yuyh.library.imgsel.ISNav;
import com.yuyh.library.imgsel.common.ImageLoader;
import com.yuyh.library.imgsel.config.ISCameraConfig;
import com.yuyh.library.imgsel.config.ISListConfig;
import com.zj.expressway.R;
import com.zj.expressway.base.BaseActivity;
import com.zj.expressway.bean.ContractorBean;
import com.zj.expressway.bean.WorkingBean;
import com.zj.expressway.dialog.DownloadApkDialog;
import com.zj.expressway.dialog.PromptDialog;
import com.zj.expressway.dialog.SelectPhotoWayDialog;
import com.zj.expressway.listener.PromptListener;
import com.zj.expressway.model.CheckVersionModel;
import com.zj.expressway.utils.AppInfoUtil;
import com.zj.expressway.utils.ConstantsUtil;
import com.zj.expressway.utils.GlideCatchUtil;
import com.zj.expressway.utils.JsonUtils;
import com.zj.expressway.utils.JudgeNetworkIsAvailable;
import com.zj.expressway.utils.LoadingUtils;
import com.zj.expressway.utils.ScreenManagerUtil;
import com.zj.expressway.utils.SpUtil;
import com.zj.expressway.utils.ToastUtil;

import org.litepal.crud.DataSupport;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

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
 *       Created by HaiJun on 2018/6/11 16:45
 *       个人设置
 */
public class MySettingActivity extends BaseActivity {
    private MyHolder myHolder;
    private Context mContext;
    private Activity mActivity;
    private PromptListener checkListener;
    private Long fileLength;

    public MySettingActivity(Context mContext, View layoutMy, PromptListener checkListener) {
        this.mContext = mContext;
        this.mActivity = (Activity) mContext;
        myHolder = new MyHolder();
        x.view().inject(myHolder, layoutMy);
        this.checkListener = checkListener;

        myHolder.txtVersion.setText("当前版本" + AppInfoUtil.getVersion(mContext));
        myHolder.txtUserName.setText((String) SpUtil.get(mContext, "UserName", ""));
        myHolder.txtCachingSize.setText(GlideCatchUtil.getCacheSize());

        // 自定义图片加载器
        ISNav.getInstance().init(new ImageLoader() {
            @Override
            public void displayImage(Context context, String path, ImageView imageView) {
                Glide.with(context).load(path).into(imageView);
            }
        });

        setData();
    }

    public void setVersion() {
        myHolder.txtVersion.setText("当前版本" + AppInfoUtil.getVersion(mContext));
    }

    /**
     * 赋值
     */
    private void setData() {
        myHolder.btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SpUtil.put(mContext, ConstantsUtil.IS_LOGIN_SUCCESSFUL, false);
                ScreenManagerUtil.popAllActivityExceptOne();
                mContext.startActivity(new Intent(mContext, LoginActivity.class));
            }
        });

        // 修改密码
        myHolder.imgViewUpdatePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(mContext, UpdatePassWordActivity.class));
            }
        });

        // 版本检查
        myHolder.imgViewCheckVersion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (JudgeNetworkIsAvailable.isNetworkAvailable(mActivity)) {
                    checkVersion();
                } else {
                    ToastUtil.showShort(mContext, mActivity.getString(R.string.not_network));
                }
            }
        });

        // 更换头像
        myHolder.imgViewUserAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (JudgeNetworkIsAvailable.isNetworkAvailable(mActivity)) {
                    SelectPhotoWayDialog selectPhotoWayDialog = new SelectPhotoWayDialog(mContext, new PromptListener() {
                        @Override
                        public void returnTrueOrFalse(boolean trueOrFalse) {
                            if (trueOrFalse) {
                                // 拍照
                                ISCameraConfig config = new ISCameraConfig.Builder()
                                        .needCrop(true) // 裁剪
                                        .cropSize(1, 1, 1200, 1200)
                                        .build();
                                ISNav.getInstance().toCameraActivity(mActivity, config, 1001);
                            } else {
                                // 相册
                                ISListConfig config = new ISListConfig.Builder()
                                        // 是否多选, 默认true
                                        .multiSelect(false)
                                        // 使用沉浸式状态栏
                                        .statusBarColor(Color.parseColor("#0099FF"))
                                        // 返回图标ResId
                                        .backResId(R.drawable.back_btn)
                                        // 标题
                                        .title("照片")
                                        // 标题文字颜色
                                        .titleColor(Color.WHITE)
                                        // TitleBar背景色
                                        .titleBgColor(Color.parseColor("#0099FF"))
                                        // 裁剪大小。needCrop为true的时候配置
                                        .cropSize(1, 1, 1200, 1200)
                                        .needCrop(true)
                                        // 第一个是否显示相机，默认true
                                        .needCamera(false)
                                        // 最大选择图片数量，默认9
                                        .maxNum(1)
                                        .build();
                                // 跳转到图片选择器
                                ISNav.getInstance().toListActivity(mActivity, config, 1002);
                            }
                        }
                    });
                    selectPhotoWayDialog.show();
                } else {
                    ToastUtil.showShort(mContext, mActivity.getString(R.string.not_network));
                }
            }
        });

        myHolder.imgViewCleanUpCaching.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoadingUtils.showLoading(mContext);
                // 清除已加载工序列表
                SpUtil.put(mContext, ConstantsUtil.LEVEL_ID, "");
                DataSupport.deleteAll(ContractorBean.class);
                /*清除工序下的图片*/
                //DataSupport.deleteAll(PhotosBean.class);
                DataSupport.deleteAll(WorkingBean.class);

                //DataSupport.deleteAll(UserInfo.class);
                boolean isClean = GlideCatchUtil.cleanCatchDisk();
                myHolder.txtCachingSize.setText(GlideCatchUtil.getCacheSize());
                LoadingUtils.hideLoading();
                if (isClean) {
                    ToastUtil.showShort(mContext, "清理成功");
                } else {
                    ToastUtil.showShort(mContext, "清理失败");
                }
            }
        });
    }

    /**
     * 版本检查
     */
    public void checkVersion() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(ConstantsUtil.BASE_URL + ConstantsUtil.CHECK_VERSION)
                .addHeader("token", (String) SpUtil.get(mContext, ConstantsUtil.TOKEN, ""))
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showShort(mContext, mActivity.getString(R.string.server_exception));
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String data = response.body().string();
                if (JsonUtils.isGoodJson(data)) {
                    Gson gson = new Gson();
                    final CheckVersionModel model = gson.fromJson(data, CheckVersionModel.class);
                    if (model.isSuccess()) {
                        int version = AppInfoUtil.compareVersion(model.getVersion(), AppInfoUtil.getVersion(mContext));
                        if (version == 1) {
                            // 发现新版本
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    fileLength = model.getFileLength();
                                    PromptDialog promptDialog = new PromptDialog(mContext, choiceListener, "发现新版本", "是否更新？", "否", "是");
                                    promptDialog.show();
                                }
                            });
                        } else {
                            // 当前为最新版本
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    myHolder.txtVersion.setText("当前已是最新版本！");
                                }
                            });
                        }
                    } else {
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.showShort(mContext, mActivity.getString(R.string.get_data_exception));
                            }
                        });
                    }
                } else {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.showShort(mContext, mActivity.getString(R.string.json_error));
                        }
                    });
                }
            }
        });
    }

    /**
     * 版本更新监听
     */
    private PromptListener choiceListener = new PromptListener() {
        @Override
        public void returnTrueOrFalse(boolean trueOrFalse) {
            if (trueOrFalse) {
                ConstantsUtil.isDownloadApk = true;
                checkListener.returnTrueOrFalse(true);
            } else {
                ConstantsUtil.isDownloadApk = false;
            }
        }
    };

    /**
     * 下载APK
     */
    public void downloadApk() {
        DownloadApkDialog downloadApkDialog = new DownloadApkDialog(mContext, fileLength);
        downloadApkDialog.setCanceledOnTouchOutside(false);
        downloadApkDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 容纳器
     */
    private class MyHolder {
        @ViewInject(R.id.txtVersion)
        private TextView txtVersion;
        @ViewInject(R.id.txtUserName)
        private TextView txtUserName;
        @ViewInject(R.id.txtCachingSize)
        private TextView txtCachingSize;
        @ViewInject(R.id.btnSignOut)
        private TextView btnSignOut;
        @ViewInject(R.id.imgViewUpdatePassword)
        private ImageView imgViewUpdatePassword;
        @ViewInject(R.id.imgViewUserAvatar)
        private ImageView imgViewUserAvatar;
        @ViewInject(R.id.imgViewCheckVersion)
        private ImageView imgViewCheckVersion;
        @ViewInject(R.id.imgViewCleanUpCaching)
        private ImageView imgViewCleanUpCaching;
    }

}
