package com.zj.expressway.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter;
import com.zj.expressway.R;
import com.zj.expressway.base.BaseActivity;
import com.zj.expressway.bean.SearchRecordBean;
import com.zj.expressway.utils.ConstantsUtil;
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
 * Created by HaiJun on 2018/6/11 17:00
 * 工序列表主界面
 */
public class WorkingProcedureActivity extends BaseActivity {
    @ViewInject(R.id.imgBtnLeft)
    private ImageButton imgBtnLeft;
    @ViewInject(R.id.imgBtnRight)
    private ImageButton imgBtnRight;
    @ViewInject(R.id.txtTitle)
    private TextView txtTitle;
    @ViewInject(R.id.searchBar)
    private MaterialSearchBar searchBar;
    @ViewInject(R.id.btnToBeAudited)
    private TextView btnToBeAudited;
    @ViewInject(R.id.rlPhoto)
    private RelativeLayout rlPhoto;
    @ViewInject(R.id.llButtons)
    private LinearLayout llButtons;
    @ViewInject(R.id.vToBeAudited)
    private View vToBeAudited;
    @ViewInject(R.id.btnFinish)
    private TextView btnFinish;
    @ViewInject(R.id.vFinish)
    private View vFinish;
    @ViewInject(R.id.vpWorkingProcedure)
    private ViewPager vpWorkingProcedure;
    @ViewInject(R.id.txtUnSubmit)
    private TextView txtUnSubmit;
    @ViewInject(R.id.txtSubmit)
    private TextView txtSubmit;

    // viewPage
    private View layToBeAudited, layFinish;
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
        imgBtnRight.setVisibility(View.VISIBLE);
        imgBtnRight.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.search_btn));

        llButtons.setVisibility(View.GONE);
        rlPhoto.setVisibility(View.GONE);
        vToBeAudited.setBackgroundColor(ContextCompat.getColor(mContext, R.color.main_check_bg));

        initViewPageData();

        Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.to_do);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        btnToBeAudited.setCompoundDrawables(drawable, null, null, null);

        btnToBeAudited.setText("待办");
        btnFinish.setText("已办");

        initSearchRecord();

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
                } else {
                    searchBar.setVisibility(View.GONE);
                    searchProcessData(String.valueOf(text));
                }
            }

            @Override
            public void onButtonClicked(int buttonCode) {
            }
        });

        searchBar.setSuggstionsClickListener(new SuggestionsAdapter.OnItemViewClickListener() {
            @Override
            public void OnItemClickListener(int position, View v) {
                if (StrUtil.isEmpty(String.valueOf(v.getTag()))) {
                    ToastUtil.showShort(mContext, "请输入搜索关键字");
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

        initRecyclerViewData();
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
        layToBeAudited = viewLI.inflate(R.layout.layout_msg, null);
        layFinish = viewLI.inflate(R.layout.layout_msg, null);
        // 待审核
        toBeAuditedActivity = new ProcessListActivity(mContext, layToBeAudited);
        // 已完成
        finishActivity = new ProcessListActivity(mContext, layFinish);

        //每个页面的view数据
        views = new ArrayList<>();
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
        toBeAuditedActivity.initData(4, txtUnSubmit, null, true);
        finishActivity.initData(5, txtSubmit, null, false);
    }

    /**
     * 搜索
     *
     * @param levelId
     */
    private void searchProcessData(String levelId) {
        switch (vpWorkingProcedure.getCurrentItem()) {
            case 0:
                toBeAuditedActivity.initData(4, txtUnSubmit, levelId, true);
                break;
            case 1:
                finishActivity.initData(5, txtSubmit, levelId, true);
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
        // 待审核
        //btnToBeAudited.setTextColor(ContextCompat.getColor(mContext, R.color.black));
        vToBeAudited.setBackgroundColor(ContextCompat.getColor(mContext, R.color.gray));
        // 已完成
        //btnFinish.setTextColor(ContextCompat.getColor(mContext, R.color.black));
        vFinish.setBackgroundColor(ContextCompat.getColor(mContext, R.color.gray));

        switch (option) {
            case 0:
                //btnToBeAudited.setTextColor(ContextCompat.getColor(mContext, R.color.main_check_bg));
                vToBeAudited.setBackgroundColor(ContextCompat.getColor(mContext, R.color.tab_2));
                break;
            case 1:
                //btnFinish.setTextColor(ContextCompat.getColor(mContext, R.color.main_check_bg));
                vFinish.setBackgroundColor(ContextCompat.getColor(mContext, R.color.tab_3));
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
    @Event({R.id.imgBtnLeft, R.id.imgBtnRight, R.id.rlUnSubmit, R.id.rlSubmitting})
    private void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgBtnLeft:
                this.finish();
                break;
            case R.id.imgBtnRight:
                searchBar.setVisibility(View.VISIBLE);
                searchBar.enableSearch();
                break;
            case R.id.rlUnSubmit:
                vpWorkingProcedure.setCurrentItem(0);
                break;
            case R.id.rlSubmitting:
                vpWorkingProcedure.setCurrentItem(1);
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
