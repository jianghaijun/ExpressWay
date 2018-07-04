package com.zj.expressway.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.tencent.bugly.crashreport.CrashReport;
import com.zj.expressway.R;
import com.zj.expressway.activity.AuditManagementActivity;
import com.zj.expressway.activity.ProcessManagerActivity;
import com.zj.expressway.activity.ProcessReportActivity;
import com.zj.expressway.activity.QrCodeScanActivity;
import com.zj.expressway.activity.QualityInspectionActivity;
import com.zj.expressway.activity.WorkingProcedureActivity;
import com.zj.expressway.bean.AppInfoBean;
import com.zj.expressway.model.SameDayModel;
import com.zj.expressway.utils.ChildThreadUtil;
import com.zj.expressway.utils.ConstantsUtil;
import com.zj.expressway.utils.JsonUtils;
import com.zj.expressway.utils.JudgeNetworkIsAvailable;
import com.zj.expressway.utils.LoadingUtils;
import com.zj.expressway.utils.SpUtil;
import com.zj.expressway.utils.ToastUtil;
import com.zj.expressway.view.TouchHighlightImageButton;

import java.io.IOException;
import java.util.List;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONObject;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

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
 *       Created by HaiJun on 2018/6/11 17:01
 *       应用信息适配器
 */
public class AppInfoAdapter extends RecyclerView.Adapter<AppInfoAdapter.AppInfoHold> {
    private Activity mContext;
    private List<AppInfoBean> appInfoBeanList;

    public AppInfoAdapter(Context mContext, List<AppInfoBean> appInfoBeanList) {
        this.mContext = (Activity) mContext;
        this.appInfoBeanList = appInfoBeanList;
    }

    @Override
    public AppInfoHold onCreateViewHolder(ViewGroup parent, int viewType) {
        return new AppInfoHold(LayoutInflater.from(mContext).inflate(R.layout.item_app_info, parent, false));
    }

    @Override
    public void onBindViewHolder(AppInfoHold holder, final int position) {
        Drawable top = ContextCompat.getDrawable(mContext, appInfoBeanList.get(position).getImgUrl());
        top.setBounds(0, 0, top.getMinimumWidth(), top.getMinimumHeight());
        holder.imgView.setImageDrawable(ContextCompat.getDrawable(mContext, appInfoBeanList.get(position).getImgUrl()));
        holder.txtTitle.setText(appInfoBeanList.get(position).getTitle());
        // 图标点击事件
        holder.imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                switch (position) {
                    // 工序检查
                    case 0:
                        /*CrashReport.testJavaCrash();*/
                        intent = new Intent(mContext, AuditManagementActivity.class);
                        SpUtil.put(mContext, ConstantsUtil.PROCESS_LIST_TYPE, "1");
                        mContext.startActivity(intent);
                        break;
                    // 质量
                    case 1:
                        intent = new Intent(mContext, QualityInspectionActivity.class);
                        SpUtil.put(mContext, ConstantsUtil.PROCESS_LIST_TYPE, "2");
                        mContext.startActivity(intent);
                        break;
                    // 安全巡查
                    case 2:
                        intent = new Intent(mContext, QualityInspectionActivity.class);
                        SpUtil.put(mContext, ConstantsUtil.PROCESS_LIST_TYPE, "3");
                        mContext.startActivity(intent);
                        break;
                    // 审核管理
                    case 3:
                        intent = new Intent(mContext, WorkingProcedureActivity.class);
                        SpUtil.put(mContext, ConstantsUtil.PROCESS_LIST_TYPE, "4");
                        mContext.startActivity(intent);
                        break;
                    // 工序报表
                    case 4:
                        if (JudgeNetworkIsAvailable.isNetworkAvailable(mContext)) {
                            getSameDayData();
                        } else {
                            ToastUtil.showShort(mContext, mContext.getString(R.string.not_network));
                        }
                        break;
                    // 工序管理
                    /*case 5:
                        intent = new Intent(mContext, ProcessManagerActivity.class);
                        mContext.startActivity(intent);
                        break;*/
                    // 二维码扫描
                    case 6:
                        intent = new Intent(mContext, QrCodeScanActivity.class);
                        mContext.startActivity(intent);
                        break;
                    default:
                        ToastUtil.showShort(mContext, "该功能正在开发中，敬请期待!");
                        break;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return appInfoBeanList == null ? 0 : appInfoBeanList.size();
    }

    /**
     * 获取数据
     */
    private void getSameDayData() {
        LoadingUtils.showLoading(mContext);
        JSONObject obj = new JSONObject();
        obj.put("startDate", System.currentTimeMillis());
        obj.put("endDate", DateUtil.tomorrow().getTime());
        Request request = ChildThreadUtil.getRequest(mContext, ConstantsUtil.PROCESS_REPORT_TODAY, obj.toString());
        ConstantsUtil.okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                ChildThreadUtil.toastMsgHidden(mContext, mContext.getString(R.string.server_exception));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String jsonData = response.body().string().toString();
                if (JsonUtils.isGoodJson(jsonData)) {
                    Gson gson = new Gson();
                    SameDayModel model = gson.fromJson(jsonData, SameDayModel.class);
                    if (model.isSuccess()) {
                        ConstantsUtil.sameDayBean = model.getData();
                        mContext.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(mContext, ProcessReportActivity.class);
                                mContext.startActivity(intent);
                                LoadingUtils.hideLoading();
                            }
                        });
                    } else {
                        ChildThreadUtil.checkTokenHidden(mContext, model.getMessage(), model.getCode());
                    }
                } else {
                    ChildThreadUtil.toastMsgHidden(mContext, mContext.getString(R.string.json_error));
                }
            }
        });
    }

    /**
     * 容纳器
     */
    public class AppInfoHold extends RecyclerView.ViewHolder {
        private TouchHighlightImageButton imgView;
        private TextView txtTitle;

        public AppInfoHold(View itemView) {
            super(itemView);
            imgView = (TouchHighlightImageButton) itemView.findViewById(R.id.imgInfo);
            txtTitle = (TextView) itemView.findViewById(R.id.txtTitle);
        }
    }

}
