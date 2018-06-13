package com.zj.expressway.utils;

import android.content.Context;
import android.graphics.Color;

import com.zyao89.view.zloading.ZLoadingDialog;
import com.zyao89.view.zloading.Z_TYPE;

/**
 * @author Administrator
 * @time 2017/10/11 0011 20:45
 */

public class LoadingUtils {
    private static ZLoadingDialog dialog;

    /**
     * 展示加载动画
     * @param mContext
     */
    public static void showLoading(Context mContext) {
        dialog = new ZLoadingDialog(mContext);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setLoadingBuilder(Z_TYPE.ROTATE_CIRCLE)//设置类型
                .setLoadingColor(Color.BLACK)//颜色
                .setHintText("Loading...")
                .setHintTextSize(16) // 设置字体大小 dp
                .setHintTextColor(Color.GRAY)  // 设置字体颜色
                .show();
    }

    /**
     * 关闭加载动画
     */
    public static void hideLoading() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }


}
