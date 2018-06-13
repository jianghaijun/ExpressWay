package com.zj.expressway.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.zj.expressway.R;
import com.zj.expressway.base.BaseActivity;
import com.zj.expressway.utils.ConstantsUtil;
import com.zj.expressway.utils.SpUtil;

import org.xutils.x;

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
 *       启动界面
 */
public class SplashScreenActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_splash_screen);
        x.view().inject(this);

        // 闪屏的核心代码
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startNextActivity();
            }
        }, 2000); // 启动动画持续3秒钟
    }

    /**
     * 启动下一界面
     */
    private void startNextActivity() {
        boolean isLoginFlag = (boolean) SpUtil.get(this, ConstantsUtil.IS_LOGIN_SUCCESSFUL, false);
        SpUtil.put(this, ConstantsUtil.TOKEN, "b7055c4fe607ed7a0f18657f5dd1c6c15d354957f883d6ad1f153ef84e9a753c66cd18d64da1d14224b6df3590b79b9e0b1c7aed240dfc23fd4f5e6a08c04fa665d908c4eea8f80a07c84e9be5682c9fe8364fed7826b064ee590527e2a8dadcac2bfa83d1058f217a8b2239721edbad01c3c688ebf77ea619249bfbf3f2b6364ac108f0cfe4feee7f28a5bc22aaead9");

        if (isLoginFlag) {
            startActivity(new Intent(this, MainActivity.class));
        } else {
            startActivity(new Intent(this, LoginActivity.class));
        }
        this.finish();
    }
}
