package com.zj.expressway.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.vipulasri.timelineview.TimelineView;
import com.zj.expressway.R;
import com.zj.expressway.bean.HistoryBean;
import com.zj.expressway.utils.DateUtils;

import java.util.List;

/**
 * Created by HaiJun on 2018/6/11 17:09
 * 普通时间轴适配器
 */
public class TimeLineAdapter extends RecyclerView.Adapter<TimeLineAdapter.TimeLineViewHolder> {
    private List<HistoryBean> mDataList;
    private Context mContext;

    public TimeLineAdapter(List<HistoryBean> mDataList) {
        this.mDataList = mDataList;
    }

    @Override
    public TimeLineViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = View.inflate(parent.getContext(), R.layout.item_time_line, null);
        return new TimeLineViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(TimeLineViewHolder holder, int position) {
        HistoryBean timeLineModel = mDataList.get(position);
        switch (timeLineModel.getHistoryFlag()) {
            // 已完成
            case "1":
                holder.mTimelineView.setMarker(ContextCompat.getDrawable(mContext, R.drawable.marker_poinit), ContextCompat.getColor(mContext, R.color.gray));
                holder.txtUserName.setTextColor(ContextCompat.getColor(mContext, R.color.black));
                holder.mDate.setTextColor(ContextCompat.getColor(mContext, R.color.gray));
                holder.mMessage.setTextColor(ContextCompat.getColor(mContext, R.color.gray));
                break;
            case "2":
                holder.mTimelineView.setMarker(ContextCompat.getDrawable(mContext, R.drawable.marker_poinit), ContextCompat.getColor(mContext, R.color.green));
                break;
            case "3":
                holder.mTimelineView.setMarker(ContextCompat.getDrawable(mContext, R.drawable.marker_poinit), ContextCompat.getColor(mContext, R.color.red));
                break;
        }
        holder.txtUserName.setText(timeLineModel.getNodeName());
        holder.mDate.setText(timeLineModel.getRealName());
        holder.mMessage.setText("到达时间：" + DateUtils.setDataToStr(timeLineModel.getActionTime()));
        //holder.mMessage.setText("累计时长：" + timeLineModel.getDoTimeShow());
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return TimelineView.getTimeLineViewType(position, getItemCount());
    }

    public class TimeLineViewHolder extends RecyclerView.ViewHolder {
        private TimelineView mTimelineView;
        private TextView txtUserName;
        private TextView mDate;
        private TextView mMessage;

        public TimeLineViewHolder(View itemView, int viewType) {
            super(itemView);
            mTimelineView = (TimelineView) itemView.findViewById(R.id.timeMarker);
            txtUserName = (TextView) itemView.findViewById(R.id.txtUserName);
            mDate = (TextView) itemView.findViewById(R.id.txtTimeLineDate);
            mMessage = (TextView) itemView.findViewById(R.id.txtTimeLineTitle);
            mTimelineView.initLine(viewType);
        }
    }

}
