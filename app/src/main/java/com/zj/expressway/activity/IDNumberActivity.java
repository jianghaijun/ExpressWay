package com.zj.expressway.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.IDCardParams;
import com.baidu.ocr.sdk.model.IDCardResult;
import com.baidu.ocr.ui.camera.CameraActivity;
import com.baidu.speech.asr.SpeechConstant;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.yuyh.library.imgsel.ISNav;
import com.yuyh.library.imgsel.config.ISListConfig;
import com.zj.expressway.R;
import com.zj.expressway.application.MyApplication;
import com.zj.expressway.base.BaseActivity;
import com.zj.expressway.base.BaseModel;
import com.zj.expressway.bean.IDNumberBean;
import com.zj.expressway.bean.PhotosBean;
import com.zj.expressway.bean.PositionBean;
import com.zj.expressway.control.MyRecognizer;
import com.zj.expressway.dialog.SelectPhotoWayDialog;
import com.zj.expressway.dialog.UpLoadPhotosDialog;
import com.zj.expressway.listener.PermissionListener;
import com.zj.expressway.listener.PromptListener;
import com.zj.expressway.model.IDNumberModel;
import com.zj.expressway.recognization.ChainRecogListener;
import com.zj.expressway.ui.BaiduASRDigitalDialog;
import com.zj.expressway.ui.DigitalDialogInput;
import com.zj.expressway.utils.ChildThreadUtil;
import com.zj.expressway.utils.ConstantsUtil;
import com.zj.expressway.utils.DateUtils;
import com.zj.expressway.utils.JsonUtils;
import com.zj.expressway.utils.LoadingUtils;
import com.zj.expressway.utils.ScreenManagerUtil;
import com.zj.expressway.utils.SpUtil;
import com.zj.expressway.utils.ToastUtil;

import org.xutils.common.util.DensityUtil;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.qqtheme.framework.picker.OptionPicker;
import cn.qqtheme.framework.widget.WheelView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 轮播图编辑页
 */
public class IDNumberActivity extends BaseActivity {
    @ViewInject(R.id.imgBtnLeft)
    private ImageView imgBtnLeft;
    @ViewInject(R.id.txtTitle)
    private TextView txtTitle;
    @ViewInject(R.id.edtIdNumName)
    private EditText txtIdNumName;
    @ViewInject(R.id.edtSex)
    private EditText txtSex;
    @ViewInject(R.id.edtNation)
    private EditText txtNation;
    @ViewInject(R.id.edtAddress)
    private EditText txtAddress;
    @ViewInject(R.id.edtDateOfBirth)
    private EditText txtDateOfBirth;
    @ViewInject(R.id.edtIdentityNumber)
    private EditText txtIdentityNumber;
    @ViewInject(R.id.imgIdNum)
    private ImageView imgIdNum;
    @ViewInject(R.id.ibVoice)
    private ImageView ibVoice;
    @ViewInject(R.id.txtEntryDate)
    private Button txtEntryDate;
    @ViewInject(R.id.txtTrainingTime)
    private Button txtTrainingTime;
    @ViewInject(R.id.txtExitTime)
    private Button txtExitTime;
    @ViewInject(R.id.txtPost)
    private TextView txtPost;
    @ViewInject(R.id.txtContract)
    private TextView txtContract;
    @ViewInject(R.id.btnQuery)
    private Button btnQuery;

