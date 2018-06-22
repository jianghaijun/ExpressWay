package com.zj.expressway.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.zj.expressway.R;
import com.zj.expressway.application.MyApplication;
import com.zj.expressway.base.BaseActivity;
import com.zj.expressway.bean.PhotosBean;
import com.zj.expressway.bean.WorkingBean;
import com.zj.expressway.listener.GPSLocationListener;
import com.zj.expressway.listener.PermissionListener;
import com.zj.expressway.listener.PromptListener;
import com.zj.expressway.manager.GPSLocationManager;
import com.zj.expressway.model.WorkFlowModel;
import com.zj.expressway.service.LocationService;
import com.zj.expressway.utils.ConstantsUtil;
import com.zj.expressway.utils.FileUtil;
import com.zj.expressway.utils.ImageUtil;
import com.zj.expressway.utils.JudgeNetworkIsAvailable;
import com.zj.expressway.utils.LoadingUtils;
import com.zj.expressway.utils.ScreenManagerUtil;
import com.zj.expressway.utils.SpUtil;
import com.zj.expressway.utils.ToastUtil;
import com.zj.expressway.view.SonnyJackDragView;

import org.litepal.crud.DataSupport;
import org.xutils.common.util.DensityUtil;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hzw.graffiti.GraffitiActivity;
import cn.hzw.graffiti.GraffitiParams;

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
 *       Created by HaiJun on 2018/6/11 17:00
 *       工序列表主界面
 */
public class QualityInspectionActivity extends BaseActivity {
    @ViewInject(R.id.imgBtnLeft)
    private ImageButton imgBtnLeft;
    @ViewInject(R.id.imgBtnRight)
    private ImageButton imgBtnRight;
    @ViewInject(R.id.txtTitle)
    private TextView txtTitle;
    @ViewInject(R.id.btnTakePicture)
    private Button btnTakePicture;
    @ViewInject(R.id.vTakePicture)
    private View vTakePicture;
    @ViewInject(R.id.btnToBeAudited)
    private Button btnToBeAudited;
    @ViewInject(R.id.vToBeAudited)
    private View vToBeAudited;
    @ViewInject(R.id.btnFinish)
    private Button btnFinish;
    @ViewInject(R.id.vFinish)
    private View vFinish;
    @ViewInject(R.id.vpWorkingProcedure)
    private ViewPager vpWorkingProcedure;
    @ViewInject(R.id.llButtons)
    private LinearLayout llButtons;
    // viewPage
    private View layQuality, layToBeAudited, layFinish;
    private QualitySafetyInspectionActivity qualityActivity;
    private WorkingProcedureListActivity toBeAuditedActivity;
    private WorkingProcedureListActivity finishActivity;
    private ArrayList<View> views;
    private Activity mContext;
    // 定位信息
    private LocationService locationService;
    private final int SDK_PERMISSION_REQUEST = 127;
    private GPSLocationManager gpsLocationManager;
    private TextView txtPressLocal;
    private TextView txtLocation;
    private Uri uri = null;
    private String fileUrlName, strFilePath;
    private PhotosBean addPhotoBean;
    private File imgFile;

