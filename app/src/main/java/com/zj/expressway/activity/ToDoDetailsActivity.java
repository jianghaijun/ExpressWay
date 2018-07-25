package com.zj.expressway.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.IdRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.zj.expressway.R;
import com.zj.expressway.adapter.PhotosListAdapter;
import com.zj.expressway.adapter.TimeLineAdapter;
import com.zj.expressway.base.BaseNoImmersionBarActivity;
import com.zj.expressway.bean.HiddenDangerTypeBean;
import com.zj.expressway.bean.HistoryBean;
import com.zj.expressway.bean.PhotosBean;
import com.zj.expressway.bean.WorkingBean;
import com.zj.expressway.bean.Working_Bean;
import com.zj.expressway.dialog.PromptDialog;
import com.zj.expressway.dialog.UpLoadPhotosDialog;
import com.zj.expressway.listener.PermissionListener;
import com.zj.expressway.listener.PromptListener;
import com.zj.expressway.listener.ShowPhotoListener;
import com.zj.expressway.model.ButtonListModel;
import com.zj.expressway.model.WorkModel;
import com.zj.expressway.popwindow.H5PopupWindow;
import com.zj.expressway.utils.AppInfoUtil;
import com.zj.expressway.utils.ChildThreadUtil;
import com.zj.expressway.utils.ConstantsUtil;
import com.zj.expressway.utils.DateUtils;
import com.zj.expressway.utils.FileUtil;
import com.zj.expressway.utils.JsonUtils;
import com.zj.expressway.utils.JudgeNetworkIsAvailable;
import com.zj.expressway.utils.LoadingUtils;
import com.zj.expressway.utils.ProviderUtil;
import com.zj.expressway.utils.ScreenManagerUtil;
import com.zj.expressway.utils.SpUtil;
import com.zj.expressway.utils.ToastUtil;

import org.litepal.crud.DataSupport;
import org.xutils.common.util.DensityUtil;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hzw.graffiti.GraffitiActivity;
import cn.hzw.graffiti.GraffitiParams;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 详情
 */
public class ToDoDetailsActivity extends BaseNoImmersionBarActivity {
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
    @ViewInject(R.id.txtHiddenTroubleType)
    private TextView txtHiddenTroubleType;
    @ViewInject(R.id.txtHiddenTroubleLevel)
    private TextView txtHiddenTroubleLevel;
    @ViewInject(R.id.txtDangerDescription)
    private TextView txtDangerDescription;
    @ViewInject(R.id.edtHiddenTroubleHeadline)
    private EditText edtHiddenTroubleHeadline;
    @ViewInject(R.id.rgLevel)
    private RadioGroup rgLevel;
    @ViewInject(R.id.gridLType)
    private GridLayout gridLType;
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
    @ViewInject(R.id.edtDangerDescription)
    private EditText edtDangerDescription;
    @ViewInject(R.id.rvContractorDetails)
    private RecyclerView rvContractorDetails;
    @ViewInject(R.id.rvTimeMarker)
    private RecyclerView rvTimeMarker;
    @ViewInject(R.id.view)
    private View view;
    @ViewInject(R.id.llButtons)
    private LinearLayout llButtons;
    private TimeLineAdapter timeLineAdapter;
    private boolean isSubmit = false;
    // 图片列表
    private PhotosListAdapter photosAdapter;
    private List<PhotosBean> photosList = new ArrayList<>();
    private List<HiddenDangerTypeBean> typeList = new ArrayList<>();

    // 拍照
    private Activity mContext;
    private String workId, flowId, processId, jsonData, buttonId, fileUrlName, strFilePath, selectText = "";
    private Gson gson = new Gson();
    private WorkModel model;
    private PhotosBean addPhotoBean;
    private WorkingBean deleteWorkingBean; // 本地数据提交审核后--删除
    private final int selectProcessPath = 1001; // 选择工序位置
    private final int takePhoto = 1002; // 拍照
    private final int graffiti = 1003; // 涂鸦
    private final int selectPersonal = 1004; // 选人
    private String checkType; // 质量or安全
    private String selectLevelId = ""; // 选中层级id
    private String levelIdAll = ""; // 选中层级id
    private String userId; // 用户Id
    private String uuid = RandomUtil.randomUUID().replaceAll("-", "");

