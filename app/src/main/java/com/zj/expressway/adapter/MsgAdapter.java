package com.zj.expressway.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zj.expressway.R;
import com.zj.expressway.activity.ContractorDetailsActivity;
import com.zj.expressway.activity.ToDoDetailsActivity;
import com.zj.expressway.bean.WorkingBean;
import com.zj.expressway.utils.ConstantsUtil;
import com.zj.expressway.utils.SpUtil;

import java.util.List;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;

/**
 * Created by HaiJun on 2018/6/11 17:07
 * 消息列表适配器
 */
public class MsgAdapter extends RecyclerView.Adapter<MsgAdapter.MsgHolder> {
    private Activity mContext;
    private List<WorkingBean> workingBeanList;

    public MsgAdapter(Context mContext, List<WorkingBean> workingBeanList) {
        this.mContext = (Activity) mContext;
        this.workingBeanList = workingBeanList;
    }

    @Override
    public MsgHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MsgHolder(LayoutInflater.from(mContext).inflate(R.layout.item_msg, parent, false));
    }

    @Override
    public void onBindViewHolder(MsgHolder holder, int position) {
        final WorkingBean workingBean = workingBeanList.get(position);
        String ready = StrUtil.equals(workingBean.getIsRead(), "1") ? "已读" : "未读";
        holder.txtTitle.setText(workingBean.getCreateUserName() + "(" + ready + ")");
        holder.txtDate.setText(DateUtil.formatDateTime(DateUtil.date(System.currentTimeMillis())));
        //holder.txtContext.setText(workingBean.getContent().contains("进入app") ? workingBean.getContent().replace("进入app", "点击") : workingBean.getContent());
        holder.txtContext.setText(workingBean.getLevelNameAll());
        holder.txtContext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if (workingBean.getFlowId().equals("zxHwZlTrouble")) {
                    intent = new Intent(mContext, ToDoDetailsActivity.class);
                    SpUtil.put(mContext, ConstantsUtil.PROCESS_LIST_TYPE, "2");
                } else if (workingBean.getFlowId().equals("zxHwAqHiddenDanger")) {
                    intent = new Intent(mContext, ToDoDetailsActivity.class);
                    SpUtil.put(mContext, ConstantsUtil.PROCESS_LIST_TYPE, "3");
                } else {
                    intent = new Intent(mContext, ContractorDetailsActivity.class);
                }
                intent.putExtra("flowId", workingBean.getFlowId() == null ? "" : workingBean.getFlowId());
                intent.putExtra("workId", workingBean.getWorkId() == null ? "" : workingBean.getWorkId());
                intent.putExtra("mainTablePrimaryId", workingBean.getMainTablePrimaryId() == null ? "" : workingBean.getMainTablePrimaryId());
                intent.putExtra("isToDo", true);
                intent.putExtra("isLocalAdd", workingBean.getIsLocalAdd());
                intent.putExtra("isPopTakePhoto", false);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return workingBeanList == null ? 0 : workingBeanList.size();
    }

    public class MsgHolder extends RecyclerView.ViewHolder {
        private TextView txtDate;
        private TextView txtTitle;
        private TextView txtContext;

        public MsgHolder(View itemView) {
            super(itemView);
            txtDate = (TextView) itemView.findViewById(R.id.txtDate);
            txtTitle = (TextView) itemView.findViewById(R.id.txtTitle);
            txtContext = (TextView) itemView.findViewById(R.id.txtContext);
        }
    }
}
