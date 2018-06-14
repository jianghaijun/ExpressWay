package com.zj.expressway.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zj.expressway.R;
import com.zj.expressway.bean.WorkingBean;

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
 *       Created by HaiJun on 2018/6/11 17:03
 *       瀑布流格式时间轴
 */
public class WaterfallFlowTimeLineAdapter extends RecyclerView.Adapter<WaterfallFlowTimeLineAdapter.ViewHolder> {
    private Context mContext;
    private List<WorkingBean> mList;

    public WaterfallFlowTimeLineAdapter(Context context, List<WorkingBean> list) {
        this.mContext = context;
        this.mList = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int resource;
        if (mList.size() == 1) {
            resource = R.layout.item_one_dot_time_line;
        } else {
            resource = R.layout.item_dot_time_line;
        }
        View view = LayoutInflater.from(mContext).inflate(resource, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.txtUser.setText(mList.get(position).getProcessName() + "   " + mList.get(position).getFlowName());
        holder.txtArriveDate.setText("到达时间：" + mList.get(position).getContent());
        holder.txtUseTime.setText("累计时长：" + mList.get(position).getProcessState());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtUser;
        TextView txtArriveDate;
        TextView txtUseTime;

        public ViewHolder(View view) {
            super(view);
            txtUser = (TextView) view.findViewById(R.id.txtUser);
            txtArriveDate = (TextView) view.findViewById(R.id.txtArriveDate);
            txtUseTime = (TextView) view.findViewById(R.id.txtUseTime);
        }
    }

}
