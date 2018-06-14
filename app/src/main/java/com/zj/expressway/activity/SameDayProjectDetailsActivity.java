package com.zj.expressway.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.gson.Gson;
import com.zj.expressway.R;
import com.zj.expressway.base.BaseActivity;
import com.zj.expressway.bean.PhotosBean;
import com.zj.expressway.bean.SameDayBean;
import com.zj.expressway.model.SameDayListModel;
import com.zj.expressway.utils.ConstantsUtil;
import com.zj.expressway.utils.JsonUtils;
import com.zj.expressway.utils.LoadingUtils;
import com.zj.expressway.utils.ScreenManagerUtil;
import com.zj.expressway.utils.SpUtil;
import com.zj.expressway.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.util.DensityUtil;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.hutool.core.date.DateUtil;
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
 *       Created by HaiJun on 2018/6/11 16:51
 *       工序报表详情
 */
public class SameDayProjectDetailsActivity extends BaseActivity {
    @ViewInject(R.id.txtTitle)
    private TextView txtTitle;
    @ViewInject(R.id.imgBtnLeft)
    private ImageButton imgBtnLeft;
    @ViewInject(R.id.txtSameDayProjectDetail)
    private TextView txtSameDayProjectDetail;
    @ViewInject(R.id.tbSameDayProjectDetail)
    private TableLayout tbSameDayProjectDetail;

