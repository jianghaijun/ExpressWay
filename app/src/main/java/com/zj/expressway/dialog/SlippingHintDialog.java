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
import android.widget.RelativeLayout;

import com.zj.expressway.R;

import org.xutils.common.util.DensityUtil;

public class SlippingHintDialog extends Dialog implements View.OnClickListener {

    public SlippingHintDialog(@NonNull Context context) {
        super(context, R.style.dialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_slipping_hint);

        RelativeLayout rlTop = findViewById(R.id.rlTop);
        Button btnPrompt = findViewById(R.id.btnPrompt);

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
        getWindow().setAttributes(layoutParams);
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }
}