    private String qrCodeId, positionId, projectId;
    private Activity mContext;
    private boolean isSelect = false;
    private List<PhotosBean> photosBeanList = new ArrayList<>();
    private List<PositionBean> positionList;
    private String filePath;
    private MyRecognizer myRecognizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_id_number);

        x.view().inject(this);
        mContext = this;
        ScreenManagerUtil.pushActivity(this);

        imgBtnLeft.setVisibility(View.VISIBLE);
        imgBtnLeft.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.back_btn));
        txtTitle.setText("身份信息");

        getData(getIntent().getStringExtra("url"));
    }

    /**
     * 获取数据
     *
     * @param qrCodeId
     */
    private void getData(String qrCodeId) {
        LoadingUtils.showLoading(mContext);
        qrCodeId = qrCodeId.substring(qrCodeId.lastIndexOf("/") + 1);
        this.qrCodeId = qrCodeId;
        JSONObject obj = new JSONObject();
        obj.put("qrcodeId", qrCodeId);
        Request request = ChildThreadUtil.getRequest(mContext, ConstantsUtil.scanGetWorkerDetails, obj.toString());
        ConstantsUtil.okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                ChildThreadUtil.toastMsgHidden(mContext, getString(R.string.server_exception));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String data = response.body().string();
                if (JsonUtils.isGoodJson(data)) {
                    Gson gson = new Gson();
                    LoadingUtils.hideLoading();
                    final IDNumberModel model = gson.fromJson(data, IDNumberModel.class);
                    if (model.isSuccess()) {
                        if (model.getData() != null && StrUtil.isEmpty(model.getData().getWorkerId())) {
                            // 新增
                            positionList = model.getData().getPositionList();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    selectFile();
                                }
                            });
                        } else {
                            // 详情
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    IDNumberBean idNumberBean = model.getData();
                                    txtIdNumName.setText(idNumberBean.getWorkerName());
                                    txtIdNumName.setFocusable(false);
                                    txtSex.setText(StrUtil.equals(idNumberBean.getGender(), "0") ? "男" : "女");
                                    txtSex.setFocusable(false);
                                    txtAddress.setText(idNumberBean.getNativePlace());
                                    txtAddress.setFocusable(false);
                                    txtIdentityNumber.setText(idNumberBean.getIdentity());
                                    txtIdentityNumber.setFocusable(false);
                                    // 籍贯语音按钮
                                    ibVoice.setVisibility(View.GONE);
                                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                                    lp.setMargins(DensityUtil.dip2px(100), DensityUtil.dip2px((float) 2.5), DensityUtil.dip2px(10), DensityUtil.dip2px((float) 2.5));
                                    txtAddress.setLayoutParams(lp);
                                    // 进场时间
                                    txtEntryDate.setCompoundDrawables(null, null, null, null);
                                    txtEntryDate.setClickable(false);
                                    txtEntryDate.setText(DateUtils.setDataToStr2(idNumberBean.getEnterTime()));
                                    // 岗前培训时间
                                    txtTrainingTime.setCompoundDrawables(null, null, null, null);
                                    txtTrainingTime.setClickable(false);
                                    txtTrainingTime.setText(DateUtils.setDataToStr2(idNumberBean.getTrainingTime()));
                                    // 退场时间
                                    txtExitTime.setCompoundDrawables(null, null, null, null);
                                    txtExitTime.setClickable(false);
                                    txtExitTime.setText(DateUtils.setDataToStr2(idNumberBean.getExitTime()));
                                    // 工种
                                    txtPost.setCompoundDrawables(null, null, null, null);
                                    txtPost.setClickable(false);
                                    String value = "";
                                    for (PositionBean bean : idNumberBean.getPositionList()) {
                                        if (StrUtil.equals(idNumberBean.getPositionId(), bean.getPositionId())) {
                                            value = bean.getPositionName();
                                            break;
                                        }
                                    }
                                    txtPost.setText(value);

                                    Glide.with(mContext).load(idNumberBean.getAttachmentList() != null && idNumberBean.getAttachmentList().size() > 0 ? idNumberBean.getAttachmentList().get(0).getUrl() : "").into(imgIdNum);
                                    imgIdNum.setClickable(false);
                                    btnQuery.setVisibility(View.GONE);
                                }
                            });
                        }
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
     * 申请读取内存卡权限
     */
    private void selectFile() {
        if (Build.VERSION.SDK_INT >= 23) {
            requestAuthority(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE}, new PermissionListener() {
                @Override
                public void agree() {
                    takePhoto();
                }

                @Override
                public void refuse(List<String> refusePermission) {
                    ToastUtil.showShort(IDNumberActivity.this, "您拒绝了读取内存卡权限!");
                }
            });
        } else {
            takePhoto();
        }
    }

    /**
     * 拍照
     */
    private void takePhoto() {
        Intent intent = new Intent(mContext, CameraActivity.class);
        filePath = ConstantsUtil.SAVE_PATH + System.currentTimeMillis() + ".png";
        intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH, filePath);
        intent.putExtra(CameraActivity.KEY_CONTENT_TYPE, CameraActivity.CONTENT_TYPE_ID_CARD_FRONT);
        startActivityForResult(intent, 1001);
    }

    /**
     * 选择照片方式
     */
    private void showPhoto() {
        SelectPhotoWayDialog selectPhotoWayDialog = new SelectPhotoWayDialog(mContext, new PromptListener() {
            @Override
            public void returnTrueOrFalse(boolean trueOrFalse) {
                if (trueOrFalse) {
                    // 拍照
                    /*ISCameraConfig config = new ISCameraConfig.Builder()
                            .needCrop(true) // 裁剪
                            .cropSize(1, 1, 1200, 1200)
                            .build();
                    ISNav.getInstance().toCameraActivity(mContext, config, 1001);*/
                    Intent intent = new Intent(mContext, CameraActivity.class);
                    filePath = ConstantsUtil.SAVE_PATH + System.currentTimeMillis() + ".png";
                    intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH, filePath);
                    intent.putExtra(CameraActivity.KEY_CONTENT_TYPE, CameraActivity.CONTENT_TYPE_ID_CARD_FRONT);
                    startActivityForResult(intent, 1001);
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
                    ISNav.getInstance().toListActivity(mContext, config, 1002);
                }
            }
        });
        selectPhotoWayDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1001) {
                LoadingUtils.showLoading(mContext);
                /*String path = data.getStringExtra("result"); // 图片地址
                if (!TextUtils.isEmpty(path)) {
                    Glide.with(mContext).load(path).into(imgIdNum);
                    recIDCard(IDCardParams.ID_CARD_SIDE_FRONT, path);
                }*/
                if (data != null) {
                    Glide.with(mContext).load(filePath).into(imgIdNum);
                    recIDCard(IDCardParams.ID_CARD_SIDE_FRONT, filePath);
                }
            } else if (requestCode == 1002) {
                LoadingUtils.showLoading(mContext);
                List<String> pathList = data.getStringArrayListExtra("result");
                if (pathList != null && pathList.size() > 0) {
                    Glide.with(mContext).load(pathList.get(0)).into(imgIdNum);
                    recIDCard(IDCardParams.ID_CARD_SIDE_FRONT, pathList.get(0));
                }
                // 语音识别回调
            } else if (requestCode == 2) {
                if (myRecognizer != null) {
                    myRecognizer.release();
                }
                String message = txtAddress.getText().toString();
                ArrayList results = data.getStringArrayListExtra("results");
                if (results != null && results.size() > 0) {
                    message += results.get(0);
                }
                txtAddress.setText(message);
            }
        } else {
            if (myRecognizer != null) {
                myRecognizer.release();
            }
        }
    }

    /**
     * 解析身份证图片
     *
     * @param idCardSide 身份证正反面
     * @param filePath   图片路径
     */
    private void recIDCard(String idCardSide, String filePath) {
        PhotosBean photosBean = new PhotosBean();
        photosBean.setPhotoName(filePath.substring(filePath.lastIndexOf("/") + 1));
        photosBean.setUrl(filePath);
        photosBeanList.add(photosBean);
        IDCardParams param = new IDCardParams();
        param.setImageFile(new File(filePath));
        // 设置身份证正反面
        param.setIdCardSide(idCardSide);
        // 设置方向检测
        param.setDetectDirection(true);
        // 设置图像参数压缩质量0-100, 越大图像质量越好但是请求时间越长。 不设置则默认值为20
        param.setImageQuality(40);
        OCR.getInstance(this).recognizeIDCard(param, new OnResultListener<IDCardResult>() {
            @Override
            public void onResult(IDCardResult result) {
                if (result != null) {
                    String name = "";
                    String sex = "";
                    String nation = "";
                    String num = "";
                    String address = "";
                    if (result.getName() != null) {
                        name = result.getName().toString();
                        txtIdNumName.setText(name);
                    }
                    if (result.getGender() != null) {
                        sex = result.getGender().toString();
                        txtSex.setText(sex);
                    }
                    if (result.getEthnic() != null) {
                        nation = result.getEthnic().toString();
                        txtNation.setText(nation);
                    }
                    if (result.getIdNumber() != null) {
                        num = result.getIdNumber().toString();
                        txtIdentityNumber.setText(num);
                    }
                    if (result.getAddress() != null) {
                        address = result.getAddress().toString();
                        txtAddress.setText(address);
                    }

                    if (result.getBirthday() != null) {
                        String birth = result.getBirthday().toString();
                        txtDateOfBirth.setText(birth.substring(0, 4) + "年" + birth.substring(4, 6) + "月" + birth.substring(6) + "日");
                    }
                    isSelect = true;
                    LoadingUtils.hideLoading();
                }
            }

            @Override
            public void onError(OCRError error) {
                LoadingUtils.hideLoading();
                ToastUtil.showShort(mContext, "识别出错");
            }
        });
    }

    /**
     * 提交
     */
    private void submitData() {
        // 提交照片
        new UpLoadPhotosDialog(mContext, 3, photosBeanList, new PromptListener() {
            @Override
            public void returnTrueOrFalse(boolean trueOrFalse) {
                if (trueOrFalse) {
                    JSONObject map = new JSONObject();
                    map.put("workerName", txtIdNumName.getText().toString());
                    map.put("gender", StrUtil.equals(txtSex.getText().toString(), "男") ? "0" : "1");
                    map.put("nativePlace", txtAddress.getText().toString());
                    map.put("identity", txtIdentityNumber.getText().toString());
                    map.put("enterTime", StrUtil.isEmpty(txtEntryDate.getText().toString()) ? null : DateUtil.parse(txtExitTime.getText().toString(), "yyyy-MM-dd").getTime());
                    map.put("trainingTime", StrUtil.isEmpty(txtExitTime.getText().toString()) ? null : DateUtil.parse(txtTrainingTime.getText().toString(), "yyyy-MM-dd").getTime());
                    map.put("exitTime", StrUtil.isEmpty(txtExitTime.getText().toString()) ? null : DateUtil.parse(txtTrainingTime.getText().toString(), "yyyy-MM-dd").getTime());
                    map.put("positionId", positionId);
                    map.put("projectId", projectId);
                    map.put("qrcodeId", qrCodeId);
                    JSONArray jsonArr = new JSONArray(SpUtil.get(mContext, "uploadImgData", "[]"));
                    map.put("attachmentList", jsonArr);
                    submit(map);
                }
            }
        }).show();
    }

    /**
     * 提交数据
     *
     * @param map
     */
    private void submit(JSONObject map) {
        LoadingUtils.showLoading(mContext);
        Request request = ChildThreadUtil.getRequest(mContext, ConstantsUtil.appAddContractWorker, map.toString());
        ConstantsUtil.okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                ChildThreadUtil.toastMsgHidden(mContext, getString(R.string.server_exception));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String data = response.body().string();
                if (JsonUtils.isGoodJson(data)) {
                    Gson gson = new Gson();
                    final BaseModel model = gson.fromJson(data, BaseModel.class);
                    if (model.isSuccess()) {
                        ChildThreadUtil.toastMsgHidden(mContext, model.getMessage());
                        IDNumberActivity.this.finish();
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
     * 展示选择框
     */
    private void showSelectDialog(List<PositionBean> positionList, final int dialogType) {
        int size = positionList == null ? 0 : positionList.size();
        final String[] strList = new String[size];
        final String[] idList = new String[size];
        for (int i = 0; i < size; i++) {
            strList[i] = positionList.get(i).getPositionName();
            idList[i] = positionList.get(i).getPositionId();
        }
        OptionPicker picker = new OptionPicker(this, strList);
        picker.setCanceledOnTouchOutside(true);
        picker.setDividerRatio(WheelView.DividerConfig.FILL);
        picker.setShadowColor(ContextCompat.getColor(mContext, R.color.main_bg), 80);
        picker.setSelectedIndex(0);
        picker.setCycleDisable(true);
        picker.setTextSize(18);
        picker.setOnOptionPickListener(new OptionPicker.OnOptionPickListener() {
            @Override
            public void onOptionPicked(int index, String item) {
                switch (dialogType) {
                    case 1:
                        positionId = idList[index];
                        txtPost.setText(strList[index]);
                        break;
                    case 2:
                        projectId = idList[index];
                        txtContract.setText(strList[index]);
                        break;
                }
            }
        });
        picker.show();
    }

    /**
     * 申请语音输入权限
     */
    private void initVoicePermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            String permissions[] = {android.Manifest.permission.RECORD_AUDIO,
                    android.Manifest.permission.READ_PHONE_STATE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            };
            requestAuthority(permissions, new PermissionListener() {
                @Override
                public void agree() {
                    speechInput();
                }

                @Override
                public void refuse(List<String> refusePermission) {
                    ToastUtil.showShort(IDNumberActivity.this, "您拒绝了语音输入所需权限权限!");
                }
            });
        } else {
            speechInput();
        }
    }

    /**
     * 语音输入
     */
    private void speechInput() {
        // 1. 确定识别参数
        Map<String, Object> params = new LinkedHashMap<>();
        params.put(SpeechConstant.ACCEPT_AUDIO_VOLUME, false);
        params.put(SpeechConstant.VAD_ENDPOINT_TIMEOUT, 0); // 长语音
        ChainRecogListener listener = new ChainRecogListener();
        myRecognizer = new MyRecognizer(mContext, listener); // DigitalDialogInput 输入
        DigitalDialogInput input = new DigitalDialogInput(myRecognizer, listener, params);
        Intent intent = new Intent(mContext, BaiduASRDigitalDialog.class);
        ((MyApplication) mContext.getApplicationContext()).setDigitalDialogInput(input);
        mContext.startActivityForResult(intent, 2);
    }

    @Event({R.id.ibVoice, R.id.imgBtnLeft, R.id.imgIdNum, R.id.btnQuery, R.id.txtEntryDate, R.id.txtTrainingTime, R.id.txtExitTime, R.id.txtPost})
    private void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgBtnLeft:
                this.finish();
                break;
            case R.id.imgIdNum:
                selectFile();
                break;
            // 进场时间
            case R.id.txtEntryDate:
                DateUtils.onYearMonthDayPicker(mContext, txtEntryDate);
                break;
            // 岗前培训时间
            case R.id.txtTrainingTime:
                DateUtils.onYearMonthDayPicker(mContext, txtTrainingTime);
                break;
            // 退场时间
            case R.id.txtExitTime:
                DateUtils.onYearMonthDayPicker(mContext, txtExitTime);
                break;
            // 职务、工种
            case R.id.txtPost:
                if (positionList == null || positionList.size() == 0) {
                    ToastUtil.showShort(mContext, "暂无工种！");
                } else {
                    showSelectDialog(positionList, 1);
                }
                break;
            // 语音输入
            case R.id.ibVoice:
                initVoicePermissions();
                break;
            // 提交
            case R.id.btnQuery:
                if (!isSelect) {
                    ToastUtil.showShort(mContext, "请先选择身份证正面照！");
                } else if (StrUtil.isEmpty(txtIdNumName.getText().toString())) {
                    ToastUtil.showShort(mContext, "请填写姓名！");
                } else if (StrUtil.isEmpty(txtSex.getText().toString())) {
                    ToastUtil.showShort(mContext, "请填写性别！");
                } else if (StrUtil.isEmpty(txtAddress.getText().toString())) {
                    ToastUtil.showShort(mContext, "请填写籍贯！");
                } else if (StrUtil.isEmpty(txtIdentityNumber.getText().toString())) {
                    ToastUtil.showShort(mContext, "请填写身份号码！");
                } else {
                    submitData();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ScreenManagerUtil.popActivity(this);
    }
}
