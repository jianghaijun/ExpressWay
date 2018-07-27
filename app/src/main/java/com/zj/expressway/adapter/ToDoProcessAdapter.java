package com.zj.expressway.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zj.expressway.R;
import com.zj.expressway.activity.ContractorDetailsActivity;
import com.zj.expressway.activity.EditScrollPhotoActivity;
import com.zj.expressway.activity.ReviewProgressActivity;
import com.zj.expressway.activity.ToDoDetailsActivity;
import com.zj.expressway.bean.WorkingBean;
import com.zj.expressway.utils.ConstantsUtil;
import com.zj.expressway.utils.SpUtil;

import java.util.List;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;

/**
 * Created by HaiJun on 2018/6/11 17:11
 * 工序列表适配器
 */
public class ToDoProcessAdapter extends RecyclerView.Adapter<ToDoProcessAdapter.ProcessHolder> {
    private Activity mContext;
    private List<WorkingBean> workingBeanList;

    /**
     * 重载
     *
     * @param mContext
     * @param workingBeanList
     */
    public ToDoProcessAdapter(Context mContext, List<WorkingBean> workingBeanList) {
        this.mContext = (Activity) mContext;
        this.workingBeanList = workingBeanList;
    }

    @Override
    public ProcessHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ProcessHolder(LayoutInflater.from(mContext).inflate(R.layout.item_working_procedure, parent, false));
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
        private TextView txtReviewProgress; // 审核状态
        private TextView txtProcedureName;  // 工序名称
        private TextView txtProcedurePath;  // 工序部位
        private ImageView imgViewProgress;  // 拍照
        private TextView txtProcedureState; // 拍照状态
        private TextView txtPersonals;      // 审核人员
        private TextView txtCheckTime;      // 检查时间
        private RelativeLayout rlProcedurePath;      // 检查时间
        private RelativeLayout rlBottom;      //
        private TextView imgViewTakePhoto;      //

        public ProcessHolder(View itemView) {
            super(itemView);
            txtReviewProgress = (TextView) itemView.findViewById(R.id.txtReviewProgress);
            txtProcedureName = (TextView) itemView.findViewById(R.id.txtProcedureName);
            txtProcedurePath = (TextView) itemView.findViewById(R.id.txtProcedurePath);
            txtProcedureState = (TextView) itemView.findViewById(R.id.txtProcedureState);
            txtPersonals = (TextView) itemView.findViewById(R.id.txtPersonals);
            txtCheckTime = (TextView) itemView.findViewById(R.id.txtCheckTime);
            imgViewProgress = (ImageView) itemView.findViewById(R.id.imgViewProgress);
            rlProcedurePath = (RelativeLayout) itemView.findViewById(R.id.rlProcedurePath);
            rlBottom = (RelativeLayout) itemView.findViewById(R.id.rlBottom);
            imgViewTakePhoto = (TextView) itemView.findViewById(R.id.imgViewTakePhoto);
        }

