package com.zj.expressway.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.vivian.timelineitemdecoration.itemdecoration.DotItemDecoration;
import com.vivian.timelineitemdecoration.itemdecoration.SpanIndexListener;
import com.zj.expressway.R;
import com.zj.expressway.adapter.WaterfallFlowTimeLineAdapter;
import com.zj.expressway.base.BaseActivity;
import com.zj.expressway.bean.WorkingBean;
import com.zj.expressway.utils.ConstantsUtil;
import com.zj.expressway.utils.DateUtils;
import com.zj.expressway.utils.JsonUtils;
import com.zj.expressway.utils.LoadingUtils;
import com.zj.expressway.utils.ScreenManagerUtil;
import com.zj.expressway.utils.SpUtil;
import com.zj.expressway.utils.ToastUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
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
 *       工序审核时间轴瀑布流格式
 */
public class ReviewProgressActivity extends BaseActivity {
    @ViewInject(R.id.imgBtnLeft)
    private ImageButton imgBtnLeft;
    @ViewInject(R.id.txtTitle)
    private TextView txtTitle;
    /*审核进度*/
    @ViewInject(R.id.rvTimeLineWaterfallFlow)
    private RecyclerView rvTimeLineWaterfallFlow;

    private List<WorkingBean> mList = new ArrayList<>();
    private WaterfallFlowTimeLineAdapter mAdapter;
    private DotItemDecoration mItemDecoration;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_review_progress);
        x.view().inject(this);
        ScreenManagerUtil.pushActivity(this);

        imgBtnLeft.setVisibility(View.VISIBLE);
        imgBtnLeft.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.back_btn));
        txtTitle.setText("工序审核进度");

        mContext = this;

        getData();
    }

    /**
     * 获取数据
     */
    private void getData() {
        LoadingUtils.showLoading(mContext);
        JSONObject obj = new JSONObject();
        try {
            obj.put("workId", getIntent().getStringExtra("workId"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(ConstantsUtil.JSON, obj.toString());
        Request request = new Request.Builder()
                .url(ConstantsUtil.BASE_URL + ConstantsUtil.getHistory)
                .addHeader("token", (String) SpUtil.get(mContext, ConstantsUtil.TOKEN, ""))
                .post(requestBody)
                .build();
        ConstantsUtil.okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LoadingUtils.hideLoading();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showShort(mContext, getString(R.string.server_exception));
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String jsonData = response.body().string().toString();
                if (JsonUtils.isGoodJson(jsonData)) {
                    try {
                        JSONObject obj = new JSONObject(jsonData);
                        boolean resultFlag = obj.getBoolean("success");
                        if (resultFlag) {
                            final JSONArray jsonArray = new JSONArray(obj.getString("data"));
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    setData(jsonArray);
                                }
                            });
                        }
                    } catch (JSONException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.showShort(mContext, getString(R.string.json_error));
                            }
                        });
                        e.printStackTrace();
                    }
                    LoadingUtils.hideLoading();
                } else {
                    LoadingUtils.hideLoading();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.showShort(mContext, getString(R.string.json_error));
                        }
                    });
                }
            }
        });
    }

    /**
     * 设置数据
     */
    private void setData(JSONArray array) {
        for (int i = 0; i < array.length(); i++) {
            try {
                JSONObject obj = new JSONObject(array.get(i).toString());
                WorkingBean bean = new WorkingBean();
                bean.setProcessName(obj.getString("realName"));
                bean.setFlowName(obj.getString("nodeName"));
                bean.setContent(DateUtils.setDataToStr(obj.getLong("actionTime")));
                bean.setProcessState(obj.getString("doTimeShow"));
                mList.add(bean);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        initView();
    }

    /**
     * 设置显示数据
     */
    private void initView() {
        rvTimeLineWaterfallFlow.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        mItemDecoration = new DotItemDecoration
                .Builder(this)
                .setOrientation(DotItemDecoration.VERTICAL)//if you want a horizontal item decoration,remember to set horizontal orientation to your LayoutManager
                .setItemStyle(DotItemDecoration.STYLE_DRAW)
                .setTopDistance(20)//dp
                .setItemInterVal(10)//dp
                .setDotColor(ContextCompat.getColor(this, R.color.main_bg))
                .setDotRadius(5)//dp
                .setDotPaddingTop(0)
                .setDotInItemOrientationCenter(true)//set true if you want the dot align center
                .setLineColor(ContextCompat.getColor(this, R.color.main_bg))
                .setLineWidth(3)//dp
                .setEndText("END")
                .setBottomDistance(40)
                .create();
        mItemDecoration.setSpanIndexListener(new SpanIndexListener() {
            @Override
            public void onSpanIndexChange(View view, int spanIndex) {
                view.setBackgroundResource(spanIndex == 0 ? R.drawable.left : R.drawable.right);
            }
        });
        rvTimeLineWaterfallFlow.addItemDecoration(mItemDecoration);
        mAdapter = new WaterfallFlowTimeLineAdapter(this, mList);
        rvTimeLineWaterfallFlow.setAdapter(mAdapter);
    }

    @Event({R.id.imgBtnLeft})
    private void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgBtnLeft:
                finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ScreenManagerUtil.popActivity(this);
    }
}
