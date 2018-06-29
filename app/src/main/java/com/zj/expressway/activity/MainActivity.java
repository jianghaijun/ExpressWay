package com.zj.expressway.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zj.expressway.R;
import com.zj.expressway.base.BaseActivity;
import com.zj.expressway.base.BaseModel;
import com.zj.expressway.bean.UserInfo;
import com.zj.expressway.bean.WorkingBean;
import com.zj.expressway.listener.PromptListener;
import com.zj.expressway.model.WorkingModel;
import com.zj.expressway.utils.ChildThreadUtil;
import com.zj.expressway.utils.ConstantsUtil;
import com.zj.expressway.utils.JsonUtils;
import com.zj.expressway.utils.JudgeNetworkIsAvailable;
import com.zj.expressway.utils.LoadingUtils;
import com.zj.expressway.utils.SpUtil;
import com.zj.expressway.utils.ToastUtil;

import org.litepal.crud.DataSupport;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by HaiJun on 2018/6/11 16:44
 * 主界面
 */
public class MainActivity extends BaseActivity {
    @ViewInject(R.id.vpMain)
    private ViewPager vpMain;
    @ViewInject(R.id.bottom_navigation_bar)
    private BottomNavigationBar bottomNavigationBar;
    private ImageView imgViewUserAvatar;
    private Activity mContext;
    private List<String> urlList;

