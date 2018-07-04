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
 * Created by HaiJun on 2018/6/11 17:01
 * 工序列表
 */
public class ProcessListActivity extends BaseActivity {
    private WorkingProcedureHolder holder;
    private ProcessListAdapter processAdapter;
    private AddProcessAdapter addProcessAdapter;
    private ToDoProcessAdapter toDoProcessAdapter;
    private Activity mContext;
    private Button btnProcessNum;
    private String userId;
    private boolean isFirstLoad = true;
    private int viewType, pagePosition = 1, processSum = 0, loadType = 0;
    private List<WorkingBean> workingBeanList = new ArrayList<>();

    /**
     * 重载
     *
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
     *
     * @param viewType      tab类型（1：待拍照 2：本地质量安全数据 4：待办 5：已办）
     * @param btnProcessNum 工序数量
     * @param searchContext 搜索文字
     */
    public void initData(int viewType, Button btnProcessNum, final String searchContext) {
        this.viewType = viewType;
        this.btnProcessNum = btnProcessNum;
        loadType = 0;
        if (StrUtil.isEmpty(searchContext)) {
            isFirstLoad = true;
        }
        workingBeanList.clear();

        // 设置主题颜色
        holder.refreshLayout.setPrimaryColorsId(R.color.main_bg, android.R.color.white);
        holder.refreshLayout.setFooterTriggerRate(1);
        holder.refreshLayout.setEnableFooterFollowWhenLoadFinished(true);
        // 通过多功能监听接口实现 在第一次加载完成之后 自动刷新
        holder.refreshLayout.setOnMultiPurposeListener(new SimpleMultiPurposeListener() {
            @Override
            public void onStateChanged(@NonNull RefreshLayout refreshLayout, @NonNull RefreshState oldState, @NonNull RefreshState newState) {
            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                loadType = 1;
                if (workingBeanList.size() < processSum) {
                    pagePosition++;
                    if (JudgeNetworkIsAvailable.isNetworkAvailable(mContext)) {
                        getData(searchContext);
                    } else {
                        getLocalData(searchContext);
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
                workingBeanList.clear();
                if (JudgeNetworkIsAvailable.isNetworkAvailable(mContext)) {
                    getData(searchContext);
                } else {
                    getLocalData(searchContext);
                }
            }
        });

        holder.btnAddProcess.setOnClickListener(new onClick(0));
        holder.btnNoProcessAdd.setOnClickListener(new onClick(1));
        holder.txtClear.setOnClickListener(new onClick(2));

        if (viewType == 2) {
            List<WorkingBean> beanList = DataSupport.where("type = ? and userId = ? and flowType=?", viewType + "", userId, String.valueOf(SpUtil.get(mContext, ConstantsUtil.PROCESS_LIST_TYPE, "2"))).find(WorkingBean.class);
            if (beanList == null || beanList.size() == 0) {
                holder.btnNoProcessAdd.setVisibility(View.VISIBLE);
                holder.btnAddProcess.setVisibility(View.GONE);
                String str = btnProcessNum.getText().toString();
                if (str.contains("（")) {
                    btnProcessNum.setText(str.substring(0, str.indexOf("（")) + "（" + 0 + "）");
                } else {
                    if (str.length() <= 3) {
                        btnProcessNum.setText(str + "（" + 0 + "）");
                    } else {
                        btnProcessNum.setText(str.substring(0, 3) + "（" + 0 + "）");
                    }
                }
            } else {
                processSum = beanList.size();
                workingBeanList.addAll(beanList);
                holder.btnNoProcessAdd.setVisibility(View.GONE);
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
            getLocalData(searchContext);
        } else {
            getData(searchContext);
        }
    }

    /**
     * 获取工序列表
     *
     * @param searchContext
     */
    private void getData(String searchContext) {
        JSONObject obj = new JSONObject();
        obj.put("page", pagePosition);
        obj.put("limit", 10);
        if (!StrUtil.isEmpty(searchContext)) {
            if (viewType == 1) {
                obj.put("levelId", searchContext);
            } else {
                obj.put("title", searchContext);
            }
        }

        String url = "";
        String processType = (String) SpUtil.get(mContext, ConstantsUtil.PROCESS_LIST_TYPE, "");
        switch (viewType) {
            case 1:
                url = ConstantsUtil.getZxHwGxProcessList;
                obj.put("flowStatus", "0");
                break;
            case 4:
                // 待办
                obj.put("flowStatus", "1");
                if (processType.equals("1")) {
                    url = ConstantsUtil.getTodoListBySenduser;
                    obj.put("flowId", "sxdehzl");
                } else if (processType.equals("2")) {
                    url = ConstantsUtil.getTodoListBySenduser;
                    obj.put("flowId", "zxHwZlTrouble");
                } else if (processType.equals("3")) {
                    url = ConstantsUtil.getTodoListBySenduser;
                    obj.put("flowId", "zxHwAqHiddenDanger");
                } else {
                    url = ConstantsUtil.TO_DO_LIST;
                }
                break;
            case 5:
                // 已办
                obj.put("flowStatus", "2");
                if (processType.equals("1")) {
                    url = ConstantsUtil.getHasTodoListBySenduser;
                    obj.put("flowId", "sxdehzl");
                } else if (processType.equals("2")) {
                    url = ConstantsUtil.getHasTodoListBySenduser;
                    obj.put("flowId", "zxHwZlTrouble");
                } else if (processType.equals("3")) {
                    url = ConstantsUtil.getHasTodoListBySenduser;
                    obj.put("flowId", "zxHwAqHiddenDanger");
                } else {
                    url = ConstantsUtil.HAS_TO_DO_LIST;
                }
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
                                        if (viewType == 1) {
                                            bean.saveOrUpdate("processId=?", bean.getProcessId());
                                        } else {
                                            bean.setProcessId(bean.getWorkId());
                                            bean.saveOrUpdate("processId=? and nodeName=?", bean.getWorkId(), bean.getNodeName());
                                        }
                                    }

                                    if (viewType == 1) {
                                        List<WorkingBean> workingBeenList = DataSupport.where("userId=? and type=1 and isLocalAdd=1", String.valueOf(SpUtil.get(mContext, ConstantsUtil.USER_ID, ""))).find(WorkingBean.class);
                                        if (workingBeenList != null && workingBeenList.size() > 0) {
                                            workingBeanList.addAll(workingBeenList);
                                            processSum+=workingBeenList.size();
                                        }
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
        private int point;

        public onClick(int point) {
            this.point = point;
        }

        @Override
        public void onClick(View v) {
            switch (point) {
                case 0:
                case 1:
                    Intent intent = new Intent(mContext, ToDoDetailsActivity.class);
                    intent.putExtra("workId", "add");
                    String processType = (String) SpUtil.get(mContext, ConstantsUtil.PROCESS_LIST_TYPE, "");
                    if (StrUtil.equals("2", processType)) {
                        intent.putExtra("flowId", "zxHwZlTrouble");
                    } else {
                        intent.putExtra("flowId", "zxHwAqHiddenDanger");
                    }
                    intent.putExtra("processId", "");
                    mContext.startActivity(intent);
                    break;
                case 2:
                    initData(viewType, btnProcessNum, "");
                    break;
            }
        }
    }

    /**
     * 更新适配器
     */
    public void updateData() {
        List<WorkingBean> beanList = DataSupport.where("type = ? and userId = ? and flowType=?", viewType + "", userId, String.valueOf(SpUtil.get(mContext, ConstantsUtil.PROCESS_LIST_TYPE, "2"))).find(WorkingBean.class);
        if (beanList == null || beanList.size() == 0) {
            holder.btnNoProcessAdd.setVisibility(View.VISIBLE);
            holder.btnAddProcess.setVisibility(View.GONE);
        } else {
            processSum = beanList.size();
            workingBeanList.clear();
            workingBeanList.addAll(beanList);
            holder.btnNoProcessAdd.setVisibility(View.GONE);
            holder.btnAddProcess.setVisibility(View.VISIBLE);
            initProcessListData();
            holder.refreshLayout.finishLoadMoreWithNoMoreData();
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
     */
    private void getLocalData(String searchContext) {
        String start = String.valueOf((pagePosition - 1) * 10);
        String end = String.valueOf(pagePosition * 10);
        List<WorkingBean> workingBeen;
        String str = (String) SpUtil.get(mContext, ConstantsUtil.PROCESS_LIST_TYPE, "");
        if (StrUtil.isEmpty(searchContext)) {
            if (viewType == 1 || viewType == 2) {
                workingBeen = DataSupport.where("userId=? and type=? order by enterTime desc limit ?, ?", userId, viewType + "", start, end).find(WorkingBean.class);
            } else {
                if (str.equals("1")) {
                    workingBeen = DataSupport.where("userId=? and type=? and flowId=? order by enterTime desc limit ?, ?", userId, viewType + "", "sxdehzl", start, end).find(WorkingBean.class);
                } else if (str.equals("2")) {
                    workingBeen = DataSupport.where("userId=? and type=? and flowId=? order by enterTime desc limit ?, ?", userId, viewType + "", "zxHwZlTrouble", start, end).find(WorkingBean.class);
                } else if (str.equals("3")) {
                    workingBeen = DataSupport.where("userId=? and type=? and flowId=? order by enterTime desc limit ?, ?", userId, viewType + "", "zxHwAqHiddenDanger", start, end).find(WorkingBean.class);
                } else {
                    workingBeen = DataSupport.where("userId=? and type=? order by enterTime desc limit ?, ?", userId, viewType + "", start, end).find(WorkingBean.class);
                }
            }
        } else {
            if (viewType == 1) {
                workingBeen = DataSupport.where("userId=? and type=? and (processName=? or levelId=?) order by enterTime desc limit ?, ?", userId, viewType + "", searchContext, searchContext, start, end).find(WorkingBean.class);
            } else if (viewType == 2) {
                if (str.equals("2")) {
                    workingBeen = DataSupport.where("userId=? and type=? and (troubleTitle=? or levelId=?) order by enterTime desc limit ?, ?", userId, viewType + "", searchContext, searchContext, start, end).find(WorkingBean.class);
                } else {
                    workingBeen = DataSupport.where("userId=? and type=? and (dangerTitle=? or levelId=?) order by enterTime desc limit ?, ?", userId, viewType + "", searchContext, searchContext, start, end).find(WorkingBean.class);
                }
            } else {
                if (str.equals("1")) {
                    workingBeen = DataSupport.where("userId=? and type=? and title=? and flowId=? order by enterTime desc limit ?, ?", userId, viewType + "", searchContext, "sxdehzl", start, end).find(WorkingBean.class);
                } else if (str.equals("2")) {
                    workingBeen = DataSupport.where("userId=? and type=? and title=? and flowId=? order by enterTime desc limit ?, ?", userId, viewType + "", searchContext, "zxHwZlTrouble", start, end).find(WorkingBean.class);
                } else if (str.equals("3")) {
                    workingBeen = DataSupport.where("userId=? and type=? and title=? and flowId=? order by enterTime desc limit ?, ?", userId, viewType + "", searchContext, "zxHwAqHiddenDanger", start, end).find(WorkingBean.class);
                } else {
                    workingBeen = DataSupport.where("userId=? and type=? and title=? order by enterTime desc limit ?, ?", userId, viewType + "", searchContext, start, end).find(WorkingBean.class);
                }
            }
        }
        processSum = workingBeen == null ? 0 : workingBeen.size();
        workingBeanList.addAll(workingBeen);
        stopLoad();
        initProcessListData();
    }

    /**
     * 初始化工序列表
     */
    private void initProcessListData() {
        // 显示无数据
        if (!isFirstLoad && viewType != 2 && pagePosition == 1 && processSum == 0) {
            holder.rvMsg.setVisibility(View.GONE);
            holder.llSearchData.setVisibility(View.VISIBLE);
            holder.txtMsg.setText("未搜索到任何数据");
            holder.txtClear.setText("，清空搜索条件");
        } else if (pagePosition == 1 && processSum == 0) {
            holder.rvMsg.setVisibility(View.GONE);
            holder.llSearchData.setVisibility(View.VISIBLE);
            holder.txtMsg.setText("暂无数据");
            holder.txtClear.setText("");
        } else {
            holder.rvMsg.setVisibility(View.VISIBLE);
            holder.llSearchData.setVisibility(View.GONE);
        }

        // 设置tab显示工序数量
        if (isFirstLoad) {
            isFirstLoad = false;
            String str = btnProcessNum.getText().toString();
            if (str.contains("（")) {
                btnProcessNum.setText(str.substring(0, str.indexOf("（")) + "（" + processSum + "）");
            } else {
                if (str.length() <= 3) {
                    btnProcessNum.setText(str + "（" + processSum + "）");
                } else {
                    btnProcessNum.setText(str.substring(0, 3) + "（" + processSum + "）");
                }
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
