package com.zj.expressway.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;
import com.zj.expressway.R;
import com.zj.expressway.adapter.AddProcessAdapter;
import com.zj.expressway.adapter.ProcessListAdapter;
import com.zj.expressway.adapter.ToDoProcessAdapter;
import com.zj.expressway.base.BaseActivity;
import com.zj.expressway.bean.WorkingBean;
import com.zj.expressway.listener.ILoadCallback;
import com.zj.expressway.model.WorkingListModel;
import com.zj.expressway.utils.ChildThreadUtil;
import com.zj.expressway.utils.ConstantsUtil;
import com.zj.expressway.utils.JsonUtils;
import com.zj.expressway.utils.JudgeNetworkIsAvailable;
import com.zj.expressway.utils.SpUtil;
import com.zj.expressway.utils.ToastUtil;

import org.litepal.crud.DataSupport;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
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
 *       Created by HaiJun on 2018/6/11 17:01
 *       工序列表
 */
public class ProcessListActivity extends BaseActivity {
    private WorkingProcedureHolder holder;
    private ProcessListAdapter processAdapter;
    private AddProcessAdapter addProcessAdapter;
    private ToDoProcessAdapter toDoProcessAdapter;
    private Activity mContext;
    private Button btnProcessNum;
    private String userId;
    private boolean isFirstLoad = false;
    private int viewType, pagePosition = 1, processSum = 0, loadType = 0;
    private List<WorkingBean> workingBeanList = new ArrayList<>();

    /**
     * 重载
     * @param mContext
     * @param layout
     */
    public ProcessListActivity(Activity mContext, View layout) {
        this.mContext = mContext;
        holder = new WorkingProcedureHolder();
        x.view().inject(holder, layout);
        userId = (String) SpUtil.get(mContext, ConstantsUtil.USER_ID, "");
    }

    /**
     * 初始化
     * @param viewType
     */
    public void initData(int viewType, Button btnProcessNum, final String searchContext) {
        this.viewType = viewType;
        this.btnProcessNum = btnProcessNum;
        loadType = 0;
        workingBeanList.clear();

        // 设置主题颜色
        holder.refreshLayout.setPrimaryColorsId(R.color.main_bg, android.R.color.white);
        holder.refreshLayout.setFooterTriggerRate(1);
        holder.refreshLayout.setEnableFooterFollowWhenLoadFinished(true);
        // 通过多功能监听接口实现 在第一次加载完成之后 自动刷新
        holder.refreshLayout.setOnMultiPurposeListener(new SimpleMultiPurposeListener() {
            @Override
            public void onStateChanged(@NonNull RefreshLayout refreshLayout, @NonNull RefreshState oldState, @NonNull RefreshState newState) {
                if (oldState == RefreshState.LoadFinish && newState == RefreshState.None) {
                    // refreshLayout.autoRefresh();
                    // refreshLayout.setOnMultiPurposeListener(null);
                }
            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                loadType = 1;
                if (workingBeanList.size() < processSum) {
                    pagePosition++;
                    getData(searchContext);
                } else {
                    ToastUtil.showShort(mContext, "没有更多数据了！");
                    refreshLayout.finishLoadMore(1000);
                }
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                loadType = 2;
                pagePosition = 1;
                workingBeanList.clear();
                getData(searchContext);
            }
        });

        holder.btnAddProcess.setOnClickListener(new onClick());
        holder.btnNoProcessAdd.setOnClickListener(new onClick());

        if (viewType == 2) {
            List<WorkingBean> beanList = DataSupport.where("type = ? and userId = ? and flowType=?", viewType+"", userId, String.valueOf(SpUtil.get(mContext, "ToDoType", "2"))).find(WorkingBean.class);
            if (beanList == null || beanList.size() == 0) {
                holder.btnNoProcessAdd.setVisibility(View.VISIBLE);
            } else {
                processSum = beanList.size();
                workingBeanList.addAll(beanList);
                holder.btnAddProcess.setVisibility(View.VISIBLE);
                initProcessListData();
                holder.refreshLayout.finishLoadMoreWithNoMoreData();
            }
            holder.refreshLayout.setEnableRefresh(false);
            holder.refreshLayout.setEnableLoadMore(false);
            return;
        }

        // 有网---无网
        if (!JudgeNetworkIsAvailable.isNetworkAvailable(mContext)) {
            // getLocalData(pagePosition, pageSize, callback, isHave);
        } else {
            getData(searchContext);
        }
    }

    /**
     * 获取工序列表
     * @param searchContext
     */
    private void getData(String searchContext) {
        JSONObject obj = new JSONObject();
        obj.put("page", pagePosition);
        obj.put("limit", 10);
        String url = "";
        switch (viewType) {
            case 1:
                url = ConstantsUtil.getZxHwGxProcessList;
                break;
            case 4:
                // 待办
                url = ConstantsUtil.TO_DO_LIST;
                break;
            case 5:
                // 已办
                url = ConstantsUtil.HAS_TO_DO_LIST;
                break;
        }
        Request request = ChildThreadUtil.getRequest(mContext, url, obj.toString());
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
                    final WorkingListModel model = gson.fromJson(jsonData, WorkingListModel.class);
                    if (model.isSuccess()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                processSum = model.getTotalNumber();
                                // 向本地数据库保存数据
                                if (model.getData() != null) {
                                    for (WorkingBean bean : model.getData()) {
                                        bean.setType(viewType + "");
                                        bean.setUserId(userId);
                                        bean.saveOrUpdate("processId=?", StrUtil.isEmpty(bean.getProcessId()) ? bean.getWorkId() : bean.getProcessId());
                                    }
                                    workingBeanList.addAll(model.getData());
                                }
                                initProcessListData();
                                stopLoad();
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
     * 点击事件
     */
    private class onClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(mContext, ToDoDetailsActivity.class);
            intent.putExtra("workId", "添加");
            intent.putExtra("flowId", "");
            intent.putExtra("processId", "");
            mContext.startActivityForResult(intent, 20001);
        }
    }

