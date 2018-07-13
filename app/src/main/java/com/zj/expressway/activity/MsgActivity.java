package com.zj.expressway.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;
import com.zj.expressway.R;
import com.zj.expressway.adapter.MsgAdapter;
import com.zj.expressway.base.BaseActivity;
import com.zj.expressway.bean.MsgBean;
import com.zj.expressway.bean.WorkingBean;
import com.zj.expressway.model.MsgModel;
import com.zj.expressway.model.WorkingListModel;
import com.zj.expressway.utils.ChildThreadUtil;
import com.zj.expressway.utils.ConstantsUtil;
import com.zj.expressway.utils.JsonUtils;
import com.zj.expressway.utils.JudgeNetworkIsAvailable;
import com.zj.expressway.utils.LoadingUtils;
import com.zj.expressway.utils.ScreenManagerUtil;
import com.zj.expressway.utils.SpUtil;
import com.zj.expressway.utils.ToastUtil;

import org.litepal.crud.DataSupport;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.hutool.json.JSONObject;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by HaiJun on 2018/6/11 17:01
 * 消息列表
 */
public class MsgActivity extends BaseActivity {
    @ViewInject(R.id.imgBtnLeft)
    private ImageView imgBtnLeft;
    @ViewInject(R.id.txtTitle)
    private TextView txtTitle;
    @ViewInject(R.id.actionBar)
    private View actionBar;
    @ViewInject(R.id.refreshLayout)
    private RefreshLayout refreshLayout;
    @ViewInject(R.id.rvMsg)
    private RecyclerView rvMsg;
    @ViewInject(R.id.ivLogo)
    private Button ivLogo;

    private MsgAdapter msgAdapter;
    private Activity mContext;
    private int pagePosition = 1, processSum = 0, loadType = 0;
    private List<MsgBean> msgBeanList = new ArrayList<>();
    private String userId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_msg);

        mContext = this;
        x.view().inject(this);
        ScreenManagerUtil.pushActivity(this);

        userId = (String) SpUtil.get(mContext, ConstantsUtil.USER_ID, "");

        txtTitle.setText("消息列表");
        actionBar.setVisibility(View.VISIBLE);
        imgBtnLeft.setVisibility(View.VISIBLE);
        imgBtnLeft.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.back_btn));

        // 设置主题颜色
        refreshLayout.setPrimaryColorsId(R.color.main_bg, android.R.color.white);
        refreshLayout.setFooterTriggerRate(1);
        refreshLayout.setEnableFooterFollowWhenLoadFinished(true);
        refreshLayout.setEnableScrollContentWhenLoaded(true);//是否在加载完成时滚动列表显示新的内容
        refreshLayout.setEnableScrollContentWhenRefreshed(true);
        // 通过多功能监听接口实现 在第一次加载完成之后 自动刷新
        refreshLayout.setOnMultiPurposeListener(new SimpleMultiPurposeListener() {
            @Override
            public void onStateChanged(@NonNull RefreshLayout refreshLayout, @NonNull RefreshState oldState, @NonNull RefreshState newState) {
            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                loadType = 1;
                if (msgBeanList.size() < processSum) {
                    pagePosition++;
                    if (JudgeNetworkIsAvailable.isNetworkAvailable(mContext)) {
                        getMsgData(false);
                    } else {
                        getLocalData(false);
                    }
                } else {
                    ToastUtil.showShort(mContext, "没有更多数据了！");
                    refreshLayout.finishLoadMore(1000);
                }
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                loadType = 2;
                pagePosition = 1;
                msgBeanList.clear();
                if (JudgeNetworkIsAvailable.isNetworkAvailable(mContext)) {
                    getMsgData(false);
                } else {
                    getLocalData(false);
                }
            }
        });

        // 有网---无网
        if (!JudgeNetworkIsAvailable.isNetworkAvailable(mContext)) {
            getLocalData(true);
        } else {
            getMsgData(true);
        }
    }

    /**
     * 获取消息列表
     *
     * @param isLoading 是否显示加载动画
     */
    private void getMsgData(final boolean isLoading) {
        if (isLoading) {
            LoadingUtils.showLoading(mContext);
        }
        JSONObject obj = new JSONObject();
        obj.put("page", pagePosition);
        obj.put("limit", 10);
        obj.put("flowStatus", "0");
        Request request = ChildThreadUtil.getRequest(mContext, ConstantsUtil.appGetMessageList, obj.toString());
        ConstantsUtil.okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                stopLoad();
                ChildThreadUtil.toastMsgHidden(mContext, mContext.getString(R.string.server_exception));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String jsonData = response.body().string().toString();
                if (JsonUtils.isGoodJson(jsonData)) {
                    Gson gson = new Gson();
                    final MsgModel model = gson.fromJson(jsonData, MsgModel.class);
                    if (model.isSuccess()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                processSum = model.getTotalNumber();
                                // 向本地数据库保存数据
                                if (model.getData() != null) {
                                    for (MsgBean bean : model.getData()) {
                                        bean.setUserId(userId);
                                        bean.saveOrUpdate("msgId=? and userId=?", bean.getMsgId(), userId);
                                    }
                                    msgBeanList.addAll(model.getData());
                                }
                                initProcessListData();
                                stopLoad();
                                if (isLoading) {
                                    LoadingUtils.hideLoading();
                                }
                            }
                        });
                    } else {
                        stopLoad();
                        ChildThreadUtil.checkTokenHidden(mContext, model.getMessage(), model.getCode());
                    }
                } else {
                    stopLoad();
                    ChildThreadUtil.toastMsgHidden(mContext, mContext.getString(R.string.json_error));
                }
            }
        });
    }

    /**
     * 获取本地数据
     *
     * @param isLoading
     */
    private void getLocalData(boolean isLoading) {
        if (isLoading) {
            LoadingUtils.showLoading(mContext);
        }
        String start = String.valueOf((pagePosition - 1) * 10);
        String end = "10";
        List<MsgBean> workingBeen = DataSupport.where("userId=? order by createTime desc limit ?, ?", userId, start, end).find(MsgBean.class);
        List<MsgBean> workingBeenSum = DataSupport.where("userId=?", userId).find(MsgBean.class);
        processSum = workingBeenSum == null ? 0 : workingBeenSum.size();
        msgBeanList.addAll(workingBeen);
        stopLoad();
        initProcessListData();
        if (isLoading) {
            LoadingUtils.hideLoading();
        }
    }

    /**
     * 初始化工序列表
     */
    private void initProcessListData() {
        // 显示无数据
        if (pagePosition == 1 && processSum == 0) {
            rvMsg.setVisibility(View.GONE);
            ivLogo.setVisibility(View.VISIBLE);
        }

        // 数据处理
        if (msgAdapter == null) {
            msgAdapter = new MsgAdapter(mContext, msgBeanList);
            rvMsg.setAdapter(msgAdapter);
            rvMsg.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        } else {
            msgAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 停止加载
     */
    private void stopLoad() {
        if (loadType == 1) {
            refreshLayout.finishLoadMore(1000);
        } else if (loadType == 2) {
            refreshLayout.finishRefresh(1000);
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
        ScreenManagerUtil.popActivity(this);    // 退出当前activity
    }
}
