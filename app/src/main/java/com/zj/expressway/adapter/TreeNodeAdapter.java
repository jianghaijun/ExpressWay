package com.zj.expressway.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.zj.expressway.R;
import com.zj.expressway.activity.AddProcessActivity;
import com.zj.expressway.activity.MainActivity;
import com.zj.expressway.base.BaseModel;
import com.zj.expressway.bean.ContractorBean;
import com.zj.expressway.bean.WorkingBean;
import com.zj.expressway.dialog.PromptDialog;
import com.zj.expressway.dialog.RejectDialog;
import com.zj.expressway.listener.ContractorListener;
import com.zj.expressway.listener.PromptListener;
import com.zj.expressway.listener.ReportListener;
import com.zj.expressway.tree.Node;
import com.zj.expressway.utils.ChildThreadUtil;
import com.zj.expressway.utils.ConstantsUtil;
import com.zj.expressway.utils.JsonUtils;
import com.zj.expressway.utils.JudgeNetworkIsAvailable;
import com.zj.expressway.utils.LoadingUtils;
import com.zj.expressway.utils.ScreenManagerUtil;
import com.zj.expressway.utils.SpUtil;
import com.zj.expressway.utils.ToastUtil;
import com.zj.expressway.view.SwipeMenuLayout;

import org.litepal.crud.DataSupport;

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
 * Created by HaiJun on 2018/6/11 17:13
 * 无限树形图适配器
 */
public class TreeNodeAdapter extends RecyclerView.Adapter<TreeNodeAdapter.TreeNodeHolder> {
    public List<Node> allCache;
    public List<Node> all;
    private int expandedIcon = -1;
    private int collapsedIcon = -1;
    private Activity mContext;
    private Node rootNode;
    private List<String> nodeName = new ArrayList<>();
    private ContractorListener listener;
    private String processType;
    private boolean showSelect;

