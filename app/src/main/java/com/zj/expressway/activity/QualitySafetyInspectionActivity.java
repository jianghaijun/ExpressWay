package com.zj.expressway.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.SensorManager;
import android.support.annotation.IdRes;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.zj.expressway.R;
import com.zj.expressway.adapter.PhotosListAdapter;
import com.zj.expressway.base.BaseActivity;
import com.zj.expressway.base.BaseModel;
import com.zj.expressway.bean.PhotosBean;
import com.zj.expressway.bean.WorkingBean;
import com.zj.expressway.dialog.HorizontalScreenHintDialog;
import com.zj.expressway.dialog.PromptDialog;
import com.zj.expressway.dialog.UpLoadPhotosDialog;
import com.zj.expressway.listener.PromptListener;
import com.zj.expressway.listener.ShowPhotoListener;
import com.zj.expressway.model.WorkModel;
import com.zj.expressway.utils.AppInfoUtil;
import com.zj.expressway.utils.ChildThreadUtil;
import com.zj.expressway.utils.ConstantsUtil;
import com.zj.expressway.utils.JsonUtils;
import com.zj.expressway.utils.LoadingUtils;
import com.zj.expressway.utils.SpUtil;
import com.zj.expressway.utils.ToastUtil;

import org.json.JSONException;
import org.litepal.crud.DataSupport;
import org.xutils.common.util.DensityUtil;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.qqtheme.framework.picker.DatePicker;
import cn.qqtheme.framework.util.ConvertUtils;
import okhttp3.Call;
import okhttp3.Callback;
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
 *       Created by HaiJun on 2018/6/11 16:43
 */
public class QualitySafetyInspectionActivity extends BaseActivity {
    private QualitySafetyHolder holder;
    private Activity mContext;
    // 屏幕方向监听
    private AlbumOrientationEventListener mAlbumOrientationEventListener;
    private int mOrientation = 0;
    private boolean isHorizontalScreen = false;
    private List<PhotosBean> localPhoneList;
    private PhotosListAdapter photosAdapter;
    private String selectText = "";
    private String type, levelId;

    private View view;

    public QualitySafetyInspectionActivity(Activity mContext, View layout) {
        this.mContext = mContext;
        this.view = layout;
        holder = new QualitySafetyHolder();
        x.view().inject(holder, layout);

        // 屏幕方向监听
        mAlbumOrientationEventListener = new AlbumOrientationEventListener(mContext, SensorManager.SENSOR_DELAY_NORMAL);
        if (mAlbumOrientationEventListener.canDetectOrientation()) {
            mAlbumOrientationEventListener.enable();
        }
    }

    /**
     * 提交审核
     * @param reviewNodeId
     * @param userId
     * @param userName
     * @param userType
     * @param promptListener
     */
    public void submitData(String reviewNodeId, String userId, String userName, String userType, PromptListener promptListener) {
        Map<String, Object> object = new HashMap<>();
        Map<String, Object> tableDataMap = new HashMap<>();
        tableDataMap.put("levelNameAll", holder.txtPressLocal.getText().toString());
        tableDataMap.put("createTime", System.currentTimeMillis());
        if (type.equals("1")) {
            tableDataMap.put("troubleTitle", holder.edtHiddenTroubleHeadline.getText().toString());
        } else {
            tableDataMap.put("dangerTitle", holder.edtHiddenTroubleHeadline.getText().toString());
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
                level = 3;
                break;
        }

        if (type.equals("1")) {
            tableDataMap.put("troubleLevel", level);
        } else {
            tableDataMap.put("dangerLevel", level);
        }

        tableDataMap.put("deadline", DateUtil.parse(holder.btnChangeDate.getText().toString()).getTime());
        if (type.equals("1")) {
            tableDataMap.put("troubleRequire", holder.edtRectificationRequirements.getText().toString());
        } else {
            tableDataMap.put("dangerRequire", holder.edtRectificationRequirements.getText().toString());
        }

        object.put("mainTableDataObject", tableDataMap);
        object.put("reviewNodeId", reviewNodeId);

        Map<String, Object> jsonObject = new HashMap<>();
        jsonObject.put("value",  userId);
        jsonObject.put("label",  userName);
        jsonObject.put("type",  userType);
        List<Map<String, Object>> jsonArray = new ArrayList<>();
        jsonArray.add(jsonObject);
        object.put("reviewUserObjectList", jsonArray);
        object.put("mainTablePrimaryIdName", "dangerId");
        object.put("mainTableName", "fileId");

        if (type.equals("1")) {
            object.put("mainTableName", "zxHwZlHiddenDanger");
            object.put("flowId", "zxHwZlHiddenDanger");
        } else {
            object.put("mainTableName", "zxHwAqHiddenDanger");
            object.put("flowId", "zxHwAqHiddenDanger");
        }

        object.put("mainTablePrimaryId", "");

        JSONArray jsonArr = new JSONArray(SpUtil.get(mContext, "uploadImgData", "[]"));
        Map<String, Object> b = new HashMap<>();
        Map<String, Object> o = new HashMap<>();
        o.put("subTableType", "2");
        o.put("subTablePrimaryIdName", "uid");
        o.put("subTableDataObject", jsonArr);

        if (type.equals("1")) {
            b.put("zxHwZlAttachment", o);
        } else {
            b.put("zxHwAqAttachment", o);
        }

        object.put("subTableObject", b);
        org.json.JSONObject data = new org.json.JSONObject(object);
        submitData(data.toString(), promptListener);
    }

