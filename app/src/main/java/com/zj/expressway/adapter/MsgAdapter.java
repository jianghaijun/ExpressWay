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
import com.zj.expressway.base.BaseAdapter;
import com.zj.expressway.bean.WorkingBean;

import java.util.List;

import cn.hutool.core.date.DateUtil;

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
 *       Created by HaiJun on 2018/6/11 17:07
 *       消息列表适配器
 */
public class MsgAdapter extends BaseAdapter<List<WorkingBean>> {
    private Activity mContext;

    public MsgAdapter(Context mContext) {
        this.mContext = (Activity) mContext;
    }

    @Override
    public MsgHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MsgHolder(LayoutInflater.from(mContext).inflate(R.layout.item_msg, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((MsgHolder) holder).bind((WorkingBean) getDataSet().get(position));
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

        public void bind(final WorkingBean data) {
            String ready = data.getIsRead().equals("1") ? "已读" : "未读";
            txtTitle.setText(data.getCreateUserName() + "(" + ready + ")");
            txtDate.setText(DateUtil.formatDateTime(DateUtil.date(data.getSendTime())));
            txtContext.setText(data.getContent().contains("进入app") ? data.getContent().replace("进入app", "点击") : data.getContent());
            txtContext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent;
                    if (data.getFlowId().equals("zxHwZlHiddenDanger")) {
                        intent = new Intent(mContext, ToDoDetailsActivity.class);
                    } else if (data.getFlowId().equals("zxHwAqHiddenDanger")) {
                        intent = new Intent(mContext, ToDoDetailsActivity.class);
                    } else {
                        intent = new Intent(mContext, ContractorDetailsActivity.class);
                    }
                    intent.putExtra("flowId", data.getFlowId());
                    intent.putExtra("processId", data.getProcessId());
                    intent.putExtra("processState", data.getProcessState());
                    intent.putExtra("processPath", data.getLevelNameAll());
                    intent.putExtra("taskId", data.getTaskId());
                    intent.putExtra("isLocalAdd", data.getIsLocalAdd());
                    intent.putExtra("canCheck", data.getCanCheck());
                    intent.putExtra("isPopTakePhoto", data);
                    mContext.startActivity(intent);
                }
            });
        }
    }
}
