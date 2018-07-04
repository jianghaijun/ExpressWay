package com.zj.expressway.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.zj.expressway.R;
import com.zj.expressway.bean.ProcessDictionaryBean;
import com.zj.expressway.popwindow.ProcessPopupWindow;

import java.util.List;

/**
 * Created by HaiJun on 2018/6/11 17:11
 * 工序列表适配器
 */
public class LocalProcessListAdapter extends RecyclerView.Adapter<LocalProcessListAdapter.ProcessHolder> {
    private Activity mContext;
    private List<ProcessDictionaryBean> processList;

    /**
     * 重载
     *
     * @param mContext
     * @param processList
     */
    public LocalProcessListAdapter(Context mContext, List<ProcessDictionaryBean> processList) {
        this.mContext = (Activity) mContext;
        this.processList = processList;
    }

    @Override
    public ProcessHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ProcessHolder(LayoutInflater.from(mContext).inflate(R.layout.item_local_process, parent, false));
    }

    @Override
    public void onBindViewHolder(final ProcessHolder holder, final int position) {
        if (position == 0) {
            holder.txtMoreInfo.setText(processList.get(position).getOperation());
            holder.txtTitle.setText(processList.get(position).getDictName());
        } else {
            if (processList.get(position).isSelect()) {
                holder.txtSelect.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.btn_check));
            } else {
                holder.txtSelect.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.btn_un_check));
            }
            String str = "<font color=\"#0099FF\">" + processList.get(position).getOperation() + "</font>";
            holder.txtMoreInfo.setText(Html.fromHtml(str));
            holder.txtTitle.setText(processList.get(position).getDictName());

            holder.txtSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (processList.get(position).isSelect()) {
                        processList.get(position).setSelect(false);
                    } else {
                        processList.get(position).setSelect(true);
                    }

                    /*for (ProcessDictionaryBean bean : processList) {
                        bean.setSelect(false);
                    }
                    processList.get(position).setSelect(true);*/
                    notifyDataSetChanged();
                }
            });

            holder.txtMoreInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ProcessPopupWindow popupWindow = new ProcessPopupWindow(mContext, processList.get(position).getDictId());
                    popupWindow.showAtDropDownRight(holder.txtMoreInfo);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return processList == null ? 0 : processList.size();
    }

    public class ProcessHolder extends RecyclerView.ViewHolder {
        private ImageButton txtSelect; // 审核状态
        private TextView txtMoreInfo;  // 工序名称
        private TextView txtTitle;  // 工序部位

        public ProcessHolder(View itemView) {
            super(itemView);
            txtSelect = (ImageButton) itemView.findViewById(R.id.txtSelect);
            txtMoreInfo = (TextView) itemView.findViewById(R.id.txtMoreInfo);
            txtTitle = (TextView) itemView.findViewById(R.id.txtTitle);
        }
    }
}
