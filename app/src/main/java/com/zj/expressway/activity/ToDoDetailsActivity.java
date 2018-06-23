package com.zj.expressway.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.OrientationEventListener;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.zj.expressway.R;
import com.zj.expressway.adapter.PhotosListAdapter;
import com.zj.expressway.adapter.TimeLineAdapter;
import com.zj.expressway.base.BaseActivity;
import com.zj.expressway.base.BaseModel;
import com.zj.expressway.bean.HistoryBean;
import com.zj.expressway.bean.PhotosBean;
import com.zj.expressway.bean.WorkingBean;
import com.zj.expressway.dialog.HorizontalScreenHintDialog;
import com.zj.expressway.dialog.UpLoadPhotosDialog;
import com.zj.expressway.listener.PermissionListener;
import com.zj.expressway.listener.PromptListener;
import com.zj.expressway.listener.ShowPhotoListener;
import com.zj.expressway.model.ButtonListModel;
import com.zj.expressway.model.WorkModel;
import com.zj.expressway.utils.AppInfoUtil;
import com.zj.expressway.utils.ChildThreadUtil;
import com.zj.expressway.utils.ConstantsUtil;
import com.zj.expressway.utils.DateUtils;
import com.zj.expressway.utils.JsonUtils;
import com.zj.expressway.utils.JudgeNetworkIsAvailable;
import com.zj.expressway.utils.LoadingUtils;
import com.zj.expressway.utils.ScreenManagerUtil;
import com.zj.expressway.utils.SpUtil;
import com.zj.expressway.utils.ToastUtil;

import org.litepal.crud.DataSupport;
import org.xutils.common.util.DensityUtil;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hzw.graffiti.GraffitiActivity;
import cn.hzw.graffiti.GraffitiParams;
import cn.qqtheme.framework.picker.DatePicker;
import cn.qqtheme.framework.util.ConvertUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 详情
 */
