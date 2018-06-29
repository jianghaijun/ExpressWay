package com.zj.expressway.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.zj.expressway.R;
import com.zj.expressway.base.BaseActivity;
import com.zj.expressway.base.BaseModel;
import com.zj.expressway.utils.ChildThreadUtil;
import com.zj.expressway.utils.ConstantsUtil;
import com.zj.expressway.utils.JsonUtils;
import com.zj.expressway.utils.JudgeNetworkIsAvailable;
import com.zj.expressway.utils.LoadingUtils;
import com.zj.expressway.utils.ScreenManagerUtil;
import com.zj.expressway.utils.SpUtil;
import com.zj.expressway.utils.ToastUtil;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.IOException;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

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
 *       Created by HaiJun on 2018/6/11 16:56
 *       修改密码
 */
public class UpdatePassWordActivity extends BaseActivity {
    @ViewInject(R.id.imgBtnLeft)
    private ImageView imgBtnLeft;
    @ViewInject(R.id.txtTitle)
    private TextView txtTitle;
    @ViewInject(R.id.edtOldPassWord)
    private EditText edtOldPassWord;
    @ViewInject(R.id.edtNewPassWord)
    private EditText edtNewPassWord;
    @ViewInject(R.id.edtQueryPassWord)
    private EditText edtQueryPassWord;

    private Activity mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_update_pass_word);

        x.view().inject(this);
        ScreenManagerUtil.pushActivity(this);
        mContext = this;

        imgBtnLeft.setVisibility(View.VISIBLE);
        imgBtnLeft.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.back_btn));
        txtTitle.setText(getString(R.string.update_password));
    }

    @Event({R.id.imgBtnLeft, R.id.btnQuery})
    private void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgBtnLeft:
                this.finish();
                break;
            case R.id.btnQuery:
                if (JudgeNetworkIsAvailable.isNetworkAvailable(this)) {
                    checkPassWord();
                } else {
                    ToastUtil.showShort(this, getString(R.string.not_network));
                }
                break;
        }
    }

    /**
     * 密码校验
     */
    private void checkPassWord() {
        String oldPassWord = edtOldPassWord.getText().toString().trim();
        String newPassWord = edtNewPassWord.getText().toString().trim();
        String queryPassWord = edtQueryPassWord.getText().toString().trim();
        if (StrUtil.isEmpty(oldPassWord)) {
            ToastUtil.showShort(this, getString(R.string.old_pswd_can_not_be_empty));
        } else if (StrUtil.isEmpty(newPassWord)) {
            ToastUtil.showShort(this, getString(R.string.new_pswd_can_not_be_empty));
        } else if (!newPassWord.equals(queryPassWord)) {
            ToastUtil.showShort(this, getString(R.string.two_pswd_are_not_consistent));
        } else {
            updatePassWord(newPassWord, oldPassWord);
        }
    }

    /**
     * 修改密码
     * @param newPassWord
     * @param oldPassWord
     */
    private void updatePassWord(String newPassWord, String oldPassWord) {
        LoadingUtils.showLoading(this);
        JSONObject obj = new JSONObject();
        obj.put("userPwdOld", oldPassWord);
        obj.put("userPwd", newPassWord);
        obj.put("userPwdNew", newPassWord);
        Request request = ChildThreadUtil.getRequest(mContext, ConstantsUtil.UPDATE_PASSWORD, obj.toString());
        ConstantsUtil.okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                ChildThreadUtil.toastMsgHidden(mContext, getString(R.string.server_exception));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String jsonData = response.body().string().toString();
                if (JsonUtils.isGoodJson(jsonData)) {
                    Gson gson = new Gson();
                    BaseModel model = gson.fromJson(jsonData, BaseModel.class);
                    if (model.isSuccess()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                LoadingUtils.hideLoading();
                                // Token异常重新登录
                                ToastUtil.showLong(mContext, "密码修改成功请重新登录！");
                                SpUtil.put(mContext, ConstantsUtil.IS_LOGIN_SUCCESSFUL, false);
                                ScreenManagerUtil.popAllActivityExceptOne();
                                startActivity(new Intent(mContext, LoginActivity.class));
                            }
                        });
                    } else {
                        ChildThreadUtil.checkTokenHidden(mContext, model.getMessage(), model.getCode());
                    }
                } else {
                    ChildThreadUtil.toastMsgHidden(mContext, getString(R.string.json_error));
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ScreenManagerUtil.popActivity(this);
    }
}
