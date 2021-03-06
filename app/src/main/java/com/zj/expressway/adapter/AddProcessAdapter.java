package com.zj.expressway.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.zj.expressway.R;
import com.zj.expressway.activity.ToDoDetailsActivity;
import com.zj.expressway.bean.PhotosBean;
import com.zj.expressway.bean.WorkingBean;
import com.zj.expressway.dialog.PromptDialog;
import com.zj.expressway.listener.PromptListener;
import com.zj.expressway.utils.DateUtils;
import com.zj.expressway.utils.ToastUtil;

import java.util.List;

import cn.hutool.core.util.StrUtil;

/**
 * _ooOoo_
 * o8888888o
 * 88" . "88
 * (| -_- |)
 * O\  =  /O
 * ____/`---'\____
 * .'  \\|     |//  `.
 * /  \\|||  :  |||//  \
 * /  _||||| -:- |||||-  \
 * |   | \\\  -  /// |   |
 * | \_|  ''\---/''  |   |
 * \  .-\__  `-`  ___/-. /
 * ___`. .'  /--.--\  `. . __
 * ."" '<  `.___\_<|>_/___.'  >'"".
 * | | :  `- \`.;`\ _ /`;.`/ - ` : | |
 * \  \ `-.   \_ __\ /__ _/   .-` /  /
 * ======`-.____`-.___\_____/___.-`____.-'======
 * `=---='
 * ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
 * 佛祖保佑       永无BUG
 * Created by HaiJun on 2018/6/11 17:11
 * 工序列表适配器
 */
public class AddProcessAdapter extends RecyclerView.Adapter<AddProcessAdapter.ProcessHolder> {
    private Activity mContext;
    private List<WorkingBean> workingBeanList;

    /**
     * 重载
     *
     * @param mContext
     * @param workingBeanList
     */
    public AddProcessAdapter(Context mContext, List<WorkingBean> workingBeanList) {
        this.mContext = (Activity) mContext;
        this.workingBeanList = workingBeanList;
    }

    @Override
    public ProcessHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ProcessHolder(LayoutInflater.from(mContext).inflate(R.layout.item_add_procedure, parent, false));
    }

    @Override
    public void onBindViewHolder(final ProcessHolder holder, final int position) {
        holder.bind(workingBeanList.get(position));
    }

    @Override
    public int getItemCount() {
        return workingBeanList == null ? 0 : workingBeanList.size();
    }

    public class ProcessHolder extends RecyclerView.ViewHolder {
        private TextView btnLevel;
        private TextView txtTitle;
        private TextView btnRequirements;
        private TextView btnEnterTime;
        private TextView btnDelete;


        public ProcessHolder(View itemView) {
            super(itemView);
            btnLevel = (TextView) itemView.findViewById(R.id.btnLevel);
            txtTitle = (TextView) itemView.findViewById(R.id.txtTitle);
            btnRequirements = (TextView) itemView.findViewById(R.id.btnRequirements);
            btnEnterTime = (TextView) itemView.findViewById(R.id.btnEnterTime);
            btnDelete = (TextView) itemView.findViewById(R.id.btnDelete);
        }

        public void bind(WorkingBean data) {
            switch (StrUtil.isEmpty(data.getDangerLevel()) ? data.getTroubleLevel() : data.getDangerLevel()) {
                case "1":
                    btnLevel.setText("一般");
                    break;
                case "2":
                    btnLevel.setText("严重");
                    break;
                case "3":
                    btnLevel.setText("紧要");
                    break;
            }
            txtTitle.setText(StrUtil.isEmpty(data.getTroubleTitle()) ? data.getDangerTitle() : data.getTroubleTitle());
            btnRequirements.setText(StrUtil.isEmpty(data.getTroubleRequire()) ? data.getDangerRequire() : data.getTroubleRequire());
            btnEnterTime.setText(DateUtils.setDataToStr(data.getCreateTime()));
            btnRequirements.setOnClickListener(new onClick(data));
            btnDelete.setOnClickListener(new onClick(data));
        }
    }

    /**
     * 点击事件
     */
    private class onClick implements View.OnClickListener {
        private WorkingBean workingBean;

        public onClick(WorkingBean workingBean) {
            this.workingBean = workingBean;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnRequirements:
                    toDoDetailsActivity(workingBean.getProcessId());
                    break;
                case R.id.btnDelete:
                    new PromptDialog(mContext, new PromptListener() {
                        @Override
                        public void returnTrueOrFalse(boolean trueOrFalse) {
                            if (trueOrFalse) {
                                PhotosBean.deleteAll("PhotosBean", "processId=?", workingBean.getProcessId());
                                workingBean.delete();
                                workingBeanList.remove(workingBean);
                                AddProcessAdapter.this.notifyDataSetChanged();
                                ToastUtil.showShort(mContext, "删除成功！");
                            }
                        }
                    }, "提示", "确认删除该条数据？", "取消", "确认").show();
                    break;
            }
        }
    }

    /**
     * 跳转到详情
     */
    private void toDoDetailsActivity(String processId) {
        Intent intent = new Intent(mContext, ToDoDetailsActivity.class);
        intent.putExtra("flowId", "");
        intent.putExtra("workId", "详情");
        intent.putExtra("processId", processId);
        intent.putExtra("isPopTakePhoto", false);
        mContext.startActivity(intent);
    }
}
