package com.zj.expressway.activity;

import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.zj.expressway.R;
import com.zj.expressway.adapter.LoadMoreAdapter;
import com.zj.expressway.adapter.ToDoWorkingAdapter;
import com.zj.expressway.adapter.WorkingProcedureListAdapter;
import com.zj.expressway.base.BaseActivity;
import com.zj.expressway.base.BaseAdapter;
import com.zj.expressway.bean.WorkingBean;
import com.zj.expressway.listener.ILoadCallback;
import com.zj.expressway.listener.OnLoad;
import com.zj.expressway.model.WorkingListModel;
import com.zj.expressway.utils.ChildThreadUtil;
import com.zj.expressway.utils.ConstantsUtil;
import com.zj.expressway.utils.JsonUtils;
import com.zj.expressway.utils.JudgeNetworkIsAvailable;
import com.zj.expressway.utils.SpUtil;

import org.litepal.crud.DataSupport;
import org.xutils.common.util.DensityUtil;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.IOException;
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
public class WorkingProcedureListActivity extends BaseActivity {
    private WorkingProcedureListAdapter mAdapter;
    private WorkingProcedureHolder holder;
    private ToDoWorkingAdapter toDoAdapter;
    private BaseAdapter baseAdapter;
    private Activity mContext;
    private Button btnProcessNum;
    private int viewType;
    private int sum = 0;
    private String userId;
    private boolean isLoad = false;

    /**
     * 重载
     * @param mContext
     * @param layoutWorkingProcedure
     */
    public WorkingProcedureListActivity(Activity mContext, View layoutWorkingProcedure) {
        this.mContext = mContext;
        holder = new WorkingProcedureHolder();
        x.view().inject(holder, layoutWorkingProcedure);
        userId = (String) SpUtil.get(mContext, ConstantsUtil.USER_ID, "");
    }

    /**
     *初始化
     * @param viewType
     */
    public void initData(int viewType, Button btnProcessNum, final String levelId) {
        isLoad = false;
        this.viewType = viewType;
        this.btnProcessNum = btnProcessNum;
        // 创建被装饰者类实例
        switch (viewType) {
            case 1:
                mAdapter = new WorkingProcedureListAdapter(mContext);
                mAdapter.updateData();
                break;
            case 2:
            case 3:
            case 4:
            case 5:
                toDoAdapter = new ToDoWorkingAdapter(mContext);
                toDoAdapter.updateData();
                break;
        }
        // 创建装饰者实例，并传入被装饰者和回调接口
        baseAdapter = new LoadMoreAdapter(viewType == 1 ? mAdapter : toDoAdapter, new OnLoad() {
            @Override
            public void load(int pagePosition, int pageSize, ILoadCallback callback) {
                boolean isHave = pagePosition != 1 && (pagePosition-1) * pageSize > sum;
                if (!JudgeNetworkIsAvailable.isNetworkAvailable(mContext)) {
                    getLocalData(pagePosition, pageSize, callback, isHave);
                } else {
                    getData(pagePosition, pageSize, callback, isHave, levelId);
                }
            }
        });
        holder.rvMsg.setAdapter(baseAdapter);
        holder.rvMsg.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
    }

    /**
     * 获取消息列表
     * @param pagePosition
     * @param callback
     */
    private void getData(final int pagePosition, int pageSize, final ILoadCallback callback, final boolean isHave, final String levelId) {
        String url = "";
        JSONObject obj = new JSONObject();
        obj.put("page", pagePosition);
        obj.put("limit", pageSize);
        switch (viewType) {
            case 1:
                url = ConstantsUtil.getZxHwGxProcessList;
                if (levelId != null) {
                    obj.put("levelId", levelId);
                    obj.put("flowStatus", "0");
                }
                break;
            case 4:
                // 待办
                url = ConstantsUtil.TO_DO_LIST;
                if (levelId != null) {
                    obj.put("levelId", levelId);
                    obj.put("flowStatus", "1");
                }
                break;
            case 5:
                // 已办
                url = ConstantsUtil.HAS_TO_DO_LIST;
                if (levelId != null) {
                    obj.put("levelId", levelId);
                    obj.put("flowStatus", "2");
                }
                break;
        }
        Request request = ChildThreadUtil.getRequest(mContext, url, obj.toString());
        ConstantsUtil.okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                ChildThreadUtil.closeLoading(mContext, callback);
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
                                sum = model.getTotalNumber();

                                // 显示无数据
                                if (levelId != null && pagePosition == 1 && sum == 0) {
                                    holder.rvMsg.setVisibility(View.GONE);
                                    holder.llSearchData.setVisibility(View.VISIBLE);
                                    holder.txtMsg.setText("未搜索到任何数据");
                                    holder.txtClear.setText("，清空搜索条件");
                                } else {
                                    holder.rvMsg.setVisibility(View.VISIBLE);
                                    holder.llSearchData.setVisibility(View.GONE);
                                }

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
                                        mAdapter.appendData(model.getData());
                                    } else {
                                        toDoAdapter.appendData(model.getData());
                                    }

                                    if (model.getData() != null) {
                                        for (WorkingBean bean : model.getData()) {
                                            bean.setType(viewType+"");
                                            bean.setUserId(userId);
                                            bean.saveOrUpdate("processId=?", StrUtil.isEmpty(bean.getProcessId()) ? bean.getWorkId() : bean.getProcessId());
                                        }
                                    }
                                }

                                callback.onSuccess();

                                if (!isHave) {
                                    int sumSize = holder.rvMsg.computeVerticalScrollRange();
                                    int size = viewType == 1 ? mAdapter.getItemCount() * DensityUtil.dip2px(144) : toDoAdapter.getItemCount() * DensityUtil.dip2px(144);
                                    boolean isFull = size >= sumSize ? true : false;
                                    if (model == null || model.getData() == null || model.getData().size() == 0 || !isFull) {
                                        callback.onFailure();
                                    }
                                } else {
                                    callback.onFailure();
                                }
                            }
                        });
                    } else {
                        ChildThreadUtil.checkTokenHidden(mContext, model.getMessage(), model.getCode());
                        ChildThreadUtil.closeLoading(mContext, callback);
                    }
                } else {
                    ChildThreadUtil.closeLoading(mContext, callback);
                }
            }
        });
    }

    /**
     * 获取本地数据
     * @param pagePosition
     * @param pageSize
     * @param callback
     * @param isHave
     */
    private void getLocalData(final int pagePosition, final int pageSize, final ILoadCallback callback, final boolean isHave) {
        runOnUiThread(new Runnable() {
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
        });
    }

    /**
     * 容纳器
     */
    private class WorkingProcedureHolder {
        @ViewInject(R.id.rvMsg)
        private RecyclerView rvMsg;
        @ViewInject(R.id.txtMsg)
        private TextView txtMsg;
        @ViewInject(R.id.txtClear)
        private TextView txtClear;
        @ViewInject(R.id.llSearchData)
        private LinearLayout llSearchData;
    }
}
