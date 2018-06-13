package com.zj.expressway.activity;

import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.zj.expressway.R;
import com.zj.expressway.adapter.LoadMoreAdapter;
import com.zj.expressway.adapter.ToDoWorkingAdapter;
import com.zj.expressway.adapter.WorkingProcedureListAdapter;
import com.zj.expressway.base.BaseActivity;
import com.zj.expressway.base.BaseAdapter;
import com.zj.expressway.listener.ILoadCallback;
import com.zj.expressway.listener.OnLoad;
import com.zj.expressway.model.WorkingListModel;
import com.zj.expressway.utils.ConstantsUtil;
import com.zj.expressway.utils.JsonUtils;
import com.zj.expressway.utils.JudgeNetworkIsAvailable;
import com.zj.expressway.utils.SpUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.util.DensityUtil;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.IOException;

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
 *       Created by HaiJun on 2018/6/11 17:01
 *       工序列表
 */
public class WorkingProcedureListActivity extends BaseActivity {
    private WorkingProcedureListAdapter mAdapter;
    private WorkingProcedureHolder holder;
    private ToDoWorkingAdapter toDoAdapter;
    private BaseAdapter baseAdapter;
    private Activity mContext;
    private int viewType;
    private int sum = 0;

    public WorkingProcedureListActivity(Activity mContext, View layoutWorkingProcedure) {
        this.mContext = mContext;
        holder = new WorkingProcedureHolder();
        x.view().inject(holder, layoutWorkingProcedure);
    }

    public void setDate(int viewType) {
        this.viewType = viewType;
        if (!JudgeNetworkIsAvailable.isNetworkAvailable(mContext)) {
            holder.rvMsg.setVisibility(View.GONE);
            holder.txtMsg.setVisibility(View.VISIBLE);
        } else {
            holder.rvMsg.setVisibility(View.VISIBLE);
            holder.txtMsg.setVisibility(View.GONE);
            // 创建被装饰者类实例
            if (viewType == 1) {
                mAdapter = new WorkingProcedureListAdapter(mContext);
                mAdapter.updateData();
            } else {
                toDoAdapter = new ToDoWorkingAdapter(mContext);
                toDoAdapter.updateData();
            }
            // 创建装饰者实例，并传入被装饰者和回调接口
            baseAdapter = new LoadMoreAdapter(viewType == 1 ? mAdapter : toDoAdapter, new OnLoad() {
                @Override
                public void load(int pagePosition, int pageSize, ILoadCallback callback) {
                    boolean isHave = pagePosition != 1 && (pagePosition-1) * pageSize > sum;
                    getData(pagePosition, pageSize, callback, isHave);
                }
            });
            holder.rvMsg.setAdapter(baseAdapter);
            holder.rvMsg.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        }
    }

    /**
     * 获取消息列表
     * @param pagePosition
     * @param callback
     */
    private void getData(int pagePosition, int pageSize, final ILoadCallback callback, final boolean isHave) {
        String url = "";
        JSONObject obj = new JSONObject();
        try {
            obj.put("page", pagePosition);
            obj.put("limit", pageSize);
            switch (viewType) {
                case 1:
                    url = ConstantsUtil.BASE_URL + ConstantsUtil.TO_DO_LIST;
                    obj.put("limit", pageSize);
                    break;
                case 2:
                    // 待办
                    url = ConstantsUtil.BASE_URL + ConstantsUtil.TO_DO_LIST;
                    break;
                case 3:
                    // 已办
                    url = ConstantsUtil.BASE_URL + ConstantsUtil.HAS_TO_DO_LIST;
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = RequestBody.create(ConstantsUtil.JSON, obj.toString());
        Request request = new Request.Builder()
                .url(url)
                .addHeader("token", (String) SpUtil.get(mContext, ConstantsUtil.TOKEN, ""))
                .post(requestBody)
                .build();
        ConstantsUtil.okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        callback.onFailure();
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
                            Gson gson = new Gson();
                            final WorkingListModel model = gson.fromJson(jsonData, WorkingListModel.class);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    sum = model.getTotalNumber();
                                    // 数据的处理最终还是交给被装饰的adapter来处理
                                    if (!isHave) {
                                        if (viewType == 1) {
                                            mAdapter.appendData(model.getData());
                                        } else {
                                            toDoAdapter.appendData(model.getData());
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
                            callback.onFailure();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
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
    }
}