        public void bind(WorkingBean data) {
            String title = data.getTitle().replaceAll(",", "→");
            txtProcedureName.setText(title.substring(title.lastIndexOf("→") + 1));
            txtReviewProgress.setText(data.getFlowStatus());
            txtProcedurePath.setText(title);
            imgViewTakePhoto.setText(data.getNodeName());
            txtPersonals.setText(data.getSendUserName());
            txtCheckTime.setText(DateUtil.format(DateUtil.date(data.getSendTime() == 0 ? System.currentTimeMillis() : data.getSendTime()), "yyyy-MM-dd HH:mm:ss"));

            if (StrUtil.equals(data.getFlowStatus(), "审核中")) {
                imgViewProgress.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.audit));
            } else if (StrUtil.equals(data.getFlowStatus(), "结束")) {
                imgViewProgress.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.finish));
            } else if (StrUtil.equals(data.getFlowStatus(), "已驳回")) {
                imgViewProgress.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.reject));
            } else {
                imgViewProgress.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.progress));
            }
            //txtProcedureState.setText(data.getFlowName());
            rlBottom.setOnClickListener(new onClick(data));
            imgViewProgress.setOnClickListener(new onClick(data));
            txtReviewProgress.setOnClickListener(new onClick(data));
            rlProcedurePath.setOnClickListener(new onClick(data));
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
                case R.id.imgViewProgress:
                case R.id.txtReviewProgress:
                    reviewProgressActivity(workingBean.getWorkId());
                    break;
                case R.id.rlBottom:
                case R.id.rlProcedurePath:
                case R.id.imgViewTakePhoto:
                    takePhotoActivity(workingBean, false);
                    break;
            }
        }
    }

    /**
     * 跳转到详情
     */
    private void takePhotoActivity(WorkingBean bean, boolean isPopTakePhoto) {
        Intent intent;
        if (bean.getFlowId().equals("zxHwZlTrouble")) {
            intent = new Intent(mContext, ToDoDetailsActivity.class);
            SpUtil.put(mContext, ConstantsUtil.PROCESS_LIST_TYPE, "2");
        } else if (bean.getFlowId().equals("zxHwAqHiddenDanger")) {
            intent = new Intent(mContext, ToDoDetailsActivity.class);
            SpUtil.put(mContext, ConstantsUtil.PROCESS_LIST_TYPE, "3");
        } else {
            intent = new Intent(mContext, ContractorDetailsActivity.class);
        }
        intent.putExtra("flowId", bean.getFlowId() == null ? "" : bean.getFlowId());
        intent.putExtra("workId", bean.getWorkId() == null ? "" : bean.getWorkId());
        intent.putExtra("mainTablePrimaryId", bean.getMainTablePrimaryId() == null ? "" : bean.getMainTablePrimaryId());
        intent.putExtra("isToDo", true);
        intent.putExtra("isLocalAdd", bean.getIsLocalAdd());
        intent.putExtra("isPopTakePhoto", isPopTakePhoto);
        intent.putExtra("processId", bean.getProcessId());
        mContext.startActivity(intent);

        /*Intent intent;
        if (bean.getFlowId().equals("zxHwZlTrouble")) {
            intent = new Intent(mContext, EditScrollPhotoActivity.class);
            SpUtil.put(mContext, ConstantsUtil.PROCESS_LIST_TYPE, "2");
        } else if (bean.getFlowId().equals("zxHwAqHiddenDanger")) {
            intent = new Intent(mContext, EditScrollPhotoActivity.class);
            SpUtil.put(mContext, ConstantsUtil.PROCESS_LIST_TYPE, "3");
        } else {
            intent = new Intent(mContext, ContractorDetailsActivity.class);
        }
        intent.putExtra("flowId", bean.getFlowId() == null ? "" : bean.getFlowId());
        intent.putExtra("workId", bean.getWorkId() == null ? "" : bean.getWorkId());
        intent.putExtra("mainTablePrimaryId", bean.getMainTablePrimaryId() == null ? "" : bean.getMainTablePrimaryId());
        intent.putExtra("isToDo", true);
        intent.putExtra("isLocalAdd", bean.getIsLocalAdd());
        intent.putExtra("isPopTakePhoto", isPopTakePhoto);
        intent.putExtra("title", "详情");
        String url = ConstantsUtil.update + (bean.getFlowId() == null ? "" : bean.getFlowId()) + "/" + (bean.getWorkId() == null ? "" : bean.getWorkId());
        intent.putExtra("url", url);
        mContext.startActivity(intent);*/
    }

    /**
     * 跳转审核进度界面
     */
    private void reviewProgressActivity(String workId) {
        Intent intent = new Intent(mContext, ReviewProgressActivity.class);
        intent.putExtra("workId", workId);
        mContext.startActivity(intent);
    }
}