    private String tType;
    private H5PopupWindow p;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_quality_safety_details);

        mContext = this;
        x.view().inject(this);
        ScreenManagerUtil.pushActivity(this);

        // actionBar
        imgBtnLeft.setVisibility(View.VISIBLE);
        imgBtnLeft.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.back_btn));

        // 任务id
        workId = getIntent().getStringExtra("workId");
        flowId = getIntent().getStringExtra("flowId");
        processId = getIntent().getStringExtra("processId");
        userId = (String) SpUtil.get(mContext, ConstantsUtil.USER_ID, "");

        checkType = (String) SpUtil.get(mContext, ConstantsUtil.PROCESS_LIST_TYPE, "2");
        if (!StrUtil.equals("2", checkType)) {
            rBtn2.setVisibility(View.GONE);
        } else {
            txtHiddenTroubleType.setText("问题类型：");
            txtHiddenTroubleLevel.setText("问题级别：");
            txtDangerDescription.setText("问题描述：");
            rBtn3.setText("紧要");
        }

        if (StrUtil.equals("add", workId)) {
            if (StrUtil.equals("2", checkType)) {
                txtTitle.setText("质量添加");
            } else {
                txtTitle.setText("安全添加");
            }
        } else if (StrUtil.equals("details", workId)) {
            if (StrUtil.equals("2", checkType)) {
                txtTitle.setText("质量修改");
            } else {
                txtTitle.setText("安全修改");
            }
        } else {
            txtTitle.setText(R.string.app_name);
        }

        initFilePath();

        // 是否直接弹出相机
        if (getIntent().getBooleanExtra("isPopTakePhoto", false)) {
            checkPhotosPermission();
        }

        // 隐患级别点击事件
        rgLevel.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                RadioButton radioButton = (RadioButton) findViewById(checkedId);
                selectText = radioButton.getText().toString();
            }
        });

        // 质量安全--工序
        if (StrUtil.equals(workId, "add")) {
            txtEntryTime.setText(DateUtils.setDataToStr(System.currentTimeMillis()));
            setImgData(new ArrayList<PhotosBean>(), selectLevelId);
            setShowButton(setButtons());
            setHiddenTroubleType(StrUtil.equals("2", checkType), "", true);
        } else if (StrUtil.equals(workId, "details")) {
            List<WorkingBean> workingBeanList = DataSupport.where("processId = ? order by createTime desc", processId).find(WorkingBean.class);
            deleteWorkingBean = ObjectUtil.isNull(workingBeanList) || workingBeanList.size() == 0 ? null : workingBeanList.get(0);
            selectLevelId = deleteWorkingBean.getLevelId();
            levelIdAll = deleteWorkingBean.getLevelIdAll();
            setTableData(deleteWorkingBean);
            setImgData(new ArrayList<PhotosBean>(), processId);
            setShowButton(setButtons());
            List<HistoryBean> flowHistoryList = DataSupport.where("processId=?", processId).find(HistoryBean.class);
            initTimeLineView(ObjectUtil.isNull(flowHistoryList) ? new ArrayList<HistoryBean>() : flowHistoryList);
            setHiddenTroubleType(StrUtil.equals("2", checkType), StrUtil.equals("2", checkType) ? deleteWorkingBean.getTroubleType() : deleteWorkingBean.getDangerType(), true);
        } else {
            initData();
        }
    }

    /**
     * 隐患类型
     *
     * @param isAdd
     * @param selectId
     */
    private void setHiddenTroubleType(boolean isAdd, String selectId, boolean isCanClick) {
        if (selectId == null) {
            return;
        }
        if (!isAdd) {
            typeList.add(addBean("安全管理", 100, selectId));
            typeList.add(addBean("文明施工", 101, selectId));
            typeList.add(addBean("临边防护", 102, selectId));
            typeList.add(addBean("高处作业", 103, selectId));
            typeList.add(addBean("基坑支护", 104, selectId));
            typeList.add(addBean("模板工程", 105, selectId));
            typeList.add(addBean("施工机具", 106, selectId));
            typeList.add(addBean("脚手架", 107, selectId));
            typeList.add(addBean("交通安全", 108, selectId));
            typeList.add(addBean("个体防护", 109, selectId));
            typeList.add(addBean("起重吊装", 110, selectId));
            typeList.add(addBean("施工用电", 111, selectId));
            typeList.add(addBean("消防防火", 112, selectId));
        } else {
            typeList.add(addBean("外观", 100, selectId));
            typeList.add(addBean("尺寸", 101, selectId));
            typeList.add(addBean("坐标", 102, selectId));
            typeList.add(addBean("工序", 103, selectId));
            typeList.add(addBean("工艺", 104, selectId));
            typeList.add(addBean("其它", 105, selectId));
        }

        for (int i = 0; i < typeList.size(); i++) {
            final int j = i;
            HiddenDangerTypeBean bean = typeList.get(i);
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(bean.getTypeTitle());
            if (bean.isSelect()) {
                checkBox.setChecked(true);
            } else {
                checkBox.setChecked(false);
            }

            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (typeList.get(j).isSelect()) {
                        typeList.get(j).setSelect(false);
                    } else {
                        typeList.get(j).setSelect(true);
                    }
                }
            });

            if (!isCanClick) {
                checkBox.setClickable(false);
            }
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((DensityUtil.getScreenWidth() - DensityUtil.dip2px(90)) / 3, DensityUtil.dip2px(40));
            gridLType.addView(checkBox, params);
        }
    }

    /**
     * 添加隐患类型
     *
     * @param title
     * @param id
     * @param selectId
     * @return
     */
    private HiddenDangerTypeBean addBean(String title, int id, String selectId) {
        HiddenDangerTypeBean bean = new HiddenDangerTypeBean();
        bean.setTypeTitle(title);
        bean.setTypeId(id);
        if (selectId.contains("" + id)) {
            bean.setSelect(true);
        } else {
            bean.setSelect(false);
        }
        return bean;
    }

    /**
     * 初始化
     */
    private void initData() {
        if (JudgeNetworkIsAvailable.isNetworkAvailable(this)) {
            getData();
        } else {
            List<WorkingBean> workingBeanList = DataSupport.where("processId = ? order by createTime desc", workId).find(WorkingBean.class);
            WorkingBean workingBean = ObjectUtil.isNull(workingBeanList) || workingBeanList.size() == 0 ? null : workingBeanList.get(0);
            setTableData(workingBean);
            setImgData(new ArrayList<PhotosBean>(), workId);
            if (workingBean != null && StrUtil.isNotEmpty(workingBean.getFileOperationFlag()) && !StrUtil.equals(workingBean.getFileOperationFlag(), "1")) {
                llButtons.setVisibility(View.GONE);
            } else {
                List<ButtonListModel> buttons = new ArrayList<>();
                ButtonListModel btnModel = new ButtonListModel();
                btnModel.setButtonId("save");
                btnModel.setButtonName("本地保存");
                buttons.add(btnModel);
                setShowButton(buttons);
            }
            List<HistoryBean> flowHistoryList = DataSupport.where("processId=?", workId).find(HistoryBean.class);
            initTimeLineView(ObjectUtil.isNull(flowHistoryList) ? new ArrayList<HistoryBean>() : flowHistoryList);
            setHiddenTroubleType(StrUtil.equals("2", checkType), StrUtil.equals("2", checkType) ? workingBean.getTroubleType() : workingBean.getDangerType(), false);
        }
    }

    /**
     * add按钮
     *
     * @return
     */
    private List<ButtonListModel> setButtons() {
        List<ButtonListModel> buttons = new ArrayList<>();
        ButtonListModel btnModel = new ButtonListModel();
        btnModel.setButtonId("save");
        btnModel.setButtonName("本地保存");
        buttons.add(btnModel);
        ButtonListModel btnSaveAdd = new ButtonListModel();
        btnSaveAdd.setButtonId("saveAndAdd");
        btnSaveAdd.setButtonName("保存继续添加");
        buttons.add(btnSaveAdd);
        if (JudgeNetworkIsAvailable.isNetworkAvailable(this)) {
            ButtonListModel examine = new ButtonListModel();
            examine.setButtonId("examine");
            examine.setButtonName("发起审核");
            buttons.add(examine);
        }
        return buttons;
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

        rvTimeMarker.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
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
        File imgFile = new File(strFilePath);
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
        if (StrUtil.equals("2", checkType)) {
            obj.put("apiName", "getZxHwZlTroubleDetails");
        } else {
            obj.put("apiName", "getZxHwAqHiddenDangerDetails");
        }
        obj.put("apiType", "POST");
        obj.put("flowId", flowId);
        Request request = ChildThreadUtil.getRequest(mContext, ConstantsUtil.openFlow, obj.toString());
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
                                if (model.getData().getApiData() != null) {
                                    Gson gson = new Gson();
                                    WorkingBean flowBean = gson.fromJson(model.getData().getApiData(), WorkingBean.class);
                                    Working_Bean flow_Bean = gson.fromJson(model.getData().getApiData(), Working_Bean.class);
                                    //flowBean.setFileOperationFlag(model.getData().getFileOperationFlag());
                                    //flowBean.setOpinionShowFlag(model.getData().getOpinionShowFlag());
                                    setTableData(flowBean);
                                    if (StrUtil.equals(flowId, "zxHwZlTrouble")) {
                                        setImgData(flow_Bean.getZlAttachmentList(), workId);
                                    } else {
                                        setImgData(flow_Bean.getAqAttachmentList(), workId);
                                    }
                                    setShowButton(model.getData().getFlowButtons());
                                    initTimeLineView(model.getData().getFlowHistoryList());
                                    tType = StrUtil.equals("2", checkType) ? flowBean.getTroubleType() : flowBean.getDangerType();
                                    selectLevelId = flowBean.getLevelId();
                                    levelIdAll = flowBean.getLevelIdAll();
                                    setHiddenTroubleType(StrUtil.equals("2", checkType), StrUtil.equals("2", checkType) ? flowBean.getTroubleType() : flowBean.getDangerType(), false);
                                }
                                LoadingUtils.hideLoading();


                                /*WorkingBean flowBean = model.getData().getMainTableObject();
                                flowBean.setFileOperationFlag(model.getData().getFileOperationFlag());
                                flowBean.setOpinionShowFlag(model.getData().getOpinionShowFlag());
                                setTableData(flowBean);
                                if (StrUtil.equals(flowId, "zxHwZlTrouble")) {
                                    setImgData(model.getData().getSubTableObject().getZxHwZlAttachment().getSubTableObject(), workId);
                                } else {
                                    setImgData(model.getData().getSubTableObject().getZxHwAqAttachment().getSubTableObject(), workId);
                                }
                                setShowButton(model.getData().getButtonList());
                                initTimeLineView(model.getData().getFlowHistoryList());
                                tType = StrUtil.equals("2", checkType) ? flowBean.getTroubleType() : flowBean.getDangerType();
                                selectLevelId = flowBean.getLevelId();
                                levelIdAll = flowBean.getLevelIdAll();

                                setHiddenTroubleType(StrUtil.equals("2", checkType), StrUtil.equals("2", checkType) ? flowBean.getTroubleType() : flowBean.getDangerType());
                                LoadingUtils.hideLoading();*/
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
        if (!StrUtil.equals(workId, "details") && JudgeNetworkIsAvailable.isNetworkAvailable(this)) {
            flowBean.setProcessId(workId);
            flowBean.saveOrUpdate("processId=?", workId);
        }

        txtPressLocal.setText(flowBean.getLevelNameAll().replaceAll(",", "→"));   // 工序位置
        txtEntryTime.setText(DateUtils.setDataToStr(flowBean.getCreateTime()));     // 检查时间
        if (StrUtil.equals(flowId, "zxHwZlTrouble")) {
            edtHiddenTroubleHeadline.setText(flowBean.getTroubleTitle());     // 隐患标题
            if (StrUtil.equals(flowBean.getTroubleLevel(), "1")) {
                rBtn1.setChecked(true);
                selectText = "一般";
            } else if (StrUtil.equals(flowBean.getTroubleLevel(), "2")) {
                rBtn2.setChecked(true);
                selectText = "严重";
            } else {
                rBtn3.setChecked(true);
                selectText = "紧要";
            }
            edtRectificationRequirements.setText(flowBean.getTroubleRequire());
            edtDangerDescription.setText(flowBean.getTroubleContent());
        } else {
            edtHiddenTroubleHeadline.setText(flowBean.getDangerTitle());     // 隐患标题
            if (StrUtil.equals(flowBean.getDangerLevel(), "1")) {
                rBtn1.setChecked(true);
                selectText = "一般";
            } else {
                rBtn3.setChecked(true);
                rBtn3.setText("重大");
                selectText = "重大";
            }
            edtRectificationRequirements.setText(flowBean.getDangerRequire());
            edtDangerDescription.setText(flowBean.getDangerContent());
        }
        btnChangeDate.setText(DateUtils.setDataToStr2(flowBean.getDeadline()));

        if (!StrUtil.equals(workId, "details")) {
            btnChoice.setVisibility(View.GONE);
            btnChangeDate.setEnabled(false);
            rBtn1.setClickable(false);
            rBtn2.setClickable(false);
            rBtn3.setClickable(false);
            edtDangerDescription.setFocusable(false);
            edtHiddenTroubleHeadline.setFocusable(false);
            edtRectificationRequirements.setFocusable(false);
        }

        // 控制拍照按钮是否显示
        if (!StrUtil.equals(workId, "add") && !StrUtil.equals(workId, "details") && !StrUtil.equals("1", flowBean.getFileOperationFlag())) {
            imgBtnAdd.setVisibility(View.GONE);
        }
    }

    /**
     * 设置照片信息
     *
     * @param subTableObject
     * @param searchId
     */
    private void setImgData(List<PhotosBean> subTableObject, String searchId) {
        // 查询本地保存照片
        List<PhotosBean> localPhoneList = DataSupport.where("isToBeUpLoad = 1 AND userId = ? AND processId = ? order by createTime desc", userId, searchId).find(PhotosBean.class);

        // add本地照片
        if (localPhoneList != null && localPhoneList.size() > 0) {
            photosList.addAll(localPhoneList);
        }
        // add服务器照片
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
        llButtons.removeAllViews();
        if (buttons == null || buttons.size() == 0) {
            llButtons.setVisibility(View.GONE);
            return;
        }

        for (int i = buttons.size(); i > 0; i--) {
            ButtonListModel buttonModel = buttons.get(i-1);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            lp.setMargins(DensityUtil.dip2px(5), 0, DensityUtil.dip2px(5), 0);
            lp.weight = 1;
            Button button = new Button(this);
            button.setText(buttonModel.getButtonName());
            button.setTextSize(14);
            button.setTextColor(ContextCompat.getColor(mContext, R.color.white));
            button.setBackground(ContextCompat.getDrawable(mContext, R.drawable.btn_blue));
            if (StrUtil.equals(workId, "add") || StrUtil.equals(workId, "details")) {
                button.setOnClickListener(new onClick(i + 1));
            } else {
                button.setOnClickListener(new ButtonClick(buttonModel));
            }
            llButtons.addView(button, lp);
        }

        /*for (int i = 0; i < buttons.size(); i++) {
            ButtonListModel buttonModel = buttons.get(i);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            lp.setMargins(DensityUtil.dip2px(5), 0, DensityUtil.dip2px(5), 0);
            lp.weight = 1;
            Button button = new Button(this);
            button.setText(buttonModel.getButtonName());
            button.setTextSize(14);
            button.setTextColor(ContextCompat.getColor(mContext, R.color.white));
            button.setBackground(ContextCompat.getDrawable(mContext, R.drawable.btn_blue));
            if (StrUtil.equals(workId, "add") || StrUtil.equals(workId, "details")) {
                button.setOnClickListener(new onClick(i + 1));
            } else {
                button.setOnClickListener(new ButtonClick(buttonModel));
            }
            llButtons.addView(button, lp);
        }*/
    }

    /**
     * 是否都填写了
     *
     * @return
     */
    private boolean isFill() {
        if (StrUtil.isEmpty(txtPressLocal.getText().toString())) {
            ToastUtil.showShort(mContext, "请先选择工序！");
            return false;
        } else if (StrUtil.isEmpty(edtHiddenTroubleHeadline.getText().toString())) {
            ToastUtil.showShort(mContext, "请填写标题！");
            return false;
        } else if (StrUtil.isEmpty(selectText)) {
            ToastUtil.showShort(mContext, "请选择级别！");
            return false;
        } else if (StrUtil.isEmpty(isSelectType())) {
            ToastUtil.showShort(mContext, "请选择类型！");
            return false;
        } else if (StrUtil.isEmpty(btnChangeDate.getText().toString())) {
            ToastUtil.showShort(mContext, "请选择整改期限！");
            return false;
        } else if (StrUtil.isEmpty(btnChangeDate.getText().toString())) {
            ToastUtil.showShort(mContext, "请选择整改期限！");
            return false;
        } else if (StrUtil.isEmpty(edtDangerDescription.getText().toString())) {
            ToastUtil.showShort(mContext, "请填写隐患描述！");
            return false;
        } else if (photosList.size() == 0) {
            ToastUtil.showShort(mContext, "请先拍照！");
            return false;
        } else {
            return true;
        }
    }

    /**
     * 判断是否选择隐患类型
     *
     * @return
     */
    private String isSelectType() {
        StringBuffer sb = new StringBuffer();
        int size = typeList.size();
        for (int i = 0; i < size; i++) {
            if (typeList.get(i).isSelect()) {
                if (i < size - 1) {
                    sb.append(typeList.get(i).getTypeId() + ",");
                } else {
                    sb.append(typeList.get(i).getTypeId() + "");
                }
            }
        }
        return sb.toString();
    }

    /**
     * 本地保存
     */
    private void saveLocation() {
        ConstantsUtil.isLoading = true;
        WorkingBean bean = new WorkingBean();
        bean.setProcessId(StrUtil.equals(workId, "add") ? uuid : processId);
        bean.setType("2");
        if (StrUtil.isNotEmpty(selectLevelId)) {
            bean.setLevelId(selectLevelId);
            bean.setLevelIdAll(levelIdAll);
        }
        bean.setFlowType(checkType);
        bean.setUserId(userId);
        bean.setLevelNameAll(txtPressLocal.getText().toString());
        bean.setCreateTime(System.currentTimeMillis());
        int level = 0;
        switch (selectText) {
            case "一般":
                level = 1;
                break;
            case "严重":
                level = 2;
                break;
            case "紧要":
            case "重大":
                level = 3;
                break;
        }

        if (StrUtil.equals(checkType, "2")) {
            bean.setTroubleTitle(edtHiddenTroubleHeadline.getText().toString());
            bean.setTroubleLevel(level + "");
            bean.setTroubleRequire(edtRectificationRequirements.getText().toString());
            bean.setTroubleType(isSelectType());
            bean.setTroubleContent(edtDangerDescription.getText().toString());
        } else {
            bean.setDangerTitle(edtHiddenTroubleHeadline.getText().toString());
            bean.setDangerLevel(level + "");
            bean.setDangerRequire(edtRectificationRequirements.getText().toString());
            bean.setDangerType(isSelectType());
            bean.setDangerContent(edtDangerDescription.getText().toString());
        }
        bean.setDeadline(StrUtil.isEmpty(btnChangeDate.getText().toString()) ? System.currentTimeMillis() : DateUtil.parse(btnChangeDate.getText().toString()).getTime());
        bean.saveOrUpdate("processId=?", StrUtil.equals(workId, "add") ? uuid : processId);
    }

    /**
     * 清除数据
     */
    private void clearData() {
        uuid = RandomUtil.randomUUID().replaceAll("-", "");
        workId = "add";
        txtPressLocal.setText("");
        txtPressLocal.setFocusable(true);
        txtEntryTime.setText(DateUtils.setDataToStr(System.currentTimeMillis()));     // 检查时间
        edtHiddenTroubleHeadline.setText("");
        btnChangeDate.setText("");
        edtRectificationRequirements.setText("");
        edtDangerDescription.setText("");
        photosList.clear();
        photosAdapter.notifyDataSetChanged();
    }

    /**
     * 提交审核
     *
     * @param reviewNodeId
     * @param userId
     * @param userName
     * @param userType
     */
    public void submitData(String reviewNodeId, String userId, String userName, String userType) {
        Map<String, Object> object = new HashMap<>();
        Map<String, Object> tableDataMap = new HashMap<>();
        tableDataMap.put("levelNameAll", txtPressLocal.getText().toString());
        tableDataMap.put("levelId", selectLevelId);
        tableDataMap.put("levelIdAll", levelIdAll);
        tableDataMap.put("createTime", System.currentTimeMillis());
        String type = (String) SpUtil.get(mContext, ConstantsUtil.PROCESS_LIST_TYPE, "2");
        if (StrUtil.equals(type, "2")) {
            tableDataMap.put("troubleTitle", edtHiddenTroubleHeadline.getText().toString());
        } else {
            tableDataMap.put("dangerTitle", edtHiddenTroubleHeadline.getText().toString());
        }
        int level = 0;
        switch (selectText) {
            case "一般":
                level = 1;
                break;
            case "严重":
                level = 2;
                break;
            case "紧要":
            case "重大":
                level = 3;
                break;
        }

        if (StrUtil.equals(type, "2")) {
            tableDataMap.put("troubleLevel", level);
            tableDataMap.put("troubleType", isSelectType());
            tableDataMap.put("troubleContent", edtDangerDescription.getText().toString());
        } else {
            tableDataMap.put("dangerLevel", level);
            tableDataMap.put("dangerType", isSelectType());
            tableDataMap.put("dangerContent", edtDangerDescription.getText().toString());
        }

        tableDataMap.put("deadline", DateUtil.parse(btnChangeDate.getText().toString()).getTime());
        if (StrUtil.equals(type, "2")) {
            tableDataMap.put("troubleRequire", edtRectificationRequirements.getText().toString());
        } else {
            tableDataMap.put("dangerRequire", edtRectificationRequirements.getText().toString());
        }

        Map<String, Object> jsonObject = new HashMap<>();
        jsonObject.put("value", userId);
        jsonObject.put("label", userName);
        jsonObject.put("type", userType);
        List<Map<String, Object>> jsonArray = new ArrayList<>();
        jsonArray.add(jsonObject);

        JSONArray jsonArr = new JSONArray(SpUtil.get(mContext, "uploadImgData", "[]"));
        Map<String, Object> b = new HashMap<>();
        Map<String, Object> o = new HashMap<>();
        o.put("subTableType", "2");
        o.put("subTablePrimaryIdName", "uid");
        o.put("subTableDataObject", jsonArr);

        if (StrUtil.equals(type, "2")) {
            b.put("zxHwZlAttachment", o);
        } else {
            b.put("zxHwAqAttachment", o);
        }

        object.put("title", edtHiddenTroubleHeadline.getText().toString());
        if (StrUtil.equals(type, "2")) {
            object.put("flowId", "zxHwZlTrouble");
            object.put("mainTableName", "zxHwZlTrouble");
            object.put("mainTablePrimaryIdName", "troubleId");
        } else {
            object.put("flowId", "zxHwAqHiddenDanger");
            object.put("mainTableName", "zxHwAqHiddenDanger");
            object.put("mainTablePrimaryIdName", "dangerId");
        }
        object.put("mainTablePrimaryId", "");
        object.put("mainTableDataObject", tableDataMap);
        object.put("reviewNodeId", reviewNodeId);
        object.put("reviewUserObjectList", jsonArray);
        //object.put("subTableObject", b);

        org.json.JSONObject data = new org.json.JSONObject(object);
        // 暂时注释掉
        //submitData(data.toString());

        Map<String, Object> apiBody = tableDataMap;
        //tableDataMap.put("subTableObject", b);

        org.json.JSONObject starFlowData = new org.json.JSONObject(tableDataMap);

        SpUtil.put(mContext, "JSONData", data.toString());

        Map<String, Object> newobj = new HashMap<>();
        Map<String, Object> updataObj = new HashMap<>();
        if (StrUtil.equals(type, "2")) {
            newobj.put("apiName", "addZxHwZlTrouble");
            updataObj.put("apiName", "updateZxHwZlTrouble");
            newobj.put("flowId", "zxHwZlTrouble");
            updataObj.put("flowId", "zxHwZlTrouble");
        } else {
            newobj.put("apiName", "addZxHwAqHiddenDanger");
            updataObj.put("apiName", "updateZxHwAqHiddenDanger");
            newobj.put("flowId", "zxHwAqHiddenDanger");
            updataObj.put("flowId", "zxHwAqHiddenDanger");
        }

        newobj.put("apiType", "POST");
        updataObj.put("apiType", "POST");
        newobj.put("title", txtPressLocal.getText().toString() + "→" + edtHiddenTroubleHeadline.getText().toString());
        updataObj.put("title", txtPressLocal.getText().toString() + "→" + edtHiddenTroubleHeadline.getText().toString());

        if (StrUtil.equals(type, "2")) {
            apiBody.put("zlAttachmentList", jsonArr);
        } else {
            apiBody.put("aqAttachmentList", jsonArr);
        }

        newobj.put("apiBody", apiBody);
        updataObj.put("apiBody", apiBody);

        SpUtil.put(mContext, "startFlowData", new Gson().toJson(newobj));
        SpUtil.put(mContext, "updateFlowData", new Gson().toJson(updataObj));

        // 调用h5
        H5PopupWindow p = new H5PopupWindow(mContext, StrUtil.equals(workId, "details"), processId, deleteWorkingBean, ConstantsUtil.star, promptListener);
        p.setTouchable(true);
        p.setFocusable(true); //设置点击menu以外其他地方以及返回键退出
        p.setOutsideTouchable(true);   //设置触摸外面时消失
        p.showAtDropDownRight(view);

        //submitData(new Gson().toJson(apiBody));
    }

    PromptListener promptListener = new PromptListener() {
        @Override
        public void returnTrueOrFalse(boolean trueOrFalse) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    p.dismiss();
                }
            });
        }
    };

    /**
     * 提交、驳回
     */
    /*private void submitData(String obj) {
        LoadingUtils.showLoading(mContext);
        String url = ConstantsUtil.startFlow;

        Request request = ChildThreadUtil.getRequest(mContext, url, obj);
        ConstantsUtil.okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                ChildThreadUtil.toastMsgHidden(mContext, getString(R.string.server_exception));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                jsonData = response.body().string().toString();
                LoadingUtils.hideLoading();
                if (JsonUtils.isGoodJson(jsonData)) {
                    Gson gson = new Gson();
                    final WorkModel model = gson.fromJson(jsonData, WorkModel.class);
                    if (model.isSuccess()) {
                        mContext.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                isSubmit = true;
                                // 清空操作按钮
                                setShowButton(null);
                                btnChoice.setVisibility(View.GONE);
                                ConstantsUtil.isLoading = true;
                                if (StrUtil.equals(workId, "details")) {
                                    DataSupport.deleteAll(PhotosBean.class, "processId=?", processId);
                                    if (deleteWorkingBean != null) {
                                        deleteWorkingBean.delete();
                                    }
                                }

                                List<ButtonListModel> buttonList = model.getData().getButtonList();
                                for (ButtonListModel buttonListModel : buttonList) {
                                    if (StrUtil.equals(buttonListModel.getButtonId(), "submit")) {
                                        boolean isEdit = buttonListModel.getNextShowFlowInfoList() == null || buttonListModel.getNextShowFlowInfoList().size() == 0 ? false : buttonListModel.getNextShowFlowInfoList().get(0).isEdit();
                                        ConstantsUtil.buttonModel = buttonListModel;
                                        buttonId = buttonListModel.getButtonId();
                                        jumpSelectPersonal(isEdit);
                                        return;
                                    }
                                }
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
    }*/

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
            buttonId = buttonModel.getButtonId();
            ConstantsUtil.buttonModel = buttonModel;
            //boolean isEdit = buttonModel.getNextShowFlowInfoList() == null || buttonModel.getNextShowFlowInfoList().size() == 0 ? false : buttonModel.getNextShowFlowInfoList().get(0).isEdit();
            /*if (buttonModel.getButtonId().contains("reject")) {
                jumpSelectPersonal(isEdit);
            } else*/
            if (buttonModel.getButtonId().contains("save")) {
                ToastUtil.showShort(mContext, "保存成功！");
            } else {
                /*JSONObject actionMap = new JSONObject();
                actionMap.put("operate", buttonModel.getButtonId());
                actionMap.put("operateText", buttonModel.getButtonName());
                actionMap.put("operateClazz", buttonModel.getButtonClass());
                actionMap.put("operateFlag", 1);
                actionMap.put("reOpen", false);*/
                Gson gson = new Gson();
                SpUtil.put(mContext, "actionDataTwo", gson.toJson(buttonModel));
                SpUtil.put(mContext, "actionData", gson.toJson(buttonModel));
                toExaminePhoto(false);
            } /*else if (buttonModel.getButtonId().contains("submit") || buttonModel.getButtonId().contains("rejectSubmit")) {
                if (imgBtnAdd.getVisibility() == View.VISIBLE) {
                    toExaminePhoto(false);
                } else {
                    jumpSelectPersonal(isEdit);
                }
            } else if (buttonModel.getButtonId().contains("getback")) {
                ToastUtil.showShort(mContext, "未知功能按钮");
            } else {
                ToastUtil.showShort(mContext, "未知按钮");
            }*/
        }
    }

    /**
     * 本地添加、本地详情点击事件
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
                        ConstantsUtil.isLoading = true;
                        ToDoDetailsActivity.this.finish();
                    }
                    break;
                // 保存并add
                case 2:
                    if (isFill()) {
                        saveLocation();
                        clearData();
                    }
                    break;
                // 提交审核
                case 3:
                    if (isFill()) {
                        toExaminePhoto(true);
                    }
                    break;
            }
        }
    }

    /**
     * 上传照片--->提交审核
     *
     * @param isStart 是否为新流程
     */
    private void toExaminePhoto(final boolean isStart) {
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

                        if (isStart) {
                            submitData("", "", "", "");
                        } else {
                            /*boolean isEdit = ConstantsUtil.buttonModel.getNextShowFlowInfoList() == null || ConstantsUtil.buttonModel.getNextShowFlowInfoList().size() == 0 ? false : ConstantsUtil.buttonModel.getNextShowFlowInfoList().get(0).isEdit();
                            jumpSelectPersonal(isEdit);*/
                            submitData();
                        }
                    }
                }
            });
            upLoadPhotosDialog.setCanceledOnTouchOutside(false);
            upLoadPhotosDialog.show();
        } else {
            if (isStart) {
                submitData("", "", "", "");
            } else {
                /*boolean isEdit = ConstantsUtil.buttonModel.getNextShowFlowInfoList() == null || ConstantsUtil.buttonModel.getNextShowFlowInfoList().size() == 0 ? false : ConstantsUtil.buttonModel.getNextShowFlowInfoList().get(0).isEdit();
                jumpSelectPersonal(isEdit);*/
                submitData();
            }
        }
    }

    /**
     * 跳转到选人界面
     *
     * @param isEdit
     */
    private void jumpSelectPersonal(boolean isEdit) {
        Intent intent = new Intent(mContext, PersonnelSelectionActivity.class);
        intent.putExtra("isEdit", isEdit);
        startActivityForResult(intent, selectPersonal);
    }

    /**
     * 提交、驳回
     */
    private void submitData() {
        String type = (String) SpUtil.get(mContext, ConstantsUtil.PROCESS_LIST_TYPE, "2");
        Map<String, Object> newObj = new HashMap<>();
        Map<String, Object> updateObj = new HashMap<>();
        if (StrUtil.equals(type, "2")) {
            newObj.put("apiName", "getZxHwZlTroubleDetails");
            updateObj.put("apiName", "updateZxHwZlTrouble");
            newObj.put("flowId", "zxHwZlTrouble");
            updateObj.put("flowId", "zxHwZlTrouble");
        } else {
            newObj.put("apiName", "getZxHwAqHiddenDangerDetails");
            updateObj.put("apiName", "updateZxHwAqHiddenDanger");
            newObj.put("flowId", "zxHwAqHiddenDanger");
            updateObj.put("flowId", "zxHwAqHiddenDanger");
        }

        newObj.put("apiType", "POST");
        updateObj.put("apiType", "POST");
        newObj.put("title", txtPressLocal.getText().toString() + "→" + edtHiddenTroubleHeadline.getText().toString());
        updateObj.put("title", txtPressLocal.getText().toString() + "→" + edtHiddenTroubleHeadline.getText().toString());


        Map<String, Object> apiBody = new HashMap<>();

        JSONArray jsonArr = new JSONArray(SpUtil.get(mContext, "uploadImgData", "[]"));
        if (StrUtil.equals(type, "2")) {
            apiBody.put("zlAttachmentList", jsonArr);
        } else {
            apiBody.put("aqAttachmentList", jsonArr);
        }

        apiBody.put("levelNameAll", txtPressLocal.getText().toString());
        apiBody.put("createTime", DateUtil.parse(txtEntryTime.getText().toString(), "yyyy-MM-dd").getTime());
        if (StrUtil.equals(flowId, "zxHwZlTrouble")) {
            apiBody.put("troubleTitle", edtHiddenTroubleHeadline.getText().toString());
            apiBody.put("troubleType", tType);
            if (StrUtil.equals(selectText, "一般")) {
                apiBody.put("troubleLevel", 1);
            } else if (StrUtil.equals(selectText, "严重")) {
                apiBody.put("troubleLevel", 2);
            } else {
                apiBody.put("troubleLevel", 3);
            }
            apiBody.put("troubleRequire", edtRectificationRequirements.getText().toString());
            apiBody.put("troubleContent", edtDangerDescription.getText().toString());
        } else {
            apiBody.put("dangerTitle", edtHiddenTroubleHeadline.getText().toString());
            apiBody.put("dangerType", tType);
            if (StrUtil.equals(selectText, "一般")) {
                apiBody.put("dangerLevel", 1);
            } else {
                apiBody.put("dangerLevel", 2);
            }
            apiBody.put("dangerRequire", edtRectificationRequirements.getText().toString());
            apiBody.put("dangerContent", edtDangerDescription.getText().toString());
        }

        apiBody.put("deadline", DateUtil.parse(btnChangeDate.getText().toString(), "yyyy-MM-dd").getTime());
        apiBody.put("levelId", selectLevelId);
        apiBody.put("levelIdAll", levelIdAll);

        newObj.put("apiBody", apiBody);
        updateObj.put("apiBody", apiBody);

        SpUtil.put(mContext, "startFlowData", new Gson().toJson(newObj));

        JSONObject object = new JSONObject(jsonData);
        JSONObject newObject = new JSONObject(object.getObj("data").toString());
        newObject.put("apiBody", new Gson().toJson(apiBody));
        SpUtil.put(mContext, "updateFlowData", new Gson().toJson(newObject));

        // 调用h5
        p = new H5PopupWindow(mContext, StrUtil.equals(workId, "details"), processId, deleteWorkingBean, ConstantsUtil.update/* + flowId + "/" + workId*/, promptListener);
        p.setTouchable(true);
        p.setFocusable(true); //设置点击menu以外其他地方以及返回键退出
        p.setOutsideTouchable(true);   //设置触摸外面时消失
        p.showAtDropDownRight(view);

        /*LoadingUtils.showLoading(mContext);
        JSONObject object = new JSONObject(jsonData);
        String newJsonData, url;
        url = ConstantsUtil.submitFlow;
        String type;
        if (StrUtil.equals("2", checkType)) {
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
                                if (isSubmit) {
                                    if (StrUtil.equals(workId, "details")) {
                                        DataSupport.deleteAll(PhotosBean.class, "processId=?", processId);
                                        if (deleteWorkingBean != null) {
                                            deleteWorkingBean.delete();
                                        }
                                    }
                                    clearData();
                                    SpUtil.remove(mContext, "uploadImgData");
                                    ConstantsUtil.isLoading = true;
                                    ToDoDetailsActivity.this.finish();
                                } else {
                                    ChildThreadUtil.toastMsgHidden(mContext, model.getMessage());
                                    ConstantsUtil.isLoading = true;
                                    SpUtil.remove(mContext, "uploadImgData");
                                    finish();
                                }
                            }
                        });
                    } else {
                        ChildThreadUtil.toastMsgHidden(mContext, getString(R.string.json_error));
                    }
                }
            }
        });*/
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
                urls.add(fileUrl);
            }
            Intent intent = new Intent(mContext, ShowPhotosActivity.class);
            intent.putExtra(ShowPhotosActivity.EXTRA_IMAGE_URLS, urls);
            intent.putExtra(ShowPhotosActivity.EXTRA_IMAGE_INDEX, Integer.valueOf(point));
            startActivity(intent);
        }
    };

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
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUrlName = System.currentTimeMillis() + ".png";
        Uri photoUri = FileProvider.getUriForFile(mContext, ProviderUtil.getFileProviderName(mContext), new File(strFilePath + fileUrlName));
        openCameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        startActivityForResult(openCameraIntent, takePhoto);
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
                // 选择工序位置
                case selectProcessPath:
                    //clearData();
                    // 是否选择相同层级
                    if (!StrUtil.equals(txtPressLocal.getText().toString(), data.getStringExtra("procedureName"))) {
                        uuid = RandomUtil.randomUUID().replaceAll("-", "");
                        photosList.clear();
                        photosAdapter.notifyDataSetChanged();
                    }
                    workId = "add";
                    txtPressLocal.setText(data.getStringExtra("procedureName"));
                    selectLevelId = data.getStringExtra("levelId");
                    levelIdAll = data.getStringExtra("levelIdAll");
                    break;
                // 拍照回调
                case takePhoto:
                    // 涂鸦参数
                    GraffitiParams params = new GraffitiParams();
                    // 图片路径
                    params.mImagePath = strFilePath + fileUrlName;
                    params.mSavePath = ConstantsUtil.SAVE_PATH + fileUrlName;
                    // 初始画笔大小
                    params.mPaintSize = 20;
                    // 启动涂鸦页面
                    GraffitiActivity.startActivityForResult(mContext, params, graffiti);
                    break;
                // 涂鸦
                case graffiti:
                    addPhotoBean = new PhotosBean();
                    addPhotoBean.setIsToBeUpLoad(1);
                    addPhotoBean.setUrl(ConstantsUtil.SAVE_PATH + fileUrlName);
                    addPhotoBean.setProcessId(StrUtil.equals(workId, "add") ? uuid : StrUtil.equals(workId, "details") ? processId : workId);
                    addPhotoBean.setThumbUrl(ConstantsUtil.SAVE_PATH + fileUrlName);
                    addPhotoBean.setPhotoName(fileUrlName);
                    addPhotoBean.setCheckFlag("-1");
                    addPhotoBean.setIsNewAdd(1);
                    addPhotoBean.setRoleFlag("1");
                    addPhotoBean.setUserId(userId);
                    addPhotoBean.setCreateTime(System.currentTimeMillis());
                    addPhotoBean.save();
                    photosList.add(addPhotoBean);
                    photosAdapter.notifyDataSetChanged();
                    // 删除拍摄的照片
                    FileUtil.deleteFile(strFilePath + fileUrlName);
                    break;
                // 选人
                case selectPersonal:
                    if ((StrUtil.equals(workId, "add") || StrUtil.equals(workId, "details")) && !isSubmit) {
                        submitData(data.getStringExtra("reviewNodeId"), data.getStringExtra("userId"), data.getStringExtra("userName"), data.getStringExtra("type"));
                    } else {
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
                    }
                    break;

                default:
                    break;
            }
        }
    }

    /**
     * 是否有新照片
     *
     * @return
     */
    private boolean isHaveNewPhoto() {
        // 检查是否有新拍摄的照片
        for (PhotosBean photo : photosList) {
            if (photo.getIsToBeUpLoad() == 1) {
                return true;
            }
        }
        return false;
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
                if (StrUtil.equals("add", workId)) {
                    if (!StrUtil.isEmpty(txtPressLocal.getText().toString()) || isHaveNewPhoto()) {
                        new PromptDialog(mContext, new PromptListener() {
                            @Override
                            public void returnTrueOrFalse(boolean trueOrFalse) {
                                if (trueOrFalse) {
                                    saveLocation();
                                    ToastUtil.showShort(mContext, "保存成功！");
                                    ConstantsUtil.isLoading = true;
                                    mContext.finish();
                                } else {
                                    DataSupport.deleteAll(PhotosBean.class, "processId=?", uuid);
                                    mContext.finish();
                                }
                            }
                        }, "提示", "您已添加数据是否保存至本地？", "否", "是").show();
                    } else {
                        mContext.finish();
                    }
                } else {
                    mContext.finish();
                }
                break;
            // 选择位置
            case R.id.btnChoice:
                Intent intent = new Intent(mContext, ContractorTreeActivity.class);
                startActivityForResult(intent, selectProcessPath);
                break;
            // 选择日期
            case R.id.btnChangeDate:
                DateUtils.onYearMonthDayPicker(mContext, btnChangeDate);
                break;
            // 拍照
            case R.id.imgBtnAdd:
                String sdCardSize = AppInfoUtil.getSDAvailableSize();
                if (Integer.valueOf(sdCardSize) < 10) {
                    ToastUtil.showShort(mContext, "当前手机内存卡已无可用空间，请清理后再进行拍照！");
                } else if (StrUtil.isEmpty(txtPressLocal.getText().toString())) {
                    ToastUtil.showShort(mContext, "请先选择工序位置！");
                } else {
                    checkPhotosPermission();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (p != null) {
            p.dismiss();
        }
        ScreenManagerUtil.popActivity(this);    // 退出当前activity
    }
}