    /**
     * 提交、驳回
     */
    private void submitData(String obj, final PromptListener promptListener) {
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
                String jsonData = response.body().string().toString();
                LoadingUtils.hideLoading();
                if (JsonUtils.isGoodJson(jsonData)) {
                    Gson gson = new Gson();
                    BaseModel model = gson.fromJson(jsonData, BaseModel.class);
                    if (model.isSuccess()) {
                        ChildThreadUtil.toastMsgHidden(mContext, model.getMessage());
                        mContext.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                holder.txtPressLocal.setText("");
                                holder.txtPressLocal.setFocusable(true);
                                holder.edtHiddenTroubleHeadline.setText("");
                                holder.btnChangeDate.setText("");
                                holder.edtRectificationRequirements.setText("");
                                localPhoneList.clear();
                                photosAdapter = new PhotosListAdapter(mContext, localPhoneList, clickPhotoListener, "", "1");
                                LinearLayoutManager ms = new LinearLayoutManager(mContext);
                                ms.setOrientation(LinearLayoutManager.HORIZONTAL);
                                holder.rvContractorDetails.setLayoutManager(ms);
                                holder.rvContractorDetails.setAdapter(photosAdapter);
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
     * 赋值
     */
    public void initData(String type) {
        this.type = type;
        holder.btnChoice.setOnClickListener(new onClick(1));
        holder.btnChangeDate.setOnClickListener(new onClick(2));
        holder.imgBtnAdd.setOnClickListener(new onClick(3));
        holder.txtEntryTime.setText(DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        lp.setMargins(DensityUtil.dip2px(5), 0, DensityUtil.dip2px(5), 0);
        lp.weight = 1;
        Button button = new Button(mContext);
        button.setText("提交审核");
        button.setTextSize(14);
        button.setTextColor(ContextCompat.getColor(mContext, R.color.white));
        button.setBackground(ContextCompat.getDrawable(mContext, R.drawable.btn_blue));
        button.setOnClickListener(new onClick(4));
        holder.llButtons.addView(button, lp);

        holder.rgLevel.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                RadioButton radioButton = (RadioButton) view.findViewById(checkedId);
                selectText = radioButton.getText().toString();
            }
        });
    }

    /**
     * 更新适配器
     * @param levelId
     */
    public void updateFileList(String levelId) {
        this.levelId = levelId;
        // 查询本地保存照片
        localPhoneList = DataSupport.where("isToBeUpLoad = 1 AND userId = ? AND processId = ? order by createTime desc", (String) SpUtil.get(mContext, ConstantsUtil.USER_ID, ""), StrUtil.isEmpty(levelId) ? "" : levelId).find(PhotosBean.class);
        photosAdapter = new PhotosListAdapter(mContext, localPhoneList, clickPhotoListener, "", "1");
        LinearLayoutManager ms = new LinearLayoutManager(mContext);
        ms.setOrientation(LinearLayoutManager.HORIZONTAL);
        holder.rvContractorDetails.setLayoutManager(ms);
        holder.rvContractorDetails.setAdapter(photosAdapter);
    }

    /**
     * 图片点击事件监听--->全屏预览图片
     */
    private ShowPhotoListener clickPhotoListener = new ShowPhotoListener() {
        @Override
        public void selectWayOrShowPhoto(boolean isShowPhoto, String point, String photoUrl, int isUpLoad) {
            // 图片浏览
            ArrayList<String> urls = new ArrayList<>();
            int len = localPhoneList.size();
            for (int i = 0; i < len; i++) {
                String fileUrl = localPhoneList.get(i).getUrl();
                /*if (!TextUtils.isEmpty(fileUrl) && !fileUrl.contains(ConstantsUtil.SAVE_PATH)) {
                    fileUrl = ConstantsUtil.BASE_URL + ConstantsUtil.prefix + fileUrl;
                }*/
                urls.add(fileUrl);
            }
            Intent intent = new Intent(mContext, ShowPhotosActivity.class);
            intent.putExtra(ShowPhotosActivity.EXTRA_IMAGE_URLS, urls);
            intent.putExtra(ShowPhotosActivity.EXTRA_IMAGE_INDEX, Integer.valueOf(point));
            mContext.startActivity(intent);
        }
    };

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
                case 1:
                    Intent intent = new Intent(mContext, ContractorTreeActivity.class);
                    if (type.equals("1")) {
                        intent.putExtra("type", "2");
                    } else {
                        intent.putExtra("type", "3");
                    }
                    mContext.startActivityForResult(intent, 110);
                    break;
                case 2:
                    onYearMonthDayPicker();
                    break;
                case 3:
                    String sdCardSize = AppInfoUtil.getSDAvailableSize();
                    if (Integer.valueOf(sdCardSize) < 10) {
                        ToastUtil.showShort(mContext, "当前手机内存卡已无可用空间，请清理后再进行拍照！");
                    } else if (StrUtil.isEmpty(holder.txtPressLocal.getText().toString())) {
                        ToastUtil.showShort(mContext, "请先选择工序再进行拍照！");
                    } else {
                        /*String sLocation = holder.txtLocation.getText().toString().trim();
                        if (sLocation.length() < 7 || sLocation.contains("正在定位")) {
                            PromptDialog promptDialog = new PromptDialog(mContext, new PromptListener() {
                                @Override
                                public void returnTrueOrFalse(boolean trueOrFalse) {
                                    if (trueOrFalse) {
                                        takePictures();
                                    }
                                }
                            }, "提示", "未定位到当前位置，拍照后会导致拍摄照片无地理位置信息。是否继续拍照？", "否", "是");
                            promptDialog.show();
                        } else {
                            takePictures();
                        }*/
                        takePictures();
                    }
                    break;
                case 4:
                    if (StrUtil.isEmpty(holder.txtPressLocal.getText().toString())) {
                        ToastUtil.showShort(mContext, "请先选择工序再进行拍照！");
                    } else if (StrUtil.isEmpty(selectText)) {
                        ToastUtil.showShort(mContext, "请选择隐患级别！");
                    } else if (StrUtil.isEmpty(holder.btnChangeDate.getText().toString())) {
                        ToastUtil.showShort(mContext, "请选择整改期限！");
                    } else if (localPhoneList.size() == 0) {
                        ToastUtil.showShort(mContext, "请先拍照！");
                    } else {
                        toExaminePhoto();
                    }
                    break;
            }
        }
    }

    /**
     * 上传照片--->提交审核
     */
    private void toExaminePhoto() {
        // 上传
        UpLoadPhotosDialog upLoadPhotosDialog = new UpLoadPhotosDialog(mContext, 3, localPhoneList, new PromptListener() {
            @Override
            public void returnTrueOrFalse(boolean trueOrFalse) {
                if (trueOrFalse) {
                    // 修改已上传照片状态
                    for (PhotosBean phone : localPhoneList) {
                        phone.setIsToBeUpLoad(-1); // 已上传
                    }
                    // 更新适配器
                    if (null != photosAdapter) {
                        photosAdapter.notifyDataSetChanged();
                    }

                    Intent intent = new Intent(mContext, PersonnelSelectionActivity.class);
                    mContext.startActivityForResult(intent, 201);
                }
            }
        });
        upLoadPhotosDialog.setCanceledOnTouchOutside(false);
        upLoadPhotosDialog.show();
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
        String date = holder.btnChangeDate.getText().toString();
        Date time = StrUtil.isEmpty(date) ? new Date() : DateUtil.parse(date);
        picker.setSelectedItem(DateUtil.year(time), DateUtil.month(time) + 1, DateUtil.dayOfMonth(time));
        picker.setResetWhileWheel(false);
        picker.setOnDatePickListener(new DatePicker.OnYearMonthDayPickListener() {
            @Override
            public void onDatePicked(String year, String month, String day) {
                holder.btnChangeDate.setText(year + "-" + month + "-" + day);
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
     * 拍照
     */
    private void takePictures() {
        if (!isHorizontalScreen) {
            HorizontalScreenHintDialog screenHintDialog = new HorizontalScreenHintDialog(mContext, true);
            screenHintDialog.show();
        } else {
            Intent intent = new Intent();
            intent.setClass(mContext, PhotographActivity.class);
            mContext.startActivityForResult(intent, 1);
        }
    }

    /**
     * 容纳器
     */
    private class QualitySafetyHolder {
        @ViewInject(R.id.btnChoice)
        private Button btnChoice;
        @ViewInject(R.id.txtPressLocal)
        private TextView txtPressLocal;
        @ViewInject(R.id.txtEntryTime)
        private TextView txtEntryTime;
        @ViewInject(R.id.txtLocation)
        private TextView txtLocation;
        @ViewInject(R.id.edtHiddenTroubleHeadline)
        private EditText edtHiddenTroubleHeadline;
        @ViewInject(R.id.rgLevel)
        private RadioGroup rgLevel;
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
    }
}
