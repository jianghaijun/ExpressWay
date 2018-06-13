package com.zj.expressway.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.zj.expressway.R;
import com.zj.expressway.bean.WorkingBean;
import com.zj.expressway.listener.PromptListener;

import org.xutils.common.util.DensityUtil;

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
 *       Created by HaiJun on 2018/6/11 17:55
 *       拍照要求dialog
 */
public class PhotoRequirementsDialog extends Dialog implements View.OnClickListener {
    private PromptListener choiceListener;
    private WorkingBean workingBean;

    public PhotoRequirementsDialog(@NonNull Context context, PromptListener choiceListener, WorkingBean workingBean) {
        super(context, R.style.dialog);
        this.workingBean = workingBean;
        this.choiceListener = choiceListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_photo_requirement);

        Button btnRight = (Button) findViewById(R.id.btnPhotoQuery);
        Button btnLeft = (Button) findViewById(R.id.btnPhotoCancel);

        TextView txtTitle = (TextView) findViewById(R.id.txtTitle);
        TextView txtPhotoRequirement = (TextView) findViewById(R.id.txtPhotoRequirement);
        TextView txtDistanceAngle = (TextView) findViewById(R.id.txtDistanceAngle);
        TextView txtPhotosNum = (TextView) findViewById(R.id.txtPhotosNum);

        txtTitle.setText(workingBean.getProcessName() + "拍照要求" + "");
        txtPhotoRequirement.setText(workingBean.getPhotoContent());
        txtDistanceAngle.setText(workingBean.getPhotoDistance());
        txtPhotosNum.setText("最少拍照" + workingBean.getPhotoNumber() + "张");

        btnLeft.setOnClickListener(this);
        btnRight.setOnClickListener(this);
    }

    @Override
    public void show() {
        super.show();
        /**
         * 设置宽度全屏，要设置在show的后面
         */
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.gravity = Gravity.CENTER;
        layoutParams.width = DensityUtil.getScreenWidth() - DensityUtil.dip2px(20);
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        getWindow().getDecorView().setPadding(0, 0, 0, 0);
        getWindow().setAttributes(layoutParams);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // 右侧
            case R.id.btnPhotoQuery:
                dismiss();
                choiceListener.returnTrueOrFalse(true);
                break;
            // 左侧
            case R.id.btnPhotoCancel:
                dismiss();
                choiceListener.returnTrueOrFalse(false);
                break;
        }
    }
}