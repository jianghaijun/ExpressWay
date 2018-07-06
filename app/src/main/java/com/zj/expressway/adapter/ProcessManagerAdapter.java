package com.zj.expressway.adapter;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.gson.Gson;
import com.zj.expressway.R;
import com.zj.expressway.base.BaseModel;
import com.zj.expressway.dialog.PromptDialog;
import com.zj.expressway.dialog.RejectDialog;
import com.zj.expressway.listener.ContractorListener;
import com.zj.expressway.listener.PromptListener;
import com.zj.expressway.listener.ReportListener;
import com.zj.expressway.tree.Node;
import com.zj.expressway.utils.ChildThreadUtil;
import com.zj.expressway.utils.ConstantsUtil;
import com.zj.expressway.utils.JsonUtils;
import com.zj.expressway.utils.LoadingUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by HaiJun on 2018/6/11 17:01
 * 工序管理
 */
public class ProcessManagerAdapter extends RecyclerView.Adapter<ProcessManagerAdapter.AppInfoHold> {
    private List<Node> allCache = new ArrayList<>();
    public List<Node> all = new ArrayList<>();
    private ContractorListener listener;
    private Activity mActivity;

    public ProcessManagerAdapter(Activity mActivity, Node node, ContractorListener listener) {
        this.mActivity = mActivity;
        this.listener = listener;
        addNode(node);
    }

    /**
     * 添加节点
     *
     * @param node
     */
    private void addNode(Node node) {
        if (node.getParent() != null) {
            all.add(node);
            allCache.add(node);
        }
        if (node.isLeaf())
            return;
        for (int i = 0; i < node.getChildren().size(); i++) {
            addNode(node.getChildren().get(i));
        }
    }

    /**
     * 设置展开级别
     *
     * @param level
     */
    public void setExpandLevel(int level) {
        all.clear();
        for (int i = 0; i < allCache.size(); i++) {
            Node n = allCache.get(i);
            if (n.getLevel() <= level) {
                // 上层都设置展开状态
                if (n.getLevel() < level) {
                    n.setExpanded(true);
                    // 最后一层都设置折叠状态
                } else {
                    n.setExpanded(false);
                }
                all.add(n);
            }
        }
        this.notifyDataSetChanged();
    }

    /**
     * 控制节点的展开和收缩
     *
     * @param position
     */
    public void ExpandOrCollapse(int position) {
        Node n = all.get(position);
        if (n != null) {
            // 是否处于展开状态
            if (n.isExpanded()) {
                n.setExpanded(!n.isExpanded());
                filterNode();
                this.notifyDataSetChanged();
            } else {
                // 是否已加载
                if (n.isLoading()) {
                    n.setExpanded(!n.isExpanded());
                    filterNode();
                    this.notifyDataSetChanged();
                } else {
                    // 加载该节点下的工序 设置根节点的展开状态
                    n.setExpanded(true);
                    listener.returnData(allCache, all, position, all.get(position).getLevelId());
                }
            }
        }
    }

    /**
     * 控制节点的展开和折叠
     */
    private void filterNode() {
        all.clear();
        for (int i = 0; i < allCache.size(); i++) {
            Node n = allCache.get(i);
            if (!n.isParentCollapsed() || n.isRoot()) {
                all.add(n);
            }
        }
    }

    @Override
    public AppInfoHold onCreateViewHolder(ViewGroup parent, int viewType) {
        return new AppInfoHold(LayoutInflater.from(mActivity).inflate(R.layout.item_process_manager, parent, false));
    }

    @Override
    public void onBindViewHolder(AppInfoHold holder, int position) {
        // 得到当前节点
        Node n = all.get(position);
        if (n != null) {
            holder.btnProcessTitle.setOnClickListener(new onClick(position));

            // 显示文本
            holder.btnProcessTitle.setText(n.getLevelName());

            if (StrUtil.equals("0", n.getFolderFlag())) {
                if (n.isExpanded()) {
                    Drawable drawable = ContextCompat.getDrawable(mActivity, R.drawable.open);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    holder.btnProcessTitle.setCompoundDrawables(drawable, null, null, null);
                } else {
                    Drawable drawable = ContextCompat.getDrawable(mActivity, R.drawable.fold);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    holder.btnProcessTitle.setCompoundDrawables(drawable, null, null, null);
                }
            } else {
                Drawable drawable = ContextCompat.getDrawable(mActivity, R.drawable.progress_state);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                holder.btnProcessTitle.setCompoundDrawables(drawable, null, null, null);
            }

            // 控制缩进
            if (n.getLevel() != 1) {
                holder.btnProcessTitle.setPadding(50 * (n.getLevel() - 1), 3, 3, 3);
            } else {
                holder.btnProcessTitle.setPadding(0, 3, 3, 3);
            }
        }
    }

