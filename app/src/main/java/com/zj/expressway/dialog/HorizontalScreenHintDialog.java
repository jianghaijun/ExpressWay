package com.zj.expressway.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.zj.expressway.R;
import com.zj.expressway.activity.PhotographActivity;

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
 *       Created by HaiJun on 2018/6/11 17:54
 *       竖屏拍照提示dialog
 */
public class HorizontalScreenHintDialog extends Dialog implements View.OnClickListener {
    private AlbumOrientationEventListener mAlbumOrientationEventListener;
    private Button btnCancel;
    private Activity mContext;
    private boolean isJump;
    // 竖屏角度
    private int mOrientation = 0;

    public HorizontalScreenHintDialog(@NonNull Context context, boolean isJump) {
        super(context, R.style.dialog);
        this.mContext = (Activity) context;
        this.isJump = isJump;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_horizontal_screen_hint);

        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(this);

        if (!isJump) {
            btnCancel.setVisibility(View.GONE);
        }

        // 屏幕方向监听
        mAlbumOrientationEventListener = new AlbumOrientationEventListener(mContext, SensorManager.SENSOR_DELAY_NORMAL);
        if (mAlbumOrientationEventListener.canDetectOrientation()) {
            mAlbumOrientationEventListener.enable();
        }
    }

    /**
     * 屏幕方向旋转监听
     */
    private class AlbumOrientationEventListener extends OrientationEventListener {
        public AlbumOrientationEventListener(Context context) {
            super(context);
        }

        public AlbumOrientationEventListener(Context context, int rate) {
            super(context, rate);
        }

        @Override
        public void onOrientationChanged(int orientation) {
            if (orientation == OrientationEventListener.ORIENTATION_UNKNOWN) {
                return;
            }

            //保证只返回四个方向
            int newOrientation = ((orientation + 45) / 90 * 90) % 360;

            if (newOrientation != mOrientation) {
                // 返回的mOrientation就是手机方向，为0°、90°、180°和270°中的一个
                mOrientation = newOrientation;
                switch (mOrientation) {
                    case 0:
                    case 180:
                        //ToastUtil.showShort(mContext, "处于竖屏！");
                        break;
                    case 90:
                    case 270:
                        mAlbumOrientationEventListener.disable();
                        if (isJump) {
                            Intent intent = new Intent();
                            intent.setClass(mContext, PhotographActivity.class);
                            mContext.startActivityForResult(intent, 1);
                        }
                        dismiss();
                        break;
                }
            }
        }
    }

    @Override
    public void show() {
        mContext.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.show();
        /**
         * 设置宽度全屏，要设置在show的后面
         */
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        getWindow().getDecorView().setPadding(0, 0, 0, 0);
        getWindow().setAttributes(layoutParams);
    }

    @Override
    public void onClick(View v) {
        dismiss();
        mAlbumOrientationEventListener.disable();
    }
}
