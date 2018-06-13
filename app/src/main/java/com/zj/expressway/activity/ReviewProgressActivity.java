package com.zj.expressway.activity;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.vivian.timelineitemdecoration.itemdecoration.DotItemDecoration;
import com.vivian.timelineitemdecoration.itemdecoration.SpanIndexListener;
import com.zj.expressway.R;
import com.zj.expressway.adapter.WaterfallFlowTimeLineAdapter;
import com.zj.expressway.base.BaseActivity;
import com.zj.expressway.bean.WorkingBean;
import com.zj.expressway.utils.ScreenManagerUtil;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.hutool.core.date.DateUtil;

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
 *       Created by HaiJun on 2018/6/11 16:51
 *       工序审核时间轴瀑布流格式
 */
public class ReviewProgressActivity extends BaseActivity {
    @ViewInject(R.id.imgBtnLeft)
    private ImageButton imgBtnLeft;
    @ViewInject(R.id.txtTitle)
    private TextView txtTitle;
    /*审核进度*/
    @ViewInject(R.id.rvTimeLineWaterfallFlow)
    private RecyclerView rvTimeLineWaterfallFlow;

    private List<WorkingBean> mList = new ArrayList<>();
    private WaterfallFlowTimeLineAdapter mAdapter;
    private DotItemDecoration mItemDecoration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_review_progress);
        x.view().inject(this);
        ScreenManagerUtil.pushActivity(this);

        imgBtnLeft.setVisibility(View.VISIBLE);
        imgBtnLeft.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.back_btn));
        txtTitle.setText("工序审核进度");

        setData();

        initView();
    }

    /**
     * 设置数据
     */
    private void setData() {
        for (int i = 0; i < 10; i++) {
            WorkingBean bean = new WorkingBean();
            bean.setProcessName("去小北门拿快递去小北门拿快递去小北门拿快递去小北门拿快递" + i);
            bean.setDismissal(DateUtil.format(new Date(), "yyyy-MM-dd hh:mm:ss"));
            mList.add(bean);
        }
    }

    /**
     * 设置显示数据
     */
    private void initView() {
        rvTimeLineWaterfallFlow.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        mItemDecoration = new DotItemDecoration
                .Builder(this)
                .setOrientation(DotItemDecoration.VERTICAL)//if you want a horizontal item decoration,remember to set horizontal orientation to your LayoutManager
                .setItemStyle(DotItemDecoration.STYLE_DRAW)
                .setTopDistance(20)//dp
                .setItemInterVal(10)//dp
                //.setItemPaddingLeft(5)//default value equals to item interval value
                //.setItemPaddingRight(5)//default value equals to item interval value
                .setDotColor(ContextCompat.getColor(this, R.color.main_bg))
                .setDotRadius(5)//dp
                .setDotPaddingTop(0)
                .setDotInItemOrientationCenter(true)//set true if you want the dot align center
                .setLineColor(ContextCompat.getColor(this, R.color.main_bg))
                .setLineWidth(3)//dp
                .setEndText("END")
                //.setTextColor(Color.WHITE)
                //.setTextSize(8)//sp
                //.setDotPaddingText(5)//dp.The distance between the last dot and the end text
                .setBottomDistance(40)//you can add a distance to make bottom line longer
                .create();
        mItemDecoration.setSpanIndexListener(new SpanIndexListener() {
            @Override
            public void onSpanIndexChange(View view, int spanIndex) {
                view.setBackgroundResource(spanIndex == 0 ? R.drawable.left : R.drawable.right);
            }
        });
        rvTimeLineWaterfallFlow.addItemDecoration(mItemDecoration);
        mAdapter = new WaterfallFlowTimeLineAdapter(this, mList);
        rvTimeLineWaterfallFlow.setAdapter(mAdapter);
    }

    @Event({R.id.imgBtnLeft})
    private void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgBtnLeft:
                finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ScreenManagerUtil.popActivity(this);
    }
}