    @Override
    public int getItemCount() {
        return all == null ? 0 : all.size();
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
            switch (v.getId()) {
                case R.id.btnProcessTitle:
                    ExpandOrCollapse(point);
                    break;
            }
        }
    }

    /**
     * 添加
     *
     * @param point
     */
    public void addBtn(final int point) {
        new RejectDialog(mActivity, new ReportListener() {
            @Override
            public void returnUserId(String userId) {
                add(all.get(point), point, userId);
            }
        }, "提示", "请输入层级名称", "取消", "添加").show();
    }

    /**
     * 删除
     *
     * @param point
     */
    public void deleteBtn(final int point) {
        new PromptDialog(mActivity, new PromptListener() {
            @Override
            public void returnTrueOrFalse(boolean trueOrFalse) {
                if (trueOrFalse) {
                    delete(all.get(point), point);
                }
            }
        }, "提示", "数据删除无法恢复，您确认删除么？", "取消", "确认").show();
    }

    /**
     * 添加
     *
     * @param node
     * @param point
     * @param levelName
     */
    private void add(final Node node, final int point, String levelName) {
        LoadingUtils.showLoading(mActivity);
        JSONObject obj = new JSONObject();
        final String[] str = levelName.split("&&");
        obj.put("levelName", str[1]);
        if (StrUtil.equals("0", str[0])) {
            obj.put("parentId", node.getParentId());
            if (node.getParent().isRoot()) {
                obj.put("parentIdAll", "");
            } else {
                obj.put("parentIdAll", node.getParent().getParentIdAll());
            }
        } else {
            obj.put("parentId", node.getLevelId());
            obj.put("parentIdAll", node.getParentIdAll());
        }
        Request request = ChildThreadUtil.getRequest(mActivity, ConstantsUtil.addGxLevel, obj.toString());
        ConstantsUtil.okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                ChildThreadUtil.toastMsgHidden(mActivity, mActivity.getString(R.string.server_exception));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String jsonData = response.body().string().toString();
                if (JsonUtils.isGoodJson(jsonData)) {
                    Gson gson = new Gson();
                    BaseModel model = gson.fromJson(jsonData, BaseModel.class);
                    if (model.isSuccess()) {
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                JSONObject obj = new JSONObject(jsonData);
                                obj = new JSONObject(obj.getObj("data").toString());
                                Node n = new Node();
                                n.setLevelId((String) obj.getObj("levelId", ""));
                                n.setLevelName((String) obj.getObj("levelName", ""));
                                n.setParentId((String) obj.getObj("parentId", ""));
                                n.setFolderFlag((String) obj.getObj("folderFlag", ""));
                                n.setExpanded(false);
                                n.setLoading(false);
                                n.setChoice(false);

                                if (StrUtil.equals("0", str[0])) {
                                    n.setParent(node.getParent());
                                    allCache.add(point, n);
                                    all.add(point, n);
                                    notifyItemInserted(point);
                                } else {
                                    n.setParent(node);
                                    node.setExpanded(true);
                                    node.setLoading(true);
                                    node.setChoice(true);

                                    List<Node> nodeList = node.getChildren();
                                    nodeList.add(n);
                                    all.get(point).setChildren(nodeList);
                                    allCache.get(point).setChildren(nodeList);

                                    allCache.add(point + 1, n);
                                    all.add(point + 1, n);

                                    notifyItemInserted(point + 1);
                                }
                                LoadingUtils.hideLoading();
                            }
                        });
                    } else {
                        ChildThreadUtil.checkTokenHidden(mActivity, model.getMessage(), model.getCode());
                    }
                } else {
                    ChildThreadUtil.toastMsgHidden(mActivity, mActivity.getString(R.string.json_error));
                }
            }
        });
    }

    /**
     * 删除
     *
     * @param node
     * @param point
     */
    private void delete(final Node node, final int point) {
        LoadingUtils.showLoading(mActivity);
        List<Map<String, String>> mapList = new ArrayList<>();
        Map<String, String> map = new HashMap<>();
        map.put("levelId", node.getLevelId());
        mapList.add(map);
        Request request = ChildThreadUtil.getRequest(mActivity, ConstantsUtil.deleteGxLevel, new Gson().toJson(mapList));
        ConstantsUtil.okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                ChildThreadUtil.toastMsgHidden(mActivity, mActivity.getString(R.string.server_exception));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String jsonData = response.body().string().toString();
                if (JsonUtils.isGoodJson(jsonData)) {
                    Gson gson = new Gson();
                    BaseModel model = gson.fromJson(jsonData, BaseModel.class);
                    if (model.isSuccess()) {
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                allCache.remove(point);
                                all.remove(point);
                                notifyItemRemoved(point);
                                LoadingUtils.hideLoading();
                            }
                        });
                    } else {
                        ChildThreadUtil.checkTokenHidden(mActivity, model.getMessage(), model.getCode());
                    }
                } else {
                    ChildThreadUtil.toastMsgHidden(mActivity, mActivity.getString(R.string.json_error));
                }
            }
        });
    }

    /**
     * 容纳器
     */
    public class AppInfoHold extends RecyclerView.ViewHolder {
        private Button btnProcessTitle;

        public AppInfoHold(View itemView) {
            super(itemView);
            btnProcessTitle = (Button) itemView.findViewById(R.id.btnProcessTitle);
        }
    }

}
