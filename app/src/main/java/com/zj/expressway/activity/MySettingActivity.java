package com.zj.expressway.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
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
import com.zj.expressway.base.BaseModel;
import com.zj.expressway.bean.ContractorBean;
import com.zj.expressway.bean.ProcessDicBaseBean;
import com.zj.expressway.bean.ProcessDictionaryBean;
import com.zj.expressway.bean.SearchRecordBean;
import com.zj.expressway.bean.SyncLinkageMenuBean;
import com.zj.expressway.bean.WorkingBean;
import com.zj.expressway.dialog.DownloadApkDialog;
import com.zj.expressway.dialog.PromptDialog;
import com.zj.expressway.dialog.SelectPhotoWayDialog;
import com.zj.expressway.listener.PromptListener;
import com.zj.expressway.model.CheckVersionModel;
import com.zj.expressway.model.ProcessDictionaryModel;
import com.zj.expressway.model.SyncLinkageMenuModel;
import com.zj.expressway.model.SyncLinkageMenuSecondModel;
import com.zj.expressway.model.SyncLinkageMenuThirdModel;
import com.zj.expressway.utils.AppInfoUtil;
import com.zj.expressway.utils.ChildThreadUtil;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.hutool.core.util.ObjectUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by HaiJun on 2018/6/11 16:45
 * 个人设置
 */
public class MySettingActivity extends BaseActivity {
    private MyHolder myHolder;
    private Activity mActivity;
    private PromptListener checkListener;
    private Long fileLength;

    public MySettingActivity(Context mActivity, View layoutMy, PromptListener checkListener) {
        this.mActivity = (Activity) mActivity;
        myHolder = new MyHolder();
        x.view().inject(myHolder, layoutMy);
        this.checkListener = checkListener;

        myHolder.btnVersion.setText("版本检测：当前版本" + AppInfoUtil.getVersion(mActivity));
        myHolder.txtUserName.setText((String) SpUtil.get(mActivity, "UserName", ""));
        myHolder.btnCleanUpCaching.setText("清理网络图片缓存：" + GlideCatchUtil.getCacheSize());

        // 自定义图片加载器
        ISNav.getInstance().init(new ImageLoader() {
            @Override
            public void displayImage(Context context, String path, ImageView imageView) {
                Glide.with(context).load(path).into(imageView);
            }
        });

        setData();
    }

    /**
     * 设置版本号
     */
    public void setVersion() {
        myHolder.btnVersion.setText("版本检测：当前版本" + AppInfoUtil.getVersion(mActivity));
    }

    /**
     * 赋值
     */
    private void setData() {
        // 注销
        myHolder.btnSignOut.setOnClickListener(new OnClick());
        // 修改密码
        myHolder.btnUpdatePassword.setOnClickListener(new OnClick());
        // 设置ip
        myHolder.btnChangeIp.setOnClickListener(new OnClick());
        // 版本检查
        myHolder.btnVersion.setOnClickListener(new OnClick());
        // 更换头像
        myHolder.imgViewUserAvatar.setOnClickListener(new OnClick());
        // 清理缓存图片
        myHolder.btnCleanUpCaching.setOnClickListener(new OnClick());
        // 清理本地缓存
        myHolder.btnCleanLocalCaching.setOnClickListener(new OnClick());
        // 同步工序字典
        myHolder.btnSyncProcessDictionary.setOnClickListener(new OnClick());
        // 同步工序到服务器
        myHolder.btnSyncLevel.setOnClickListener(new OnClick());
    }

