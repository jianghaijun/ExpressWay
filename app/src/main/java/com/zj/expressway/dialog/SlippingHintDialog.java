package com.zj.expressway.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RelativeLayout;

import com.zj.expressway.R;
import com.zj.expressway.utils.SpUtil;

public class SlippingHintDialog extends Dialog implements View.OnClickListener {
    private Context mContext;
    private int btnBg;
    private String type;
    private String btnText;

    public SlippingHintDialog(@NonNull Context context, int btnBg, String type, String btnText) {
        super(context, R.style.dialog);
        this.mContext = context;
        this.btnBg = btnBg;
        this.type = type;
        this.btnText = btnText;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_slipping_hint);

        RelativeLayout rlTop = (RelativeLayout) findViewById(R.id.rlTop);
        Button btnPrompt = (Button) findViewById(R.id.btnPrompt);
        final CheckBox btnNoLongerPrompt = (CheckBox) findViewById(R.id.btnNoLongerPrompt);
        btnPrompt.setBackground(ContextCompat.getDrawable(mContext, btnBg));
        btnPrompt.setText(btnText);

        btnNoLongerPrompt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SpUtil.put(mContext, type, btnNoLongerPrompt.isChecked());
            }
        });

        rlTop.setOnClickListener(this);
        btnPrompt.setOnClickListener(this);
    }

    @Override
    public void show() {
        super.show();
        /**
         * 设置宽度全屏，要设置在show的后面
         */
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        getWindow().getDecorView().setPadding(0, 0, 0, 0);
        layoutParams.alpha = 100;
        getWindow().setWindowAnimations(R.style.DialogInAndOutAnim);
        getWindow().setAttributes(layoutParams);
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }
}
