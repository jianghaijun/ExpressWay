package com.zj.expressway.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.zj.expressway.R;
import com.zj.expressway.listener.ReportListener;

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
 *       Created by HaiJun on 2018/6/11 17:56
 *       驳回原因、审核通过dialog
 */
public class RejectDialog extends Dialog implements View.OnClickListener {
    private ReportListener reportListener;
    private EditText edtContext;
    private String sTitle, sLeftText, sRightText;

    /**
     * @param context
     * @param reportListener
     * @param sTitle         提示框标题
     * @param sLeftText      左侧白色按钮文本
     * @param sRightText     右侧黄色按钮文本
     */
    public RejectDialog(@NonNull Context context, ReportListener reportListener, String sTitle, String sLeftText, String sRightText) {
        super(context);
        this.sTitle = sTitle;
        this.sLeftText = sLeftText;
        this.sRightText = sRightText;
        this.reportListener = reportListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_rejcet);

        Button btnRight = (Button) findViewById(R.id.query_setting_btn);
        Button btnLeft = (Button) findViewById(R.id.close_setting_btn);

        TextView txtTitle = (TextView) findViewById(R.id.txtTitle);
        edtContext = (EditText) findViewById(R.id.edtContext);

        txtTitle.setText(sTitle);
        btnLeft.setText(sLeftText);
        btnRight.setText(sRightText);

        btnLeft.setOnClickListener(this);
        btnRight.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // 右侧
            case R.id.query_setting_btn:
                dismiss();
                reportListener.returnUserId(edtContext.getText().toString().trim());
                break;
            // 左侧
            case R.id.close_setting_btn:
                dismiss();
                break;
        }
    }
}