    /**
     * 更新适配器
     */
    public void updateData() {
        if (addProcessAdapter != null) {
            List<WorkingBean> beanList = DataSupport.where("type = ? and userId = ? and flowType=?", viewType+"", userId, String.valueOf(SpUtil.get(mContext, "ToDoType", "2"))).find(WorkingBean.class);
            workingBeanList.clear();
            workingBeanList.addAll(beanList);
            addProcessAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 停止加载
     */
    private void stopLoad() {
        if (loadType == 1) {
            holder.refreshLayout.finishLoadMore(1000);
        } else if (loadType == 2) {
            holder.refreshLayout.finishRefresh(1000);
        }
    }

    /**
     * 获取本地数据
     * @param pagePosition
     * @param pageSize
     * @param callback
     * @param isHave
     */
    private void getLocalData(final int pagePosition, final int pageSize, final ILoadCallback callback, final boolean isHave) {
        /*runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String start = String.valueOf((pagePosition-1)*pageSize);
                String end = String.valueOf(pagePosition*pageSize);
                List<WorkingBean> workingBeanList = DataSupport.where("userId=? and type=? order by enterTime desc limit ?, ?", userId, viewType+"", start, end).find(WorkingBean.class);
                List<WorkingBean> beanSize = DataSupport.where("userId=? and type=? order by enterTime desc ", userId, viewType+"").find(WorkingBean.class);
                sum = beanSize == null ? 0 : beanSize.size();
                if (!isLoad) {
                    isLoad = true;
                    String str = btnProcessNum.getText().toString();
                    if (str.length() <= 3) {
                        btnProcessNum.setText(str + "（" + sum + "）");
                    } else {
                        btnProcessNum.setText(str.substring(0, 3) + "（" + sum + "）");
                    }
                }

                // 数据的处理最终还是交给被装饰的adapter来处理
                if (!isHave) {
                    if (viewType == 1) {
                        mAdapter.appendData(workingBeanList);
                    } else {
                        toDoAdapter.appendData(workingBeanList);
                    }
                }

                callback.onSuccess();

                if (!isHave) {
                    int sumSize = holder.rvMsg.computeVerticalScrollRange();
                    int size = viewType == 1 ? mAdapter.getItemCount() * DensityUtil.dip2px(144) : toDoAdapter.getItemCount() * DensityUtil.dip2px(144);
                    boolean isFull = size >= sumSize ? true : false;
                    if (workingBeanList == null || workingBeanList.size() == 0 || !isFull) {
                        callback.onFailure();
                    }
                } else {
                    callback.onFailure();
                }
            }
        });*/
    }

    /**
     * 初始化工序列表
     */
    private void initProcessListData() {
        // 显示无数据
        if (viewType != 2 && pagePosition == 1 && processSum == 0) {
            holder.rvMsg.setVisibility(View.GONE);
            holder.llSearchData.setVisibility(View.VISIBLE);
            holder.txtMsg.setText("未搜索到任何数据");
            holder.txtClear.setText("，清空搜索条件");
        } else {
            holder.rvMsg.setVisibility(View.VISIBLE);
            holder.llSearchData.setVisibility(View.GONE);
        }

        // 设置tab显示工序数量
        if (!isFirstLoad) {
            isFirstLoad = true;
            String str = btnProcessNum.getText().toString();
            if (str.length() <= 3) {
                btnProcessNum.setText(str + "（" + processSum + "）");
            } else {
                btnProcessNum.setText(str.substring(0, 3) + "（" + processSum + "）");
            }
        }

        // 数据处理
        if (viewType == 1) {
            processAdapter = new ProcessListAdapter(mContext, workingBeanList);
        } else if (viewType == 2) {
            addProcessAdapter = new AddProcessAdapter(mContext, workingBeanList);
        } else {
            toDoProcessAdapter = new ToDoProcessAdapter(mContext, workingBeanList);
        }
        holder.rvMsg.setAdapter(processAdapter == null ? toDoProcessAdapter == null ? addProcessAdapter : toDoProcessAdapter : processAdapter);
        holder.rvMsg.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
    }

    /**
     * 容纳器
     */
    private class WorkingProcedureHolder {
        @ViewInject(R.id.refreshLayout)
        private RefreshLayout refreshLayout;
        @ViewInject(R.id.rvMsg)
        private RecyclerView rvMsg;
        @ViewInject(R.id.txtMsg)
        private TextView txtMsg;
        @ViewInject(R.id.btnAddProcess)
        private Button btnAddProcess;
        @ViewInject(R.id.btnNoProcessAdd)
        private Button btnNoProcessAdd;
        @ViewInject(R.id.txtClear)
        private TextView txtClear;
        @ViewInject(R.id.llSearchData)
        private LinearLayout llSearchData;

    }
}
