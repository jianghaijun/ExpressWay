package com.zj.expressway.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zj.expressway.R;
import com.zj.expressway.activity.ContractorDetailsActivity;
import com.zj.expressway.activity.EditScrollPhotoActivity;
import com.zj.expressway.activity.ToDoDetailsActivity;
import com.zj.expressway.bean.MsgBean;
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
    private List<MsgBean> msgBeanList;

    public MsgAdapter(Context mContext, List<MsgBean> msgBeanList) {
        this.mContext = (Activity) mContext;
        this.msgBeanList = msgBeanList;
    }

    @Override
    public MsgHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MsgHolder(LayoutInflater.from(mContext).inflate(R.layout.item_msg, parent, false));
    }

    @Override
    public void onBindViewHolder(MsgHolder holder, int position) {
        final MsgBean msgBean = msgBeanList.get(position);
        String ready = StrUtil.equals(msgBean.getIsRead(), "1") ? "已读" : "未读";
        holder.txtTitle.setText(msgBean.getMsgTitle() + "(" + ready + ")");
        holder.txtDate.setText(DateUtil.formatDateTime(DateUtil.date(msgBean.getCreateTime())));
        holder.txtContext.setText(msgBean.getMsgContent());
        // 点击跳转
        holder.llMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, EditScrollPhotoActivity.class);
                intent.putExtra("url", ConstantsUtil.Scroll_Photo + "?" + "msgId=" + msgBean.getMsgId() + "&msgUserId=" + msgBean.getMsgUserId());
                intent.putExtra("title", "详情");
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return msgBeanList == null ? 0 : msgBeanList.size();
    }

    public class MsgHolder extends RecyclerView.ViewHolder {
        private TextView txtDate;
        private TextView txtTitle;
        private TextView txtContext;
        private LinearLayout llMain;

        public MsgHolder(View itemView) {
            super(itemView);
            txtDate = (TextView) itemView.findViewById(R.id.txtDate);
            txtTitle = (TextView) itemView.findViewById(R.id.txtTitle);
            txtContext = (TextView) itemView.findViewById(R.id.txtContext);
            llMain = (LinearLayout) itemView.findViewById(R.id.llMain);
        }
    }
}
