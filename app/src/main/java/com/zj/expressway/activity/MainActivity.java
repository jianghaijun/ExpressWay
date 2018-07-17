package com.zj.expressway.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zj.expressway.InJavaScript.MailListInJavaScript;
import com.zj.expressway.R;
import com.zj.expressway.base.BaseActivity;
import com.zj.expressway.base.BaseModel;
import com.zj.expressway.bean.MainPageBean;
import com.zj.expressway.bean.UserInfo;
import com.zj.expressway.dialog.PromptDialog;
import com.zj.expressway.listener.PromptListener;
import com.zj.expressway.model.MainPageModel;
import com.zj.expressway.utils.AppInfoUtil;
import com.zj.expressway.utils.ChildThreadUtil;
import com.zj.expressway.utils.ConstantsUtil;
import com.zj.expressway.utils.JsonUtils;
import com.zj.expressway.utils.JudgeNetworkIsAvailable;
import com.zj.expressway.utils.LoadingUtils;
import com.zj.expressway.utils.SpUtil;
import com.zj.expressway.utils.ToastUtil;
import com.zj.expressway.utils.WebViewSettingUtil;
import com.zj.expressway.view.CustomWebViewClient;

import org.litepal.crud.DataSupport;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.hutool.core.util.StrUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by HaiJun on 2018/6/11 16:44
 * 主界面
 */
public class MainActivity extends BaseActivity implements CustomWebViewClient.WebViewClientListener {
    @ViewInject(R.id.vpMain)
    private ViewPager vpMain;
    @ViewInject(R.id.imgBtnRight)
    private ImageButton imgBtnRight;
    @ViewInject(R.id.rlMsg)
    private RelativeLayout rlMsg;
    @ViewInject(R.id.txtSubmitPhoneNum)
    private TextView txtSubmitPhoneNum;
    @ViewInject(R.id.bottomNavigationBar)
    private BottomNavigationBar bottomNavigationBar;

    private WebView wvMailList;
    private TextView txtHorseRaceLamp;
    private TextView txtToBeExamined;
    private TextView txtHaveBeenApproved;
    private TextView txtTodayCompletionNum;
    private ImageView imgViewUserAvatar;

    private Activity mContext;
    private boolean isLoadSuccess = false, isBigsLoad = false;

    // 子布局
    private View layoutApp, layoutFriends, layoutMap, layoutMe;
    private AppActivity appActivity;
    private BidsManageActivity bidsManageActivity;
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

        // 显示消息数量
        rlMsg.setVisibility(View.VISIBLE);

