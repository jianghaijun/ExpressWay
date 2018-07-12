package com.zj.expressway.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.yanzhenjie.recyclerview.swipe.touch.OnItemMoveListener;
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
    private SwipeMenuRecyclerView rvTreeList;

    private OnItemMoveListener getItemMoveListener() {
        return null;
    }

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

        if (JudgeNetworkIsAvailable.isNetworkAvailable(this)) {
            boolean isPrompt = (boolean) SpUtil.get(this, ConstantsUtil.slide, false);
            if (!isPrompt) {
                new SlippingHintDialog(mContext, R.drawable.cloud, ConstantsUtil.slide, "向左滑动可删除或添加层级！").show();
            }
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

            rvTreeList.setSwipeMenuItemClickListener(mMenuItemClickListener); // Item的Menu点击。
            rvTreeList.setSwipeMenuCreator(mSwipeMenuCreator); // 菜单创建器。
            mAdapter = new ProcessManagerAdapter(this, root, listener);
            rvTreeList.setOnItemMoveListener(getItemMoveListener());// 监听拖拽和侧滑删除，更新UI和数据源。
            /* 设置默认展开级别 */
            mAdapter.setExpandLevel(1);
            rvTreeList.setAdapter(mAdapter);
            rvTreeList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        }
    }

    /**
     * RecyclerView的Item的Menu点击监听。
     */
    private SwipeMenuItemClickListener mMenuItemClickListener = new SwipeMenuItemClickListener() {
        @Override
        public void onItemClick(SwipeMenuBridge menuBridge) {
            menuBridge.closeMenu();
            int direction = menuBridge.getDirection(); // 左侧还是右侧菜单。
            int adapterPosition = menuBridge.getAdapterPosition(); // RecyclerView的Item的position。
            int menuPosition = menuBridge.getPosition(); // 菜单在RecyclerView的Item中的Position。
            if (direction == SwipeMenuRecyclerView.RIGHT_DIRECTION) {
                if (menuPosition == 0) {
                    mAdapter.addBtn(adapterPosition);
                } else {
                    mAdapter.deleteBtn(adapterPosition);
                }
            }
        }
    };

    /**
     * 菜单创建器。
     */
    private SwipeMenuCreator mSwipeMenuCreator = new SwipeMenuCreator() {
        @Override
        public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType) {
            int width = getResources().getDimensionPixelSize(R.dimen.fifty_dp);
            int height = ViewGroup.LayoutParams.MATCH_PARENT;

            // 添加右侧的，如果不添加，则右侧不会出现菜单。
            SwipeMenuItem deleteItem = new SwipeMenuItem(mContext)
                    .setBackground(R.color.colorAccent)
                    .setText(getString(R.string.add))
                    .setTextColor(Color.WHITE)
                    .setTextSize(14)
                    .setWidth(width)
                    .setHeight(height);
            swipeRightMenu.addMenuItem(deleteItem);// 添加一个按钮到右侧侧菜单。

            SwipeMenuItem closeItem = new SwipeMenuItem(mContext)
                    .setBackground(R.color.main_bg)
                    .setText(getString(R.string.delete))
                    .setTextColor(Color.WHITE)
                    .setTextSize(14)
                    .setWidth(width)
                    .setHeight(height);
            swipeRightMenu.addMenuItem(closeItem); // 添加一个按钮到右侧菜单。
        }
    };

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
        n.setParentIdAll(contractorListBean.getParentIdAll());
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
