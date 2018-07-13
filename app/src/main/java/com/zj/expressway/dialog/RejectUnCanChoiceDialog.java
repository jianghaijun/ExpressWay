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
import com.zj.expressway.utils.ToastUtil;

import cn.hutool.core.util.StrUtil;

/**
 * Created by HaiJun on 2018/6/11 17:56
 * 驳回原因、审核通过dialog
 */
public class RejectUnCanChoiceDialog extends Dialog implements View.OnClickListener {
    private ReportListener reportListener;
    private EditText edtContext;
    private Context context;
    private String sTitle, sLeftText, sRightText, hint;

    /**
     * @param context
     * @param reportListener
     * @param sTitle         提示框标题
     * @param sLeftText      左侧白色按钮文本
     * @param sRightText     右侧黄色按钮文本
     * @param hint
     */
    public RejectUnCanChoiceDialog(@NonNull Context context, ReportListener reportListener, String sTitle, String hint, String sLeftText, String sRightText) {
        super(context);
        this.sTitle = sTitle;
        this.hint = hint;
        this.context = context;
        this.sLeftText = sLeftText;
        this.sRightText = sRightText;
        this.reportListener = reportListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_rejcet_un_can_choice);

        Button btnRight = (Button) findViewById(R.id.query_setting_btn);
        Button btnLeft = (Button) findViewById(R.id.close_setting_btn);

        TextView txtTitle = (TextView) findViewById(R.id.txtTitle);
        edtContext = (EditText) findViewById(R.id.edtContext);
        edtContext.setHint(hint);

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
                if (StrUtil.isEmpty(edtContext.getText().toString().trim())) {
                    ToastUtil.showShort(context, hint);
                } else {
                    dismiss();
                    reportListener.returnUserId(edtContext.getText().toString().trim());
                }
                break;
            // 左侧
            case R.id.close_setting_btn:
                dismiss();
                break;
        }
    }
}