    // 子布局
    private View layoutMsg, layoutApp, layoutFriends, layoutMe;
    private MsgMainActivity msgMainActivity;
    private AppActivity appActivity;
    private MySettingActivity mySettingActivity;
    // View列表
    private ArrayList<View> views;
    private boolean isUploadHead = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);
        x.view().inject(this);

        mContext = this;

        //将要分页显示的View装入数组中
        LayoutInflater viewLI = LayoutInflater.from(this);
        layoutMsg = viewLI.inflate(R.layout.layout_msg_main, null);
        layoutApp = viewLI.inflate(R.layout.layout_app, null);
        layoutFriends = viewLI.inflate(R.layout.layout_empty, null);
        layoutMe = viewLI.inflate(R.layout.layout_my_setting, null);
        // 用户头像
        imgViewUserAvatar = (ImageView) layoutMe.findViewById(R.id.imgViewUserAvatar);
        List<UserInfo> userList = DataSupport.where("userId=?", String.valueOf(SpUtil.get(mContext, ConstantsUtil.USER_ID, ""))).find(UserInfo.class);
        String userHead = "";
        if (userList != null && userList.size() > 0) {
            UserInfo user = userList.get(0);
            userHead = user.getImageUrl();
        }

        if (TextUtils.isEmpty(userHead)) {
            Glide.with(this).load(R.drawable.user_avatar).load(imgViewUserAvatar);
        } else {
            RequestOptions options = new RequestOptions().circleCrop();
            Glide.with(this).load(userHead).apply(options).into(imgViewUserAvatar);
        }

        DisplayMetrics dm = new DisplayMetrics();
        //取得窗口属性
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        //窗口高度
        int screenHeight = dm.heightPixels;
        // 获取屏幕高度
        SpUtil.put(mContext, ConstantsUtil.SCREEN_HEIGHT, screenHeight);

        // 消息
        msgMainActivity = new MsgMainActivity(mContext, layoutMsg);
        // 应用
        appActivity = new AppActivity(this, layoutApp);
        // 我的
        mySettingActivity = new MySettingActivity(mContext, layoutMe, choiceListener);

        urlList = new ArrayList<>();
        urlList.add("http://p0.qhimgs4.com/t018167bfb74ac52291.jpg");
        urlList.add("http://www.dfgg.cn/imageRepository/f239c0aa-d4e7-46c1-9fa8-fc772189c6ae.jpg");
        urlList.add("http://imgsrc.baidu.com/imgad/pic/item/9f2f070828381f30da0865a0a3014c086e06f0a2.jpg");

        //每个页面的view数据
        views = new ArrayList<>();
        views.add(layoutMsg);
        views.add(layoutApp);
        views.add(layoutFriends);
        views.add(layoutMe);

        bottomNavigationBar.addItem(new BottomNavigationItem(R.drawable.msg_select, "消息").setInactiveIcon(ContextCompat.getDrawable(this, R.drawable.msg_un_select)))
                .addItem(new BottomNavigationItem(R.drawable.application_select, "应用").setInactiveIcon(ContextCompat.getDrawable(this, R.drawable.application_un_select)))
                .addItem(new BottomNavigationItem(R.drawable.friend_select, "联系人").setInactiveIcon(ContextCompat.getDrawable(this, R.drawable.friend_un_select)))
                .addItem(new BottomNavigationItem(R.drawable.me_select, "个人中心").setInactiveIcon(ContextCompat.getDrawable(this, R.drawable.me_un_select)))
                .setMode(BottomNavigationBar.MODE_FIXED)
                .setActiveColor("#13227A")
                .setInActiveColor("#F78E62")
                .setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC)
                .setFirstSelectedPosition(1)
                .initialise();

        bottomNavigationBar.setTabSelectedListener(new BottomNavigationBar.SimpleOnTabSelectedListener() {
            @Override
            public void onTabSelected(int position) {
                switch (position) {
                    case 0:
                        vpMain.setCurrentItem(0);
                        break;
                    case 1:
                        appActivity.startBanner();
                        vpMain.setCurrentItem(1);
                        break;
                    case 2:
                        vpMain.setCurrentItem(2);
                        break;
                    case 3:
                        vpMain.setCurrentItem(3);
                        break;
                    default:
                        break;
                }
            }
        });

        vpMain.setOnPageChangeListener(new MyOnPageChangeListener());
        vpMain.setAdapter(mPagerAdapter);
        vpMain.setCurrentItem(1);
    }

    /**
     * 获取滚动信息
     */
    private void getData() {
        LoadingUtils.showLoading(mContext);
        Request request = ChildThreadUtil.getRequest(mContext, ConstantsUtil.GET_SCROLL_INFO, "");
        ConstantsUtil.okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                ChildThreadUtil.toastMsgHidden(mContext, getString(R.string.server_exception));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String jsonData = response.body().string().toString();
                if (JsonUtils.isGoodJson(jsonData)) {
                    Gson gson = new Gson();
                    final WorkingModel model = gson.fromJson(jsonData, WorkingModel.class);
                    if (model.isSuccess()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                LoadingUtils.hideLoading();
                                // 保存最新上传照片工序位置
                                WorkingBean workingBean = model.getData() == null ? new WorkingBean() : model.getData();
                                workingBean.setFlowType("1");
                                workingBean.setCreateTime(System.currentTimeMillis());
                                appActivity.setDate(urlList, workingBean);
                                mySettingActivity.checkVersion();
                                msgMainActivity.setDate(workingBean);
                                workingBean.saveOrUpdate();
                            }
                        });
                    } else {
                        ChildThreadUtil.checkTokenHidden(mContext, model.getMessage(), model.getCode());
                    }
                } else {
                    ChildThreadUtil.toastMsgHidden(mContext, getString(R.string.json_error));
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //开始轮播
        appActivity.startBanner();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //结束轮播
        appActivity.stopBanner();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!ConstantsUtil.isDownloadApk) {
            if (JudgeNetworkIsAvailable.isNetworkAvailable(this)) {
                if (!isUploadHead) {
                    //getData();
                    appActivity.setDate(urlList, new WorkingBean());
                    //msgMainActivity.setDate(null);
                }
            } else {
                List<WorkingBean> beanList = DataSupport.where("flowType=1 order by createTime").find(WorkingBean.class);
                appActivity.setDate(urlList, beanList != null && beanList.size() > 0 ? beanList.get(0) : null);
            }
        }
    }

    /**
     * 填充ViewPager的数据适配器
     */
    private PagerAdapter mPagerAdapter = new PagerAdapter() {
        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public int getCount() {
            return views.size();
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
            ((ViewPager) container).removeView(views.get(position));
        }

        @Override
        public Object instantiateItem(View container, int position) {
            ((ViewPager) container).addView(views.get(position));
            return views.get(position);
        }
    };

    /**
     * 页卡切换监听
     */
    private class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageSelected(int arg0) {
            bottomNavigationBar.selectTab(arg0);
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }
    }

    /**
     * 版本检查权限申请
     */
    private PromptListener choiceListener = new PromptListener() {
        @Override
        public void returnTrueOrFalse(boolean trueOrFalse) {
            if (trueOrFalse) {
                getPermissions();
            }
        }
    };

    @TargetApi(23)
    private void getPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> permissions = new ArrayList<>();
            // 读写权限
            addPermission(permissions, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            addPermission(permissions, android.Manifest.permission.READ_EXTERNAL_STORAGE);
            addPermission(permissions, android.Manifest.permission.REQUEST_INSTALL_PACKAGES);
            if (permissions.size() > 0) {
                requestPermissions(permissions.toArray(new String[permissions.size()]), 127);
            } else {
                mySettingActivity.downloadApk();
            }
        }
    }

    @TargetApi(23)
    private boolean addPermission(ArrayList<String> permissionsList, String permission) {
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(permission)) {
                return true;
            } else {
                permissionsList.add(permission);
                return false;
            }
        } else {
            return true;
        }
    }

    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mySettingActivity.downloadApk();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == RESULT_OK && data != null) {
            String path = data.getStringExtra("result"); // 图片地址
            if (!TextUtils.isEmpty(path)) {
                isUploadHead = true;
                uploadIcon(path);
            }
        } else if (requestCode == 1002 && resultCode == RESULT_OK && data != null) {
            List<String> pathList = data.getStringArrayListExtra("result");
            if (pathList != null && pathList.size() > 0) {
                isUploadHead = true;
                uploadIcon(pathList.get(0));
            }
        }
    }

    /**
     * 上传头像
     *
     * @param path
     */
    private void uploadIcon(String path) {
        LoadingUtils.showLoading(mContext);
        String fileName = path.substring(path.lastIndexOf("/") + 1);
        OkHttpUtils.post()
                .addFile("filesName", fileName, new File(path))
                .addHeader("token", (String) SpUtil.get(mContext, ConstantsUtil.TOKEN, ""))
                .url(ConstantsUtil.BASE_URL + ConstantsUtil.UPLOAD_ICON)
                .build()
                .execute(new com.zhy.http.okhttp.callback.Callback() {
                    @Override
                    public Object parseNetworkResponse(Response response, int id) throws Exception {
                        String jsonData = response.body().string().toString();
                        if (JsonUtils.isGoodJson(jsonData)) {
                            Gson gson = new Gson();
                            final BaseModel model = gson.fromJson(jsonData, BaseModel.class);
                            if (model.isSuccess()) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        String fileUrl = model.getFileUrl();
                                        List<UserInfo> userList = DataSupport.where("userId=?", String.valueOf(SpUtil.get(mContext, ConstantsUtil.USER_ID, ""))).find(UserInfo.class);
                                        if (userList != null && userList.size() > 0) {
                                            UserInfo user = userList.get(0);
                                            user.setImageUrl(ConstantsUtil.BASE_URL + ConstantsUtil.prefix + fileUrl);
                                            user.saveOrUpdate("userId=?", String.valueOf(SpUtil.get(mContext, ConstantsUtil.USER_ID, "")));
                                        }
                                        RequestOptions options = new RequestOptions().circleCrop();
                                        Glide.with(mContext).load(ConstantsUtil.BASE_URL + ConstantsUtil.prefix + fileUrl).apply(options).into(imgViewUserAvatar);
                                        ToastUtil.showShort(mContext, "头像上传成功");
                                    }
                                });
                            } else {
                                ChildThreadUtil.checkTokenHidden(mContext, model.getMessage(), model.getCode());
                            }
                        } else {
                            ChildThreadUtil.toastMsgHidden(mContext, getString(R.string.json_error));
                        }
                        isUploadHead = false;
                        return "";
                    }

                    @Override
                    public void onError(final Call call, final Exception e, final int id) {
                        isUploadHead = false;
                        ChildThreadUtil.toastMsgHidden(mContext, "头像上传失败！");
                    }

                    @Override
                    public void onResponse(Object response, int id) {
                    }

                    @Override
                    public void inProgress(float progress, long total, int id) {
                        super.inProgress(progress, total, id);
                    }
                });
    }

}