public class ToDoDetailsActivity extends BaseActivity {
    @ViewInject(R.id.imgBtnLeft)
    private ImageButton imgBtnLeft;
    @ViewInject(R.id.txtTitle)
    private TextView txtTitle;
    /*数据信息*/
    @ViewInject(R.id.btnChoice)
    private Button btnChoice;
    @ViewInject(R.id.txtPressLocal)
    private TextView txtPressLocal;
    @ViewInject(R.id.txtEntryTime)
    private TextView txtEntryTime;
    @ViewInject(R.id.edtHiddenTroubleHeadline)
    private EditText edtHiddenTroubleHeadline;
    @ViewInject(R.id.rgLevel)
    private RadioGroup rgLevel;
    @ViewInject(R.id.rBtn1)
    private RadioButton rBtn1;
    @ViewInject(R.id.rBtn2)
    private RadioButton rBtn2;
    @ViewInject(R.id.rBtn3)
    private RadioButton rBtn3;
    @ViewInject(R.id.btnChangeDate)
    private Button btnChangeDate;
    @ViewInject(R.id.imgBtnAdd)
    private ImageButton imgBtnAdd;
    @ViewInject(R.id.edtRectificationRequirements)
    private EditText edtRectificationRequirements;
    @ViewInject(R.id.rvContractorDetails)
    private RecyclerView rvContractorDetails;
    @ViewInject(R.id.rvTimeMarker)
    private RecyclerView rvTimeMarker;
    @ViewInject(R.id.llButtons)
    private LinearLayout llButtons;
    private TimeLineAdapter timeLineAdapter;
    // 屏幕方向监听
    private AlbumOrientationEventListener mAlbumOrientationEventListener;
    private int mOrientation = 0;
    private boolean isHorizontalScreen = false;
    // 图片列表
    private PhotosListAdapter photosAdapter;
    private List<PhotosBean> photosList = new ArrayList<>();
    // 拍照
    private Uri uri = null;
    private String fileUrlName, strFilePath;
    private PhotosBean addPhotoBean;
    private File imgFile;
    private Activity mContext;
    private String workId, flowId, processId, jsonData, buttonId;
    private Gson gson = new Gson();
    private WorkModel model;
    private String levelId; // 层级id
    private String selectText = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_quality_safety_details);

        mContext = this;
        x.view().inject(this);
        ScreenManagerUtil.pushActivity(this);
        // actionBar
        txtTitle.setText(R.string.app_name);
        imgBtnLeft.setVisibility(View.VISIBLE);
        imgBtnLeft.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.back_btn));
        // 任务id
        workId = getIntent().getStringExtra("workId");
        flowId = getIntent().getStringExtra("flowId");
        processId = getIntent().getStringExtra("processId");

        initFilePath();

        if (workId.equals("添加")) {
            txtEntryTime.setText(DateUtils.setDataToStr(System.currentTimeMillis()));
            setImgData(new ArrayList<PhotosBean>());
            List<ButtonListModel> buttons = new ArrayList<>();
            ButtonListModel btnModel = new ButtonListModel();
            btnModel.setButtonId("saveInLocation");
            btnModel.setButtonName("本地保存");
            buttons.add(btnModel);
            ButtonListModel btnSaveAdd = new ButtonListModel();
            btnSaveAdd.setButtonId("saveAndAdd");
            btnSaveAdd.setButtonName("保存继续添加");
            buttons.add(btnSaveAdd);
            ButtonListModel examine = new ButtonListModel();
            examine.setButtonId("examine");
            examine.setButtonName("发起审核");
            buttons.add(examine);
            setShowButton(buttons);
        } else if (workId.equals("详情")) {
            List<WorkingBean> workingBeanList = DataSupport.where("processId = ? order by createTime desc", processId).find(WorkingBean.class);
            WorkingBean workingBean = ObjectUtil.isNull(workingBeanList) || workingBeanList.size() == 0 ? null : workingBeanList.get(0);
            setTableData(workingBean);
            setImgData(new ArrayList<PhotosBean>());
            List<ButtonListModel> buttons = new ArrayList<>();
            ButtonListModel btnModel = new ButtonListModel();
            btnModel.setButtonId("saveInLocation");
            btnModel.setButtonName("本地保存");
            buttons.add(btnModel);
            ButtonListModel btnSaveAdd = new ButtonListModel();
            btnSaveAdd.setButtonId("saveAndAdd");
            btnSaveAdd.setButtonName("保存继续添加");
            buttons.add(btnSaveAdd);
            ButtonListModel examine = new ButtonListModel();
            examine.setButtonId("examine");
            examine.setButtonName("发起审核");
            buttons.add(examine);
            setShowButton(buttons);
            List<HistoryBean> flowHistoryList = DataSupport.where("processId=?", processId).find(HistoryBean.class);
            initTimeLineView(ObjectUtil.isNull(flowHistoryList) ? new ArrayList<HistoryBean>() : flowHistoryList);
        } else {
            initData();
        }

        rgLevel.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                RadioButton radioButton = (RadioButton) findViewById(checkedId);
                selectText = radioButton.getText().toString();
            }
        });
    }

    private void initData() {
        if (JudgeNetworkIsAvailable.isNetworkAvailable(this)) {
            getData();
        } else {
            List<WorkingBean> workingBeanList = DataSupport.where("processId = ? order by createTime desc", workId).find(WorkingBean.class);
            WorkingBean workingBean = ObjectUtil.isNull(workingBeanList) || workingBeanList.size() == 0 ? null : workingBeanList.get(0);
            setTableData(workingBean);
            setImgData(new ArrayList<PhotosBean>());
            List<ButtonListModel> buttons = new ArrayList<>();
            if (workingBean != null && StrUtil.isNotEmpty(workingBean.getFileOperationFlag()) && workingBean.getFileOperationFlag().equals("1")) {
                ButtonListModel btnModel = new ButtonListModel();
                btnModel.setButtonId("saveInLocation");
                btnModel.setButtonName("本地保存");
                buttons.add(btnModel);

                ButtonListModel save = new ButtonListModel();
                save.setButtonId("saveInLocation");
                save.setButtonName("保存继续添加");
                buttons.add(save);
            }
            setShowButton(buttons);
            List<HistoryBean> flowHistoryList = DataSupport.where("processId=?", workId).find(HistoryBean.class);
            initTimeLineView(ObjectUtil.isNull(flowHistoryList) ? new ArrayList<HistoryBean>() : flowHistoryList);
        }

        // 屏幕方向监听
        mAlbumOrientationEventListener = new AlbumOrientationEventListener(mContext, SensorManager.SENSOR_DELAY_NORMAL);
        if (mAlbumOrientationEventListener.canDetectOrientation()) {
            mAlbumOrientationEventListener.enable();
        }

        // 是否直接弹出相机
        boolean isPopTakePhoto = getIntent().getBooleanExtra("isPopTakePhoto", false);
        if (isPopTakePhoto) {
            checkPhotosPermission();
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
     *
     * @param flowHistoryList
     */
    private void initTimeLineView(List<HistoryBean> flowHistoryList) {
        if (flowHistoryList == null) {
            return;
        }

        for (HistoryBean history : flowHistoryList) {
            history.setProcessId(workId);
            history.saveOrUpdate("actionTime=? and processId=?", history.getActionTime() + "", workId);
        }

        rvTimeMarker.setLayoutManager(getLinearLayoutManager());
        rvTimeMarker.setHasFixedSize(true);
        timeLineAdapter = new TimeLineAdapter(flowHistoryList);
        rvTimeMarker.setAdapter(timeLineAdapter);
        rvTimeMarker.setNestedScrollingEnabled(false);
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
     * 获取表单数据
     */
    private void getData() {
        LoadingUtils.showLoading(mContext);
        JSONObject obj = new JSONObject();
        obj.put("workId", workId);
        Request request = ChildThreadUtil.getRequest(mContext, ConstantsUtil.FLOW_DETAILS, obj.toString());
        ConstantsUtil.okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                ChildThreadUtil.toastMsgHidden(mContext, getString(R.string.server_exception));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                jsonData = response.body().string().toString();
                if (JsonUtils.isGoodJson(jsonData)) {
                    // 解析
                    model = gson.fromJson(jsonData, WorkModel.class);
                    if (model.isSuccess()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                WorkingBean flowBean = model.getData().getMainTableObject();
                                flowBean.setFileOperationFlag(model.getData().getFileOperationFlag());
                                flowBean.setOpinionShowFlag(model.getData().getOpinionShowFlag());
                                setTableData(flowBean);
                                if (flowId.equals("zxHwZlHiddenDanger")) {
                                    setImgData(model.getData().getSubTableObject().getZxHwZlAttachment().getSubTableObject());
                                } else {
                                    setImgData(model.getData().getSubTableObject().getZxHwAqAttachment().getSubTableObject());
                                }
                                setShowButton(model.getData().getButtonList());
                                initTimeLineView(model.getData().getFlowHistoryList());
                                LoadingUtils.hideLoading();
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

    /**
     * 设置工序信息
     *
     * @param flowBean
     */
    private void setTableData(WorkingBean flowBean) {
        if (flowBean == null) {
            return;
        }
        // 保存

        if (!workId.equals("添加")) {
            flowBean.setProcessId(workId);
            flowBean.saveOrUpdate("processId=?", workId);
            btnChoice.setVisibility(View.GONE);
        }

        txtPressLocal.setText(flowBean.getLevelNameAll());   // 工序位置
        txtEntryTime.setText(DateUtils.setDataToStr(flowBean.getCreateTime()));     // 检查时间
        edtHiddenTroubleHeadline.setFocusable(false);
        if (flowId.equals("zxHwZlAttachment")) {
            edtHiddenTroubleHeadline.setText(flowBean.getTroubleTitle());     // 隐患标题
            if (flowBean.getTroubleLevel().equals("1")) {
                rBtn1.setChecked(true);
            } else if (flowBean.getTroubleLevel().equals("2")) {
                rBtn2.setChecked(true);
            } else {
                rBtn3.setChecked(true);
            }
            edtRectificationRequirements.setText(flowBean.getTroubleRequire());
        } else {
            edtHiddenTroubleHeadline.setText(flowBean.getDangerTitle());     // 隐患标题
            if (flowBean.getDangerLevel().equals("1")) {
                rBtn1.setChecked(true);
            } else if (flowBean.getDangerLevel().equals("2")) {
                rBtn2.setChecked(true);
            } else {
                rBtn3.setChecked(true);
            }
            edtRectificationRequirements.setText(flowBean.getDangerRequire());
        }
        rgLevel.setFocusable(false);
        btnChangeDate.setText(DateUtils.setDataToStr(flowBean.getDeadline()));
        edtRectificationRequirements.setFocusable(false);

        // 控制拍照按钮是否显示
        if (!workId.equals("添加") && !workId.equals("详情") && !StrUtil.equals("1", flowBean.getFileOperationFlag())) {
            imgBtnAdd.setVisibility(View.GONE);
        }
    }

    /**
     * 设置照片信息
     *
     * @param subTableObject
     */
    private void setImgData(List<PhotosBean> subTableObject) {
        // 查询本地保存照片
        String searchId;
        if (workId.equals("添加")) {
            searchId = StrUtil.isEmpty(levelId) ? "--" : levelId;
        } else if (workId.equals("详情")) {
            searchId = StrUtil.isEmpty(processId) ? "--" : processId;
        } else {
            searchId = workId;
        }
        List<PhotosBean> localPhoneList = DataSupport.where("isToBeUpLoad = 1 AND userId = ? AND processId = ? order by createTime desc", (String) SpUtil.get(mContext, ConstantsUtil.USER_ID, ""), searchId).find(PhotosBean.class);

        // 添加本地照片
        if (localPhoneList != null && localPhoneList.size() > 0) {
            photosList.addAll(localPhoneList);
        }
        // 添加服务器照片
        if (subTableObject != null && subTableObject.size() > 0) {
            photosList.addAll(subTableObject);
        }

        // 图片列表
        photosAdapter = new PhotosListAdapter(mContext, photosList, clickPhotoListener, "", "1");
        LinearLayoutManager ms = new LinearLayoutManager(this);
        ms.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvContractorDetails.setLayoutManager(ms);
        rvContractorDetails.setAdapter(photosAdapter);
    }

    /**
     * 设置显示按钮
     *
     * @param buttons
     */
    private void setShowButton(List<ButtonListModel> buttons) {
        if (buttons == null || buttons.size() == 0) {
            llButtons.setVisibility(View.GONE);
            return;
        }

        for (int i = 0; i < buttons.size(); i++) {
            ButtonListModel buttonModel = buttons.get(i);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            lp.setMargins(DensityUtil.dip2px(5), 0, DensityUtil.dip2px(5), 0);
            lp.weight = 1;
            Button button = new Button(this);
            button.setText(buttonModel.getButtonName());
            button.setTextSize(14);
            button.setTextColor(ContextCompat.getColor(mContext, R.color.white));
            button.setBackground(ContextCompat.getDrawable(mContext, R.drawable.btn_blue));
            if (workId.equals("添加") || workId.equals("详情")) {
                button.setOnClickListener(new onClick(i+1));
            } else {
                button.setOnClickListener(new ButtonClick(buttonModel));
            }
            llButtons.addView(button, lp);
        }
    }

    /**
     * 是否都填写了
     * @return
     */
    private boolean isFill() {
        if (StrUtil.isEmpty(txtPressLocal.getText().toString())) {
            ToastUtil.showShort(mContext, "请先选择工序！");
            return false;
        } else if (StrUtil.isEmpty(edtHiddenTroubleHeadline.getText().toString())) {
            ToastUtil.showShort(mContext, "请填写隐患标题！");
            return false;
        } else if (StrUtil.isEmpty(selectText)) {
            ToastUtil.showShort(mContext, "请选择隐患级别！");
            return false;
        } else if (StrUtil.isEmpty(btnChangeDate.getText().toString())) {
            ToastUtil.showShort(mContext, "请选择整改期限！");
            return false;
        } else if (StrUtil.isEmpty(edtRectificationRequirements.getText().toString())) {
            ToastUtil.showShort(mContext, "请填写整改要求！");
            return false;
        } else if (photosList.size() == 0) {
            ToastUtil.showShort(mContext, "请先拍照！");
            return false;
        } else {
            return true;
        }
    }


    /**
     * 本地保存
     */
    private void saveLocation() {
        WorkingBean bean = new WorkingBean();
        bean.setProcessId(workId.equals("添加") ? levelId : workId.equals("详情") ? processId : workId);
        bean.setType("2");
        bean.setFlowType(String.valueOf(SpUtil.get(mContext, "ToDoType", "2")));
        bean.setUserId((String) SpUtil.get(mContext, ConstantsUtil.USER_ID, "--"));
        bean.setLevelNameAll(txtPressLocal.getText().toString());
        bean.setCreateTime(System.currentTimeMillis());
        String type = (String) SpUtil.get(mContext, "ToDoType", "2");
        int level = 0;
        switch (selectText) {
            case "一般":
                level = 1;
                break;
            case "严重":
                level = 2;
                break;
            case "紧要":
                level = 3;
                break;
        }
        if (type.equals("2")) {
            bean.setTroubleTitle(edtHiddenTroubleHeadline.getText().toString());
            bean.setTroubleLevel(level+"");
            bean.setTroubleRequire(edtRectificationRequirements.getText().toString());
        } else {
            bean.setDangerTitle(edtHiddenTroubleHeadline.getText().toString());
            bean.setDangerLevel(level+"");
            bean.setDangerRequire(edtRectificationRequirements.getText().toString());
        }
        bean.setDeadline(DateUtil.parse(btnChangeDate.getText().toString()).getTime());
        bean.saveOrUpdate("processId=?", workId.equals("添加") ? levelId : workId.equals("详情") ? processId : workId);
    }

    private void clearData() {
        txtPressLocal.setText("");
        txtPressLocal.setFocusable(true);
        edtHiddenTroubleHeadline.setText("");
        btnChangeDate.setText("");
        edtRectificationRequirements.setText("");
        photosList.clear();
        photosAdapter.notifyDataSetChanged();
    }

    /**
     * 点击事件
     */
    private class onClick implements View.OnClickListener {
        private int point;

        public onClick(int point) {
            this.point = point;
        }

        @Override
        public void onClick(View v) {
            switch (point) {
                // 本地保存
                case 1:
                    if (isFill()) {
                        saveLocation();
                        ToastUtil.showShort(mContext, "保存成功！");
                        Intent intent = new Intent();
                        setResult(Activity.RESULT_OK, intent);
                        ToDoDetailsActivity.this.finish();
                    }
                    break;
                // 保存并添加
                case 2:
                    if (isFill()) {
                        saveLocation();
                        clearData();
                    }
                    break;
                // 提交审核
                case 3:
                    break;
            }
        }
    }

    /**
     * 点击事件
     */
    private class ButtonClick implements View.OnClickListener {
        private ButtonListModel buttonModel;

        public ButtonClick(ButtonListModel buttonModel) {
            this.buttonModel = buttonModel;
        }

        @Override
        public void onClick(View v) {
            if (imgBtnAdd.getVisibility() == View.VISIBLE) {
                if (buttonModel.getButtonName().contains("退") || buttonModel.getButtonName().contains("驳") || buttonModel.getButtonName().contains("回")) {
                    Intent intent = new Intent(mContext, PersonnelSelectionActivity.class);
                    ConstantsUtil.buttonModel = buttonModel;
                    buttonId = buttonModel.getButtonId();
                    startActivityForResult(intent, 201);
                } else {
                    ConstantsUtil.buttonModel = buttonModel;
                    buttonId = buttonModel.getButtonId();
                    toExaminePhoto();
                }
            } else {
                Intent intent = new Intent(mContext, PersonnelSelectionActivity.class);
                ConstantsUtil.buttonModel = buttonModel;
                buttonId = buttonModel.getButtonId();
                startActivityForResult(intent, 201);
            }
        }
    }

    /**
     * 上传照片--->提交审核
     */
    private void toExaminePhoto() {
        final List<PhotosBean> submitPictureList = new ArrayList<>();
        // 检查是否有新拍摄的照片
        for (PhotosBean photo : photosList) {
            if (photo.getIsToBeUpLoad() == 1) {
                submitPictureList.add(photo);
            }
        }

        if (submitPictureList.size() > 0) {
            // 上传
            UpLoadPhotosDialog upLoadPhotosDialog = new UpLoadPhotosDialog(mContext, 3, submitPictureList, new PromptListener() {
                @Override
                public void returnTrueOrFalse(boolean trueOrFalse) {
                    if (trueOrFalse) {
                        // 修改已上传照片状态
                        for (PhotosBean phone : photosList) {
                            phone.setIsToBeUpLoad(-1); // 已上传
                        }
                        // 更新适配器
                        if (null != photosAdapter) {
                            photosAdapter.notifyDataSetChanged();
                        }

                        Intent intent = new Intent(mContext, PersonnelSelectionActivity.class);
                        startActivityForResult(intent, 201);
                    }
                }
            });
            upLoadPhotosDialog.setCanceledOnTouchOutside(false);
            upLoadPhotosDialog.show();
        } else {
            Intent intent = new Intent(mContext, PersonnelSelectionActivity.class);
            startActivityForResult(intent, 201);
        }
    }

    /**
     * 提交、驳回
     */
    private void submitData() {
        LoadingUtils.showLoading(mContext);
        JSONObject object = new JSONObject(jsonData);
        String newJsonData, url;
        url = ConstantsUtil.submitFlow;
        String type;
        if (flowId.equals("zxHwZlAttachment")) {
            type = "zxHwZlAttachment";
        } else {
            type = "zxHwAqAttachment";
        }
        JSONArray jsonArr = new JSONArray(SpUtil.get(mContext, "uploadImgData", "[]"));
        JSONArray arr = new JSONArray(object.getJSONObject("data").getJSONObject("subTableObject").getJSONObject(type).getJSONArray("subTableDataObject"));
        jsonArr.addAll(arr);

        object.getJSONObject("data").getJSONObject("subTableObject").getJSONObject(type).put("subTableDataObject", jsonArr);
        newJsonData = String.valueOf(object.getJSONObject("data"));

        Request request = ChildThreadUtil.getRequest(mContext, url, newJsonData);
        ConstantsUtil.okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                ChildThreadUtil.toastMsgHidden(mContext, getString(R.string.server_exception));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                jsonData = response.body().string().toString();
                if (JsonUtils.isGoodJson(jsonData)) {
                    // 解析
                    final BaseModel model = gson.fromJson(jsonData, BaseModel.class);
                    if (model.isSuccess()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ChildThreadUtil.toastMsgHidden(mContext, model.getMessage());
                                ConstantsUtil.isLoading = true;
                                finish();
                            }
                        });
                    } else {
                        ChildThreadUtil.toastMsgHidden(mContext, getString(R.string.json_error));
                    }
                }
            }
        });
    }

    /**
     * 图片点击事件监听--->全屏预览图片
     */
    private ShowPhotoListener clickPhotoListener = new ShowPhotoListener() {
        @Override
        public void selectWayOrShowPhoto(boolean isShowPhoto, String point, String photoUrl, int isUpLoad) {
            // 图片浏览
            ArrayList<String> urls = new ArrayList<>();
            int len = photosList.size();
            for (int i = 0; i < len; i++) {
                String fileUrl = photosList.get(i).getUrl();
                /*if (!TextUtils.isEmpty(fileUrl) && !fileUrl.contains(ConstantsUtil.SAVE_PATH)) {
                    fileUrl = ConstantsUtil.BASE_URL + ConstantsUtil.prefix + fileUrl;
                }*/
                urls.add(fileUrl);
            }
            Intent intent = new Intent(mContext, ShowPhotosActivity.class);
            intent.putExtra(ShowPhotosActivity.EXTRA_IMAGE_URLS, urls);
            intent.putExtra(ShowPhotosActivity.EXTRA_IMAGE_INDEX, Integer.valueOf(point));
            startActivity(intent);
        }
    };

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

    /**
     * 检查拍照权限
     */
    private void checkPhotosPermission() {
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
     * 回调
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
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
                    // 涂鸦参数
                    GraffitiParams params = new GraffitiParams();
                    // 图片路径
                    params.mImagePath = uri.toString();

                    params.mSavePath = ConstantsUtil.SAVE_PATH + fileUrlName;
                    // 初始画笔大小
                    params.mPaintSize = 20;
                    // 启动涂鸦页面
                    GraffitiActivity.startActivityForResult(mContext, params, 202);
                    break;
                case 110:
                    clearData();
                    txtPressLocal.setText(data.getStringExtra("procedureName"));
                    levelId = data.getStringExtra("levelId");
                    List<WorkingBean> workingBeanList = DataSupport.where("processId = ? order by createTime desc", levelId).find(WorkingBean.class);
                    WorkingBean workingBean = ObjectUtil.isNull(workingBeanList) || workingBeanList.size() == 0 ? null : workingBeanList.get(0);
                    setTableData(workingBean);
                    setImgData(new ArrayList<PhotosBean>());
                    List<HistoryBean> flowHistoryList = DataSupport.where("processId=?", levelId).find(HistoryBean.class);
                    initTimeLineView(ObjectUtil.isNull(flowHistoryList) ? new ArrayList<HistoryBean>() : flowHistoryList);
                    break;
                case 201:
                    JSONObject object = new JSONObject(jsonData);
                    object.getJSONObject("data").put("buttonId", buttonId);
                    object.getJSONObject("data").put("reviewNodeId", data.getStringExtra("reviewNodeId"));
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("value", data.getStringExtra("userId"));
                    jsonObject.put("label", data.getStringExtra("userName"));
                    jsonObject.put("type", data.getStringExtra("type"));
                    JSONArray jsonArray = new JSONArray();
                    jsonArray.add(jsonObject);
                    object.getJSONObject("data").put("reviewUserObjectList", jsonArray);
                    jsonData = object.toString();
                    submitData();
                    break;
                case 202:
                    addPhotoBean = new PhotosBean();
                    addPhotoBean.setIsToBeUpLoad(1);
                    addPhotoBean.setUrl(ConstantsUtil.SAVE_PATH + fileUrlName);
                    addPhotoBean.setProcessId(workId.equals("添加") ? levelId : workId.equals("详情") ? processId : workId);
                    addPhotoBean.setThumbUrl(ConstantsUtil.SAVE_PATH + fileUrlName);
                    addPhotoBean.setPhotoName(fileUrlName);
                    addPhotoBean.setCheckFlag("-1");
                    addPhotoBean.setIsNewAdd(1);
                    addPhotoBean.setRoleFlag("1");
                    addPhotoBean.setUserId((String) SpUtil.get(mContext, ConstantsUtil.USER_ID, ""));
                    addPhotoBean.setPhotoType((String) SpUtil.get(mContext, ConstantsUtil.USER_TYPE, ""));
                    addPhotoBean.setCreateTime(System.currentTimeMillis());
                    addPhotoBean.save();
                    photosList.add(addPhotoBean);
                    photosAdapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 日期选择
     */
    public void onYearMonthDayPicker() {
        final DatePicker picker = new DatePicker(mContext);
        picker.setCanceledOnTouchOutside(true);
        picker.setUseWeight(true);
        picker.setTopPadding(ConvertUtils.toPx(mContext, 10));
        picker.setRangeEnd(2100, 1, 31);
        picker.setRangeStart(2000, 1, 31);
        String date = btnChangeDate.getText().toString();
        Date time = StrUtil.isEmpty(date) ? new Date() : DateUtil.parse(date);
        picker.setSelectedItem(DateUtil.year(time), DateUtil.month(time) + 1, DateUtil.dayOfMonth(time));
        picker.setResetWhileWheel(false);
        picker.setOnDatePickListener(new DatePicker.OnYearMonthDayPickListener() {
            @Override
            public void onDatePicked(String year, String month, String day) {
                btnChangeDate.setText(year + "-" + month + "-" + day);
            }
        });
        picker.setOnWheelListener(new DatePicker.OnWheelListener() {
            @Override
            public void onYearWheeled(int index, String year) {
                picker.setTitleText(year + "-" + picker.getSelectedMonth() + "-" + picker.getSelectedDay());
            }

            @Override
            public void onMonthWheeled(int index, String month) {
                picker.setTitleText(picker.getSelectedYear() + "-" + month + "-" + picker.getSelectedDay());
            }

            @Override
            public void onDayWheeled(int index, String day) {
                picker.setTitleText(picker.getSelectedYear() + "-" + picker.getSelectedMonth() + "-" + day);
            }
        });
        picker.show();
    }

    /**
     * 点击事件
     *
     * @param v
     */
    @Event({R.id.imgBtnLeft, R.id.imgBtnAdd, R.id.btnRight, R.id.btnChoice, R.id.btnChangeDate})
    private void click(View v) {
        switch (v.getId()) {
            // 返回
            case R.id.imgBtnLeft:
                this.finish();
                break;
            // 选择位置
            case R.id.btnChoice:
                Intent intent = new Intent(mContext, ContractorTreeActivity.class);
                String type = (String) SpUtil.get(mContext, "ToDoType", "2");
                intent.putExtra("type", type);
                startActivityForResult(intent, 110);
                break;
            // 选择日期
            case R.id.btnChangeDate:
                onYearMonthDayPicker();
                break;
            // 拍照
            case R.id.imgBtnAdd:
                String sdCardSize = AppInfoUtil.getSDAvailableSize();
                if (Integer.valueOf(sdCardSize) < 10) {
                    ToastUtil.showShort(mContext, "当前手机内存卡已无可用空间，请清理后再进行拍照！");
                } else {
                    checkPhotosPermission();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ScreenManagerUtil.popActivity(this);    // 退出当前activity
        // 取消屏幕旋转监听
        if (mAlbumOrientationEventListener != null) {
            mAlbumOrientationEventListener.disable();
        }
    }
}
