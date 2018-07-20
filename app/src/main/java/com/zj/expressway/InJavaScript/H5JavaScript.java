package com.zj.expressway.InJavaScript;

import android.app.Activity;
import android.content.Context;
import android.webkit.JavascriptInterface;

import com.zj.expressway.activity.MainActivity;
import com.zj.expressway.activity.QualityInspectionActivity;
import com.zj.expressway.base.BaseInJavaScript;
import com.zj.expressway.bean.PhotosBean;
import com.zj.expressway.bean.WorkingBean;
import com.zj.expressway.popwindow.H5PopupWindow;
import com.zj.expressway.utils.ConstantsUtil;
import com.zj.expressway.utils.ScreenManagerUtil;
import com.zj.expressway.utils.SpUtil;

import org.litepal.crud.DataSupport;

/**
 * Create dell By 2018/7/9 9:37
 */

public class H5JavaScript extends BaseInJavaScript {
    private Activity mContext;
    private H5PopupWindow h5Dialog;
    private boolean isDetails;
    private String processId;
    private WorkingBean deleteWorkingBean;

    public H5JavaScript(Context mContext) {
        super(mContext);
        this.mContext = (Activity) mContext;
    }

    public H5JavaScript(Context mContext, H5PopupWindow h5Dialog, boolean isDetails, String processId, WorkingBean deleteWorkingBean) {
        super(mContext);
        this.h5Dialog = h5Dialog;
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
        return (String) SpUtil.get(mContext, "JSONData", "");
    }

    @JavascriptInterface
    public String getStartFlowData() {
        return (String) SpUtil.get(mContext, "StartFlowData", "");
    }

    @JavascriptInterface
    public void hiddenDialog() {
        if (h5Dialog != null) {
            h5Dialog.dismiss();
        }
    }

    @JavascriptInterface
    public void closeStartActivity() {
        // 清空操作按钮
        ConstantsUtil.isLoading = true;
        if (isDetails) {
            DataSupport.deleteAll(PhotosBean.class, "processId=?", processId);
            if (deleteWorkingBean != null) {
                deleteWorkingBean.delete();
            }
        }
        SpUtil.remove(mContext, "uploadImgData");
        ScreenManagerUtil.popAllActivityExceptOne(QualityInspectionActivity.class);
    }
}
