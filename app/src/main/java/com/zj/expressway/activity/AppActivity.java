package com.zj.expressway.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;
import com.zj.expressway.R;
import com.zj.expressway.adapter.AppInfoAdapter;
import com.zj.expressway.base.BaseActivity;
import com.zj.expressway.bean.AppInfoBean;
import com.zj.expressway.bean.MainPageBean;
import com.zj.expressway.loader.GlideImageLoader;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by HaiJun on 2018/6/11 16:32
 * 应用图标列表
 */
public class AppActivity extends BaseActivity {
    private AppHold hold;
    private Activity mContext;
    private AppInfoAdapter appInfoAdapter;

    public AppActivity(Activity mContext, View layoutApp) {
        this.mContext = mContext;
        hold = new AppHold();
        x.view().inject(hold, layoutApp);
    }

    /**
     * 赋值
     *
     * @param objList
     */
    public void setDate(List<MainPageBean> objList) {
        List<String> urlList = new ArrayList<>();
        final List<String> strList = new ArrayList<>();
        final List<String> idList = new ArrayList<>();
        if (objList != null && objList.size() != 0) {
            for (MainPageBean bean : objList) {
                idList.add(bean.getViewId());
                urlList.add(bean.getFileUrl());
                strList.add(bean.getViewContent());
            }
        }

        //设置banner样式
        hold.banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
        //设置图片加载器
        hold.banner.setImageLoader(new GlideImageLoader());
        //设置banner动画效果
        hold.banner.setBannerAnimation(Transformer.DepthPage);
        //设置自动轮播，默认为true
        hold.banner.isAutoPlay(true);
        //设置轮播时间
        hold.banner.setDelayTime(5000);
        //设置指示器位置（当banner模式中有指示器时）
        hold.banner.setIndicatorGravity(BannerConfig.RIGHT);
        //设置图片集合
        hold.banner.setImages(urlList);
        hold.banner.start();
        // 设置图片下文字信息
        hold.txtEnterPriseInfo.setText(strList.size() > 0 ? strList.get(0) : "");
        // 活动监听
        hold.banner.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                hold.txtEnterPriseInfo.setText(strList.get(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        // 点击事件
        hold.banner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {
                Intent intent = new Intent(mContext, EditScrollPhotoActivity.class);
                intent.putExtra("viewId", idList.get(position));
                mContext.startActivity(intent);
            }
        });

        // 添加图片、标题
        List<AppInfoBean> appInfoList = new ArrayList<>();
        AppInfoBean bean = new AppInfoBean();
        bean.setImgUrl(R.drawable.concealment_project);
        bean.setTitle(mContext.getString(R.string.process_inspection));
        appInfoList.add(bean);
        bean = new AppInfoBean();
        bean.setImgUrl(R.drawable.quality_testing);
        bean.setTitle(mContext.getString(R.string.qualityPatrol));
        appInfoList.add(bean);
        bean = new AppInfoBean();
        bean.setImgUrl(R.drawable.hidden_trouble_investigation);
        bean.setTitle(mContext.getString(R.string.securityPatrol));
        appInfoList.add(bean);
        bean = new AppInfoBean();
        bean.setImgUrl(R.drawable.audit_management);
        bean.setTitle(mContext.getString(R.string.audit_management));
        appInfoList.add(bean);
        bean = new AppInfoBean();
        bean.setImgUrl(R.drawable.data_report);
        bean.setTitle("数据报表");
        appInfoList.add(bean);
        bean = new AppInfoBean();
        bean.setImgUrl(R.drawable.qr_code);
        bean.setTitle(mContext.getString(R.string.qr_code));
        appInfoList.add(bean);
        bean = new AppInfoBean();
        bean.setImgUrl(R.drawable.bid_management);
        bean.setTitle("地图展示");
        appInfoList.add(bean);
        bean = new AppInfoBean();
        bean.setImgUrl(R.drawable.group_management);
        bean.setTitle("资料库");
        appInfoList.add(bean);

        appInfoAdapter = new AppInfoAdapter(mContext, appInfoList);
        hold.rvAppInfo.setLayoutManager(new GridLayoutManager(mContext, 4));
        hold.rvAppInfo.setAdapter(appInfoAdapter);
    }

    /**
     * 开始轮播
     */
    public void startBanner() {
        //开始轮播
        if (hold != null && hold.banner != null) {
            hold.banner.startAutoPlay();
        }
    }

    /**
     * 停止轮播
     */
    public void stopBanner() {
        if (hold != null && hold.banner != null) {
            hold.banner.stopAutoPlay();
        }
    }

    /**
     * 容纳器
     */
    private class AppHold {
        @ViewInject(R.id.banner)
        private Banner banner;

        @ViewInject(R.id.rvAppInfo)
        private RecyclerView rvAppInfo;
        @ViewInject(R.id.txtEnterPriseInfo)
        private TextView txtEnterPriseInfo;

    }
}
