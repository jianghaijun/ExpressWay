package com.zj.expressway.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.zj.expressway.R;
import com.zj.expressway.adapter.CommonWorkingProcedureAdapter;
import com.zj.expressway.adapter.LoadMoreAdapter;
import com.zj.expressway.base.BaseActivity;
import com.zj.expressway.base.BaseAdapter;
import com.zj.expressway.bean.WorkingBean;
import com.zj.expressway.listener.ILoadCallback;
import com.zj.expressway.listener.OnLoad;
import com.zj.expressway.utils.ScreenManagerUtil;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;


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
 *       Created by HaiJun on 2018/6/11 16:34
 *       常用工序
 */
public class CommonWorkingProcedureActivity extends BaseActivity {
    @ViewInject(R.id.imgBtnLeft)
    private ImageButton imgBtnLeft;
    @ViewInject(R.id.txtTitle)
    private TextView txtTitle;
    @ViewInject(R.id.rvCommonWorkingProcedure)
    private RecyclerView rvCommonWorkingProcedure;

    private CommonWorkingProcedureAdapter mAdapter;
    private Activity mContext;
    private BaseAdapter baseAdapter;

    private int num = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_common_working_procedure);
        x.view().inject(this);
        ScreenManagerUtil.pushActivity(this);

        mContext = this;
        imgBtnLeft.setVisibility(View.VISIBLE);
        imgBtnLeft.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.back_btn));
        txtTitle.setText(getString(R.string.commonly_used));

        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        // 创建被装饰者类实例
        mAdapter = new CommonWorkingProcedureAdapter(mContext);
        mAdapter.updateData();
        // 创建装饰者实例，并传入被装饰者和回调接口
        baseAdapter = new LoadMoreAdapter(mAdapter, new OnLoad() {
            @Override
            public void load(int pagePosition, int pageSize, final ILoadCallback callback) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        List<WorkingBean> dataSet = new ArrayList();
                        for (int i = 0; i < 10; i++) {
                            WorkingBean bean = new WorkingBean();
                            bean.setProcessName("工序" + (num * 10 + i));
                            dataSet.add(bean);
                        }
                        // 数据的处理最终还是交给被装饰的adapter来处理
                        mAdapter.appendData(dataSet);
                        callback.onSuccess();
                        // 模拟加载到没有更多数据的情况，触发onFailure
                        if (num++ == 3) {
                            callback.onFailure();
                        }
                    }
                }, 2000);
            }
        });
        rvCommonWorkingProcedure.setAdapter(baseAdapter);
        rvCommonWorkingProcedure.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
    }

    @Event({R.id.imgBtnLeft})
    private void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgBtnLeft:
                this.finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ScreenManagerUtil.popActivity(this);
    }
}
