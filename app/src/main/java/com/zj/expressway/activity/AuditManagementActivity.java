package com.zj.expressway.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zj.expressway.R;
import com.zj.expressway.base.BaseActivity;
import com.zj.expressway.bean.WorkingBean;
import com.zj.expressway.utils.ConstantsUtil;
import com.zj.expressway.utils.ScreenManagerUtil;
import com.zj.expressway.utils.SpUtil;
import com.zj.expressway.view.SonnyJackDragView;

import org.litepal.crud.DataSupport;
import org.xutils.common.util.DensityUtil;
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
 *       Created by HaiJun on 2018/6/11 17:00
 *       工序列表主界面
 */
public class AuditManagementActivity extends BaseActivity {
    @ViewInject(R.id.imgBtnLeft)
    private ImageButton imgBtnLeft;
    @ViewInject(R.id.txtTitle)
    private TextView txtTitle;
    @ViewInject(R.id.btnTakePicture)
    private Button btnTakePicture;
    @ViewInject(R.id.vTakePicture)
    private View vTakePicture;
    @ViewInject(R.id.btnToBeAudited)
    private Button btnToBeAudited;
    @ViewInject(R.id.vToBeAudited)
    private View vToBeAudited;
    @ViewInject(R.id.btnFinish)
    private Button btnFinish;
    @ViewInject(R.id.vFinish)
    private View vFinish;
    @ViewInject(R.id.vpWorkingProcedure)
    private ViewPager vpWorkingProcedure;
    @ViewInject(R.id.llButtons)
    private LinearLayout llButtons;
    // viewPage
    private View layTakePicture, layToBeAudited, layFinish;
    private ProcessListActivity takePictureActivity;
    private ProcessListActivity toBeAuditedActivity;
    private ProcessListActivity finishActivity;
    private ArrayList<View> views;
    private Activity mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_working_procedure);
        x.view().inject(this);
        mContext = this;
        ScreenManagerUtil.pushActivity(this);

        txtTitle.setText(R.string.app_name);
        imgBtnLeft.setVisibility(View.VISIBLE);
        imgBtnLeft.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.back_btn));

        initViewPageData();

        llButtons.setVisibility(View.GONE);

        initFabBtn();
        initRecyclerViewData();

        btnTakePicture.setText("待拍照");
        btnToBeAudited.setText("未提交");
        List<WorkingBean> beanSize = DataSupport.where("userId=? and type=? order by enterTime desc ", String.valueOf(SpUtil.get(mContext, ConstantsUtil.USER_ID, "")), "5").find(WorkingBean.class);
        int sum = beanSize == null ? 0 : beanSize.size();
        btnFinish.setText("已提交（" + sum + "）");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ConstantsUtil.isLoading) {
            initRecyclerViewData();
            ConstantsUtil.isLoading = false;
        }
    }

    /**
     * 添加悬浮按钮
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void initFabBtn() {
        Button imgBtn = new Button(this);
        imgBtn.setTextColor(ContextCompat.getColor(this, R.color.white));
        imgBtn.setTextSize(10);
        imgBtn.setElevation(50f);
        imgBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.all_tree));
        imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ContractorTreeActivity.class);
                intent.putExtra("type", "1");
                startActivityForResult(intent, 10002);
            }
        });

        new SonnyJackDragView.Builder()
                .setActivity(this)
                .setDefaultLeft(DensityUtil.getScreenWidth() - DensityUtil.dip2px(75))
                .setDefaultTop(DensityUtil.getScreenHeight() - DensityUtil.dip2px(100))
                .setNeedNearEdge(false)
                .setSize(DensityUtil.dip2px(50))
                .setView(imgBtn)
                .build();
    }

    /**
     * 初始化viewPage数据
     */
    private void initViewPageData() {
        // 将要分页显示的View装入数组中
        LayoutInflater viewLI = LayoutInflater.from(this);
        layTakePicture = viewLI.inflate(R.layout.layout_msg, null);
        layToBeAudited = viewLI.inflate(R.layout.layout_msg, null);
        layFinish = viewLI.inflate(R.layout.layout_msg, null);
        // 待拍照
        takePictureActivity = new ProcessListActivity(mContext, layTakePicture);
        // 待审核
        toBeAuditedActivity = new ProcessListActivity(mContext, layToBeAudited);
        // 已完成
        finishActivity = new ProcessListActivity(mContext, layFinish);


        TextView txtClear = (TextView) layTakePicture.findViewById(R.id.txtClear);

        txtClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePictureActivity.initData(1, btnTakePicture, null);
            }
        });

        //每个页面的view数据
        views = new ArrayList<>();
        views.add(layTakePicture);
        views.add(layToBeAudited);
        views.add(layFinish);

        vpWorkingProcedure.setOnPageChangeListener(new MyOnPageChangeListener());
        vpWorkingProcedure.setAdapter(mPagerAdapter);
        vpWorkingProcedure.setCurrentItem(0);
    }

    /**
     * 初始化列表数据
     */
    private void initRecyclerViewData() {
        takePictureActivity.initData(1, btnTakePicture, null);
        toBeAuditedActivity.initData(4, btnToBeAudited, null);
        finishActivity.initData(5, btnFinish, null);
    }

    /**
     * 搜索
     * @param levelId
     */
    private void searchProcessData(String levelId) {
        switch (vpWorkingProcedure.getCurrentItem()) {
            case 0:
                takePictureActivity.initData(1, btnTakePicture, levelId);
                break;
            case 1:
                toBeAuditedActivity.initData(4, btnToBeAudited, levelId);
                break;
            case 2:
                finishActivity.initData(5, btnFinish, levelId);
                break;
        }
    }

    /**
     * 填充ViewPager的数据适配器
     */
    private PagerAdapter mPagerAdapter = new PagerAdapter() {
        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public int getCount() {
            return views.size();
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
            ((ViewPager) container).removeView(views.get(position));
        }

        @Override
        public Object instantiateItem(View container, int position) {
            ((ViewPager) container).addView(views.get(position));
            return views.get(position);
        }
    };

    /**
     * 页卡切换监听
     */
    private class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageSelected(int arg0) {
            setStates(arg0);
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }
    }

    /**
     * 设置背景
     *
     * @param option
     */
    private void setStates(int option) {
        // 待拍照
        btnTakePicture.setTextColor(ContextCompat.getColor(mContext, R.color.black));
        vTakePicture.setBackgroundColor(ContextCompat.getColor(mContext, R.color.black));
        // 待审核
        btnToBeAudited.setTextColor(ContextCompat.getColor(mContext, R.color.black));
        vToBeAudited.setBackgroundColor(ContextCompat.getColor(mContext, R.color.black));
        // 已完成
        btnFinish.setTextColor(ContextCompat.getColor(mContext, R.color.black));
        vFinish.setBackgroundColor(ContextCompat.getColor(mContext, R.color.black));

        switch (option) {
            case 0:
                btnTakePicture.setTextColor(ContextCompat.getColor(mContext, R.color.main_check_bg));
                vTakePicture.setBackgroundColor(ContextCompat.getColor(mContext, R.color.main_check_bg));
                break;
            case 1:
                btnToBeAudited.setTextColor(ContextCompat.getColor(mContext, R.color.main_check_bg));
                vToBeAudited.setBackgroundColor(ContextCompat.getColor(mContext, R.color.main_check_bg));
                break;
            case 2:
                btnFinish.setTextColor(ContextCompat.getColor(mContext, R.color.main_check_bg));
                vFinish.setBackgroundColor(ContextCompat.getColor(mContext, R.color.main_check_bg));
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 10002) {
                searchProcessData(data.getStringExtra("levelId"));
            }
        }
    }

    /**
     * 点击事件
     *
     * @param v
     */
    @Event({R.id.imgBtnLeft, R.id.btnTakePicture, R.id.btnToBeAudited, R.id.btnFinish })
    private void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgBtnLeft:
                this.finish();
                break;
            case R.id.btnTakePicture:
                vpWorkingProcedure.setCurrentItem(0);
                break;
            case R.id.btnToBeAudited:
                vpWorkingProcedure.setCurrentItem(1);
                break;
            case R.id.btnFinish:
                vpWorkingProcedure.setCurrentItem(2);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ScreenManagerUtil.popActivity(this);
    }
}