    private String sLocation, levelId, viewType;
    private double longitude, latitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_working_procedure);
        x.view().inject(this);
        mContext = this;
        ScreenManagerUtil.pushActivity(this);

        txtTitle.setText(R.string.app_name);
        imgBtnLeft.setVisibility(View.VISIBLE);
        imgBtnLeft.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.back_btn));
        imgBtnRight.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.search_btn));

        viewType = getIntent().getStringExtra("type");

        initViewPageData();
        getPermissions();
        initFilePath();

        llButtons.setVisibility(View.GONE);

        if (viewType.equals("1")) {
            btnTakePicture.setText("质量巡查");
        } else {
            btnTakePicture.setText("安全巡查");
        }

        btnToBeAudited.setText("未提交");
        btnFinish.setText("已提交");

        initRecyclerViewData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ConstantsUtil.isLoading) {
            initRecyclerViewData();
            ConstantsUtil.isLoading = false;
        }
    }

    /**
     * 初始化照片存储位置
     */
    private void initFilePath() {
        strFilePath = mContext.getExternalCacheDir().getAbsolutePath() + "/";
        imgFile = new File(strFilePath);
        if (!imgFile.exists()) {
            imgFile.mkdirs();
        }
    }

    /**
     * 初始化viewPage数据
     */
    private void initViewPageData() {
        // 将要分页显示的View装入数组中
        LayoutInflater viewLI = LayoutInflater.from(this);
        layQuality = viewLI.inflate(R.layout.layout_quality_safety_details, null);
        layToBeAudited = viewLI.inflate(R.layout.layout_msg, null);
        layFinish = viewLI.inflate(R.layout.layout_msg, null);
        // 质量巡查
        qualityActivity = new QualitySafetyInspectionActivity(mContext, layQuality);
        // 待审核
        toBeAuditedActivity = new WorkingProcedureListActivity(mContext, layToBeAudited);
        // 已完成
        finishActivity = new WorkingProcedureListActivity(mContext, layFinish);

        txtPressLocal = (TextView) layQuality.findViewById(R.id.txtPressLocal);
        txtLocation = (TextView) layQuality.findViewById(R.id.txtLocation);

        //每个页面的view数据
        views = new ArrayList<>();
        views.add(layQuality);
        views.add(layToBeAudited);
        views.add(layFinish);

        vpWorkingProcedure.setOnPageChangeListener(new MyOnPageChangeListener());
        vpWorkingProcedure.setAdapter(mPagerAdapter);
        vpWorkingProcedure.setCurrentItem(0);
    }

    /**
     * 初始化列表数据
     */
    private void initRecyclerViewData() {
        qualityActivity.initData(viewType);
        toBeAuditedActivity.initData(4, btnToBeAudited, null);
        finishActivity.initData(5, btnFinish, null);
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
            if (arg0 == 0) {
                imgBtnRight.setVisibility(View.GONE);
            } else {
                imgBtnRight.setVisibility(View.VISIBLE);
            }
            setStates(arg0);
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }
    }

    /**
     * 设置背景
     *
     * @param option
     */
    private void setStates(int option) {
        // 待拍照
        btnTakePicture.setTextColor(ContextCompat.getColor(mContext, R.color.black));
        vTakePicture.setBackgroundColor(ContextCompat.getColor(mContext, R.color.black));
        // 待审核
        btnToBeAudited.setTextColor(ContextCompat.getColor(mContext, R.color.black));
        vToBeAudited.setBackgroundColor(ContextCompat.getColor(mContext, R.color.black));
        // 已完成
        btnFinish.setTextColor(ContextCompat.getColor(mContext, R.color.black));
        vFinish.setBackgroundColor(ContextCompat.getColor(mContext, R.color.black));

        switch (option) {
            case 0:
                btnTakePicture.setTextColor(ContextCompat.getColor(mContext, R.color.main_check_bg));
                vTakePicture.setBackgroundColor(ContextCompat.getColor(mContext, R.color.main_check_bg));
                break;
            case 1:
                btnToBeAudited.setTextColor(ContextCompat.getColor(mContext, R.color.main_check_bg));
                vToBeAudited.setBackgroundColor(ContextCompat.getColor(mContext, R.color.main_check_bg));
                break;
            case 2:
                btnFinish.setTextColor(ContextCompat.getColor(mContext, R.color.main_check_bg));
                vFinish.setBackgroundColor(ContextCompat.getColor(mContext, R.color.main_check_bg));
                break;
        }
    }

    /**
     * 获取定位权限
     */
    @TargetApi(23)
    private void getPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> permissions = new ArrayList<>();
            // 定位精确位置
            if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if (checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);
            }
            // 读写权限
            if (addPermission(permissions, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {}
            // 读取电话状态权限
            if (addPermission(permissions, android.Manifest.permission.READ_PHONE_STATE)) {}

            if (permissions.size() > 0) {
                requestPermissions(permissions.toArray(new String[permissions.size()]), SDK_PERMISSION_REQUEST);
            } else {
                if (JudgeNetworkIsAvailable.isNetworkAvailable(this)) {
                    locationService = ((MyApplication) getApplication()).locationService;
                    locationService.registerListener(mListener);
                    //注册监听
                    int type = getIntent().getIntExtra("from", 0);
                    if (type == 0) {
                        locationService.setLocationOption(locationService.getDefaultLocationClientOption());
                    } else if (type == 1) {
                        locationService.setLocationOption(locationService.getOption());
                    }
                    locationService.start();// 定位SDK
                } else {
                    //开启GPS定位
                    gpsLocationManager = GPSLocationManager.getInstances(this);
                    gpsLocationManager.start(new GpsListener());
                }
            }
        } else {
            if (JudgeNetworkIsAvailable.isNetworkAvailable(this)) {
                locationService = ((MyApplication) getApplication()).locationService;
                locationService.registerListener(mListener);
                //注册监听
                int type = getIntent().getIntExtra("from", 0);
                if (type == 0) {
                    locationService.setLocationOption(locationService.getDefaultLocationClientOption());
                } else if (type == 1) {
                    locationService.setLocationOption(locationService.getOption());
                }
                locationService.start();// 定位SDK
            } else {
                //开启GPS定位
                gpsLocationManager = GPSLocationManager.getInstances(this);
                gpsLocationManager.start(new GpsListener());
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
        if (JudgeNetworkIsAvailable.isNetworkAvailable(this)) {
            locationService = ((MyApplication) getApplication()).locationService;
            locationService.registerListener(mListener);
            //注册监听
            int type = getIntent().getIntExtra("from", 0);
            if (type == 0) {
                locationService.setLocationOption(locationService.getDefaultLocationClientOption());
            } else if (type == 1) {
                locationService.setLocationOption(locationService.getOption());
            }
            locationService.start();// 定位SDK
        } else {
            //开启GPS定位
            gpsLocationManager = GPSLocationManager.getInstances(this);
            gpsLocationManager.start(new GpsListener());
        }
    }

    /**
     * GPS定位监听
     */
    private class GpsListener implements GPSLocationListener {
        @Override
        public void UpdateLocation(Location location) {
            if (location != null) {
                longitude = location.getLongitude();
                latitude = location.getLatitude();
                sLocation = "";
                txtLocation.setText("经度：" + location.getLongitude() + " 纬度：" + location.getLatitude());
            }
        }

        @Override
        public void UpdateStatus(String provider, int status, Bundle extras) {}

        @Override
        public void UpdateGPSProviderStatus(int gpsStatus) {}
    }

    /**
     * 定位结果回调
     */
    private BDAbstractLocationListener mListener = new BDAbstractLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                StringBuffer sb = new StringBuffer(256);
                sb.append(location.getAddrStr());
                longitude = location.getLongitude();
                latitude = location.getLatitude();
                sLocation = sb.toString() == null || TextUtils.isEmpty(sb.toString()) || sb.toString().equals("null") ? "" : sb.toString();
                txtLocation.setText(sLocation);
            }
        }

    };

    /**
     * 旋转图片
     *
     * @param angle
     * @return Bitmap
     */
    public static Bitmap rotateImageView(int angle, String path) {
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        // 此处采样，导致分辨率降到1/4,否则会报OOM
        bitmapOptions.inSampleSize = 1;
        Bitmap cameraBitmap = BitmapFactory.decodeFile(path, bitmapOptions);
        // 旋转图片 动作
        Matrix matrix = new Matrix();

        matrix.postRotate(angle);
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(cameraBitmap, 0, 0, cameraBitmap.getWidth(), cameraBitmap.getHeight(), matrix, true);
        cameraBitmap.recycle();
        return resizedBitmap;
    }

    /**
     * 保存图片
     *
     * @param bm
     * @param path
     * @param filename
     * @return
     */
    private String saveBitmap(Bitmap bm, String path, String filename) {
        File f = new File(path);
        if (!f.exists()) {
            f.mkdirs();
        }

        path = path + "/" + filename;
        File f2 = new File(path);
        try {
            FileOutputStream out = new FileOutputStream(f2);
            bm.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            return path;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 搜索
     * @param levelId
     */
    private void searchProcessData(String levelId) {
        switch (vpWorkingProcedure.getCurrentItem()) {
            case 1:
                toBeAuditedActivity.initData(4, btnToBeAudited, levelId);
                break;
            case 2:
                finishActivity.initData(5, btnFinish, levelId);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 10002) {
                searchProcessData(data.getStringExtra("levelId"));
            } else if (requestCode == 110) {
                txtPressLocal.setText(data.getStringExtra("procedureName"));
                levelId = data.getStringExtra("levelId");
                qualityActivity.updateFileList(levelId);
            } else if (requestCode == 1) {
                if (data == null) {
                    return;
                }
                Bundle extras = data.getExtras();
                if (extras != null) {
                    String path = extras.getString("maxImgPath");
                    if (path != null) {
                        uri = Uri.parse(path);
                        // 如果图像是旋转的，需要旋转后保存,目前只发现三星如此
                        int degree = extras.getInt("degree");
                        switch (degree) {
                            case 0:
                                degree = 90;
                                break;
                            case 90:
                                degree = 180;
                                break;
                            case 180:
                                degree = 270;
                                break;
                            case 270:
                                degree = 0;
                                break;
                        }
                        if (degree != 0) {
                            Bitmap bitmap = rotateImageView(degree, path);
                            String newPath = saveBitmap(bitmap, ConstantsUtil.SAVE_PATH, System.currentTimeMillis() + ".png");
                            uri = Uri.parse("file://" + newPath);
                        }
                        LoadingUtils.hideLoading();
                    }
                }

                fileUrlName = String.valueOf(System.currentTimeMillis()) + ".png";
                // 涂鸦参数
                GraffitiParams params = new GraffitiParams();
                // 图片路径
                params.mImagePath = uri.toString();

                params.mSavePath = ConstantsUtil.SAVE_PATH + fileUrlName;
                // 初始画笔大小
                params.mPaintSize = 20;
                // 启动涂鸦页面
                GraffitiActivity.startActivityForResult(mContext, params, 202);
            } else if (requestCode == 201) {
                qualityActivity.submitData(data.getStringExtra("reviewNodeId"), data.getStringExtra("userId"), data.getStringExtra("userName"), data.getStringExtra("type"), new PromptListener() {
                    @Override
                    public void returnTrueOrFalse(boolean trueOrFalse) {
                        if (trueOrFalse) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    initRecyclerViewData();
                                }
                            });
                        }
                    }
                });
            } else if (requestCode == 202) {
                addPhotoBean = new PhotosBean();
                addPhotoBean.setIsToBeUpLoad(1);
                addPhotoBean.setUrl(ConstantsUtil.SAVE_PATH + fileUrlName);
                addPhotoBean.setProcessId(levelId);
                addPhotoBean.setThumbUrl(ConstantsUtil.SAVE_PATH + fileUrlName);
                addPhotoBean.setPhotoName(fileUrlName);
                addPhotoBean.setCheckFlag("-1");
                addPhotoBean.setIsNewAdd(1);
                addPhotoBean.setRoleFlag("1");
                addPhotoBean.setUserId((String) SpUtil.get(mContext, ConstantsUtil.USER_ID, ""));
                addPhotoBean.setPhotoType((String) SpUtil.get(mContext, ConstantsUtil.USER_TYPE, ""));
                addPhotoBean.setCreateTime(System.currentTimeMillis());
                addPhotoBean.save();
                qualityActivity.updateFileList(levelId);
            }
        }
    }

    /**
     * 点击事件
     *
     * @param v
     */
    @Event({ R.id.imgBtnLeft, R.id.imgBtnRight, R.id.btnTakePicture, R.id.btnToBeAudited, R.id.btnFinish })
    private void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgBtnLeft:
                this.finish();
                break;
            case R.id.imgBtnRight:
                Intent intent = new Intent(mContext, ContractorTreeActivity.class);
                if (viewType.equals("1")) {
                    intent.putExtra("type", "2");
                } else {
                    intent.putExtra("type", "3");
                }
                startActivityForResult(intent, 10002);
                break;
            case R.id.btnTakePicture:
                vpWorkingProcedure.setCurrentItem(0);
                break;
            case R.id.btnToBeAudited:
                vpWorkingProcedure.setCurrentItem(1);
                break;
            case R.id.btnFinish:
                vpWorkingProcedure.setCurrentItem(2);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ScreenManagerUtil.popActivity(this);
        // 终止定位
        if (gpsLocationManager != null) {
            gpsLocationManager.stop();
        }
        if (locationService != null) {
            locationService.stop();
        }
    }
}