        imgBtnRight.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.refresh));

        //将要分页显示的View装入数组中
        LayoutInflater viewLI = LayoutInflater.from(this);
        layoutApp = viewLI.inflate(R.layout.layout_app, null);
        layoutFriends = viewLI.inflate(R.layout.layout_mail_list, null);
        layoutMap = viewLI.inflate(R.layout.layout_contractor_tree, null);
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
        // 应用
        appActivity = new AppActivity(this, layoutApp);
        // 我的
        mySettingActivity = new MySettingActivity(mContext, layoutMe, choiceListener);
        // 通讯录--->html
        wvMailList = (WebView) layoutFriends.findViewById(R.id.wvMailList);
        // 标段管理
        bidsManageActivity = new BidsManageActivity(this, layoutMap);

        // 滚动信息
        txtHorseRaceLamp = (TextView) layoutApp.findViewById(R.id.txtHorseRaceLamp);
        txtToBeExamined = (TextView) layoutApp.findViewById(R.id.txtToBeExamined);
        txtHaveBeenApproved = (TextView) layoutApp.findViewById(R.id.txtHaveBeenApproved);
        txtTodayCompletionNum = (TextView) layoutApp.findViewById(R.id.txtTodayCompletionNum);

        //每个页面的view数据
        views = new ArrayList<>();
        views.add(layoutApp);
        views.add(layoutFriends);
        views.add(layoutMap);
        views.add(layoutMe);

        // 设置底部导航按钮
        bottomNavigationBar
                .addItem(new BottomNavigationItem(R.drawable.application_select, "应用").setInactiveIcon(ContextCompat.getDrawable(this, R.drawable.application_un_select)))
                .addItem(new BottomNavigationItem(R.drawable.friend_select, "通讯录").setInactiveIcon(ContextCompat.getDrawable(this, R.drawable.friend_un_select)))
                .addItem(new BottomNavigationItem(R.drawable.msg_select, "标段管理").setInactiveIcon(ContextCompat.getDrawable(this, R.drawable.msg_un_select)))
                .addItem(new BottomNavigationItem(R.drawable.me_select, "个人中心").setInactiveIcon(ContextCompat.getDrawable(this, R.drawable.me_un_select)))
                .setMode(BottomNavigationBar.MODE_FIXED)
                .setActiveColor("#13227A")
                .setInActiveColor("#F78E62")
                .setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC)
                .setFirstSelectedPosition(0)
                .initialise();

        // 点击事件
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
        vpMain.setCurrentItem(0);

        if (!JudgeNetworkIsAvailable.isNetworkAvailable(this)) {
            // 轮播图信息
            List<MainPageBean> mainPageBeanList = DataSupport.where("1=1").find(MainPageBean.class);
            // 滚动信息---数量信息
            String scrollContext = (String) SpUtil.get(mContext, "ScrollContext", "");
            // 开启跑马灯
            txtHorseRaceLamp.setText(scrollContext);
            txtHorseRaceLamp.setSelected(true);
            // 待办
            String todoCount = (String) SpUtil.get(mContext, "todoCount", "0");
            txtToBeExamined.setText(todoCount);
            // 已办
            String hasTodoCount = (String) SpUtil.get(mContext, "hasTodoCount", "0");
            txtHaveBeenApproved.setText(hasTodoCount);
            String todayFinishNum = (String) SpUtil.get(mContext, "todayFinishNum", "0");
            txtTodayCompletionNum.setText(todayFinishNum);

            String unReadNum = (String) SpUtil.get(mContext, "unReadNum", "0");
            int num = Integer.valueOf(unReadNum);
            if (num != 0) {
                txtSubmitPhoneNum.setVisibility(View.VISIBLE);
                if (num > 99) {
                    txtSubmitPhoneNum.setTextSize(6);
                }
                txtSubmitPhoneNum.setText(num > 99 ? "99+" : num+"");
            } else {
                txtSubmitPhoneNum.setVisibility(View.GONE);
            }
            appActivity.setDate(mainPageBeanList);
        }
    }

    /**
     * 获取首页数据
     */
    private void getMainPageData() {
        LoadingUtils.showLoading(mContext);
        Request request = ChildThreadUtil.getRequest(mContext, ConstantsUtil.getIndexviewHomePage, "");
        ConstantsUtil.okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                ChildThreadUtil.toastMsgHidden(mContext, mContext.getString(R.string.server_exception));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String jsonData = response.body().string().toString();
                if (JsonUtils.isGoodJson(jsonData)) {
                    Gson gson = new Gson();
                    final MainPageModel model = gson.fromJson(jsonData, MainPageModel.class);
                    if (model.isSuccess()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // 保存轮播图信息
                                if (model.getData().getViewList() != null && model.getData().getViewList().size() != 0) {
                                    DataSupport.deleteAll(MainPageBean.class);
                                    for (MainPageBean bean : model.getData().getViewList()) {
                                        bean.save();
                                    }
                                }
                                // 保存滚动信息---数量信息
                                if (model.getData().getNewsList() != null && model.getData().getNewsList().size() != 0) {
                                    SpUtil.put(mContext, "ScrollContext", model.getData().getNewsList().get(0).getViewSummary());
                                    // 开启跑马灯
                                    txtHorseRaceLamp.setText(model.getData().getNewsList().get(0).getViewSummary());
                                    txtHorseRaceLamp.setSelected(true);
                                }
                                SpUtil.put(mContext, "todoCount", model.getData().getTodoCount());
                                txtToBeExamined.setText(model.getData().getTodoCount());
                                SpUtil.put(mContext, "hasTodoCount", model.getData().getHasTodoCount());
                                txtHaveBeenApproved.setText(model.getData().getHasTodoCount());
                                SpUtil.put(mContext, "todayFinishNum", model.getData().getTodayFinishNum());
                                txtTodayCompletionNum.setText(model.getData().getTodayFinishNum());
                                appActivity.setDate(model.getData().getViewList());
                                LoadingUtils.hideLoading();

                                SpUtil.put(mContext, "unSubmittedNum", StrUtil.isEmpty(model.getData().getUnSubmitted()) ? "0" : model.getData().getUnSubmitted());
                                SpUtil.put(mContext, "unReadNum", StrUtil.isEmpty(model.getData().getUnReadNum()) ? "0" : model.getData().getUnReadNum());

                                String unReadNum = (String) SpUtil.get(mContext, "unReadNum", "0");
                                int num = Integer.valueOf(unReadNum);
                                if (num != 0) {
                                    txtSubmitPhoneNum.setVisibility(View.VISIBLE);
                                    if (num > 99) {
                                        txtSubmitPhoneNum.setTextSize(6);
                                    }
                                    txtSubmitPhoneNum.setText(num > 99 ? "99+" : num+"");
                                } else {
                                    txtSubmitPhoneNum.setVisibility(View.GONE);
                                }
                                int version = AppInfoUtil.compareVersion(model.getData().getVersion(), AppInfoUtil.getVersion(mContext));
                                if (version == 1) {
                                    // 发现新版本
                                    mySettingActivity.downloadApk(model.getData().getFileLength());
                                }
                            }
                        });
                    } else {
                        ChildThreadUtil.checkTokenHidden(mContext, model.getMessage(), model.getCode());
                    }
                } else {
                    ChildThreadUtil.toastMsgHidden(mContext, mContext.getString(R.string.json_error));
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
        if (ConstantsUtil.jumpPersonalInfo) {
            ConstantsUtil.jumpPersonalInfo = false;
            vpMain.setCurrentItem(3);
        } else if (!ConstantsUtil.isDownloadApk) {
            if (JudgeNetworkIsAvailable.isNetworkAvailable(this)) {
                getMainPageData();
                if (!isUploadHead) {
                    // 是否第一次启动---->异步同步工序
                    boolean isFirstStar = (boolean) SpUtil.get(mContext, "isFirstStar", true);
                    if (isFirstStar) {
                        SpUtil.put(mContext, "isFirstStar", false);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    mySettingActivity.syncProcessDictionary(false);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                }
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

    /**
     * 页卡切换监听
     */
    private class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageSelected(int arg0) {
            if (arg0 == 0) {
                rlMsg.setVisibility(View.VISIBLE);
            } else {
                rlMsg.setVisibility(View.GONE);
            }

            if (arg0 == 2) {
                imgBtnRight.setVisibility(View.VISIBLE);
            } else {
                imgBtnRight.setVisibility(View.GONE);
            }

            bottomNavigationBar.selectTab(arg0);
            if (arg0 == 1 && !isLoadSuccess) {
                isLoadSuccess = !isLoadSuccess;
                WebViewSettingUtil.setSetting(wvMailList);
                wvMailList.addJavascriptInterface(new MailListInJavaScript(mContext, wvMailList), "android_api");
                wvMailList.setWebViewClient(new CustomWebViewClient(MainActivity.this, mContext));
                wvMailList.loadUrl(ConstantsUtil.Mail_Url);
            }

            if (arg0 == 2 && !isBigsLoad) {
                isBigsLoad = !isBigsLoad;
                SpUtil.put(mContext, ConstantsUtil.PROCESS_LIST_TYPE, "1");
                SpUtil.put(mContext, "showSelectBtn", false);
                bidsManageActivity.setDataInfo();
            }
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
        } else if (requestCode == 1005 && resultCode == RESULT_OK && data != null) {
            // 子级新增
            String pileNo = data.getStringExtra("pileNo");
            int position = data.getIntExtra("position", 0);
            ArrayList<String> dictId = data.getStringArrayListExtra("dictIdList");
            bidsManageActivity.addLevel(pileNo, position, dictId);
        } else if (requestCode == 1006 && resultCode == RESULT_OK && data != null) {
            String pileNo = data.getStringExtra("pileNo");
            String levelId = data.getStringExtra("levelId");
            int position = data.getIntExtra("position", 0);
            ArrayList<String> dictId = data.getStringArrayListExtra("dictIdList");
            ArrayList<String> oldDictId = data.getStringArrayListExtra("oldDictIdList");
            ArrayList<String> oldDictName = data.getStringArrayListExtra("oldDictNameList");
            bidsManageActivity.updateLevel(pileNo, levelId, position, dictId, oldDictId, oldDictName);
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

    @Event({R.id.rlMsg, R.id.imgBtnMsg, R.id.txtSubmitPhoneNum, R.id.imgBtnRight})
    private void onClick(View v) {
        switch (v.getId()) {
            case R.id.rlMsg:
            case R.id.imgBtnMsg:
            case R.id.txtSubmitPhoneNum:
                Intent intent = new Intent(mContext, MsgActivity.class);
                startActivity(intent);
                break;
            case R.id.imgBtnRight:
                bidsManageActivity.setDataInfo();
                break;
        }
    }
}