    @ViewInject(R.id.rlPhotos)
    private RelativeLayout rlPhotos;
    @ViewInject(R.id.txtSameDayPhotos)
    private TextView txtSameDayPhotos;
    @ViewInject(R.id.tbSameDayPhotos)
    private TableLayout tbSameDayPhotos;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_same_day_project_details);
        x.view().inject(this);
        ScreenManagerUtil.pushActivity(this);

        mContext = this;

        txtTitle.setText("工序报表");
        imgBtnLeft.setVisibility(View.VISIBLE);
        imgBtnLeft.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.back_btn));

        txtSameDayProjectDetail.setText(getIntent().getStringExtra("rootLevelName") + "完成工序");

        getSameDayData();
    }

    /**
     * 获取数据
     */
    private void getSameDayData() {
        LoadingUtils.showLoading(mContext);
        JSONObject obj = new JSONObject();
        try {
            obj.put("rootLevelId", getIntent().getStringExtra("rootLevelId"));
            obj.put("startDate", getIntent().getLongExtra("beganDate", System.currentTimeMillis()));
            obj.put("endDate", getIntent().getLongExtra("endDate", DateUtil.tomorrow().getTime()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(ConstantsUtil.JSON, obj.toString());
        Request request = new Request.Builder()
                .url(ConstantsUtil.BASE_URL + ConstantsUtil.PROCESS_PROCESS_REPORT_TODAY)
                .addHeader("token", (String) SpUtil.get(mContext, ConstantsUtil.TOKEN, ""))
                .post(requestBody)
                .build();
        ConstantsUtil.okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LoadingUtils.hideLoading();
                runChildrenThread(getString(R.string.server_exception));
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
                            Gson gson = new Gson();
                            final SameDayListModel model = gson.fromJson(jsonData, SameDayListModel.class);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (model.getData() != null && model.getData().size() > 0) {
                                        initSameDayProjectDetails(model.getData());
                                    } else {
                                        initSameDayProjectDetails(new ArrayList<SameDayBean>());
                                    }
                                    LoadingUtils.hideLoading();
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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LoadingUtils.hideLoading();
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
     * @param beanList
     */
    private void initSameDayProjectDetails(List<SameDayBean> beanList) {
        List<SameDayBean> sList = new ArrayList<>();
        SameDayBean bean = new SameDayBean();
        bean.setTwoLevelName("");
        bean.setTotalNumBy("报验");
        bean.setTotalNumZj("自检");
        bean.setTotalNumCj("监理抽检");
        bean.setOperation("操作");
        sList.add(bean);

        for (SameDayBean b : beanList) {
            b.setOperation("工序详情");
            sList.add(b);
        }

        // 动态添加行
        int size = sList.size();
        DisplayMetrics dm = new DisplayMetrics();
        //取得窗口属性
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        //窗口的宽度
        int screenWidth = dm.widthPixels;
        int width = (screenWidth - DensityUtil.dip2px(102)) / 4 - DensityUtil.dip2px(1);
        int height = DensityUtil.dip2px(30);
        for (int i = 0; i < size; i++) {
            final SameDayBean sameDayBean = sList.get(i);
            TableRow tableRow = new TableRow(this);
            // 工程名称
            TextView projectName = new TextView(this);
            projectName.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
            projectName.setText(sameDayBean.getTwoLevelName());
            projectName.setTextSize(12);
            projectName.setGravity(Gravity.CENTER);
            projectName.setTextColor(ContextCompat.getColor(this, R.color.black));
            TableRow.LayoutParams lp = new TableRow.LayoutParams(width, height);
            if (i == 0) {
                lp.setMargins(0, 0, DensityUtil.dip2px(1), 0);
            } else {
                lp.setMargins(0, DensityUtil.dip2px(1), DensityUtil.dip2px(1), 0);
            }
            tableRow.addView(projectName, lp);

            // 施工单位
            TextView sgdwNum = new TextView(this);
            sgdwNum.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
            sgdwNum.setText(sameDayBean.getTotalNumBy());
            sgdwNum.setTextSize(12);
            sgdwNum.setGravity(Gravity.CENTER);
            sgdwNum.setTextColor(ContextCompat.getColor(this, R.color.black));
            lp = new TableRow.LayoutParams(width, height);
            if (i == 0) {
                lp.setMargins(0, 0, DensityUtil.dip2px(1), 0);
            } else {
                lp.setMargins(0, DensityUtil.dip2px(1), DensityUtil.dip2px(1), 0);
            }
            tableRow.addView(sgdwNum, lp);

            // 施工单位
            TextView zj = new TextView(this);
            zj.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
            zj.setText(sameDayBean.getTotalNumZj());
            zj.setTextSize(12);
            zj.setGravity(Gravity.CENTER);
            zj.setTextColor(ContextCompat.getColor(this, R.color.black));
            lp = new TableRow.LayoutParams(width, height);
            if (i == 0) {
                lp.setMargins(0, 0, DensityUtil.dip2px(1), 0);
            } else {
                lp.setMargins(0, DensityUtil.dip2px(1), DensityUtil.dip2px(1), 0);
            }
            tableRow.addView(zj, lp);

            // 监理
            TextView jlNum = new TextView(this);
            jlNum.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
            jlNum.setText(sameDayBean.getTotalNumCj());
            jlNum.setTextSize(12);
            jlNum.setGravity(Gravity.CENTER);
            jlNum.setTextColor(ContextCompat.getColor(this, R.color.black));
            lp = new TableRow.LayoutParams(width, height);
            if (i == 0) {
                lp.setMargins(0, 0, DensityUtil.dip2px(1), 0);
            } else {
                lp.setMargins(0, DensityUtil.dip2px(1), DensityUtil.dip2px(1), 0);
            }
            tableRow.addView(jlNum, lp);

            // 操作
            TextView operation = new TextView(this);
            operation.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
            operation.setText(sameDayBean.getOperation());
            operation.setTextSize(12);
            operation.setGravity(Gravity.CENTER);
            lp = new TableRow.LayoutParams(DensityUtil.dip2px(70), height);
            if (i == 0) {
                operation.setTextColor(ContextCompat.getColor(this, R.color.black));
                lp.setMargins(0, 0, 0, 0);
            } else {
                operation.setTextColor(ContextCompat.getColor(this, R.color.main_check_bg));
                operation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getSameDayPhoto(sameDayBean.getTwoLevelId(), sameDayBean.getTwoLevelName());
                    }
                });
                lp.setMargins(0, DensityUtil.dip2px(1), 0, 0);
            }
            tableRow.addView(operation, lp);

            tbSameDayProjectDetail.addView(tableRow, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
        }
    }

    /**
     * @param levelId
     */
    private void getSameDayPhoto(String levelId, final String levelNam) {
        LoadingUtils.showLoading(mContext);
        JSONObject obj = new JSONObject();
        try {
            obj.put("rootLevelId", getIntent().getStringExtra("rootLevelId"));
            obj.put("twoLevelId", levelId);
            obj.put("startDate", getIntent().getLongExtra("beganDate", System.currentTimeMillis()));
            obj.put("endDate", getIntent().getLongExtra("endDate", DateUtil.tomorrow().getTime()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(ConstantsUtil.JSON, obj.toString());
        Request request = new Request.Builder()
                .url(ConstantsUtil.BASE_URL + ConstantsUtil.PROCESS_AND_PHOTO_LIST_TODAY)
                .addHeader("token", (String) SpUtil.get(mContext, ConstantsUtil.TOKEN, ""))
                .post(requestBody)
                .build();
        ConstantsUtil.okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LoadingUtils.hideLoading();
                runChildrenThread(getString(R.string.server_exception));
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
                            Gson gson = new Gson();
                            final SameDayListModel model = gson.fromJson(jsonData, SameDayListModel.class);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (model.getData() != null && model.getData().size() > 0) {
                                        initSameDayPhotos(model.getData(), levelNam);
                                    } else {
                                        initSameDayPhotos(new ArrayList<SameDayBean>(), levelNam);
                                    }
                                    LoadingUtils.hideLoading();
                                }
                            });
                            LoadingUtils.hideLoading();
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

    private void initSameDayPhotos(List<SameDayBean> beanList, String levelName) {
        tbSameDayPhotos.removeAllViews();
        rlPhotos.setVisibility(View.VISIBLE);
        txtSameDayPhotos.setText(getIntent().getStringExtra("rootLevelName") + levelName + "工序照片");

        List<SameDayBean> sList = new ArrayList<>();
        SameDayBean bean = new SameDayBean();
        bean.setRootLevelName("序号");
        bean.setProcessName("工序位置");
        bean.setLevelNameAll("");
        bean.setOperation("照片数据");
        sList.add(bean);

        if (beanList != null) {
            for (int n = 0; n < beanList.size(); n++) {
                beanList.get(n).setRootLevelName((n + 1) + "");
                beanList.get(n).setOperation("查看");
                sList.add(beanList.get(n));
            }
        }

        // 动态添加行
        int size = sList.size();
        DisplayMetrics dm = new DisplayMetrics();
        //取得窗口属性
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        //窗口的宽度
        int screenWidth = dm.widthPixels;
        int width = screenWidth - DensityUtil.dip2px(143);
        int height = DensityUtil.dip2px(30);
        for (int i = 0; i < size; i++) {
            final SameDayBean sameDayBean = sList.get(i);
            TableRow tableRow = new TableRow(this);
            // 序号
            TextView no = new TextView(this);
            no.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
            no.setText(sameDayBean.getRootLevelName());
            no.setTextSize(12);
            no.setGravity(Gravity.CENTER);
            no.setTextColor(ContextCompat.getColor(this, R.color.black));
            TableRow.LayoutParams lp = new TableRow.LayoutParams(DensityUtil.dip2px(39), height);
            if (i == 0) {
                lp.setMargins(0, 0, DensityUtil.dip2px(1), 0);
            } else {
                lp.setMargins(0, DensityUtil.dip2px(1), DensityUtil.dip2px(1), 0);
            }
            tableRow.addView(no, lp);

            // 完成工序内容
            TextView finishContext = new TextView(this);
            finishContext.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
            if (TextUtils.isEmpty(sameDayBean.getLevelNameAll())) {
                finishContext.setText(sameDayBean.getProcessName());
            } else {
                String s = sameDayBean.getLevelNameAll().replaceAll(",", "→") + "→" + sameDayBean.getProcessName();
                finishContext.setText(s);
            }
            finishContext.setTextSize(12);
            finishContext.setTextColor(ContextCompat.getColor(this, R.color.black));
            lp = new TableRow.LayoutParams(width, height);
            if (i == 0) {
                finishContext.setGravity(Gravity.CENTER);
                lp.setMargins(0, 0, DensityUtil.dip2px(1), 0);
            } else {
                finishContext.setGravity(Gravity.CENTER_VERTICAL);
                lp.setMargins(0, DensityUtil.dip2px(1), DensityUtil.dip2px(1), 0);
                finishContext.setSingleLine(false);
                finishContext.setHorizontallyScrolling(true);
                finishContext.setMovementMethod(ScrollingMovementMethod.getInstance());
            }
            finishContext.setPadding(DensityUtil.dip2px(5), 0, DensityUtil.dip2px(5), 0);
            tableRow.addView(finishContext, lp);

            // 照片数据
            TextView operation = new TextView(this);
            operation.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
            operation.setText(sameDayBean.getOperation());
            operation.setTextSize(12);
            operation.setGravity(Gravity.CENTER);
            lp = new TableRow.LayoutParams(DensityUtil.dip2px(70), height);
            if (i == 0) {
                operation.setTextColor(ContextCompat.getColor(this, R.color.black));
                lp.setMargins(0, 0, 0, 0);
            } else {
                operation.setTextColor(ContextCompat.getColor(this, R.color.main_check_bg));
                operation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        List<PhotosBean> photoList = sameDayBean.getSxZlPhotoList();
                        if (photoList == null || photoList.size() == 0) {
                            ToastUtil.showShort(mContext, "暂无照片！");
                        } else {
                            // 图片浏览
                            ArrayList<String> urls = new ArrayList<>();
                            int len = photoList.size();
                            for (int i = 0; i < len; i++) {
                                String fileUrl = photoList.get(i).getPhoto_address();
                                if (!TextUtils.isEmpty(fileUrl) && !fileUrl.contains(ConstantsUtil.SAVE_PATH)) {
                                    fileUrl = ConstantsUtil.BASE_URL + ConstantsUtil.prefix + fileUrl;
                                }
                                urls.add(fileUrl);
                            }
                            Intent intent = new Intent(mContext, ShowPhotosActivity.class);
                            intent.putExtra(ShowPhotosActivity.EXTRA_IMAGE_URLS, urls);
                            intent.putExtra(ShowPhotosActivity.EXTRA_IMAGE_INDEX, Integer.valueOf(0));
                            startActivity(intent);
                        }
                    }
                });
                lp.setMargins(0, DensityUtil.dip2px(1), 0, 0);
            }
            tableRow.addView(operation, lp);

            tbSameDayPhotos.addView(tableRow, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
        }
    }

    @Event({R.id.imgBtnLeft})
    private void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgBtnLeft:
                this.finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ScreenManagerUtil.popActivity(this);
    }
}