    /**
     * @param mContext
     * @param all
     * @param allCache
     * @param listener
     */
    public TreeNodeAdapter(Activity mContext, List<Node> all, List<Node> allCache, ContractorListener listener) {
        this.mContext = mContext;
        this.listener = listener;
        this.all = all;
        this.allCache = allCache;
        processType = (String) SpUtil.get(mContext, ConstantsUtil.PROCESS_LIST_TYPE, "1");
        showSelect = (boolean) SpUtil.get(mContext, "showSelectBtn", true);
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

    /**
     * 设置展开和折叠状态图标
     *
     * @param expandedIcon  展开时图标
     * @param collapsedIcon 折叠时图标
     */
    public void setExpandedCollapsedIcon(int expandedIcon, int collapsedIcon) {
        this.expandedIcon = expandedIcon;
        this.collapsedIcon = collapsedIcon;
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
     * 获取节点的根节点
     *
     * @param node
     * @return
     */
    private void getNodeRootNode(Node node) {
        rootNode = node.getParent();
        if (rootNode == null) {
            return;
        }

        if (rootNode.isRoot()) {
            rootNode = node;
        } else {
            nodeName.add(rootNode.getLevelName());
            getNodeRootNode(rootNode);
        }
    }

    /**
     * 控制节点的展开和收缩
     *
     * @param position
     */
    public void ExpandOrCollapse(final int position) {
        Node n = all.get(position);
        if (n != null) {
            // 是否是文件夹（文件夹继续展开---工序进入上传照片界面）0:不是文件夹 1：是文件夹
            if (n.getFolderFlag().equals("0")) {
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
            } else {
                for (Node node : all) {
                    node.setChoice(false);
                }
                n.setChoice(true);
                SpUtil.put(mContext, "selectProcess", position);
                notifyDataSetChanged();
            }
        }
    }

    /**
     * 选中工序--->确认
     *
     * @param position
     */
    public void selectProcess(int position) {
        if (position == -1) {
            ToastUtil.showShort(mContext, "请先选择工序！");
            return;
        }

        Node n = all.get(position);
        nodeName.clear();
        nodeName.add(n.getLevelName());
        getNodeRootNode(n);
        StringBuffer sb = new StringBuffer();
        int len = nodeName.size() - 1;
        for (int i = len; i >= 0; i--) {
            String name = nodeName.get(i);
            if (name.contains("(")) {
                name = name.substring(0, name.lastIndexOf("("));
            }
            if (i != 0) {
                sb.append(name.trim() + "→");
            } else {
                sb.append(name.trim());
            }
        }
        Intent intent = new Intent();
        intent.putExtra("procedureName", sb.toString());
        intent.putExtra("levelId", n.getLevelId());
        intent.putExtra("levelIdAll", n.getParentIdAll());
        mContext.setResult(Activity.RESULT_OK, intent);
        mContext.finish();
    }

    /**
     * 选中工序--->确认
     *
     * @param position
     */
    public String getProcessPath(int position) {
        if (position == -1) {
            return "";
        }

        Node n = all.get(position);
        nodeName.clear();
        nodeName.add(n.getLevelName());
        getNodeRootNode(n);
        StringBuffer sb = new StringBuffer();
        int len = nodeName.size() - 1;
        for (int i = len; i >= 0; i--) {
            String name = nodeName.get(i);
            if (name.contains("(")) {
                name = name.substring(0, name.lastIndexOf("("));
            }
            if (i != 0) {
                sb.append(name.trim() + "→");
            } else {
                sb.append(name.trim());
            }
        }
        return sb.toString();
    }

    @Override
    public TreeNodeAdapter.TreeNodeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TreeNodeAdapter.TreeNodeHolder(LayoutInflater.from(mContext).inflate(R.layout.item_contractor, parent, false));
    }

    @Override
    public void onBindViewHolder(final TreeNodeAdapter.TreeNodeHolder holder, final int position) {
        // 得到当前节点
        final Node n = all.get(position);

        if (n != null) {
            // 显示文本
            String roleName = n.getLevelName();
            // 去掉节点上该节点下有多少工序，是否已完成
            holder.txtTitle.setText(roleName == null || "null".equals(roleName) ? "" : roleName);
            if (!n.getFolderFlag().equals("0")) {
                // 是叶节点 不显示展开和折叠状态图标
                holder.imgViewState.setVisibility(View.GONE);
                holder.imgViewNode.setVisibility(View.VISIBLE);
            } else {
                holder.imgViewState.setVisibility(View.VISIBLE);
                holder.imgViewNode.setVisibility(View.GONE);
            }

            /* 单击时控制子节点展开和折叠,状态图标改变  */
            if (n.isExpanded()) {
                if (expandedIcon != -1) {
                    holder.imgViewState.setImageDrawable(ContextCompat.getDrawable(mContext, expandedIcon));
                }
            } else {
                if (collapsedIcon != -1) {
                    holder.imgViewState.setImageDrawable(ContextCompat.getDrawable(mContext, collapsedIcon));
                }
            }

            if (showSelect) {
                if (n.isChoice()) {
                    holder.imgViewSelect.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.radio_check));
                } else {
                    holder.imgViewSelect.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.radio_un_check));
                }
            } else {
                holder.imgViewSelect.setVisibility(View.GONE);
            }

            // 展开收缩
            holder.llMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ExpandOrCollapse(position);
                }
            });

            // 只用工序才能长按添加
            if (processType.equals("1")) {
                // 长按事件
                holder.llMain.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        new PromptDialog(mContext, new PromptListener() {
                            @Override
                            public void returnTrueOrFalse(boolean trueOrFalse) {
                                if (trueOrFalse) {
                                    boolean isSync = (boolean) SpUtil.get(mContext, "isSync", false);
                                    if (isSync) {
                                        Intent intent = new Intent(mContext, AddProcessActivity.class);
                                        intent.putExtra("position", position);
                                        intent.putExtra("levelId", "update");
                                        intent.putExtra("levelName", "");
                                        mContext.startActivityForResult(intent, 1005);
                                    } else {
                                        new PromptDialog(mContext, new PromptListener() {
                                            @Override
                                            public void returnTrueOrFalse(boolean trueOrFalse) {
                                                if (trueOrFalse) {
                                                    ConstantsUtil.jumpPersonalInfo = true;
                                                    ScreenManagerUtil.popAllActivityExceptOne(MainActivity.class);
                                                }
                                            }
                                        }, "提示", "是否跳转到个人中心页同步工序字典后再进行添加？", "否", "是").show();
                                    }
                                }
                            }
                        }, "提示", "是否添加新层级？", "否", "是").show();
                        return true;
                    }
                });
            }

            // 添加工序
            holder.btnAddLevel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((SwipeMenuLayout) holder.itemView).quickClose();
                    if (!JudgeNetworkIsAvailable.isNetworkAvailable(mContext)) {
                        ToastUtil.showShort(mContext, mContext.getString(R.string.not_network));
                    } else if (n.isLocalAdd()) {
                        // 防止服务器没有父节点导致异常问题
                        new PromptDialog(mContext, new PromptListener() {
                            @Override
                            public void returnTrueOrFalse(boolean trueOrFalse) {
                                if (trueOrFalse) {
                                    ConstantsUtil.jumpPersonalInfo = true;
                                    ScreenManagerUtil.popAllActivityExceptOne(MainActivity.class);
                                }
                            }
                        }, "提示", "是否跳转到个人中心页将本地工序提交至服务器后再进行添加？", "否", "是").show();
                    } else {
                        addBtn(position);
                    }
                }
            });

            // 编辑功能
            if (n.isLocalAdd() && StrUtil.equals("1", n.getLevelLevel())) {
                holder.btnUpdateLevel.setVisibility(View.VISIBLE);
                holder.btnUpdateLevel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, AddProcessActivity.class);
                        intent.putExtra("position", position);
                        intent.putExtra("levelId", n.getLevelId());
                        intent.putExtra("levelName", n.getLevelName());
                        mContext.startActivityForResult(intent, 1006);
                    }
                });
            }

            // 删除工序
            holder.btnDeleteLevel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((SwipeMenuLayout) holder.itemView).quickClose();
                    if (n.isLocalAdd()) {
                        new PromptDialog(mContext, new PromptListener() {
                            @Override
                            public void returnTrueOrFalse(boolean trueOrFalse) {
                                if (trueOrFalse) {
                                    DataSupport.deleteAll(ContractorBean.class, "levelId=?", n.getLevelId());
                                    DataSupport.deleteAll(ContractorBean.class, "parentId=?", n.getLevelId());
                                    DataSupport.deleteAll(WorkingBean.class, "levelId=?", n.getLevelId());
                                    List<Node> nodeList = n.getChildren();

                                    allCache.remove(position);
                                    all.remove(position);

                                    if (n.isExpanded() && nodeList != null) {
                                        removeLevel(nodeList);
                                    }
                                    notifyDataSetChanged();
                                }
                            }
                        }, "提示", "数据删除无法恢复，您确认删除么？", "取消", "确认").show();
                    } else {
                        deleteBtn(position);
                    }
                }
            });

            // 单选
            holder.rlRight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (Node node : all) {
                        node.setChoice(false);
                    }
                    n.setChoice(true);
                    SpUtil.put(mContext, "selectProcess", position);
                    notifyDataSetChanged();
                }
            });

            // 控制缩进
            if (n.getLevel() != 1) {
                holder.rlItemTree.setPadding(50 * (n.getLevel() - 1), 3, 3, 3);
            } else {
                holder.rlItemTree.setPadding(0 * (n.getLevel() - 1), 3, 3, 3);
            }
        }
    }

    private void removeLevel(List<Node> nodeList) {
        for (Node n : nodeList) {
            allCache.remove(n);
            all.remove(n);
            if (n.getChildren() != null) {
                removeLevel(n.getChildren());
            }
        }
    }

    @Override
    public int getItemCount() {
        return all == null ? 0 : all.size();
    }

    /**
     * 添加
     *
     * @param point
     */
    private void addBtn(final int point) {
        new RejectDialog(mContext, new ReportListener() {
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
    private void deleteBtn(final int point) {
        new PromptDialog(mContext, new PromptListener() {
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
        LoadingUtils.showLoading(mContext);
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
        String url;
        if (StrUtil.equals("1", processType)) {
            url = ConstantsUtil.addGxLevel;
        } else if (StrUtil.equals("2", processType)) {
            url = ConstantsUtil.addZlLevel;
        } else {
            url = ConstantsUtil.addAqLevel;
        }

        Request request = ChildThreadUtil.getRequest(mContext, url, obj.toString());
        ConstantsUtil.okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                ChildThreadUtil.toastMsgHidden(mContext, mContext.getString(R.string.server_exception));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String jsonData = response.body().string().toString();
                if (JsonUtils.isGoodJson(jsonData)) {
                    Gson gson = new Gson();
                    BaseModel model = gson.fromJson(jsonData, BaseModel.class);
                    if (model.isSuccess()) {
                        mContext.runOnUiThread(new Runnable() {
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

                                    all.get(point).setFolderFlag("0");
                                    all.get(point).setExpanded(true);
                                    allCache.get(point).setFolderFlag("0");
                                    allCache.get(point).setExpanded(true);

                                    allCache.add(point + 1, n);
                                    all.add(point + 1, n);

                                    notifyDataSetChanged();
                                    //notifyItemInserted(point + 1);
                                }
                                LoadingUtils.hideLoading();
                            }
                        });
                    } else {
                        ChildThreadUtil.checkTokenHidden(mContext, model.getMessage(), model.getCode());
                    }
                } else {
                    ChildThreadUtil.toastMsgHidden(mContext, mContext.getString(R.string.json_error));
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
        LoadingUtils.showLoading(mContext);
        List<Map<String, String>> mapList = new ArrayList<>();
        Map<String, String> map = new HashMap<>();
        map.put("levelId", node.getLevelId());
        mapList.add(map);
        String url;
        if (StrUtil.equals("1", processType)) {
            url = ConstantsUtil.deleteGxLevel;
        } else if (StrUtil.equals("2", processType)) {
            url = ConstantsUtil.deleteZlLevel;
        } else {
            url = ConstantsUtil.deleteAqLevel;
        }

        Request request = ChildThreadUtil.getRequest(mContext, url, new Gson().toJson(mapList));
        ConstantsUtil.okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                ChildThreadUtil.toastMsgHidden(mContext, mContext.getString(R.string.server_exception));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String jsonData = response.body().string().toString();
                if (JsonUtils.isGoodJson(jsonData)) {
                    Gson gson = new Gson();
                    BaseModel model = gson.fromJson(jsonData, BaseModel.class);
                    if (model.isSuccess()) {
                        mContext.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                List<Node> nodeList = node.getChildren();
                                allCache.remove(point);
                                all.remove(point);

                                if (node.isExpanded() && nodeList != null) {
                                    removeLevel(nodeList);
                                }
                                notifyDataSetChanged();
                                LoadingUtils.hideLoading();
                            }
                        });
                    } else {
                        ChildThreadUtil.checkTokenHidden(mContext, model.getMessage(), model.getCode());
                    }
                } else {
                    ChildThreadUtil.toastMsgHidden(mContext, mContext.getString(R.string.json_error));
                }
            }
        });
    }

    /**
     * 列表项控件集合
     */
    public class TreeNodeHolder extends RecyclerView.ViewHolder {
        private ImageView imgViewState;
        private ImageView imgViewNode;
        private ImageView imgViewSelect;
        private TextView txtTitle;
        private Button btnAddLevel;
        private Button btnUpdateLevel;
        private Button btnDeleteLevel;
        private RelativeLayout rlItemTree;
        private LinearLayout llMain;
        private RelativeLayout rlRight;

        public TreeNodeHolder(View itemView) {
            super(itemView);
            imgViewState = (ImageView) itemView.findViewById(R.id.imgViewState);
            imgViewNode = (ImageView) itemView.findViewById(R.id.imgViewNode);
            imgViewSelect = (ImageView) itemView.findViewById(R.id.imgViewSelect);
            txtTitle = (TextView) itemView.findViewById(R.id.txtTitle);
            btnAddLevel = (Button) itemView.findViewById(R.id.btnAddLevel);
            btnUpdateLevel = (Button) itemView.findViewById(R.id.btnUpdateLevel);
            btnDeleteLevel = (Button) itemView.findViewById(R.id.btnDeleteLevel);
            rlItemTree = (RelativeLayout) itemView.findViewById(R.id.rlItemTree);
            rlRight = (RelativeLayout) itemView.findViewById(R.id.rlRight);
            llMain = (LinearLayout) itemView.findViewById(R.id.llMain);
        }
    }
}