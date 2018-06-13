package com.zj.expressway.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.SensorManager;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.OrientationEventListener;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.google.gson.Gson;
import com.zj.expressway.R;
import com.zj.expressway.adapter.ContractorDetailsPhotoAdapter;
import com.zj.expressway.adapter.TimeLineAdapter;
import com.zj.expressway.application.MyApplication;
import com.zj.expressway.base.BaseActivity;
import com.zj.expressway.bean.PhotosBean;
import com.zj.expressway.bean.PictureBean;
import com.zj.expressway.bean.WorkingBean;
import com.zj.expressway.dialog.HorizontalScreenHintDialog;
import com.zj.expressway.dialog.PromptDialog;
import com.zj.expressway.dialog.RejectDialog;
import com.zj.expressway.dialog.SelectionAuditorDialog;
import com.zj.expressway.dialog.UpLoadPhotosDialog;
import com.zj.expressway.listener.FileInfoListener;
import com.zj.expressway.listener.GPSLocationListener;
import com.zj.expressway.listener.PermissionListener;
import com.zj.expressway.listener.PromptListener;
import com.zj.expressway.listener.ReportListener;
import com.zj.expressway.listener.ShowPhotoListener;
import com.zj.expressway.manager.GPSLocationManager;
import com.zj.expressway.model.PictureModel;
import com.zj.expressway.model.ProcessDetailsModel;
import com.zj.expressway.model.TimeLineModel;
import com.zj.expressway.service.LocationService;
import com.zj.expressway.utils.AppInfoUtil;
import com.zj.expressway.utils.ConstantsUtil;
import com.zj.expressway.utils.FileUtil;
import com.zj.expressway.utils.ImageUtil;
import com.zj.expressway.utils.JsonUtils;
import com.zj.expressway.utils.JudgeNetworkIsAvailable;
import com.zj.expressway.utils.LoadingUtils;
import com.zj.expressway.utils.OrderStatusUtil;
import com.zj.expressway.utils.ScreenManagerUtil;
import com.zj.expressway.utils.SpUtil;
import com.zj.expressway.utils.ToastUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;
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

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.RequestBody;
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
 *       Created by HaiJun on 2018/6/11 16:39
 *       工序详情
 */
public class ContractorDetailsActivity extends BaseActivity {
    @ViewInject(R.id.imgBtnLeft)
    private ImageButton imgBtnLeft;
    @ViewInject(R.id.txtTitle)
    private TextView txtTitle;
    /*数据信息*/
    @ViewInject(R.id.txtWorkingNo)
    private TextView txtWorkingNo;
    @ViewInject(R.id.txtWorkingName)
    private TextView txtWorkingName;
    @ViewInject(R.id.txtEntryTime)
    private TextView txtEntryTime;
    @ViewInject(R.id.txtLocation)
    private TextView txtLocation;
    @ViewInject(R.id.txtDistanceAngle)
    private TextView txtDistanceAngle;
    @ViewInject(R.id.txtTakePhotoNum)
    private TextView txtTakePhotoNum;
    @ViewInject(R.id.txtTakePhotoRequirement)
    private TextView txtTakePhotoRequirement;
    @ViewInject(R.id.btnLocalSave)
    private Button btnLocalSave;
    @ViewInject(R.id.btnReject)
    private Button btnReject;
    @ViewInject(R.id.btnExamine)
    private Button btnExamine;
    @ViewInject(R.id.btnLocalPreservation)
    private Button btnLocalPreservation;
    @ViewInject(R.id.imgBtnAdd)
    private ImageButton imgBtnAdd;
    @ViewInject(R.id.rlRejectPhotos)
    private RelativeLayout rlRejectPhotos;
    @ViewInject(R.id.rlFixedPoint)
    private RelativeLayout rlFixedPoint;
    @ViewInject(R.id.llButton)
    private LinearLayout llButton;
    @ViewInject(R.id.workingNo)
    private TextView workingNo;
    @ViewInject(R.id.workingName)
    private TextView workingName;
    @ViewInject(R.id.txtRejectPhoto)
    private TextView txtRejectPhoto;
    /*层厚*/
    @ViewInject(R.id.edtPositionOfPileNumber1)
    private EditText edtPositionOfPileNumber1;
    @ViewInject(R.id.edtElevation1)
    private EditText edtElevation1;
    @ViewInject(R.id.edtPositionOfPileNumber2)
    private EditText edtPositionOfPileNumber2;
    @ViewInject(R.id.edtElevation2)
    private EditText edtElevation2;
    @ViewInject(R.id.edtPositionOfPileNumber3)
    private EditText edtPositionOfPileNumber3;
    @ViewInject(R.id.edtElevation3)
    private EditText edtElevation3;
    @ViewInject(R.id.edtPositionOfPileNumber4)
    private EditText edtPositionOfPileNumber4;
    @ViewInject(R.id.edtElevation4)
    private EditText edtElevation4;
    @ViewInject(R.id.edtPositionOfPileNumber5)
    private EditText edtPositionOfPileNumber5;
    @ViewInject(R.id.edtElevation5)
    private EditText edtElevation5;
    @ViewInject(R.id.rvContractorDetails)
    private RecyclerView rvContractorDetails;

