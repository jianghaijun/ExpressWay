package com.zj.expressway.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.zj.expressway.R;
import com.zj.expressway.tree.Node;
import com.zj.expressway.utils.ConstantsUtil;
import com.zj.expressway.utils.SpUtil;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * 树数据源构造器
 */
public class PersonnelTreeAdapter extends BaseAdapter {
    private LayoutInflater lif;
    private Activity mContext;
    private List<Node> rootNode = new ArrayList<>();
    private List<Node> oldNode = new ArrayList<>();

    /**
     * @param mContext
     * @param rootNode
     */
    public PersonnelTreeAdapter(Activity mContext, List<Node> rootNode) {
        this.lif = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mContext = mContext;
        for (Node node : rootNode) {
            this.rootNode.add(node);
            oldNode.add(node);
        }
    }

    /**
     * 节点点击事件
     * @param node
     */
    public void ExpandOrCollapse(Node node) {
        if (node.getFolderFlag().equals("2")) {
            // 是人员
            for (Node n : rootNode) {
                n.setCanClick(false);
                if (n.getLevelId().equals(node.getLevelId())) {
                    n.setCanClick(true);
                    SpUtil.remove(mContext, ConstantsUtil.SELECT_USER_ID);
                    SpUtil.put(mContext, ConstantsUtil.SELECT_USER_ID, n.getLevelId());
                }
            }
        } else {
            // 部门
            //rootNode.clear();
            rootNode = node.getChildren();
        }
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return rootNode.size();
    }

    @Override
    public Object getItem(int position) {
        return rootNode.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view == null) {
            view = this.lif.inflate(R.layout.item_personnel, null);
            holder = new ViewHolder();
            x.view().inject(holder, view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        // 得到当前节点
        Node n = rootNode.get(position);
        holder.levelId = n.getLevelId();
        // 显示文本
        holder.txtTitle.setText(n.getLevelName());
        if (n.getFolderFlag().equals("2")) { // 人员
            // 显示选中未选中图标、隐藏右箭头
            holder.ivSelectState.setVisibility(View.VISIBLE);
            holder.ivType.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.person));
            holder.ivDetails.setVisibility(View.GONE);
        } else {
            // 隐藏选中未选中图标、显示右箭头
            holder.ivSelectState.setVisibility(View.GONE);
            holder.ivType.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.folder));
            holder.ivDetails.setVisibility(View.VISIBLE);
        }

        String selectUserId = (String) SpUtil.get(mContext, ConstantsUtil.SELECT_USER_ID, "");
        n.setCanClick(selectUserId.equals(n.getLevelId()));

        // 是否选中
        if (n.isCanClick()) {
            holder.ivSelectState.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.radio_check));
        } else {
            holder.ivSelectState.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.radio_un_check));
        }
        return view;
    }

    /**
     * 列表项控件集合
     */
    public class ViewHolder {
        // 选中图标
        @ViewInject(R.id.ivSelectState)
        private ImageView ivSelectState;
        // 类型
        @ViewInject(R.id.ivType)
        private ImageView ivType;
        // 右箭头
        @ViewInject(R.id.ivDetails)
        private ImageView ivDetails;
        // 标题
        @ViewInject(R.id.txtTitle)
        private TextView txtTitle;
        public String levelId;
    }

}