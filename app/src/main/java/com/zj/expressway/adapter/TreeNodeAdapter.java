package com.zj.expressway.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zj.expressway.R;
import com.zj.expressway.listener.ContractorListener;
import com.zj.expressway.tree.Node;
import com.zj.expressway.utils.SpUtil;
import com.zj.expressway.utils.ToastUtil;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

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
 *       Created by HaiJun on 2018/6/11 17:13
 *       无限树形图适配器
 */
public class TreeNodeAdapter extends BaseAdapter {
    private LayoutInflater lif;
    public List<Node> allCache = new ArrayList<>();
    public List<Node> all = new ArrayList<>();
    private int expandedIcon = -1;
    private int collapsedIcon = -1;
    private Activity mContext;
    private Node rootNode;
    private List<String> nodeName = new ArrayList<>();
    private ContractorListener listener;

    /**
     * @param mContext
     * @param rootNode
     */
    public TreeNodeAdapter(Activity mContext, Node rootNode, ContractorListener listener) {
        this.mContext = mContext;
        this.listener = listener;
        this.lif = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        addNode(rootNode);
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
                TreeNodeAdapter.this.notifyDataSetChanged();
            }
        }
    }

    /**
     * 选中工序--->确认
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
        mContext.setResult(Activity.RESULT_OK, intent);
        mContext.finish();
    }

    /**
     * 选中工序--->确认
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
    public int getCount() {
        return all.size();
    }

    @Override
    public Object getItem(int position) {
        return all.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            view = this.lif.inflate(R.layout.item_contractor, null);
            holder = new ViewHolder();
            x.view().inject(holder, view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

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

            if (n.isChoice()) {
                holder.imgViewSelect.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.btn_check));
            } else {
                holder.imgViewSelect.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.btn_un_check));
            }

            holder.rlRight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (Node node : all) {
                        node.setChoice(false);
                    }
                    n.setChoice(true);
                    SpUtil.put(mContext, "selectProcess", position);
                    TreeNodeAdapter.this.notifyDataSetChanged();
                }
            });

            // 控制缩进
            if (n.getLevel() != 1) {
                holder.rlItemTree.setPadding(50 * (n.getLevel() - 1), 3, 3, 3);
            } else {
                holder.rlItemTree.setPadding(0 * (n.getLevel() - 1), 3, 3, 3);
            }
        }
        return view;
    }

    /**
     * 列表项控件集合
     */
    private class ViewHolder {
        // 展开收缩图标
        @ViewInject(R.id.imgViewState)
        private ImageView imgViewState;
        @ViewInject(R.id.imgViewNode)
        private ImageView imgViewNode;
        @ViewInject(R.id.imgViewSelect)
        private ImageView imgViewSelect;

        // 标题
        @ViewInject(R.id.txtTitle)
        private TextView txtTitle;
        @ViewInject(R.id.rlItemTree)
        private RelativeLayout rlItemTree;
        @ViewInject(R.id.rlNodeState)
        private RelativeLayout rlNodeState;
        @ViewInject(R.id.rlRight)
        private RelativeLayout rlRight;
    }

}