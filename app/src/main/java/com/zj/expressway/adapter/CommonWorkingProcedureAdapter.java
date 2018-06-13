package com.zj.expressway.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zj.expressway.R;
import com.zj.expressway.base.BaseAdapter;
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
 *       Created by HaiJun on 2018/6/11 17:02
 *       常用工序适配器
 */
public class CommonWorkingProcedureAdapter extends BaseAdapter<List<WorkingBean>> {
    private Activity mContext;

    public CommonWorkingProcedureAdapter(Activity mContext) {
        this.mContext = mContext;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyHolder(LayoutInflater.from(mContext).inflate(R.layout.item_procedure, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((CommonWorkingProcedureAdapter.MyHolder) holder).bind((WorkingBean) getDataSet().get(position));
    }

    private class MyHolder extends RecyclerView.ViewHolder {
        private TextView txtTitle;

        public MyHolder(View itemView) {
            super(itemView);
            txtTitle = (TextView) itemView.findViewById(R.id.txtTitle);
        }

        public void bind(final WorkingBean bean) {
            txtTitle.setText(bean.getProcessName());
            txtTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.putExtra("procedureName", bean.getProcessName());
                    mContext.setResult(Activity.RESULT_OK, intent);
                    mContext.finish();
                }
            });
        }
    }
}
