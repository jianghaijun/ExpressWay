package com.zj.expressway.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.zj.expressway.R;
import com.zj.expressway.base.BaseActivity;
import com.zj.expressway.utils.ConstantsUtil;
import com.zj.expressway.utils.JsonUtils;
import com.zj.expressway.utils.JudgeNetworkIsAvailable;
import com.zj.expressway.utils.LoadingUtils;
import com.zj.expressway.utils.ScreenManagerUtil;
import com.zj.expressway.utils.SpUtil;
import com.zj.expressway.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.RequestBody;
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
    @ViewInject(R.id.edtNewPassWord)
    private EditText edtNewPassWord;
    @ViewInject(R.id.edtQueryPassWord)
    private EditText edtQueryPassWord;

    private Context mContext;

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
        String newPassWord = edtNewPassWord.getText().toString().trim();
        String queryPassWord = edtQueryPassWord.getText().toString().trim();
        if (TextUtils.isEmpty(newPassWord)) {
            ToastUtil.showShort(this, getString(R.string.new_pswd_can_not_be_empty));
        } else if (!newPassWord.equals(queryPassWord)) {
            ToastUtil.showShort(this, getString(R.string.two_pswd_are_not_consistent));
        } else {
            updatePassWord(newPassWord);
        }
    }

    /**
     * 修改密码
     *
     * @param newPassWord
     */
    private void updatePassWord(String newPassWord) {
        LoadingUtils.showLoading(this);
        JSONObject obj = new JSONObject();
        try {
            obj.put("userPwd", newPassWord);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(ConstantsUtil.JSON, obj.toString());
        Request request = new Request.Builder()
                .url(ConstantsUtil.BASE_URL + ConstantsUtil.UPDATE_PASSWORD)
                .addHeader("token", String.valueOf(SpUtil.get(this, ConstantsUtil.TOKEN, "")))
                .post(body)
                .build();
        ConstantsUtil.okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LoadingUtils.hideLoading();
                childThread(getString(R.string.server_exception));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String jsonData = response.body().string().toString();
                if (JsonUtils.isGoodJson(jsonData)) {
                    try {
                        JSONObject obj = new JSONObject(jsonData);
                        boolean resultFlag = obj.getBoolean("success");
                        final String msg = obj.getString("message");
                        final String code = obj.getString("code");
                        if (resultFlag) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // Token异常重新登录
                                    ToastUtil.showLong(mContext, "密码修改成功请重新登录！");
                                    SpUtil.put(mContext, ConstantsUtil.IS_LOGIN_SUCCESSFUL, false);
                                    ScreenManagerUtil.popAllActivityExceptOne();
                                    startActivity(new Intent(mContext, LoginActivity.class));
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    switch (code) {
                                        case "3003":
                                        case "3004":
                                            // Token异常重新登录
                                            ToastUtil.showLong(mContext, "Token过期请重新登录！");
                                            SpUtil.put(mContext, ConstantsUtil.IS_LOGIN_SUCCESSFUL, false);
                                            ScreenManagerUtil.popAllActivityExceptOne();
                                            startActivity(new Intent(mContext, LoginActivity.class));
                                            break;
                                        default:
                                            ToastUtil.showLong(mContext, msg);
                                            break;
                                    }
                                }
                            });
                        }
                    } catch (JSONException e) {
                        childThread(getString(R.string.data_error));
                        e.printStackTrace();
                    }
                } else {
                    childThread(getString(R.string.json_error));
                }
                LoadingUtils.hideLoading();
            }
        });
    }

    /**
     * 子线程提示信息
     *
     * @param msg
     */
    private void childThread(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtil.showShort(mContext, msg);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ScreenManagerUtil.popActivity(this);
    }
}
