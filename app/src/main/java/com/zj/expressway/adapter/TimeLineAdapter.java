package com.zj.expressway.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.vipulasri.timelineview.TimelineView;
import com.zj.expressway.R;
import com.zj.expressway.model.TimeLineModel;
import com.zj.expressway.utils.OrderStatusUtil;
import com.zj.expressway.utils.VectorDrawableUtils;

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
 *       Created by HaiJun on 2018/6/11 17:09
 *       普通时间轴适配器
 */
public class TimeLineAdapter extends RecyclerView.Adapter<TimeLineAdapter.TimeLineViewHolder> {
    private List<TimeLineModel> mDataList;
    private Context mContext;

    public TimeLineAdapter(List<TimeLineModel> mDataList) {
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
        TimeLineModel timeLineModel = mDataList.get(position);
        if (timeLineModel.getStatus() == OrderStatusUtil.INACTIVE) {
            holder.mTimelineView.setMarker(VectorDrawableUtils.getDrawable(mContext, R.drawable.marker_inactive_point, android.R.color.darker_gray));
        } else if (timeLineModel.getStatus() == OrderStatusUtil.ACTIVE) {
            holder.mTimelineView.setMarker(VectorDrawableUtils.getDrawable(mContext, R.drawable.marker_active_point, R.color.colorAccent));
        } else {
            holder.mTimelineView.setMarker(ContextCompat.getDrawable(mContext, R.drawable.marker_poinit), ContextCompat.getColor(mContext, R.color.main_check_bg));
        }

        holder.mDate.setText(mDataList.get(position).getDate());
        holder.mMessage.setText(mDataList.get(position).getMessage());
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return TimelineView.getTimeLineViewType(position, getItemCount());
    }

    public class TimeLineViewHolder extends RecyclerView.ViewHolder {
        private TimelineView mTimelineView;
        private TextView mDate;
        private TextView mMessage;

        public TimeLineViewHolder(View itemView, int viewType) {
            super(itemView);
            mTimelineView = (TimelineView) itemView.findViewById(R.id.timeMarker);
            mDate = (TextView) itemView.findViewById(R.id.txtTimeLineDate);
            mMessage = (TextView) itemView.findViewById(R.id.txtTimeLineTitle);
            mTimelineView.initLine(viewType);
        }
    }

}
