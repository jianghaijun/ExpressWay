package com.zj.expressway.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.zj.expressway.R;
import com.zj.expressway.base.BaseActivity;
import com.zj.expressway.bean.UserInfo;
import com.zj.expressway.bean.UserLevelBean;
import com.zj.expressway.listener.PermissionListener;
import com.zj.expressway.model.AliasModel;
import com.zj.expressway.model.LoginModel;
import com.zj.expressway.utils.ChildThreadUtil;
import com.zj.expressway.utils.ConstantsUtil;
import com.zj.expressway.utils.JsonUtils;
import com.zj.expressway.utils.JudgeNetworkIsAvailable;
import com.zj.expressway.utils.LoadingUtils;
import com.zj.expressway.utils.ScreenManagerUtil;
import com.zj.expressway.utils.SpUtil;
import com.zj.expressway.utils.ToastUtil;

import org.litepal.crud.DataSupport;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.IOException;
import java.util.List;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.jpush.android.api.JPushInterface;
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
 *       Created by dell on 2017/10/20 14:13
 */
public class LoginActivity extends BaseActivity {
    @ViewInject(R.id.edtUserName)
    private EditText edtUserName;
    @ViewInject(R.id.edtUserPassWord)
    private EditText edtUserPassWord;
    private Activity mContext;
    @ViewInject(R.id.imgLogo)
    private ImageView imgLogo;
    // 登录锁
    private boolean isLogin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_login);
        x.view().inject(this);
        mContext = this;

        ScreenManagerUtil.pushActivity(this);

        RequestOptions options = new RequestOptions().circleCrop();
        Glide.with(this).load(R.mipmap.logo).apply(options).into(imgLogo);

        String userName = (String) SpUtil.get(this, "user", "");
        edtUserName.setText(userName);

        if (Build.VERSION.SDK_INT >= 23) {
            requestAuthority(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA}, new PermissionListener() {
                @Override
                public void agree() {}

                @Override
                public void refuse(List<String> refusePermission) {
                    ToastUtil.showLong(mContext, "您已拒绝拍照权限!");
                }
            });
        }
    }

    @Event({R.id.btnLogin})
    private void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLogin:
                if (StrUtil.isEmpty(edtUserName.getText().toString().trim())) {
                    ToastUtil.showShort(this, getString(R.string.please_input_user_name));
                } else if (StrUtil.isEmpty(edtUserPassWord.getText().toString().trim())) {
                    ToastUtil.showShort(this, getString(R.string.please_input_user_password));
                } else {
                    if (JudgeNetworkIsAvailable.isNetworkAvailable(this)) {
                        if (!isLogin) {
                            isLogin = true;
                            Login ();
                        }
                    } else {
                        // 根据userId password获取用户信息
                        List<UserInfo> userList = DataSupport.where("userId=? and userPwd=?", edtUserName.getText().toString().trim(), edtUserPassWord.getText().toString().trim()).find(UserInfo.class);
                        if (userList != null && userList.size() > 0) {
                            if (!isLogin) {
                                isLogin = true;
                                UserInfo user = userList.get(0);
                                SpUtil.put(mContext, ConstantsUtil.USER_LEVEL, StrUtil.isEmpty(user.getUserLevel()) ? "" : user.getUserLevel());
                                SpUtil.put(mContext, "UserName", StrUtil.isEmpty(user.getRealName()) ? "" : user.getRealName());
                                SpUtil.put(mContext, "user", StrUtil.isEmpty(user.getUserId()) ? "" : user.getUserId());
                                SpUtil.put(mContext, ConstantsUtil.USER_ID, StrUtil.isEmpty(user.getUserId()) ? "" : user.getUserId());
                                SpUtil.put(mContext, ConstantsUtil.TOKEN, StrUtil.isEmpty(user.getToken()) ? "" : user.getToken());
                                SpUtil.put(mContext, ConstantsUtil.USER_HEAD, StrUtil.isEmpty(user.getImageUrl()) ? "" : user.getImageUrl());
                                SpUtil.put(mContext, ConstantsUtil.IS_LOGIN_SUCCESSFUL, true);
                                startActivity(new Intent(mContext, MainActivity.class));
                                LoginActivity.this.finish();
                                edtUserPassWord.setText("");
                                isLogin = false;
                            }
                        } else {
                            ToastUtil.showShort(mContext, "用户名或密码错误!");
                        }
                    }
                }
                break;
            default:
                break;
        }
    }

    /**
     * 登录
     */
    private void Login () {
        LoadingUtils.showLoading(mContext);
        JSONObject boj = new JSONObject();
        boj.put("userId", edtUserName.getText().toString().trim());
        boj.put("userPwd", edtUserPassWord.getText().toString().trim());
        boj.put("accountId", ConstantsUtil.ACCOUNT_ID);
        boj.put("loginType", "1");
        Request request = ChildThreadUtil.getRequest(mContext, ConstantsUtil.LOGIN, boj.toString());
        ConstantsUtil.okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                isLogin = false;
                ChildThreadUtil.toastMsgHidden(mContext, getString(R.string.server_exception));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String jsonData = response.body().string().toString();
                if (JsonUtils.isGoodJson(jsonData)) {
                    Gson gson = new Gson();
                    final LoginModel loginModel = gson.fromJson(jsonData, LoginModel.class);
                    if (loginModel.isSuccess()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                UserInfo userInfo = loginModel.getData().getUserInfo();
                                SpUtil.put(mContext, "UserName", userInfo.getRealName());
                                SpUtil.put(mContext, "user", edtUserName.getText().toString().trim());
                                SpUtil.put(mContext, ConstantsUtil.USER_ID, userInfo.getUserId());
                                SpUtil.put(mContext, ConstantsUtil.TOKEN, loginModel.getData().getToken());
                                SpUtil.put(mContext, ConstantsUtil.USER_HEAD, userInfo.getImageUrl() == null ? "" : ConstantsUtil.BASE_URL + ConstantsUtil.prefix + userInfo.getImageUrl().toString());
                                // 保存至本地用户登录信息
                                userInfo.setUserPwd(edtUserPassWord.getText().toString().trim());
                                userInfo.setImageUrl(userInfo.getImageUrl() == null ? "" : ConstantsUtil.BASE_URL + ConstantsUtil.prefix + userInfo.getImageUrl().toString());
                                userInfo.setToken(loginModel.getData().getToken());
                                userInfo.saveOrUpdate("userId=?", userInfo.getUserId());
                                // 设置极光别名
                                int sequence = (int) System.currentTimeMillis();
                                JPushInterface.setAlias(mContext, sequence, userInfo.getUserId());


                                LoadingUtils.hideLoading();
                                isLogin = false;
                                SpUtil.put(mContext, ConstantsUtil.IS_LOGIN_SUCCESSFUL, true);
                                //SpUtil.put(mContext, ConstantsUtil.USER_LEVEL, aliasModel.getData() == null ? "" : aliasModel.getData().getRoleFlag());
                                // 各分部质检负责人
                                /*if (aliasModel.getData().getSxZlUserExtendList() != null) {
                                    for (UserLevelBean qualityBean : aliasModel.getData().getSxZlUserExtendList()) {
                                        qualityBean.saveOrUpdate("userExtendId=?", qualityBean.getUserExtendId());
                                    }
                                }*/
                                // 保存用户信息
                                /*List<UserInfo> userList = DataSupport.where("userId=?", String.valueOf(SpUtil.get(mContext, ConstantsUtil.USER_ID, ""))).find(UserInfo.class);
                                if (userList != null && userList.size() > 0) {
                                    UserInfo user = userList.get(0);
                                    user.setUserLevel(aliasModel.getData().getRoleFlag() == null ? "0" : aliasModel.getData().getRoleFlag());
                                    user.saveOrUpdate("userId=?", String.valueOf(SpUtil.get(mContext, ConstantsUtil.USER_ID, "")));
                                }*/
                                startActivity(new Intent(mContext, MainActivity.class));
                                LoginActivity.this.finish();
                                edtUserPassWord.setText("");

                                //LoginSuccessful();
                                //startActivity(new Intent(mContext, MainActivity.class));
                            }
                        });
                    } else {
                        isLogin = false;
                        ChildThreadUtil.checkTokenHidden(mContext, loginModel.getMessage(), loginModel.getCode());
                    }
                } else {
                    isLogin = false;
                    ChildThreadUtil.toastMsgHidden(mContext, getString(R.string.json_error));
                }
            }
        });
    }

    /**
     * 登录成功--->上传极光别名
     */
    private void LoginSuccessful () {
        JSONObject boj = new JSONObject();
        boj.put("userId", SpUtil.get(mContext, ConstantsUtil.USER_ID, ""));
        boj.put("alias", SpUtil.get(mContext, ConstantsUtil.USER_ID, ""));
        Request request = ChildThreadUtil.getRequest(mContext, ConstantsUtil.SUBMIT_ALIAS, boj.toString());
        ConstantsUtil.okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                isLogin = false;
                ChildThreadUtil.toastMsgHidden(mContext, "别名上传失败！");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String jsonData = response.body().string().toString();
                if (JsonUtils.isGoodJson(jsonData)) {
                    Gson gson = new Gson();
                    final AliasModel aliasModel = gson.fromJson(jsonData, AliasModel.class);
                    if (aliasModel.isSuccess()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                LoadingUtils.hideLoading();
                                isLogin = false;
                                SpUtil.put(mContext, ConstantsUtil.IS_LOGIN_SUCCESSFUL, true);
                                SpUtil.put(mContext, ConstantsUtil.USER_LEVEL, aliasModel.getData() == null ? "" : aliasModel.getData().getRoleFlag());
                                // 各分部质检负责人
                                if (aliasModel.getData().getSxZlUserExtendList() != null) {
                                    for (UserLevelBean qualityBean : aliasModel.getData().getSxZlUserExtendList()) {
                                        qualityBean.saveOrUpdate("userExtendId=?", qualityBean.getUserExtendId());
                                    }
                                }
                                // 保存用户信息
                                List<UserInfo> userList = DataSupport.where("userId=?", String.valueOf(SpUtil.get(mContext, ConstantsUtil.USER_ID, ""))).find(UserInfo.class);
                                if (userList != null && userList.size() > 0) {
                                    UserInfo user = userList.get(0);
                                    user.setUserLevel(aliasModel.getData().getRoleFlag() == null ? "0" : aliasModel.getData().getRoleFlag());
                                    user.saveOrUpdate("userId=?", String.valueOf(SpUtil.get(mContext, ConstantsUtil.USER_ID, "")));
                                }
                                startActivity(new Intent(mContext, MainActivity.class));
                                LoginActivity.this.finish();
                                edtUserPassWord.setText("");
                            }
                        });
                    } else {
                        isLogin = false;
                        ChildThreadUtil.checkTokenHidden(mContext, aliasModel.getMessage(), aliasModel.getCode());
                    }
                } else {
                    isLogin = false;
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
