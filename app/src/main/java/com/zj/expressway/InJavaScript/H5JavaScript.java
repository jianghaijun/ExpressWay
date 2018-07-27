package com.zj.expressway.InJavaScript;

import android.app.Activity;
import android.content.Context;
import android.webkit.JavascriptInterface;

import com.zj.expressway.activity.AuditManagementActivity;
import com.zj.expressway.activity.MainActivity;
import com.zj.expressway.activity.QualityInspectionActivity;
import com.zj.expressway.activity.WorkingProcedureActivity;
import com.zj.expressway.base.BaseInJavaScript;
import com.zj.expressway.bean.PhotosBean;
import com.zj.expressway.bean.WorkingBean;
import com.zj.expressway.listener.PromptListener;
import com.zj.expressway.popwindow.H5PopupWindow;
import com.zj.expressway.utils.ConstantsUtil;
import com.zj.expressway.utils.ScreenManagerUtil;
import com.zj.expressway.utils.SpUtil;

import org.litepal.crud.DataSupport;

import cn.hutool.json.JSONObject;

/**
 * Create dell By 2018/7/9 9:37
 */

public class H5JavaScript extends BaseInJavaScript {
    private Activity mContext;
    private boolean isDetails;
    private String processId;
    private PromptListener promptListener;
    private WorkingBean deleteWorkingBean;

    public H5JavaScript(Context mContext) {
        super(mContext);
        this.mContext = (Activity) mContext;
    }

    public H5JavaScript(Context mContext, PromptListener promptListener, boolean isDetails, String processId, WorkingBean deleteWorkingBean) {
        super(mContext);
        this.promptListener = promptListener;
        this.isDetails = isDetails;
        this.processId = processId;
        this.mContext = (Activity) mContext;
        this.deleteWorkingBean = deleteWorkingBean;
    }

    @JavascriptInterface
    public String getLoginToken() {
        String token = (String) SpUtil.get(mContext, ConstantsUtil.TOKEN, "");
        return token;
    }

    @JavascriptInterface
    public void closeActivity() {
        ScreenManagerUtil.popAllActivityExceptOne(MainActivity.class);
    }

    @JavascriptInterface
    public String getSubmitData() {
        //return (String) SpUtil.get(mContext, "JSONData", "");
        return (String) SpUtil.get(mContext, "startFlowData", "");
    }

    @JavascriptInterface
    public String getUpdateData() {
        return (String) SpUtil.get(mContext, "updateFlowData", "");
    }

    @JavascriptInterface
    public String getStartFlowData() {
        return (String) SpUtil.get(mContext, "startFlowData", "");
    }

    @JavascriptInterface
    public String getActionData() {
        return (String) SpUtil.get(mContext, "actionData", "");
    }

    @JavascriptInterface
    public JSONObject getActionDataTwo() {
        String s = (String) SpUtil.get(mContext, "actionData", "");
        return new JSONObject(s);
    }

    @JavascriptInterface
    public void hiddenDialog() {
        if (promptListener != null) {
            promptListener.returnTrueOrFalse(true);
        }
    }

    @JavascriptInterface
    public void closeStartActivity() {
        // 清空操作按钮
        ConstantsUtil.isLoading = true;
        if (isDetails) {
            if (processId != null) {
                DataSupport.deleteAll(PhotosBean.class, "processId=?", processId);
            }
            if (deleteWorkingBean != null) {
                deleteWorkingBean.delete();
            }
        }
        SpUtil.remove(mContext, "uploadImgData");
        String type = (String) SpUtil.get(mContext, "PROCESS_TYPE", "1");
        switch (type) {
            case "1":
                ConstantsUtil.isFirst = true;
                ScreenManagerUtil.popAllActivityExceptOne(AuditManagementActivity.class);
                break;
            case "2":
            case "3":
                ScreenManagerUtil.popAllActivityExceptOne(QualityInspectionActivity.class);
                break;
            case "4":
                ScreenManagerUtil.popAllActivityExceptOne(WorkingProcedureActivity.class);
                break;
        }
    }
}
