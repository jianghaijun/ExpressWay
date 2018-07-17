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
        }, 2000); // 启动动画持续2秒钟
    }

    /**
     * 启动下一界面
     */
    private void startNextActivity() {
//        ConstantsUtil.BASE_URL = (String) SpUtil.get(this, "BASE_URL", "http://114.116.12.219:8012/tongren/");
//        ConstantsUtil.ACCOUNT_ID = (String) SpUtil.get(this, "ACCOUNT_ID", "tongren_qyh_app_id");
        ConstantsUtil.BASE_URL = "http://192.168.1.119:8080/web/";
        ConstantsUtil.ACCOUNT_ID = "sjz_qyh_woa_id";

        SpUtil.put(this, "isFirstStar", true);
        boolean isLoginFlag = (boolean) SpUtil.get(this, ConstantsUtil.IS_LOGIN_SUCCESSFUL, false);
        if (isLoginFlag) {
            startActivity(new Intent(this, MainActivity.class));
        } else {
            startActivity(new Intent(this, LoginActivity.class));
        }
        this.finish();
    }
}
