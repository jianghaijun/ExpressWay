package com.zj.expressway.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.zj.expressway.R;
import com.zj.expressway.listener.ReportListener;
import com.zj.expressway.utils.ToastUtil;

import cn.hutool.core.util.StrUtil;

/**
 *       Created by HaiJun on 2018/6/11 17:56
 */
public class HierarchyDialog extends Dialog implements View.OnClickListener {
    private ReportListener reportListener;
    private Context context;
    private RadioGroup radioGroup;
    private String sTitle, sLeftText, sRightText;
    private boolean isSelect = true;

    /**
     * @param context
     * @param reportListener
     * @param sTitle         提示框标题
     * @param sLeftText      左侧白色按钮文本
     * @param sRightText     右侧黄色按钮文本
     */
    public HierarchyDialog(@NonNull Context context, ReportListener reportListener, String sTitle, String sLeftText, String sRightText) {
        super(context);
        this.sTitle = sTitle;
        this.context = context;
        this.sLeftText = sLeftText;
        this.sRightText = sRightText;
        this.reportListener = reportListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_hierarchy);

        Button btnRight = (Button) findViewById(R.id.query_setting_btn);
        Button btnLeft = (Button) findViewById(R.id.close_setting_btn);

        TextView txtTitle = (TextView) findViewById(R.id.txtTitle);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);

        //点击事件
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                RadioButton radioButton = (RadioButton) findViewById(checkedId);
                if (radioButton.getText().toString().equals(context.getString(R.string.sameLevelAdd))) {
                    isSelect = true;
                } else {
                    isSelect = false;
                }
            }
        });

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
                reportListener.returnUserId((isSelect ? "2001" : "2002"));
                break;
            // 左侧
            case R.id.close_setting_btn:
                dismiss();
                break;
        }
    }
}