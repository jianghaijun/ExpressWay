package com.zj.expressway.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.zj.expressway.R;
import com.zj.expressway.adapter.ProcessManagerAdapter;
import com.zj.expressway.base.BaseActivity;
import com.zj.expressway.bean.ContractorBean;
import com.zj.expressway.dialog.SlippingHintDialog;
import com.zj.expressway.listener.ContractorListener;
import com.zj.expressway.model.ContractorModel;
import com.zj.expressway.tree.Node;
import com.zj.expressway.utils.ChildThreadUtil;
import com.zj.expressway.utils.ConstantsUtil;
import com.zj.expressway.utils.JsonUtils;
import com.zj.expressway.utils.JudgeNetworkIsAvailable;
import com.zj.expressway.utils.LoadingUtils;
import com.zj.expressway.utils.ScreenManagerUtil;
import com.zj.expressway.utils.SpUtil;
import com.zj.expressway.utils.ToastUtil;

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
 * Created by HaiJun on 2018/6/11 16:59
 * 工序树形图
 */
public class ProcessManagerActivity extends BaseActivity {
    @ViewInject(R.id.imgBtnLeft)
    private ImageButton imgBtnLeft;
    @ViewInject(R.id.txtTitle)
    private TextView txtTitle;
    @ViewInject(R.id.rvTimeLineWaterfallFlow)
    private RecyclerView rvTreeList;

    private ProcessManagerAdapter mAdapter;
    private Activity mContext;
    private List<Node> allCache;
    private boolean isFirstLoad = true;
    private List<Node> all;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_progress_manager);

        mContext = this;
        x.view().inject(this);
        ScreenManagerUtil.pushActivity(this);

        imgBtnLeft.setVisibility(View.VISIBLE);
        imgBtnLeft.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.back_btn));
        txtTitle.setText("工序管理");

        new SlippingHintDialog(mContext).show();
        if (JudgeNetworkIsAvailable.isNetworkAvailable(this)) {
            getData();
        } else {
            ToastUtil.showShort(mContext, getString(R.string.not_network));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 获取数据
     */
    private void getData() {
        if (!isFirstLoad) {
            LoadingUtils.showLoading(mContext);
        }
        JSONObject obj = new JSONObject();
        obj.put("parentId", "0");
        Request request = ChildThreadUtil.getRequest(mContext, ConstantsUtil.getZxHwGxProjectLevelList, obj.toString());
        ConstantsUtil.okHttpClient.newCall(request).enqueue(callback);
    }

    /**
     * 数据请求回调
     */
    private Callback callback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            ChildThreadUtil.toastMsgHidden(mContext, getString(R.string.server_exception));
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String jsonData = response.body().string().toString();
            if (JsonUtils.isGoodJson(jsonData)) {
                Gson gson = new Gson();
                final ContractorModel model = gson.fromJson(jsonData, ContractorModel.class);
                if (model.isSuccess()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // 设置节点
                            isFirstLoad = false;
                            setContractorNode(model.getData());
                            LoadingUtils.hideLoading();
                        }
                    });
                } else {
                    ChildThreadUtil.checkTokenHidden(mContext, model.getMessage(), model.getCode());
                }
            } else {
                ChildThreadUtil.toastMsgHidden(mContext, getString(R.string.json_error));
            }
        }
    };

    /**
     * 设置节点,可以通过循环或递归方式添加节点
     *
     * @param contractorBean
     */
    private void setContractorNode(List<ContractorBean> contractorBean) {
        // 添加节点
        if (contractorBean != null && contractorBean.size() > 0) {
            int listSize = contractorBean.size();
            // 创建根节点
            Node root = new Node();
            root.setFolderFlag("1");

            for (int i = 0; i < listSize; i++) {
                getNode(contractorBean.get(i), root);
            }

            mAdapter = new ProcessManagerAdapter(this, root, listener);
            /* 设置默认展开级别 */
            mAdapter.setExpandLevel(1);
            rvTreeList.setAdapter(mAdapter);
            rvTreeList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        }
    }

    /**
     * 子节点
     *
     * @param contractorListBean
     * @param root
     * @return
     */
    private Node getNode(ContractorBean contractorListBean, Node root) {
        String levelId = contractorListBean.getLevelId();
        String levelName = contractorListBean.getLevelName();
        // 创建子节点
        Node n = new Node();
        n.setParent(root);
        n.setLevelId(levelId);
        n.setLevelName(levelName);
        n.setParentId(contractorListBean.getParentId());
        n.setFolderFlag(contractorListBean.getCanExpand());
        n.setExpanded(false);
        n.setLoading(false);
        n.setChoice(false);
        root.add(n);
        return n;
    }

    /**
     * 是否已加载监听
     */
    private ContractorListener listener = new ContractorListener() {
        @Override
        public void returnData(List<Node> allCaches, List<Node> allNode, int point, String levelId) {
            allCache = allCaches;
            all = allNode;
            // 没有网络并且没有加载过
            if (JudgeNetworkIsAvailable.isNetworkAvailable(mContext)) {
                loadProcedureByNodeId(point, levelId);
            }
        }
    };

    /**
     * 加载层级下的节点
     *
     * @param position
     * @param parentId
     */
    private void loadProcedureByNodeId(final int position, final String parentId) {
        LoadingUtils.showLoading(mContext);
        JSONObject obj = new JSONObject();
        obj.put("parentId", parentId);
        obj.put("levelType", SpUtil.get(mContext, ConstantsUtil.USER_TYPE, ""));
        Request request = ChildThreadUtil.getRequest(mContext, ConstantsUtil.getZxHwGxProjectLevelList, obj.toString());
        ConstantsUtil.okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                ChildThreadUtil.toastMsgHidden(mContext, getString(R.string.server_exception));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String jsonData = response.body().string().toString();
                if (JsonUtils.isGoodJson(jsonData)) {
                    Gson gson = new Gson();
                    final ContractorModel model = gson.fromJson(jsonData, ContractorModel.class);
                    if (model.isSuccess()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // 将数据添加到Node的子节点中
                                setNodeInChildren(model.getData(), position);
                                LoadingUtils.hideLoading();
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
     * 将数据插入到Node中
     *
     * @param data
     * @param position
     */
    private void setNodeInChildren(List<ContractorBean> data, final int position) {
        List<Node> nodes = new ArrayList<>();
        for (ContractorBean contractor : data) {
            String levelId = contractor.getLevelId();
            String levelName = contractor.getLevelName();
            // 创建子节点
            Node n = new Node();
            n.setParent(all.get(position));
            n.setLevelId(levelId);
            n.setLevelName(levelName);
            n.setParentId(contractor.getParentId());
            n.setFolderFlag(contractor.getCanExpand());
            n.setExpanded(false);
            n.setLoading(false);
            n.setChoice(false);
            nodes.add(n);
        }

        // 添加子节点到指定根节点下面
        all.addAll(position + 1, nodes);
        // 需要放到此节点下
        Node node = all.get(position);
        int point = allCache.indexOf(node);
        allCache.addAll(point + 1, nodes);

        all.get(position).setChildren(nodes);
        all.get(position).setLoading(true);
        allCache.get(position).setChildren(nodes);
        allCache.get(position).setLoading(true);

        if (data == null || data.size() == 0) {
            all.get(position).setFolderFlag("1");
            allCache.get(position).setFolderFlag("1");
        }
        mAdapter.notifyDataSetChanged();
    }


    @Event({R.id.imgBtnLeft})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgBtnLeft:
                this.finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        ScreenManagerUtil.popActivity(this);
        super.onDestroy();
    }
}
