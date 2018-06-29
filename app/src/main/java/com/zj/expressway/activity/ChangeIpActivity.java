package com.zj.expressway.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.zj.expressway.R;
import com.zj.expressway.base.BaseActivity;
import com.zj.expressway.utils.ConstantsUtil;
import com.zj.expressway.utils.ScreenManagerUtil;
import com.zj.expressway.utils.SpUtil;
import com.zj.expressway.utils.ToastUtil;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import cn.hutool.core.util.StrUtil;

/**
 * Created by HaiJun on 2018/6/11 16:56
 * 动态修改IP
 */
public class ChangeIpActivity extends BaseActivity {
    @ViewInject(R.id.imgBtnLeft)
    private ImageView imgBtnLeft;
    @ViewInject(R.id.txtTitle)
    private TextView txtTitle;
    @ViewInject(R.id.edtNewIp)
    private EditText edtNewIp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_change_ip);

        x.view().inject(this);
        ScreenManagerUtil.pushActivity(this);

        imgBtnLeft.setVisibility(View.VISIBLE);
        imgBtnLeft.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.back_btn));
        txtTitle.setText("设置IP");

        edtNewIp.setText(ConstantsUtil.BASE_URL);
    }

    @Event({R.id.imgBtnLeft, R.id.btnQuery})
    private void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgBtnLeft:
                this.finish();
                break;
            case R.id.btnQuery:
                if (StrUtil.isNotEmpty(edtNewIp.getText().toString())) {
                    ToastUtil.showShort(this, "新IP设置成功，请重新登录！");
                    SpUtil.put(this, "BASE_URL", edtNewIp.getText().toString());
                    ConstantsUtil.BASE_URL = edtNewIp.getText().toString();
                    SpUtil.put(this, ConstantsUtil.IS_LOGIN_SUCCESSFUL, false);
                    ScreenManagerUtil.popAllActivityExceptOne();
                    startActivity(new Intent(this, LoginActivity.class));
                } else {
                    ToastUtil.showShort(this, "IP地址不能为空！");
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ScreenManagerUtil.popActivity(this);
    }
}