    /*------------------------------时间轴Start-----------------------------------*/
    @ViewInject(R.id.rvTimeMarker)
    private RecyclerView rvTimeMarker;
    private List<TimeLineModel> mDataList = new ArrayList<>();
    private TimeLineAdapter timeLineAdapter;
    /*------------------------------时间轴End-------------------------------------*/
    /*------------------------------屏幕方向监听Start------------------------------*/
    private AlbumOrientationEventListener mAlbumOrientationEventListener;
    private int mOrientation = 0;
    private boolean isHorizontalScreen = false;
    /*------------------------------屏幕方向监听End--------------------------------*/
    /*------------------------------图片信息Start---------------------------------*/
    private ContractorDetailsPhotoAdapter adapter;
    private List<PhotosBean> phoneList = new ArrayList<>();
    private List<PhotosBean> localPhotoList = new ArrayList<>();
    /*------------------------------图片信息End-----------------------------------*/
    /*------------------------------定位信息Start---------------------------------*/
    private LocationService locationService;
    private final int SDK_PERMISSION_REQUEST = 127;
    private GPSLocationManager gpsLocationManager;
    /*------------------------------定位信息End-----------------------------------*/
    /*------------------------------拍照Start-------------------------------------*/
    private Uri uri = null;
    private String fileUrlName, strFilePath;
    private PhotosBean addPhotoBean;
    private File imgFile;
    /*------------------------------拍照End---------------------------------------*/
    private Context mContext;
    private String processId, taskId, processState, sLocation, userLevel, processPath, strType;
    private double longitude, latitude;
    // 最少拍照张数
    private int num;
    private Gson gson = new Gson();
    /**
     * 是否已点击上报按钮
     */
    public static boolean isCanSelect = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_contractor_details);

        mContext = this;
        x.view().inject(this);
        ScreenManagerUtil.pushActivity(this);

        imgBtnLeft.setVisibility(View.VISIBLE);
        imgBtnLeft.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.back_btn));
        txtTitle.setText(R.string.app_name);

        // 工序id
        processId = getIntent().getStringExtra("processId");
        // 任务id
        taskId = getIntent().getStringExtra("taskId");
        // 工序状态
        processState = getIntent().getStringExtra("processState");
        // 工序位置
        processPath = getIntent().getStringExtra("processPath");
        // 根据用户级别显示不同按钮(0:施工人员; 1:质检部长; 2:监理; 3:领导 21:监理组长 22：总监)
        userLevel = (String) SpUtil.get(this, ConstantsUtil.USER_LEVEL, "");

        initFilePath();
        getPermissions();
        initData();

        if (JudgeNetworkIsAvailable.isNetworkAvailable(this)) {
            getData();
        } else {
            // 查询本地保存的照片
            phoneList = DataSupport.where("isToBeUpLoad = 1 AND userId = ? AND processId = ? order by createTime desc", (String) SpUtil.get(mContext, ConstantsUtil.USER_ID, ""), processId).find(PhotosBean.class);
            localPhotoList = phoneList;
            List<WorkingBean> workList = DataSupport.where("processId = ?", processId).find(WorkingBean.class);
            WorkingBean bean = null;
            if (workList != null && workList.size() > 0) {
                bean = workList.get(0);
            }
            setData(bean);
        }

        initTimeLineView();

        // 屏幕方向监听
        mAlbumOrientationEventListener = new AlbumOrientationEventListener(mContext, SensorManager.SENSOR_DELAY_NORMAL);
        if (mAlbumOrientationEventListener.canDetectOrientation()) {
            mAlbumOrientationEventListener.enable();
        }

        // 是否直接弹出相机
        boolean isPopTakePhoto = getIntent().getBooleanExtra("isPopTakePhoto", false);
        if (isPopTakePhoto) {
            takePictures();
        }
    }

    /**
     * 设置列表方向
     *
     * @return
     */
    private LinearLayoutManager getLinearLayoutManager() {
        return new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
    }

    /**
     * 初始化时间轴
     */
    private void initTimeLineView() {
        rvTimeMarker.setLayoutManager(getLinearLayoutManager());
        rvTimeMarker.setHasFixedSize(true);
        setDataListItems();
        timeLineAdapter = new TimeLineAdapter(mDataList);
        rvTimeMarker.setAdapter(timeLineAdapter);
        rvTimeMarker.setNestedScrollingEnabled(false);
    }

    /**
     * 添加时间轴数据
     */
    private void setDataListItems() {
        mDataList.add(new TimeLineModel("Courier is out to delivery your order", "2017-02-12 08:00", OrderStatusUtil.ACTIVE));
        mDataList.add(new TimeLineModel("Item has reached courier facility at New Delhir facility at New Delhi", "2017-02-11 21:00", OrderStatusUtil.COMPLETED));
        mDataList.add(new TimeLineModel("Order confirmed by seller", "2017-02-10 14:30", OrderStatusUtil.COMPLETED));
        mDataList.add(new TimeLineModel("Order placed successfully", "2017-02-10 14:00", OrderStatusUtil.COMPLETED));
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
     * 初始化数据
     */
    private void initData() {
        // 质量(0)--安全(1)
        String type = (String) SpUtil.get(this, ConstantsUtil.USER_TYPE, "");
        if (type.equals("1")) {
            strType = "隐患";
            workingNo.setText("隐患编号");
            workingName.setText("隐患内容");
        } else {
            strType = "工序";
        }

        switch (userLevel) {
            // 1:质检部长(驳回--审核)
            case "1":
                imgBtnAdd.setVisibility(View.GONE); // 隐藏拍照按钮
                btnLocalSave.setVisibility(View.GONE);  // 隐藏保存照片至本地
                break;
            // 2:监理(驳回--审核--保存照片到本地)
            case "2":
                break;
            // 3:领导(查看)
            case "3":
            case "22":
                imgBtnAdd.setVisibility(View.GONE); // 隐藏拍照功能
                llButton.setVisibility(View.GONE); // 隐藏功能按钮
                break;
            // 21:监理组长--审核
            case "21":
                imgBtnAdd.setVisibility(View.GONE); // 隐藏拍照功能
                btnLocalSave.setVisibility(View.GONE);  // 隐藏保存照片至本地
                btnExamine.setText("终审通过");
                break;
            // 其它(审核)
            default:
                btnReject.setVisibility(View.GONE);
                break;
        }

        // 如果是驳回状态 显示驳回原因(0:待拍照1:已拍照 2:已提交初审 3:初审驳回 4:初审通过 5:复审驳回 6:复审通过 7:终审驳回 8:终审通过)
        if (processState.equals("3") || processState.equals("5") || processState.equals("7")) {
            rlRejectPhotos.setVisibility(View.VISIBLE);
        }

        // 所有填方工序显示层厚定点位置 隐藏当前位置和定位位置
        if (processPath.contains("填方")) {
            rlFixedPoint.setVisibility(View.VISIBLE);
        } else {
            btnLocalPreservation.setVisibility(View.GONE);
        }
    }

    /**
     * 获取数据
     */
    private void getData() {
        LoadingUtils.showLoading(mContext);
        Map<String, Object> map = new HashMap<>();
        map.put("processId", processId);
        map.put("taskId", taskId);
        RequestBody requestBody = RequestBody.create(ConstantsUtil.JSON, gson.toJson(map));
        Request request = new Request.Builder()
                .url(ConstantsUtil.BASE_URL + ConstantsUtil.GET_PROCESS_DETAIL)
                .addHeader("token", (String) SpUtil.get(mContext, ConstantsUtil.TOKEN, ""))
                .post(requestBody)
                .build();
        ConstantsUtil.okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runChildrenThread(getString(R.string.server_exception));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String jsonData = response.body().string().toString();
                if (JsonUtils.isGoodJson(jsonData)) {
                    List<String> strList = analysisJson(jsonData);
                    if (strList.size() > 0) {
                        if (strList.get(0).equals("true")) {
                            final List<PhotosBean> fileList = new ArrayList<>();
                            final ProcessDetailsModel model = gson.fromJson(jsonData, ProcessDetailsModel.class);
                            try {
                                JSONObject jsonObj = new JSONObject(jsonData);
                                jsonObj = new JSONObject(jsonObj.get("data").toString());
                                JSONArray jsonArr = jsonObj.has("sxZlPhotoList") ? jsonObj.getJSONArray("sxZlPhotoList") : null;
                                int size = jsonArr == null ? 0 : jsonArr.length();
                                for (int k = 0; k < size; k++) {
                                    PhotosBean bean = gson.fromJson(jsonArr.get(k).toString(), PhotosBean.class);
                                    fileList.add(bean);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // 查询本地保存的照片
                                    phoneList = DataSupport.where("isToBeUpLoad = 1 AND userId = ? AND processId = ? order by createTime desc", (String) SpUtil.get(mContext, ConstantsUtil.USER_ID, ""), processId).find(PhotosBean.class);
                                    localPhotoList = phoneList;
                                    for (PhotosBean photo : fileList) {
                                        phoneList.add(photo);
                                    }
                                    setData(model.getData());
                                    LoadingUtils.hideLoading();
                                }
                            });
                        } else {
                            tokenErr(strList.get(2), strList.get(1));
                        }
                    }
                } else {
                    runChildrenThread(getString(R.string.json_error));
                }
            }
        });
    }

    /**
     * 赋值
     */
    private void setData(WorkingBean workingBean) {
        txtRejectPhoto.setText(workingBean.getDismissal()); // 驳回原因
        String strLocation = workingBean.getLocation();
        String distance = strLocation == null || TextUtils.isEmpty(strLocation) || strLocation.equals("unknown") || strLocation.equals("null") ? "" : strLocation;
        txtLocation.setText(distance);    // 拍照位置
        txtWorkingNo.setText(workingBean.getProcessCode());     // 工序编号
        txtWorkingName.setText(workingBean.getProcessName());   // 工序名称
        txtDistanceAngle.setText(workingBean.getPhotoDistance());   // 距离角度
        txtEntryTime.setText(DateUtil.format(DateUtil.date(workingBean.getEnterTime() == 0 ? System.currentTimeMillis() : workingBean.getEnterTime()), "yyyy-MM-dd HH:mm:ss"));    // 检查时间
        num = Integer.valueOf(StrUtil.isEmpty(workingBean.getActualNumber()) ? "3" : workingBean.getActualNumber());
        txtTakePhotoNum.setText("最少拍照" + num + "张");  // 拍照张三
        txtTakePhotoRequirement.setText(workingBean.getPhotoContent());     // 拍照要求
        // 图片列表
        if (phoneList != null) {
            adapter = new ContractorDetailsPhotoAdapter(mContext, phoneList, listener, getIntent().getStringExtra("levelId"), processState);
            LinearLayoutManager ms = new LinearLayoutManager(this);
            ms.setOrientation(LinearLayoutManager.HORIZONTAL);
            rvContractorDetails.setLayoutManager(ms);
            rvContractorDetails.setAdapter(adapter);
        }
        // 所有填方工序显示层厚定点位置
        if (processPath.contains("填方")) {
            // 层厚定点位置信息
            List<WorkingBean> workList = DataSupport.where("processId = ?", processId).find(WorkingBean.class);
            WorkingBean bean = null;
            if (workList != null && workList.size() > 0) {
                bean = workList.get(0);
            }
            edtPositionOfPileNumber1.setText(checkData(workingBean) ? bean == null ? "" : bean.getExt1() : workingBean.getExt1());
            edtElevation1.setText(checkData(workingBean) ? bean == null ? "" : bean.getExt2() : workingBean.getExt2());
            edtPositionOfPileNumber2.setText(checkData(workingBean) ? bean == null ? "" : bean.getExt3() : workingBean.getExt3());
            edtElevation2.setText(checkData(workingBean) ? bean == null ? "" : bean.getExt4() : workingBean.getExt4());
            edtPositionOfPileNumber3.setText(checkData(workingBean) ? bean == null ? "" : bean.getExt5() : workingBean.getExt5());
            edtElevation3.setText(checkData(workingBean) ? bean == null ? "" : bean.getExt6() : workingBean.getExt6());
            edtPositionOfPileNumber4.setText(checkData(workingBean) ? bean == null ? "" : bean.getExt7() : workingBean.getExt7());
            edtElevation4.setText(checkData(workingBean) ? bean == null ? "" : bean.getExt8() : workingBean.getExt8());
            edtPositionOfPileNumber5.setText(checkData(workingBean) ? bean == null ? "" : bean.getExt9() : workingBean.getExt9());
            edtElevation5.setText(checkData(workingBean) ? bean == null ? "" : bean.getExt10() : workingBean.getExt10());

            // 有网络并且不是领导 提交本地保存的层厚定点位置信息
            if (JudgeNetworkIsAvailable.isNetworkAvailable(this) && !userLevel.equals("3")) {
                if (!getIntent().getBooleanExtra("check", true)) {
                    PromptDialog promptDialog = new PromptDialog(mContext, new PromptListener() {
                        @Override
                        public void returnTrueOrFalse(boolean trueOrFalse) {
                            if (trueOrFalse) {
                                submitElevation(true);
                            }
                        }
                    }, "提示", "该工序填写的层厚定点位置信息还未上传到云端，是否上传？", "否", "是");
                    promptDialog.show();
                }
            }
        }
    }

    /**
     * 是否都为空
     *
     * @return
     */
    private boolean checkData(WorkingBean workingBean) {
        String ext1 = workingBean.getExt1();
        String ext2 = workingBean.getExt2();
        String ext3 = workingBean.getExt3();
        String ext4 = workingBean.getExt4();
        String ext5 = workingBean.getExt5();
        String ext6 = workingBean.getExt6();
        String ext7 = workingBean.getExt7();
        String ext8 = workingBean.getExt8();
        String ext9 = workingBean.getExt9();
        String ext10 = workingBean.getExt10();

        if (TextUtils.isEmpty(ext1) && TextUtils.isEmpty(ext2) && TextUtils.isEmpty(ext3) && TextUtils.isEmpty(ext4) && TextUtils.isEmpty(ext5) && TextUtils.isEmpty(ext6) && TextUtils.isEmpty(ext7) && TextUtils.isEmpty(ext8) && TextUtils.isEmpty(ext9) && TextUtils.isEmpty(ext10)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 图片点击事件监听--->全屏预览图片
     */
    private ShowPhotoListener listener = new ShowPhotoListener() {
        @Override
        public void selectWayOrShowPhoto(boolean isShowPhoto, String point, String photoUrl, int isUpLoad) {
            // 图片浏览
            ArrayList<String> urls = new ArrayList<>();
            int len = phoneList.size();
            for (int i = 0; i < len; i++) {
                String fileUrl = phoneList.get(i).getPhotoAddress();
                if (!TextUtils.isEmpty(fileUrl) && !fileUrl.contains(ConstantsUtil.SAVE_PATH)) {
                    fileUrl = ConstantsUtil.BASE_URL + ConstantsUtil.prefix + fileUrl;
                }
                urls.add(fileUrl);
            }
            Intent intent = new Intent(mContext, ShowPhotosActivity.class);
            intent.putExtra(ShowPhotosActivity.EXTRA_IMAGE_URLS, urls);
            intent.putExtra(ShowPhotosActivity.EXTRA_IMAGE_INDEX, Integer.valueOf(point));
            startActivity(intent);
        }
    };

    /**
     * 选择上报给谁
     *
     * @param pictureBeanList
     */
    private void reported(final List<PictureBean> pictureBeanList) {
        SelectionAuditorDialog reportDialog = new SelectionAuditorDialog(mContext, new ReportListener() {
            @Override
            public void returnUserId(String userId) {
                if (!StrUtil.isEmpty(userId)) {
                    submitElevation(false);
                    adapter = new ContractorDetailsPhotoAdapter(mContext, phoneList, listener, getIntent().getStringExtra("levelId"), processState);
                    LinearLayoutManager ms = new LinearLayoutManager(mContext);
                    ms.setOrientation(LinearLayoutManager.HORIZONTAL);
                    rvContractorDetails.setLayoutManager(ms);
                    rvContractorDetails.setAdapter(adapter);

                    if (StrUtil.isNotEmpty(userId)) {
                        submitReported(userId, pictureBeanList);
                    }
                }
            }
        });
        reportDialog.setCanceledOnTouchOutside(false);
        reportDialog.show();
    }

    /**
     * 上报审核图片
     *
     * @param userId
     * @param pictureBeanList
     */
    private void submitReported(String userId, final List<PictureBean> pictureBeanList) {
        LoadingUtils.showLoading(mContext);
        final PictureModel model = new PictureModel();
        model.setSelectUserId(userId);
        model.setPushMessage(processPath);
        model.setStateFlag("1");
        model.setProcessId(processId);
        model.setRecordType((String) SpUtil.get(mContext, ConstantsUtil.USER_TYPE, "0"));
        Gson gson = new Gson();
        RequestBody requestBody = RequestBody.create(ConstantsUtil.JSON, gson.toJson(model).toString());
        String url;
        if (userLevel.equals("0")) {
            url = ConstantsUtil.SUBMIT_AUDITORS_PICTURE;
        } else {
            url = ConstantsUtil.REJECT_FINISH;
        }

        Request request = new Request.Builder()
                .url(ConstantsUtil.BASE_URL + url)
                .addHeader("token", (String) SpUtil.get(mContext, ConstantsUtil.TOKEN, ""))
                .post(requestBody)
                .build();
        ConstantsUtil.okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LoadingUtils.hideLoading();
                runChildrenThread("上报审核失败!");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String jsonData = response.body().string().toString();
                if (JsonUtils.isGoodJson(jsonData)) {
                    try {
                        JSONObject obj = new JSONObject(jsonData);
                        boolean resultFlag = obj.getBoolean("success");
                        final String msg = obj.getString("message");
                        final String code = obj.getString("code");
                        if (resultFlag) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    LoadingUtils.hideLoading();
                                    ToastUtil.showShort(mContext, "上报成功!");
                                    // 上报成功修改状态
                                    for (PhotosBean bean : phoneList) {
                                        bean.setCanSelect(false);
                                        for (PictureBean picBean : pictureBeanList) {
                                            if (bean.getPhotoId().equals(picBean.getPhotoId())) {
                                                bean.setCheckFlag("1");
                                                break;
                                            }
                                        }
                                    }

                                    // 根据用户级别显示不同按钮(0:施工人员; 1:质检部长; 2:监理; 3:领导)
                                    if (userLevel.equals("0")) {
                                        processState = "2";
                                    } else if (userLevel.equals("1")) {
                                        processState = "4";
                                    } else {
                                        processState = "6";
                                    }

                                    for (PhotosBean bean : phoneList) {
                                        bean.setRoleFlag("0");
                                    }

                                    adapter = new ContractorDetailsPhotoAdapter(mContext, phoneList, listener, getIntent().getStringExtra("levelId"), processState);

                                    LinearLayoutManager ms = new LinearLayoutManager(mContext);
                                    ms.setOrientation(LinearLayoutManager.HORIZONTAL);
                                    rvContractorDetails.setLayoutManager(ms);
                                    rvContractorDetails.setAdapter(adapter);
                                }
                            });
                        } else {
                            LoadingUtils.hideLoading();
                            tokenErr(code, msg);
                        }
                    } catch (JSONException e) {
                        LoadingUtils.hideLoading();
                        runChildrenThread(getString(R.string.data_error));
                        e.printStackTrace();
                    }
                } else {
                    LoadingUtils.hideLoading();
                    runChildrenThread(getString(R.string.json_error));
                }
            }
        });
    }

    /**
     * 拍照
     */
    private void takePictures() {
        if (!isHorizontalScreen) {
            HorizontalScreenHintDialog screenHintDialog = new HorizontalScreenHintDialog(mContext, true);
            screenHintDialog.show();
        } else {
            Intent intent = new Intent();
            intent.setClass(mContext, PhotographActivity.class);
            startActivityForResult(intent, 1);
        }
    }

    /**
     * 屏幕方向旋转监听
     */
    private class AlbumOrientationEventListener extends OrientationEventListener {
        public AlbumOrientationEventListener(Context context) {
            super(context);
        }

        public AlbumOrientationEventListener(Context context, int rate) {
            super(context, rate);
        }

        @Override
        public void onOrientationChanged(int orientation) {
            if (orientation == OrientationEventListener.ORIENTATION_UNKNOWN) {
                return;
            }

            //保证只返回四个方向
            int newOrientation = ((orientation + 45) / 90 * 90) % 360;

            if (newOrientation != mOrientation) {
                // 返回的mOrientation就是手机方向，为0°、90°、180°和270°中的一个
                mOrientation = newOrientation;
                switch (mOrientation) {
                    case 0:
                    case 180:
                        isHorizontalScreen = false;
                        break;
                    case 90:
                    case 270:
                        isHorizontalScreen = true;
                        break;
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case 1:
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
                    fileInfoListener.fileInfo("太原东二环高速公路", processPath, "", "", false);
                    break;
                default:
                    break;
            }
        }
    }

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
     * 文件描述监听
     */
    private FileInfoListener fileInfoListener = new FileInfoListener() {
        @Override
        public void fileInfo(String engineeringName, String rootNodeName, String parentNodeName, String nodeName, boolean isUploadNow) {
            LoadingUtils.showLoading(mContext);
            // 向LitePal数据库中添加一条数据
            addPhotoBean = new PhotosBean();
            addPhotoBean.setPhotoAddress(ConstantsUtil.SAVE_PATH + fileUrlName);
            addPhotoBean.setProcessId(processId);
            addPhotoBean.setThumbPath(ConstantsUtil.SAVE_PATH + fileUrlName);
            addPhotoBean.setPhotoDesc(rootNodeName); //描述换成rootNodeName
            addPhotoBean.setPhotoName(fileUrlName);
            addPhotoBean.setCheckFlag("-1");
            addPhotoBean.setIsNewAdd(1);
            addPhotoBean.setRoleFlag("1");
            addPhotoBean.setLatitude(String.valueOf(latitude));
            addPhotoBean.setLongitude(String.valueOf(longitude));
            addPhotoBean.setLocation(sLocation);
            addPhotoBean.setUserId((String) SpUtil.get(mContext, ConstantsUtil.USER_ID, ""));
            addPhotoBean.setPhotoType((String) SpUtil.get(mContext, ConstantsUtil.USER_TYPE, ""));
            addPhotoBean.setCreateTime(System.currentTimeMillis());
            String[] strings = new String[]{engineeringName, rootNodeName};
            addPhotoBean.setIsToBeUpLoad(1);
            addPhotoBean.save();
            // 添加图片按钮
            phoneList.add(0, addPhotoBean);
            // 显示本地保存照片文件夹
            //imgBtnPhotos.setVisibility(View.VISIBLE);
            // 异步将图片存储到SD卡指定文件夹下
            new StorageTask().execute(strings);
        }
    };

    /**
     * 将照片存储到SD卡
     */
    private class StorageTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            Bitmap bitmap = BitmapFactory.decodeFile(FileUtil.getRealFilePath(mContext, uri));
            // 压缩图片
            bitmap = FileUtil.compressBitmap(bitmap);
            // 在图片上添加水印
            bitmap = ImageUtil.createWaterMaskLeftTop(mContext, bitmap, params[1], addPhotoBean);
            // 保存到SD卡指定文件夹下
            saveBitmapFile(bitmap, fileUrlName);
            // 删除拍摄的照片
            FileUtil.deleteFile(FileUtil.getRealFilePath(mContext, uri));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            LoadingUtils.hideLoading();
            if (null != adapter) {
                adapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * 保存图片到SD卡指定目录下
     *
     * @param bitmap
     * @param fileName
     */
    public void saveBitmapFile(Bitmap bitmap, String fileName) {
        // 将要保存图片的路径
        File imgFile = new File(ConstantsUtil.SAVE_PATH);
        if (!imgFile.exists()) {
            imgFile.mkdirs();
        }

        File file = new File(ConstantsUtil.SAVE_PATH + fileName);

        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();

            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        if (locationService != null) {
            locationService.unregisterListener(mListener); //注销掉监听
            locationService.stop(); //停止定位服务
        }
        super.onStop();
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
            }
        }

    };

    /**
     * 获取定位权限
     */
    @TargetApi(23)
    private void getPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String permissionInfo = "";
            ArrayList<String> permissions = new ArrayList<>();
            // 定位精确位置
            if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if (checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);
            }
            // 读写权限
            if (addPermission(permissions, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissionInfo += "Manifest.permission.WRITE_EXTERNAL_STORAGE Deny \n";
            }
            // 读取电话状态权限
            if (addPermission(permissions, android.Manifest.permission.READ_PHONE_STATE)) {
                permissionInfo += "Manifest.permission.READ_PHONE_STATE Deny \n";
            }

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
            gpsLocationManager.start(new GpsListener());
            gpsLocationManager = GPSLocationManager.getInstances(this);
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
            }
        }

        @Override
        public void UpdateStatus(String provider, int status, Bundle extras) {
        }

        @Override
        public void UpdateGPSProviderStatus(int gpsStatus) {
        }
    }

    /**
     * 审核照片
     */
    private void toExaminePhoto(final boolean isFinish) {
        final List<PictureBean> checkPictureList = new ArrayList<>();
        final List<PhotosBean> submitPictureList = new ArrayList<>();
        for (PhotosBean phoneListBean : phoneList) {
            // 需要上传的照片
            if (phoneListBean.getCheckFlag().equals("-1")) {
                // 待上传
                submitPictureList.add(phoneListBean);
            }

            if (phoneListBean.getCheckFlag().equals("0")) {
                // 待审核
                PictureBean bean = new PictureBean();
                bean.setPhotoId(phoneListBean.getPhotoId());
                checkPictureList.add(bean);
            }
        }

        if (!isFinish) {
            if (checkPictureList.size() == 0 && submitPictureList.size() == 0) {
                ToastUtil.showLong(mContext, "没有待审核的照片，请先拍照再进行审核!");
                return;
            }
        }

        // 上报审核
        if (JudgeNetworkIsAvailable.isNetworkAvailable(this)) {
            if (submitPictureList.size() > 0) {
                // 上传
                UpLoadPhotosDialog upLoadPhotosDialog = new UpLoadPhotosDialog(mContext, submitPictureList, new PromptListener() {
                    @Override
                    public void returnTrueOrFalse(boolean trueOrFalse) {
                        if (trueOrFalse) {
                            // 隐藏本地图片菜单
                            //imgBtnPhotos.setVisibility(View.GONE);
                            btnLocalPreservation.setVisibility(View.GONE);

                            // 完成工序接口
                            for (PhotosBean phone : phoneList) {
                                phone.setIsToBeUpLoad(-1);
                                phone.setIsNewAdd(-1);
                                phone.setCheckFlag("0");
                            }

                            // 更新adapter
                            for (PhotosBean phone : submitPictureList) {
                                phone.setRoleFlag("2");
                            }

                            if (isFinish) {
                                RejectDialog rejectDialog = new RejectDialog(mContext, new ReportListener() {
                                    @Override
                                    public void returnUserId(String userId) {
                                        rejectSubmit(userId, "1");
                                    }
                                }, "处理意见", "取消", "确认");
                                rejectDialog.show();
                            } else {
                                // 设置工序状态
                                switch (userLevel) {
                                    case "0":
                                        processState = "1";
                                        break;
                                    case "2":
                                        processState = "4";
                                        break;
                                }
                                // 更新adapter
                                adapter = new ContractorDetailsPhotoAdapter(mContext, phoneList, listener, getIntent().getStringExtra("levelId"), processState);
                                LinearLayoutManager ms = new LinearLayoutManager(mContext);
                                ms.setOrientation(LinearLayoutManager.HORIZONTAL);
                                rvContractorDetails.setLayoutManager(ms);
                                rvContractorDetails.setAdapter(adapter);
                                reported(checkPictureList);
                            }
                        }
                    }
                });
                upLoadPhotosDialog.setCanceledOnTouchOutside(false);
                upLoadPhotosDialog.show();
            } else {
                if (isFinish) {
                    // 完成工序接口
                    RejectDialog rejectDialog = new RejectDialog(mContext, new ReportListener() {
                        @Override
                        public void returnUserId(String userId) {
                            // 调用驳回接口
                            rejectSubmit(userId, "1");
                        }
                    }, "备注", "取消", "确认");
                    rejectDialog.show();
                } else {
                    reported(checkPictureList);
                }
            }
        } else {
            ToastUtil.showLong(mContext, "当前无网络，请连接网络再进行审核!");
        }
    }

    /**
     * 完成工序
     */
    private void finishPhoto() {
        switch (processState) {
            case "8":
                ToastUtil.showLong(mContext, "该工序已抽检完成，不能再次提交!");
                return;
            case "7":
                ToastUtil.showLong(mContext, "该工序终审被驳回，请等待班组人员重新办理后再审核!");
                return;
            case "5":
                ToastUtil.showLong(mContext, "该工序复审被驳回，请等待班组人员重新办理后再审核!");
                return;
            case "3":
                ToastUtil.showLong(mContext, "该工序初审被驳回，请等待班组人员重新办理后再审核!");
                return;
            case "2":
                ToastUtil.showLong(mContext, "该工序正在审核中，请等待班组人员提交复审后再进行审核!");
                return;
            case "1":
            case "0":
                ToastUtil.showLong(mContext, "该工序还未进行审核，请等待班组人员提交初审后再进行审核!");
                return;
        }
        if (getIntent().getStringExtra("canCheck").equals("0")) {
            ToastUtil.showLong(mContext, "该工序已提交给其它人审核，您不能执行抽检完成操作!");
            return;
        }
        toExaminePhoto(true);
    }

    /**
     * 拍照
     */
    private void takePhotos() {
        if (processState.equals("8")) {
            ToastUtil.showLong(mContext, "抽检完成的" + strType + "不能再进行拍照!");
        } else if (processState.equals("7")) {
            if (userLevel.equals("2")) {
                ToastUtil.showLong(mContext, "该" + strType + "已被监理组长驳回，请驳回给施工人员修改后再进行拍照!");
            } else {
                ToastUtil.showLong(mContext, "该" + strType + "还未驳回给您，请等待质检部长驳回给您后再进行拍照!");
            }
        } else if (processState.equals("6")) {
            ToastUtil.showLong(mContext, "该" + strType + "已复审通过，您不能再进行拍照!");
        } else if (processState.equals("5")) {
            ToastUtil.showLong(mContext, "该" + strType + "还未驳回给您，请等待质检部长驳回给您后再进行拍照!");
        } else if (processState.equals("4") && userLevel.equals("0")) {
            ToastUtil.showLong(mContext, "该" + strType + "已提交至监理审核,不能再进行拍照!");
        } else if (processState.equals("2")) {
            ToastUtil.showLong(mContext, "该" + strType + "已提交至质检部长审核,不能再进行拍照!");
        } else {
            if (Build.VERSION.SDK_INT >= 23) {
                requestAuthority(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA}, new PermissionListener() {
                    @Override
                    public void agree() {
                        takePictures();
                    }

                    @Override
                    public void refuse(List<String> refusePermission) {
                        ToastUtil.showLong(mContext, "您已拒绝拍照权限!");
                    }
                });
            } else {
                takePictures();
            }
        }
    }

    /**
     * 驳回
     */
    private void reject() {
        if (userLevel.equals("1")) {
            switch (processState) {
                case "8":
                    ToastUtil.showLong(mContext, "该" + strType + "已抽检完成，您不能进行驳回操作!");
                    return;
                case "7":
                    ToastUtil.showLong(mContext, "该" + strType + "监理还未驳回给您，您不能进行驳回操作!");
                    return;
                case "6":
                    ToastUtil.showLong(mContext, "该" + strType + "复审已通过，您不能进行驳回操作!");
                    return;
                case "4":
                    ToastUtil.showLong(mContext, "该" + strType + "已提交至监理审核，您不能进行驳回操作!");
                    return;
                case "3":
                    ToastUtil.showLong(mContext, "该" + strType + "已被驳回，不能再次进行驳回操作!");
                    return;
                case "1":
                case "0":
                    ToastUtil.showLong(mContext, "该" + strType + "还未进行审核，进行审核后才能进行驳回操作!");
                    return;
            }
        } else if (userLevel.equals("2")) {
            switch (processState) {
                case "8":
                    ToastUtil.showLong(mContext, "该" + strType + "已抽检完成，您不能进行驳回操作!");
                    return;
                case "6":
                    ToastUtil.showLong(mContext, "该" + strType + "复审已通过，您不能进行驳回操作!");
                    return;
                case "5":
                    ToastUtil.showLong(mContext, "该" + strType + "已被驳回，不能再次进行驳回操作!");
                    return;
                case "3":
                    ToastUtil.showLong(mContext, "该" + strType + "已被初审人员驳回，不能再次进行驳回操作!");
                    return;
                case "2":
                case "1":
                case "0":
                    ToastUtil.showLong(mContext, "该" + strType + "还未进行审核，进行审核后才能进行驳回操作!");
                    return;
            }
        } else if (userLevel.equals("21")) {
            switch (processState) {
                case "8":
                    ToastUtil.showLong(mContext, "该" + strType + "已抽检完成，您不能进行驳回操作!");
                    return;
                case "7":
                    ToastUtil.showLong(mContext, "该" + strType + "已被驳回，您不能进行驳回操作!");
                    return;
                case "5":
                    ToastUtil.showLong(mContext, "该" + strType + "已被驳回，不能再次进行驳回操作!");
                    return;
                case "4":
                    ToastUtil.showLong(mContext, "该" + strType + "还未提交终审，不能进行驳回操作!");
                    return;
                case "3":
                    ToastUtil.showLong(mContext, "该" + strType + "已被初审人员驳回，不能再次进行驳回操作!");
                    return;
                case "2":
                case "1":
                case "0":
                    ToastUtil.showLong(mContext, "该" + strType + "还未进行审核，进行审核后才能进行驳回操作!");
                    return;
            }
        }

        RejectDialog rejectDialog = new RejectDialog(mContext, new ReportListener() {
            @Override
            public void returnUserId(String userId) {
                // 调用驳回接口
                rejectSubmit(userId, "0");
            }
        }, "驳回原因", "取消", "确认");
        rejectDialog.show();
    }

    /**
     * 提交驳回
     *
     * @param remark
     */
    private void rejectSubmit(String remark, final String stateFlag) {
        LoadingUtils.showLoading(mContext);
        PictureModel model = new PictureModel();
        model.setProcessId(processId);
        model.setRecordType((String) SpUtil.get(mContext, ConstantsUtil.USER_TYPE, "0"));
        model.setDismissal(remark);
        model.setStateFlag(stateFlag);

        Gson gson = new Gson();
        RequestBody requestBody = RequestBody.create(ConstantsUtil.JSON, gson.toJson(model).toString());
        Request request = new Request.Builder()
                .url(ConstantsUtil.BASE_URL + ConstantsUtil.REJECT_FINISH)
                .addHeader("token", (String) SpUtil.get(mContext, ConstantsUtil.TOKEN, ""))
                .post(requestBody)
                .build();
        ConstantsUtil.okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LoadingUtils.hideLoading();
                runChildrenThread("操作失败!");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String jsonData = response.body().string().toString();
                if (JsonUtils.isGoodJson(jsonData)) {
                    try {
                        JSONObject obj = new JSONObject(jsonData);
                        boolean resultFlag = obj.getBoolean("success");
                        final String msg = obj.getString("message");
                        final String code = obj.getString("code");
                        if (resultFlag) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    LoadingUtils.hideLoading();
                                    ToastUtil.showShort(mContext, "操作成功!");
                                    // 设置状态
                                    List<WorkingBean> workList = DataSupport.where("processId = ?", processId).find(WorkingBean.class);
                                    if (workList != null && workList.size() > 0) {
                                        WorkingBean bean = workList.get(0);
                                        if (stateFlag.equals("0")) {
                                            // 根据用户级别显示不同按钮(0:施工人员; 1:质检部长; 2:监理; 3:领导)
                                            // 质检部长驳回
                                            if (userLevel.equals("1")) {
                                                processState = "3";
                                                bean.setProcessState("3");
                                            } else if (userLevel.equals("2")) {
                                                processState = "5";
                                                bean.setProcessState("5");
                                            } else {
                                                processState = "7";
                                                bean.setProcessState("7");
                                            }
                                        } else {
                                            processState = "8";
                                            bean.setProcessState("8");
                                        }
                                        bean.saveOrUpdate("processId = ?", bean.getProcessId());
                                    }

                                    adapter = new ContractorDetailsPhotoAdapter(mContext, phoneList, listener, getIntent().getStringExtra("levelId"), processState);

                                    LinearLayoutManager ms = new LinearLayoutManager(mContext);
                                    ms.setOrientation(LinearLayoutManager.HORIZONTAL);
                                    rvContractorDetails.setLayoutManager(ms);
                                    rvContractorDetails.setAdapter(adapter);
                                }
                            });
                        } else {
                            LoadingUtils.hideLoading();
                            tokenErr(code, msg);
                        }
                    } catch (JSONException e) {
                        LoadingUtils.hideLoading();
                        runChildrenThread(getString(R.string.data_error));
                        e.printStackTrace();
                    }
                } else {
                    LoadingUtils.hideLoading();
                    runChildrenThread(getString(R.string.json_error));
                }
            }
        });
    }

    /**
     * 子线程运行
     */
    private void runChildrenThread(final String msg) {
        LoadingUtils.hideLoading();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtil.showLong(mContext, msg);
            }
        });
    }

    /**
     * Token过期
     *
     * @param code
     * @param msg
     */
    private void tokenErr(final String code, final String msg) {
        LoadingUtils.hideLoading();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (code) {
                    case "3003":
                    case "3004":
                        // Token异常重新登录
                        ToastUtil.showLong(mContext, "Token过期请重新登录！");
                        SpUtil.put(mContext, ConstantsUtil.IS_LOGIN_SUCCESSFUL, false);
                        ScreenManagerUtil.popAllActivityExceptOne();
                        startActivity(new Intent(mContext, LoginActivity.class));
                        break;
                    default:
                        ToastUtil.showLong(mContext, msg);
                        break;
                }
            }
        });
    }

    /**
     * 解析后台返回数据
     *
     * @param json
     * @return
     */
    private List<String> analysisJson(String json) {
        List<String> strList = new ArrayList<>();
        try {
            JSONObject obj = new JSONObject(json);
            boolean resultFlag = obj.getBoolean("success");
            String code = obj.getString("code");
            String msg = obj.getString("message");
            strList.add(resultFlag + "");
            strList.add(code);
            strList.add(msg);
        } catch (JSONException e) {
            runChildrenThread(getString(R.string.data_error));
            e.printStackTrace();
        }
        return strList;
    }

    /**
     * 保存到服务器
     */
    private void submitElevation(final boolean isLogin) {
        if (isLogin) {
            LoadingUtils.showLoading(mContext);
        }
        JSONObject obj = new JSONObject();
        try {
            obj.put("processId", processId);
            obj.put("ext1", edtPositionOfPileNumber1.getText().toString().trim());
            obj.put("ext2", edtElevation1.getText().toString().trim());
            obj.put("ext3", edtPositionOfPileNumber2.getText().toString().trim());
            obj.put("ext4", edtElevation2.getText().toString().trim());
            obj.put("ext5", edtPositionOfPileNumber3.getText().toString().trim());
            obj.put("ext6", edtElevation3.getText().toString().trim());
            obj.put("ext7", edtPositionOfPileNumber4.getText().toString().trim());
            obj.put("ext8", edtElevation4.getText().toString().trim());
            obj.put("ext9", edtPositionOfPileNumber5.getText().toString().trim());
            obj.put("ext10", edtElevation5.getText().toString().trim());
            RequestBody requestBody = RequestBody.create(ConstantsUtil.JSON, obj.toString());
            Request request = new Request.Builder()
                    .url(ConstantsUtil.BASE_URL + ConstantsUtil.UPDATE_SX_ZL_PROCESS)
                    .addHeader("token", (String) SpUtil.get(mContext, ConstantsUtil.TOKEN, ""))
                    .post(requestBody)
                    .build();
            ConstantsUtil.okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runChildrenThread("保存失败!");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String jsonData = response.body().string().toString();
                    if (JsonUtils.isGoodJson(jsonData)) {
                        try {
                            JSONObject obj = new JSONObject(jsonData);
                            boolean resultFlag = obj.getBoolean("success");
                            final String msg = obj.getString("message");
                            final String code = obj.getString("code");
                            if (resultFlag) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (isLogin) {
                                            ToastUtil.showShort(mContext, "保存成功!");
                                        }
                                        saveOnLocal();
                                    }
                                });
                            } else {
                                tokenErr(code, msg);
                            }
                        } catch (JSONException e) {
                            runChildrenThread(getString(R.string.data_error));
                            e.printStackTrace();
                        }
                    } else {
                        runChildrenThread(getString(R.string.json_error));
                    }
                    if (isLogin) {
                        LoadingUtils.hideLoading();
                    }
                }
            });
        } catch (JSONException e) {
            if (isLogin) {
                LoadingUtils.hideLoading();
            }
            ToastUtil.showShort(mContext, "参数有误！");
            e.printStackTrace();
        }
    }

    /**
     * 保存至本地
     */
    private void saveOnLocal() {
        List<WorkingBean> workList = DataSupport.where("processId = ?", processId).find(WorkingBean.class);
        if (workList != null && workList.size() > 0) {
            WorkingBean bean = workList.get(0);
            bean.setExt1(edtPositionOfPileNumber1.getText().toString().trim());
            bean.setExt2(edtElevation1.getText().toString().trim());
            bean.setExt3(edtPositionOfPileNumber2.getText().toString().trim());
            bean.setExt4(edtElevation2.getText().toString().trim());
            bean.setExt5(edtPositionOfPileNumber3.getText().toString().trim());
            bean.setExt6(edtElevation3.getText().toString().trim());
            bean.setExt7(edtPositionOfPileNumber4.getText().toString().trim());
            bean.setExt8(edtElevation4.getText().toString().trim());
            bean.setExt9(edtPositionOfPileNumber5.getText().toString().trim());
            bean.setExt10(edtElevation5.getText().toString().trim());
            bean.saveOrUpdate("processId = ?", processId);
        }
    }

    @Event({R.id.imgBtnLeft, R.id.btnExamine, R.id.btnReject, R.id.imgBtnAdd, R.id.btnLocalSave, R.id.imgBtnPhotos, R.id.btnLocalPreservation})
    private void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgBtnLeft:
                this.finish();
                break;
            // 审核
            case R.id.btnExamine:
                // 根据用户级别显示不同按钮(0:施工人员; 1:质检部长; 2:监理; 3:领导 21:监理组长 22：总监)
                // 工序状态 (0:待拍照1:已拍照 2:已提交初审 3:初审驳回 4:初审通过 5:复审驳回 6:复审通过 7:终审驳回 8:终审通过)
                if ((userLevel.equals("1") || userLevel.equals("2")) && (processState.equals("1") || processState.equals("0"))) {
                    ToastUtil.showLong(mContext, "该" + strType + "施工人员还未提交审核，您还不能进行审核!");
                    return;
                }
                if (userLevel.equals("2") && processState.equals("2")) {
                    ToastUtil.showLong(mContext, "该" + strType + "质检负责人还未进行审核，您还不能进行审核!");
                    return;
                } else if (userLevel.equals("2") && processState.equals("3")) {
                    ToastUtil.showLong(mContext, "该" + strType + "已被质检负责人驳回给施工人员，您还不能进行审核!");
                    return;
                }

                if (phoneList.size() < num) {
                    ToastUtil.showShort(mContext, "拍照数量不能小于最少拍照张数！");
                } else {
                    if (btnExamine.getText().toString().equals("审核")) {
                        // 审核照片
                        if (userLevel.equals("0")) {
                            switch (processState) {
                                case "8":
                                    ToastUtil.showLong(mContext, "该" + strType + "已抽检完成，您不能再次提交审核!");
                                    return;
                                case "7":
                                    ToastUtil.showLong(mContext, "该" + strType + "终审未通过，请等待监理驳回修改后再进行审核!");
                                    return;
                                case "6":
                                    ToastUtil.showLong(mContext, "该" + strType + "监理已审核通过，您不能再次提交审核!");
                                    return;
                                case "5":
                                    ToastUtil.showLong(mContext, "该" + strType + "复审未通过，请等待质检部长驳回修改后再进行审核!");
                                    return;
                                case "4":
                                    ToastUtil.showLong(mContext, "该" + strType + "正在复审中，您不能再次提交审核!");
                                    return;
                                case "2":
                                    ToastUtil.showLong(mContext, "该" + strType + "已提交审核，您不能再次提交审核!");
                                    return;
                            }
                        } else if (userLevel.equals("1")) {
                            switch (processState) {
                                case "8":
                                    ToastUtil.showLong(mContext, "该" + strType + "已抽检完成，您不能再次提交审核!");
                                    return;
                                case "7":
                                    ToastUtil.showLong(mContext, "该" + strType + "终审未通过，请等待监理驳回修改后再进行审核!");
                                    return;
                                case "6":
                                    ToastUtil.showLong(mContext, "该" + strType + "监理已审核通过，您不能进行驳回操作!");
                                    return;
                                case "5":
                                    ToastUtil.showLong(mContext, "该" + strType + "复审被驳回，请先驳回给施工人员修改后再进行审核!");
                                    return;
                                case "4":
                                    ToastUtil.showLong(mContext, "该" + strType + "监理正在审核，您不能再次提交审核!");
                                    return;
                                case "3":
                                    ToastUtil.showLong(mContext, "该" + strType + "初审被驳回，请等待施工人员修改后再进行审核!");
                                    return;
                                case "1":
                                case "0":
                                    ToastUtil.showLong(mContext, "该" + strType + "还未进行初审，进行初审后才能进行审核操作!");
                                    return;
                            }

                            if (getIntent().getStringExtra("canCheck").equals("0")) {
                                ToastUtil.showLong(mContext, "该" + strType + "已提交给其它人审核，您不能提交审核!");
                                return;
                            }
                        } else if (userLevel.equals("2")) {
                            switch (processState) {
                                case "8":
                                    ToastUtil.showLong(mContext, "该" + strType + "已抽检完成，您不能再次提交审核!");
                                    return;
                                case "7":
                                    ToastUtil.showLong(mContext, "该" + strType + "终审未通过，请驳回给质检负责人修改后再进行审核!");
                                    return;
                                case "6":
                                    ToastUtil.showLong(mContext, "该" + strType + "复审已通过，您不能进行驳回操作!");
                                    return;
                                case "5":
                                    ToastUtil.showLong(mContext, "该" + strType + "复审被驳回，请先驳回给施工人员修改后再进行审核!");
                                    return;
                                case "3":
                                    ToastUtil.showLong(mContext, "该" + strType + "初审被驳回，请等待施工人员修改后再进行审核!");
                                    return;
                                case "1":
                                case "0":
                                    ToastUtil.showLong(mContext, "该" + strType + "还未进行初审，进行初审后才能进行审核操作!");
                                    return;
                            }

                            if (getIntent().getStringExtra("canCheck").equals("0")) {
                                ToastUtil.showLong(mContext, "该" + strType + "已提交给其它人审核，您不能提交审核!");
                                return;
                            }
                        }
                        toExaminePhoto(false);
                    } else {
                        // 完成
                        finishPhoto();
                    }
                }
                break;
            // 拍照
            case R.id.imgBtnAdd:
                if (getIntent().getStringExtra("canCheck").equals("0") && !userLevel.equals("0")) {
                    ToastUtil.showLong(mContext, "该工序已提交给其它人处理，您不能拍照上传!");
                    return;
                }

                String sdSize = AppInfoUtil.getSDAvailableSize();
                if (Integer.valueOf(sdSize) < 10) {
                    ToastUtil.showShort(mContext, "当前手机内存卡已无可用空间，请清理后再进行拍照！");
                } else {
                    if (sLocation.length() < 7 || sLocation.contains("正在定位")) {
                        PromptDialog promptDialog = new PromptDialog(mContext, new PromptListener() {
                            @Override
                            public void returnTrueOrFalse(boolean trueOrFalse) {
                                if (trueOrFalse) {
                                    takePhotos();
                                }
                            }
                        }, "提示", "未定位到当前位置，拍照后会导致拍摄照片无地理位置信息。是否继续拍照？", "否", "是");
                        promptDialog.show();
                    } else {
                        takePhotos();
                    }
                }
                break;
            // 本地保存
            case R.id.btnLocalSave:
                if (localPhotoList.size() == 0) {
                    ToastUtil.showShort(mContext, "您还未拍摄照片，请拍照后在进行保存！");
                } else {
                    ToastUtil.showShort(mContext, "照片已成功保存至本地！");
                }
                break;
            // 驳回
            case R.id.btnReject:
                reject();
                break;
            // 保存层厚定点位置
            case R.id.btnLocalPreservation:
                if (JudgeNetworkIsAvailable.isNetworkAvailable(this)) {
                    submitElevation(true);
                } else {
                    PromptDialog promptDialog = new PromptDialog(mContext, new PromptListener() {
                        @Override
                        public void returnTrueOrFalse(boolean trueOrFalse) {
                            if (trueOrFalse) {
                                saveOnLocal();
                                ToastUtil.showShort(mContext, "保存成功！");
                            }
                        }
                    }, "提示", "当前无可用网络，是否先保存至本地？", "否", "是");
                    promptDialog.show();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isCanSelect = false;
        ScreenManagerUtil.popActivity(this);
        mAlbumOrientationEventListener.disable();
        // 终止定位
        if (gpsLocationManager != null) {
            gpsLocationManager.stop();
        }
    }
}