    /**
     * 点击事件
     */
    private class OnClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                // 注销
                case R.id.btnSignOut:
                    SpUtil.put(mActivity, "isFirstStar", true);
                    SpUtil.put(mActivity, ConstantsUtil.IS_LOGIN_SUCCESSFUL, false);
                    ScreenManagerUtil.popAllActivityExceptOne();
                    mActivity.startActivity(new Intent(mActivity, LoginActivity.class));
                    break;
                // 修改密码
                case R.id.btnUpdatePassword:
                    mActivity.startActivity(new Intent(mActivity, UpdatePassWordActivity.class));
                    break;
                // 设置ip
                case R.id.btnChangeIp:
                    mActivity.startActivity(new Intent(mActivity, ChangeIpActivity.class));
                    break;
                // 版本检查
                case R.id.btnVersion:
                    if (JudgeNetworkIsAvailable.isNetworkAvailable(mActivity)) {
                        checkVersion();
                    } else {
                        ToastUtil.showShort(mActivity, mActivity.getString(R.string.not_network));
                    }
                    break;
                // 更换头像
                case R.id.imgViewUserAvatar:
                    uploadUserAvatar();
                    break;
                // 清除网络图片缓存
                case R.id.btnCleanUpCaching:
                    LoadingUtils.showLoading(mActivity);
                    // 清理图片缓存
                    boolean isClean = GlideCatchUtil.cleanCatchDisk();
                    myHolder.btnCleanUpCaching.setText("清理网络图片缓存：" + GlideCatchUtil.getCacheSize());
                    LoadingUtils.hideLoading();
                    if (isClean) {
                        ToastUtil.showShort(mActivity, "清理网络图片缓存成功");
                    } else {
                        ToastUtil.showShort(mActivity, "清理网络图片缓存失败");
                    }
                    break;
                // 清除本地缓存
                case R.id.btnCleanLocalCaching:
                    new PromptDialog(mActivity, new PromptListener() {
                        @Override
                        public void returnTrueOrFalse(boolean trueOrFalse) {
                            if (trueOrFalse) {
                                // 清除已加载层级列表
                                DataSupport.deleteAll(ContractorBean.class);
                                // 清除工序下的图片
                                // DataSupport.deleteAll(PhotosBean.class);
                                // 清除已加载工序列表
                                DataSupport.deleteAll(WorkingBean.class);
                                // 清除用户信息
                                // DataSupport.deleteAll(UserInfo.class);
                                // 清除搜索记录
                                DataSupport.deleteAll(SearchRecordBean.class);
                                // 清除同步至本地字典
                                DataSupport.deleteAll(ProcessDictionaryBean.class);
                                // 清除同步至本地三级联动菜单
                                DataSupport.deleteAll(SyncLinkageMenuBean.class);
                                ToastUtil.showShort(mActivity, "清理本地缓存成功");
                            }
                        }
                    }, "提示", "清理后本地存储的数据无法恢复，是否继续清理？", "否", "是").show();
                    break;
                // 同步工序字典
                case R.id.btnSyncProcessDictionary:
                    if (JudgeNetworkIsAvailable.isNetworkAvailable(mActivity)) {
                        syncProcessDictionaryClick(true);
                    } else {
                        ToastUtil.showShort(mActivity, mActivity.getString(R.string.not_network));
                    }
                    break;
                // 同步工序到服务器
                case R.id.btnSyncLevel:
                    if (JudgeNetworkIsAvailable.isNetworkAvailable(mActivity)) {
                        syncToTheServer();
                    } else {
                        ToastUtil.showShort(mActivity, mActivity.getString(R.string.not_network));
                    }
                    break;
            }
        }
    }

    /**
     * 更换头像
     */
    private void uploadUserAvatar() {
        if (JudgeNetworkIsAvailable.isNetworkAvailable(mActivity)) {
            SelectPhotoWayDialog selectPhotoWayDialog = new SelectPhotoWayDialog(mActivity, new PromptListener() {
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
            ToastUtil.showShort(mActivity, mActivity.getString(R.string.not_network));
        }
    }

    /**
     * 版本检查
     */
    public void checkVersion() {
        Request request = ChildThreadUtil.getRequestByGet(mActivity, ConstantsUtil.CHECK_VERSION);
        ConstantsUtil.okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                ChildThreadUtil.toastMsgHidden(mActivity, mActivity.getString(R.string.server_exception));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String data = response.body().string();
                if (JsonUtils.isGoodJson(data)) {
                    Gson gson = new Gson();
                    final CheckVersionModel model = gson.fromJson(data, CheckVersionModel.class);
                    if (model.isSuccess()) {
                        int version = AppInfoUtil.compareVersion(model.getVersion(), AppInfoUtil.getVersion(mActivity));
                        if (version == 1) {
                            // 发现新版本
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    downloadApk(model.getFileLength());
                                }
                            });
                        } else {
                            // 当前为最新版本
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    myHolder.btnVersion.setText("当前已是最新版本！");
                                }
                            });
                        }
                    } else {
                        ChildThreadUtil.checkTokenHidden(mActivity, model.getMessage(), model.getCode());
                    }
                } else {
                    ChildThreadUtil.toastMsgHidden(mActivity, mActivity.getString(R.string.json_error));
                }
            }
        });
    }

    /**
     * 是否下载Apk
     * @param fileLength
     */
    public void downloadApk(long fileLength) {
        ConstantsUtil.isDownloadApk = true;
        this.fileLength = fileLength;
        PromptDialog promptDialog = new PromptDialog(mActivity, choiceListener, "发现新版本", "是否更新？", "否", "是");
        promptDialog.show();
    }

    /**
     * （异步）同步服务器字典表
     */
    public void syncProcessDictionary(final boolean isShowLogin) throws IOException {
        if (isShowLogin) {
            LoadingUtils.showLoading(mActivity);
        }
        Request request = ChildThreadUtil.getRequest(mActivity, ConstantsUtil.appGetTwoinoneDictList, "");
        Response response = ConstantsUtil.okHttpClient.newCall(request).execute();
        String data = response.body().string();
        if (JsonUtils.isGoodJson(data)) {
            Gson gson = new Gson();
            final ProcessDictionaryModel model = gson.fromJson(data, ProcessDictionaryModel.class);
            if (model.isSuccess()) {
                if (ObjectUtil.isNotNull(model)) {
                    syncLinkageMenu(isShowLogin);
                    for (ProcessDicBaseBean processBean : model.getData()) {
                        // 保存工序字典
                        ProcessDictionaryBean dictBean = new ProcessDictionaryBean();
                        dictBean.setDelFlag(processBean.getDelFlag());
                        dictBean.setCreateTime(processBean.getCreateTime());
                        dictBean.setCreateUser(processBean.getCreateUser());
                        dictBean.setCreateUserName(processBean.getCreateUserName());
                        dictBean.setModifyTime(processBean.getModifyTime());
                        dictBean.setModifyUser(processBean.getModifyUser());
                        dictBean.setCreateUserName(processBean.getModifyUserName());
                        dictBean.setDictId(processBean.getDictId());
                        dictBean.setDictName(processBean.getDictName());
                        dictBean.setDictCode(processBean.getDictCode());
                        dictBean.setParentId(processBean.getParentId());
                        dictBean.setPhotoContent(processBean.getPhotoContent());
                        dictBean.setPhotoDistance(processBean.getPhotoDistance());
                        dictBean.setPhotoNumber(processBean.getPhotoNumber());
                        dictBean.setFirstLevelId(processBean.getFirstLevelId());
                        dictBean.setFirstLevelName(processBean.getFirstLevelName());
                        dictBean.setSecondLevelId(processBean.getSecondLevelId());
                        dictBean.setSecondLevelName(processBean.getSecondLevelName());
                        dictBean.setThirdLevelId(processBean.getThirdLevelId());
                        dictBean.setThirdLevelName(processBean.getThirdLevelName());
                        dictBean.setType("1");
                        dictBean.saveOrUpdate("dictId=?", processBean.getDictId());
                        if (ObjectUtil.isNotNull(processBean.getGxDictionaryList())) {
                            for (ProcessDictionaryBean bean : processBean.getGxDictionaryList()) {
                                bean.setType("2");
                                bean.saveOrUpdate("dictId=?", bean.getDictId());
                            }
                        }
                    }
                } else {
                    if (isShowLogin) {
                        ChildThreadUtil.toastMsgHidden(mActivity, mActivity.getString(R.string.json_error));
                    }
                }
            } else {
                if (isShowLogin) {
                    ChildThreadUtil.checkTokenHidden(mActivity, model.getMessage(), model.getCode());
                }
            }
        } else {
            if (isShowLogin) {
                ChildThreadUtil.toastMsgHidden(mActivity, mActivity.getString(R.string.json_error));
            }
        }
    }

    /**
     * (异步) 同步三级联动菜单
     */
    private void syncLinkageMenu(final boolean isShowLogin) throws IOException {
        Request request = ChildThreadUtil.getRequest(mActivity, ConstantsUtil.getFirSecThiLevelSelect, "");
        Response response = ConstantsUtil.okHttpClient.newCall(request).execute();
        String data = response.body().string();
        if (JsonUtils.isGoodJson(data)) {
            Gson gson = new Gson();
            final SyncLinkageMenuModel model = gson.fromJson(data, SyncLinkageMenuModel.class);
            if (model.isSuccess()) {
                if (ObjectUtil.isNotNull(model)) {
                    for (SyncLinkageMenuSecondModel secondModel : model.getData()) {
                        SyncLinkageMenuBean firstBean = new SyncLinkageMenuBean();
                        firstBean.setDelFlag(secondModel.getDelFlag());
                        firstBean.setCreateTime(secondModel.getCreateTime());
                        firstBean.setCreateUser(secondModel.getCreateUser());
                        firstBean.setCreateUserName(secondModel.getCreateUserName());
                        firstBean.setModifyTime(secondModel.getModifyTime());
                        firstBean.setModifyUser(secondModel.getModifyUser());
                        firstBean.setModifyUserName(secondModel.getModifyUserName());
                        firstBean.setFirstLevelId(secondModel.getFirstLevelId());
                        firstBean.setFirstLevelName(secondModel.getFirstLevelName());
                        firstBean.setFirstLevelCode(secondModel.getFirstLevelCode());
                        firstBean.setSortFlag(secondModel.getSortFlag());
                        firstBean.setSelectFlag(secondModel.getSelectFlag());
                        firstBean.setType("1");
                        firstBean.saveOrUpdate("firstLevelId=?", secondModel.getFirstLevelId());
                        if (ObjectUtil.isNotNull(secondModel.getSecondLevelList())) {
                            for (SyncLinkageMenuThirdModel thirdBean : secondModel.getSecondLevelList()) {
                                SyncLinkageMenuBean secondBean = new SyncLinkageMenuBean();
                                secondBean.setDelFlag(thirdBean.getDelFlag());
                                secondBean.setCreateTime(thirdBean.getCreateTime());
                                secondBean.setCreateUser(thirdBean.getCreateUser());
                                secondBean.setCreateUserName(thirdBean.getCreateUserName());
                                secondBean.setModifyTime(thirdBean.getModifyTime());
                                secondBean.setModifyUser(thirdBean.getModifyUser());
                                secondBean.setModifyUserName(thirdBean.getModifyUserName());
                                secondBean.setFirstLevelId(thirdBean.getFirstLevelId());
                                secondBean.setSecondLevelId(thirdBean.getSecondLevelId());
                                secondBean.setSecondLevelName(thirdBean.getSecondLevelName());
                                secondBean.setSecondLevelCode(thirdBean.getSecondLevelCode());
                                secondBean.setSortFlag(thirdBean.getSortFlag());
                                secondBean.setType("2");
                                secondBean.saveOrUpdate("secondLevelId=?", thirdBean.getSecondLevelId());
                                if (ObjectUtil.isNotNull(thirdBean.getThirdLevelList())) {
                                    for (SyncLinkageMenuBean bean : thirdBean.getThirdLevelList()) {
                                        bean.setType("3");
                                        bean.saveOrUpdate("thirdLevelId=?", bean.getThirdLevelId());
                                    }
                                }
                            }
                        }
                    }
                    SpUtil.put(mActivity, "isSync", true);
                    if (isShowLogin) {
                        ChildThreadUtil.toastMsgHidden(mActivity, "同步成功！");
                    }
                } else {
                    if (isShowLogin) {
                        ChildThreadUtil.toastMsgHidden(mActivity, mActivity.getString(R.string.json_error));
                    }
                }
            } else {
                if (isShowLogin) {
                    ChildThreadUtil.checkTokenHidden(mActivity, model.getMessage(), model.getCode());
                }
            }
        } else {
            if (isShowLogin) {
                ChildThreadUtil.toastMsgHidden(mActivity, mActivity.getString(R.string.json_error));
            }
        }
    }

    /**
     * （同步）同步服务器字典表
     */
    public void syncProcessDictionaryClick(final boolean isShowLogin) {
        if (isShowLogin) {
            LoadingUtils.showLoading(mActivity);
        }
        Request request = ChildThreadUtil.getRequest(mActivity, ConstantsUtil.appGetTwoinoneDictList, "");
        ConstantsUtil.okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                ChildThreadUtil.toastMsgHidden(mActivity, getString(R.string.server_exception));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String data = response.body().string();
                if (JsonUtils.isGoodJson(data)) {
                    Gson gson = new Gson();
                    final ProcessDictionaryModel model = gson.fromJson(data, ProcessDictionaryModel.class);
                    if (model.isSuccess()) {
                        if (ObjectUtil.isNotNull(model)) {
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    for (ProcessDicBaseBean processBean : model.getData()) {
                                        // 保存工序字典
                                        ProcessDictionaryBean dictBean = new ProcessDictionaryBean();
                                        dictBean.setDelFlag(processBean.getDelFlag());
                                        dictBean.setCreateTime(processBean.getCreateTime());
                                        dictBean.setCreateUser(processBean.getCreateUser());
                                        dictBean.setCreateUserName(processBean.getCreateUserName());
                                        dictBean.setModifyTime(processBean.getModifyTime());
                                        dictBean.setModifyUser(processBean.getModifyUser());
                                        dictBean.setCreateUserName(processBean.getModifyUserName());
                                        dictBean.setDictId(processBean.getDictId());
                                        dictBean.setDictName(processBean.getDictName());
                                        dictBean.setDictCode(processBean.getDictCode());
                                        dictBean.setParentId(processBean.getParentId());
                                        dictBean.setPhotoContent(processBean.getPhotoContent());
                                        dictBean.setPhotoDistance(processBean.getPhotoDistance());
                                        dictBean.setPhotoNumber(processBean.getPhotoNumber());
                                        dictBean.setFirstLevelId(processBean.getFirstLevelId());
                                        dictBean.setFirstLevelName(processBean.getFirstLevelName());
                                        dictBean.setSecondLevelId(processBean.getSecondLevelId());
                                        dictBean.setSecondLevelName(processBean.getSecondLevelName());
                                        dictBean.setThirdLevelId(processBean.getThirdLevelId());
                                        dictBean.setThirdLevelName(processBean.getThirdLevelName());
                                        dictBean.setType("1");
                                        dictBean.saveOrUpdate("dictId=?", processBean.getDictId());
                                        if (ObjectUtil.isNotNull(processBean.getGxDictionaryList())) {
                                            for (ProcessDictionaryBean bean : processBean.getGxDictionaryList()) {
                                                bean.setType("2");
                                                bean.saveOrUpdate("dictId=?", bean.getDictId());
                                            }
                                        }
                                    }
                                    syncLinkageMenuClick(isShowLogin);
                                }
                            });
                        } else {
                            if (isShowLogin) {
                                ChildThreadUtil.toastMsgHidden(mActivity, mActivity.getString(R.string.json_error));
                            }
                        }
                    } else {
                        if (isShowLogin) {
                            ChildThreadUtil.checkTokenHidden(mActivity, model.getMessage(), model.getCode());
                        }
                    }
                } else {
                    if (isShowLogin) {
                        ChildThreadUtil.toastMsgHidden(mActivity, mActivity.getString(R.string.json_error));
                    }
                }
            }
        });
    }

    /**
     * (同步) 同步三级联动菜单
     */
    private void syncLinkageMenuClick(final boolean isShowLogin) {
        Request request = ChildThreadUtil.getRequest(mActivity, ConstantsUtil.getFirSecThiLevelSelect, "");
        ConstantsUtil.okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                ChildThreadUtil.toastMsgHidden(mActivity, getString(R.string.server_exception));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String data = response.body().string();
                if (JsonUtils.isGoodJson(data)) {
                    Gson gson = new Gson();
                    final SyncLinkageMenuModel model = gson.fromJson(data, SyncLinkageMenuModel.class);
                    if (model.isSuccess()) {
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (ObjectUtil.isNotNull(model)) {
                                    for (SyncLinkageMenuSecondModel secondModel : model.getData()) {
                                        SyncLinkageMenuBean firstBean = new SyncLinkageMenuBean();
                                        firstBean.setDelFlag(secondModel.getDelFlag());
                                        firstBean.setCreateTime(secondModel.getCreateTime());
                                        firstBean.setCreateUser(secondModel.getCreateUser());
                                        firstBean.setCreateUserName(secondModel.getCreateUserName());
                                        firstBean.setModifyTime(secondModel.getModifyTime());
                                        firstBean.setModifyUser(secondModel.getModifyUser());
                                        firstBean.setModifyUserName(secondModel.getModifyUserName());
                                        firstBean.setFirstLevelId(secondModel.getFirstLevelId());
                                        firstBean.setFirstLevelName(secondModel.getFirstLevelName());
                                        firstBean.setFirstLevelCode(secondModel.getFirstLevelCode());
                                        firstBean.setSortFlag(secondModel.getSortFlag());
                                        firstBean.setSelectFlag(secondModel.getSelectFlag());
                                        firstBean.setType("1");
                                        firstBean.saveOrUpdate("firstLevelId=?", secondModel.getFirstLevelId());
                                        if (ObjectUtil.isNotNull(secondModel.getSecondLevelList())) {
                                            for (SyncLinkageMenuThirdModel thirdBean : secondModel.getSecondLevelList()) {
                                                SyncLinkageMenuBean secondBean = new SyncLinkageMenuBean();
                                                secondBean.setDelFlag(thirdBean.getDelFlag());
                                                secondBean.setCreateTime(thirdBean.getCreateTime());
                                                secondBean.setCreateUser(thirdBean.getCreateUser());
                                                secondBean.setCreateUserName(thirdBean.getCreateUserName());
                                                secondBean.setModifyTime(thirdBean.getModifyTime());
                                                secondBean.setModifyUser(thirdBean.getModifyUser());
                                                secondBean.setModifyUserName(thirdBean.getModifyUserName());
                                                secondBean.setFirstLevelId(thirdBean.getFirstLevelId());
                                                secondBean.setSecondLevelId(thirdBean.getSecondLevelId());
                                                secondBean.setSecondLevelName(thirdBean.getSecondLevelName());
                                                secondBean.setSecondLevelCode(thirdBean.getSecondLevelCode());
                                                secondBean.setSortFlag(thirdBean.getSortFlag());
                                                secondBean.setType("2");
                                                secondBean.saveOrUpdate("secondLevelId=?", thirdBean.getSecondLevelId());
                                                if (ObjectUtil.isNotNull(thirdBean.getThirdLevelList())) {
                                                    for (SyncLinkageMenuBean bean : thirdBean.getThirdLevelList()) {
                                                        bean.setType("3");
                                                        bean.saveOrUpdate("thirdLevelId=?", bean.getThirdLevelId());
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    SpUtil.put(mActivity, "isSync", true);
                                    if (isShowLogin) {
                                        ChildThreadUtil.toastMsgHidden(mActivity, "同步成功！");
                                    }
                                } else {
                                    if (isShowLogin) {
                                        ChildThreadUtil.toastMsgHidden(mActivity, mActivity.getString(R.string.json_error));
                                    }
                                }
                            }
                        });
                    } else {
                        if (isShowLogin) {
                            ChildThreadUtil.checkTokenHidden(mActivity, model.getMessage(), model.getCode());
                        }
                    }
                } else {
                    if (isShowLogin) {
                        ChildThreadUtil.toastMsgHidden(mActivity, mActivity.getString(R.string.json_error));
                    }
                }
            }
        });
    }

    /**
     * 同步数据到服务器
     */
    private void syncToTheServer() {
        List<ContractorBean> contractBeenList = DataSupport.where("userId=? and isLocalAdd=1", String.valueOf(SpUtil.get(mActivity, ConstantsUtil.USER_ID, ""))).find(ContractorBean.class);
        if (contractBeenList == null || contractBeenList.size() == 0) {
            ToastUtil.showShort(mActivity, "没有可同步的数据！");
        } else {
            // 层级
            List<Map<String, Object>> levelMap = new ArrayList<>();
            for (ContractorBean contractorbean : contractBeenList) {
                // 添加层级
                Map<String, Object> level = new HashMap<>();
                level.put("levelId", contractorbean.getLevelId());
                level.put("levelName", contractorbean.getLevelName());
                level.put("levelCode", contractorbean.getLevelCode());
                level.put("parentId", contractorbean.getParentId());
                level.put("parentIdAll", contractorbean.getParentIdAll());
                level.put("parentNameAll", contractorbean.getParentNameAll());
                level.put("totalNum", contractorbean.getProcessNum());
                level.put("finishedNum", 0);
                level.put("canExpand", contractorbean.getCanExpand());
                level.put("delFlag", 0);
                level.put("createTime", System.currentTimeMillis());
                level.put("createUser", SpUtil.get(mActivity, ConstantsUtil.USER_ID, ""));
                level.put("createUserName", SpUtil.get(mActivity, "UserName", ""));
                level.put("modifyTime", System.currentTimeMillis());
                level.put("modifyUser", SpUtil.get(mActivity, ConstantsUtil.USER_ID, ""));
                level.put("modifyUserName", SpUtil.get(mActivity, "UserName", ""));
                levelMap.add(level);
            }

            List<WorkingBean> workingBeenList = DataSupport.where("userId=? and type=1 and isLocalAdd=1", String.valueOf(SpUtil.get(mActivity, ConstantsUtil.USER_ID, ""))).find(WorkingBean.class);
            // 工序
            List<Map<String, Object>> processMap = new ArrayList<>();
            for (WorkingBean workingBean : workingBeenList) {
                // 添加层级
                Map<String, Object> process = new HashMap<>();
                process.put("processId", workingBean.getProcessId());
                process.put("processName", workingBean.getProcessName());
                process.put("processCode", workingBean.getProcessCode());
                process.put("photoContent", workingBean.getPhotoContent());
                process.put("photoDistance", workingBean.getPhotoDistance());
                process.put("photoNumber", workingBean.getPhotoNumber());
                process.put("levelId", workingBean.getLevelId());
                process.put("levelNameAll", workingBean.getLevelNameAll() + "," + workingBean.getProcessName());
                process.put("levelIdAll", workingBean.getLevelIdAll() + "," + workingBean.getProcessId());
                process.put("enterTime", workingBean.getEnterTime());
                process.put("workId", workingBean.getWorkId());
                process.put("flowStatus", "0");
                process.put("delFlag", 0);
                process.put("createTime", System.currentTimeMillis());
                process.put("createUser", SpUtil.get(mActivity, ConstantsUtil.USER_ID, ""));
                process.put("createUserName", SpUtil.get(mActivity, "UserName", ""));
                process.put("modifyTime", System.currentTimeMillis());
                process.put("modifyUser", SpUtil.get(mActivity, ConstantsUtil.USER_ID, ""));
                process.put("modifyUserName", SpUtil.get(mActivity, "UserName", ""));
                processMap.add(process);
            }
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("gxProjectLevelList", levelMap);
            dataMap.put("gxProcessList", processMap);
            syncDataToServer(new Gson().toJson(dataMap));
        }
    }

    /**
     * 同步数据到服务器
     *
     * @param jsonData
     */
    private void syncDataToServer(String jsonData) {
        LoadingUtils.showLoading(mActivity);
        Request request = ChildThreadUtil.getRequest(mActivity, ConstantsUtil.syncDataToServer, jsonData);
        ConstantsUtil.okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                ChildThreadUtil.toastMsgHidden(mActivity, getString(R.string.server_exception));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String jsonData = response.body().string().toString();
                if (JsonUtils.isGoodJson(jsonData)) {
                    Gson gson = new Gson();
                    final BaseModel model = gson.fromJson(jsonData, BaseModel.class);
                    if (model.isSuccess()) {
                        DataSupport.deleteAll(ContractorBean.class, "userId=? and isLocalAdd=1", String.valueOf(SpUtil.get(mActivity, ConstantsUtil.USER_ID, "")));
                        DataSupport.deleteAll(WorkingBean.class, "userId=? and isLocalAdd=1", String.valueOf(SpUtil.get(mActivity, ConstantsUtil.USER_ID, "")));
                        ChildThreadUtil.toastMsgHidden(mActivity, model.getMessage());
                    } else {
                        ChildThreadUtil.checkTokenHidden(mActivity, model.getMessage(), model.getCode());
                    }
                } else {
                    ChildThreadUtil.toastMsgHidden(mActivity, getString(R.string.json_error));
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
        DownloadApkDialog downloadApkDialog = new DownloadApkDialog(mActivity, fileLength);
        downloadApkDialog.setCanceledOnTouchOutside(false);
        downloadApkDialog.show();
    }

    /**
     * 容纳器
     */
    private class MyHolder {
        @ViewInject(R.id.imgViewUserAvatar)
        private ImageView imgViewUserAvatar;
        @ViewInject(R.id.txtUserName)
        private TextView txtUserName;
        @ViewInject(R.id.btnSignOut)
        private TextView btnSignOut;
        @ViewInject(R.id.btnUpdatePassword)
        private Button btnUpdatePassword;
        @ViewInject(R.id.btnChangeIp)
        private Button btnChangeIp;
        @ViewInject(R.id.btnVersion)
        private Button btnVersion;
        @ViewInject(R.id.btnCleanUpCaching)
        private Button btnCleanUpCaching;
        @ViewInject(R.id.btnSyncProcessDictionary)
        private Button btnSyncProcessDictionary;
        @ViewInject(R.id.btnSyncLevel)
        private Button btnSyncLevel;
        @ViewInject(R.id.btnCleanLocalCaching)
        private Button btnCleanLocalCaching;
    }

}
