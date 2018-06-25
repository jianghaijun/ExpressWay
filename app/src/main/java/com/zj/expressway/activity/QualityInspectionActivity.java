package com.zj.expressway.activity;

import android.app.Activity;
import android.content.Intent;
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

import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter;
import com.zj.expressway.R;
import com.zj.expressway.base.BaseActivity;
import com.zj.expressway.bean.SearchRecordBean;
import com.zj.expressway.utils.ConstantsUtil;
import com.zj.expressway.utils.JudgeNetworkIsAvailable;
import com.zj.expressway.utils.ScreenManagerUtil;
import com.zj.expressway.utils.ToastUtil;

import org.litepal.crud.DataSupport;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import cn.hutool.core.util.StrUtil;

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
 *       质量、安全检查主界面
 */
public class QualityInspectionActivity extends BaseActivity {
    @ViewInject(R.id.imgBtnLeft)
    private ImageButton imgBtnLeft;
    @ViewInject(R.id.imgBtnRight)
    private ImageButton imgBtnRight;
    @ViewInject(R.id.txtTitle)
    private TextView txtTitle;
    @ViewInject(R.id.btnTakePicture)
    private Button btnTakePicture;
    @ViewInject(R.id.searchBar)
    private MaterialSearchBar searchBar;
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
    private View layQuality, layToDo, layHasToDo;
    private ProcessListActivity qualityActivity;
    private ProcessListActivity toDoActivity;
    private ProcessListActivity hasToDoActivity;
    private ArrayList<View> views;

    private Activity mContext;
    private String viewType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_working_procedure);

        mContext = this;
        x.view().inject(this);
        llButtons.setVisibility(View.GONE);
        ScreenManagerUtil.pushActivity(this);
        viewType = getIntent().getStringExtra("type");

        txtTitle.setText(R.string.app_name);
        imgBtnLeft.setVisibility(View.VISIBLE);
        imgBtnLeft.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.back_btn));
        imgBtnRight.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.search_btn));

        initViewPageData();
        initTabName();
        initSearchRecord();
        initRecyclerViewData();

        searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                if (!enabled) {
                    searchBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                if (StrUtil.isEmpty(text)) {
                    ToastUtil.showShort(mContext, "请输入搜索关键字");
                } else if (!JudgeNetworkIsAvailable.isNetworkAvailable(mContext)) {
                    ToastUtil.showShort(mContext, "请连接您的网络！");
                } else {
                    searchBar.setVisibility(View.GONE);
                    searchProcessData(String.valueOf(text));
                }
            }

            @Override
            public void onButtonClicked(int buttonCode) {}
        });

        searchBar.setSuggstionsClickListener(new SuggestionsAdapter.OnItemViewClickListener() {
            @Override
            public void OnItemClickListener(int position, View v) {
                if (StrUtil.isEmpty(String.valueOf(v.getTag()))) {
                    ToastUtil.showShort(mContext, "请输入搜索关键字");
                } else if (!JudgeNetworkIsAvailable.isNetworkAvailable(mContext)) {
                    ToastUtil.showShort(mContext, "请连接您的网络！");
                } else {
                    searchBar.setVisibility(View.GONE);
                    searchProcessData(String.valueOf(v.getTag()));
                }
            }

            @Override
            public void OnItemDeleteListener(int position, View v) {
                DataSupport.deleteAll(SearchRecordBean.class, "searchTitle=? and searchType=2", String.valueOf(searchBar.getLastSuggestions().get(position)));
                searchBar.getLastSuggestions().remove(position);
                searchBar.updateLastSuggestions(searchBar.getLastSuggestions());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ConstantsUtil.isLoading) {
            ConstantsUtil.isLoading = false;
            initRecyclerViewData();
        }
    }

    /**
     * 设置搜索历史列表
     */
    private void initSearchRecord() {
        List<SearchRecordBean> searchList = DataSupport.where("searchType=1").find(SearchRecordBean.class);
        if (searchList != null) {
            List<String> stringList = new ArrayList<>();
            for (SearchRecordBean bean : searchList) {
                stringList.add(bean.getSearchTitle());
            }
            searchBar.setLastSuggestions(stringList);
        }
    }

    /**
     * 初始化viewPage数据
     */
    private void initViewPageData() {
        // 将要分页显示的View装入数组中
        LayoutInflater viewLI = LayoutInflater.from(this);
        layQuality = viewLI.inflate(R.layout.layout_msg, null);
        layToDo = viewLI.inflate(R.layout.layout_msg, null);
        layHasToDo = viewLI.inflate(R.layout.layout_msg, null);
        // 质量、安全(待发起)
        qualityActivity = new ProcessListActivity(mContext, layQuality);
        // 已发起
        toDoActivity = new ProcessListActivity(mContext, layToDo);
        // 已提交
        hasToDoActivity = new ProcessListActivity(mContext, layHasToDo);

        // 每个页面的view数据
        views = new ArrayList<>();
        views.add(layQuality);
        views.add(layToDo);
        views.add(layHasToDo);

        vpWorkingProcedure.setOnPageChangeListener(new MyOnPageChangeListener());
        vpWorkingProcedure.setAdapter(mPagerAdapter);
        vpWorkingProcedure.setCurrentItem(0);
    }

    /**
     * 设置tab名称
     */
    private void initTabName() {
        btnTakePicture.setText("待发起");
        btnToBeAudited.setText("已发起");
        btnFinish.setText("已提交");
    }

    /**
     * 初始化列表数据
     */
    private void initRecyclerViewData() {
        qualityActivity.initData(2, btnTakePicture, null);
        toDoActivity.initData(4, btnToBeAudited, null);
        hasToDoActivity.initData(5, btnFinish, null);
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
            if (arg0 == 0) {
                imgBtnRight.setVisibility(View.GONE);
            } else {
                imgBtnRight.setVisibility(View.VISIBLE);
            }
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

    /**
     * 搜索
     * @param levelId
     */
    private void searchProcessData(String levelId) {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 10002) {
                searchProcessData(data.getStringExtra("levelId"));
            } else if (requestCode == 20001) {
                qualityActivity.updateData();
            }
        }
    }

    /**
     * 点击事件
     *
     * @param v
     */
    @Event({ R.id.imgBtnLeft, R.id.imgBtnRight, R.id.btnTakePicture, R.id.btnToBeAudited, R.id.btnFinish })
    private void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgBtnLeft:
                this.finish();
                break;
            case R.id.imgBtnRight:
                searchBar.setVisibility(View.VISIBLE);
                searchBar.enableSearch();
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
        List<String> stringList = searchBar.getLastSuggestions();
        if (stringList != null) {
            DataSupport.deleteAll(SearchRecordBean.class, "searchType=1");
            for (String str : stringList) {
                SearchRecordBean bean = new SearchRecordBean();
                bean.setSearchTitle(str);
                bean.setSearchType("1");
                bean.save();
            }
        }
    }
}
