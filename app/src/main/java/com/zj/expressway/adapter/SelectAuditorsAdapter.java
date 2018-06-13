package com.zj.expressway.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zj.expressway.R;
import com.zj.expressway.bean.SelectAuditorsBean;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

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
 *       Created by HaiJun on 2017/11/9 17:22
 *       上报人员适配器
 */

public class SelectAuditorsAdapter extends BaseAdapter {
    private Context mContext;
    private List<SelectAuditorsBean> selectAuditorsList;
    private LayoutInflater inflater;

    public SelectAuditorsAdapter(Context mContext, List<SelectAuditorsBean> selectAuditorsList) {
        this.mContext = mContext;
        this.selectAuditorsList = selectAuditorsList;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return selectAuditorsList == null ? 0 : selectAuditorsList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        final SelectAuditorsHandler selectAuditorsHandler;
        if (view == null) {
            selectAuditorsHandler = new SelectAuditorsHandler();
            view = inflater.inflate(R.layout.item_select_auditors, null);
            x.view().inject(selectAuditorsHandler, view);

            view.setTag(selectAuditorsHandler);
        } else {
            selectAuditorsHandler = (SelectAuditorsHandler) view.getTag();
        }

        boolean isSelect = selectAuditorsList.get(position).isSelect();
        Drawable drawable;
        if (isSelect) {
            drawable = ContextCompat.getDrawable(mContext, R.drawable.btn_check);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            selectAuditorsHandler.txtAuditors.setCompoundDrawables(null, null, drawable, null);
        } else {
            drawable = ContextCompat.getDrawable(mContext, R.drawable.btn_un_check);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            selectAuditorsHandler.txtAuditors.setCompoundDrawables(null, null, drawable, null);
        }

        selectAuditorsHandler.txtAuditors.setText(selectAuditorsList.get(position).getRealName());

        return view;
    }

    private class SelectAuditorsHandler {
        @ViewInject(R.id.txtAuditors)
        private TextView txtAuditors;
    }
}
